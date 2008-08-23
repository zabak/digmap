package com.fourspaces.featherdb.httpd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.auth.Credentials;
import com.fourspaces.featherdb.backend.BackendException;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.DocumentCreationException;
import com.fourspaces.featherdb.utils.Logger;
import com.fourspaces.featherdb.views.ViewException;

public abstract class BaseRequestHandler {
	protected static final String JSON_MIMETYPE = "application/javascript";
	protected FeatherDB featherDB;

	abstract public boolean match(Credentials credentials, HttpServletRequest request, String db, String id);
	abstract protected void handleInner(Credentials credentials, HttpServletRequest request, HttpServletResponse response, String db, String id, String rev) throws BackendException, IOException, DocumentCreationException, ViewException;

	protected Logger log = Logger.get(getClass());
	
	public void handle(Credentials credentials, HttpServletRequest request, HttpServletResponse response, String db, String id, String rev, boolean xml){
		try {
			handleInner(credentials,request,response,db,id,rev);//,fields);
		} catch (JSONException e) {
			sendError(response, "JSON processing error",xml);
			log.error(e,"JSON error");
		} catch (ViewException e) {
			sendError(response, "View processing error",xml);
			log.error(e,"View error");
		} catch (BackendException e) {
			sendError(response, "Backend storage error",xml);
			log.error(e,"Backend error",xml);
			e.printStackTrace();
		} catch (IOException e) {
			sendError(response, "IO error",xml);
			log.error(e,"Backend error");
			e.printStackTrace();
		} catch (DocumentCreationException e) {
			sendError(response, "IO error",xml);
			log.error(e,"Backend error");
			e.printStackTrace();
		}
	}

	public void setFeatherDB(FeatherDB featherDB) {
		this.featherDB=featherDB;
	}
	
	protected void sendNotAuth(HttpServletResponse response, boolean xml) {
		sendError(response,"Not authorized to perform requested action", HttpServletResponse.SC_UNAUTHORIZED,xml);
	}

	protected void sendError(HttpServletResponse response, String string, boolean xml) {
		sendError(response,string,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,xml);
	}
	protected void sendError(HttpServletResponse response, String string, String  status, boolean xml) {
		sendError(response,string,status,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,xml);
	}
	protected void sendError(HttpServletResponse response, String string, JSONObject status, boolean xml) {
		sendError(response,string,status,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,xml);
	}
	
	protected void sendError(HttpServletResponse response, String string, int statusCode, boolean xml) {
		response.setStatus(statusCode);
		response.setContentType(JSON_MIMETYPE);
		try {
			JSONObject aux = new JSONObject();
			aux.put("error",true);
			aux.put("message", string);
			if (xml) response.getWriter().write( new XMLSerializer().write(aux));
			else response.getWriter().write(aux.toString());			
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	protected void sendError(HttpServletResponse response, String string, String  status, int statusCode, boolean xml) {
		response.setStatus(statusCode);
		response.setContentType(JSON_MIMETYPE);
		try {
			JSONObject aux = new JSONObject();
			aux.put("error",true);
			aux.put("message", string);
			aux.put("status", status);
			if (xml) response.getWriter().write( new XMLSerializer().write(aux));
			else response.getWriter().write(aux.toString());
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	protected void sendError(HttpServletResponse response, String string, JSONObject status, int statusCode, boolean xml) {
		response.setStatus(statusCode);
		response.setContentType(JSON_MIMETYPE);
		try {
			JSONObject aux = new JSONObject();
			aux.put("error",true);
			aux.put("message", string);
			aux.put("status", status);
			if (xml) response.getWriter().write( new XMLSerializer().write(aux));
			else response.getWriter().write(aux.toString());
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	protected void sendOK(HttpServletResponse response, String string, boolean xml){
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(JSON_MIMETYPE);
		try {
			JSONObject aux = new JSONObject();
			aux.put("ok",true);
			aux.put("message", string);
			if (xml) response.getWriter().write( new XMLSerializer().write(aux));
			else response.getWriter().write(aux.toString());
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	protected void sendJSONString(HttpServletResponse response, JSONArray ar, boolean xml) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(JSON_MIMETYPE);
			if (xml) response.getWriter().write(new XMLSerializer().write(ar));
			else response.getWriter().write(ar.toString(4));
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	protected void sendJSONString(HttpServletResponse response, JSONObject json, boolean xml) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(JSON_MIMETYPE);
			if (xml) response.getWriter().write(new XMLSerializer().write(json));
			else response.getWriter().write(json.toString(2));
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}

	protected void sendJSONString(HttpServletResponse response, String s, boolean xml) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(JSON_MIMETYPE);
			if (xml) response.getWriter().write(new XMLSerializer().write(JSONObject.fromString(s)));
			else response.getWriter().write(s);
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}

	protected void sendDocument(HttpServletResponse response, Document doc, Map<String,String[]> params, boolean xml) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(doc.getContentType());
			if (!xml) doc.sendDocument(response.getOutputStream(),params); else {
				ByteArrayOutputStream aux = new ByteArrayOutputStream();
				doc.sendDocument(aux,params);
				response.getWriter().write(new XMLSerializer().write(JSONObject.fromString(aux.toString())));
			}
			
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	protected void sendMetaData(HttpServletResponse response, Document doc, Map<String,String[]> params, boolean xml) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(Document.DEFAULT_CONTENT_TYPE);			
			if (!xml) doc.writeMetaData(response.getWriter(),params); else {
				StringWriter aux = new StringWriter();
				doc.writeMetaData(aux,params);
				response.getWriter().write(new XMLSerializer().write(JSONObject.fromString(aux.toString())));
			}
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
}
