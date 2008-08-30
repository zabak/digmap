package pt.utl.ist.lucene.test.highlights;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.VersionEnum;

import java.io.IOException;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestHighlightsWithSIMPLE extends TestCase
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
        String query = "contents:(jorge bruno in lisbon) lat:38.754720 lng:-9.185010 radiumKm:250";


        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            if(LuceneVersionFactory.getLuceneVersion().getVersion() == VersionEnum.v2)
                assertEquals(lgteHits.summary(0,"contents"),"<b><i>Jorge</i></b>&nbsp;Machado&nbsp;and&nbsp;<b><i>Bruno</i></b>&nbsp;Martins&nbsp;<b><i>in</i></b>&nbsp;<b><i>Lisbon</i></b>");
            else if(LuceneVersionFactory.getLuceneVersion().getVersion() == VersionEnum.v143)
               assertEquals(lgteHits.summary(0,"contents"),"<b><i>Jorge</i></b> Machado and <b><i>Bruno</i></b> Martins <b><i>in</i></b> <b><i>Lisbon</i></b>");
            else
                fail("Please add the new version of lucene into this test case");
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
