package com.fourspaces.featherdb.httpd;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONException;
import net.sf.json.util.JSONBuilder;

import com.fourspaces.featherdb.auth.Credentials;

public class Sessions extends BaseRequestHandler {

	public void handleInner(Credentials credentials, HttpServletRequest request, HttpServletResponse response, String db, String id, String rev) throws IOException{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(JSON_MIMETYPE);
		try {
			JSONBuilder w = new JSONBuilder(response.getWriter());
			w.array();
			for (Credentials cred:featherDB.getAuthentication().getCredentials()) {
				w.object()
					.key("username")
					.value(cred.getUsername())
					.key("token")
					.value(cred.getToken())
					.endObject();
			}
			w.endArray();
			
		} catch (JSONException e) {
			throw new IOException(e.toString());
		}
	}

	public boolean match(Credentials credentials, HttpServletRequest request, String db, String id) {
		return (db.equals("_sessions") && credentials.isSA());
	}

}
