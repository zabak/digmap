package pt.utl.ist.lucene.treceval.geotime.webservices;

import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.GMLGeometryAdapter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.*;
import java.net.*;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.tools.javac.util.Pair;
import com.vividsolutions.jts.geom.Geometry;
import pt.utl.ist.lucene.config.ConfigProperties;

public class CallWebServices {


    static final String proxyHost = ConfigProperties.getProperty("proxy.host");
    static final int proxyPort = ConfigProperties.getIntProperty("proxy.port");
    private static final Logger logger = Logger.getLogger(CallWebServices.class);
    private static Proxy httpProxy = Proxy.NO_PROXY;
    protected static String yahooAppId = "AVNVvo3V34EOqIaAO7Uo.CrlQeGg8Ss43EhQfPm0HMZjqnkSUtA2MkhAiTkQ6T3XE6FWGg--";


    public static org.w3c.dom.Document callServices ( String data, String title, int year, int month, int day ,String file, String id) throws Exception {
        if(proxyHost != null && !proxyHost.equals("proxy.host"))
        {
            System.getProperties().setProperty("http.proxyHost",proxyHost);
            System.getProperties().setProperty("http.proxyPort","" + proxyPort);
            InetSocketAddress addr = new InetSocketAddress(proxyHost,proxyPort);
            httpProxy = new Proxy(Proxy.Type.HTTP, addr);
            System.out.println("Using Proxy:" + proxyHost + ":" + proxyPort);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder loader = factory.newDocumentBuilder();
        HttpClient client = new HttpClient();
        if(proxyHost != null && !proxyHost.equals("proxy.host"))
            client.getHostConfiguration().setProxy(proxyHost,proxyPort);
        try{



            String url = "http://wherein.yahooapis.com/v1/document";
            PostMethod post = new PostMethod(url);

            post.addParameter("documentType","text/plain");
            post.addParameter("appid", yahooAppId);
            post.addParameter("documentContent", data);
            if(title != null)
                post.addParameter("documentTitle", title);
            post.addParameter("inputLanguage", "en-EN");
            post.setDoAuthentication( false );
            client.executeMethod( post );
            String response = post.getResponseBodyAsString();
            Document document;
            try{
                document = loader.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
            }catch(Exception e)
            {
                logger.error("Trying this XML");
                logger.error(response);
                throw e;
            }
            post.releaseConnection();
            String geo = getGeometryString(document);
            String xml = geo.toString();

            logger.info(xml);


            Document docGmlBox = loader.parse(new ByteArrayInputStream(xml.getBytes()));

            Node elmDest = ((Element)(document.getFirstChild())).getElementsByTagName("document").item(0);
            if(elmDest == null)
            {
                logger.error("PlaceMaker dont did not bring doc tag >>>>>>");
                logger.error(xml);
            }
            Element box = (Element) document.importNode(docGmlBox.getFirstChild(),true);
            if(box == null)
            {
                logger.error("PlaceMaker geometry did not bring Geometry >>>>>>");
                logger.error(geo);
            }
            elmDest.appendChild(box);
//            try{
//                url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/";
//                GetMethod get = new GetMethod(url);
//                client = new HttpClient();
//                client.getHostConfiguration().setProxy("proxy1.ipp.pt",3128);
//                client.executeMethod( get );
//                response = get.getResponseBodyAsString();
//                response = response.substring(response.indexOf("<INPUT TYPE=\"hidden\" id=\"session\" value='"));
//                response = response.substring(response.indexOf("'")+1);
//                response = response.substring(0,response.indexOf("'"));
//                get.releaseConnection();
//                url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/terseo.php";
//                post = new PostMethod(url);
//                post.setParameter("session",response);
//                if(year<0) {
//                    post.setParameter("pDiaP",""+new Date().getDay());
//                    post.setParameter("pMesP",""+new Date().getMonth());
//                    post.setParameter("pAnyoP",""+new Date().getYear());
//                } else {
//                    post.setParameter("pDiaP",""+day);
//                    post.setParameter("pMesP",""+month);
//                    post.setParameter("pAnyoP",""+year);
//                }
//                post.setParameter("pIdioma","2");
//                post.setParameter("pEsquema","1");
//                post.setParameter("texto",data);
//                post.setDoAuthentication( false );
//                client.executeMethod( post );
//                response = post.getResponseBodyAsString();
////        org.dom4j.Document document2 = Dom4jUtil.parse("<aux>" + response.replace("&amp ;","&amp;") + "</aux>").getDocument();
//
//
//                Document document2 = loader.parse(new ByteArrayInputStream((("<aux>" + response.replace("&amp ;","&amp;") + "</aux>").getBytes())));
//                Node elmDest = ((Element)(((Element)(document.getFirstChild())).getElementsByTagName("document").item(0))).getElementsByTagName("referenceList").item(0);
//                if(elmDest == null)
//                {
//                    if(id.equals("NYT_ENG_20020101.0013"))
//                        System.out.println("NYT_ENG_20020101.0013");
//                    logger.error(id + "@" + file + " - dont have referenceList from yahoo PlaceMaker");
//                    logger.error("DATA:" + data);
//                    StringWriter w = new StringWriter();
//                    serializeDoc(w,document);
//                    logger.error(w.toString());
//                    logger.error("adding referenceList to document");
//                    Element elem = (Element)(((Element)(document.getFirstChild())).getElementsByTagName("document").item(0));
//                    elmDest = document.createElement("referenceList");
//                    elem.appendChild(elmDest);
//                }
//                NodeList elmSource = ((Element)(document2.getFirstChild())).getElementsByTagName("TIMEX2");
//                ArrayList<Integer> positionsBegin = new ArrayList<Integer>();
//                ArrayList<Integer> positionsEnd = new ArrayList<Integer>();
//                String aux = new String(response.getBytes("UTF-8"));
//                int last = 0;
//                while ( aux.indexOf("<TIMEX2") >= 0 ) {
//                    int begin = aux.indexOf("<TIMEX2");
//                    int end = aux.indexOf("</TIMEX2");
//                    positionsBegin.add(last + begin);
//                    positionsEnd.add(last + end);
//                    last = end + 1;
//                    aux = aux.substring(last);
//                }
//                if(elmSource.getLength()==0)
//                    logger.error("Article don't have time references: " + id + " - @ " + file);
//                for (int i=0; i<elmSource.getLength(); i++) {
//                    Element element = document.createElement("timeReference");
//                    Element eText = document.createElement("text");
//                    if(elmSource.item(i).getFirstChild() != null)
//                    {
//                        eText.appendChild(document.createTextNode(elmSource.item(i).getFirstChild().getNodeValue()));
//                    }
//                    element.appendChild(eText);
//                    Element eValue = document.createElement("value");
//                    if(elmSource.item(i).getAttributes() != null && elmSource.item(i).getAttributes().getNamedItem("VAL") != null)
//                    {
//                        eValue.appendChild(document.createTextNode(elmSource.item(i).getAttributes().getNamedItem("VAL").getNodeValue()));
//                        element.appendChild(eValue);
//                        Element eStart = document.createElement("start");
//                        eStart.appendChild(document.createTextNode(""+positionsBegin.get(i)));
//                        element.appendChild(eStart);
//                        Element eEnd = document.createElement("end");
//                        eEnd.appendChild(document.createTextNode(""+positionsEnd.get(i)));
//                        element.appendChild(eEnd);
//                    }
//                    try{
//                        elmDest.appendChild(element);
//                    }
//                    catch(NullPointerException e)
//                    {
//                        logger.error(id + "@" + file + ":" + e,e);
//                        logger.error("data:" + response);
//                    }
//                }
//            }
//            catch(Throwable e)
//            {
//                logger.error("TERSEO: " + id + " - @ " + file + ":" + e.toString(),e);
//                logger.error("data:" + response);
//            }
            return document;
        }
        catch(Exception e)
        {
            logger.error(id + " - @ " + file + ":" + e.toString(),e);
            throw e;
        }
    }


    public static org.w3c.dom.Document callTimextag(String url, String xml, String title, int year, int month, int day ,String file, String id) throws Exception {
        if(proxyHost != null && !proxyHost.equals("proxy.host"))
        {
            System.getProperties().setProperty("http.proxyHost",proxyHost);
            System.getProperties().setProperty("http.proxyPort","" + proxyPort);
            InetSocketAddress addr = new InetSocketAddress(proxyHost,proxyPort);
            httpProxy = new Proxy(Proxy.Type.HTTP, addr);
            System.out.println("Using Proxy:" + proxyHost + ":" + proxyPort);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder loader = factory.newDocumentBuilder();
        HttpClient client = new HttpClient();
        if(proxyHost != null && !proxyHost.equals("proxy.host"))
            client.getHostConfiguration().setProxy(proxyHost,proxyPort);
        try{

            PostMethod post = new PostMethod(url);

            xml = xml.replace("\\R"," ").replace("\\S"," ").replace("\\T"," ").replace("\\N"," ").replace("\\","/").replace("Eastern","east").replace("millennia ago"," ");
            post.addParameter("debug","false");
            post.addParameter("service", "sgml2Timexes");
            post.addParameter("t", "1");
            post.addParameter("input", xml);
            post.setDoAuthentication( false );
            client.executeMethod( post );
            String response = post.getResponseBodyAsString();
            Document document;
            try{
                document = loader.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
            }catch(Exception e)
            {
                logger.error("Parsing this XML:");
                logger.error(xml);
                logger.error("Response from tagger:");
                logger.error(response);
                throw e;
            }
            post.releaseConnection();
//            String geo = getGeometryString(document);


            logger.info(xml);


//            Document docGmlBox = loader.parse(new ByteArrayInputStream(xml.getBytes()));
//
//            Node elmDest = ((Element)(document.getFirstChild())).getElementsByTagName("document").item(0);
//            if(elmDest == null)
//            {
//                logger.error("PlaceMaker dont did not bring doc tag >>>>>>");
//                logger.error(xml);
//            }
//            Element box = (Element) document.importNode(docGmlBox.getFirstChild(),true);
//            if(box == null)
//            {
//                logger.error("PlaceMaker geometry did not bring Geometry >>>>>>");
//                logger.error(geo);
//            }
//            elmDest.appendChild(box);

            return document;
        }
        catch(Exception e)
        {
            logger.error(id + " - @ " + file + ":" + e.toString(),e);
            throw e;
        }
    }

    protected static List<Pair<String,String>> proxy;

    public static void setRandomProxy () {
        if (proxy==null) {
            proxy = new ArrayList<Pair<String,String>>();
//                proxy.add(new Pair<String,String>("72.55.191.6","3128"));
//                proxy.add(new Pair<String,String>("87.101.94.30","3128"));
//                proxy.add(new Pair<String,String>("80.80.111.133","3128"));
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


    private static Geometry getGeometry(Document doc) throws Exception {
        return JTSAdapter.export(GMLGeometryAdapter.wrap(getGeometryString(doc)));
    }

    private static Geometry getGeometry(String geometry) throws Exception {
        return JTSAdapter.export(GMLGeometryAdapter.wrap(geometry));
    }

    private static String getGeometryString ( Document doc ) throws Exception {
        NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                String uri;
                if (prefix.equals("yahoo"))
                    uri = "http://www.yahooapis.com/v1/base.rng";
                else if (prefix.equals("ys"))
                    uri = "http://wherein.yahooapis.com/v1/schema";
                else if (prefix.equals("ys2"))
                    uri = "http://where.yahooapis.com/v1/schema.rng";
                else if (prefix.equals("gml"))
                    uri = "http://www.opengis.net/gml";
                else
                    uri = null;
                return uri;
            }
            public Iterator getPrefixes(String val) { return null; }
            public String getPrefix(String uri) { return null; }
        };
        try {
            setRandomProxy();

            List<Geometry> list = new ArrayList<com.vividsolutions.jts.geom.Geometry>();
            NodeList lst = doc.getDocumentElement().getElementsByTagName("gml:Box");
            StringWriter sw = new StringWriter();
            ByteArrayOutputStream w = new ByteArrayOutputStream();
            if (lst.getLength()!=0) {
//                return JTSAdapter.export(GMLGeometryAdapter.wrap((Element)(lst.item(0))));
                printXML((Element) lst.item(0),new PrintStream(w));
                return new String(w.toByteArray());
            }
            javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(ctx);
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setValidating(false);
            dfactory.setNamespaceAware(true);
            DocumentBuilder loader = dfactory.newDocumentBuilder();
            printXML(doc.getDocumentElement(),new PrintStream(w));
            Document doc2 = loader.parse(new ByteArrayInputStream(w.toByteArray()));
            String name = xpath.compile("//ys:geographicScope/ys:name/text()").evaluate(doc2);
            String woeid = xpath.compile("//ys:geographicScope/ys:woeId/text()").evaluate(doc2);
            String swLat = xpath.compile("//ys:extents/ys:southWest/ys:latitude/text()").evaluate(doc2);
            String swLon = xpath.compile("//ys:extents/ys:southWest/ys:longitude/text()").evaluate(doc2);
            String neLat = xpath.compile("//ys:extents/ys:northEast/ys:latitude/text()").evaluate(doc2);
            String neLon = xpath.compile("//ys:extents/ys:northEast/ys:longitude/text()").evaluate(doc2);
            List<String> parents = new ArrayList<String>();
            if(woeid!=null && woeid.trim().length()>0 && !woeid.trim().equals("1")) {
                if(woeid.equals("2461607") || woeid.equals("55959673")) { woeid = "55959673"; name = "North Sea"; }
                try {
                    String url = "http://where.yahooapis.com/v1/place/"+woeid+"/belongtos;count=0?appid=" + yahooAppId;
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection(httpProxy);
                    doc2 = loader.parse(conn.getInputStream());
                    NodeList auxp = (NodeList) xpath.compile("//ys2:place/ys2:woeid/text()").evaluate(doc2, XPathConstants.NODESET);
                    for (int i=auxp.getLength()-1; i>=0; i--) parents.add(auxp.item(i).getNodeValue().trim());
                    parents.add(woeid);
                    conn.disconnect();
                } catch ( Exception e ) { e.printStackTrace(); }
                try {
                    String url = "http://where.yahooapis.com/v1/place/" + woeid + "?appid=" + yahooAppId;

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection(httpProxy);
                    doc2 = loader.parse(conn.getInputStream());
                    swLat = xpath.compile("//ys2:boundingBox/ys2:southWest/ys2:latitude/text()").evaluate(doc2);
                    swLon = xpath.compile("//ys2:boundingBox/ys2:southWest/ys2:longitude/text()").evaluate(doc2);
                    neLat = xpath.compile("//ys2:boundingBox/ys2:northEast/ys2:latitude/text()").evaluate(doc2);
                    neLon = xpath.compile("//ys2:boundingBox/ys2:northEast/ys2:longitude/text()").evaluate(doc2);
                } catch ( Exception e ) { e.printStackTrace(); }
            }
//			if(swLat==null || swLat.length()==0 || sw.toString().trim().length()==0)
            if(swLat==null || swLat.length()==0)
                sw.write("<gml:Box xmlns:gml='http://www.opengis.net/gml'><gml:coord><gml:X>-180</gml:X><gml:Y>-90</gml:Y></gml:coord><gml:coord><gml:X>180</gml:X><gml:Y>90</gml:Y></gml:coord></gml:Box>");
            else
                sw.write("<gml:Box xmlns:gml='http://www.opengis.net/gml'><gml:coord><gml:X>" + swLon + "</gml:X><gml:Y>" + swLat + "</gml:Y></gml:coord><gml:coord><gml:X>" + neLon + "</gml:X><gml:Y>" + neLat + "</gml:Y></gml:coord></gml:Box>");
//        	Geometry geo = JTSAdapter.export(GMLGeometryAdapter.wrap(sw.toString()));
//        	geo.setUserData(new Pair<String,List<String>>(name,parents));
//          return geo;
            return sw.toString();
        } catch ( Throwable e ) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            sw.write("<gml:Box xmlns:gml='http://www.opengis.net/gml'><gml:coord><gml:X>-180</gml:X><gml:Y>-90</gml:Y></gml:coord><gml:coord><gml:X>180</gml:X><gml:Y>90</gml:Y></gml:coord></gml:Box>");
//        	Geometry geo = JTSAdapter.export(GMLGeometryAdapter.wrap(sw.toString()));
//        	List<String> parents = new ArrayList<String>();
//        	parents.add("1");
//        	geo.setUserData(new Pair<String,List<String>>("World",parents));
//        	return geo;
            return sw.toString();
        }
    }


    public static void printXML ( Element elm, PrintStream out ) throws Exception {
        OutputFormat of = new OutputFormat("XML","UTF-8",true);
        of.setIndent(1);
        of.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(out,of);
        serializer.asDOMSerializer();
        serializer.serialize( elm );
    }

    public static void main ( String args[] ) throws Exception {
        String data = "John Doe lives in the city of Lisbon, in Portugal. John Doe visited France, during the month of December 2004";
        String title = "Test title";
        Document doc = callServices( data, title, 2009,11,11,"test","test");
        serializeDoc(System.out,doc);
    }

    private static void serializeDoc(OutputStream stream, Document doc) throws IOException {
        OutputFormat of = new OutputFormat("XML","UTF-8",true);
        of.setIndent(1);
        of.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(stream,of);
        serializer.asDOMSerializer();
        serializer.serialize( doc.getDocumentElement() );
    }

    private static void serializeDoc(Writer writer, Document doc) throws IOException {
        OutputFormat of = new OutputFormat("XML","UTF-8",true);
        of.setIndent(1);
        of.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(writer,of);
        serializer.asDOMSerializer();
        serializer.serialize( doc.getDocumentElement() );
    }

}
