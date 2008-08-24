package pt.utl.ist.lucene.level1query.parser.tests;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import pt.utl.ist.lucene.level1query.Level1Query;
import pt.utl.ist.lucene.level1query.parser.Level1QueryParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author pjaol
 */
public class TestBuilder extends TestCase
{
    protected void setUp() throws IOException
    {
    }

    public void testBuilder() throws IOException, InvalidGeoException
    {
        Level1Query q = new Level1QueryParser().buildQuery("lat:-38.2323232 time:1990 starttime:1980 endtime:2009 lng:2.34432342 dlib.year:\\(year99\\) east:53.23442 dlib.year:(year99) west:54.23442 radiumYears:12 AND title:jonhy^0.223 AND north:51.23442 creator:joao~3 south:52.23442 AND (contents:(ticer))");
        assertEquals(q.toString(),"dlib.year:\\(year99\\) dlib.year:(year99) AND title:jonhy^0.223 AND creator:joao~3 AND (contents:(ticer))");
        assertTrue(q.getQueryParams().getLatitude() == -38.2323232);
        assertTrue(q.getQueryParams().getLongitude() == 2.34432342);
        assertTrue(q.getQueryParams().getRadiumYears() == 12);
        assertTrue(q.getQueryParams().getNorthlimit() == 51.23442);
        assertTrue(q.getQueryParams().getSouthlimit() == 52.23442);
        assertTrue(q.getQueryParams().getEastlimit() == 53.23442);
        assertTrue(q.getQueryParams().getWestlimit() == 54.23442);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        assertEquals(format.format(new Date(q.getQueryParams().getTimeMiliseconds())),"1990");
        assertEquals(format.format(new Date(q.getQueryParams().getStartTimeMiliseconds())),"1980");
        assertEquals(format.format(new Date(q.getQueryParams().getEndTimeMiliseconds())),"2009");
        q = new Level1QueryParser().buildQuery("lat:-38.2323232 lng:2.34432342 (dlib.year:(year99)^0.555 AND dlib.year:\"year99\"^0.555 (contents:[00000 TO 44444]) OR (contents:(ticer)))");
        assertEquals(q.toString(),"(dlib.year:(year99)^0.555 AND dlib.year:\"year99\"^0.555 (contents:[00000 TO 44444]) OR (contents:(ticer)))");
        q = new Level1QueryParser().buildQuery("dlib.year:(year99) AND (contents:(ticer))");
        assertEquals(q.toString(),"dlib.year:(year99) AND (contents:(ticer))");
        q = new Level1QueryParser().buildQuery("(tese) AND dc.creator:\"lusiada\"");
        assertEquals(q.toString(),"(tese) AND dc.creator:\"lusiada\"");
        q = new Level1QueryParser().buildQuery("((tese) AND \"lusiada\")");
        assertEquals(q.toString(),"((tese) AND \"lusiada\")");
        q = new Level1QueryParser().buildQuery("contents:jorge");
        assertEquals(q.toString(),"contents:jorge");
        q = new Level1QueryParser().buildQuery("site:jorge AND contents:joao");
        assertEquals(q.toString(),"site:jorge AND contents:joao");
        q = new Level1QueryParser().buildQuery("site:(jorge) AND joao");
        assertEquals(q.toString(),"site:(jorge) AND joao");
        q = new Level1QueryParser().buildQuery("site:\"jorge\" NOT (joao)");
        assertEquals(q.toString(),"site:\"jorge\" NOT (joao)");
        q = new Level1QueryParser().buildQuery("site:(jorge) ORG \"joao\"");
        assertEquals(q.toString(),"site:(jorge) org \"joao\"");
        q = new Level1QueryParser().buildQuery("site:(jorge) joao");
        assertEquals(q.toString(),"site:(jorge) joao");
        q = new Level1QueryParser().buildQuery("site:(jorge)joao");
        assertEquals(q.toString(),"site:(jorge) joao");
        q = new Level1QueryParser().buildQuery("   site:( jorge  )    joao");
        assertEquals(q.toString(),"site:(jorge) joao");
        q = new Level1QueryParser().buildQuery("   site:( jorge ana)    joao");
        assertEquals(q.toString(),"site:(jorge ana) joao");
        q = new Level1QueryParser().buildQuery("site:(\"http://purl.pt/1\") AND contents:(lusiadas) AND dc.creator:jorge AND jonhy");
        assertEquals(q.toString(),"site:(\"http://purl.pt/1\") AND contents:(lusiadas) AND dc.creator:jorge AND jonhy");
    }
}
