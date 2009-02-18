package pt.utl.ist.lucene.context;

import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.dijkstra.DijkstraNode;
import pt.utl.ist.lucene.utils.dijkstra.DijkstraEngine;
import pt.utl.ist.lucene.utils.dijkstra.RoutesMap;

import java.util.*;

/**
 * @author Jorge
 * @date 15/Fev/2009
 * @time 20:45:23
 */
public class ContextNode implements DijkstraNode
{
    Context context;
    int index;
    List<ContextNode> outLinks;
    List<ContextNode> inLinks;
    String docId;
    Map<ContextNode,Integer> distanceVector = new HashMap<ContextNode,Integer>();


    public ContextNode(Context context, String docId, int index)
    {
        this.context = context;
        this.docId = docId;
        this.index = index;
    }


    public String getDocId()
    {
        return docId;
    }

    public List<ContextNode> getOutLinks()
    {
        return outLinks;
    }

    public List<ContextNode> getInLinks()
    {
        return inLinks;
    }

    protected void addOutLink(ContextNode contextNode)
    {
        if(outLinks == null)
            outLinks = new ArrayList<ContextNode>();

        if(!outLinks.contains(contextNode))
            outLinks.add(contextNode);

        if(contextNode.inLinks == null)
            contextNode.inLinks = new ArrayList<ContextNode>();

        if(!contextNode.inLinks.contains(this))
            contextNode.inLinks.add(this);
    }

    protected void addInLink(ContextNode contextNode)
    {
        if(inLinks == null)
            inLinks = new ArrayList<ContextNode>();

        if(!inLinks.contains(contextNode))
            inLinks.add(contextNode);

        if(contextNode.outLinks == null)
            contextNode.outLinks = new ArrayList<ContextNode>();

        if(!contextNode.outLinks.contains(this))
            contextNode.outLinks.add(this);
    }

    protected void addBiDirectionalLink(ContextNode contextNode)
    {
        addOutLink(contextNode);
        addInLink(contextNode);
    }


    public DijkstraNode getNode(int index)
    {
        return context.getNode(index);
    }/*
    * Package members only.
    */

    public int getIndex()
    {
        return index;
    }

    public int compareTo(DijkstraNode o)
    {
        return getIndex() - o.getIndex();
    }

    protected void initDistances(DijkstraEngine engine, RoutesMap routesMap, List<ContextNode> nodes)
    {
        for(ContextNode otherNode: nodes)
        {
            if(otherNode != this)
            {
                engine.execute(this,otherNode);
                distanceVector.put(otherNode,engine.getShortestDistance(otherNode));
            }
        }

        System.out.println("");
    }

    public Map<ContextNode,Integer> getDistancesVector()
    {
        return distanceVector;
    }
}
