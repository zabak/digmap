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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/vfs/VirtualFileTest.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import net.weta.dfs.ChunkMetaData;
import net.weta.dfs.server.mds.vfs.VirtualFile;

/**
 * VirtualFileTest
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class VirtualFileTest extends AbstractVirtualEntryTest {

    /**
     * 
     */
    public VirtualFileTest() {
        this.fVirEntry = new VirtualFile("fileID", "file");
    }

    /**
     * 
     */
    public void testAddChunk() {
        ChunkMetaData chunkMD = new ChunkMetaData("id_a", "file_id_1", 1);
        ((VirtualFile) this.fVirEntry).addChunk(chunkMD);
        chunkMD = new ChunkMetaData("id_b", "file_id_1", 2);
        ((VirtualFile) this.fVirEntry).addChunk(chunkMD);
        chunkMD = new ChunkMetaData("id_b", "file_id_1", 3);
        ((VirtualFile) this.fVirEntry).addChunk(chunkMD);

        ChunkMetaData[] chunkMDs = ((VirtualFile) this.fVirEntry).getChunks();
        assertEquals(2, chunkMDs.length);

        chunkMD = ((VirtualFile) this.fVirEntry).getChunk(1);
        assertEquals("id_a", chunkMD.getId());
        assertEquals("file_id_1", chunkMD.getFileId());
        assertEquals(1, chunkMD.getPosition());
        chunkMD = ((VirtualFile) this.fVirEntry).getChunk(2);
        assertEquals("id_b", chunkMD.getId());
        assertEquals("file_id_1", chunkMD.getFileId());
        assertEquals(2, chunkMD.getPosition());

        assertNull(((VirtualFile) this.fVirEntry).getChunk(3));
    }

    /**
     * 
     */
    public void testGetChunks() {
        ChunkMetaData chunkMD0 = new ChunkMetaData("chunkId0", "fileID0", 0);
        ChunkMetaData chunkMD1 = new ChunkMetaData("chunkId1", "fileID1", 1);
        ChunkMetaData chunkMD2 = new ChunkMetaData("chunkId2", "fileID2", 2);

        ((VirtualFile) this.fVirEntry).addChunk(chunkMD0);
        ((VirtualFile) this.fVirEntry).addChunk(chunkMD2);
        ((VirtualFile) this.fVirEntry).addChunk(chunkMD1);

        ChunkMetaData[] chunkMDs = ((VirtualFile) this.fVirEntry).getChunks();
        assertEquals(3, chunkMDs.length);
        // test order
        assertEquals(chunkMD0, chunkMDs[0]);
        assertEquals(chunkMD1, chunkMDs[1]);
        assertEquals(chunkMD2, chunkMDs[2]);
    }
}