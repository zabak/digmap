package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.index.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 26/Jan/2010
 * @time 20:15:29
 * @email machadofisher@gmail.com
 */
public class TestIndex {

    //PARA VERIFICAR SE ESTA A USAR A DESCRICAO ONLY VERIFICAR
    //esta conf no file pt.utl.ist.lucene.utils.queries.QueryProcessor
    // public static boolean DESC_ONLY = false;
    private static final Logger logger = Logger.getLogger(TestIndex.class);


    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
        System.out.println("USING args:" + args.length);

        String query = "contents:(space shuttle Columbia disaster) west:-81.728111 south:-4.23048 east:-66.869827 north:13.39029 starttime:1990-03-29 endtime:2009-04-02 filter:no";
//        String queryLgte = "west:12.728111 south:4.23048 east:13.869827 north:13.39029  starttime:1990-03-29 endtime:2009-04-02";
//        System.out.println("query:" + query);
//        System.out.println("queryLgte:" + queryLgte);
        LgteIndexSearcherWrapper searcher = Config.openMultiSearcher2011();//new LgteIndexSearcherWrapper(Model.OkapiBM25Model, "/home/jmachado/ntcir/DATA/INDEXES/METRICS_INDEXED");

        Map<String, Analyzer> analyzersMap = new HashMap<String, Analyzer>();
        analyzersMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        analyzersMap.put(Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer brokerStemAnalyzer = new LgteBrokerStemAnalyzer(analyzersMap,new LgteWhiteSpacesAnalyzer());

        QueryConfiguration queryConfigurationBase = new QueryConfiguration();
        queryConfigurationBase.setProperty("bm25.idf.policy","standard");
        queryConfigurationBase.setProperty("bm25.k1","1.2d");
        queryConfigurationBase.setProperty("bm25.b","0.75d");

//        IndexReader readerMetrics = LgteIndexManager.openReader(IndexMetrics.indexPath, Model.OkapiBM25Model);
        


        if(args.length == 1)
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                System.out.println("Search for:" + line);
                search(line, searcher, brokerStemAnalyzer, queryConfigurationBase);
            }
            bufferedReader.close();
        }
        search(query, searcher, brokerStemAnalyzer, queryConfigurationBase);
        search("Washington beltway snipers arrested west:-106.645653 south:24.521 east:-75.04847 north:40.638729 starttime:1982-05-19 endtime:2010-06-20 filter:no", searcher, brokerStemAnalyzer, queryConfigurationBase);
        search("Washington beltway snipers arrested west:-106.645653 south:24.521 east:-75.04847 north:40.638729 starttime:1982 endtime:2010 filter:no", searcher, brokerStemAnalyzer, queryConfigurationBase);
        searcher.close();
    }

    private static void search(String query, LgteIndexSearcherWrapper searcher, LgteBrokerStemAnalyzer brokerStemAnalyzer, QueryConfiguration queryConfigurationBase) throws ParseException, IOException {
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,brokerStemAnalyzer,searcher,queryConfigurationBase);

//        LgteQuery lgteQuery = new LgteQuery(query,lgteQuery1.getQueryParams(),brokerStemAnalyzer);
//        lgteQuery.setQueryConfiguration(queryConfigurationBase);
        LgteHits hits = searcher.search(lgteQuery);

        System.out.println("Encontrados " + hits.length() + " resultados");

        for(int i = 0; i < hits.length() && i < 10;i++)
        {
            String snippet = hits.summary(i,"contents");
            String id = hits.doc(i).getId().toString();
            float score = hits.score(i);
            float spatialScore = hits.spatialScore(i);
            float timeScore = hits.timeScore(i);
            float textScore = hits.textScore(i);
            System.out.println("----------------------");
            System.out.println("n:" + hits.doc(i).getNorthLimit());
            System.out.println("s:" + hits.doc(i).getSouthLimit());
            System.out.println("e:" + hits.doc(i).getEastLimit());
            System.out.println("w:" + hits.doc(i).getWestLimit());

            System.out.println("latitude:" + hits.doc(i).getLatitude());
            System.out.println("longitude:" + hits.doc(i).getLongitude());

            System.out.println("START TIME:" + hits.doc(i).getTimeBox().getStartTimeYear());
            System.out.println("END TIME:" + hits.doc(i).getTimeBox().getEndTimeYear());


            System.out.println(i + " - DOC: " + id + " score:" + score + " text:" + textScore + " geo:" + spatialScore + " time:" + timeScore);
            System.out.println(snippet);
        }

    }
}
