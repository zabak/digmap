package experiments;

/**
 * @author Jorge Machado
 * @date 24/Nov/2009
 * @time 9:44:03
 * @email machadofisher@gmail.com
 */
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
public class CallWebServices {


    protected static String yahooAppId = "AVNVvo3V34EOqIaAO7Uo.CrlQeGg8Ss43EhQfPm0HMZjqnkSUtA2MkhAiTkQ6T3XE6FWGg--";

    public static Document callServices ( String data, String title, Date baseReference ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder loader = factory.newDocumentBuilder();
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setProxy("proxy1.ipp.pt",3128);
        String url = "http://wherein.yahooapis.com/v1/document";
        PostMethod post = new PostMethod(url);

        post.addParameter("documentType","text/plain");
        post.addParameter("appid",yahooAppId);
        post.addParameter("documentContent", data);
        post.addParameter("documentTitle", title);
        post.addParameter("inputLanguage", "en-EN");
        post.setDoAuthentication( false );
        client.executeMethod( post );
        String response = post.getResponseBodyAsString();
        Document document = loader.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
        post.releaseConnection();
        url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/";
        GetMethod get = new GetMethod(url);
        client = new HttpClient();
        client.getHostConfiguration().setProxy("proxy1.ipp.pt",3128);
        client.executeMethod( get );
        response = get.getResponseBodyAsString();
        response = response.substring(response.indexOf("<INPUT TYPE=\"hidden\" id=\"session\" value='"));
        response = response.substring(response.indexOf("'")+1);
        response = response.substring(0,response.indexOf("'"));
        get.releaseConnection();
        url = "http://gplsi.dlsi.ua.es/~stela/TERSEO/terseo.php";
        post = new PostMethod(url);
        post.setParameter("session",response);
        if(baseReference==null) {
            post.setParameter("pDiaP",""+new Date().getDay());
            post.setParameter("pMesP",""+new Date().getMonth());
            post.setParameter("pAnyoP",""+new Date().getYear());
        } else {
            post.setParameter("pDiaP",""+baseReference.getDay());
            post.setParameter("pMesP",""+baseReference.getMonth());
            post.setParameter("pAnyoP",""+baseReference.getYear());
        }
        post.setParameter("pIdioma","2");
        post.setParameter("pEsquema","1");
        post.setParameter("texto",data);
        post.setDoAuthentication( false );
        client.executeMethod( post );
        response = post.getResponseBodyAsString();
        Document document2 = loader.parse(new ByteArrayInputStream((("<aux>" +new String(response.getBytes("UTF-8"))+"</aux>").getBytes())));
        org.w3c.dom.Node elmDest = ((Element)(((Element)(document.getFirstChild())).getElementsByTagName("document").item(0))).getElementsByTagName("referenceList").item(0);
        org.w3c.dom.NodeList elmSource = ((Element)(document2.getFirstChild())).getElementsByTagName("TIMEX2");
        ArrayList<Integer> positionsBegin = new ArrayList<Integer>();
        ArrayList<Integer> positionsEnd = new ArrayList<Integer>();
        String aux = new String(response.getBytes("UTF-8"));
        int last = 0;
        while ( aux.indexOf("<TIMEX2") >= 0 ) {
            int begin = aux.indexOf("<TIMEX2");
            int end = aux.indexOf("</TIMEX2");
            positionsBegin.add(last + begin);
            positionsEnd.add(last + end);
            last = end + 1;
            aux = aux.substring(last);
        }
        for (int i=0; i<elmSource.getLength(); i++) {
            Element element = document.createElement("timeReference");
            Element eText = document.createElement("text");
            eText.appendChild(document.createTextNode(elmSource.item(i).getFirstChild().getNodeValue()));
            element.appendChild(eText);
            Element eValue = document.createElement("value");
            eValue.appendChild(document.createTextNode(elmSource.item(i).getAttributes().getNamedItem("VAL").getNodeValue()));
            element.appendChild(eValue);
            Element eStart = document.createElement("start");
            eStart.appendChild(document.createTextNode(""+positionsBegin.get(i)));
            element.appendChild(eStart);
            Element eEnd = document.createElement("end");
            eEnd.appendChild(document.createTextNode(""+positionsEnd.get(i)));
            element.appendChild(eEnd);
            elmDest.appendChild(element);
        }
        return document;
    }

    public static void main ( String args[] ) throws Exception {
        String data = "John Doe lives in the city of Lisbon, in Portugal. John Doe visited France, during the month of December 2004";
        String title = "Test title";
        Document doc = callServices ( data, title, new Date());
        OutputFormat of = new OutputFormat("XML","UTF-8",true);
        of.setIndent(1);
        of.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(System.out,of);
        serializer.asDOMSerializer();
        serializer.serialize( doc.getDocumentElement() );
    }

}