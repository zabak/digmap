package pt.utl.ist.lucene.utils.tests;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.pjaol.search.geo.utils.InvalidGeoException;
import pt.utl.ist.lucene.utils.Dates;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */

public class TestDates extends TestCase
{
    protected void setUp() throws IOException
    {
    }


    public void testMiddleDate() throws InvalidGeoException, IOException
    {
        testMiddleDate("1980","1990","1985",5);
        testMiddleDate("1980-1-2","1990-4-5","1985",5);
        testMiddleDate("2-1980","4-1990","1985",5);
        testMiddleDate("3-2-1980","1990-5","1985",5);
        testMiddleDate("2-1980","1-1990","1985",5);
        testMiddleDate("1980-3","1990-3","1985",5);
        testMiddleDate("1970","1960","1965",5);
        testMiddleDate("1970-2","3-1960","1965",5);
        testMiddleDate("1970-1","12-1960","1965",5);
        testMiddleDate("12-1970","1-1960","1965",5);
        testMiddleDate("1-12-1970","1960-3-4","1965",5);
        testMiddleDate("","1960-3-4","1960",Integer.MAX_VALUE);
        testMiddleDate("","1960","1960",Integer.MAX_VALUE);
        testMiddleDate("","1960","1960",Integer.MAX_VALUE);
        testMiddleDate("","1960","1960",Integer.MAX_VALUE);
        testMiddleDate("1970","","1970",Integer.MAX_VALUE);
        testMiddleDate("19702-3","",null,Integer.MAX_VALUE);
        testMiddleDate("1970-3","","1970",Integer.MAX_VALUE);
        testMiddleDate("-11-1970","","1970",Integer.MAX_VALUE);
        testMiddleDate("","",null,Integer.MAX_VALUE);
        testMiddleDate("asd","asdasd",null,Integer.MAX_VALUE);
        testMiddleDate("","asdasd",null,Integer.MAX_VALUE);
        testMiddleDate("asfdsdf","",null,Integer.MAX_VALUE);
        testMiddleDate("asfdsdf",null,null,Integer.MAX_VALUE);
        testMiddleDate(null,null,null,Integer.MAX_VALUE);
    }

    protected void testMiddleDate(String startDate, String endDate, String expectedYear, int expectedRadium) throws IOException, InvalidGeoException
    {

        Date middleDate = Dates.getMiddleDate(startDate,endDate);
        int radium = Integer.MAX_VALUE;
        if(middleDate != null)
            radium = Dates.getDistanceYears(Dates.getNormalizedDate(middleDate),endDate);

        if(middleDate != null && expectedYear != null)
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String middleYear = format.format(middleDate);
            assertEquals(expectedYear,middleYear);
        }
        else if(expectedYear != null)
        {
            fail("Expected year != null");
        }
        else if(middleDate != null)
        {
            fail("Expected year null");
        }
        else
        {
            assertTrue(radium == expectedRadium);
        }

    }


}
