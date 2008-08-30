package pt.utl.ist.lucene.test.qe;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

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
public class TestQueryExpansionWithLgteInsideExpansionSIMPLE extends TestCase
{

    /********************************
     *
     * #######################################################################
     * README
     * ######################################################################
     * PLEASE SET DATA DIR WHERE INDEXES SHOULD BE PLACED
     * IT's IS NECESSARY BECAUSE LANGUAGE MODELING USES A SPETIAL DOCUMENT ID INDEX
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins in Lisbon");
        doc1.addGeoPointField(38.788440, -9.171290);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins in Portalegre");
        doc2.addGeoPointField(39.292166, -7.428733);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins in Arhus in airplane");
        doc3.addGeoPointField(39.292166, -7.428733);

        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText("id", "4");
        doc4.indexText("contents","cars  motocycles  bikes  airplane");
        doc4.addGeoPointField(56.162500, 10.144250);

        LgteDocumentWrapper doc5 = new LgteDocumentWrapper();
        doc5.indexText("id", "5");
        doc5.indexText("contents","cars motocycles bikes");
        doc5.addGeoPointField(56.162500, 10.144250);

        LgteDocumentWrapper doc6 = new LgteDocumentWrapper();
        doc6.indexText("id", "6");
        doc6.indexText("contents","cars motocycles");
        doc6.addGeoPointField(56.162500, 10.144250);

        LgteDocumentWrapper doc7 = new LgteDocumentWrapper();
        doc7.indexText("id", "7");
        doc7.indexText("contents","motocycles");
        doc7.addGeoPointField(56.162500, 10.144250);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);
        writer.addDocument(doc5);
        writer.addDocument(doc6);
        writer.addDocument(doc7);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }


    public void testRange() throws IOException, InvalidGeoException
    {
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find lisbon documents. Only one document in index contains lisbon word
        //After this simple test we will test with query expansion and we will obtain all documents
        String query = "lisbon";

        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 1);
            assertEquals(lgteHits.doc(0).get("id"),"1");

            //Test one only searchCallback
            query = "jorge lisbon portalegre lat:38.78844 lng:-9.171290 radiumKm:200 qe:lgte";

            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);


            //Test 3 set rochio properties to expand only first 3 documents, with radium set to 200 km
            //in this case rochio only will see the first 2 documents so will not expand airplane word
            QueryConfiguration queryConfiguration = new QueryConfiguration();
            queryConfiguration.getQueryProperties().setProperty("QE.doc.num","3");
            LgteQuery lgteQuery = LgteQueryParser.parseQuery("jorge lat:38.788440 lng:-9.171290 qe:lgte",searcher,queryConfiguration);
            lgteHits = searcher.search(lgteQuery);
            assertTrue(lgteHits.length() == 4);
            



            //Test 4 set rochio properties to expand only first 3 documents, with radium set to 200 km but
            //with filters off
            //in this case rochio only will see the first 3 documents so will not expand airplane word

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
