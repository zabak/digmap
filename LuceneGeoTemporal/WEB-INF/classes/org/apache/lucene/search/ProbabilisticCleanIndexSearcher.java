package org.apache.lucene.search;

/**
 * @author Jorge Machado
 *
 */

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.store.Directory;
import pt.utl.ist.lucene.ModelManager;

import java.io.IOException;
import java.util.BitSet;

/**
 * Search using a probabilistic model.
 * The objective of thias class is to provide capacity to invoque final scorer to sum of terms scores
 * <p/>
 * <p>inherits everything from IndexSearcher, except the
 * search() method which adds the language length prior.
 */
public class ProbabilisticCleanIndexSearcher extends ProbabilisticIndexSearcher
{

    public ProbabilisticCleanIndexSearcher(String path)
            throws IOException
    {
        super(path);
    }

    public ProbabilisticCleanIndexSearcher(Directory directory)
            throws IOException
    {
        super(directory);
    }

    public ProbabilisticCleanIndexSearcher(IndexReader r)
    {
        super(r);
    }


    /**
     * New method to search and collect results
     * @param query
     * @param filter
     * @param nDocs
     * @return
     * @throws IOException
     */
    public TopDocs search(Query query, Filter filter, final int nDocs)
            throws IOException
    {

        Scorer scorer = query.weight(this).scorer(reader);
        if (scorer == null)
            return new TopDocs(0, new ScoreDoc[0]);

        final BitSet bits = filter != null ? filter.bits(reader) : null;
        final HitQueue hq = new HitQueue(nDocs);
        final int[] totalHits = new int[1];
        scorer.score(new HitCollector()
        {

            public final void collect(int doc, float score)
            {
                if (              // ignore zeroed buckets
                        (bits == null || bits.get(doc)))
                {
                    totalHits[0]++;
                    int docLen = getDocLength(doc);

                    if (docLen > 0)
                    {
                        score = ModelManager.getInstance().getModel().getDocumentFinalScorer().computeFinalScore(score, lmIndexReader,docLen);
                    }
                    else
                    {
                        System.err.println("Zero doc length for doc " + doc);
                    }
                    hq.insert(new ScoreDoc(doc, score));
                }
            }
        });

        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size() - 1; i >= 0; i--)      // put docs in array
            scoreDocs[i] = (ScoreDoc) hq.pop();

        return new TopDocs(totalHits[0], scoreDocs);
    }

    private int getDocLength(int doc)
    {
        int docLen = 0;
        try
        {
            docLen = lmIndexReader.getDocLength(doc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return docLen;
    }


    // inherit javadoc
    public void search(Query query, Filter filter,
                       final HitCollector results) throws IOException
    {
        HitCollector collector = results;
        if (filter != null)
        {
            final BitSet bits = filter.bits(reader);
            collector = new HitCollector()
            {
                public final void collect(int doc, float score)
                {
                    if (bits.get(doc))
                    {          // skip docs not in bits
                        results.collect(doc, score);
                    }
                }
            };
        }

        Scorer scorer = query.weight(this).scorer(reader);
        if (scorer == null)
            return;
        scorer.score(collector);
    }

    /**
     * @param directory path to index directory
     */
    public void storeExtendedData(String directory)
    {
        lmIndexReader.storeExtendedData(directory);
    }

}
