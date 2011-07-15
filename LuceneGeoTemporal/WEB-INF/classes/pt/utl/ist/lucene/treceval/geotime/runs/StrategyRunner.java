package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.SearchTopics;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.queries.StrategyQueryBuilder;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.RunIdOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.SimpleTopic;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 26/Jan/2010
 * @time 20:15:29
 * @email machadofisher@gmail.com
 */
public class StrategyRunner {

    //PARA VERIFICAR SE ESTA A USAR A DESCRICAO ONLY VERIFICAR
    //esta conf no file pt.utl.ist.lucene.utils.queries.QueryProcessor
    // public static boolean DESC_ONLY = false;
    private static final Logger logger = Logger.getLogger(StrategyRunner.class);
    static String outputFile =    Config.ntcirBase +  File.separator + "runs2" + File.separator;
    static String topicsFile =    Config.ntcirBase +  File.separator + "topics" + File.separator + "topics.xml";



    static String run1 = "DOC Granularity BM25 with BaseFilters to exclude documents without time or geographic references";
    static String run2 = "SENTENCE Granularity BM25 with BaseFilters to exclude sentences without time or geographic references";
    static String run3 = "DOC Granularity BM25 with BaseFilters and Query Filters for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";
    static String run4 = "SENTENCE Granularity BM25 with BaseFilters and Query Filters for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";
    static String run5 = "COMB Granularity BM25(0.7*sentence + 0.3*doc)  with BaseFilters and Query Filters (only at doc granularity level) for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";

//    static String run3_D = "DOC Granularity BM25 with BaseFilters and Query Filters for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";
//    static String run5_D = "COMB Granularity BM25(0.7*sentence + 0.3*doc)  with BaseFilters and Query Filters (only at doc granularity level) for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";

    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
        StrategyQueryBuilder strategyQueryBuilderTimeKeys = new StrategyQueryBuilder(topicsFile,true);
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);
        int step = 1;
        if(args != null && args.length > 0)
        {
            step = Integer.parseInt(args[0]);
        }
        LgteIndexSearcherWrapper searcher;

//        if(step < 1)
//        {
//            logger.info("#######base");
//            searcher = Config.openMultiSearcher();
////            runBase(run1,null,outputFile + "base_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseSimpleIterator(),true,null,false);
//            runBase(run1,null,outputFile + "base_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator(),false,null,false);
//            searcher.close();
//        }
//
//        if(step < 2)
//        {
//            logger.info("#######base_sentences");
//            searcher = Config.openMultiSearcherSentences();
////            runBase(run2,null,outputFile + "base_sentences_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseSimpleIterator_sentences(),true,Config.DOC_ID,false);
//            runBase(run2,null,outputFile + "base_sentences_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator_sentences(),false,Config.DOC_ID,false);
//            searcher.close();
//        }

        if(step < 3)
        {
            logger.info("#######filtered_qe");
            searcher = Config.openMultiSearcher();
//            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator(),false,null,true);
            searcher.close();
        }
//
//        if(step < 4)
//        {
//            logger.info("#######filtered_qe_sentences");
//            searcher = Config.openMultiSearcherSentences();
////            runBase(run4,RunType.Sentences,outputFile + "filtered_qe_sentences_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_sentences(),true,Config.DOC_ID,true);
//            runBase(run4,RunType.Sentences,outputFile + "filtered_qe_sentences_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_sentences(),false,Config.DOC_ID,true);
//            searcher.close();
//        }
//
//        if(step < 5)
//        {
//            logger.info("#######filtered_qe_comb");
//            searcher = Config.openMultiSearcherForContentsAndSentences();
////            runBase(run5,RunType.Comb, outputFile + "filtered_qe_comb_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_comb(),true,Config.DOC_ID,true);
//            runBase(run5,RunType.Comb, outputFile + "filtered_qe_comb_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_comb(),false,Config.DOC_ID,true);
//            searcher.close();
//        }


    }

    enum RunType
    {
        Text,
        Sentences,
        Comb
    }

    public static void runBase(String desc, RunType runType, String outputFile, LgteIndexSearcherWrapper searcher, StrategyQueryBuilder.Iterator iter,boolean keys, String groupField,boolean expansion) throws IOException, DocumentException, ParseException
    {
        logger.info("####################################");

        StrategyQueryBuilder.Iterator.QueryPackage queryPackage;

        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap,new LgteWhiteSpacesAnalyzer());
        new File(outputFile).getParentFile().mkdirs();
        FileOutputStream stream = new FileOutputStream(new File(outputFile));
        OutputFormat outputFormat = new TrecEvalOutputFormatFactory(Config.ID).createNew(stream);
//        OutputFormat outputFormat = new RunIdOutputFormatFactory(Config.ID, outputFile.substring(outputFile.lastIndexOf(File.separator)+1,outputFile.lastIndexOf(".")),desc).createNew(stream);
        outputFormat.init("id","id");
        outputFormat.setMaxDocsToFlush(100);

        int i = 1;
        while((queryPackage = iter.next())!=null)
        {
            QueryConfiguration queryConfigurationBase = new QueryConfiguration();
            queryConfigurationBase.setProperty("bm25.idf.policy","standard");
            queryConfigurationBase.setProperty("bm25.k1","1.2d");
            queryConfigurationBase.setProperty("bm25.b","0.75d");
            if(runType == RunType.Comb)
                queryConfigurationBase.setProperty("index.tree","true");
            logger.info(queryPackage.getTopicId() + "-----------------------------------");
            logger.info(queryPackage.filter);
            logger.info(queryPackage.query);
            LgteQuery lgteQuery = new LgteQuery(queryPackage.query,brokerStemAnalyzer,queryConfigurationBase);
            if(expansion && queryPackage.queryFilters == null)
            {
                logger.info("Using Query Expansion for topic: " + queryPackage.getTopicId());
                lgteQuery.getQueryParams().setQEEnum(QEEnum.lgte);
                queryConfigurationBase.setProperty("QE.doc.num","5");
                queryConfigurationBase.setProperty("QE.term.num","15"); //doc terms + 15
                queryConfigurationBase.setProperty("QE.decay","0.15");
                if(keys)
                {
                    if(runType == RunType.Text || runType == RunType.Comb)
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY,Config.keyTimeFactor);
                    if(runType == RunType.Sentences || runType == RunType.Comb)
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,Config.keyTimeFactor);
                }
                else
                {
                    if(runType == RunType.Text || runType == RunType.Comb)
                    {
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY,     Config.keyTimeFactor);
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_RELATIVE,Config.relativeTimeFactor);
                        queryConfigurationBase.setProperty("field.boost." + Config.T_DURATION,      Config.durationTimeFactor);
                    }
                    if(runType == RunType.Sentences || runType == RunType.Comb)
                    {
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,     Config.keyTimeFactor);
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_RELATIVE + Config.SEP + Config.SENTENCES,Config.relativeTimeFactor);
                        queryConfigurationBase.setProperty("field.boost." + Config.T_DURATION + Config.SEP + Config.SENTENCES,      Config.durationTimeFactor);
                    }
                }
                if(runType == RunType.Text || runType == RunType.Comb)
                {
//                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_BELONG_TOS_WOEID,"0.2");     dont use here for expasion
                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_REF_WOEID,"1");
                }
                if(runType == RunType.Sentences || runType == RunType.Comb)
                {
//                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_BELONG_TOS_WOEID + Config.SEP + Config.SENTENCES,"0.2");   dont use here for expasion
                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_REF_WOEID + Config.SEP + Config.SENTENCES,"1");
                }
                org.apache.lucene.search.Query q = LgteQueryParser.lucQE(lgteQuery,queryPackage.query,searcher,queryPackage.filter);
                lgteQuery = new LgteQuery(q,lgteQuery.getQueryParams(),brokerStemAnalyzer);
                logger.info("New Query: " + q.toString());
            }
            Topic t = new SimpleTopic(queryPackage.getTopicId());
            outputFormat.setTopic(t);
            LgteHits hits = searcher.search(lgteQuery,queryPackage.filter);
            try{
                if(hits.length() > 0)
                    logger.info(searcher.explain(lgteQuery,hits.id(0)));
                if(hits.length() > 1)
                    logger.info(searcher.explain(lgteQuery,hits.id(1)));
                if(hits.length() > 2)
                    logger.info(searcher.explain(lgteQuery,hits.id(2)));
                if(hits.length() > 3)
                    logger.info(searcher.explain(lgteQuery,hits.id(3)));
                if(hits.length() > 4)
                    logger.info(searcher.explain(lgteQuery,hits.id(4)));
                if(hits.length() > 5)
                    logger.info(searcher.explain(lgteQuery,hits.id(5)));
                if(hits.length() > 6)
                    logger.info(searcher.explain(lgteQuery,hits.id(6)));
            }catch(Throwable e)
            {
                logger.error(e,e);
            }
            SearchTopics.writeSearch(outputFormat,hits,groupField,outputFile.substring(outputFile.lastIndexOf(File.separator)+1,outputFile.lastIndexOf(".")),Config.outputDocs,null);

        }
        outputFormat.close();
    }
}
