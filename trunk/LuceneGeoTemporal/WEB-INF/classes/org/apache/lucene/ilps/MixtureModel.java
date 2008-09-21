/*
 * MixtureModel.java
 *
 * Created on June 27, 2006, 12:13 PM
 *
 * MixtureModel implementation, build on top of ILPS Lucene
 *
 */

package org.apache.lucene.ilps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Vector;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.aid.Retrieve;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.WhitespaceStopLowercaseAnalyzer;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;


/**
 *
 * @author Edgar Meij
 */
public class MixtureModel {
    
    private static String[] luceneConfigFile = null;
    private static IndexSearcher[] searcher = null;
    private static Retrieve[] lucene = null;
    private static HashMap[] docIdMap = null;	
    private static HashMap[] lucIdMap = null;
    private static String[] field = null;
    private static float[] lambda = null;

    private static float lengthPrior = 1.0f;
    private static boolean useLuceneIds = false;    

    private static String LUCENE_PROPERTY_PREFIX;
    private static int indexNum = 0;
    
    /*
    private static String luceneConfigFile1 = null;
    private static String luceneConfigFile2 = null;
    private static IndexSearcher searcher1 = null;
    private static IndexSearcher searcher2 = null;
    private static Retrieve lucene1 = null;
    private static Retrieve lucene2 = null;
    private static HashMap docIdMap1 = null;	
    private static HashMap docIdMap2 = null;	
    private static HashMap lucIdMap1 = null;
    private static HashMap lucIdMap2 = null;

    private static String field = Retrieve.LUCENE_DEFAULT_FIELD;
    private static String field1;
    private static String field2;
    private static float eLambda = 0.1f;
    private static float dLambda = 0.1f;
     */
    
    
    /** Creates a new instance of MixtureModel */
    public MixtureModel() {
    }
    
        
    public static void main(String[] args) {
        /*
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-h")) {
                throw new IllegalArgumentException("Not implemented yet");
                //printUsage();
            } else if (args[i].equals("-c1")) {
                i++;
                if (i >= args.length)
                        throw new IllegalArgumentException("specify first lucene config file after -c1");
                luceneConfigFile1 = args[i];
            } else if (args[i].equals("-c2")) {
                i++;
                if (i >= args.length)
                        throw new IllegalArgumentException("specify first lucene config file after -c2");
                luceneConfigFile2 = args[i];                
            } else if (args[i].equals("-elam")) {
                i++;
                if (i >= args.length)
                        throw new IllegalArgumentException("specify lambda after -elam");
                eLambda = Float.parseFloat(args[i]);
            } else if (args[i].equals("-dlam")) {
                i++;
                if (i >= args.length)
                        throw new IllegalArgumentException("specify lambda after -dlam");
                dLambda = Float.parseFloat(args[i]);
            } else if (args[i].equals("-luceneIds")) {
                useLuceneIds = true;
            } else {
                throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }

        }
	
        if (luceneConfigFile1 == null || luceneConfigFile2 == null) {
            //printUsage();
            throw new IllegalArgumentException("Must define both configfiles");
        }
        
        // Get Searchers
        //lucene1 = new Retrieve();
        //lucene1.main(new String[] {"-c",luceneConfigFile1,"-doNothing"});
        //searcher1 = (IndexSearcher)lucene1.getSearcher();
        
        //lucene2 = new Retrieve();
        //lucene2.main(new String[] {"-c",luceneConfigFile2,"-doNothing"});
        //searcher2 = (IndexSearcher)lucene2.getSearcher();
        
        //LuceneRetriever retriever1 = setProperties(luceneConfigFile1);
        searcher1 = setProperties(luceneConfigFile1);

        //LuceneRetriever retriever2 = setProperties(luceneConfigFile2);
        searcher2 = setProperties(luceneConfigFile2);
        
        try {    
            // TODO: select field?
            System.err.println("fields in index1: " + getIndexedFields(searcher1).length);
            System.err.println("fields in index2: " + getIndexedFields(searcher2).length);
        
            if (getIndexedFields(searcher1).length == 1) {
                field1 = getIndexedFields(searcher1)[0];
                System.err.println("Using field1: " + field1);
            } else {
                System.err.println("please check the defined fieldname: " + field);
                field1 = field;
            }
            
            if (getIndexedFields(searcher2).length == 1) {
                field2 = getIndexedFields(searcher2)[0];
                System.err.println("Using field2: " + field2);
            } else {
                System.err.println("please check the defined fieldname: " + field);
                field2 = field;
            }            
            
            // Get mappings 
            // TODO: multi-thread this
            System.err.print("Loading HashMap for the first index...");
            docIdMap1 = LuceneId2docIdMap(searcher1);
            System.err.println(" done");
            System.err.print("Loading HashMap for the second index...");
            docIdMap2 = LuceneId2docIdMap(searcher2);
            System.err.println(" done");

            System.err.print("Loading Inv. HashMap for the first index...");
            //lucIdMap1 = docId2LuceneIdMap(searcher1);
            System.err.println(" done");
            System.err.print("Loading Inv. HashMap for the second index...");
            //lucIdMap2 = docId2LuceneIdMap(searcher2);
            System.err.println(" done");            

            // The actual retrieval
            int qid = 0;
            String queryID;
            BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            while (true) {
                qid++;
                queryID = String.valueOf(qid);
                System.out.print("Query " + queryID + ": ");
                line = in.readLine();
                if (line == null || line.length() == -1)
                        break;

                runQuery(queryID, line);
                //results = retriever.getHits(line, queryID, true);
                //System.out.println(results);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
         */           
    }

    /** init mixture */
    public void initMixture(String[] configfiles, String[] fields, float[] lambdas) {
        
        luceneConfigFile = configfiles;
        field = fields;
        lambda = lambdas;
        indexNum = luceneConfigFile.length;
        
        for (int i=0;i<indexNum;i++) {
            
            System.err.println("Index #" + (i+1));
            
            System.err.print(" - loading config: " + luceneConfigFile[i] + "...");
            searcher[i] = setProperties(luceneConfigFile[i]);            
            System.err.println(" done");
            
            System.err.print(" - loading HashMap...");
            try {
                docIdMap[i] = LuceneId2docIdMap(searcher[i]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.err.println(" done");        
        }
        
    }
    
    // see Borkur's code for a more general implementation
    public static HashMap LuceneId2docIdMap(IndexSearcher searcher) throws IOException {

        //LGTE
        IndexReader reader = LuceneVersionFactory.getLuceneVersion().getReader(searcher);
        HashMap docIdMap = new HashMap(reader.maxDoc());
        Document doc = null;
        String curdocid = null;
        
        for (int i = 0; i<reader.maxDoc(); i++) {
            doc = reader.document(i);
            curdocid = doc.get("id");
            docIdMap.put(new Integer(i), curdocid);
        }

        return docIdMap;
    }     
    
    // see Borkur's code for a more general implementation
    public static String LuceneId2docId(HashMap docIdMap, Integer docID) throws IOException {
        return (String)docIdMap.get(docID);
    }       
    
    // see Borkur's code for a more general implementation
    public static HashMap docId2LuceneIdMap(IndexSearcher searcher) throws IOException {

        //LGTE
        IndexReader reader = LuceneVersionFactory.getLuceneVersion().getReader(searcher);
        HashMap docIdMap = new HashMap(reader.maxDoc());
        Document doc = null;
        String curdocid = null;
        
        for (int i = 0; i<reader.maxDoc(); i++) {
            doc = reader.document(i);
            curdocid = doc.get("id");
            docIdMap.put(curdocid, new Integer(i));
        }

        return docIdMap;
    }    
    
    // see Borkur's code for a more general implementation
    public static int docId2LuceneId(HashMap docIdMap, String docID) throws IOException {
        Integer i = (Integer)docIdMap.get(docID);
        return (i==null ? -1 : i.intValue());
    }    
    
    // QUICK HACK
    public static int getCollectionTokenNumber(IndexReader in, String field) throws IOException {
	
	int collSize = 0;
	for (int doc = 0; doc < in.maxDoc(); ++doc) {
		collSize += ((LanguageModelIndexReader)in).getFieldLength(doc, field);
	}
	return collSize;
    }
    
    
    public static void runQuery(String queryID, String query){
                
        String[] terms = query.split("[ \t]+");
       
        //HashMap[] scoreMap;
        Hashtable scores = new Hashtable();
               
        // collection lambda
        float cLambda = 1.0f;
        for (int i=0;i<indexNum;i++)
            cLambda -= lambda[i];
        
        float[] sumDFs = new float[indexNum];
        //float[] TF = new float[indexNum];
        //float[] Length = new float[indexNum];
        //Integer[] Id = new Integer[indexNum];
        float TF = -1f;
        Integer Id = null;
        float Length = -1f;

        HashMap scoreMap = new HashMap();

        for (int i=0;i<indexNum;i++) {

            //Id[i] = null;
            //TF[i] = -1f;
            //Length[i] = -1f;
            
            try {
                //LGTE
                sumDFs[i] = (float) getCollectionTokenNumber(
                        LuceneVersionFactory.getLuceneVersion().getReader(searcher[i])
                        , field[i]);
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }

        Comparator resultComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry entry1 = (Map.Entry) o1;
                Map.Entry entry2 = (Map.Entry) o2;
                Comparable item1 = (Comparable) entry1.getValue();
                Comparable item2 = (Comparable) entry2.getValue();
                return item2.compareTo(item1);
           }
        };
		
        // Loop through all the terms in the query
        for (int i=0; i<terms.length; i++) {
            
            // QUICK HACK: no complex terms here
            /*
            String[] complexTerm = terms[i].split(":");
            String term = null;
            if (complexTerm.length == 2){
                    field1 = complexTerm[0];
                    field2 = complexTerm[0];
                    term = complexTerm[1];
            } else if (complexTerm.length == 1 ){
                    term = complexTerm[0];
            }*/

            String term = terms[i];
            ArrayList hits;

            // df is calculated using the 0th index!!!
            float df = 0.0f;
            try {
                df = searcher[0].docFreq(new Term(field[0],term));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            for (int j=0;j<indexNum;j++) {
                
                Object[] hitsx                     
                    = hitListDocLength(new Term(field[j],term), searcher[j]);
                                                
                for (int k=0; k<(hitsx.length-2); k=k+3) {

                    Id = (Integer)hitsx[k];
                    TF = ((Integer)hitsx[k+1]).floatValue();
                    Length = ((Integer)hitsx[k+2]).floatValue();
                
                    try {
                        Id = new Integer(LuceneId2docId(docIdMap[j], Id));
                        
                    } catch (IOException e) {
                        System.err.println("Something wrong with casting, are you using numeric id's?");
                        e.printStackTrace();
                    }
                
                    float comp = (lambda[j] * TF * sumDFs[j]) / (cLambda * df * Length);

                    float prev = 0.0f;
                    if (scoreMap.containsKey(Id)) {
                        prev = ((Float)scoreMap.get(Id)).floatValue();
                    }
                    scoreMap.put(Id, new Float((float)(prev+comp)));
                    
                }               
            } // end: indexes 
            
            Iterator iter = scoreMap.keySet().iterator();

            while(iter.hasNext()) {
                Integer ID = (Integer)iter.next();

                float val = ((Float)scoreMap.get(ID)).floatValue();
                                   
                float score = (float)Math.log(1+val)/(float)Math.log(10);          
                     
                // Check whether there  has been a hit for 
                // a previous term in this document
                if (scores.containsKey(ID))
                    score += ((Float)scores.get(ID)).floatValue();
                
                scores.put(ID, new Float(score));
            }

        } // end: query terms
     
        //listContents(scores);
        
        List list = new ArrayList(scores.entrySet()); 
        Collections.sort(list, resultComparator );

        Iterator iter = list.iterator();
        int count = 1;
        //while(iter.hasNext() && count < 15000){
        while(iter.hasNext() && count < 1000){
            Map.Entry me = (Map.Entry)iter.next();
            String fName = null;
            String eName = null;
            System.out.println(
					queryID
					+ " 0 "
					+ ""//fName
					+ ""//eName
                    + me.getKey()
					+ " "
					+ count
					+ " "
					+ me.getValue()
					+ " UAms");
			count++;
		}
         
	}
    
    public static void listContents(Hashtable scores) {
        
        System.out.println("---\nSize of table: " + scores.keySet().size());                
        Iterator iter = scores.keySet().iterator();
        while (iter.hasNext()) {
            Integer key = (Integer)iter.next();
            System.out.println("Key: " + key);
            System.out.println("Value: " + scores.get(key));
        }
        System.out.println("---");
                
    }
    
    public static String[] getIndexedFields(IndexSearcher searcher)
            throws IOException {
        //LGTE
        Collection fieldsCollection1 = LuceneVersionFactory.getLuceneVersion().getFieldsNames(searcher,true);
        java.util.Iterator it = fieldsCollection1.iterator();
        TreeSet fields = new TreeSet();

        while (it.hasNext()) {
            String fld = (String)it.next();
            fields.add(fld);
        }
        
        return (String[]) fields.toArray(new String[0]); 
    }

    public static Object[] hitListDocLength(Term term, IndexSearcher searcher) {
    	Vector hitListVec = new Vector();
	int[] docs = new int[512]; // buffered doc numbers
	int[] freqs = new int[512]; // buffered term freqs        
        //hitListVec.clear();
   		TermDocs termDocs;
   		try {
			termDocs = LuceneVersionFactory.getLuceneVersion().getReader(searcher).termDocs(term);
                        //System.err.println("Current searcher: " + searcher.getReader().directory().toString());
			int numDocs;
			do {
				numDocs = termDocs.read(docs, freqs);
				for (int i=0; i<numDocs; ++i) {
					hitListVec.addElement(new Integer(docs[i]));
					hitListVec.addElement(new Integer(freqs[i]));
                                        int len = getDocLen(docs[i], searcher);
					hitListVec.addElement(new Integer(len));
				}
			} while (numDocs>0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return hitListVec.toArray();
    }    
    
    public static int getDocLen(int docid, IndexSearcher searcher) {
    	int res = -1;
    	if (docid >= 0 && LuceneVersionFactory.getLuceneVersion().getReader(searcher).numDocs() > docid) {
	    	if (searcher instanceof IndexSearcherLanguageModel) {
	    		IndexSearcherLanguageModel lms = (IndexSearcherLanguageModel)searcher;
	    		LanguageModelIndexReader lmir = lms.getLangModelReader();
	    		try {
					res = lmir.getDocLength(docid);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    	return res;
    }    
    
	public static IndexSearcher setProperties(String fileName) {
            
        BufferedReader input = null;
        String line = "";
        int defaultMaxResults = 1000;
        HashSet stopSet = null;
        IndexSearcher searcher = null;
                
        try {
            LUCENE_PROPERTY_PREFIX = "Lucene_" + new File(fileName).getCanonicalPath() + "/";
            //System.err.println("LPP: " + LUCENE_PROPERTY_PREFIX);                    
            input = new BufferedReader(new FileReader(fileName));
            while ((line = input.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] lineParts = line.split("[\t ]+");
                    if (lineParts.length > 1) {
                        String property = lineParts[1];
                        for (int i = 2; i < lineParts.length; i++) {
                            property += " " + lineParts[i];
                        }
                        System.setProperty(
                            LUCENE_PROPERTY_PREFIX + lineParts[0],
                            property);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }            

        String stopwords =
            System.getProperty(LUCENE_PROPERTY_PREFIX + "stopwords");
        if (stopwords == null) {
            throw new IllegalArgumentException("stopwords not set");
        } 
            System.clearProperty(LUCENE_PROPERTY_PREFIX + "stopwords");
            File stopwordsFile = new File(stopwords);
            if (stopwordsFile == null
                || !stopwordsFile.exists()
                || !stopwordsFile.canRead()) {
                throw new IllegalArgumentException(
                    "can't read stopword file " + stopwords);
		}
            String indexDir = System.getProperty(LUCENE_PROPERTY_PREFIX + "indexDir");
            if (indexDir == null) {
                throw new IllegalArgumentException("indexDir not set");
            }
            System.clearProperty(LUCENE_PROPERTY_PREFIX + "indexDir");
            String model = System.getProperty(LUCENE_PROPERTY_PREFIX + "model");
            if (model == null) {
                throw new IllegalArgumentException("Retrieval model not set");
            }
            System.clearProperty(LUCENE_PROPERTY_PREFIX + "model");
            String stemmer = System.getProperty(LUCENE_PROPERTY_PREFIX + "stemmer");
            if (stemmer != null)
                System.clearProperty(LUCENE_PROPERTY_PREFIX + "stemmer");
            String priorsFile =
                System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "priors");
            if (priorsFile != null) {
                // read priors file
                System.out.println("Opening priors file: " + priorsFile);
                DataCacher.Instance().loadFromFile(priorsFile);
                System.setProperty("priors", "1");
                System.clearProperty(LUCENE_PROPERTY_PREFIX + "priors");
            }
            String maxResultsStr =
                System.getProperty(LUCENE_PROPERTY_PREFIX + "maxResults");
            if (maxResultsStr != null) {
                defaultMaxResults = Integer.valueOf(maxResultsStr).intValue();
                System.clearProperty(LUCENE_PROPERTY_PREFIX + "maxResults");
            }
            
        System.setProperty("RetrievalModel", "LanguageModel");
        if (DataCacher.Instance().get("LM-beta") == null)
                DataCacher.Instance().put("LM-beta", "1.0");
        if (DataCacher.Instance().get("LM-lambda") == null)
                DataCacher.Instance().put("LM-lambda", "0.15");
        if (DataCacher.Instance().get("LM-cmodel") == null)
                DataCacher.Instance().put("LM-cmodel", "df");
        if (DataCacher.Instance().get("LM-lengths") == null)
                DataCacher.Instance().put("LM-lengths", "document");
        Similarity similarity = new LangModelSimilarity();

        try {
            searcher = new IndexSearcherLanguageModel(indexDir);
            ((IndexSearcherLanguageModel) searcher)
                .readExtendedDate(indexDir);

            searcher.setSimilarity(similarity);
            stopSet = WordlistLoader.getWordSet(stopwordsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] stopwordsArray =
                (String[]) stopSet.toArray(new String[stopSet.size()]);
        Analyzer analyzer = new WhitespaceStopLowercaseAnalyzer(stopwordsArray);


        //sumDFs1 = searcher.getReader()
        //return new LuceneRetriever(searcher, analyzer, defaultMaxResults);        
          return searcher;          
    }    
}
