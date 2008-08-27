package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.Node;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class DigmapExample
{

    private static final Logger logger = Logger.getLogger(DigmapExample.class);
    public static void main(String [] args) throws DocumentException, IOException
    {

        args = new String[4];
        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\documents";
        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\topics";
        args[2] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\digmap\\output";
        args[3] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index\\digmap";



        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

        String dataDir = Globals.DATA_DIR;
        if(args.length>3)
            dataDir = args[3];

        logger.info("Forcing data dir to: " + dataDir);

        Globals.DATA_DIR = dataDir;



        XmlFieldHandler xmlFieldHandlerTitle = new SimpleXmlFieldHandler("./dc:title",new MultipleFieldFilter(3),"contents");
        XmlFieldHandler xmlFieldHandlerSubject = new SimpleXmlFieldHandler("./dc:subject",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlFieldHandlerDescription = new SimpleXmlFieldHandler("./dc:description",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlFieldHandlerCreator = new SimpleXmlFieldHandler("./dc:creator",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlFieldHandlerCreatorPerson = new SimpleXmlFieldHandler("./digmap:creator-person/dc:creator",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlFieldHandlerContributor = new SimpleXmlFieldHandler("./dc:contributor",new SimpleFieldFilter(),"contents");
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlFieldHandlerTitle);
        xmlFieldHandlers.add(xmlFieldHandlerSubject);
        xmlFieldHandlers.add(xmlFieldHandlerDescription);
        xmlFieldHandlers.add(xmlFieldHandlerCreator);
        xmlFieldHandlers.add(xmlFieldHandlerCreatorPerson);
        xmlFieldHandlers.add(xmlFieldHandlerContributor);
        XPath x;

        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("digmap","http://www.digmap.eu/schemas/resource/");
        namespaces.put("dc","http://purl.org/dc/elements/1.1/");
        namespaces.put("terms","http://purl.org/dc/terms/");
        

        ResourceHandler resourceHandler = new XmlResourceHandler("//record/digmap:record","@urn","./dc:identifier[0]",xmlFieldHandlers,namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

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

        Configuration d1 = new Configuration("version1", "digmap","vs", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, null,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(d1);
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

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
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, BM25_STEMMER_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_BC));
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, BM25_STEMMER_BC));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M1));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M2));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M3));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M4));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M5));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M6));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M7));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M8));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M9));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M10));
//
//        //Search Topics Runs to submission
//        SearchTopics.search(searchConfigurations);
    }

    /*Specific filter for BrounCorpus Resource Collection*/
    static class DigmapSpatialFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node node, String fieldName)
        {
            Element spatial = (Element) node;
            HashMap<String, String> map = new HashMap<String, String>();
            String text = spatial.getText();

            BufferedReader reader = new BufferedReader(new StringReader(text));
            StringBuilder firstLines = new StringBuilder();
            try
            {
                String line = reader.readLine();
                for(int li = 0; li < 10 && line != null;li++)
                {
                    if(line.indexOf("TITEL") < 0)
                        firstLines.append(" ").append(line);
                    line = reader.readLine();
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            map.put(Globals.DOCUMENT_TITLE,firstLines.toString());
            map.put(fieldName,firstLines.toString() + " " + firstLines.toString() + " "  + text);
            return null;
        }
    }
}
