package com.pjaol.search.geo.utils;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.solr.util.NumberUtils;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.pjaol.lucene.search.SerialChainFilter;

public class DistanceAndConfigurableBoxQueryQuery
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BoundaryBoxFilter latFilter;
    public BoundaryBoxFilter lngFilter;
    public DistanceFilter distanceFilter;
    public List<BoundaryBoxFilter> otherFilters;

    private double lat;
    private double lng;
    private double miles;
    private String latField;
    private String lngField;

    /**
     * Create a distance level1query using
     * a boundary box wrapper around a more precise
     * DistanceFilter.
     *
     * @see com.pjaol.lucene.search.SerialChainFilter
     * @param lat
     * @param lng
     * @param miles
     */
    public DistanceAndConfigurableBoxQueryQuery(double lat, double lng, double miles, String latField, String lngField, List<BoundaryBoxFilter> otherFilters){

        this.lat = lat;
        this.lng = lng;
        this.miles = miles;
        this.latField = latField;
        this.lngField = lngField;

        /* create boundary box filter */
        Rectangle2D box = DistanceUtils.getBoundary(lat, lng, miles);


        latFilter = new BoundaryBoxFilter(latField, NumberUtils.double2sortableStr(box.getY()), NumberUtils.double2sortableStr(box.getMaxY()),
                true, true);
        lngFilter = new BoundaryBoxFilter(lngField, NumberUtils.double2sortableStr(box.getX()), NumberUtils.double2sortableStr(box.getMaxX()),
                true, true);

        /* create precise distance filter */
        distanceFilter = new DistanceFilter(lat, lng, miles, latField, lngField);
    }

    public Filter getFilter()
    {
        return getFilter((List<BoundaryBoxFilter>) null);
    }
    /**
     * Create a distance level1query using
     * a boundary box wrapper around a more precise
     * DistanceFilter.
     *
     * @see com.pjaol.lucene.search.SerialChainFilter
     *
     * @author Jorge Machado
     */
    public Filter getFilter(List<BoundaryBoxFilter> boundaryBoxFilters)
    {

        Filter[] filters;
        int[] serialOps;
        int i = 0;
        if(boundaryBoxFilters != null)
        {
            filters = new Filter[boundaryBoxFilters.size() + 3];
            serialOps = new int[boundaryBoxFilters.size() + 3];
            for(BoundaryBoxFilter boundaryBoxFilter: boundaryBoxFilters)
            {
                filters[i] = boundaryBoxFilter;
                serialOps[i] = SerialChainFilter.AND;
                i++;
            }
        }
        else
        {
            filters = new Filter[3];
            serialOps = new int[3];
        }
        filters[i+1] = latFilter;
        serialOps[i] = SerialChainFilter.AND;
        filters[i+1] = lngFilter;
        serialOps[i] = SerialChainFilter.AND;
        filters[i+1] = distanceFilter;
        serialOps[i] = SerialChainFilter.SERIALAND;
        return new SerialChainFilter(filters,serialOps);

    }

    public Filter getFilter(Query query) {
        QueryWrapperFilter qf = new QueryWrapperFilter(query);


        return new SerialChainFilter(new Filter[] {latFilter, lngFilter, qf, distanceFilter},
                new int[] {SerialChainFilter.AND,
                        SerialChainFilter.AND,
                        SerialChainFilter.AND,
                        SerialChainFilter.SERIALAND});
    }

    public Query getQuery() {
        return new ConstantScoreQuery(getFilter());
    }

    public String toString() {
        return "DistanceQuery lat: " + lat + " lng: " + lng + " miles: "+ miles;
    }
}
