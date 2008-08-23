package com.fourspaces.featherdb.httpd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fourspaces.featherdb.auth.Credentials;

public class Shutdown extends BaseRequestHandler {

	public void handleInner(Credentials credentials, HttpServletRequest request, HttpServletResponse response, String db, String id, String rev){
		featherDB.shutdown();
		boolean showXML = "true".equals(request.getParameter("xml")) | "true".equals(request.getParameter("toXML"));
		sendOK(response, "Shutting down",showXML);
	}

	public boolean match(Credentials credentials, HttpServletRequest request, String db, String id) {
		return (db.equals("_shutdown") && credentials.isSA());
	}

}
