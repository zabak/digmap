package pt.utl.ist.lucene.test.hierarchicindexes;

import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.queryParser.ParseException;
import junit.framework.TestCase;

/**
 * @author Jorge Machado
 * @date 18/Jan/2010
 * @time 14:35:43
 * @email machadofisher@gmail.com
 */
public class JustAnExample extends TestCase {


    /**
     * The objective of the Tree Indexes is to avoid to index certain fields more than once
     *
     * Example:
     *     A document has a Content which could be separated in a set of statements
     *     We want to index each statement as an object but we want to score it with a linear
     *     combination of scores:  0.7*statement + 0.3*contentOfDocument
     *
     * With out Tree Indexes we will need to index each statement with two fields content and statement
     * This will makes us to index the content as much times as the number of statements in that document
     *
     * With Tree Indexes
     *
     * We build an index for statements and another for contents
     * The indexes will be much smaller
     * Then we create an LgteIsolatedReader and we add a mapping telling the system that the index
     * of contents use a diferente space of ID's and that those ids should be mapped into the space
     * of id's in statements index. The rest of the LGTE API (for example get fields from documents) is unchanged.
     * We just need to mapp the readers to the fields like we use to do before TreeIndexes
     *
     *
     */

    private String pathSentences = Globals.INDEX_DIR + "/" + getClass().getName();
    private String pathDocuments = Globals.INDEX_DIR + "/" + getClass().getName() + "Docs";


    public void setUp() throws IOException
    {

        String stm1_1 = "word1 word2 word3 word32 word1 word45 word56 word67 word67 word2 word67 word88 word99 word99 word33";
        String stm1_2 = "word2 word3 word4 word55 word96 word2 word54 word33 wordss";
        String stm1_3 = "word1 word100 word400 word555 word966 word544 word333 wordss";
        String document1 = stm1_1 + " " + stm1_2 + " " + stm1_3;


        String stm2_1 = "word5 word67 word67";
        String stm2_2 = "word6 word3 word44";
        String stm2_3 = "word555 word966 word1000";
        String document2 = stm2_1 + " " + stm2_2 + " " + stm2_3;

        //DOCS INDEX
        LgteIndexWriter writerDocs = new LgteIndexWriter(pathDocuments,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents",document1);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents",document2);

        writerDocs.addDocument(doc1);
        writerDocs.addDocument(doc2);
        writerDocs.close();



        //SENTENCES INDEX
        LgteIndexWriter writerSentences = new LgteIndexWriter(pathSentences,true);

        LgteDocumentWrapper stmVirtualDoc1_1 = new LgteDocumentWrapper();
        stmVirtualDoc1_1.indexText(Globals.DOCUMENT_ID_FIELD, "1_1");
        stmVirtualDoc1_1.indexText("doc_id", "1");
        stmVirtualDoc1_1.indexText("statements",stm1_1);

        LgteDocumentWrapper stmVirtualDoc1_2 = new LgteDocumentWrapper();
        stmVirtualDoc1_2.indexText(Globals.DOCUMENT_ID_FIELD, "1_2");
        stmVirtualDoc1_2.indexText("doc_id", "1");
        stmVirtualDoc1_2.indexText("statements",stm1_2);

        LgteDocumentWrapper stmVirtualDoc1_3 = new LgteDocumentWrapper();
        stmVirtualDoc1_3.indexText(Globals.DOCUMENT_ID_FIELD, "1_3");
        stmVirtualDoc1_3.indexText("doc_id", "1");
        stmVirtualDoc1_3.indexText("statements",stm1_3);

        LgteDocumentWrapper stmVirtualDoc2_1 = new LgteDocumentWrapper();
        stmVirtualDoc2_1.indexText(Globals.DOCUMENT_ID_FIELD, "2_1");
        stmVirtualDoc2_1.indexText("doc_id", "2");
        stmVirtualDoc2_1.indexText("statements",stm2_1);

        LgteDocumentWrapper stmVirtualDoc2_2 = new LgteDocumentWrapper();
        stmVirtualDoc2_2.indexText(Globals.DOCUMENT_ID_FIELD, "2_2");
        stmVirtualDoc2_2.indexText("doc_id", "2");
        stmVirtualDoc2_2.indexText("statements",stm2_2);

        LgteDocumentWrapper stmVirtualDoc2_3 = new LgteDocumentWrapper();
        stmVirtualDoc2_3.indexText(Globals.DOCUMENT_ID_FIELD, "2_3");
        stmVirtualDoc2_3.indexText("doc_id", "2");
        stmVirtualDoc2_3.indexText("statements",stm2_3);

        writerSentences.addDocument(stmVirtualDoc1_1);
        writerSentences.addDocument(stmVirtualDoc1_2);
        writerSentences.addDocument(stmVirtualDoc1_3);
        writerSentences.addDocument(stmVirtualDoc2_1);
        writerSentences.addDocument(stmVirtualDoc2_2);
        writerSentences.addDocument(stmVirtualDoc2_3);

        writerSentences.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathSentences);
        Files.delDirsE(pathDocuments);
    }

    public void testIndexTree() throws IOException, ParseException
    {
        IndexReader readerDocs = LgteIndexManager.openReader(pathDocuments, Model.OkapiBM25Model);
        IndexReader readerSentences = LgteIndexManager.openReader(pathSentences,Model.OkapiBM25Model);

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put("contents",readerDocs);
        readers.put("statements",readerSentences);
        readers.put("doc_id",readerSentences);
        readers.put("id",readerSentences);
        LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
        lgteIsolatedIndexReader.addTreeMapping("contents(id)>statements(doc_id)");

        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,lgteIsolatedIndexReader);
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setProperty("bm25.idf.policy","floor_epslon");
        queryConfiguration.setProperty("bm25.idf.epslon","0.05");
        queryConfiguration.setProperty("bm25.k1","2.0d");
        queryConfiguration.setProperty("bm25.b","0.75d");
        queryConfiguration.setProperty("index.tree","true");

        LgteQuery lgteQuery = LgteQueryParser.parseQuery("contents:(word1 word2 word3)^0.3 statements:(word1 word2 word3)^0.7",searcher,queryConfiguration);
        LgteHits lgteHits = searcher.search(lgteQuery);
        System.out.println("SCHEME 0.7*score(stm) + (0.3)*score(doc)");
        System.out.println("Stm " + lgteHits.doc(0).get("id") + ":" + lgteHits.doc(0).get("doc_id") + " (0.7*stm1 + 0.3*doc1):" + lgteHits.score(0));
        System.out.println("Stm " + lgteHits.doc(1).get("id") + ":" + lgteHits.doc(1).get("doc_id") + " (0.7*stm2 + 0.3*doc1):" + lgteHits.score(1));
        System.out.println("Stm " + lgteHits.doc(2).get("id") + ":" + lgteHits.doc(2).get("doc_id") + " (0.7*stm3 + 0.3*doc1):" + lgteHits.score(2));
        System.out.println("Stm " + lgteHits.doc(3).get("id") + ":" + lgteHits.doc(3).get("doc_id") + " (0.7*stm5 + 0.3*doc2):" + lgteHits.score(3));
    }
}
