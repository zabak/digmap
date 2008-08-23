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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/vfs/VirtualFileSystemTest.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.server.mds.vfs.VirtualFileSystem;

/**
 * 
 * created on 12.02.2005
 * 
 * @author jz
 * @version $Revision: 1.2 $
 * 
 */
public class VirtualFileSystemTest extends TestCase {

    private VirtualFileSystem fFileSystem;

    private MetaDataServer fMds;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
        this.fFileSystem = new VirtualFileSystem(this.fMds, "getId");
    }

    protected void tearDown() throws Exception {
        this.fMds.stopServer();
        super.tearDown();
    }

    /**
     */
    public void testCreateDeleteDirectories() {
        // create directories
        String dir1 = "dir1";
        String dir2 = "dir2";
        String dir3 = "dir3";

        assertFalse(this.fFileSystem.isDirectory("/" + dir1));
        assertFalse(this.fFileSystem.isDirectory("/" + dir2));
        assertFalse(this.fFileSystem.isDirectory("/" + dir1 + "/" + dir3));

        assertTrue(this.fFileSystem.createDirectories("/" + dir1));
        assertTrue(this.fFileSystem.createDirectories("/" + dir2));
        assertTrue(this.fFileSystem.createDirectories("/" + dir1 + "/" + dir3));

        assertTrue(this.fFileSystem.isDirectory("/" + dir1));
        assertTrue(this.fFileSystem.isDirectory("/" + dir2));
        assertTrue(this.fFileSystem.isDirectory("/" + dir1 + "/" + dir3));
        // get directory
        String[] content = this.fFileSystem
                .getDirectoryContent(VirtualFileSystem.ROOT_DIRECTORY);
        Collection map = Arrays.asList(content);
        assertTrue(map.contains(dir1));
        assertTrue(map.contains(dir2));
        assertFalse(map.contains(dir3));

        content = this.fFileSystem.getDirectoryContent(dir1);
        map = Arrays.asList(content);
        assertTrue(map.contains(dir3));
        assertFalse(map.contains(dir1));
        // delete directory
        assertTrue(this.fFileSystem.delete("/" + dir1));

        assertFalse(this.fFileSystem.isDirectory("/" + dir1));
        assertTrue(this.fFileSystem.isDirectory("/" + dir2));
        assertFalse(this.fFileSystem.isDirectory("/" + dir1 + "/" + dir3));

        assertTrue(this.fFileSystem.delete("/" + dir2));
        assertFalse(this.fFileSystem.isDirectory("/" + dir2));

        this.fFileSystem.delete(VirtualFileSystem.ROOT_DIRECTORY);
        assertTrue(this.fFileSystem.isDirectory("/"));
    }

    /**
     * 
     */
    public void testGetDirectoryContent() {

        String dir1 = "/dir1";
        String dir2 = "/dir1/dir2";
        String dir3 = "/dir1/dir2/dir3";
        String file1Name = "file1.test";

        assertTrue(this.fFileSystem.createDirectories(dir3));
        this.fFileSystem.createFile(dir2, file1Name, MetaDataServer.getId());

        String[] content = this.fFileSystem
                .getDirectoryContent(VirtualFileSystem.ROOT_DIRECTORY);
        Collection map = Arrays.asList(content);
        assertTrue(map.contains("dir1"));
        assertFalse(map.contains("dir2"));
        assertFalse(map.contains("dir3"));
        assertFalse(map.contains(file1Name));

        content = this.fFileSystem.getDirectoryContent(dir1);
        map = Arrays.asList(content);
        assertTrue(map.contains("dir2"));
        assertFalse(map.contains(file1Name));
        assertFalse(map.contains("dir3"));

        content = this.fFileSystem.getDirectoryContent(dir2);
        map = Arrays.asList(content);
        assertTrue(map.contains("dir3"));
        assertTrue(map.contains(file1Name));
        assertFalse(map.contains("dir1"));

        content = this.fFileSystem.getDirectoryContent(dir3);
        map = Arrays.asList(content);
        assertTrue(map.isEmpty());
    }

    /**
     */
    public void testCreateDeleteFile() {
        String fileName = "aFile";
        String filePath = VirtualFileSystem.ROOT_DIRECTORY + fileName;
        String fileID = MetaDataServer.getId();

        assertFalse(this.fFileSystem.exists(filePath));
        assertTrue(this.fFileSystem.createFile(filePath, fileID));
        assertTrue(this.fFileSystem.exists(filePath));
        assertEquals(fileID, this.fFileSystem.getFileId(filePath));
        assertEquals(filePath, this.fFileSystem.getFilePath(fileID));
        assertEquals(0, this.fFileSystem.getFileChunks(filePath).length);

        // create again
        assertFalse(this.fFileSystem.createFile(filePath, fileID));

        assertTrue(this.fFileSystem.delete(filePath));
        assertFalse(this.fFileSystem.exists(filePath));
        assertNull(this.fFileSystem.getFileChunks(filePath));
        assertNull(this.fFileSystem.getFileId(filePath));
        assertNull(this.fFileSystem.getFilePath(fileID));
    }

    /**
     */
    public void testAddGetChunks() {
        String file = "file.test";

        ChunkMetaData chunkId1 = new ChunkMetaData("23657_0", "file", 1);
        ChunkMetaData chunkId2 = new ChunkMetaData("23657_1", "file", 2);

        this.fFileSystem.createFile("/", file, MetaDataServer.getId());
        assertTrue(this.fFileSystem.isFile("/" + file));
        assertEquals(0, this.fFileSystem.getFileChunks(file).length);

        assertTrue(this.fFileSystem.addChunkToFile(chunkId1, file));
        assertEquals(1, this.fFileSystem.getFileChunks(file).length);

        ChunkMetaData[] chunks = this.fFileSystem.getFileChunks(file);
        Collection map = Arrays.asList(chunks);
        assertTrue(map.contains(chunkId1));

        assertTrue(this.fFileSystem.addChunkToFile(chunkId2, file));
        assertEquals(2, this.fFileSystem.getFileChunks(file).length);

        chunks = this.fFileSystem.getFileChunks(file);
        map = Arrays.asList(chunks);
        assertTrue(map.contains(chunkId1));
        assertTrue(map.contains(chunkId2));

        assertTrue(this.fFileSystem.delete(file));
    }

    /**
     * 
     */
    public void testFileLength() {
        String filePath = "file.test";
        long fileSize = 10000;

        ChunkMetaData chunkMD1 = new ChunkMetaData("23657_0", "file", 1);
        ChunkMetaData chunkMD2 = new ChunkMetaData("23657_1", "file", 2);
        chunkMD1.setSize(fileSize / 2);
        chunkMD2.setSize(fileSize / 2);

        this.fFileSystem.createFile("/", filePath, MetaDataServer.getId());
        assertTrue(this.fFileSystem.isFile("/" + filePath));
        assertEquals(0, this.fFileSystem.getFileChunks(filePath).length);
        assertEquals(0, this.fFileSystem.getFileLength(filePath));

        assertTrue(this.fFileSystem.addChunkToFile(chunkMD1, filePath));
        assertTrue(this.fFileSystem.addChunkToFile(chunkMD2, filePath));
        assertEquals(2, this.fFileSystem.getFileChunks(filePath).length);

        assertEquals(fileSize, this.fFileSystem.getFileLength(filePath));
        assertTrue(this.fFileSystem.delete(filePath));

        // try to get length of a dir
        String dirPath = "/dir8";
        assertTrue(this.fFileSystem.createDirectories(dirPath));
        assertEquals(0, this.fFileSystem.getFileLength(dirPath));

        // try to get length of a nonexistent file
        assertEquals(0, this.fFileSystem.getFileLength("/hulahulahup.hop"));
    }

    /**
     */
    public void testRecurivsDeleteDirs() {
        String dir2 = "/dir1/dir2";
        String dir3 = "/dir1/dir2/dir3";
        String dir4 = "/dir1/dir2/dir4";
        String dir5 = "/dir1/dir2/dir4/dir5";
        String file1 = "file.test";

        this.fFileSystem.createDirectories(dir2);
        this.fFileSystem.createDirectories(dir3);
        this.fFileSystem.createDirectories(dir5);
        this.fFileSystem.createFile(dir4, file1);
        assertTrue(this.fFileSystem.isDirectory(dir2));
        assertTrue(this.fFileSystem.isDirectory(dir3));
        assertTrue(this.fFileSystem.isDirectory(dir5));

        this.fFileSystem.delete(dir2);
        assertFalse(this.fFileSystem.isDirectory(dir2));
        assertFalse(this.fFileSystem.isDirectory(dir3));
        assertFalse(this.fFileSystem.isDirectory(dir5));
    }

    /**
     * 
     */
    public void testSetMaxChunkSize() {
        String filePath = "file.test";
        assertTrue(this.fFileSystem.createFile(filePath, "aFileId"));
        assertTrue(this.fFileSystem.setMaxChunkSize(filePath, 50));
        assertEquals(50,this.fFileSystem.getMaxChunkSize(filePath));
        assertTrue(this.fFileSystem.setMaxChunkSize(filePath, 60));
        assertEquals(60,this.fFileSystem.getMaxChunkSize(filePath));

        //add a chunk
        assertTrue(this.fFileSystem.addChunkToFile(new ChunkMetaData("cId",
                "aFileId", 0), filePath));
        
        //-> can't change chunk size anymore
        assertFalse(this.fFileSystem.setMaxChunkSize(filePath, 70));
        assertEquals(60,this.fFileSystem.getMaxChunkSize(filePath));
    }
}