package org.apache.lucene.search;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.ilps.DataCacher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.treceval.geotime.index.IndexGeoTime;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.config.ConfigProperties;

/*
 * 
 * This class implements several DivergenceFromRandomness (DFR) models
 * See a description at http://ir.dcs.gla.ac.uk/wiki/FormulasOfDFRModels
 * 
 */
final class TermScorerDFR extends LgteFieldedTermScorer {


    private static final Logger logger = Logger.getLogger(TermScorerDFR.class);


    // TODO: Set model and free parameters dynamically
    private static final double c = 1;
    private Model model = Model.DLHHypergeometricDFRModel;

    private Weight weight;
    private TermDocs termDocs;
    private byte[] norms;
    private float weightValue;

    private LanguageModelIndexReader indexReader;
    private boolean useFieldLengths;

    private final int[] docs = new int[32]; // buffered doc numbers
    private final int[] freqs = new int[32]; // buffered term freqs
    private int pointer;
    private int pointerMax;

    double docFreq;

    QueryConfiguration queryConfiguration;

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

        queryConfiguration = ModelManager.getInstance().getQueryConfiguration();
        if(queryConfiguration == null) queryConfiguration = new QueryConfiguration();

        // Get data for the collection model

        String docLengthType = (String) DataCacher.Instance().get("LM-lengths");
        if (docLengthType.equalsIgnoreCase("field")){
            this.useFieldLengths = true;
        } else if (docLengthType.equalsIgnoreCase("document")) {
            this.useFieldLengths = false;
        } else {
            throw new IllegalArgumentException("Unknown document length type: " + docLengthType);
        }
        initCollectionDetails(indexReader);
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




    public void initCollectionDetails(LanguageModelIndexReader indexReader) throws IOException
    {
//        System.out.println("Calling initCollection Details");
        if(!useFieldLengths && tokenNumber < 0)
        {
//            System.out.println("Calling initCollection Details");

            tokenNumber = indexReader.getCollectionTokenNumber();
            totalFreqs = indexReader.getTotalDocFreqs();
            avgLen = tokenNumber/numDocs;
            collSize = indexReader.getTotalDocFreqs();
        }
        numDocs = indexReader.maxDoc();
        this.docFreq = indexReader.docFreq(term);
//        System.out.println("//Calling initCollection Details");
    }

    public float score() throws IOException
    {


        Double avgDocLen = avgLen;
        int docLen;

        if (useFieldLengths)
        {
//            System.out.println("Using Fields useFieldLengths = true");
            docLen = indexReader.getFieldLength(doc, term.field());
            avgDocLen = avgLenFields.get(term.field());
            if(avgDocLen == null)
            {
//                System.out.println("Inicializando o AVG Doc Len");
                avgDocLen = (((double)indexReader.getCollectionTokenNumber(term.field()))+1.0) / numDocs;
                avgLenFields.put(term.field(),avgDocLen);
            }
        }
        else
        {
            docLen = indexReader.getDocLength(doc);
        }
        float tfDoc = freqs[pointer];
        double sim = 0;




        if(model == Model.OkapiBM25Model || model == Model.BM25b)
        {

            switch ( model ) {
                case OkapiBM25Model :
                {
                    double epslon = 0.01d;
                    double k1 = 2.0, b = 0.75;

                    double idf = idf(epslon, docFreq,queryConfiguration);
                    Double k1Cache = getDoubleCache("bm25.k1", queryConfiguration,k1,BM25_k1_CACHE_INDEX);
                    Double bCache = getDoubleCache("bm25.b", queryConfiguration,b,BM25_b_CACHE_INDEX);

                    sim = idf *
                            (
                                    (tfDoc*(k1Cache + 1))
                                            /
                                            ( tfDoc + k1Cache*(1.0 - bCache + bCache*(docLen/avgDocLen)))
                            );

//                System.out.println("doc-Id");
//                System.out.println("Doc-Len:" + docLen);
//                System.out.println("Avg-Doc-Len:" + avgDocLen);
//                System.out.println("docFreq:" + docFreq);
//                System.out.println("tfDoc:" + tfDoc);
                    break;
                }
                case BM25b:
                {

                    ///**
                    //  /** The constant k_1.*/
                    //	private double k_1 = 1.2d;
                    //
                    //	/** The constant k_3.*/
                    //	private double k_3 = 8d;
                    //
                    //	/** The parameter b.*/
                    //	private double b;
                    //
                    //	/** A default constructor.*/
                    //	public BM25() {
                    //		super();
                    //		b=0.75d;
                    //	}
                    //	 * Uses BM25 to compute a weight for a term in a document.
                    //	 * @param tf The term frequency in the document
                    //	 * @param docLength the document's length
                    //	 * @param n_t The document frequency of the term
                    //	 * @param F_t the term frequency in the collection
                    //	 * @param keyFrequency the term frequency in the query
                    //	 * @return the score assigned by the weighting model BM25.
                    //	 */
                    //	public final double score(
                    //		double tf,
                    //		double docLength,
                    //		double n_t,
                    //		double F_t,
                    //		double keyFrequency) {
                    //	    double K = k_1 * ((1 - b) + b * docLength / averageDocumentLength) + tf;
                    //	    return Idf.log((numberOfDocuments - n_t + 0.5d) / (n_t+ 0.5d)) *
                    //			((k_1 + 1d) * tf / (K + tf)) *
                    //			((k_3+1)*keyFrequency/(k_3+keyFrequency));
                    //	}
                    double k_1 = 1.2d;
                    double k_3 = 8d;
                    double b = 0.75d;
                    double keyFrequency = 1.0d;
                    double epslon = 0.01d;



                    double idf = idf(epslon,docFreq,queryConfiguration);
                    Double k1Cache = getDoubleCache("bm25.k1", queryConfiguration,k_1,BM25_k1_CACHE_INDEX);
                    Double k3Cache = getDoubleCache("bm25.k3", queryConfiguration,k_3,BM25_k3_CACHE_INDEX);
                    Double bCache = getDoubleCache("bm25.b", queryConfiguration,b,BM25_b_CACHE_INDEX);

                    //assumo 1 no keyFreq porque o Lucene vai invocar este metodo tantas vezes quantos os termos mesmo que repetidos
                    double K = k1Cache * ((1 - bCache) + bCache * docLen / avgDocLen) + tfDoc;
                    sim = idf *
                            ((k1Cache + 1d) * tfDoc / (K + tfDoc)) *
                            ((k3Cache+1)*keyFrequency/(k3Cache+keyFrequency));
                    break;
                }
            }
        }
        else
        {
            double docFreq = indexReader.docFreq(term);
            double tfCollection = indexReader.collFreq(term);
            double lambda = tfCollection / collSize;
            double ne = collSize * (1-Math.pow(1-docFreq/collSize, tfCollection));
            double tfn = tfDoc * Math.log1p(1+c*(avgLen/docLen));
            double tfne = tfDoc * Math.log(1+c*(avgLen/docLen));
            switch ( model ) {
                case DLHHypergeometricDFRModel: sim = (1 / (tfDoc + 0.5)) * Math.log1p( ((tfDoc * avgLen) / docLen) * ( collSize / tfCollection) ) + ((docLen-tfDoc) * Math.log1p( 1 - ( tfDoc / docLen ) ) ) + ( 0.5 * Math.log1p( 2*Math.PI*tfDoc*(1 - ( tfDoc / docLen ))) ); break;
                case InExpC2DFRModel : sim = (tfCollection/(docFreq*(tfne+1))) * (tfne*Math.log1p((collSize+1)/(ne+0.5))); break;
                case InExpB2DFRModel : sim = (tfCollection/(docFreq*(tfn+1))) * (tfn*Math.log1p((collSize+1)/(ne+0.5))); break;
                case IFB2DFRModel :	sim = (tfCollection/(docFreq*(tfn+1))) * (tfn*Math.log1p((collSize+1)/(tfCollection+0.5))); break;
                case InL2DFRModel : sim = (1/(tfn+1)) * (tfn*Math.log1p((collSize+1)/(docFreq+0.5))); break;
                case PL2DFRModel : sim = (1/(tfn+1)) * (tfn*Math.log1p(tfn/lambda) + (lambda-tfn) * Math.log1p(Math.E) + 0.5 * Math.log1p(Math.PI*2*tfn) ); break;
                case BB2DFRModel : sim = ((tfCollection+1)/(docFreq+(tfn+1))) * (-Math.log1p(collSize-1)-Math.log1p(Math.E)+stirlingFormula(tfCollection+collSize-1,tfCollection+collSize-tfn-2)-stirlingFormula(tfCollection,tfCollection+tfn)); break;
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

        if(sim < 0 && model != Model.OkapiBM25Model  && model != Model.BM25b )
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>FATAL  : PLEASE CHECK DFR Formulas similarity come negative for doc:" + doc + " term: " + term.text());
        return weightValue * (float)sim;
    }

    private Double getDoubleCache(String propertyName, QueryConfiguration queryConfiguration, Double defaultValue, int cacheIndex)
    {
        Double valueCache = (Double) queryConfiguration.getCacheObject(cacheIndex);
        if(valueCache == null)
        {
            if(queryConfiguration == null)
            {
                String valueCacheStr = ConfigProperties.getProperty(propertyName);
                if(valueCacheStr == null)
                {
                    logger.error(propertyName + " lookup fail in query configuration and in app.properties, using " + defaultValue);
                    valueCache = defaultValue;
                }
                else
                    valueCache = Double.parseDouble(valueCacheStr);
            }
            else
            {
                String valueCacheStr = queryConfiguration.getProperty(propertyName);
                if(valueCacheStr == null)
                {
                    logger.error(propertyName + " lookup fail in query configuration and in app.properties, using " + defaultValue);
                    valueCache = defaultValue;
                }
                else
                    valueCache = Double.parseDouble(valueCacheStr);
            }
            queryConfiguration.addCacheObject(valueCache,cacheIndex);
        }
        return valueCache;
    }


    static int BM25_POLICY_CACHE_INDEX = 0;
    static int BM25_EPSLON_CACHE_INDEX = 1;
    static int BM25_k1_CACHE_INDEX = 2;
    static int BM25_k2_CACHE_INDEX = 3;
    static int BM25_k3_CACHE_INDEX = 4;
    static int BM25_b_CACHE_INDEX = 5;

    private double idf(double epslonDefault, double docFreq, QueryConfiguration queryConfiguration)
    {
        Bm25Policy idfPolicy = (Bm25Policy) queryConfiguration.getCacheObject(0);
        if(idfPolicy == null)
        {
            String policy;
            policy = queryConfiguration.getProperty("bm25.idf.policy");
            if(policy == null)
                policy = ConfigProperties.getProperty("bm25.idf.policy");
            idfPolicy = Bm25Policy.parse(policy);
            queryConfiguration.addCacheObject(idfPolicy, BM25_POLICY_CACHE_INDEX);
        }
        if(idfPolicy == Bm25Policy.DontSubtractNt)
        {
            return Math.log((numDocs + 0.5)/(docFreq+0.5))/Math.log(2.0d);
        }
        double idf = Math.log((numDocs - docFreq + 0.5)/(docFreq+0.5))/Math.log(2.0d);
        if(idf <=  0)
        {
            if(idfPolicy == Bm25Policy.FloorZero)
                idf = 0d;
            else if(idfPolicy == Bm25Policy.FloorEpslon)
            {
                Double epslon = (Double) queryConfiguration.getCacheObject(1);
                if(epslon == null)
                {
                    String idfEpslon = queryConfiguration.getProperty("bm25.idf.epslon");
                    if(idfEpslon == null)
                    {
                        idfEpslon = ConfigProperties.getProperty("bm25.idf.epslon");
                        if(idfEpslon == null)
                        {
                            logger.error("bm25.idf.epslon lookup fail in query configuration and in app.properties, using floor " + epslonDefault);
                            idfEpslon = "" + epslonDefault;
                        }
                    }
                    epslon = Double.parseDouble(idfEpslon);
                    queryConfiguration.addCacheObject( epslon, BM25_EPSLON_CACHE_INDEX);
                }
                idf = epslon;
            }
        }

        return idf;
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

    public String getField()
    {
        return term.field();
    }


    public static enum Bm25Policy
    {
        Standard("standard"),
        DontSubtractNt("dont_subtract_n_t"),
        FloorZero("floor_zero"),
        FloorEpslon("floor_epslon");

        String policy;

        private Bm25Policy(String policy)
        {
            this.policy = policy;
        }

        public static Bm25Policy parse(String policy)
        {
            if(policy == null)
                return Standard;
            for(Bm25Policy bm25Policy: values())
            {
                if(bm25Policy.policy.equals(policy))
                    return bm25Policy;
            }
            return Standard;
        }
    }


    public static void main(String [] args) throws IOException, ParseException {
        long time = System.currentTimeMillis();
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, IndexGeoTime.indexPath);
        Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();
//                LgteQuery query = LgteQueryParser.parseQuery(request.getParameter("q"), analyzer);
//        System.out.println("Searching for: " + request.getParameter("q"));

        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.getQueryProperties().put("bm25.k1","40");
        queryConfiguration.getQueryProperties().put("bm25.b","0.2");
        queryConfiguration.getQueryProperties().put("bm25.k3","0.2");
        LgteQuery query = LgteQueryParser.parseQuery("What is the controversy surrounding the use of the Stealth Fighter in Yugoslavia",searcher,analyzer,queryConfiguration);

        LgteHits hits = searcher.search(query);



        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        query = LgteQueryParser.parseQuery("What is the controversy surrounding the use of the Stealth Fighter in Yugoslavia",searcher,analyzer,queryConfiguration);

        hits = searcher.search(query);

        time = System.currentTimeMillis() - time;
        System.out.println(time);

        
    }

}
