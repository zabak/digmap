package pt.utl.ist.lucene.test.reasoningqueryprocessor;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.placemaker.PlaceNameNormalizer;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.geotime.queries.Query;
import pt.utl.ist.lucene.treceval.geotime.queries.QueryParser;
import pt.utl.ist.lucene.treceval.geotime.queries.QueryProcessor;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 13:23:52
 * @email machadofisher@gmail.com
 */
public class TestQueryProcessorSentences extends TestCase {

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

    public void testTimeFilter() throws IOException, DocumentException, ParseException {
        LgteIndexWriter writer = new LgteIndexWriter(path + "testTimeFilter",true);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexStringNoStore(Config.S_HAS_TIMEXES + Config.SEP + Config.SENTENCES,"true");
        doc1.indexText("sentences","word1 word2 word3");
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("sentences","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        doc2.indexStringNoStore(Config.S_HAS_YYYY + Config.SEP + Config.SENTENCES,"true");
        doc2.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"1990");
        doc2.indexStringNoStore(Config.T_TIME_EXPRESSIONS + Config.SEP + Config.SENTENCES,"1990");
        doc2.indexStringNoStore(Config.S_HAS_TIMEXES + Config.SEP + Config.SENTENCES,"true");
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc3.indexStringNoStore(Config.S_HAS_YYYYMM + Config.SEP + Config.SENTENCES,"true");
        doc3.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"199002");
        doc3.indexStringNoStore(Config.T_TIME_EXPRESSIONS + Config.SEP + Config.SENTENCES,"199002");
        doc3.indexStringNoStore(Config.S_HAS_TIMEXES + Config.SEP + Config.SENTENCES,"true");
        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc4.indexStringNoStore(Config.S_HAS_YYYYMMDD + Config.SEP + Config.SENTENCES,"true");
        doc4.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"20090203");
        doc4.indexStringNoStore(Config.T_TIME_EXPRESSIONS + Config.SEP + Config.SENTENCES,"20090203");
        doc4.indexStringNoStore(Config.S_HAS_TIMEXES + Config.SEP + Config.SENTENCES,"true");
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);
        writer.close();

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, path + "testTimeFilter");
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","0.01");
        queryConfiguration.setProperty("bm25.k1","2.0");
        queryConfiguration.setProperty("bm25.b","0.75");
        LgteQuery lgteQuery;
        LgteHits lgteHits;

        Query qYYYY = new QueryParser(xmlTimeTestYYYY).getQuery();
        QueryProcessor qpYYYY = new QueryProcessor(qYYYY);

        Query qYYYYMM = new QueryParser(xmlTimeTestYYYYMM).getQuery();
        QueryProcessor qpYYYYMM = new QueryProcessor(qYYYYMM);

        Query qYYYYMMDD = new QueryParser(xmlTimeTestYYYYMMDD).getQuery();
        QueryProcessor qpYYYYMMDD = new QueryProcessor(qYYYYMMDD);

        Query q_ALL_OR = new QueryParser(xmlTimeTestYYYYMMDD_ALL_OR).getQuery();
        QueryProcessor qp_ALL_OR = new QueryProcessor(q_ALL_OR);

        Query q_ALL_AND = new QueryParser(xmlTimeTestYYYYMMDD_ALL_AND).getQuery();
        QueryProcessor qp_ALL_AND = new QueryProcessor(q_ALL_AND);

        Query q_1990 = new QueryParser(xmlTimeTest1990).getQuery();
        QueryProcessor qp_1990 = new QueryProcessor(q_1990);

        Query q_1990_Month = new QueryParser(xmlTimeTest1990_AND_MONTH).getQuery();
        QueryProcessor qp_1990_Month = new QueryProcessor(q_1990_Month);

        Query q_1990_Month_OR = new QueryParser(xmlTimeTest1990_OR_MONTH).getQuery();
        QueryProcessor qp_1990_Month_OR = new QueryProcessor(q_1990_Month_OR);

        Query q_1990_Month_OR_ANY = new QueryParser(xmlTimeTest1990_OR_MONTH_ANY).getQuery();
        QueryProcessor qp_1990_Month_OR_ANY = new QueryProcessor(q_1990_Month_OR_ANY);


        String query = qpYYYY.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        Filter filter = qpYYYY.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));

        query = qpYYYYMM.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpYYYYMM.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 2 && lgteHits.id(1) == 3 || lgteHits.id(0) == 3 && lgteHits.id(1) == 2);

        query = qpYYYYMMDD.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpYYYYMMDD.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertTrue(lgteHits.id(0) == 3);



        query = qp_ALL_OR.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_ALL_OR.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));


        query = qp_ALL_AND.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_ALL_AND.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertTrue(lgteHits.id(0) == 3);


        query = qp_1990.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 1 && lgteHits.id(1) == 2 || lgteHits.id(0) == 2 && lgteHits.id(1) == 1);

        query = qp_1990_Month.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990_Month.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),2);

        query = qp_1990_Month_OR.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990_Month_OR.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));

        query = qp_1990_Month_OR_ANY.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990_Month_OR_ANY.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),4);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3 || lgteHits.id(0) == 0) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3 || lgteHits.id(1) == 0) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3 || lgteHits.id(2) == 0) &&
                (lgteHits.id(3) == 1 || lgteHits.id(3) == 2 || lgteHits.id(3) == 3 || lgteHits.id(3) == 0));



        searcher.close();
        Files.delDirsE(path + "testTimeFilter");
    }





    public void testTimeFilterKey() throws IOException, DocumentException, ParseException {
        LgteIndexWriter writer = new LgteIndexWriter(path + "testTimeFilterKey",true);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexStringNoStore(Config.S_HAS_TIMEXES,"true");
        doc1.indexText("sentences","word1 word2 word3");
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("sentences","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        doc2.indexStringNoStore(Config.S_HAS_YYYY_KEY + Config.SEP + Config.SENTENCES,"true");
        doc2.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"1990");
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc3.indexStringNoStore(Config.S_HAS_YYYYMM_KEY + Config.SEP + Config.SENTENCES,"true");
        doc3.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"199002");
        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc4.indexStringNoStore(Config.S_HAS_YYYYMMDD_KEY + Config.SEP + Config.SENTENCES,"true");
        doc4.indexStringNoStore(Config.T_POINT_KEY + Config.SEP + Config.SENTENCES,"20090203");
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);
        writer.close();

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, path + "testTimeFilterKey");
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","0.01");
        queryConfiguration.setProperty("bm25.k1","2.0");
        queryConfiguration.setProperty("bm25.b","0.75");
        LgteQuery lgteQuery;
        LgteHits lgteHits;

        Query qYYYY = new QueryParser(xmlTimeTestYYYY).getQuery();
        QueryProcessor qpYYYY = new QueryProcessor(qYYYY,true);

        Query qYYYYMM = new QueryParser(xmlTimeTestYYYYMM).getQuery();
        QueryProcessor qpYYYYMM = new QueryProcessor(qYYYYMM,true);

        Query qYYYYMMDD = new QueryParser(xmlTimeTestYYYYMMDD).getQuery();
        QueryProcessor qpYYYYMMDD = new QueryProcessor(qYYYYMMDD,true);

        Query q_ALL_OR = new QueryParser(xmlTimeTestYYYYMMDD_ALL_OR).getQuery();
        QueryProcessor qp_ALL_OR = new QueryProcessor(q_ALL_OR,true);

        Query q_ALL_AND = new QueryParser(xmlTimeTestYYYYMMDD_ALL_AND).getQuery();
        QueryProcessor qp_ALL_AND = new QueryProcessor(q_ALL_AND,true);

        Query q_1990 = new QueryParser(xmlTimeTest1990).getQuery();
        QueryProcessor qp_1990 = new QueryProcessor(q_1990,true);

        Query q_1990_Month = new QueryParser(xmlTimeTest1990_AND_MONTH).getQuery();
        QueryProcessor qp_1990_Month = new QueryProcessor(q_1990_Month,true);

        Query q_1990_Month_OR = new QueryParser(xmlTimeTest1990_OR_MONTH).getQuery();
        QueryProcessor qp_1990_Month_OR = new QueryProcessor(q_1990_Month_OR,true);


        String query = qpYYYY.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        Filter filter = qpYYYY.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));

        query = qpYYYYMM.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpYYYYMM.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 2 && lgteHits.id(1) == 3 || lgteHits.id(0) == 3 && lgteHits.id(1) == 2);

        query = qpYYYYMMDD.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpYYYYMMDD.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertTrue(lgteHits.id(0) == 3);



        query = qp_ALL_OR.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_ALL_OR.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));


        query = qp_ALL_AND.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_ALL_AND.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertTrue(lgteHits.id(0) == 3);


        query = qp_1990.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 1 && lgteHits.id(1) == 2 || lgteHits.id(0) == 2 && lgteHits.id(1) == 1);

        query = qp_1990_Month.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990_Month.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),2);

        query = qp_1990_Month_OR.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qp_1990_Month_OR.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));



        searcher.close();
        Files.delDirsE(path + "testTimeFilterKey");
    }





    public void testPlaceFilter() throws IOException, DocumentException, ParseException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path + "testPlaceFilter",true);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("sentences","word1 word2 word3");
        doc1.indexStringNoStore(Config.S_GEO_INDEXED + "_sentences","true");
        doc1.indexStringNoStore(Config.G_GEO_ALL_WOEID + "_sentences", PlaceNameNormalizer.normalizeWoeid("798"));
        doc1.indexStringNoStore(Config.G_GEO_PLACE_TYPE + "_sentences","Country");
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();

        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("sentences","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        doc2.indexStringNoStore(Config.G_GEO_ALL_WOEID + "_sentences", PlaceNameNormalizer.normalizeWoeid("123"));
        doc2.indexStringNoStore(Config.G_GEO_PLACE_TYPE + "_sentences","Suburb");
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();

        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc3.indexStringNoStore(Config.G_GEO_ALL_WOEID + "_sentences",PlaceNameNormalizer.normalizeWoeid("321"));
        doc3.indexStringNoStore(Config.G_GEO_ALL_WOEID + "_sentences",PlaceNameNormalizer.normalizeWoeid("456"));
        doc3.indexStringNoStore(Config.G_GEO_PLACE_TYPE + "_sentences","Town");
        doc3.indexStringNoStore(Config.S_GEO_INDEXED + "_sentences","true");
        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();

        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("sentences","word1 word2 word100 word400 word555 word966 word544 word333 wordss");
        doc4.indexStringNoStore(Config.G_GEO_ALL_WOEID + "_sentences",PlaceNameNormalizer.normalizeWoeid("321"));
        doc4.indexStringNoStore(Config.G_GEO_PLACE_TYPE + "_sentences","State");
        doc4.indexStringNoStore(Config.S_GEO_INDEXED + "_sentences","true");
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);
        writer.close();

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, path + "testPlaceFilter");
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","0.01");
        queryConfiguration.setProperty("bm25.k1","2.0");
        queryConfiguration.setProperty("bm25.b","0.75");
        LgteQuery lgteQuery;
        LgteHits lgteHits;


        String xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"123\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        Query queryXml = new QueryParser(xml).getQuery();
        QueryProcessor qpXml = new QueryProcessor(queryXml);

        String query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        Filter filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),1);

        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"321\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "          <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>province</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertEquals(lgteHits.id(0),3);



        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"123\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "          <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>province</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 1 && lgteHits.id(1) == 3 || lgteHits.id(0) == 3 && lgteHits.id(1) == 1);



        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"123\">TestPlace</value>\n" +
                        "               <value woeid=\"321\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),3);
        assertTrue((lgteHits.id(0) == 1 || lgteHits.id(0) == 2 || lgteHits.id(0) == 3) &&
                (lgteHits.id(1) == 1 || lgteHits.id(1) == 2 || lgteHits.id(1) == 3) &&
                (lgteHits.id(2) == 1 || lgteHits.id(2) == 2 || lgteHits.id(2) == 3));


        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"123\">TestPlace</value>\n" +
                        "               <value woeid=\"321\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>city</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),2);
        assertTrue(lgteHits.id(0) == 1 && lgteHits.id(1) == 2 || lgteHits.id(0) == 2 && lgteHits.id(1) == 1);




        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"893\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>place</field>\n" +
                        "               <value woeid=\"234234\">Test</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>place</field>\n" +
                        "               <value woeid=\"2343234\">Test</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>country</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        filter = qpXml.getFilters(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");
        lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration);
        lgteHits = searcher.search(lgteQuery,filter);
        assertEquals(lgteHits.length(),1);
        assertTrue(lgteHits.id(0) == 0);


        xml =
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
                        "               <field>place</field>\n" +
                        "               <value woeid=\"123\">TestPlace</value>\n" +
                        "               <value woeid=\"321\">TestPlace</value>\n" +
                        "           </term>\n" +
                        "           <term>\n" +
                        "               <field>placeType</field>\n" +
                        "               <value>city</value>\n" +
                        "           </term>\n" +
                        "       </boolean>\n" +
                        "  </filterChain>\n" +
                        "  <terms>\n" +
                        "    <desc>word2</desc>\n" +
                        "    <narr>word3</narr>\n" +
                        "  </terms>\n" +
                        "  <places>\n" +
                        "    <term woeid=\"987654321\">Test</term>\n" +
                        "    <term woeid=\"12345678\">Test</term>\n" +
                        "  </places>\n"+
                        "  <times>\n" +
                        "    <term>2002</term>\n" +
                        "    <term>200304</term>\n" +
                        "    <term>20050401</term>\n" +
                        "  </times>\n"+
                        "</topic>";
        queryXml = new QueryParser(xml).getQuery();
        qpXml = new QueryProcessor(queryXml);

        query = qpXml.getTermsQuery(QueryProcessor.QueryTarget.SENTENCES);
        assertEquals(query,"sentences:(word2 word2 word3)");

        assertEquals(qpXml.getPlacesQuery(QueryProcessor.QueryTarget.SENTENCES),Config.G_GEO_ALL_WOEID + "_sentences:(WOEID-987654321 WOEID-12345678)");
        assertEquals(qpXml.getPlacesRefQuery(QueryProcessor.QueryTarget.SENTENCES, "0.2"),Config.G_PLACE_REF_WOEID + "_sentences:(WOEID-987654321 WOEID-12345678)^0.2");
        assertEquals(qpXml.getPlacesBeolongTosQuery(QueryProcessor.QueryTarget.SENTENCES, "0.8"),Config.G_PLACE_BELONG_TOS_WOEID + "_sentences:(WOEID-987654321 WOEID-12345678)^0.8");

        assertEquals(qpXml.getTimesQueryTimeExpressions(QueryProcessor.QueryTarget.SENTENCES),Config.T_TIME_EXPRESSIONS + "_sentences:(2002* 200304* 20050401*)");
        assertEquals(qpXml.getTimesQueryKeyTimeExpressions(QueryProcessor.QueryTarget.SENTENCES, "0.5"),Config.T_POINT_KEY + "_sentences:(2002* 200304* 20050401*)^0.5");
        assertEquals(qpXml.getTimesQueryRelativeTimeExpressions(QueryProcessor.QueryTarget.SENTENCES, "0.3"),Config.T_POINT_RELATIVE + "_sentences:(2002* 200304* 20050401*)^0.3");
        assertEquals(qpXml.getTimesQueryDurationsTimeExpressions(QueryProcessor.QueryTarget.SENTENCES, "0.2"),Config.T_DURATION + "_sentences:(2002* 200304* 20050401*)^0.2");



        searcher.close();
        Files.delDirsE(path + "testPlaceFilter");
    }
}
