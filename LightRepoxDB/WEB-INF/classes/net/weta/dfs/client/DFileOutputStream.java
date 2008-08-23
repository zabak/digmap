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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/DFileOutputStream.java,v $
 */

package net.weta.dfs.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceDNS;
import net.weta.dfs.com.ServerFaceMDS;
import net.weta.dfs.util.FileUtil;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * A stream that can write to a set of chunks located on different datanodes.
 * 
 * <br/><br/>created on 13.01.2005
 * 
 * @version $Revision: 1.19 $
 */
public class DFileOutputStream extends OutputStream {

	static Category fLogger = Logger.getLogger(DFileOutputStream.class);

    private DFile fFile;

    private Connection fDnsConnection;

    private ArrayList fWrittenChunkMDs;

    private ChunkMetaData fActChunkMD;

    private NodeMetaData fActNodeMD;

    private long fChunkSize;

    private byte[] fBuffer = new byte[FileUtil.DEFAULT_BUFFER_SIZE];

    private int fBufferPos = 0;

    /**
     * Constructs a <code>DFileOutputStream</code> to write to the file
     * represented by the specified <code>DFile</code> object respectively to
     * write the file as chunks to different datanodeserver.
     * 
     * The behaviour of DFileOutputStream(..) is like FileOutputStream(..):
     * <br/>
     * 
     * o file already exist -> Stream will overwrite it(beside append mode is
     * choosen) <br/>
     * 
     * o file already exist & readableOnly -> IOException <br/>
     * 
     * o file does not exist -> Stream will try to create it <br/>
     * 
     * o file is a directory -> IOException <br/>
     * 
     * 
     * @param file
     * @param chunkSizeInMb
     * @throws IOException
     *             if file could not be created
     */
    public DFileOutputStream(DFile file, long chunkSizeInMb) throws IOException {
        this.fFile = file;
        this.fChunkSize = chunkSizeInMb * FileUtil.MB_Byte;

        if (!this.fFile.canWrite()) {
            throw new IOException("write access denied to file "
                    + file.getPath());
        }
        if (this.fFile.length() > 0)
            this.fFile.delete();
        this.fFile.createNewFile();
        if (!this.fFile.isFile()) {
            throw new IOException("file " + file.getPath()
                    + " can't be created for any reasons");
        }

        FileMetaData fileMD = new FileMetaData(this.fFile.getFileId(),
                this.fFile.getPath());
        fileMD.setMaxChunkSize(chunkSizeInMb * FileUtil.MB_Byte);
        ServerFaceMDS
                .fileSetMaxChunkSize(this.fFile.getMDSConnection(), fileMD);
        this.fWrittenChunkMDs = new ArrayList();
        beginNextChunk();
    }

    /**
     * Constructs a <code>DFileOutputStream</code> to write to the file
     * represented by the specified <code>DFile</code> object respectively to
     * write the file as chunks to different datanodeserver.
     * 
     * @param filePath
     * @param ipAddress
     * @param port
     * @param chunkSizeInMb
     * @throws IOException
     *             if file could not be created
     */
    public DFileOutputStream(String filePath, String ipAddress, int port, long chunkSizeInMb) throws IOException {
        this(new DFile(filePath, ipAddress, port), chunkSizeInMb);
    }

    /**
     * @param data
     * @throws IOException
     */
    public void write(int data) throws IOException {
        this.fBuffer[this.fBufferPos] = (byte) data;
        this.fBufferPos++;
        if (this.fBufferPos >= this.fBuffer.length) { flush(); }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] bytes, int off, int length) throws IOException {
        // TODO implement this in a more performand way
        for (int i = off; i < length; i++) { write(FileUtil.toInt(bytes[i])); }
    }

    /**
     * Forces the intern buffer to send the actual buffered bytes to data node.
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        if (this.fBufferPos == 0)
            return;

        if (this.fActChunkMD.getSize() == 0)
            openNodeConnection(false);

        // TODO does that maintains the case when buffer is over the amount of
        // the actual node and buffer must be split?
        if (this.fActChunkMD.getSize() + this.fBufferPos <= this.fChunkSize) {
            // first write byte number
            this.fDnsConnection.writeInt(this.fBufferPos);
            // then write bytes
            this.fDnsConnection.write(FileUtil.getBytes(this.fBuffer,
                    this.fBufferPos));

            this.fActChunkMD.setSize(this.fActChunkMD.getSize()
                    + this.fBufferPos);
            if (this.fActChunkMD.getSize() == this.fChunkSize) {
                closeNodeConnection();
                beginNextChunk();
            }
            this.fBufferPos = 0;
        }
    }

    /**
     * @throws IOException
     */
    public void close() throws IOException {
        flush();
        if (this.fDnsConnection != null && this.fDnsConnection.isConnected()) closeNodeConnection();
        this.fFile.getMDSConnection().close();
        super.close();
    }

    private void beginNextChunk() throws IOException {
        if (this.fActChunkMD != null) {
            this.fWrittenChunkMDs.add(this.fActChunkMD);
            fLogger.debug("new Block at position " + this.fActChunkMD.getSize()
                    + " (" + this.fActChunkMD.getSize() / FileUtil.MB_Byte
                    + "MB)");
        }
        this.fActNodeMD = ServerFaceMDS.nodeGetAFree(this.fFile
                .getMDSConnection(), this.fChunkSize / FileUtil.MB_Byte);

        if (this.fActNodeMD == null)
            throw new IOException("no node with an amount of "
                    + this.fChunkSize + " bytes free disc space availible ");
        this.fActChunkMD = new ChunkMetaData(ServerFaceMDS.getAnId(this.fFile
                .getMDSConnection()), this.fFile.getFileId(),
                this.fWrittenChunkMDs.size());
    }

    private void openNodeConnection(boolean append) throws IOException {
        fLogger.debug("opening new connection to NDS: "
                + this.fActNodeMD.getId());
        this.fDnsConnection = new Connection(this.fActNodeMD.getHostAddress(),
                this.fActNodeMD.getPort());
        FileMetaData fileMD = new FileMetaData(this.fFile.getFileId(),
                this.fFile.getPath());
        fileMD.setMaxChunkSize(this.fChunkSize);

        // check permission
        try {
            ServerFaceDNS.openWriteConnection(this.fDnsConnection, fileMD,
                    this.fActChunkMD);
        } catch (CommandException e) {
            throw new IOException("Cannot write to node :" + e.getMessage());
        }
    }

    private void closeNodeConnection() throws IOException {
        fLogger.debug("closing connection to NDS: " + this.fActNodeMD.getId());
        // check if successful
        try {
            ServerFaceDNS.closeWriteConnection(this.fDnsConnection);
        } catch (CommandException e) {
            throw new IOException("Error by writing chunk");
        }
        this.fDnsConnection.close();
        this.fDnsConnection = null;
    }

}