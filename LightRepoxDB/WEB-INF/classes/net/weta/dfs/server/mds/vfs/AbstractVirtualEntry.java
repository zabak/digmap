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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/vfs/AbstractVirtualEntry.java,v $
 */

package net.weta.dfs.server.mds.vfs;


/**
 * TODO comment for AbstractVirtualEntry
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public abstract class AbstractVirtualEntry {

    private String fId;

    private String fName;

    private int fReplicMax;

    private int fReplicMin;

    private int fReplicCur;

    private boolean fReadable;

    private boolean fWriteable;

    /**
     * @param id
     * @param name
     * 
     */
    public AbstractVirtualEntry(String id, String name) {
        this.fName = name;
        this.fReplicMin = 0;
        this.fReplicMax = 0;
        this.fReplicCur = 0;
        this.fId = id;
    }

    /**
     * The Id of the entry.
     * 
     * @return The ID
     */
    public String getId() {
        return this.fId;
    }

    /**
     * @return Returns the fReplicMax.
     */
    public int getReplicMax() {
        return this.fReplicMax;
    }

    /**
     * @param replicMax
     *            The fReplicMax to set.
     */
    public void setReplicMax(int replicMax) {
        this.fReplicMax = replicMax;
    }

    /**
     * @return Returns the fReplicMin.
     */
    public int getReplicMin() {
        return this.fReplicMin;
    }

    /**
     * @param replicMin
     *            The fReplicMin to set.
     */
    public void setReplicMin(int replicMin) {
        this.fReplicMin = replicMin;
    }

    /**
     * @return Returns the readable.
     */
    public boolean isReadable() {
        return this.fReadable;
    }

    /**
     * @param readable
     *            The readable to set.
     */
    public void setReadable(boolean readable) {
        this.fReadable = readable;
    }

    /**
     * @return Returns the fWriteable.
     */
    public boolean isWriteable() {
        return this.fWriteable;
    }

    /**
     * @param writeable
     *            The fWriteable to set.
     */
    public void setWriteable(boolean writeable) {
        this.fWriteable = writeable;
    }

    /**
     * @return Returns the entry name.
     */
    public String getName() {
        return this.fName;
    }

    /**
     * @param name
     *            The entry name to set.
     */
    public void setName(String name) {
        this.fName = name;
    }

    /**
     * @return the hashcode of the name
     */
    public int hashCode() {
        return this.fName.hashCode();
    }

    /**
     * Two AbstractVirtualEntry's equals if their names equals.
     * 
     * @param obj
     * @return true if obj is an AbstractVirtualEntry and has the same hashCode
     * 
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof AbstractVirtualEntry))
            return false;

        return this.hashCode() == obj.hashCode();
    }
}