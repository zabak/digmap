package pt.utl.ist.lucene.test.multiindexes;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.LgteIndexWriterIsolateFields;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 11:05:21
 * @email machadofisher@gmail.com
 */
public class TestUpdateJustOneField extends TestCase {
    private String pathUnique = Globals.INDEX_DIR + "/" + getClass().getName() + "unique";
    private String pathMulti1 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi1";
    private String pathMulti2 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi2";

    String contents1Doc1OLD = "word1 word2";
    String contents1Doc1UPDATED = "word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
    String contents2Doc1 = "word1 word2 word3 word32 word1 word45 word56 word67";
    String contents1Doc2 = "word2 word3 word4 word55 word96 word2 word54 word33 wordss";
    String contents2Doc2 = "word2 word3 word4 word55";
    String contents1Doc3 = "word1 word100 word400 word555 word966 word544 word333 wordss";
    String contents2Doc3 = "word1 word100";

    protected void setUp() throws IOException {




        //This is the Index build in the old way and will be indexed with the UPDATED stuff

        LgteIndexWriter writer = new LgteIndexWriter(pathUnique,true, Model.OkapiBM25Model);
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1UPDATED);
        doc1.indexText("contents2",contents2Doc1);
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.close();


        //Now lets build an index with the isolation of fields using the OLD Contents of contents1
        LgteIndexWriterIsolateFields writerIsolate = new LgteIndexWriterIsolateFields(pathMulti1,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1OLD);
        doc1.indexText("contents2",contents2Doc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);

        writerIsolate.addDocument(doc1);
        writerIsolate.addDocument(doc2);
        writerIsolate.addDocument(doc3);

        writerIsolate.close();

        //Now lets build an index with the isolation of fields using the OLD Contents of contents1
        writerIsolate = new LgteIndexWriterIsolateFields(pathMulti2,true);
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1OLD);
        doc1.indexText("contents2",contents2Doc1);
        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);
        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);

        writerIsolate.addDocument(doc1);
        writerIsolate.addDocument(doc2);
        writerIsolate.addDocument(doc3);

        writerIsolate.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathUnique);
        Files.delDirsE(pathMulti1);
        Files.delDirsE(pathMulti2);
    }



    public void testUpdateOneFieldAddingAllTheFieldsInDoc() throws IOException, InvalidGeoException {



        //Now will we test to rebuild the field contents1 and see if the contents2 folder is changed
        //In this test we will put all the fields like we did in setup to check if the writer uses just the targetFields
        long timeContents2UpdatedBefore = new File(pathMulti1 + File.separator + "contents2_field").listFiles()[0].lastModified();
        long timeContents1UpdatedBefore = new File(pathMulti1 + File.separator + "contents1_field").listFiles()[0].lastModified();

        List<String> targetFields = new ArrayList<String>();
        targetFields.add("contents1");
        LgteIndexWriterIsolateFields writerIsolate = new LgteIndexWriterIsolateFields(targetFields,pathMulti1,true);

        //Now lets build an index with the changing just the contents1
        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1UPDATED); // Here is the updated content
        doc1.indexText("contents2",contents2Doc1);   //the system will ignore contents2
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents1",contents1Doc2);
        doc2.indexText("contents2",contents2Doc2);      //the system will ignore contents2
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);
        doc3.indexText("contents2",contents2Doc3);  //the system will ignore contents2

        writerIsolate.addDocument(doc1);
        writerIsolate.addDocument(doc2);
        writerIsolate.addDocument(doc3);

        writerIsolate.close();
        assertEquals(timeContents2UpdatedBefore,new File(pathMulti1 + File.separator + "contents2_field").listFiles()[0].lastModified());
        assertNotSame(timeContents1UpdatedBefore,new File(pathMulti1 + File.separator + "contents1_field").listFiles()[0].lastModified());
        assertResults(pathMulti1);

        //Now will we will test to rebuild the field contents1 and see if the contents2 folder is changed
        //In this test we will put just the field we want to update (contents1)
        timeContents2UpdatedBefore = new File(pathMulti2 + File.separator + "contents2_field").listFiles()[0].lastModified();
        timeContents1UpdatedBefore = new File(pathMulti2 + File.separator + "contents1_field").listFiles()[0].lastModified();


        writerIsolate = new LgteIndexWriterIsolateFields(pathMulti2,true);

        //Now lets build an index with the changing just the contents1
        doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents1",contents1Doc1UPDATED); // Here is the updated content

        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents1",contents1Doc2);

        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents1",contents1Doc3);


        writerIsolate.addDocument(doc1);
        writerIsolate.addDocument(doc2);
        writerIsolate.addDocument(doc3);

        writerIsolate.close();


        assertEquals(timeContents2UpdatedBefore,new File(pathMulti2 + File.separator + "contents2_field").listFiles()[0].lastModified());
        assertNotSame(timeContents1UpdatedBefore,new File(pathMulti2 + File.separator + "contents1_field").listFiles()[0].lastModified());

        assertResults(pathMulti2);
    }





    private void assertResults(String path) throws IOException {

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
                new LgteIsolatedIndexReader(path,Model.OkapiBM25Model)
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
