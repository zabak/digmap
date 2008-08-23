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
 *  $Source: /cvsroot/weta/weta-dfs/src/test/net/weta/dfs/server/mds/NodeManagerTest.java,v $
 */

package net.weta.dfs.server.mds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import net.weta.dfs.NodeMetaData;
import net.weta.dfs.server.mds.NodeManager;
import net.weta.dfs.util.MethodInvokerThread;

/**
 * NodeManagerTest
 * 
 * <br/><br/>created on 12.04.2005
 * 
 * @version $Revision: 1.4 $
 * 
 */
public class NodeManagerTest extends TestCase {

    private NodeManager fNodeManager;

    protected void setUp() throws Exception {
        super.setUp();
        this.fNodeManager = new NodeManager();
    }

    /**
     * 
     */
    public void testConstructor() {
        assertEquals(0, this.fNodeManager.getFreeDiscSpace());
        assertEquals(0, this.fNodeManager.getMemorizedDiscSpace());
        assertEquals(0, this.fNodeManager.getNodeCount());
        assertNull(this.fNodeManager.getFreeNode(23));
        assertNull(this.fNodeManager.getNode("hu"));
        assertNull(this.fNodeManager.getChunkNodes("hu"));
        assertEquals(0, this.fNodeManager.freeOutdatedDiscSpace(new Long(23)));
        assertEquals(0,
                this.fNodeManager.removeOutdatedNodes(new Long(23)).length);
    }

    /**
     * 
     */
    public void testUpdateNode() {
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 2300000);

        // add node1
        assertFalse(this.fNodeManager.updateNode(nodeMD1));
        assertEquals(nodeMD1, this.fNodeManager.getNode(nodeMD1.getId()));
        assertEquals(nodeMD1.getFreeDiskSpace(), this.fNodeManager
                .getFreeDiscSpace());
        assertEquals(1, this.fNodeManager.getNodeCount());
        nodeMD1 = new NodeMetaData("host1", 22, 2100000);

        // change free space
        assertTrue(this.fNodeManager.updateNode(nodeMD1));
        assertEquals(nodeMD1, this.fNodeManager.getNode(nodeMD1.getId()));
        assertEquals(nodeMD1.getFreeDiskSpace(), this.fNodeManager
                .getFreeDiscSpace());
        assertEquals(1, this.fNodeManager.getNodeCount());

        // add node2
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 23, 2300000);
        assertFalse(this.fNodeManager.updateNode(nodeMD2));
        assertEquals(nodeMD2, this.fNodeManager.getNode(nodeMD2.getId()));
        assertEquals(nodeMD1.getFreeDiskSpace() + nodeMD2.getFreeDiskSpace(),
                this.fNodeManager.getFreeDiscSpace());
        assertEquals(2, this.fNodeManager.getNodeCount());
        assertTrue(this.fNodeManager.updateNode(nodeMD2));
        assertEquals(nodeMD2, this.fNodeManager.getNode(nodeMD2.getId()));
    }

    /**
     * 
     */
    public void testLifeSigns() {
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        nodeMD1.setLastLiveSign(23);
        this.fNodeManager.updateNode(nodeMD1);
        assertEquals(23, this.fNodeManager.getNode(nodeMD1.getId())
                .getLastLifeSign());

        nodeMD1 = new NodeMetaData("host1", 22, 1);
        nodeMD1.setLastLiveSign(24);
        this.fNodeManager.updateNode(nodeMD1);
        assertEquals(24, this.fNodeManager.getNode(nodeMD1.getId())
                .getLastLifeSign());

        this.fNodeManager.getFreeNode(1);
        assertEquals(24, this.fNodeManager.getNode(nodeMD1.getId())
                .getLastLifeSign());
    }

    /**
     * 
     */
    public void testGetFreeNode() {
        // fill node manager
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        nodeMD1.setLastLiveSign(23);
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 22, 2);
        NodeMetaData nodeMD3 = new NodeMetaData("host3", 22, 0);
        this.fNodeManager.updateNode(nodeMD1);
        this.fNodeManager.updateNode(nodeMD2);
        this.fNodeManager.updateNode(nodeMD3);

        assertEquals(3, this.fNodeManager.getNodeCount());
        assertEquals(3, this.fNodeManager.getFreeDiscSpace());

        assertNull(this.fNodeManager.getFreeNode(3));
        assertEquals(nodeMD3, this.fNodeManager.getFreeNode(0));
        assertEquals(3, this.fNodeManager.getFreeDiscSpace());
        assertEquals(nodeMD2, this.fNodeManager.getFreeNode(2));
        assertEquals(1, this.fNodeManager.getFreeDiscSpace());

        assertEquals(nodeMD1, this.fNodeManager.getFreeNode(1));
        assertEquals(0, this.fNodeManager.getFreeDiscSpace());
        assertNull(this.fNodeManager.getFreeNode(1));
        assertEquals(3, this.fNodeManager.getNodeCount());
    }

    /**
     * 
     */
    public void testGetNode_sDistinctFrom() {

        Set nodes = new HashSet();
        for (int i = 0; i < 100; i++) {
            NodeMetaData nodeMD = new NodeMetaData("host" + i, 22, 10);
            nodes.add(nodeMD);
            this.fNodeManager.updateNode(nodeMD);
        }
        assertNull(this.fNodeManager.getFreeNodeDistinctFrom(0, nodes));
        assertEquals(0, this.fNodeManager.getFreeNodesDistinctFrom(0, nodes
                .size(), nodes).length);

        Iterator iterator = nodes.iterator();
        iterator.next();
        iterator.remove();
        assertNotNull(this.fNodeManager.getFreeNodeDistinctFrom(0, nodes));
        assertEquals(1, this.fNodeManager.getFreeNodesDistinctFrom(0, nodes
                .size(), nodes).length);

        iterator.next();
        iterator.remove();
        assertNotNull(this.fNodeManager.getFreeNodeDistinctFrom(0, nodes));
        assertEquals(2, this.fNodeManager.getFreeNodesDistinctFrom(0, nodes
                .size(), nodes).length);
    }

    /**
     * @throws InterruptedException
     * 
     */
    public void testMemoDiscSpace() throws InterruptedException {
        NodeMetaData nodeMD = new NodeMetaData("host2", 22, 8);
        this.fNodeManager.updateNode(nodeMD);
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(2));

        // change free disc spaces
        // (while we get nodeMD2 through getFreeNode()
        // call, and have no feedback if the request space is used or not,
        // increasing amount of free discspace of that node is not able
        nodeMD = new NodeMetaData(nodeMD.getHostAddress(), nodeMD.getPort(), 9);
        assertTrue(this.fNodeManager.updateNode(nodeMD));
        assertEquals(6, this.fNodeManager.getFreeDiscSpace());
        assertNull(this.fNodeManager.getFreeNode(8));

        // request 2times again
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(2));
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(2));
        assertEquals(2, this.fNodeManager.getFreeDiscSpace());

        // calling addNodeToChunk() with chunkSize was not requested will not
        // release any of the memorized disc space
        long memoedDiscSpace = this.fNodeManager.getMemorizedDiscSpace();
        assertTrue(memoedDiscSpace > 0);
        this.fNodeManager.addNodeToChunk("chunkId0", 6, nodeMD);
        assertEquals(memoedDiscSpace, this.fNodeManager.getMemorizedDiscSpace());
        assertEquals(2, this.fNodeManager.getFreeDiscSpace());

        // after adding all chunk and so releasing every memorized space,
        // increasing space will be able
        this.fNodeManager.addNodeToChunk("chunkId1", 2, nodeMD);
        nodeMD = new NodeMetaData(nodeMD.getHostAddress(), nodeMD.getPort(), 9);
        assertTrue(this.fNodeManager.updateNode(nodeMD));
        assertEquals(2, this.fNodeManager.getFreeDiscSpace());
        this.fNodeManager.addNodeToChunk("chunkId2", 2, nodeMD);
        this.fNodeManager.addNodeToChunk("chunkId3", 2, nodeMD);

        nodeMD = new NodeMetaData(nodeMD.getHostAddress(), nodeMD.getPort(), 9);
        assertTrue(this.fNodeManager.updateNode(nodeMD));
        assertEquals(9, this.fNodeManager.getFreeDiscSpace());

        // free outdated nodes
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(2));
        assertEquals(2, this.fNodeManager.getMemorizedDiscSpace());
        assertEquals(7, this.fNodeManager.getFreeDiscSpace());
        Thread.sleep(200);
        assertEquals(2, this.fNodeManager.freeOutdatedDiscSpace(new Long(1)));
        assertEquals(0, this.fNodeManager.getMemorizedDiscSpace());
        assertEquals(9, this.fNodeManager.getFreeDiscSpace());
    }

    /**
     * 
     */
    public void testSustainedNodeDistribution() {
        // fill node manager
        NodeMetaData[] nodeMDs = new NodeMetaData[1000];
        long nodesDiscSize = 10000;
        for (int i = 0; i < nodeMDs.length; i++) {
            nodeMDs[i] = new NodeMetaData("host" + i, 22, nodesDiscSize);
            this.fNodeManager.updateNode(nodeMDs[i]);
        }
        assertEquals(nodeMDs.length * nodesDiscSize, this.fNodeManager
                .getFreeDiscSpace());

        // check sustained distribution of nodes
        for (int i = 0; i < nodeMDs.length; i++) {
            assertEquals(nodeMDs[nodeMDs.length - 1 - i], this.fNodeManager
                    .getFreeNode(nodesDiscSize / 2));
        }
        assertEquals((nodeMDs.length * nodesDiscSize) / 2, this.fNodeManager
                .getFreeDiscSpace());

        // add node with greater disc size
        NodeMetaData nodeMD = new NodeMetaData("newHost", 22, nodesDiscSize * 2);
        this.fNodeManager.updateNode(nodeMD);
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(nodeMD
                .getFreeDiskSpace() / 2));

        // check that node move to mid
        for (int i = 0; i < nodeMDs.length / 2; i++) {
            this.fNodeManager.getFreeNode(nodesDiscSize / 2);
        }
        assertEquals(nodeMD, this.fNodeManager.getFreeNode(nodeMD
                .getFreeDiskSpace() / 2));
    }

    /**
     * 
     */
    public void testGetFreeNodes() {
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 22, 2);
        NodeMetaData nodeMD3 = new NodeMetaData("host3", 22, 3);
        NodeMetaData nodeMD4 = new NodeMetaData("host4", 22, 4);
        NodeMetaData nodeMD5 = new NodeMetaData("host5", 22, 5);

        this.fNodeManager.updateNode(nodeMD1);
        this.fNodeManager.updateNode(nodeMD2);
        this.fNodeManager.updateNode(nodeMD3);
        this.fNodeManager.updateNode(nodeMD4);
        this.fNodeManager.updateNode(nodeMD5);

        assertEquals(5, this.fNodeManager.getNodeCount());
        assertEquals(15, this.fNodeManager.getFreeDiscSpace());
        assertEquals(0, this.fNodeManager.getFreeNodes(6, 6).length);
        assertEquals(1, this.fNodeManager.getFreeNodes(5, 6).length);
        assertEquals(2, this.fNodeManager.getFreeNodes(3, 6).length);
        assertEquals(3, this.fNodeManager.getFreeNodes(1, 6).length);
        assertEquals(5, this.fNodeManager.getFreeNodes(0, 6).length);
        assertEquals(5, this.fNodeManager.getFreeNodes(0, 5).length);

        Collection collection = Arrays.asList(this.fNodeManager.getFreeNodes(0,
                5));
        assertTrue(collection.contains(nodeMD1));
        assertTrue(collection.contains(nodeMD2));
        assertTrue(collection.contains(nodeMD3));
        assertTrue(collection.contains(nodeMD4));
        assertTrue(collection.contains(nodeMD5));
    }

    /**
     * 
     */
    public void testRemoveNode() {
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 22, 2);
        this.fNodeManager.updateNode(nodeMD1);
        this.fNodeManager.updateNode(nodeMD2);
        this.fNodeManager.getFreeNode(1);

        assertEquals(2, this.fNodeManager.getNodeCount());
        assertEquals(2, this.fNodeManager.getFreeDiscSpace());
        assertEquals(1, this.fNodeManager.getMemorizedDiscSpace());

        assertFalse(this.fNodeManager.removeNode("hu"));
        assertTrue(this.fNodeManager.removeNode(nodeMD2.getId()));
        assertEquals(1, this.fNodeManager.getNodeCount());
        assertEquals(1, this.fNodeManager.getFreeDiscSpace());
        assertEquals(0, this.fNodeManager.getMemorizedDiscSpace());
    }

    /**
     * @throws InterruptedException
     */
    public void testRemoveOutdatedNodes() throws InterruptedException {
        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 22, 2);
        nodeMD1.setLastLiveSign(System.currentTimeMillis() + 20000);
        nodeMD2.setLastLiveSign(System.currentTimeMillis());
        this.fNodeManager.updateNode(nodeMD1);
        this.fNodeManager.updateNode(nodeMD2);
        this.fNodeManager.getFreeNode(1);

        Thread.sleep(250);
        assertEquals(2, this.fNodeManager.getNodeCount());
        assertEquals(1,
                this.fNodeManager.removeOutdatedNodes(new Long(1)).length);
        assertEquals(1, this.fNodeManager.getNodeCount());
        assertEquals(1, this.fNodeManager.getFreeDiscSpace());
        assertEquals(0, this.fNodeManager.getMemorizedDiscSpace());
    }

    /**
     * 
     */
    public void testAddNodeToChunk() {
        String chunkId1 = "chunk1";
        String chunkId2 = "chunk2";

        NodeMetaData nodeMD1 = new NodeMetaData("host1", 22, 1);
        NodeMetaData nodeMD2 = new NodeMetaData("host2", 22, 2);
        NodeMetaData nodeMD3 = new NodeMetaData("host3", 22, 3);
        NodeMetaData nodeMD4 = new NodeMetaData("host4", 22, 4);
        NodeMetaData nodeMD5 = new NodeMetaData("host5", 22, 5);

        assertNull(this.fNodeManager.getChunkNodes(chunkId1));

        assertTrue(this.fNodeManager.addNodeToChunk(chunkId1, 0, nodeMD1));
        assertTrue(this.fNodeManager.addNodeToChunk(chunkId1, 0, nodeMD2));
        assertTrue(this.fNodeManager.addNodeToChunk(chunkId1, 0, nodeMD3));
        assertTrue(this.fNodeManager.addNodeToChunk(chunkId2, 0, nodeMD4));
        assertTrue(this.fNodeManager.addNodeToChunk(chunkId2, 0, nodeMD5));
        assertFalse(this.fNodeManager.addNodeToChunk(chunkId2, 0, nodeMD5));

        assertEquals(3, this.fNodeManager.getChunkNodes(chunkId1).length);
        assertEquals(2, this.fNodeManager.getChunkNodes(chunkId2).length);

        Collection collection = Arrays.asList(this.fNodeManager
                .getChunkNodes(chunkId1));
        assertTrue(collection.contains(nodeMD1));
        assertTrue(collection.contains(nodeMD2));
        assertTrue(collection.contains(nodeMD3));

        this.fNodeManager.removeChunk(chunkId1);
        assertNull(this.fNodeManager.getChunkNodes(chunkId1));
        assertEquals(2, this.fNodeManager.getChunkNodes(chunkId2).length);
        this.fNodeManager.removeChunk(chunkId2);
        assertNull(this.fNodeManager.getChunkNodes(chunkId2));
    }

    /**
     * @throws InterruptedException
     */
    public void testAll() throws InterruptedException {
        ArrayList nodesList = new ArrayList();

        long overallSpace = 0;
        Random random = new Random();

        // fill node manager
        for (int i = 0; i < 100; i++) {
            long nodesFreeSpace = random.nextInt(Integer.MAX_VALUE);
            NodeMetaData nodeMD = new NodeMetaData("host" + i, i,
                    nodesFreeSpace);
            nodesList.add(nodeMD);
            this.fNodeManager.updateNode(nodeMD);
            overallSpace += nodesFreeSpace;
        }
        assertEquals(nodesList.size(), this.fNodeManager.getNodeCount());
        assertEquals(overallSpace, this.fNodeManager.getFreeDiscSpace());

        // get some nodes and removin discspace
        long memorizedSpace = 0;
        HashMap nodesToRequestedSpace = new HashMap();
        for (int i = 0; i < nodesList.size(); i++) {
            if (random.nextBoolean()) {
                NodeMetaData node = this.fNodeManager.getFreeNode(0);
                long requestedDiscSpace = random.nextInt((int) node
                        .getFreeDiskSpace());
                memorizedSpace += requestedDiscSpace;

                node = this.fNodeManager.getFreeNode(requestedDiscSpace);
                assertNotNull(node);
                nodesToRequestedSpace.put(node.getId(), new Long(
                        requestedDiscSpace));
            }
        }
        assertEquals(memorizedSpace, this.fNodeManager.getMemorizedDiscSpace());

        // release some discspace (with writting chunks)
        long writtenSpace = 0;
        for (int i = 0; i < nodesList.size(); i++) {
            if (random.nextBoolean()) {
                NodeMetaData node = (NodeMetaData) nodesList.get(i);
                Long reqSpace = (Long) nodesToRequestedSpace.get(node.getId());
                if (reqSpace != null) {
                    this.fNodeManager.addNodeToChunk("chunk" + i, reqSpace
                            .longValue(), node);
                    writtenSpace += reqSpace.longValue();
                }
            }
        }
        assertTrue(writtenSpace > 0);
        assertEquals(memorizedSpace - writtenSpace, this.fNodeManager
                .getMemorizedDiscSpace());
        assertEquals(overallSpace, this.fNodeManager.getFreeDiscSpace()
                + this.fNodeManager.getMemorizedDiscSpace() + writtenSpace);

        // release outdated rest of space
        Thread.sleep(200);
        this.fNodeManager.freeOutdatedDiscSpace(new Long(1));
        assertEquals(0, this.fNodeManager.getMemorizedDiscSpace());
        assertEquals(overallSpace, this.fNodeManager.getFreeDiscSpace()
                + writtenSpace);
    }

    /**
     * Uncomment this test if you change some synchronisation stuff of
     * NodeManager.
     * 
     * It's commented because adjusting the test to run on every (slow) computer
     * will gain more effort then earnings in moment.
     * 
     * If this method will hang, it points to deadlocks, to be sure start this
     * method through the main() method of this test class from console and use
     * the java deadlock cognition. <br/>
     * 
     * press therefore
     * 
     * 'Ctrl-Break' windows
     * 
     * 'Ctrl+\' linux/solaris
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     */
    public void testSynchronization() throws Exception {
        System.out.println("test commented");
//        NodeMetaData[] nodeMds = new NodeMetaData[100];
//        Random random = new Random();
//        long nodesFreeSpace = 10000;
//        for (int i = 0; i < nodeMds.length; i++) {
//            nodeMds[i] = new NodeMetaData("host" + i, 22, nodesFreeSpace);
//            nodeMds[i].setLastLiveSign(System.currentTimeMillis());
//        }
//
//        // start freeOutdatedDiscSpace() thread
//        MethodInvokerThread freeDiscSpaceThread = new MethodInvokerThread(
//                this.fNodeManager, "freeOutdatedDiscSpace", 250,
//                new Class[] { Long.class }, new Object[] { new Long(2300) });
//        freeDiscSpaceThread.setName("freeDiscSpaceT");
//        freeDiscSpaceThread.start();
//
//        // start removeOutdatedNodes() thread
//        MethodInvokerThread outdatedNodesThread = new MethodInvokerThread(
//                this.fNodeManager, "removeOutdatedNodes", 250,
//                new Class[] { Long.class }, new Object[] { new Long(2300) });
//        outdatedNodesThread.setName("removeOutdatedNodesT");
//        outdatedNodesThread.start();
//
//        // start updateNode() threads
//        MethodInvokerThread[] updateThreads = new MethodInvokerThread[nodeMds.length];
//        for (int i = 0; i < updateThreads.length; i++) {
//            updateThreads[i] = new MethodInvokerThread(this.fNodeManager,
//                    "updateNode", random.nextInt(50),
//                    new Class[] { NodeMetaData.class },
//                    new Object[] { nodeMds[i] });
//            updateThreads[i].setName("updateT:" + nodeMds[i].getId());
//            updateThreads[i].start();
//        }
//        Thread.sleep(75);
//        assertTrue(this.fNodeManager.getNodeCount() > 0);
//
//        // start getFreeNode() threads
//        MethodInvokerThread[] getNodesThreads = new MethodInvokerThread[nodeMds.length];
//        for (int i = 0; i < updateThreads.length; i++) {
//            getNodesThreads[i] = new MethodInvokerThread(this,
//                    "syncGetFreeNode", random.nextInt(100),
//                    new Class[] { Long.class }, new Object[] { new Long(50) });
//            getNodesThreads[i].setName("getFreeNodeT:" + nodeMds[i].getId());
//            getNodesThreads[i].start();
//        }
//        Thread.sleep(75);
//
//        // start addNodeToChunks() threads
//        String chunkPrefix = "chunk";
//        MethodInvokerThread[] addChunksThreads = new MethodInvokerThread[nodeMds.length];
//        for (int i = 0; i < addChunksThreads.length; i++) {
//            addChunksThreads[i] = new MethodInvokerThread(this,
//                    "syncaddNodeToChunk", random.nextInt(100), new Class[] {
//                            String.class, Long.class, NodeMetaData.class },
//                    new Object[] { chunkPrefix + i, new Long(50), nodeMds[i] });
//            addChunksThreads[i].setName("addChunksT:" + nodeMds[i].getId());
//            addChunksThreads[i].start();
//        }
//        Thread.sleep(75);
//
//        // start removeChunk() threads
//        MethodInvokerThread[] removeChunksThreads = new MethodInvokerThread[nodeMds.length];
//        for (int i = 0; i < addChunksThreads.length; i++) {
//            removeChunksThreads[i] = new MethodInvokerThread(this.fNodeManager,
//                    "removeChunk", random.nextInt(100),
//                    new Class[] { String.class }, new Object[] { chunkPrefix
//                            + i });
//            removeChunksThreads[i].setName("removeChunksT:"
//                    + nodeMds[i].getId());
//            removeChunksThreads[i].start();
//        }
//
//        // call all methods threads don't call
//        Thread.sleep(75);
//        for (int i = 0; i < addChunksThreads.length; i++) {
//            this.fNodeManager.getChunkNodes(chunkPrefix + i);
//            this.fNodeManager.getNode(nodeMds[i].getId());
//        }
//        this.fNodeManager.getFreeNodes(23, 5);
//        this.fNodeManager.getFreeDiscSpace();
//        this.fNodeManager.getMemorizedDiscSpace();
//        this.fNodeManager.getNodeCount();
//        this.fNodeManager.removeNode(nodeMds[0].getId());
//
//        // let the threads play
//        Thread.sleep(2000);
//
//        // shutdown threads
//        for (int i = 0; i < updateThreads.length; i++) {
//            assertTrue(updateThreads[i].isAlive());
//            assertTrue(getNodesThreads[i].isAlive());
//            assertTrue(addChunksThreads[i].isAlive());
//            assertTrue(removeChunksThreads[i].isAlive());
//
//            updateThreads[i].stopSmooth();
//            getNodesThreads[i].stopSmooth();
//            addChunksThreads[i].stopSmooth();
//            removeChunksThreads[i].stopSmooth();
//        }
//        Thread.sleep(1000);
//        for (int i = 0; i < updateThreads.length; i++) {
//            // if this goes wrong then there is probably a deadlock
//            assertFalse(updateThreads[i].isAlive());
//            assertFalse(getNodesThreads[i].isAlive());
//            assertFalse(addChunksThreads[i].isAlive());
//            assertFalse(removeChunksThreads[i].isAlive());
//        }
//        assertTrue(freeDiscSpaceThread.isAlive());
//        assertTrue(outdatedNodesThread.isAlive());
//        freeDiscSpaceThread.stopSmooth();
//        outdatedNodesThread.stopSmooth();
    }

    /**
     * Helper method for testSynchronizeThreads to wrap primitives.
     * 
     * @param freeSpace
     */
    public void syncGetFreeNode(Long freeSpace) {
        this.fNodeManager.getFreeNode(freeSpace.longValue());
    }

    /**
     * Helper method for testSynchronizeThreads to wrap primitives
     * 
     * @param chunkId
     * @param chunkSize
     * @param nodeMD
     */
    public void syncaddNodeToChunk(String chunkId, Long chunkSize,
            NodeMetaData nodeMD) {
        this.fNodeManager
                .addNodeToChunk(chunkId, chunkSize.longValue(), nodeMD);
    }

    /**
     * Starts synchronisation test for using VM deadlock cognition.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        NodeManagerTest managerTest = new NodeManagerTest();
        managerTest.setUp();
        managerTest.testSynchronization();
    }
}
