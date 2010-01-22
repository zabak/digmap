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

import pt.utl.ist.lucene.ModelManager;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.treceval.geotime.runs.BaseLineSentences;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.index.NotImplemented;

final class BooleanScorer extends Scorer {
    private SubScorer scorers = null;
    private BucketTable bucketTable = new BucketTable(this);

    private int maxCoord = 1;
    private float[] coordFactors = null;

    private int requiredMask = 0;
    private int prohibitedMask = 0;
    private int nextMask = 1;


    BooleanScorer(Similarity similarity)
    {
        super(similarity);
    }

    static final class SubScorer {
        public Scorer scorer;
        public boolean done;
        public boolean required = false;
        public boolean prohibited = false;
        public HitCollector collector;
        public SubScorer next;

        public SubScorer(Scorer scorer, boolean required, boolean prohibited,
                         HitCollector collector, SubScorer next)
                throws IOException {
            this.scorer = scorer;
            this.done = !scorer.next();
            this.required = required;
            this.prohibited = prohibited;
            this.collector = collector;
            this.next = next;

        }
    }

    final void add(Scorer scorer, boolean required, boolean prohibited)
            throws IOException {
        int mask = 0;
        if (required || prohibited) {
            if (nextMask == 0)
                throw new IndexOutOfBoundsException
                        ("More than 32 required/prohibited clauses in query.");
            mask = nextMask;
            nextMask = nextMask << 1;
        } else
            mask = 0;

        if (!prohibited)
            maxCoord++;

        if (prohibited)
            prohibitedMask |= mask;			  // update prohibited mask
        else if (required)
            requiredMask |= mask;			  // update required mask

        scorers = new SubScorer(scorer, required, prohibited,
                bucketTable.newCollector(mask), scorers);
    }

    private final void computeCoordFactors() throws IOException {
        coordFactors = new float[maxCoord];
        for (int i = 0; i < maxCoord; i++)
            coordFactors[i] = getSimilarity().coord(i, maxCoord-1);
    }

    private int end;
    private Bucket current;

    public int doc() { return current.doc; }


    //    List<>
    //    class docCollector
    public boolean next() throws IOException {
        boolean more;
        do {
            while (bucketTable.first != null) {         // more queued
                current = bucketTable.first;
                bucketTable.first = current.next;         // pop the queue

                // check prohibited & required
                if ((current.bits & prohibitedMask) == 0 &&
                        (current.bits & requiredMask) == requiredMask) {
                    return true;
                }
            }

            // refill the queue
            more = false;
            end += BucketTable.SIZE;
//            System.out.println("increasing bucket to " + end);

            for (SubScorer sub = scorers; sub != null; sub = sub.next) {
                Scorer scorer = sub.scorer;

                int maxSubDoc = scorer.doc();
                while (!sub.done && maxSubDoc < end)
                {
//                    collect(sub.collector,sub.scorer);
//                    sub.collector.collect(scorer.doc(), scorer.score());




                    Scorer scorerX = scorer;
                    HitCollector collectorX = sub.collector;

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
                                boolean stop = false;

                                for (int doc1 : docs)
                                {
                                    if(doc1 >= (end - BucketTable.SIZE))
                                    {
                                        if(doc1 < end)
                                            collectorX.collect(doc1, scoreX);
                                        else
                                        {
                                            stop = true;
                                            break;
                                        }
                                    }
                                }
                                if(stop)
                                    break;
                            }
                            else
                            {
                                collectorX.collect(docX, scoreX);
                            }
                        }
                        else
                            throw new NotImplemented("index.tree is implmented only when using LgteIsolatedIndexReader with multiindexes");
                        BaseLineSentences.totalTimeTree = BaseLineSentences.totalTimeTree + (System.currentTimeMillis() - start);
                    }
                    else
                    {   //keeping for the old classes
                        collectorX.collect(docX,scoreX);
                    }

                    sub.done = !scorer.next();
                    maxSubDoc = scorer.doc();
                }
                if (!sub.done) {
                    more = true;
                }
            }
        } while (bucketTable.first != null | more);

        return false;
    }

    public float score() throws IOException {
        if (coordFactors == null)
            computeCoordFactors();
        return current.score; //* coordFactors[current.coord];
    }

    static final class Bucket {
        int	doc = -1;				  // tells if bucket is valid
        float	score;				  // incremental score
        int	bits;					  // used for bool constraints
        int	coord;					  // count of terms in score
        Bucket 	next;				  // next valid bucket
    }

    /** A simple hash table of document scores within a range. */
    static final class BucketTable {
        public static final int SIZE = 1 << 10;
        public static final int MASK = SIZE - 1;

        final Bucket[] buckets = new Bucket[SIZE];
        Bucket first = null;			  // head of valid list

        private BooleanScorer scorer;

        public BucketTable(BooleanScorer scorer) {
            this.scorer = scorer;
        }

        public final int size() { return SIZE; }

        public HitCollector newCollector(int mask) {
            return new Collector(mask, this);
        }
    }

    static final class Collector extends HitCollector {
        private BucketTable bucketTable;
        private int mask;
        public Collector(int mask, BucketTable bucketTable) {
            this.mask = mask;
            this.bucketTable = bucketTable;
        }
        public final void collect(final int doc, final float score) {
            final BucketTable table = bucketTable;
            final int i = doc & BucketTable.MASK;
            Bucket bucket = table.buckets[i];
            if (bucket == null)
                table.buckets[i] = bucket = new Bucket();

            if (bucket.doc != doc) {			  // invalid bucket
                bucket.doc = doc;			  // set doc
                bucket.score = score;			  // initialize score
                bucket.bits = mask;			  // initialize mask
                bucket.coord = 1;			  // initialize coord

                bucket.next = table.first;		  // push onto valid list
                table.first = bucket;
            } else {
                // valid bucket
                bucket.score += score;			  // increment score
                bucket.bits |= mask;			  // add bits in mask
                bucket.coord++;				  // increment coord
            }
        }
    }

    public boolean skipTo(int target) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Explanation explain(int doc) throws IOException
    {
        Explanation explanation = new Explanation();
        StringBuffer buffer = new StringBuffer();
        buffer.append("boolean(");
        for (SubScorer sub = scorers; sub != null; sub = sub.next) {
            Explanation detail = sub.scorer.explain(doc);
            buffer.append(detail.getDescription());
            explanation.addDetail(detail);
            buffer.append(" ");
        }
        buffer.append(")^");
//        return buffer.toString();
//        tfExplanation.setDescription(model.getShortName() + "(" + query.getTerm() + ")^" + weightValue + "=" + score + ")");
//        throw new UnsupportedOperationException();

        explanation.setValue(score());
        explanation.setDescription(buffer.toString());
        return explanation;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("boolean(");
        for (SubScorer sub = scorers; sub != null; sub = sub.next) {
            buffer.append(sub.scorer.toString());
            buffer.append(" ");
        }
        buffer.append(")");
        return buffer.toString();
    }


}
