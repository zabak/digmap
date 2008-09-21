package org.apache.lucene.aid;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.WhitespaceStopAnalyzer;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.ilps.*;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

public class Retrieve {

	private static Similarity similarity;
	private static Analyzer analyzer;
	private static IndexSearcher searcher;
	private static boolean interactive = true;

	private static boolean isServer = false;
	private static boolean doNothing = false;
	private static String configFile = null;
	private static File stopwordsFile = null;
	private static String indexDir = null;
	private static String queryFile = null;
	private static String model = null;
	private static String collectionType = null;
	private static String stemmer = null;
	private static int defaultMaxResults = Integer.MAX_VALUE;
	private static final int[] docs = new int[512]; // buffered doc numbers
	private static final int[] freqs = new int[512]; // buffered term freqs
	private static final Vector hitListVec = new Vector();

	private static LuceneRetriever retriever = null;

    private static HashMap docIdMap = null;

	public static final String LUCENE_PROPERTY_PREFIX = "Lucene_";
	public static final String LUCENE_DEFAULT_FIELD = "content";
	public static final int LUCENE_PORT = 5050;

    public Retrieve() {
    }

	public LuceneRetriever getRetriever() {
        return retriever;
	}
	
	public Searcher getSearcher() {
        return searcher;
	}
	
	public Similarity getSimilarity() {
        return similarity;
	}
	
	public Analyzer getAnalyzer() {
        return analyzer;
	}
	
	private static void usage() {
		String usage =
			"java "
				+ Retrieve.class.getName()
				+ "\n"
				+ "-c <config_file>\n"
				+ "-q <query-file, query per line>   OR\n"
				+ "-f <query-file (flex-style)>   OR\n"
				+ "-s (server mode)\n"
				+ "  if -s or -f not used, interactive session starts\n"
				+ "[-p <port-number>] (uses 5050 if not specified)\n";
		System.err.println("Usage: " + usage);
		System.exit(-1);
	}

    public static String luceneId2DocId(int luceneId) {
        String docId = "";
        Document doc = null;
		try {
            //LGTE
            doc = LuceneVersionFactory.getLuceneVersion().getReader(searcher).document(luceneId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doc != null) {
            docId = doc.get("id");
        }
        return docId;
    }

    public static int docId2LuceneId(String docID) throws IOException {
        if (docIdMap == null) {
            //LGTE
            IndexReader reader = LuceneVersionFactory.getLuceneVersion().getReader(searcher);
            docIdMap = new HashMap(reader.maxDoc());
            Document doc = null;
            String curdocid = null;
            for (int i = 0; i<reader.maxDoc(); i++) {
                doc = reader.document(i);
                curdocid = doc.get("id");
                docIdMap.put(curdocid, new Integer(i));
            }
        }
        Integer i = (Integer)docIdMap.get(docID);
        return (i==null ? -1 : i.intValue());
    }

    public static int getCollectionTokenNumber() {
    	int res = -1;
    	if (searcher instanceof IndexSearcherLanguageModel) {
    		IndexSearcherLanguageModel lms = (IndexSearcherLanguageModel)searcher;
    		LanguageModelIndexReader lmir = lms.getLangModelReader();
			try {
				res = lmir.getCollectionTokenNumber();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return res;
    }
    
    public static int getDocLen(int docid) {
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
    
    public static int getCollFreq(Term t) {
    	int res = -1;
    	if (searcher instanceof IndexSearcherLanguageModel) {
    		IndexSearcherLanguageModel lms = (IndexSearcherLanguageModel)searcher;
    		LanguageModelIndexReader lmir = lms.getLangModelReader();
    		try {
				res = lmir.collFreq(t);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return res;
    }
    
    public static int getFieldLen(String field, int docid) {
    	int res = -1;
        //LGTE
        if (docid >= 0 && LuceneVersionFactory.getLuceneVersion().getReader(searcher).numDocs() > docid) {
	    	if (searcher instanceof IndexSearcherLanguageModel) {
	    		IndexSearcherLanguageModel lms = (IndexSearcherLanguageModel)searcher;
	    		LanguageModelIndexReader lmir = lms.getLangModelReader();
	    		try {
					res = lmir.getFieldLength(docid, field);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    	return res;
    }
    
    public static String hitList2String(Object[] arr) {
    	int estimatedBufferSize = (arr.length)*10;  
    	StringBuffer buf = new StringBuffer(estimatedBufferSize);
    	for (int i=0; i<arr.length-1;) {
    		buf.append(arr[i++]).append(":").append(arr[i++]).append("\n");
    	}
    	return buf.toString();
    }
    
    public static String hitListDocLength2String(Object[] arr) {
    	int estimatedBufferSize = (arr.length)*10;  
    	StringBuffer buf = new StringBuffer(estimatedBufferSize);
    	for (int i=0; i<arr.length-1;) {
    		buf.append(arr[i++]).append(":").append(arr[i++]).
                append(":").append(arr[i++]).append("\n");
    	}
    	return buf.toString();
    }
    
    public static Object[] hitListDocLength(Term term) {
    	hitListVec.clear();
   		TermDocs termDocs;
   		try {
               //LGTE
            termDocs = LuceneVersionFactory.getLuceneVersion().getReader(searcher).termDocs(term);
               //LGTE
                        System.out.println("Current searcher: " + LuceneVersionFactory.getLuceneVersion().getReader(searcher).directory().toString());
			int numDocs;
			do {
				numDocs = termDocs.read(docs, freqs);
				for (int i=0; i<numDocs; ++i) {
					hitListVec.addElement(new Integer(docs[i]));
					hitListVec.addElement(new Integer(freqs[i]));
                    int len = getDocLen(docs[i]);
					hitListVec.addElement(new Integer(len));
				}
			} while (numDocs>0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return hitListVec.toArray();
    }
    
    public static Object[] hitList(Term term) {
    	hitListVec.clear();
   		TermDocs termDocs;
   		try {
               //LGTE
            termDocs = LuceneVersionFactory.getLuceneVersion().getReader(searcher).termDocs(term);
			int numDocs;
			do {
				numDocs = termDocs.read(docs, freqs);
				for (int i=0; i<numDocs; ++i) {
					hitListVec.addElement(new Integer(docs[i]));
					hitListVec.addElement(new Integer(freqs[i]));
				}
			} while (numDocs>0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return hitListVec.toArray();
    }
    
	public static void setProperties() {

		String stopwords =
			System.getProperty(LUCENE_PROPERTY_PREFIX + "stopwords");
		if (stopwords == null) {
			throw new IllegalArgumentException("stopwords not set");
		}
		stopwordsFile = new File(stopwords);
		if (stopwordsFile == null
			|| !stopwordsFile.exists()
			|| !stopwordsFile.canRead()) {
			throw new IllegalArgumentException(
				"can't read stopword file " + stopwords);
		}
		indexDir = System.getProperty(LUCENE_PROPERTY_PREFIX + "indexDir");
		if (indexDir == null) {
			throw new IllegalArgumentException("indexDir not set");
		}
		model = System.getProperty(LUCENE_PROPERTY_PREFIX + "model");
		if (model == null) {
			throw new IllegalArgumentException("Retrieval model not set");
		}
		stemmer = System.getProperty(LUCENE_PROPERTY_PREFIX + "stemmer");
        String priorsFile =
            System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "priors");
        if (priorsFile != null) {
            // read priors file
            System.out.println("Opening priors file: " + priorsFile);
            DataCacher.Instance().loadFromFile(priorsFile);
            System.setProperty("priors", "1");
        }
		String maxResultsStr =
			System.getProperty(LUCENE_PROPERTY_PREFIX + "maxResults");
		if (maxResultsStr != null) {
			defaultMaxResults = Integer.valueOf(maxResultsStr).intValue();
		}
		collectionType =
			System.getProperty(LUCENE_PROPERTY_PREFIX + "collectionType");
		if (collectionType == null) {
			collectionType = "normal";
		}

	}

	public static void readConfigFile(String fileName) {
		BufferedReader input = null;
		String line = "";
		try {
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
	}

	public static void main(String[] args) {

                boolean flexSyntax = false;

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-h")) {
				usage();
			} else if (args[i].equals("-c")) {
				i++;
				if (i >= args.length)
					throw new IllegalArgumentException("specify config file after -c");
				configFile = args[i];
			} else if (args[i].equals("-s")) {
				isServer = true;
			} else if (args[i].equals("-q")) {
				interactive = false;
				i++;
				if (i >= args.length)
					throw new IllegalArgumentException("specify query file after -q");
				queryFile = args[i];
			} else if (args[i].equals("-f")) {
				interactive = false;
				flexSyntax = true;
				i++;
				if (i >= args.length)
					throw new IllegalArgumentException("specify query file after -f");
				queryFile = args[i];
			} else if (args[i].equals("-p")) {
					i++;
					if (i >= args.length)
							throw new IllegalArgumentException("specify port after -p");
					DataCacher.Instance().put("Lucene-port",  Integer.valueOf(args[i]));

			} else if (args[i].equals("-X")) {
				i++;
				if (i >= args.length)
					throw new IllegalArgumentException("specify propery after -X");
				String[] prop = args[i].split("=");
				if (prop.length != 2)
					throw new IllegalArgumentException("propery should have format proerty=value");
				DataCacher.Instance().put(prop[0], prop[1]);
			} else if (args[i].equals("-doNothing")) {
				doNothing = true;
			} else
				System.err.println("Unsupported argument " + args[i]);
		}
		if (configFile == null) {
			usage();
		}

		readConfigFile(configFile);
		setProperties();

		searcher = null;	
		if (model.equals("vs")) {
			System.setProperty("RetrievalModel", "VectorSpace");
			similarity = new DefaultSimilarity();
			try {
				searcher = new IndexSearcher(indexDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        } else if (model.equals("lm")) {
			System.setProperty("RetrievalModel", "LanguageModel");
			if (DataCacher.Instance().get("LM-beta") == null)
				DataCacher.Instance().put("LM-beta", "1.0");
			if (DataCacher.Instance().get("LM-lambda") == null)
				DataCacher.Instance().put("LM-lambda", "0.15");
			if (DataCacher.Instance().get("LM-cmodel") == null)
				DataCacher.Instance().put("LM-cmodel", "df");
			if (DataCacher.Instance().get("LM-lengths") == null)
				DataCacher.Instance().put("LM-lengths", "document");
			similarity = new LangModelSimilarity();
			//			System.out.println("Using lambda:"+
			//			((Float)DataCacher.Instance().get("LM-lambda")).floatValue());
			try {
				searcher = new IndexSearcherLanguageModel(indexDir);
				((IndexSearcherLanguageModel) searcher).readExtendedDate(
					indexDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// TODO: clean this - execute only when arg given
			//	((IndexSearcherLanguageModel)searcher).storeExtendedData(indexDir);
		} else {
			throw new IllegalArgumentException(
				"unknown retrieval model " + model);
		}
		try {
			searcher.setSimilarity(similarity);
			HashSet stopSet = WordlistLoader.getWordSet(stopwordsFile);
			String[] stopwords =
				(String[]) stopSet.toArray(new String[stopSet.size()]);
			analyzer = new WhitespaceStopAnalyzer(stopwords);
			String queryID = "";

			retriever = new LuceneRetriever(searcher, analyzer, defaultMaxResults);

			String results;
			if (isServer) {
				retriever.startServer();
			} else if (doNothing) {
                // for Perl access, which uses this class to submit queries
				return;
			} else if (interactive) {
		                int qid = 0;
				BufferedReader in =
					new BufferedReader(new InputStreamReader(System.in));
				String line = null;
				while (true) {
                                        qid++;
                                        queryID = String.valueOf(qid);
					System.out.print("Query" + queryID + ": ");
					line = in.readLine();
					if (line == null || line.length() == -1)
						break;
					results = retriever.getHits(line, queryID, interactive);
					System.out.println(results);
				}
			} else if (!flexSyntax) {
				BufferedReader input =
					new BufferedReader(new FileReader(queryFile));
				String line = null;
				queryID = "-1";
				while ((line = input.readLine()) != null) {
					results = retriever.getHits(line, queryID, interactive);
					System.out.print(results);
                                }
                                
			} else {
				BufferedReader input =
					new BufferedReader(new FileReader(queryFile));
				String line = null;
				String query = "";
				queryID = "-1";
				while ((line = input.readLine()) != null) {
					if (line.length() < 1)
						continue;
					if (line.startsWith(".i ")) {
						if (query.length() > 0) {
							results =
								retriever.getHits(query, queryID, interactive);
							System.out.println(results);
						}
						String[] lineParts = line.split("[\t ]");
						queryID = lineParts[1];
						query = "";
					} else {
						line.replaceAll("\\s", "");
						query += " " + line;
					}
				}
				if (query.length() > 0) {
					results = retriever.getHits(query, queryID, interactive);
					System.out.println(results);
				}
			}
			try {
				searcher.close();
			} catch (Exception e) {
				System.out.println(
					" caught a "
						+ e.getClass()
						+ "\n with message: "
						+ e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(
				" caught a "
					+ e.getClass()
					+ "\n with message: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}
}
