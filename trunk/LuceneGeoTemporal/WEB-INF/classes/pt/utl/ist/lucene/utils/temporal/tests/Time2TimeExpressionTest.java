package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.Timex2;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.Timex2TimeExpression;
import pt.utl.ist.lucene.utils.temporal.TimexesDocument;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 11:28:31
 * @email machadofisher@gmail.com
 */
public class Time2TimeExpressionTest extends TestCase
{

    public void testSimpleTimexs()
    {
        Timex2 t = new Timex2("2010");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"2010");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("201");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"201");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("20");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"2");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("02");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"02");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"201004");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33:44");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33:44Z");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15-5");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void testMappingWeeksOfYear()
    {
        Timex2 t = new Timex2("2010-W1");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20091228");
            assertEquals(mapping.getTimeExpressions().get(1).getNormalizedExpression(),"20091229");
            assertEquals(mapping.getTimeExpressions().get(2).getNormalizedExpression(),"20091230");
            assertEquals(mapping.getTimeExpressions().get(3).getNormalizedExpression(),"20091231");
            assertEquals(mapping.getTimeExpressions().get(4).getNormalizedExpression(),"20100101");
            assertEquals(mapping.getTimeExpressions().get(5).getNormalizedExpression(),"20100102");
            assertEquals(mapping.getTimeExpressions().get(6).getNormalizedExpression(),"20100103");

        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        t = new Timex2("2009-W54");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20091228");
            assertEquals(mapping.getTimeExpressions().get(1).getNormalizedExpression(),"20091229");
            assertEquals(mapping.getTimeExpressions().get(2).getNormalizedExpression(),"20091230");
            assertEquals(mapping.getTimeExpressions().get(3).getNormalizedExpression(),"20091231");
            assertEquals(mapping.getTimeExpressions().get(4).getNormalizedExpression(),"20100101");
            assertEquals(mapping.getTimeExpressions().get(5).getNormalizedExpression(),"20100102");
            assertEquals(mapping.getTimeExpressions().get(6).getNormalizedExpression(),"20100103");

        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        t = new Timex2("2009-W52");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getNormalizedExpression(),"20091214");
            assertEquals(mapping.getTimeExpressions().get(1).getNormalizedExpression(),"20091215");
            assertEquals(mapping.getTimeExpressions().get(2).getNormalizedExpression(),"20091216");
            assertEquals(mapping.getTimeExpressions().get(3).getNormalizedExpression(),"20091217");
            assertEquals(mapping.getTimeExpressions().get(4).getNormalizedExpression(),"20091218");
            assertEquals(mapping.getTimeExpressions().get(5).getNormalizedExpression(),"20091219");
            assertEquals(mapping.getTimeExpressions().get(6).getNormalizedExpression(),"20091220");

        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
    public void testMappingPassTimeStarting()
    {
        Timex2 t2wStarting = new Timex2("P2W","2009-12-30","STARTING");
        Timex2 t2dStarting = new Timex2("P2D","2009-12-30","STARTING");
        Timex2 t2mStarting = new Timex2("P2M","2009-12-30","STARTING");
        Timex2 t2yStarting = new Timex2("P2Y","2009-12-30","STARTING");
        Timex2 t40dStarting = new Timex2("P40D","2009-12-30","STARTING");
        Timex2 t20mStarting = new Timex2("P20M","2009-12-30","STARTING");


        try
        {
            Timex2TimeExpression t2wStartingTimex2TimeExpression = new Timex2TimeExpression(t2wStarting);
            Timex2TimeExpression t2dStartingTimex2TimeExpression = new Timex2TimeExpression(t2dStarting);
            Timex2TimeExpression t2mStartingTimex2TimeExpression = new Timex2TimeExpression(t2mStarting);
            Timex2TimeExpression t2yStartingTimex2TimeExpression = new Timex2TimeExpression(t2yStarting);
            Timex2TimeExpression t40dStartingTimex2TimeExpression = new Timex2TimeExpression(t40dStarting);
            Timex2TimeExpression t20mStartingTimex2TimeExpression = new Timex2TimeExpression(t20mStarting);


            assertEquals(t2wStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091228");
            assertEquals(t2wStartingTimex2TimeExpression.getTimeExpressions().get(20).getNormalizedExpression(),"20100117");    //14 + 7 of the start week

            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091230");
            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"20091231");
            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"20100101");

            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"200912");
            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"201001");
            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"201002");

            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"2009");
            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"2010");
            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"2011");

            assertEquals(t40dStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091230");
            assertEquals(t40dStartingTimex2TimeExpression.getTimeExpressions().get(40).getNormalizedExpression(),"20100208"); //40 plus the start day

            assertEquals(t20mStartingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"200912");
            assertEquals(t20mStartingTimex2TimeExpression.getTimeExpressions().get(20).getNormalizedExpression(),"201108"); //20 plus the start month


        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();
        }
    }



    public void testMappingPassTimeEnding()
    {
        Timex2 t2wStarting = new Timex2("P2W","2010-01-03","ENDING");
        Timex2 t2dStarting = new Timex2("P4D","2010-01-03","ENDING");
        Timex2 t2mStarting = new Timex2("P2M","2010-01-03","ENDING");
        Timex2 t2yStarting = new Timex2("P2Y","2010-01-03","ENDING");
        Timex2 t40dStarting = new Timex2("P40D","2010-01-03","ENDING");
        Timex2 t20mStarting = new Timex2("P20M","2010-01-03","ENDING");


        try
        {
            Timex2TimeExpression t2wEndingTimex2TimeExpression = new Timex2TimeExpression(t2wStarting);
            Timex2TimeExpression t2dEndingTimex2TimeExpression = new Timex2TimeExpression(t2dStarting);
            Timex2TimeExpression t2mEndingTimex2TimeExpression = new Timex2TimeExpression(t2mStarting);
            Timex2TimeExpression t2yEndingTimex2TimeExpression = new Timex2TimeExpression(t2yStarting);
            Timex2TimeExpression t40dEndingTimex2TimeExpression = new Timex2TimeExpression(t40dStarting);
            Timex2TimeExpression t20mEndingTimex2TimeExpression = new Timex2TimeExpression(t20mStarting);


            assertEquals(t2wEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091214");
            assertEquals(t2wEndingTimex2TimeExpression.getTimeExpressions().get(20).getNormalizedExpression(),"20100103");    //14 + 7 of the start week

            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091230");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"20091231");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"20100101");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(3).getNormalizedExpression(),"20100102");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(4).getNormalizedExpression(),"20100103");

            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"200911");
            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"200912");
            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"201001");

            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"2008");
            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(1).getNormalizedExpression(),"2009");
            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(2).getNormalizedExpression(),"2010");

            assertEquals(t40dEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"20091124");
            assertEquals(t40dEndingTimex2TimeExpression.getTimeExpressions().get(40).getNormalizedExpression(),"20100103"); //40 plus the start day

            assertEquals(t20mEndingTimex2TimeExpression.getTimeExpressions().get(0).getNormalizedExpression(),"200805");
            assertEquals(t20mEndingTimex2TimeExpression.getTimeExpressions().get(20).getNormalizedExpression(),"201001"); //29 plus the start month


        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();
        }

    }


    public void buildTimex2TimeExpressionsSetTest()
    {
        String timex2 = "<DOC generator=\"timexdoc.py\">\n" +
                "<reftime rstart=\"1\" rend=\"10\" val=\"2005-12-09\">\n" +
                "<TIMEX2 rstart=\"1\" rend=\"10\" val=\"2005-12-09\">2005-12-09</TIMEX2>\n" +
                "</reftime>\n" +
                "<TEXT rstart=\"29\" rend=\"4117\">\n" +
                "<TIMEX2 set=\"\" rend=\"171\" val=\"2005-12\" tmxclass=\"point\" rstart=\"162\" dirclass=\"same\" parsenode=\".1 p51\" prenorm=\"|amb|M|_\">this month</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"898\" val=\"\" tmxclass=\"point\" rstart=\"871\" dirclass=\"same\" parsenode=\".6 p5\" prenorm=\"\">the better part of a century</TIMEX2>\n" +
                "<TIMEX2 set=\"YES\" rend=\"1760\" val=\"XXXX-XX-XX\" tmxclass=\"recur\" rstart=\"1752\" parsenode=\".10 p13\" prenorm=\"XXXX-XX-XX\">every day</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"1785\" val=\"P7Y\" anchor_dir=\"ENDING\" tmxclass=\"duration\" rstart=\"1766\" anchor_val=\"2005\" parsenode=\".10 p18\" prenorm=\"P7Y\">the past seven years</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"1801\" val=\"\" tmxclass=\"duration\" rstart=\"1791\" parsenode=\".10 p26\" prenorm=\"\">those years</TIMEX2>\n" +
                "\n" +
                "<TIMEX2 set=\"\" rend=\"2022\" val=\"\" tmxclass=\"point\" rstart=\"2005\" dirclass=\"same\" parsenode=\".11 p11\" prenorm=\"\">My best sales hour</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"2314\" val=\"2005-12-04\" tmxclass=\"point\" rstart=\"2309\" dirclass=\"before\" parsenode=\".16 w2\" prenorm=\"|dex|W|XXXX-WXX-7\">Sunday</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"2324\" val=\"2005-12-08T16:00\" tmxclass=\"point\" rstart=\"2319\" dirclass=\"before\" parsenode=\".16 p9\" prenorm=\"|dex|D|T16:00\">4 p.m.</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"3313\" val=\"P1D\" anchor_dir=\"BEFORE\" tmxclass=\"duration\" rstart=\"3309\" anchor_val=\"2005-12-09\" parsenode=\".25 p46\" prenorm=\"P1D\">a\n" +
                "day</TIMEX2>\n" +
                "<TIMEX2 set=\"\" rend=\"4031\" val=\"2005\" tmxclass=\"point\" rstart=\"4028\" dirclass=\"same\" parsenode=\".31 w19\" prenorm=\"|fq|_2005\">2005</TIMEX2>\n" +
                "</TEXT>\n" +
                "</DOC>";
        List<Timex2TimeExpression.Timex2TimeExpressionsSet> set = new TimexesDocument(timex2).getTimex2TimeExpressionsSets();

        assertEquals(set.get(0).getStartOffset(),1);
        assertEquals(set.get(0).getEndOffset(),10);
        assertEquals(set.get(1).getStartOffset(),162);
        assertEquals(set.get(1).getEndOffset(),171);
        assertEquals(set.get(2).getStartOffset(),871);
        assertEquals(set.get(2).getEndOffset(),898);
        assertEquals(set.get(3).getStartOffset(),1752);
        assertEquals(set.get(3).getEndOffset(),1760);
        assertEquals(set.get(4).getStartOffset(),1766);
        assertEquals(set.get(4).getEndOffset(),1785);
        assertEquals(set.get(5).getStartOffset(),1791);
        assertEquals(set.get(5).getEndOffset(),1801);

        assertEquals(set.get(0).getExpressions().get(0).getNormalizedExpression(),"20051209");
        assertEquals(set.get(1).getExpressions().get(0).getNormalizedExpression(),"200512");
        assertEquals(set.get(2).getExpressions().size(),0);
        assertEquals(set.get(3).getExpressions().size(),0);
        assertEquals(set.get(4).getExpressions().get(0).getNormalizedExpression(),"1998");
        assertEquals(set.get(4).getExpressions().get(1).getNormalizedExpression(),"1999");
        assertEquals(set.get(4).getExpressions().get(2).getNormalizedExpression(),"2000");
        assertEquals(set.get(4).getExpressions().get(3).getNormalizedExpression(),"2001");
        assertEquals(set.get(4).getExpressions().get(4).getNormalizedExpression(),"2002");
        assertEquals(set.get(4).getExpressions().get(5).getNormalizedExpression(),"2003");
        assertEquals(set.get(4).getExpressions().get(6).getNormalizedExpression(),"2004");
        assertEquals(set.get(4).getExpressions().get(7).getNormalizedExpression(),"2005");




    }
}
