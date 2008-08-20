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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/MetaDataServerTest.java,v $
 */

package net.weta.dfs.server.mds;

import java.io.IOException;
import java.net.ServerSocket;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.util.PathUtil;

import org.apache.log4j.Category;

/**
 * MetaDataServerTest
 * 
 * created on 12.01.2005
 * 
 * @author sg
 * @version $Revision: 1.1 $
 */
public class MetaDataServerTest extends TestCase {

    static Category fLogger = Category.getInstance(MetaDataServerTest.class
            .getName());

    private MetaDataServer fMds;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
    }

    protected void tearDown() throws Exception {
        this.fMds.stopServer();
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testStartStopServer() throws IOException {
        // test running
        assertNotNull(this.fMds.getFileSystem());
        assertNotNull(this.fMds.getNodeManager());
        assertNotNull(this.fMds.getIpAddress());
        assertTrue(this.fMds.getPort() > 0);
        assertTrue(this.fMds.isRunning());

        // stop
        this.fMds.stopServer();
        assertNull(this.fMds.getFileSystem());
        assertNull(this.fMds.getNodeManager());
        assertFalse(this.fMds.isRunning());
        this.fMds.stopServer();

        // start again
        this.fMds.startServer();
        assertNotNull(this.fMds.getFileSystem());
        assertNotNull(this.fMds.getNodeManager());
        assertNotNull(this.fMds.getIpAddress());
        assertTrue(this.fMds.getPort() > 0);
        assertTrue(this.fMds.isRunning());

        // port in use
        int port = this.fMds.getPort();
        this.fMds.stopServer();
        ServerSocket socket = new ServerSocket(port);
        try {
            this.fMds.startServer();
            fail("port in use");
        } catch (IOException e) {
            fLogger.info("!!!!! TEST-ERROR-OUTPUT (wanted)");
        }
        socket.close();
        this.fMds.stopServer();
    }

    /**
     * 
     */
    public void testGetId() {
        String oldChunkID = "-";
        String newChunkID = "-";
        final long MAX_IDS = 100000;

        long startTime = System.currentTimeMillis();
        for (long i = 0; i < MAX_IDS; i++) {
            newChunkID = MetaDataServer.getId();
            assertFalse(newChunkID == oldChunkID);
            oldChunkID = newChunkID;
        }
        long stopTime = System.currentTimeMillis();

        fLogger.debug("different possible IDs on this calculator: " + MAX_IDS
                / (stopTime - startTime) + "IDs/ms");
    }

    /**
     * 
     */
    public void testProcessFileMetaData() {
        String filePath1 = "/test5476474.txt";
        String filePath2 = "/dir/test5476474.txt";

        FileMetaData[] files = new FileMetaData[2];
        files[0] = new FileMetaData(MetaDataServer.getId(), filePath1);
        files[1] = new FileMetaData(MetaDataServer.getId(), filePath2);
        files[0].setMaxChunkSize(100);
        files[1].setMaxChunkSize(200);

        NodeMetaData nodeMD = new NodeMetaData("127.0.0.1", 12, 1000);
        this.fMds.processFileMetaData(nodeMD, files);

        assertTrue(this.fMds.getFileSystem().isFile(filePath1));
        assertTrue(this.fMds.getFileSystem().isDirectory(
                PathUtil.getParentDirectoryPath(filePath2)));
        assertTrue(this.fMds.getFileSystem().isFile(filePath2));

        assertNotNull(this.fMds.getFileSystem().getFileChunks(filePath1));
        assertEquals(0,
                (this.fMds.getFileSystem().getFileChunks(filePath1)).length);
        assertNotNull(this.fMds.getFileSystem().getDirectoryContent(
                PathUtil.getParentDirectoryPath(filePath2)));
        assertEquals(100, this.fMds.getFileSystem().getMaxChunkSize(filePath1));
        assertEquals(200, this.fMds.getFileSystem().getMaxChunkSize(filePath2));
        assertEquals(1, this.fMds.getNodeManager().getNodeCount());
    }

    /**
     * 
     */
    public void testProcessChunkMetaData() {
        String filePath1 = "/test5476474.txt";
        String fileId1 = "/test5476474.txtID";
        String filePath2 = "/dir/test5476474.txt";
        String fileId2 = "/dir/test5476474.txtID";

        // create files
        assertTrue(this.fMds.getFileSystem().createFile(filePath1, fileId1));
        assertTrue(this.fMds.getFileSystem().createDirectories(
                PathUtil.getParentDirectoryPath(filePath2)));
        assertTrue(this.fMds.getFileSystem().createFile(filePath2, fileId2));
        assertTrue(this.fMds.getFileSystem().isFile(filePath1));
        assertTrue(this.fMds.getFileSystem().isFile(filePath2));

        // process chunks
        ChunkMetaData[] chunks = new ChunkMetaData[4];
        chunks[0] = new ChunkMetaData("chunk1_1", fileId1, 1);
        chunks[1] = new ChunkMetaData("chunk1_2", fileId1, 2);
        chunks[2] = new ChunkMetaData("chunk1_3", fileId1, 3);
        chunks[3] = new ChunkMetaData("chunk2_1", fileId2, 1);
        NodeMetaData nodeMD = new NodeMetaData("127.0.0.1", 12, 1000);
        this.fMds.processChunkMetaData(nodeMD, chunks);

        // check results
        assertEquals(3,
                this.fMds.getFileSystem().getFileChunks(filePath1).length);
        assertEquals(1,
                this.fMds.getFileSystem().getFileChunks(filePath2).length);
        assertEquals(1, this.fMds.getNodeManager().getNodeCount());
        assertEquals(nodeMD, this.fMds.getNodeManager().getChunkNodes(
                chunks[0].getId())[0]);

        // check process none-existing chunk
        ChunkMetaData[] nonExistingChunks = new ChunkMetaData[1];
        nonExistingChunks[0] = new ChunkMetaData("chunkABC_1", "/doesnotexist",
                1);
        assertEquals(1, this.fMds.processChunkMetaData(nodeMD,
                nonExistingChunks).length);
    }

    /**
     * @throws IOException
     */
    public void testSendGetNodeInformation() throws IOException {
        // reachable
        DataNodeServer dns = new DataNodeServer(Configuration.getInstance());
        dns.startServer();
        NodeMetaData nodeMD = new NodeMetaData(dns.getIpAddress(), dns
                .getPort(), dns.getChunkManager().getFreeDiskSpace());
        this.fMds.stopServer();
        this.fMds.startServer();
        assertTrue(this.fMds.sendGetNodeInformation(nodeMD));
        dns.stopServer();

        // not reachable
        assertFalse(this.fMds.sendGetNodeInformation(nodeMD));
    }

    /**
     * @throws IOException
     */
    public void testRecoverMds() throws IOException {
        DataNodeServer dns = new DataNodeServer(Configuration.getInstance());
        dns.startServer();
        NodeMetaData nodeMD = new NodeMetaData(dns.getIpAddress(), dns
                .getPort(), dns.getChunkManager().getFreeDiskSpace());
        assertNotNull(this.fMds.getNodeManager().getNode(nodeMD.getId()));

        this.fMds.stopServer();
        this.fMds.startServer();
        assertNull(this.fMds.getNodeManager().getNode(nodeMD.getId()));
        dns.sendLifeSign();
        assertNotNull(this.fMds.getNodeManager().getNode(nodeMD.getId()));
        dns.stopServer();
    }
}
