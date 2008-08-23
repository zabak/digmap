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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/dns/ChunkManagerTest.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.util.FileUtil;

/**
 * ChunkManagerTest
 * 
 * <br/><br/>created on 01.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class ChunkManagerTest extends TestCase {

    private String fChunkFolder;

    private long fChunkSize = 1016;

    private int fMaxDiscSpace = 8;

    private ChunkManager fChunkManager;

    private FileMetaData fFileMD1 = new FileMetaData("fileId1",
            "/dir1/dir2/file1.test");

    private FileMetaData fFileMD2 = new FileMetaData("fileId2",
            "/dir1/dir2/file2.test");

    private ChunkMetaData fChunkMD1_1 = new ChunkMetaData("chunk_id1",
            this.fFileMD1.getFileId(), 1);

    private ChunkMetaData fChunkMD1_2 = new ChunkMetaData("chunk_id2",
            this.fFileMD1.getFileId(), 2);

    private ChunkMetaData fChunkMD2_1 = new ChunkMetaData("chunk_id3",
            this.fFileMD2.getFileId(), 1);

    protected void setUp() throws Exception {
        super.setUp();
        this.fChunkFolder = Configuration.getInstance().getProperty(
                Configuration.CHUNK_DIRECTORY);
        this.fChunkManager = new ChunkManager(this.fChunkFolder,
                this.fMaxDiscSpace);
        this.fFileMD1.setMaxChunkSize(this.fChunkSize);
        this.fFileMD2.setMaxChunkSize(this.fChunkSize);
    }

    protected void tearDown() throws Exception {
        this.fChunkManager.deleteAll();
        File file = new File(this.fChunkFolder);
        assertFalse(file.exists());
        assertEquals(this.fMaxDiscSpace * 1024 * 1024, this.fChunkManager
                .getFreeDiskSpace());
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testConstructor() throws IOException {
        // create new chunk dir
        File file = new File(this.fChunkFolder);
        assertTrue(file.isDirectory());
        assertTrue(file.exists());
        assertTrue(file.canWrite());
        assertEquals(this.fMaxDiscSpace * 1024 * 1024, this.fChunkManager
                .getFreeDiskSpace());

        // chunk dir already exist
        this.fChunkManager = new ChunkManager(this.fChunkFolder,
                this.fMaxDiscSpace);
        assertTrue(file.isDirectory());
        assertTrue(file.exists());
        assertTrue(file.canWrite());

        assertTrue(this.fChunkManager.deleteAll());
        assertFalse(file.exists());
    }

    /**
     * @throws Exception
     */
    public void testWriteReadChunk() throws Exception {
        writeChunks();
        // check chunks
        assertTrue(this.fChunkManager.exists(this.fChunkMD1_1));
        assertTrue(this.fChunkManager.exists(this.fChunkMD1_2));
        assertTrue(this.fChunkManager.exists(this.fChunkMD2_1));
        assertEquals(this.fChunkSize, this.fChunkManager
                .getChunkSize(this.fChunkMD1_1));
        assertEquals(this.fChunkSize, this.fChunkManager
                .getChunkSize(this.fChunkMD1_2));
        assertEquals(this.fChunkSize, this.fChunkManager
                .getChunkSize(this.fChunkMD2_1));
        assertEquals(-1, this.fChunkManager.getChunkSize(new ChunkMetaData(
                "soWrong", "soUnimportant ", 0)));
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - 3 * this.fChunkSize,
                this.fChunkManager.getFreeDiskSpace());

        // check non exists
        ChunkMetaData chunkMetaData = new ChunkMetaData("chunk_idX",
                this.fFileMD1.getFilePath(), 3);
        assertFalse(this.fChunkManager.exists(chunkMetaData));
        chunkMetaData = new ChunkMetaData("chunk_idX", "/dir1/dir2/file1.tes",
                1);
        assertFalse(this.fChunkManager.exists(chunkMetaData));

        // check read chunks
        checkResult(this.fChunkManager, this.fChunkMD1_1);
        checkResult(this.fChunkManager, this.fChunkMD1_2);
        checkResult(this.fChunkManager, this.fChunkMD2_1);
        try {
            checkResult(this.fChunkManager, new ChunkMetaData("soWrong",
                    "soUnimportant ", 0));
            fail();
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    private void writeChunks() throws Exception {
        // write file1 chunk1
        InputStream iStream = getIStream();
        assertEquals(this.fChunkSize, this.fChunkManager.writeChunk(iStream,
                this.fFileMD1, this.fChunkMD1_1));
        iStream.close();

        // write file1 chunk2
        iStream = getIStream();
        assertEquals(this.fChunkSize, this.fChunkManager.writeChunk(iStream,
                this.fFileMD1, this.fChunkMD1_2));
        iStream.close();

        // write file2 chunk2
        iStream = getIStream();
        assertEquals(this.fChunkSize, this.fChunkManager.writeChunk(iStream,
                this.fFileMD2, this.fChunkMD2_1));
        iStream.close();
    }

    /**
     */
    public void testIdDiffers() {
        FileMetaData fileMD = new FileMetaData("fileIdXYZ",
                "/dir1/dir2/fileXYZ.test");
        try {
            this.fChunkManager.writeChunk(System.in, fileMD, this.fChunkMD1_1);
            fail();
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    /**
     */
    public void testPathDiffers() {
        ChunkMetaData chunkMetaData = new ChunkMetaData("chunk_idX",
                "somthingWrong", 3);
        try {
            this.fChunkManager.writeChunk(System.in, this.fFileMD1,
                    chunkMetaData);
            fail();
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    /**
     * @throws Exception
     */
    public void testChunkAlreadyExist() throws Exception {
        writeChunks();
        try {
            this.fChunkManager.writeChunk(System.in, this.fFileMD1,
                    this.fChunkMD1_1);
            fail();
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    /**
     * @throws Exception
     */
    public void testGetFileMetaDatas() throws Exception {
        writeChunks();
        FileMetaData[] chunkMDs = this.fChunkManager.getFileMetaDatas();
        assertEquals(2, chunkMDs.length);

        Collection collection = Arrays.asList(chunkMDs);
        assertTrue(collection.contains(this.fFileMD1));
        assertTrue(collection.contains(this.fFileMD2));
    }

    /**
     * @throws Exception
     */
    public void testGetChunkMetaDatas() throws Exception {
        writeChunks();
        ChunkMetaData[] chunkMDs = this.fChunkManager.getChunkMetaDatas();
        assertEquals(3, chunkMDs.length);

        Collection collection = Arrays.asList(chunkMDs);
        assertTrue(collection.contains(this.fChunkMD1_1));
        assertTrue(collection.contains(this.fChunkMD1_2));
        assertTrue(collection.contains(this.fChunkMD2_1));
    }

    /**
     * @throws Exception
     */
    public void testDeleteChunk() throws Exception {
        writeChunks();
        File file = new File(this.fChunkFolder);
        assertEquals(2, file.list().length);

        // delete file1 chunk1
        assertEquals(3, this.fChunkManager.getChunkMetaDatas().length);
        assertTrue(this.fChunkManager.deleteChunk(this.fChunkMD1_1));
        assertFalse(this.fChunkManager.exists(this.fChunkMD1_1));
        assertEquals(2, this.fChunkManager.getChunkMetaDatas().length);
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - 2 * this.fChunkSize,
                this.fChunkManager.getFreeDiskSpace());

        // delete file1 chunk1 again
        assertFalse(this.fChunkManager.deleteChunk(this.fChunkMD1_1));
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - 2 * this.fChunkSize,
                this.fChunkManager.getFreeDiskSpace());

        // delete file1 chunk2
        assertTrue(this.fChunkManager.deleteChunk(this.fChunkMD1_2));
        assertFalse(this.fChunkManager.exists(this.fChunkMD1_2));
        assertEquals(1, this.fChunkManager.getChunkMetaDatas().length);
        assertEquals(1, file.list().length);
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - this.fChunkSize,
                this.fChunkManager.getFreeDiskSpace());
    }

    /**
     * @throws Exception
     */
    public void testChunkManagerRestart() throws Exception {
        long filesMaxChunkSize = this.fChunkSize + 32;
        this.fFileMD1.setMaxChunkSize(filesMaxChunkSize);

        // write chunk
        InputStream iStream = getIStream();
        assertEquals(this.fChunkSize, this.fChunkManager.writeChunk(iStream,
                this.fFileMD1, this.fChunkMD1_1));
        iStream.close();
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - filesMaxChunkSize,
                this.fChunkManager.getFreeDiskSpace());

        // after restart
        this.fChunkManager = new ChunkManager(this.fChunkFolder,
                this.fMaxDiscSpace);
        assertEquals(this.fMaxDiscSpace * 1024 * 1024 - filesMaxChunkSize,
                this.fChunkManager.getFreeDiskSpace());
        assertTrue(this.fChunkManager.exists(this.fChunkMD1_1));
        checkResult(this.fChunkManager, this.fChunkMD1_1);
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

        return iStream;
    }

    private void checkResult(ChunkManager chunkManager,
            ChunkMetaData chunkMetaData) throws IOException {

        PipedOutputStream poStream = new PipedOutputStream();
        PipedInputStream piStream = new PipedInputStream(poStream);
        chunkManager.readChunk(poStream, chunkMetaData);
        poStream.close();

        assertEquals(this.fChunkSize, piStream.available());
        int result;
        while ((result = piStream.read()) != -1) {
            assertEquals(7, result);
        }
        piStream.close();
    }
}