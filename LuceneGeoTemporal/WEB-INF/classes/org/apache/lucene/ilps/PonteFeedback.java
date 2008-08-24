/*
 * Created on Jul 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.lucene.ilps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

/**
 * @author borkur
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PonteFeedback {

	public static Query getExpansionTerms(
		LanguageModelIndexReader ir,
		Query query,
		int[] docs,
		int expandSize) {

		Map termMap = new HashMap();

		System.err.println(
			"Expaning: " + query.toString(Retrieve.LUCENE_DEFAULT_FIELD));
		for (int i = 0; i < docs.length; i++) {
			try {
				TermFreqVector[] tfvs = ir.getTermFreqVectors(docs[i]);
				for (int k=0; k< tfvs.length; k++){
					String[] terms = tfvs[k].getTerms();	
					String field = tfvs[k].getField();
					int[] freqs = tfvs[k].getTermFrequencies();
					for (int j = 0; j < terms.length; j++) {
						Term t = new Term(field, terms[j]);
						float tf = freqs[j];
						float docSize =
							ir.getFieldLength(
									docs[i],
									field);
						float collSize = ir.getTotalDocFreqs();
						float termColl = ir.docFreq(t);
						float termScore =
							(float) (Math
									.log((tf * collSize) / (docSize * termColl))
									/ Math.log(10));
						if (termMap.containsKey(t)) {
							termScore
							+= ((Float) termMap.get(t)).floatValue();
						}
						termMap.put(t, new Float(termScore));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List entries = new ArrayList(termMap.entrySet());
		Comparator cmp = new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				Comparable v1 = (Comparable) e1.getValue();
				Comparable v2 = (Comparable) e2.getValue();
				return v2.compareTo(v1);
			}
		};

		Collections.sort(entries, cmp);

		Iterator keys = entries.iterator();
		int resultCounter = 0;
		String queryString = "";
		while (keys.hasNext() && resultCounter < expandSize) {
			Map.Entry en = (Map.Entry) keys.next();

			queryString += en.getKey() + " ";
			Term t =
				new Term(((Term) en.getKey()).field() , ((Term) en.getKey()).text());
			int df = -1;
			try {
				df = ir.docFreq(t);				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.err.println(
				"Added term "
					+ en.getKey()
					+ " with value "
					+ en.getValue()
					+ "and df "
					+ df);
			resultCounter++;
		}
		Query expQuery = null;
		try {
            //LGTE
            expQuery =

                    LuceneVersionFactory.getLuceneVersion()
                            .parseQuery(
                                    queryString,
                                    Retrieve.LUCENE_DEFAULT_FIELD,
                                    new WhitespaceAnalyzer());
        } catch (ParseException e) {
			e.printStackTrace();
		}
		System.err.println("Added: " + queryString);
		return expQuery;
	}

	public static void main(String[] args) {

	}
}
