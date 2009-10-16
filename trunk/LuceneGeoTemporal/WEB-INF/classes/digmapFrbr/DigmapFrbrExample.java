package digmapFrbr;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.dom4j.*;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.Configuration;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.config.LocalProperties;
import pt.utl.ist.lucene.utils.Strings;

import java.io.IOException;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class DigmapFrbrExample {

    private static final Logger logger = Logger.getLogger(DigmapFrbrExample.class);

    public static void main(String[] args) throws DocumentException, IOException
    {

        Properties props = new LocalProperties("digmapFrbr/conf.properties");
        Globals.DATA_DIR = props.getProperty("data.dir");
        Globals.INDEX_DIR = props.getProperty("indexes.dir");
//        Globals.DATA_DIR = "F:\\coleccoesIR\\digmapFrbr\\records";
//
//        Globals.INDEX_DIR = "D:\\Servidores\\DATA\\digmapFrbr\\INDEXES";
//        Globals.DATA_DIR = "F:\\coleccoesIR\\digmapFrbr\\records\\kbr\\nobel\\dc";

        String collectionPath = Globals.DATA_DIR;
        String topicsPath = Globals.DATA_DIR + "\\topics";
        String outputDir = Globals.DATA_DIR +  "\\output";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);

        XmlFieldHandler xmlFieldHandlerOriginalRelativePath = new SimpleXmlFieldHandler("../@relativeFilePath", new SimpleNotTokenizedFieldFilter(), "originalRelativePath");
        XmlFieldHandler xmlFieldHandlerOriginalFilename = new SimpleXmlFieldHandler("../@filename", new SimpleNotTokenizedFieldFilter(), "originalFileName");
        XmlFieldHandler xmlFieldHandlerCollection = new SimpleXmlFieldHandler("../../@collection", new SimpleNotTokenizedFieldFilter(), "collection");
        XmlFieldHandler xmlFieldHandlerTitlePresent = new SimpleXmlFieldHandler("./dc:title", new SimpleFieldFilter(), Globals.DOCUMENT_TITLE);
        XmlFieldHandler xmlFieldHandlerTitle = new SimpleXmlFieldHandler("./dc:title", new MultipleFieldFilter(3), "contents");
        XmlFieldHandler xmlFieldHandlerSubject = new SimpleXmlFieldHandler("./dc:subject", new MultipleFieldFilter(2), "contents");
        XmlFieldHandler xmlFieldHandlerDescription = new SimpleXmlFieldHandler("./dc:description", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerCreator = new SimpleXmlFieldHandler("./dc:creator", new SimpleFieldFilter(), "contents");
//        XmlFieldHandler xmlFieldHandlerCreatorPerson = new SimpleXmlFieldHandler("./digmap:creator-person/dc:creator", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerContributor = new SimpleXmlFieldHandler("./dc:contributor", new SimpleFieldFilter(), "contents");
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
//        XmlFieldHandler xmlFieldHandlerSpatial = new SimpleXmlFieldHandler("./dcterms:spatial", new DigmapSpatialFieldFilter(), "contents");
        XmlFieldHandler xmlFieldHandlerTime = new SimpleXmlFieldHandler("./dc:date", new SimpleFieldFilter(), "contents");

        XmlFieldHandler xmlFieldHandlerTimeLgte = new SimpleXmlFieldHandler("./dc:date", new DigmapTimeFieldFilter(), null);

        //To store all content in line
        XmlFieldHandler xmlFieldHandlerTitlePresentStore = new SimpleXmlFieldHandler("./dc:title", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerCreatorStore = new SimpleXmlFieldHandler("./dc:creator", new SimpleStoreFieldFilter(), "contentsStore");
//        XmlFieldHandler xmlFieldHandlerCreatorPersonStore = new SimpleXmlFieldHandler("./digmap:creator-person/dc:creator", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerContributorStore = new SimpleXmlFieldHandler("./dc:contributor", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerSubjectStore = new SimpleXmlFieldHandler("./dc:subject", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerDescriptionStore = new SimpleXmlFieldHandler("./dc:description", new SimpleStoreFieldFilter(), "contentsStore");
//        XmlFieldHandler xmlFieldHandlerSpatialStore = new SimpleXmlFieldHandler("./dcterms:spatial", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerTimeStore = new SimpleXmlFieldHandler("./dc:date", new SimpleStoreFieldFilter(), "contentsStore");

        XmlFieldHandler xmlFieldHandlerCreatorField = new SimpleXmlFieldHandler("./dc:creator", new SimpleIsolatedFieldFilter(), "creator");
//        XmlFieldHandler xmlFieldHandlerCreatorPersonStore = new SimpleXmlFieldHandler("./digmap:creator-person/dc:creator", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerContributorField = new SimpleXmlFieldHandler("./dc:contributor", new SimpleIsolatedFieldFilter(), "contributor");
        XmlFieldHandler xmlFieldHandlerSubjectField = new SimpleXmlFieldHandler("./dc:subject", new SimpleIsolatedFieldFilter(), "subject");
        XmlFieldHandler xmlFieldHandlerDescriptionField = new SimpleXmlFieldHandler("./dc:description", new SimpleIsolatedFieldFilter(), "description");
//        XmlFieldHandler xmlFieldHandlerSpatialStore = new SimpleXmlFieldHandler("./dcterms:spatial", new SimpleStoreFieldFilter(), "contentsStore");
        XmlFieldHandler xmlFieldHandlerTimeField = new SimpleXmlFieldHandler("./dc:date", new SimpleIsolatedFieldFilter(), "date");

        xmlFieldHandlers.add(xmlFieldHandlerOriginalRelativePath);
        xmlFieldHandlers.add(xmlFieldHandlerOriginalFilename);
        xmlFieldHandlers.add(xmlFieldHandlerCollection);
        xmlFieldHandlers.add(xmlFieldHandlerTitlePresent);
        xmlFieldHandlers.add(xmlFieldHandlerTitle);
        xmlFieldHandlers.add(xmlFieldHandlerSubject);
        xmlFieldHandlers.add(xmlFieldHandlerDescription);
        xmlFieldHandlers.add(xmlFieldHandlerCreator);
//        xmlFieldHandlers.add(xmlFieldHandlerCreatorPerson);
//        xmlFieldHandlers.add(xmlFieldHandlerContributor);
//        xmlFieldHandlers.add(xmlFieldHandlerSpatial);
        xmlFieldHandlers.add(xmlFieldHandlerTime);

        xmlFieldHandlers.add(xmlFieldHandlerCreatorField);
        xmlFieldHandlers.add(xmlFieldHandlerSubjectField);
        xmlFieldHandlers.add(xmlFieldHandlerDescriptionField);
        xmlFieldHandlers.add(xmlFieldHandlerContributorField);
        xmlFieldHandlers.add(xmlFieldHandlerTimeField);

        //To store all content in line
        xmlFieldHandlers.add(xmlFieldHandlerTitlePresentStore);
        xmlFieldHandlers.add(xmlFieldHandlerCreatorStore);
//        xmlFieldHandlers.add(xmlFieldHandlerCreatorPersonStore);
        xmlFieldHandlers.add(xmlFieldHandlerContributorStore);
        xmlFieldHandlers.add(xmlFieldHandlerSubjectStore);
        xmlFieldHandlers.add(xmlFieldHandlerDescriptionStore);
//        xmlFieldHandlers.add(xmlFieldHandlerSpatialStore);
        xmlFieldHandlers.add(xmlFieldHandlerTimeStore);
        xmlFieldHandlers.add(xmlFieldHandlerTimeLgte);


        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");


        ResourceHandler resourceHandler = new XmlResourceHandler("//oai_dc:dc" ,"dc:identifier[1]", "../@possibleId", xmlFieldHandlers, namespaces);
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

        Configuration d1 = new Configuration("version1", "digmapFrbr", "lm", Model.LanguageModel, IndexCollections.en.getAnalyzerNoStemming(), collectionPath, collectionsDirectory, topicsPath, null, "contents", IndexCollections.en.getWordList(), outputDir, maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(d1);
        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
//        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
//        queryConfiguration1.setForceQE(QEEnum.no);
//
//        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
//        queryConfiguration2.setForceQE(QEEnum.text);
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
        QName qNameXsiType = new QName(DigmapFrbrExample.DigmapSpatialFieldFilter.xsiType, new Namespace(DigmapFrbrExample.DigmapSpatialFieldFilter.xsiPrefix, DigmapFrbrExample.DigmapSpatialFieldFilter.xsiTypeNamespace));

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
                if (xsiTypeAttr != null && attValue.getName().equals(DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_point) && attValue.getNamespaceURI().equals(DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_point_namespace))
                {
                    Double lat = null;
                    Double lng = null;
                    List<String> coordinates = Strings.getListStrings(text, DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_spatial_limits_separator);
                    for (String coord : coordinates)
                    {
                        List<String> fields = Strings.getListStrings(coord, DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_spatial_limit_value_separator);
                        if (fields.size() == 2)
                        {
                            String name = fields.get(0).trim();
                            String value = fields.get(1).trim();
                            if (name.equals("north"))
                                lat = Double.parseDouble(value.trim());
                            else if (name.equals("east"))
                                lng = Double.parseDouble(value.trim());
                            else
                                logger.error("Error in Field spatial " + DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_point + ":" + text);
                        }
                    }
                    if (lat == null || lng == null)
                        logger.error("Error in Field spatial " + DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_point + ":" + text);
                    else
                    {
                        List<Field> pointFields = LgteDocumentWrapper.getGeoPointFields(lat, lng);
                        uniqueFields.addAll(pointFields);
                    }
                }
                //get dcterms Box field
                else
                if (xsiTypeAttr != null && attValue.getName().equals(DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_box) && attValue.getNamespaceURI().equals(DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_box_namespace))
                {
                    Double northlimit = null;
                    Double southlimit = null;
                    Double westlimit = null;
                    Double eastlimit = null;
                    List<String> coordinates = Strings.getListStrings(text, DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_spatial_limits_separator);
                    for (String coord : coordinates)
                    {
                        List<String> fields = Strings.getListStrings(coord, DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_spatial_limit_value_separator);
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
                                logger.error("Error in Field spatial " + DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_box + ":" + text);
                        }
                    }
                    if (northlimit == null || southlimit == null || eastlimit == null || westlimit == null)
                        logger.error("Error in Field spatial " + DigmapFrbrExample.DigmapSpatialFieldFilter.dcterms_att_box + ":" + text);
                    else
                    {
                        if(southlimit > northlimit)
                        {
                            logger.info("Possible record error, changing north with south.");
                            double aux = southlimit;
                            southlimit = northlimit;
                            northlimit = aux;
                        }
                        if(westlimit > eastlimit)
                        {
                            logger.info("Possible record error, changing east with west.");
                            double aux = westlimit;
                            westlimit = eastlimit;
                            eastlimit = aux;
                        }
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
