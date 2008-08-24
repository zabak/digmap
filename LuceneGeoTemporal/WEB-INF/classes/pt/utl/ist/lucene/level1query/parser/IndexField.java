package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.level1query.IQuery;

/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class IndexField implements IQuery
{

    String indexName;
    SubQuery query;


    public String getIndexName()
    {
        return indexName;
    }

    public void setIndexName(String indexName)
    {
        this.indexName = indexName;
    }

    public SubQuery getSubQuery()
    {
        return query;
    }

    public void setQuery(SubQuery query)
    {
        this.query = query;
    }

    public String toStringToPresent()
    {
        return indexName + ":" + query.toStringToPresent();
    }

    public String toString()
    {
        return indexName + ":" + query.toString();
    }

    public String toStringHighLight()
    {
        return indexName + ":" + query.toStringHighLight();
    }
}
