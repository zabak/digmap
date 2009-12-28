package pt.utl.ist.lucene.utils.temporal;

import java.util.List;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 17:54:53
 * @email machadofisher@gmail.com
 */
public class Timex2TimeExpression
{
    Timex2 timex2;

    List<TimeExpression> expressions ;





    public Timex2TimeExpression(Timex2 timex2) throws TimeExpression.BadTimeExpression
    {
        this.timex2 = timex2;
        if(timex2.getVal()!=null && timex2.getVal().trim().length() > 0)
        {
            expressions = getTimeExpressions(timex2.getVal(),timex2.getAnchorVal(),timex2.getAnchorDir());

        }

        if(expressions.size() == 0)
            if(timex2.getAnchorVal() != null && timex2.getAnchorVal().trim().length() > 0)
            {
                expressions = getTimeExpressions(timex2.getAnchorVal(),null,null);
            }
    }

    private List<TimeExpression> getTimeExpressions(String timeExpr,String anchor, String anchorDir) throws TimeExpression.BadTimeExpression {
        List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();
        if(Timex2val.REGEXPR_YYYY_MM_DD_ANY.match(timeExpr))
        {
            timeExpressions.add(new TimeExpression(timeExpr.substring(0,10).replace("-","")));
        }
        else if(Timex2val.REGEXPR_YYYY_YYY_YY_Y.match(timeExpr))
        {
            timeExpressions.add(new TimeExpression(timeExpr));
        }
        else if(Timex2val.REGEXPR_YYYY_WXX.match(timeExpr))
        {
            int week = Integer.parseInt(timeExpr.substring(6));
            String year = timeExpr.substring(0,4);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set( Calendar.YEAR, Integer.parseInt(year) );
            calendar.set( Calendar.WEEK_OF_YEAR, week-1 );
            int startDay = calendar.get(Calendar.DAY_OF_MONTH);
            int startMonth = calendar.get(Calendar.MONTH)+1;
            int startYear = calendar.get(Calendar.YEAR);

            timeExpressions.add(new TimeExpression(startYear,startMonth,startDay));
            for(int i = 1; i < 7; i++)
            {
                calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1);
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endMonth = calendar.get(Calendar.MONTH)+1;
                int endYear = calendar.get(Calendar.YEAR);
                timeExpressions.add(new TimeExpression(endYear,endMonth,endDay));
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
                                TimeExpression timeExpression = new TimeExpression(i);
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
                                TimeExpression timeExpression = new TimeExpression(i);
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
                            timeExpressions.add(new TimeExpression(year,month));
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1);
                                timeExpressions.add(timeExpression);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            timeExpressions.add(new TimeExpression(year,month));
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.MONTH,c.get(Calendar.MONTH)-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1);
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
                            timeExpressions.add(new TimeExpression(year,month,day));
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.DAY_OF_YEAR,c.get(Calendar.DAY_OF_YEAR)+1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));
                                timeExpressions.add(timeExpression);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            timeExpressions.add(new TimeExpression(year,month,day));
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.DAY_OF_YEAR,c.get(Calendar.DAY_OF_YEAR)-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));
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
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            List<TimeExpression> wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + (c.get(Calendar.WEEK_OF_YEAR)+1),null,null);
                            timeExpressions.addAll(wexp);
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.WEEK_OF_YEAR,c.get(Calendar.WEEK_OF_YEAR)+1);
                                wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + (c.get(Calendar.WEEK_OF_YEAR)+1),null,null);
                                timeExpressions.addAll(wexp);
                            }
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            List<TimeExpression> wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + (c.get(Calendar.WEEK_OF_YEAR)+1),null,null);
                            timeExpressions.addAll(wexp);
                            for(int i = 0; i < x;i++)
                            {
                                c.set(Calendar.WEEK_OF_YEAR,c.get(Calendar.WEEK_OF_YEAR)-1);
                                wexp = getTimeExpressions(c.get(Calendar.YEAR) + "-W" + (c.get(Calendar.WEEK_OF_YEAR)+1),null,null);
                                timeExpressions.addAll(wexp);
                            }
                        }
                    }
                }
            }
        }
        return timeExpressions;
    }


    public Timex2 getTimex2() {
        return timex2;
    }

    public List<TimeExpression> getTimeExpressions() {
        return expressions;
    }

    public static void main(String[] args) throws TimeExpression.BadTimeExpression {

        Timex2 t = new Timex2("P2W","2009-12-30","STARTING");
        Timex2TimeExpression t2t = new Timex2TimeExpression(t);

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
