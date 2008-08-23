package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;

import java.util.List;

/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class InsideQuery extends SubQuery
{
    //can be words, logical, Subquery
    List objects;


    public List getObjects()
    {
        return objects;
    }

    public void setObjects(List objects)
    {
        this.objects = objects;
    }

    public String toStringToPresent()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toStringToPresent();
        }
        return toReturn.trim();
    }

    public String toString()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toString();
        }
        return toReturn.trim();
    }

    public String toStringHighLight()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toStringHighLight();
        }
        return toReturn.trim();
    }


}
