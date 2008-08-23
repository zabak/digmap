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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parses the HTTP response returned by the FeatherDB server.
 * This is almost never called directly by the user, but indirectly through
 * the Session and Database objects.
 * <p>
 * Given a CouchDB response, it will determine if the request was successful 
 * (status 200,201,202), or was an error.  If there was an error, it parses the returned json error
 * message.
 * 
 * @author mbreese
 *
 */
class FeatherDBResponse {
	
	Log log = LogFactory.getLog(FeatherDBResponse.class);
	
	private byte[] body;
	private String path;
	private Header[] headers;
	private int statusCode;
	private String methodName;
	
	boolean ok = false;

	private String error_id;
	private String error_reason;
	
	/**
	 * C-tor parses the method results to build the CouchResponse object.
	 * First, it reads the body (hence the IOException) from the method
	 * Next, it checks the status codes to determine if the request was successful.
	 * If there was an error, it parses the error codes.
	 * @param method
	 * @throws IOException
	 */
	FeatherDBResponse(HttpMethod method) throws IOException {
		methodName=method.getName();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream reader = new BufferedInputStream(method.getResponseBodyAsStream());
		int aux;
		while((aux=reader.read())!=-1) out.write((byte)(aux));
		body = out.toByteArray();
		path = method.getPath();
		if (method.getQueryString()!=null && !method.getQueryString().equals("")) {
			path += "?"+method.getQueryString();
		}
		headers = method.getResponseHeaders();
		statusCode=method.getStatusCode();
		if (
				(methodName.equals("GET") && statusCode==404) || 
				(methodName.equals("PUT") && statusCode==409) ||
				(methodName.equals("POST") && statusCode==404) ||
				(methodName.equals("DELETE") && statusCode==404) 
			){
				JSONArray jbody = JSONArray.fromObject(body);
				error_id = jbody.getString(1);
				error_reason = jbody.getString(2);
		} else if (
				(methodName.equals("PUT") && statusCode==201) ||
				(methodName.equals("POST") && statusCode==201) ||
				(methodName.equals("DELETE") && statusCode==202) 
			) {
				ok = JSONObject.fromObject(body).getBoolean("ok");
			
		} else if ((method.getName().equals("GET") || method.getName().equals("POST")) && statusCode==200) {
			ok=true;
		}
		log.debug(toString());
	}

	@Override
	/**
	 * A better toString for this object... can be very verbose though.
	 */
	public String toString() {
		return "["+methodName+"] "+path+" ["+statusCode+"] "+" => "+body;
	}
	
	/**
	 * Retrieves the body of the request as a JSONArray object. (such as listing database names)
	 * @return
	 */
	public JSONArray getBodyAsJSONArray() {		
		return JSONArray.fromObject(new String(body));
	}
	
	/**
	 * Retrieves the body of the request as a JSONArray object. (such as listing database names)
	 * @return
	 */
	public byte[] getBody() {		
		return body;
	}

	/**
	 * Was the request successful?
	 * @return
	 */
	public boolean isOk() {
		return ok;
	}
	
	/**
	 * What was the error id?
	 * @return
	 */
	public String getErrorId() {
		if (error_id!=null) {
			return error_id;
		}
		return null;
	}
	
	/**
	 * what was the error reason given?
	 * @return
	 */
	public String getErrorReason() {
		if (error_reason!=null) {
			return error_reason;
		}
		return null;
	}

	/**
	 * Returns the body of the response as a JSON Object (such as for a document)
	 * @return
	 */
	public JSONObject getBodyAsJSON() {
		if (body==null) {
			return null;
		}
		return JSONObject.fromObject(new String(body));
	}

	/**
	 * Retrieves a specific header from the response (not really used anymore)
	 * @param key
	 * @return
	 */
	public String getHeader(String key) {
		for (Header h: headers) {
			if (h.getName().equals(key)) {
				return h.getValue();
			}
		}
		return null;
	}
}
