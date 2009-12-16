package pt.utl.ist.lucene.test.multiindexes;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.LgteIndexWriterIsolateFields;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 11:05:21
 * @email machadofisher@gmail.com
 */
public class TestAddDocumentsWithOutAllFields extends TestCase {
    private String pathUnique = Globals.INDEX_DIR + "/" + getClass().getName() + "unique";
    private String pathMulti = Globals.INDEX_DIR + "/" + getClass().getName() + "multi";

    String contents1Doc1 = "word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
//    String contents2Doc1 = "word1 word2 word3 word32 word1 word45 word56 word67";           Wll not use these fields
//    String contents1Doc2 = "word2 word3 word4 word55 word96 word2 word54 word33 wordss";
    String contents2Doc2 = "word2 word3 word4 word55";
    String contents1Doc3 = "word1 word100 word400 word555 word966 word544 word333 wordss";
    String contents2Doc3 = "word1 word100";

    protected void setUp() throws IOException {


        //This is the Index build in the old way with out those fields
        LgteIndexWriter writer = new LgteIndexWriter(pathUnique,true, Model.OkapiBM25Model);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1);
//        doc1.indexText("contents2",contents2Doc1);
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
//        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();



    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathUnique);
        Files.delDirsE(pathMulti);
    }



    public void testUpdateOneFieldPassingAllTheData() throws IOException, InvalidGeoException {



        //Now lets build an index with the isolation of fields to check if missing fields affect the indexes
        //Lgte avoid problems in score formulas adding all documents to all indexes
        //because at first place Lucene use the internal identifiers to score the documents so wee need the documents to be the same
        //in all indexes

         //This tell Lgte to index the document to these two indexes even if the fields are missing
        List<String> targetFields = new ArrayList<String>();
        targetFields.add("contents1");
        targetFields.add("contents2");

        LgteIndexWriterIsolateFields writerIsolate = new LgteIndexWriterIsolateFields(targetFields,pathMulti,true);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexString(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1);
//        doc1.indexText("contents2",contents2Doc1);
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexString(Globals.DOCUMENT_ID_FIELD, "2");
//        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexString(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);

        writerIsolate.addDocument(doc1);
        writerIsolate.addDocument(doc2);
        writerIsolate.addDocument(doc3);

        writerIsolate.close();



        double k1 = 2.0d;
        double b = 0.75d;
        double epslon = 0.05d;
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","" + epslon);
        queryConfiguration.setProperty("bm25.k1","" + k1);
        queryConfiguration.setProperty("bm25.b","" + b);






        //Transparent Reader
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, pathUnique);
        LgteIndexSearcherWrapper searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,
                new LgteIsolatedIndexReader(pathMulti,Model.OkapiBM25Model)
        );


        try{
            LgteQuery lgteQuery = LgteQueryParser.parseQuery("contents1:(word2 word67 word1*) contents2:(word1* word2 word67)",searcher,queryConfiguration);
            LgteHits lgteHits = searcher.search(lgteQuery);
            LgteQuery lgteQueryMulti = LgteQueryParser.parseQuery("contents1:(word1* word2 word67) contents2:(word1* word2 word67)",searcherMulti,queryConfiguration);
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
