package pt.utl.ist.lucene.sort;

import org.apache.lucene.search.SortComparatorSource;
import pt.utl.ist.lucene.level1query.QueryParams;

/**
 * @author Jorge Machado
 * @date 18/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface LgteScorer
{
    public float getScore(int doc, float score);
    public float getTimeScore(int doc, float score);
    public float getSpatialScore(int doc, float score);
    public float getTextScore(int doc, float score);
}
