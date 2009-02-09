package pt.utl.ist.lucene.treceval.geoclef.indexer;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.dom4j.*;
import org.xml.sax.SAXException;
import pt.utl.ist.lucene.treceval.*;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResultsIterator;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResult;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.forms.RectangleForm;
import pt.utl.ist.lucene.forms.UnknownForm;
import pt.utl.ist.lucene.forms.GeoPoint;

import java.io.*;
import java.util.*;

import com.vividsolutions.jts.io.gml2.GMLReader;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefIndexer
{


    public static String LM_LAMBDA = "0.10";
    public static String TEXT_FACTOR = "0.2";
    public static String SPATIAL_FACTOR = "0.8";

    static boolean usaNarrativa = true;

    //if null will test all with treckEval
//    public static String[] testTopics = null;

    public static String[] testTopics = {
            "10.2452/100-GC",
            "10.2452/76-GC",
            "10.2452/77-GC",
            "10.2452/78-GC",
//            "10.2452/79-GC", //Timor ??
            "10.2452/80-GC",
//            "10.2452/81-GC", //Mediterraneo muito grande a query
//            "10.2452/82-GC",
//            "10.2452/83-GC",
//            "10.2452/84-GC", // Irlanda poder-se-ha confindir as duas irlandas norte e sul
            "10.2452/85-GC",
            "10.2452/86-GC",
            "10.2452/87-GC", // Nao tem coordenadas por apanharem o mundo imigrantes
            "10.2452/88-GC", // OCDE
//            "10.2452/89-GC",
//            "10.2452/90-GC",  //Europa toda
//            "10.2452/91-GC",
            "10.2452/92-GC",
            "10.2452/93-GC",
//            "10.2452/94-GC",
            "10.2452/95-GC",
            "10.2452/96-GC",
            "10.2452/97-GC",
            "10.2452/98-GC",
            "10.2452/99-GC",

    };

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */


    static int maxResults = 1000;
    static GeoParserResultsIterator geoParserResultsIterator = null;

    /*This method has been created adhoc just to pass Optimazable parameters from Mallet class*/
    private static void checkConfigurationFile()
    {
        File params = new File("d:\\geoClefParameters.properties");
        if(params.exists())
        {
            try{
                Properties p = new Properties();
                p.load(new FileInputStream(params));
                LM_LAMBDA = p.getProperty("LM_LAMBDA");
                TEXT_FACTOR = p.getProperty("TEXT_FACTOR");
                SPATIAL_FACTOR = p.getProperty("SPATIAL_FACTOR");
            }
            catch (FileNotFoundException e)
            {
                logger.error(e,e);
            }
            catch (IOException e)
            {
                logger.error(e,e);
            }

        }

    }

    public static void main(String[] args) throws DocumentException, IOException
    {

        checkConfigurationFile();
        //topics and output wil be the same for english collections
        String topicsPath = "\\geoclef08en\\topics";
        String outputPath = "\\geoclef08en\\output";
        String assessementsPath = "\\geoclef08en\\assessements";

        //we will use a specific location for collection dir, and not data dir like in other examples.
        String collectionPath2 = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95";
        String collectionPath1 = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\lat-en";

        TextFieldsReader collectionTextReader2 = new Gh95TextFieldsReader();
        TextFieldsReader collectionTextReader1 = new LatEnTextFieldsReader();
        String collectionUnionName = "geoclef_en";
        LgteAnalyzerManager.LanguagePackage languagePackage = IndexCollections.en;

        String geoParseInputDirCollection2 = pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\gh95";
        String geoParseInputDirCollection1 = pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\latEn";


        String collection1DefaultEncoding = "ISO-8859-1";
        String collection2DefaultEncoding = "ISO-8859-1";
        String collection1DefaultGzipType = "sgml";
        String collection2DefaultGzipType = "sgml";

        run(    topicsPath,
                outputPath,
                assessementsPath,
                collectionPath1,
                collectionPath2,
                collectionTextReader1,
                collectionTextReader2,
                collectionUnionName,
                languagePackage,
                geoParseInputDirCollection1,
                geoParseInputDirCollection2,
                collection1DefaultEncoding,
                collection2DefaultEncoding,
                collection1DefaultGzipType,
                collection2DefaultGzipType);

//        //topics and output wil be the same for english collections
//        String topicsPath = Globals.DATA_DIR + "\\geoclef08pt\\topics";
//        String outputPath = Globals.DATA_DIR + "\\geoclef08pt\\output";
//
//        //we will use a specific location for collection dir, and not data dir like in other examples.
//        String collectionPath1 = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathPt + "\\publico-pt";
//        String collectionPath2 = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathPt + "\\folha-pt";
//
//        TextFieldsReader collectionTextReader1 = new PublicoTextFieldsReader();
//        TextFieldsReader collectionTextReader2 = new FolhaTextFieldsReader();
//        String collectionUnionName = "geoclef_pt";
//        LgteAnalyzerManager.LanguagePackage languagePackage = IndexCollections.pt;
//
//        String geoParseInputDirCollection1 = pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\publico";
//        String geoParseInputDirCollection2 = pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\folha";
//
//
//        String collection1DefaultEncoding = "ISO-8859-1";
//        String collection2DefaultEncoding = "ISO-8859-1";
//        String collection1DefaultGzipType = "sgml";
//        String collection2DefaultGzipType = "sgml";
//
//        run(    topicsPath,
//                outputPath,
//                assessementsPath,
//                collectionPath1,
//                collectionPath2,
//                collectionTextReader1,
//                collectionTextReader2,
//                collectionUnionName,
//                languagePackage,
//                geoParseInputDirCollection1,
//                geoParseInputDirCollection2,
//                collection1DefaultEncoding,
//                collection2DefaultEncoding,
//                collection1DefaultGzipType,
//                collection2DefaultGzipType);

    }

    public static void run(String topicsPath,
                           String outputPath,
                           String assessementsPath,
                           String collectionPath1,
                           String collectionPath2,
                           TextFieldsReader collectionTextReader1,
                           TextFieldsReader collectionTextReader2,
                           String collectionUnionName,
                           LgteAnalyzerManager.LanguagePackage languagePackage,
                           String geoParseInputDirCollection1,
                           String geoParseInputDirCollection2,
                           String collection1DefaultEncoding,
                           String collection2DefaultEncoding,
                           String collection1DefaultGzipType,
                           String collection2DefaultGzipType) throws DocumentException, IOException
    {


        String[] args = new String[2];
        args[0] = "D:\\INDEXES";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";

        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];

        topicsPath = Globals.DATA_DIR + topicsPath;
        outputPath = Globals.DATA_DIR + outputPath;
        assessementsPath = Globals.DATA_DIR + assessementsPath;

        //Collection CDirectory iterator will use DATA dir to substring files path
        //so in this special case we will set data dir with special collection path dir and
        //then we will put the old value again before call Topics Runners
        String dataDir = Globals.DATA_DIR;


        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + collectionPath1);
        logger.info("and Reading data from:" + collectionPath2);

        //Global Search Index

        /**
         * Collection 1 Collection Doc FIELDS
         */
        List<XmlFieldHandler> xmlFieldHandlersCollection1 = new ArrayList<XmlFieldHandler>();
        XmlFieldHandler xmlHandlerColection1 = new SimpleXmlFieldHandler(".", new GenericFieldFilter(collectionTextReader1), "contents");
        xmlFieldHandlersCollection1.add(xmlHandlerColection1);
        ResourceHandler resourceHandlerCollection1 = new XmlResourceHandler("//DOC", "DOCNO", xmlFieldHandlersCollection1);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryCollection1 = new CDirectory(resourceHandlerCollection1, null);

        /**
         * Collection2 Collection Doc FIELDS
         */
        List<XmlFieldHandler> xmlFieldHandlersCollection2 = new ArrayList<XmlFieldHandler>();
        XmlFieldHandler xmlHandlerCollection2 = new SimpleXmlFieldHandler(".", new GenericFieldFilter(collectionTextReader2), "contents");
        xmlFieldHandlersCollection2.add(xmlHandlerCollection2);
        ResourceHandler resourceHandlerCollection2 = new XmlResourceHandler("//DOC", "DOCNO", xmlFieldHandlersCollection2);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryCollection2 = new CDirectory(resourceHandlerCollection2, null);

        /**
         * EN TOPICS
         * <topics>
         *   <topic lang="en">
         <identifier>10.2452/100-GC</identifier>
         <title>Natural disasters in the Western USA</title>
         <description>Douments need to describe natural disasters in the Western USA</description>
         <narrative>Relevant documents report on natural disasters like earthquakes or
         flooding which took place in Western states of the United States. To the Western states belong California, Washington and Oregon.</narrative>
         </topic>
         ....
         ....
         ....
         <Folder>
         <name>GEO_CLEF2008_PT</name>
         <open>1</open>
         <Placemark>
         <name>10.2452/100-GC</name>
         <styleUrl>#msn_ylw-pushpin4</styleUrl>
         <Polygon>
         <tessellate>1</tessellate>
         <outerBoundaryIs>
         <LinearRing>
         <coordinates>
         -124.498358122888,48.49603245415199,0 -124.5200563442772,40.45084338706569,0 -123.7647141075587,38.74297829560835,0 -120.384613013749,34.43506099289216,0 -117.0812613361983,32.49464394321812,0 -114.5320789875417,32.78102951503264,0 -114.581922096389,35.00220307292381,0 -120.019926726697,38.97223517088855,0 -119.9531998861885,41.98891288245463,0 -116.8977822526316,41.90123522215136,0 -116.8927350005536,44.13990621280611,0 -117.1406641320271,44.28484372301948,0 -116.454233154681,45.61098788882967,0 -116.6873899985583,45.76042303867632,0 -116.8608635868606,45.81735407688642,0 -116.9524586951087,46.19855779194396,0 -116.9975325174537,48.99651222436788,0 -123.2253443267105,48.93184033829019,0 -123.155470485594,48.31707212522704,0 -124.498358122888,48.49603245415199,0 </coordinates>
         </LinearRing>
         </outerBoundaryIs>
         </Polygon>
         </Placemark>
         ....
         </topics>
         */
        String topics;
        if(testTopics!= null && testTopics.length > 0)
        {
            StringBuilder xPath = new StringBuilder();
            boolean first = true;
            for(String testTopic: testTopics)
            {
                if(!first)
                    xPath.append(" or ");
                xPath.append(" identifier='").append(testTopic).append("'");
                first = false;
            }
            topics =  "//topic[" + xPath.toString() + "]";
        }
        else
            topics = "//topic";
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title", new MultipleFieldFilter(2), "contents");
        XmlFieldHandler xmlDescriptionTopicFieldHandler = new SimpleXmlFieldHandler("./description", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlNarrativeTopicFieldHandler = new SimpleXmlFieldHandler("./narrative", new SimpleFieldFilter(), "contents");
        XmlFieldHandler xmlGeoTopicFieldHandler = new SimpleXmlFieldHandler(".", new GeoTopicFieldFilter(), "contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        if(usaNarrativa)
            xmlTopicFieldHandlers.add(xmlNarrativeTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlDescriptionTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlGeoTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler(topics, "./identifier", xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory = new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler, factory);


        //Create Index Configurations for LM ( and will be used both to LM and Vector Space Model)
        Configuration LM_GEO_COLLECTION1 = new Configuration("version1", collectionUnionName, "lm", Model.LanguageModel, languagePackage.getAnalyzerNoStemming(), collectionPath1, collectionsDirectoryCollection1, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        Configuration LM_GEO_COLLECTION2 = new Configuration("version1", collectionUnionName, "lm", Model.LanguageModel, languagePackage.getAnalyzerNoStemming(), collectionPath2, collectionsDirectoryCollection2, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        LM_GEO_COLLECTION2.setCreateIndex(false);       //will use index of first configuration
        //Create Index Configurations LM with stemming (will work both to LM and Vector Space Model)
        Configuration LM_STEMMER_COLLECTION1 = new Configuration("version1", collectionUnionName, "lmstem", Model.LanguageModel, languagePackage.getAnalyzerWithStemming(), collectionPath1, collectionsDirectoryCollection1, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        Configuration LM_STEMMER_COLLECTION2 = new Configuration("version1", collectionUnionName, "lmstem", Model.LanguageModel, languagePackage.getAnalyzerWithStemming(), collectionPath2, collectionsDirectoryCollection2, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        LM_STEMMER_COLLECTION2.setCreateIndex(false); //will use index of first configuration

//        //Index GH95 without stemming
//        List<Configuration> configurations = new ArrayList<Configuration>();
//        configurations.add(LM_GEO_COLLECTION1);
//        geoParserResultsIterator = new GeoParserResultsIterator(geoParseInputDirCollection1);
//        Globals.COLLECTION_FILES_DEFAULT_ENCODING = collection1DefaultEncoding;
//        Globals.GZipDefaultContentType = collection1DefaultGzipType;
//        Globals.DATA_DIR = collectionPath1;
////        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
//
//        //Index in same index folder LatEn without stemming
//        configurations = new ArrayList<Configuration>();
//        configurations.add(LM_GEO_COLLECTION2);
//        geoParserResultsIterator = new GeoParserResultsIterator(geoParseInputDirCollection2);
//        Globals.COLLECTION_FILES_DEFAULT_ENCODING = collection2DefaultEncoding;
//        Globals.GZipDefaultContentType = collection2DefaultGzipType;
//        Globals.DATA_DIR = collectionPath2;
////        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
//
//        //Index GH95 with stemming
//        configurations = new ArrayList<Configuration>();
//        configurations.add(LM_STEMMER_COLLECTION1);
//        geoParserResultsIterator = new GeoParserResultsIterator(geoParseInputDirCollection1);
//        Globals.COLLECTION_FILES_DEFAULT_ENCODING = collection1DefaultEncoding;
//        Globals.GZipDefaultContentType = collection1DefaultGzipType;
//        Globals.DATA_DIR = collectionPath1;
////        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
//
//        //Index in same index folder LatEn with stemming
//        configurations = new ArrayList<Configuration>();
//        configurations.add(LM_STEMMER_COLLECTION2);
//        geoParserResultsIterator = new GeoParserResultsIterator(geoParseInputDirCollection2);
//        Globals.COLLECTION_FILES_DEFAULT_ENCODING = collection2DefaultEncoding;
//        Globals.GZipDefaultContentType = collection2DefaultGzipType;
//        Globals.DATA_DIR = collectionPath2;
////        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);
//

        Globals.DATA_DIR = dataDir;
        //Configurations to Perform Topic Search
        Configuration LM_GEO_UNION = LM_GEO_COLLECTION1;
        Configuration LM_STEMMER_GEO_UNION = LM_STEMMER_COLLECTION1;
        Configuration VS_GEO_UNION = new Configuration("version1", collectionUnionName, "lm", Model.VectorSpaceModel, languagePackage.getAnalyzerNoStemming(), null, null, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        Configuration VS_STEMMER_GEO_UNION = new Configuration("version1", collectionUnionName, "lmstem", Model.VectorSpaceModel, languagePackage.getAnalyzerWithStemming(), null, null, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
        Configuration BM25_STEMMER_GEO_UNION = new Configuration("version1", collectionUnionName, "lmstem", Model.OkapiBM25Model, languagePackage.getAnalyzerWithStemming(), null, null, topicsPath, topicsDirectory, "contents", languagePackage.getWordList(), outputPath, maxResults);
//
//
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();

        queryConfiguration1.setForceQE(QEEnum.text);
        queryConfiguration1.getQueryProperties().put("lgte.default.filter", "no");
        queryConfiguration1.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration1.getQueryProperties().put("lgte.default.order", "sc");
        queryConfiguration1.getQueryProperties().put("LM-lambda",LM_LAMBDA);
//        queryConfiguration1.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator")  ;
////
//        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
//        queryConfiguration2.setForceQE(QEEnum.text);
//        queryConfiguration2.getQueryProperties().put("lgte.default.filter", "no");
//        queryConfiguration2.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
//        queryConfiguration2.getQueryProperties().put("lgte.default.order", "sc");
//        queryConfiguration2.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator") ;
//
//        QueryConfiguration queryConfiguration3 = new QueryConfiguration();
//        queryConfiguration3.setForceQE(QEEnum.lgte);
//        queryConfiguration3.getQueryProperties().put("lgte.default.filter", "no");
//        queryConfiguration3.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
//        queryConfiguration3.getQueryProperties().put("lgte.default.order", "sc");
//        queryConfiguration3.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator");


        QueryConfiguration queryConfiguration4 = new QueryConfiguration();
        queryConfiguration4.setForceQE(QEEnum.text);
        queryConfiguration4.getQueryProperties().put("lgte.default.filter", "no");
        queryConfiguration4.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration4.getQueryProperties().put("lgte.default.order", "sc_sp");
        queryConfiguration4.getQueryProperties().put("default.model.spatial.factor",SPATIAL_FACTOR);
        queryConfiguration4.getQueryProperties().put("default.model.text.factor",TEXT_FACTOR);
        queryConfiguration4.getQueryProperties().put("LM-lambda",LM_LAMBDA);
//        queryConfiguration4.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator");


//        QueryConfiguration queryConfiguration5 = new QueryConfiguration();
//        queryConfiguration5.setForceQE(QEEnum.text);
//        queryConfiguration5.getQueryProperties().put("lgte.default.filter", "no");
//        queryConfiguration5.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
//        queryConfiguration5.getQueryProperties().put("lgte.default.order", "sc_sp");
//        queryConfiguration5.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator")    ;

//        QueryConfiguration queryConfiguration6 = new QueryConfiguration();
//        queryConfiguration6.setForceQE(QEEnum.lgte);
//        queryConfiguration6.getQueryProperties().put("lgte.default.filter", "no");
//        queryConfiguration6.getQueryProperties().put("spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.models.comparators.strategy.BoxQueryWithBoxDoc");
//        queryConfiguration6.getQueryProperties().put("lgte.default.order", "sc_sp");
//        queryConfiguration6.getQueryProperties().put("score.model","pt.utl.ist.lucene.sort.sorters.models.PiModelSortDocComparator")   ;


        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();


//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_GEO_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_GEO_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_GEO_UNION));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_GEO_UNION));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM_STEMMER_GEO_UNION));
////
////
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM_STEMMER_GEO_UNION));
////
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM_STEMMER_GEO_UNION));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS_STEMMER_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM_GEO_UNION));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM_STEMMER_GEO_UNION));
////
////
//        //Search Topics Runs to submission
        generateQrelsWithChoosedTopics(assessementsPath,assessementsPath + "/qrels.gen");

        SearchTopics.search(searchConfigurations);



    }


    private static void generateQrelsWithChoosedTopics(String assessementsPath, String toFile)
    {
        try
        {
            HashMap map = new HashMap();
            if(testTopics != null && testTopics.length > 0)
                for(String topicChoosed: testTopics)
                    map.put(topicChoosed,topicChoosed);

            FileWriter fw = new FileWriter(toFile);
            BufferedReader reader = new BufferedReader(new FileReader(new File(assessementsPath).listFiles()[0]));
            String line;
            while((line = reader.readLine())!=null)
            {
                String topic = line.substring(0,line.indexOf(' '));
                if(map.size() == 0 || map.get(topic) != null)
                    fw.write(line+"\n");
            }
            fw.close();
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            logger.error(e);
        }
        catch (IOException e)
        {
            logger.error(e);
        }
    }


    static GeometryFactory fact = new GeometryFactory();

    static class GeoTopicFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {

            System.gc();
            //todo testar isto ver se le mesmo do Ficheiro
            Element identifierElem = (Element) element.selectSingleNode("identifier");
            String identifier = identifierElem.getText();
            XPath xPath = element.getDocument().createXPath("//Placemark[name/text()='" + identifier.trim() +"']/Polygon");
            Element polygon = (Element) xPath.selectSingleNode(element.getDocument());
            if(polygon == null)
                return null;
            StringWriter sw = new StringWriter();
            try
            {

                Dom4jUtil. write(polygon, sw);
                GMLReader reader = new GMLReader();
                Geometry geo = reader.read(sw.toString().replace("xmlns:gml=\"http://www.opengis.net/gml\"", ""), fact);
                Envelope envelope = geo.getEnvelopeInternal();
                //Cames from Google Earth coordinates are changed
                UnknownForm unknownForm = new RectangleForm(envelope.getMaxY(), envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), new GeoPoint(geo.getCentroid().getX(), geo.getCentroid().getY()));
                //radium will be passed through and LGTE will use it as Diagonal
                double radium = unknownForm.getWidth() / ((double) 2);
                Map<String, String> fields = new HashMap<String, String>();
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_LATITUDE_FIELD_QUERY, "" + geo.getCentroid().getY());
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_LONGITUDE_FIELD_QUERY, "" + geo.getCentroid().getX());
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_RADIUM_FIELD_QUERY, "" + radium);

                return new FilteredFields(fields);
            }
            catch (IOException e)
            {
                logger.error(e, e);
            }
            catch (ParserConfigurationException e)
            {
                logger.error(e, e);
            }
            catch (SAXException e)
            {
                logger.error(e, e);
            }
            return null;
        }
    }


    static class GenericFieldFilter implements FieldFilter
    {


        TextFieldsReader textFieldsReader;

        public GenericFieldFilter(TextFieldsReader textFieldsReader)
        {
            this.textFieldsReader = textFieldsReader;
        }

        public FilteredFields filter(Node element, String fieldName)
        {

            Element docElem = (Element) element;
            List<Field> fieldsGeo = null;
            Map<String, String> fieldsText = new HashMap<String, String>();

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
                fieldsText = textFieldsReader.getText(docElem, docno, fieldName);
                //AppendingPlaces
                GeoParserResult geoParserResult = geoParserResultsIterator.next(docno);
                if (geoParserResult == null)
                    logger.error("No GEO PARSE RESULT to docno: " + docno);
                else
                {
                    //GeoParserIterator allays return a GeoResult in an UnkownForm if is a point diagonal comes zero
                    if (geoParserResult.getGenericUnknownForm() != null)
                    {
                        if (geoParserResult.getGenericUnknownForm() instanceof RectangleForm)
                        {
                            fieldsGeo = LgteDocumentWrapper.getGeoBoxFields((RectangleForm) geoParserResult.getGenericUnknownForm());
                        }
                        else
                        {
                            fieldsGeo = LgteDocumentWrapper.getGeoPointFields(geoParserResult.getGenericUnknownForm().getCentroide());
                        }
                    }
                }
                StringBuilder strBuilder = new StringBuilder(fieldsText.get(fieldName));
                if (geoParserResult != null)
                {
                    for (String place : geoParserResult.getPlaces())
                    {
                        strBuilder
                                .append(" ").append(place)
                                .append(" ").append(place);
                    }
                }
                fieldsText.put(fieldName, strBuilder.toString());
            }

            if (fieldsGeo == null)
                return new FilteredFields(fieldsText);
            else
                return new FilteredFields(fieldsText, fieldsGeo);
        }
    }

    public static interface TextFieldsReader
    {
        public Map<String, String> getText(Element docElem, String docno, String fieldName);
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
    public static class Gh95TextFieldsReader implements TextFieldsReader
    {
        public Map<String, String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
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
            strBuilder.append(headline).append(" ")
                    .append(headline).append(" ")
                    .append(text);
            fields.put(fieldName, strBuilder.toString());
            return fields;
        }
    }

    /**
     * latin-en example
     * <DOC>
     * <DOCNO> LA012394-0086 </DOCNO>
     * <DOCID> 006365 </DOCID>
     * <SOURCE><P>Los Angeles Times</P></SOURCE>
     * <DATE><P>January 23, 1994, Sunday, Ventura West Edition</P></DATE>
     * <SECTION><P>Metro; Part B; Page 1; Column 2</P></SECTION>
     * <LENGTH><P>4337 words</P></LENGTH>
     * <HEADLINE><P>
     * EARTHQUAKE: THE LONG ROAD BACK; DIARY OF A DISASTER; RESIDENTS STRUGGLING TO
     * PUT LIVES BACK IN ORDER ONE DAY AT A TIME</P>
     * </HEADLINE>
     * <BYLINE><P>By STEPHANIE SIMON, TIMES STAFF WRITER</P></BYLINE>
     * <TEXT>
     * <P>DAY 1</P>
     * <P>Darkness. Then, abruptly, a jolt. A crash. Rumbling, screaming, shattering,tumbling. Panic. And again, darkness.</P>
     * ...
     * </TEXT>
     * </DOC>
     */

    public static class LatEnTextFieldsReader implements TextFieldsReader
    {
        public Map<String, String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
            XPath xPathHeadline = docElem.createXPath("./HEADLINE//text()");
            XPath xPathText = docElem.createXPath("./TEXT//text()");
            List<Node> headlineElems = xPathHeadline.selectNodes(docElem);
            String headline = "";
            if (headlineElems != null)
            {
                StringBuilder headlinesBuilder = new StringBuilder();
                for (Node n : headlineElems)
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
                for (Node n : textElems)
                {
                    textBuilder.append(" ").append(n.getText());
                }
                text = textBuilder.toString();
            }
            else
                logger.warn("DOC " + docno + " with no text");
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(headline).append(" ").append(headline).append(" ").append(text);
            fields.put(fieldName, strBuilder.toString());
            return fields;
        }
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
    public static class PublicoTextFieldsReader implements TextFieldsReader
    {
        public Map<String, String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
            Element textElem = (Element) docElem.selectSingleNode("TEXT");
            String text = "";
            if (textElem != null)
                text = textElem.getText().trim();
            else
                logger.warn("DOC " + docno + " with no text");

            String headline = "";
            int firstlineEnd = text.indexOf("\n");
            if (firstlineEnd > 0)
            {
                headline = text.substring(0, firstlineEnd);
            }
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(headline).append(" ").append(headline).append(" ").append(text);
            fields.put(fieldName, strBuilder.toString());
            return fields;
        }
    }

    /* FOLHA EXAMPLE
           * <DOC>
           <DOCNO>FSP940101-131</DOCNO>
           <DOCID>FSP940101-131</DOCID>
           <DATE>940101</DATE>
           <TEXT>
           Livro retoma a conversa com Otto Lara
           ...
           </TEXT>
           </DOC>

    */

    public static class FolhaTextFieldsReader implements TextFieldsReader
    {
        public Map<String, String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
            Element textElem = (Element) docElem.selectSingleNode("TEXT");
            String text = "";
            if (textElem != null)
                text = textElem.getText();
            else
                logger.warn("DOC " + docno + " with no text");
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(" ").append(text);
            fields.put(fieldName, strBuilder.toString());
            return fields;
        }
    }
}