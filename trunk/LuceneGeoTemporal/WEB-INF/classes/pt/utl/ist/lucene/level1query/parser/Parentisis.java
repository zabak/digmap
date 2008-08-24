package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Parentisis extends InsideQuery implements IQuery
{

    public Parentisis(String suffix)
    {
        this.suffix = suffix;
    }

    public String suffix = "";

    public String toString()
    {
        return "(" + super.toString() + ")" + suffix;
    }

    public String toStringToPresent()
    {
        return "(" + super.toString() + ")" + suffix;
    }

    public String toStringHighLight()
    {
        return "(" + super.toStringHighLight() + ")" + suffix;
    }


    public String toString(String[] notAdmitedIndexes)
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toString();
        }
        return toReturn.trim();
    }


}
