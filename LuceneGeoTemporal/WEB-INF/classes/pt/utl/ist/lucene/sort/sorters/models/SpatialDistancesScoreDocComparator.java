package pt.utl.ist.lucene.sort.sorters.models;

import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface SpatialDistancesScoreDocComparator extends LgteScoreDocComparator
{
    public void addSpaceDistancesWrapper(ISpatialDistancesWrapper iSpatialDistances);
    public ISpatialDistancesWrapper getSpaceDistancesWrapper();
}
