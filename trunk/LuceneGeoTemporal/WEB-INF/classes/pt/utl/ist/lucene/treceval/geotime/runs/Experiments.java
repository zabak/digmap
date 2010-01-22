package pt.utl.ist.lucene.treceval.geotime.runs;

import pt.utl.ist.lucene.treceval.geotime.index.*;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.search.Query;

/**
 * @author Jorge Machado
 * @date 21/Jan/2010
 * @time 12:26:25
 * @email machadofisher@gmail.com
 */
public class Experiments
{
    public static void main(String[] args) throws IOException, ParseException {

        IndexReader readerSentences = LgteIndexManager.openReader(IndexSentences.indexPath, Model.OkapiBM25Model);
        IndexReader readerContents = LgteIndexManager.openReader(IndexContents.indexPath, Model.OkapiBM25Model);
        IndexReader readerGeoRefs = LgteIndexManager.openReader(IndexWoeid.indexPath, Model.OkapiBM25Model);

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();

        readers.put(Config.SENTENCES,readerSentences);
        readers.put(Config.CONTENTS,readerContents);
        readers.put(Config.DOC_ID,readerSentences);
        readers.put(Config.ID,readerSentences);

//        readers.put("regexpr(^g_.*)",readerGeoRefs);

//        LgteIndexSearcherWrapper searcher = Config.openMultiSearcherSentences();
        LgteIsolatedIndexReader reader = new LgteIsolatedIndexReader(readers);
        System.out.println("Tree Mapping");
        reader.addTreeMapping(readerContents,readerSentences,Config.DOC_ID);
        System.out.println("Saiu do Tree Mapping");
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,reader);

        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents", IndexCollections.en.getAnalyzerWithStemming());
        ngramsAnalizers.put("sentences", IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers,new LgteNothingAnalyzer());

//        String queryStr = "contents:(soccer smith)";
        String queryStr = "contents:(soccer smith) sentences:(smith soccer)";
        LgteQuery query = new LgteQuery(queryStr,lgteBrokerStemAnalyzer,"contents");
        query.getQueryParams().getQueryConfiguration().setProperty("index.tree","true");
        LgteHits hits = searcher.search(query);

        for(int i = 0; i < 10;i++)
        {
            System.out.println(hits.doc(i).get("id") + "-" + hits.score(i));
            System.out.println(searcher.explain(query,hits.id(i), lgteBrokerStemAnalyzer));
        }


        System.out.println("****************************");

        query = new LgteQuery(queryStr,lgteBrokerStemAnalyzer,"contents");
        query.getQueryParams().getQueryConfiguration().setProperty("index.tree","true");
        hits = searcher.search(query);

        for(int i = 0; i < 10;i++)
        {
            System.out.println(hits.doc(i).get("id") + "-" + hits.score(i));
            System.out.println(searcher.explain(query,hits.id(i), lgteBrokerStemAnalyzer));
        }





    }

    public static int aqui = 0;
}
