package pt.utl.ist.lucene.test.BoxBoxStrategy;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * The objective of this class s help you to use Lgte Box Queries and Box Documents in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestSpatialBoxDocumentBoxQueryStrategyWithLgte extends TestCase
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

    static String portalegreCenterPolygon = "<Polygon>\n" +
                        "\t\t\t\t<tessellate>1</tessellate>\n" +
                        "\t\t\t\t<outerBoundaryIs>\n" +
                        "\t\t\t\t\t<LinearRing>\n" +
                        "\t\t\t\t\t\t<coordinates>\n" +
                        "-7.434327383369592,39.29857488478616,0 -7.434137047433325,39.29373754051745,0 -7.426759823314072,39.29345295799969,0 -7.426450011325102,39.2979246007796,0 -7.434327383369592,39.29857488478616,0 </coordinates>\n" +
                        "\t\t\t\t\t</LinearRing>\n" +
                        "\t\t\t\t</outerBoundaryIs>\n" +
                        "\t\t\t</Polygon>";

    static String pracaDaRepublicaZone =  "<Polygon>\n" +
                        "\t\t\t\t<tessellate>1</tessellate>\n" +
                        "\t\t\t\t<outerBoundaryIs>\n" +
                        "\t\t\t\t\t<LinearRing>\n" +
                        "\t\t\t\t\t\t<coordinates>\n" +
                        "-7.430377677947291,39.28984151721939,0 -7.429493162002388,39.2890699211173,0 -7.429137413718479,39.28937210487553,0 -7.43003423363588,39.29010250509971,0 -7.430377677947291,39.28984151721939,0 </coordinates>\n" +
                        "\t\t\t\t\t</LinearRing>\n" +
                        "\t\t\t\t</outerBoundaryIs>\n" +
                        "\t\t\t</Polygon>";

    protected void setUp() throws IOException, ParserConfigurationException, SAXException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path, true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins in Portalegre in Gemeos Bar");
        //We will now add a Polygon capturing Praca da Repoublica Zone in Portalegre, this has been generated with Google Earth tool
        doc1.addGmlPolygonUnknownForm(pracaDaRepublicaZone);


        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins in Portalegre shooping in center");
//        doc2.addGeoPointField(39.292166, -7.428733);
        doc2.addGmlPolygonUnknownForm(portalegreCenterPolygon);

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


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }


    public void testRange() throws IOException, InvalidGeoException, ParserConfigurationException, SAXException, ParseException
    {
        //Lets use Lgte wrapper to dont worry about details with latitudes and longitudes, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents in Lisbon the closest one is in Lisbon and the next is in Portalegre, in center of Portugal, Jorge home city
        //Arhus will be out of the box
        String query = "contents:(jorge bruno shoping)";
        QueryParams queryParams = new QueryParams();
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.getQueryProperties().put("scorer.spatial.score.strategy", "pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryParams.setGmlPolygon(portalegreCenterPolygon);
        queryParams.setFilter(FilterEnum.no);
        queryParams.setOrder(OrderEnum.scoreSpace);
        queryParams.setQEEnum(QEEnum.no);
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query,searcher,queryConfiguration,queryParams);

        //LgteHits give you Documents and Latitudes and Longitudes with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);


            assertEquals(lgteHits.doc(0).get("id"), "2");
            assertEquals(lgteHits.doc(1).get("id"), "1");
            assertEquals(lgteHits.doc(2).get("id"), "3");

//
//            //doc 1
//            assertTrue(lgteHits.spaceDistanceKm(1) < 250);
//            assertTrue(lgteHits.doc(1).getLatitude() == 39.292166);
//            assertTrue(lgteHits.doc(1).getLongitude() - 7.428733 < 0.001 ); //we have a very litle round error in this case
//            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins in Portalegre");
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
