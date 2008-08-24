package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Filter;
import pt.utl.ist.lucene.filter.*;
import pt.utl.ist.lucene.filter.distancebuilders.SpatialDistanceBuilder;
import pt.utl.ist.lucene.filter.distancebuilders.TimeDistanceBuilder;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.utils.Dates;
import pt.utl.ist.lucene.utils.GeoUtils;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class FilterOrchestrator implements ITimeSpatialDistancesWrapper
{

    private static final Logger logger = Logger.getLogger(FilterOrchestrator.class);

    ITimeDistancesWrapper iTimeDistances = null;
    ISpatialDistancesWrapper iSpatialDistances = null;


    /**
     * Builds a SerialChain Filter Time and Space Filters
     *
     * @param query to filter
     * @return Searchable Filter
     */
    public Filter getFilter(LgteQuery query)
    {
        return getFilter(query,null);
    }

    /**
     * Builds a SerialChain Filter with user filter, Time and Space Filters
     *
     * @param query to filter
     * @param userFilter filters requested from user
     * @return Searchable Filter
     */
    public Filter getFilter(LgteQuery query, Filter userFilter )
    {
        SerialChainFilterBuilder serialChainFilterBuilder = new SerialChainFilterBuilder();

        QueryParams queryParams = query.getQueryParams();
        if(userFilter != null)
            serialChainFilterBuilder.andFilter(userFilter);
        buildTimeFilters(queryParams, serialChainFilterBuilder);
        buildSpaceFilters(queryParams,serialChainFilterBuilder);

        return serialChainFilterBuilder.getFilter();
    }

    public void buildSpaceFilters(QueryParams queryParams, SerialChainFilterBuilder serialChainFilterBuilder)
    {
        if(queryParams.isSpatial())
        {
            if(queryParams.getFilter().isSpace() && queryParams.isSpatialPoint())
            {
                if(!queryParams.isRadium())
                {
                    queryParams.setRadium(-1);
                }
                SpatialDistanceRadiumChainBuilder spatialDistanceRadiumChainBuilder = new SpatialDistanceRadiumChainBuilder(Globals.LUCENE_CENTROIDE_LATITUDE_INDEX,Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX,queryParams.getLatitude(),queryParams.getLongitude(),queryParams.getRadiumMiles());
                spatialDistanceRadiumChainBuilder.build(serialChainFilterBuilder);
                iSpatialDistances = spatialDistanceRadiumChainBuilder;
            }
            else if(queryParams.getFilter().isSpace() && queryParams.isSpatialBox())
            {
                SpatialDistanceBoxChainBuilder spatialDistanceBoxChainBuilder = new SpatialDistanceBoxChainBuilder(Globals.LUCENE_CENTROIDE_LATITUDE_INDEX, Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX, queryParams.getNorthlimit(),queryParams.getSouthlimit(), queryParams.getWestlimit(), queryParams.getEastlimit());
                queryParams.setLatitude("" + spatialDistanceBoxChainBuilder.getLat());
                queryParams.setLongitude("" + spatialDistanceBoxChainBuilder.getLng());
                queryParams.setRadiumMiles(spatialDistanceBoxChainBuilder.getMiles());
                spatialDistanceBoxChainBuilder.build(serialChainFilterBuilder);
                iSpatialDistances = spatialDistanceBoxChainBuilder;
            }
            //Even if we don't have spatial fields the user should choose to order in distance 
            else if(!queryParams.getFilter().isSpace() && queryParams.getOrder().isSpatial())
            {
                //in case of spatial box let's set the middle points to order
                setMiddlePoints(queryParams);
                SpatialDistanceBuilder spaceDistanceBuilder = new SpatialDistanceBuilder(Globals.LUCENE_CENTROIDE_LATITUDE_INDEX,Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX,queryParams.getLatitude(),queryParams.getLongitude());
                spaceDistanceBuilder.build(serialChainFilterBuilder);
                iSpatialDistances = spaceDistanceBuilder;
            }
            else
            {
                //do nothing there will be no distances wrapper
                //In sorting we need to check the same state
            }
        }
        else
        {
            logger.info("Space fields not found in user query, space filters not in use");
        }
    }
    public void buildTimeFilters(QueryParams queryParams, SerialChainFilterBuilder serialChainFilterBuilder)
    {

        if(queryParams.isTime())
        {
            if(queryParams.getFilter().isTime() && queryParams.isTimePoint())
            {
                if(!queryParams.isRadiumTime())
                    queryParams.setRadiumMiliseconds(-1);

                TimeRadiumChainBuilder timeIntervalChainBuilder;
                timeIntervalChainBuilder = new TimeRadiumChainBuilder(queryParams.getTimeMiliseconds(),queryParams.getRadiumMiliseconds(),Globals.LUCENE_TIME_INDEX);
                timeIntervalChainBuilder.build(serialChainFilterBuilder);
                iTimeDistances = timeIntervalChainBuilder;
            }
            else if(queryParams.getFilter().isTime() && queryParams.isDateInterval())
            {
                setMiddleDateTime(queryParams);
                TimeIntervalChainBuilder timeIntervalChainBuilder;
                timeIntervalChainBuilder = new TimeIntervalChainBuilder(queryParams.getStartTimeMiliseconds(),queryParams.getEndTimeMiliseconds(),Globals.LUCENE_TIME_INDEX);

                timeIntervalChainBuilder.build(serialChainFilterBuilder);
                iTimeDistances = timeIntervalChainBuilder;
            }
            else if(!queryParams.getFilter().isTime() && queryParams.getOrder().isTime())
            {
                setMiddleDateTime(queryParams);
                TimeDistanceBuilder timeDistanceBuilder = new TimeDistanceBuilder(queryParams.getTimeMiliseconds(),Globals.LUCENE_TIME_INDEX);
                timeDistanceBuilder.build(serialChainFilterBuilder);
                iTimeDistances = timeDistanceBuilder;
            }
            else
            {
                //do nothing
            }
        }
        else
        {
            logger.info("Time fields not found in user query, time filters not in use");
        }
    }

    private void setMiddlePoints(QueryParams queryParams)
    {
        if(queryParams.isSpatialBox())
        {
            double lat = GeoUtils.calcMiddleLatitude(queryParams.getNorthlimit(),queryParams.getSouthlimit());
            double lng = GeoUtils.calcMiddleLongitude(queryParams.getWestlimit(),queryParams.getEastlimit());
            queryParams.setLatitude(lat);
            queryParams.setLongitude(lng);
        }
    }
    
    private void setMiddleDateTime(QueryParams queryParams)
    {
        if(queryParams.isDateInterval())
        {
            long middleDate = Dates.getMiddleDate(queryParams.getStartTimeMiliseconds(),queryParams.getEndTimeMiliseconds());
            if(middleDate >=  0)
            {

                long radium = Math.abs(queryParams.getStartTimeMiliseconds() - queryParams.getEndTimeMiliseconds())/2;
                queryParams.setRadiumMiliseconds(radium);
                queryParams.setTimeMiliseconds(middleDate);
            }
        }
    }


    public Map<Integer, Long> getTimeDistances()
    {
        if(iTimeDistances == null)
            return null;
        return iTimeDistances.getTimeDistances();
    }

    public Map<Integer, Double> getSpaceDistances()
    {
        if(iSpatialDistances == null)
            return null;
        return iSpatialDistances.getSpaceDistances();
    }

    public Double getSpaceDistance(int doc)
    {
        if(iSpatialDistances == null)
            return null;
        return iSpatialDistances.getSpaceDistance(doc);
    }

    public Long getTimeDistance(int doc)
    {
        if(iTimeDistances == null)
            return null;
        return iTimeDistances.getTimeDistance(doc);
    }
}
