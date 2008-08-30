package pt.utl.ist.lucene.test;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.SortField;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.TimeDistanceSortSource;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * The objective of this class s help you to use Lgte Time Queries in a very quick example in a very simple example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestTimeDistanceWithLgteSIMPLE extends TestCase
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
        LgteIndexWriter writer = new LgteIndexWriter(path, true, Model.OkapiBM25Model);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins three years ago");
        doc1.addTimeField(2005);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins last year");
        doc2.addTimeField(2007);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins several years ago at the high school and very far away in time from lucene");
        doc3.addTimeField(1990);

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
        //Lets use Lgte wrapper to dont worry about details with time, filters and all of that boring stuff
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel,path);

        //try find Jorge and Bruno documents last year
        // In this case the closest one will be in 2007
        String query = "contents:(Jorge Bruno) time:2007 radiumYears:4";

        //Lets use aos Time Sorter to dont worry about distances and stuff like that, he will do the job
        TimeDistanceSortSource dsort = new TimeDistanceSortSource();
        LgteSort sort = new LgteSort(new SortField("foo", dsort));

        //LgteHits give you Documents and time with unecessary lines of boring code
        LgteHits lgteHits;
        try
        {
            lgteHits = searcher.search(query, sort);
            assertTrue(lgteHits.length() == 2);

             SimpleDateFormat s = new SimpleDateFormat("yyyy");

            //doc 0
            Date time0 = lgteHits.doc(0).getTime();
            int year0 = Integer.parseInt(s.format(time0));
            assertTrue(year0 == 2007);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins last year");

            //doc 1
            Date time1 = lgteHits.doc(1).getTime();
            int year1 = Integer.parseInt(s.format(time1));
            assertTrue(year1 == 2005);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins three years ago");


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}
