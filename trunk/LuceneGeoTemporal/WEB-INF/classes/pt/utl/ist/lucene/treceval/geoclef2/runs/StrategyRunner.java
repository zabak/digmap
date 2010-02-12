package pt.utl.ist.lucene.treceval.geoclef2.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.LgteHits;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.LgteQuery;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.SearchTopics;
import pt.utl.ist.lucene.treceval.geoclef2.index.Config;
import pt.utl.ist.lucene.treceval.geoclef2.queries.StrategyQueryBuilder;
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

    private static final Logger logger = Logger.getLogger(StrategyRunner.class);
    
    static String outputFile06 = Config.geoclefBase +  File.separator + "runs06" + File.separator;
    static String outputFile08 = Config.geoclefBase +  File.separator + "runs08" + File.separator;

    static String topicsFile06 = Config.geoclefBase +  File.separator + "topics" + File.separator + "topics06Formatted.xml";
    static String topicsFile08 = Config.geoclefBase +  File.separator + "topics" + File.separator + "topics08Formatted.xml";

    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
           go(args, topicsFile06, outputFile06);
           go(args, topicsFile08, outputFile08);
    }
    public static void go(String[] args, String topicsFile, String outputFile) throws DocumentException, IOException, ParseException
    {
        new File(outputFile).mkdir();
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);
        int step = 0;
        if(args != null && args.length > 0)
        {
            step = Integer.parseInt(args[0]);
        }
        LgteIndexSearcherWrapper searcher;

        if(step < 1)
        {
            logger.info("#######base");
            searcher = Config.openMultiSearcher();
            run(null,outputFile + "base.xml",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator(), null);
            searcher.close();
        }

        if(step < 2)
        {
            logger.info("#######base_paragraphs");
            searcher = Config.openMultiSearcherParagraphs();
            run(null,outputFile + "base_paragraphs.xml",searcher,strategyQueryBuilderTimeAll.baseSimpleIterator_sentences(), Config.DOC_ID);
            searcher.close();
        }

        if(step < 3)
        {
            logger.info("#######filtered");
            searcher = Config.openMultiSearcher();
            run(RunType.Text,outputFile + "filtered.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator(), null);
            searcher.close();
        }

        if(step < 4)
        {
            logger.info("#######filtered_paragraphs");
            searcher = Config.openMultiSearcherParagraphs();
            run(RunType.Sentences,outputFile + "filtered_paragraphs.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_sentences(), Config.DOC_ID);
            searcher.close();
        }


        if(step < 5)
        {
            logger.info("#######filtered_extension");
            searcher = Config.openMultiSearcher();
            run(RunType.Text,outputFile + "filtered_extension.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator(), null);
            searcher.close();
        }

        if(step < 6)
        {
            logger.info("#######filtered_extension_paragraphs");
            searcher = Config.openMultiSearcherParagraphs();
            run(RunType.Sentences,outputFile + "filtered_extension_paragraphs.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_sentences(), Config.DOC_ID);
            searcher.close();
        }

        logger.info("#######filtered_extension_comb");
        searcher = Config.openMultiSearcherForContentsAndParagraphs();
        run(RunType.Comb, outputFile + "filtered_extension_comb.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_comb(), Config.DOC_ID);
        searcher.close();
    }

    enum RunType
    {
        Text,
        Sentences,
        Comb
    }

    public static void run(RunType runType, String outputFile, LgteIndexSearcherWrapper searcher, StrategyQueryBuilder.Iterator iter, String groupField) throws IOException, DocumentException, ParseException
    {
        logger.info("####################################");

        StrategyQueryBuilder.Iterator.QueryPackage queryPackage;

        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap,new LgteWhiteSpacesAnalyzer());
        FileOutputStream stream = new FileOutputStream(new File(outputFile));
        OutputFormat outputFormat = new TrecEvalOutputFormatFactory(Config.ID).createNew(stream);
        outputFormat.init("id","id");
        outputFormat.setMaxDocsToFlush(100);

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