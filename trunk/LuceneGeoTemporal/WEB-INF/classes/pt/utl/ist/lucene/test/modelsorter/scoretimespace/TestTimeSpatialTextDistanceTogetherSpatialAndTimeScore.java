package pt.utl.ist.lucene.test.modelsorter.scoretimespace;

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
public class TestTimeSpatialTextDistanceTogetherSpatialAndTimeScore extends TestCase
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
        doc1.addTimeField(2007);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins in Portalegre");
        doc2.addGeoPointField(39.292166, -7.428733);
        doc2.addTimeField(2005);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins several years ago visinting europe");
        doc3.addGeoPointField(56.162500, 10.144250);
        doc3.addTimeField(1988);


        //Note that this paper is archived in portalegre and we use geographic cordinates of portalegre in this case
        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText("id", "4");
        doc4.indexText("contents","Jorge Machado and Bruno Martins Ahrus Paper archived in Superior School of Technology and Managemet in Portalegre");
        doc4.addGeoPointField(39.292166, -7.428733);
        doc4.addTimeField(2008);


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


    public void testRange() throws IOException, InvalidGeoException
    {
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents in 2006 near Portalegre
        //Document 2 has time 2005 it is at one year distance like document 1 that is in 2007
        //Document 2 is in Portalegre, near the query geo point, and document 1 in lisbon, in terms of words they are equal
        //Then we have document 4 in portalegre at 2 years distance and with all words
        //we gone test two stuations
        // 1* one with radium 3 years
        // 2* the other with out radium
        // 1 - with radium set in 3 years and spatial radium in 200 km documents 2 and 4 in this order will come first
        //      document 1 (Lisbon) will come in last place because time score will be medium but spatial will be in the bottom of the sigmoid.
        // 2 - but with no radium in spatial and no radium in time will be assumed a radium of about 10 years because is half
        //     the diference beteween the two most distante documents [1988 and 2008] and half earth radium 3000 km in spatial
        //     In this case document 1 is in advantage because it is also in the top of the SIGMOID ranking in Time
        String query = "contents:(jorge bruno) time:2006 lat:39.292166 lng:-7.428733 radiumKm:200 radiumYears:3";


        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(0) < 250);
            assertTrue(lgteHits.timeDistanceYears(0) < 3);
            assertTrue(lgteHits.doc(0).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(0).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(0).get("id"),"2");

            //doc 1
            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
            assertTrue(lgteHits.timeDistanceYears(1) < 3);
            assertTrue(lgteHits.doc(1).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(1).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(1).get("id"),"4");

            //doc 2
            assertTrue(lgteHits.spaceDistanceKm(2) < 250);
            assertTrue(lgteHits.timeDistanceYears(2) < 3);
            assertTrue(lgteHits.doc(2).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(2).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(2).get("id"),"1");



            /******************************************************************+
             *
             *
             * TEST 2
             *
             * @see explanation in test 1
             *
             *
             */
            query = "contents:(jorge bruno) time:2006 lat:39.292166 lng:-7.428733";

            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 4);

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(0) < 250);
            assertTrue(lgteHits.timeDistanceYears(0) == 1);
            assertTrue(lgteHits.doc(0).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(0).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(0).get("id"),"2");

            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
            assertTrue(lgteHits.timeDistanceYears(1) == 1);
            assertTrue(lgteHits.doc(1).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(1).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(1).get("id"),"1");

            assertTrue(lgteHits.spaceDistanceKm(2) < 250);
            assertTrue(lgteHits.timeDistanceYears(2) == 2);
            assertTrue(lgteHits.doc(2).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(2).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(2).get("id"),"4");

            assertTrue(lgteHits.spaceDistanceKm(3) > 250);
            assertTrue(lgteHits.timeDistanceYears(3) == 18);
            assertTrue(lgteHits.doc(3).getLatitude() == 56.162500);
            assertTrue(lgteHits.doc(3).getLongitude() == 10.144250);
            assertEquals(lgteHits.doc(3).get("id"),"3");

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
        System.out.println("");
    }
}
