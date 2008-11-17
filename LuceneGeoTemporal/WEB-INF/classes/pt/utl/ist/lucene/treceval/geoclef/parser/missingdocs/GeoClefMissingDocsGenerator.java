package pt.utl.ist.lucene.treceval.geoclef.parser.missingdocs;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import pt.utl.ist.lucene.forms.RectangleForm;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.GeoClefExample;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.RunCollections;
import pt.utl.ist.lucene.treceval.geoclef.parser.GeoParseFileNameNormalizer;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResult;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResultsIterator;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefMissingDocsGenerator
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */


    static Writer osw = null;
    static GeoParserResultsIterator geoParserResultsIterator = null;
    static int missing = 0;
    static int total = 0;
    static int geoRecords = 0;
    static int boxs = 0;
    static int points = 0;
    static int geoNames = 0;
    static int recordsWithNames = 0;
    static String docNoSelector;

    public static void main(String[]args) throws DocumentException, IOException
    {
        run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "gh95","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95");
//        run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "latEn","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95");
//        run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "folha","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathPt + "\\folha-pt");
//        run("//DOC","DOCNO",pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + "publico","ISO-8859-1",pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathPt + "\\publico-pt");
    }

    public static void run(String docSelector,String DOCNOSelector, String geoParsedPath, String defaultSGMLEncoding, String collectionPath) throws DocumentException, IOException
    {
        osw = null;
        geoParserResultsIterator = null;
        missing = 0;
        total = 0;
        geoRecords = 0;
        boxs = 0;
        points = 0;
        geoNames = 0;
        recordsWithNames = 0;
        docNoSelector = DOCNOSelector;



//        args = new String[2];
//        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
//        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";
//
//
//        Globals.INDEX_DIR = args[0];
//        Globals.DATA_DIR = args[1];






        Globals.COLLECTION_FILES_DEFAULT_ENCODING = defaultSGMLEncoding;
        new GeoParseFileNameNormalizer().normalize(geoParsedPath);
        //Global Search Index
        XmlFieldHandler gh95XmlHandler = new SimpleXmlFieldHandler(".", new GeoClefMissingDocsGenerator.MissingWriterFieldFilter());
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(gh95XmlHandler);
        ResourceHandler resourceHandler = new XmlResourceHandler(docSelector, DOCNOSelector, xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler, null);

        //Lets create our configuration
        Configuration conf = new Configuration(collectionPath, collectionsDirectory, null);
        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(conf);

        geoParserResultsIterator = new GeoParserResultsIterator(geoParsedPath,false);
        new File(geoParsedPath + "-missing-collection-docs").mkdirs();

        FileOutputStream fos = new FileOutputStream(geoParsedPath + "-missing-collection-docs" + File.separator + new File(collectionPath).getName() + "-missing.xml");
        osw = new OutputStreamWriter(fos,"UTF-8");
        osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        osw.write("<docs>");
        Globals.DATA_DIR = collectionPath;
        RunCollections.runConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
        osw.write("</docs>");
        osw.close();
        FileWriter fw = new FileWriter(geoParsedPath + "-report.txt");
        fw.write("Missing:" + missing + "\n");
        fw.write("Total:" + total + "\n");
        fw.write("GeoRecords:" + geoRecords + "\n");
        fw.write("Boxs:" + boxs + "\n");
        fw.write("Points:" + points + "\n");
        fw.write("Records with Names:" + recordsWithNames + "\n");
        fw.write("Total of GeoNames:" + geoNames + "\n");
        fw.close();
    }

    static class MissingWriterFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {
            total++;
            XPath xPath = element.createXPath(docNoSelector);
            Node docnoElem = xPath.selectSingleNode(element);
            String docno = docnoElem.getText();
            GeoParserResult result = geoParserResultsIterator.next(docno);
            if(result == null)
            {
                logger.info("missing:(" + missing + ") of (" + total + ")" + docno);
                missing++;
                try
                {
                    Dom4jUtil.writeDontCloseStream((Element) element,osw);
                    osw.write('\n');
                    if(total%100 == 0)
                        osw.flush();
                }
                catch (IOException e)
                {
                    logger.fatal(e,e);
                    System.exit(-1);
                }
            }
            else if(result.getGenericUnknownForm() != null)
            {
                geoRecords++;
                if(result.getGenericUnknownForm() instanceof RectangleForm)
                    boxs++;
                else
                    points++;
                if(result.getPlaces() != null)
                {
                    geoNames += result.getPlaces().size();
                    recordsWithNames++;
                }
            }

            Map<String, String> fields = new HashMap<String, String>();
            return new FilteredFields(fields);
        }


    }

}
