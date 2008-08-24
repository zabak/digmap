package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.SortComparatorSource;

import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface SpatialDistancesSorterSource extends SortComparatorSource
{
    public void addSpaceDistancesWrapper(ISpatialDistancesWrapper iSpatialDistances);
    public ISpatialDistancesWrapper getSpaceDistancesWrapper();
}
