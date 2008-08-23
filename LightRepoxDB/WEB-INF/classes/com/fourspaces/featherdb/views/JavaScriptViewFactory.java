package com.fourspaces.featherdb.views;

import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import com.fourspaces.featherdb.document.JSONDocument;

@ViewType("text/javascript")
public class JavaScriptViewFactory implements ViewFactory {

	public Map<String, View> buildViews(JSONDocument doc) throws ViewException {
		JSONObject viewDefs = doc.getMetaData().getJSONObject("view");
		Map<String,View> views = new HashMap<String,View>();
		for (Object k:viewDefs.keySet()) {
			String src = viewDefs.getString((String)k);
			views.put(src, new JavaScriptView(doc.getDatabase(),src));
		}
		return views;
	}

}
