package pt.utl.ist.lucene.sort.sorters.models;

import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.index.IndexReader;
import pt.utl.ist.lucene.level1query.QueryParams;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene.sort
 */
public interface LgteScoreDocComparator extends ScoreDocComparator
{
    public void cleanUp();
    public void addQueryParams(QueryParams queryParams);
    public void init(IndexReader reader) throws IOException;

}
