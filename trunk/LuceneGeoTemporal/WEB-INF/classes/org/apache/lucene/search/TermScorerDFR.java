package org.apache.lucene.search;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.ilps.DataCacher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import pt.utl.ist.lucene.Model;

/*
 * 
 * This class implements several DivergenceFromRandomness (DFR) models
 * See a description at http://ir.dcs.gla.ac.uk/wiki/FormulasOfDFRModels
 * 
 */
final class TermScorerDFR extends Scorer  {



    // TODO: Set model and free parameters dynamically
    private static final double c = 1, k1 = 1.2, b = 0.5;
    private Model model = Model.DLHHypergeometricDFRModel;

    private Weight weight;
    private TermDocs termDocs;
    private byte[] norms;
    private float weightValue;
    private int doc;
    private LanguageModelIndexReader indexReader;
    private boolean useFieldLengths;

    private final int[] docs = new int[32]; // buffered doc numbers
    private final int[] freqs = new int[32]; // buffered term freqs
    private int pointer;
    private int pointerMax;
    private Term term;



    TermScorerDFR(
            Weight weight,
            TermDocs td,
            Similarity similarity,
            byte[] norms,
            IndexReader reader,
            Model model)
            throws IOException {
        super(similarity);
        this.model = model;
        this.weight = weight;
        this.termDocs = td;
        this.norms = norms;
        this.weightValue = weight.getValue();
        this.indexReader = new LanguageModelIndexReader(reader);
        this.term = ((TermQueryProbabilisticModel) weight.getQuery()).getTerm();

        // Get data for the collection model
        String docLengthType = (String) DataCacher.Instance().get("LM-lengths");
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

    static double tokenNumber = -1;
    static double totalFreqs ;
    static double avgLen;
    static double collSize;
    static double numDocs;
    static Map<String,Double> avgLenFields = new HashMap<String,Double>();

    public static void initCollectionDetails(LanguageModelIndexReader indexReader) throws IOException
    {
        if(tokenNumber < 0)
        {
            numDocs = indexReader.maxDoc();
            tokenNumber = indexReader.getCollectionTokenNumber();
            totalFreqs = indexReader.getTotalDocFreqs();
            avgLen = tokenNumber/numDocs;
            collSize = indexReader.getTotalDocFreqs();

        }
    }

    public float score() throws IOException {
        Double avgDocLen = avgLen;
        int docLen;
        if (useFieldLengths) {
            docLen = indexReader.getFieldLength(doc, term.field());
            avgDocLen = avgLenFields.get(term.field());
            if(avgDocLen == null)
            {
                avgDocLen = ((double)indexReader.getCollectionTokenNumber(term.field())) / numDocs;
                avgLenFields.put(term.field(),avgDocLen);
            }
        } else {
            docLen = indexReader.getDocLength(doc);
        }
        float tfDoc = freqs[pointer];
        double sim = 0;

        initCollectionDetails(indexReader);
        double tfCollection = indexReader.collFreq(term);
        double docFreq = indexReader.docFreq(term);
//		double numTerms = indexReader.getCollectionTokenNumber();    tava repetido
        double nt = indexReader.docFreq(term);
        double lambda = tfCollection / collSize;
        double ne = collSize * (1-Math.pow(1-nt/collSize, tfCollection));
        double tfn = tfDoc * Math.log1p(1+c*(avgLen/docLen));
        double tfne = tfDoc * Math.log(1+c*(avgLen/docLen));
        switch ( model ) {
            case DLHHypergeometricDFRModel: sim = (1 / (tfDoc + 0.5)) * Math.log1p( ((tfDoc * avgLen) / docLen) * ( collSize / tfCollection) ) + ((docLen-tfDoc) * Math.log1p( 1 - ( tfDoc / docLen ) ) ) + ( 0.5 * Math.log1p( 2*Math.PI*tfDoc*(1 - ( tfDoc / docLen ))) ); break;
            case InExpC2DFRModel : sim = (tfCollection/(nt*(tfne+1))) * (tfne*Math.log1p((collSize+1)/(ne+0.5))); break;
            case InExpB2DFRModel : sim = (tfCollection/(nt*(tfn+1))) * (tfn*Math.log1p((collSize+1)/(ne+0.5))); break;
            case IFB2DFRModel :	sim = (tfCollection/(nt*(tfn+1))) * (tfn*Math.log1p((collSize+1)/(tfCollection+0.5))); break;
            case InL2DFRModel : sim = (1/(tfn+1)) * (tfn*Math.log1p((collSize+1)/(nt+0.5))); break;
            case PL2DFRModel : sim = (1/(tfn+1)) * (tfn*Math.log1p(tfn/lambda) + (lambda-tfn) * Math.log1p(Math.E) + 0.5 * Math.log1p(Math.PI*2*tfn) ); break;
            case BB2DFRModel : sim = ((tfCollection+1)/(nt+(tfn+1))) * (-Math.log1p(collSize-1)-Math.log1p(Math.E)+stirlingFormula(tfCollection+collSize-1,tfCollection+collSize-tfn-2)-stirlingFormula(tfCollection,tfCollection+tfn)); break;
            case OkapiBM25Model :
            {
                double idf = Math.log((numDocs - docFreq +0.5)/(docFreq+0.5));
                sim = idf *
                        (
                            (tfDoc*(k1 + 1))
                                    /
                            ( tfDoc + k1*(1 - b + b*(docLen/avgDocLen)))
                        ); break;
            }
        }



        /*
        *
        * Token tok = (Token)it.next();
            	Integer dfInteger = (Integer)documentFrequency.get(tok);
                double df = dfInteger==null ? 0.0 : dfInteger.intValue();
            	double idf = Math.log((numDocs+0.5)/(df+0.5));
            	score += idf * (
            	        (bags.getWeight(tok) * (k1+1))
            	        /
            	        (
            	            bags.getWeight(tok)
            	                +
            	            k1 * (
            	                    (1.0-b)
            	                         +
            	                     b   *   (bags.size()/((totalTokenCount+1.0) / numDocs)))) );
        *
        *
        * */


        if(sim < 0)
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>FATAL  : PLEASE CHECK DFR Formulas similarity come negative for doc:" + doc + " term: " + term.text());
        return weightValue * (float)sim;
    }

    private double stirlingFormula ( double m, double n )
    {
        return (m+0.5)*Math.log1p(n/m)+(n-m)*Math.log1p(n);
    }

    public boolean skipTo(int target) throws IOException
    {
        // first scan in cache
        for (pointer++; pointer < pointerMax; pointer++)
        {
            if (!(target > docs[pointer]))
            {
                doc = docs[pointer];
                return true;
            }
        }
        // not found in cache, seek underlying stream
        boolean result = termDocs.skipTo(target);
        if (result)
        {
            pointerMax = 1;
            pointer = 0;
            docs[pointer] = doc = termDocs.doc();
            freqs[pointer] = termDocs.freq();
        }
        else
        {
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
        tfExplanation.setDescription("tf(termFreq(" + query.getTerm() + ")=" + tf + ")");
        return tfExplanation;
    }

    public String toString() {
        return "scorer(" + weight + ")";
    }

}
