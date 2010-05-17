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
public class TestBm25Normalized extends TestCase {

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException {
        LgteIndexWriter writer = new LgteIndexWriter(path,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        //word1 2 times
        //word2 2 time
        //len 15
        doc1.indexText("contents","word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33");
        doc1.indexText("contents2","word1 word2 word33 word88 word99 word99 word33");



        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        //word1 0 times
        //word2 2 time
        //len 9
        doc2.indexText("contents","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        doc2.indexText("contents2","word4 word33 word5 word4 word4");



        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        //word1 1 times
        //word2 0 time
        //len 8
        doc3.indexText("contents","word1 word100 word400 word555 word966 word544 word333 wordss");
        doc3.indexText("contents2","word10 word4 word7 word10");


        //CollectionTokensSize 32
        //word1 docFreq 2
        //word2 docFreq 2
        //doc 1 tf(word1) = 2
        //doc 2 tf(word1) = 0
        //doc 3 tf(word1) = 1
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

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.BM25Normalized, path);


        //Calculate BM25 for each test document
        //will calculate for query "word1 word2"
        double numDocs = 3;
        double doc1len = 15;
        double doc2len = 9;
        double doc3len = 8;
        double colSize = doc1len + doc2len + doc3len;

        double doc1len2 = 7;
        double doc2len2 = 5;
        double doc3len2 = 4;
        double colSize2 = doc1len2 + doc2len2 + doc3len2;

        double avgDocLen = (colSize + 1.0) / numDocs;
        double docFreqWord1 = 2;
        double docFreqWord2 = 2;
        double idfWord1 = idf(numDocs,docFreqWord1);
        double idfWord2 = idf(numDocs,docFreqWord2);


        double avgDocLen2 = (colSize2 + 1.0) / numDocs;
        double docFreqWord33_2 = 2;
        double docFreqWord4_2 = 2;
        double idfWord33_2 = idf(numDocs,docFreqWord33_2);
        double idfWord4_2 = idf(numDocs,docFreqWord4_2);


        //epslon policy
        if(idfWord1 <= 0)
            idfWord1 = epslon;
        if(idfWord2 <= 0)
            idfWord2 = epslon;
        if(idfWord33_2 <= 0)
            idfWord33_2 = epslon;
        if(idfWord4_2 <= 0)
            idfWord4_2 = epslon;

        double tfDoc1Word1 = 2;
        double tfDoc2Word1 = 0;
        double tfDoc3Word1 = 1;
        double tfDoc1Word2 = 2;
        double tfDoc2Word2 = 2;
        double tfDoc3Word2 = 0;


        double tfDoc1Word33_2 = 2;
        double tfDoc2Word33_2 = 1;
        double tfDoc3Word33_2 = 0;
        double tfDoc1Word4_2 = 0;
        double tfDoc2Word4_2 = 3;
        double tfDoc3Word4_2 = 1;


        float score1 = (float) (bm25(idfWord1,tfDoc1Word1,doc1len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc1Word2,doc1len,avgDocLen,k1,b));
        float score2 = (float) (bm25(idfWord1,tfDoc2Word1,doc2len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc2Word2,doc2len,avgDocLen,k1,b));
        float score3 = (float) (bm25(idfWord1,tfDoc3Word1,doc3len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc3Word2,doc3len,avgDocLen,k1,b));


        float score1_2 = (float) (bm25(idfWord33_2,tfDoc1Word33_2,doc1len2,avgDocLen2,k1,b) + bm25(idfWord4_2,tfDoc1Word4_2,doc1len2,avgDocLen2,k1,b));
        float score2_2 = (float) (bm25(idfWord33_2,tfDoc2Word33_2,doc2len2,avgDocLen2,k1,b) + bm25(idfWord4_2,tfDoc2Word4_2,doc2len2,avgDocLen2,k1,b));
        float score3_2 = (float) (bm25(idfWord33_2,tfDoc3Word33_2,doc3len2,avgDocLen2,k1,b) + bm25(idfWord4_2,tfDoc3Word4_2,doc3len2,avgDocLen2,k1,b));

        //now will sort results using a wrapper for a pair <score,docId>
        TestBm25Normalized.ScoreDoc scoreDoc1 = new TestBm25Normalized.ScoreDoc(score1,"1");
        TestBm25Normalized.ScoreDoc scoreDoc2 = new TestBm25Normalized.ScoreDoc(score2,"2");
        TestBm25Normalized.ScoreDoc scoreDoc3 = new TestBm25Normalized.ScoreDoc(score3,"3");
        List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
        scoreDocs.add(scoreDoc1);
        scoreDocs.add(scoreDoc2);
        scoreDocs.add(scoreDoc3);
        Collections.sort(scoreDocs,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25Normalized.ScoreDoc o1, TestBm25Normalized.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });

        //now will create a configuration and execute a search

        double maxScoreContents = scoreDocs.get(0).score;

        System.out.println("EXPECTED IN CONTENTS");
        System.out.println("doc:" + scoreDocs.get(0).id + ":"  + scoreDocs.get(0).score + " > normalized " + (scoreDocs.get(0).score / maxScoreContents));
        System.out.println("doc:" + scoreDocs.get(1).id + ":" + scoreDocs.get(1).score  + " > normalized " + (scoreDocs.get(1).score / maxScoreContents));
        System.out.println("doc:" + scoreDocs.get(2).id + ":" + scoreDocs.get(2).score  + " > normalized " + (scoreDocs.get(2).score / maxScoreContents));


        TestBm25Normalized.ScoreDoc scoreDoc1_2 = new TestBm25Normalized.ScoreDoc(score1_2,"1");
        TestBm25Normalized.ScoreDoc scoreDoc2_2 = new TestBm25Normalized.ScoreDoc(score2_2,"2");
        TestBm25Normalized.ScoreDoc scoreDoc3_2 = new TestBm25Normalized.ScoreDoc(score3_2,"3");
        List<ScoreDoc> scoreDocs_2 = new ArrayList<ScoreDoc>();
        scoreDocs_2.add(scoreDoc1_2);
        scoreDocs_2.add(scoreDoc2_2);
        scoreDocs_2.add(scoreDoc3_2);
        Collections.sort(scoreDocs_2,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25Normalized.ScoreDoc o1, TestBm25Normalized.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });
        double maxScoreContents2 = scoreDocs_2.get(0).score;

        System.out.println("EXPECTED IN CONTENTS2");
        System.out.println("doc:" + scoreDocs_2.get(0).id + ":"  + scoreDocs_2.get(0).score + " > normalized " + (scoreDocs_2.get(0).score / maxScoreContents2));
        System.out.println("doc:" + scoreDocs_2.get(1).id + ":" + scoreDocs_2.get(1).score  + " > normalized " + (scoreDocs_2.get(1).score / maxScoreContents2));
        System.out.println("doc:" + scoreDocs_2.get(2).id + ":" + scoreDocs_2.get(2).score  + " > normalized " + (scoreDocs_2.get(2).score / maxScoreContents2));


        //now will sort results using a wrapper for a pair <score,docId>
        TestBm25Normalized.ScoreDoc scoreDoc1_t = new TestBm25Normalized.ScoreDoc(score1/maxScoreContents + score1_2/maxScoreContents2,"1");
        TestBm25Normalized.ScoreDoc scoreDoc2_t = new TestBm25Normalized.ScoreDoc(score2/maxScoreContents + score2_2/maxScoreContents2,"2");
        TestBm25Normalized.ScoreDoc scoreDoc3_t = new TestBm25Normalized.ScoreDoc(score3/maxScoreContents + score3_2/maxScoreContents2,"3");
        List<ScoreDoc> scoreDocsTotal = new ArrayList<ScoreDoc>();
        scoreDocsTotal.add(scoreDoc1_t);
        scoreDocsTotal.add(scoreDoc2_t);
        scoreDocsTotal.add(scoreDoc3_t);
        Collections.sort(scoreDocsTotal,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25Normalized.ScoreDoc o1, TestBm25Normalized.ScoreDoc o2) {
                if(o1.score < o2.score) return 1;
                else if(o1.score == o2.score) return 0;
                else return -1;
            }
        });


        try
        {
            QueryConfiguration queryConfiguration = new QueryConfiguration();
            queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
            queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
            queryConfiguration.setProperty("bm25.k1","" + k1);
            queryConfiguration.setProperty("bm25.b","" + b);
//            queryConfiguration.setProperty("bm25.k3","8d");

            LgteQuery lgteQuery = LgteQueryParser.parseQuery("contents:word1 contents:word2 contents2:word33 contents2:word4",searcher,queryConfiguration);

            System.out.println(searcher.explain(lgteQuery,0));
            System.out.println(searcher.explain(lgteQuery,1));
            System.out.println(searcher.explain(lgteQuery,2));
            LgteHits lgteHits = searcher.search(lgteQuery);

            System.out.println("EXPECTED");
            System.out.println("doc:" + scoreDocsTotal.get(0).id + ":"  + scoreDocsTotal.get(0).score );
            System.out.println("doc:" + scoreDocsTotal.get(1).id + ":" + scoreDocsTotal.get(1).score  );
            System.out.println("doc:" + scoreDocsTotal.get(2).id + ":" + scoreDocsTotal.get(2).score  );
            System.out.println("RETURN:");
            System.out.println("doc:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));
            System.out.println("doc:" + lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(2));



            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD),scoreDocsTotal.get(0).id);
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD),scoreDocsTotal.get(1).id);
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD),scoreDocsTotal.get(2).id);

            assertTrue(lgteHits.score(0) - scoreDocsTotal.get(0).score < 0.0001 && lgteHits.score(0) - scoreDocsTotal.get(0).score > -0.0001);
            assertTrue(lgteHits.score(1) - scoreDocsTotal.get(1).score < 0.0001 && lgteHits.score(1) - scoreDocsTotal.get(1).score > -0.0001);
            assertTrue(lgteHits.score(2) - scoreDocsTotal.get(2).score < 0.0001 && lgteHits.score(2) - scoreDocsTotal.get(2).score > -0.0001);

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }

    private class ScoreDoc
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
