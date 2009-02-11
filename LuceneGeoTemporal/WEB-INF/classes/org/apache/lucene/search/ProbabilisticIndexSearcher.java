package org.apache.lucene.search;

import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.BitSet;

import pt.utl.ist.lucene.ModelManager;

/**
 * Search using a probabilistic model.
 * The objective of thias class is to provide capacity to invoque final scorer to sum of terms scores
 * <p/>
 * <p>inherits everything from IndexSearcher, except the
 * search() method which adds the language length prior.
 */
public abstract class ProbabilisticIndexSearcher extends IndexSearcher
{


    float log10 = (float) Math.log(10);
    LanguageModelIndexReader lmIndexReader = null;
    boolean extendedDataRead = false;


    /**
     * Creates a searcher searching the index in the named directory.
     */
    public ProbabilisticIndexSearcher(String path) throws IOException
    {
        super(path);
        if (lmIndexReader == null)
        {
            lmIndexReader = new LanguageModelIndexReader(reader);
        }
    }

    /**
     * Creates a searcher searching the index in the provided directory.
     */
    public ProbabilisticIndexSearcher(Directory directory) throws IOException
    {
        super(directory);
        if (lmIndexReader == null)
        {
            lmIndexReader = new LanguageModelIndexReader(reader);
        }
    }

    /**
     * Creates a searcher searching the provided index.
     */
    public ProbabilisticIndexSearcher(IndexReader r)
    {
        super(r);
        if (lmIndexReader == null)
        {
            lmIndexReader = new LanguageModelIndexReader(reader);
        }

        System.err.println("");

    }

    /**
     * @param directory path to index directory
     */
    public void storeExtendedData(String directory)
    {
        lmIndexReader.storeExtendedData(directory);
    }

    /**
     */
    public LanguageModelIndexReader getLangModelReader()
    {
        return lmIndexReader;
    }

    /**
     * @param directory path to index directory
     */
    public void readExtendedDate(String directory)
    {
        if (!extendedDataRead)
        {
            lmIndexReader.readExtendedData(directory);
            extendedDataRead = true;
        }
    }

}
