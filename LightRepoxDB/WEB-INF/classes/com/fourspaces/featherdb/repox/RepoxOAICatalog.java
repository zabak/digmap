package com.fourspaces.featherdb.repox;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import com.fourspaces.featherdb.FeatherDB;
import com.fourspaces.featherdb.backend.Backend;
import com.fourspaces.featherdb.document.Document;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.BadResumptionTokenException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.IdDoesNotExistException;
import ORG.oclc.oai.server.verb.NoItemsMatchException;
import ORG.oclc.oai.server.verb.NoMetadataFormatsException;
import ORG.oclc.oai.server.verb.NoSetHierarchyException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;

public class RepoxOAICatalog extends AbstractCatalog {

    static final boolean debug=false;
    
    private SimpleDateFormat dateFormatter = new SimpleDateFormat();
    private String           homeDB;
    private HashMap          fileDateMap = new HashMap();
    private HashMap          resumptionResults = new HashMap();
    private int              maxListSize;
    private boolean hideExtension = false;
    private Backend db;
    
	public RepoxOAICatalog (Properties properties) {
			FeatherDB fdb = new FeatherDB();		
			db = fdb.getBackend();
			db.init(fdb);
			dateFormatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	        String temp=properties.getProperty("FileSystemOAICatalog.maxListSize");
	        if (temp==null) throw new IllegalArgumentException("FileSystemOAICatalog."+ "maxListSize is missing from the properties file");
	        maxListSize = Integer.parseInt(temp);
	        if(debug) System.out.println("in FileSystemOAICatalog(): maxListSize="+ maxListSize);
	        hideExtension = "true".equalsIgnoreCase(properties.getProperty("FileSystemOAICatalog.hideExtension"));
	        homeDB=properties.getProperty("FileSystemOAICatalog.homeDir");
	        if (homeDB==null) throw new IllegalArgumentException("FileSystemOAICatalog."+ "homeDir is missing from the properties file");
	        try {
	        	if(!db.doesDatabaseExist(homeDB)) fdb.addDatabase(homeDB);
	        } catch ( Exception e ) { throw new RuntimeException(e); }
	        if(debug) System.out.println("in FileSystemOAICatalog(): homeDB="+ homeDB);
	        loadFileMap(homeDB);
	}

	    private void loadFileMap(String currentDir) {
	    	Iterator<Document> list = db.allDocuments(currentDir).iterator();
			while(list.hasNext()) {
				Document child = db.getDocument(currentDir, list.next().getId());
		    	String localIdentifier = file2LocalIdentifier(child.getId());
		    	String datestamp = date2OAIDatestamp(child.getRevisionDate());
		    	fileDateMap.put(localIdentifier, datestamp);
		    }
	    }

	    private String date2OAIDatestamp(Date date) {
	        return dateFormatter.format(date);
	    }

	    private HashMap getNativeHeader(String localIdentifier) {
	        HashMap recordMap = null;
	        if (fileDateMap.containsKey(localIdentifier)) {
	            recordMap = new HashMap();
	            recordMap.put("localIdentifier", localIdentifier);
	            recordMap.put("lastModified", fileDateMap.get(localIdentifier));
	            return recordMap;
	        }
	        return recordMap;
	    }
	    
	    protected String file2LocalIdentifier(String path) {
		 String fileName = path.replace(java.io.File.separatorChar, '/');
		 if (hideExtension && fileName.endsWith(".xml")) {
		    fileName = fileName.substring(0, fileName.lastIndexOf(".xml"));
		 }
		 return fileName;
	    }

	    /**
	     * Override this method if you don't like the default localIdentifiers.
	     * @param localIdentifier the localIdentifier as parsed from the OAI identifier
	     * @return the File object containing the native record
	     */
	    protected String localIdentifier2Path(String localIdentifier) {
	        String fileName = localIdentifier.replace('/', java.io.File.separatorChar);
	        if (hideExtension) {
	        	fileName = fileName + ".xml";
	        }
	        return homeDB + java.io.File.separatorChar + fileName;
	    }
	    
	    private HashMap getNativeRecord(String localIdentifier) throws IOException {
	        HashMap recordMap = getNativeHeader(localIdentifier);
	        if (recordMap == null) { return null; } else {
	            String file = localIdentifier2Path(localIdentifier); 
	            if(db.doesDocumentExist(homeDB, file)) {
	            	Document doc = db.getDocument(homeDB, file);
	            	Writer writer = new StringWriter();
	                doc.writeCommonData(writer);
	                recordMap.put("recordBytes", writer.toString().getBytes() );
	                return recordMap;
	            } else return null;
	        }
	    }
	    
	    /**
	     * Retrieve the specified metadata for the specified oaiIdentifier
	     *
	     * @param     oaiIdentifier the OAI identifier
	     * @param     metadataPrefix the OAI metadataPrefix
	     * @return    the Record object containing the result.
	     * @exception CannotDisseminateFormatException signals an http status
	     *                code 400 problem
	     * @exception IdDoesNotExistException signals an http status code 404
	     *                problem
	     * @exception OAIInternalServerError signals an http status code 500
	     *                problem
	     */
	    public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
	        HashMap nativeItem = null;
	        try {
	        	String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
	            nativeItem = getNativeRecord(localIdentifier);
	            if (nativeItem == null) throw new IdDoesNotExistException(oaiIdentifier);
	            return constructRecord(nativeItem, metadataPrefix);
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new OAIInternalServerError("Database Failure");
	        }
	    }


	    /**
	     * Retrieve a list of schemaLocation values associated with the specified
	     * oaiIdentifier.
	     *
	     * We get passed the ID for a record and are supposed to return a list
	     * of the formats that we can deliver the record in.  Since we are assuming
	     * that all the records in the directory have the same format, the
	     * response to this is static;
	     *
	     * @param oaiIdentifier the OAI identifier
	     * @return a Vector containing schemaLocation Strings
	     * @exception OAIBadRequestException signals an http status code 400
	     *            problem
	     * @exception OAINotFoundException signals an http status code 404 problem
	     * @exception OAIInternalServerError signals an http status code 500
	     *            problem
	     */
	    public Vector getSchemaLocations(String oaiIdentifier)
	      throws IdDoesNotExistException, OAIInternalServerError, NoMetadataFormatsException {
	        HashMap nativeItem = null;
	        try {
	        	String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
	            nativeItem = getNativeRecord(localIdentifier);
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new OAIInternalServerError("Database Failure");
	        }
	        if (nativeItem != null) {
	            return getRecordFactory().getSchemaLocations(nativeItem);
	        } else {
	            throw new IdDoesNotExistException(oaiIdentifier);
	        }
	    }


	    /**
	     * Retrieve a list of Identifiers that satisfy the criteria parameters
	     *
	     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
	     * date is desired
	     * @param until ending date in the form of YYYY-MM-DD or null if latest
	     * date is desired
	     * @param set set name or null if no set is desired
	     * @return a Map object containing an optional "resumptionToken" key/value
	     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
	     * identifier keys with corresponding values of "true" or null depending on
	     * whether the identifier is deleted or not.
	     * @exception OAIBadRequestException signals an http status code 400
	     *            problem
	     */
	    public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException {
	        purge(); // clean out old resumptionTokens
	        Map listIdentifiersMap = new HashMap();
	        ArrayList headers = new ArrayList();
	        ArrayList identifiers = new ArrayList();
	        Iterator iterator = fileDateMap.entrySet().iterator();
	        int numRows = fileDateMap.entrySet().size();
	        int count = 0;
	        while (count < maxListSize && iterator.hasNext()) {
		    Map.Entry entryDateMap = (Map.Entry)iterator.next();
	            String fileDate = (String)entryDateMap.getValue();
	            if (fileDate.compareTo(from) >= 0
	                && fileDate.compareTo(until) <= 0) {
	                HashMap nativeHeader = getNativeHeader((String)entryDateMap.getKey());
	                String[] header = getRecordFactory().createHeader(nativeHeader);
	                headers.add(header[0]);
	                identifiers.add(header[1]);
	                count++;
	            }
	        }
	        if (count == 0) throw new NoItemsMatchException();
	        if (iterator.hasNext()) {
	        	String resumptionId = getRSName();
	        	resumptionResults.put(resumptionId, iterator);
	        	StringBuffer resumptionTokenSb = new StringBuffer();
	        	resumptionTokenSb.append(resumptionId);
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(count));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(numRows));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(metadataPrefix);
	            listIdentifiersMap.put("resumptionMap",
	 				   getResumptionMap(resumptionTokenSb.toString(),
	 						    numRows,
	 						    0));
	        }
	        	listIdentifiersMap.put("headers", headers.iterator());
	        	listIdentifiersMap.put("identifiers", identifiers.iterator());
	        	return listIdentifiersMap;
	    	}

	    /**
	     * Retrieve the next set of Identifiers associated with the resumptionToken
	     *
	     * @param resumptionToken implementation-dependent format taken from the
	     * previous listIdentifiers() Map result.
	     * @return a Map object containing an optional "resumptionToken" key/value
	     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
	     * identifier keys with corresponding values of "true" or null depending on
	     * whether the identifier is deleted or not.
	     * @exception OAIBadRequestException signals an http status code 400
	     *            problem
	     */
	    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException {
	        purge(); // clean out old resumptionTokens
	        Map listIdentifiersMap = new HashMap();
	        ArrayList headers = new ArrayList();
	        ArrayList identifiers = new ArrayList();
	        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
	        String resumptionId;
	        int oldCount;
	        String metadataPrefix;
	        int numRows;
	        try {
	            resumptionId = tokenizer.nextToken();
	            oldCount = Integer.parseInt(tokenizer.nextToken());
	            numRows = Integer.parseInt(tokenizer.nextToken());
	            metadataPrefix = tokenizer.nextToken();
	        } catch (NoSuchElementException e) {
	            throw new BadResumptionTokenException();
	        }
	        Iterator iterator = (Iterator)resumptionResults.remove(resumptionId);
	        if (iterator == null) {
	        	System.out.println("FileSystemOAICatalog.listIdentifiers: reuse of old resumptionToken?");
	        	iterator = fileDateMap.entrySet().iterator();
	        	for (int i = 0; i<oldCount; ++i)
	        		iterator.next();
	        }
	        int count = 0;
	        while (count < maxListSize && iterator.hasNext()) {
	        	Map.Entry entryDateMap = (Map.Entry)iterator.next();
	        	HashMap nativeHeader = getNativeHeader((String)entryDateMap.getKey());
	        	String[] header = getRecordFactory().createHeader(nativeHeader);
	        	headers.add(header[0]);
	        	identifiers.add(header[1]);
	        	count++;
	        }
	        if (iterator.hasNext()) {
	        	resumptionId = getRSName();
	        	resumptionResults.put(resumptionId, iterator);
	        	StringBuffer resumptionTokenSb = new StringBuffer();
	        	resumptionTokenSb.append(resumptionId);
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(oldCount + count));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(numRows));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(metadataPrefix);
	        	listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
									     numRows,
									     oldCount));
	        }
	        	listIdentifiersMap.put("headers", headers.iterator());
	        	listIdentifiersMap.put("identifiers", identifiers.iterator());
	        	return listIdentifiersMap;
	    	}

	    private String constructRecord(HashMap nativeItem, String metadataPrefix)
	        throws CannotDisseminateFormatException {
	        String schemaURL = null;
	        Iterator setSpecs = getSetSpecs(nativeItem);
	        Iterator abouts = getAbouts(nativeItem);
	        if (metadataPrefix != null) {
	            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
	                throw new CannotDisseminateFormatException(metadataPrefix);
	        }
	        return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix, setSpecs, abouts);
	    }

	    /**
	     * Retrieve a list of records that satisfy the specified criteria
	     *
	     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
	     * date is desired
	     * @param until ending date in the form of YYYY-MM-DD or null if latest
	     * date is desired
	     * @param set set name or null if no set is desired
	     * @param metadataPrefix the OAI metadataPrefix
	     * @return a Map object containing an optional "resumptionToken" key/value
	     * pair and a "records" Iterator object. The "records" Iterator contains a
	     * set of Records objects.
	     * @exception OAIBadRequestException signals an http status code 400
	     *            problem
	     * @exception OAIInternalServerError signals an http status code 500
	     *            problem
	     */
	    public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError, NoItemsMatchException {
	        purge(); // clean out old resumptionTokens
	        Map listRecordsMap = new HashMap();
	        ArrayList records = new ArrayList();
	        Iterator iterator = fileDateMap.entrySet().iterator();
	        int numRows = fileDateMap.entrySet().size();
	        int count = 0;
	        while (count < maxListSize && iterator.hasNext()) {
	        	Map.Entry entryDateMap = (Map.Entry)iterator.next();
	            String fileDate = (String)entryDateMap.getValue();
	            if (fileDate.compareTo(from) >= 0
	                && fileDate.compareTo(until) <= 0) {
	                try {
	                    HashMap nativeItem = getNativeRecord((String)entryDateMap.getKey());
	                    String record = constructRecord(nativeItem, metadataPrefix);
	                    records.add(record);
	                    count++;
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    throw new OAIInternalServerError(e.getMessage());
	                }
	            }
	        }
	        if (count == 0) throw new NoItemsMatchException();
	        if (iterator.hasNext()) {
	        	String resumptionId = getRSName();
	        	resumptionResults.put(resumptionId, iterator);
	        	StringBuffer resumptionTokenSb = new StringBuffer();
	        	resumptionTokenSb.append(resumptionId);
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(count));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(Integer.toString(numRows));
	        	resumptionTokenSb.append(":");
	        	resumptionTokenSb.append(metadataPrefix);
	        	listRecordsMap.put("resumptionMap",
	 				   getResumptionMap(resumptionTokenSb.toString(),
	 						    numRows,
	 						    0));
	        }
	        	listRecordsMap.put("records", records.iterator());
	        	return listRecordsMap;
	    	}


	    /**
	     * Retrieve the next set of records associated with the resumptionToken
	     *
	     * @param resumptionToken implementation-dependent format taken from the
	     * previous listRecords() Map result.
	     * @return a Map object containing an optional "resumptionToken" key/value
	     * pair and a "records" Iterator object. The "records" Iterator contains a
	     * set of Records objects.
	     * @exception OAIBadRequestException signals an http status code 400
	     *            problem
	     */
	    public Map listRecords(String resumptionToken) throws BadResumptionTokenException {
	        purge(); // clean out old resumptionTokens
	        Map listRecordsMap = new HashMap();
	        ArrayList records = new ArrayList();
	        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
	        String resumptionId;
	        int oldCount;
	        String metadataPrefix;
	        int numRows;
	        try {
	            resumptionId = tokenizer.nextToken();
	            oldCount = Integer.parseInt(tokenizer.nextToken());
	            numRows = Integer.parseInt(tokenizer.nextToken());
	            metadataPrefix = tokenizer.nextToken();
	        } catch (NoSuchElementException e) {
	            throw new BadResumptionTokenException();
	        }
	        Iterator iterator = (Iterator)resumptionResults.remove(resumptionId);
	        if (iterator == null) {
	        	System.out.println("FileSystemOAICatalog.listRecords: reuse of old resumptionToken?");
		    	iterator = fileDateMap.entrySet().iterator();
		    	for (int i = 0; i<oldCount; ++i)
		    		iterator.next();
	        }	        
	        int count = 0;
	        while (count < maxListSize && iterator.hasNext()) {
	        	Map.Entry entryDateMap = (Map.Entry)iterator.next();
	        	try {
	                HashMap nativeItem = getNativeRecord((String)entryDateMap.getKey());
	                String record = constructRecord(nativeItem, metadataPrefix);
	                records.add(record);
	                count++;
	            } catch (CannotDisseminateFormatException e) {
	                /* the client hacked the resumptionToken beyond repair */
	                throw new BadResumptionTokenException();
	            } catch (IOException e) {
	                /* the file is probably missing */
	                throw new BadResumptionTokenException();
		    }
		}
		if (iterator.hasNext()) {
		    resumptionId = getRSName();
		    resumptionResults.put(resumptionId, iterator);
		    StringBuffer resumptionTokenSb = new StringBuffer();
		    resumptionTokenSb.append(resumptionId);
		    resumptionTokenSb.append(":");
		    resumptionTokenSb.append(Integer.toString(oldCount + count));
		    resumptionTokenSb.append(":");
		    resumptionTokenSb.append(Integer.toString(numRows));
		    resumptionTokenSb.append(":");
		    resumptionTokenSb.append(metadataPrefix);
		    listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
									     numRows,
									     oldCount));
		}
	    listRecordsMap.put("records", records.iterator());
	    return listRecordsMap;
	    }


	    public void close() { }
	    
	    private synchronized static String getRSName() {
	        Date now = new Date();
	        return Long.toString(now.getTime());
	    }
	    
	    private void purge() {
	        ArrayList old = new ArrayList();
	        java.util.Date      then, now = new java.util.Date();
	        Iterator  keySet = resumptionResults.keySet().iterator();
	        String    key;

	        while (keySet.hasNext()) {
	            key=(String)keySet.next();
	            then=new Date(Long.parseLong(key)+getMillisecondsToLive());
	            if (now.after(then)) {
	                old.add(key);
	            }
	        }
	        Iterator iterator = old.iterator();
	        while (iterator.hasNext()) {
	            key = (String)iterator.next();
	            resumptionResults.remove(key);
	        }
	    }
	    
	    private Iterator getSetSpecs(HashMap nativeItem) {
	        return null;
	    }

	    private Iterator getAbouts(HashMap nativeItem) {
	        return null;
	    }

	    protected boolean isMetadataFile(String child) {
	        return true;
	    }

	    public Map listSets() throws NoSetHierarchyException {
	    	throw new NoSetHierarchyException();
	    }

	    public Map listSets(String resumptionToken) throws BadResumptionTokenException {
	    	throw new BadResumptionTokenException();
	    }
}