package org.apache.lucene.ilps;

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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.ilps.StreamReaderThread;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;


class IndexPreprocessFiles {

  static TreeMap docs = new TreeMap();
  static boolean storeTermVectors = true;

  static String preprocessor  = null;
  static String topDir        = null;
  static String indexDir      = null;
  static File stopwordsFile   = null;
  static String model         = null;
  static String configFile    = null;
  static String stemmer       = null;
  static String analyzer_name = null;    // KH 2006/10/10

  static final int MAX_BUFFERED_DOCS = 5000;  // max docs in local buffer, before adding to 
                                              // Lucene's index

  static int totalDocsIndexed = 0;
  static Date start;

  public static void setProperties() {
	String stopwords = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "stopwords");
	if (stopwords == null) {
		throw new IllegalArgumentException("stopwords not set");
	}
	stopwordsFile = new File(stopwords);
	if (stopwordsFile == null || !stopwordsFile.exists() || !stopwordsFile.canRead()) {
		throw new IllegalArgumentException("can't read stopword file " + stopwords);
	}
	indexDir = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "indexDir");
	if (indexDir == null) {
		throw new IllegalArgumentException("indexDir not set");
	}
	topDir = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "topDir");
	if (topDir == null) {
		throw new IllegalArgumentException("topDir not set");
	}
	model = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "model");
	if (model == null) {
		throw new IllegalArgumentException("Retrieval model not set");
	}
	preprocessor = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "preprocessor");
	stemmer = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "stemmer");
	analyzer_name = System.getProperty(Retrieve.LUCENE_PROPERTY_PREFIX + "analyzer");
    System.setProperty("org.apache.lucene.maxFieldLength", "100000");
  }
  

  public static void usage() {
	String usage = "Usage: java " + IndexPreprocessFiles.class.getName() +  
	" -c <config_file>";
	System.err.println(usage);
	System.exit(-1);
  }

  public static void main(String[] args) throws IOException {
	start = new Date();
    System.out.println("Indexing started at " + start.toString());

    for (int i=0; i<args.length; ++i) {
    	if (args[i].equals("-h")) 
			usage();
		else if (args[i].equals("-c")) {
			i++;
			if (i>=args.length)
				throw new IllegalArgumentException("specify config file after -c");
			configFile = args[i];
		} else 
			System.err.println("Unsupported argument " + args[i]);
    }
    if (configFile == null) {
		usage();
    }

    Retrieve retr = new Retrieve();
    retr.readConfigFile(configFile);
    setProperties();
	
    try {
		HashSet stopSet = WordlistLoader.getWordSet(stopwordsFile);
		String[] stopwords = (String[]) stopSet.toArray(new String[stopSet.size()]);
        IndexWriter writer = null; 
        if (analyzer_name != null) {
            writer = new IndexWriter(indexDir, 
      	        (Analyzer)Class.forName(analyzer_name).newInstance(), true);
        } else {
            writer = new IndexWriter(indexDir, 
      	        new SnowballAnalyzer(stemmer, stopwords), true);
        }
      if (model.equals("lm")) {
		System.setProperty("RetrievalModel", "LanguageModel");
		storeTermVectors = true;
      	writer.setSimilarity(new LangModelSimilarity());
      } else if (model.equals("vs")) {
		System.setProperty("RetrievalModel", "VectorSpace");
		writer.setSimilarity(new DefaultSimilarity());
      } else {
      	System.err.println("Unknown retrieval model " + model);
      	throw new IllegalArgumentException();
      }
      indexDocs(writer, preprocessor, new File(topDir));
      writer.optimize();
      writer.close();

	  if (storeTermVectors) {
		  // store some cached data
		  IndexSearcherLanguageModel searcher = new IndexSearcherLanguageModel(indexDir);
		  searcher.storeExtendedData(indexDir);
	  }

      // write docid to internal id mapping
      String docidFile = indexDir + "/docid.txt";
      System.out.println("Writing docid file to " + docidFile);
      FileWriter outFile = new FileWriter(docidFile);
      PrintWriter fileOutput = new PrintWriter(outFile);
      IndexReader reader = IndexReader.open(indexDir);
      String docid = null;
      Document doc = null;
      for (int i=0; i<reader.maxDoc(); ++i) {
          doc = reader.document(i);
          docid = doc.get("id");
          fileOutput.println(i + " " + docid);
      }
      fileOutput.close();
      outFile.close();
      reader.close();

      // finally - copy config file to the indexed dir to "remember" how it was indexed
      String cpCmd = "cp " + configFile + " " + indexDir;
      Process p = Runtime.getRuntime().exec(cpCmd);
      System.out.println("Config file saved to " + indexDir + " / " + configFile);

      Date end = new Date();

      System.out.print(end.getTime() - start.getTime());
      System.out.println(" total milliseconds");
      System.out.println("Indexing ended at " + end.toString());


    } catch (IOException e) {
      System.out.println(" >> caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
       e.printStackTrace();
    } catch (ClassNotFoundException e) {    // thrown by Class.forName()
      System.out.println(" >> caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
       e.printStackTrace();
    } catch (InstantiationException e) {    // thrown by Class.newInstance()
      System.out.println(" >> caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
       e.printStackTrace();
    } catch (IllegalAccessException e) {    // thrown by Class.newInstance()
      System.out.println(" >> caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
       e.printStackTrace();
    }
  }

  public static void indexDocs(IndexWriter writer, String preprocessor, File file)
    throws IOException {

    int numDocsIndexed = -1;
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        // an IO error could occur
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(writer, preprocessor, new File(file, files[i]));
          }
        }
      } else {
          numDocsIndexed = 0;
          System.out.print("adding " + file + "... ");
          BufferedReader input = null;
          try {
              // first preprocess docs
              if (preprocessor != null) {
                  Process p = Runtime.getRuntime().exec(preprocessor + " " + file);
                  // get output/err streams
                  StringBuffer err = new StringBuffer();
                  StringBuffer out = new StringBuffer();
                  StreamReaderThread outThread =
                      new StreamReaderThread(p.getInputStream(), out);
                  StreamReaderThread errThread =
                      new StreamReaderThread(p.getErrorStream(), err);
                  outThread.start();
                  errThread.start();
                  try {
                      int result = p.waitFor();
                      outThread.join();
                      errThread.join();
                  } catch (java.lang.InterruptedException e) {
                      e.printStackTrace();
                  }
                  String errors = err.toString();
                  if (errors.length() > 0) {
                      System.err.println("Errors encountered:\n" + errors);
                  }
                  input = new BufferedReader(new StringReader(out.toString()));
              } else {
                  input = new BufferedReader(new FileReader(file));
              }
              String line;
              String docid = "";
              String type = "";
              String fieldName = "";
              String fieldData = "";
              int lineno = 0;
              try {
                while ((line = input.readLine()) != null) {
                	++lineno;
                    line = line.trim();
                    fieldData = "";
                    if (line.length() == 0) {
                        continue;
                    }
                    String [] lineParts = line.split("\t");
                    try {
						docid     = lineParts[0];
						type      = lineParts[1];
						fieldName = lineParts[2];
						fieldData = lineParts[3];
                    } catch (ArrayIndexOutOfBoundsException e) {
                    	System.err.println("Suspicious line " + lineno + ": "+ line);
                    	e.printStackTrace();
                    }
                    fieldData = fieldData.trim();
                    if (fieldData == null || fieldData.length() == 0) {
                    	System.err.println("Warning: empty line " + lineno + " (after processing)");
                    }
                    Document doc;
                    if (docs.containsKey(docid)) {
                        doc = (Document)docs.get(docid);
                    } else {
                        if (docs.size() >= MAX_BUFFERED_DOCS) {
                            indexCurrentDocs(writer);
                        }
                        doc = new Document();
                        //LGTE
                        LuceneVersionFactory.getLuceneVersion().addFieldUnIndexed(doc,"id",docid);
                        docs.put(docid, doc);
                    }
                    if (doc.getField(fieldName) != null) {
                        String oldVal = doc.getField(fieldName).stringValue();
                        fieldData = oldVal + " " + fieldData;
                        doc.removeField(fieldName);
                    }
                    if (type.equalsIgnoreCase("*")) {
						LuceneVersionFactory.getLuceneVersion().addFieldText(doc,fieldName, fieldData, storeTermVectors);
                    } else if (type.equalsIgnoreCase("s")) {
						LuceneVersionFactory.getLuceneVersion().addFieldUnIndexed(doc,fieldName, fieldData);
					} else if (type.equalsIgnoreCase("i")) {
						LuceneVersionFactory.getLuceneVersion().addFieldUnStored(doc,fieldName, fieldData, storeTermVectors);
                    } else {
                    	System.err.println("Unknown type in line " + lineno + 
						": " + line);
                    }
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // at least on windows, some temporary files raise this exception with an "access denied" message
            // checking if the file can be read doesn't help
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
      }
    }
    indexCurrentDocs(writer);
  }
    
    static void indexCurrentDocs(IndexWriter writer) {
      Set keys = docs.keySet();
      int numDocsIndexed = docs.size();
      Iterator iter = keys.iterator();
      while (iter.hasNext()) {
      	  String docID = (String)iter.next(); 
      	  Document doc = (Document)docs.get(docID);
          try {
              writer.addDocument(doc);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
      docs.clear();
      if (numDocsIndexed > 0) {
          Date now = new Date();
          totalDocsIndexed += numDocsIndexed;
          System.out.println(numDocsIndexed + " docs converted and indexed (total " + totalDocsIndexed + "), time " + now.toString());
      } else if (numDocsIndexed == 0) {
          System.out.println("no docs found to be indexed");
      }
    }
}
