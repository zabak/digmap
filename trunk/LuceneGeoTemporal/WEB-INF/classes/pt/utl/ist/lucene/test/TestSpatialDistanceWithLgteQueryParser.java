package pt.utl.ist.lucene.test;

import com.pjaol.search.geo.utils.DistanceQuery;
import com.pjaol.search.geo.utils.DistanceUtils;
import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.SortField;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.filter.ISpatialDistancesWrapper;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.SpatialDistanceSortSource;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

/**
 *
 * The objective of this class s help you to use Lgte with Lgte Query Parser and specific Queries
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestSpatialDistanceWithLgteQueryParser extends TestCase
{
    /********************************
     *
     * #######################################################################
     * README
     * ######################################################################
     * PLEASE SET DATA DIR WHERE INDEXES SHOULD BE PLACED
     * IT's IS NECESSARY BECAUSE LANGUAGE MODELING USES A SPETIAL DOCUMENT ID INDEX
     */
    private String path = Globals.DATA_DIR + "/" + getClass().getName();
    
   //Portalegre Football Stadium
    private double lat = 39.297754;
    private double lng = -7.433410;

    boolean lm = true;
    static boolean indexCreated = false;


    protected void setUp() throws IOException
    {
        if (!indexCreated)
        {
            indexCreated = true;
            IndexWriter writer;
            if(lm)
                writer = new LgteIndexWriter(path, true, Model.LanguageModel);
            else
                writer = new LgteIndexWriter(path, true, Model.VectorSpaceModel);

            addData(writer);
            writer.close();
        }
    }

    protected void tearDown() throws java.lang.Exception
    {
        Files.delDirsE(path);
    }
    
    int i = 0;


    private void addPoint(IndexWriter writer, String name, double lat, double lng) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        ++i;
        doc.indexText("id", "" + i);
        doc.indexText("name",name);
        doc.addGeoPointField(lat,lng);
        doc.indexText("contents","doc");
        ((LgteIndexWriter)writer).addDocument(doc);
    }


    private void addData(IndexWriter writer) throws IOException
    {
        addPoint(writer, "KJ Bar Portalegre", 39.292166, -7.428733);
        addPoint(writer, "Restaurante o Cavalinho II Grill", 39.292025, -7.431437);
        addPoint(writer, "Restaurante o Cavalinho", 39.291931, -7.431602);
        addPoint(writer, "Gemeos Bar Praça da Republica Portalegre", 39.2894, -7.4293);
        addPoint(writer, "Restaurante Escondidinho Portalegre", 39.294059, -7.431563);
        addPoint(writer, "Alibaba Bar", 39.292650, -7.432820);
        addPoint(writer, "Te kenFim Restaurante Cafe", 39.291332, -7.429922);
        addPoint(writer, "TGIFriday", 38.8725000, -77.3829000);
        addPoint(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
        addPoint(writer, "White Tiger Restaurant", 38.9027000, -77.2638000);
        addPoint(writer, "Jammin' Java", 38.9039000, -77.2622000);
        addPoint(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
        addPoint(writer, "WiseAcres Comedy Club", 38.9248000, -77.2344000);
        addPoint(writer, "Glen Echo Spanish Ballroom", 38.9691000, -77.1400000);
        addPoint(writer, "Whitlow's on Wilson", 38.8889000, -77.0926000);
        addPoint(writer, "Cafe Planalto Portalegre", 39.289058, -7.420409);
        addPoint(writer, "Iota Club and Cafe", 38.8890000, -77.0923000);
        addPoint(writer, "Hilton Washington Embassy Row", 38.9103000, -77.0451000);
        addPoint(writer, "2 in 1 Bar Portalegre", 39.2897, -7.4297);
        addPoint(writer, "Dom Pedro V Castelo de Vide", 39.414938, -7.455584);
    }

    public void testRange() throws IOException, InvalidGeoException, ParseException
    {
        LgteIndexSearcherWrapper searcher;

        if(lm)
            searcher = new LgteIndexSearcherWrapper(Model.LanguageModel,path);
        else
            searcher = new LgteIndexSearcherWrapper(Model.VectorSpaceModel,path);


        double miles = 6.0;
        //doc is a word to find in text
        String query = "doc lat:" + lat + " lng:" + lng + " radiumMiles:" + miles;
        LgteQuery lgteQuery = LgteQueryParser.parseQuery(query);

        assertTrue(lgteQuery.getQueryParams().getLatitude() == lat);
        assertTrue(lgteQuery.getQueryParams().getLongitude() == lng);
        assertTrue(lgteQuery.getQueryParams().getRadiumMiles() == miles);

        SpatialDistanceSortSource dsort = new SpatialDistanceSortSource();
        LgteSort sort = new LgteSort(new SortField("foo", dsort));
        LgteHits hits = searcher.search(lgteQuery, sort);
        int results = hits.length();




        // Get a list of distances
        ISpatialDistancesWrapper distancesWrapper = dsort.getSpaceDistancesWrapper();

        // distances calculated from filter first pass must be less than total
        // docs, from the above test of 20 items, 10 will come from the boundary box
        // filter, but only 9 are actually in the radius of the results.

        // Note Boundary Box filtering, is not accurate enough for most systems.


        System.out.println("Distance Filter filtered: " + distancesWrapper.getSpaceDistances().size());
        System.out.println("Results: " + results);
        System.out.println("=============================");
        assertEquals(9, distancesWrapper.getSpaceDistances().size());
        assertEquals(9, results);

        double last = 0;
        for (int i = 0; i < results; i++)
        {
            LgteDocumentWrapper d = hits.doc(i);

            String name = d.get("name");
            //hits give you latitude and longitude and all other fields available in time space
            double rsLat = d.getLatitude();
            double rsLng = d.getLongitude();
            //With Lgte you don't need to use some complicated Wrapper or Filter, it is integrated in Hits see bellow
            Double geo_distance = distancesWrapper.getSpaceDistance(hits.id(i));

            double distance = DistanceUtils.getDistanceMi(lat, lng, rsLat, rsLng);
            //hits wrapper give you distance in Km or Miles
            assertTrue(distance == hits.spaceDistanceMiles(i));
            assertTrue((distance < miles));
            assertTrue(distance > last);
            last = distance;
            double llm = DistanceUtils.getLLMDistance(lat, lng, rsLat, rsLng);

            System.out.println("Name: " + name + ", Distance (km, res, ortho, harvesine):" + hits.spaceDistanceKm(i) + " - " + distance + " |" + geo_distance + "|" + llm);
            
            switch(i)
            {
                case 0:assertTrue(d.get("name").equals("Restaurante Escondidinho Portalegre"));break;
                case 1: assertTrue(d.get("name").equals("Alibaba Bar"));break;
                case 2:assertTrue(d.get("name").equals("Restaurante o Cavalinho II Grill"));break;
                case 3:assertTrue(d.get("name").equals("Restaurante o Cavalinho"));break;
                case 4:assertTrue(d.get("name").equals("KJ Bar Portalegre"));break;
                case 5: assertTrue(d.get("name").equals("Te kenFim Restaurante Cafe"));break;
                case 6: assertTrue(d.get("name").equals("2 in 1 Bar Portalegre"));break;
                case 7:assertTrue(d.get("name").equals("Gemeos Bar Praça da Republica Portalegre"));break;
                case 8: assertTrue(d.get("name").equals("Cafe Planalto Portalegre"));break;
            }

        }
        searcher.close();
    }


    public void testMiles()
    {
        double LLM = DistanceUtils.getLLMDistance(lat, lng, 39.012200001, -77.3942);
        System.out.println(LLM);
        System.out.println("-->" + DistanceUtils.getDistanceMi(lat, lng, 39.0122, -77.3942));
    }

    public void testMiles2()
    {
        System.out.println("Test Miles 2");
        double LLM = DistanceUtils.getLLMDistance(44.30073, -78.32131, 43.687267, -79.39842);
        System.out.println(LLM);
        System.out.println("-->" + DistanceUtils.getDistanceMi(44.30073, -78.32131, 43.687267, -79.39842));
    }


    public void testDistanceQueryCacheable() throws IOException
    {

        // create two of the same distance queries
        double miles = 6.0;
        DistanceQuery dq1 = new DistanceQuery(lat, lng, miles, Globals.LUCENE_CENTROIDE_LATITUDE_INDEX, Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX, false);
        DistanceQuery dq2 = new DistanceQuery(lat, lng, miles, Globals.LUCENE_CENTROIDE_LATITUDE_INDEX, Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX, false);

        /* ensure that they hash to the same code, which will cause a cache hit in solr */
        assertEquals(dq1.getQuery().hashCode(), dq2.getQuery().hashCode());

        /* ensure that changing the radius makes a different hash code, creating a cache miss in solr */
        DistanceQuery widerQuery = new DistanceQuery(lat, lng, miles + 5.0, Globals.LUCENE_CENTROIDE_LATITUDE_INDEX, Globals.LUCENE_CENTROIDE_LONGITUDE_INDEX, false);
        assertTrue(dq1.getQuery().hashCode() != widerQuery.getQuery().hashCode());
    }
}
