package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Term extends SubQuery implements IQuery
{


    private String value;
    private String index;
    private long id;
    private boolean highlight = false;

    private int getTermsStringOffSet = -1;


    public int getGetTermsStringOffSet()
    {
        return getTermsStringOffSet;
    }

    public void setGetTermsStringOffSet(int getTermsStringOffSet)
    {
        this.getTermsStringOffSet = getTermsStringOffSet;
    }

    public long getId()
    {
        return id;
    }

    public Term(String value, String index, long id)
    {
        this.value = value;
        this.index = index;
        this.id = id;
    }

    public boolean isHighlight()
    {
        return highlight;
    }

    public void setHighlight(boolean highlight)
    {
        this.highlight = highlight;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }


    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String toStringToPresent()
    {
        return value;
    }

    public String toString()
    {
        return value;
    }

    public String toStringHighLight()
    {
        if (highlight)
            return "<b><i>" + value + "</b></i>";
        else
            return toString();
    }


}
