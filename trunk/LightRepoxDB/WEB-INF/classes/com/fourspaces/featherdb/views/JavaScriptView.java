package com.fourspaces.featherdb.views;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.bsf.util.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import com.fourspaces.featherdb.backend.Backend;
import com.fourspaces.featherdb.document.Document;
import com.fourspaces.featherdb.document.JSONDocument;
import com.fourspaces.featherdb.utils.Logger;

/**
 * 
 * Creates new JavaScript Views...  
 * 
 * This will create a new javascript engine for each view instance (not optimal, but without proper nested
 * ScriptContext's I'm not sure how else to do it).  This is <b>not thread-safe</b>.  It must be called from
 * a single ViewRunner to lower the overhead of having a JS Engine instance for each JavaScriptView instance.
 * <p>
 * The alternative is to have an engine (and initialize it) for each document... and that's even worse.
 * 
 */

@ViewType("text/javascript")
public class JavaScriptView implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 713522274681368650L;

	transient protected Backend backend;
	transient protected Logger log = Logger.get(JavaScriptView.class);

	protected String db;
	protected String src;
		
	public JavaScriptView(String db,String src) throws ViewException {		
		this.db=db;
		this.src=src;
	}
		
	public void setBackend(Backend backend) {
		this.backend=backend;
	}
	
	public JSONObject get(String id, String rev, String mydb) {
		if (mydb==null) {
			mydb = this.db;
		}
		Document d = null;
		d = backend.getDocument(mydb, id,rev);
		if (d!=null) {
			if (d instanceof JSONDocument) {
				return JSONObject.fromString(d.toString());
			}
			JSONObject aux = new JSONObject();
			aux.put("error", mydb+"/"+id+" is not a JSONDocument");
			return aux;
		}
		return null;
	}

	public JSONObject filter(Document doc1) {
		JSONDocument doc = (JSONDocument) doc1;
		try {
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
		    Reader in = new InputStreamReader(getClass().getResourceAsStream("json.js"));
		    String script = IOUtils.getStringFromReader (in);
			cx.evaluateString(scope, script, "<cmd>", 1, null);
			cx.evaluateString(scope,"function map(key,val) { _FeatherDB_retval = { 'key':key,'value':val }; }", "<cmd>", 1, null);
			cx.evaluateString(scope,"function get(id,revision,db) { return eval('('+_FeatherDB_JSVIEW.get(id,revision,db)+')'); }", "<cmd>", 1, null);
			cx.evaluateString(scope,"function toJSON(obj) { if (obj!=null && typeof obj != 'undefined') { return obj.toJSONString(); } } ", "<cmd>", 1, null);
			cx.evaluateString(scope,"_FeatherDB_filter="+src, "<cmd>", 1, null);
			cx.evaluateString(scope,"_FeatherDB_retval=''", "<cmd>", 1, null);
			cx.evaluateString(scope,"_FeatherDB_doc = eval('('+'"+ doc.toString()+"'+')');", "<cmd>", 1, null);	
			Object wrappedOut = Context.javaToJS(this, scope);
			ScriptableObject.putProperty(scope, "_FeatherDB_JSVIEW", wrappedOut);
			Function f = (Function)(scope.get("_FeatherDB_filter", scope));
			Object functionArgs[] = { scope.get("_FeatherDB_doc", scope) };
			Object retval = f.call(cx, scope, scope, functionArgs);
			String json=null;			
			if (retval != null) {
				f = (Function)(scope.get("toJSON", scope));
				functionArgs[0] = retval;
				json = cx.toString(f.call(cx, scope, scope, functionArgs));
				if(json.equals("undefined")) json = cx.toString(cx.evaluateString(scope,"_FeatherDB_retval.toJSONString();", "<cmd>", 1, null));
			} else {
				json = cx.toString(cx.evaluateString(scope,"_FeatherDB_retval.toJSONString();", "<cmd>", 1, null));
			}
			if (json!=null && !json.equals("") && !json.equals("\"\"")) return JSONObject.fromString(json);
		} catch (JavaScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}