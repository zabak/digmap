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
import java.util.Properties;

import org.apache.lucene.index.*;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.ModelManager;

final class TermScorerLanguageModel extends LgteFieldedTermScorer {
    private Weight weight;
    private TermDocs termDocs;
    private byte[] norms;
    private float weightValue;
    //private float collSizeDFS; // sum of docFreq for all terms
    //private float collSizeCFS; // sum of collFreq for all terms
    private float collSize = 0.0f;
    private int tfCollection; // collection frequency of the term
    private ProbabilisticIndexReader indexReader;
    private float lambda;
    private boolean useFieldLengths;

    private final int[] docs = new int[32]; // buffered doc numbers
    private final int[] freqs = new int[32]; // buffered term freqs
    //    private int[][] freqsDist;
    private int pointer;
    private int pointerMax;
    private float log10 = (float) Math.log(10);

    int docLen;

    Properties modelProperties;
    QueryConfiguration queryConfiguration;

    TermScorerLanguageModel(
            Weight weight,
            TermDocs td,
            Similarity similarity,
            byte[] norms,
            IndexReader reader)
            throws IOException {
        super(reader, similarity);
        this.weight = weight;
        this.termDocs = td;
        this.norms = norms;
        this.weightValue = weight.getValue();
        this.indexReader = (ProbabilisticIndexReader) reader;

        this.term = ((TermQueryProbabilisticModel) weight.getQuery()).getTerm();


        queryConfiguration = ModelManager.getInstance().getQueryConfiguration();
        modelProperties = ModelManager.getInstance().getModelProperties();
        if(queryConfiguration == null) queryConfiguration = new QueryConfiguration();

        String collectionModel = queryConfiguration.getProperty("LM-cmodel",modelProperties);
        String docLengthType = queryConfiguration.getProperty("LM-lengths",modelProperties);
        this.lambda = queryConfiguration.getFloatProperty("LM-lambda",modelProperties);

        if (collectionModel.equals("cf")){
            this.collSize = (float) indexReader.getCollectionSize();
            this.tfCollection = indexReader.collFreq(term);
        } else if (collectionModel.equals("df")) {
            this.collSize = (float) indexReader.numDocs();
            this.tfCollection = indexReader.docFreq(term);
        } else {
            throw new IllegalArgumentException("Unknown collection model: " + collectionModel);
        }
        if (docLengthType.equalsIgnoreCase("field")){
            this.useFieldLengths = true;
        } else if (docLengthType.equalsIgnoreCase("document")) {
            this.useFieldLengths = false;
        } else {
            throw new IllegalArgumentException("Unknown document length type: " + docLengthType);
        }
    }

    
    public String getField()
    {
        return term.field();
    }


    public boolean next() throws IOException {
        pointer++;
        if (pointer >= pointerMax) {
            pointerMax = termDocs.read(docs, freqs); // refill buffer
//            freqsDist = ((SegmentTermContextDistanceDocs)termDocs).getFreqsDist();
            if (pointerMax != 0) {
                pointer = 0;
            } else {
                termDocs.close(); // close stream
                doc = Integer.MAX_VALUE; // set to sentinel value
                return false;
            }
        }
        doc = docs[pointer];
        return true;
    }

    public float score() throws IOException {

//        int contextSize = indexReader.getFieldLength(doc, term.field() + "$");
        if (useFieldLengths) {
            docLen = indexReader.getFieldLength(doc, term.field());
        } else {
            docLen = indexReader.getDocLength(doc);
        }
        float tfDoc = freqs[pointer];
        float sim =
                (float) Math.log(1.0f + ((lambda * tfDoc * collSize) /  ((1.0f - lambda) * tfCollection * docLen)));
        sim /= log10;


        return sim * weightValue;
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
        TermQuery query = (TermQuery) weight.getQuery();
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
        tfExplanation.setDescription(
                "tf(termFreq(" + query.getTerm() + ")=" + tf + ")");

        return tfExplanation;
    }

    public String toString() {
        return "scorer(" + weight + ")";
    }

}
