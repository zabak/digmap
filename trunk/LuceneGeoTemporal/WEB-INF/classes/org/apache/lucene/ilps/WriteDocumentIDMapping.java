/*
 * Created on Jul 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.lucene.ilps;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

/**
 * @author borkur
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WriteDocumentIDMapping {

	public static void main(String[] args){
		String directory = args[0];
		try {
			IndexReader ir = IndexReader.open(directory);
			for(int i=0; i<ir.maxDoc(); i++){
				Document doc = ir.document(i);
				System.out.println(i + " " + doc.getValues("id")[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
