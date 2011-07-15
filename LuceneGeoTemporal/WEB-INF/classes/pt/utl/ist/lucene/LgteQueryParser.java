package pt.utl.ist.lucene;

import com.hrstc.lucene.Defs;
import com.hrstc.lucene.queryexpansion.QueryExpansion;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Filter;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.level1query.Level1Query;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.level1query.parser.Level1QueryParser;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.versioning.LuceneVersion;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteQueryParser
{
    private static final Logger logger = Logger.getLogger(LgteQueryParser.class);

    private static LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();

    public static LgteQuery parseQuery(String query) throws IOException, ParseException
    {
        return parseQuery(query,LgteAnalyzer.defaultAnalyzer);
    }

    
    public static LgteQuery parseQuery(String query, QueryConfiguration queryConfiguration) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer,null,queryConfiguration,null,null);
    }
    
    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, Analyzer a) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,a,lgteIndexSearcherWrapper,null, null,null);
    }
    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, Analyzer a, QueryConfiguration queryConfiguration) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,a,lgteIndexSearcherWrapper,queryConfiguration, null,null);
    }

    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer,lgteIndexSearcherWrapper,null, null,null);
    }

    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, LgteSort sort) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer,lgteIndexSearcherWrapper,null, sort,null);
    }

    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, QueryConfiguration queryConfiguration) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer,lgteIndexSearcherWrapper,queryConfiguration);
    }
    public static LgteQuery parseQuery(String query, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, QueryConfiguration queryConfiguration, QueryParams queryParams) throws IOException, ParseException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,LgteAnalyzer.defaultAnalyzer,lgteIndexSearcherWrapper,queryConfiguration,null, queryParams);
    }

    public static LgteQuery parseQuery(String query, Analyzer analyzer,LgteIndexSearcherWrapper lgteIndexSearcherWrapper, QueryConfiguration queryConfiguration) throws ParseException, IOException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer,lgteIndexSearcherWrapper,queryConfiguration);
    }
    public static LgteQuery parseQuery(String query, Analyzer analyzer,LgteIndexSearcherWrapper lgteIndexSearcherWrapper) throws ParseException, IOException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer,lgteIndexSearcherWrapper,null);
    }


    public static LgteQuery parseQuery(String query, String field, LgteIndexSearcherWrapper lgteIndexSearcherWrapper, QueryConfiguration queryConfiguration) throws ParseException, IOException
    {
        return parseQuery(query,field,LgteAnalyzer.defaultAnalyzer,lgteIndexSearcherWrapper,queryConfiguration);
    }

    public static LgteQuery parseQuery(String query, Analyzer analyzer) throws ParseException, IOException
    {
        return parseQuery(query,Globals.LUCENE_DEFAULT_FIELD,analyzer);
    }


    public static LgteQuery parseQuery(String query, String field, Analyzer analyzer) throws ParseException, IOException
    {
        return parseQuery(query,field,analyzer,(LgteSort) null);
    }

    public static LgteQuery parseQuery(String query, String field, Analyzer analyzer, LgteSort sort) throws ParseException, IOException
    {
        return parseQuery(query,field,analyzer,null,null,sort,null);
    }

    public static LgteQuery parseQuery(String query, String field, Analyzer analyzer, LgteIndexSearcherWrapper indexSearcherWrapper) throws ParseException, IOException
    {
        return parseQuery(query,field,analyzer, indexSearcherWrapper,null);
    }

    public static LgteQuery parseQuery(String query, String field, Analyzer analyzer, LgteIndexSearcherWrapper indexSearcherWrapper, QueryConfiguration queryConfiguration) throws ParseException, IOException
    {
        return parseQuery(query, field, analyzer, indexSearcherWrapper, queryConfiguration, null,null);
    }
    public static LgteQuery parseQuery(String query, String field, Analyzer analyzer, LgteIndexSearcherWrapper indexSearcherWrapper, QueryConfiguration queryConfiguration, LgteSort sort, QueryParams queryParams) throws ParseException, IOException
    {
        String queryStr;
        System.out.println("queryBefore:" + query);
        if(queryParams == null)
        {
            Level1QueryParser parser = new Level1QueryParser();
            Level1Query level1Query = parser.buildQuery(query);
            queryParams = level1Query.getQueryParams();
            queryStr = level1Query.toString().trim();
        }
        else
        {
            Level1QueryParser parser = new Level1QueryParser();
            Level1Query level1Query = parser.buildQuery(query);
            queryStr = level1Query.toString().trim();
            queryStr = query;
        }


        if(queryConfiguration != null)
            queryParams.setQueryConfiguration(queryConfiguration);
        //Set Model in Manager
        if(queryParams.getModel() != null) ModelManager.getInstance().setModel(queryParams.getModel(),queryConfiguration);
        if(queryParams.getModel() == null && queryParams.getQueryConfiguration() != null) ModelManager.getInstance().setQueryConfiguration(queryConfiguration);        

        Query returnQuery;

        if(queryStr.length() == 0)
        {
            StringBuilder newQueryBuilder = new StringBuilder();
            if(queryParams.isSpatial())
                newQueryBuilder.append(Globals.LUCENE_GEO_DOC_QUERY).append(' ');
            if(queryParams.isTime())
                newQueryBuilder.append(Globals.LUCENE_TIME_DOC_QUERY);
            String finalQuery = newQueryBuilder.toString();
            System.out.println("Query without LGTE information: " + finalQuery);
            if(finalQuery.length() > 0)
                returnQuery = luceneVersion.parseQuery(finalQuery,field,new WhitespaceAnalyzer());
            else
                return null;
        }
        else
        {
            System.out.println("Query without LGTE information: " + queryStr);
            returnQuery = luceneVersion.parseQuery(queryStr,field,analyzer);
        }
        if(queryParams.getQeEnum().isQE() || (queryConfiguration != null && queryConfiguration.getForceQE().isQE()))
        {
            returnQuery = lucQE(returnQuery, queryStr,field, analyzer, indexSearcherWrapper,queryParams, sort);
        }
        return new LgteQuery(returnQuery,queryParams,analyzer);
    }

    public static Query lucQE(LgteQuery query, String queryStr, LgteIndexSearcherWrapper indexSearcherWrapper, Filter filter) throws IOException, ParseException
    {
        return lucQE(query.getQuery(),queryStr,null,query.getAnalyzer(),indexSearcherWrapper,query.getQueryParams(),null,filter);
    }

    public static Query lucQE(Query query, String queryStr, String field, Analyzer analyzer, LgteIndexSearcherWrapper indexSearcherWrapper, QueryParams queryParams, LgteSort sort) throws IOException, ParseException
    {
        return lucQE(query, queryStr,field,analyzer,indexSearcherWrapper,queryParams,sort,null);
    }
    public static Query lucQE(Query query, String queryStr, String field, Analyzer analyzer, LgteIndexSearcherWrapper indexSearcherWrapper, QueryParams queryParams, LgteSort sort, Filter filter) throws IOException, ParseException
    {
        QueryConfiguration configuration = queryParams.getQueryConfiguration();

        if(indexSearcherWrapper == null || indexSearcherWrapper.getIndexSearcher() == null)
        {
            logger.warn("Please pass indexSearcherWrapper to QueryParser in order to expand Query with lucQE");
            logger.warn("Ignoring Query Expansion, missing Searcher");
            return query;
        }
        long time = System.currentTimeMillis();
        Similarity similarity = query.getSimilarity(indexSearcherWrapper.getIndexSearcher());
        Hits hits;
        if(queryParams.getQeEnum() == QEEnum.lgte || queryParams.getQueryConfiguration().getForceQE() == QEEnum.lgte)
        {
            LgteQuery lgteQuery = new LgteQuery(query,queryParams,analyzer);
            QEEnum oldValueForceQE = configuration.getForceQE();
            configuration.setForceQE(QEEnum.no);
            queryParams.setQEEnum(QEEnum.no);

            LgteHits lgteHits;
            if(sort == null && filter == null)
                lgteHits = indexSearcherWrapper.search(lgteQuery);
            else if(sort != null && filter == null)
                lgteHits = indexSearcherWrapper.search(lgteQuery,sort);
            else if(sort == null)
                lgteHits = indexSearcherWrapper.search(lgteQuery,filter);
            else
                lgteHits = indexSearcherWrapper.search(lgteQuery,filter,sort);


            hits = lgteHits.getHits();
            queryParams.setQEEnum(QEEnum.lgte);
            configuration.setForceQE(oldValueForceQE);
        }
        else
        {
            hits = indexSearcherWrapper.getIndexSearcher().search(query);
        }
        logger.info("lucQE matches: " + hits.length());

        if (hits.length() == 0)
        {
            return query;
        }
        else
        {
            if(configuration == null || configuration.getQueryProperties() == null)
            {
                Properties properties = new Properties();
                configuration = new QueryConfiguration();
                configuration.setQueryProperties(properties);
            }
            initConfiguration(configuration);
        }

        if(field != null)
            Defs.setFldText(field);
        else
            Defs.setFldText(Globals.LUCENE_DEFAULT_FIELD);

        QueryExpansion queryExpansion;
        queryExpansion = new QueryExpansion(analyzer, indexSearcherWrapper.getIndexSearcher(), similarity, configuration);
        query = queryExpansion.expandQuery(queryStr, hits);
        logger.info("Expanded Level1Query: " + query.toString());
        long endTime = System.currentTimeMillis() - time;
        logger.info("LucQE take: " + endTime + " ms");
        return query;
    }


    private static void initConfiguration(QueryConfiguration queryConfiguration)
    {
        Properties properties = queryConfiguration.getQueryProperties();
        List<String> props =  ConfigProperties.getListProperties("");
        for(String prop: props)
        {
        	if(properties.getProperty(prop) == null)
        	{
        		properties.setProperty(prop,ConfigProperties.getProperty(prop));
        	}
        }
//        if(properties.getProperty("QE.method") == null)
//            properties.setProperty("QE.method",ConfigProperties.getProperty("QE.method"));
//
//        if(properties.getProperty("QE.decay") == null)
//            properties.setProperty("QE.decay",ConfigProperties.getProperty("QE.decay"));
//
//        if(properties.getProperty("QE.doc.num") == null)
//            properties.setProperty("QE.doc.num",ConfigProperties.getProperty("QE.doc.num"));
//
//        if(properties.getProperty("QE.term.num") == null)
//            properties.setProperty("QE.term.num",ConfigProperties.getProperty("QE.term.num"));
//
//        if(properties.getProperty("rocchio.alpha") == null)
//            properties.setProperty("rocchio.alpha",ConfigProperties.getProperty("rocchio.alpha"));
//
//        if(properties.getProperty("rocchio.beta") == null)
//            properties.setProperty("rocchio.beta",ConfigProperties.getProperty("rocchio.beta"));
    }

}