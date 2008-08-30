package pt.utl.ist.lucene.test.advancedtests;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.SortField;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.TimeDistanceSortSource;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.MyCalendar;

import java.io.IOException;

/**
 *
 * The objective of this class is test miliseconds precision defining a box
 * @see TestTimeDistanceInMilisecondsWithLgte  This is the sabe test but using a TimeBox to index
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestTimeIntervalsInMilisecondsWithLgte extends TestCase
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
        LgteIndexWriter writer = new LgteIndexWriter(path, true, Model.defaultModel);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText("id", "1");
        doc1.indexText("contents","Jorge Machado and Bruno Martins three years ago entering in laboratory");
        doc1.addTimeBoxField(2004,11,20,9,0,10,2006,11,20,9,0,10);

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText("id", "2");
        doc2.indexText("contents","Jorge Machado last year entering in cinema");
        doc2.addTimeBoxField(2006,1,12,20,45,15,2008,1,12,20,45,15);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText("id", "3");
        doc3.indexText("contents","Jorge Machado and Bruno Martins several years ago at the high school and very far away in time from lucene");
        doc3.addTimeBoxField(1989,2,3,10,50,10,1991,2,3,10,50,10);

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
        String query = "contents:(Jorge Bruno)";

        LgteQuery lgteQueryResult1;
        LgteQuery lgteQueryResult1and2;
        try
        {
            lgteQueryResult1 = LgteQueryParser.parseQuery(query);
            lgteQueryResult1and2 = LgteQueryParser.parseQuery(query);

            MyCalendar c1Aux = new MyCalendar(2005,11,20,9,0,10);
            MyCalendar c2Aux = new MyCalendar(2007,1,12,20,45,15);
            long dif = c2Aux.getTimeInMillis() - c1Aux.getTimeInMillis();

            lgteQueryResult1.getQueryParams().setTimeMiliseconds(c1Aux.getTimeInMillis());
            lgteQueryResult1.getQueryParams().setRadiumMiliseconds(dif -1);
            lgteQueryResult1and2.getQueryParams().setTimeMiliseconds(c1Aux.getTimeInMillis());
            lgteQueryResult1and2.getQueryParams().setRadiumMiliseconds(dif+1);
        }
        catch (ParseException e)
        {
            fail(e.toString());
            return;
        }

        //Lets use aos Time Sorter to dont worry about distances and stuff like that, he will do the job
        TimeDistanceSortSource dsort1 = new TimeDistanceSortSource();
        LgteSort sort1 = new LgteSort(new SortField("foo", dsort1));

        //LgteHits give you Documents and time with unecessary lines of boring code
        LgteHits lgteHits1;
        LgteHits lgteHits1and2;
        lgteHits1 = searcher.search(lgteQueryResult1, sort1);
        assertTrue(lgteHits1.length() == 1);

        lgteHits1and2 = searcher.search(lgteQueryResult1and2, sort1);
        assertTrue(lgteHits1and2.length() == 2);


        long time1_0 = lgteHits1.doc(0).getTime().getTime();
        long time1and2_0 = lgteHits1and2.doc(0).getTime().getTime();
        long time1and2_1 = lgteHits1and2.doc(1).getTime().getTime();
        assertTrue(time1_0 == new MyCalendar(2005,11,20,9,0,10).getTimeInMillis());
        assertTrue(time1and2_0 == new MyCalendar(2005,11,20,9,0,10).getTimeInMillis());
        assertTrue(time1and2_1 == new MyCalendar(2007,1,12,20,45,15).getTimeInMillis());


        searcher.close();
    }
}
