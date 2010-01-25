package pt.utl.ist.lucene.treceval.geotime.queries;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.index.Term;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.geotime.index.Config;

import java.io.File;
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
                                queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.CONTENTS),queryProcessor);
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
                                queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES),queryProcessor);
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
                String queryPlaces = queryProcessor.getPlacesQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryTimes;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                else
                    queryTimes = queryProcessor.getTimesQueryPointTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);

                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append(queryTerms);
                if(queryPlaces != null && queryPlaces.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlaces);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);
                return
                        new QueryPackage(
                                filter,finalQuery.toString(),queryProcessor);
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
                String queryPlaces = queryProcessor.getPlacesQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimes;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                else
                    queryTimes = queryProcessor.getTimesQueryPointTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);

                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append(queryTerms);
                if(queryPlaces != null && queryPlaces.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlaces);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);
                return
                        new QueryPackage(
                                filter,finalQuery.toString(),queryProcessor);
            }
        };
    }

    /*********************
     * Filter Strategies
     * @return
     */
    public Iterator baseFilteredIterator_comb_sentences()
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
                String queryPlaces = queryProcessor.getPlacesQuery(QueryProcessor.QueryTarget.CONTENTS);
                String queryTimes;
                if(queryProcessor.isTime_key())
                    queryTimes = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);
                else
                    queryTimes = queryProcessor.getTimesQueryPointTimeExpressions(QueryProcessor.QueryTarget.CONTENTS);

                StringBuilder finalQuery = new StringBuilder();
                finalQuery.append("(");
                finalQuery.append(queryTerms);
                if(queryPlaces != null && queryPlaces.trim().length() > 0)
                    finalQuery.append(" ").append(queryPlaces);
                if(queryTimes != null && queryTimes.trim().length() > 0)
                    finalQuery.append(" ").append(queryTimes);

                //FILTER QUERIES CONTENTS
                String queryPlaceFilters = queryProcessor.getPlaceFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                String queryPlaceTypeFilters = queryProcessor.getPlaceTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                String queryTimeFilters;
                String queryTimeTypeFilters;
                if(queryProcessor.isTime_key())
                {
                    queryTimeFilters = queryProcessor.getTimeKeyPointsFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimeTypeFilters = queryProcessor.getTimeKeyTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                }
                else
                {
                    queryTimeFilters = queryProcessor.getTimeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                    queryTimeTypeFilters = queryProcessor.getTimeTypeFiltersAsQueries(QueryProcessor.QueryTarget.CONTENTS);
                }
               //append FILTER QUERIES
                if(queryPlaceFilters != null && queryPlaceFilters.trim().length()>0)
                    finalQuery.append(" ").append(queryPlaceFilters);
                if(queryPlaceTypeFilters != null && queryPlaceTypeFilters.trim().length()>0)
                    finalQuery.append(" ").append(queryPlaceTypeFilters);
                if(queryTimeFilters != null && queryTimeFilters.trim().length()>0)
                    finalQuery.append(" ").append(queryTimeFilters);
                if(queryTimeTypeFilters != null && queryTimeTypeFilters.trim().length()>0)
                    finalQuery.append(" ").append(queryTimeTypeFilters);

                finalQuery.append(")^0.3");




                //SENTENCES QUERIES
                String queryTermsSentences = queryProcessor.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlacesSentences = queryProcessor.getPlacesQuery(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimesSentences;
                if(queryProcessor.isTime_key())
                    queryTimesSentences = queryProcessor.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);
                else
                    queryTimesSentences = queryProcessor.getTimesQueryPointTimeExpressions(QueryProcessor.QueryTarget.SENTENCES);

                StringBuilder finalQuerySentences = new StringBuilder();
                finalQuerySentences.append("(");
                finalQuerySentences.append(queryTermsSentences);
                if(queryPlacesSentences != null && queryPlacesSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryPlacesSentences);
                if(queryTimesSentences != null && queryTimesSentences.trim().length() > 0)
                    finalQuerySentences.append(" ").append(queryTimesSentences);

                //FILTER QUERIES
                String queryPlaceFiltersSentences = queryProcessor.getPlaceFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                String queryPlaceTypeFiltersSentences = queryProcessor.getPlaceTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                String queryTimeFiltersSentences;
                String queryTimeTypeFiltersSentences;
                if(queryProcessor.isTime_key())
                {
                    queryTimeFiltersSentences = queryProcessor.getTimeKeyPointsFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimeTypeFiltersSentences = queryProcessor.getTimeKeyTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                }
                else
                {
                    queryTimeFiltersSentences = queryProcessor.getTimeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                    queryTimeTypeFiltersSentences = queryProcessor.getTimeTypeFiltersAsQueries(QueryProcessor.QueryTarget.SENTENCES);
                }
                //append FILTER QUERIES
                if(queryPlaceFiltersSentences != null && queryPlaceFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryPlaceFiltersSentences);
                if(queryPlaceTypeFiltersSentences != null && queryPlaceTypeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryPlaceTypeFiltersSentences);
                if(queryTimeFiltersSentences != null && queryTimeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeFiltersSentences);
                if(queryTimeTypeFiltersSentences != null && queryTimeTypeFiltersSentences.trim().length()>0)
                    finalQuerySentences.append(" ").append(queryTimeTypeFiltersSentences);


                finalQuerySentences.append(")^0.7");



                return
                        new QueryPackage(
                                filter,finalQuerySentences.toString() + " " + finalQuery.toString(),queryProcessor);
            }
        };
    }


    public abstract class Iterator
    {
        public class QueryPackage
        {
            public QueryProcessor queryProcessor;
            public Filter filter;
            public String query;

            public String getTopicId()
            {
                return queryProcessor.getQ().getId();
            }

            public QueryPackage(Filter filter, String query,QueryProcessor queryProcessor) {
                this.filter = filter;
                this.query = query;
                this.queryProcessor = queryProcessor;
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
                    timeFilter.addTerm(new Term(Config.S_HAS_YYYY_KEY + suffix,"true"));
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


    public static void main(String [] args) throws MalformedURLException, DocumentException
    {
        StrategyQueryBuilder strategyQueryBuilder = new StrategyQueryBuilder("D:\\Jorge\\Documents\\ist\\doutoramento\\conferencias\\NTCIR\\topics.xml",true);

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
        iter = strategyQueryBuilder.baseFilteredIterator_comb_sentences();
        while((queryPackage = iter.next())!=null)
        {
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
        }
    }
}
