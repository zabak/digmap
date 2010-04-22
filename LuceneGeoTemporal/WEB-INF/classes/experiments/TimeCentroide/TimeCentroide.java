package experiments.TimeCentroide;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.MultiPoint;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 21:44:00
 * @email machadofisher@gmail.com
 */
public class TimeCentroide
{
    public static void main(String[] args)
    {
        GeometryFactory fact = new GeometryFactory();


            MultiPoint multiPoint = fact.createMultiPoint(new Point[]{
                    fact.createPoint(new Coordinate(1990, 0)),
                    fact.createPoint(new Coordinate(1990, 0)),
                    fact.createPoint(new Coordinate(1990, 0)),
                    fact.createPoint(new Coordinate(1991, 0)),
                    fact.createPoint(new Coordinate(2000, 0)),
                    fact.createPoint(new Coordinate(2000, 0)),
                    fact.createPoint(new Coordinate(2000, 0)),
                    fact.createPoint(new Coordinate(2001, 0)),
                    fact.createPoint(new Coordinate(2001, 0))
                    
            });
        System.out.println("x="+multiPoint.getCentroid().getX());
        System.out.println("y="+multiPoint.getCentroid().getY());

    }
}
