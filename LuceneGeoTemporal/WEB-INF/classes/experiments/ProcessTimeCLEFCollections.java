package experiments;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.tools.javac.util.Pair;
import com.wcohen.ss.BasicStringWrapper;
import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.StringWrapperIterator;
import com.wcohen.ss.tokens.SimpleTokenizer;

public class ProcessTimeCLEFCollections {
	
	protected static List<Pair<String,String>> proxy;
	
	protected static List<Pair<String,Pair<String,OpenGisDate>>> topics;
	
	protected Map<String,Map<String,Boolean>> relevanceTopics = new HashMap<String,Map<String,Boolean>>();

	protected Map<String,Map<String,Boolean>> relevanceDocuments = new HashMap<String,Map<String,Boolean>>();

	protected Map<String,Set<String>> judgedDocuments = new HashMap<String,Set<String>>();
	
	protected TextSimilarityScorer tfidf;
	
	protected TextSimilarityScorer tfidfh;
	
	protected int numDocumentsTest = -1;
	
	public ProcessTimeCLEFCollections () throws Exception {
		tfidf = new TextSimilarityScorer(new SimpleTokenizer(true,true));
		tfidfh = new TextSimilarityScorer(new SimpleTokenizer(true,true));
		proxy = new ArrayList<Pair<String,String>>();
		topics = new ArrayList<Pair<String,Pair<String,OpenGisDate>>>();
	 	File dtd = new File("geomarkup.dtd");
        dtd.createNewFile();
        dtd.deleteOnExit();
	}
	
	public static void printXML ( Element elm, PrintStream out ) throws Exception {
	    OutputFormat of = new OutputFormat("XML","UTF-8",true);
    	of.setIndent(1);
    	of.setIndenting(true);
    	XMLSerializer serializer = new XMLSerializer(out,of);
    	serializer.asDOMSerializer();
    	serializer.serialize( elm );
	}
	
	public static void setRandomProxy () {
        if (proxy==null) {
        	proxy = new ArrayList<Pair<String,String>>();
    		//proxy.add(new Pair<String,String>("72.55.191.6","3128"));
    		//proxy.add(new Pair<String,String>("87.101.94.30","3128"));
    		//proxy.add(new Pair<String,String>("80.80.111.133","3128"));
        }
		int aux = (int)(Math.random() * proxy.size());
        if (aux==proxy.size()) {
        	System.getProperties().put("http.proxySet", "false");
        } else {
        	Pair<String,String> p = proxy.get(aux);
        	System.getProperties().put("http.proxySet", "true");
        	System.setProperty("http.proxyHost", p.fst);
        	System.setProperty("http.proxyPort", p.snd);
        }
	}	
	
	public double timeSimilarity ( OpenGisDate g1, OpenGisDate g2, int formula ) {
		switch ( formula ) {
			case 1  : return g1.overlap(g2);
			default : return g1.diference(g2);
		}
	}
	
	public void addTopics ( String file ) throws Exception {
		String lang = file.toLowerCase().endsWith("pt") ? "pt-PT" : "en-EN";
		addTopics(file,lang);
	}
	
	public void addTopics ( String file, String lang ) throws Exception {
		if(new File(file).isDirectory()) {
			File files[] = new File(file).listFiles();
			for (int i=0; i<files.length; i++) if(files[i].getName().endsWith(".xml")) addTopics(files[i].getAbsolutePath(),lang);
		} else {
			System.out.println("Adding topics from file " + file);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder loader = factory.newDocumentBuilder();
		    Document document = loader.parse(new File(file));
		    Element tree = document.getDocumentElement();
		    NodeList nodes = tree.getElementsByTagName("top");
		    for (int j=0; j<nodes.getLength() && (((Element)(nodes.item(j))).getElementsByTagName("title")).getLength()>0; j++) {
		    	String topic = ((Element)(nodes.item(j))).getElementsByTagName("title").item(0).getTextContent();
		    	String id = ((Element)(nodes.item(j))).getElementsByTagName("num").item(0).getTextContent().trim();
		    	String description = ((Element)(nodes.item(j))).getElementsByTagName("desc").item(0).getTextContent() + 
		    	                     ". " + 
		    	                     ((Element)(nodes.item(j))).getElementsByTagName("narr").item(0).getTextContent();
		    	OpenGisDate geo = getOpenGisDate(temporalMining(description, topic, lang, null));
		    	if(geo==null) getOpenGisDate(temporalMining(topic, topic,lang, null));
		    	topics.add(new Pair<String,Pair<String,OpenGisDate>>(id.trim(),new Pair<String,OpenGisDate>(topic.toLowerCase().trim(),geo)));
		    }
		    nodes = tree.getElementsByTagName("topic");
		    for (int j=0; j<nodes.getLength(); j++) {
		    	String topic = ((Element)(nodes.item(j))).getElementsByTagName("title").item(0).getTextContent();
		    	String id = ((Element)(nodes.item(j))).getElementsByTagName("identifier").item(0).getTextContent().trim();
		    	String description = ((Element)(nodes.item(j))).getElementsByTagName("description").item(0).getTextContent() + 
		    	                     ". " + 
		    	                     ((Element)(nodes.item(j))).getElementsByTagName("narrative").item(0).getTextContent();
		    	OpenGisDate geo = getOpenGisDate(temporalMining(description, topic,lang, null));
		    	if(geo==null) getOpenGisDate(temporalMining(topic, topic,lang, null));
		    	topics.add(new Pair<String,Pair<String,OpenGisDate>>(id.trim(),new Pair<String,OpenGisDate>(topic.toLowerCase().trim(),geo)));
		    }
		    nodes = tree.getElementsByTagName("top");
		    String lang2 = lang.substring(lang.indexOf("-")+1);
		    for (int j=0; j<nodes.getLength() && (((Element)(nodes.item(j))).getElementsByTagName(lang2.toUpperCase() + "-title")).getLength()>0; j++) {
		    	String topic = ((Element)(nodes.item(j))).getElementsByTagName(lang2.toUpperCase() + "-title").item(0).getTextContent();
		    	String id = ((Element)(nodes.item(j))).getElementsByTagName("num").item(0).getTextContent().trim();
		    	String description = ((Element)(nodes.item(j))).getElementsByTagName(lang2.toUpperCase() + "-desc").item(0).getTextContent() + 
		    	                     ". " + 
		    	                     ((Element)(nodes.item(j))).getElementsByTagName(lang2.toUpperCase() + "-narr").item(0).getTextContent();
		    	OpenGisDate geo = getOpenGisDate(temporalMining(description, topic,lang, null));
		    	if(geo==null) getOpenGisDate(temporalMining(topic, topic,lang, null));
		    	topics.add(new Pair<String,Pair<String,OpenGisDate>>(id.trim(),new Pair<String,OpenGisDate>(topic.toLowerCase().trim(),geo)));
		    }
		}
	}
	
	public void addRelevance ( String relevance ) throws Exception {
      if(new File(relevance).isDirectory()) {
			File files[] = new File(relevance).listFiles();
			for (int i=0; i<files.length; i++) if(files[i].getName().endsWith(".txt")) addRelevance(files[i].getAbsolutePath());
	  } else {
		System.out.println("Adding relevance judgemnts from file " + relevance);
		BufferedReader rel = new BufferedReader(new FileReader(relevance));
		String j;
		while ((j=rel.readLine())!=null) {
			String aux[] = j.split(" ");
			if(aux[0].startsWith("0")) aux[0] = "GC"+aux[0];
			System.out.println(aux[0]+"\t"+aux[1]+"\t"+aux[2]+"\t"+aux[3]);	
			Set<String> tops = judgedDocuments.get(aux[2].trim());
			if(tops==null) tops = new HashSet<String>();
			tops.add(aux[0].trim());
			judgedDocuments.put(aux[2].trim(),tops);
			if (aux[3].trim().equals("1")) {
				Map m1 = relevanceTopics.get(aux[0].trim());
				if(m1==null) m1 = new HashMap<String,Boolean>();
				m1.put(aux[2].trim(),true);
				relevanceTopics.put(aux[0].trim(),m1);
				Map m2 = relevanceDocuments.get(aux[2].trim());
				if(m2==null) m2 = new HashMap<String,Boolean>();
				m2.put(aux[0].trim(),true);
				relevanceDocuments.put(aux[2].trim(),m2);
			}
		}
	  }
	}
	
	public void trainTextMetrics ( File path ) throws Exception {
		class MyInputStream extends InputStream {
	        InputStream in = null, r1 = null, r2 = null;
	        boolean closed = false;
	        public MyInputStream ( File f ) throws FileNotFoundException {
	        	this ( new BufferedInputStream(new FileInputStream(f)) );
	        }
	        public MyInputStream ( InputStream r ) {
	            r1 = new ByteArrayInputStream("<aux>".getBytes()); 
	            in = new BufferedInputStream(r); 
	            r2 = new ByteArrayInputStream("</aux>".getBytes());
	            closed = false;
	        } 
	        public int read() throws IOException {
	            if (closed) return -1;
	            int read = r1.read();
	            if(read!=-1) return read;
	            read = in.read();
	            if(read == '&') read = ' '; // TODO: replace this ugly fix!
	            if(read!=-1) return read;
	            read = r2.read();
	            return read;
	        }
	        public void close() throws IOException {
	           in.close();
	           closed = true;
	        }
		}
		class XMLFilenameFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				String ext = name.substring(name.lastIndexOf(".")+1, name.length());
				if (name.indexOf(".")!=-1 && ext.compareToIgnoreCase("sgml") == 0) return(true);
				if (name.indexOf(".")==-1 && name.startsWith("la")) return(true);
				else return(false);
			}
		}
		class MyStringWrapperIterator extends BasicStringWrapperIterator {
			boolean onlyHeadline;
			public MyStringWrapperIterator ( Iterator it, boolean h ) {
				super(it);
				this.onlyHeadline = h;
			}
			public StringWrapper nextStringWrapper() {
				String obj = ((StringWrapper)super.next()).unwrap();
				String file = obj.substring(0,obj.indexOf(" : ")).trim();
				String mid = obj.substring(obj.indexOf(" :" ) +3).trim();;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				Document document = null;
				try {
					document = factory.newDocumentBuilder().parse(new MyInputStream(new File(file)));
			    } catch ( Exception e ) {  }
				Element tree = document.getDocumentElement();
			    NodeList nodes = tree.getElementsByTagName("DOC");
			    for (int j=0; j<nodes.getLength(); j++) try {
			    	String id = ((Element)(nodes.item(j))).getElementsByTagName("DOCID").item(0).getTextContent().trim();
			    	if(!id.equals(mid)) continue;
			    	String text = ((Element)(nodes.item(j))).getElementsByTagName("TEXT").item(0).getTextContent().trim();
			    	String headline = text.substring(0,text.indexOf("\n") == -1 ? 0 : text.indexOf("\n"));
			    	try { headline = ((Element)(nodes.item(j))).getElementsByTagName("HEADLINE").item(0).getTextContent().trim();	} catch ( Exception e ) { }
			    	if (onlyHeadline) return new BasicStringWrapper(headline.toLowerCase());
			    	else return new BasicStringWrapper(text.toLowerCase() +". " + headline.toLowerCase());
			    } catch ( Exception e ) { return new BasicStringWrapper(""); }
				return new BasicStringWrapper("");
			}
		}
		File files[] = path.listFiles(new XMLFilenameFilter());
		Set<StringWrapper> wrappers = new HashSet<StringWrapper>();
		for (int i=0, numDocuments=0; i<files.length  && (numDocumentsTest==-1 || numDocuments<numDocumentsTest); i++) {
			System.out.println("Loading " + files[i]);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder loader = factory.newDocumentBuilder();
		    Document document = loader.parse(new MyInputStream(files[i]));
		    Element tree = document.getDocumentElement();
		    NodeList nodes = tree.getElementsByTagName("DOC");
		    for (int j=0; j<nodes.getLength() && (numDocumentsTest==-1 || numDocuments<numDocumentsTest); j++) {
		    	String id = ((Element)(nodes.item(j))).getElementsByTagName("DOCID").item(0).getTextContent().trim();		    	
		    	System.out.println("Adding " + files[i] + " " + id + " to the TF/IDF training examples.");
		    	wrappers.add(new BasicStringWrapper(files[i].getAbsolutePath() + " : " + id));
		    	numDocuments++;
		    }
		}
    	StringWrapperIterator it = new MyStringWrapperIterator(wrappers.iterator(),false);
    	tfidf.train(it);
    	it = new MyStringWrapperIterator(wrappers.iterator(),true);
    	tfidfh.train(it);
	}
	
	public OpenGisDate getOpenGisDate ( Document doc ) throws Exception {
		OpenGisDate date = null;
		javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
    	javax.xml.xpath.XPath xpath = factory.newXPath();
		NodeList list = (NodeList) xpath.compile("//TIMEX2/@VAL").evaluate(doc, XPathConstants.NODESET);
		for (int i=0; i<list.getLength(); i++) try {
			OpenGisDate d2 = new OpenGisDate(list.item(i).getNodeValue());
			if(date==null) date = d2; else {
				if(d2.getStartDate().before(date.getStartDate())) date.setStartDate(d2.getStartDate());
				if(d2.getEndDate().after(date.getEndDate())) date.setEndDate(d2.getEndDate());
			}
		} catch ( Exception e ) { }
		return date;
	}
	
	public Document temporalMining ( String data, String title, String lang, String baseDate ) throws Exception {
		setRandomProxy();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder loader = factory.newDocumentBuilder();
        String url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/";
        GetMethod get = new GetMethod(url);
        HttpClient client = new HttpClient();
        client.executeMethod( get );
        String response = get.getResponseBodyAsString();
        response = response.substring(response.indexOf("<INPUT TYPE=\"hidden\" id=\"session\" value='"));
        response = response.substring(response.indexOf("'")+1);
        response = response.substring(0,response.indexOf("'"));
        get.releaseConnection();
        url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/terseo.php";
        PostMethod post = new PostMethod(url);
        post.setParameter("session",response);
        post.setParameter("pDiaP",""+new Date().getDay());
        post.setParameter("pMesP",""+new Date().getMonth());
        post.setParameter("pAnyoP",""+new Date().getYear());
        post.setParameter("pIdioma","2");
        post.setParameter("pEsquema","1");
        post.setParameter("texto",data);
        post.setDoAuthentication( false );
        client.executeMethod( post );
        response = post.getResponseBodyAsString();
        Document document = loader.parse(new ByteArrayInputStream((("<aux>" +new String(response.getBytes("UTF-8"))+"</aux>").getBytes())));       
        return document;
	}
	
	public Document computeMetrics ( String id, String no, String headline, String data2, Document doc ) throws Exception {
		String data = data2 + "\n" + headline;
		NodeList lst = doc.getDocumentElement().getElementsByTagName("topicFeatures");
		while (lst.getLength()!=0) {
			doc.getDocumentElement().removeChild(lst.item(0));
			lst = doc.getDocumentElement().getElementsByTagName("topicFeatures");
		}
		OpenGisDate dataScope = getOpenGisDate(doc);
		for (int i=0; i<topics.size(); i++) {
			String topic = topics.get(i).fst;
			String topicText = topics.get(i).snd.fst;			
			OpenGisDate topicScope = topics.get(i).snd.snd;
			Element elm1 = doc.createElement("topicFeatures");
			elm1.setAttribute("topic", topic);
			elm1.setAttribute("docid", no);			
			elm1.setAttribute("relevance", relevanceDocuments.get(id) == null || relevanceDocuments.get(id).get(topic) == null || relevanceDocuments.get(id).get(topic) == false ? "0.0" : "1.0" );
			Element elmt = doc.createElement("textFeatures");
			elmt.setAttribute("length", "" + tfidf.length(data));
			elmt.setAttribute("headline-length", "" + tfidfh.length(headline));
			elmt.setAttribute("topic-length", "" + tfidf.length(topicText));
			elmt.setAttribute("tfidf", ""+tfidf.tfidf(topicText, data));
			elmt.setAttribute("bm25", ""+tfidf.bm25(topicText, data));
			elmt.setAttribute("tf", ""+tfidf.tf(topicText, data));
			elmt.setAttribute("idf", ""+tfidf.idf(topicText, data));
			elmt.setAttribute("headline-tfidf", ""+tfidfh.tfidf(topicText, headline));
			elmt.setAttribute("headline-bm25", ""+tfidfh.bm25(topicText, headline));
			elmt.setAttribute("headline-tf", ""+tfidfh.tf(topicText, headline));
			elmt.setAttribute("headline-idf", ""+tfidfh.idf(topicText, headline));
			Element elmg = doc.createElement("timeFeatures");
			elmg.setAttribute("distance", topicScope==null || dataScope==null ? "-1.0" : "" + timeSimilarity(topicScope,dataScope,0));
			elmg.setAttribute("overlap", topicScope==null || dataScope==null ? "-1.0" : "" + timeSimilarity(topicScope,dataScope,0));
			elm1.appendChild(elmt);
			elm1.appendChild(elmg);
			doc.getDocumentElement().appendChild(elm1);
		}
		return doc;
	}
	
	public void processTimeCLEF ( File in, File out, boolean reprocess ) throws Exception {
		class MyInputStream extends InputStream {
	        InputStream in = null, r1 = null, r2 = null;
	        boolean closed = false;
	        public MyInputStream ( File f ) throws FileNotFoundException {
	        	this ( new BufferedInputStream(new FileInputStream(f)) );
	        }
	        public MyInputStream ( InputStream r ) {
	            r1 = new ByteArrayInputStream("<aux>".getBytes()); 
	            in = new BufferedInputStream(r); 
	            r2 = new ByteArrayInputStream("</aux>".getBytes());
	            closed = false;
	        } 
	        public int read() throws IOException {
	            if (closed) return -1;
	            int read = r1.read();
	            if(read!=-1) return read;
	            read = in.read();
	            if(read == '&') read = ' '; // TODO: replace this ugly fix!
	            if(read!=-1) return read;
	            read = r2.read();
	            return read;
	        }
	        public void close() throws IOException {
	           in.close();
	           closed = true;
	        }
		}
		class XMLFilenameFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				String ext = name.substring(name.lastIndexOf(".")+1, name.length());
				if (name.indexOf(".")!=-1 && ext.compareToIgnoreCase("sgml") == 0) return(true);
				if (name.indexOf(".")==-1 && name.startsWith("la")) return(true);
				else return(false);
			}
		}
		String lang = in.getName().toLowerCase().endsWith("pt") ? "pt-PT" : "en-EN"; 
		File files[] = in.listFiles(new XMLFilenameFilter());
		for (int i=0, numDocuments=0; i<files.length && (numDocumentsTest==-1 || numDocuments<numDocumentsTest); i++) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder loader = factory.newDocumentBuilder();
		    Document document = loader.parse(new MyInputStream(files[i]));
		    Element tree = document.getDocumentElement();
		    NodeList nodes = tree.getElementsByTagName("DOC");
		    for (int j=0; j<nodes.getLength() && (numDocumentsTest==-1 || numDocuments<numDocumentsTest); j++) try {
		    	String id = ((Element)(nodes.item(j))).getElementsByTagName("DOCID").item(0).getTextContent().trim();
		    	String no = ((Element)(nodes.item(j))).getElementsByTagName("DOCNO").item(0).getTextContent().trim();
		    	String date = ((Element)(nodes.item(j))).getElementsByTagName("DATE").item(0).getTextContent().trim();
		    	System.out.println("** Processing document " + new File (new File(out,""+Math.abs(id.hashCode()) % 21),id+".xml").getAbsolutePath() + " " + no + " to compute features. ");
		    	if(!judgedDocuments.containsKey(no)) continue;
		    	String text = ((Element)(nodes.item(j))).getElementsByTagName("TEXT").item(0).getTextContent().trim();
		    	String headline = text.substring(0,text.indexOf("\n") == -1 ? 0 : text.indexOf("\n"));
		    	try { headline = ((Element)(nodes.item(j))).getElementsByTagName("HEADLINE").item(0).getTextContent().trim(); } catch ( Exception e ) {}
		    	File outdir = new File(out,""+Math.abs(id.hashCode()) % 21);
		    	Document doc ;
		    	if(reprocess) {
		    		try {
		    			doc = factory.newDocumentBuilder().parse(new FileInputStream(new File (outdir,id+".xml")));
		    			doc = computeMetrics(id, no, text.toLowerCase(), headline.toLowerCase(), doc);
		    		} catch ( Exception ex ) { doc = computeMetrics(id, no, text.toLowerCase(), headline.toLowerCase(), temporalMining(text,headline,lang,date)); }
		    	} else doc = computeMetrics(id, no, text.toLowerCase(), headline.toLowerCase(), temporalMining(text,headline,lang,date));
		    	outdir.mkdir();
		    	PrintStream output = new PrintStream(new FileOutputStream(new File (outdir,id+".xml")));
		    	printXML(doc.getDocumentElement(),output);
		    } catch ( Exception e ) { e.printStackTrace(); continue; }
		}
	}
	
	public void getL2RFeatureVectors ( File data, String outFileName ) throws Exception {
		PrintStream out = new PrintStream(new FileOutputStream( new File(outFileName)));
		out.println("@RELATION timeclef-learn-to-rank");
		out.println("@ATTRIBUTE topic                    STRING");
		out.println("@ATTRIBUTE topic-length			 NUMERIC");
		out.println("@ATTRIBUTE doclength				 NUMERIC");
		out.println("@ATTRIBUTE tf						 NUMERIC");
		out.println("@ATTRIBUTE idf				         NUMERIC");
		out.println("@ATTRIBUTE tfidf				     NUMERIC");
		out.println("@ATTRIBUTE bm25				     NUMERIC");
		out.println("@ATTRIBUTE headline-length			 NUMERIC");
		out.println("@ATTRIBUTE headline-tf				 NUMERIC");
		out.println("@ATTRIBUTE headline-idf			 NUMERIC");
		out.println("@ATTRIBUTE headline-tfidf			 NUMERIC");
		out.println("@ATTRIBUTE headline-bm25			 NUMERIC");
		out.println("@ATTRIBUTE distance				 NUMERIC");
		out.println("@ATTRIBUTE overlap					 NUMERIC");
		out.println("@ATTRIBUTE relevance				 NUMERIC");
		out.println("@DATA");
		class XMLFilenameFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				String ext = name.substring(name.lastIndexOf(".")+1, name.length());
				if (name.indexOf(".")!=-1 && ext.compareToIgnoreCase("xml") == 0) return(true);
				else return(false);
			}
		}
		File dirs[] = data.listFiles();
		for (int k=0; k<dirs.length; k++) if (dirs[k].isDirectory()) {
		File files[] = dirs[k].listFiles(new XMLFilenameFilter());
		for (int i=0; i<files.length; i++) try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder loader = factory.newDocumentBuilder();
		    Document document = loader.parse(new FileInputStream(files[i]));
		    Element tree = document.getDocumentElement();
		    NodeList nodes = tree.getElementsByTagName("topicFeatures");
		    for (int j=0; j<nodes.getLength(); j++) try {
		    	Element text = (Element)(((Element)(nodes.item(j))).getElementsByTagName("textFeatures").item(0));
		    	Element time = (Element)(((Element)(nodes.item(j))).getElementsByTagName("timeFeatures").item(0));
		    	String docid = ((Element)(nodes.item(j))).getAttributes().getNamedItem("docid").getNodeValue().trim();
		    	String topic = ((Element)(nodes.item(j))).getAttributes().getNamedItem("topic").getNodeValue().trim();
		    	String id =  docid + "_" + topic;
		    	boolean rel1 = relevanceDocuments.get(docid) == null ? false : relevanceDocuments.get(docid).get(topic) == null ? false : relevanceDocuments.get(docid).get(topic);
		    	boolean rel2 = relevanceTopics.get(topic) == null ?  false : relevanceTopics.get(topic).get(docid) == null ? false : relevanceTopics.get(topic).get(docid);
		    	String relevance = ((Element)(nodes.item(j))).getAttributes().getNamedItem("relevance").getNodeValue().trim();
		    	relevance = (rel1 || rel2) ? "1.0" : "0.0";
		    	String length = text.getAttributes().getNamedItem("length").getNodeValue();
		    	String hlength = text.getAttributes().getNamedItem("headline-length").getNodeValue();
		    	String tlength = text.getAttributes().getNamedItem("topic-length").getNodeValue();
		    	String tf = text.getAttributes().getNamedItem("tf").getNodeValue();
		    	String idf = text.getAttributes().getNamedItem("idf").getNodeValue();
		    	String tfidf = text.getAttributes().getNamedItem("tfidf").getNodeValue();
		    	String bm25 = text.getAttributes().getNamedItem("bm25").getNodeValue();
		    	String htf = text.getAttributes().getNamedItem("headline-tf").getNodeValue();
		    	String hidf = text.getAttributes().getNamedItem("headline-idf").getNodeValue();
		    	String htfidf = text.getAttributes().getNamedItem("headline-tfidf").getNodeValue();
		    	String hbm25 = text.getAttributes().getNamedItem("headline-bm25").getNodeValue();
		    	String distance = time.getAttributes().getNamedItem("distance").getNodeValue();
		    	String overlap = time.getAttributes().getNamedItem("overlap").getNodeValue();
		    	out.println('"' + id + '"' + "," + tlength + "," + length + "," + tf + "," + idf + "," + tfidf + "," + bm25  + "," + hlength + "," + htf + "," + hidf + "," + htfidf + "," + hbm25 + ","  + distance + "," + overlap + "," + "," + relevance);
		    } catch ( Exception e ) { e.printStackTrace(); continue; }
		} catch ( Exception e ) { e.printStackTrace(); continue; }
		}
	}
	
	public void weka2SVMMAP ( String path, boolean filterRelevanceJudgements ) throws Exception {
		File input = new File(path);
		if (path.indexOf(".")!=-1) path = path.substring(0,path.lastIndexOf(".")) + ".svmmap";
		File output = new File(path);
		PrintStream out = new PrintStream(new FileOutputStream(output));
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String aux;
		Map<Integer,Pair<Double,Double>> normalization = new HashMap<Integer,Pair<Double,Double>>();
		while ( (aux=reader.readLine())!=null && !aux.equals("@DATA")) {};
		while ( (aux=reader.readLine())!=null) {
			String data[] = aux.split(",");
			String t = data[0].substring(data[0].indexOf("_")+1,1+data[0].substring(1).indexOf("\""));
			String t2 = t.indexOf("/")!=-1 ? t.substring(t.indexOf("/")+1,t.lastIndexOf("-")).replaceAll("[A-Z]*", "") : t.replaceAll("[A-Z]*", "");
			int qid = new Integer(t2) - 1;
			Double maxScore = normalization.get(qid) == null ? null : normalization.get(qid).fst;
			Double minScore = normalization.get(qid) == null ? null : normalization.get(qid).snd;
			if(maxScore==null) maxScore = new Double(0);
			if(minScore==null) minScore = new Double(1.0);
			for (int i=1; i<data.length-1; i++) {
				Double nscore = new Double(data[i]);
				if(nscore > maxScore) maxScore = nscore;
				if(nscore < minScore && nscore>=0.0) minScore = nscore;				
			}
			normalization.put(qid,new Pair<Double,Double>(maxScore,minScore));
		}
		reader = new BufferedReader(new FileReader(input));
		while ( (aux=reader.readLine())!=null && !aux.equals("@DATA")) {};
		while ( (aux=reader.readLine())!=null) {
			String data[] = aux.split(",");
			String t = data[0].substring(data[0].indexOf("_")+1,1+data[0].substring(1).indexOf("\""));
			String did = data[0].substring(1,data[0].indexOf("_")).trim();			
			String t2 = t.indexOf("/")!=-1 ? t.substring(t.indexOf("/")+1,t.lastIndexOf("-")).replaceAll("[A-Z]*", "") : t.replaceAll("[A-Z]*", "");
			int qid = new Integer(t2) - 1;
			if(!filterRelevanceJudgements || (judgedDocuments.containsKey(did) && judgedDocuments.get(did).contains(t))) {
				out.print(data[data.length-1] + " qid:" + qid);
				double max = normalization.get(qid).fst;
				double min = normalization.get(qid).snd;
				double mm = max - min;
				double w = 0.0;
				for (int i=1; i<data.length-1; i++) {
					w = mm == 0.0 || new Double(data[i]) <= 0.0 ? new Double(data[i]) : (new Double(data[i]) - min) / mm;
					out.print(" "+i+":"+w);
				}
				double distance = mm == 0.0 ? new Double(data[12]) : (new Double(data[12]) - min) / mm;
				double overlap = mm == 0.0 ? new Double(data[13]) : (new Double(data[13]) - min) / mm;
				double tfidf = mm == 0.0 ? new Double(data[3]) : (new Double(data[3]) - min) / mm;
				double bm25 = mm == 0.0 ? new Double(data[4]) : (new Double(data[4]) - min) / mm;
				w = (distance + tfidf) * 0.5; out.print(" "+(data.length-1+0)+":"+ w);
				w = (distance + bm25)  * 0.5; out.print(" "+(data.length-1+1)+":"+ w);
				w = (overlap + tfidf)  * 0.5; out.print(" "+(data.length-1+2)+":"+ w);
				w = (overlap + bm25)   * 0.5; out.print(" "+(data.length-1+3)+":"+ w);
				out.println(" #docid = "+did);
			}
		}
		out.close();
		reader.close();
	}
	
	public void topicInfo() {
		System.out.println("*** General topic information ***");		
		int termSize[] = new int[4];
		double area[][] = new double[4][25];
		for (int i=0, j=-1; i<topics.size(); i++) {
			if(i % 25 == 0) { j++; };
			System.out.println(topics.get(i).fst.toString() + " " + topics.get(i).snd.fst + " " + (topics.get(i).snd.snd == null ? 0 : topics.get(i).snd.snd.getDuration() ) + " " + (topics.get(i).snd.snd == null ? "none" : topics.get(i).snd.snd.toString() ));
			if (topics.get(i).snd.fst==null) System.out.println(" *** " + topics.get(i).fst);
			else termSize[j] += topics.get(i).snd.fst.split(" ").length;
			area[j][i % 25] = topics.get(i).snd.snd == null ? 0 : topics.get(i).snd.snd.getDuration();
		}
		for (int i=0, j=-1; i<topics.size(); i++) {
			if(i % 25 == 0) { j++; };
			String col = "0.0";
			if (area[j][i % 25]==0) col = "2.0";
			System.out.println(i + "\t" + col + " # " + topics.get(i).snd.fst + " : " + (topics.get(i).snd.snd == null ? "" :  topics.get(i).snd.snd.toString() ));			
		}
		for (int i=0; i<4; i++) {
			System.out.println("===============================");
			System.out.println("Average Term size colection " + i + " = " + (termSize[i]/25.0));
			int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;
			for (int j=0; j<25; j++) {
				sum1 += area[i][j] ==   0 ? 1 : 0;
				sum2 += area[i][j] <  100 ? 1 : 0;
				sum3 += area[i][j] >= 100 && area[i][j] <= 1000 ? 1 : 0;
				sum4 += area[i][j] > 1000 ? 1 : 0;
			}
			System.out.println("Duration 0 for collection " + i + " = " + sum1);
			System.out.println("Duration <100 for collection " + i + " = " + sum2);
			System.out.println("Duration >100 for collection " + i + " = " + sum3);
			System.out.println("Duration >1000 for collection " + i + " = " + sum4);
		}
	}
	
	public static void main ( String args[] ) throws Exception {
		Logger logger = Logger.getRootLogger();
		logger.setLevel(Level.ERROR);
		ProcessTimeCLEFCollections aux = new ProcessTimeCLEFCollections();
		String data = "/home/datasets/public_html/time-clef/clef-en";
		
		data = "/Users/bmartins/Desktop/time-clef";
		
		if(args.length==1) data = args[0];		
		aux.addTopics(data + File.separator + "topics"); 
		aux.addRelevance(data + File.separator + "topics");
		aux.topicInfo();
		aux.trainTextMetrics(new File(data));
		aux.processTimeCLEF(new File(data),new File(data + File.separator + "output"), false);
		aux.getL2RFeatureVectors(new File(data + File.separator + "output"),data + File.separator + "output" + File.separator + "results.arff");
		aux.weka2SVMMAP(data + File.separator + "output" + File.separator + "results.arff", true);
	}
    
}
