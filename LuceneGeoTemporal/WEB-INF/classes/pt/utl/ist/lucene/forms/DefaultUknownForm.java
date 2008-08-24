package pt.utl.ist.lucene.forms;

/**
 * @author Jorge Machado
 * @date 19/Ago/2008
 * @see pt.utl.ist.lucene.forms
 */
public class DefaultUknownForm implements UnknownForm
{
    GeoPoint geoPoint;
    double diagonal;


    public DefaultUknownForm(GeoPoint geoPoint, double diagonal)
    {
        this.geoPoint = geoPoint;
        this.diagonal = diagonal;
    }

    public GeoPoint getCentroide()
    {
        return geoPoint;
    }

    public double getWidth()
    {
        return diagonal;
    }
}
