package pt.utl.ist.lucene.sort.sorters.models.comparators;

import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import org.apache.lucene.search.ScoreDoc;

/**
 * @author Jorge Machado
 * @date 14/Nov/2008
 * @see pt.utl.ist.lucene.sort.sorters.models.comparators
 */
public interface ISpatialScoreStrategy
{
    public Comparable sortValue(ScoreDoc scoreDoc);
    public void init(Double biggerDiagonal, QueryParams queryParams, ISpatialDistancesWrapper iSpatialDistancesWrapper, String[] diagonalIndex,String[] radiumIndex);

}
