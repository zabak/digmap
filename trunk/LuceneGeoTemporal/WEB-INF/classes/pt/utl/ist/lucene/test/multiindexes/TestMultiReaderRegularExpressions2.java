package pt.utl.ist.lucene.test.multiindexes;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 11:05:21
 * @email machadofisher@gmail.com
 */
public class TestMultiReaderRegularExpressions2 extends TestCase {
    private String pathUnique = Globals.INDEX_DIR + "/" + getClass().getName() + "unique";
    private String pathMulti1 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi1";
    private String pathMulti2 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi2";

    protected void setUp() throws IOException {
        String contents1Doc1 ="word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
        String contents2Doc1 ="word1 word2 word3 word32 word1 word45 word56 word67";
        String contents1Doc2 ="word2 word3 word4 word55 word96 word2 word54 word33 wordss";
        String contents2Doc2 ="word2 word3 word4 word55";
        String contents1Doc3 ="word1 word100 word400 word555 word966 word544 word333 wordss";
        String contents2Doc3 ="word1 word100";
        LgteIndexWriter writer = new LgteIndexWriter(pathUnique,true, Model.OkapiBM25Model);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("c1_contents",contents1Doc1);
        doc1.indexText("c2_contents",contents2Doc1);
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("c1_contents",contents1Doc2);
        doc2.indexText("c2_contents",contents2Doc2);
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("c1_contents",contents1Doc3);
        doc3.indexText("c2_contents",contents2Doc3);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();

        // these two writers will replace the previous one
        // The first one will index contents1 and the second will index the contents2
        writer = new LgteIndexWriter(pathMulti1,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("c1_contents",contents1Doc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("c1_contents",contents1Doc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("c1_contents",contents1Doc3);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();

        writer = new LgteIndexWriter(pathMulti2,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("c2_contents",contents2Doc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("c2_contents",contents2Doc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("c2_contents",contents2Doc3);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathUnique);
        Files.delDirsE(pathMulti1);
        Files.delDirsE(pathMulti2);
    }

    public void testRange() throws IOException, InvalidGeoException {

        double k1 = 2.0d;
        double b = 0.75d;
        double epslon = 0.05d;
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
        queryConfiguration.setProperty("bm25.k1","" + k1);
        queryConfiguration.setProperty("bm25.b","" + b);



        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, pathUnique);
        IndexReader readerMulti1 = new LanguageModelIndexReader(IndexReader.open(pathMulti1));
        IndexReader readerMulti2 = new LanguageModelIndexReader(IndexReader.open(pathMulti2));
        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put("regexpr(c1_.*)",readerMulti1);
        readers.put("regexpr(c2_.*)",readerMulti2);

        LgteIndexSearcherWrapper searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));


        try{
            LgteQuery lgteQuery = LgteQueryParser.parseQuery("c1_contents:(word2 word67 word1*) c2_contents:(word1* word2 word67)",searcher,queryConfiguration);
            LgteHits lgteHits = searcher.search(lgteQuery);
            LgteQuery lgteQueryMulti = LgteQueryParser.parseQuery("c1_contents:(word1* word2 word67) c2_contents:(word1* word2 word67)",searcherMulti,queryConfiguration);
            LgteHits lgteHitsMulti = searcherMulti.search(lgteQueryMulti);

            System.out.println("EXPECTED");
            System.out.println("doc:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));
            System.out.println("doc:" + lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(2));
            System.out.println("RETURN:");
            System.out.println("doc:" + lgteHitsMulti.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHitsMulti.score(0));
            System.out.println("doc:" + lgteHitsMulti.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHitsMulti.score(1));
            System.out.println("doc:" + lgteHitsMulti.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHitsMulti.score(2));



            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD),lgteHitsMulti.doc(0).get(Globals.DOCUMENT_ID_FIELD));
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD),lgteHitsMulti.doc(1).get(Globals.DOCUMENT_ID_FIELD));
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD),lgteHitsMulti.doc(2).get(Globals.DOCUMENT_ID_FIELD));

            assertEquals(lgteHits.score(0),lgteHitsMulti.score(0));
            assertEquals(lgteHits.score(1),lgteHitsMulti.score(1));
            assertEquals(lgteHits.score(2),lgteHitsMulti.score(2));
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }

}
