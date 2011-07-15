package pt.utl.ist.lucene.utils.queries;

import com.pjaol.lucene.search.SerialChainFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.Dom4jUtil;

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

    boolean usePlaceRefsTypesFiltersInQuery = false;
    boolean usePlaceRefsBelongTosFiltersInQuery = false;



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

    public Iterator baseOriginalBaseIterator()
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
                                null,
                                queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS),queryProcessor,null);
            }
        };
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

    public Iterator originalTermsIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
                return
                        new QueryPackage(null,
                                queryProcessor.getQ().getOriginalDescClean(),queryProcessor,null);
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

    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator baseFilteredIterator()
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

                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);


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
    public Iterator baseWikipediaIterator()
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
                addWikipediaTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                //addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                return
                        new QueryPackage(
                                createBaseFilter(queryProcessor, ""),finalQuery.toString(),queryProcessor,null);
            }
        };
    }


    public Iterator baseMetricsIterator()
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
                addGeoTemporalQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                //addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                return
                        new QueryPackage(
                                null,finalQuery.toString(),queryProcessor,null);
            }
        };
    }



    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator baseWikipediaFilterIterator()
    {
        return new Iterator(queryProcessors)
        {
            public QueryPackage next()
            {
                if(!super.queryProcessorIter.hasNext())
                    return null;
                QueryProcessor queryProcessor = queryProcessorIter.next();
//                Filter queryFilter = queryProcessor.getFilters(QueryProcessor.QueryTarget.CONTENTS);

                Filter queryFilter = queryProcessor.getWikipediaFilters(QueryProcessor.QueryTarget.CONTENTS);
                Filter filter = super.addBaseFilter(queryFilter,queryProcessor, "");

                //CONTENTS QUERIES
                StringBuilder finalQuery = new StringBuilder();
                addTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);
                addWikipediaTermQueries(queryProcessor,QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                //addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.CONTENTS,finalQuery);


                return
                        new QueryPackage(
                                filter,finalQuery.toString(),queryProcessor,queryFilter);
            }
        };
    }



    private void addWikipediaTermQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        String queryTermsWikipedia = queryProcessor.getWikipediaTermsQuery(queryTarget);
        finalQuery.append(" " + queryTermsWikipedia + " ");
    }


    private void addGeoTemporalQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        Query.GeographicQuery gq = queryProcessor.getQ().getGeographicQuery();

        boolean wantLastTime = false;
        if(queryProcessor.getQ().getTimes() != null)
        {
            for(Query.Times.Term time: queryProcessor.getQ().getTimes().getTerms())
            {
                if(time.getTime().equals("lastTime"))
                {
                    wantLastTime = true;
                    break;
                }
            }
        }

        Query.TemporalQuery tq = queryProcessor.getQ().getTemporalQuery();

        if(gq != null && gq.getQuery() != null)
            finalQuery.append(" " + gq.getQuery() + " ");

        if(wantLastTime)
            finalQuery.append(" time:2005 radiumYears:10 ");
        else if(tq != null && tq.getQuery() != null)
            finalQuery.append(" " + tq.getQuery() + " ");

        if((gq != null && gq.getQuery() != null) || (tq != null && tq.getQuery() != null))
            finalQuery.append(" filter: no ");
    }



    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator baseFilteredIterator_sentences()
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
                addFilterQueries(queryProcessor, QueryProcessor.QueryTarget.SENTENCES,finalQuery);

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
    public Iterator baseFilteredIterator_comb()
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
                                filter,finalQuerySentences.toString() + " " + finalQuery.toString(),queryProcessor,queryFilter);
            }
        };
    }

    private void addTermQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        String queryTermsSentences = queryProcessor.getTermsQuery(queryTarget);
        String queryPlacesRef = queryProcessor.getPlacesRefQuery(queryTarget, Config.placeRefFactor);
        String queryPlacesBelongTos = queryProcessor.getPlacesBeolongTosQuery(queryTarget, Config.belongTosFactor);
        String queryTimesKeys;
        String queryTimesRelative = null;
        String queryTimesDuration = null;
        if(queryProcessor.isTime_key())
            queryTimesKeys = queryProcessor.getTimesQueryKeyTimeExpressions(queryTarget, "1");
        else
        {
            queryTimesKeys = queryProcessor.getTimesQueryKeyTimeExpressions(queryTarget, Config.keyTimeFactor);
            queryTimesRelative = queryProcessor.getTimesQueryRelativeTimeExpressions(queryTarget, Config.relativeTimeFactor);
//            queryTimesDuration = queryProcessor.getTimesQueryDurationsTimeExpressions(queryTarget, Config.durationTimeFactor);
        }

        finalQuery.append(queryTermsSentences);
        if(queryPlacesRef != null && queryPlacesRef.trim().length() > 0)
            finalQuery.append(" ").append(queryPlacesRef);
        if(queryPlacesBelongTos != null && queryPlacesBelongTos.trim().length() > 0)
            finalQuery.append(" ").append(queryPlacesBelongTos);
        if(queryTimesKeys != null && queryTimesKeys.trim().length() > 0)
            finalQuery.append(" ").append(queryTimesKeys);
        if(queryTimesRelative != null && queryTimesRelative.trim().length() > 0)
            finalQuery.append(" ").append(queryTimesRelative);
        if(queryTimesDuration != null && queryTimesDuration.trim().length() > 0)
            finalQuery.append(" ").append(queryTimesDuration);
    }

    private void addFilterQueries(QueryProcessor queryProcessor, QueryProcessor.QueryTarget queryTarget, StringBuilder  finalQuery)
    {
        String queryPlaceRefsFilters = queryProcessor.getPlaceRefsFiltersAsQueries(queryTarget, Config.placeRefFactor);

        String queryPlaceBelongTosFilters = queryProcessor.getPlaceFiltersAsQueriesBelongTos(queryTarget, Config.belongTosFactor);
        String queryPlaceTypeFilters = queryProcessor.getPlaceTypeFiltersAsQueries(queryTarget);

        String queryTimeFiltersKeys = null;
        String queryTimeFiltersRelative = null;
        String queryTimeFiltersDuration = null;
        String queryTimeTypeFilters;
        if(queryProcessor.isTime_key())
        {
            queryTimeFiltersKeys = queryProcessor.getTimeKeyPointsFiltersAsQueries(queryTarget, "1");
            queryTimeTypeFilters = queryProcessor.getTimeKeyTypeFiltersAsQueries(queryTarget);
        }
        else
        {
            queryTimeFiltersKeys = queryProcessor.getTimeKeyPointsFiltersAsQueries(queryTarget, Config.keyTimeFactor);
            queryTimeFiltersRelative = queryProcessor.getTimeRelativePointsFiltersAsQueries(queryTarget, Config.relativeTimeFactor);
//            queryTimeFiltersDuration = queryProcessor.getTimeDurationPointsFiltersAsQueries(queryTarget, Config.durationTimeFactor);

            queryTimeTypeFilters = queryProcessor.getTimeTypeFiltersAsQueries(queryTarget);
        }
        //append FILTER QUERIES
        if(queryPlaceRefsFilters != null && queryPlaceRefsFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceRefsFilters);
        if(usePlaceRefsBelongTosFiltersInQuery && queryPlaceBelongTosFilters != null && queryPlaceBelongTosFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceBelongTosFilters);
        if(usePlaceRefsTypesFiltersInQuery && queryPlaceTypeFilters != null && queryPlaceTypeFilters.trim().length()>0)
            finalQuery.append(" ").append(queryPlaceTypeFilters);
        if(queryTimeFiltersKeys != null && queryTimeFiltersKeys.trim().length()>0)
            finalQuery.append(" ").append(queryTimeFiltersKeys);
        if(queryTimeFiltersRelative != null && queryTimeFiltersRelative.trim().length()>0)
            finalQuery.append(" ").append(queryTimeFiltersRelative);
        if(queryTimeFiltersDuration != null && queryTimeFiltersDuration.trim().length()>0)
            finalQuery.append(" ").append(queryTimeFiltersDuration);
        if(queryTimeTypeFilters != null && queryTimeTypeFilters.trim().length()>0)
            finalQuery.append(" ").append(queryTimeTypeFilters);
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
            if(q.wantPlaces() && q.wantTimes())
            {   TermsFilter timeFilter = new TermsFilter();
                if(q.isTime_key())
                    timeFilter.addTerm(new Term(Config.S_HAS_TIME_POINTS_KEY + suffix,"true"));
                else
                    timeFilter.addTerm(new Term(Config.S_HAS_ANY_TIME_POINT + suffix,"true"));  //OPTAR POR DURACOES

                TermsFilter filterGeo = new TermsFilter();
                filterGeo.addTerm(new Term(Config.S_GEO_INDEXED + suffix,"true"));

                Filter[] filterChain = new Filter[]{filterGeo,timeFilter};
                int[] actionType = new int[]{SerialChainFilter.AND,SerialChainFilter.AND};
                return new SerialChainFilter(filterChain,actionType);
            }
            else if(q.wantPlaces())
            {
                TermsFilter filter = new TermsFilter();
                filter.addTerm(new Term(Config.S_GEO_INDEXED  + suffix,"true"));
                return filter;
            }
            else if(q.wantTimes())
            {
                if(q.isTime_key())
                {
                    TermsFilter filter = new TermsFilter();
                    filter.addTerm(new Term(Config.S_HAS_YYYY_KEY + suffix,"true"));
                    return filter;
                }
                else
                {
                    TermsFilter filter = new TermsFilter();
                    filter.addTerm(new Term(Config.S_HAS_ANY_TIME_POINT + suffix,"true"));  //OPTAR POR DURACOES
                    return filter;
                }
            }
            else return null;
        }
    }


    public static void main(String [] args) throws IOException, DocumentException, ParseException {
        StrategyQueryBuilder strategyQueryBuilder = new StrategyQueryBuilder(Config.ntcirBase +  File.separator + "topics" + File.separator + "topics.xml",false);
        Iterator.QueryPackage lastQ = null;
        System.out.println("####################################");
        Iterator iter = strategyQueryBuilder.baseSimpleIterator();
        Iterator.QueryPackage queryPackage;
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseSimpleIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            lastQ = queryPackage;
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            if(queryPackage.queryFilters == null)
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$EXPANSION");
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




        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator_comb();

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
