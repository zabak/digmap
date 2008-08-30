package pt.utl.ist.lucene.test.advancedtests;

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
 * The objective of this class is test the filtering
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestSpatialDistanceFiltering extends TestCase
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
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents in Lisbon the closest one is in Lisbon and the next is in Portalegre, in center of Portugal, Jorge home city
        //Arhus will be out of the box
        String query = "contents:(jorge bruno in lisbon) lat:38.754720 lng:-9.185010 radiumKm:250 filter:no";

        //Lets use aos Space Sorter to dont worry about distances and stuff like that, he will do the job
        SpatialDistanceSortSource dsort = new SpatialDistanceSortSource();
        LgteSort sort = new LgteSort(new SortField("foo", dsort));

        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query, sort);
            assertTrue(lgteHits.length() == 3);

            //doc 0
            assertTrue(lgteHits.spaceDistanceKm(0) < 250);
            assertTrue(lgteHits.doc(0).getLatitude() == 38.788440);
            assertTrue(lgteHits.doc(0).getLongitude() == -9.171290);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins in Lisbon");

            //doc 1
            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
            assertTrue(lgteHits.doc(1).getLatitude() == 39.292166);
            assertTrue(lgteHits.doc(1).getLongitude() == -7.428733);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins in Portalegre");
            
            //doc 3
            assertTrue(lgteHits.spaceDistanceKm(2) > 250);
            assertTrue(lgteHits.doc(2).getLatitude() == 56.162500 );
            assertTrue(lgteHits.doc(2).getLongitude() == 10.144250);
            assertEquals(lgteHits.doc(2).get("contents"),"Jorge Machado and Bruno Martins in Arhus");
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
