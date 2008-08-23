package com.fourspaces.featherdb.backend;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.weta.dfs.client.DFile;
import net.weta.dfs.server.MetaDataServerRunner;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.DocumentCreationException;
import com.fourspaces.featherdb.utils.DFileUtils;
import com.fourspaces.featherdb.utils.JSONUtils;
import com.fourspaces.featherdb.utils.LineCallback;
import com.fourspaces.featherdb.utils.Lock;
import com.fourspaces.featherdb.utils.Logger;
import com.fourspaces.featherdb.views.ViewException;

/**
 * Stores documents in a weta-dfs repository. All of the information is stored
 * 
 * The database structure is: / db / doc id / _revisions - list of revisions in
 * order - one line per revision, most recent is last / _common - common data
 * for all revisions (created date, current revision, etc... ) in JSON format /
 * _permissions - the permissions for this document / rev_id - a file for each
 * revision in JSON format
 * 
 */
public class WetaDFSBackend implements Backend {

	private FeatherDB featherDB;
	private DFile rootDir;

	final private Logger log = Logger.get(WetaDFSBackend.class);


	public WetaDFSBackend() {
	}

	private DFile dbDir(String db) {
		return new DFile(rootDir, db);
	}

	private DFile docDir(String db, String id) {
		return new DFile(dbDir(db), id);
	}

	public void deleteDocument(String db, String id) throws BackendException {
		try {
		DFile docDir = docDir(db, id);
		if (!docDir.exists()) {
			log.warn("Deleting non-existant document {}/{}", db,id);
			return;
		}
		log.debug("Deleting document {}/{}", db, id);
		deleteRecursive(docDir);
		} catch ( IOException e ) { throw new BackendException(e.toString()); }
	}

	public void init() { }

	public void shutdown() { }

	public Iterable<Document> allDocuments(final String db) {
		return getDocuments(db, null);
	}

	protected void findAllDocuments(List<String> ids, DFile baseDir, String baseName) throws BackendException {
		try {
		for (DFile f: baseDir.listFiles()) {
			if (f.isDirectory()) {
				if (new DFile(f,"_common").exists()) {
					log.debug("found _common file in {}",f.getAbsolutePath());
					if (baseName!=null) {
						log.debug("adding id: {}",baseName+"/"+f.getName());
						ids.add(baseName+"/"+f.getName());
					} else {
						log.debug("adding id: {}",f.getName());
						ids.add(f.getName());
					}
				} else {
					if (baseName!=null) {
						findAllDocuments(ids,f,baseName+"/"+f.getName());
					} else {
						findAllDocuments(ids,f,f.getName());
					}
				}
			}
		}
		} catch ( IOException e ) { throw new BackendException(e.toString()); }
	}
	
	public Iterable<Document> getDocuments(final String db, final String[] ids) {
		DFile dbDir = dbDir(db);
		final String[] idList;
		if (ids == null) {
			List<String> existingIds = new ArrayList<String>();
			try {
				findAllDocuments(existingIds,dbDir,null);
			} catch ( BackendException e ) { 
				log.error(e);
			}
			idList = new String[existingIds.size()];
			int i=0;
			for (String id:existingIds) {
				log.debug("found doc id => {}",id);
				idList[i++] = id;
			}
		} else {
			idList = ids;
		}
		final Iterator<Document> i = new Iterator<Document>() {
			int index = 0;
			Document nextDoc = null;

			public void findNext() {
				nextDoc = null;
				while (idList != null && index < idList.length && nextDoc == null) {
					nextDoc = getDocument(db,idList[index]);
					index++;
				}
			}

			public boolean hasNext() {
				if (index == 0 && nextDoc == null) {
					findNext();
				}
				return nextDoc != null;
			}

			public Document next() {
				Document nd = nextDoc;
				findNext();
				return nd;
			}

			public void remove() {
				findNext();
			}

		};
		return new Iterable<Document>() {
			public Iterator<Document> iterator() {
				return i;
			}
		};
	}

	public Set<String> getDatabaseNames() {
		Set<String> dbs = new HashSet<String>();
		try {
			for (DFile f : rootDir.listFiles()) if (f.isDirectory()) dbs.add(f.getName());
		} catch ( IOException e ) { log.error(e); };
		return dbs;
	}

	public Document getDocument(String db, String id) {
		return getDocument(db, id, null);
	}
	
	public Document getDocument(String db, String id, String rev) {
		DFile docDir = docDir(db, id);
		log.debug("Retrieving document {}/{}/{}", db, id, rev);
		try {
			DFile commonFile = new DFile(docDir, "_common");

			if (!commonFile.exists()) {
				log.warn("Document _common file not found: {}/{}",db,id);
				return null;
			}

			JSONObject commonJSON = JSONUtils.fromInputStream(DFileUtils.getDFileInputStream(commonFile));

			if (rev == null) {
				rev = commonJSON.getString("_current_revision");
			}


			DFile metaFile = new DFile(docDir, rev+".meta");
			if (!metaFile.exists()) {
				log.warn("Document .meta file not found: {}/{}/{}",db,id,rev);
				return null;
			}

			JSONObject metaJSON = JSONUtils.fromInputStream(DFileUtils.getDFileInputStream(metaFile));

			Document d =  Document.loadDocument(commonJSON,metaJSON);
			if (d.writesRevisionData()) {
				DFile revFile = new DFile(docDir, rev);
				if (!revFile.exists()) {
					log.warn("Document revision file not found: {}/{}/{}",db,id,rev);
					return null;
				}
				d.setRevisionData(DFileUtils.getDFileInputStream(revFile));
			}
			return d;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentCreationException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void addDatabase(String name) throws BackendException {
		DFile dir = new DFile(rootDir, name);
		try {
		if (dir.exists()) {
			log.error("Database dir '{}' already exists!",name);
			throw new BackendException("The database " + name + " already exists!");
		}
		log.debug("Adding database: {}",name);
		dir.mkdir();
		} catch ( IOException e ) { 
			log.error(e);
			throw new BackendException(e.toString());
		};
		try {
			featherDB.getViewManager().initDatabaseViews(name);
		} catch ( ViewException e ) {
			log.error(e);
		}
	}

	public boolean deleteDatabase(String name) throws BackendException {
		try {
		DFile dir = dbDir(name);
		if (dir.exists() && dir.isDirectory()) {
			deleteRecursive(dir);
		}
		} catch ( IOException e ) { throw new BackendException(e.toString()); }
		return true;
	}

	private void deleteRecursive(DFile dir) throws BackendException {
		try {
		if (dir.isDirectory()) {
			for (DFile child : dir.listFiles()) {
				if (child.isDirectory()) {
					deleteRecursive(child);
				}
				child.delete();
			}
		}
		if (dir.exists()) {
			dir.delete();
		}
	} catch ( IOException e ) { throw new BackendException(e.toString()); }
	}

	public boolean doesDatabaseExist(String db) {
	  try {	
		return dbDir(db).exists();
	  } catch ( IOException e ) { log.error(e); return false; }
	}

	public boolean doesDocumentExist(String db, String id) {
		try {
		return docDir(db, id).exists();
		  } catch ( IOException e ) { log.error(e); return false; }
	}

	public boolean doesDocumentRevisionExist(String db, String id, String revision) {
		try {
		return new DFile(docDir(db, id), revision).exists();
		  } catch ( IOException e ) { log.error(e); return false; }
	}

	public Document saveDocument(Document doc) throws BackendException {
		JSONObject commonJSON = doc.getCommonData();
		commonJSON.put("_current_revision", doc.getRevision());
		DFile docDir = docDir(doc.getDatabase(), doc.getId());
		try {		
		if (!docDir.exists()) {
			log.info("Creating document dir: {}",docDir(doc.getDatabase(),doc.getId()));
			docDir.mkdirs();
			DFile commonFile = new DFile(docDir, "_common");
			commonFile.createNewFile();
			DFileUtils.writeToFile(commonFile, "{}");
			new DFile(docDir, "_revisions").createNewFile();
		}
		} catch (IOException e) {
			throw new BackendException(e);
		}

		Lock lock = Lock.lock(docDir.getAbsolutePath()); // TODO : check if this lock holds
		log.info("updating revision file : {} #{}",doc.getId(),doc.getRevision());

		// write out all revision'd elements
		if (doc.isDataDirty()) {
			try {
				DFile revisionMetaFile = new DFile(docDir, doc.getRevision()+".meta");

				// add the current revision to the _revisions file (if needed)	
				if (!revisionMetaFile.exists()) {
					DFile revListFile = new DFile(docDir, "_revisions");
					DFileUtils.writeToFile(revListFile, doc.getRevision() + "\n", true);
					
					// write the document's revision data if req'd
					if (doc.writesRevisionData()) {
						DFile revisionFile = new DFile(docDir, doc.getRevision());
						OutputStream out = DFileUtils.getDFileOutputStream(revisionFile);
						doc.writeRevisionData(out);
						out.close();
					}			
					// write the meta json data
					Writer metaWriter = DFileUtils.getDFileWriter(revisionMetaFile);
					doc.getMetaData().write(metaWriter);
					metaWriter.close();
				}
				
			} catch (IOException e) {
				throw new BackendException(e);
			}
	
		}
		log.info("updating common file : {}",doc.getId());
		// write out all common elements ( if the data is dirty, there is a new current_rev, so a new common
		// needs to be written
		if (doc.isCommonDirty() || doc.isDataDirty()) {
			try {
				DFile commonFile = new DFile(docDir, "_common");
				System.err.println("Writing common: "+commonJSON.toString(2));
				Writer writer = DFileUtils.getDFileWriter(commonFile);
				commonJSON.write(writer);
				writer.close();
			} catch (IOException e) {
				throw new BackendException(e);
			}
		}
		lock.release();
		featherDB.recalculateViewForDocument(doc);
		return doc;
	}

	public JSONArray getDocumentRevisions(String db, final String id) {
		final JSONArray ar = new JSONArray();
		DFile docDir = docDir(db, id);
		DFile revListFile = new DFile(docDir, "_revisions");
		try {
			DFileUtils.readFileByLine(revListFile,new LineCallback() {
				public void process(String line) {
					ar.add(line.trim());
				}				
			});
		} catch (IOException e) {
			// I don't like silent exceptions...
			log.error("Error loading revisions for {}/{}",db,id);
		}
		return ar;
	}

	public Map<String, Object> getDatabaseStats(String name) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("db_name", name);
		int count = 0;
		try {
		for (DFile f : dbDir(name).listFiles()) {
			if (f.isDirectory()) {
				count++;
			}
		}
		} catch ( IOException e ) { log.error(e); }
		m.put("doc_count", count);
		return m;
	}

	public void init(FeatherDB featherDB) {
		this.featherDB=featherDB;
		String path = featherDB.getProperty("backend.fs.path");
		if (path == null) {
			throw new RuntimeException(
					"You must include a backend.fs.path element in coffeedb.properties or specify the path in the constructor");
		}
		log.info("Using database path {}", path);
		this.rootDir = new DFile(path,MetaDataServerRunner.mn.getIpAddress(),MetaDataServerRunner.mn.getPort());
		try {
		if (!rootDir.exists()) {
			log.debug("Creating database directory");
			rootDir.mkdirs();
		} else if (!rootDir.isDirectory()) {
			log.error("Path: {} not valid!", path);
			throw new RuntimeException("Path: " + path + " not valid!");
		}
		} catch ( IOException e ) { throw new RuntimeException(e.toString()); }
	}


	/**
	 * This makes a place-holder file to avoid revision name duplicates.
	 */
	public void touchRevision(String db, String id, String rev) {
		try {
			docDir(db, id).mkdirs();
			new DFile(docDir(db, id), rev).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
