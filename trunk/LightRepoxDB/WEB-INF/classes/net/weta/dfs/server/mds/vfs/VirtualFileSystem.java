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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/vfs/VirtualFileSystem.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.util.PathUtil;

/**
 * The VirtualFileSystem act as in-memory directory-service.
 * 
 * <br/><br/>created on 13.02.2005
 * 
 * @version $Revision: 1.2 $
 */
public class VirtualFileSystem {

    /**
     * Comment for <code>ROOT_DIRECTORY</code>
     */
    public static final String ROOT_DIRECTORY = PathUtil.SEPERATOR;

    private VirtualDirectory fRoot;

    private HashMap fId2File;

    private Object fIdObject;

    private Method fIdMethod;

    /**
     * @param idObject
     * @param idMethod
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     */
    public VirtualFileSystem(Object idObject, String idMethod) throws SecurityException, NoSuchMethodException, IllegalArgumentException {
        this.fIdObject = idObject;
        this.fIdMethod = idObject.getClass().getMethod(idMethod, null);
        this.fRoot = new VirtualDirectory("rootDir", "/");
        this.fRoot.setReadable(true);
        this.fId2File = new HashMap();
    }

    private String getId() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String result = null;
        result = (String) this.fIdMethod.invoke(this.fIdObject, null);
        return result;
    }

    // ----------------------directories-------------------
    /**
     * Creates all directories from path, which aren't in the filesystem yet.
     * 
     * @param path
     *            a correct directory path
     * @return true or false
     */
    public boolean createDirectories(String path) {
        if (isFile(path)) return false;
        boolean result = false;
        VirtualDirectory vDir = this.fRoot;
        VirtualDirectory prevVDir = null;
        String[] tokens = PathUtil.getPathTokens(path);
        String newPath = ROOT_DIRECTORY;
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                prevVDir = vDir;
                vDir = vDir.getDirectory(tokens[i]);
                if (null == vDir) {
                    if (createDirectory(newPath, tokens[i])) {
                        vDir = prevVDir.getDirectory(tokens[i]);
                        result = true;
                    } else {
                        result = false;
                        break;
                    }
                }
                newPath = PathUtil.concatPath(newPath, tokens[i]);
            }
        }

        return result;
    }

    /**
     * @param parentPath
     * @param dirName
     * @return true or false
     */
    public boolean createDirectory(String parentPath, String dirName) {
        boolean result = false;
        VirtualDirectory parentDir = getDirectory(parentPath);

        if (null != parentDir) {
            try {
                VirtualDirectory newDir = new VirtualDirectory(getId(), dirName);
                newDir.setReadable(true);
                parentDir.addDirectory(newDir);
                result = true;
            } catch (Exception e) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Returns the directory to the given path.
     * 
     * @param path
     * @return null or the VirtualDirectory
     * @todo integrate the second loop in the first
     */
    private VirtualDirectory getDirectory(String path) {
        VirtualDirectory result = this.fRoot;
        String[] tokens = PathUtil.getPathTokens(path);

        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                if (null != result) {
                    result = result.getDirectory(tokens[i]);
                } else {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Returns the directory to the given path.
     * 
     * @param path
     * @return null or the VirtualDirectory
     */
    private VirtualFile getFile(String path) {
        VirtualFile result = null;
        String parent = PathUtil.getParentDirectoryPath(path);

        if (null != parent) {
            VirtualDirectory parentDir = getDirectory(parent);

            if (null != parentDir) {
                String dir = PathUtil.getFileName(path);
                result = parentDir.getFile(dir);
            }
        }

        return result;
    }

    /**
     * @param path
     * @return true if path exists and points to a directory
     */
    public boolean isDirectory(String path) {

        return getDirectory(path) != null;
    }

    /**
     * @param path
     * @return true if path exist and points to a file
     */
    public boolean isFile(String path) {

        return getFile(path) != null;
    }

    /**
     * 
     * 
     * @param directoryPath
     * @return all file names an directory names
     */
    public String[] getDirectoryContent(String directoryPath) {
        VirtualDirectory dir = getDirectory(directoryPath);
        if (dir == null)
            return null;

        ArrayList result = new ArrayList();
        VirtualDirectory[] allDirs = dir.getDirectories();
        for (int i = 0; i < allDirs.length; i++) {
            result.add(allDirs[i].getName());
        }
        VirtualFile[] allFiles = dir.getFiles();
        for (int i = 0; i < allFiles.length; i++) {
            result.add(allFiles[i].getName());
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    // ----------------------files-------------------------
    /**
     * 
     * @param filePath
     * @param name
     * @param fileId
     * @return true if file not already exists
     */
    public boolean createFile(String filePath, String name, String fileId) {
        createDirectories(filePath);
        VirtualDirectory dir = getDirectory(filePath);

        VirtualFile newFile = new VirtualFile(fileId, name);
        newFile.setReadable(true);

        boolean success = dir.addFile(newFile);
        if (success)
            this.fId2File.put(fileId, PathUtil.concatPath(filePath, name));
        
        return success;

    }

    /**
     * 
     * @param filePath
     * @param fileId
     * @return  true if file not already exists
     */
    public boolean createFile(String filePath, String fileId) {
        //TODO throw FileNotFoundException if parent don't exist
        String parentDir = PathUtil.getParentDirectoryPath(filePath);
        String fileName = PathUtil.getFileName(filePath);
        return createFile(parentDir, fileName, fileId);
    }

    /**
     * @param path
     * @return true or false
     */
    public boolean exists(String path) {
        return getFile(path) != null || getDirectory(path) != null;
    }

    /**
     * @param filePath
     * @return a collection of the file chunks
     */
    public ChunkMetaData[] getFileChunks(String filePath) {
        ChunkMetaData[] result = null;

        VirtualFile file = getFile(filePath);

        if (null != file) {
            result = file.getChunks();
        }

        return result;
    }

    /**
     * @param filePath
     * @return the length of the file or -1 if not exist
     */
    public long getFileLength(String filePath) {
        long length = 0;

        if (isFile(filePath)) {
            ChunkMetaData[] chunkMDs = getFileChunks(filePath);
            for (int i = 0; i < chunkMDs.length; i++) {
                length += chunkMDs[i].getSize();
            }
        }

        return length;
    }

    /**
     * @param filePath
     * @param maxChunkSize
     * @return true if file exists and no chunk already exist
     */
    public boolean setMaxChunkSize(String filePath, long maxChunkSize) {
        boolean success = false;
        VirtualFile file = getFile(filePath);
        if (file != null && file.getChunks().length <= 0) {
            file.setMaxChunkSize(maxChunkSize);
            success = true;
        }
        return success;
    }

    /**
     * @param filePath
     * @return the maximal size of a files chunk or 0 if it's not set
     */
    public long getMaxChunkSize(String filePath) {
        long length = 0;
        VirtualFile file = getFile(filePath);
        if (file != null)
            length = file.getMaxChunkSize();

        return length;
    }

    /**
     * Delete the file under the given path.
     * 
     * @param path
     * @return false if the entry does not exsit, true otherwise
     */
    public boolean delete(String path) {
        boolean result = false;

        if (null != path) {
            String parent = PathUtil.getParentDirectoryPath(path);
            String name = PathUtil.getFileName(path);

            if ((null != parent) && (null != name)) {
                VirtualDirectory dir = getDirectory(parent);

                if (null != dir) {
                    String fileId = getFileId(path);
                    result = dir.delete(name);
                    if (result) {
                        this.fId2File.remove(fileId);
                    }
                }
            }
        }

        return result;
    }

    // ----------------------chunks----------------------
    /**
     * @param newChunk
     * @param path
     * @return true if file exists
     */
    public boolean addChunkToFile(ChunkMetaData newChunk, String path) {
        boolean result = false;

        VirtualFile file = getFile(path);
        if (null != file) {
            file.addChunk(newChunk);

            result = true;
        }

        return result;
    }

    /**
     * Updates an Chunk if its version is newer than the old one. If the chunk
     * doesn't exist it is added.
     * 
     * @param newChunk
     * @param path
     * @return true if the corresponding file exists and the chunk is updated
     */
    public boolean replaceChunkToFile(ChunkMetaData newChunk, String path) {
        boolean result = true;

        VirtualFile file = getFile(path);
        if (null != file) {
            file.updateChunk(newChunk);

            result = true;
        }

        return result;
    }

    /**
     * @param path
     * @return A file id
     */
    public String getFileId(String path) {
        String result = null;

        VirtualFile file = getFile(path);
        if (null != file) {
            result = file.getId();
        }

        return result;
    }

    /**
     * 
     * 
     * @param id
     * @return The file path to the file id or null if not exist.
     */
    public String getFilePath(String id) {
        return (String) this.fId2File.get(id);
    }
}