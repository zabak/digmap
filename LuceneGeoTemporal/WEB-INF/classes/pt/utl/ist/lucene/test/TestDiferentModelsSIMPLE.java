package pt.utl.ist.lucene.test;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.sorters.SpatialDistanceSortSource;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.search.SortField;
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
public class TestDiferentModelsSIMPLE extends TestCase
{

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.DATA_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path,true,Model.OkapiBM25Model);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins in Lisbon");
        doc1.addGeoPointField(38.788440, -9.171290);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins in Portalegre");
        doc2.addGeoPointField(39.292166, -7.428733);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins in Arhus");
        doc3.addGeoPointField(56.162500, 10.144250);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.OkapiBM25Model, path);
        /**
         * LanguageModel or lm
         * DLHHypergeometricDFRModel
         * InExpC2DFRModel
         * InExpB2DFRModel
         * IFB2DFRModel
         * InL2DFRModel
         * PL2DFRModel
         * BB2DFRModel
         * OkapiBM25Model or bm25
         */

        try
        {

            printQuery("jorge bruno in lisbon model:lm",searcher);
            printQuery("jorge bruno in lisbon model:DLHHypergeometricDFRModel",searcher);
            printQuery("jorge bruno in lisbon model:InExpC2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:InExpB2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:IFB2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:InL2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:PL2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:BB2DFRModel",searcher);
            printQuery("jorge bruno in lisbon model:bm25",searcher);


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }

    private void printQuery(String query,LgteIndexSearcherWrapper searcherWrapper) throws IOException, ParseException
    {
        LgteHits lgteHits = searcherWrapper.search(query);
        System.out.println(query);
        System.out.println(lgteHits.doc(0).get("id") + " - " + lgteHits.score(0));
        System.out.println(lgteHits.doc(1).get("id") + " - " + lgteHits.score(1));
        System.out.println(lgteHits.doc(2).get("id") + " - " + lgteHits.score(2));
    }
}
