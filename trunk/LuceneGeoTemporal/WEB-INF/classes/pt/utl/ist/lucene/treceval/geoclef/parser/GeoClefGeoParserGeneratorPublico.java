package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.*;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.treceval.GeoClefExample;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.RunCollections;
import pt.utl.ist.lucene.treceval.geoclef.parser.missingdocs.GeoClefMissingDocsGenerator;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGeneratorPublico
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */
    public static int skipFiles = 444;


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
        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\publico-pt",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\publico",new PublicoFieldFilter(), skipFiles);
        //Generate a new Collection file just with missing files
        GeoClefMissingDocsGenerator.run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "publico","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\publico-pt");
        //Run Again and output to missing dir
        GeoClefGeoParserGenerator.run(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\publico" + "-missing-collection-docs",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\publico-missing",new PublicoFieldFilter(),skipFiles);
        //Normalize Missing Dir
        new GeoParseFileNameNormalizer().normalize(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir  + "\\publico-missing");
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

    static class PublicoFieldFilter implements FieldFilter
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
                    Element textElem = (Element) docElem.selectSingleNode("TEXT");
                    String text = "";
                    if (textElem != null)
                        text = textElem.getText();
                    else
                        logger.warn("DOC " + docno + " with no text");
                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(" ").append(text);

                    try
                    {
                        Document dom = GeoParser.geoParse(strBuilder.toString(),"http://geoparser.digmap.eu/geoparser-dispatch");
                        GeoParserOutputFileMonitor.writeGeoParseElement(docno, dom.getRootElement());
                    }
                    catch (IOException e)
                    {
                        logger.error("DOC with DONO:" + docno + " - " + e.toString(), e);
                    }
                    catch (DocumentException e)
                    {
                        logger.error("DOC with DONO:" + docno + " - " + e.toString(), e);
                    }
                }
            }
            Map<String, String> fields = new HashMap<String, String>();
            return new FilteredFields(fields);
        }


    }

}
