package pt.utl.ist.lucene;

import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;

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

    public LgteQuery(String query, QueryParams queryParams, Analyzer analyzer) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer);
        this.queryParams = queryParams;
        this.analyzer = analyzer;
    }

    public LgteQuery(String query, QueryParams queryParams, Analyzer analyzer, String defaultField) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,defaultField,analyzer);
        this.queryParams = queryParams;
        this.analyzer = analyzer;
    }
    public LgteQuery(String query, Analyzer analyzer, String defaultField) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,defaultField,analyzer);
        this.queryParams = new QueryParams();
        this.analyzer = analyzer;
    }

    public LgteQuery(String query, Analyzer analyzer, String defaultField, QueryConfiguration queryConfiguration) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,defaultField,analyzer);
        this.queryParams = new QueryParams();
        this.analyzer = analyzer;
        setQueryConfiguration(queryConfiguration);
    }
    public LgteQuery(String query, Analyzer analyzer) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer);
        this.queryParams = new QueryParams();
        this.analyzer = analyzer;
    }

     public LgteQuery(String query, Analyzer analyzer, QueryConfiguration queryConfiguration) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer);
        this.queryParams = new QueryParams();
        this.analyzer = analyzer;
        setQueryConfiguration(queryConfiguration);
    }

    public LgteQuery(String query) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer);
        this.queryParams = new QueryParams();
        this.analyzer = LgteAnalyzer.defaultAnalyzer;
    }

    public LgteQuery(String query,QueryConfiguration queryConfiguration) throws ParseException
    {
        this.query = LuceneVersionFactory.getLuceneVersion().parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer);
        this.queryParams = new QueryParams();
        this.analyzer = LgteAnalyzer.defaultAnalyzer;
        setQueryConfiguration(queryConfiguration);
    }

    public void setQueryConfiguration(QueryConfiguration queryConfiguration)
    {
        getQueryParams().setQueryConfiguration(queryConfiguration);
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
