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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/FileMetaData.java,v $
 */

package net.weta.dfs;

import java.io.Serializable;

/**
 * Metadata of a file that will be saved on the datanodeserver if the dns
 * contains a chunk of the file.
 * 
 * <br/><br/>created on 01.03.2005
 * 
 * @version $Revision: 1.7 $
 * 
 */
public class FileMetaData implements Serializable {

    private String fFileId;

    private String fFilePath;

    private long fMaxChunkSize;

    /**
     * 
     * @param fileID
     * @param filePath
     */
    public FileMetaData(String fileID, String filePath) {
        if (fileID == null || filePath == null)
            throw new NullPointerException(
                    "fileId and filePath must not be null");
        this.fFileId = fileID;
        this.fFilePath = filePath;
    }

    /**
     * @return Returns the fileId.
     */
    public String getFileId() {
        return this.fFileId;
    }

    /**
     * @param filePath
     *            The filePath to set.
     */
    public void setFilePath(String filePath) {

        this.fFilePath = filePath;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath() {

        return this.fFilePath;
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
     * @return The hashcode of the file id.
     */
    public int hashCode() {

        return this.fFileId.hashCode();
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

        if (!(obj instanceof FileMetaData))
            return false;

        return this.hashCode() == obj.hashCode();
    }
}