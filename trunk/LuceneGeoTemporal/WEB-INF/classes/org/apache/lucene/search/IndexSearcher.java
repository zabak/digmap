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

import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import pt.utl.ist.lucene.Model;


/** Implements search over a single IndexReader.
 *
 * <p>Applications usually need only call the inherited {@link #search(Query)}
 * or {@link #search(Query,Filter)} methods.
 */
public class IndexSearcher extends Searcher {
    IndexReader reader;
    private boolean closeReader;
    private boolean usePriors = false;

    /** Creates a searcher searching the index in the named directory. */
    public IndexSearcher(String path) throws IOException {
        this(IndexReader.open(path), true);
    }

    /** Creates a searcher searching the index in the provided directory. */
    public IndexSearcher(Directory directory) throws IOException {
        this(IndexReader.open(directory), true);
    }

    /** Creates a searcher searching the provided index. */
    public IndexSearcher(IndexReader r) {
        this(r, false);
    }

    private IndexSearcher(IndexReader r, boolean closeReader) {
        reader = r;
        this.closeReader = closeReader;
        String usePriorsStr = System.getProperty("priors");
        if (usePriorsStr != null) {
            usePriors = true;
        }
    }

    /**
     * Note that the underlying IndexReader is not closed, if
     * IndexSearcher was constructed with IndexSearcher(IndexReader r).
     * If the IndexReader was supplied implicitly by specifying a directory, then
     * the IndexReader gets closed.
     */
    public void close() throws IOException {
        if(closeReader)
            reader.close();
    }

    // inherit javadoc
    public int docFreq(Term term) throws IOException {
        return reader.docFreq(term);
    }

    public IndexReader getReader() {
        return reader;
    }

    // inherit javadoc
    public Document doc(int i) throws IOException {
        return reader.document(i);
    }

    // inherit javadoc
    public int maxDoc() throws IOException {
        return reader.maxDoc();
    }

    private int getDocLength(int doc)
    {
        int docLen = 0;
        try
        {
            docLen = reader.getDocLength(doc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return docLen;
    }


    // inherit javadoc
    public TopDocs search(Query query, Filter filter, final int nDocs)
            throws IOException {
        Scorer scorer = query.weight(this).scorer(reader);
        if (scorer == null)
            return new TopDocs(0, new ScoreDoc[0]);

        final BitSet bits = filter != null ? filter.bits(reader) : null;
        final HitQueue hq = new HitQueue(nDocs);
        final int[] totalHits = new int[1];
        scorer.score(new HitCollector() {
            private float minScore = 0.0f;
            public final void collect(int doc, float score) {
                if (score > 0.0f &&			  // ignore zeroed buckets
                        (bits==null || bits.get(doc))) {	  // skip docs not in bits
                    totalHits[0]++;

                    int docLen = getDocLength(doc);
                    if (docLen > 0)
                    {
                        score = Model.VectorSpaceModel.getDocumentFinalScorer().computeFinalScore(score,reader,docLen);
                    }
                    else
                    {
                        System.err.println(">>>>>>>>>>>>>>>>Zero doc length for doc " + doc);
                        try {
                            System.out.println("Contents for this document: " + reader.document(doc).getField("contents").stringValue());
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
//                        score = Model.VectorSpaceModel.getDocumentFinalScorer().computeFinalScore(score,reader,docLen);
//                        System.exit(-1);
                    }
//                    if (usePriors) {
//                        float prior = 1.0f;
//                        Object priorObj = DataCacher.Instance().get("priors", doc);
//                        if (priorObj != null) {
//                            try {
//                                prior = ((Float)priorObj).floatValue();
//                            } catch (Exception e) {
//                                try {
//                                    int i = ((Integer)priorObj).intValue();
//                                    prior = (float)i;
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        }
//                        // TODO: replace with better combination of prior
//                        score *= prior;
//                    }
                    if (hq.size() < nDocs || score >= minScore) {
                        hq.insert(new ScoreDoc(doc, score));
                        minScore = ((ScoreDoc)hq.top()).score; // maintain minScore
                    }
                }
            }
        });

        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size()-1; i >= 0; i--)	  // put docs in array
            scoreDocs[i] = (ScoreDoc)hq.pop();

        return new TopDocs(totalHits[0], scoreDocs);
    }

    // inherit javadoc
    public TopFieldDocs search(Query query, Filter filter, final int nDocs,
                               Sort sort)
            throws IOException {
        Scorer scorer = query.weight(this).scorer(reader);
        if (scorer == null)
            return new TopFieldDocs(0, new ScoreDoc[0], sort.fields);

        final BitSet bits = filter != null ? filter.bits(reader) : null;
        final FieldSortedHitQueue hq =
                new FieldSortedHitQueue(reader, sort.fields, nDocs);
        final int[] totalHits = new int[1];
        scorer.score(new HitCollector() {
            public final void collect(int doc, float score) {
                if (score > 0.0f &&			  // ignore zeroed buckets
                        (bits==null || bits.get(doc))) {	  // skip docs not in bits
                    totalHits[0]++;
                    hq.insert(new FieldDoc(doc, score));
                }
            }
        });

        ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
        for (int i = hq.size()-1; i >= 0; i--)	  // put docs in array
            scoreDocs[i] = hq.fillFields ((FieldDoc) hq.pop());

        return new TopFieldDocs(totalHits[0], scoreDocs, hq.getFields());
    }


    // inherit javadoc
    public void search(Query query, Filter filter,
                       final HitCollector results) throws IOException {
        HitCollector collector = results;
        if (filter != null) {
            final BitSet bits = filter.bits(reader);
            collector = new HitCollector() {
                public final void collect(int doc, float score) {
                    if (bits.get(doc)) {		  // skip docs not in bits
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

    public Query rewrite(Query original) throws IOException {
        Query query = original;
        for (Query rewrittenQuery = query.rewrite(reader); rewrittenQuery != query;
             rewrittenQuery = query.rewrite(reader)) {
            query = rewrittenQuery;
        }
        return query;
    }

    public Explanation explain(Query query, int doc) throws IOException {
        return query.weight(this).explain(reader, doc);
    }

}
