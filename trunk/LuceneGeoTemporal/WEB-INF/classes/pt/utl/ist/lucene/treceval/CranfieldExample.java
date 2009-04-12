package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.IndexSearcher;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
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
public class CranfieldExample
{

    private static final Logger logger = Logger.getLogger(CranfieldExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {

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

        //Global Search Index
        XmlFieldHandler xmlTextGlobalFieldHandler = new SimpleXmlFieldHandler("./TEXT",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlTitleGlobalFieldHandler = new SimpleXmlFieldHandler("./TITLE",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlAuthorGlobalFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new MultipleFieldFilter(2),"contents");

        //Special Indexes for Advanced Search
        XmlFieldHandler xmlAuthorFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new SimpleFieldFilter(),"author");
        XmlFieldHandler xmlTitleFieldHandler = new SimpleXmlFieldHandler("./TITLE",new SimpleFieldFilter(),pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE);

        //Index to store all fields with out repetitions to be used in summary
        XmlFieldHandler xmlContentSummaryTitleStoreFieldHandler = new SimpleXmlFieldHandler("./TITLE",new SimpleStoreFieldFilter(),"contentStore");
        XmlFieldHandler xmlContentSummaryAuthorStoreFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new SimpleStoreFieldFilter(),"contentStore");
        XmlFieldHandler xmlContentSummaryTextStoreFieldHandler = new SimpleXmlFieldHandler("./TEXT",new SimpleStoreFieldFilter(),"contentStore");

        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlTextGlobalFieldHandler);
        xmlFieldHandlers.add(xmlTitleGlobalFieldHandler);
        xmlFieldHandlers.add(xmlAuthorGlobalFieldHandler);
        xmlFieldHandlers.add(xmlAuthorFieldHandler);
        xmlFieldHandlers.add(xmlTitleFieldHandler);
        xmlFieldHandlers.add(xmlContentSummaryTitleStoreFieldHandler);
        xmlFieldHandlers.add(xmlContentSummaryAuthorStoreFieldHandler);
        xmlFieldHandlers.add(xmlContentSummaryTextStoreFieldHandler);

        ResourceHandler resourceHandler = new XmlResourceHandler("//DOC","DOCNO",xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /**
         * Now let's create a Topics processor
         * The principle is the same, we need a directoryHandler
         * and we need to give it a ResourceHandler
         *  Example
         *  <top>
         *     <num>1</num>
         *     <description>what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .</description>
         *  </top>
         */
        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//top","./num",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 500;

        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser

        Configuration VS_CRAN = new Configuration("version1", "cran","lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_CRAN = new Configuration("version1", "cran","lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);

        Configuration VS_3_5GRAMS_CRAN = new Configuration("version1", "cran","lmstem3_5grams", Model.VectorSpaceModel, IndexCollections.n2_6gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
//        Configuration VS_3_6GRAMS_CRAN = new Configuration("version1", "cran","lmstem3_6grams", Model.VectorSpaceModel, IndexCollections.n3_6gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
//        Configuration LM_3_6GRAMS_CRAN = new Configuration("version1", "cran","lmstem3_6grams", Model.LanguageModel, IndexCollections.n3_6gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
//
//        Configuration VS_3_6GRAMS_FRONT_CRAN = new Configuration("version1", "cran","lmstem3_6gramsFront", Model.VectorSpaceModel, IndexCollections.n3_6gramsFrontEdjeStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
//        Configuration LM_3_6GRAMS_FRONT_CRAN = new Configuration("version1", "cran","lmstem3_6gramsFront", Model.LanguageModel, IndexCollections.n3_6gramsFrontEdjeStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);



//        Configuration VS_4GRAMS_CRAN = new Configuration("version1", "cran","lmstem4grams", Model.VectorSpaceModel, IndexCollections.enStop4gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
//        Configuration LM_4GRAMS_CRAN = new Configuration("version1", "cran","lmstem4grams", Model.LanguageModel, IndexCollections.enStop4gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);

//        Configuration M3 = new Configuration("version1", "cran","lm", Model.BB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M4 = new Configuration("version1", "cran","lm", Model.DLHHypergeometricDFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M5 = new Configuration("version1", "cran","lm", Model.IFB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M6 = new Configuration("version1", "cran","lm", Model.InExpB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M7 = new Configuration("version1", "cran","lm", Model.InExpC2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M8 = new Configuration("version1", "cran","lm", Model.InL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M9 = new Configuration("version1", "cran","lm", Model.OkapiBM25Model, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M10 = new Configuration("version1", "cran","lm", Model.PL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        //we just need these two configurations because the lm and lmstem indexes are the same for all probabilistic models and can be used in vector space because the diference is just an extra index with documents lenght

//        configurations.add(VS_3_6GRAMS_CRAN);
//        configurations.add(LM_3_6GRAMS_FRONT_CRAN);

//        configurations.add(LM_CRAN);
  //      configurations.add(LM_STEMMER_CRAN);
        configurations.add(VS_3_5GRAMS_CRAN);

        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

//        MultiSearcher ms = new MultiSearcher(IndexSearcher)
        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);
        queryConfiguration1.getQueryProperties().setProperty("lgte.default.order", "sc");


        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.text);
        queryConfiguration2.getQueryProperties().setProperty("lgte.default.order", "sc");
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_3_5GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_3_6GRAMS_FRONT_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_3_6GRAMS_FRONT_CRAN));

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_3_6GRAMS_FRONT_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_3_6GRAMS_FRONT_CRAN));



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

}
