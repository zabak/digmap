package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pt.utl.ist.lucene.treceval.handlers.*;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGeneratorLatEn
{

    private static final Logger logger = Logger.getLogger(GeoClefGeoParserGeneratorLatEn.class);

    /**
     * Number of Files to skip if already done
     */
    public static int skipFiles = 0;
    public static int stopFile = 2000;
    public static String server = "http://localhost:8080/geoparser";


   /**
     * Import every files from GeoParser
     * Generate a new Collection file just with missing files
     * Run Again and output to missing dir
     * Normalize Missing Dir
     *
     * Index will use GeoParserIterator that opens an internal iterator to missing files
     *
     */
    public static void main(String[] args) throws DocumentException, IOException
    {
        if(args.length == 2)
        {
            skipFiles = Integer.parseInt(args[0]);
            stopFile = Integer.parseInt(args[1]);
        }
        //new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\latEn");
        //Import every files from GeoParser
//        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\lat-en",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\latEn",new LatEnFieldFilter(), skipFiles);
        //Generate a new Collection file just with missing files
//        GeoClefMissingDocsGenerator.run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "latEn","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\lat-en");
//        //Run Again and output to missing dir
        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\latEn" + "-missing-collection-docs",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\latEn-missing",new LatEnFieldFilter(),skipFiles);
//        //Normalize Missing Dir
//        new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\latEn-missing");
    }


    /**
     *  latin-en example
         <DOC>
         <DOCNO> LA012394-0086 </DOCNO>
         <DOCID> 006365 </DOCID>
         <SOURCE><P>Los Angeles Times</P></SOURCE>
         <DATE><P>January 23, 1994, Sunday, Ventura West Edition</P></DATE>
         <SECTION><P>Metro; Part B; Page 1; Column 2</P></SECTION>
         <LENGTH><P>4337 words</P></LENGTH>
         <HEADLINE><P>
         EARTHQUAKE: THE LONG ROAD BACK; DIARY OF A DISASTER; RESIDENTS STRUGGLING TO
         PUT LIVES BACK IN ORDER ONE DAY AT A TIME</P>
         </HEADLINE>
         <BYLINE><P>By STEPHANIE SIMON, TIMES STAFF WRITER</P></BYLINE>
         <TEXT>
         <P>DAY 1</P>
         <P>Darkness. Then, abruptly, a jolt. A crash. Rumbling, screaming, shattering,tumbling. Panic. And again, darkness.</P>
         ...
         </TEXT>
         </DOC>
     */

    static class LatEnFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {

            System.out.print(".");
            if(GeoParserOutputFileMonitor.counter % GeoParserOutputFileMonitor.numberOfRecordsInFile == 0)
            {
                logger.info("\nCrossing: geoParse" + (GeoParserOutputFileMonitor.counter  / GeoParserOutputFileMonitor.numberOfRecordsInFile) + ".xml");
            }
            if (GeoParserOutputFileMonitor.counter / GeoParserOutputFileMonitor.numberOfRecordsInFile < skipFiles)
            {
                GeoParserOutputFileMonitor.counter++;
            }
            else if(GeoParserOutputFileMonitor.counter / GeoParserOutputFileMonitor.numberOfRecordsInFile > stopFile)
            {
                logger.info("\nFinishing job");
                System.exit(0);
            }
            else
            {
                Element docElem = (Element) element;


                Element docnoElem = (Element) docElem.selectSingleNode("DOCNO");
                if (docnoElem == null)
                {
                    logger.error("Record with no DOCNO");
                    logger.warn("trying docid");
                    docnoElem = (Element) docElem.selectSingleNode("DOCID");
                }
                if (docnoElem == null)
                    logger.error("Record with no DOCID");
                else
                {

                    String docno = docnoElem.getText();
                    logger.debug("DOCNO:" + docno);
                    XPath xPathHeadline = docElem.createXPath("./HEADLINE//text()");
                    XPath xPathText = docElem.createXPath("./TEXT//text()");
                    List<Node> headlineElems = xPathHeadline.selectNodes(docElem);
                    String headline = "";
                    if (headlineElems != null)
                    {
                        StringBuilder headlinesBuilder = new StringBuilder();
                        for(Node n: headlineElems)
                        {
                            headlinesBuilder.append(" ").append(n.getText());
                        }
                        headline = headlinesBuilder.toString();
                    }
                    else
                        logger.warn("DOC " + docno + " with no headline");
                    List<Node> textElems = xPathText.selectNodes(docElem);
                    String text = "";
                    if (textElems != null)
                    {
                        StringBuilder textBuilder = new StringBuilder();
                        for(Node n: textElems)
                        {
                            textBuilder.append(" ").append(n.getText());
                        }
                        text = textBuilder.toString();
                    }
                    else
                        logger.warn("DOC " + docno + " with no text");
                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(headline).append(" ").append(text);

                    try
                    {
                        Document dom = GeoParser.geoParse(strBuilder.toString(),server + "/geoparser-dispatch");
                        GeoParserOutputFileMonitor.writeGeoParseElement(docno, dom.getRootElement());
                    }
                    catch (IOException e)
                    {
                       logger.error("DOCNO:" + docno + " " + e.toString(),e);
                    }
                    catch (DocumentException e)
                    {
                        logger.error("DOCNO:" + docno + " " + e.toString(),e);
                    }
                }
            }
            Map<String, String> fields = new HashMap<String, String>();
            return new FilteredFields(fields);
        }


    }

}
