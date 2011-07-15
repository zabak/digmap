package pt.utl.ist.lucene.utils;

/**
 * @author Jorge Machado
 * @date 25/Jul/2008
 * @see pt.utl.ist.lucene.utils
 */
public class GeoUtils
{
    public static double calcMiddleLatitude(double northlimit, double southlimit)
    {
        return (northlimit + southlimit) / 2.0;
    }
    public static double calcMiddleLatitude(String northlimit, String southlimit)
    {
        double north = Double.parseDouble(northlimit);
        double south = Double.parseDouble(southlimit);
        return GeoUtils.calcMiddleLatitude(north,south);
    }

    public static double calcMiddleLongitude(String westLimit, String eastLimit)
    {
        double west = Double.parseDouble(westLimit);
        double east = Double.parseDouble(eastLimit);
        return GeoUtils.calcMiddleLongitude(west, east);
    }

    public static double calcMiddleLongitude(double west, double east)
    {
        if(west <= east)
            return west - ((west - east) / 2.0);
        else
        {
            double eastDistance_2_180 = 180.0 - east;
            double westDistance_2_n180 = 180.0 + west;
            double average = (eastDistance_2_180 + westDistance_2_n180) / 2.0;
            double middle;
            if(westDistance_2_n180 > eastDistance_2_180)
            {
                middle = west - average;
            }
            else
            {
                middle = east + average;
            }
            return middle;
        }
    }

    public static double distancePointAreaMapExpDDmbr(double distanceCentroides, double distanceDiagonal)
    {
        double sub = distanceCentroides - distanceDiagonal;
        double halfDmbr = distanceDiagonal * 0.5;
        double sign = Math.signum(sub);
        double exp = Math.exp(-(Math.pow(sub/halfDmbr,2)));
        return 1.0 - ((1.0+sign*(1-exp))/2);
    }

    /**
     * radium = 10
       alfa = 0.75
       beta = 3
     * 1 / (1 + (x/10 * (0.75 * euler))^3)
     * @param distance
     * @param radium
     * @param alfa
     * @param beta
     * @return
     */
    public static double sigmoideDistanceRadium(double distance, double radium, double alfa, double beta)
    {
        double distanceRadium = distance / radium;
        distanceRadium *= alfa * Math.E;
        double exp = Math.pow(distanceRadium,beta);
        return 1.0 / (1.0 + exp);
    }



}
