package pt.utl.ist.lucene.forms;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.forms
 */
public class CircleForm implements UnknownForm
{

    private GeoPoint geoPoint;
    private double lat;
    private double lng;
    private double radium;


    public CircleForm(double lat, double lng, double radium)
    {
        geoPoint = new GeoPoint(lat,lng);
        this.lat = lat;
        this.lng = lng;
        this.radium = radium;
    }

    public double getLat()
    {
        return lat;
    }

    public double getRadium()
    {
        return radium;
    }

    public void setRadium(double radium)
    {
        this.radium = radium;
    }

    public double getLng()
    {
        return lng;
    }

    public GeoPoint getCentroide()
    {
        return geoPoint;
    }

    public double getWidth()
    {
        return radium*2;
    }
}
