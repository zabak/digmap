package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
public class GeoClefExample
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        String collectionPathPt = "D:\\Projectos\\coleccoesIR\\geoclef\\pt";
        String collectionPathEn = "D:\\Projectos\\coleccoesIR\\geoclef\\en";

        String topics08PathPt = Globals.DATA_DIR + "\\geoclef08pt\\topics";
        String output08DirPt = Globals.DATA_DIR + "\\geoclef08pt\\output";

        String topics08PathEn = Globals.DATA_DIR + "\\geoclef08en\\topics";
        String output08DirEn = Globals.DATA_DIR + "\\geoclef08en\\output";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);

        /**
         * In this file we create 2 types of indexes, one for portuguese collections and topics and another to english ones
         *
         *  We gone create the follow indexes with listed configurations:
         *
         *    1.1 - Portuguese Index (Folha and Publico collections) with no stemming
         *    1.2 - Portuguese Index (Folha and Publico collections) with Potter Stemming using Snowball
         *    2.1 - English Index (Folha and Publico collections) with no stemming
         *    2.2 - English Index (Gh-95 and Latin-en collections) with Potter Stemming using Snowball
         *
         *    We gone experiment follow runs in each index
         *      a) Vector Space Model
         *      b) Language Model
         *      c) Vector Space Model + Rochio Query Expansion
         *      d) Language Model + Rochio Query Expansion
         *
         *   A total of 16 runs
         *
         */
       /**

         gh-95 example
         <DOC>
            <DOCNO>GH950103-000000</DOCNO>
            <DOCID>GH950103-000000</DOCID>
            <DATE>950103</DATE>
            <HEADLINE>Chance of being a victim of crime is less than you think</HEADLINE>
            <EDITION>3</EDITION>
            <PAGE>3</PAGE>
            <RECORDNO>980549733</RECORDNO>
            <TEXT>
                  PEOPLE greatl...
            </TEXT>
         </DOC>
         latin-en example
         <DOC>
            <DOCNO> LA012394-0086 </DOCNO>
            <DOCID> 006365 </DOCID>
            <SOURCE><P>Los Angeles Times</P></SOURCE>
            <DATE><P>January 23, 1994, Sunday, Ventura West Edition</P></DATE>
            <SECTION><P>Metro; Part B; Page 1; Column 2</P></SECTION>
            <LENGTH><P>4337 words</P></LENGTH>
            <HEADLINE><P>
                EARTHQUAKE: THE LONG ROAD BACK; DIARY OF A DISASTER; RESIDENTS STRUGGLING TO
                PUT LIVES BACK IN ORDER ONE DAY AT A TIME</P>
            </HEADLINE>
            <BYLINE><P>By STEPHANIE SIMON, TIMES STAFF WRITER</P></BYLINE>
            <TEXT>
                <P>DAY 1</P>
                <P>Darkness. Then, abruptly, a jolt. A crash. Rumbling, screaming, shattering,tumbling. Panic. And again, darkness.</P>
                ...
            </TEXT>
         </DOC>
         */

        /*******************************************************************************
         * Portuguese Index Folha and Publico
         * we gone use 
         *
         * FOLHA EXAMPLE
         * <DOC>
                <DOCNO>FSP940101-131</DOCNO>
                <DOCID>FSP940101-131</DOCID>
                <DATE>940101</DATE>
                <TEXT>
                    Livro retoma a conversa com Otto Lara
                    ...
                </TEXT>
            </DOC>

            PUBLICO EXAMPLE
            <DOC>
                <DOCNO>PUBLICO-19940101-001</DOCNO>
                <DOCID>PUBLICO-19940101-001</DOCID>
                <DATE>19940101</DATE>
                <CATEGORY>Cultura</CATEGORY>
                <AUTHOR>MRMS</AUTHOR>
                <TEXT>
                Crítica de Música
                ...
                </TEXT>
            </DOC>
                        **/

        //Global Search Index
        XmlFieldHandler xmlTextGlobalFieldHandler = new SimpleXmlFieldHandler("./TEXT",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlTitleGlobalFieldHandler = new SimpleXmlFieldHandler("./TITLE",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlAuthorGlobalFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new MultipleFieldFilter(2),"contents");

        //Special Indexes for Advanced Search
        XmlFieldHandler xmlAuthorFieldHandler = new SimpleXmlFieldHandler("./AUTHOR",new SimpleFieldFilter(),"author");
        XmlFieldHandler xmlTitleFieldHandler = new SimpleXmlFieldHandler("./TITLE",new SimpleFieldFilter(), Globals.DOCUMENT_TITLE);

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
         *     <description>what similarity laws must be obeyed when constructing aeroelastic priors of heated high speed aircraft .</description>
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

        Configuration VS_CRAN = new Configuration("version1", "cran","lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPathPt,collectionsDirectory,topics08PathPt, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirPt,maxResults);
        Configuration LM_CRAN = new Configuration("version1", "cran","lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPathPt,collectionsDirectory,topics08PathPt, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirPt,maxResults);
        Configuration VS_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPathPt,collectionsDirectory,topics08PathPt, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirPt,maxResults);
        Configuration LM_STEMMER_CRAN = new Configuration("version1", "cran","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPathPt,collectionsDirectory,topics08PathPt, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirPt,maxResults);

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
        //we just need these two configurations because the lm and lmstem indexes are the same for all probabilistic priors and can be used in vector space because the diference is just an extra index with documents lenght

//        configurations.add(VS_3_6GRAMS_CRAN);
//        configurations.add(LM_3_6GRAMS_FRONT_CRAN);
        configurations.add(LM_CRAN);
        configurations.add(LM_STEMMER_CRAN);

        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.text);
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_3_6GRAMS_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_3_6GRAMS_FRONT_CRAN));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_3_6GRAMS_FRONT_CRAN));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_CRAN));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_CRAN));
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
