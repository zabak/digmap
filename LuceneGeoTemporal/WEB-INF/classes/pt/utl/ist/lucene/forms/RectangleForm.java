package pt.utl.ist.lucene.forms;

import com.pjaol.search.geo.utils.DistanceUtils;
import pt.utl.ist.lucene.utils.GeoUtils;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.forms
 */
public class RectangleForm implements UnknownForm
{
    private double north;
    private double south;
    private double east;
    private double west;
    private GeoPoint centroide;
    private double diagonal;

    public RectangleForm(double north, double west, double south, double east, GeoPoint geoPoint)
    {
        this.north = north;
        this.west = west;
        this.south = south;
        this.east = east;
        diagonal = DistanceUtils.getDistanceMi(south, west, north, east);
        centroide = geoPoint;
    }

    public RectangleForm(double north, double west, double south, double east)
    {
        this.north = north;
        this.west = west;
        this.south = south;
        this.east = east;
        diagonal = DistanceUtils.getDistanceMi(south, west, north, east);
        double middleLatitude = GeoUtils.calcMiddleLatitude(north, south);
        double middleLongitude = GeoUtils.calcMiddleLongitude(west, east);
        centroide = new GeoPoint(middleLatitude,middleLongitude);
    }


    public double getNorth()
    {
        return north;
    }

    public double getSouth()
    {
        return south;
    }

    public double getEast()
    {
        return east;
    }

    public double getWest()
    {
        return west;
    }

    public GeoPoint getCentroide()
    {
        return centroide;
    }

    public double getWidth()
    {
        return diagonal;
    }

    public String toString()
    {
        return "[north:" + north  + ";west:" + west + ";south:" + south + ";east:" + east +"]";
    }


    public boolean isOverlap(RectangleForm anotherRectangleForm)
    {
        return ((anotherRectangleForm.getEast() > west && anotherRectangleForm.getEast() < east) ||
                (anotherRectangleForm.getWest() > west && anotherRectangleForm.getWest() < east))
                &&
                ((anotherRectangleForm.getNorth() > south && anotherRectangleForm.getNorth() < north) ||
                        (anotherRectangleForm.getSouth() > south && anotherRectangleForm.getSouth() < north));
    }
    public boolean isContained(RectangleForm anotherRectangleForm)
    {
        return  anotherRectangleForm.getEast() <= east &&
                anotherRectangleForm.getWest() >= west &&
                anotherRectangleForm.getNorth() <= north &&
                anotherRectangleForm.getSouth() >= south;
    }
}
