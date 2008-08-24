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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

public class LuceneRetriever {

	protected Similarity similarity;
	protected Analyzer analyzer;
	protected Searcher searcher;
	protected int defaultMaxResults;

	LuceneRetriever() {
		this.similarity = null;
		this.analyzer = null;
		this.searcher = null;
		this.defaultMaxResults = Integer.MAX_VALUE;
	}

	public LuceneRetriever(Searcher searcher, Analyzer analyzer, int defaultMaxResults) {
		this.searcher = searcher;
		this.similarity = searcher.getSimilarity();
		this.analyzer = analyzer;
		this.defaultMaxResults = defaultMaxResults;
	}

	public void setAnalyzer(Analyzer a) {
		analyzer = a;
	}

	public void setSearcher(Searcher s) {
		searcher = s;
		similarity = s.getSimilarity();
	}

	public void startServer() {
		SocketThrdServer server = new SocketThrdServer();
		server.listenSocket(this);
	}

	/**
	 * Retruns the name of a docuement with the id docID. 
	 * Note that this is collection dependent value. 
	 * @param docID 
	 * @return name of the document ... to be fed to the retrieval file
	 */
	public String getDocumentName(String docID) {
		return docID;
	}

	public Hits getHitsAux(String queryString, String queryID, boolean interactive, int maxResults) {

		Query query = null;

		if (queryString == null || queryString.length() == 0) {
			return null;
		}

		try {
            //LGTE
            query =
                    LuceneVersionFactory.getLuceneVersion()
                            .parseQuery(
                                    queryString,
                                    Retrieve.LUCENE_DEFAULT_FIELD,
                                    analyzer);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (interactive)
			System.out.println(
				"Searching for: "
					+ query.toString(Retrieve.LUCENE_DEFAULT_FIELD));

		if (query == null) {
			return null;
		}

		//Blind feedback 
		if (DataCacher.Instance().get("FB-ponte") != null) {
			Hits hits = null;
			try {
				hits = searcher.search(query);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			int feedbackDocs =
				Integer.parseInt(
					(String) DataCacher.Instance().get("FB-ponte-docs"));
			int expandSize =
				Integer.parseInt(
					(String) DataCacher.Instance().get("FB-ponte-terms"));
			int[] relDocs = new int[Math.min(feedbackDocs,hits.length())];
			System.err.println(
				"Ponte-feedback: Docs: "
					+ feedbackDocs
					+ " terms: "
					+ expandSize);
			int tmpMaxResults = maxResults;
			maxResults = feedbackDocs;

			for (int i = 0; i < hits.length() && i < feedbackDocs; i++) {
				try {
					relDocs[i] = hits.id(i);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			LanguageModelIndexReader ir = null;
			try {
				ir =
					new LanguageModelIndexReader(
						IndexReader.open(
							System.getProperty(
								Retrieve.LUCENE_PROPERTY_PREFIX + "indexDir")));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			if(relDocs.length > 0){
			Query expandedQuery =
				PonteFeedback.getExpansionTerms(ir, query, relDocs, expandSize);
			try{
				//System.err.println("Old query: " + query.toString(Retrieve.LUCENE_DEFAULT_FIELD));
				//System.err.println("Expanded query: " + expandedQuery.toString());

                //LGTE
                query = LuceneVersionFactory.getLuceneVersion().parseQuery(query.toString(Retrieve.LUCENE_DEFAULT_FIELD) + " " + expandedQuery.toString(),
						Retrieve.LUCENE_DEFAULT_FIELD,
						analyzer);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
			//System.err.println("New query: " + query.toString());
			maxResults = tmpMaxResults;
		}

		// The actual retrieval
		Hits hits = null;
		try {
			hits = searcher.search(query);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

                return hits;
        }
        
	public String getHits(String queryString, String queryID, boolean interactive) {
		String results = "";
                String fieldsToGet = null;
		boolean getPositionInfo = false;
		boolean countOnly= false;
                int maxResults = defaultMaxResults;

		// get ilps-specific options
		String[] lineParts = queryString.split("[ \t]+");
		for (int i = 0; i < lineParts.length; ++i) {
			if (lineParts[i].startsWith(".")) {
				int equalsIdx = lineParts[i].indexOf('=');
				if (equalsIdx != -1) {
					String paramName = lineParts[i].substring(1, equalsIdx);
					String paramValue = lineParts[i].substring(equalsIdx + 1);
					if (paramName.equals("maxResults")) {
						maxResults = Integer.valueOf(paramValue).intValue();
					} else if (paramName.equals("withPosition")) {
						getPositionInfo = true;
					} else if (paramName.equals("countOnly")) {
						countOnly = true;
					} else if (paramName.equals("queryID")) {
						queryID = paramValue;
					} else if (paramName.equals("getFields")) {
						fieldsToGet = paramValue;
					} else {
						System.err.println("Unknown parameter " + lineParts[i]);
					}
				}
				// remove from the line, because it is sent to the query parser
				queryString = queryString.replaceFirst(lineParts[i], "");
			}
		}

		Hits hits = getHitsAux(queryString, queryID, interactive, maxResults);

		if (interactive)
			System.out.println(hits.length() + " total matching documents");

		Document doc = null;
		float score = 0f;
		String docID = null;

		if (countOnly) {
			results = hits.length() + "\n";
		} else {
			for (int i = 0; i < hits.length() && i < maxResults; i++) {
				try {
					score = hits.score(i);
					doc = hits.doc(i);
					docID = doc.get("id");
					if (docID == null) {
						if (interactive)
							System.err.println("Document with no ID in collection");
					} else {
	
						results += queryID
							+ " 0 "
							+ getDocumentName(docID)
							+ " 0 "
							+ score
							+ " 0";
	                    if (getPositionInfo) {
	                        String position = null;
	                        position = doc.get("position");
	                        if (position != null) {
	                            results += " " + position;
	                        }
	                    }
                        if (fieldsToGet != null) {
                            results += "\t";
                            String[] fields = fieldsToGet.split(",");
                            for (int j=0; j<fields.length; ++j) {
                                results += fields[j] + "\t" + doc.get(fields[j]) + "\t";
                            }
                        }
	                    results += "\n";
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return results;
	}
}

class SocketThrdServer {

	ServerSocket server = null;

	SocketThrdServer() {
	}

	protected void finalize() {
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Could not close socket");
			System.exit(-1);
		}
	}

	public void listenSocket(LuceneRetriever retriever) {
			int port = Retrieve.LUCENE_PORT;
			if (DataCacher.Instance().get("Lucene-port") != null)
					port = ((Integer)DataCacher.Instance().get("Lucene-port")).intValue();

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(
				"Could not listen on port " + port);
			System.exit(-1);
		}
		System.out.println("Listening on port " + port);
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(retriever, server.accept());
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				System.err.println("Accept failed: " + Retrieve.LUCENE_PORT);
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}

class ClientWorker implements Runnable {
	private Socket client;
	private LuceneRetriever retriever;
    
	ClientWorker(LuceneRetriever retriever, Socket client) {
		this.retriever = retriever;
		this.client = client;
	}
    
	public void run() {
		String line;
		String results;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(
                                    new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Could not allocate read/write buffers");
			System.exit(-1);
		}
        
		while (true) {
			try {
				line = in.readLine();
				if (line != null) {
					if (line.equals(".close")) {
						System.out.println("Received CLOSE request");
                		System.exit(0);
                	}
                    results = retriever.getHits(line, "-1", false);
                    out.println(results);
                    out.println("DONE");
                } else {
                    break;
                }
            } catch (IOException e) {
                System.err.println("Read failed");
                System.exit(-1);
            }
        }
        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Close failed");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
