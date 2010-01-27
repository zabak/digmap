package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.SearchTopics;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.geotime.queries.StrategyQueryBuilder;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
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
    static String outputFile =    Config.ntcirBase +  File.separator + "runs" + File.separator;
    static String topicsFile =    Config.ntcirBase +  File.separator + "topics" + File.separator + "topics.xml";


    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
        StrategyQueryBuilder strategyQueryBuilderTimeKeys = new StrategyQueryBuilder(topicsFile,true);
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);

        LgteIndexSearcherWrapper searcher;

//        searcher = Config.openMultiSearcher();
//        runBase(null,outputFile + "base_t_keys.txt",searcher,strategyQueryBuilderTimeKeys.baseSimpleIterator(),true,null,false,true);
//        runBase(null,outputFile + "base_t_all.txt",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator(),false,null);
//        searcher.close();

        searcher = Config.openMultiSearcherSentences();
//        runBase(null,outputFile + "base_sentences_t_keys.txt",searcher,strategyQueryBuilderTimeKeys.baseSimpleIterator_sentences(),true,Config.DOC_ID,false);
//        runBase(null,outputFile + "base_t_all.base_sentences_t_all",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator_sentences(),false,Config.DOC_ID,false);
        searcher.close();

        searcher = Config.openMultiSearcher();
        runBase(RunType.Text,outputFile + "filtered_qe_t_keys.txt",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
//        runBase(RunType.Text,outputFile + "filtered_qe_t_all.txt",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator(),false,null,true);
        searcher.close();

        searcher = Config.openMultiSearcherSentences();
        runBase(RunType.Sentences,outputFile + "filtered_qe_sentences_t_keys.txt",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_sentences(),true,Config.DOC_ID,true);
//        runBase(RunType.Sentences,outputFile + "filtered_qe_sentences_t_all.txt",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_sentences(),false,Config.DOC_ID,true);
        searcher.close();

//        searcher = Config.openMultiSearcherForContentsAndSentences();
        runBase(RunType.Comb, outputFile + "filtered_qe_comb_t_keys.txt",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_comb(),true,Config.DOC_ID,true);
        runBase(RunType.Comb, outputFile + "filtered_qe_comb_qe_t_all.txt",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_comb(),false,Config.DOC_ID,true);
//        searcher.close();
    }

    enum RunType
    {
        Text,
        Sentences,
        Comb

    }

    public static void runBase(RunType runType, String outputFile, LgteIndexSearcherWrapper searcher, StrategyQueryBuilder.Iterator iter,boolean keys, String groupField,boolean expansion) throws IOException, DocumentException, ParseException
    {
        System.out.println("####################################");

        StrategyQueryBuilder.Iterator.QueryPackage queryPackage;

        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap,new LgteWhiteSpacesAnalyzer());
        FileOutputStream stream = new FileOutputStream(new File(outputFile));
        OutputFormat outputFormat = new TrecEvalOutputFormatFactory(Config.ID).createNew(stream);
        outputFormat.init("id","id");
        outputFormat.setMaxDocsToFlush(100);
        iter.next();iter.next();
        while((queryPackage = iter.next())!=null)
        {
            QueryConfiguration queryConfigurationBase = new QueryConfiguration();
            queryConfigurationBase.setProperty("bm25.idf.policy","standard");
            queryConfigurationBase.setProperty("bm25.k1","1.2d");
            queryConfigurationBase.setProperty("bm25.b","0.75d");
            System.out.println(queryPackage.getTopicId() + "-----------------------------------");
            System.out.println(queryPackage.filter);
            System.out.println(queryPackage.query);
            LgteQuery lgteQuery = new LgteQuery(queryPackage.query,brokerStemAnalyzer,queryConfigurationBase);
            if(expansion && queryPackage.queryFilters == null)
            {
                System.out.println("Using Query Expansion for topic: " + queryPackage.getTopicId());
                lgteQuery.getQueryParams().setQEEnum(QEEnum.lgte);
                queryConfigurationBase.setProperty("QE.doc.num","5");
                queryConfigurationBase.setProperty("QE.term.num","15"); //doc terms + 15
                queryConfigurationBase.setProperty("QE.decay","0.15");
                if(keys)
                {
                    if(runType == RunType.Text || runType == RunType.Comb)
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY,"0.5");
                    if(runType == RunType.Sentences || runType == RunType.Comb)
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"0.5");
                }
                else
                {
                    if(runType == RunType.Text || runType == RunType.Comb)
                    {
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY,"0.3");
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_RELATIVE,"0.15");
                        queryConfigurationBase.setProperty("field.boost." + Config.T_DURATION,"0.1");
                    }
                    if(runType == RunType.Sentences || runType == RunType.Comb)
                    {
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"0.3");
                        queryConfigurationBase.setProperty("field.boost." + Config.T_POINT_RELATIVE + Config.SEP + Config.SENTENCES,"0.15");
                        queryConfigurationBase.setProperty("field.boost." + Config.T_DURATION + Config.SEP + Config.SENTENCES,"0.1");
                    }
                }
                if(runType == RunType.Text || runType == RunType.Comb)
                {
//                queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_BELONG_TOS_WOEID,"0.2"); todo uncomment
                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_REF_WOEID,"0.3");
                }
                if(runType == RunType.Sentences || runType == RunType.Comb)
                {
//                queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_BELONG_TOS_WOEID + Config.SEP + Config.SENTENCES,"0.2"); todo uncomment
                    queryConfigurationBase.setProperty("field.boost." + Config.G_PLACE_REF_WOEID + Config.SEP + Config.SENTENCES,"0.3");
                }
                org.apache.lucene.search.Query q = LgteQueryParser.lucQE(lgteQuery,queryPackage.query,searcher,queryPackage.filter);
                lgteQuery = new LgteQuery(q,lgteQuery.getQueryParams(),brokerStemAnalyzer);
                System.out.println("New Query: " + q.toString());
            }
            Topic t = new SimpleTopic(queryPackage.getTopicId());
            outputFormat.setTopic(t);
            LgteHits hits = searcher.search(lgteQuery,queryPackage.filter);
            SearchTopics.writeSearch(outputFormat,hits,groupField,outputFile.substring(outputFile.lastIndexOf(File.separator)+1,outputFile.lastIndexOf(".")),500,null);
        }
        outputFormat.close();
    }
}
