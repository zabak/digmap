package pt.utl.ist.lucene.treceval.geoclef2.queries;

import com.pjaol.lucene.search.SerialChainFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import pt.utl.ist.lucene.treceval.geoclef2.index.Config;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.queries.QueryParser;
import pt.utl.ist.lucene.utils.queries.QueryProcessor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Machado
 * @date 25/Jan/2010
 * @time 12:43:27
 * @email machadofisher@gmail.com
 */
public class StrategyQueryBuilder
{
    Document topics;
    List<QueryProcessor> queryProcessors;



    public StrategyQueryBuilder(String topicsFile,boolean timesKey) throws MalformedURLException, DocumentException
    {
        queryProcessors = new ArrayList<QueryProcessor>();
        topics = Dom4jUtil.parse(new File(topicsFile));
        XPath xPath = topics.createXPath("//topic");
        List<Element> topicsElems = xPath.selectNodes(topics);
        for(Element topic: topicsElems)
        {
            QueryParser queryParser = new QueryParser(topic);
            QueryProcessor queryProcessor = new QueryProcessor(queryParser.getQuery(),timesKey);
            queryProcessors.add(queryProcessor);
        }
    }

    /**
     * Base Strategies
     * @return
     */

    public Iterator baseSimpleIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor, ""),
                                queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS),queryProcessor,null);
            }
        };
    }

    public Iterator baseSimpleIterator_sentences()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor,Config.SEP + Config.SENTENCES),
                                queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES),queryProcessor,null);
            }
        };
    }

    public Iterator baseCombSimpleIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();


                //CONTENTS QUERIES
                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append("(");
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);
                finalQuery.append(")^").append(Config.combContentsFactor);


                //SENTENCES QUERIES
                StringBuilder finalQuerySentences = new StringBuilder();
                finalQuerySentences.append("(");
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.SENTENCES,finalQuerySentences);
                finalQuerySentences.append(")^").append(Config.combSentencesFactor);

                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor,""),
                                finalQuerySentences.toString() + " " + finalQuery.toString(),queryProcessor,null);
            }
        };
    }

    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator filteredIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.CONTENTS);
                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, "");


                //CONTENTS QUERIES
                StringBuilder finalQuery = new StringBuilder();
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                return
                        new QueryPackage(
                                filter,finalQuery.toString(),queryProcessor,queryFilter);
            }
        };
    }



    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator filteredIterator_sentences()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.SENTENCES);
                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, Config.SEP + Config.SENTENCES);

                StringBuilder finalQuery = new StringBuilder();
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.SENTENCES,finalQuery);

                return
                        new QueryPackage(
                                filter,finalQuery.toString(),queryProcessor,queryFilter);
            }
        };
    }



    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator unifiedIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
//                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.CONTENTS);
//                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, "");


                //CONTENTS QUERIES
                StringBuilder finalQuery = new StringBuilder();
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);
                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor, ""),finalQuery.toString(),queryProcessor,null);
            }
        };
    }



    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator unifiedIterator_sentences()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
//                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.SENTENCES);
//                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, Config.SEP + Config.SENTENCES);

                StringBuilder finalQuery = new StringBuilder();
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.SENTENCES,finalQuery);
                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.SENTENCES,finalQuery);

                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor, Config.SEP + Config.SENTENCES),finalQuery.toString(),queryProcessor,null);
            }
        };
    }

    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator unified_comb()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
//                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.CONTENTS);
//                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, "");

                //CONTENTS QUERIES
                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append("(");
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);
                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);
                finalQuery.append(")^").append(Config.combContentsFactor);


                //SENTENCES QUERIES
                StringBuilder finalQuerySentences = new StringBuilder();
                finalQuerySentences.append("(");
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.SENTENCES,finalQuerySentences);
                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.SENTENCES,finalQuerySentences);
                finalQuerySentences.append(")^").append(Config.combSentencesFactor);

                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor, ""),finalQuerySentences.toString() + " " + finalQuery.toString(),queryProcessor,null);
            }
        };
    }

    private void addTermQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        String queryTermsSentences = queryProcessor.getTermsQuery(queryTarget);
        String queryPlacesRef = queryProcessor.getPlacesRefQuery(queryTarget, Config.placeRefFactor);
        String queryPlacesBelongTos = queryProcessor.getPlacesBeolongTosQuery(queryTarget, Config.belongTosFactor);

        finalQuery.append(queryTermsSentences);
        if(queryPlacesRef != null && queryPlacesRef.trim().length() > 0)
            finalQuery.append(" ").append(queryPlacesRef);
        if(queryPlacesBelongTos != null && queryPlacesBelongTos.trim().length() > 0)
            finalQuery.append(" ").append(queryPlacesBelongTos);
    }

    private void addFilterQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        String queryPlaceRefsFilters = queryProcessor.getPlaceRefsFiltersAsQueries(queryTarget, Config.placeRefFactor);
        String queryPlaceBelongTosFilters = queryProcessor.getPlaceFiltersAsQueriesBelongTos(queryTarget, Config.belongTosFactor);
        String queryPlaceTypeFilters = queryProcessor.getPlaceTypeFiltersAsQueries(queryTarget);

        //append FILTER QUERIES
        if(queryPlaceRefsFilters != null && queryPlaceRefsFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceRefsFilters);
        if(queryPlaceBelongTosFilters != null && queryPlaceBelongTosFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceBelongTosFilters);
        if(queryPlaceTypeFilters != null && queryPlaceTypeFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceTypeFilters);
    }


    public abstract class Iterator
    {
        public class QueryPackage
        {
            public Filter queryFilters;
            public QueryProcessor queryProcessor;
            public Filter filter;
            public String query;

            public String getTopicId()
            {
                return queryProcessor.getQ().getId();
            }

            public QueryPackage(Filter filter, String query,QueryProcessor queryProcessor, Filter queryFilter) {
                this.filter = filter;
                this.query = query;
                this.queryProcessor = queryProcessor;
                this.queryFilters = queryFilter;
            }
        }
        protected java.util.Iterator<QueryProcessor> queryProcessorIter;
        private Iterator(List<QueryProcessor> queryProcessors) {this.queryProcessorIter = queryProcessors.iterator();}
        public abstract QueryPackage next();

        protected Filter addBaseFilter(Filter queryFilter, QueryProcessor q, String suffix)
        {
            Filter filter = createBaseFilter(q,suffix);
            if(filter == null)
                return queryFilter;

            if(queryFilter == null)
                return filter;

            return new SerialChainFilter(new Filter[]{filter,queryFilter},new int[]{SerialChainFilter.AND,SerialChainFilter.AND});
        }

        protected Filter createBaseFilter(QueryProcessor q, String suffix)
        {
            TermsFilter filter = new TermsFilter();
            filter.addTerm(new Term(Config.S_GEO_INDEXED  + suffix,"true"));
            return filter;
        }
    }


    public static void main(String [] args) throws IOException, DocumentException, ParseException {
        StrategyQueryBuilder strategyQueryBuilder = new StrategyQueryBuilder(Config.geoclefBase +  File.separator + "topics" + File.separator + "topics08Formatted.xml",false);
        Iterator.QueryPackage lastQ = null;
        System.out.println("CONTENTS ####################################");
        Iterator iter = strategyQueryBuilder.baseSimpleIterator();
        Iterator.QueryPackage queryPackage;
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("SENTENCES ####################################");
        iter = strategyQueryBuilder.baseSimpleIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("COMB ####################################");
        iter = strategyQueryBuilder.baseCombSimpleIterator();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("FILTER CONTENTS ####################################");
        iter = strategyQueryBuilder.filteredIterator();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("FILTER PARAGRAPHS ####################################");
        iter = strategyQueryBuilder.filteredIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }


        System.out.println("UNIFIED ####################################");
        iter = strategyQueryBuilder.unifiedIterator();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("UNIFIED PARAGRAPHS ####################################");
        iter = strategyQueryBuilder.unifiedIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

//        LgteIndexSearcherWrapper searcher = Config.openMultiSearcherSentences();
//        QueryConfiguration queryConfiguration = new QueryConfiguration();
//        queryConfiguration.setProperty("bm25.k1", "1.2d");
//        queryConfiguration.setProperty("bm25.b", "0.75d");
//        queryConfiguration.setProperty("bm25.k3", "0.75d");
//        queryConfiguration.setProperty("index.tree", "true");
//        LgteQuery query = LgteQueryParser.parseQuery("g_allWoeid_sentences:WOEID-23424778", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
//        LgteHits hits = searcher.search(query,lastQ.filter);
//
//        for(int i = 0; i < hits.length();i++)
//            System.out.println(i + ":" + hits.doc(i).get("id"));
//
//        searcher.close();




        System.out.println("UNIFIED COMB ####################################");
        iter = strategyQueryBuilder.unified_comb();

        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }


//        searcher = Config.openMultiSearcherForContentsAndSentences();
//        queryConfiguration = new QueryConfiguration();
//        queryConfiguration.setProperty("bm25.k1", "1.2d");
//        queryConfiguration.setProperty("bm25.b", "0.75d");
//        queryConfiguration.setProperty("bm25.k3", "0.75d");
//        queryConfiguration.setProperty("index.tree", "true");
//        query = LgteQueryParser.parseQuery("g_allWoeid_sentences:(WOEID-23424778 WOEID-12493166) S_HAS_TIMEXES_sentences:true", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
//        hits = searcher.search(query,lastQ.filter);
//
//        for(int i = 0; i < hits.length();i++)
//            System.out.println(i + ":" + hits.doc(i).get("id"));
//
//        searcher.close();



    }
}