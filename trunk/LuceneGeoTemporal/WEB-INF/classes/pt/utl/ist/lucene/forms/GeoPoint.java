package pt.utl.ist.lucene.forms;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.forms
 */
public class GeoPoint
{
    private double lat;
    private double lng;


    public GeoPoint(double lat, double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }


    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }
}
