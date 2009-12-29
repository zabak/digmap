package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.GregorianCalendar;

import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import org.geotools.nature.Calendar;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 11:36:12
 * @email machadofisher@gmail.com
 */
public class TimeExpressionTest extends TestCase
{
    protected void setUp() throws IOException
    {
    }

    public void testExpressions()
    {
        //Good TimeExpressions
        String[] goodTimeExpressions =
                {
                        "2009",
                        "2020",
                        "1970",
                        "1800",
                        "199001","199002","199003","199004","199005","199006","199007","199008","199009","199010","199011","199012",
                        "19900101","19900110","19900131",
                        "19900201","19900210","19900228",
                        "19900301","19900310","19900331",
                        "19900401","19900410","19900430",
                        "19900501","19900510","19900531",
                        "19900601","19900610","19900630",
                        "19900701","19900710","19900731",
                        "19900801","19900810","19900831",
                        "19900901","19900910","19900930",
                        "19901001","19901010","19901031",
                        "19901101","19901110","19911130",
                        "19901202","19901210","19911231",
                };

        String[] badTimeExpressions =
                {
                        "-1",
//                        "0",
                        "19701",
                        "1800123",
                        "18001328",
                        "18001428",
                        "1800-328",
                        "180013-8",
                        "1800Novembro28",
                        "-1001228",
                        "180013283",
                        "19900132",
                        "19900230",
                        "19900332",
                        "19900431",
                        "19900532",
                        "19900631",
                        "19900732",
                        "19900832",
                        "19900931",
                        "19911032",
                        "19911131",
                        "19911232",
                };
        for(String expression: goodTimeExpressions)
        {
            try {
                new TimeExpression(expression);
            }catch (TimeExpression.BadTimeExpression badTimeExpression)
            { fail("Expected to be accepted and was rejected: " + expression + " : " + badTimeExpression.toString());}
        }
        for(String expression: badTimeExpressions)
        {
            try {
                new TimeExpression(expression);
                fail("Expected to be rejected and was accepted: " + expression);
            }catch (TimeExpression.BadTimeExpression badTimeExpression){ }
        }

        for(String expression: goodTimeExpressions)
        {
            try
            {
                TimeExpression t = new TimeExpression(expression);
                if(t.getType() == TimeExpression.Type.YYY)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression()) * 10);
                }
                if(t.getType() == TimeExpression.Type.YY)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression()) * 100);
                }
                if(t.getType() == TimeExpression.Type.Y)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression()) * 1000);
                }
                if(t.getType() == TimeExpression.Type.YYYY)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression()));
                    assertTrue(t.getYear() + 1 == t.getRightLimit().get(GregorianCalendar.YEAR));
                    assertTrue(1 == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                    assertTrue(1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                }
                else if(t.getType() == TimeExpression.Type.YYYYMM)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression().substring(0,4)));
                    assertTrue(t.getMonth() == Integer.parseInt(t.getExpression().substring(4,6)));

                    if(t.getMonth() == 12)
                    {
                        assertTrue(t.getYear() + 1 == t.getRightLimit().get(GregorianCalendar.YEAR));
                        assertTrue(1 == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                        assertTrue(1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                    }
                    else
                    {
                        assertTrue(t.getYear() == t.getRightLimit().get(GregorianCalendar.YEAR));
                        assertTrue(t.getMonth() + 1 == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                        assertTrue(1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                    }
                }
                else if(t.getType() == TimeExpression.Type.YYYYMMDD)
                {
                    assertTrue(t.getYear() == Integer.parseInt(t.getExpression().substring(0,4)));
                    assertTrue(t.getMonth() == Integer.parseInt(t.getExpression().substring(4,6)));
                    assertTrue(t.getDay() == Integer.parseInt(t.getExpression().substring(6)));

                    if(t.getDay() == t.getC().getActualMaximum(GregorianCalendar.DAY_OF_MONTH))
                    {
                        if(t.getMonth() == 12)
                        {
                            assertTrue(t.getYear() + 1 == t.getRightLimit().get(GregorianCalendar.YEAR));
                            assertTrue(1 == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                            assertTrue(1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                        }
                        else
                        {
                            assertTrue(t.getYear() == t.getRightLimit().get(GregorianCalendar.YEAR));
                            assertTrue(t.getMonth() + 1 == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                            assertTrue(1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                        }
                    }
                    else
                    {
                        assertTrue(t.getYear() == t.getRightLimit().get(GregorianCalendar.YEAR));
                        assertTrue(t.getMonth() == (t.getRightLimit().get(GregorianCalendar.MONTH)+1));
                        assertTrue(t.getDay() + 1 == t.getRightLimit().get(GregorianCalendar.DAY_OF_MONTH));
                    }
                }
            }
            catch (TimeExpression.BadTimeExpression badTimeExpression)
            {
                badTimeExpression.printStackTrace();
            }
        }


    }

    public void testNumbers()
    {
        Integer[][] goodDates =
                {
                        {0},
                        {2009},
                        {2020},
                        {1970},
                        {1800},
                        {1990,1},{1990,2},{1990,3},{1990,4},{1990,5},{1990,6},{1990,7},{1990,8},{1990,9},{1990,10},{1990,11},{1990,12},
                        {1990,10,1},{1990,1,10},{1990,1,31},
                        {1990,2,1},{1990,2,10},{1990,2,29},
                        {1990,3,1},{1990,3,10},{1990,3,31},
                        {1990,4,1},{1990,4,10},{1990,4,30},
                        {1990,5,1},{1990,5,10},{1990,5,31},
                        {1990,6,1},{1990,6,10},{1990,6,30},
                        {1990,7,1},{1990,7,10},{1990,7,31},
                        {1990,8,1},{1990,8,10},{1990,8,31},
                        {1990,9,1},{1990,9,10},{1990,9,30},
                        {1990,10,1},{1990,10,10},{1990,10,31},
                        {1990,11,1},{1990,11,10},{1990,11,30},
                        {1990,12,2},{1990,12,10},{1990,12,31},
                };
        Integer[][] badDates =
                {
                        {-1},
                        {1800,13,28},
                        {1800,14,28},
                        {1800,-3,28},
                        {18001,3,-8},
                        {-100,12,28},
                        {1800,13,283},
                        {1990,1,32},
                        {1990,2,30},
                        {1990,3,32},
                        {1990,4,31},
                        {1990,5,32},
                        {1990,6,31},
                        {1990,7,32},
                        {1990,8,32},
                        {1990,9,31},
                        {1991,10,32},
                        {1991,11,31},
                        {1991,12,32},
                        {1990,-1,3},
                        {1990,0,3},
                        {1990,3,0},
                };

        for(Integer[] date: goodDates)
        {
            try {
                TimeExpression t = null;
                switch(date.length)
                {
                    case(1):t = new TimeExpression(date[0]);break;
                    case(2):t = new TimeExpression(date[0],date[1]);break;
                    case(3):t = new TimeExpression(date[0],date[1],date[2]);break;
                }
                assertTrue(t != null);
                if(t.getType() == TimeExpression.Type.YYYY)
                {
                    assertTrue(date.length == 1);
                    assertTrue(date[0] == t.getYear() && date[0] == Integer.parseInt(t.getExpression()));
                }
                else if(t.getType() == TimeExpression.Type.YYYYMM)
                {
                    assertTrue(date.length == 2);
                    assertTrue(date[0] == t.getYear() && date[0] == Integer.parseInt(t.getExpression().substring(0,4)));
                    assertTrue(date[1] == t.getMonth() && date[1] == Integer.parseInt(t.getExpression().substring(4,6)));
                    if(date[1] < 10)
                        assertTrue(t.getExpression().substring(4,6).equals("0" + date[1]));
                }
                else if(t.getType() == TimeExpression.Type.YYYYMMDD)
                {
                    assertTrue(date.length == 3);
                    assertTrue(date[0] == t.getYear() && date[0] == Integer.parseInt(t.getExpression().substring(0,4)));
                    assertTrue(date[1] == t.getMonth() && date[1] == Integer.parseInt(t.getExpression().substring(4,6)));
                    if(date[1] < 10)
                        assertTrue(t.getExpression().substring(4,6).equals("0" + date[1]));
                    assertTrue(date[2] == t.getDay() && date[2] == Integer.parseInt(t.getExpression().substring(6)));
                    if(date[2] < 10)
                        assertTrue(t.getExpression().substring(6).equals("0" + date[2]));
                }
            } catch (TimeExpression.BadTimeExpression badTimeExpression) {
                String dateStr = "" + date[0];
                if(date.length > 1)
                    dateStr+= "/" + date[1];
                if(date.length > 2)
                    dateStr+= "/" + date[2];
                fail("Expected to be accepted and was rejected: " + dateStr + " : " + badTimeExpression.toString());
            }

        }

        for(Integer[] date: badDates)
        {
            try {
                switch(date.length)
                {
                    case(1):new TimeExpression(date[0]);break;
                    case(2):new TimeExpression(date[0],date[1]);break;
                    case(3):new TimeExpression(date[0],date[1],date[2]);break;
                }
                String dateStr = "" + date[0];
                if(date.length > 1)
                    dateStr+= "/" + date[1];
                if(date.length > 2)
                    dateStr+= "/" + date[2];
                fail("Expected to be rejected and was accepted: " + dateStr);
            } catch (TimeExpression.BadTimeExpression badTimeExpression) {

            }

        }


    }
}
