package pt.utl.ist.lucene.test.objectwidth;

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
public class TestWidthSigmoidLgteSIMPLE extends TestCase
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
        doc1.indexText("contents","Portugal Center");
        doc1.addGeoBoxField(39.788440,37.788440, -9.171290, -7.171290);
        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Coimbra Portugal");
        doc2.addGeoBoxField(40.4,40.0, -8.728733, -8.0);
        writer.addDocument(doc1);
        writer.addDocument(doc2);
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
        String query = "contents:(portugal) lng:-8.23635 lat:39.84330";

        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 2);
            assertEquals(lgteHits.doc(0).get("id"),"1");
            assertEquals(lgteHits.doc(1).get("id"),"2");
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
