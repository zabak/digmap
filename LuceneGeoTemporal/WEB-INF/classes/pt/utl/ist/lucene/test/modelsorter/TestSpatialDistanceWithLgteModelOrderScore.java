package pt.utl.ist.lucene.test.modelsorter;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * The objective of this class s help you to use Lgte Order fields and filtering at same time
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestSpatialDistanceWithLgteModelOrderScore extends TestCase
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
        doc3.indexText("contents","Jorge Machado in Arhus");
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


    public void testRange() throws IOException, InvalidGeoException, ParseException
    {
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents in Lisbon
        //In this test we place the query in Ahrus but we will choose score order ONLY
        //Lisbon and portalegre are out of the box but once we'he turned off the filter these results will
        // appear first because text score will be bigger
        // We remove Bruno Name from Ahrus document to have a big score in portalegre and a bigger score in Lisbon
        //The order will be Lisbon at 1400 miles, Portalegre at about 1200 mile, Ahrus near the query
        String query = "contents:(jorge bruno in lisbon) lat:56.162500 lng:10.144250 radiumKm:250 filter:no order:sc";

        //Lets use aos Space Sorter to dont worry about distances and stuff like that, he will do the job
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query);


        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            //Note that we are not using sorter any more, will be created inside searcher
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);

            //note that distances will not be calculated
            assertTrue(lgteHits.spaceDistanceKm(0) > 250);
            assertTrue(lgteHits.doc(0).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(0).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins in Lisbon");
            //will use sgmoid function the distance is ~ 4 miles / 160miles ~0.99999 between 0 and 1 in sigmoid will give score = 1
            // The time dont exist so will return 1, the text is about 0.7
            // 0.33*0.99999 + 0.33*1 + 0.33*0.7 = 0.89
//            assertTrue(lgteHits.score(1) >= 0.89 && lgteHits.score(0) < 0.9);

            //doc 1
            assertTrue(lgteHits.spaceDistanceKm(1) > 250);
            assertTrue(lgteHits.doc(1).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(1).getLongitude() == -7.428733);
            //will use sgmoid function the distance is ~ 100 miles / 160miles ~0.6 between 0 and 1 in sigmoid will give score = 0.29
            // Time dont exist so will return score 1, the text is about 0.22
            // 0.33*0.29 + 0.33*1 + 0.33*0.23 = 0.5
//            assertTrue(lgteHits.score(2) >= 0.50 && lgteHits.score(2) < 0.51);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins in Portalegre");

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(2) < 250);
            assertTrue(lgteHits.doc(2).getLatitude() == 56.162500);
            assertTrue(lgteHits.doc(2).getLongitude() == 10.144250);
            //will use sgmoid function the distance is ~ 1400 miles / 160miles ~9.6 in sigmoid will give score = 0
            // The time dont exist so will return 1, the text is about 0.22
            // 0.33*0 + 0.33*1 + 0.33*0.22 = 0.4
//            assertTrue(lgteHits.score(2) >= 0.4 && lgteHits.score(2) < 0.41);
            assertEquals(lgteHits.doc(2).get("contents"),"Jorge Machado in Arhus");


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
