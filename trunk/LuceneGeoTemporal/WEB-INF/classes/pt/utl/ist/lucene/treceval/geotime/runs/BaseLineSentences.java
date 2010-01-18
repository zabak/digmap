package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.index.Term;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.*;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.geotime.index.IndexContents;
import pt.utl.ist.lucene.treceval.geotime.index.IndexSentences;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.utils.DataCacher;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class BaseLineSentences {

    private static final Logger logger = Logger.getLogger(BaseLineSentences.class);

    public static void main(String [] args) throws DocumentException, IOException {

//        Globals.INDEX_DIR = args[0];
//        Globals.DATA_DIR = args[1];

        String topicsPath =   "D:\\Servidores\\DATA\\ntcir\\topics";
        String outputDir =    "D:\\Servidores\\DATA\\ntcir\\runs";
        String assessements = "D:\\Servidores\\DATA\\ntcir\\assessements\\qrels";


        new File(outputDir).mkdirs();

        /*******************************************
         //TOPICS
         ********************************************/
        /**
         * <TOPIC ID="ACLIA1-JA-T119" type="DP">
         <QUESTION LANG="EN" >
         <![CDATA[ What is the controversy surrounding the use of the Stealth Fighter in Yugoslavia?]]>
         </QUESTION>
         <QUESTION LANG="JA">
         <![CDATA[ ???????????????????????????????????]]>
         </QUESTION>
         <NARRATIVE LANG="EN">
         <![CDATA[ I would like to know about the dates and times of events and places in which there was a controversy surrounding the use of the Stealth Fighter in Yugoslavia. ]]>
         </NARRATIVE>
         <NARRATIVE LANG="JA">
         <![CDATA[????????????????????????????????????????]]>
         </NARRATIVE>
         </TOPIC>
         */

        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();

        xmlTopicFieldHandlers.add(new SimpleXmlFieldHandler("./QUESTION/text()",new SimpleFieldFilter(),Config.SENTENCES));
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//TOPIC","./@ID",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);


        /*******************************************
         //Configurations
         ********************************************/
        //maxResults in output File per topic;
        int maxResults = 500;
        Configuration BM25_STEMMER = new Configuration("version1", "geotime","bm25", Model.OkapiBM25Model, IndexCollections.en.getAnalyzerWithStemming(),null,null,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);

//        LgteSort lgteSort = new LgteSort(new SortField[] {new SortField(Config.GEO_AND_TEMPORAL_INDEXED),new SortField(Config.GEO_OR_TEMPORAL_INDEXED)});



        /***
         * Search Configurations
         */
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        QueryConfiguration queryConfigurationBase = new QueryConfiguration();
        queryConfigurationBase.setProperty("bm25.idf.policy","standard");
        queryConfigurationBase.setProperty("bm25.k1","1.2d");
        queryConfigurationBase.setProperty("bm25.b","0.75d");
        queryConfigurationBase.setProperty("index.tree","true");
        searchConfigurations.add(new SearchConfiguration(queryConfigurationBase, BM25_STEMMER,0,null,null));

        /***
         * Search Configurations
         */
        TermsFilter filterGeoOrTemp = new TermsFilter();
        filterGeoOrTemp.addTerm(new Term(Config.S_GEO_OR_TEMPORAL_INDEXED,"true"));
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setProperty("bm25.idf.policy","standard");
        queryConfiguration1.setProperty("bm25.k1","1.2d");
        queryConfiguration1.setProperty("bm25.b","0.75d");
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, BM25_STEMMER,1,null,filterGeoOrTemp));


        TermsFilter filterGeoAndTemp = new TermsFilter();
        filterGeoAndTemp.addTerm(new Term(Config.S_GEO_AND_TEMPORAL_INDEXED,"true"));
        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setProperty("bm25.idf.policy","standard");
        queryConfiguration2.setProperty("bm25.k1","1.2d");
        queryConfiguration2.setProperty("bm25.b","0.75d");
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, BM25_STEMMER,2,null,filterGeoAndTemp));


        LgteIndexSearcherWrapper searcherMulti = Config.openMultiSearcherSentences();

//        DataCacher dataCacher = new DataCacher();
//        dataCacher.loadFromFile(IndexSentences.indexPath + File.separator + "docid.cache",true);
//        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations,searcherMulti,null);

        searcherMulti.close();

//        SearchTopics.evaluateMetrics(searchConfigurations,assessements);
//        SearchTopics.createRunPackage(searchConfigurations.get(0).getConfiguration().getOutputDir(),searchConfigurations);
    }
    public static long totalTimeTree = 0;
}
