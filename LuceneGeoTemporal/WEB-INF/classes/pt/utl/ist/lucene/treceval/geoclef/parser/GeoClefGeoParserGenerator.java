package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.GeoClefExample;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.RunCollections;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGenerator
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */



    public static void run(String collectionPath, String outDir,FieldFilter fieldFilter, int skipFiles) throws DocumentException, IOException
    {


        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);

        //Global Search Index
        XmlFieldHandler gh95XmlHandler = new SimpleXmlFieldHandler(".", fieldFilter);
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(gh95XmlHandler);
        ResourceHandler resourceHandler = new XmlResourceHandler("//DOC", "DOCNO", xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler, null);

        //Lets create our configuration
        Configuration GH95Conf = new Configuration(collectionPath, collectionsDirectory, null);
        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(GH95Conf);

        GeoParserOutputFileMonitor.initMonitor(outDir, skipFiles);
        Globals.DATA_DIR = collectionPath;
        Globals.COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";
        RunCollections.runConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
        GeoParserOutputFileMonitor.flushClose();

    }

}
