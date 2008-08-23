package com.fourspaces.featherdb.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.JSONDocument;

/**
 * This represents a particular database on the FeatherDB server
 * <p>
 * Using this object, you can get/create/update/delete documents.
 * You can also call views (named and adhoc) to query the underlying database.
 * 
 * @author mbreese
 *
 */
class Database {
	
	Log log = LogFactory.getLog(Database.class);
	private final String name;
	private int documentCount;
	
	private Session session;
	
	/**
	 * C-tor only used by the Session object.  You'd never call this directly.
	 * @param json
	 * @param session
	 */
	Database(JSONObject json, Session session) {
		name = json.getString("db_name");
		documentCount = json.getInt("doc_count");
		this.session = session;
	}
	
	/**
	 * The name of the database
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * The number of documents in the database <b>at the time that it was retrieved from the session</b>
	 * This number probably isn't accurate after the initial load... so if you want an accurate
	 * assessment, call Session.getDatabase() again to reload a new database object.
	 * @return
	 */
	public int getDocumentCount() {
		return documentCount;
	}		

	/**
	 * Save a document at the given _id
	 * <p>
	 * if the docId is null or empty, then this performs a POST to the database and retrieves a new
	 * _id.
	 * <p>
	 * Otherwise, a PUT is called.
	 * <p>
	 * Either way, a new _id and _rev are retrieved and updated in the Document object
	 * 
	 * @param doc
	 * @param docId
	 */
	public Document saveDocument(Document doc, String docId) {
		FeatherDBResponse resp;
		if (docId==null || docId.equals("")) {
			resp= session.put(name+"/", doc.toString());
		} else {
			resp= session.put(name+"/"+ docId, doc.toString());
		}		
		if (resp.isOk()) {
			try {
				if (doc.getId()==null || doc.getId().equals("")) {
					doc.setId(resp.getBodyAsJSON().getString("id"));
				}
				doc.setRevision(resp.getBodyAsJSON().getString("_rev"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			log.warn("Error adding document - "+resp.getErrorId()+" "+resp.getErrorReason());
		}
		return doc;
	}
	
	/**
	 * Save a document w/o specifying an id (can be null)
	 * @param doc
	 */
	public Document saveDocument(Document doc) {
		return saveDocument(doc,doc.getId());
	}
	
	/**
	 * Retrieves a document from the CouchDB database
	 * @param id
	 * @return
	 */
	public Document getDocument(String id) {
		return getDocument(id,null,false);
	}
	/**
	 * Retrieves a document from the database and asks for a list of it's revisions.
	 * The list of revision keys can be retrieved from Document.getRevisions();
	 * 
	 * @param id
	 * @return
	 */
	public Document getDocumentWithRevisions(String id) {
		return getDocument(id,null,true);
	}

	/**
	 * Retrieves a specific document revision
	 * @param id
	 * @param revision
	 * @return
	 */
	public Document getDocument(String id, String revision) {
		return getDocument(id,revision,false);
	}
	
	/**
	 * Retrieves a specific document revision and (optionally) asks for a list of all revisions 
	 * @param id
	 * @param revision
	 * @param showRevisions
	 * @return the document
	 */
	public Document getDocument(String id, String revision, boolean showRevisions) {
		FeatherDBResponse resp;
		Document doc = null;
		if (revision!=null && showRevisions) {
			resp=session.get(name+"/"+id,"rev="+revision+"&full=true");
		} else if (revision!=null && !showRevisions) {
			resp=session.get(name+"/"+id,"rev="+revision);
		} else if (revision==null && showRevisions) {
			resp=session.get(name+"/"+id,"revs=true");
		} else {
			resp=session.get(name+"/"+id);
		}
		if (resp.isOk()) {
			try {
				doc = Document.newDocument(resp.getBody());
			} catch ( Exception e ) {
				log.warn("Error getting document - "+ e.toString());
			}
		} else {
			log.warn("Error getting document - "+resp.getErrorId()+" "+resp.getErrorReason());
		}
		return doc;
	}
	
	/**
	 * Deletes a document
	 * @param d
	 * @return was the delete successful?
	 */
	public boolean deleteDocument(Document d) {
		FeatherDBResponse resp = session.delete(name+"/"+d.getId() + "?rev=" + d.getRevision());
		if(resp.isOk()) {
			return true;
		} else {
			log.warn("Error deleting document - "+resp.getErrorId()+" "+resp.getErrorReason());
			return false;
		}
		
	}
	
	public boolean doesDocumentExist ( String name ) {
		Document doc = getDocument(name);
		return doc != null && doc.getCreated() != null;
	}
	
	public boolean doesDocumentRevisionExist ( String name, String rev ) {
		if(rev==null) return doesDocumentExist(name);
		Document doc = getDocument(name,rev);
		return doc != null && doc.getCreated() != null;
	}
	
	public Map<String,Object> getDatabaseStats() {
		FeatherDBResponse resp;
		resp=session.get(name);
		if (resp.isOk()) {
			try {
				JSONObject obj = resp.getBodyAsJSON();
				Iterator<String> aux = obj.keys();
				HashMap<String, Object> map = new HashMap<String,Object>();
				while (aux.hasNext()) {
					String key = aux.next();
					map.put(key, obj.get(key));
				}
				return map;
			} catch ( Exception e ) {
				log.warn("Error getting database stats - "+ e.toString());
			}
		} else {
			log.warn("Error getting database stats - "+resp.getErrorId()+" "+resp.getErrorReason());
		}
		return null;
	}
	
	public Iterable<Document> getDocuments( String id[] ) {
		Set<Document> aux = new HashSet<Document>();
		if (id==null) {
			FeatherDBResponse resp;
			JSONArray doc = null;
			resp=session.get(name+"/_all_docs");
			if (resp.isOk()) {
				try {
					doc = resp.getBodyAsJSONArray();
					for(int i=0; i<doc.length(); i++) aux.add(Document.newDocument(doc.getJSONObject(i)));					
				} catch ( Exception e ) {
					log.warn("Error getting document - "+ e.toString());
				}
			} else {
				log.warn("Error getting document - "+resp.getErrorId()+" "+resp.getErrorReason());
			}
			return aux;
		} else {
			for (int i=0; i<id.length; i++) aux.add(getDocument(id[i]));
			return aux;
		}
	}	

}