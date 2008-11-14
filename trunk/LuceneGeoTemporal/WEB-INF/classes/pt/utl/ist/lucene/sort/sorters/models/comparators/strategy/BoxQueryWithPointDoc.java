package pt.utl.ist.lucene.sort.sorters.models.comparators.strategy;

import pt.utl.ist.lucene.sort.sorters.models.comparators.ISpatialScoreStrategy;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.utils.GeoUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.ScoreDoc;
import org.apache.solr.util.NumberUtils;

/**
 * @author Jorge Machado
 * @date 14/Nov/2008
 * @see pt.utl.ist.lucene.sort.sorters.models.comparators.strategy
 */
public class BoxQueryWithPointDoc implements ISpatialScoreStrategy
{

    private static final Logger logger = Logger.getLogger(BoxQueryWithPointDoc.class);

    ISpatialDistancesWrapper iSpatialDistancesWrapper;
    String[] diagonalIndex;
    QueryParams queryParams;

    public BoxQueryWithPointDoc(){}

    public void init(Double biggerDiagonal,QueryParams queryParams, ISpatialDistancesWrapper iSpatialDistancesWrapper, String[] diagonalIndex, String[] radiumIndex)
    {
        this.queryParams = queryParams;
        this.iSpatialDistancesWrapper = iSpatialDistancesWrapper;
        this.diagonalIndex = diagonalIndex;
    }

    /**
     * Uses default formula of box query diagonal and distance from query centroide to document centroide
     *
     * @param scoreDoc
     * @return
     */
    public Comparable sortValue(ScoreDoc scoreDoc)
    {
        if(iSpatialDistancesWrapper == null || iSpatialDistancesWrapper.getSpaceDistances() == null)
            return 1f;

        double queryDiagonal = queryParams.getDiagonal();
        Double distance = iSpatialDistancesWrapper.getSpaceDistance(scoreDoc.doc);
        if(distance != null)
        {
            return ((Double) GeoUtils.distancePointAreaMapExpDDmbr(distance,queryDiagonal)).floatValue();
        }
        return 0f;
    }
}
