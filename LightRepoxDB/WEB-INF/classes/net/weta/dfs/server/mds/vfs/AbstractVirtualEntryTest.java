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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/vfs/AbstractVirtualEntryTest.java,v $
 */

package net.weta.dfs.server.mds.vfs;

import net.weta.dfs.server.mds.vfs.AbstractVirtualEntry;
import junit.framework.TestCase;

/**
 * Tests for VirtualEntry.
 * 
 * <br/><br/>created on 07.03.2005
 * 
 * @version $Revision: 1.1 $
 *  
 */
public abstract class AbstractVirtualEntryTest extends TestCase {
    protected AbstractVirtualEntry fVirEntry;

    /**
     *  
     */
    public void testEntrySetGet() {
        assertEquals(0, this.fVirEntry.getReplicMax());
        assertEquals(0, this.fVirEntry.getReplicMin());

        this.fVirEntry.setReplicMax(10);
        this.fVirEntry.setReplicMin(1);
        assertEquals(1, this.fVirEntry.getReplicMin());
        assertEquals(10, this.fVirEntry.getReplicMax());

        this.fVirEntry.setReplicMax(10);
        this.fVirEntry.setReplicMin(70);
        assertEquals(70, this.fVirEntry.getReplicMin());
        assertEquals(10, this.fVirEntry.getReplicMax());

        this.fVirEntry.setReplicMax(70);
        this.fVirEntry.setReplicMin(80);
        assertEquals(80, this.fVirEntry.getReplicMin());
        assertEquals(70, this.fVirEntry.getReplicMax());
    }

    /**
     *  
     */
    public void testReadWriteable() {
        this.fVirEntry.setReadable(true);
        this.fVirEntry.setWriteable(true);

        assertTrue(this.fVirEntry.isReadable());
        assertTrue(this.fVirEntry.isWriteable());

        this.fVirEntry.setReadable(false);
        this.fVirEntry.setWriteable(false);

        assertFalse(this.fVirEntry.isReadable());
        assertFalse(this.fVirEntry.isWriteable());
    }
}
