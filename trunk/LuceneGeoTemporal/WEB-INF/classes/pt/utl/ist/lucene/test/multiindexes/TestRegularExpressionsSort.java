package pt.utl.ist.lucene.test.multiindexes;

import junit.framework.TestCase;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 25/Jan/2010
 * @time 15:21:21
 * @email machadofisher@gmail.com
 */
public class TestRegularExpressionsSort extends TestCase
{
    private String pathMultiSentences = Globals.INDEX_DIR + "/" + getClass().getName() + "multi1";
    private String pathMultiContents = Globals.INDEX_DIR + "/" + getClass().getName() + "multi2";

    protected void setUp() throws IOException
    {
        String contentsSentencesDoc1 ="word1 WORDTEST1111 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
        String contentsSentencesDoc2 ="word2 word3 word4 word55 word96 word2 word54 word33 wordss";
        String contentsSentencesDoc3 ="word1 word100 word400 word555 word966 word544 word333 wordss";
        String contents2Doc1 ="word1 word2 word3 word32 word1 word45 word56 word67";
        String contents2Doc2 ="word2 word3 word4 word55";
        String contents2Doc3 ="word1 word100 WORDTEST1111 ";

        LgteIndexWriter writer;
        LgteDocumentWrapper doc1;
        LgteDocumentWrapper doc2;
        LgteDocumentWrapper doc3;

        // these two writers will replace the previous one
        // The first one will index contents1 and the second will index the contents2
        writer = new LgteIndexWriter(pathMultiSentences,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents_sentences",contentsSentencesDoc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents_sentences",contentsSentencesDoc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents_sentences",contentsSentencesDoc3);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();

        writer = new LgteIndexWriter(pathMultiContents,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents",contents2Doc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents",contents2Doc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents",contents2Doc3);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathMultiSentences);
        Files.delDirsE(pathMultiContents);
    }

    public void testRegExprOrder() throws IOException, ParseException {
        double k1 = 2.0d;
        double b = 0.75d;
        double epslon = 0.05d;
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
        queryConfiguration.setProperty("bm25.k1","" + k1);
        queryConfiguration.setProperty("bm25.b","" + b);



        IndexReader readerMultiSentences = LgteIndexManager.openReader(pathMultiSentences,Model.OkapiBM25Model);
        IndexReader readerMultiContents = LgteIndexManager.openReader(pathMultiContents,Model.OkapiBM25Model);
        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put("regexpr(^contents.*)",readerMultiContents);
        readers.put("regexpr(^contents_sentences.*)",readerMultiSentences);
        LgteIndexSearcherWrapper searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));
        LgteQuery lgteQueryMulti = LgteQueryParser.parseQuery("contents_sentences:(WORDTEST1111)",searcherMulti,queryConfiguration);
        LgteHits lgteHitsMulti = searcherMulti.search(lgteQueryMulti);
        assertEquals(lgteHitsMulti.length(),1);
        assertEquals(lgteHitsMulti.id(0),0);

        lgteQueryMulti = LgteQueryParser.parseQuery("contents:(WORDTEST1111)",searcherMulti,queryConfiguration);
        lgteHitsMulti = searcherMulti.search(lgteQueryMulti);
        assertEquals(lgteHitsMulti.length(),1);
        assertEquals(lgteHitsMulti.id(0),2);

        searcherMulti.close();


        readers = new HashMap<String,IndexReader>();
        readers.put("regexpr(^contents_sentences.*)",readerMultiSentences);
        readers.put("regexpr(^contents.*)",readerMultiContents);
        searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));
        lgteQueryMulti = LgteQueryParser.parseQuery("contents_sentences:(WORDTEST1111)",searcherMulti,queryConfiguration);
        lgteHitsMulti = searcherMulti.search(lgteQueryMulti);
        assertEquals(lgteHitsMulti.length(),1);
        assertEquals(lgteHitsMulti.id(0),0);

        lgteQueryMulti = LgteQueryParser.parseQuery("contents:(WORDTEST1111)",searcherMulti,queryConfiguration);
        lgteHitsMulti = searcherMulti.search(lgteQueryMulti);
        assertEquals(lgteHitsMulti.length(),1);
        assertEquals(lgteHitsMulti.id(0),2);

        searcherMulti.close();

    }
}
