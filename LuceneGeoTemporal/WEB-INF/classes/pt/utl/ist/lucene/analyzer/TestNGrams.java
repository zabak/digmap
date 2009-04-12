package pt.utl.ist.lucene.analyzer;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;

import java.io.IOException;

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
public class TestNGrams extends TestCase
{

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path, new LgteStemAnalyzer(3,5),true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents","abcdef");


        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents","defghij");

        writer.addDocument(doc1);
        writer.addDocument(doc2);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, path);


        try
        {

            printQuery("def",searcher);



        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }



    private void printQuery(String query,LgteIndexSearcherWrapper searcherWrapper) throws IOException, ParseException
    {
        LgteHits lgteHits = searcherWrapper.search(query,new LgteStemAnalyzer(3,5));
        System.out.println(query);
        System.out.println(lgteHits.doc(0).get("id") + " - " + lgteHits.score(0));
        System.out.println(lgteHits.doc(1).get("id") + " - " + lgteHits.score(1));

    }
}
