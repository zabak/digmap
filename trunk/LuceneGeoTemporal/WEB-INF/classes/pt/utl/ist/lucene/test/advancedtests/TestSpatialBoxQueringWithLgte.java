package pt.utl.ist.lucene.test.advancedtests;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.SortField;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.SpatialDistanceSortSource;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

/**
 *
 * The objective of this class s help you to use Lgte Box Queries in a very quick example
 *
 * With Box Queries you can searchCallback in a well delimited place in world
 *
 * In this case the centroide is been used to order the results
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestSpatialBoxQueringWithLgte extends TestCase
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
        LgteIndexWriter writer = new LgteIndexWriter(path, true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins in Lisbon");
//        doc1.addGeoPointField(38.788440, -9.171290);
        doc1.addGeoBoxField( 39.788440, 37.788440, -10.171290, -8.171290);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins in Portalegre");
//        doc2.addGeoPointField(39.292166, -7.428733);
        doc2.addGeoBoxField(40.292166,38.292166, -8.428733, -6.428733);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins in Arhus");
//        doc3.addGeoPointField(56.162500, 10.144250);
        doc3.addGeoBoxField(57.162500,55.162500, 9.144250, 11.144250);

        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);

        writer.close();
    }


    protected void tearDown() throws java.lang.Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents in Lisbon
        // In this case tha closest one will be in Portalegre because the right limit of square (east) will pass in middle of Spain so
        // the centroide will be near portalegre that is in the border with Spain
        //Arhus will be out of the box
        String query = "contents:(jorge bruno in lisbon) north:39.754720 south:37.754720 west:-9.5 east:-4";

        //Lets use aos Space Sorter to dont worry about distances and stuff like that, he will do the job
        SpatialDistanceSortSource dsort = new SpatialDistanceSortSource();
        LgteSort sort = new LgteSort(new SortField("foo", dsort));

        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query, sort);
            assertTrue(lgteHits.length() == 2);

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(0) < 250);
            assertTrue(lgteHits.doc(0).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(0).getLongitude() - 7.428733 < 0.001 ); //we have a very litle round error in this case
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins in Portalegre");

            //doc 1
            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
            assertTrue(lgteHits.doc(1).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(1).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins in Lisbon");


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
