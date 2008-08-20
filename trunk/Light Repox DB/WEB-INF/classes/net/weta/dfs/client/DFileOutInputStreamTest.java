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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/client/DFileOutInputStreamTest.java,v $
 */

package net.weta.dfs.client;

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;
import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.util.FileUtil;

import org.apache.log4j.Category;

/**
 * Test the distributed reading and writing of file streams.
 * 
 * created on 13.01.2005
 * 
 * @author sg
 * @version $Revision: 1.26 $
 * 
 */
public class DFileOutInputStreamTest extends TestCase {
    static Category fLogger = Category
            .getInstance(DFileOutInputStreamTest.class.getName());

    private MetaDataServer fMds;

    private DataNodeServer fDns;

    private DFile fFile;

    private long fFileLength = FileUtil.MB_Byte + 32;

    private int fChunkSizeInMB = 1;

    protected void setUp() throws Exception {
        super.setUp();
        this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
        this.fDns = new DataNodeServer(Configuration.getInstance());
        this.fDns.startServer();
        this.fFile = new DFile(getName(), this.fMds.getIpAddress(), this.fMds
                .getPort());
    }

    protected void tearDown() throws Exception {
        this.fFile.delete();
        assertFalse(this.fFile.exists());
        this.fFile.getMDSConnection().close();
        this.fDns.stopServer();
        this.fMds.stopServer();
        super.tearDown();
    }

    // /**
    // */
    // public void testChunkFolderIsEmpty() {
    // assertEquals(this.fDns.getChunkManager().getMaxDiskSpace(), this.fDns
    // .getChunkManager().getFreeDiskSpace());
    // }

    /**
     * @throws IOException
     */
    public void testOutputSConstructor() throws IOException {
        // file is dir
        DFileOutputStream outputStream = null;
        assertTrue(this.fFile.mkdirs());
        try {
            outputStream = new DFileOutputStream(this.fFile,
                    this.fChunkSizeInMB);
            fail();
        } catch (IOException e) {
            assertTrue(this.fFile.delete());
            this.fFile.getMDSConnection().close();
        }

        // file always exist
        this.fFile = new DFile(this.fFile.getPath(), this.fMds.getIpAddress(),
                this.fMds.getPort());
        this.fFile.createNewFile();
        outputStream = new DFileOutputStream(this.fFile, this.fChunkSizeInMB);
        this.fFile.getMDSConnection().close();

        // parent dir does not exist
        try {
            outputStream = new DFileOutputStream("/dir1/file", this.fMds
                    .getIpAddress(), this.fMds.getPort(), this.fChunkSizeInMB);
            fail();
        } catch (IOException e) {
            this.fFile.getMDSConnection().close();
        }

        // everything alright
        this.fFile = new DFile("aFile", this.fMds.getIpAddress(), this.fMds
                .getPort());
        assertFalse(this.fFile.exists());
        outputStream = new DFileOutputStream(this.fFile, this.fChunkSizeInMB);
        assertTrue(this.fFile.exists());
        assertTrue(this.fFile.isFile());
        outputStream.close();
    }

    /**
     * @throws IOException
     */
    public void testInputSConstructor() throws IOException {
        assertTrue(this.fFile.mkdirs());

        // file is a dir
        try {
            new DFileInputStream(this.fFile);
            fail();
        } catch (IOException e) {
            assertTrue(this.fFile.delete());
            this.fFile.getMDSConnection().close();
        }

        // file does not exist
        try {
            new DFileInputStream(this.fFile);
            fail();
        } catch (IOException e) {
            this.fFile.getMDSConnection().close();
        }

        // file exist but has no chunks
        assertTrue(this.fFile.createNewFile());
        DFileInputStream inputStream = new DFileInputStream(this.fFile);
        assertEquals(-1, inputStream.read());
        this.fFile.getMDSConnection().close();
        inputStream.close();
    }

    /**
     * Check if written int's machtes read int's and byte's.
     * 
     * @throws Exception
     */
    public void testWriteInt() throws Exception {
        DFileOutputStream outputStream = new DFileOutputStream(this.fFile, 1);
        Random random = new Random();
        int[] transferedInts = new int[(int) this.fFileLength];

        // write ints
        for (int i = 0; i < transferedInts.length; i++) {
            int value = random.nextInt(255);
            transferedInts[i] = value;
            outputStream.write(value);
        }
        outputStream.close();

        // check file
        assertTrue(this.fFile.isFile());
        assertEquals(transferedInts.length, this.fFile.length());

        // read as ints
        checkReadInt(transferedInts);

        // read as bytes
        byte[] bytes = new byte[transferedInts.length];
        for (int i = 0; i < transferedInts.length; i++) {
            bytes[i] = (byte) transferedInts[i];
        }
        checkReadByte1(bytes);
    }

    /**
     * Check if written bytes's machtes read int's and byte's.
     * 
     * @throws Exception
     */
    public void testWrite_ReadBytes() throws Exception {
        DFileOutputStream outputStream = new DFileOutputStream(this.fFile, 1);
        byte[] transferedBytes = new byte[(int) this.fFileLength];
        new Random().nextBytes(transferedBytes);

        // write bytes
        outputStream.write(transferedBytes);
        outputStream.close();

        // check file
        assertTrue(this.fFile.isFile());
        assertEquals(transferedBytes.length, this.fFile.length());

        // read as bytes
        checkReadByte1(transferedBytes);

        // read as ints
        int[] ints = new int[transferedBytes.length];
        for (int i = 0; i < transferedBytes.length; i++) {
            ints[i] = FileUtil.toInt(transferedBytes[i]);
        }
        checkReadInt(ints);
    }

    /**
     * @throws Exception
     */
    public void testChunks() throws Exception {
        int chunksCount = 5;
        this.fFileLength = (chunksCount - 1) * FileUtil.MB_Byte + 32;
        DFileOutputStream foStream = new DFileOutputStream(this.fFile, 1);

        // write chunks
        byte[] bytes = new byte[(int) this.fFileLength];
        new Random().nextBytes(bytes);
        foStream.write(bytes);
        foStream.close();

        // check mds
        assertTrue(this.fMds.getFileSystem().isFile(this.fFile.getPath()));
        ChunkMetaData[] chunkMDs = this.fMds.getFileSystem().getFileChunks(
                this.fFile.getPath());
        assertEquals(chunksCount, chunkMDs.length);
        assertEquals(this.fFileLength, this.fMds.getFileSystem().getFileLength(
                this.fFile.getPath()));
        for (int i = 0; i < chunkMDs.length; i++) {
            assertEquals(this.fDns.getNodeMD(), this.fMds.getNodeManager()
                    .getChunkNodes(chunkMDs[i].getId())[0]);
        }

        // check dns
        assertEquals(chunksCount * this.fChunkSizeInMB * FileUtil.MB_Byte,
                this.fDns.getChunkManager().getMaxDiskSpace()
                        - this.fDns.getChunkManager().getFreeDiskSpace());
        for (int i = 0; i < chunkMDs.length; i++) {
            this.fDns.getChunkManager().exists(chunkMDs[i]);
        }

        // read chunks
        checkReadByte1(bytes);
    }

    /**
     * @throws Exception
     */
    public void testDelete() throws Exception {
        DFileOutputStream foStream = new DFileOutputStream(this.fFile, 1);

        // write chunks
        byte[] bytes = new byte[(int) this.fFileLength];
        new Random().nextBytes(bytes);
        foStream.write(bytes);
        foStream.close();
        assertTrue(this.fMds.getFileSystem().isFile(this.fFile.getPath()));

        // get chunk
        ChunkMetaData[] chunkMDs = this.fMds.getFileSystem().getFileChunks(
                this.fFile.getPath());
        assertEquals(this.fFileLength
                / (this.fChunkSizeInMB * FileUtil.MB_Byte) + 1, chunkMDs.length);

        // delete file
        assertTrue(this.fFile.delete());
        assertFalse(this.fFile.exists());

        // check servers
        assertFalse(this.fMds.getFileSystem().isFile(this.fFile.getPath()));
        assertNull(this.fMds.getNodeManager()
                .getChunkNodes(chunkMDs[0].getId()));
        for (int i = 0; i < chunkMDs.length; i++) {
            assertFalse(this.fDns.getChunkManager().exists(chunkMDs[i]));
        }
    }

    /**
     * @throws Exception
     */
    public void testSetMaxChunkSize() throws Exception {
        DFileOutputStream foStream = new DFileOutputStream(this.fFile, 5);
        assertEquals(5 * FileUtil.MB_Byte, this.fMds.getFileSystem()
                .getMaxChunkSize(this.fFile.getPath()));
        foStream.close();

        // change and add a chunk
        foStream = new DFileOutputStream(this.fFile, 6);
        assertEquals(6 * FileUtil.MB_Byte, this.fMds.getFileSystem()
                .getMaxChunkSize(this.fFile.getPath()));
        foStream.write(1);
        foStream.close();

        // change cause file gets overwritten
        foStream = new DFileOutputStream(this.fFile, 7);
        assertEquals(7 * FileUtil.MB_Byte, this.fMds.getFileSystem()
                .getMaxChunkSize(this.fFile.getPath()));
        foStream.write(1);
        foStream.close();
    }

    private void checkReadInt(int[] transferedInts) throws Exception {
        DFileInputStream inputStream = new DFileInputStream(this.fFile);
        assertEquals(transferedInts.length, inputStream.available());
        int i = 0;
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            assertEquals(transferedInts[i], b);
            i++;
        }
        assertEquals(0, inputStream.available());
        assertEquals(transferedInts.length, i);
        inputStream.close();
    }

    private void checkReadByte1(byte[] transferedBytes) throws Exception {
        DFileInputStream inputStream = new DFileInputStream(this.fFile);
        assertEquals(transferedBytes.length, inputStream.available());
        byte[] readBytes = new byte[transferedBytes.length];
        assertEquals(transferedBytes.length, inputStream.read(readBytes));
        for (int i = 0; i < transferedBytes.length; i++) {
            assertEquals(transferedBytes[i], readBytes[i]);
        }
        assertEquals(0, inputStream.available());
        inputStream.close();
    }
}
