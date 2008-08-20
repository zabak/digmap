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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/dns/ChunkManager.java,v $
 */

package net.weta.dfs.server.dns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.FileMetaData;
import net.weta.dfs.util.FileUtil;
import net.weta.dfs.util.PathUtil;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * The ChunkManager is responsible for writing and reading chunks and it
 * belonging metadata on dns to harddisk.
 * 
 * <br/><br/>created on 01.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */

public class ChunkManager {

    static Category fLogger = Logger.getLogger(ChunkManager.class);

    private String fChunkDir;

    private long fMaxDiscSpace;

    private long fActDiscSpace = 0;

    /**
     * Name of the file containing the ChunkMetaDatas.
     */
    private final String fChunkMDName = "chunk.meta";

    /**
     * Name of the file containing the FileMetaData.
     */
    private final String fFileMDName = "file.meta";

    /**
     * 
     * @param chunkDir
     * @param maxDiscSpace
     * @throws IOException
     */
    public ChunkManager(String chunkDir, int maxDiscSpace) throws IOException {
        this.fMaxDiscSpace = maxDiscSpace * FileUtil.MB_Byte;
        this.fChunkDir = chunkDir;

        File dir = new File(chunkDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("cannot create the chunk directory '"
                    + dir.getPath() + "'");
        }

        countFreeDiskSpace();
    }

    /**
     * @return the free space of the node in bytes
     */
    public synchronized long getFreeDiskSpace() {
        long freeSpache;
        synchronized (this.fChunkDir) {
            freeSpache = this.fMaxDiscSpace - this.fActDiscSpace;
        }
        return freeSpache;
    }

    /**
     * 
     * @return the maximum disc space of the node in bytes
     */
    public long getMaxDiskSpace() {
        return this.fMaxDiscSpace;
    }

    /**
     * Writes the chunk described by chunkMD from iStream to harddisk. Awaits
     * first the number of bytes in "real int" (see FileUtil.readInt) then write
     * the bytes and repeat this till it read a "real int" 0.
     * 
     * @param iStream
     * @param fileMD
     * @param chunkMD
     * @return the size of the chunk which is written
     * @throws IOException
     */
    public long writeChunk(InputStream iStream, FileMetaData fileMD, ChunkMetaData chunkMD) throws IOException {

        FileOutputStream foStream = null;
        File chunkFile = null;

        synchronized (this.fChunkDir) {
            if (!fileMD.getFileId().equals(chunkMD.getFileId()))
                throw new IOException(
                        "fileId of chunk differs from file's fileID");
            if (exists(chunkMD))
                throw new IOException("chunk already exists");
            if (fileMD.getMaxChunkSize()==0)
                throw new IOException("chunksize not set");

            writeFileMetaData(fileMD);
            writeChunkMetaData(chunkMD);

            chunkFile = getChunkFile(chunkMD);
            foStream = new FileOutputStream(chunkFile);
            this.fActDiscSpace += fileMD.getMaxChunkSize();
        }

        int commingBytes = FileUtil.readInt(iStream);
        while (commingBytes != 0) {
            FileUtil.writeBytes(iStream, foStream, commingBytes);
            commingBytes = FileUtil.readInt(iStream);
        }
        foStream.close();

        chunkMD.setSize(chunkFile.length());
        synchronized (this.fChunkDir) {
            updateChunkMetaData(chunkMD);
        }

        fLogger.info("wrote chunk '" + chunkMD.getId());
        return chunkFile.length();
    }

    /**
     * Reads the chunk described by chunkMD from harddisk to oStream. If chunk
     * doesn't exist, throws an IOExeption.
     * 
     * @param oStream
     * @param chunkMD
     * @throws IOException
     */
    public void readChunk(OutputStream oStream, ChunkMetaData chunkMD)
            throws IOException {
        File chunkFile = getChunkFile(chunkMD);
        if (!chunkFile.canRead()) {
            throw new IOException("cannot read file " + chunkFile);
        }
        FileInputStream fiStream = new FileInputStream(chunkFile);
        FileUtil.writeBytes(fiStream, oStream);
        fiStream.close();
        oStream.close();
        fLogger.info("read chunk '" + chunkMD.getId());
    }

    /**
     * Note: proofs only if metaData exist not the file itself
     * 
     * @param chunkMD
     * @return true if chunk described by metaData exists
     */
    public boolean exists(ChunkMetaData chunkMD) {
        ArrayList chunkMDs = getChunkMetaDatas(chunkMD.getFileId());
        boolean exists = false;

        if (chunkMDs != null)
            exists = chunkMDs.contains(chunkMD);

        return exists;
    }

    /**
     * 
     * @param chunkMD
     * @return the size of the chunk descriped in chunkMD or -1 if not exist
     */
    public long getChunkSize(ChunkMetaData chunkMD) {
        if (!exists(chunkMD))
            return -1;

        return getChunkFile(chunkMD).length();
    }

    /**
     * 
     * @return all existing FileMetaData
     * @throws IOException
     */
    public FileMetaData[] getFileMetaDatas() throws IOException {
        File[] fileDirs = new File(this.fChunkDir).listFiles();

        if (fileDirs == null)
            return null;

        ArrayList files = new ArrayList(fileDirs.length);
        for (int i = 0; i < fileDirs.length; i++) {
            File fileMDFile = getFileMDFile(fileDirs[i].getName());
            FileMetaData fileMD = (FileMetaData) FileUtil
                    .readSerializable(fileMDFile);
            files.add(fileMD);
        }

        return (FileMetaData[]) files.toArray(new FileMetaData[files.size()]);
    }

    /**
     * 
     * @return all existing ChunkMetaData
     */
    public ChunkMetaData[] getChunkMetaDatas() {
        File[] fileDirs = new File(this.fChunkDir).listFiles();

        if (fileDirs == null)
            return null;

        ArrayList chunks = new ArrayList(fileDirs.length);
        for (int i = 0; i < fileDirs.length; i++) {
            ArrayList fileChunks = getChunkMetaDatas(fileDirs[i].getName());
            if (fileChunks != null)
                chunks.addAll(fileChunks);
        }

        return (ChunkMetaData[]) chunks
                .toArray(new ChunkMetaData[chunks.size()]);
    }

    /**
     * 
     * @param chunkMD
     * @return true if chunk exists and deleting was successful
     * @throws IOException
     */
    public boolean deleteChunk(ChunkMetaData chunkMD) throws IOException {
        synchronized (this.fChunkDir) {
            if (!exists(chunkMD))
                return false;

            File chunkFile = getChunkFile(chunkMD);
            long fileSize = chunkFile.length();

            if (!chunkFile.delete())
                return false;

            ArrayList chunkMDs = getChunkMetaDatas(chunkMD.getFileId());
            chunkMDs.remove(chunkMD);

            if (chunkMDs.isEmpty())
                deleteDirRec(chunkFile.getParentFile());
            else {
                File chunkMDFile = getChunkMDFile(chunkMD.getFileId());
                FileUtil.writeSerializable(chunkMDFile, chunkMDs);
            }

            this.fActDiscSpace -= fileSize;
            fLogger.info("delete chunk '" + chunkMD.getId());
        }

        return true;
    }

    /**
     * Deletes all files from chunkDir.
     * 
     * @return true if every file could be deleted.
     * @throws IOException 
     */
    public boolean deleteAll() throws IOException {
        boolean success = deleteDirRec(new File(this.fChunkDir));
        countFreeDiskSpace();
        return success;
    }

    private File getFileMDFile(String fileID) {

        return new File(PathUtil.concatPath(this.fChunkDir, fileID),
                this.fFileMDName);
    }

    private File getChunkMDFile(String fileId) {

        return new File(PathUtil.concatPath(this.fChunkDir, fileId),
                this.fChunkMDName);
    }

    private File getChunkFile(ChunkMetaData chunkMD) {

        return new File(PathUtil
                .concatPath(this.fChunkDir, chunkMD.getFileId()), chunkMD
                .getId());
    }

    private void countFreeDiskSpace() throws IOException {
        // TODO respect size of metadata
        synchronized (this.fChunkDir) {
            FileMetaData[] fileMDs = getFileMetaDatas();
            this.fActDiscSpace = 0;
            if (fileMDs == null)
                this.fActDiscSpace = 0;
            else
                for (int i = 0; i < fileMDs.length; i++) {
                    this.fActDiscSpace += fileMDs[i].getMaxChunkSize();
                }
        }
    }

    private ArrayList getChunkMetaDatas(String fileID) {
        File chunkMdFile = getChunkMDFile(fileID);
        ArrayList chunkMDs = null;
        try {
            chunkMDs = (ArrayList) FileUtil.readSerializable(chunkMdFile);
        } catch (IOException e) {
            fLogger.error(e.getLocalizedMessage());
        }

        return chunkMDs;
    }

    private void writeFileMetaData(FileMetaData fileMD) throws IOException {

        File fileMDFile = getFileMDFile(fileMD.getFileId());

        if (!fileMDFile.exists()) {
            fLogger.debug("create '" + fileMDFile + "', fileID = "
                    + fileMD.getFileId());

            fileMDFile.getParentFile().mkdirs();
            fileMDFile.createNewFile();
            FileUtil.writeSerializable(fileMDFile, fileMD);
        } else {
            fLogger.debug("update '" + fileMDFile + "', fileID = "
                    + fileMD.getFileId());

            FileMetaData oldFileMD = (FileMetaData) FileUtil
                    .readSerializable(fileMDFile);
            if (!oldFileMD.getFilePath().equals(fileMD.getFilePath()))
                throw new IOException("inconsistent filePath, fileID="
                        + fileMD.getFileId() + " old filePath="
                        + oldFileMD.getFilePath() + " new filePath="
                        + fileMD.getFilePath());
        }
    }

    private void writeChunkMetaData(ChunkMetaData chunkMD) throws IOException {
        File chunkMdFile = getChunkMDFile(chunkMD.getFileId());
        ArrayList chunkMDs = getChunkMetaDatas(chunkMD.getFileId());

        if (chunkMDs == null) {
            fLogger.debug("create '" + chunkMdFile + "', chunkID = "
                    + chunkMD.getId());
            chunkMDs = new ArrayList(1);
        } else {
            fLogger.debug("update '" + chunkMdFile + "', chunkID = "
                    + chunkMD.getId());
        }
        chunkMDs.add(chunkMD);
        FileUtil.writeSerializable(chunkMdFile, chunkMDs);
    }

    private void updateChunkMetaData(ChunkMetaData chunkMD) throws IOException {
        File chunkMdFile = getChunkMDFile(chunkMD.getFileId());
        ArrayList chunkMDs = getChunkMetaDatas(chunkMD.getFileId());

        chunkMDs.remove(chunkMD);
        chunkMDs.add(chunkMD);

        FileUtil.writeSerializable(chunkMdFile, chunkMDs);
    }

    private boolean deleteDirRec(File file) {
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            for (int i = 0; i < subFiles.length; i++) {
                if (subFiles[i].isDirectory())
                    deleteDirRec(subFiles[i]);
                subFiles[i].delete();
            }
        }
        return file.delete();
    }
}
