package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class CranfieldExample
{

    private static final Logger logger = Logger.getLogger(CranfieldExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {

//        System.setProperty("lgte.score.fields.query.independent","true");

        args = new String[2];
        args[0] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        String collectionPath = Globals.DATA_DIR + "\\cran\\documents";
        String topicsPath = Globals.DATA_DIR + "\\cran\\topics";
        String outputDir = Globals.DATA_DIR + "\\cran\\output";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         * Lets create Cranfield Collection preprocessor
         * We just need one field handler for TEXT field @see example
         * example:
         *
         * <CRAN>
         *    <DOC>
         *     <DOCNO>1</DOCNO>
         *     <TITLE>experimental investigation of the aerodynamics of a wing in a slipstream .</TITLE>
         *     <AUTHOR>brenckman,m.</AUTHOR>
         *     <BIBLIO>j. ae. scs. 25, 1958, 324.</BIBLIO>
         *     <TEXT>
         *          an experimental study of a wing in a propeller slipstream was made in order to determine
         *          the spanwise distribution of the lift increase due to slipstream at different angles of
         *          attack of the wing and at different free stream to slipstream velocity ratios .
         *          the results were intended in part as an evaluation basis for different theoretical
         *          treatments of this problem . the comparative span loading curves, together with
         *   *          supporting evidence, showed that a substantial part of the lift increment produced by
         *          ...
         *     </TEXT>
         *    </DOC>
         *    ...
         *  </CRAN>
         *
         * Our text field will have no special filters, to see a special filter see BrounCorpusExample,
         *
         * for details @see ICollectionPreprocessor Architecture Diagrams @ LGTE website
         */
        //The handlers will be the same in 6 different fields, each one associated with different analyzers
        //The fields suffixed with NX will have XGrams Tokenizers
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlers, "contents");
        addFields(xmlFieldHandlers, "contentsN2");
        addFields(xmlFieldHandlers, "contentsN3");
        addFields(xmlFieldHandlers, "contentsN4");
        addFields(xmlFieldHandlers, "contentsN5");
        addFields(xmlFieldHandlers, "contentsN6");
        //Special Indexes for Advanced Search        
        XmlFieldHandler xmlAuthorFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new SimpleFieldFilter(),"author");
        XmlFieldHandler xmlTitleFieldHandler = new SimpleXmlFieldHandler("./TITLE",new SimpleFieldFilter(),pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE);
        xmlFieldHandlers.add(xmlAuthorFieldHandler);
        xmlFieldHandlers.add(xmlTitleFieldHandler);

        ResourceHandler resourceHandler = new XmlResourceHandler("//DOC","DOCNO",xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        
        
        /*******************************************
        //TOPICS
        ********************************************/
        /**
         * Now let's create a Topics processor
         * The principle is the same, we need a directoryHandler
         * and we need to give it a ResourceHandler
         *  Example
         *  <top>
         *     <num>1</num>
         *     <description>what similarity laws must be obeyed when constructing aeroelastic priors of heated high speed aircraft .</description>
         *  </top>
         */
        
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();

        xmlTopicFieldHandlers.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contents"));

        ResourceHandler topicResourceHandler = new XmlResourceHandler("//top","./num",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);


        List<XmlFieldHandler> xmlTopicFieldHandlersGrams = new ArrayList<XmlFieldHandler>();

        xmlTopicFieldHandlersGrams.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contents"));
        xmlTopicFieldHandlersGrams.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contentsN5"));
        xmlTopicFieldHandlersGrams.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contentsN4"));
        xmlTopicFieldHandlersGrams.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contentsN3"));
        xmlTopicFieldHandlersGrams.add(new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contentsN2"));

        ResourceHandler topicResourceHandlerGrams = new XmlResourceHandler("//top","./num",xmlTopicFieldHandlersGrams);
        TrecEvalOutputFormatFactory factoryGrams =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectoryGrams = new TDirectory(topicResourceHandlerGrams,factoryGrams);

        
        /*******************************************
        //Configurations
        ********************************************/
        //maxResults in output File per topic;
        int maxResults = 500;

        //Lets create our configurations
        //We gone put the differences about model, output folder name, analyzer, etc
        Configuration VS_CRAN = new Configuration("version1", "cran","lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_CRAN = new Configuration("version1", "cran","lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);

        //We will create now one broker analizer with different analizers to handle different fields
        //The objective is to stem our text fields differently in each index field
        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents",IndexCollections.en.getAnalyzerNoStemming());
        ngramsAnalizers.put("author",IndexCollections.en.getAnalyzerNoStemming());
        ngramsAnalizers.put(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE,IndexCollections.en.getAnalyzerNoStemming());
        ngramsAnalizers.put("contentsN2", LgteAnalyzerManager.getInstance().getLanguagePackage(2,2,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN3", LgteAnalyzerManager.getInstance().getLanguagePackage(3,3,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN4", LgteAnalyzerManager.getInstance().getLanguagePackage(4,4,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN5", LgteAnalyzerManager.getInstance().getLanguagePackage(5,5,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN6", LgteAnalyzerManager.getInstance().getLanguagePackage(6,6,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers);
        Configuration VS_2_6GRAMS_CRAN = new Configuration("version1", "cran","lmstem2_6grams", Model.VectorSpaceModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectory,topicsPath, topicsDirectoryGrams,"contents", null,outputDir,maxResults);
        Configuration LM_2_6GRAMS_CRAN = new Configuration("version1", "cran","lmstem2_6grams", Model.LanguageModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectory,topicsPath, topicsDirectoryGrams,"contents", null,outputDir,maxResults);

//        Configuration M3 = new Configuration("version1", "cran","lm", Model.BB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M4 = new Configuration("version1", "cran","lm", Model.DLHHypergeometricDFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M5 = new Configuration("version1", "cran","lm", Model.IFB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M6 = new Configuration("version1", "cran","lm", Model.InExpB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M7 = new Configuration("version1", "cran","lm", Model.InExpC2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M8 = new Configuration("version1", "cran","lm", Model.InL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M9 = new Configuration("version1", "cran","lm", Model.OkapiBM25Model, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M10 = new Configuration("version1", "cran","lm", Model.PL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);


        
        List<Configuration> configurations = new ArrayList<Configuration>();
        //we just need these three configurations because the indexes are the same for all probabilistic models and can be used in vector space because the difference is just an extra index with documents lenghts
//        configurations.add(LM_CRAN);
//        configurations.add(LM_STEMMER_CRAN);
        configurations.add(LM_2_6GRAMS_CRAN);

//        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);
        
        
        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);
        queryConfiguration1.getQueryProperties().setProperty("lgte.default.order", "sc");
        
        QueryConfiguration queryConfiguration1_grams = new QueryConfiguration();
        queryConfiguration1_grams.setForceQE(QEEnum.no);
        queryConfiguration1_grams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contents","0.53f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN5","0.14f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN4","0.11f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN3","0.11f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN2","0.11");
        

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.text);
        queryConfiguration2.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfiguration2.getQueryProperties().setProperty("field.boost.contents","1.0f");
        
        QueryConfiguration queryConfiguration2_grams = new QueryConfiguration();
        queryConfiguration2_grams.setForceQE(QEEnum.text);
        queryConfiguration2_grams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfiguration2_grams.getQueryProperties().setProperty("field.boost.contents","0.53f");
        queryConfiguration2_grams.getQueryProperties().setProperty("field.boost.contentsN5","0.14f");
        queryConfiguration2_grams.getQueryProperties().setProperty("field.boost.contentsN4","0.11f");
        queryConfiguration2_grams.getQueryProperties().setProperty("field.boost.contentsN3","0.11f");
        queryConfiguration2_grams.getQueryProperties().setProperty("field.boost.contentsN2","0.11");

        
        
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1_grams, VS_2_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1_grams, LM_2_6GRAMS_CRAN));

        
        
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2_grams, VS_2_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2_grams, LM_2_6GRAMS_CRAN));

    

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M3));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M4));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M5));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M6));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M7));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M8));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M9));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M10));

        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);
    }

    public static void addFields(List<XmlFieldHandler> xmlFieldHandlers, String field)
    {
    	 XmlFieldHandler xmlTextGlobalFieldHandler = new SimpleXmlFieldHandler("./TEXT",new SimpleFieldFilter(),field);
         XmlFieldHandler xmlTitleGlobalFieldHandler = new SimpleXmlFieldHandler("./TITLE",new MultipleFieldFilter(2),field);
         XmlFieldHandler xmlAuthorGlobalFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new MultipleFieldFilter(2),field);

         //Index to store all fields with out repetitions to be used in summary
         XmlFieldHandler xmlContentSummaryTitleStoreFieldHandler = new SimpleXmlFieldHandler("./TITLE",new SimpleStoreFieldFilter(),"contentStore");
         XmlFieldHandler xmlContentSummaryAuthorStoreFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new SimpleStoreFieldFilter(),"contentStore");
         XmlFieldHandler xmlContentSummaryTextStoreFieldHandler = new SimpleXmlFieldHandler("./TEXT",new SimpleStoreFieldFilter(),"contentStore");

         xmlFieldHandlers.add(xmlTextGlobalFieldHandler);
         xmlFieldHandlers.add(xmlTitleGlobalFieldHandler);
         xmlFieldHandlers.add(xmlAuthorGlobalFieldHandler);
         
         xmlFieldHandlers.add(xmlContentSummaryTitleStoreFieldHandler);
         xmlFieldHandlers.add(xmlContentSummaryAuthorStoreFieldHandler);
         xmlFieldHandlers.add(xmlContentSummaryTextStoreFieldHandler);
    }
}
