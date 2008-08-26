package pt.utl.ist.lucene.test.advancedtests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.TimeDistanceSortSource;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.MyCalendar;

import java.io.IOException;
import java.util.Date;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.search.SortField;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * The objective of this class s help you to use Lgte Time Box Indexing
 * We use the same example in TestTimeDistanceSIMPLE but using time intervals
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestTimeBoxIndexingWithLgte extends TestCase
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
        doc1.addTimeBoxField(2004,2006);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado and Bruno Martins last year");
        doc2.addTimeBoxField(2006,2008);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins several years ago at the high school and very far away in time from lucene");
        doc3.addTimeBoxField(1989,1991);

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

            //doc 0
            MyCalendar date0 = new MyCalendar(2007,1,1,0,0,0);
            Date time0 = lgteHits.doc(0).getTime();
            //We need to put this tolerance in assertion because of years with 365 days because the middle time calculated will be in december 31 at 12 o'clock
            assertTrue(Math.abs(date0.getTimeInMillis() -  time0.getTime()) <= Globals.HALF_DAY_IN_MILISECONDS);
            assertEquals(lgteHits.doc(0).get("contents"),"Jorge Machado and Bruno Martins last year");

            //doc 1
            MyCalendar date1 = new MyCalendar(2005,1,1,0,0,0);
            Date time1 = lgteHits.doc(1).getTime();
            //We need to put this tolerance in assertion because of years with 365 days because the middle time calculated will be in december 31 at 12 o'clock
            assertTrue(Math.abs(date1.getTimeInMillis() -  time1.getTime())  <= Globals.HALF_DAY_IN_MILISECONDS);
            assertEquals(lgteHits.doc(1).get("contents"),"Jorge Machado and Bruno Martins three years ago");


        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }
}