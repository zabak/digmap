package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;

import java.io.IOException;
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
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class ClefAdHocExample
{

    private static final Logger logger = Logger.getLogger(ClefAdHocExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "F:\\INDEXES";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


//        String collectionPath = Globals.DATA_DIR + "\\clef2008AdHoc\\documents";
        String collectionPath = "F:\\coleccoesIR\\CLEFAdHoc\\telCollection\\bl";
        String topicsPath = Globals.DATA_DIR + "\\clef2008AdHoc\\topics\\bl";
        String outputDir = Globals.DATA_DIR + "\\clef2008AdHoc\\output\\bl";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         *

         */
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlers,"contents");
        addFields(xmlFieldHandlers,"contentsN2");
        addFields(xmlFieldHandlers,"contentsN3");
        addFields(xmlFieldHandlers,"contentsN4");
        addFields(xmlFieldHandlers,"contentsN5");
        addFields(xmlFieldHandlers,"contentsN6");

        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaces.put("mods","http://www.loc.gov/mods");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");
        ResourceHandler resourceHandler = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlers,namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /**
         *
           <topic lang="fr">
                <identifier>10.2452/451-AH</identifier>
                <title>L'armée romaine en Grande-Bretagne</title>
                <description>Trouver des livres ou des publications sur l'invasion et l'occupation de la Grande-Bretagne par les Romains.</description>
            </topic>
         */
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        addTopics(xmlTopicFieldHandlers,"contents");
        addTopics(xmlTopicFieldHandlers,"contentsN2");
        addTopics(xmlTopicFieldHandlers,"contentsN3");
        addTopics(xmlTopicFieldHandlers,"contentsN4");
        addTopics(xmlTopicFieldHandlers,"contentsN5");
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 1000;

        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser


        Configuration VS_ADHOC = new Configuration("version1", "clef2008AdHoc","lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_ADHOC = new Configuration("version1", "clef2008AdHoc","lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);

        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents",IndexCollections.en.getAnalyzerNoStemming());
        ngramsAnalizers.put("contentsN2", LgteAnalyzerManager.getInstance().getLanguagePackage(2,2).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN3", LgteAnalyzerManager.getInstance().getLanguagePackage(3,3).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN4", LgteAnalyzerManager.getInstance().getLanguagePackage(4,4).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN5", LgteAnalyzerManager.getInstance().getLanguagePackage(5,5).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN6", LgteAnalyzerManager.getInstance().getLanguagePackage(6,6).getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers);
        Configuration VS_2_6GRAMS_CRAN = new Configuration("version1", "clef2008AdHoc","lmstem2_6grams", Model.VectorSpaceModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
        Configuration LM_2_6GRAMS_CRAN = new Configuration("version1", "clef2008AdHoc","lmstem2_6grams", Model.LanguageModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
//        configurations.add(LM_ADHOC);
//        configurations.add(LM_STEMMER_ADHOC);
        configurations.add(LM_2_6GRAMS_CRAN);


        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);


//        /***
//         * Search Configurations
//         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);
        queryConfiguration1.getQueryProperties().setProperty("lgte.default.order", "sc");

        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_ADHOC));

        SearchConfiguration.TopicsConfiguration topicsConfiguration = new SearchConfiguration.TopicsConfiguration();
        topicsConfiguration.setFieldBoost(new HashMap<String,Float>());
        topicsConfiguration.getFieldBoost().put("contents",0.53f);
        topicsConfiguration.getFieldBoost().put("contentsN5",0.14f);
        topicsConfiguration.getFieldBoost().put("contentsN4",0.11f);
        topicsConfiguration.getFieldBoost().put("contentsN3",0.11f);
        topicsConfiguration.getFieldBoost().put("contentsN2",0.11f);
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1,LM_2_6GRAMS_CRAN,topicsConfiguration ));

//        //Search Topics Runs to submission
//        SearchTopics.search(searchConfigurations);
    }


    private static void addTopics(List<XmlFieldHandler> xmlTopicFieldHandlers, String field)
    {
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(3),field);
        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),field);
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
    }

    private static void addFields(List<XmlFieldHandler> xmlFieldHandlers, String field)
    {
        XmlFieldHandler xmlTitle = new SimpleXmlFieldHandler(".//dc:title",new MultipleFieldFilter(4),field);
        XmlFieldHandler xmlAlternative = new SimpleXmlFieldHandler(".//dcterms:alternative",new MultipleFieldFilter(3),field);


        XmlFieldHandler xmlSubject = new SimpleXmlFieldHandler(".//dc:subject",new MultipleFieldFilter(3),field);

        XmlFieldHandler xmlCreator = new SimpleXmlFieldHandler(".//dc:creator",new MultipleFieldFilter(3),field);
        XmlFieldHandler xmlContributor = new SimpleXmlFieldHandler(".//dc:contributor",new MultipleFieldFilter(2),field);

        XmlFieldHandler xmlDescription = new SimpleXmlFieldHandler(".//dc:description",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlTableOfContents = new SimpleXmlFieldHandler(".//dc:tableOfContents",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlDctermsAbstract = new SimpleXmlFieldHandler(".//dcterms:abstract",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlDcAbstract = new SimpleXmlFieldHandler(".//dc:abstract",new MultipleFieldFilter(2),field);



        XmlFieldHandler xmlRights = new SimpleXmlFieldHandler(".//dc:rights",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlFormat = new SimpleXmlFieldHandler(".//dc:format",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlRelation = new SimpleXmlFieldHandler(".//dc:relation",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlIsPartOf = new SimpleXmlFieldHandler(".//dcterms:isPartOf",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlBCitation = new SimpleXmlFieldHandler(".//dcterms:bibliographicCitation",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlDate = new SimpleXmlFieldHandler(".//dc:date",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlCoverage = new SimpleXmlFieldHandler(".//dc:coverage",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlPublisher = new SimpleXmlFieldHandler(".//dc:publisher",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlType = new SimpleXmlFieldHandler(".//dc:type",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlLocation = new SimpleXmlFieldHandler(".//dc:location",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlModsLocation = new SimpleXmlFieldHandler(".//mods:location",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlSource = new SimpleXmlFieldHandler(".//dc:source",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlIssued = new SimpleXmlFieldHandler(".//dcterms:issued",new MultipleFieldFilter(1),field);

        //Global Search Index



        xmlFieldHandlers.add(xmlTitle);
        xmlFieldHandlers.add(xmlAlternative);
        xmlFieldHandlers.add(xmlSubject);
        xmlFieldHandlers.add(xmlCreator);
        xmlFieldHandlers.add(xmlContributor);
        xmlFieldHandlers.add(xmlDescription);
        xmlFieldHandlers.add(xmlTableOfContents);
        xmlFieldHandlers.add(xmlDctermsAbstract);
        xmlFieldHandlers.add(xmlDcAbstract);
        xmlFieldHandlers.add(xmlRights);
        xmlFieldHandlers.add(xmlFormat);
        xmlFieldHandlers.add(xmlRelation);
        xmlFieldHandlers.add(xmlIsPartOf);
        xmlFieldHandlers.add(xmlBCitation);
        xmlFieldHandlers.add(xmlDate);
        xmlFieldHandlers.add(xmlCoverage);
        xmlFieldHandlers.add(xmlPublisher);
        xmlFieldHandlers.add(xmlType);
        xmlFieldHandlers.add(xmlLocation);
        xmlFieldHandlers.add(xmlModsLocation);
        xmlFieldHandlers.add(xmlSource);
        xmlFieldHandlers.add(xmlIssued);
    }
}
