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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/vfs/VirtualDirectoryTest.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import net.weta.dfs.server.mds.vfs.VirtualDirectory;
import net.weta.dfs.server.mds.vfs.VirtualFile;

/**
 * VirtualDirectoryTest
 * 
 * <br/><br/>created on 08.03.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class VirtualDirectoryTest extends AbstractVirtualEntryTest {

    private final int MAX_ENTRIES = 10;

    /**
     * 
     */
    public VirtualDirectoryTest() {
        this.fVirEntry = new VirtualDirectory("dirID", "dir");
    }

    /**
     * Directory can contain entries(files/dirs) of same id but not of same
     * name.
     */
    public void testAddFilesAndDirsTwice() {
        // addFile()
        assertTrue(((VirtualDirectory) this.fVirEntry).addFile(new VirtualFile(
                "007", "aFile")));
        assertTrue(((VirtualDirectory) this.fVirEntry).addFile(new VirtualFile(
                "007", "aFile2")));
        assertFalse(((VirtualDirectory) this.fVirEntry)
                .addFile(new VirtualFile("008", "aFile")));

        // addDirectory()
        assertTrue(((VirtualDirectory) this.fVirEntry)
                .addDirectory(new VirtualDirectory("009", "aDir")));
        assertTrue(((VirtualDirectory) this.fVirEntry)
                .addDirectory(new VirtualDirectory("009", "aDir2")));
        assertFalse(((VirtualDirectory) this.fVirEntry)
                .addDirectory(new VirtualDirectory("0010", "aDir")));
    }

    /**
     * 
     */
    public void testGetFileDir() {
        assertEquals(0, ((VirtualDirectory) this.fVirEntry).getFiles().length);
        assertEquals(0,
                ((VirtualDirectory) this.fVirEntry).getDirectories().length);

        for (int i = 0; i < this.MAX_ENTRIES; i++) {
            ((VirtualDirectory) this.fVirEntry).addFile(new VirtualFile("fId"
                    + i, "file" + i));
            ((VirtualDirectory) this.fVirEntry)
                    .addDirectory(new VirtualDirectory("dId" + i, "dir" + i));
        }

        VirtualDirectory[] dirs = ((VirtualDirectory) this.fVirEntry)
                .getDirectories();
        assertNotNull(dirs);
        assertEquals(this.MAX_ENTRIES, dirs.length);

        VirtualFile[] files = ((VirtualDirectory) this.fVirEntry).getFiles();
        assertNotNull(files);
        assertEquals(this.MAX_ENTRIES, files.length);
    }
}