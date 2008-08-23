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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/dns/DataNodeServerTest.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.server.mds.vfs.VirtualFileSystem;
import net.weta.dfs.util.FileUtil;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * DataNodeServerTest comment
 * 
 * created on 28.01.2005
 * 
 * @author ms
 * @version $Revision: 1.2 $
 */
public class DataNodeServerTest extends TestCase {

    static Category fLogger = Logger.getLogger(DataNodeServerTest.class);
    
    private MetaDataServer fMds;

    private DataNodeServer fDns;

    private long fChunkSize = 5000;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
        this.fDns = new DataNodeServer(Configuration.getInstance());
        this.fDns.startServer(); 
    }

    protected void tearDown() throws Exception {
        this.fDns.stopServer();
        this.fMds.stopServer();
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testStartStopServer() throws IOException {
        //start again
        this.fDns.startServer();
        
        // test running
        assertNotNull(this.fDns.getChunkManager());
        assertNotNull(this.fDns.getIpAddress());
        assertTrue(this.fDns.getPort()>0);
        NodeMetaData nodeMD = new NodeMetaData(this.fDns.getIpAddress(),
                this.fDns.getPort(), this.fDns.getChunkManager()
                        .getFreeDiskSpace());
        assertEquals(nodeMD, this.fDns.getNodeMD());
        assertTrue(this.fDns.isRunning());

        // stop
        this.fDns.stopServer();
        assertNotNull(this.fDns.getChunkManager());
        assertFalse(this.fDns.isRunning());
        this.fDns.stopServer();

        // start again
        this.fDns.startServer();
        assertNotNull(this.fDns.getChunkManager());
        assertNotNull(this.fDns.getIpAddress());
        assertTrue(this.fDns.getPort()>0);
        assertEquals(nodeMD, this.fDns.getNodeMD());
        assertTrue(this.fDns.isRunning());
        
        //mds down
        this.fDns.stopServer();
        this.fMds.stopServer();
        try {
            this.fDns.startServer();
            fail("mds is down");
        } catch (IOException e) {
            fLogger.info("!!!!! TEST-ERROR-OUTPUT (wanted)");
        }        
    }

    /**
     * @throws IOException
     */
    public void testGetIP_Port() throws IOException {
        assertEquals(Configuration.getInstance().getPropertyAsInt(
                Configuration.DATA_NODE_PORT), this.fDns.getPort());
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     * @throws CommandException
     */
    public void testSendChunkWritten() throws IOException,
            InterruptedException, CommandException {
        String filePath = "/file.test";
        String fileID = "fileID";

        // create file at mds
        VirtualFileSystem fs = this.fMds.getFileSystem();
        assertTrue(fs.createFile(filePath, fileID));
        assertTrue(fs.exists(filePath));

        ChunkMetaData chunkMD = new ChunkMetaData("chunkId_1", fileID, 1);
        chunkMD.setSize(this.fChunkSize);
        // send chunk written
        this.fDns.sendChunkWritten(chunkMD);
        Thread.sleep(1000);

        // does it exist on the mds?
        ChunkMetaData[] chunkMDs = fs.getFileChunks(filePath);
        assertNotNull(chunkMDs);
        assertEquals(1, chunkMDs.length);
        assertEquals(chunkMD, chunkMDs[0]);
        assertEquals(this.fChunkSize, chunkMDs[0].getSize());
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testInitDataNode() throws IOException, InterruptedException {
        this.fChunkSize = 500;
        String filePath1 = "/dir1/dir3/file1.test";
        String filePath2 = "/dir1/dir3/file2.test";

        VirtualFileSystem fs = this.fMds.getFileSystem();
        fs.createFile(filePath1, MetaDataServer.getId());
        fs.createFile(filePath2, MetaDataServer.getId());

        String fileId1 = fs.getFileId(filePath1);
        assertNotNull(fileId1);
        String fileId2 = fs.getFileId(filePath2);
        assertNotNull(fileId2);

        FileMetaData fileMD1 = new FileMetaData(fileId1, filePath1);
        FileMetaData fileMD2 = new FileMetaData(fileId2, filePath2);
        fileMD1.setMaxChunkSize(this.fChunkSize);
        fileMD2.setMaxChunkSize(this.fChunkSize);

        ChunkMetaData chunkMD1_1 = new ChunkMetaData("chunkId1_1", fileId1, 1);
        ChunkMetaData chunkMD1_2 = new ChunkMetaData("chunkId1_2", fileId1, 2);
        ChunkMetaData chunkMD2_1 = new ChunkMetaData("chunkId2_1", fileId2, 1);

        // write chunk 1 of file 1
        InputStream iStream = getIStream();
        this.fDns.getChunkManager().writeChunk(iStream, fileMD1, chunkMD1_1);
        iStream.close();
        assertTrue(this.fDns.getChunkManager().exists(chunkMD1_1));

        // write chunk 2 of file 1
        iStream = getIStream();
        this.fDns.getChunkManager().writeChunk(iStream, fileMD1, chunkMD1_2);
        iStream.close();
        assertTrue(this.fDns.getChunkManager().exists(chunkMD1_2));

        // write chunk 1 of file 2
        iStream = getIStream();
        this.fDns.getChunkManager().writeChunk(iStream, fileMD2, chunkMD2_1);
        iStream.close();
        assertTrue(this.fDns.getChunkManager().exists(chunkMD2_1));

        // shutdown servers
        this.fDns.stopServer();
        this.fMds.stopServer();

        // start metadataserver
        this.fMds.startServer();
        fs = this.fMds.getFileSystem();

        assertFalse(fs.exists(fileMD1.getFilePath()));
        assertFalse(fs.exists(fileMD2.getFilePath()));

        // start datanodeserver
        this.fDns.startServer();
        Thread.sleep(1000);

        assertTrue(this.fMds.getFileSystem().exists(fileMD1.getFilePath()));
        assertTrue(this.fMds.getFileSystem().exists(fileMD2.getFilePath()));

        // check chunks file1
        ChunkMetaData[] chunkMDs = fs.getFileChunks(filePath1);
        assertEquals(2, chunkMDs.length);
        Collection collection = Arrays.asList(chunkMDs);
        assertTrue(collection.contains(chunkMD1_1));
        assertTrue(collection.contains(chunkMD1_2));

        // check chunks file2
        chunkMDs = fs.getFileChunks(filePath2);
        assertEquals(1, chunkMDs.length);

        // delete chunks
        assertTrue(this.fDns.getChunkManager().deleteAll());
    }

    private InputStream getIStream() throws IOException {

        assertTrue(this.fChunkSize <= 1016);// else write() will hang

        PipedOutputStream poStream = new PipedOutputStream();
        InputStream iStream = new PipedInputStream(poStream);

        FileUtil.writeInt(poStream, (int) this.fChunkSize);
        for (long i = 0; i < this.fChunkSize; i++) {
            poStream.write(7);
        }
        FileUtil.writeInt(poStream, 0);

        poStream.close();
        assertEquals(this.fChunkSize + 8, iStream.available());
        // +8 because of writing ints

        return iStream;
    }
}