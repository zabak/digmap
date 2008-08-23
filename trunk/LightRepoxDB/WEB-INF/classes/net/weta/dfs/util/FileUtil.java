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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/util/FileUtil.java,v $
 */

package net.weta.dfs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Util class providing some file-/stream operations and some units of
 * measurement.
 * 
 * <br/><br/>created on 15.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class FileUtil {

    /**
     * The size of one kilobyte in byte.
     */
    public static final long KB_BYTE = 1024;

    /**
     * The size of one megabyte in byte.
     */
    public static final long MB_Byte = KB_BYTE * KB_BYTE;

    /**
     * The size of one gigabyte in byte.
     */
    public static final long GB_BYTE = KB_BYTE * MB_Byte;

    /**
     * The size of one terrabyte in byte.
     */
    public static final long TB_BYTE = KB_BYTE * GB_BYTE;

    /**
     * A default buffer size, could be used f.e. by copying bytes from stream to
     * stream.
     */
    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    /**
     * Convert an byte to an corresponding int value from 0 to 255.
     * 
     * @param theByte
     * @return an int between 0 and 255.
     */
    public static int toInt(byte theByte) {
        return theByte & 0xFF;
    }

    /**
     * Write all bytes available from Input- to OutputStream in pieces of
     * DEFAULT_BUFFER_SIZE. <br/>Block until it reads -1.
     * 
     * 
     * @param iStream
     * @param oStream
     * @throws IOException
     */
    public static void writeBytes(InputStream iStream, OutputStream oStream)
            throws IOException {
        int length = -1;
        byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
        while ((length = iStream.read(bytes)) != -1) {
            oStream.write(bytes, 0, length);
        }
    }

    /**
     * Write length bytes available from Input- to OutputStream in pieces of
     * DEFAULT_BUFFER_SIZE. <br/>Block until it reads -1.
     * 
     * @param iStream
     * @param oStream
     * @param length
     * @throws IOException
     */
    public static void writeBytes(InputStream iStream, OutputStream oStream,
            int length) throws IOException {

        byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
        int pieceLength = bytes.length;
        while (length > 0) {
            if (length < bytes.length)
                pieceLength = iStream.read(bytes, 0, length);
            else
                pieceLength = iStream.read(bytes, 0, bytes.length);

            oStream.write(bytes, 0, pieceLength);
            length -= pieceLength;
        }
    }

    /**
     * Writes an "real int value" to OutputStream. <br/>Note: To read the "real
     * int value" from an InputStream FileUtil.readInt() must be called.
     * 
     * @param oStream
     * @param theInt
     * @throws IOException
     */
    public static void writeInt(OutputStream oStream, int theInt)
            throws IOException {
        oStream.write((byte) (theInt >> 24));
        oStream.write((byte) (theInt >> 16));
        oStream.write((byte) (theInt >> 8));
        oStream.write((byte) theInt);
    }

    /**
     * Reads an "real int value" which is written with FileUtil.writeInt() from
     * InputStream.
     * 
     * @param iStream
     * @return The next int data of the stream.
     * @throws IOException
     */
    public static int readInt(InputStream iStream) throws IOException {
        return ((iStream.read() & 0xFF) << 24)
                | ((iStream.read() & 0xFF) << 16)
                | ((iStream.read() & 0xFF) << 8) | (iStream.read() & 0xFF);
    }

    /**
     * Writes an serializable object to the given file.
     * <p>
     * NOTE: file must be existent.
     * 
     * @param file
     * @param serializable
     * @throws IOException
     */
    public static void writeSerializable(File file, Serializable serializable)
            throws IOException {
        FileOutputStream fileOutStream = new FileOutputStream(file);
        ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
        objOutStream.writeObject(serializable);
        fileOutStream.close();
    }

    /**
     * Reads an serializable object from the given file.
     * 
     * @param file
     * @return the serializable or null if none exist
     * @throws IOException
     */
    public static Serializable readSerializable(File file) throws IOException {
        if (!file.exists() || file.length() == 0)
            return null;

        Serializable serializable = null;

        FileInputStream fileInStream = new FileInputStream(file);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);

        try {
            serializable = (Serializable) objInStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getLocalizedMessage());
        } finally {
            objInStream.close();
            fileInStream.close();
        }

        return serializable;
    }

    /**
     * Simple copy method, with which you could extract an byte array out of an
     * byte array.
     * 
     * @param byteSrc
     * @param length
     * @return the byte array with elements from byteSrc[0] till
     *         byteSrc[length-1]
     */
    public static byte[] getBytes(byte[] byteSrc, int length) {

        if (byteSrc.length == length)
            return byteSrc;
        return getBytes(byteSrc, 0, length - 1, false);
    }

    /**
     * Simple copy method, with which you could extract an byte array out of an
     * byte array.
     * 
     * @param byteSrc
     * @param startPos
     * @param endPos
     * @param invariableLength
     *            if returned byte array have length of byteSrc or not
     * @return the byte array with elements from byteSrc[startPos] till
     *         byteSrc[endPos]
     */
    public static byte[] getBytes(byte[] byteSrc, int startPos, int endPos,
            boolean invariableLength) {

        if (endPos > byteSrc.length || endPos < 0 || startPos < 0
                || endPos < startPos)
            throw new IllegalArgumentException(
                    "startPos and endPos must be in byteSrc range");

        byte[] result;
        if (invariableLength)
            result = new byte[byteSrc.length];
        else
            result = new byte[endPos - startPos + 1];

        for (int i = 0; i < endPos - startPos + 1; i++) {
            result[i] = byteSrc[i + startPos];
        }

        return result;
    }
}
