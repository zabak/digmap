package pt.utl.ist.lucene;

import pt.utl.ist.lucene.level1query.QueryParams;
import org.apache.lucene.search.Query;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteQuery
{

    private Query q;
    private QueryParams queryParams;


    public LgteQuery(Query q, QueryParams queryParams)
    {
        this.q = q;
        this.queryParams = queryParams;
    }


    public Query getQuery()
    {
        return q;
    }

    public QueryParams getQueryParams()
    {
        return queryParams;
    }
    
    public String toString(String s) {
    	return q.toString(s);
    }

}
