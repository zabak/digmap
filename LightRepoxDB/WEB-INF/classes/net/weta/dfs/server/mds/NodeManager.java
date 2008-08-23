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
 *  $Source: /cvsroot/weta/weta-dfs/src/java/net/weta/dfs/server/mds/NodeManager.java,v $
 */

package net.weta.dfs.server.mds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.weta.dfs.NodeMetaData;
import net.weta.dfs.util.CircularList;

/**
 * The NodeManager maintains all data nodes and the mapping from chunks to data
 * nodes in the dfs. <p/>
 * 
 * The order in which free data nodes are given back, depends upon their disc
 * space in first and then upon the given Comparators. Note that the minimum is
 * choosen. <p/>
 * 
 * The call of getFreeNode(long freespace) and getFreeNodes(long freespace) will
 * substract the given freespace from nodes freespace. The nodes freespace will
 * then actualized through addNodeToChunk() or if writing failed after a some
 * time through updateNode(). <p/>
 * 
 * <br/><br/>created on 12.04.2005
 * 
 * @version $Revision: 1.1 $
 * 
 */

public class NodeManager {

    /**
     * Note: this list contains to all objects in fNodes equal objects, but not
     * the same(for actual timestamp and freeDiscSpace refer fNodes)
     */
    private CircularList fOrderedNodes;

    private HashMap fNodes;

    private HashMap fChunksToNodes = new HashMap();

    private DiscSpaceMemory fSpaceMemory = new DiscSpaceMemory();

    private long fFreeDiscSpace;

    /**
     * Constructs a new instance of a NodeManager with initialCapacity of 10.
     */
    public NodeManager() {
        this(10);
    }

    /**
     * Constructs a new instance of a NodeManager with the given
     * initialCapacity.
     * 
     * @param initialCapacity
     */
    public NodeManager(int initialCapacity) {
        this.fOrderedNodes = new CircularList(initialCapacity);
        this.fNodes = new HashMap(initialCapacity);
    }

    /**
     * Call this method to add and update data nodes. Note: this method will not
     * set the last lifsign onf the node.
     * 
     * @param nodeMD
     * @return true if node is already known
     */
    public synchronized boolean updateNode(NodeMetaData nodeMD) {

        NodeMetaData oldNode = getNode(nodeMD.getId());
        if (oldNode == null) {
            this.fOrderedNodes.add(nodeMD);
            this.fFreeDiscSpace += nodeMD.getFreeDiskSpace();
        } else {
            if (this.fSpaceMemory.containsDiscSpace(nodeMD.getId()))
                nodeMD.setFreeDiscSpace(oldNode.getFreeDiskSpace());
            this.fFreeDiscSpace += nodeMD.getFreeDiskSpace()
                    - oldNode.getFreeDiskSpace();
        }
        this.fNodes.put(nodeMD.getId(), nodeMD);

        return oldNode != null;
    }

    /**
     * @param nodeId
     * @return the NodeMetaData with the given id or null if not exist
     */
    public NodeMetaData getNode(String nodeId) {
        return (NodeMetaData) this.fNodes.get(nodeId);
    }

    /**
     * @return how many nodes are maintained by NodeManager
     */
    public int getNodeCount() {
        return this.fNodes.size();
    }

    /**
     * @return the summed up discspace of all data nodes
     */
    public long getFreeDiscSpace() {
        return this.fFreeDiscSpace;
    }

    private long getAvarageFreeNodeDiscSpace() {
        return this.fFreeDiscSpace / getNodeCount();
    }

    /**
     * @return the memorized discspace which is the number between requested and
     *         written discspace
     */
    public synchronized long getMemorizedDiscSpace() {
        return this.fSpaceMemory.getMemorizedDiscSpace();

    }

    /**
     * Give back NodeMetaData which has the required amount of free disc space.
     * The given discspace will be substracted from the nodes free space.
     * 
     * @param freeDiscSpace
     * @return a datanode which has the amount of given freeDiscSpace or null if
     *         none exists
     */
    public NodeMetaData getFreeNode(long freeDiscSpace) {
        return getFreeNodeDistinctFrom(freeDiscSpace, null);
    }

    /**
     * Give back a NodeMetaData which has the required amount of free disc space
     * and is not contained by the given Set distinctNodes. The given discspace
     * will be substracted from the nodes free space.
     * 
     * @param distinctNodes
     * @param freeDiscSpace
     * @return a datanode which has the amount of given freeDiscSpace and is not
     *         contained by the given Set distinctNodes or null if none exists
     */
    public synchronized NodeMetaData getFreeNodeDistinctFrom(
            long freeDiscSpace, Set distinctNodes) {
        // get one node with sufficent space
        NodeMetaData nodeMD;
        int i = 0;
        do {
            nodeMD = (NodeMetaData) this.fOrderedNodes.getTop();
            if (nodeMD != null) {
                nodeMD = (NodeMetaData) this.fNodes.get(nodeMD.getId());
                if (nodeMD.getFreeDiskSpace() < freeDiscSpace
                        || (distinctNodes != null && distinctNodes
                                .contains(nodeMD))) {
                    nodeMD = null;
                    this.fOrderedNodes.moveToEnd();
                }
            }
            i++;
        } while (nodeMD == null && i < this.fNodes.size());

        // memorize the free disc space
        if (nodeMD != null) {
            nodeMD.setFreeDiscSpace(nodeMD.getFreeDiskSpace() - freeDiscSpace);
            this.fFreeDiscSpace -= freeDiscSpace;
            this.fSpaceMemory.memoDiscSpace(nodeMD.getId(), freeDiscSpace);
            this.fNodes.put(nodeMD.getId(), nodeMD);
            if (nodeMD.getFreeDiskSpace() > getAvarageFreeNodeDiscSpace())
                this.fOrderedNodes.moveToMid();
            else
                this.fOrderedNodes.moveToEnd();
        }
        return nodeMD;
    }

    /**
     * Give back the NodeMetaData's which has the required amount of free disc
     * space. The given discspace will be substracted from the nodes free space.
     * The NodeMetaData's does not contain one Node twice.
     * 
     * @param count
     * @param freeDiscSpace
     * @return NodeMetaData[count] if enough datanodes with the given
     *         freeDiscSpace exists otherwise NodeMetaData[ < count]
     */
    public NodeMetaData[] getFreeNodes(long freeDiscSpace, int count) {
        return getFreeNodesDistinctFrom(freeDiscSpace, count, null);
    }

    /**
     * Give back the NodeMetaData's which has the required amount of free disc
     * space and are not contained by the given Set distinctNodes. The given
     * discspace will be substracted from the nodes free space. The
     * NodeMetaData's does not contain one Node twice.
     * 
     * @param count
     * @param freeDiscSpace
     * @param distinctNodes
     * @return NodeMetaData[count] if enough datanodes with the given
     *         freeDiscSpace exists otherwise NodeMetaData[ < count]
     */
    public NodeMetaData[] getFreeNodesDistinctFrom(long freeDiscSpace,
            int count, Set distinctNodes) {
        if (count > getNodeCount())
            count = getNodeCount();

        Set nodeMDs = new HashSet(count);
        if (distinctNodes != null)
            nodeMDs.addAll(distinctNodes);

        for (int i = 0; i < count; i++) {
            NodeMetaData nodeMD = getFreeNodeDistinctFrom(freeDiscSpace,
                    nodeMDs);
            if (nodeMD == null)
                break;
            if (!nodeMDs.add(nodeMD))
                i--;
        }

        if (distinctNodes != null)
            nodeMDs.removeAll(distinctNodes);
        return (NodeMetaData[]) nodeMDs
                .toArray(new NodeMetaData[nodeMDs.size()]);
    }

    /**
     * Checks every memorized discspace if the time from its creation up to now
     * last longer as the given existens time and removes it if so.
     * 
     * @param existensTime ,
     *            maximum time a space memo should exist
     * @return the freed discspace
     */
    public synchronized long freeOutdatedDiscSpace(Long existensTime) {
        long freedSpace = this.fSpaceMemory.freeOutdatedDiscSpace(existensTime
                .longValue());
        this.fFreeDiscSpace += freedSpace;
        return freedSpace;
    }

    /**
     * @param timeWithoutLifeSign
     * @return how many nodes get removed
     */
    public synchronized NodeMetaData[] removeOutdatedNodes(
            Long timeWithoutLifeSign) {
        Set nodesToRemove = new HashSet();
        Collection nodes = this.fNodes.keySet();
        long now = System.currentTimeMillis();

        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            NodeMetaData nodeMD = (NodeMetaData) this.fNodes.get(iter.next());
            if (now - nodeMD.getLastLifeSign() > timeWithoutLifeSign
                    .longValue()) {
                nodesToRemove.add(nodeMD);
            }
        }

        for (Iterator iter = nodesToRemove.iterator(); iter.hasNext();) {
            NodeMetaData nodeMD = (NodeMetaData) iter.next();
            removeNode(nodeMD.getId());
        }

        return (NodeMetaData[]) nodesToRemove
                .toArray(new NodeMetaData[nodesToRemove.size()]);
    }

    /**
     * @param nodeId
     * @return if NodeManager contained node with the given nodeId
     */
    public synchronized boolean removeNode(String nodeId) {
        NodeMetaData nodeMD = null;

        nodeMD = (NodeMetaData) this.fNodes.remove(nodeId);
        if (nodeMD != null) {
            this.fOrderedNodes.remove(nodeMD);
            this.fSpaceMemory.releaseDiscSpace(nodeMD.getId());
            this.fFreeDiscSpace -= nodeMD.getFreeDiskSpace();
        }

        return nodeMD != null;
    }

    /**
     * Adds a node to a chunk. Note: not the real written chunkSize but the
     * files chunkSize is needed.
     * 
     * @param chunkId
     * @param chunkSize
     *            ,the maximum chunkSize
     * @param nodeMD
     * @return true if this node was not already contained by chunkToNode list
     */
    public synchronized boolean addNodeToChunk(String chunkId, long chunkSize,
            NodeMetaData nodeMD) {
        if (!this.fNodes.containsKey(nodeMD.getId()))
            updateNode(nodeMD);

        boolean success = false;
        Set nodesList = (Set) this.fChunksToNodes.get(chunkId);
        if (null == nodesList)
            nodesList = new HashSet();
        success = nodesList.add(nodeMD);
        this.fChunksToNodes.put(chunkId, nodesList);

        if (success)
            this.fSpaceMemory.releaseDiscSpace(nodeMD.getId(), chunkSize);

        return success;
    }

    /**
     * 
     * @param chunkId
     * @return an array of NodeMetaData
     */
    public synchronized NodeMetaData[] getChunkNodes(String chunkId) {
        NodeMetaData[] nodeMDs = null;

        Collection nodes = (Collection) this.fChunksToNodes.get(chunkId);
        if (nodes != null)
            nodeMDs = (NodeMetaData[]) nodes.toArray(new NodeMetaData[nodes
                    .size()]);

        return nodeMDs;
    }

    /**
     * @param chunkId
     */
    public void removeChunk(String chunkId) {
        this.fChunksToNodes.remove(chunkId);
    }

    /**
     * 
     * Just a little helper class for memorize the discspace of requested nodes.
     * 
     * <br/><br/>created on 28.04.2005
     * 
     * @version $Revision: 1.1 $
     * 
     */
    private class DiscSpaceMemory {

        private Map fDiscSpace = new HashMap();

        /**
         * @param nodeId
         * @param discSpace
         */
        public void memoDiscSpace(String nodeId, long discSpace) {
            if (discSpace <= 0) return;
            SpaceEntry entry = new SpaceEntry(System.currentTimeMillis(), discSpace);
            Collection entries = (Collection) this.fDiscSpace.get(nodeId);
            if (entries == null) entries = new ArrayList(3);
            entries.add(entry);
            this.fDiscSpace.put(nodeId, entries);
        }

        /**
         * @param nodeId
         * @return the saved discspace to this nodeId
         */
        public long getDiscSpace(String nodeId) {
            if (!this.fDiscSpace.containsKey(nodeId)) return 0;
            long result = 0;
            Collection entries = (Collection) this.fDiscSpace.get(nodeId);
            for (Iterator iter = entries.iterator(); iter.hasNext();) {
                SpaceEntry entry = (SpaceEntry) iter.next();
                result += entry.getSpace();
            }
            return result;
        }

        /**
         * 
         * @param nodeId
         * @return true if DiscSpaceMemory contains discspace for this nodeId
         */
        public boolean containsDiscSpace(String nodeId) {
            return this.fDiscSpace.containsKey(nodeId);
        }

        /**
         * @param nodeId
         * @param discSpace
         * @return true if no space is left
         */
        public boolean releaseDiscSpace(String nodeId, long discSpace) {
            Collection entries = (Collection) this.fDiscSpace.get(nodeId);
            if (entries == null)
                return true;

            for (Iterator iter = entries.iterator(); iter.hasNext();) {
                SpaceEntry entry = (SpaceEntry) iter.next();
                if (discSpace == entry.getSpace()) {
                    iter.remove();
                    break;
                }
            }

            if (entries.isEmpty())
                this.fDiscSpace.remove(nodeId);
            return entries.isEmpty();
        }

        /**
         * Releases all discspace no matter how much it is.
         * 
         * @param nodeId
         * @return the amount of released discspace
         */
        public long releaseDiscSpace(String nodeId) {
            if (!this.fDiscSpace.containsKey(nodeId))
                return 0;

            long releasedSpace = this.getDiscSpace(nodeId);
            this.fDiscSpace.remove(nodeId);
            return releasedSpace;
        }

        /**
         * Checks every memorized discspace if the time from its creation up to
         * now last longer as the given existens time and removes it if so.
         * 
         * @param existensTime ,
         *            maximum time a space memo should exist
         * @return the freed discspace
         */
        public synchronized long freeOutdatedDiscSpace(long existensTime) {
            Collection entriesCollections = this.fDiscSpace.values();
            long now = System.currentTimeMillis();
            long freedSpace = 0;
            for (Iterator iter = entriesCollections.iterator(); iter.hasNext();) {
                Collection spaceEntries = (Collection) iter.next();
                for (Iterator iterator = spaceEntries.iterator(); iterator
                        .hasNext();) {
                    SpaceEntry entry = (SpaceEntry) iterator.next();
                    if (now - entry.getTimestamp() > existensTime) {
                        freedSpace += entry.getSpace();
                        iterator.remove();
                    }
                }
                if (spaceEntries.isEmpty())
                    iter.remove();
            }

            return freedSpace;
        }

        /**
         * @return the memorized discspace which is the number between requested
         *         and written discspace
         */
        public long getMemorizedDiscSpace() {
            long result = 0;
            Collection keys = this.fDiscSpace.keySet();
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                String nodeId = (String) iter.next();
                result += getDiscSpace(nodeId);
            }
            return result;
        }

        private class SpaceEntry {

            private long timestamp;

            private long space;

            /**
             * @param timestamp
             * @param space
             */
            public SpaceEntry(long timestamp, long space) {
                this.timestamp = timestamp;
                this.space = space;
            }

            /**
             * @return Returns the timestamp.
             */
            public long getTimestamp() {
                return this.timestamp;
            }

            /**
             * @return Returns the space.
             */
            public long getSpace() {
                return this.space;
            }
        }
    }
}
