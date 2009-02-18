package pt.utl.ist.lucene.utils.dijkstra;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * An implementation of Dijkstra's shortest path algorithm. It computes the shortest path (in distance)
 * to all cities in the map. The output of the algorithm is the shortest distance from the start city 
 * to every other city, and the shortest path from the start city to every other.
 * <p>
 * Upon calling
 * {@link #execute(DijkstraNode, DijkstraNode)},
 * the results of the algorithm are made available by calling
 * {@link #getPredecessor(DijkstraNode)}
 * and 
 * {@link #getShortestDistance(DijkstraNode)}.
 * 
 * To get the shortest path between the city <var>destination</var> and
 * the source city after running the algorithm, one would do:
 * <pre>
 * ArrayList&lt;City&gt; l = new ArrayList&lt;City&gt;();
 *
 * for (City city = destination; city != null; city = engine.getPredecessor(city))
 * {
 *     l.add(city);
 * }
 *
 * Collections.reverse(l);
 *
 * return l;
 * </pre>
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: DijkstraEngine.java 2379 2007-08-23 19:06:29Z renaud $
 */

public class DijkstraEngine
{
    /**
     * Infinity value for distances.
     */
    public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

    /**
     * Some value to initialize the priority queue with.
     */
    private static final int INITIAL_CAPACITY = 8;
    
    /**
     * This comparator orders cities according to their shortest distances,
     * in ascending fashion. If two cities have the same shortest distance,
     * we compare the cities themselves.
     */
    private final Comparator<DijkstraNode> shortestDistanceComparator = new Comparator<DijkstraNode>()
        {
            public int compare(DijkstraNode left, DijkstraNode right)
            {
                // note that this trick doesn't work for huge distances, close to Integer.MAX_VALUE
                int result = getShortestDistance(left) - getShortestDistance(right);
                
                return (result == 0) ? left.compareTo(right) : result;
            }
        };
    
    /**
     * The graph.
     */
    private final RoutesMap map;
    
    /**
     * The working set of cities, kept ordered by shortest distance.
     */
    private final PriorityQueue<DijkstraNode> unsettledNodes = new PriorityQueue<DijkstraNode>(INITIAL_CAPACITY, shortestDistanceComparator);
    
    /**
     * The set of cities for which the shortest distance to the source
     * has been found.
     */
    private final Set<DijkstraNode> settledNodes = new HashSet<DijkstraNode>();
    
    /**
     * The currently known shortest distance for all cities.
     */
    private final Map<DijkstraNode, Integer> shortestDistances = new HashMap<DijkstraNode, Integer>();

    /**
     * Predecessors list: maps a city to its predecessor in the spanning tree of
     * shortest paths.
     */
    private final Map<DijkstraNode, DijkstraNode> predecessors = new HashMap<DijkstraNode, DijkstraNode>();
    
    /**
     * Constructor.
     */
    public DijkstraEngine(RoutesMap map)
    {
        this.map = map;
    }

    /**
     * Initialize all data structures used by the algorithm.
     * 
     * @param start the source node
     */
    private void init(DijkstraNode start)
    {
        settledNodes.clear();
        unsettledNodes.clear();
        
        shortestDistances.clear();
        predecessors.clear();
        
        // add source
        setShortestDistance(start, 0);
        unsettledNodes.add(start);
    }
    
    /**
     * Run Dijkstra's shortest path algorithm on the map.
     * The results of the algorithm are available through
     * {@link #getPredecessor(DijkstraNode)}
     * and 
     * {@link #getShortestDistance(DijkstraNode)}
     * upon completion of this method.
     * 
     * @param start the starting city
     * @param destination the destination city. If this argument is <code>null</code>, the algorithm is
     * run on the entire graph, instead of being stopped as soon as the destination is reached.
     */
    public void execute(DijkstraNode start, DijkstraNode destination)
    {
        init(start);
        
        // the current node
        DijkstraNode u;
        
        // extract the node with the shortest distance
        while ((u = unsettledNodes.poll()) != null)
        {
            assert !isSettled(u);
            
            // destination reached, stop
            if (u == destination) break;
            
            settledNodes.add(u);
            
            relaxNeighbors(u);
        }
    }

    /**
	 * Compute new shortest distance for neighboring nodes and update if a shorter
	 * distance is found.
	 * 
	 * @param u the node
	 */
    private void relaxNeighbors(DijkstraNode u)
    {
        for (DijkstraNode v : map.getDestinations(u))
        {
            // skip node already settled
            if (isSettled(v)) continue;
            
            int shortDist = getShortestDistance(u) + map.getDistance(u, v);
            
            if (shortDist < getShortestDistance(v))
            {
            	// assign new shortest distance and mark unsettled
                setShortestDistance(v, shortDist);
                                
                // assign predecessor in shortest path
                setPredecessor(v, u);
            }
        }        
    }

	/**
	 * Test a node.
	 * 
     * @param v the node to consider
     * 
     * @return whether the node is settled, ie. its shortest distance
     * has been found.
     */
    private boolean isSettled(DijkstraNode v)
    {
        return settledNodes.contains(v);
    }

    /**
     * @return the shortest distance from the source to the given city, or
     * {@link DijkstraEngine#INFINITE_DISTANCE} if there is no route to the destination.
     * @param node node to get Distance of
     */    
    public int getShortestDistance(DijkstraNode node)
    {
        Integer d = shortestDistances.get(node);
        return (d == null) ? INFINITE_DISTANCE : d;
    }

	/**
	 * Set the new shortest distance for the given node,
	 * and re-balance the queue according to new shortest distances.
	 * 
	 * @param node the node to set
	 * @param distance new shortest distance value
	 */        
    private void setShortestDistance(DijkstraNode node, int distance)
    {
        /*
         * This crucial step ensures no duplicates are created in the queue
         * when an existing unsettled node is updated with a new shortest 
         * distance.
         * 
         * Note: this operation takes linear time. If performance is a concern,
         * consider using a TreeSet instead instead of a PriorityQueue. 
         * TreeSet.remove() performs in logarithmic time, but the PriorityQueue
         * is simpler. (An earlier version of this class used a TreeSet.)
         */
        unsettledNodes.remove(node);

        /*
         * Update the shortest distance.
         */
        shortestDistances.put(node, distance);
        
		/*
		 * Re-balance the queue according to the new shortest distance found
		 * (see the comparator the queue was initialized with).
		 */
		unsettledNodes.add(node);
    }
    
    /**
     * @return the city leading to the given city on the shortest path, or
     * <code>null</code> if there is no route to the destination.
     */
    public DijkstraNode getPredecessor(DijkstraNode city)
    {
        return predecessors.get(city);
    }
    
    private void setPredecessor(DijkstraNode a, DijkstraNode b)
    {
        predecessors.put(a, b);
    }

}
