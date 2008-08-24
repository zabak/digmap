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
import java.io.InputStreamReader;
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
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceStopLowercaseAnalyzer;
import org.apache.lucene.analysis.de.WordlistLoader;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;


/**
 *
 * @author Edgar Meij
 */
public class MixtureModelTestPriors {
    
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
    private static String LUCENE_PROPERTY_PREFIX;
    private static String field1;
    private static String field2;
    private static float eLambda = 0.1f;
    private static float dLambda = 0.1f;
    private static float lengthPrior = 1.0f;
    private static boolean useLuceneIds = false;    
    
    
    /** Creates a new instance of MixtureModel */
    public MixtureModelTestPriors() {
    }
    
    
    
    public static void main(String[] args) {
        
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

        //LGTE Changed by Jorge Machado
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
    
    public static void runQuery(String queryID, String query){
        
        String[] terms = query.split("[ \t]+");
        
        HashMap scoreMap1;
        HashMap scoreMap2;
        Hashtable scores = new Hashtable();
               
        float cLambda = 1.0f - eLambda - dLambda;
        float sumDFs1 = -1f;
        float sumDFs2 = -1f;
        
        Integer dId = null;
        float dTF = -1f;
        float dLength = -1f;
        
        Integer elId = null;
        float eTF = -1f;
        float eLength = -1f;
        
        
        try {
            sumDFs1 = (float)
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher1)
                    .getLangModelReader())
                    .getCollectionTokenNumber();
            //.getTotalDocFreqs();
            
            System.out.println("getTotalDocFreqs1: " + 
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher1)
                    .getLangModelReader())
                    .getTotalDocFreqs()                   
                    );
            System.out.println("getTotalDocFreqs2: " + 
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher2)
                    .getLangModelReader())
                    .getTotalDocFreqs()                   
                    );
            System.out.println("getCollectionTokenNumber1: " + 
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher1)
                    .getLangModelReader())
                    .getCollectionTokenNumber()
                    );
            System.out.println("getCollectionTokenNumber2: " + 
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher2)
                    .getLangModelReader())
                    .getCollectionTokenNumber()
                    );            
            
            sumDFs2 = (float)
                    ((LanguageModelIndexReader)
                    ((IndexSearcherLanguageModel) searcher2)
                    .getLangModelReader())
                    .getCollectionTokenNumber();            
            //.getTotalDocFreqs();
            
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        //System.out.println("sumDFs1: " + sumDFs1);
        //System.out.println("sumDFs2: " + sumDFs2);

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
            scoreMap1 = new HashMap();
            scoreMap2 = new HashMap();
            String[] complexTerm = terms[i].split(":");
            String term = null;
            if (complexTerm.length == 2){
                    field1 = complexTerm[0];
                    field2 = complexTerm[0];
                    term = complexTerm[1];
            } else if (complexTerm.length == 1 ){
                    term = complexTerm[0];
            }

            // TODO: multi-thread this
            Object[] hits1 
                    //= hitListDocLength(new Term("AB",term), searcher1);
                    = hitListDocLength(new Term(field1,term), searcher1);

            //System.out.println("query1: " + new Term(field1,term).toString());
            
            Object[] hits2 
                    //= hitListDocLength(new Term("TI",term), searcher2);
                    = hitListDocLength(new Term(field2,term), searcher2);
            
            //System.err.println("Term '" + term + "' in '"+ field + "', hits: " + lucene1.getSearcher().search(new Term(field,term)).toString());

            float df = 0.0f;
            float ef = 0.0f;
            try {
                df = searcher1.docFreq(new Term(field1,term));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
			
            System.err.println("hits1: " + (hits1.length / 3));
            // Looping through first Hits object
            // TODO: convert to internal id, instead of Lucene ID
            for(int j=0; j<(hits1.length-2); j=j+3) {

                elId = (Integer)hits1[j];
                eTF = ((Integer)hits1[j+1]).floatValue();
                eLength = ((Integer)hits1[j+2]).floatValue();
                
                try {
                    elId = (Integer)Integer.parseInt(LuceneId2docId(docIdMap1, elId));
                } catch (IOException e) {
                    System.err.println("Something wrong with casting, are you using numeric id's?");
                    e.printStackTrace();
                }
                
                // TODO: add up scores for multiple queryterms
                
                scoreMap1.put( elId, 
                        new float[]{
                            ((Integer)hits1[j+1]).floatValue(), 
                            ((Integer)hits1[j+2]).floatValue()
                        });
                //System.out.println((j/3+1) + ":");
                //System.out.println("\tID: " + elId);
                //System.out.println("\teTF: " + eTF);
                //System.out.println("\teLength: " + eLength);
                //System.out.println("Found id: " + elId + ", is this a LuceneDocID?");
                //System.out.println(docId + " " + elId);
                //System.out.println(fileMap.get(new Integer(docId)));
            }
            //System.out.println("Found id: " + elId + ", is this a LuceneDocID?");
            
            System.err.println("hits2: " + (hits2.length / 3)); 
            System.err.println("------------------\n");
            // Looping through second Hits object
            for(int j=0; j<(hits2.length-2); j=j+3) {
                
                dId = (Integer)hits2[j];
                dTF = ((Integer)hits2[j+1]).floatValue();
                dLength = ((Integer)hits2[j+2]).floatValue();
                
                try {
                    dId = (Integer)Integer.parseInt(LuceneId2docId(docIdMap2, dId));
                } catch (IOException e) {
                    System.err.println("Something wrong with casting, are you using numeric id's?");
                    e.printStackTrace();
                }        
                
                // TODO: Add earlier scores.
                
                scoreMap2.put( dId, 
                        new float[]{
                            ((Integer)hits2[j+1]).floatValue(), 
                            ((Integer)hits2[j+2]).floatValue()
                        });
                

                //System.out.println((j/3+1) + ":");
                //System.out.println("\tID: " + dId);
                //System.out.println("\tdTF: " + dTF);
                //System.out.println("\tdLength: " + dLength);                
                //System.out.println("Found id: " + dId + ", is this a LuceneDocID?");
                //System.out.println(docId + " " + elId);
                //System.out.println(fileMap.get(new Integer(docId)));
            }

            Iterator iter1 = scoreMap1.keySet().iterator();
            int cnt = 0;

            while(iter1.hasNext()) {
                cnt++;
                Integer elID = (Integer)iter1.next();
                float score = 0.0f;
                float[] dVals = new float[2];
                float[] eVals = new float[2];

                eVals = (float[])scoreMap1.get(elID);
                eTF = eVals[0];
                eLength = eVals[1];

                if (scoreMap2.containsKey(elID)) {
                    dVals = (float[])scoreMap2.get(elID);
                    dTF = dVals[0];
                    dLength = dVals[1];
                // TODO: incldue length prior
                    
                score += Math.log(
                    1 
                    + ((eLambda * eTF * sumDFs1)
                            / (cLambda * df * eLength))
                    + ((dLambda * dTF * sumDFs2)
                            / (cLambda * df * dLength)))/(float)Math.log(10);            
                    
                } else {
                    dVals = new float[]{0.0f, 0.0f};
                    dTF = dVals[0];
                    dLength = dVals[1];
                    
                // TODO: incldue length prior
                score += Math.log(
                    1 
                    + ((eLambda * eTF * sumDFs1)
                            / (cLambda * df * eLength))
                    + 0)/(float)Math.log(10);            
                    
                }

                // Check whether there  has been a hit for 
                // a previous term in this document
                if (scores.containsKey(elID))
                    score += ((Float)scores.get(elID)).floatValue();
                
                scores.put(elID, new Float(score));
                System.err.println( cnt +
                              ": term '" + term
                            + "' (docID " + elID + ")"
                            + "\n\t eTF " + eTF 
                            + "\n\t dTF " + dTF 
                            + "\n\t eLen " + eLength
                            + "\n\t dLen " + dLength
                            + "\n\t df " + df
                            + "\n\t eSumDFs " + sumDFs1
                            + "\n\t dSumDFs " + sumDFs2
                            + "\n\t score " + score
                );
                //listContents(scores);
            }
            
            Iterator iter2 = scoreMap2.keySet().iterator();
            cnt = 0;

            while(iter2.hasNext()) {
                cnt++;
                Integer dID = (Integer)iter2.next();
                float score = 0.0f;
                float[] dVals = new float[2];
                float[] eVals = new float[2];

                dVals = (float[])scoreMap2.get(dID);
                dTF = dVals[0];
                dLength = dVals[1];

                if (scoreMap1.containsKey(dID)) {
                    // Already calculated this score
                    //dVals = (float[])scoreMap1.get(dID);
                } else {
                    eVals = new float[]{0.0f, 0.0f};
                    eTF = eVals[0];
                    eLength = eVals[1];

                    // TODO: incldue length prior
                    score += Math.log(
                        1 
                        + 0
                        + ((dLambda * dTF * sumDFs2)
                                / (cLambda * df * dLength)))/(float)Math.log(10);            

                    // Check whether there  has been a hit for 
                    // a previous term in this document
                    if (scores.containsKey(dID))
                        score += ((Float)scores.get(dID)).floatValue();
                    scores.put(dID, new Float(score));
                    
                    System.err.println(cnt+
                              ": term '" + term
                            + "' (docID " + dID + ")"
                            + "\n\t eTF " + eTF 
                            + "\n\t dTF " + dTF 
                            + "\n\t eLen " + eLength
                            + "\n\t dLen " + dLength
                            + "\n\t df " + df
                            + "\n\t eSumDFs " + sumDFs1
                            + "\n\t dSumDFs " + sumDFs2
                            + "\n\t score " + score
                    );                    
                    
                    //listContents(scores);
                
                }
            }            
        }
                
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
        Collection fieldsCollection1 = LuceneVersionFactory.getLuceneVersion().getFieldsNames(searcher, true);
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
               //LGTE
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
        //LGTE
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
