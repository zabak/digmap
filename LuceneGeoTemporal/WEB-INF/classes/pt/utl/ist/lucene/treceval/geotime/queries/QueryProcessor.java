package pt.utl.ist.lucene.treceval.geotime.queries;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.LgteQuery;
import pt.utl.ist.lucene.LgteQueryParser;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import com.pjaol.lucene.search.SerialChainFilter;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 11:44:28
 * @email machadofisher@gmail.com
 */
public class QueryProcessor
{
    private static final Logger logger = Logger.getLogger(QueryProcessor.class);

    Query q;
    Filter filter = null;
    boolean time_key = false;


    public enum QueryTarget
    {
        CONTENTS,
        SENTENCES,
        SENTENCES_CONTENTS
    }

    public QueryProcessor(Query q)
    {
        this.q = q;
    }

    public QueryProcessor(Query q, boolean timeKey)
    {
        this.q = q;
        this.time_key = timeKey;
    }

    public String getQuery(QueryTarget queryTarget)
    {
        return prepareQueryString(queryTarget);
    }

    public String getPlacesQuery(QueryTarget queryTarget)
    {
        return preparePlacesQueryString(queryTarget);
    }

    public Filter getFilters(QueryTarget queryTarget)
    {
        if(filter == null)
            filter = prepareFilters(queryTarget);
        return filter;
    }

    /**
     * todo need test
     * @param queryTarget
     * @return
     */
    private Filter prepareFilters(QueryTarget queryTarget)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            if(q.getFilterChain().getBooleanClause().getTerms().size() == 1)
            {
                return getTermFilter(q.getFilterChain().getBooleanClause().getTerms().get(0),queryTarget);
            }
            else
            {
                Filter[] filters = new Filter[q.getFilterChain().getBooleanClause().getTerms().size()];
                int[] actionType = new int[q.getFilterChain().getBooleanClause().getTerms().size()];
                int action = q.getFilterChain().getBooleanClause().getLogicValue() == Query.FilterChain.BooleanClause.LogicValue.AND ? SerialChainFilter.AND: SerialChainFilter.OR;
                for(int i = 0; i < actionType.length; i++)
                    actionType[i] = action;

                for(int i = 0; i < filters.length; i++)
                {
                    Query.FilterChain.BooleanClause.Term t= q.getFilterChain().getBooleanClause().getTerms().get(i);
                    filters[i] = getTermFilter(t,queryTarget);
                }
                return new SerialChainFilter(filters,actionType);
            }
        }
        return null;
    }

    private String prepareQueryString(QueryTarget queryTarget)
    {
        if(queryTarget == QueryTarget.CONTENTS)
            return Config.CONTENTS + ":(" + q.getTerms().getDesc() + " " + q.getTerms().getDesc() + " " + q.getTerms().getNarr() + ")";
        else if(queryTarget == QueryTarget.SENTENCES)
            return Config.SENTENCES + ":(" + q.getTerms().getDesc() + " " + q.getTerms().getDesc() + " " + q.getTerms().getNarr() + ")";
        else
        {
            return
                    Config.SENTENCES + ":(" + q.getTerms().getDesc() + " " + q.getTerms().getDesc() + " " + q.getTerms().getNarr() + ")^0.7 " +
                            Config.CONTENTS + ":(" + q.getTerms().getDesc() + " " + q.getTerms().getDesc() + " " + q.getTerms().getNarr() + ")^0.3";
        }
    }

    private String preparePlacesQueryString(QueryTarget queryTarget)
    {

        if(queryTarget == QueryTarget.CONTENTS)
        {
            if(q.getPlaces().getTerms().size()>0)
            {
                StringBuilder places = new StringBuilder();
                for(Query.Places.Term place: q.getPlaces().getTerms())
                {
                    if(!place.getPlace().equals("?"))
                        for(String woeid: place.getWoeid())
                            places.append(woeid).append(" ");
                }
            }
        }
        //todo sentences e sentences_contents
        return null;
    }

    public String prepareTimesQueryString(QueryTarget queryTarget)
    {
        if(queryTarget == QueryTarget.CONTENTS)
        {
            if(q.getTimes().getTerms().size()>0)
            {
                StringBuilder times = new StringBuilder();
                for(Query.Times.Term time: q.getTimes().getTerms())
                {
                    if(!time.getTime().equals("?"))
                        times.append(time).append("* ");   
                }
                return Config.T_TIME_EXPRESSIONS + ":(" + times.toString() + ")";
            }
        }
        //todo sentences e sentences_contents
        return null;
    }

    private Filter getTermFilter(Query.FilterChain.BooleanClause.Term term, QueryTarget queryTarget)
    {
        String suffix = queryTarget == QueryTarget.SENTENCES_CONTENTS || queryTarget == QueryTarget.SENTENCES ? Config.SEP + Config.SENTENCES : "";

        if(term.getField().equals("time"))
            return getTimeFilter(term,suffix);
        else if(term.getField().equals("timeType"))
            return getTimeTypeFilter(term,suffix);
        else if(term.getField().equals("place"))
            return getPlaceFilter(term,suffix);
        else if(term.getField().equals("placeType"))
            return getPlaceTypeFilter(term,suffix);

        return null;
    }

    private Filter getTimeTypeFilter(Query.FilterChain.BooleanClause.Term term , String suffix)
    {
        Filter filter = null;

        if(term.getValue().equals("year"))
        {
            int[] actionType = new int[3];
            actionType[0] = SerialChainFilter.OR;
            actionType[1] = SerialChainFilter.OR;
            actionType[2] = SerialChainFilter.OR;
            TermsFilter termsFilterYYYY = new TermsFilter();
            if(time_key)
                termsFilterYYYY.addTerm(new Term(Config.S_HAS_YYYY_KEY + suffix,"true"));
            else
                termsFilterYYYY.addTerm(new Term(Config.S_HAS_YYYY + suffix,"true"));
            TermsFilter termsFilterYYYYMM = new TermsFilter();
            if(time_key)
                termsFilterYYYYMM.addTerm(new Term(Config.S_HAS_YYYYMM_KEY + suffix,"true"));
            else
                termsFilterYYYYMM.addTerm(new Term(Config.S_HAS_YYYYMM + suffix,"true"));
            TermsFilter termsFilterYYYYMMDD = new TermsFilter();
            if(time_key)
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD_KEY + suffix,"true"));
            else
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD + suffix,"true"));
            Filter[] filters = new Filter[]{termsFilterYYYY,termsFilterYYYYMM,termsFilterYYYYMMDD};
            filter = new SerialChainFilter(filters,actionType);
        }
        if(term.getValue().equals("year-month"))
        {
            int[] actionType = new int[2];
            actionType[0] = SerialChainFilter.OR;
            actionType[1] = SerialChainFilter.OR;
            TermsFilter termsFilterYYYYMM = new TermsFilter();
            if(time_key)
                termsFilterYYYYMM.addTerm(new Term(Config.S_HAS_YYYYMM_KEY + suffix,"true"));
            else
                termsFilterYYYYMM.addTerm(new Term(Config.S_HAS_YYYYMM + suffix,"true"));
            TermsFilter termsFilterYYYYMMDD = new TermsFilter();
            if(time_key)
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD_KEY + suffix,"true"));
            else
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD + suffix,"true"));
            Filter[] filters = new Filter[]{termsFilterYYYYMM,termsFilterYYYYMMDD};
            filter = new SerialChainFilter(filters,actionType);
        }
        if(term.getValue().equals("exact-date"))
        {
            TermsFilter termsFilterYYYYMMDD = new TermsFilter();
            if(time_key)
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD_KEY + suffix,"true"));
            else
                termsFilterYYYYMMDD.addTerm(new Term(Config.S_HAS_YYYYMMDD + suffix,"true"));
            filter = termsFilterYYYYMMDD;
        }
        else if(term.getValue().equals("any"))
        {
            TermsFilter termsFilter = new TermsFilter();
            termsFilter.addTerm(new Term(Config.S_HAS_TIMEXES + suffix,"true"));
            filter = termsFilter;
        }
        else
            logger.error("bad term filter:" + term.getField() + ":" + term.getValue());

        return filter;
    }


    private Filter getTimeFilter(Query.FilterChain.BooleanClause.Term term , String suffix)
    {
        try {
            String field = time_key ? Config.T_POINT_KEY + suffix:Config.T_TIME_EXPRESSIONS + suffix;
            return new QueryFilter(org.apache.lucene.queryParser.QueryParser.parse(term.getValue() + "*",field , new LgteNothingAnalyzer()));
        } catch (ParseException e) {
            logger.error(e,e);
        } catch (IOException e) {
            logger.error(e,e);
        }
        return null;
    }

    private Filter getPlaceFilter(Query.FilterChain.BooleanClause.Term term , String suffix)
    {
        int[] actionType = new int[term.getWoeid().size()];
        Filter[] filter = new Filter[term.getWoeid().size()];
        for(int i=0; i < filter.length;i++)
        {
            TermsFilter termsFilter = new TermsFilter();
            termsFilter.addTerm(new Term(Config.G_GEO_ALL_WOEID + suffix,term.getWoeid().get(i)));
            filter[i] = termsFilter;
            actionType[i] = SerialChainFilter.OR;
        }
        return new SerialChainFilter(filter, actionType);
    }

    private Filter getPlaceTypeFilter(Query.FilterChain.BooleanClause.Term term , String suffix)
    {
        TermsFilter termsFilter = new TermsFilter();
        String type=term.getValue();
        if(type.equalsIgnoreCase("country"))
        {
            termsFilter.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Country"));
            return termsFilter;
        }
        else if(type.equalsIgnoreCase("city"))
        {
            int[] actionType = new int[5];
            actionType[0] = SerialChainFilter.OR;
            actionType[1] = SerialChainFilter.OR;
            actionType[2] = SerialChainFilter.OR;
            actionType[4] = SerialChainFilter.OR;

            TermsFilter city = new TermsFilter();
            city.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"City"));

            TermsFilter town = new TermsFilter();
            town.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Town"));

            TermsFilter suburb = new TermsFilter();
            suburb.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Suburb"));

            TermsFilter historicalTown = new TermsFilter();
            historicalTown.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"HistoricalTown"));

            TermsFilter zone = new TermsFilter();
            zone.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Zone"));

            Filter[] filters = new Filter[]{city,town,suburb,historicalTown,zone};
            return new SerialChainFilter(filters,actionType);
        }
        else if(type.equalsIgnoreCase("province"))
        {
            int[] actionType = new int[8];
            actionType[0] = SerialChainFilter.OR;
            actionType[1] = SerialChainFilter.OR;
            actionType[2] = SerialChainFilter.OR;
            actionType[4] = SerialChainFilter.OR;
            actionType[5] = SerialChainFilter.OR;
            actionType[6] = SerialChainFilter.OR;
            actionType[7] = SerialChainFilter.OR;


            TermsFilter state = new TermsFilter();
            state.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"State"));

            TermsFilter estate = new TermsFilter();
            estate.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Estate"));

            TermsFilter historicalState = new TermsFilter();
            historicalState.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"HistoricalState"));

            TermsFilter county = new TermsFilter();
            county.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"County"));

            TermsFilter historicalCounty = new TermsFilter();
            historicalCounty.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"HistoricalCounty"));

            TermsFilter province = new TermsFilter();
            province.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Province"));

            TermsFilter zone = new TermsFilter();
            zone.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Zone"));

            TermsFilter island = new TermsFilter();
            island.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,"Island"));

            Filter[] filters = new Filter[]{state,estate, historicalState,county,historicalCounty,province,zone,island};
            return new SerialChainFilter(filters,actionType);
        }
        else
        {
            logger.error("Unkown type: " + type);
        }
        return null;
    }

}
