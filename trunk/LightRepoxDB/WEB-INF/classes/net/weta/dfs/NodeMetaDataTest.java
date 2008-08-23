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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/NodeMetaDataTest.java,v $
 */

package net.weta.dfs;

import junit.framework.TestCase;

/**
 * TODO comment for NodeMetaDataTest
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.3 $
 * 
 */
public class NodeMetaDataTest extends TestCase {
    private NodeMetaData fTestNode;

    private String fHostAddress = "196.255.255.6";

    private int fPort = 12;

    private long freeSpace = 5667;

    /**
     * 
     */
    public NodeMetaDataTest() {
        this.fTestNode = new NodeMetaData(this.fHostAddress, this.fPort,
                this.freeSpace);
    }

    /**
     * 
     */
    public void testGetPort() {
        assertEquals(this.fPort, this.fTestNode.getPort());
    }

    /**
     * 
     */
    public void testGetInetAddress() {
        assertEquals(this.fHostAddress, this.fTestNode.getHostAddress());
    }

    /**
     * 
     */
    public void testLastLifeSign() {
        assertEquals(0, this.fTestNode.getLastLifeSign());
        this.fTestNode.setLastLiveSign(4765757);
        assertEquals(4765757, this.fTestNode.getLastLifeSign());
    }

    /**
     * 
     */
    public void testGetFreeSpace() {
        assertEquals(this.freeSpace, this.fTestNode.getFreeDiskSpace());
    }

    /**
     * 
     */
    public void testGetId() {
        assertNotNull(this.fTestNode.getId());
        NodeMetaData nodeMD2 = new NodeMetaData(
                this.fTestNode.getHostAddress(), this.fTestNode.getPort(),
                this.fTestNode.getFreeDiskSpace());
        assertEquals(this.fTestNode.getId(), nodeMD2.getId());

        nodeMD2 = new NodeMetaData(this.fTestNode.getHostAddress(),
                this.fTestNode.getPort() + 1, this.fTestNode.getFreeDiskSpace());
        assertNotSame(this.fTestNode.getId(), nodeMD2.getId());
    }

    /**
     * 
     */
    public void testEqualsHashCode() {
        NodeMetaData nodeMD2 = new NodeMetaData(
                this.fTestNode.getHostAddress(), this.fTestNode.getPort(),
                this.fTestNode.getFreeDiskSpace());
        assertTrue(this.fTestNode.equals(nodeMD2));
        assertEquals(this.fTestNode.hashCode(), nodeMD2.hashCode());

        nodeMD2 = new NodeMetaData(this.fTestNode.getHostAddress(),
                this.fTestNode.getPort() + 1, this.fTestNode.getFreeDiskSpace());
        assertNotSame(this.fTestNode, nodeMD2);
        assertFalse(this.fTestNode.hashCode() == nodeMD2.hashCode());
    }

}