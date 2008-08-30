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
public class TestTimeSpatialTextDistanceTogetherButOnlySpatialActive extends TestCase
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
        doc3.indexText("contents","Jorge Machado and Bruno Martins in Arhus");
        doc3.addGeoPointField(56.162500, 10.144250);
        doc3.addTimeField(2008);


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

        //try find Jorge and Bruno documents in Ahrus with the latitude and longitude of Portalegre
        //Jorge and Bruno are in Ahrus in Document 3 but documents 2 and 4 are in Portalegre,
        // document 4 also has the word ahrus inside so the natural order will be 4, 2 and the 3 and 1 will depende on the score
        //Ahrus is very far away so spatial score will be lower but the text score will be hight so it is natural that the score
        //of documents 4 and 1 be similar
        String query = "contents:(jorge bruno Ahrus paper) lat:39.292166 lng:-7.428733 radiumKm:250";


        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(0) < 250);
            assertTrue(lgteHits.doc(0).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(0).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins Ahrus Paper archived in Superior School of Technology and Managemet in Portalegre");

            //doc 1
            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
            assertTrue(lgteHits.doc(1).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(1).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins in Portalegre");

            //doc 2
            assertTrue(lgteHits.spaceDistanceKm(2) < 250);
            assertTrue(lgteHits.doc(2).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(2).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(2).get("contents"),"Jorge Machado and Bruno Martins in Lisbon");

        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
