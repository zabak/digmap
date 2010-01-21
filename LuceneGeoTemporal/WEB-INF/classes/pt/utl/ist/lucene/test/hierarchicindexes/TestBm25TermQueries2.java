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
public class TestBm25TermQueries2 extends TestCase {

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

    private double idf(double numDocs,double docFreq)
    {
        return Math.log((numDocs - docFreq + 0.5)/(docFreq+0.5))/Math.log(2.0d);
    }

    private double bm25(double idf, double tfDoc, double docLen, double avgDocLen, double k1, double b)
    {
        return idf *
                (
                        (tfDoc*(k1 + 1))
                                /
                                ( tfDoc + k1*(1.0 - b + b*(docLen/avgDocLen)))
                );
    }
    public void testRange() throws IOException, InvalidGeoException {

        double k1 = 2.0d;
        double b = 0.75d;
        double epslon = 0.05d;

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, pathSentences);


        //Calculate BM25 for each test document
        //will calculate for query "word1 word2"
        double numDocs = 2;
        double numVDocs = 6;
        double vdoc1len = 15;
        double vdoc2len = 9;
        double vdoc3len = 8;
        double vdoc5len = 3;
        double document1len = vdoc1len+vdoc2len+vdoc3len;
        double document2len = 9;
        double colSize = vdoc1len + vdoc2len + vdoc3len + 9;

        double avgVDocLen = (colSize + 1.0) / numVDocs;
        double avgDocLen = (colSize + 1.0) / numDocs;
        double vdocFreqWord1 = 2;
        double vdocFreqWord2 = 2;
        double vdocFreqWord3 = 3;

        double docFreqWord1 = 1;
        double docFreqWord2 = 1;
        double docFreqWord3 = 2;

        double vidfWord1 = idf(numVDocs,vdocFreqWord1);
        double vidfWord2 = idf(numVDocs,vdocFreqWord2);
        double vidfWord3 = idf(numVDocs,vdocFreqWord3);

        double idfWord1 = idf(numDocs,docFreqWord1);
        double idfWord2 = idf(numDocs,docFreqWord2);
        double idfWord3 = idf(numDocs,docFreqWord3);

        //epslon policy
        if(vidfWord1 <= 0)
            vidfWord1 = epslon;
        if(vidfWord2 <= 0)
            vidfWord2 = epslon;
        if(vidfWord3 <= 0)
            vidfWord3 = epslon;

        if(idfWord1 <= 0)
            idfWord1 = epslon;
        if(idfWord2 <= 0)
            idfWord2 = epslon;
        if(idfWord3 <= 0)
            idfWord3 = epslon;

        double tfVDoc1Word1 = 2;
        double tfVDoc2Word1 = 0;
        double tfVDoc3Word1 = 1;
        double tfVDoc1Word2 = 2;
        double tfVDoc2Word2 = 2;
        double tfVDoc3Word2 = 0;
        double tfVDoc1Word3 = 1;
        double tfVDoc2Word3 = 1;
        double tfVDoc5Word3 = 1;


        double tfDoc1Word1 = 3; //vdoc1 = 2 , vdoc2=0 , vdoc3 = 1
        double tfDoc2Word1 = 0;

        double tfDoc1Word2 = 4;
        double tfDoc2Word2 = 0;

        double tfDoc1Word3 = 2;
        double tfDoc2Word3 = 1;



        float scoreV1 = (float) (bm25(vidfWord1,tfVDoc1Word1,vdoc1len,avgVDocLen,k1,b) + bm25(vidfWord2,tfVDoc1Word2,vdoc1len,avgVDocLen,k1,b) + bm25(vidfWord3,tfVDoc1Word3,vdoc1len,avgVDocLen,k1,b));
        float scoreV2 = (float) (bm25(vidfWord1,tfVDoc2Word1,vdoc2len,avgVDocLen,k1,b) + bm25(vidfWord2,tfVDoc2Word2,vdoc2len,avgVDocLen,k1,b) + bm25(vidfWord3,tfVDoc2Word3,vdoc2len,avgVDocLen,k1,b));
        float scoreV3 = (float) (bm25(vidfWord1,tfVDoc3Word1,vdoc3len,avgVDocLen,k1,b) + bm25(vidfWord2,tfVDoc3Word2,vdoc3len,avgVDocLen,k1,b));
        float scoreV5 = (float) (bm25(vidfWord3,tfVDoc5Word3,vdoc5len,avgVDocLen,k1,b));

        float score1 = (float) (bm25(idfWord1,tfDoc1Word1,document1len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc1Word2,document1len,avgDocLen,k1,b) + bm25(idfWord3,tfDoc1Word3,document1len,avgDocLen,k1,b));
        float score2 = (float) (bm25(idfWord1,tfDoc2Word1,document2len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc2Word2,document2len,avgDocLen,k1,b) + bm25(idfWord3,tfDoc2Word3,document2len,avgDocLen,k1,b));

        //now will sort results using a wrapper for a pair <score,docId>
        TestBm25TermQueries2.ScoreDoc scoreDocV1 = new TestBm25TermQueries2.ScoreDoc(scoreV1,"1_1");
        TestBm25TermQueries2.ScoreDoc scoreDocV2 = new TestBm25TermQueries2.ScoreDoc(scoreV2,"1_2");
        TestBm25TermQueries2.ScoreDoc scoreDocV3 = new TestBm25TermQueries2.ScoreDoc(scoreV3,"1_3");
        TestBm25TermQueries2.ScoreDoc scoreDocV5 = new TestBm25TermQueries2.ScoreDoc(scoreV5,"2_2");
        List<ScoreDoc> scoreVDocs = new ArrayList<ScoreDoc>();
        scoreVDocs.add(scoreDocV1);
        scoreVDocs.add(scoreDocV2);
        scoreVDocs.add(scoreDocV3);
        scoreVDocs.add(scoreDocV5);
        Collections.sort(scoreVDocs,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25TermQueries2.ScoreDoc o1, TestBm25TermQueries2.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });


        TestBm25TermQueries2.ScoreDoc scoreDoc1 = new TestBm25TermQueries2.ScoreDoc(score1,"1");
        TestBm25TermQueries2.ScoreDoc scoreDoc2 = new TestBm25TermQueries2.ScoreDoc(score2,"2");
        List<TestBm25TermQueries2.ScoreDoc> scoreDocs = new ArrayList<TestBm25TermQueries2.ScoreDoc>();
        scoreDocs.add(scoreDoc1);
        scoreDocs.add(scoreDoc2);
        Collections.sort(scoreDocs,new Comparator<TestBm25TermQueries2.ScoreDoc>()
        {
            public int compare(TestBm25TermQueries2.ScoreDoc o1, TestBm25TermQueries2.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });



        //now will create a configuration and execute a search

        try
        {
            QueryConfiguration queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
            queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
            queryConfiguration.setProperty("bm25.k3","8d");


            LgteQuery lgteQuery = LgteQueryParser.parseQuery("sentences:word1 sentences:word2 sentences:word3",searcher,queryConfiguration);

            LgteHits lgteHits = searcher.search(lgteQuery);

            System.out.println("EXPECTED");
            System.out.println("Sentence:" + scoreVDocs.get(0).id + ":"  + scoreVDocs.get(0).score);
            System.out.println("Sentence:" + scoreVDocs.get(1).id + ":" + scoreVDocs.get(1).score);
            System.out.println("Sentence:" + scoreVDocs.get(2).id + ":" + scoreVDocs.get(2).score);
            System.out.println("Sentence:" + scoreVDocs.get(3).id + ":" + scoreVDocs.get(3).score);
            System.out.println("RETURN:");
            System.out.println("Sentence:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("Sentence:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));
            System.out.println("Sentence:" + lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(2));
            System.out.println("Sentence:" + lgteHits.doc(3).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(3));

            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD), scoreVDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD), scoreVDocs.get(1).id);
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD), scoreVDocs.get(2).id);
            assertEquals(lgteHits.doc(3).get(Globals.DOCUMENT_ID_FIELD), scoreVDocs.get(3).id);

            assertTrue(lgteHits.score(0) - scoreVDocs.get(0).score < 0.0001);
            assertTrue(lgteHits.score(1) - scoreVDocs.get(1).score < 0.0001);
            assertTrue(lgteHits.score(2) - scoreVDocs.get(2).score < 0.0001);
            assertTrue(lgteHits.score(3) - scoreVDocs.get(3).score < 0.0001);



            searcher.close();
            queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
            queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
            queryConfiguration.setProperty("bm25.k3","8d");
            searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, pathDocuments);
            lgteQuery = LgteQueryParser.parseQuery("contents:word1 contents:word2 contents:word3",searcher,queryConfiguration);
            lgteHits = searcher.search(lgteQuery);
            System.out.println("EXPECTED");
            System.out.println("doc:" + scoreDocs.get(0).id + ":"  + scoreDocs.get(0).score);
            System.out.println("doc:" + scoreDocs.get(1).id + ":" + scoreDocs.get(1).score);
            System.out.println("RETURN:");
            System.out.println("doc:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));

            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD), scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD), scoreDocs.get(1).id);

            assertTrue(lgteHits.score(0) - scoreDocs.get(0).score < 0.0001);
            assertTrue(lgteHits.score(1) - scoreDocs.get(1).score < 0.0001);

            System.out.println("SCHEME score(sentence) + score(contents)");
            System.out.println("Sentence " + scoreVDocs.get(0).id + " (stm1 + doc1):" + (scoreVDocs.get(0).score + scoreDocs.get(0).score) + " = " + (scoreVDocs.get(0).score) + "+" + (scoreDocs.get(0).score));
            System.out.println("Sentence " + scoreVDocs.get(1).id + " (stm2 + doc1):" + (scoreVDocs.get(1).score + scoreDocs.get(0).score) + " = " + (scoreVDocs.get(1).score) + "+" + (scoreDocs.get(0).score));
            System.out.println("Sentence " + scoreVDocs.get(2).id + " (stm3 + doc1):" + (scoreVDocs.get(2).score + scoreDocs.get(0).score) + " = " + (scoreVDocs.get(2).score) + "+" + (scoreDocs.get(0).score));
            System.out.println("Sentence " + scoreVDocs.get(3).id + " (stm5 + doc2):" + (scoreVDocs.get(3).score + scoreDocs.get(1).score) + " = " + (scoreVDocs.get(3).score) + "+" + (scoreDocs.get(1).score));



            searcher.close();


            System.out.println("##########################################################");




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

             lgteQuery = LgteQueryParser.parseQuery("contents:word1 contents:word2 contents:word3 sentences:word1 sentences:word2 sentences:word3",searcher,queryConfiguration);
//            lgteQuery = LgteQueryParser.parseQuery("contents:(word1 word2 word3)^0.3 sentences:(word1 word2 word3)^0.7",searcher,queryConfiguration);
            lgteHits = searcher.search(lgteQuery);
            System.out.println("SCHEME score(sentences) + score(contents)");
            System.out.println("Stm " + lgteHits.doc(0).get("id") + ":" + lgteHits.doc(0).get("doc_id") + " (stm1 + doc1):" + lgteHits.score(0));
            System.out.println("Stm " + lgteHits.doc(1).get("id") + ":" + lgteHits.doc(1).get("doc_id") + " (stm2 + doc1):" + lgteHits.score(1));
            System.out.println("Stm " + lgteHits.doc(2).get("id") + ":" + lgteHits.doc(2).get("doc_id") + " (stm3 + doc1):" + lgteHits.score(2));
            System.out.println("Stm " + lgteHits.doc(3).get("id") + ":" + lgteHits.doc(3).get("doc_id") + " (stm5 + doc2):" + lgteHits.score(3));

            assertEquals(lgteHits.doc(0).get("id"),scoreVDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get("id"),scoreVDocs.get(1).id);
            assertEquals(lgteHits.doc(2).get("id"),scoreVDocs.get(2).id);
            assertEquals(lgteHits.doc(3).get("id"),scoreVDocs.get(3).id);

            assertEquals(lgteHits.doc(0).get("doc_id"),scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get("doc_id"),scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(2).get("doc_id"),scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(3).get("doc_id"),scoreDocs.get(1).id);

            assertTrue(lgteHits.score(0)-(scoreVDocs.get(0).score + scoreDocs.get(0).score) <0.001);
            assertTrue(lgteHits.score(1)-(scoreVDocs.get(1).score + scoreDocs.get(0).score) <0.001);
            assertTrue(lgteHits.score(2)-(scoreVDocs.get(2).score + scoreDocs.get(0).score) <0.001);
            assertTrue(lgteHits.score(3)-(scoreVDocs.get(3).score + scoreDocs.get(1).score) <0.001);

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
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
