package pt.utl.ist.lucene.test.hierarchicindexes;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.*;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestBm25TermQueries3 extends TestCase {

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String pathSentences = Globals.INDEX_DIR + "/" + getClass().getName();
    private String pathDocuments = Globals.INDEX_DIR + "/" + getClass().getName() + "Docs";


    protected void setUp() throws IOException {
        LgteIndexWriter writer = new LgteIndexWriter(pathSentences,true);
        LgteIndexWriter writerDocs = new LgteIndexWriter(pathDocuments,true);

        String stm1_1 = "word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
        String stm1_2 = "word2 word3 word4 word55 word96 word2 word54 word33 wordss";
        String stm1_3 = "word1 word100 word400 word555 word966 word544 word333 wordss";

        String stm2_1 = "word5 word67 word67";
        String stm2_2 = "word6 word3 word44";
        String stm2_3 = "word555 word966 word1000";

        String document1 = stm1_1 + " " + stm1_2 + " " + stm1_3;
        String document2 = stm2_1 + " " + stm2_2 + " " + stm2_3;


        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents",document1);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents",document2);

        writerDocs.addDocument(doc1);
        writerDocs.addDocument(doc2);
        writerDocs.close();



        LgteDocumentWrapper stmVirtualDoc1_1 = new LgteDocumentWrapper();
        stmVirtualDoc1_1.indexText(Globals.DOCUMENT_ID_FIELD, "1_1");
        stmVirtualDoc1_1.indexText("doc_id", "1");
        stmVirtualDoc1_1.indexText("sentences",stm1_1);

        LgteDocumentWrapper stmVirtualDoc1_2 = new LgteDocumentWrapper();
        stmVirtualDoc1_2.indexText(Globals.DOCUMENT_ID_FIELD, "1_2");
        stmVirtualDoc1_2.indexText("doc_id", "1");
        stmVirtualDoc1_2.indexText("sentences",stm1_2);

        LgteDocumentWrapper stmVirtualDoc1_3 = new LgteDocumentWrapper();
        stmVirtualDoc1_3.indexText(Globals.DOCUMENT_ID_FIELD, "1_3");
        stmVirtualDoc1_3.indexText("doc_id", "1");
        stmVirtualDoc1_3.indexText("sentences",stm1_3);

        LgteDocumentWrapper stmVirtualDoc2_1 = new LgteDocumentWrapper();
        stmVirtualDoc2_1.indexText(Globals.DOCUMENT_ID_FIELD, "2_1");
        stmVirtualDoc2_1.indexText("doc_id", "2");
        stmVirtualDoc2_1.indexText("sentences",stm2_1);

        LgteDocumentWrapper stmVirtualDoc2_2 = new LgteDocumentWrapper();
        stmVirtualDoc2_2.indexText(Globals.DOCUMENT_ID_FIELD, "2_2");
        stmVirtualDoc2_2.indexText("doc_id", "2");
        stmVirtualDoc2_2.indexText("sentences",stm2_2);

        LgteDocumentWrapper stmVirtualDoc2_3 = new LgteDocumentWrapper();
        stmVirtualDoc2_3.indexText(Globals.DOCUMENT_ID_FIELD, "2_3");
        stmVirtualDoc2_3.indexText("doc_id", "2");
        stmVirtualDoc2_3.indexText("sentences",stm2_3);

        writer.addDocument(stmVirtualDoc1_1);
        writer.addDocument(stmVirtualDoc1_2);
        writer.addDocument(stmVirtualDoc1_3);
        writer.addDocument(stmVirtualDoc2_1);
        writer.addDocument(stmVirtualDoc2_2);
        writer.addDocument(stmVirtualDoc2_3);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathSentences);
        Files.delDirsE(pathDocuments);
    }


    public void testRange() throws IOException, InvalidGeoException {

        double k1 = 2.0d;
        double b = 0.75d;
        double epslon = 0.05d;

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, pathDocuments);

        try
        {
            QueryConfiguration queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
            queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
            queryConfiguration.setProperty("bm25.k3","8d");


            LgteQuery lgteQuery = LgteQueryParser.parseQuery("contents:word1000",searcher,queryConfiguration);
            LgteHits lgteHits = searcher.search(lgteQuery);
            float s1S = lgteHits.getHits().score(0);
            assertEquals(lgteHits.doc(0).get("id"),"2");
            searcher.close();


            IndexReader readerMulti1 = LgteIndexManager.openReader(pathDocuments,Model.OkapiBM25Model);
            IndexReader readerMulti2 = LgteIndexManager.openReader(pathSentences,Model.OkapiBM25Model);
            Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
            readers.put("contents",readerMulti1);
            readers.put("sentences",readerMulti2);
            readers.put("doc_id",readerMulti2);
            readers.put("id",readerMulti2);
            LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
            lgteIsolatedIndexReader.addTreeMapping(readerMulti1,readerMulti2,"doc_id");


            searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,lgteIsolatedIndexReader);
            queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
            queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
            queryConfiguration.setProperty("bm25.k3","8d");
            queryConfiguration.setProperty("index.tree","true");

            lgteQuery = LgteQueryParser.parseQuery("contents:word1000",searcher,queryConfiguration);
            lgteHits = searcher.search(lgteQuery);
            assertEquals(s1S,lgteHits.score(0));
            assertEquals(lgteHits.doc(0).get("id"),"2_1");
            assertEquals(lgteHits.doc(1).get("id"),"2_2");
            assertEquals(lgteHits.doc(2).get("id"),"2_3");

            lgteQuery = LgteQueryParser.parseQuery("sentences:word1000",searcher,queryConfiguration);
            lgteHits = searcher.search(lgteQuery);
            assertEquals(lgteHits.doc(0).get("id"),"2_3");


            searcher.close();
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }

    }

    public static String debugStopTerm = "";

    private class ScoreDoc
    {
        public float score;
        public String id;

        public ScoreDoc(float score, String id) {
            this.score = score;
            this.id = id;
        }
    }

//    static class MyStringWrapperIterator extends BasicStringWrapperIterator
//    {
//
//        public MyStringWrapperIterator(Iterator iterator) {
//            super(iterator);
//        }
//
//    }
    //
    public static void main(String [] args)
    {
//        TextSimilarityScorer tfidf = new TextSimilarityScorer(new SimpleTokenizer(true,true));
//        BasicStringWrapper doc1 = new BasicStringWrapper("a b c d e f g h i j k l m");
//        BasicStringWrapper doc2 = new BasicStringWrapper("b c d e f g h i");
//        List<BasicStringWrapper> list = new ArrayList<BasicStringWrapper>();
//        list.add(doc1);
//        list.add(doc2);
//        MyStringWrapperIterator myStringWrapperIterator = new MyStringWrapperIterator(list.iterator());
//        tfidf.train(myStringWrapperIterator);
//        System.out.println("BM25:" + tfidf.bm25("a b","a b c d e f g h i j k l m"));
//        System.out.println("BM25:" + tfidf.bm25("a b","b c d e f g h i"));
        System.out.println(73938 % 7);
    }


}
