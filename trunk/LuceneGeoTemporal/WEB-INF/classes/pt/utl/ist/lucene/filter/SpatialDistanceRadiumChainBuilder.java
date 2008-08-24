package pt.utl.ist.lucene.filter;

import com.pjaol.search.geo.utils.BoundaryBoxFilter;
import com.pjaol.search.geo.utils.DistanceFilter;
import com.pjaol.search.geo.utils.DistanceUtils;
import org.apache.solr.util.NumberUtils;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import pt.utl.ist.lucene.filter.distancebuilders.GetAllDocumentsFilter;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class SpatialDistanceRadiumChainBuilder implements ISpatialDistancesWrapper
{
    double lat;
    double lng;
    private BoundaryBoxFilter latFilter;
    private BoundaryBoxFilter lngFilter;
    private DistanceFilter distanceFilter;


    public SpatialDistanceRadiumChainBuilder(String latField, String lngField, double lat, double lng, double miles)
    {
        this.lat = lat;
        this.lng = lng;
        if(miles >= 0)
        {
            Rectangle2D box = DistanceUtils.getBoundary(lat, lng, miles);
            latFilter = new BoundaryBoxFilter(latField, NumberUtils.double2sortableStr(box.getY()), NumberUtils.double2sortableStr(box.getMaxY()),
                    true, true);
            lngFilter = new BoundaryBoxFilter(lngField, NumberUtils.double2sortableStr(box.getX()), NumberUtils.double2sortableStr(box.getMaxX()),
                    true, true);
        }
        else
        {
            latFilter = null;
            lngFilter = null;
        }
        distanceFilter = new DistanceFilter(lat, lng, miles, latField, lngField);
    }

    public void build(SerialChainFilterBuilder builder)
    {
        if(latFilter != null && lngFilter != null)
        {
            builder.andFilter(latFilter);
            builder.andFilter(lngFilter);
        }
        else
            builder.andFilter(new GetAllDocumentsFilter());
        builder.serialAndFilter(distanceFilter);
    }


    public BoundaryBoxFilter getLatFilter()
    {
        return latFilter;
    }

    public BoundaryBoxFilter getLngFilter()
    {
        return lngFilter;
    }

    public DistanceFilter getDistanceFilter()
    {
        return distanceFilter;
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
