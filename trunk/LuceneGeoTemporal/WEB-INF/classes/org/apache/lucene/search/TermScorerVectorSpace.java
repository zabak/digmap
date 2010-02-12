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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

import java.io.IOException;

final class TermScorerVectorSpace extends LgteFieldedTermScorer {
    private Weight weight;
    private TermDocs termDocs;
    private byte[] norms;
    private float weightValue;

    private final int[] docs = new int[32];	  // buffered doc numbers
    private final int[] freqs = new int[32];	  // buffered term freqs
    private int pointer;
    private int pointerMax;

    private static final int SCORE_CACHE_SIZE = 32;
    private float[] scoreCache = new float[SCORE_CACHE_SIZE];

    TermScorerVectorSpace(IndexReader reader, Weight weight, Term term, TermDocs td, Similarity similarity,
                          byte[] norms) throws IOException {
        super(reader, similarity);
        this.weight = weight;
        this.termDocs = td;
        this.term = term;
        this.norms = norms;
        this.weightValue = weight.getValue();

        for (int i = 0; i < SCORE_CACHE_SIZE; i++)
            scoreCache[i] = getSimilarity().tf(i) * weightValue;
    }

    public boolean next() throws IOException {
        pointer++;
        if (pointer >= pointerMax) {
            pointerMax = termDocs.read(docs, freqs);    // refill buffer
            if (pointerMax != 0) {
                pointer = 0;
            } else {
                termDocs.close();			  // close stream
                doc = Integer.MAX_VALUE;		  // set to sentinel value
                return false;
            }
        }
        doc = docs[pointer];
        return true;
    }

    public float score() throws IOException {

        int f = freqs[pointer];
        float raw =                                   // compute tf(f)*weight
                f < SCORE_CACHE_SIZE			  // check cache
                        ? scoreCache[f]                             // cache hit
                        : getSimilarity().tf(f)*weightValue;        // cache miss

        return raw * Similarity.decodeNorm(norms[doc]); // normalize for field
    }

    public boolean skipTo(int target) throws IOException {
        // first scan in cache
        for (pointer++; pointer < pointerMax; pointer++) {
            if (!(target > docs[pointer])) {
                doc = docs[pointer];
                return true;
            }
        }

        // not found in cache, seek underlying stream
        boolean result = termDocs.skipTo(target);
        if (result) {
            pointerMax = 1;
            pointer = 0;
            docs[pointer] = doc = termDocs.doc();
            freqs[pointer] = termDocs.freq();
        } else {
            doc = Integer.MAX_VALUE;
        }
        return result;
    }

    public Explanation explain(int doc) throws IOException {
        TermQueryVectorSpace query = (TermQueryVectorSpace)weight.getQuery();
        Explanation tfExplanation = new Explanation();
        int tf = 0;
        while (pointer < pointerMax) {
            if (docs[pointer] == doc)
                tf = freqs[pointer];
            pointer++;
        }
        if (tf == 0) {
            while (termDocs.next()) {
                if (termDocs.doc() == doc) {
                    tf = termDocs.freq();
                }
            }
        }
        termDocs.close();
        tfExplanation.setValue(getSimilarity().tf(tf));
        tfExplanation.setDescription("tf(termFreq("+query.getTerm()+")="+tf+")");

        return tfExplanation;
    }

    public String toString() { return "scorer(" + weight + ")"; }

}
