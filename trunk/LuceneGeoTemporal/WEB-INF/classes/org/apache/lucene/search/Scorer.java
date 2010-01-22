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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.index.NotImplemented;

import java.io.IOException;

import pt.utl.ist.lucene.treceval.geotime.runs.BaseLineSentences;
import pt.utl.ist.lucene.treceval.geotime.runs.Experiments;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.ModelManager;

/** Expert: Implements scoring for a class of queries. */
public abstract class Scorer {
    private Similarity similarity;
    protected boolean indexTree;

    /** Constructs a Scorer. */
    protected Scorer(Similarity similarity) {
        this.similarity = similarity;
        QueryConfiguration queryConfiguration = ModelManager.getInstance().getQueryConfiguration();
        indexTree = queryConfiguration.getBooleanProperty("index.tree");
    }

    /** Returns the Similarity implementation used by this scorer. */
    public Similarity getSimilarity() {
        return this.similarity;
    }
                               static int i = 0;
    /** Scores all documents and passes them to a collector. */
    public void score(HitCollector hc) throws IOException {
        while (next())
        {
            collect(hc,this);
//            hc.collect(doc(),score());
        }
    }

    /**
     * LGTE MODIFICATION TO IMPLEMENT HIERARCHIC INDEXES
     * THE OLD CODE
     * In Hierarchic Indexes a document has a list of children documents in other indexes.
     * During the search these childrens are the returned documents
     * Parent document id in his own index is diferent from the id's of the childrens
     * This is a problem of id's mapping.
     * The objective of this modification is to calculate the score of the parent just one time
     * and collect that score for all the childrens
     * Jorge Machado
     *
     while (!sub.done && scorer.doc() < end)
     {
     sub.collector.collect(scorer.doc(), scorer.score());
     sub.done = !scorer.next();
     }
     */
    protected void collect(HitCollector collectorX,Scorer scorerX) throws IOException {
        int docX = scorerX.doc();
        float scoreX = scorerX.score();
        if(indexTree && scorerX instanceof LgteFieldedTermScorer)
        {
            long start = System.currentTimeMillis();
            LgteFieldedTermScorer  lgteFieldedTermScorer = (LgteFieldedTermScorer) scorerX;
            IndexReader reader = lgteFieldedTermScorer.getIndexReader();
            if(reader instanceof LgteIsolatedIndexReader)
            {

                String field = lgteFieldedTermScorer.getField();
                if(((LgteIsolatedIndexReader)reader).hasMapping(field))
                {
                    int[] docs =  ((LgteIsolatedIndexReader)reader).translateId(docX,field);
                    for (int doc1 : docs) {
                        collectorX.collect(doc1, scoreX);
                    }
                }

                else
                    collectorX.collect(docX, scoreX);
            }
            else
                throw new NotImplemented("index.tree is implmented only when using LgteIsolatedIndexReader with multiindexes");
            BaseLineSentences.totalTimeTree = BaseLineSentences.totalTimeTree + (System.currentTimeMillis() - start);
        }
        else
        {   //keeping for the old classes
            collectorX.collect(docX,scoreX);
        }
    }

    /** Advance to the next document matching the query.  Returns true iff there
     * is another match. */
    public abstract boolean next() throws IOException;

    /** Returns the current document number.  Initially invalid, until {@link
     * #next()} is called the first time. */
    public abstract int doc();

    /** Returns the score of the current document.  Initially invalid, until
     * {@link #next()} is called the first time. */
    public abstract float score() throws IOException;

    /** Skips to the first match beyond the current whose document number is
     * greater than or equal to <i>target</i>. <p>Returns true iff there is such
     * a match.  <p>Behaves as if written: <pre>
     *   boolean skipTo(int target) {
     *     do {
     *       if (!next())
     * 	     return false;
     *     } while (target > doc());
     *     return true;
     *   }
     * </pre>
     * Most implementations are considerably more efficient than that.
     */
    public abstract boolean skipTo(int target) throws IOException;

    /** Returns an explanation of the score for <code>doc</code>. */
    public abstract Explanation explain(int doc) throws IOException;

}
