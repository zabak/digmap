package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Logical implements IQuery
{

    //Can be AND, OR, NOT, TO
    String logical;

    public String getLogical()
    {
        return logical;
    }

    public void setLogical(String logical)
    {
        this.logical = logical;
    }

    public String toStringToPresent()
    {
        return toString();
    }

    public String toString()
    {
        return logical;
    }

    public String toStringHighLight()
    {
        return toString();
    }
}
