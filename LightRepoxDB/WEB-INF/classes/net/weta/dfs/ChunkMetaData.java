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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/ChunkMetaData.java,v $
 */

package net.weta.dfs;

import java.io.Serializable;

/**
 * Holds all informations for chunk of a file.
 * 
 * <br/><br/>created on 14.01.2005
 * 
 * @version $Revision: 1.11 $
 */
public class ChunkMetaData implements Serializable {

    private String fId;

    private String fFileId;

    private long fSize;

    private int fPosition;

    private int fVersion;

    /**
     * @param id
     * @param fileId
     * @param pos
     */
    public ChunkMetaData(String id, String fileId, int pos) {
        if (id == null || fileId == null)
            throw new NullPointerException("id and fileId must not be null");
        this.fId = id;
        this.fSize = 0;
        this.fPosition = pos;
        this.fVersion = 0;
        this.fFileId = fileId;
    }

    /**
     * @return Returns the id of the chunk.
     */
    public String getId() {
        return this.fId;
    }

    /**
     * @return Returns the size.
     */
    public long getSize() {
        return this.fSize;
    }

    /**
     * @param sizeToSet
     *            The size to set.
     */
    public void setSize(long sizeToSet) {
        this.fSize = sizeToSet;
    }

    /**
     * @return the position of the chunk in the file
     */
    public int getPosition() {
        return this.fPosition;
    }

    /**
     * @return chunk version
     */
    public int getVersion() {
        return this.fVersion;
    }

    /**
     * @param version
     */
    public void setVersion(int version) {
        this.fVersion = version;
    }

    /**
     * @return Returns the fileId.
     */
    public String getFileId() {
        return this.fFileId;
    }

    /**
     * @return The hashcode of the chunk id.
     */
    public int hashCode() {
        return this.fId.hashCode();
    }

    /**
     * Equals when the hashcode of the two objects are equal.
     * 
     * @param obj
     * @return Returns true when equal.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof ChunkMetaData))
            return false;

        return this.hashCode() == obj.hashCode();
    }
}
