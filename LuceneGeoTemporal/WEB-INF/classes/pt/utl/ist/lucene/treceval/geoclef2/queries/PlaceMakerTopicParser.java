package pt.utl.ist.lucene.treceval.geoclef2.queries;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.w3c.dom.*;
import pt.utl.ist.lucene.treceval.geoclef2.index.Config;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;


/**
 * @author Jorge
 * @date 10/Fev/2010
 * @time 17:30:23
 * @mail machadofisher@gmail.com
 */


///<topics xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="topics.xsd">
//	<topic id="GeoTime-0001">
//		<original>
//			<desc>When and where did Astrid Lindgren die</desc>
//			<narr>The user wants to know when and in what city the children's author Astrid Lindgren died</narr>
//		</original>
//		<originalClean>
//			<desc>Astrid Lindgren die</desc>
//			<narr>city children author Astrid Lindgren died</narr>
//		</originalClean>
//		<filterChain>
//			<boolean type="AND">
//				<term>
//					<field>placeType</field>
//					<value>city</value>
//				</term>
//               <term>
//					<field>place</field>
//					<value woeid="23424781">China</value>
//				</term>
//			</boolean>
//		</filterChain>
//		<terms>
//			<desc>Astrid Lindgren die</desc>
//			<narr>children author Astrid Lindgren died</narr>
//		</terms>
//		<places>
//			<term woeid="?">?</term>
//		</places>
//		<times>
//			<term>?</term>
//		</times>
//	</topic>
public class PlaceMakerTopicParser
{
    static String topicsFile06 =  Config.geoclefBase +  File.separator + "topics" + File.separator + "topics06.xml";
    static String topicsFileFormatted06 =  Config.geoclefBase +  File.separator + "topics" + File.separator + "topics06Formatted.xml";

    static String topicsFile08 =  Config.geoclefBase +  File.separator + "topics" + File.separator + "topics08.xml";
    static String topicsFileFormatted08 =  Config.geoclefBase +  File.separator + "topics" + File.separator + "topics08Formatted.xml";

    public static void main(String[] args) throws Exception, DocumentException
    {
        parse(topicsFile08,topicsFileFormatted08);
//        parse(topicsFile06,topicsFileFormatted06);
    }

    public static void parse(String topicsFile, String topicsFileFormatted) throws Exception, DocumentException
    {
        FileWriter topicsFileFormattedWriter = new FileWriter(topicsFileFormatted,false);

        topicsFileFormattedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        topicsFileFormattedWriter.write("<topics xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"topics.xsd\">");

        Document dom = Dom4jUtil.parse(new File(topicsFile));
        XPath topicXpath = dom.createXPath("//topic");
        List<Element> topics = topicXpath.selectNodes(dom);
        for (Element topic : topics)
        {
            XPath idXpath = dom.createXPath("./identifier");
            XPath titleXpath = dom.createXPath("./title");
            XPath descXpath = dom.createXPath("./description");
            XPath narrXpath = dom.createXPath("./narrative");

            Element idElem = (Element) idXpath.selectSingleNode(topic);
            Element titleElem = (Element) titleXpath.selectSingleNode(topic);
            Element descElem = (Element) descXpath.selectSingleNode(topic);
            Element narrElem = (Element) narrXpath.selectSingleNode(topic);

            String id = idElem.getTextTrim();
            String title = titleElem.getTextTrim();
            String desc = descElem.getTextTrim();
            String narr = narrElem.getTextTrim();

            System.out.println("id = " + id);
            System.out.println("title = " + title);
            System.out.println("desc = " + desc);
            System.out.println("narr = " + narr);

            Topic t = new Topic(id,title,desc,narr);

            PlaceMakerDocument document = getPlaceMaker(t);

            for(PlaceMakerDocument.PlaceDetails details : document.getPlaceDetails())
            {
                System.out.println(details.getWoeId() + ":" + details.getName() + " : " + details.getRefs().size());
            }
            topicsFileFormattedWriter.write("<topic id=\"" + t.getId() + "\">\n");
            topicsFileFormattedWriter.write("  <original>\n");
            topicsFileFormattedWriter.write("     <desc>" + t.getTitle() + "</desc>\n");
            topicsFileFormattedWriter.write("     <narr>" + t.getDesc() + "</narr>\n");
            topicsFileFormattedWriter.write("  </original>\n");
            topicsFileFormattedWriter.write("  <originalClean>\n");
            topicsFileFormattedWriter.write("     <desc>" + t.getTitle() + "</desc>\n");
            topicsFileFormattedWriter.write("     <narr>" + t.getDesc() + "</narr>\n");
            topicsFileFormattedWriter.write("  </originalClean>\n");
            topicsFileFormattedWriter.write("  <filterChain>\n");
            topicsFileFormattedWriter.write("      <boolean type=\"OR\">\n");
            for(PlaceMakerDocument.PlaceDetails details : document.getPlaceDetails())
            {
                topicsFileFormattedWriter.write("         <term><field>place</field><value woeid=\"" + details.getWoeId() + "\">" + details.getShortName() + "</value></term>\n");
            }
            topicsFileFormattedWriter.write("      </boolean>\n");
            topicsFileFormattedWriter.write("  </filterChain>\n");
            topicsFileFormattedWriter.write("  <terms>\n");
            topicsFileFormattedWriter.write("     <desc>" + t.getTitle() + "</desc>\n");
            topicsFileFormattedWriter.write("     <narr>" + t.getDesc() + "</narr>\n");
            topicsFileFormattedWriter.write("  </terms>\n");
            topicsFileFormattedWriter.write("</topic>\n");
            Thread.sleep(3000);
        }

        topicsFileFormattedWriter.write("</topics>");
        topicsFileFormattedWriter.flush();
        topicsFileFormattedWriter.close();
    }

    private static PlaceMakerDocument getPlaceMaker(Topic t) throws Exception
    {
        org.w3c.dom.Document dom = CallWebServices.callServices(t.getTitle() + " " + t.getDesc() + " " + t.getNarr(),t.getTitle(),0,0,0,null,t.getId());

        StringWriter writer = new StringWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<doc docno=\"xpto\">\n");
        OutputFormat of = new OutputFormat("XML","UTF-8",true);
        of.setIndent(1);
        of.setOmitXMLDeclaration(true);
        of.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(writer,of);
        serializer.asDOMSerializer();
        serializer.serialize( dom.getDocumentElement() );
        writer.write("</doc>\n");
        writer.close();
        return new PlaceMakerDocument(writer.toString(),"//doc/@docno");
    }

    public static class Topic
    {
        String id;
        String title;
        String desc;
        String narr;

        Topic(String id, String title, String desc, String narr) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.narr = narr;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getNarr() {
            return narr;
        }
    }
}
