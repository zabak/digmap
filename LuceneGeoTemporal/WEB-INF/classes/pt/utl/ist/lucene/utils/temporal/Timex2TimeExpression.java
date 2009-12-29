package pt.utl.ist.lucene.utils.temporal;

import org.dom4j.Document;
import org.dom4j.XPath;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.apache.log4j.Logger;

import java.util.*;

import pt.utl.ist.lucene.utils.Dom4jUtil;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 17:54:53
 * @email machadofisher@gmail.com
 */
public class Timex2TimeExpression
{
    Timex2 timex2;
    List<TimeExpression> expressions;
    private static final Logger logger = Logger.getLogger(TimextagDocumentSpliter.class);


    public static class Timex2TimeExpressionsSet
    {
        int startOffset;
        int endOffset;
        List<TimeExpression> expressions;
        Timex2 timex2;


        public Timex2TimeExpressionsSet(int startOffset, int endOffset, Timex2 timex2, List<TimeExpression> expressions) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.timex2 = timex2;
            this.expressions = expressions;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public List<TimeExpression> getExpressions() {
            return expressions;
        }

        public Timex2 getTimex2() {
            return timex2;
        }
    }

    public static List<Timex2TimeExpressionsSet> buildTimex2TimeExpressionsSet(String timexesXml)
    {
        List<Timex2TimeExpressionsSet> timex2TimeExpressionsSets = new ArrayList<Timex2TimeExpressionsSet>();

        if(timexesXml != null)
        {
            Document dom;
            try {
                dom = Dom4jUtil.parse(timexesXml);
                XPath xPath = dom.createXPath("//TIMEX2");
                List<Element> timexes2 = xPath.selectNodes(dom.getRootElement());
                if(timexes2 != null && timexes2.size() > 0)
                {
                    for(Element timexElement: timexes2)
                    {
                        Timex2 timex2 = new Timex2(timexElement);
                        try {
                            Timex2TimeExpression timex2TimeExpression = new Timex2TimeExpression(timex2);
                            Timex2TimeExpressionsSet timex2TimeExpressionsSet = new Timex2TimeExpressionsSet(timex2.getRstart(),timex2.getRend(),timex2,timex2TimeExpression.getTimeExpressions());
                            timex2TimeExpressionsSets.add(timex2TimeExpressionsSet);
                        } catch (TimeExpression.BadTimeExpression badTimeExpression)
                        {
                            logger.error(badTimeExpression + ": val(" + timex2.getVal() + ") anchor_val(" + timex2.getAnchorVal() + ") anchor_dir(" + timex2.getAnchorDir() + ")",badTimeExpression);
                        }
                    }
                }
            } catch (DocumentException e) {
                logger.error(e,e);
                logger.error("TIMEXES XML was:" + timexesXml);
            }
        }
        return timex2TimeExpressionsSets;
    }


    public Timex2TimeExpression(Timex2 timex2) throws TimeExpression.BadTimeExpression
    {
        this.timex2 = timex2;
        if(timex2.getVal()!=null && timex2.getVal().trim().length() > 0)
            expressions = getTimeExpressions(timex2.getVal(),timex2.getAnchorVal(),timex2.getAnchorDir());

        if(expressions == null || expressions.size() == 0)
        {
            if(timex2.getAnchorVal() != null && timex2.getAnchorVal().trim().length() > 0)
                expressions = getTimeExpressions(timex2.getAnchorVal(),null,null);
            else if(expressions == null)
                expressions = new ArrayList<TimeExpression>();
        }
        
        if(expressions.size() == 0)
        {
            expressions.add(new TimeExpressionUnkown(timex2.getText()));
        }
    }

    /**
     * todo PX.ZW PX.ZY PX.ZM PX.ZD  where X.Z is a fraction number e.g. for the last 6 and a half weeks could be represented by P6.5W
     * todo PXYZM PXMZD PXWZD PXMZW etc etc  previous e.g. P6W3D six weeks and three days 
     * @param timeExpr
     * @param anchor
     * @param anchorDir
     * @return
     * @throws TimeExpression.BadTimeExpression
     */
    private List<TimeExpression> getTimeExpressions(String timeExpr,String anchor, String anchorDir) throws TimeExpression.BadTimeExpression {
        List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();
        if(Timex2val.REGEXPR_YYYY_MM_DD_ANY.match(timeExpr))
        {
            timeExpressions.add(new TimeExpression(timeExpr.substring(0,10).replace("-",""),timex2.getText()));
        }
        if(Timex2val.REGEXPR_YYYY_MM.match(timeExpr))
        {
            timeExpressions.add(new TimeExpression(timeExpr.substring(0,7).replace("-",""),timex2.getText()));
        }
        else if(Timex2val.REGEXPR_YYYY_YYY_YY_Y.match(timeExpr))
        {
            timeExpressions.add(new TimeExpression(timeExpr,timex2.getText()));
        }
        else if(Timex2val.REGEXPR_YYYY_WXX.match(timeExpr))
        {
            int week = Integer.parseInt(timeExpr.substring(6));
            String year = timeExpr.substring(0,4);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set( Calendar.YEAR, Integer.parseInt(year) );
            calendar.set( Calendar.WEEK_OF_YEAR, week-1 );
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            int startDay = calendar.get(Calendar.DAY_OF_MONTH);
            int startMonth = calendar.get(Calendar.MONTH)+1;
            int startYear = calendar.get(Calendar.YEAR);

            timeExpressions.add(new TimeExpression(startYear,startMonth,startDay,timex2.getText()));
            for(int i = 1; i < 7; i++)
            {
                calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1);
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endMonth = calendar.get(Calendar.MONTH)+1;
                int endYear = calendar.get(Calendar.YEAR);
                timeExpressions.add(new TimeExpression(endYear,endMonth,endDay,timex2.getText()));
            }
        }
        if(anchor != null && anchor.trim().length() > 0 && anchorDir != null && anchorDir.trim().length() > 0)
        {
            if(Timex2val.REGEXPR_PXY.match(timeExpr))
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("Y")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0)
                {
                    int year = anchorExprs.get(0).getYear();
                    if(anchorDir.equalsIgnoreCase("starting"))
                    {
                        for(int i = year; i <= year+x;i++)
                        {
                            if(i >= 0)
                            {
                                TimeExpression timeExpression = new TimeExpression(i,timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                    }
                    else if(anchorDir.equalsIgnoreCase("ending"))
                    {
                        for(int i = year-x; i <= year;i++)
                        {
                            if(i >= 0)
                            {
                                TimeExpression timeExpression = new TimeExpression(i,timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                    }
                }
            }
            else if(Timex2val.REGEXPR_PXM.match(timeExpr))
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("M")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0)
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    if(year > 0 && month > 0)
                    {
                        if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            timeExpressions.add(new TimeExpression(year,month,timex2.getText()));
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.MONTH,1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            timeExpressions.add(new TimeExpression(year,month,timex2.getText()));
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.MONTH,-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                    }
                }
            }
            else if(Timex2val.REGEXPR_PXD.match(timeExpr))
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("D")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0)
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    int day = anchorExprs.get(0).getDay();
                    if(year > 0 && month > 0 && day > 0)
                    {
                        if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            timeExpressions.add(new TimeExpression(year,month,day,timex2.getText()));
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.DAY_OF_YEAR,1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            timeExpressions.add(new TimeExpression(year,month,day,timex2.getText()));
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.DAY_OF_YEAR,-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),timex2.getText());
                                timeExpressions.add(timeExpression);
                            }
                        }
                    }
                }
            }
            else if(Timex2val.REGEXPR_PXW.match(timeExpr))
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("W")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0)
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    int day = anchorExprs.get(0).getDay();
                    if(year > 0 && month > 0 && day > 0)
                    {
                        if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar(year,month-1,day);
                            int week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                            if(month == 1 && week > 52)       //to avoid a bug in JVM (error example: 03-01-2010 gives 2010-W54)
                                week = 1;
                            List<TimeExpression> wexp = getTimeExpressions(String.format("%04d",year) + "-W" + week,null,null);
                            timeExpressions.addAll(wexp);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.WEEK_OF_YEAR,1);
                                week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                                if(c.get(Calendar.MONTH) == 1 && week > 52)
                                {
                                    week = 1;
                                }
                                wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + (week),null,null);
                                timeExpressions.addAll(wexp);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar(year,month-1,day);
                            int week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                            if(month == 1 && week > 52)       //to avoid a bug in JVM (error example: 03-01-2010 gives 2010-W54)
                                week = 1;
                            List<TimeExpression> wexp = getTimeExpressions(String.format("%04d",year) + "-W" + week,null,null);
                            timeExpressions.addAll(wexp);
                            for(int i = 0; i < x;i++)
                            {

                                c.add(Calendar.WEEK_OF_YEAR,-1);
                                week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                                if(c.get(Calendar.MONTH) == 1 && week > 52)
                                {
                                    week = 1;
                                }
                                wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + week,null,null);
                                timeExpressions.addAll(wexp);
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(timeExpressions,new Comparator<TimeExpression>()
        {
            public int compare(TimeExpression o1, TimeExpression o2) {
                if(o1.getC().getTimeInMillis() == o2.getC().getTimeInMillis())
                    return 0;
                if(o1.getC().getTimeInMillis() > o2.getC().getTimeInMillis() )
                    return 1;
                else return -1;
            }
        });
        return timeExpressions;
    }


    public Timex2 getTimex2() {
        return timex2;
    }

    public List<TimeExpression> getTimeExpressions() {
        return expressions;
    }

    public static void main(String[] args) throws TimeExpression.BadTimeExpression {

        Timex2 t = new Timex2("P2W","2010-01-03","ENDING");
        Timex2TimeExpression t2t = new Timex2TimeExpression(t);

        for(TimeExpression expression: t2t.getTimeExpressions())
        {
            System.out.println(expression);
        }

        t = new Timex2("P20M","2010-01-03","ENDING");
        t2t = new Timex2TimeExpression(t);

        for(TimeExpression expression: t2t.getTimeExpressions())
        {
            System.out.println(expression);
        }

        System.out.println("");
        t = new Timex2("P2W","2010-01-03","ENDING");
        t2t = new Timex2TimeExpression(t);

        for(TimeExpression expression: t2t.getTimeExpressions())
        {
            System.out.println(expression);
        }

    }
}
