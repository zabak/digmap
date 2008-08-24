package pt.utl.ist.lucene;

import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteQuery
{

    private Query query;
    private QueryParams queryParams;
    Analyzer analyzer;


    public LgteQuery(Query q)
    {
        this.query = q;
        this.queryParams = new QueryParams();
        this.analyzer = LgteAnalyzer.defaultAnalyzer;
    }

    public LgteQuery(Query q, QueryParams queryParams, Analyzer analyzer)
    {
        this.query = q;
        this.queryParams = queryParams;
        this.analyzer = analyzer;
    }

    public LgteQuery(Query q, QueryParams queryParams)
    {
        this.query = q;
        this.queryParams = queryParams;
        this.analyzer = LgteAnalyzer.defaultAnalyzer;
    }


    public Query getQuery()
    {
        return query;
    }

    public QueryParams getQueryParams()
    {
        return queryParams;
    }


    public Analyzer getAnalyzer()
    {
        return analyzer;
    }


}
