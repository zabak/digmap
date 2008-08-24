package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;

/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Coma extends InsideQuery implements IQuery
{

    private String suffix = "";

    public Coma(String suffix)
    {
        this.suffix = suffix;
    }

    public String toStringToPresent()
    {
        return "\"" + super.toString() + "\"" + suffix;
    }

    public String toString()
    {
        return "\"" + super.toString() + "\"" + suffix;
    }

    public String toStringHighLight()
    {
        return "\"" + super.toStringHighLight() + "\"" + suffix;
    }

}
