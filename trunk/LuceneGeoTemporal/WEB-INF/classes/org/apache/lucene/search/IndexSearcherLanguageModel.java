package org.apache.lucene.search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.ilps.DataCacher;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.HitQueue;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

/**
 * Search using a language model.
 * <p/>
 * <p>inherits everything from IndexSearcher, except the
 * search() method which adds the language length prior.
 */
public class IndexSearcherLanguageModel extends IndexSearcher
{

    boolean usePriors =
            (System.getProperty("priors") != null) ? true : false;

    //float beta = 1.0f;
    float beta = -1.0f;
    float log10 = (float) Math.log(10);
    LanguageModelIndexReader lmIndexReader = null;
    boolean extendedDataRead = false;


    private float getLmBeta()
    {

        if (beta == -1.0f)
            beta = (DataCacher.Instance().get("LM-beta") != null)
                    ? (Float.valueOf((String) DataCacher.Instance().get("LM-beta")))
                    .floatValue() : 1.0f;
        return beta;
    }

    /**
     * Creates a searcher searching the index in the named directory.
     */
    public IndexSearcherLanguageModel(String path) throws IOException
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
    public IndexSearcherLanguageModel(Directory directory) throws IOException
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
    public IndexSearcherLanguageModel(IndexReader r)
    {
        super(r);
        if (lmIndexReader == null)
        {
            lmIndexReader = new LanguageModelIndexReader(reader);
        }

        System.err.println("");

    }

    // inherit javadoc
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
            private float minScore = 0.0f;

            public final void collect(int doc, float score)
            {


                if (score > 0.0f &&              // ignore zeroed buckets
                        (bits == null || bits.get(doc)))
                {      // skip docs not in bits
                    totalHits[0]++;
                    // add prior to score
                    int docLen = getDocLength(doc);
                    float prior = 1.0f;
                    if (docLen > 0)
                    {
//                  System.out.println("add prior " + docLen + " (" + (float)Math.log(docLen) / log10 + ")");
                        score += getLmBeta() * (float) Math.log(docLen) / log10;
//                  System.out.println("final: " + score);
                    }
                    else
                    {
                        System.err.println("Zero doc length for doc " + doc);
                    }

                    if (usePriors)
                    {
                        Object priorObj = DataCacher.Instance().get("priors", doc);
                        if (priorObj != null)
                        {
                            try
                            {
                                prior = ((Float) priorObj).floatValue();
                            }
                            catch (Exception e)
                            {
                                try
                                {
                                    int i = ((Integer) priorObj).intValue();
                                    prior = (float) i;
                                }
                                catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }
                            }
                            // TODO: replace with better combination of prior
                            score *= prior;
                        }
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

