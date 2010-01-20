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
import pt.utl.ist.lucene.treceval.geotime.index.IndexContents;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.DataCacher;
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

        String topicsPath =   Config.ntcirBase +  File.separator + "topics" + File.separator + "GeoTime-EN-JA-Topics_ntcirEdited_10.xml";
        String outputDir =    Config.ntcirBase +  File.separator + "runs";
        String assessements = Config.ntcirBase +  File.separator + "assessements" + File.separator + "qrels";


        new File(outputDir).mkdirs();

        /*******************************************
         //TOPICS
         ********************************************/
        /**
         *   <TOPIC ID="GeoTime-0001">
                <DESCRIPTION LANG="EN">
                <![CDATA[When and where did Astrid Lindgren die?]]>
                </DESCRIPTION>
                <NARRATIVE LANG="EN">
                <![CDATA[The user wants to know when and in what city the children's author Astrid Lindgren died.]]>
                </NARRATIVE>
            </TOPIC>
         */

        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();

        xmlTopicFieldHandlers.add(new SimpleXmlFieldHandler("./DESCRIPTION[@LANG='EN']/text()",new MultipleFieldFilter(2),"contents"));
        xmlTopicFieldHandlers.add(new SimpleXmlFieldHandler("./NARRATIVE [@LANG='EN']/text()",new SimpleFieldFilter(),"contents"));
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
        TermsFilter filterGeoAndTemp = new TermsFilter();
        filterGeoAndTemp.addTerm(new Term(Config.S_GEO_AND_TEMPORAL_INDEXED,"true"));
        searchConfigurations.add(new SearchConfiguration(queryConfigurationBase, BM25_STEMMER,0,null,filterGeoAndTemp));

//        /***
//         * Search Configurations
//         */
//        TermsFilter filterGeoOrTemp = new TermsFilter();
//        filterGeoOrTemp.addTerm(new Term(Config.S_GEO_OR_TEMPORAL_INDEXED,"true"));
//        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
//        queryConfiguration1.setProperty("bm25.idf.policy","standard");
//        queryConfiguration1.setProperty("bm25.k1","1.2d");
//        queryConfiguration1.setProperty("bm25.b","0.75d");
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, BM25_STEMMER,1,null,filterGeoOrTemp));
//
//
//        filterGeoAndTemp = new TermsFilter();
//        filterGeoAndTemp.addTerm(new Term(Config.S_GEO_AND_TEMPORAL_INDEXED,"true"));
//        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
//        queryConfiguration2.setProperty("bm25.idf.policy","standard");
//        queryConfiguration2.setProperty("bm25.k1","1.2d");
//        queryConfiguration2.setProperty("bm25.b","0.75d");
////        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, BM25_STEMMER,2,null,filterGeoAndTemp));


        LgteIndexSearcherWrapper searcherMulti = Config.openMultiSearcher();

        DataCacher dataCacher = new DataCacher();
        dataCacher.loadFromFile(IndexContents.indexPath + File.separator + "docid.cache",true);
//        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations,searcherMulti,dataCacher);

        searcherMulti.close();

//        SearchTopics.evaluateMetrics(searchConfigurations,assessements);
//        SearchTopics.createRunPackage(searchConfigurations.get(0).getConfiguration().getOutputDir(),searchConfigurations);
    }
}
