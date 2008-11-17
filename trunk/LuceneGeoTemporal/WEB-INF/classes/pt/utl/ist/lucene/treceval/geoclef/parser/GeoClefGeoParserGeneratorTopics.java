package pt.utl.ist.lucene.treceval.geoclef.parser;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import pt.utl.ist.lucene.treceval.GeoClefExample;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.RunCollections;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefGeoParserGeneratorTopics
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */
    public static int skipFiles = 0;



    public static void run(String topicsPath,String topicsOutFolder) throws DocumentException, IOException
    {


        String folha = topicsPath;

        //Global Search Index
        XmlFieldHandler topicHandler = new SimpleXmlFieldHandler(".", new GeoClefGeoParserGeneratorTopics.Topic());
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(topicHandler);
        ResourceHandler resourceHandler = new XmlResourceHandler("//topic", "identifier", xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler, null);

        //Lets create our configuration
        Configuration GH95Conf = new Configuration(folha, collectionsDirectory, null);
        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(GH95Conf);

        GeoParserOutputFileMonitor.initMonitor(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + File.separator + topicsOutFolder, skipFiles);
        Globals.DATA_DIR = folha;
        RunCollections.runConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
        GeoParserOutputFileMonitor.flushClose();

    }

    /**
     * <topic lang="pt">
        <identifier>10.2452/100-GC</identifier>
        <title>Desastres naturais no Oeste dos Estados Unidos</title>
        <description>Os documentos relevantes devem mencionar terramotos e enchentes nos estados da região oeste dos EUA</description>
        <narrative>Os documentos devem mencionar terramotos e enchentes nos estados do Oeste dos EUA. Estes estados são: a Califórnia, o Oregon e o estado de Washington.</narrative>
        </topic>
     */
    static class Topic implements FieldFilter
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


                Element docnoElem = (Element) docElem.selectSingleNode("identifier");
                if (docnoElem == null)
                {
                    logger.error("Record with no identifier");
                }
                else
                {
                    String identifier = docnoElem.getText();
                    Element titleElem = (Element) docElem.selectSingleNode("title");
                    String title = "";
                    if (titleElem != null)
                        title = titleElem.getText();
                    else
                        logger.warn("Topic " + identifier + " with no title");
                    Element descriptionElem = (Element) docElem.selectSingleNode("description");
                    String description = "";
                    if (descriptionElem != null)
                        description = descriptionElem.getText();
                    else
                        logger.warn("Topic " + identifier + " with no description");

                    Element NarrativeElem = (Element) docElem.selectSingleNode("narrative");
                    String narrative = "";
                    if (NarrativeElem != null)
                        narrative = NarrativeElem.getText();
                    else
                        logger.warn("Topic " + identifier + " with no narrative");

                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(title).append(" ").append(description).append(" ").append(narrative);

                    try
                    {
                        Document dom = GeoParser.geoParse(strBuilder.toString(),"http://geoparser.digmap.eu/geoparser-dispatch");
                        GeoParserOutputFileMonitor.writeGeoParseElement(identifier, dom.getRootElement());
                    }
                    catch (IOException e)
                    {
                        logger.error("Topic with identifier:" + identifier + " - " + e.toString(), e);
                    }
                    catch (DocumentException e)
                    {
                        logger.error("Topic with identifier:" + identifier + " - " + e.toString(), e);
                    }
                }
            }
            Map<String, String> fields = new HashMap<String, String>();
            return new FilteredFields(fields);
        }
    }

    public static void main(String[] args) throws DocumentException, IOException
    {
        run("C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\geoclef08pt\\topics","topicsPt");
        run("C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\geoclef08en\\topics","topicsEn");
    }

}
