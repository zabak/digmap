package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.*;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class BaseLine {

    private static final Logger logger = Logger.getLogger(BaseLine.class);

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

        xmlTopicFieldHandlers.add(new SimpleXmlFieldHandler("./QUESTION/text()",new SimpleFieldFilter(),"contents"));
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//TOPIC","./@ID",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);


        /*******************************************
         //Configurations
         ********************************************/
        //maxResults in output File per topic;
        int maxResults = 500;
        Configuration BM25_STEMMER = new Configuration("version1", "geotime","bm25", Model.OkapiBM25Model, IndexCollections.en.getAnalyzerWithStemming(),null,null,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);



        LgteSort lgteSort = new LgteSort(new SortField[] {new SortField(Config.GEO_AND_TEMPORAL_INDEXED),new SortField(Config.GEO_OR_TEMPORAL_INDEXED)});
        TermsFilter filter1 = new TermsFilter();
        filter1.addTerm(new Term(Config.GEO_INDEXED,"true"));

        TermsFilter filter2 = new TermsFilter();
        filter2.addTerm(new Term(Config.GEO_INDEXED,"false"));


        /***
         * Search Configurations
         */
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","standard");
        queryConfiguration.setProperty("bm25.k1","1.2d");
        queryConfiguration.setProperty("bm25.b","0.75d");
        searchConfigurations.add(new SearchConfiguration(queryConfiguration, BM25_STEMMER,1,null,null));


        LgteIndexSearcherWrapper searcherMulti = Config.openMultiSearcher();

        try {
            LgteHits hits = searcherMulti.search("GEO_INDEXED:true");
            System.out.println("GEO_INDEXED:true = " + hits.length());

            hits = searcherMulti.search("GEO_INDEXED:false");
            System.out.println("GEO_INDEXED:false = " + hits.length());

            hits = searcherMulti.search("TEMPORAL_INDEXED:true OR TEMPORAL_INDEXED:false",filter1);
            System.out.println("GEO_INDEXED:true (FILTER) = " + hits.length());

            hits = searcherMulti.search("TEMPORAL_INDEXED:true OR TEMPORAL_INDEXED:false",filter2);
            System.out.println("GEO_INDEXED:false (FILTER) = " + hits.length());
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        //Search Topics Runs to submission
//        SearchTopics.search(searchConfigurations,searcherMulti);

        searcherMulti.close();

//        SearchTopics.evaluateMetrics(searchConfigurations,assessements);
//        SearchTopics.createRunPackage(searchConfigurations.get(0).getConfiguration().getOutputDir(),searchConfigurations);
    }
}
