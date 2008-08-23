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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/FileMetaDataTest.java,v $
 */

package net.weta.dfs;

import junit.framework.TestCase;

/**
 * Tests for FileMetaData.
 * 
 * <br/><br/>created on 24.03.2005
 * 
 * @version $Revision: 1.1 $
 *  
 */
public class FileMetaDataTest extends TestCase {
    private FileMetaData fFMDa;

    private FileMetaData fFMDb;

    /**
     *  
     */
    public FileMetaDataTest() {
        this.fFMDa = new FileMetaData("a", "/patha");
        this.fFMDb = new FileMetaData("b", "/pathb");
    }

    /**
     *  
     */
    public void testsetFilePath() {
        assertEquals("/patha", this.fFMDa.getFilePath());

        this.fFMDa.setFilePath("/pathc");
        assertEquals("/pathc", this.fFMDa.getFilePath());

        this.fFMDa.setFilePath("/patha");
        assertEquals("/patha", this.fFMDa.getFilePath());
    }

    /**
     *  
     */
    public void testEquals() {
        assertFalse(this.fFMDa.equals(this.fFMDb));

        FileMetaData otherFMD = new FileMetaData("a", "/patha");
        assertTrue(this.fFMDa.equals(otherFMD));

        otherFMD = this.fFMDa;
        assertTrue(this.fFMDa.equals(otherFMD));

        assertTrue(this.fFMDa.equals(this.fFMDa));

        String other = new String("aaa");
        assertFalse(this.fFMDa.equals(other));
    }
}