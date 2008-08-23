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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/ChunkMetaDataTest.java,v $
 */

package net.weta.dfs;

import junit.framework.TestCase;

/**
 * TODO comment for ChunkMetaDataTest
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.6 $
 *  
 */
public class ChunkMetaDataTest extends TestCase {
    private ChunkMetaData fChunkMD;

    private String fId = "id_a";

    private String fFileId = "file_id";

    private int pos = 0;

    /**
     *  
     */
    public ChunkMetaDataTest() {
        this.fChunkMD = new ChunkMetaData(this.fId, this.fFileId, this.pos);
    }

    /**
     *  
     */
    public void testGet() {
        assertEquals(this.fId, this.fChunkMD.getId());
        assertEquals(this.fFileId, this.fChunkMD.getFileId());
        assertEquals(this.pos, this.fChunkMD.getPosition());
        assertEquals(0, this.fChunkMD.getVersion());
        assertEquals(0, this.fChunkMD.getSize());

        this.fChunkMD.setSize(34647647);
        assertEquals(34647647, this.fChunkMD.getSize());
    }

    /**
     *  
     */
    public void testEquals() {
        assertTrue(this.fChunkMD.equals(this.fChunkMD));
        assertTrue(this.fChunkMD.equals(new ChunkMetaData(
                this.fChunkMD.getId(), this.fChunkMD.getFileId(), this.fChunkMD
                        .getPosition())));
        assertFalse(this.fChunkMD.equals(null));
        assertFalse(this.fChunkMD.equals(new ChunkMetaData("otherID",
                this.fChunkMD.getFileId(), this.fChunkMD.getPosition())));

        ChunkMetaData chunkMD = new ChunkMetaData(this.fChunkMD.getId(),
                this.fChunkMD.getFileId(), this.fChunkMD.getPosition());
        chunkMD.setVersion(2);
        assertTrue(this.fChunkMD.equals(chunkMD));

        chunkMD = new ChunkMetaData(this.fChunkMD.getId(),
                this.fChunkMD.getFileId(), this.fChunkMD.getPosition());
        chunkMD.setVersion(2);
        assertTrue(this.fChunkMD.equals(chunkMD));
    }
}