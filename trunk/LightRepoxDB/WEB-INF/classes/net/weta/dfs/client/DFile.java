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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/client/DFile.java,v $
 */

package net.weta.dfs.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import net.weta.dfs.com.CommandException;
import net.weta.dfs.com.Connection;
import net.weta.dfs.com.ServerFaceMDS;
import net.weta.dfs.util.PathUtil;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * Class to handle a distributed file. Its methods are similar to java.io.File.
 * 
 * <br/><br/>created on 22.02.2005
 * 
 * @version $Revision: 1.15 $
 * 
 */
public class DFile implements Serializable, Comparable {

    static Category fLogger = Logger.getLogger(DFile.class);

    private String fPath;

    private Connection fMdsConnection;

    /**
     * @param pathname
     * @param ipAddress
     * @param port
     */
    public DFile(String pathname, String ipAddress, int port) {
        this.fPath = PathUtil.correctPath(pathname);
        this.fMdsConnection = new Connection(ipAddress, port);
    }

    /**
     * 
     * @param parent
     * @param child
     * @param ipAddress
     * @param port
     */
    public DFile(String parent, String child, String ipAddress, int port) {
        this(PathUtil.concatPath(parent, child), ipAddress, port);
    }

    /**
     * 
     * @param parent
     * @param child
     */
    public DFile(DFile parent, String child) {
        this(PathUtil.concatPath(parent.getPath(), child), parent
                .getMDSConnection().getIpAddress(), parent.getMDSConnection()
                .getPort());
    }

    /**
     * 
     * @return The name of the file.
     */
    public String getName() {
        return PathUtil.getFileName(this.fPath);
    }

    /**
     * @return The parent directory path.
     */
    public String getParent() {
        return PathUtil.getParentDirectoryPath(this.fPath);
    }

    /**
     * @return The parent DFile object.
     */
    public DFile getParentFile() {
        String parent = getParent();
        if (parent == null)
            return null;

        return new DFile(parent, getMDSConnection().getIpAddress(),
                getMDSConnection().getPort());
    }

    /**
     * @return The path of the file.
     */
    public String getPath() {
        return this.fPath;
    }

    /**
     * 
     * @return True if the path is absolute otherwise false.
     */
    public boolean isAbsolute() {
        return true;
    }

    /**
     * @return The absolute path.
     */
    public String getAbsolutePath() {
        return this.fPath;
    }

    /**
     * @return A DFile object from the absolute path.
     */
    public DFile getAbsoluteFile() {
        return new DFile(getAbsolutePath(), getMDSConnection().getIpAddress(),
                getMDSConnection().getPort());
    }

    /**
     * It is the same like the absolute path.
     * 
     * @return The canonical path.
     */
    public String getCanonicalPath() {
        return this.fPath;
    }

    /**
     * @return A DFile object from the canonical path.
     */
    public DFile getCanonicalFile() {
        return new DFile(getCanonicalPath(), getMDSConnection().getIpAddress(),
                getMDSConnection().getPort());
    }

    /**
     * Not implemented yet.
     * 
     * @return null
     * @throws MalformedURLException
     * @todo implement toURL()
     */
    public URL toURL() throws MalformedURLException {
        return null;
    }

    /**
     * Not implemented yet.
     * 
     * @return null
     * @todo implement toURI()
     */
    public URI toURI() {
        return null;
    }

    /**
     * Not implemented yet.
     * 
     * @return false
     * @todo implement canRead()
     */
    public boolean canRead() {
        // TODO implement if FileSystem substitute this functionality
        return true;
    }

    /**
     * Not implemented yet.
     * 
     * @return false
     * @todo implement canWrite()
     */
    public boolean canWrite() {
        // TODO implement if FileSystem substitute this functionality
        return true;
    }

    /**
     * @return True if it exists, false otherwise.
     * @throws IOException
     */
    public boolean exists() throws IOException {
        return ServerFaceMDS.fileExists(getMDSConnection(), this.fPath);
    }

    /**
     * @return True if it is a directory and exist, false otherwise.
     * @throws IOException
     */
    public boolean isDirectory() throws IOException {
        boolean isDir = ServerFaceMDS.fileIsDirectory(getMDSConnection(),
                this.fPath);
        return isDir;

    }

    /**
     * @return True if it is a file and exist, false otherwise.
     * @throws IOException
     */
    public boolean isFile() throws IOException {
        boolean isFile = ServerFaceMDS
                .fileIsFile(getMDSConnection(), this.fPath);

        return isFile;

    }

    /**
     * Not implemented yet.
     * 
     * @return false
     * @todo implement isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Not implemented yet.
     * 
     * @return 0
     * @todo implement lastModified()
     */
    public long lastModified() {
        // TODO implement if FileSystem substitute this functionality
        return 0;
    }

    /**
     * @return Return the length of the file, otherwise 0.
     * @throws IOException
     */
    public long length() throws IOException {
        long fileLength = ServerFaceMDS.fileGetLength(getMDSConnection(),
                this.fPath);
        return fileLength;
    }

    /**
     * @return True if the file is successful created, false if exists before.
     * @throws IOException
     */
    public boolean createNewFile() throws IOException {
        boolean notExistentBefore;
        try {
            notExistentBefore = ServerFaceMDS.fileCreate(getMDSConnection(),
                    this.fPath);
        } catch (CommandException e) {
            throw new IOException(e.getMessage());
        }

        return notExistentBefore;
    }

    /**
     * @return true if file was existend and is now deleted, false otherwise.
     * @throws IOException
     */
    public boolean delete() throws IOException {
        boolean success = ServerFaceMDS.fileDelete(getMDSConnection(),
                this.fPath);

        return success;
    }

    /**
     * Not implemented yet.
     * 
     * @todo implement deleteOnExit()
     */
    public void deleteOnExit() {
        // TODO implement if FileSystem substitute this functionality
    }

    /**
     * @return The content of the current directory if it is one and exists.
     *         Otherwise null.
     * @throws IOException
     */
    public String[] list() throws IOException {
        String[] files = ServerFaceMDS.fileList(this.getMDSConnection(),
                this.fPath);

        return files;
    }

    /**
     * Uses DFilenameFilter to specify the file names that should be returned.
     * 
     * @param filter
     * @return The content of the current directory if it is one and exists.
     *         Otherwise null.
     * @throws IOException
     */
    public String[] list(DFilenameFilter filter) throws IOException {
        String names[] = list();
        if ((names == null) || (filter == null)) {
            return names;
        }
        ArrayList v = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            if (filter.accept(this, names[i])) {
                v.add(names[i]);
            }
        }
        return (String[]) (v.toArray(new String[0]));
    }

    /**
     * @return an array of all files and directories contained by this directory
     *         if it is a directory, otherwise null
     * @throws IOException
     * @see java.io.File
     */
    public DFile[] listFiles() throws IOException {
        String[] fileNames = list();
        if (fileNames == null)
            return null;

        DFile[] files = new DFile[fileNames.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new DFile(this, fileNames[i]);
        }

        return files;
    }

    /**
     * @param filter
     * @return Array of DFile objects.
     * @throws IOException
     */
    public DFile[] listFiles(DFilenameFilter filter) throws IOException {
        String fileNames[] = list();
        if (fileNames == null || filter == null)
            return null;

        ArrayList files = new ArrayList();
        for (int i = 0; i < fileNames.length; i++) {
            if (filter.accept(this, fileNames[i])) {
                files.add(new DFile(this, fileNames[i]));
            }
        }
        return (DFile[]) (files.toArray(new DFile[0]));
    }

    /**
     * @param filter
     * @return Array of DFile objects.
     * @throws IOException
     */
    public DFile[] listFiles(DFileFilter filter) throws IOException {
        String fileNames[] = list();
        if (fileNames == null)
            return null;

        ArrayList files = new ArrayList();
        for (int i = 0; i < fileNames.length; i++) {
            DFile f = new DFile(this, fileNames[i]);
            if ((filter == null) || filter.accept(f)) {
                files.add(f);
            }
        }
        return (DFile[]) (files.toArray(new DFile[0]));
    }

    /**
     * @return True if the directory can be created and is created, otherwise
     *         false.
     * @throws IOException
     */
    public boolean mkdir() throws IOException {
        if (!ServerFaceMDS.fileExists(getMDSConnection(), getParent()))
            return false;

        return mkdirs();
    }

    /**
     * Creates all directories in the path if they not exist.
     * 
     * @return True if the file can be created and is created, otherwise false.
     * @throws IOException
     */
    public boolean mkdirs() throws IOException {
        boolean success = ServerFaceMDS.fileMkDirs(getMDSConnection(),
                this.fPath);
        return success;
    }

    /**
     * Not implemented yet.
     * 
     * @param dest
     * @return false
     * @todo implement renameTo(DFile dest)
     */
    public boolean renameTo(DFile dest) {
        // TODO implement if FileSystem substitute this functionality
        return false;
    }

    /**
     * Not implemented yet.
     * 
     * @param time
     * @return false
     * @todo implement setLastModified(long time)
     */
    public boolean setLastModified(long time) {
        // TODO implement if FileSystem substitute this functionality
        return false;
    }

    /**
     * Not implemented yet.
     * 
     * @return false
     * @todo implement setReadOnly()
     */
    public boolean setReadOnly() {
        // TODO implement if FileSystem substitute this functionality
        return false;
    }

    /**
     * Not implemented yet.
     * 
     * @return null
     * @todo implement listRoots()
     */
    public static DFile[] listRoots() {
        // TODO return new File[] {new DFile("/")} will not work until we need
        // port & ip in constructor
        return null;
    }

    /**
     * Not implemented yet.
     * 
     * @param prefix
     * @param suffix
     * @param directory
     * @return null
     * @throws IOException
     * @todo implement createTempFile()
     */
    public static DFile createTempFile(String prefix, String suffix,
            DFile directory) throws IOException {
        // TODO implement if FileSystem substitute this functionality
        return null;
    }

    /**
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static DFile createTempFile(String prefix, String suffix)
            throws IOException {
        // TODO implement if FileSystem substitute this functionality
        return null;
    }

    /**
     * @param pathname
     * @return
     */
    public int compareTo(DFile pathname) {
        // TODO file compare with something other then hashcode
        if (this.fPath.equals(pathname.getPath()))
            return 0;

        if (this.fPath.hashCode() > pathname.getPath().hashCode()) {
            return 1;
        }

        return -1;
    }

    public int compareTo(Object o) {
        return compareTo((DFile) o);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DFile))
            return false;

        return this.fPath.equals(((DFile) obj).getPath());
    }

    /**
     * @return the hashcode of the path.
     */
    public int hashCode() {
        return this.fPath.hashCode();
    }

    /**
     * @return Returns a connection to mds.
     */
    public Connection getMDSConnection() {
        return this.fMdsConnection;
    }

    /**
     * @return Returns the FileId.
     * @throws IOException
     */
    public String getFileId() throws IOException {
        String fileId = ServerFaceMDS.fileGetId(getMDSConnection(), this.fPath);
        return fileId;
    }

}