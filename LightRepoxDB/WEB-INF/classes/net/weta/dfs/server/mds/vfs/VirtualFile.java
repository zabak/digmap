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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/vfs/VirtualFile.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.weta.dfs.ChunkMetaData;

/**
 * TODO comment for VirtualFile
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class VirtualFile extends AbstractVirtualEntry {

    private Set fChunks;

    private long fMaxChunkSize = 0;

    private static Comparator fChunkPosComparator = new Comparator() {
        /**
         * Assumes that both objects are ChunkMetaData's.
         * 
         * @param o1
         * @param o2
         * @return a negative int if position of o1 is lesser then position of
         *         o2, 0 if their position equals, a positive int otherwise
         */
        public int compare(Object o1, Object o2) {
            return ((ChunkMetaData) o1).getPosition()
                    - ((ChunkMetaData) o2).getPosition();
        }
    };

    /**
     * @param id
     * @param name
     * 
     */
    public VirtualFile(String id, String name) {
        super(id, name);
        this.fChunks = new HashSet();
    }

    /**
     * @param maxChunkSize
     *            The maxChunkSize to set.
     */
    public void setMaxChunkSize(long maxChunkSize) {
        this.fMaxChunkSize = maxChunkSize;
    }

    /**
     * @return Returns the maxChunkSize.
     */
    public long getMaxChunkSize() {
        return this.fMaxChunkSize;
    }

    /**
     * @param chunkMD
     * @return false if chunk already contained
     */
    public boolean addChunk(ChunkMetaData chunkMD) {
        // TODO could it happen that a chunk gets add with different id but same
        // pos ?
        return this.fChunks.add(chunkMD);
    }

    /**
     * @param chunkMD
     * @return true if version of the given chunkMD was higher then the already
     *         contained old chunk or if no old chunk exists
     */
    public boolean updateChunk(ChunkMetaData chunkMD) {
        boolean tookChunk = false;
        ChunkMetaData oldChunk = getChunk(chunkMD);
        if (null == oldChunk) {
            addChunk(chunkMD);
            tookChunk = true;
        } else if (chunkMD.getVersion() > oldChunk.getVersion()) {
            this.fChunks.remove(oldChunk);
            this.addChunk(chunkMD);
            tookChunk = true;
        }

        return tookChunk;
    }

    /**
     * All chunks as an array sorted by their positions.
     * 
     * @return ChunkMetaData array
     */
    public ChunkMetaData[] getChunks() {

        ChunkMetaData[] chunkMDs = (ChunkMetaData[]) this.fChunks
                .toArray(new ChunkMetaData[0]);
        Arrays.sort(chunkMDs, fChunkPosComparator);
        return chunkMDs;
    }

    /**
     * The chunk of the given position.
     * 
     * @param position
     * @return ChunkMetaData
     */
    public ChunkMetaData getChunk(int position) {
        ChunkMetaData chunkMD = null;

        for (Iterator iter = this.fChunks.iterator(); iter.hasNext();) {
            ChunkMetaData element = (ChunkMetaData) iter.next();
            if (position == element.getPosition()) {
                chunkMD = element;
                break;
            }
        }

        return chunkMD;
    }

    /**
     * @param chunkMD
     * @return the chunk that equals the given chunkMd or null if none exists
     */
    public ChunkMetaData getChunk(ChunkMetaData chunkMD) {
        for (Iterator iter = this.fChunks.iterator(); iter.hasNext();) {
            ChunkMetaData element = (ChunkMetaData) iter.next();
            if (chunkMD.equals(element)) {
                return element;
            }
        }
        return null;
    }
}
