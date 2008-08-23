package pt.utl.ist.lucene.filter;

import com.pjaol.search.geo.utils.BoundaryBoxFilter;
import com.pjaol.search.geo.utils.DistanceFilter;
import com.pjaol.search.geo.utils.DistanceUtils;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.utils.GeoUtils;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class SpatialDistanceBoxChainBuilder implements ISpatialDistancesWrapper
{
    public BoundaryBoxFilter latFilter;
    public BoundaryBoxFilter lngFilter;
    public DistanceFilter distanceFilter;

    private double lat;
    private double lng;
    private double miles;

    public SpatialDistanceBoxChainBuilder(String latField, String lngField, double northlimit, double southlimit, double westlimit, double eastlimit)
    {
        latFilter = new BoundaryBoxFilter(latField, NumberUtils.double2sortableStr(southlimit), NumberUtils.double2sortableStr(northlimit),
                true, true);
        lngFilter = new BoundaryBoxFilter(lngField, NumberUtils.double2sortableStr(westlimit),NumberUtils.double2sortableStr(eastlimit),
                true, true);

        this.lat = GeoUtils.calcMiddleLatitude(northlimit,southlimit);
        this.lng = GeoUtils.calcMiddleLongitude(westlimit,eastlimit);
//        double lngDistance = DistanceUtils.getDistanceMi(westlimit,northlimit,eastlimit,northlimit);
//        double latDistance = DistanceUtils.getDistanceMi(westlimit, northlimit,westlimit,southlimit);
//
        double diagonal = DistanceUtils.getDistanceMi(southlimit, westlimit, northlimit, eastlimit);
        miles = diagonal/2;
        distanceFilter = new DistanceFilter(lat, lng, miles, latField, lngField);
    }

    public void build(SerialChainFilterBuilder builder)
    {
        builder.andFilter(latFilter);
        builder.andFilter(lngFilter);
        builder.serialAndFilter(distanceFilter);
    }

    public BoundaryBoxFilter getLatFilter()
    {
        return latFilter;
    }

    public void setLatFilter(BoundaryBoxFilter latFilter)
    {
        this.latFilter = latFilter;
    }

    public BoundaryBoxFilter getLngFilter()
    {
        return lngFilter;
    }

    public void setLngFilter(BoundaryBoxFilter lngFilter)
    {
        this.lngFilter = lngFilter;
    }


    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public double getMiles()
    {
        return miles;
    }

    public DistanceFilter getDistanceFilter()
    {
        return distanceFilter;
    }

    public Map<Integer, Double> getDistances()
    {
        return distanceFilter.getDistances();
    }

    public Map<Integer, Double> getSpaceDistances()
    {
        return distanceFilter.getDistances();
    }

    public Double getSpaceDistance(int doc)
    {
        return distanceFilter.getDistance(doc);
    }
}
