package pt.utl.ist.lucene.treceval.geotime.queries;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

import com.pjaol.lucene.search.SerialChainFilter;

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
                String queryTerms = queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryPlacesRef = queryProcessor.getPlacesRefQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryPlacesBelongTos = queryProcessor.getPlacesBeolongTosQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryTimes = null;
                String queryTimesKeys = null;
                String queryTimesRelative = null;
                String queryTimesDuration = null;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                else
                {
                    queryTimesKeys = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimesRelative = queryProcessor.getTimesQueryRelativeTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimesDuration = queryProcessor.getTimesQueryDurationsTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                }

                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append(queryTerms);
                if(queryPlacesRef != null && queryPlacesRef.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesRef);
                if(queryPlacesBelongTos != null && queryPlacesBelongTos.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesBelongTos);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);
                if(queryTimesKeys != null && queryTimesKeys.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesKeys);
                if(queryTimesRelative != null && queryTimesRelative.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesRelative);
                if(queryTimesDuration != null && queryTimesDuration.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesDuration);

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
                String queryTerms = queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlacesRef = queryProcessor.getPlacesRefQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlacesBelongTos = queryProcessor.getPlacesBeolongTosQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimes = null;
                String queryTimesKeys = null;
                String queryTimesRelative = null;
                String queryTimesDuration = null;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                else
                {
                    queryTimesKeys = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimesRelative = queryProcessor.getTimesQueryRelativeTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimesDuration = queryProcessor.getTimesQueryDurationsTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                }

                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append(queryTerms);
                if(queryPlacesRef != null && queryPlacesRef.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesRef);
                if(queryPlacesBelongTos != null && queryPlacesBelongTos.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesBelongTos);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);
                if(queryTimesKeys != null && queryTimesKeys.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesKeys);
                if(queryTimesRelative != null && queryTimesRelative.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesRelative);
                if(queryTimesDuration != null && queryTimesDuration.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesDuration);
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

                String queryTerms = queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryPlacesRef = queryProcessor.getPlacesRefQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryPlacesBelongTos = queryProcessor.getPlacesBeolongTosQuery(QueryProcessor.QueryTarget.CONTENTS);

                String queryTimes = null;
                String queryTimesKeys = null;
                String queryTimesRelative = null;
                String queryTimesDuration = null;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                else
                {
                    queryTimesKeys = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimesRelative = queryProcessor.getTimesQueryRelativeTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimesDuration = queryProcessor.getTimesQueryDurationsTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                }


                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append("(");
                finalQuery.append(queryTerms);
                if(queryPlacesRef != null && queryPlacesRef.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesRef);
                if(queryPlacesBelongTos != null && queryPlacesBelongTos.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlacesBelongTos);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);
                if(queryTimesKeys != null && queryTimesKeys.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesKeys);
                if(queryTimesRelative != null && queryTimesRelative.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesRelative);
                if(queryTimesDuration != null && queryTimesDuration.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimesDuration);

//                //FILTER QUERIES CONTENTS
//                String queryPlaceFilters = queryProcessor.getPlaceFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                String queryPlaceTypeFilters = queryProcessor.getPlaceTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                String queryTimeFilters;
//                String queryTimeTypeFilters;
//                if(queryProcessor.isTime_key())
//                {
//                    queryTimeFilters = queryProcessor.getTimeKeyPointsFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                    queryTimeTypeFilters = queryProcessor.getTimeKeyTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                }
//                else
//                {
//                    queryTimeFilters = queryProcessor.getTimeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                    queryTimeTypeFilters = queryProcessor.getTimeTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
//                }
//               //append FILTER QUERIES
//                if(queryPlaceFilters != null && queryPlaceFilters.trim().length()>0)
//                    finalQuery.append(" ").append(queryPlaceFilters);
//                if(queryPlaceTypeFilters != null && queryPlaceTypeFilters.trim().length()>0)
//                    finalQuery.append(" ").append(queryPlaceTypeFilters);
//                if(queryTimeFilters != null && queryTimeFilters.trim().length()>0)
//                    finalQuery.append(" ").append(queryTimeFilters);
//                if(queryTimeTypeFilters != null && queryTimeTypeFilters.trim().length()>0)
//                    finalQuery.append(" ").append(queryTimeTypeFilters);

                finalQuery.append(")^" + Config.combContentsFactor);




                //SENTENCES QUERIES
                String queryTermsSentences = queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlacesRefSentences = queryProcessor.getPlacesRefQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlacesBelongTosSentences = queryProcessor.getPlacesBeolongTosQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimesSentences = null;
                String queryTimesKeysSentences = null;
                String queryTimesRelativeSentences = null;
                String queryTimesDurationSentences = null;
                if(queryProcessor.isTime_key())
                    queryTimesSentences = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                else
                {
                    queryTimesKeysSentences = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimesRelativeSentences = queryProcessor.getTimesQueryRelativeTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimesDurationSentences = queryProcessor.getTimesQueryDurationsTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                }

                StringBuilder finalQuerySentences = new StringBuilder();
                finalQuerySentences.append("(");
                finalQuerySentences.append(queryTermsSentences);
                if(queryPlacesRefSentences != null && queryPlacesRefSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryPlacesRefSentences);
                if(queryPlacesBelongTosSentences != null && queryPlacesBelongTosSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryPlacesBelongTosSentences);
                if(queryTimesSentences != null && queryTimesSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryTimesSentences);
                if(queryTimesKeysSentences != null && queryTimesKeysSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryTimesKeysSentences);
                if(queryTimesRelativeSentences != null && queryTimesRelativeSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryTimesRelativeSentences);
                if(queryTimesDurationSentences != null && queryTimesDurationSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryTimesDurationSentences);

                //FILTER QUERIES
                String queryPlaceRefsFiltersSentences = queryProcessor.getPlaceRefsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlaceBelongTosFiltersSentences = queryProcessor.getPlaceFiltersAsQueriesBelongTos(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlaceTypeFiltersSentences = queryProcessor.getPlaceTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimeFiltersSentences = null;
                String queryTimeFiltersSentencesKeys = null;
                String queryTimeFiltersSentencesRelative = null;
                String queryTimeFiltersSentencesDuration = null;
                String queryTimeTypeFiltersSentences;
                if(queryProcessor.isTime_key())
                {
                    queryTimeFiltersSentences = queryProcessor.getTimeKeyPointsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimeTypeFiltersSentences = queryProcessor.getTimeKeyTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                }
                else
                {
                    queryTimeFiltersSentencesKeys = queryProcessor.getTimeKeyPointsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimeFiltersSentencesRelative = queryProcessor.getTimeRelativePointsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimeFiltersSentencesDuration = queryProcessor.getTimeDurationPointsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);

                    queryTimeTypeFiltersSentences = queryProcessor.getTimeTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                }
                //append FILTER QUERIES
                if(queryPlaceRefsFiltersSentences != null && queryPlaceRefsFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryPlaceRefsFiltersSentences);
                if(queryPlaceBelongTosFiltersSentences != null && queryPlaceBelongTosFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryPlaceBelongTosFiltersSentences);
                if(queryPlaceTypeFiltersSentences != null && queryPlaceTypeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryPlaceTypeFiltersSentences);
                if(queryTimeFiltersSentences != null && queryTimeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeFiltersSentences);

                if(queryTimeFiltersSentencesKeys != null && queryTimeFiltersSentencesKeys.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeFiltersSentencesKeys);
                if(queryTimeFiltersSentencesRelative != null && queryTimeFiltersSentencesRelative.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeFiltersSentencesRelative);
                if(queryTimeFiltersSentencesDuration != null && queryTimeFiltersSentencesDuration.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeFiltersSentencesDuration);

                if(queryTimeTypeFiltersSentences != null && queryTimeTypeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeTypeFiltersSentences);


                finalQuerySentences.append(")^" + Config.combSentencesFactor);



                return
                        new QueryPackage(
                                filter,finalQuerySentences.toString() + " " + finalQuery.toString(),queryProcessor,queryFilter);
            }
        };
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

        System.out.println("####################################");
        Iterator iter = strategyQueryBuilder.baseSimpleIterator();
        Iterator.QueryPackage queryPackage;
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseSimpleIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
        }

        System.out.println("####################################");
        iter = strategyQueryBuilder.baseFilteredIterator_comb();
        Iterator.QueryPackage q = null;
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            q = queryPackage;
        }


//        LgteIndexSearcherWrapper searcher = Config.openMultiSearcherForContentsAndSentences();
//        QueryConfiguration queryConfiguration = new QueryConfiguration();
//        queryConfiguration.setProperty("bm25.k1", "1.2d");
//        queryConfiguration.setProperty("bm25.b", "0.75d");
//        queryConfiguration.setProperty("bm25.k3", "0.75d");
//        queryConfiguration.setProperty("index.tree", "true");
//        LgteQuery query = LgteQueryParser.parseQuery("g_allWoeid:WOEID-23424778", searcher, new LgteWhiteSpacesAnalyzer(), queryConfiguration);
//        LgteHits hits = searcher.search(query,q.filter);
//
//        System.out.println(hits.doc(0).get("id"));
//        System.out.println(hits.doc(0).get(Config.G_PLACE_BELONG_TOS_WOEID));
//        System.out.println(hits.doc(1).get("id"));
//        System.out.println(hits.doc(2).get("id"));
//        System.out.println(hits.doc(3).get("id"));
//        System.out.println(hits.doc(4).get("id"));
//
//        searcher.close();

    }
}
