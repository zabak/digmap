package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.dom4j.*;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.utils.Strings;

import java.io.IOException;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class DigmapExample
{

    private static final Logger logger = Logger.getLogger(DigmapExample.class);

    public static void main(String[] args) throws DocumentException, IOException
    {

//        args = new String[4];
//        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\documents";
//        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\topics";
//        args[2] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\output";
//        args[3] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";


        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

        String dataDir = Globals.DATA_DIR;
        if (args.length > 3)
            dataDir = args[3];

        logger.info("Forcing data dir to: " + dataDir);

        Globals.DATA_DIR = dataDir;


        XmlFieldHandler xmlFieldHandlerCollection = new SimpleXmlFieldHandler("../@collection", new SimpleNotTokenizedFieldFilter(), "collection");
        XmlFieldHandler xmlFieldHandlerTitlePresent = new SimpleXmlFieldHandler("./dc:title", new SimpleFieldFilter(), Globals.DOCUMENT_TITLE);
        XmlFieldHandler xmlFieldHandlerTitle = new SimpleXmlFieldHandler("./dc:title", new MultipleFieldFilter(3), "contents");
        XmlFieldHandler xmlFieldHandlerSubject = new SimpleXmlFieldHandler("./dc:subject", new MultipleFieldFilter(2), "contents");
        XmlFieldHandler xmlFieldHandlerDescription = new SimpleXmlFieldHandler("./dc:description", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerCreator = new SimpleXmlFieldHandler("./dc:creator", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerCreatorPerson = new SimpleXmlFieldHandler("./digmap:creator-person/dc:creator", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerContributor = new SimpleXmlFieldHandler("./dc:contributor", new SimpleFieldFilter(), "contents");
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        XmlFieldHandler xmlFieldHandlerSpatial = new SimpleXmlFieldHandler("./dcterms:spatial", new DigmapSpatialFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerTime = new SimpleXmlFieldHandler("./dc:date", new DigmapTimeFieldFilter(), null);

        xmlFieldHandlers.add(xmlFieldHandlerCollection);
        xmlFieldHandlers.add(xmlFieldHandlerTitlePresent);
        xmlFieldHandlers.add(xmlFieldHandlerTitle);
        xmlFieldHandlers.add(xmlFieldHandlerSubject);
        xmlFieldHandlers.add(xmlFieldHandlerDescription);
        xmlFieldHandlers.add(xmlFieldHandlerCreator);
        xmlFieldHandlers.add(xmlFieldHandlerCreatorPerson);
        xmlFieldHandlers.add(xmlFieldHandlerContributor);
        xmlFieldHandlers.add(xmlFieldHandlerSpatial);
        xmlFieldHandlers.add(xmlFieldHandlerTime);

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("digmap", "http://www.digmap.eu/schemas/resource/");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");


        ResourceHandler resourceHandler = new XmlResourceHandler("//record/digmap:record", "@urn", "dc:identifier", xmlFieldHandlers, namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler, null);

//        /**
//         * Now let's create a Topics processor
//         * The principle is the same, we need a directoryHandler
//         * and we need to give it a ResourceHandler
//         */
//        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(2),"contents");
//        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./desc",new SimpleFieldFilter(),"contents");
//        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
//        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
//        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
//        ResourceHandler topicResourceHandler = new XmlResourceHandler("//top","./num",xmlTopicFieldHandlers);
//        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
//        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 500;

        Configuration d1 = new Configuration("version1", "digmap", "vs", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(), collectionPath, collectionsDirectory, topicsPath, null, "contents", IndexCollections.en.getWordList(), outputDir, maxResults);
        Configuration d2 = new Configuration("version1", "digmap", "lm", Model.LanguageModel, IndexCollections.en.getAnalyzerNoStemming(), collectionPath, collectionsDirectory, topicsPath, null, "contents", IndexCollections.en.getWordList(), outputDir, maxResults);
        Configuration d3 = new Configuration("version1", "digmap", "lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(), collectionPath, collectionsDirectory, topicsPath, null, "contents", IndexCollections.en.getWordList(), outputDir, maxResults);
        Configuration d4 = new Configuration("version1", "digmap", "vsstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(), collectionPath, collectionsDirectory, topicsPath, null, "contents", IndexCollections.en.getWordList(), outputDir, maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(d1);
        configurations.add(d2);
        configurations.add(d3);
        configurations.add(d4);
        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
//        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
//        queryConfiguration1.setForceQE(QEEnum.no);
//
//        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
//        queryConfiguration2.setForceQE(QEEnum.yes);
//        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();
//
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_BC));
//
//        //Search Topics Runs to submission
//        SearchTopics.search(searchConfigurations);
    }

    /**
     * SPatial field filter to parse fields of these two types:
     * <p/>
     * <dcterms:spatial xsi:type="ns3:Point">east=3.3833333333333333; north=50.6;</dcterms:spatial>
     * or
     * <dcterms:spatial xsi:type="ns3:Box">northlimit=51.7; eastlimit=3.0166666666666666; southlimit=51.266666666666666; westlimit=4.316666666666666;</dcterms:spatial>
     * <p/>
     * creates a collection of fields with LgteDocumentWrapper static helper methods to get GeoBoxs and GeoPoints
     */
    static class DigmapSpatialFieldFilter implements FieldFilter
    {
        public static final String dcterms_att_box = "Box";
        public static final String dcterms_att_point = "Point";
        public static final String dcterms_att_box_namespace = "http://purl.org/dc/terms/";
        public static final String dcterms_att_point_namespace = "http://purl.org/dc/terms/";
        public static final String dcterms_att_w3cdtf_namedpace = "http://purl.org/dc/terms/";
        public static final String dcterms_spatial_limits_separator = ";";
        public static final String dcterms_spatial_limit_value_separator = "=";
        public static final String xsiTypeNamespace = "http://www.w3.org/2001/XMLSchema-instance";
        public static final String xsiPrefix = "xsi";
        public static final String xsiType = "type";
        QName qNameXsiType = new QName(xsiType, new Namespace(xsiPrefix, xsiTypeNamespace));

        public FilteredFields filter(Node node, String fieldName)
        {
            Collection<Field> uniqueFields = new ArrayList<Field>();
            Element spatial = (Element) node;
            String text = spatial.getText();
            Attribute xsiTypeAttr = spatial.attribute(qNameXsiType);

            //get dcterms Point field
            if(xsiTypeAttr != null)
            {
                QName attValue = spatial.getQName(xsiTypeAttr.getValue());
                if (xsiTypeAttr != null && attValue.getName().equals(dcterms_att_point) && attValue.getNamespaceURI().equals(dcterms_att_point_namespace))
                {
                    Double lat = null;
                    Double lng = null;
                    List<String> coordinates = Strings.getListStrings(text, dcterms_spatial_limits_separator);
                    for (String coord : coordinates)
                    {
                        List<String> fields = Strings.getListStrings(coord, dcterms_spatial_limit_value_separator);
                        if (fields.size() == 2)
                        {
                            String name = fields.get(0).trim();
                            String value = fields.get(1).trim();
                            if (name.equals("north"))
                                lat = Double.parseDouble(value.trim());
                            else if (name.equals("east"))
                                lng = Double.parseDouble(value.trim());
                            else
                                logger.error("Error in Field spatial " + dcterms_att_point + ":" + text);
                        }
                    }
                    if (lat == null || lng == null)
                        logger.error("Error in Field spatial " + dcterms_att_point + ":" + text);
                    else
                    {
                        List<Field> pointFields = LgteDocumentWrapper.getGeoPointFields(lat, lng);
                        uniqueFields.addAll(pointFields);
                    }
                }
                //get dcterms Box field
                else
                if (xsiTypeAttr != null && attValue.getName().equals(dcterms_att_box) && attValue.getNamespaceURI().equals(dcterms_att_box_namespace))
                {
                    Double northlimit = null;
                    Double southlimit = null;
                    Double westlimit = null;
                    Double eastlimit = null;
                    List<String> coordinates = Strings.getListStrings(text, dcterms_spatial_limits_separator);
                    for (String coord : coordinates)
                    {
                        List<String> fields = Strings.getListStrings(coord, dcterms_spatial_limit_value_separator);
                        if (fields.size() == 2)
                        {
                            String name = fields.get(0).trim();
                            String value = fields.get(1).trim();
                            if (name.equals("northlimit"))
                                northlimit = Double.parseDouble(value.trim());
                            else if (name.equals("southlimit"))
                                southlimit = Double.parseDouble(value.trim());
                            else if (name.equals("westlimit"))
                                westlimit = Double.parseDouble(value.trim());
                            else if (name.equals("eastlimit"))
                                eastlimit = Double.parseDouble(value.trim());
                            else
                                logger.error("Error in Field spatial " + dcterms_att_box + ":" + text);
                        }
                    }
                    if (northlimit == null || southlimit == null || eastlimit == null || westlimit == null)
                        logger.error("Error in Field spatial " + dcterms_att_box + ":" + text);
                    else
                    {
                        List<Field> pointFields = LgteDocumentWrapper.getGeoBoxFields(northlimit, southlimit, westlimit, eastlimit);
                        uniqueFields.addAll(pointFields);
                    }
                }
                return new FilteredFields(uniqueFields);
            }
            else
            {
                Map<String,String> map = new HashMap<String,String>();
                map.put(fieldName,text);
                return new FilteredFields(map);
            }
        }
    }



    static class DigmapTimeFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node node, String fieldName)
        {
            Element time = (Element) node;
            String text = time.getText();
            Strings.YearPattern yearPattern = Strings.findYears(text);
            if(yearPattern != null)
            {
                List<Field> timeFields = null;
                if(yearPattern.getPoint() > 0)
                {
                    timeFields = LgteDocumentWrapper.getTimeFields(yearPattern.getPoint());
                }
                else if(yearPattern.getStart() > 0 && yearPattern.getEnd() > 0)
                {
                    timeFields = LgteDocumentWrapper.getTimeBoxFields(yearPattern.getStart(),yearPattern.getEnd());
                }
                if(timeFields != null)
                    return new FilteredFields(timeFields);
            }
            return null;
        }
    }
}
