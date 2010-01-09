package org.apache.lucene.search;

import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.ilps.DataCacher;

import java.io.IOException;

final class TermScorerLanguageModelHiemstra extends Scorer {
    private Weight weight;
    private TermDocs termDocs;
    private byte[] norms;
    private float weightValue;
    private int doc;
    //private float collSizeDFS; // sum of docFreq for all terms
    //private float collSizeCFS; // sum of collFreq for all terms
    private float collSize = 0.0f;
    private int tfCollection; // collection frequency of the term
    private LanguageModelIndexReader indexReader;
    private float lambda;
    private boolean useFieldLengths;

    private final int[] docs = new int[32]; // buffered doc numbers
    private final int[] freqs = new int[32]; // buffered term freqs
    private int pointer;
    private int pointerMax;
    private Term term;
    private float log10 = (float) Math.log(10);

    int fieldLen;

    TermScorerLanguageModelHiemstra(
            Weight weight,
            TermDocs td,
            Similarity similarity,
            byte[] norms,
            IndexReader reader)
            throws IOException
    {
        super(similarity);
        this.weight = weight;
        this.termDocs = td;
        this.norms = norms;
        this.weightValue = weight.getValue();
        this.indexReader = new LanguageModelIndexReader(reader);
        this.term = ((TermQueryProbabilisticModel) weight.getQuery()).getTerm();

        // Get data for the collection model
        String collectionModel =
                (String) DataCacher.Instance().get("LM-cmodel");
        this.lambda =
                (Float.valueOf((String) DataCacher.Instance().get("LM-lambda")))
                        .floatValue();
        String docLengthType =
                (String) DataCacher.Instance().get("LM-lengths");

        if (collectionModel.equals("cf")){
            this.collSize = (float) indexReader.getCollectionTokenNumber();
            this.tfCollection = indexReader.collFreq(term);
        } else if (collectionModel.equals("df")) {
            this.collSize = (float) indexReader.getTotalDocFreqs();
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

    public int doc() {
        return doc;
    }

    public boolean next() throws IOException {
        pointer++;
        if (pointer >= pointerMax) {
            pointerMax = termDocs.read(docs, freqs); // refill buffer
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

        if (useFieldLengths) {
            fieldLen = indexReader.getFieldLength(doc, term.field());
        } else {
            fieldLen = indexReader.getDocLength(doc);
        }
        float tfDoc = freqs[pointer];
//		float sim =
//			(float) Math.log(1.0f + ((lambda * tfDoc * collSize) /  ((1.0f - lambda) * tfCollection * fieldLen)));
//		sim /= log10;


//        float probabilityTermCollection = tfCollection / collSize;
//        float probabilityTermDocument = tfDoc / fieldLen;
//        return weightValue * (float) Math.log10( 1.0f + (lambda * (probabilityTermDocument) / ((1-lambda)*probabilityTermCollection)));


        float probabilityTermCollection = tfCollection / collSize;
        float probabilityTermDocument = tfDoc / fieldLen;

//        return weightValue * (float) Math.log(lambda * (probabilityTermDocument) / ((1-lambda)*probabilityTermCollection));
        if(probabilityTermDocument == 0)
            return (1-lambda)*probabilityTermCollection;
        return weightValue * (float) Math.log(lambda * (probabilityTermDocument) / ((1-lambda)*probabilityTermCollection));
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
