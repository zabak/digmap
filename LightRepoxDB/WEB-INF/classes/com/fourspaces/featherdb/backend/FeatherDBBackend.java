package com.fourspaces.featherdb.backend;

import java.util.Map;
import java.util.Set;
import net.sf.json.JSONArray;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.client.Session;
import com.fourspaces.featherdb.document.Document;

/**
 * Stores documents inside an external third-party FeatherDB our CouchDB
 */
public class FeatherDBBackend implements Backend {

	Session session;
	
	public void init(FeatherDB featherDB) { 
		session = new Session("localhost",8888);
		session.init(featherDB); 
	}
	
	public void shutdown() { session.shutdown(); }

	public Set<String> getDatabaseNames() { return session.getDatabaseNames(); }
	
	public void addDatabase(String name) throws BackendException { session.addDatabase(name); }
	public boolean deleteDatabase(String name) throws BackendException { return session.deleteDatabase(name); }
	public Map<String,Object> getDatabaseStats(String name) { return session.getDatabaseStats(name); } 
	
	public Iterable<Document> allDocuments(String db) { return session.allDocuments(db); }
	public Iterable<Document> getDocuments(String db, String[] ids) { return session.getDocuments(db, ids); }
	
	public Document getDocument(String db,String id) { return session.getDocument(db, id); }
	public Document getDocument(String db,String id, String rev) { return session.getDocument(db, id, rev); }
	public JSONArray getDocumentRevisions(String db,String id) { return session.getDocumentRevisions(db, id); }

	public boolean doesDatabaseExist(String db) { return session.doesDatabaseExist(db); }
	public boolean doesDocumentExist(String db, String id) { return session.doesDocumentExist(db, id); }
	public boolean doesDocumentRevisionExist(String db, String id, String revision) { return session.doesDocumentRevisionExist(db, id, revision); } 
	
	public Document saveDocument(Document doc) throws BackendException { return session.saveDocument(doc); }
	
	public void deleteDocument(String db,String id) throws BackendException { session.deleteDocument(db, id); }
	public void touchRevision(String database, String id, String rev) { session.touchRevision(database, id, rev); }
	
}