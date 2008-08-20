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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/client/DFileTest.java,v $
 */

package net.weta.dfs.client;

import java.io.IOException;

import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.FeatherDBProperties;

import junit.framework.TestCase;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;
import net.weta.dfs.server.mds.MetaDataServer;
import net.weta.dfs.util.PathUtil;

/**
 * Tests for class DFile.
 * 
 * <br/><br/>created on 22.02.2005
 * 
 * @version $Revision: 1.19 $
 * 
 */
public class DFileTest extends TestCase {

    private MetaDataServer fMds;
    private DataNodeServer fDns;

    private String mdsAddress;

    private int mdsPort;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
		FeatherDB app = new FeatherDB();
		System.setProperty(Configuration.DFS_CONFIG_PATH,FeatherDBProperties.propsFile.getCanonicalPath());
		this.fMds = new MetaDataServer(Configuration.getInstance());
        this.fMds.startServer();
		this.fDns = new DataNodeServer(Configuration.getInstance());
        this.fDns.startServer();
        this.mdsAddress = this.fMds.getIpAddress();
        this.mdsPort = this.fMds.getPort();
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        this.fMds.stopServer();
        this.fDns.stopServer();
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testLocalFilesSettings() throws IOException {
        this.fMds.stopServer();
        // assertEquals(1, DFile.listRoots().length);
        // assertEquals(PathUtil.SEPERATOR, DFile.listRoots()[0].getPath());

        DFile dFile = null;
        try {
            dFile = new DFile(null, this.mdsAddress, this.mdsPort);
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        // create file object at 1st level
        String path = "dir1";
        dFile = new DFile(path, this.mdsAddress, this.mdsPort);
        checkFile(dFile, path);
        assertNotNull(dFile.getParentFile());

        // create file object 'root dir'
        path = "/";
        dFile = new DFile(path, this.mdsAddress, this.mdsPort);
        checkFile(dFile, path);
        assertNull(dFile.getParentFile());

        // check dir
        path = "dir1/dir2/";
        dFile = new DFile(path, this.mdsAddress, this.mdsPort);
        checkFile(dFile, path);

        // create file object with 'path'
        path = "dir1/dir2/file1.test";
        dFile = new DFile(path, this.mdsAddress, this.mdsPort);
        checkFile(dFile, path);

        // create file object with 'parentPath,childName'
        dFile = new DFile("dir1/dir2", "file1.test", this.mdsAddress,
                this.mdsPort);
        checkFile(dFile, path);

        // check parent file object
        String path2 = PathUtil.getParentDirectoryPath(path);
        DFile dFile2 = dFile.getParentFile();
        checkFile(dFile2, path2);

        // check absolute file object
        dFile2 = dFile.getAbsoluteFile();
        checkFile(dFile2, path);

        // check canonical file object
        dFile2 = dFile.getCanonicalFile();
        checkFile(dFile2, path);
    }

    private void checkFile(DFile file, String path) throws IOException {
        path = PathUtil.correctPath(path);

        assertEquals(PathUtil.getFileName(path), file.getName());
        assertEquals(PathUtil.getParentDirectoryPath(path), file.getParent());
        assertEquals(path, file.getPath());

        assertTrue(file.isAbsolute());
        assertEquals(path, file.getAbsolutePath());
        assertEquals(path, file.getCanonicalPath());

        assertNull(file.toURL());
        assertNull(file.toURI());
    }

    /**
     * @throws IOException 
     * 
     */
    public void testEquals() throws IOException {
        this.fMds.stopServer();
        String dir1 = "dir1/dir2";

        DFile file1 = new DFile(dir1, this.mdsAddress, this.mdsPort);
        DFile file2 = new DFile(dir1, this.mdsAddress, this.mdsPort);

        assertTrue(file1.equals(file2));
        assertEquals(file1.hashCode(), file2.hashCode());

        file2 = new DFile("dir1/file2", this.mdsAddress, this.mdsPort);
        assertFalse(file1.equals(file2));
        assertFalse(file1.hashCode() == file2.hashCode());

        assertFalse(file1.equals(null));
        assertFalse(file1.equals("something"));
    }

    /**
     * @throws IOException
     */
    public void testCreateFiles() throws IOException {
        String dirPath = "/dir1/dir2";
        String fileName = "file1";

        // create file object
        DFile dir = new DFile(dirPath, this.mdsAddress, this.mdsPort);
        assertFalse(dir.exists());
        assertFalse(dir.isDirectory());
        assertFalse(dir.isFile());
        assertNull(dir.list());

        // fail create new file because non-existing parent
        try {
            dir.createNewFile();
            fail("parent does not exist");
        } catch (IOException e) { }
        
        assertTrue(dir.mkdirs());
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
        assertFalse(dir.isFile());
        assertEquals(0, dir.list().length);

        // create file object
        DFile file = new DFile(dir, fileName);
        assertFalse(file.exists());
        assertFalse(file.isDirectory());
        assertFalse(file.isFile());
        assertNull(file.list());

        // create new file
        assertTrue(file.createNewFile());
        assertTrue(file.exists());
        assertFalse(file.isDirectory());
        assertTrue(file.isFile());
        assertNull(file.list());

        dir.getMDSConnection().close();
        file.getMDSConnection().close();

        this.fMds.stopServer();
        try {
            file.exists();
            fail("mds is down");
        } catch (IOException e) {

        }
    }

    /**
     * @throws IOException
     */
    public void testDeleteFiles() throws IOException {
/*        DFile file = new DFile("dir1/dir2/file1.test", this.mdsAddress,
                this.mdsPort);
        DFile dir = new DFile("dir1/dir2/dir3", this.mdsAddress, this.mdsPort);

        // create files
        assertTrue(dir.mkdirs());
        assertTrue(file.createNewFile());
        assertTrue(file.exists());
        assertTrue(dir.exists());

        // delete files
        assertTrue(file.delete());
        assertTrue(dir.delete());

        assertFalse(file.exists());
        assertFalse(dir.exists());
        assertTrue(file.getParentFile().exists());
        assertTrue(dir.getParentFile().exists());

        // delete dir1
        assertTrue(dir.getParentFile().getParentFile().delete());
        assertFalse(dir.getParentFile().exists());

        // create files again
        assertTrue(dir.mkdirs());
        assertTrue(file.createNewFile());

        (dir).getMDSConnection().close();
        (file).getMDSConnection().close();*/
    }

    /**
     * @throws IOException
     */
    public void testList() throws IOException {
        String dir1Path = "/dir1";
        String dir2Path = "/dir1/dir2";
        String dir3Path = "/dir1/dir3";
        String dir4Path = "/dir4";

        String file1Name = "file1";
        String file2Name = "file2";

        // mkdirs
        DFile dir = new DFile(dir1Path, this.mdsAddress, this.mdsPort);
        assertNull(dir.list());
        DFile dir2 = new DFile(dir2Path, this.mdsAddress, this.mdsPort);
        assertTrue(dir2.mkdirs());
        
        
        DFile dir3 = new DFile(dir3Path, this.mdsAddress, this.mdsPort);
        assertTrue(dir3.mkdirs());
        DFile dir4 = new DFile(dir4Path, this.mdsAddress, this.mdsPort);
        assertTrue(dir4.mkdirs());

        assertEquals(2, dir.list().length);

        // create files
        DFile file = new DFile(dir, file1Name);
        assertTrue(file.createNewFile());
        file = new DFile(dir, file2Name);
        assertTrue(file.createNewFile());

        // check list content
        assertEquals(4, dir.list().length);
        assertEquals(dir2.getName(), dir.list()[0]);
        assertEquals(dir3.getName(), dir.list()[1]);
        assertTrue(dir.list()[2].indexOf(file1Name) != -1);
        assertTrue(dir.list()[3].indexOf(file2Name) != -1);

        // check list(FilenameFilter)
        class TestFilter implements DFilenameFilter {
            private String fMustContain;

            /**
             * @param mustContain
             */
            public TestFilter(String mustContain) {
                this.fMustContain = mustContain;
            }

            public boolean accept(DFile dir, String name) {
                return name.indexOf(this.fMustContain) != -1;
            }

        }
        DFilenameFilter filter = new TestFilter("file");
        String[] list = dir.list(filter);
        assertEquals(2, list.length);
        assertTrue(list[0].indexOf(file1Name) != -1);
        assertTrue(list[1].indexOf(file2Name) != -1);

        // check listFiles()
        DFile[] fileList = dir.listFiles();
        assertNotNull(fileList);
        assertEquals(4, fileList.length);

        // check listFiles(FilenameFilter)
        fileList = dir.listFiles(filter);
        assertEquals(2, fileList.length);
        assertEquals(file1Name, fileList[0].getName());
        assertEquals(dir.getPath(), fileList[0].getParent());
        assertEquals(file2Name, fileList[1].getName());
        assertEquals(dir.getPath(), fileList[1].getParent());

        // check listFiles(FileFilter)
        class TestFileFilter implements DFileFilter {
            private String fMustContain;

            /**
             * @param mustContain
             */
            public TestFileFilter(String mustContain) {
                this.fMustContain = mustContain;
            }

            public boolean accept(DFile pathname) {
                return pathname.getName().indexOf(this.fMustContain) != -1;
            }
        }
        DFileFilter fileFilter = new TestFileFilter("dir");
        fileList = dir.listFiles(fileFilter);
        assertEquals(2, fileList.length);

        assertEquals(dir2, fileList[0]);
        assertEquals(dir3, fileList[1]);

        dir.getMDSConnection().close();
        dir2.getMDSConnection().close();
        dir3.getMDSConnection().close();
        dir4.getMDSConnection().close();
        file.getMDSConnection().close();
    }

    /**
     * @throws IOException
     */
    public void testGetFileId() throws IOException {
        DFile file = new DFile("/dir1/dir5/file2.test", this.mdsAddress,
                this.mdsPort);
        assertNull(file.getFileId());
        assertTrue(file.getParentFile().mkdirs());
        assertTrue(file.createNewFile());
        assertNotNull(file.getFileId());
    }

}