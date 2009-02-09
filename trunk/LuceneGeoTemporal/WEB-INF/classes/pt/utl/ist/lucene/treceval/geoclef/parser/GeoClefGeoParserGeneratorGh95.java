package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.treceval.GeoClefExample;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.RunCollections;
import pt.utl.ist.lucene.treceval.geoclef.parser.missingdocs.GeoClefMissingDocsGenerator;

import java.io.IOException;
import java.io.File;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGeneratorGh95
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */
    public static int skipFiles = 0;


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
//        GeoClefMissingDocsGenerator.run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "gh95","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95");
        //Run Again and output to missing dir
        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95" + "-missing-collection-docs",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95-missing",new Gh95FieldFilter(),skipFiles);
        //Normalize Missing Dir
//        new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\gh95-missing");
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
            if (GeoParserOutputFileMonitor.counter / GeoParserOutputFileMonitor.numberOfRecordsInFile < skipFiles)
            {
                GeoParserOutputFileMonitor.counter++;
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
                            Document dom = GeoParser.geoParse(strBuilder.toString(),"http://geoparser.digmap.eu/geoparser-dispatch");
                            GeoParserOutputFileMonitor.writeGeoParseElement(docno, dom.getRootElement());
                        }
                        catch (IOException e)
                        {
                            logger.error("DOCNO:" + docno + " " + e.toString());
                        }
                        catch (DocumentException e)
                        {
                            logger.error("DOCNO:" + docno + " " + e.toString());
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