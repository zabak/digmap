package pt.utl.ist.lucene.filter.distancebuilders;

import java.util.Map;

import pt.utl.ist.lucene.filter.distancebuilders.SpaceDistanceBuilderFilter;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.filter.SerialChainFilterBuilder;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class SpatialDistanceBuilder implements ISpatialDistancesWrapper
{
    public SpaceDistanceBuilderFilter spaceDistanceFilter;


    public SpatialDistanceBuilder(String latField, String lngField, double lat, double lng)
    {
        spaceDistanceFilter = new SpaceDistanceBuilderFilter(lat, lng, latField, lngField);
    }

    public void build(SerialChainFilterBuilder builder)
    {
        builder.andFilter(spaceDistanceFilter);
    }

    public Map<Integer, Double> getSpaceDistances()
    {
        return spaceDistanceFilter.getDistances();
    }

    public Double getSpaceDistance(int doc)
    {
        return spaceDistanceFilter.getDistance(doc);
    }
}
