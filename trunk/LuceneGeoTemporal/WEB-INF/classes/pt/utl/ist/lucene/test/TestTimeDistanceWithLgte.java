package pt.utl.ist.lucene.test;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.filter.ITimeDistancesWrapper;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.sort.LgteSort;
import pt.utl.ist.lucene.sort.sorters.TimeDistanceSortSource;

import java.io.IOException;

/**
 *
 * The objective of this class s help you to use Lgte with Dates
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestTimeDistanceWithLgte extends TestCase
{

    /********************************
     *
     * #######################################################################
     * README
     * ######################################################################
     * PLEASE SET DATA DIR WHERE INDEXES SHOULD BE PLACED IN app.properties
     * IT's IS NECESSARY BECAUSE LANGUAGE MODELING USES A SPETIAL DOCUMENT ID INDEX
     */
    private String path = Globals.DATA_DIR + "/" + getClass().getName();

    boolean lm = false;

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


    private void addPoint(IndexWriter writer, String name, int year, int month, int day) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        ++i;
        doc.indexText("id", "" + i);
        doc.indexText("name",name);
        doc.addTimeField(year,month,day);
        doc.indexText("metafile","doc");
        ((LgteIndexWriter)writer).addDocument(doc);
    }


    private void addData(IndexWriter writer) throws IOException
    {
        addPoint(writer, "6",1975,1,1);
        addPoint(writer, "4", 1980,2,4);
        addPoint(writer, "2", 1989,3,10);
        addPoint(writer, "1", 1990,6,8);  //Center GeoPoint
        addPoint(writer, "3", 1994,12,6);
        addPoint(writer, "5", 2003,1,7);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        LgteIndexSearcherWrapper searcher;

        if(lm)
            searcher = new LgteIndexSearcherWrapper(Model.LanguageModel,path);
        else
            searcher = new LgteIndexSearcherWrapper(Model.VectorSpaceModel,path);


        int years = 14;

        //Building query
        QueryParams queryParams = new QueryParams();
        queryParams.setTime("1990-6-8");
        queryParams.setRadiumYears(years);
        //create a term level1query to searchCallback against indexText documents
        //doc is a word to find in text
        Query tq = new TermQuery(new Term("metafile", "doc"));


        LgteQuery lgteQuery = new LgteQuery(tq,queryParams);
        TimeDistanceSortSource dsort = new TimeDistanceSortSource();
        LgteSort sort = new LgteSort(new SortField("foo", dsort));

        LgteHits hits = searcher.search(lgteQuery, sort);
        int results = hits.length();

        // Get a list of distances, you don't need this but we keep it available, our LgteHits gives you Distance Information
        ITimeDistancesWrapper timeDistancesWrapper = dsort.getTimeDistancesWrapper();

        // distances calculated from filter first pass must be less than total
        // docs, from the above test of 6 items, 5 will come from the boundary box
        // filter, but only 5 are actually in the radius of the results.

        // Note Boundary Box filtering, is not accurate enough for most systems.


        System.out.println("Distance Filter filtered: " + timeDistancesWrapper.getTimeDistances().size());
        System.out.println("Results: " + results);
        System.out.println("=============================");
        assertEquals(5, timeDistancesWrapper.getTimeDistances().size());
        assertEquals(5, results);

        int lastYears = 0;
        long lastMili = 0;
        for (int i = 0; i < results; i++)
        {
            LgteDocumentWrapper d = hits.doc(i);

            String name = d.get("name");

            int distanceYears = hits.timeDistanceYears(i);
            long distanceMili = hits.timeDistanceMiliseconds(i);
            assertTrue(distanceYears <= years);
            assertTrue(distanceYears >= lastYears);
            assertTrue(distanceMili >= lastMili);
            lastYears = distanceYears;
            lastMili = distanceMili;

            System.out.println("Name: " + name + ", Distance (years, mili):" + distanceYears + " |" + distanceMili);

            switch(i)
            {
                case 0:assertTrue(d.get("name").equals("1"));break;
                case 1:assertTrue(d.get("name").equals("2"));break;
                case 2:assertTrue(d.get("name").equals("3"));break;
                case 3: assertTrue(d.get("name").equals("4"));break;
                case 4: assertTrue(d.get("name").equals("5"));break;
            }

        }
        searcher.close();

    }
}
