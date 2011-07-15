package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.SearchTopics;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.RunIdOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.SimpleTopic;
import pt.utl.ist.lucene.utils.queries.StrategyQueryBuilder;

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

/**
 *
 *  Coisas as quais e necessario ter atencao
 *  Para usar o BM25Normalized e necessario fazer setModel depois de cada pesquisa
 *  ou recriar o Searcher para ele limpar os maxFieldsValues e meter o step novamente a
 *  BM25Normalized em vez de fica no Step2
 *
 *  O indice de metricas funciona muito bem com modelos de indices distribuidos
 *  esta validado mas e necessario colocar filter:none nas pesquisas e garantir que
 *  se usarmos o normalized o Searcher nao mete o Sort que vem do FilterOrchestrator
 *  no primeiro Step se nao os scores de texto ficam em cache
 *
 *  E necesario ter em atencao os pontos das Boxes Geographicas
 *
 *         Y
 *         ^       Ponto2(X,Y)
 *         |
 *  Ponto1(X,Y)   |------------> X
 *
 *  Foi colocado um getNorth getWest getSouth e getEast no PlaceMakerDocument
 *
 * O Config.openMultiSearcher2011 tem uma expressao regular * para os indices nao encontrados
 * o que vai usar os indices de Pesquisa Metrica GeoTemporal
 *
 * Podemos usar oBM25Normalized para as metricas e podemos meter outros filtros sem problemas
 *
 *
 *
 *
 *
 */
public class StrategyRunner2011 {

    //PARA VERIFICAR SE ESTA A USAR A DESCRICAO ONLY VERIFICAR
    //esta conf no file pt.utl.ist.lucene.utils.queries.QueryProcessor
    // public static boolean DESC_ONLY = false;
    private static final Logger logger = Logger.getLogger(StrategyRunner2011.class);
    static String outputFile =    Config.ntcirBase +  File.separator + "runs2" + File.separator;
    static String topicsFile =    Config.ntcirBase +  File.separator + "topics" + File.separator + "XSL OutputWikipedia.xml";



    static String run1 = "BASE RUN SCORE BM25 DESC ONLY plus GEOTIME BASIC FILTER";
    static String run2 = "BASE RUN SCORE BM25 DESC ONLY plus FILTERS FROM QUERY PARSING GEO TIME";
    static String run3 = "GOOGLE and WIKIPEDIA BEST GEOTIME TERMS FOR QUERY EXPANSION";
    static String run4 = "GOOGLE and WIKIPEDIA BEST GEOTIME TERMS AS PLACES+DATES FILTERING";
//    static String run4 = "WIKIPEDIA QUERY EXPANSION AND PLACES+DATES FILTERING";
    static String run5 = "DISTANCE(GEO + TIME) SIGMOIDS + BM25 WHEN THERE ARE NO DATES OR PLACES IN TOPIC";

//    static String run3_D = "DOC Granularity BM25 with BaseFilters and Query Filters for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";
//    static String run5_D = "COMB Granularity BM25(0.7*sentence + 0.3*doc)  with BaseFilters and Query Filters (only at doc granularity level) for places, times, timeformats and placetypes or Query Expansion if no filters were defined in the topic";

    static boolean showOnlyQueries = false;
    static String startTopic = "";
    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
//        StrategyQueryBuilder strategyQueryBuilderTimeKeys = new StrategyQueryBuilder(topicsFile,true);
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);
        int step = 0;

        if(args != null && args.length > 0)
        {
            step = Integer.parseInt(args[0]);
        }
        if(args != null && args.length > 1)
        {
            showOnlyQueries = Boolean.parseBoolean(args[1]);
        }

        if(args != null && args.length > 2)
        {
            startTopic = args[2];
        }

        LgteIndexSearcherWrapper searcher;

        if(step < 1)
        {
            logger.info(run1);
//            searcher = Config.openMultiSearcher();
//            runBase(run1,null,outputFile + "base_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseSimpleIterator(),true,null,false);
            runBase(run1,null,outputFile + "base.xml",null,strategyQueryBuilderTimeAll.baseSimpleIterator(),false,null,false,startTopic);
//            searcher.close();
            startTopic = "";
        }




        if(step < 2)
        {
            logger.info("#######filtered_qe");
//            searcher = Config.openMultiSearcher();
//            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
            runBase(run2, RunType.Text,outputFile + "filtered_qe_t_all.xml",null,strategyQueryBuilderTimeAll.baseFilteredIterator(),false,null,true,startTopic);
//            searcher.close();
            startTopic = "";
        }

        if(step < 3)
        {
            logger.info("#######wikipedia_expansion");
//            searcher = Config.openMultiSearcher();
//            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
            runBase(run3, RunType.Text,outputFile + "wikipedia_qe.xml",null,strategyQueryBuilderTimeAll.baseWikipediaIterator(),false,null,false,startTopic);
//            searcher.close();
            startTopic = "";
        }

        if(step < 4)
        {
            logger.info("#######wikipedia_expansion_filter");
//            searcher = Config.openMultiSearcher();
//            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
            runBase(run4, RunType.Text,outputFile + "wikipedia_filtered.xml",null,strategyQueryBuilderTimeAll.baseWikipediaFilterIterator(),false,null,false,startTopic);
//            searcher.close();
            startTopic = "";
        }

        if(step < 5)
        {
            logger.info("#######geo_time_metric");
//            searcher = Config.openMultiSearcher();
//            runBase(run3,RunType.Text,outputFile + "filtered_qe_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator(),true,null,true);
            runBase(run5, RunType.Text,outputFile + "geo_time_metric.xml",null,strategyQueryBuilderTimeAll.baseMetricsIterator(),false,null,false,startTopic);
//            searcher.close();
            startTopic = "";
        }
//
//        if(step < 4)
//        {
//            logger.info("#######filtered_qe_sentences");
//            searcher = Config.openMultiSearcherSentences();
////            runBase(run4,RunType.Sentences,outputFile + "filtered_qe_sentences_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_sentences(),true,Config.DOC_ID,true,startTopic);
//            runBase(run4,RunType.Sentences,outputFile + "filtered_qe_sentences_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_sentences(),false,Config.DOC_ID,true,startTopic);
//            searcher.close();
//        startTopic = "";
//        }
//
//        if(step < 5)
//        {
//            logger.info("#######filtered_qe_comb");
//            searcher = Config.openMultiSearcherForContentsAndSentences();
////            runBase(run5,RunType.Comb, outputFile + "filtered_qe_comb_t_keys.xml",searcher,strategyQueryBuilderTimeKeys.baseFilteredIterator_comb(),true,Config.DOC_ID,true,startTopic);
//            runBase(run5,RunType.Comb, outputFile + "filtered_qe_comb_t_all.xml",searcher,strategyQueryBuilderTimeAll.baseFilteredIterator_comb(),false,Config.DOC_ID,true,startTopic);
//            searcher.close();
//        startTopic = "";
//        }


    }

    enum RunType
    {
        Text,
        Sentences,
        Comb
    }

    public static void runBase(String desc, RunType runType, String outputFile, LgteIndexSearcherWrapper searcherOld, StrategyQueryBuilder.Iterator iter,boolean keys, String groupField,boolean expansion, String startTopic) throws IOException, DocumentException, ParseException
    {
        logger.info("####################################");

        StrategyQueryBuilder.Iterator.QueryPackage queryPackage;

        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap,new LgteWhiteSpacesAnalyzer());
        new File(outputFile).getParentFile().mkdirs();
        boolean append = startTopic.length() > 0;
        FileOutputStream stream = new FileOutputStream(new File(outputFile),append);
//        OutputFormat outputFormat = new TrecEvalOutputFormatFactory(Config.ID).createNew(stream);
        OutputFormat outputFormat = new RunIdOutputFormatFactory(Config.ID, outputFile.substring(outputFile.lastIndexOf(File.separator)+1,outputFile.lastIndexOf(".")),desc).createNew(stream,append);
        outputFormat.init("id","id");
        outputFormat.setMaxDocsToFlush(100);

        LgteIndexSearcherWrapper searcher = Config.openMultiSearcher2011();
        while((queryPackage = iter.next())!=null)

        {
            //INIT MODEL
            ModelManager.getInstance().setModel(Model.BM25Normalized);
            if(queryPackage.getTopicId().compareTo(startTopic)>=0)
            {


//                LgteIndexSearcherWrapper searcher = Config.openMultiSearcher2011();
                QueryConfiguration queryConfigurationBase = new QueryConfiguration();
                queryConfigurationBase.setProperty("bm25.idf.policy","standard");
                queryConfigurationBase.setProperty("bm25.k1","1.2d");
                queryConfigurationBase.setProperty("bm25.b","0.75d");




                if(runType == RunType.Comb)
                    queryConfigurationBase.setProperty("index.tree","true");
                logger.info(queryPackage.getTopicId() + "-----------------------------------");
                setGeoTemporalFactors(queryPackage, queryConfigurationBase);
                logger.info(queryPackage.filter);
                logger.info(queryPackage.query);
                if(!showOnlyQueries)
                {
                    LgteQuery lgteQuery = LgteQueryParser.parseQuery(queryPackage.query,brokerStemAnalyzer,searcher,queryConfigurationBase);
//                    LgteQuery lgteQuery = new LgteQuery(queryPackage.query,brokerStemAnalyzer,queryConfigurationBase);
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
                    for(int i = 0; i < 5;i++)
                    {
                        String snippet = hits.summary(i,"contents");
                        String id = hits.doc(i).getId().toString();
                        float score = hits.score(i);
                        float spatialScore = hits.spatialScore(i);
                        float timeScore = hits.timeScore(i);
                        float textScore = hits.textScore(i);
                        System.out.println("n:" + hits.doc(i).getNorthLimit());
                        System.out.println("s:" + hits.doc(i).getSouthLimit());
                        System.out.println("e:" + hits.doc(i).getEastLimit());
                        System.out.println("w:" + hits.doc(i).getWestLimit());

                        System.out.println("latitude:" + hits.doc(i).getLatitude());
                        System.out.println("longitude:" + hits.doc(i).getLongitude());

                        if( hits.doc(i).getTimeBox() != null)
                        {
                            System.out.println("START TIME:" + hits.doc(i).getTimeBox().getStartTimeYear());
                            System.out.println("END TIME:" + hits.doc(i).getTimeBox().getEndTimeYear());
                        }



                        System.out.println(i + " - DOC: " + id + " score:" + score + " text:" + textScore + " geo:" + spatialScore + " time:" + timeScore);
                        System.out.println(snippet);
                    }
                    SearchTopics.writeSearch(outputFormat,hits,groupField,outputFile.substring(outputFile.lastIndexOf(File.separator)+1,outputFile.lastIndexOf(".")),Config.outputDocs,null);
                }
//                searcher.close();
            }
        }
        searcher.close();
        outputFormat.close();
    }

    private static void setGeoTemporalFactors(StrategyQueryBuilder.Iterator.QueryPackage queryPackage, QueryConfiguration queryConfigurationBase) {
        /*SET GEOTEMPORAL FACTORS*/
        System.out.println("scorer.default.model.text.factor was = " + queryConfigurationBase.getProperty("scorer.default.model.text.factor"));
        queryConfigurationBase.setProperty("scorer.default.model.text.factor", Config.metricTextFactor + "");
        System.out.println("GeoTime scorer.default.model.text.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.text.factor"));

        System.out.println("scorer.default.model.time.factor was = " + queryConfigurationBase.getProperty("scorer.default.model.time.factor"));
        queryConfigurationBase.setProperty("scorer.default.model.time.factor",Config.metricTimeFactor + "");
        System.out.println("scorer.default.model.time.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.time.factor"));

        System.out.println("scorer.default.model.spatial.factor was = " + queryConfigurationBase.getProperty("scorer.default.model.spatial.factor"));
        queryConfigurationBase.setProperty("scorer.default.model.spatial.factor",Config.metricGeoFactor + "");
        System.out.println("scorer.default.model.spatial.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.spatial.factor"));

        boolean oneFromWikipedia = false;

        if(queryPackage.queryProcessor.getQ().getTemporalQuery() != null)
        {
            if(queryPackage.queryProcessor.getQ().getTemporalQuery().isWikipedia())
            {
                System.out.println("TimeBox in " + queryPackage.getTopicId() + " if from wikipedia");
                queryConfigurationBase.setProperty("scorer.default.model.time.factor",Config.wikipediaTermsBoundingBoxTimeMetricFactor + "");
                System.out.println("scorer.default.model.time.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.time.factor"));
                oneFromWikipedia = true;
            }
        }
        if(queryPackage.queryProcessor.getQ().getGeographicQuery() != null)
        {
            if(queryPackage.queryProcessor.getQ().getGeographicQuery().isWikipedia())
            {
                System.out.println("GeoBox in " +  queryPackage.getTopicId()  + " if from wikipedia");
                queryConfigurationBase.setProperty("scorer.default.model.spatial.factor",Config.wikipediaTermsBoundingBoxGeoMetricFactor + "");
                System.out.println("scorer.default.model.spatial.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.spatial.factor"));
                oneFromWikipedia = true;
            }
        }
        if(oneFromWikipedia)
        {
            queryConfigurationBase.setProperty("scorer.default.model.text.factor",Config.wikipediaTermsBoundingBoxTextMetricFactor + "");
            System.out.println("scorer.default.model.text.factor is = " + queryConfigurationBase.getProperty("scorer.default.model.text.factor"));
        }
    }
}
