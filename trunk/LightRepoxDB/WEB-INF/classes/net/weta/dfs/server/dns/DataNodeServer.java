/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/dns/DataNodeServer.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.IOException;
import java.net.SocketException;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceMDS;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.Server;
import net.weta.dfs.util.MethodInvokerThread;

/**
 * A Data node service starting and stoping a server socket listener and
 * installs a stream handler.
 * 
 * <br/><br/>created on 14.01.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class DataNodeServer extends Server {

    private Connection fMdsConnection;

    private ChunkManager fChunkManager;

    private MethodInvokerThread fLifeSignThread;

    /**
     * @param config
     * @throws IOException
     */
    public DataNodeServer(Configuration config) throws IOException {
        super(config.getPropertyAsInt(Configuration.DATA_NODE_PORT), ClientHandlerDNS.class);
        // read configuration
        fLogger.info("reading config file from "
                + config.getProperty(Configuration.DFS_CONFIG_PATH));
        int mdsPort = config
                .getPropertyAsInt(Configuration.META_DATA_SERVER_PORT);
        String mdsAddress = config
                .getProperty(Configuration.META_DATA_SERVER_IP);
        String chunkDir = config.getProperty(Configuration.CHUNK_DIRECTORY);
        int maxDiscSize = config
                .getPropertyAsInt(Configuration.CHUNK_DISK_SIZE);

        // start particular components
        fLogger.info("setup datanodeserver with port " + getPort()
                + " and chunk directory at " + chunkDir);
        this.fChunkManager = new ChunkManager(chunkDir, maxDiscSize);
        this.fMdsConnection = new Connection(mdsAddress, mdsPort);
    }

    /**
     * @return Returns the chunkManager.
     */
    public ChunkManager getChunkManager() {
        return this.fChunkManager;
    }

    /**
     * @throws IOException
     */
    public synchronized void startServer() throws IOException {
        if (!isRunning()) {
            fLogger.info("starting datanodeserver at port " + getPort());
            super.startServer();
            // connect to metadataserver
            fLogger.info("try to connect to metadataserver "
                    + this.fMdsConnection.getIpAddress() + " at port "
                    + this.fMdsConnection.getPort());
            try {
                this.fLifeSignThread = new MethodInvokerThread(this,
                        "sendLifeSign", 60000);
                this.fLifeSignThread.setName("DataNodeServer");
                this.fLifeSignThread.start();
                setIpAddress(ServerFaceMDS.getIp(this.fMdsConnection));
                initNode();
            } catch (Exception e) {
                fLogger.error("could not connect to metadataserver", e);
                stopServer();
                throw new IOException("could not connect to metadataserver");
            }
        }
        fLogger.info("datanodeserver started, host: " + getIpAddress()
                + ", port: " + getPort());
    }

    /**
     * @throws IOException
     */
    public synchronized void stopServer() throws IOException {
        if (isRunning()) {
            fLogger.info("stopping datanodeserver");
            super.stopServer();
            this.fLifeSignThread.interrupt();
            this.fLifeSignThread = null;
            this.fMdsConnection.close();
        }
        fLogger.info("datanodeserver stopped");
    }

    /**
     * 
     * @param chunkMD
     * @throws IOException
     * @throws CommandException
     */
    public synchronized void sendChunkWritten(ChunkMetaData chunkMD)
            throws IOException, CommandException {
        ServerFaceMDS.chunkWritten(this.fMdsConnection, getNodeMD(), chunkMD);
    }

    /**
     * @return all chunkMds which has no file
     * @throws IOException
     */
    public synchronized ChunkMetaData[] initNode() throws IOException {
        NodeMetaData nodeMD = getNodeMD();
        // init files
        FileMetaData[] fileMds = this.fChunkManager.getFileMetaDatas();
        ServerFaceMDS.nodeInitFiles(this.fMdsConnection, nodeMD, fileMds);

        // init chunks
        ChunkMetaData[] chunkMds = this.fChunkManager.getChunkMetaDatas();
        ChunkMetaData[] unAssChunks = ServerFaceMDS.nodeInitChunks(
                this.fMdsConnection, nodeMD, chunkMds);

        return unAssChunks;
    }

    /**
     * @throws IOException
     */
    public synchronized void sendLifeSign() throws IOException {
        boolean known = false;
        boolean contact = true;
        try {
            known = ServerFaceMDS.sendLifeSign(this.fMdsConnection, getNodeMD());
        } catch (SocketException e) {
            // if mds is down connection may recognize that not until
            // sc.receiveResponse()
            try {
                known = ServerFaceMDS
                        .sendLifeSign(this.fMdsConnection, getNodeMD());
            } catch (SocketException e2) {
                // TODO what happens now with client/dns contacts
                contact = false;
                fLogger.error("connection with metadataserver lost", e2);
            }
        }
        if (contact && !known) {
            ChunkMetaData[] outdatedChunks = initNode();
            for (int i = 0; i < outdatedChunks.length; i++) {
                // TODO remove chunks?
            }
        }
    }

    /**
     * @return get the NodeMetaData of this DataNodeServer
     */
    public NodeMetaData getNodeMD() {
        return new NodeMetaData(this.getIpAddress(), this.getPort(),
                this.fChunkManager.getFreeDiskSpace());
    }

    /**
     * Starts the DataNodeServer.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new DataNodeServer(Configuration.getInstance()).startServer();
    }
}