package pt.utl.ist.lucene.utils.queries;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.placemaker.PlaceNameNormalizer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import com.pjaol.lucene.search.SerialChainFilter;
import pt.utl.ist.lucene.utils.queries.Query;

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
    boolean time_key = false;
    String placesQuery;
    String timesQuery;
    private Boolean wantPlaces = null;
    private Boolean wantTimes = null;


    public enum QueryTarget
    {
        CONTENTS,
        SENTENCES
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

    public boolean isTime_key() {
        return time_key;
    }

    public Query getQ() {
        return q;
    }

    public String getTermsQuery(QueryTarget queryTarget)
    {
        return prepareQueryString(queryTarget);
    }

    public String getPlacesQuery(QueryTarget queryTarget)
    {
        if(placesQuery == null)
            preparePlacesQueryString();
        if(placesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.G_GEO_ALL_WOEID + ":(" + placesQuery + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.G_GEO_ALL_WOEID + Config.SEP + Config.SENTENCES + ":(" + placesQuery + ")";
        }
        return null;
    }

    public String getPlacesRefQuery(QueryTarget queryTarget, String boost)
    {
        if(placesQuery == null)
            preparePlacesQueryString();
        if(placesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.G_PLACE_REF_WOEID + ":(" + placesQuery + ")^" +boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.G_PLACE_REF_WOEID + Config.SEP + Config.SENTENCES + ":(" + placesQuery + ")^" + boost;
        }
        return null;
    }

    public String getPlacesBeolongTosQuery(QueryTarget queryTarget, String boost)
    {
        if(placesQuery == null)
            preparePlacesQueryString();
        if(placesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.G_PLACE_BELONG_TOS_WOEID + ":(" + placesQuery + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.G_PLACE_BELONG_TOS_WOEID + Config.SEP + Config.SENTENCES + ":(" + placesQuery + ")^" + boost;
        }
        return null;
    }

    public String getTimesQueryKeyTimeExpressions(QueryTarget queryTarget, String boost)
    {
        if(timesQuery == null)
            prepareTimesQueryString();
        if(timesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT_KEY + ":(" + timesQuery + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT_KEY + Config.SEP + Config.SENTENCES + ":(" + timesQuery + ")^" + boost;
        }
        return null;
    }

    public String getTimesQueryPointTimeExpressions(QueryTarget queryTarget)
    {
        if(timesQuery == null)
            prepareTimesQueryString();
        if(timesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT + ":(" + timesQuery + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT + Config.SEP + Config.SENTENCES + ":(" + timesQuery + ")";
        }
        return null;
    }

    public String getTimesQueryTimeExpressions(QueryTarget queryTarget)
    {
        if(timesQuery == null)
            prepareTimesQueryString();
        if(timesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_TIME_EXPRESSIONS + ":(" + timesQuery + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_TIME_EXPRESSIONS + Config.SEP + Config.SENTENCES + ":(" + timesQuery + ")";
        }
        return null;
    }

    public String getTimesQueryRelativeTimeExpressions(QueryTarget queryTarget, String boost)
    {
        if(timesQuery == null)
            prepareTimesQueryString();
        if(timesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT_RELATIVE + ":(" + timesQuery + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT_RELATIVE + Config.SEP + Config.SENTENCES + ":(" + timesQuery + ")^" + boost;
        }
        return null;
    }

    public String getTimesQueryDurationsTimeExpressions(QueryTarget queryTarget, String boost)
    {
        if(timesQuery == null)
            prepareTimesQueryString();
        if(timesQuery.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_DURATION + ":(" + timesQuery + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_DURATION + Config.SEP + Config.SENTENCES + ":(" + timesQuery + ")^" + boost;
        }
        return null;
    }

    private String prepareQueryString(QueryTarget queryTarget)
    {
        if(queryTarget == QueryTarget.CONTENTS)
            return Config.CONTENTS + ":(" + q.getOriginalDescClean() + " " + q.getOriginalDescClean() + " " + q.getOriginalNarrClean() + ")";
        else if(queryTarget == QueryTarget.SENTENCES)
            return Config.SENTENCES + ":(" + q.getOriginalDescClean() + " " + q.getOriginalDescClean() + " " + q.getOriginalNarrClean() + ")";
        else
        {
            return
                    Config.SENTENCES + ":(" + q.getOriginalDescClean() + " " + q.getOriginalDescClean() + " " + q.getOriginalNarrClean() + ")^" + Config.combSentencesFactor + " " +
                            Config.CONTENTS + ":(" + q.getOriginalDescClean() + " " + q.getOriginalDescClean() + " " + q.getOriginalNarrClean() + ")^" + Config.combContentsFactor;
        }
    }

    private void preparePlacesQueryString()
    {
        wantPlaces = false;
        if(q.getPlaces().getTerms().size()>0)
        {
            StringBuilder places = new StringBuilder();
            for(Query.Places.Term place: q.getPlaces().getTerms())
            {
                if(!place.getPlace().equals("?"))
                {
                    for(String woeid: place.getWoeid())
                        places.append(PlaceNameNormalizer.normalizeWoeid(woeid)).append(" ");
                }
                else
                    wantPlaces = true;
            }
            placesQuery = places.toString().trim();
        }
        else placesQuery = "";

    }

    public void prepareTimesQueryString()
    {
        wantTimes = false;
        if(q.getTimes().getTerms().size()>0)
        {
            StringBuilder times = new StringBuilder();
            for(Query.Times.Term time: q.getTimes().getTerms())
            {
                if(!time.getTime().equals("?"))
                {
                    times.append(time.getTime()).append("* ");
                }
                else
                    wantTimes = true;
            }
            timesQuery = times.toString().trim();
        }else timesQuery = "";

    }

    public boolean wantPlaces()
    {
        if(wantPlaces == null)
            preparePlacesQueryString();
        return wantPlaces;
    }
    public boolean wantTimes() {
        if(wantTimes == null)
            prepareTimesQueryString();
        return wantTimes;
    }

    public Filter getFilters(QueryTarget queryTarget)
    {
        return prepareFilters(queryTarget);
    }


    /*****************************
     * PLACE QUERIES BUILT WITH FILTERS TERMS
     *
     * @param queryTarget
     * @return
     */

    public String getPlaceFiltersAsQueries(QueryTarget queryTarget)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildPlaceFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder);
            if(queryBuilder.toString() == null || queryBuilder.toString().trim().length() == 0)
                return null;
            if(queryTarget == QueryTarget.CONTENTS)
            {
                return Config.G_GEO_ALL_WOEID + ":(" + queryBuilder.toString() + ")";
            }
            else if(queryTarget == QueryTarget.SENTENCES)
            {
                return Config.G_GEO_ALL_WOEID + Config.SEP + Config.SENTENCES + ":(" + queryBuilder.toString() + ")";
            }
        }
        return null;
    }

    public String getPlaceRefsFiltersAsQueries(QueryTarget queryTarget, String boost)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildPlaceFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder);
            if(queryBuilder.toString() == null || queryBuilder.toString().trim().length() == 0)
                return null;
            if(queryTarget == QueryTarget.CONTENTS)
            {
                return Config.G_PLACE_REF_WOEID + ":(" + queryBuilder.toString() + ")^" + boost;
            }
            else if(queryTarget == QueryTarget.SENTENCES)
            {
                return Config.G_PLACE_REF_WOEID + Config.SEP + Config.SENTENCES + ":(" + queryBuilder.toString() + ")^" + boost;
            }
        }
        return null;
    }

    public String getPlaceFiltersAsQueriesBelongTos(QueryTarget queryTarget, String boost)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildPlaceFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder);
            if(queryBuilder.toString() == null || queryBuilder.toString().trim().length() == 0)
                return null;
            if(queryTarget == QueryTarget.CONTENTS)
            {
                return Config.G_PLACE_BELONG_TOS_WOEID + ":(" + queryBuilder.toString() + ")^" + boost;
            }
            else if(queryTarget == QueryTarget.SENTENCES)
            {
                return Config.G_PLACE_BELONG_TOS_WOEID + Config.SEP + Config.SENTENCES + ":(" + queryBuilder.toString() + ")^" + boost;
            }
        }
        return null;
    }

    private void buildPlaceFiltersAsQueries(Query.FilterChain.BooleanTerm booleanTerm, StringBuilder query)
    {
        if(booleanTerm instanceof Query.FilterChain.BooleanClause.Term && ((Query.FilterChain.BooleanClause.Term)booleanTerm).getField().equals("place"))
        {
            for(String woeid: ((Query.FilterChain.BooleanClause.Term)booleanTerm).getWoeid())
            {
                query.append(" ").append(PlaceNameNormalizer.normalizeWoeid(woeid));
            }
        }
        else if(booleanTerm instanceof Query.FilterChain.BooleanClause)
        {
            for(Query.FilterChain.BooleanTerm term: ((Query.FilterChain.BooleanClause)booleanTerm).getTerms())
            {
                buildPlaceFiltersAsQueries(term,query);
            }
        }
    }

    /****
     * PLACE TYPE
     * @param queryTarget
     * @return
     */
    public String getPlaceTypeFiltersAsQueries(QueryTarget queryTarget)
    {
        String queryTerms = getPlaceTypeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.G_GEO_PLACE_TYPE + ":(" + queryTerms + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.G_GEO_PLACE_TYPE + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")";
        }
        return null;
    }

    private String getPlaceTypeFiltersAsQueries()
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            getPlaceTypeFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder);
            return queryBuilder.toString();
        }
        return null;
    }

    private void getPlaceTypeFiltersAsQueries(Query.FilterChain.BooleanTerm booleanTerm, StringBuilder query)
    {
        if(booleanTerm instanceof Query.FilterChain.BooleanClause.Term && ((Query.FilterChain.BooleanClause.Term)booleanTerm).getField().equals("placeType"))
        {
            String [] mapped = mapPlaceType(((Query.FilterChain.BooleanClause.Term)booleanTerm).getValue());
            for(String s: mapped)
                query.append(" ").append(s);
        }
        else if(booleanTerm instanceof Query.FilterChain.BooleanClause)
        {
            for(Query.FilterChain.BooleanTerm term: ((Query.FilterChain.BooleanClause)booleanTerm).getTerms())
            {
                getPlaceTypeFiltersAsQueries(term,query);
            }
        }
    }


    /********************************************
     * TIME QUERIES BUILT WITH FILTERS TERMS
     * @param queryTarget
     * @return
     */


    public String getTimeFiltersAsQueries(QueryTarget queryTarget)
    {
        String queryTerms = getTimeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_TIME_EXPRESSIONS + ":(" + queryTerms + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_TIME_EXPRESSIONS + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")";
        }
        return null;
    }
    public String getTimePointsFiltersAsQueries(QueryTarget queryTarget)
    {
        String queryTerms = getTimeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT + ":(" + queryTerms + ")";
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")";
        }
        return null;
    }
    public String getTimeKeyPointsFiltersAsQueries(QueryTarget queryTarget, String boost)
    {
        String queryTerms = getTimeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT_KEY + ":(" + queryTerms + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT_KEY + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")^" + boost;
        }
        return null;
    }
    public String getTimeRelativePointsFiltersAsQueries(QueryTarget queryTarget, String boost)
    {
        String queryTerms = getTimeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_POINT_RELATIVE + ":(" + queryTerms + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_POINT_RELATIVE + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")^" + boost;
        }
        return null;
    }
    public String getTimeDurationPointsFiltersAsQueries(QueryTarget queryTarget, String boost)
    {
        String queryTerms = getTimeFiltersAsQueries();
        if(queryTerms == null || queryTerms.trim().length() == 0)
            return null;
        if(queryTarget == QueryTarget.CONTENTS)
        {
            return Config.T_DURATION + ":(" + queryTerms + ")^" + boost;
        }
        else if(queryTarget == QueryTarget.SENTENCES)
        {
            return Config.T_DURATION + Config.SEP + Config.SENTENCES + ":(" + queryTerms + ")^" + boost;
        }
        return null;
    }

    private String getTimeFiltersAsQueries()
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildTimeFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder);
            return queryBuilder.toString();
        }
        return null;
    }

    private void buildTimeFiltersAsQueries(Query.FilterChain.BooleanTerm booleanTerm, StringBuilder query)
    {
        if(booleanTerm instanceof Query.FilterChain.BooleanClause.Term && ((Query.FilterChain.BooleanClause.Term)booleanTerm).getField().equals("time"))
        {
            query.append(" ").append(((Query.FilterChain.BooleanClause.Term)booleanTerm).getValue() + "*");
        }
        else if(booleanTerm instanceof Query.FilterChain.BooleanClause)
        {
            for(Query.FilterChain.BooleanTerm term: ((Query.FilterChain.BooleanClause)booleanTerm).getTerms())
            {
                buildTimeFiltersAsQueries(term,query);
            }
        }
    }


    /**
     * TimeTYPE
     * @param queryTarget
     * @return
     */

    public String getTimeTypeFiltersAsQueries(QueryTarget queryTarget)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildTimeTypeFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder,queryTarget,false);
            if(queryBuilder.toString().trim().length() > 0)
                return queryBuilder.toString();
        }
        return null;
    }

    public String getTimeKeyTypeFiltersAsQueries(QueryTarget queryTarget)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            StringBuilder queryBuilder = new StringBuilder();
            buildTimeTypeFiltersAsQueries(q.getFilterChain().getBooleanClause(),queryBuilder,queryTarget,true);
            if(queryBuilder.toString().trim().length() > 0)
                return queryBuilder.toString();
        }
        return null;
    }

    private void buildTimeTypeFiltersAsQueries(Query.FilterChain.BooleanTerm booleanTerm, StringBuilder query,QueryTarget queryTarget, boolean key)
    {
        if(booleanTerm instanceof Query.FilterChain.BooleanClause.Term && ((Query.FilterChain.BooleanClause.Term)booleanTerm).getField().equals("timeType"))
        {
            String value = ((Query.FilterChain.BooleanClause.Term)booleanTerm).getValue();
            String suffix = queryTarget == QueryTarget.SENTENCES? Config.SEP + Config.SENTENCES : "";
            if(value.equals("any"))
            {
                query.append(" ").append(Config.S_HAS_TIMEXES).append(suffix).append(":").append("true").append("");
            }
            else
            {
                if(key)
                {
                    query.append(" ").append(Config.S_HAS_YYYY_KEY).append(suffix).append(":").append("true").append("");
                    if(!value.equals("year"))
                        query.append(" ").append(Config.S_HAS_YYYYMM_KEY).append(suffix).append(":").append("true").append("");
                    if(!value.equals("year-month"))
                        query.append(" ").append(Config.S_HAS_YYYYMMDD_KEY).append(suffix).append(":").append("true").append("");
                }
                else
                {

                    query.append(" ").append(Config.S_HAS_YYYY).append(suffix).append(":").append("true").append("");
                    if(!value.equals("year"))
                        query.append(" ").append(Config.S_HAS_YYYYMM).append(suffix).append(":").append("true").append("");
                    if(!value.equals("year-month"))
                        query.append(" ").append(Config.S_HAS_YYYYMMDD).append(suffix).append(":").append("true").append("");
                }
            }
        }
        else if(booleanTerm instanceof Query.FilterChain.BooleanClause)
        {
            for(Query.FilterChain.BooleanTerm term: ((Query.FilterChain.BooleanClause)booleanTerm).getTerms())
            {
                buildTimeTypeFiltersAsQueries(term,query,queryTarget,key);
            }
        }
    }

    /**
     * @param queryTarget
     * @return
     */
    private Filter prepareFilters(QueryTarget queryTarget)
    {
        if(q.getFilterChain().getBooleanClause().getTerms().size() > 0)
        {
            return createFilter(q.getFilterChain().getBooleanClause(),queryTarget);
        }
        return null;
    }

    private Filter createFilter(Query.FilterChain.BooleanTerm booleanTerm, QueryTarget queryTarget)
    {
        if(booleanTerm instanceof Query.FilterChain.BooleanClause.Term)
            return createTermFilter(((Query.FilterChain.BooleanClause.Term)booleanTerm),queryTarget);
        else
            return createBooleanFilter(((Query.FilterChain.BooleanClause)booleanTerm),queryTarget);
    }

    private Filter createBooleanFilter(Query.FilterChain.BooleanClause booleanClause, QueryTarget queryTarget)
    {
        Filter[] filters = new Filter[booleanClause.getTerms().size()];
        int[] actionType = new int[booleanClause.getTerms().size()];
        int action = booleanClause.getLogicValue() == Query.FilterChain.BooleanClause.LogicValue.AND ? SerialChainFilter.AND: SerialChainFilter.OR;
        for(int i = 0; i < actionType.length; i++)
            actionType[i] = action;

        for(int i = 0; i < filters.length; i++)
        {
            Query.FilterChain.BooleanTerm booleanTerm = booleanClause.getTerms().get(i);
            filters[i] = createFilter(booleanTerm, queryTarget);
        }
        return new SerialChainFilter(filters,actionType);
    }

    private Filter createTermFilter(Query.FilterChain.BooleanClause.Term term, QueryTarget queryTarget)
    {
        String suffix = queryTarget == QueryTarget.SENTENCES ? Config.SEP + Config.SENTENCES : "";

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
        else if(term.getValue().equals("year-month"))
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
        else if(term.getValue().equals("exact-date"))
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
            termsFilter.addTerm(new Term(Config.G_GEO_ALL_WOEID + suffix, PlaceNameNormalizer.normalizeWoeid(term.getWoeid().get(i))));
            filter[i] = termsFilter;
            actionType[i] = SerialChainFilter.OR;
        }
        return new SerialChainFilter(filter, actionType);
    }


    private String[] mapPlaceType(String type)
    {
        final String[] state = { "State","Estate","HistoricalState","County","HistoricalCounty","Province","Zone","Island"};
        final String[] city = { "City","Town","Suburb","HistoricalTown","Zone"};
        final String[] country = { "Country"};
        if(type.equalsIgnoreCase("province") || type.equalsIgnoreCase("state"))
            return state;
        else if(type.equalsIgnoreCase("city"))
            return city;
        else if(type.equalsIgnoreCase("country"))
            return country;
        else
            logger.error("ATENTION UNESPECTED PLACE TYPE: " + type);
        return null;
    }

    private Filter getPlaceTypeFilter(Query.FilterChain.BooleanClause.Term term , String suffix)
    {
        String type=term.getValue();
        String [] map = mapPlaceType(type);
        int[] actionType = new int[map.length];
        Filter[] filter = new Filter[map.length];
        for(int i=0;i< map.length;i++)
        {
            actionType[i]=SerialChainFilter.OR;
            TermsFilter termFilter = new TermsFilter();
            termFilter.addTerm(new Term(Config.G_GEO_PLACE_TYPE + suffix,map[i]));
            filter[i] = termFilter;
        }
        return new SerialChainFilter(filter,actionType);
    }

}
