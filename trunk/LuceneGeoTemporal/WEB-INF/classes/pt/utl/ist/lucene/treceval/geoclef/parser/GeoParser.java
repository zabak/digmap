package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;

import pt.utl.ist.lucene.treceval.util.EscapeChars;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.Strings;

/**
 * @author Jorge Machado
 * @date 10/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef
 */
public class GeoParser
{
    // <wfs:Query wfs:TypeName="People" /><wfs:Query wfs:TypeName="Organizations" />
    // <Resource mime="text/plain">Documents mentioning sport events occurring in (or passing through) the Sahara. Relevant documents must make reference to athletic events and to the place where they take place. The Sahara covers huge parts of Algeria, Chad, Egypt, Libya, Mali, Mauritania, Morocco, Niger, Western Sahara, Sudan, Senegal and Tunisia.</Resource>
    static String requestStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GetFeature xmlns=\"http://www.opengis.net/gp\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/gp ../gp/GetFeatureRequest.xsd http://www.opengis.net/wfs ../wfs/GetFeatureRequest.xsd\" wfs:outputFormat=\"GML2\"><wfs:Query wfs:TypeName=\"PlaceName\" /><wfs:Query wfs:TypeName=\"DateTime\" /><Resource mime=\"text/plain\">";
    static String requestEnd = "</Resource></GetFeature>";

    public static Document geoParse(String text,String server) throws IOException, MalformedURLException, DocumentException
    {
        // Construct data
        String data = URLEncoder.encode("showmap", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");
        data += "&" + URLEncoder.encode("request", "UTF-8") + "=" + URLEncoder.encode(requestStart + EscapeChars.forXML(text) + requestEnd, "UTF-8");
//        data += "&" + URLEncoder.encode("request2", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");
        // Send data

        URL url = new URL(server);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
//
//        StringWriter sw = new StringWriter();
//        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//        BufferedReader buffered = new BufferedReader(new InputStreamReader(bis));
//        String lineM
//        while(buffered.readLine()


        Document dom = Dom4jUtil.parse(new InputSource(conn.getInputStream()));
        wr.close();
        return dom;
    }
}
