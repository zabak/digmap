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
 * $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/NodeMetaData.java,v $
 */

package net.weta.dfs;

import java.io.Serializable;

/**
 * Container for node meta data.
 * 
 * <br/><br/>created on 13.01.2005
 * 
 * @version $Revision: 1.7 $
 */
public class NodeMetaData implements Serializable {

    private String fHostAddress;

    private int fPort;

    private long fLastLifeSign = 0;

    private long fFreeDiskSpace;

    /**
     * 
     * @param hostAddress
     * @param port
     * @param freeSpace
     */
    public NodeMetaData(String hostAddress, int port, long freeSpace) {
        if (hostAddress == null)
            throw new NullPointerException("hostAddress must not be null");
        this.fHostAddress = hostAddress;
        this.fPort = port;
        this.fFreeDiskSpace = freeSpace;
    }

    /**
     * @return node sever port
     */
    public int getPort() {
        return this.fPort;
    }

    /**
     * @return InetAddress node server IP
     */
    public String getHostAddress() {
        return this.fHostAddress;
    }

    /**
     * @param hostAddress
     */
    public void setHostAddress(String hostAddress) {
        this.fHostAddress = hostAddress;
    }

    /**
     * @return the node id
     */
    public String getId() {
        StringBuffer buffer = new StringBuffer(this.fHostAddress);
        buffer.append(":");
        buffer.append(this.fPort);
        return buffer.toString();
    }

    /**
     * @param time
     */
    public void setLastLiveSign(long time) {
        this.fLastLifeSign = time;
    }

    /**
     * The date of the last connection in milliseconds.
     * 
     * @return last life sign in milliseconds
     */
    public long getLastLifeSign() {
        return this.fLastLifeSign;
    }

    /**
     * @param freeDiscSpace
     */
    public void setFreeDiscSpace(long freeDiscSpace) {
        this.fFreeDiskSpace = freeDiscSpace;
    }

    /**
     * The free space of the node in bytes.
     * 
     * @return Free disk space in bytes
     */
    public long getFreeDiskSpace() {
        return this.fFreeDiskSpace;
    }

    /**
     * Hashcode from id.
     * 
     * @return the hashcode
     */
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Two NodeMetaData equals, if their id (ipaddress + port) equals.
     * 
     * @param obj
     * @return true id equals
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof NodeMetaData))
            return false;

        return this.hashCode() == obj.hashCode();
    }

    /**
     * @return the string representation of this object
     * 
     */
    public String toString() {
        return getId();
    }
}