package pt.utl.ist.lucene.test.models;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.LgteIndexWriterIsolateFields;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.*;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.index.LgteIsolatedIndexReader;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestBm25WithMultiFields extends TestCase {

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException {

        
        /**************
         * CONTENTS INDEXED ISOLATED FROM TITLE AND IN SEPARATED STEPS
         *
         * Lets index Contents
         */
        //This list is unecessary because all documents have the field but if that is not true we need to use the list
        List<String> targetFields = new ArrayList<String>();
        targetFields.add("contents");
        LgteIndexWriterIsolateFields writer = new LgteIndexWriterIsolateFields(targetFields,path,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        //word1 2 times
        //word2 2 time
        //len contents 15
        doc1.indexText("contents","word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33");
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        //word1 0 times
        //word2 2 time
        //len contents 9
        doc2.indexText("contents","word2 word3 word4 word55 word96 word2 word54 word33 wordss");
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        //word1 1 times
        //word2 0 time
        //len contents 8
        doc3.indexText("contents","word1 word100 word400 word555 word966 word544 word333 wordss");
        //CollectionTokensSize contents 32
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

        /**************
         * TITLE INDEXED ISOLATED AND IN SEPARATED STEPS
         */
        targetFields = new ArrayList<String>();
        targetFields.add("title");
        writer = new LgteIndexWriterIsolateFields(targetFields,path,true);

        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        //title1 1 times
        //title5 0 times
        //len title 3
        doc1.indexText("title","title1 title2 title3");

        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        //title1 1 times
        //title5 0 times
        //len title 2
        doc2.indexText("title","title1 title4");
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        //title1 0 times
        //title5 1 times
        //len title 2
        doc3.indexText("title","title5 title6");
        //CollectionTokensSize title 7
        //title1 docFreq 2
        //title5 docFreq 1
        //doc 1 tf(title1) = 1
        //doc 2 tf(title1) = 1
        //doc 3 tf(title1) = 0
        //doc 1 tf(title5) = 0
        //doc 2 tf(title5) = 0
        //doc 3 tf(title5) = 1
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

        double k1 = 1.2d;
        double b = 0.75d;
        double factorTitle = 0.7;
        double factorContents = 0.3;
        double epslon = 0.05d;

        Map<String,String> map = new HashMap<String,String>();
        map.put("contents",path + "/contents_field");
        map.put("title",path + "/title_field");
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,
                /********NEW******/
                new LgteIsolatedIndexReader(map,Model.OkapiBM25Model)
                /********NEW******/
        );


        //Calculate BM25 for each test document
        //will calculate for query "word1 word2"
        double numDocs = 3;
        double doc1len = 15;
        double doc2len = 9;
        double doc3len = 8;
        double colSize = doc1len + doc2len + doc3len;

        double avgDocLen = (colSize + 1.0) / numDocs;
        double docFreqWord1 = 2;
        double docFreqWord2 = 2;
        double idfWord1 = idf(numDocs,docFreqWord1);
        double idfWord2 = idf(numDocs,docFreqWord2);
        //epslon policy
        if(idfWord1 < 0)
            idfWord1 = epslon;
        if(idfWord2 < 0)
            idfWord2 = epslon;
        double tfDoc1Word1 = 2;
        double tfDoc2Word1 = 0;
        double tfDoc3Word1 = 1;
        double tfDoc1Word2 = 2;
        double tfDoc2Word2 = 2;
        double tfDoc3Word2 = 0;



        float score1contents = (float) (bm25(idfWord1,tfDoc1Word1,doc1len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc1Word2,doc1len,avgDocLen,k1,b));
        float score2contents = (float) (bm25(idfWord1,tfDoc2Word1,doc2len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc2Word2,doc2len,avgDocLen,k1,b));
        float score3contents = (float) (bm25(idfWord1,tfDoc3Word1,doc3len,avgDocLen,k1,b) + bm25(idfWord2,tfDoc3Word2,doc3len,avgDocLen,k1,b));



        double doc1Titlelen = 3;
        double doc2Titlelen = 2;
        double doc3Titlelen = 2;
        double colTitleSize = doc1Titlelen + doc2Titlelen + doc3Titlelen;

        double avgDocTitleLen = (colTitleSize + 1.0) / numDocs;
        double docFreqTitle1 = 2;
        double docFreqTitle5 = 1;
        double idfTitle1 = idf(numDocs,docFreqTitle1);
        double idfTitle5 = idf(numDocs,docFreqTitle5);
        //epslon policy
        if(idfTitle1 < 0)
            idfTitle1 = epslon;
        if(idfTitle5 < 0)
            idfTitle5 = epslon;
        double tfDoc1Title1 = 1;
        double tfDoc2Title1 = 1;
        double tfDoc3Title1 = 0;
        double tfDoc1Title5 = 0;
        double tfDoc2Title5 = 0;
        double tfDoc3Title5 = 1;



        float score1Title = (float) (bm25(idfTitle1,tfDoc1Title1,doc1Titlelen,avgDocTitleLen,k1,b) + bm25(idfTitle5,tfDoc1Title5,doc1Titlelen,avgDocTitleLen,k1,b));
        float score2Title = (float) (bm25(idfTitle1,tfDoc2Title1,doc2Titlelen,avgDocTitleLen,k1,b) + bm25(idfTitle5,tfDoc2Title5,doc2Titlelen,avgDocTitleLen,k1,b));
        float score3Title = (float) (bm25(idfTitle1,tfDoc3Title1,doc3Titlelen,avgDocTitleLen,k1,b) + bm25(idfTitle5,tfDoc3Title5,doc3Titlelen,avgDocTitleLen,k1,b));

        double score1 = score1contents*factorContents + score1Title*factorTitle;
        double score2 = score2contents*factorContents + score2Title*factorTitle;
        double score3 = score3contents*factorContents + score3Title*factorTitle;
        //now will sort results using a wrapper for a pair <score,docId>
        TestBm25WithMultiFields.ScoreDoc scoreDoc1 = new TestBm25WithMultiFields.ScoreDoc(score1,"1");
        TestBm25WithMultiFields.ScoreDoc scoreDoc2 = new TestBm25WithMultiFields.ScoreDoc(score2,"2");
        TestBm25WithMultiFields.ScoreDoc scoreDoc3 = new TestBm25WithMultiFields.ScoreDoc(score3,"3");
        List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
        scoreDocs.add(scoreDoc1);
        scoreDocs.add(scoreDoc2);
        scoreDocs.add(scoreDoc3);
        Collections.sort(scoreDocs,new Comparator<ScoreDoc>()
        {
            public int compare(TestBm25WithMultiFields.ScoreDoc o1, TestBm25WithMultiFields.ScoreDoc o2) {
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
//            queryConfiguration.setProperty("bm25.k3","8d");

            LgteQuery lgteQuery = LgteQueryParser.parseQuery("contents:(word1 word2)^" + factorContents + " title:(title1 title5)^" + factorTitle,searcher,queryConfiguration);

            LgteHits lgteHits = searcher.search(lgteQuery);

            System.out.println("EXPECTED");
            System.out.println("doc:" + scoreDocs.get(0).id + ":"  + scoreDocs.get(0).score);
            System.out.println("doc:" + scoreDocs.get(1).id + ":" + scoreDocs.get(1).score);
            System.out.println("doc:" + scoreDocs.get(2).id + ":" + scoreDocs.get(2).score);
            System.out.println("RETURN:");
            System.out.println("doc:" + lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(1));
            System.out.println("doc:" + lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD) + ":" + lgteHits.score(2));

            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(0).id);
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(1).id);
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD),scoreDocs.get(2).id);

            assertTrue(Math.abs(lgteHits.score(0) - scoreDocs.get(0).score) < 0.0001);
            assertTrue(Math.abs(lgteHits.score(1) - scoreDocs.get(1).score) < 0.0001);
            assertTrue(Math.abs(lgteHits.score(2) - scoreDocs.get(2).score) < 0.0001);

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
