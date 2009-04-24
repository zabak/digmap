package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
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

    public static String country = "bl";
    public static LgteAnalyzerManager.LanguagePackage lang;

    private static final Logger logger = Logger.getLogger(ClefAdHocExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {
        country = "bl";
        lang  = IndexCollections.en;
        compute(args);

//        country = "bnf";
//        lang  = IndexCollections.fr;
//        compute(args);
//
//        country = "onb";
//        lang  = IndexCollections.de;
//        compute(args);
    }
    public static void compute(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "F:\\INDEXES";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        String collectionPath = "F:\\coleccoesIR\\CLEFAdHoc\\telCollection\\" + country;
//        String collectionPath = "C:\\WORKSPACE_JM\\DATA\\COLLECTIONS\\telCollection\\" + country;
        String topicsPath = Globals.DATA_DIR + "\\clef2008AdHoc\\topics\\" + country;
        String outputDir = Globals.DATA_DIR + "\\clef2008AdHoc\\output\\" + country;

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         *
         *  Collections Configuration
         */

        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaces.put("mods","http://www.loc.gov/mods");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");
        
        /*Regular Indexes*/
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlers,"contents");
        ResourceHandler resourceHandler = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlers,namespaces);
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);
        
        /*N-Grams*/
        List<XmlFieldHandler> xmlFieldHandlersNG = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlersNG,"contents");
        addFields(xmlFieldHandlersNG,"contentsN2");
        addFields(xmlFieldHandlersNG,"contentsN3");
        addFields(xmlFieldHandlersNG,"contentsN4");
        addFields(xmlFieldHandlersNG,"contentsN5");
        addFields(xmlFieldHandlersNG,"contentsN6");
        ResourceHandler resourceHandlerNG = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlersNG,namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryNG = new CDirectory(resourceHandlerNG,null);

        /*****************************************************
         *
         *  Topics Configuration
         *
         *
         * <topic lang="fr">
         *       <identifier>10.2452/451-AH</identifier>
         *       <title>L'arme romaine en Grande-Bretagne</title>
         *       <description>Trouver des livres ou des publications sur l'invasion et l'occupation de la Grande-Bretagne par les Romains.</description>
         *   </topic>
         ******************************************************/
        
        //Regular Indexes
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        addTopics(xmlTopicFieldHandlers,"contents");
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);
        
        //N-Grams
        //The handlers will be the same in 6 different fields, each one associated with different analyzers
        //The fields suffixed with NX will have XGrams Tokenizers        
        List<XmlFieldHandler> xmlTopicFieldHandlersNG = new ArrayList<XmlFieldHandler>();
        addTopics(xmlTopicFieldHandlersNG,"contents");
        addTopics(xmlTopicFieldHandlersNG,"contentsN2");
        addTopics(xmlTopicFieldHandlersNG,"contentsN3");
        addTopics(xmlTopicFieldHandlersNG,"contentsN4");
        addTopics(xmlTopicFieldHandlersNG,"contentsN5");
        ResourceHandler topicResourceHandlerNG = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlersNG);
        TrecEvalOutputFormatFactory factoryNG =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectoryNG = new TDirectory(topicResourceHandlerNG,factoryNG);
        
        

        /*****************
         * 
         * Configurations
         *  
         */
        //maxResults in output File per topic;
        int maxResults = 1000;

        //Regular Indexes
        Configuration VS_ADHOC = new Configuration("version1", "clef2008AdHoc" + country,"lm", Model.VectorSpaceModel, lang.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration LM_ADHOC = new Configuration("version1", "clef2008AdHoc" + country,"lm",Model.LanguageModel , lang.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc" + country,"lmstem", Model.VectorSpaceModel, lang.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc" + country,"lmstem", Model.LanguageModel, lang.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);

        //N-Grams will use one broker analyzer where each index will be created using different grams
        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents",lang.getAnalyzerNoStemming());
        ngramsAnalizers.put("contentsN2", LgteAnalyzerManager.getInstance().getLanguagePackage(2,2,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN3", LgteAnalyzerManager.getInstance().getLanguagePackage(3,3,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN4", LgteAnalyzerManager.getInstance().getLanguagePackage(4,4,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN5", LgteAnalyzerManager.getInstance().getLanguagePackage(5,5,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN6", LgteAnalyzerManager.getInstance().getLanguagePackage(6,6,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers);
        Configuration VS_2_6GRAMS_CRAN = new Configuration("version1", "clef2008AdHoc" + country,"lmstem2_6grams", Model.VectorSpaceModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectoryNG,topicsPath, topicsDirectoryNG,"contents", null,outputDir,maxResults);
        Configuration LM_2_6GRAMS_CRAN = new Configuration("version1", "clef2008AdHoc" + country,"lmstem2_6grams", Model.LanguageModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectoryNG,topicsPath, topicsDirectoryNG,"contents", null,outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
//        configurations.add(LM_ADHOC);
        configurations.add(LM_STEMMER_ADHOC);
        configurations.add(LM_2_6GRAMS_CRAN);


//        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);


//        /***
//         * Search Configurations
//         */



        QueryConfiguration queryConfigurationNoQE = new QueryConfiguration();
        queryConfigurationNoQE.setForceQE(QEEnum.no);
        queryConfigurationNoQE.getQueryProperties().setProperty("lgte.default.order", "sc");

        QueryConfiguration queryConfigurationNoQE_ngrams = new QueryConfiguration();
        queryConfigurationNoQE_ngrams.setForceQE(QEEnum.no);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contents","0.53f");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN5","0.14f");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN4","0.11f");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN3","0.11f");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN2","0.11f");


        //With Query Expansion
        QueryConfiguration queryConfigurationQE = new QueryConfiguration();
        queryConfigurationQE.setForceQE(QEEnum.text);
        queryConfigurationQE.getQueryProperties().setProperty("lgte.default.order", "sc");

        QueryConfiguration queryConfigurationQE_ngrams = new QueryConfiguration();
        queryConfigurationQE_ngrams.setForceQE(QEEnum.text);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contents","0.53f");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN5","0.14f");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN4","0.11f");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN3","0.11f");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN2","0.11f");

        

        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, VS_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, VS_STEMMER_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, LM_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, LM_STEMMER_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE_ngrams,LM_2_6GRAMS_CRAN ));

        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, VS_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, VS_STEMMER_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, LM_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, LM_STEMMER_ADHOC));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE_ngrams,LM_2_6GRAMS_CRAN ));


//        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);
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
