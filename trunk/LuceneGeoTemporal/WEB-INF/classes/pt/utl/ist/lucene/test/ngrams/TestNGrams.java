package pt.utl.ist.lucene.test.ngrams;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.FieldsTopic;
import pt.utl.ist.lucene.analyzer.LgteStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestNGrams extends TestCase
{

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();
    LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = null;


    protected void setUp() throws IOException
    {


        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents", IndexCollections.en.getAnalyzerNoStemming());
        ngramsAnalizers.put("contentsN2", LgteAnalyzerManager.getInstance().getLanguagePackage(2,2, EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN3", LgteAnalyzerManager.getInstance().getLanguagePackage(3,3,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN4", LgteAnalyzerManager.getInstance().getLanguagePackage(4,4,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN5", LgteAnalyzerManager.getInstance().getLanguagePackage(5,5,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN6", LgteAnalyzerManager.getInstance().getLanguagePackage(6,6,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers);
        LgteIndexWriter writer = new LgteIndexWriter(path, lgteBrokerStemAnalyzer,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        indexTextFields(doc1,"word1 word2 word3");


        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        indexTextFields(doc2,"word3 word4 word5");

        writer.addDocument(doc1);
        writer.addDocument(doc2);

        writer.close();
    }

    private void indexTextFields(LgteDocumentWrapper doc, String text)
    {
        doc.indexText("contents",text);
        doc.indexText("contentsN2",text);
        doc.indexText("contentsN3",text);
        doc.indexText("contentsN4",text);
        doc.indexText("contentsN5",text);
        doc.indexText("contentsN6",text);
    }

    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException, ParseException
    {
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, path);

        QueryConfiguration queryConfiguration1_grams = new QueryConfiguration();
        queryConfiguration1_grams.setForceQE(QEEnum.text);
        queryConfiguration1_grams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contents","0.53f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN5","0.14f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN4","0.11f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN3","0.11f");
        queryConfiguration1_grams.getQueryProperties().setProperty("field.boost.contentsN2","0.11");
        LgteQuery query = LgteQueryParser.parseQuery(FieldsTopic.expandQueryInFields(queryConfiguration1_grams.getQueryProperties(),"word1",lgteBrokerStemAnalyzer),searcher,lgteBrokerStemAnalyzer,queryConfiguration1_grams);


        LgteHits lgteHits = searcher.search(query);
        System.out.println(query);
        System.out.println(lgteHits.doc(0).get("id") + " - " + lgteHits.score(0));
        System.out.println(lgteHits.doc(1).get("id") + " - " + lgteHits.score(1));


        searcher.close();
    }
}
