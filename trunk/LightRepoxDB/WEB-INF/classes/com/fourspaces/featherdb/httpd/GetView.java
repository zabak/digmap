package com.fourspaces.featherdb.httpd;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import com.fourspaces.featherdb.auth.Credentials;
import com.fourspaces.featherdb.views.ViewManager;

public class GetView extends BaseRequestHandler {

	@SuppressWarnings("unchecked")
	public void handleInner(Credentials credentials, HttpServletRequest request, HttpServletResponse response, String db, String id, String rev/*, String[] fields*/) throws IOException{
		boolean showXML = "true".equals(request.getParameter("xml")) | "true".equals(request.getParameter("toXML"));

		String viewName = id;
		String functionName = null;
		
		if (featherDB.getViewManager().doesViewExist(db, viewName,ViewManager.DEFAULT_FUNCTION_NAME)) {
			functionName=ViewManager.DEFAULT_FUNCTION_NAME;
		} else {
			int idx = viewName.lastIndexOf("/");
			if (idx>-1) {
				functionName = viewName.substring(idx+1);
				viewName = viewName.substring(0,idx);
			} else {
				JSONObject status = new JSONObject();
				status.put("db",db);
				status.put("view",viewName);
				status.put("function",functionName);
				sendError(response, "View not found",status,HttpServletResponse.SC_NOT_FOUND,showXML);
			}
 		}
		
		if (featherDB.getViewManager().doesViewExist(db, viewName, functionName)) {
			boolean pretty="true".equals(request.getParameter("pretty"));
			if (pretty) {
				sendJSONString(response, featherDB.getViewManager().getViewResults(db, viewName, functionName),showXML);
			} else {
				sendJSONString(response, featherDB.getViewManager().getViewResults(db, viewName, functionName).toString(),showXML);
			}
		} else {
			JSONObject status = new JSONObject();
			status.put("db",db);
			status.put("view",viewName);
			status.put("function",functionName);
			sendError(response, "View not found",status,HttpServletResponse.SC_NOT_FOUND,showXML);
		}
	}

	public boolean match(Credentials credentials, HttpServletRequest request, String db, String id) {
		return (db!=null  && !db.startsWith("_") && id!=null && id.startsWith("_") && request.getMethod().equals("GET") && credentials.isAuthorizedRead(db));
	}

}
