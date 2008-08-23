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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/util/CircularListTest.java,v $
 */

package net.weta.dfs.util;

import net.weta.dfs.util.CircularList;
import junit.framework.TestCase;

/**
 * CircularListTest
 * 
 * <br/><br/>created on 28.04.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */
public class CircularListTest extends TestCase {

    /**
     * 
     */
    public void testConstructor() {
        CircularList list = new CircularList();
        assertEquals(0, list.size());
        assertEquals(null, list.getNext());
        assertEquals(null, list.getTop());
        list.moveToEnd();
        list.moveToMid();
    }

    /**
     * 
     */
    public void testAdd_GetNext() {
        CircularList list = new CircularList();
        assertEquals(0, list.size());

        int valueCount = 100;
        for (int i = 0; i < valueCount; i++) {
            list.add(new Integer(i));
        }
        assertEquals(valueCount, list.size());
        assertEquals(new Integer(valueCount - 1), list.getTop());
        assertEquals(new Integer(valueCount - 1), list.getTop());

        for (int i = 0; i < valueCount; i++) {
            assertEquals(new Integer(valueCount - 1 - i), list.getNext());
        }
        for (int i = 0; i < valueCount; i++) {
            assertEquals(new Integer(valueCount - 1 - i), list.getNext());
        }

        list.add(new Integer(-1));
        assertEquals(new Integer(-1), list.getTop());
        assertEquals(new Integer(-1), list.getNext());
    }

    /**
     * 
     */
    public void testMoveToEnd() {
        CircularList list = new CircularList();
        int valueCount = 10;
        for (int i = 0; i < valueCount; i++) {
            list.add(new Integer(i));
        }

        assertEquals(new Integer(valueCount - 1), list.getTop());
        list.moveToEnd();
        assertEquals(new Integer(valueCount - 2), list.getTop());
        for (int i = 0; i < valueCount - 1; i++) {
            list.getNext();
        }
        assertEquals(new Integer(valueCount - 1), list.getTop());

        for (int i = 0; i < valueCount; i++) {
            list.moveToEnd();
        }
        assertEquals(new Integer(valueCount - 1), list.getTop());
    }

    /**
     * 
     */
    public void testMoveToMid() {
        CircularList list = new CircularList();
        // test just work if valueCount is even
        int valueCount = 10;
        for (int i = 0; i < valueCount; i++) {
            list.add(new Integer(i));
        }

        assertEquals(new Integer(valueCount - 1), list.getTop());
        list.moveToMid();
        assertEquals(new Integer(valueCount - 2), list.getTop());
        for (int i = 0; i < (valueCount - 1) / 2; i++) {
            list.getNext();
        }
        assertEquals(new Integer(valueCount - 1), list.getTop());

        for (int i = 0; i < (valueCount) / 2; i++) {
            list.moveToMid();
        }
        assertEquals(new Integer(valueCount - 1), list.getTop());
    }

    /**
     * 
     */
    public void testRemoveTop() {
        CircularList list = new CircularList();
        int valueCount = 10;
        for (int i = 0; i < valueCount; i++) {
            list.add(new Integer(i));
        }

        assertEquals(new Integer(valueCount - 1), list.getTop());
        list.removeTop();
        assertEquals(new Integer(valueCount - 2), list.getTop());
        for (int i = list.size(); i > 0; i--) {
            assertEquals(new Integer(list.size() - 1), list.removeTop());
        }
        assertEquals(0, list.size());
        assertEquals(null, list.removeTop());
        assertEquals(null, list.getNext());
        assertEquals(null, list.getTop());

        list.moveToEnd();
        list.moveToMid();
    }

    /**
     * 
     */
    public void testRemove() {
        CircularList list = new CircularList();
        int valueCount = 10;
        for (int i = 0; i < valueCount; i++) {
            list.add(new Integer(i));
        }

        assertEquals(new Integer(valueCount - 1), list.getTop());
        list.remove(new Integer(valueCount - 1));
        assertEquals(new Integer(valueCount - 2), list.getTop());

        list.remove(new Integer(valueCount - 4));
        assertEquals(new Integer(valueCount - 2), list.getNext());
        assertEquals(new Integer(valueCount - 3), list.getNext());
        assertEquals(new Integer(valueCount - 5), list.getNext());

        assertEquals(valueCount - 2, list.size());
        assertEquals(new Integer(valueCount - 6), list.removeTop());
        assertEquals(new Integer(valueCount - 7), list.getTop());
        list.moveToEnd();
        list.moveToMid();
        while (list.size() > 0)
            list.remove(list.getTop());

        list.remove("nothing");
    }
}
