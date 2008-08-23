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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/vfs/VirtualDirectory.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * TODO comment for VirtualDirectory
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class VirtualDirectory extends AbstractVirtualEntry {

    private Set fEntries;

    /**
     * @param id
     * @param name
     */
    public VirtualDirectory(String id, String name) {
        super(id, name);
        this.fEntries = new HashSet();
    }

    /**
     * @param vDir
     * @return true if this directory not already contains the given vDir
     */
    public boolean addDirectory(VirtualDirectory vDir) {
        return this.fEntries.add(vDir);
    }

    /**
     * @param vFile
     * @return true if this directory not already contains the vFile
     */
    public boolean addFile(VirtualFile vFile) {
        return this.fEntries.add(vFile);
    }

    /**
     * All files in this directory.
     * 
     * @return array of entries
     */
    public VirtualFile[] getFiles() {
        ArrayList result = new ArrayList();

        for (Iterator iter = this.fEntries.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof VirtualFile) {
                result.add(element);
            }
        }

        return (VirtualFile[]) result.toArray(new VirtualFile[result.size()]);
    }

    /**
     * All directories in this directory.
     * 
     * @return array of entries
     */
    public VirtualDirectory[] getDirectories() {
        ArrayList result = new ArrayList();

        for (Iterator iter = this.fEntries.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof VirtualDirectory) {
                result.add(element);
            }
        }

        return (VirtualDirectory[]) result.toArray(new VirtualDirectory[result
                .size()]);
    }

    /**
     * @param path
     * @return returns a VirtualDirectory
     */
    public VirtualDirectory getDirectory(String path) {
        VirtualDirectory result = null;

        for (Iterator iter = this.fEntries.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof VirtualDirectory) {
                result = (VirtualDirectory) element;
                if (!path.equals(result.getName())) {
                    result = null;
                } else {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * @param path
     * @return returns a VirtualFile
     */
    public VirtualFile getFile(String path) {
        VirtualFile result = null;

        for (Iterator iter = this.fEntries.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof VirtualFile) {
                result = (VirtualFile) element;
                if (!path.equals(result.getName())) {
                    result = null;
                } else {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * @param dir
     * @return true or false
     */
    public boolean delete(String dir) {
        boolean result = false;

        for (Iterator iter = this.fEntries.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof AbstractVirtualEntry) {
                AbstractVirtualEntry removeDir = (AbstractVirtualEntry) element;
                if (dir.equals(removeDir.getName())) {
                    if (this.fEntries.remove(element)) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }
}