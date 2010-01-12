package pt.utl.ist.lucene.test;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortComparatorSource;
import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.queryParser.ParseException;

/**
 * @author Jorge Machado
 * @date 16/Dez/2009
 * @time 11:05:21
 * @email machadofisher@gmail.com
 */
public class TestSort extends TestCase {

    private String path = Globals.INDEX_DIR + "/" + getClass().getName();

    protected void setUp() throws IOException {



        String flag1Doc1 ="true"; String flag2Doc1 ="true";
        String flag1Doc2 ="true"; String flag2Doc2 ="false";
        String flag1Doc3 ="false"; String flag2Doc3 ="true";
        String flag1Doc4 ="false"; String flag2Doc4 ="false";




        String contentsDoc1 ="word1 word12 word123";
        String contentsDoc2 ="word12 word123 word234";
        String contentsDoc3 ="word123 word234 word34";
        String contentsDoc4 ="word234 word234 word34 word4";





        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents",contentsDoc1);
        doc1.indexText("flag1",flag1Doc1);
        doc1.indexText("flag2",flag2Doc1);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents",contentsDoc2);
        doc2.indexText("flag1",flag1Doc2);
        doc2.indexText("flag2",flag2Doc2);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents",contentsDoc3);
        doc3.indexText("flag1",flag1Doc3);
        doc3.indexText("flag2",flag2Doc3);

        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("contents",contentsDoc4);
        doc4.indexText("flag1",flag1Doc4);
        doc4.indexText("flag2",flag2Doc4);

        LgteIndexWriter writer = new LgteIndexWriter(path,true);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);

        writer.close();


    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
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


        LgteIndexSearcherWrapper searcherMulti = new LgteIndexSearcherWrapper(Model.OkapiBM25Model,path);


        try{

            LgteSort lgteSort = new LgteSort(new SortField[] {new SortField("flag2",SortField.STRING,true),SortField.FIELD_SCORE});

            LgteHits lgteHits = searcherMulti.search("word234",lgteSort);
            assertEquals(lgteHits.doc(0).get(Globals.DOCUMENT_ID_FIELD),"3");
            assertEquals(lgteHits.doc(1).get(Globals.DOCUMENT_ID_FIELD),"4");
            assertEquals(lgteHits.doc(2).get(Globals.DOCUMENT_ID_FIELD),"2");
            assertEquals(lgteHits.length(),3);


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcherMulti.close();
    }

}

