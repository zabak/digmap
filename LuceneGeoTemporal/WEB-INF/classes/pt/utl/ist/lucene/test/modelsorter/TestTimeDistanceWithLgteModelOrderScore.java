package pt.utl.ist.lucene.test.modelsorter;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * The objective of this class is test order provided by default score model
 * time*0.33 + space*0.33 + text*0.33
 * where time := sigmoid(distance, radium)
 *
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestTimeDistanceWithLgteModelOrderScore extends TestCase
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


    protected void setUp() throws IOException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path, true, Model.LanguageModel);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins three years ago");
        doc1.addTimeField(2005);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins last year help TEXT FIRST DOCUMENT");
        doc2.addTimeField(2007);

        //we take out Bruno to decrease text score in this document
        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado several years ago at the high school and very far away in time from lucene");
        doc3.addTimeField(1990);

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
        //Lets use Lgte wrapper to dont worry about details with time, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(path);

        //try find Jorge and Bruno documents last year
        // In this case the closest one will be in 1990 but once we are using score order the first will be
        String query = "contents:(Jorge Bruno FIRST DOCUMENT) time:1980 radiumYears:4 order:sc filter:no";

        //Lets use aos Time Sorter to dont worry about distances and stuff like that, he will do the job


        //LgteHits give you Documents and time with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query);
            assertTrue(lgteHits.length() == 3);

            SimpleDateFormat s = new SimpleDateFormat("yyyy");

            //doc 0
            Date time0 = lgteHits.doc(0).getTime();
            int year0 = Integer.parseInt(s.format(time0));
            assertTrue(year0 == 2007);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins last year help TEXT FIRST DOCUMENT");

            //Scores are diferent in diferent models
            if(Model.defaultModel== Model.VectorSpaceModel)
            {
                //doc 1
                Date time1 = lgteHits.doc(1).getTime();
                int year1 = Integer.parseInt(s.format(time1));
                assertTrue(year1 == 2005);
                assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins three years ago");

                //doc 3
                Date time2 = lgteHits.doc(2).getTime();
                int year2 = Integer.parseInt(s.format(time2));
                assertTrue(year2 == 1990);
                assertEquals(lgteHits.doc(2).get("contents"),"Jorge Machado several years ago at the high school and very far away in time from lucene");
            }
            else
            {

                //doc 3
                Date time1 = lgteHits.doc(1).getTime();
                int year1 = Integer.parseInt(s.format(time1));
                assertTrue(year1 == 1990);
                assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado several years ago at the high school and very far away in time from lucene");

                //doc 1
                Date time2 = lgteHits.doc(2).getTime();
                int year2 = Integer.parseInt(s.format(time2));
                assertTrue(year2 == 2005);
                assertEquals(lgteHits.doc(2).get("contents"),"Jorge Machado and Bruno Martins three years ago");

            }


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
