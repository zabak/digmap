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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/util/FileUtilTest.java,v $
 */

package net.weta.dfs.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import net.weta.dfs.util.FileUtil;

import junit.framework.TestCase;

/**
 * comment for FileUtilTest
 * 
 * <br/><br/>created on 17.03.2005
 * 
 * @version $Revision: 1.2 $
 *  
 */
public class FileUtilTest extends TestCase {

    /**
     *  
     */
    public void testByteLength() {
        long bytes = 1024;
        assertEquals(bytes, FileUtil.KB_BYTE);
        bytes *= 1024;
        assertEquals(bytes, FileUtil.MB_Byte);
        bytes *= 1024;
        assertEquals(bytes, FileUtil.GB_BYTE);
        bytes *= 1024;
        assertEquals(bytes, FileUtil.TB_BYTE);
    }

    /**
     *  
     */
    public void testToInt() {
        for (byte b = -128; b < 127; b++) {
            int i = FileUtil.toInt(b);
            assertEquals(b, (byte) i);
        }
    }

    /**
     * @throws IOException
     */
    public void testWriteInt() throws IOException {
        PipedOutputStream oStream = new PipedOutputStream();
        PipedInputStream iStream = new PipedInputStream(oStream);

        FileUtil.writeInt(oStream, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, FileUtil.readInt(iStream));
        FileUtil.writeInt(oStream, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, FileUtil.readInt(iStream));
        FileUtil.writeInt(oStream, 0);
        assertEquals(0, FileUtil.readInt(iStream));

        oStream.close();
        iStream.close();
    }

    /**
     * @throws IOException
     */
    public void testWriteBytes() throws IOException {
        // set size not over 1024 otherwise PipedOutputStream will hang
        long size = FileUtil.KB_BYTE;
        byte transferValue = 8;

        // fill iStream1
        PipedOutputStream oStream1 = new PipedOutputStream();
        InputStream iStream1 = new PipedInputStream(oStream1);
        for (int i = 0; i < size; i++) {
            oStream1.write(transferValue);
        }
        oStream1.close();

        // connect iStream2 with oStream 2
        PipedOutputStream oStream2 = new PipedOutputStream();
        InputStream iStream2 = new PipedInputStream(oStream2);

        assertEquals(size, iStream1.available());
        assertEquals(0, iStream2.available());

        // copy bytes from iStream1 to oStream2
        FileUtil.writeBytes(iStream1, oStream2);

        assertEquals(0, iStream1.available());
        assertEquals(size, iStream2.available());

        iStream1.close();
        iStream2.close();
        oStream2.close();
    }

    /**
     * @throws IOException
     */
    public void testWriteBytesWithLength() throws IOException {
        // set size not over 1024 otherwise PipedOutputStream will hang
        long size = FileUtil.KB_BYTE;
        byte transferValue = 8;

        // fill iStream1
        PipedOutputStream oStream1 = new PipedOutputStream();
        InputStream iStream1 = new PipedInputStream(oStream1);
        for (int i = 0; i < size; i++) {
            oStream1.write(transferValue);
        }

        // connect iStream2 with oStream 2
        PipedOutputStream oStream2 = new PipedOutputStream();
        InputStream iStream2 = new PipedInputStream(oStream2);

        assertEquals(size, iStream1.available());
        assertEquals(0, iStream2.available());

        // copy bytes from iStream1 to oStream2
        FileUtil.writeBytes(iStream1, oStream2, iStream1.available());

        assertEquals(0, iStream1.available());
        assertEquals(size, iStream2.available());

        iStream1.close();
        oStream1.close();
        iStream2.close();
        oStream2.close();
    }

    /**
     * @throws IOException
     */
    public void testWriteSerializable() throws IOException {
        File file = new File("tmp/aFile");
        String serializable = "aString";

        assertFalse(file.exists());
        assertTrue(file.getParentFile().mkdirs());
        assertTrue(file.createNewFile());
        assertEquals(0, file.length());

        FileUtil.writeSerializable(file, serializable);
        assertTrue(file.length() > 0);
        assertEquals(serializable, FileUtil.readSerializable(file));

        assertTrue(file.delete());
        file.getParentFile().delete();
    }

    /**
     *  
     */
    public void testGetBytes() {
        byte[] bytes = new byte[3234];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        // get the same byte array
        byte[] copiedBytes = FileUtil.getBytes(bytes, bytes.length);
        assertEquals(bytes.length, copiedBytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], copiedBytes[i]);
        }

        // get a shorter byte array
        int length = bytes.length - bytes.length / 2;
        copiedBytes = FileUtil.getBytes(bytes, length);
        assertEquals(length, copiedBytes.length);
        for (int i = 0; i < length; i++) {
            assertEquals(bytes[i], copiedBytes[i]);
        }

        // get a bigger byte array
        length = bytes.length + 10;
        try {
            copiedBytes = FileUtil.getBytes(bytes, length);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        // get a very small byte array
        length = -23;
        try {
            copiedBytes = FileUtil.getBytes(bytes, length);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    /**
     *  
     */
    public void testGetBytes2() {
        byte[] bytes = new byte[3234];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        // get the same byte array
        byte[] copiedBytes = FileUtil.getBytes(bytes, 0, bytes.length - 1,
                false);
        assertEquals(bytes.length, copiedBytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], copiedBytes[i]);
        }

        // get the same byte array with invariable length
        copiedBytes = FileUtil.getBytes(bytes, 0, bytes.length - 1, true);
        assertEquals(bytes.length, copiedBytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], copiedBytes[i]);
        }

        // get a shorter byte array
        int startPos = bytes.length - bytes.length / 2;
        int endPos = bytes.length - bytes.length / 4;
        copiedBytes = FileUtil.getBytes(bytes, startPos, endPos, false);
        assertEquals(endPos - startPos + 1, copiedBytes.length);
        for (int i = 0; i < endPos - startPos + 1; i++) {
            assertEquals(bytes[i + startPos], copiedBytes[i]);
        }

        // get a shorter byte array with invariable length
        copiedBytes = FileUtil.getBytes(bytes, startPos, endPos, true);
        assertEquals(bytes.length, copiedBytes.length);
        for (int i = 0; i < endPos - startPos + 1; i++) {
            assertEquals(bytes[i + startPos], copiedBytes[i]);
        }

        copiedBytes = FileUtil.getBytes(bytes, 5, 5, false);
        assertEquals(1,copiedBytes.length);
        assertEquals(bytes[5],copiedBytes[0]);
        // fail with startPos < endPos
        try {
            copiedBytes = FileUtil.getBytes(bytes, 5, 4, false);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

    }
}