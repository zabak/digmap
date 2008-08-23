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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/ClientHandlerMDSTest.java,v $
 */

package net.weta.dfs.server.mds;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceMDS;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;

/**
 * ClientHandlerMDSTest tests both, ClientHandlerMDS and
 * ServerFaceMDS.
 * 
 * created on 19.01.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class ClientHandlerMDSTest extends TestCase {

    private Connection fMdsClient;

    private MetaDataServer fMds;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
        this.fMdsClient = new Connection("127.0.0.1", this.fMds.getPort());
    }

    protected void tearDown() throws Exception {
        this.fMds.stopServer();
        this.fMdsClient.close();
        super.tearDown();
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    public void testFile() throws IOException, CommandException {
        String filePath = "aFile";
        String fileId;
        // create File and test all file... calls
        assertTrue(ServerFaceMDS.fileCreate(this.fMdsClient, filePath));
        assertTrue(this.fMds.getFileSystem().isFile(filePath));
        assertTrue(ServerFaceMDS.fileExists(this.fMdsClient, filePath));
        fileId = ServerFaceMDS.fileGetId(this.fMdsClient, filePath);
        assertNotNull(fileId);
        assertTrue(ServerFaceMDS.fileIsFile(this.fMdsClient, filePath));
        assertFalse(ServerFaceMDS.fileIsDirectory(this.fMdsClient, filePath));
        assertNotNull(ServerFaceMDS.fileGetChunks(this.fMdsClient, filePath));
        assertEquals(0, ServerFaceMDS.fileGetLength(this.fMdsClient, filePath));
        assertNull(ServerFaceMDS.fileList(this.fMdsClient, filePath));
        assertFalse(ServerFaceMDS.fileMkDirs(this.fMdsClient, filePath));
        assertTrue(ServerFaceMDS.fileSetMaxChunkSize(this.fMdsClient,
                new FileMetaData(fileId, filePath)));

        // file already exists
        assertFalse(ServerFaceMDS.fileCreate(this.fMdsClient, filePath));

        // parent don't exists
        try {
            ServerFaceMDS.fileCreate(this.fMdsClient, "dir/file");
            fail("parent does not exists");
        } catch (CommandException e) {
        }

        // delete file
        assertTrue(ServerFaceMDS.fileDelete(this.fMdsClient, filePath));
        assertFalse(this.fMds.getFileSystem().exists(filePath));
        assertFalse(ServerFaceMDS.fileDelete(this.fMdsClient, filePath));

        // check standarts
        assertFalse(ServerFaceMDS.fileExists(this.fMdsClient, filePath));
        fileId = ServerFaceMDS.fileGetId(this.fMdsClient, filePath);
        assertNull(fileId);
        assertFalse(ServerFaceMDS.fileIsFile(this.fMdsClient, filePath));
        assertFalse(ServerFaceMDS.fileIsDirectory(this.fMdsClient, filePath));
        assertNull(ServerFaceMDS.fileGetChunks(this.fMdsClient, filePath));
        assertEquals(0, ServerFaceMDS.fileGetLength(this.fMdsClient, filePath));
        assertNull(ServerFaceMDS.fileList(this.fMdsClient, filePath));
        assertFalse(ServerFaceMDS.fileSetMaxChunkSize(this.fMdsClient,
                new FileMetaData("someID", filePath)));
        assertTrue(ServerFaceMDS.fileMkDirs(this.fMdsClient, filePath));
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    public void testFileChunks() throws IOException, CommandException {
        String filePath = "/aFile";
        String fileId = "aFileID";
        assertTrue(this.fMds.getFileSystem().createFile(filePath, fileId));

        // add chunks
        DataNodeServer dns = new DataNodeServer(Configuration.getInstance());
        dns.startServer();
        NodeMetaData node = new NodeMetaData(dns.getIpAddress(), dns.getPort(),
                20000000);
        ChunkMetaData[] chunkMds = new ChunkMetaData[10];
        for (int i = 0; i < chunkMds.length; i++) {
            chunkMds[i] = new ChunkMetaData("chunkId" + i, fileId, i);
            chunkMds[i].setSize(1);
            ServerFaceMDS.chunkWritten(this.fMdsClient, node, chunkMds[i]);
            assertEquals(i + 1, this.fMds.getFileSystem().getFileChunks(
                    filePath).length);
            assertEquals(node, this.fMds.getNodeManager().getChunkNodes(
                    chunkMds[i].getId())[0]);
        }

        // get chunks
        ChunkMetaData[] recChunks = ServerFaceMDS.fileGetChunks(
                this.fMdsClient, filePath);
        assertEquals(chunkMds.length, recChunks.length);
        Collection collection = Arrays.asList(recChunks);
        for (int i = 0; i < chunkMds.length; i++) {
            assertTrue(collection.contains(chunkMds[i]));
        }

        // get fileLength
        assertEquals(chunkMds.length, ServerFaceMDS.fileGetLength(
                this.fMdsClient, filePath));

        // get node
        for (int i = 0; i < chunkMds.length; i++) {
            assertEquals(node, ServerFaceMDS.nodeGetFromChunk(this.fMdsClient,
                    chunkMds[i].getId())[0]);
        }

        // delete file
        assertTrue(ServerFaceMDS.fileDelete(this.fMdsClient, filePath));
        assertFalse(this.fMds.getFileSystem().exists(filePath));
        assertNull(ServerFaceMDS.fileGetChunks(this.fMdsClient, filePath));
        assertEquals(0, ServerFaceMDS.fileGetLength(this.fMdsClient, filePath));
        for (int i = 0; i < chunkMds.length; i++) {
            assertNull(ServerFaceMDS.nodeGetFromChunk(this.fMdsClient,
                    chunkMds[i].getId()));
            assertFalse(dns.getChunkManager().exists(chunkMds[i]));
        }

        // try add chunk to non-existent file
        ChunkMetaData chunkMD = new ChunkMetaData("aId", fileId + 7, 2);
        try {
            ServerFaceMDS.chunkWritten(this.fMdsClient, node, chunkMD);
            fail("file does not exist");
        } catch (CommandException e) {
        }
        dns.stopServer();
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    public void testDirectory() throws IOException, CommandException {
        String dirPath = "/myFolder/myFolder";
        // create directory and test all file... calls
        assertTrue(ServerFaceMDS.fileMkDirs(this.fMdsClient, dirPath));
        assertTrue(this.fMds.getFileSystem().isDirectory(dirPath));
        assertTrue(ServerFaceMDS.fileExists(this.fMdsClient, dirPath));
        assertFalse(ServerFaceMDS.fileCreate(this.fMdsClient, dirPath));
        String fileId = ServerFaceMDS.fileGetId(this.fMdsClient, dirPath);
        assertNull(fileId);
        assertFalse(ServerFaceMDS.fileIsFile(this.fMdsClient, dirPath));
        assertNull(ServerFaceMDS.fileGetChunks(this.fMdsClient, dirPath));
        assertEquals(0, ServerFaceMDS.fileGetLength(this.fMdsClient, dirPath));
        assertNotNull(ServerFaceMDS.fileList(this.fMdsClient, dirPath));
        assertFalse(ServerFaceMDS.fileSetMaxChunkSize(this.fMdsClient,
                new FileMetaData("someID", dirPath)));

        // add file & dir
        String fileChild = dirPath + "/aFile";
        String dirChild = dirPath + "/aDir";
        assertTrue(this.fMds.getFileSystem().createFile(fileChild,
                "fileChildID"));
        assertTrue(this.fMds.getFileSystem().createFile(dirChild, "dirChildID"));
        assertEquals(2, ServerFaceMDS.fileList(this.fMdsClient, dirPath).length);

        // dir already exists
        assertFalse(ServerFaceMDS.fileMkDirs(this.fMdsClient, dirPath));

        // delete dir
        assertTrue(ServerFaceMDS.fileDelete(this.fMdsClient, dirPath));
        assertFalse(this.fMds.getFileSystem().isDirectory(dirPath));
        assertFalse(ServerFaceMDS.fileDelete(this.fMdsClient, dirPath));
    }

    /**
     * @throws IOException
     */
    public void testNodeGetAFree() throws IOException {
        assertNull(ServerFaceMDS.nodeGetAFree(this.fMdsClient, 1));

        NodeMetaData nodeMD = new NodeMetaData("host", 22, 23);
        this.fMds.getNodeManager().updateNode(nodeMD);
        assertEquals(nodeMD, ServerFaceMDS.nodeGetAFree(this.fMdsClient, 1));
    }

    /**
     * @throws IOException
     */
    public void testSignalInitDataNode() throws IOException {
        String filePath1 = "file1";
        String filePath2 = "file2";
        String fileId1 = "fileId1";
        String fileId2 = "fileId2";

        NodeMetaData nodeMD = new NodeMetaData("aNode", 22, 100);
        FileMetaData[] files = new FileMetaData[2];
        files[0] = new FileMetaData(fileId1, filePath1);
        files[1] = new FileMetaData(fileId2, filePath2);

        ChunkMetaData[] chunks = new ChunkMetaData[4];
        chunks[0] = new ChunkMetaData("chunkId1_1", fileId1, 1);
        chunks[1] = new ChunkMetaData("chunkId1_2", fileId1, 2);
        chunks[2] = new ChunkMetaData("chunkId1_3", fileId1, 3);
        chunks[3] = new ChunkMetaData("chunkId2_1", fileId2, 1);

        // init chunks before files
        ChunkMetaData[] unAssChunks = ServerFaceMDS.nodeInitChunks(
                this.fMdsClient, nodeMD, chunks);
        assertEquals(4, unAssChunks.length);
        assertTrue(this.fMds.getFileSystem().createFile(filePath2, fileId2));
        assertEquals(3, ServerFaceMDS.nodeInitChunks(this.fMdsClient, nodeMD,
                chunks).length);

        // init files
        ServerFaceMDS.nodeInitFiles(this.fMdsClient, nodeMD, files);
        assertTrue(this.fMds.getFileSystem().isFile(filePath1));
        assertTrue(this.fMds.getFileSystem().isFile(filePath2));

        // init chunks
        assertEquals(0, ServerFaceMDS.nodeInitChunks(this.fMdsClient, nodeMD,
                chunks).length);
        assertEquals(3,
                this.fMds.getFileSystem().getFileChunks(filePath1).length);
        assertEquals(1,
                this.fMds.getFileSystem().getFileChunks(filePath2).length);
    }

    /**
     * @throws IOException
     */
    public void testGetAnId() throws IOException {
        // get chunk Ids
        String id1 = ServerFaceMDS.getAnId(this.fMdsClient);
        assertNotNull(id1);
        String id2 = ServerFaceMDS.getAnId(this.fMdsClient);
        assertNotNull(id2);
        assertNotSame(id1, id2);
    }

    /**
     * @throws IOException
     */
    public void testGetIp() throws IOException {
        assertEquals(this.fMdsClient.getIpAddress(), ServerFaceMDS
                .getIp(this.fMdsClient));
    }

    /**
     * @throws IOException
     */
    public void testSignalLifeSign() throws IOException {
        // unknown node
        NodeMetaData nodeMD = new NodeMetaData("host", 14, 2323);
        assertFalse(ServerFaceMDS.sendLifeSign(this.fMdsClient, nodeMD));

        // known node
        DataNodeServer dns = new DataNodeServer(Configuration.getInstance());
        dns.startServer();
        nodeMD = new NodeMetaData(dns.getIpAddress(), dns.getPort(), 2323);
        assertTrue(ServerFaceMDS.sendLifeSign(this.fMdsClient, nodeMD));
        dns.stopServer();
    }
}
