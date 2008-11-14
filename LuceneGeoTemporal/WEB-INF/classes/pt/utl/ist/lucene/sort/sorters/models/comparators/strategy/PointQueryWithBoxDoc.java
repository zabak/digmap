package pt.utl.ist.lucene.sort.sorters.models.comparators.strategy;

import org.apache.log4j.Logger;
import org.apache.lucene.search.ScoreDoc;
import org.apache.solr.util.NumberUtils;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.sort.sorters.models.comparators.ISpatialScoreStrategy;
import pt.utl.ist.lucene.utils.GeoUtils;

/**
 * @author Jorge Machado
 * @date 14/Nov/2008
 * @see pt.utl.ist.lucene.sort.sorters.models.comparators.strategy
 */
public class PointQueryWithBoxDoc implements ISpatialScoreStrategy
{

    private static final Logger logger = Logger.getLogger(PointQueryWithBoxDoc.class);

    ISpatialDistancesWrapper iSpatialDistancesWrapper;
    String[] diagonalIndex;
    QueryParams queryParams;

    public int radiumStrategy = ConfigProperties.getIntProperty("sigmoide.radium.strategy");
    public boolean useOnlyBoundaryBoxes = ConfigProperties.getBooleanProperty("sigmoide.use.only.boundary.boxes");

    public double alfa;
    public double alfa2;
    public double beta;

    Double biggerDiagonal;
    Double twiceBiggerDiagonal;
    Double halfBiggerDiagonal;

    static Double halfEarthRadium = 3000.0;


    public PointQueryWithBoxDoc(){}

    public void init(Double biggerDiagonal,QueryParams queryParams, ISpatialDistancesWrapper iSpatialDistancesWrapper, String[] diagonalIndex, String[] radiumIndex)
    {
        this.queryParams = queryParams;
        this.iSpatialDistancesWrapper = iSpatialDistancesWrapper;
        this.diagonalIndex = diagonalIndex;
        this.biggerDiagonal = biggerDiagonal;
        twiceBiggerDiagonal = 2*biggerDiagonal;
        halfBiggerDiagonal = biggerDiagonal / ((double )2);
        alfa = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa");
        beta = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.beta");
        alfa2 = queryParams.getQueryConfiguration().getDoubleProperty("sigmoide.distance.alfa.2");
    }

    /**
     * If document has Diagonal uses default formula
     * If document don't has diagonal but we have radium we use a Radium Based Sigmoid
     * If we don't have radium we use has radium half of the bigger document diagonal, or twice bigger, or just the bigger doc diagonal
     * Other wise we use half earth radium
     *
     * @param scoreDoc
     * @return
     */
    public Comparable sortValue(ScoreDoc scoreDoc)
    {
        if(iSpatialDistancesWrapper == null || iSpatialDistancesWrapper.getSpaceDistances() == null)
            return 1f;
        Double distance = iSpatialDistancesWrapper.getSpaceDistance(scoreDoc.doc);
        if(distance != null)
        {
            String diagonalStr = diagonalIndex[scoreDoc.doc];
            if(diagonalStr != null)
            {
                double diagonal = NumberUtils.SortableStr2double(diagonalStr);
                float areaScore = ((Double) GeoUtils.distancePointAreaMapExpDDmbr(distance,diagonal)).floatValue();
                if(!useOnlyBoundaryBoxes && queryParams.getRadium() > 0)
                {
                    float sigmoidScore = ((Double)GeoUtils.sigmoideDistanceRadium(distance,queryParams.getRadiumMiles(),alfa,beta)).floatValue();
                    return sigmoidScore > areaScore? sigmoidScore : areaScore;
                }
                return areaScore;
            }
            else if(diagonalStr == null && useOnlyBoundaryBoxes)
            {
                logger.error(">>>>>>>FATAL WARNING ------- Using only boundary boxes and diagonal comming NULL doc:" + scoreDoc.doc);
            }
            else if(queryParams.getRadium() > 0 && queryParams.getRadium() != Integer.MAX_VALUE)
            {
                return ((Double)GeoUtils.sigmoideDistanceRadium(distance,queryParams.getRadiumMiles(),alfa,beta)).floatValue();
            }
            else if(radiumStrategy == 1 && biggerDiagonal > 0)
            {
                return ((Double)GeoUtils.sigmoideDistanceRadium(distance,biggerDiagonal,alfa2,beta)).floatValue();
            }
            else if(radiumStrategy == 2 && halfBiggerDiagonal > 0)
            {
                return ((Double)GeoUtils.sigmoideDistanceRadium(distance,halfBiggerDiagonal,alfa2,beta)).floatValue();
            }
            else if(radiumStrategy == 3 && twiceBiggerDiagonal > 0)
            {
                return ((Double)GeoUtils.sigmoideDistanceRadium(distance,twiceBiggerDiagonal,alfa2,beta)).floatValue();
            }
            else
            {
                return ((Double)GeoUtils.sigmoideDistanceRadium(distance,halfEarthRadium,alfa2,beta)).floatValue();
            }
        }
        return 0f;
    }
}
