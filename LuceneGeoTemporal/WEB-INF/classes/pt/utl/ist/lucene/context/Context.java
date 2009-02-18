package pt.utl.ist.lucene.context;

import pt.utl.ist.lucene.utils.dijkstra.DenseRoutesMap;
import pt.utl.ist.lucene.utils.dijkstra.DijkstraEngine;
import pt.utl.ist.lucene.utils.dijkstra.RoutesMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge
 * @date 15/Fev/2009
 * @time 20:43:37
 */
public class Context
{
    int indexCounter = 0;
    List<ContextNode> nodes = new ArrayList<ContextNode>();

    ContextNode topNode;
    RoutesMap routesMap;
    String idField;

    public Context(String idField)
    {
        this.idField = idField;
    }

    public ContextNode getTopNode()
    {
        return topNode;
    }

    protected ContextNode getNode(int index)
    {
        if(nodes != null)
            for(ContextNode node: nodes)
            {
                if(node.index == index)
                    return node;
            }
        return null;
    }

    public ContextNode createTopNode(String docId)
    {
        topNode = new ContextNode(this,docId,indexCounter);
        nodes.add(topNode);
        indexCounter++;
        return topNode;
    }

    public ContextNode createInternalNode(String docId)
    {
        ContextNode node = new ContextNode(this,docId,indexCounter);
        indexCounter++;
        nodes.add(node);
        return node;
    }

    public void addOutLink(ContextNode startNode, ContextNode endNode)
    {
        startNode.addOutLink(endNode);
    }

    public void addBiDirectionalLink(ContextNode startNode, ContextNode endNode)
    {
        startNode.addOutLink(endNode);
        startNode.addInLink(endNode);
    }


    public List<ContextNode> getNodes()
    {
        return nodes;
    }

    public void generateDistances()
    {
        routesMap = new DenseRoutesMap(nodes.size());
        for(ContextNode node: nodes)
        {
            if(node.getOutLinks() != null)
            {
                for(ContextNode nodeOut: node.getOutLinks())
                {
                    routesMap.addDirectRoute(node,nodeOut,1);
                }
            }
        }
        DijkstraEngine engine = new DijkstraEngine(routesMap);
        for(ContextNode node: nodes)
        {
            node.initDistances(engine,routesMap,nodes);
        }
    }
}
