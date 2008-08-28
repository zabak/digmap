package pt.utl.ist.lucene.sort.sorters.models;

import pt.utl.ist.lucene.sort.ModelSortDocComparator;
import pt.utl.ist.lucene.level1query.QueryParams;

import java.util.HashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;

/**
 * @author Jorge Machado
 * @date 17/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public class PiModelSortDocComparator extends DefaultModelSortDocComparator
{
    protected float merge(float time, float spatial, float text)
    {
        return (0.0001f + time) * (0.0001f + spatial) * (0.0001f + text);
    }
}
