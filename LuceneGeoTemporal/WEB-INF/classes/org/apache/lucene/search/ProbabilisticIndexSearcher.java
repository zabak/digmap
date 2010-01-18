package org.apache.lucene.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Search using a probabilistic model.
 * The objective of this class is to provide capacity to invoque final scorer to sum of terms scores
 * <p/>
 * <p>inherits everything from IndexSearcher, except the
 * search() method which adds the language length prior.
 */
public abstract class ProbabilisticIndexSearcher extends IndexSearcher
{


    float log10 = (float) Math.log(10);


    /**
     * Creates a searcher searching the index in the named directory.
     */
    public ProbabilisticIndexSearcher(String path) throws IOException
    {
        super(path);
    }

    /**
     * Creates a searcher searching the index in the provided directory.
     */
    public ProbabilisticIndexSearcher(Directory directory) throws IOException
    {
        super(directory);
    }

    /**
     * Creates a searcher searching the provided index.
     */
    public ProbabilisticIndexSearcher(IndexReader r)
    {
        super(r);
    }
}
