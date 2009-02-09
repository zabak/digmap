package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.lucene.treceval.handlers.*;

import java.io.IOException;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGeneratorGh95
{

    private static final Logger logger = Logger.getLogger(GeoClefGeoParserGeneratorGh95.class);

    /**
     * Number of Files to skip if already done
     */
    public static int skipFiles = 0;
    public static int stopFiles = 2000;

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
        //Import every files from GeoParser
//        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\gh95",new Gh95FieldFilter(), skipFiles);
        //Generate a new Collection file just with missing files
//        new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95");
//        GeoClefMissingDocsGenerator.run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "gh95","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95");
        //Run Again and output to missing dir


//        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95" + "-missing-collection-docs",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95-missing",new Gh95FieldFilter(),skipFiles);


        //Normalize Missing Dir
//      new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95-missing");
    }


    /**
     * gh-95 example
     * <DOC>
     * <DOCNO>GH950103-000000</DOCNO>
     * <DOCID>GH950103-000000</DOCID>
     * <DATE>950103</DATE>
     * <HEADLINE>Chance of being a victim of crime is less than you think</HEADLINE>
     * <EDITION>3</EDITION>
     * <PAGE>3</PAGE>
     * <RECORDNO>980549733</RECORDNO>
     * <TEXT>
     * PEOPLE greatl...
     * </TEXT>
     * </DOC>
     */

    static class Gh95FieldFilter implements FieldFilter
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
            else if(GeoParserOutputFileMonitor.counter / GeoParserOutputFileMonitor.numberOfRecordsInFile > stopFiles)
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
                    Element headlineElem = (Element) docElem.selectSingleNode("HEADLINE");
                    String headline = "";
                    if (headlineElem != null)
                        headline = headlineElem.getText();
                    else
                        logger.warn("DOC " + docno + " with no headline");
                    Element textElem = (Element) docElem.selectSingleNode("TEXT");
                    String text = "";
                    if (textElem != null)
                        text = textElem.getText();
                    else
                        logger.warn("DOC " + docno + " with no text");
                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(headline).append(" ").append(text);
                    int attempts = 3;
                    while(attempts > 0)
                    {
                        if(attempts < 3)
                        {
                            logger.warn("WILL TRY AGAIN in 5 seconds. Record:" + docno);
                            try
                            {
                                Thread.sleep(5000);
                            }
                            catch (InterruptedException e)
                            {
                                logger.error(e,e);
                            }
                        }
                        try
                        {
                            Document dom = GeoParser.geoParse(strBuilder.toString(),server + "/geoparser-dispatch");
                            GeoParserOutputFileMonitor.writeGeoParseElement(docno, dom.getRootElement());
                            break;
                        }
                        catch (IOException e)
                        {
                            logger.error("DOCNO:" + docno + " " + e.toString(),e);
                        }
                        catch (DocumentException e)
                        {
                            logger.error("DOCNO:" + docno + " " + e.toString(),e);
                            attempts = 0;
                        }
                        attempts--;
                    }
                }
            }
            Map<String, String> fields = new HashMap<String, String>();
            return new FilteredFields(fields);
        }


    }

}
