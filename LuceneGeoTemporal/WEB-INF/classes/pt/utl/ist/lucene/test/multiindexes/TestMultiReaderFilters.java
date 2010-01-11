package pt.utl.ist.lucene.test.multiindexes;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.pjaol.search.geo.utils.InvalidGeoException;
import com.pjaol.lucene.search.SerialChainFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TermsFilter;
import org.apache.lucene.search.Filter;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 11:05:21
 * @email machadofisher@gmail.com
 */
public class TestMultiReaderFilters extends TestCase {

    private String pathMulti1 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi1";
    private String pathMulti2 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi2";
    private String pathMulti3 = Globals.INDEX_DIR + "/" + getClass().getName() + "multi3";

    protected void setUp() throws IOException {



        String flag1Doc1 ="true"; String flag2Doc1 ="true";
        String flag1Doc2 ="true"; String flag2Doc2 ="false";
        String flag1Doc3 ="false"; String flag2Doc3 ="true";
        String flag1Doc4 ="false"; String flag2Doc4 ="false";




        String contentsDoc1 ="word1 word12 word123";
        String contentsDoc2 ="word12 word123 word234";
        String contentsDoc3 ="word123 word234 word34";
        String contentsDoc4 ="word234 word34 word4";





        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents",contentsDoc1);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents",contentsDoc2);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents",contentsDoc3);

        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("contents",contentsDoc4);

        LgteIndexWriter writer = new LgteIndexWriter(pathMulti1,true);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);

        writer.close();

        writer = new LgteIndexWriter(pathMulti2,true);
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("flag1",flag1Doc1);

        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("flag1",flag1Doc2);

        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("flag1",flag1Doc3);

        doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("flag1",flag1Doc4);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);

        writer.close();


        writer = new LgteIndexWriter(pathMulti3,true);
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("flag2",flag2Doc1);

        doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("flag2",flag2Doc2);

        doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("flag2",flag2Doc3);

        doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("flag2",flag2Doc4);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(pathMulti1);
        Files.delDirsE(pathMulti2);
        Files.delDirsE(pathMulti3);
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




        IndexReader readerMulti1 = new LanguageModelIndexReader(IndexReader.open(pathMulti1));
        IndexReader readerMulti2 = new LanguageModelIndexReader(IndexReader.open(pathMulti2));
        IndexReader readerMulti3 = new LanguageModelIndexReader(IndexReader.open(pathMulti3));
        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put("contents",readerMulti1);
        readers.put("flag1",readerMulti2);
        readers.put("flag2",readerMulti3);

        LgteIndexSearcherWrapper searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));


        try{

            TermsFilter filterFlag1True = new TermsFilter();
            filterFlag1True.addTerm(new Term("flag1","true"));
            TermsFilter filterFlag2True = new TermsFilter();
            filterFlag2True.addTerm(new Term("flag2","true"));
            TermsFilter filterFlag1False = new TermsFilter();
            filterFlag1False.addTerm(new Term("flag1","false"));
            TermsFilter filterFlag2False = new TermsFilter();
            filterFlag2False.addTerm(new Term("flag2","false"));

            TermsFilter filterFlag1TrueFlag2False = new TermsFilter();
            filterFlag1TrueFlag2False.addTerm(new Term("flag1","true"));
            filterFlag1TrueFlag2False.addTerm(new Term("flag2","false"));

            TermsFilter filterFlag1FalseFlag2False = new TermsFilter();
            filterFlag1FalseFlag2False.addTerm(new Term("flag1","false"));
            filterFlag1FalseFlag2False.addTerm(new Term("flag2","false"));

            LgteHits lgteHits = searcherMulti.search("word234",filterFlag1FalseFlag2False);
            assertFalse(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD).equals("1"));
            assertFalse(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD).equals("1"));
            assertFalse(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD).equals("1"));
            assertEquals(lgteHits.length(),3);

            SerialChainFilter serialChainFilter = new SerialChainFilter(new Filter[]{filterFlag1False,filterFlag2False},new int[]{SerialChainFilter.AND,SerialChainFilter.AND});
            lgteHits = searcherMulti.search("word234",serialChainFilter);
            assertFalse(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD).equals("1"));
            assertEquals(lgteHits.length(),1);




        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcherMulti.close();
    }

}
