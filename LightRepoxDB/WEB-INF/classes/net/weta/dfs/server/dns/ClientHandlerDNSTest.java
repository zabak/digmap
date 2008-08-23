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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/dns/ClientHandlerDNSTest.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.CommandRequest;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceDNS;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.server.mds.vfs.VirtualFileSystem;

/**
 * ClientHandlerDNSTest tests both, ClientHandlerDNS and ServerFaceDNS.
 * 
 * created on 14.01.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 * 
 */
public class ClientHandlerDNSTest extends TestCase {

    private MetaDataServer fMds;

    private DataNodeServer fDns;

    private Connection fDnsConn;

    private long fChunkSize = (long) (0.1 * 1024 * 1024);

    private byte fTransportValue = 8;

    private static FileMetaData fFileMD1;

    private static ChunkMetaData fChunkMD1_1;

    private static ChunkMetaData fChunkMD1_2;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
        this.fDns = new DataNodeServer(Configuration.getInstance());
        this.fDns.startServer();
        this.fDnsConn = new Connection(this.fDns.getIpAddress(), this.fDns
                .getPort());
    }

    protected void tearDown() throws Exception {
        this.fMds.stopServer();
        this.fDns.stopServer();
        this.fDnsConn.close();
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    public void testWriteChunk() throws Exception {
        String filePath = "/dir1/dir2/file1.test";

        // create file on mds
        VirtualFileSystem fs = this.fMds.getFileSystem();
        fs.createFile(filePath, MetaDataServer.getId());
        assertTrue(fs.exists(filePath));

        String fileID = fs.getFileId(filePath);
        fFileMD1 = new FileMetaData(fileID, filePath);
        fChunkMD1_1 = new ChunkMetaData("chunkId1_1", fileID, 1);
        fChunkMD1_2 = new ChunkMetaData("chunkId1_2", fileID, 2);

        // write the chunks
        writeChunk(fFileMD1, fChunkMD1_1);
        writeChunk(fFileMD1, fChunkMD1_2);
        Thread.sleep(1000);

        // does it exist on the mds?
        ChunkMetaData[] chunkMDs = fs.getFileChunks(fFileMD1.getFilePath());
        assertNotNull(chunkMDs);
        assertEquals(2, chunkMDs.length);

        Collection collection = Arrays.asList(chunkMDs);
        assertTrue(collection.contains(fChunkMD1_1));
        assertTrue(collection.contains(fChunkMD1_2));
    }

    private void writeChunk(FileMetaData fileMD, ChunkMetaData chunkMD)
            throws IOException, CommandException {
        fileMD.setMaxChunkSize(this.fChunkSize);
        chunkMD.setSize(this.fChunkSize);
        ServerFaceDNS.openWriteConnection(this.fDnsConn, fileMD, chunkMD);
        this.fDnsConn.writeInt((int) this.fChunkSize);
        byte[] bytes = new byte[4096];
        int pos = 0;
        for (int i = 0; i < this.fChunkSize; i++) {
            bytes[pos] = this.fTransportValue;
            pos++;
            if (pos == bytes.length || i == this.fChunkSize - 1) {
                this.fDnsConn.write(bytes, 0, pos);
                pos = 0;
            }
        }
        ServerFaceDNS.closeWriteConnection(this.fDnsConn);
        this.fDnsConn.close();
    }

    /**
     * @throws Exception
     */
    public void testReadChunk() throws Exception {
        readChunk(fChunkMD1_1);
        readChunk(fChunkMD1_2);
    }

    /**
     * @param chunkMD
     * @throws IOException
     * @throws CommandException
     */
    private void readChunk(ChunkMetaData chunkMD) throws IOException,
            CommandException {
        this.fDnsConn.connect();
        long remainingChunkSize = ServerFaceDNS.openReadConnection(
                this.fDnsConn, chunkMD);
        assertEquals(fChunkMD1_1.getSize(), remainingChunkSize);
        byte[] bytes = new byte[4075];
        int length = -1;
        while ((remainingChunkSize > 0)
                && (length = this.fDnsConn.read(bytes)) != -1) {
            for (int i = 0; i < length; i++) {
                assertEquals(this.fTransportValue, bytes[i]);
            }
            remainingChunkSize -= length;
        }
        assertEquals(0, remainingChunkSize);
        this.fDnsConn.close();
    }

    /**
     * @throws Exception
     */
    public void testGetNodeInformation() throws Exception {
        NodeMetaData nodeMD = ServerFaceDNS.getNodeInformation(this.fDnsConn);
        assertEquals(nodeMD.getHostAddress(), this.fDns.getIpAddress());
        assertEquals(nodeMD.getPort(), this.fDns.getPort());
        assertEquals(nodeMD.getFreeDiskSpace(), this.fDns.getChunkManager()
                .getFreeDiskSpace());
    }

    /**
     * @throws Exception
     */
    public void testDeleteChunk() throws Exception {
        // delete 1st chunk
        assertTrue(ServerFaceDNS.deleteChunk(this.fDnsConn, fChunkMD1_1));
        // delete 2nd chunk
        assertTrue(ServerFaceDNS.deleteChunk(this.fDnsConn, fChunkMD1_2));
    }

    /**
     * @throws Exception
     */
    public void testUnknownCommand() throws Exception {
        CommandRequest request = new CommandRequest(-1, fChunkMD1_1);
        this.fDnsConn.sendRequest(request);

        try {
            this.fDnsConn.receiveResponse();
            fail("unknown command");
        } catch (CommandException e) {
        }
    }
}
