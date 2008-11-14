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
public class TestTimeSpatialTextDistanceTogetherChangingConfiguration extends TestCase
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
        doc3.indexText("contents","Jorge Machado and Bruno Martins in Ahrus");
        doc3.addGeoPointField(56.162500, 10.144250);
        doc3.addTimeField(2000);


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

        //lets change system configuration seting ALFA in sigmoid distance/radium formula
        //We gone now be more permissive for results out of the box.First we will set a radium of 500 km
        // with our configuration a document in Ahrus at about 2000 km from lisbon will see spatial score decreased but
        // not so decreased as in base configuration 2000 / 500 km radium = 4 in sigmoid with alfa = 0.4 will obtain 5% in 100% possibles
        // but if we don't set the radium this will assume 3000 km and alfa 2 will be used about half of earth radium this document will have 0.8 spatial score

        String query;
        try
        {
            query = "contents:(jorge bruno ahrus)  time:2006 lat:39.292166 lng:-7.428733  radiumYears:3 filter:no";
            LgteQuery lgteQuery = LgteQueryParser.parseQuery(query);
            QueryConfiguration qc = lgteQuery.getQueryParams().getQueryConfiguration();
            qc.getQueryProperties().setProperty("sigmoide.distance.alfa","0.5");
            qc.getQueryProperties().setProperty("sigmoide.distance.alfa.2","0.3");
            qc.getQueryProperties().setProperty("sigmoide.distance.beta","2");

            LgteHits lgteHits;
            //document 3 is very far away and will see score at about 0.5
            lgteHits = searcher.search(lgteQuery);
            assertTrue(lgteHits.length() == 4);
            lgteHits.spatialScore(0);
            assertEquals(lgteHits.doc(0).get("id"),"2");
            assertEquals(lgteHits.doc(1).get("id"),"1");
            assertEquals(lgteHits.doc(2).get("id"),"4");
            assertEquals(lgteHits.doc(3).get("id"),"3");


            //turning off the radiuns the system will be more permissive and document 3 at 2000 km distante and 6 years will appear in first place
            //because of text and because distance and time are more permissives
            query = "contents:(jorge bruno ahrus) lat:39.292166 lng:-7.428733 filter:no";
            lgteQuery = LgteQueryParser.parseQuery(query);
            qc = lgteQuery.getQueryParams().getQueryConfiguration();
            qc.getQueryProperties().setProperty("sigmoide.distance.alfa","0.5");
            qc.getQueryProperties().setProperty("sigmoide.distance.alfa.2","0.3");
            qc.getQueryProperties().setProperty("sigmoide.distance.beta","2");
            lgteHits = searcher.search(lgteQuery);

            if(Model.defaultModel == Model.VectorSpaceModel)
            {
                assertEquals(lgteHits.doc(1).get("id"),"4");
                assertEquals(lgteHits.doc(0).get("id"),"3");

            }else
            {
                assertEquals(lgteHits.doc(0).get("id"),"3");
                assertEquals(lgteHits.doc(1).get("id"),"4");
            }
            assertEquals(lgteHits.doc(2).get("id"),"2");
            assertEquals(lgteHits.doc(3).get("id"),"1");



        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
        System.out.println("");
    }
}
