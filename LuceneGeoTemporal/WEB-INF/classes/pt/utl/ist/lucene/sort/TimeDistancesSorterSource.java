package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.SortComparatorSource;

import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface TimeDistancesSorterSource extends LgteSortComparatorSource
{
    public void addTimeDistancesWrapper(ITimeDistancesWrapper iTimeDistances);
    public ITimeDistancesWrapper getTimeDistancesWrapper();
}
