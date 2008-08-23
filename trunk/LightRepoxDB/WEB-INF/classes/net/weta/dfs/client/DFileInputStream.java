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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/DFileInputStream.java,v $
 */

package net.weta.dfs.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceDNS;
import net.weta.dfs.com.ServerFaceMDS;
import net.weta.dfs.util.FileUtil;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * An InputStream to read from an distributed file.
 * 
 * <br/><br/>created on 19.01.2005
 * 
 * @version $Revision: 1.20 $
 */
public class DFileInputStream extends InputStream {

    static Category fLogger = Logger.getLogger(DFileInputStream.class);

    private DFile fFile;

    private Connection fDnsConnection;

    private ChunkMetaData[] fChunkMDs;

    private int fActChunkPos;

    private byte[] fBuffer = new byte[FileUtil.DEFAULT_BUFFER_SIZE];

    private int fBufferPos = 0;

    private int fBufferLength = 0;

    private long fBytesRead = 0;

    /**
     * Constructs a <code>DFileInputStream</code> that will read from a given
     * <code>DFile</code>.
     * 
     * @param file
     * @throws IOException
     *             if <code>file</code> is a directory, does not exist or
     *             cannot be opened for reading for any other reason.
     */
    public DFileInputStream(DFile file) throws IOException {
        if (!file.exists())
            throw new FileNotFoundException("file does not exists");

        if (file.isDirectory())
            throw new FileNotFoundException("file is a directory");

        this.fFile = file;
        this.fChunkMDs = ServerFaceMDS.fileGetChunks(this.fFile
                .getMDSConnection(), this.fFile.getPath());

        if (this.fChunkMDs.length > 0) {
            openNodeConnection();
            fillBuffer();
        } else {
            this.fBuffer[this.fBufferPos] = -1;
            this.fBufferPos++;
        }
    }

    /**
     * Constructs a <code>DFileInputStream</code> that will read from a given
     * <code>DFile</code>.
     * 
     * @param filePath
     * @param ipAddress
     *            of metadataserver
     * @param port
     *            of metadataserver
     * @throws IOException
     *             if <code>file</code> is a directory, does not exist or
     *             cannot be opened for reading for any other reason.
     */
    public DFileInputStream(String filePath, String ipAddress, int port)
            throws IOException {
        this(new DFile(filePath, ipAddress, port));
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (this.fBufferPos == this.fBufferLength) {
            fillBuffer();
        }

        int result = -1;
        if (this.fBufferLength > 0) {
            result = FileUtil.toInt(this.fBuffer[this.fBufferPos]);
            this.fBufferPos++;
            this.fBytesRead++;
        }

        return result;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] bytes) throws IOException {

        return read(bytes, 0, bytes.length);
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] bytes, int off, int len) throws IOException {
        // TODO think we could do it more perfomantly
        return super.read(bytes, off, len);
    }

    private void fillBuffer() throws IOException {
        this.fBufferLength = this.fDnsConnection.read(this.fBuffer);
        this.fBufferPos = 0;

        if (this.fBufferLength == -1
                && this.fActChunkPos < this.fChunkMDs.length - 1) {
            closeNodeConnection();
            this.fActChunkPos++;
            openNodeConnection();
            fillBuffer();
        } else if (this.fBufferLength == -1
                && this.fActChunkPos == this.fChunkMDs.length - 1) {
            closeNodeConnection();
        }
    }

    /**
     * Doesn't throws an IOException.
     * 
     * @throws IOException
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return (int) (this.fFile.length() - this.fBytesRead);
    }

    /**
     * @throws IOException
     */
    public void close() throws IOException {
        this.fFile.getMDSConnection().close();
        if (this.fDnsConnection != null)
            closeNodeConnection();
        super.close();
    }

    private void openNodeConnection() throws IOException {
        fLogger.debug("opening node connection ");
        NodeMetaData[] nodeMDs = ServerFaceMDS.nodeGetFromChunk(this.fFile
                .getMDSConnection(), this.fChunkMDs[this.fActChunkPos].getId());
        boolean connect = false;
        int i = 0;

        // always try to get the first, mds should determine the order
        while (!connect && i < nodeMDs.length) {
            this.fDnsConnection = new Connection(nodeMDs[i].getHostAddress(),
                    nodeMDs[i].getPort());
            try {
                // TODO with help of chunkSize we could achieve a final ok
                // response
                long chunkSize = ServerFaceDNS.openReadConnection(
                        this.fDnsConnection, this.fChunkMDs[this.fActChunkPos]);
                connect = true;
            } catch (Exception e) {
                // nothing here, just try next node
                fLogger.debug("could not read chunk "
                        + this.fChunkMDs[this.fActChunkPos] + " from node "
                        + nodeMDs[i - 1].getId());
            }
            i++;
        }
        if (!connect)
            throw new IOException(
                    "could not get access to any node ,containing the next chunk ");
    }

    private void closeNodeConnection() throws IOException {
        fLogger.debug("closing node connection ");
        this.fDnsConnection.close();
        this.fDnsConnection = null;

    }
}
