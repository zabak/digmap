package pt.utl.ist.lucene.utils.temporal.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.Timex2;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.Timex2TimeExpression;

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
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"2010");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("201");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"201");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("20");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"2");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("02");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"02");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33:44");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33:44Z");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15-5");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20100405");
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        t = new Timex2("2010-04-05T15:33");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20100405");
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
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20091228");
            assertEquals(mapping.getTimeExpressions().get(1).getExpression(),"20091229");
            assertEquals(mapping.getTimeExpressions().get(2).getExpression(),"20091230");
            assertEquals(mapping.getTimeExpressions().get(3).getExpression(),"20091231");
            assertEquals(mapping.getTimeExpressions().get(4).getExpression(),"20100101");
            assertEquals(mapping.getTimeExpressions().get(5).getExpression(),"20100102");
            assertEquals(mapping.getTimeExpressions().get(6).getExpression(),"20100103");

        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        t = new Timex2("2009-W54");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20091228");
            assertEquals(mapping.getTimeExpressions().get(1).getExpression(),"20091229");
            assertEquals(mapping.getTimeExpressions().get(2).getExpression(),"20091230");
            assertEquals(mapping.getTimeExpressions().get(3).getExpression(),"20091231");
            assertEquals(mapping.getTimeExpressions().get(4).getExpression(),"20100101");
            assertEquals(mapping.getTimeExpressions().get(5).getExpression(),"20100102");
            assertEquals(mapping.getTimeExpressions().get(6).getExpression(),"20100103");

        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        t = new Timex2("2009-W52");
        try {
            Timex2TimeExpression mapping = new Timex2TimeExpression(t);
            assertEquals(mapping.getTimeExpressions().get(0).getExpression(),"20091214");
            assertEquals(mapping.getTimeExpressions().get(1).getExpression(),"20091215");
            assertEquals(mapping.getTimeExpressions().get(2).getExpression(),"20091216");
            assertEquals(mapping.getTimeExpressions().get(3).getExpression(),"20091217");
            assertEquals(mapping.getTimeExpressions().get(4).getExpression(),"20091218");
            assertEquals(mapping.getTimeExpressions().get(5).getExpression(),"20091219");
            assertEquals(mapping.getTimeExpressions().get(6).getExpression(),"20091220");

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


            assertEquals(t2wStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091228");
            assertEquals(t2wStartingTimex2TimeExpression.getTimeExpressions().get(20).getExpression(),"20100117");    //14 + 7 of the start week

            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091230");
            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"20091231");
            assertEquals(t2dStartingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"20100101");

            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"200912");
            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"201001");
            assertEquals(t2mStartingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"201002");

            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"2009");
            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"2010");
            assertEquals(t2yStartingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"2011");

            assertEquals(t40dStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091230");
            assertEquals(t40dStartingTimex2TimeExpression.getTimeExpressions().get(40).getExpression(),"20100208"); //40 plus the start day

            assertEquals(t20mStartingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"200912");
            assertEquals(t20mStartingTimex2TimeExpression.getTimeExpressions().get(20).getExpression(),"201108"); //20 plus the start month


        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();
        }

//        for(TimeExpression expression: t2t.getTimeExpressions())
//        {
//            System.out.println(expression);
//        }
//
//        System.out.println("");
//        t = new Timex2("P2W","2010-01-03","ENDING");
//        t2t = new Timex2TimeExpression(t);
//
//        for(TimeExpression expression: t2t.getTimeExpressions())
//        {
//            System.out.println(expression);
//        }
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


            assertEquals(t2wEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091214");
            assertEquals(t2wEndingTimex2TimeExpression.getTimeExpressions().get(20).getExpression(),"20100103");    //14 + 7 of the start week

            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091230");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"20091231");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"20100101");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(3).getExpression(),"20100102");
            assertEquals(t2dEndingTimex2TimeExpression.getTimeExpressions().get(4).getExpression(),"20100103");

            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"200911");
            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"200912");
            assertEquals(t2mEndingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"201001");

            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"2008");
            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(1).getExpression(),"2009");
            assertEquals(t2yEndingTimex2TimeExpression.getTimeExpressions().get(2).getExpression(),"2010");

            assertEquals(t40dEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"20091124");
            assertEquals(t40dEndingTimex2TimeExpression.getTimeExpressions().get(40).getExpression(),"20100103"); //40 plus the start day

            assertEquals(t20mEndingTimex2TimeExpression.getTimeExpressions().get(0).getExpression(),"200805");
            assertEquals(t20mEndingTimex2TimeExpression.getTimeExpressions().get(20).getExpression(),"201001"); //29 plus the start month


        }
        catch (TimeExpression.BadTimeExpression badTimeExpression)
        {
            fail(badTimeExpression.toString());
            badTimeExpression.printStackTrace();
        }

//        for(TimeExpression expression: t2t.getTimeExpressions())
//        {
//            System.out.println(expression);
//        }
//
//        System.out.println("");
//        t = new Timex2("P2W","2010-01-03","ENDING");
//        t2t = new Timex2TimeExpression(t);
//
//        for(TimeExpression expression: t2t.getTimeExpressions())
//        {
//            System.out.println(expression);
//        }
    }
}
