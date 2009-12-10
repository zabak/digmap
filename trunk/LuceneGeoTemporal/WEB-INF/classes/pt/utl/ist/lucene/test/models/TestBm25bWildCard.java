package pt.utl.ist.lucene.test.models;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.pjaol.search.geo.utils.InvalidGeoException;
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
public class TestBm25bWildCard extends TestCase {

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException {
        LgteIndexWriter writer = new LgteIndexWriter(path,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        //word3 2 times
        //word32 1 time
        //word33 1 time
        //word333 0 times
        //word2 2 time
        //len 15
        doc1.indexText("contents","word1 word2 word3 word32 word1 word3 word56 word67 word67 word2 word67 word88 word99 word99 word33");



        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        //word3 1 times
        //word32 0 times
        //word33 1 time
        //word333 0 times
        //word2 2 times
        //len 9
        doc2.indexText("contents","word2 word3 word4 word55 word96 word2 word54 word33 wordss");



        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        //word3 0 times
        //word32 0 times
        //word33 0 times
        //word333 1 times
        //word2 0 times
        //len 8
        doc3.indexText("contents","word1 word100 word400 word555 word966 word544 word333 wordss");


        //CollectionTokensSize 32
        //word3 docFreq 2
        //word32 docFreq 1
        //word33 docFreq 2
        //word333 docFreq 1
        //word2 docFreq 2

        //doc 1 tf(word3) = 2
        //doc 2 tf(word3) = 1
        //doc 3 tf(word3) = 0
        //doc 1 tf(word32) = 1
        //doc 2 tf(word32) = 0
        //doc 3 tf(word32) = 0
        //doc 1 tf(word33) = 1
        //doc 2 tf(word33) = 1
        //doc 3 tf(word33) = 0
        //doc 1 tf(word333) = 0
        //doc 2 tf(word333) = 0
        //doc 3 tf(word333) = 1
        //doc 1 tf(word2) = 2
        //doc 2 tf(word2) = 2
        //doc 3 tf(word2) = 0

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    private double idf(double numDocs,double docFreq)
    {
        return Math.log((numDocs + 0.5)/(docFreq+0.5))/Math.log(2.0d);
    }

   /*
    *   Terrier Formula with keyFreq = 1
    *   keyFreq is the frequency of the term in the query, LGTE will assume 1 so we do the same here
    */
    private double bm25(double idf, double tfDoc, double docLen, double avgDocLen, double k1,double k3, double b)
    {
        double K = k1 * ((1 - b) + b * docLen / avgDocLen) + tfDoc;
        return  idf *
                ((k1 + 1d) * tfDoc / (K + tfDoc)) *
                ((k3+1)*1.0/(k3+1.0));
    }
    
    public void testRange() throws IOException, InvalidGeoException
    {

        double k1 = 1.2d;
        double b = 0.75d;
        double k3 = 8d;
//        double epslon = 0.05d;

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.BM25b, path);


        //Calculate BM25 for each test document
        //will calculate for query "word3* word2"
        double numDocs = 3;
        double doc1len = 15;
        double doc2len = 9;
        double doc3len = 8;
        double colSize = doc1len + doc2len + doc3len;

        double avgDocLen = (colSize + 1.0) / numDocs;

        double docFreqWord3 = 2;
        double docFreqWord32 = 1;
        double docFreqWord33 = 2;
        double docFreqWord333 = 1;
        double docFreqWord2 = 2;

        double idfWord3 = idf(numDocs,docFreqWord3);
        double idfWord32 = idf(numDocs,docFreqWord32);
        double idfWord33 = idf(numDocs,docFreqWord33);
        double idfWord333 = idf(numDocs,docFreqWord333);
        double idfWord2 = idf(numDocs,docFreqWord2);

        double tfDoc1Word3 = 2;
        double tfDoc2Word3 = 1;
        double tfDoc3Word3 = 0;
        double tfDoc1Word32 = 1;
        double tfDoc2Word32 = 0;
        double tfDoc3Word32 = 0;
        double tfDoc1Word33 = 1;
        double tfDoc2Word33 = 1;
        double tfDoc3Word33 = 0;
        double tfDoc1Word333 = 0;
        double tfDoc2Word333 = 0;
        double tfDoc3Word333 = 1;
        double tfDoc1Word2 = 2;
        double tfDoc2Word2 = 2;
        double tfDoc3Word2 = 0;



        float score1 = (float) (bm25(idfWord3,tfDoc1Word3,doc1len,avgDocLen,k1,k3,b) + bm25(idfWord32,tfDoc1Word32,doc1len,avgDocLen,k1,k3,b) + bm25(idfWord33,tfDoc1Word33,doc1len,avgDocLen,k1,k3,b) + bm25(idfWord333,tfDoc1Word333,doc1len,avgDocLen,k1,k3,b) + bm25(idfWord2,tfDoc1Word2,doc1len,avgDocLen,k1,k3,b));
        float score2 = (float) (bm25(idfWord3,tfDoc2Word3,doc2len,avgDocLen,k1,k3,b) + bm25(idfWord32,tfDoc2Word32,doc2len,avgDocLen,k1,k3,b) + bm25(idfWord33,tfDoc2Word33,doc2len,avgDocLen,k1,k3,b) + bm25(idfWord333,tfDoc2Word333,doc2len,avgDocLen,k1,k3,b) + bm25(idfWord2,tfDoc2Word2,doc2len,avgDocLen,k1,k3,b));
        float score3 = (float) (bm25(idfWord3,tfDoc3Word3,doc3len,avgDocLen,k1,k3,b) + bm25(idfWord32,tfDoc3Word32,doc3len,avgDocLen,k1,k3,b) + bm25(idfWord33,tfDoc3Word33,doc3len,avgDocLen,k1,k3,b) + bm25(idfWord333,tfDoc3Word333,doc3len,avgDocLen,k1,k3,b) + bm25(idfWord2,tfDoc3Word2,doc3len,avgDocLen,k1,k3,b));

        //now will sort results using a wrapper for a pair <score,docId>
        TestBm25bWildCard.ScoreDoc scoreDoc1 = new TestBm25bWildCard.ScoreDoc(score1,"1");
        TestBm25bWildCard.ScoreDoc scoreDoc2 = new TestBm25bWildCard.ScoreDoc(score2,"2");
        TestBm25bWildCard.ScoreDoc scoreDoc3 = new TestBm25bWildCard.ScoreDoc(score3,"3");
        List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
        scoreDocs.add(scoreDoc1);
        scoreDocs.add(scoreDoc2);
        scoreDocs.add(scoreDoc3);
        Collections.sort(scoreDocs,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25bWildCard.ScoreDoc o1, TestBm25bWildCard.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });



        //now will create a configuration and execute a search

        try
        {
            QueryConfiguration queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","dont_subtract_n_t");
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
            queryConfiguration.setProperty("bm25.k3","" + k3);

            LgteQuery lgteQuery = LgteQueryParser.parseQuery("word3* word2",searcher,queryConfiguration);

            LgteHits lgteHits = searcher.search(lgteQuery);

            System.out.println("doc:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));
            System.out.println("doc:" + lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(2));

            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(1).id);
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(2).id);

            assertTrue(lgteHits.score(0) - scoreDocs.get(0).score < 0.0001);
            assertTrue(lgteHits.score(1) - scoreDocs.get(1).score < 0.0001);
            assertTrue(lgteHits.score(2) - scoreDocs.get(2).score < 0.0001);

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }

    public class ScoreDoc
    {
        public double score;
        public String id;

        public ScoreDoc(double score, String id) {
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
////
//    public static void main(String [] args)
//    {
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
//    }


}
