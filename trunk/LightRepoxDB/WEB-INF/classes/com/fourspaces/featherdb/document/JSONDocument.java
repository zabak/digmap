package com.fourspaces.featherdb.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fourspaces.featherdb.utils.JSONUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ContentTypes({Document.DEFAULT_CONTENT_TYPE,"text/javascript","image/png","application/json"})
public class JSONDocument extends Document implements Map {
	
	public JSONDocument() {}

	public JSONDocument(JSONObject source) throws DocumentCreationException {
		setRevisionData(source);
	}
	
	@Override
	public void setRevisionData(InputStream dataInput) throws DocumentCreationException {
		try {
			metaData = JSONUtils.fromInputStream(dataInput);
		} catch (IOException e) {
			throw new DocumentCreationException(e);
		}
	}

	public void setRevisionData(JSONObject source) throws DocumentCreationException {
		if(source == null || source.keySet()==null) return;
		for (Object k:source.keySet()) {
			String key = (String)k;
			if (key.startsWith("_") && !key.equals(REV) && !key.equals(REV_DATE) && !key.equals(REV_USER)) {
				commonData.put(key, source.get(key));
			} else {
				metaData.put(key, source.get(key));
			}
		}
	}

	@Override
	public void writeRevisionData(OutputStream dataOutput) throws IOException {
		// does nothing since all JSON data is stored in the MetaData stream
	}

	@Override
	public void sendDocument(OutputStream dataOutput,Map<String,String[]> params) throws IOException {
		boolean pretty=false;
		if (params.containsKey("pretty")) {
			String[] values = params.get("pretty");
			for (String value:values) {
				if (value.equals("true")) {
					pretty=true;
				}
			}
		}
		for (String key: params.keySet()) {
			log.debug("{} => {}",key,params.get(key));
			int i=0;
			for (String value:params.get(key)) {
				log.debug("[{}] => ", i++,value);
			}
		}
		log.info("pretty? = {}", pretty);
		Writer writer = new OutputStreamWriter(dataOutput);
		if (pretty) {
			writer.write(toString(2));
		} else {
			writer.write(toString());
		}
		writer.close();
	}

	public void put(String key,Object value) {
		if (key.startsWith("_")) {
			commonDirty=true;
			commonData.put(key, value);
		} else {
			dataDirty=true;
			metaData.put(key, value);
		}
	}

	public Object get(String key) {
		if (key.startsWith("_")) {
			return commonData.get(key);
		} 
		return metaData.get(key);
	}
	
	public Set<String> keys() {
		return metaData.keySet();
	}

	@Override
	public boolean writesRevisionData() {
		return false;
	}
	
	/*
	 * Delegate methods to the JSON Object.
	 */	
	public JSONObject accumulate(String arg0, boolean arg1) {
		return commonData.accumulate(arg0, arg1);
	}
	public JSONObject accumulate(String arg0, double arg1) {
		return commonData.accumulate(arg0, arg1);
	}
	public JSONObject accumulate(String arg0, int arg1) {
		return commonData.accumulate(arg0, arg1);
	}
	public JSONObject accumulate(String arg0, long arg1) {
		return commonData.accumulate(arg0, arg1);
	}
	public JSONObject accumulate(String arg0, Object arg1) {
		return commonData.accumulate(arg0, arg1);
	}
	public void accumulateAll(Map arg0) {
		commonData.accumulateAll(arg0);
	}
	public void clear() {
		commonData.clear();
	}
	public boolean containsKey(Object arg0) {
		return commonData.containsKey(arg0);
	}
	public boolean containsValue(Object arg0) {
		return commonData.containsValue(arg0);
	}
	public JSONObject element(String arg0, boolean arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, Collection arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, double arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, int arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, long arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, Map arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject element(String arg0, Object arg1) {
		return commonData.element(arg0, arg1);
	}
	public JSONObject elementOpt(String arg0, Object arg1) {
		return commonData.elementOpt(arg0, arg1);
	}
	public Set entrySet() {
		return commonData.entrySet();
	}
	public Object get(Object arg0) {
		return commonData.get(arg0);
	}
	public boolean getBoolean(String arg0) {
		return commonData.getBoolean(arg0);
	}
	public double getDouble(String arg0) {
		return commonData.getDouble(arg0);
	}
	public int getInt(String arg0) {
		return commonData.getInt(arg0);
	}
	public JSONArray getJSONArray(String arg0) {
		return commonData.getJSONArray(arg0);
	}
	public JSONObject getJSONObject(String arg0) {
		return commonData.getJSONObject(arg0);
	}
	public long getLong(String arg0) {
		return commonData.getLong(arg0);
	}
	public String getString(String arg0) {
		return commonData.getString(arg0);
	}
	public boolean has(String arg0) {
		return commonData.has(arg0);
	}
	public Set keySet() {
		return commonData.keySet();
	}
	public JSONArray names() {
		return commonData.names();
	}
	public Object opt(String arg0) {
		return commonData.opt(arg0);
	}
	public boolean optBoolean(String arg0, boolean arg1) {
		return commonData.optBoolean(arg0, arg1);
	}
	public boolean optBoolean(String arg0) {
		return commonData.optBoolean(arg0);
	}
	public double optDouble(String arg0, double arg1) {
		return commonData.optDouble(arg0, arg1);
	}
	public double optDouble(String arg0) {
		return commonData.optDouble(arg0);
	}
	public int optInt(String arg0, int arg1) {
		return commonData.optInt(arg0, arg1);
	}
	public int optInt(String arg0) {
		return commonData.optInt(arg0);
	}
	public JSONArray optJSONArray(String arg0) {
		return commonData.optJSONArray(arg0);
	}
	public JSONObject optJSONObject(String arg0) {
		return commonData.optJSONObject(arg0);
	}
	public long optLong(String arg0, long arg1) {
		return commonData.optLong(arg0, arg1);
	}
	public long optLong(String arg0) {
		return commonData.optLong(arg0);
	}
	public String optString(String arg0, String arg1) {
		return commonData.optString(arg0, arg1);
	}
	public String optString(String arg0) {
		return commonData.optString(arg0);
	}
	public Object put(Object arg0, Object arg1) {
		return commonData.put(arg0, arg1);
	}
	public void putAll(Map arg0) {
		commonData.putAll(arg0);
	}
	public Object remove(Object arg0) {
		return commonData.remove(arg0);
	}
	public Object remove(String arg0) {
		return commonData.remove(arg0);
	}
	public int size() {
		return commonData.size();
	}
	public Collection values() {
		return commonData.values();
	}
	public boolean isEmpty() {
		return commonData.isEmpty();
	}

}
