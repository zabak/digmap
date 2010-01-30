package pt.utl.ist.lucene.test.reasoningqueryprocessor;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.geotime.queries.Query;
import pt.utl.ist.lucene.treceval.geotime.queries.QueryParser;
import pt.utl.ist.lucene.treceval.geotime.queries.QueryProcessor;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.DocumentException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.index.Term;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 13:23:52
 * @email machadofisher@gmail.com
 */
public class TestQueryProcessorHierarchicIndexes extends TestCase {

    String xmlTimeTestYYYY =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";
    String xmlTimeTestYYYYMM =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year-month</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";
    String xmlTimeTestYYYYMMDD =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>exact-date</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";

    String xmlTimeTestYYYYMMDD_ALL_OR =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>exact-date</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year-month</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";

    String xmlTimeTestYYYYMMDD_ALL_AND =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"AND\">\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>exact-date</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year-month</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";




    String xmlTimeTest1990 =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>time</field>\n" +
                    "               <value>1990</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";


    String xmlTimeTest1990_AND_MONTH =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"AND\">\n" +
                    "           <term>\n" +
                    "               <field>time</field>\n" +
                    "               <value>1990</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year-month</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";

    String xmlTimeTest1990_OR_MONTH =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>time</field>\n" +
                    "               <value>1990</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>year-month</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";

    String xmlTimeTest1990_OR_MONTH_ANY =
            "<topic id=\"GeoTime-0025\">\n" +
                     "  <original>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </original>\n" +
                    "  <originalClean>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </originalClean>\n" +
                    "  <filterChain>\n" +
                    "       <boolean type=\"OR\">\n" +
                    "           <term>\n" +
                    "               <field>time</field>\n" +
                    "               <value>1990</value>\n" +
                    "           </term>\n" +
                    "           <term>\n" +
                    "               <field>timeType</field>\n" +
                    "               <value>any</value>\n" +
                    "           </term>\n" +
                    "       </boolean>\n" +
                    "  </filterChain>\n" +
                    "  <terms>\n" +
                    "    <desc>word2</desc>\n" +
                    "    <narr>word3</narr>\n" +
                    "  </terms>\n" +
                    "</topic>";



    private String path = Globals.INDEX_DIR + "/" + getClass().getName();

    public void testTimeFilter() throws IOException, DocumentException, ParseException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path + "Contents",true);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents","word1 word2 word3");
        doc1.indexStringNoStore(Config.S_HAS_TIMEXES,"true");
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.close();


        LgteIndexWriter writer2 = new LgteIndexWriter(path + "Sentences",true);
        LgteDocumentWrapper sentence0 = new LgteDocumentWrapper();
        sentence0.indexText(Globals.DOCUMENT_ID_FIELD, "1_0");
        sentence0.indexText("doc_id", "1");
        sentence0.indexText("sentences","word1 word3");
        LgteDocumentWrapper sentence1 = new LgteDocumentWrapper();
        sentence1.indexText(Globals.DOCUMENT_ID_FIELD, "1_1");
        sentence1.indexText("doc_id", "1");
        sentence1.indexText("sentences","word1 word2 word3");
        LgteDocumentWrapper sentence2 = new LgteDocumentWrapper();
        sentence2.indexStringNoStore(Config.S_HAS_TIMEXES + "_sentences","true");
        sentence2.indexText(Globals.DOCUMENT_ID_FIELD, "2_1");
        sentence2.indexText("doc_id", "2");
        sentence2.indexText("sentences","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        writer2.addDocument(sentence0);
        writer2.addDocument(sentence1);
        writer2.addDocument(sentence2);
        writer2.close();


        IndexReader readerContents = LgteIndexManager.openReader(path + "Contents",Model.OkapiBM25Model);
        IndexReader readerSentences = LgteIndexManager.openReader(path + "Sentences",Model.OkapiBM25Model);
        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put("contents",readerContents);
        readers.put("sentences",readerSentences);
        readers.put(Config.S_HAS_TIMEXES,readerContents);
        readers.put(Config.S_HAS_TIMEXES + "_sentences",readerSentences);
        readers.put("doc_id",readerSentences);
        readers.put("id",readerSentences);
        LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
        lgteIsolatedIndexReader.addTreeMapping(readerContents,readerSentences,"doc_id");


        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,lgteIsolatedIndexReader);
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","0.01");
        queryConfiguration.setProperty("bm25.k1","2.0");
        queryConfiguration.setProperty("bm25.b","0.75");
        queryConfiguration.setProperty("index.tree","true");
        QueryFilter queryFilter = new QueryFilter(org.apache.lucene.queryParser.QueryParser.parse("true",Config.S_HAS_TIMEXES,new LgteNothingAnalyzer()));
        LgteQuery lgteQuery = LgteQueryParser.parseQuery("sentences:word2",new LgteNothingAnalyzer(),searcher,queryConfiguration);
        LgteHits lgteHits = searcher.search(lgteQuery,queryFilter);

        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),1);

        TermsFilter termsFilter = new TermsFilter();
        termsFilter.addTerm(new Term(Config.S_HAS_TIMEXES,"true"));
        lgteHits = searcher.search(lgteQuery,termsFilter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),1);



        termsFilter = new TermsFilter();
        termsFilter.addTerm(new Term(Config.S_HAS_TIMEXES + "_sentences","true"));
        lgteHits = searcher.search(lgteQuery,termsFilter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),2);

        searcher.close();

        Files.delDirsE(path + "Contents");
        Files.delDirsE(path + "Sentences");
    }

}
