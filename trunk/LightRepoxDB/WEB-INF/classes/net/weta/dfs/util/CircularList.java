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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/util/CircularList.java,v $
 */

package net.weta.dfs.util;

import java.util.ArrayList;

/**
 * CircularList where added elements are always on top. <br/>You can cycle
 * through the list with getNext(), whereat the returned object is moved to the
 * end of list. <br/>Also you could get the top of the list through getTop() and
 * then move it to end with moveToEnd() or to the middle of this list with
 * moveToMid().
 * 
 * Note: This implementation is not synchronized.
 * 
 * <br/><br/>created on 28.04.2005
 * 
 * @version $Revision: 1.2 $
 * 
 */
public class CircularList {

    private ArrayList fElements;

    private int fActPos = 0;

    /**
     * Constructs an empty list with the given initial capacity.
     * 
     * @param initialCapacity
     */
    public CircularList(int initialCapacity) {
        this.fElements = new ArrayList(initialCapacity);
    }

    /**
     * Constructs an empty list with initial capacity of 10.
     */
    public CircularList() {
        this(10);
    }

    /**
     * Adds the element at top of this list
     * 
     * @param element
     */
    public void add(Object element) {
        this.fElements.add(this.fActPos, element);
    }

    /**
     * @return the top of this list
     */
    public Object getTop() {
        if (size() == 0)
            return null;

        return this.fElements.get(this.fActPos);
    }

    /**
     * Returns the top of this list and moves it to end.
     * 
     * @return the top of this list
     */
    public Object getNext() {
        if (size() == 0)
            return null;

        Object result = this.fElements.get(this.fActPos);
        incrementPos();
        return result;
    }

    /**
     * Moves the top of this list to end.
     */
    public void moveToEnd() {
        incrementPos();
    }

    /**
     * Moves the top of this list to the middle.
     */
    public void moveToMid() {
        if (size() == 0)
            return;

        Object element = this.fElements.remove(this.fActPos);
        int midPos = size() / 2 + this.fActPos;
        if (midPos > size())
            midPos -= size();
        this.fElements.add(midPos, element);
    }

    /**
     * @return the top of this list
     */
    public Object removeTop() {
        if (size() == 0)
            return null;

        Object top = this.fElements.remove(this.fActPos);
        if (this.fActPos == size())
            this.fActPos = 0;
        return top;
    }

    /**
     * @param element
     * @return true if list contained this element
     */
    public boolean remove(Object element) {
        boolean contained = this.fElements.remove(element);
        if (this.fActPos == size())
            this.fActPos = 0;
        return contained;
    }

    /**
     * @return the number of elements in this list
     * 
     */
    public int size() {
        return this.fElements.size();
    }

    private void incrementPos() {
        this.fActPos++;
        if (this.fActPos == this.fElements.size())
            this.fActPos = 0;
    }
}
