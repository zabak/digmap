package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.index.IndexReader;

import pt.utl.ist.lucene.sort.sorters.models.TimeDistancesScoreDocComparator;
import pt.utl.ist.lucene.sort.sorters.models.SpatialDistancesScoreDocComparator;
import pt.utl.ist.lucene.sort.sorters.models.LgteScoreDocComparator;
import pt.utl.ist.lucene.level1query.QueryParams;

/**
 * @author Jorge Machado
 * @date 17/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface ModelSortDocComparator extends ScoreDocComparator, LgteScorer
{
    public void initModel(TimeDistancesScoreDocComparator time,
                          SpatialDistancesScoreDocComparator spatial,
                          LgteScoreDocComparator text,
                          QueryParams queryParams,
                          IndexReader reader);

    public void cleanUp();
}
