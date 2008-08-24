package com.fourspaces.featherdb.repox;

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.backend.Backend;
import com.fourspaces.featherdb.client.Session;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.JSONDocument;
import com.fourspaces.featherdb.views.AdHocViewRunner;

public class TestLocalRepositoryAccess extends TestCase {
	
	static String remoteLocation = null;//"http://127.0.0.1:8889";
	FeatherDB fdb;
	Backend db;
	
	protected void setUp() throws Exception {
		if(remoteLocation!=null) {
			// Initialize a remote repository
			db = new Session(remoteLocation);
		} else {
			// Initializing a local repository database
			fdb = new FeatherDB();		
			db = fdb.getBackend();
			db.init(fdb);
		}
		// Create a database
		if (db.doesDatabaseExist("foodb")) fdb.deleteDatabase("foodb");
		fdb.addDatabase("foodb");
	}
	
    protected void tearDown() throws Exception {
    }
    
    public void testWriteDocumentAutoID() throws Exception {
		// Writing a document
		JSONDocument doc = (JSONDocument)Document.newDocument(db, "foodb", "bgmartins");
		doc.put("foo","bar");
		db.saveDocument(doc);
    }
    
    public void testWriteDocument() throws Exception {
		// Writing a document with an auto-generated id given by the database
		JSONDocument newdoc = (JSONDocument)Document.newDocument(db, "foodb", "documentid1234", "bgmartins");
		newdoc.put("foo","bar");
		db.saveDocument(newdoc);
    }
    
    public void testRetrieveDocument() throws Exception {
		// Getting a document
		Document myDoc = db.getDocument("foodb","documentid1234");
		System.out.println(myDoc);
    }
    
    public void testView() throws Exception {
		// Running a view
		Iterable<Document> result = db.allDocuments("foodb");
		Iterator<Document> it = result.iterator();
		while (it.hasNext()) {
				Document d = it.next();
		        System.out.println(d.getId());
                // ViewResults don't actually contain the full document, only what the view
		        // returned. So, in order to get the full document, you need to request a new copy from the database     
		        Document full = db.getDocument("foodb",d.getId());
		        System.out.println(full);
		}
    }
    
    public void testAdHocView() throws Exception {
		// Ad-Hoc view
		JSONArray resultAdHoc = AdHocViewRunner.adHocView(fdb,"foodb","function (doc) { if (doc.foo=='bar') return doc; }").getJSONArray("rows");
		int numObjects = 0;
		for (Object k : resultAdHoc) {
			Document id = Document.newDocument((JSONObject)k);
            // ViewResults don't actually contain the full document, only what the view
	        // returned. So, in order to get the full document, you need to request a new copy from the database     
	        Document full = db.getDocument("foodb",id.getId());
			System.out.println(full);
			numObjects++;
		}
		assertEquals(numObjects,2);
	}
    
    public static void main ( String args[] ) throws Exception {
    	TestLocalRepositoryAccess t = new TestLocalRepositoryAccess();
    	t.setUp();
    	t.testAdHocView();
    }
	
}