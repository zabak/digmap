/*
   Copyright 2007 Fourspaces Consulting, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.fourspaces.featherdb.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONArray;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.backend.Backend;
import com.fourspaces.featherdb.backend.BackendException;
import com.fourspaces.featherdb.document.Document;

/**
 * The Session is the main connection to the FeatherDB instance.  However, you'll only use the Session
 * to obtain a reference to a CouchDB Database.  All of the main work happens at the Database level.
 * <p>
 * It uses the Apache-Commons HttpClient library for all communication with the server.  This is
 * a little more robust than the standard URLConnection.
 * <p>
 * Ex usage: <br>
 * Session session = new Session(host,port);
 * Database db = session.getDatabase("dbname");
 * 
 * @author mbreese
 * @author brennanjubb - HTTP-Auth username/pass
 */
public class Session implements Backend {

	protected Log log = LogFactory.getLog(Session.class);
	protected final String host;
	protected final String path;
	protected final int port;
	protected final String user;
	protected final String pass;
	protected final boolean secure;
	protected final boolean usesAuth;
	
	protected FeatherDBResponse lastResponse;
	
	protected HttpClient httpClient = new HttpClient();

	/**
	 * Constructor for obtaining a Session with an HTTP-AUTH username/password and (optionally) a secure connection
	 * This isn't supported by CouchDB - you need a proxy in front to use this
	 * @param host - hostname
	 * @param port - port to use
	 * @param user - username
	 * @param pass - password
	 * @param secure  - use an SSL connection?
	 */
	public Session(String host, int port, String user, String pass,boolean secure) {
		this.host = host;
		this.path = "";
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.usesAuth = true;
		this.secure = secure;
		this.httpClient.getState().setCredentials( AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
	}

	/**
	 * Constructor for obtaining a Session with an HTTP-AUTH username/password
	 * This isn't supported by CouchDB - you need a proxy in front to use this
	 * @param host
	 * @param port
	 * @param user - username
	 * @param pass - password
	 */
	public Session(String host, int port, String user, String pass) {
		this.host = host;
		this.path = "";
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.usesAuth = true;
		this.secure = false;
		this.httpClient.getState().setCredentials( AuthScope.ANY, new UsernamePasswordCredentials(user, pass));

	}

	/**
	 * Main constructor for obtaining a Session.
	 * @param host
	 * @param port
	 */
	public Session(String host, int port) {
		this.host = host;
		this.path = "";
		this.port = port;
		this.user = null;
		this.pass = null;
		this.usesAuth = false;
		this.secure = false;
	}
	
	/**
	 * Main constructor for obtaining a Session.
	 * @param host
	 * @param port
	 */
	public Session(String s) throws MalformedURLException {
		URL url = new URL(s);
		String protocol = url.getProtocol();
		this.host = url.getHost();
		this.port = url.getPort();
		if(url.getPath().indexOf("?")==-1) { 
			this.user = null;
			this.pass = null;
			this.path = url.getPath();
		} else {
			String data = url.getPath().substring(url.getPath().indexOf("?")+1);
			if(data.indexOf("username=")!=-1) {
				String user = data.substring(data.indexOf("username=")+9);
				this.user = user.indexOf("?")==-1 ? user : user.substring(0,user.indexOf("?")); 
			} else this.user = null;
			if(data.indexOf("password=")!=-1) {
				String pass = data.substring(data.indexOf("password=")+9);
				this.pass = pass.indexOf("?")==-1 ? pass : pass.substring(0,user.indexOf("?"));
			} else this.pass = null;
			this.path = url.getPath().substring(0,url.getPath().indexOf("?"));
		}
		this.secure = protocol.equals("https");
		this.usesAuth = user != null && pass != null && secure;
	}
	/**
	 * Optional constructor that indicates an HTTPS connection should be used.
	 * This isn't supported by CouchDB - you need a proxy in front to use this
	 * 
	 * @param host
	 * @param port
	 * @param secure
	 */
	public Session(String host, int port, boolean secure) {
		this.host = host;
		this.path = "";
		this.port = port;
		this.secure = secure;
		this.user = null;
		this.pass = null;
		this.usesAuth = false;
	}
	
	/**
	 * Read-only
	 * @return the host name
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Read-only
	 * @return the path name
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Read-only
	 * @return the host name
	 */
	public String getServerURL() {
		return buildUrl("");
	}

	/**
	 * read-only
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Is this a secured connection (set in constructor)
	 * @return
	 */
	public boolean isSecure() {
		return secure;
	}
	
	/**
	 * Loads a database instance from the server
	 * @param name
	 * @return the database (or null if it doesn't exist)
	 */
	protected Database getDatabase(String name) {
		FeatherDBResponse resp = get(name);
		if (resp.isOk()) {
			return new Database(resp.getBodyAsJSON(),this);
		} else {
			log.warn("Error getting database: "+name);
		}
		return null;
	}
	
	/**
	 * Creates a new database (if the name doesn't already exist)
	 * @param name
	 * @return the new database (or null if there was an error)
	 */
	protected Database createDatabase(String name) {
		String dbname = name.toLowerCase().replaceAll("[^a-z0-9_$()+\\-/]", "_");
		if (!dbname.endsWith("/")) {
			dbname+="/";
		}
		FeatherDBResponse resp = put(dbname);
		if (resp.isOk()) {
			return getDatabase(dbname);
		} else {
			log.warn("Error creating database: "+name);
			return null;
		}
	}
	/**
	 * Deletes a database (by name) from the server.
	 * @param name
	 * @return true = successful, false = an error occurred (likely the database named didn't exist) 
	 */
	public boolean deleteDatabase(String name) {
		return delete(name).isOk();		
	}
	/**
	 * Deletes a database from the CouchDB server
	 * @param db
	 * @return was successful
	 */
	protected boolean deleteDatabase(Database db) {
		return deleteDatabase(db.getName());		
	}
	
	/**
	 * For a given url (such as /_all_dbs/), build the database connection url
	 * @param url
	 * @return the absolute URL (hostname/port/etc)
	 */
	protected String buildUrl(String url) {
		return (secure) ? "https://"+host+":"+port+"/"+path+url : "http://"+host+":"+port+"/"+path+url;
	}
	
	/**
	 * Package level access to send a DELETE request to the given URL 
	 * @param url
	 * @return
	 */
	protected FeatherDBResponse delete(String url) {
		DeleteMethod del = new DeleteMethod(buildUrl(url));
		return http(del);
	}

	/**
	 * Send a POST with no body / parameters
	 * @param url
	 * @return
	 */
	protected FeatherDBResponse post(String url) {
		return post(url,null,null);
	}
	
	/**
	 * Send a POST with body
	 * @param url
	 * @param content
	 * @return
	 */
	protected FeatherDBResponse post(String url, String content) {
		return post(url,content,null);
	}
	/**
	 * Send a POST with a body and query string
	 * @param url
	 * @param content
	 * @param queryString
	 * @return
	 */
	protected FeatherDBResponse post(String url, String content, String queryString) {
		PostMethod post = new PostMethod(buildUrl(url));
		if (content!=null) {
			RequestEntity entity;
			try {
				if (url.indexOf("_temp_view") != -1) {
					entity = new StringRequestEntity(content,"text/javascript","UTF-8");
				} else {
					entity = new StringRequestEntity(content,"application/json","UTF-8");
				}
				post.setRequestEntity(entity);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		if (queryString!=null) {
			post.setQueryString(queryString);
		}
		return http(post);
	}
	
	/**
	 * Send a PUT  (for creating databases)
	 * @param url
	 * @return
	 */
	protected FeatherDBResponse put(String url) {
		return put(url,null);
	}
	/**
	 * Send a PUT with a body (for creating documents)
	 * @param url
	 * @param content
	 * @return
	 */
	protected FeatherDBResponse put(String url, String content) {
		PutMethod put = new PutMethod(buildUrl(url));
		if (content!=null) {
			RequestEntity entity;
			try {
				entity = new StringRequestEntity(content, "application/json","UTF-8");
				put.setRequestEntity(entity);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		return http(put);
	}

	/**
	 * Send a GET request
	 * @param url
	 * @return
	 */
	protected FeatherDBResponse get(String url) {
		GetMethod get = new GetMethod(buildUrl(url));
		return http(get);
	}
	/**
	 * Send a GET request with a number of name/value pairs as a query string
	 * @param url
	 * @param queryParams
	 * @return
	 */
	protected FeatherDBResponse get(String url, NameValuePair[] queryParams) {
		GetMethod get = new GetMethod(buildUrl(url));
		get.setQueryString(queryParams);
		return http(get);
	}
	
	/**
	 * Send a GET request with a queryString (?foo=bar)
	 * @param url
	 * @param queryString
	 * @return
	 */
	protected FeatherDBResponse get(String url, String queryString) {
		GetMethod get = new GetMethod(buildUrl(url));
		get.setQueryString(queryString);
		return http(get);
	}
	
	/**
	 * Method that actually performs the GET/PUT/POST/DELETE calls.
	 * Executes the given HttpMethod on the HttpClient object (one HttpClient per Session).
	 * <p>
	 * This returns a CouchResponse, which can be used to get the status of the call (isOk), 
	 * and any headers / body that was sent back.
	 * 
	 * @param method
	 * @return the CouchResponse (status / error / json document)
	 */
	protected FeatherDBResponse http(HttpMethod method) {
		try {
			method.setDoAuthentication(usesAuth);
			httpClient.executeMethod(method);
			lastResponse = new FeatherDBResponse(method);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			  method.releaseConnection();
		}
		return lastResponse;	
	}

	/**
	 * Returns the last response for this given session
	 * - useful for debugging purposes
	 * @return
	 */
	protected FeatherDBResponse getLastResponse() {
		return lastResponse;
	}
	
	public void init(FeatherDB featherDB) {
	}
	
	public void shutdown() {
	}
	
	public Document saveDocument(Document doc) {
		return getDatabase(doc.getDatabase()).saveDocument(doc);
	}
	
	public void deleteDocument(String dname,String name) {
		Document doc = getDatabase(dname).getDocument(name);
		getDatabase(dname).deleteDocument(doc);
	}

	public void touchRevision(String database, String id, String rev) {	
	}
	
	public JSONArray getDocumentRevisions(String dname,String name) {
		return getDatabase(dname).getDocumentWithRevisions(name).getMetaData().getJSONArray("_rev");
	}
	
	public Map<String,Object> getDatabaseStats ( String dname ) {
		return getDatabase(dname).getDatabaseStats();
	}

	public Iterable<Document> allDocuments(String dname) {
		return getDocuments(dname,null);
	}
	
	public Iterable<Document> getDocuments(String dname, String[] names) {
		Set<Document> docs = new HashSet();
		if (names==null) {
			Database db = getDatabase(dname);
			db.getDocuments(names);
			return docs;
		}
		for (int i=0; i<names.length; i++) docs.add(getDatabase(dname).getDocument(names[i]));
		return docs;
	}
	
	public void addDatabase ( String name ) throws BackendException {
		if (createDatabase(name)==null) throw new BackendException("Error creating database: "+name);
	}
	
	public Document getDocument ( String dname, String name ) {
		return getDatabase(dname).getDocument(name);
	}
	
	public Document getDocument ( String dname, String name, String rev ) {
		return getDatabase(dname).getDocument(name,rev);
	}
	
	public boolean doesDatabaseExist( String dname ) {
		return getDatabaseNames().contains(dname);
	}

	public boolean doesDocumentExist( String dname, String name ) {
		boolean aux = doesDatabaseExist(dname);
		if (!aux) return false;
		return getDatabase(dname).doesDocumentExist(name);		
	}
	
	public boolean doesDocumentRevisionExist( String dname, String name, String rev ) {
		boolean aux = doesDatabaseExist(dname);
		if (!aux) return false;
		return getDatabase(dname).doesDocumentRevisionExist(name,rev);		
	}
	
	/**
	 * Retrieves a list of all database names from the server
	 * @return
	 */
	public Set<String> getDatabaseNames() {
		FeatherDBResponse resp = get("_all_dbs");
		JSONArray ar = resp.getBodyAsJSONArray();
		Set<String> dbs = new HashSet<String>(ar.size());
		for (int i=0 ; i< ar.size(); i++) {
			dbs.add(ar.getString(i));
		}
		return dbs;	
	}
}