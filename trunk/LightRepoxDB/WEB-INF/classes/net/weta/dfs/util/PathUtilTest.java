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
 * $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/util/PathUtilTest.java,v $
 */

package net.weta.dfs.util;

import java.util.Arrays;
import java.util.Collection;

import net.weta.dfs.util.PathUtil;

import junit.framework.TestCase;

/**
 * 
 * created on 12.02.2005
 * 
 * @author jz
 * @version $Revision: 1.1 $
 *  
 */
public class PathUtilTest extends TestCase {

    /**
     *  
     */
    public void testCorrectPath() {
        assertEquals("/", PathUtil.SEPERATOR);

        assertEquals("/", PathUtil.correctPath("/"));
        assertEquals("/", PathUtil.correctPath(""));
        assertEquals("/", PathUtil.correctPath("    "));
        
        // test valid pathes
        String dir = "/dir1/dir2/";
        String file = "/dir1/dir2/file.test";
        assertEquals("/dir1/dir2", PathUtil.correctPath(dir));
        assertEquals("/dir1/dir2/file.test", PathUtil.correctPath(file));

        dir = "/";
        assertEquals("/", PathUtil.correctPath(dir));

        dir = "\\";
        assertEquals("/", PathUtil.correctPath(dir));

        dir = "\\dir1\\dir2\\";
        file = "\\dir1\\dir2\\file.test";
        assertEquals("/dir1/dir2", PathUtil.correctPath(dir));
        assertEquals("/dir1/dir2/file.test", PathUtil.correctPath(file));

        // test invalid pathes
        String path = "////dir1//dir2//////";
        assertEquals("/dir1/dir2", PathUtil.correctPath(path));

        path = "\\\\dir1\\dir2\\\\\\";
        assertEquals("/dir1/dir2", PathUtil.correctPath(path));

        path = "\\//dir1\\/dir2\\/\\";
        assertEquals("/dir1/dir2", PathUtil.correctPath(path));

        path = "\\//dir1/\\dir2//\\//";
        assertEquals("/dir1/dir2", PathUtil.correctPath(path));
    }

    /**
     *  
     */
    public void testGetParentPath() {
        assertNull(PathUtil.getParentDirectoryPath("/"));
        assertEquals("/", PathUtil.getParentDirectoryPath("/dir1"));
        assertEquals("/dir1", PathUtil.getParentDirectoryPath("/dir1/dir2"));
        assertEquals("/dir1/dir2", PathUtil
                .getParentDirectoryPath("/dir1/dir2/file.test"));

    }

    /**
     *  
     */
    public void testGetFileName() {
        assertEquals("/", PathUtil.getFileName("/"));
        assertEquals("dir2", PathUtil.getFileName("/dir1/dir2"));
        assertEquals("file", PathUtil.getFileName("/dir1/dir2/file"));
    }

    /**
     *  
     */
    public void testGetPathTokens() {
        String[] result = PathUtil
                .getPathTokens("/kfhsdgmdf.gmd,lg/dgojs/dlgj/dgkjsdkgksdgmbv<s, hfgh/");
        assertNotNull(result);
        assertEquals(5,result.length);
        Collection map = Arrays.asList(result);
        assertTrue(map.contains("kfhsdgmdf.gmd,lg"));
        assertTrue(map.contains("dgojs"));
        assertTrue(map.contains("dlgj"));
        assertTrue(map.contains("dgkjsdkgksdgmbv<s, hfgh"));

        result = PathUtil.getPathTokens("fgdflgh");
        assertNotNull(result);
        assertEquals(1,result.length);
        map = Arrays.asList(result);
        assertTrue(map.contains("fgdflgh"));
        
        result = PathUtil.getPathTokens("fgdflgh/sdgfd");
        assertNotNull(result);
        assertEquals(2,result.length);
        map = Arrays.asList(result);
        assertTrue(map.contains("fgdflgh"));
        assertTrue(map.contains("sdgfd"));
    }
    
    
    /**
     * 
     */
    public void testConcatPath() {
        assertEquals("////a/bhgfhf/////", PathUtil.concatPath("////a////////", "/////bhgfhf/////"));
        assertEquals("a/bhgfhf", PathUtil.concatPath("a//\\//\\////", "///\\\\\\//bhgfhf"));
    }
}