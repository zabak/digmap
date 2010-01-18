package pt.utl.ist.lucene.test.hierarchicindexes;

import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;

/**
 * @author Jorge Machado
 * @date 17/Jan/2010
 * @time 10:47:45
 * @email machadofisher@gmail.com
 */
public class TestIndexTreeIdMapping extends TestCase
{

    private String pathSentences = Globals.INDEX_DIR + "/" + getClass().getName() + "Sentences";
    private String pathDocuments = Globals.INDEX_DIR + "/" + getClass().getName() + "Docs";

    protected void setUp()
    {

        try
        {
            LgteIndexWriter writerDocuments = new LgteIndexWriter(pathDocuments,true);
            LgteIndexWriter writerSentences = new LgteIndexWriter(pathSentences,true);

            LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
            doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
            doc1.indexText("contents","w1 w2 w3 w4 w5 w6");
            writerDocuments.addDocument(doc1);


            LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
            doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
            doc2.indexText("contents","w1 w2 w3 w4 w5 w6");
            writerDocuments.addDocument(doc2);



            LgteDocumentWrapper stm1_1 = new LgteDocumentWrapper();
            stm1_1.indexText(Globals.DOCUMENT_ID_FIELD, "1_1");
            stm1_1.indexText("doc_id", "1");
            stm1_1.indexText("contents","w1 w2");
            writerSentences.addDocument(stm1_1);

            LgteDocumentWrapper stm1_2 = new LgteDocumentWrapper();
            stm1_2.indexText(Globals.DOCUMENT_ID_FIELD, "1_2");
            stm1_2.indexText("doc_id", "1");
            stm1_2.indexText("contents","w1 w2");
            writerSentences.addDocument(stm1_2);

            LgteDocumentWrapper stm2_1 = new LgteDocumentWrapper();
            stm2_1.indexText(Globals.DOCUMENT_ID_FIELD, "2_1");
            stm2_1.indexText("doc_id", "2");
            stm2_1.indexText("contents","w1 w2");
            writerSentences.addDocument(stm2_1);

            LgteDocumentWrapper stm2_2 = new LgteDocumentWrapper();
            stm2_2.indexText(Globals.DOCUMENT_ID_FIELD, "2_2");
            stm2_2.indexText("doc_id", "2");
            stm2_2.indexText("contents","w1 w2");
            writerSentences.addDocument(stm2_2);

            writerDocuments.close();
            writerSentences.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathSentences);
        Files.delDirsE(pathDocuments);
    }

    public void testMapping()
    {
        try
        {
            IndexReader readerContents = LgteIndexManager.openReader(pathDocuments, Model.OkapiBM25Model);
            IndexReader readerSentences = LgteIndexManager.openReader(pathSentences,Model.OkapiBM25Model);

            Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
            readers.put("contents",readerContents);
            readers.put("sentences",readerSentences);
            readers.put("doc_id",readerSentences);
            readers.put("id",readerSentences);

            LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
            lgteIsolatedIndexReader.addTreeMapping(readerContents,readerSentences,"doc_id");

            assertEquals(lgteIsolatedIndexReader.translateId(0,"contents")[0],0);
            assertEquals(lgteIsolatedIndexReader.translateId(0,"contents")[1],1);
            assertEquals(lgteIsolatedIndexReader.translateId(1,"contents")[0],2);
            assertEquals(lgteIsolatedIndexReader.translateId(1,"contents")[1],3);




        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
