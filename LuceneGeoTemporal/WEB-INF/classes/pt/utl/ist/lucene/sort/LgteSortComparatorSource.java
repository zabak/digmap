package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.SortComparatorSource;
import pt.utl.ist.lucene.level1query.QueryParams;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface LgteSortComparatorSource extends SortComparatorSource
{
   
    public void cleanUp();
    public void addQueryParams(QueryParams queryParams);
}
