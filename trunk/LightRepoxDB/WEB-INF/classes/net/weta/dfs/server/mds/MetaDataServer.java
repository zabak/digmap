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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/MetaDataServer.java,v $
 */

package net.weta.dfs.server.mds;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceDNS;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.Server;
import net.weta.dfs.server.mds.vfs.VirtualFileSystem;
import net.weta.dfs.util.MethodInvokerThread;
import net.weta.dfs.util.PathUtil;

/**
 * Stores and manage the information of a distributed file system.
 * 
 * <br/><br/>created on 12.01.2005
 * 
 * @version $Revision: 1.1 $
 */
public class MetaDataServer extends Server {

    private volatile VirtualFileSystem fFileSystem;

    private NodeManager fNodeManager;

    private MethodInvokerThread fOutdatedNodesThread;

    private MethodInvokerThread fOutdatedDiscSpaceThread;

    private volatile static long fCounter = 0;

    /**
     * 
     * @param config
     * @throws IOException
     */
    public MetaDataServer(Configuration config) throws IOException {
        super(config.getPropertyAsInt(Configuration.META_DATA_SERVER_PORT), ClientHandlerMDS.class);
        fLogger.info("reading config file from " + config.getProperty(Configuration.DFS_CONFIG_PATH));
        setIpAddress(config.getProperty(Configuration.META_DATA_SERVER_IP));
    }

    /**
     * @throws IOException
     */
    public synchronized void startServer() throws IOException {
        if (!isRunning()) {
            fLogger.info("starting metadataserver at port " + getPort());
            super.startServer();
            this.fNodeManager = new NodeManager();
            try {
                this.fFileSystem = new VirtualFileSystem(this, "getId");
                this.fOutdatedNodesThread = new MethodInvokerThread(
                        this.fNodeManager, "removeOutdatedNodes", 400000,
                        new Class[] { Long.class }, new Object[] { new Long(
                                400000) });
                this.fOutdatedDiscSpaceThread = new MethodInvokerThread(
                        this.fNodeManager, "freeOutdatedDiscSpace", 300000,
                        new Class[] { Long.class }, new Object[] { new Long(
                                300000) });
            } catch (NoSuchMethodException e) {
                throw new IOException(e.getMessage());
            }
        }
        fLogger.info("metadataserver started, host: " + getIpAddress()
                + ", port: " + getPort());
    }

    /**
     * Stops the server and clears the FileSystem and the NodeManager
     * 
     * @throws IOException
     */
    public synchronized void stopServer() throws IOException {
        fLogger.info("stopping metadataserver");
        if (isRunning()) {
            super.stopServer();
            this.fOutdatedNodesThread.interrupt();
            this.fOutdatedNodesThread = null;
            this.fOutdatedDiscSpaceThread.interrupt();
            this.fOutdatedDiscSpaceThread = null;
            this.fFileSystem = null;
            this.fNodeManager = null;
        }
        fLogger.info("metadataserver stopped");
    }

    /**
     * @return the virtual fileSystem
     */
    public VirtualFileSystem getFileSystem() {
        return this.fFileSystem;
    }

    /**
     * @return the node manager
     */
    public NodeManager getNodeManager() {
        return this.fNodeManager;
    }

    /**
     * 
     * @param nodeMD
     * @param chunks
     * @return all chunks without a file
     */
    public ChunkMetaData[] processChunkMetaData(NodeMetaData nodeMD,
            ChunkMetaData[] chunks) {
        nodeMD.setLastLiveSign(System.currentTimeMillis());
        this.fNodeManager.updateNode(nodeMD);

        Set unAssociatedChunks = new HashSet();
        for (int i = 0; i < chunks.length; i++) {
            synchronized (this.fFileSystem) {
                String filePath = this.fFileSystem.getFilePath(chunks[i]
                        .getFileId());
                if ((null == filePath)
                        || (!this.fFileSystem.replaceChunkToFile(chunks[i],
                                filePath))) {
                    unAssociatedChunks.add(chunks[i]);
                } else {
                    long maxChunkSize = this.fFileSystem
                            .getMaxChunkSize(filePath);
                    this.fNodeManager.addNodeToChunk(chunks[i].getId(),
                            maxChunkSize, nodeMD);
                }
            }
        }

        return (ChunkMetaData[]) unAssociatedChunks
                .toArray(new ChunkMetaData[unAssociatedChunks.size()]);
    }

    /**
     * @param nodeMD
     * @param files
     */
    public void processFileMetaData(NodeMetaData nodeMD, FileMetaData[] files) {
        nodeMD.setLastLiveSign(System.currentTimeMillis());
        this.fNodeManager.updateNode(nodeMD);

        for (int i = 0; i < files.length; i++) {
            this.fFileSystem.createDirectories(PathUtil
                    .getParentDirectoryPath(files[i].getFilePath()));
            this.fFileSystem.createFile(files[i].getFilePath(), files[i]
                    .getFileId());
            this.fFileSystem.setMaxChunkSize(files[i].getFilePath(), files[i]
                    .getMaxChunkSize());
        }
    }

    /**
     * @return a generated chunk id
     */
    public static synchronized String getId() {
        String result = null;
        long ms = System.currentTimeMillis();

        if (Long.MAX_VALUE == fCounter) {
            fCounter = 0;
        } else {
            fCounter++;
        }
        result = ms + "_" + fCounter;

        return result;
    }

    /**
     * @param nodeMD
     * @return if succeed or not
     * @throws IOException
     */
    public boolean sendGetNodeInformation(NodeMetaData nodeMD)
            throws IOException {
        Connection dnsClient = new Connection(nodeMD.getHostAddress(),
                nodeMD.getPort());
        try {
            nodeMD = ServerFaceDNS.getNodeInformation(dnsClient);
        } catch (IOException e) {
            return false;
        }
        dnsClient.close();

        // TODO really update?
        nodeMD.setLastLiveSign(System.currentTimeMillis());
        this.fNodeManager.updateNode(nodeMD);

        return true;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new MetaDataServer(Configuration.getInstance()).startServer();
    }
}
