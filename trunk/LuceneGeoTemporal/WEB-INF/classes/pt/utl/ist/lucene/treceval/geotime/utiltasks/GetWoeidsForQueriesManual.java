package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import nmaf.util.DomUtil;
import org.w3c.dom.Document;
import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 9/Jul/2011
 * Time: 20:31:04
 * To change this template use File | Settings | File Templates.
 */
public class GetWoeidsForQueriesManual {
    public static void main ( String args[] ) throws Exception {
        String data = "King of country Jordan, king Hussein";
        String title = "King of Jordan, king Hussein";
        Document doc = CallWebServices.callServices( data, title, 2009,11,11,"test","test");
        PlaceMakerDocument pm = new PlaceMakerDocument("<doc id=\"teste\">\n" + DomUtil.domToString(doc,false) + "\n</doc>");
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
}
