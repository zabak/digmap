package pt.utl.ist.lucene.utils.temporal.tides;

import org.apache.log4j.Logger;

import java.util.*;

import pt.utl.ist.lucene.utils.temporal.tides.Timex2;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.DocumentTemporalSentences;
import pt.utl.ist.lucene.utils.temporal.TimeExpressionUnkown;

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
    private static final Logger logger = Logger.getLogger(DocumentTemporalSentences.class);
    TimeExpression.TEClass teClass = TimeExpression.TEClass.Point;


    public Timex2TimeExpression(Timex2 timex2) throws TimeExpression.BadTimeExpression
    {


        this.timex2 = timex2;
        if(timex2.getTmxclass() != null && timex2.getTmxclass().equals("genpoint"))
            teClass = TimeExpression.TEClass.GenPoint;

        if(timex2.getVal()!=null && timex2.getVal().trim().length() > 0)
            expressions = getTimeExpressions(timex2.getVal(),timex2.getAnchorVal(),timex2.getAnchorDir());

        if(expressions == null)
        {
//            if(timex2.getAnchorVal() != null && timex2.getAnchorVal().trim().length() > 0)
//                expressions = getTimeExpressions(timex2.getAnchorVal(),null,null);
//            else if(expressions == null)
                expressions = new ArrayList<TimeExpression>();
        }

        if(expressions.size() == 0)
        {
            TimeExpression timeExpression = new TimeExpressionUnkown(timex2.getText());
            timeExpression.setTimex2(timex2);
            expressions.add(timeExpression);
        }
    }

    /**
     * Clean expressions like P100000D and replace with months or years or with unknown expression
     * @param type
     * @param timeExpr to clean
     * @return
     */
    private String cleanExpression(Timex2ValRegExprs type, String timeExpr)
    {
        if(type.isP())
        {
            if(type == Timex2ValRegExprs.PyearsY)
            {
                //Years
                int x = Integer.MAX_VALUE;
                try{
                    x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("Y")));
                }catch(NumberFormatException e){}
                if(x > 1000)
                {
                    logger.warn("Cleaning expr: " + timeExpr + " using P1000Y");
                    return "P1000Y";
                }
            }
            else if(type == Timex2ValRegExprs.PmonthsM)
            {
                //Months
                int x = Integer.MAX_VALUE;
                try{
                    x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("M")));
                }catch(NumberFormatException e){}
                if(x >= 24)
                {
                    int years = x / 12;
                    logger.warn("Cleaning expr: " + timeExpr + " using normalizing to P" + years + "Y");
                    return cleanExpression(Timex2ValRegExprs.PyearsY,"P" + years + "Y");
                }
            }
            else if(type == Timex2ValRegExprs.PdaysD)
            {   //Days
                int x = Integer.MAX_VALUE;
                try{
                    x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("D")));
                }catch(NumberFormatException e){}
                if(x >= 60)
                {
                    int months = x / 30;
                    logger.warn("Cleaning expr: " + timeExpr + " using normalizing to P" + months + "M");
                    return cleanExpression(Timex2ValRegExprs.PmonthsM,"P" + months + "M");
                }
            }
            else
            {   //Weeks
                int x = Integer.MAX_VALUE;
                try{
                    x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("W")));
                }catch(NumberFormatException e){}
                if(x >= 8)
                {
                    int months = x / 4;
                    logger.warn("Cleaning expr: " + timeExpr + " using normalizing to P" + months + "M");
                    return cleanExpression(Timex2ValRegExprs.PmonthsM,"P" + months + "M");
                }
            }

        }
        return timeExpr;
    }

    private void setIntrevalLimits(List<TimeExpression> timeExpressions)
    {

        timeExpressions.get(0).setTimex2LimitType(TimeExpression.Timex2LimitType.LEFT);
        for(int i = 1; i < timeExpressions.size()-1; i++)
        {
            timeExpressions.get(i).setTimex2LimitType(TimeExpression.Timex2LimitType.INSIDE);
        }
        timeExpressions.get(timeExpressions.size()-1).setTimex2LimitType(TimeExpression.Timex2LimitType.RIGHT);
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


        boolean internalDuration = false;
        if(timeExpr.startsWith("DURATION-"))
        {
            timeExpr = timeExpr.substring("DURATION-".length());
            internalDuration = true;
        }
        List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();
        Timex2ValRegExprs type = Timex2ValRegExprs.getType(timeExpr);

        //To avoid expressions like P100000D
        timeExpr = cleanExpression(type,timeExpr);
        type = Timex2ValRegExprs.getType(timeExpr);

        if(Timex2ValRegExprs.YYYY_MM_DD_ANY == type)
        {
            TimeExpression timeExpression = new TimeExpression(timeExpr.substring(0,10).replace("-",""),timex2.getText(),teClass);
            timeExpression.setTimex2(timex2);
            timeExpressions.add(timeExpression);
        }
        else if(Timex2ValRegExprs.YYYY_MM == type)
        {
            TimeExpression timeExpression = new TimeExpression(timeExpr.substring(0,7).replace("-",""),timex2.getText(),teClass);
            timeExpression.setTimex2(timex2);
            timeExpressions.add(timeExpression);
        }
        else if(Timex2ValRegExprs.YYYY_or_YYY_or_YY_or_Y == type || Timex2ValRegExprs.YYYY == type
                || Timex2ValRegExprs.YYY == type || Timex2ValRegExprs.YY == type || Timex2ValRegExprs.Y == type)
        {
            TimeExpression timeExpression = new TimeExpression(timeExpr,timex2.getText(),teClass);
            timeExpression.setTimex2(timex2);
            timeExpressions.add(timeExpression);
        }
        //todo XXXX-AU
        //todo XXXX-SP
        //todo XXXX-SU
        //todo XXXX-WI  it depends on the point of the world

        else if(Timex2ValRegExprs.YYYY_Wweek == type)
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

            TimeExpression wanchor = null;
            if(internalDuration)
            {
                TimeExpression timeExpression = new TimeExpression(startYear,startMonth,startDay,timex2.getText(), false, TimeExpression.TEClass.Duration);
                timeExpression.setTimex2(timex2);
                timeExpression.setWeekDuration(true);
                timeExpressions.add(timeExpression); //(This is comming from PweeksW)
            }
            else
            {
                TimeExpression timeExpression = new TimeExpression(startYear,startMonth,startDay,timex2.getText(), false, teClass);
                wanchor = new TimeExpression(startYear, startMonth, startDay);
                timeExpression.setAnchor(wanchor);
                timeExpression.setTimex2LimitType(TimeExpression.Timex2LimitType.LEFT);
                timeExpression.setTimex2(timex2);
                timeExpression.setWeekDuration(true);
                timeExpressions.add(timeExpression);//cosidering the first day of the week a point
            }

            for(int i = 1; i < 7; i++)
            {
                calendar.add(Calendar.DAY_OF_YEAR,1);
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endMonth = calendar.get(Calendar.MONTH)+1;
                int endYear = calendar.get(Calendar.YEAR);
                TimeExpression timeExpression = new TimeExpression(endYear,endMonth,endDay,timex2.getText(), false, TimeExpression.TEClass.Duration);
                timeExpression.setWeekDuration(true);

                if(!internalDuration)
                    timeExpression.setAnchor(wanchor);

                if(i == 6 && !internalDuration)
                    timeExpression.setTimex2LimitType(TimeExpression.Timex2LimitType.RIGHT);
                else if(!internalDuration)
                    timeExpression.setTimex2LimitType(TimeExpression.Timex2LimitType.INSIDE);
                else
                {
                    //todo
                }
                timeExpression.setTimex2(timex2);
                timeExpressions.add(timeExpression);
            }
        }
        else if(anchor != null && anchor.trim().length() > 0 && anchorDir != null && anchorDir.trim().length() > 0)
        {

            if(Timex2ValRegExprs.PyearsY == type)
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("Y")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0 && anchorExprs.get(0).isValid())
                {
                    int year = anchorExprs.get(0).getYear();
                    if(x == 0)
                    {
                        TimeExpression timeExpression = new TimeExpression(year,timex2.getText(), false, TimeExpression.TEClass.GenPoint);
                        timeExpression.setAnchor(anchorExprs.get(0));
                        timeExpression.setTimex2(timex2);
                        timeExpressions.add(timeExpression);
                    }
                    else if(anchorDir.equalsIgnoreCase("starting"))
                    {
                        for(int i = year; i <= year+x;i++)
                        {
                            if(i >= 0)
                            {
                                TimeExpression timeExpression = new TimeExpression(i,timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setAnchor(anchorExprs.get(0));
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(timeExpression);
                            }
                        }
                        setIntrevalLimits(timeExpressions);
                    }
                    else if(anchorDir.equalsIgnoreCase("ending"))
                    {
                        for(int i = year-x; i <= year;i++)
                        {
                            if(i >= 0)
                            {
                                TimeExpression timeExpression = new TimeExpression(i,timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setAnchor(anchorExprs.get(0));
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(timeExpression);
                            }
                        }
                        setIntrevalLimits(timeExpressions);
                    }
                }
            }
            else if(Timex2ValRegExprs.PmonthsM == type)
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("M")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0 && anchorExprs.get(0).isValid())
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    if(year > 0 && month > 0)
                    {
                        if(x == 0)
                        {
                            TimeExpression timeExpression = new TimeExpression(year,month,timex2.getText(), false, TimeExpression.TEClass.GenPoint);
                            timeExpression.setAnchor(anchorExprs.get(0));
                            timeExpression.setTimex2(timex2);
                            timeExpressions.add(timeExpression);
                        }
                        else if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            TimeExpression timeExpression = new TimeExpression(year,month,timex2.getText(), false, TimeExpression.TEClass.Duration);
                            timeExpression.setAnchor(anchorExprs.get(0));
                            timeExpression.setTimex2(timex2);
                            timeExpressions.add(timeExpression);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.MONTH,1);
                                timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setAnchor(anchorExprs.get(0));
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(timeExpression);
                            }
                            setIntrevalLimits(timeExpressions);
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            TimeExpression timeExpressionEnd = new TimeExpression(year,month,timex2.getText(), false, TimeExpression.TEClass.Duration);
                            timeExpressionEnd.setAnchor(anchorExprs.get(0));
                            timeExpressionEnd.setTimex2(timex2);
                            timeExpressions.add(timeExpressionEnd);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.MONTH,-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(0,timeExpression);
                                timeExpression.setAnchor(anchorExprs.get(0));
                            }
                            setIntrevalLimits(timeExpressions);
                        }
                    }
                }
            }
            else if(Timex2ValRegExprs.PdaysD == type)
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("D")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0 && anchorExprs.get(0).isValid())
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    int day = anchorExprs.get(0).getDay();
                    if(year > 0 && month > 0 && day > 0)
                    {
                        if(x == 0)
                        {
                            TimeExpression timeExpression = new TimeExpression(year,month,day,timex2.getText(), false, TimeExpression.TEClass.Duration);
                            timeExpression.setAnchor(anchorExprs.get(0));
                            timeExpression.setTimex2(timex2);
                            timeExpressions.add(timeExpression);
                        }
                        else if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            TimeExpression timeExpression = new TimeExpression(year,month,day,timex2.getText(), false, TimeExpression.TEClass.Duration);
                            timeExpression.setAnchor(anchorExprs.get(0));
                            timeExpression.setTimex2(timex2);
                            timeExpressions.add(timeExpression);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.DAY_OF_YEAR,1);
                                timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setAnchor(anchorExprs.get(0));
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(timeExpression);
                            }
                            setIntrevalLimits(timeExpressions);
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar();
                            c.set(Calendar.YEAR,year);
                            c.set(Calendar.MONTH,month-1);
                            c.set(Calendar.DAY_OF_MONTH,day);
                            TimeExpression timeExpressionEnd = new TimeExpression(year,month,day,timex2.getText(), false, TimeExpression.TEClass.Duration);
                            timeExpressionEnd.setAnchor(anchorExprs.get(0));
                            timeExpressionEnd.setTimex2(timex2);
                            timeExpressions.add(timeExpressionEnd);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.DAY_OF_YEAR,-1);
                                TimeExpression timeExpression = new TimeExpression(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),timex2.getText(), false, TimeExpression.TEClass.Duration);
                                timeExpression.setAnchor(anchorExprs.get(0));
                                timeExpression.setTimex2(timex2);
                                timeExpressions.add(0,timeExpression);
                            }
                            setIntrevalLimits(timeExpressions);
                        }
                    }
                }
            }
            else if(Timex2ValRegExprs.PweeksW == type)
            {
                int x = Integer.parseInt(timeExpr.substring(1,timeExpr.indexOf("W")));
                List<TimeExpression> anchorExprs = getTimeExpressions(anchor,null,null);
                if(anchorExprs.size() > 0 && anchorExprs.get(0).isValid())
                {
                    int year = anchorExprs.get(0).getYear();
                    int month = anchorExprs.get(0).getMonth();
                    int day = anchorExprs.get(0).getDay();
                    if(year > 0 && month > 0 && day > 0)
                    {
                        if(anchorDir.equalsIgnoreCase("starting"))
                        {
                            GregorianCalendar c = new GregorianCalendar(year,month-1,day);
                            List<TimeExpression> wexp;
                            int week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                            if(c.get(Calendar.MONTH) == 11 && week <= 2)
                                wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)+1)) + "-W" + (week),null,null);
                            else if(c.get(Calendar.MONTH) == 0 && week >= 52)
                                wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)-1)) + "-W" + (week),null,null);
                            else
                                wexp = getTimeExpressions("DURATION-" + String.format("%04d",c.get(Calendar.YEAR)) + "-W" + (week),null,null);


                            for(TimeExpression t: wexp){t.setAnchor(anchorExprs.get(0));}
                            timeExpressions.addAll(wexp);
                            for(int i = 0; i < x;i++)
                            {
                                c.add(Calendar.WEEK_OF_YEAR,1);
                                week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                                if(c.get(Calendar.MONTH) == 11 && week <= 2)
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)+1)) + "-W" + (week),null,null);
                                else if(c.get(Calendar.MONTH) == 0 && week >= 52)
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)-1)) + "-W" + (week),null,null);
                                else
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",c.get(Calendar.YEAR)) + "-W" + (week),null,null);
                                for(TimeExpression t: wexp){t.setAnchor(anchorExprs.get(0));}
                                timeExpressions.addAll(wexp);
                            }
                            setIntrevalLimits(timeExpressions);
                        }
                        else if(anchorDir.equalsIgnoreCase("ending"))
                        {
                            GregorianCalendar c = new GregorianCalendar(year,month-1,day);
                            int week = (c.get(Calendar.WEEK_OF_YEAR)+1);

                            List<TimeExpression> wexpLast;
                            if(c.get(Calendar.MONTH) == 11 && week <= 2)
                                wexpLast = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)+1)) + "-W" + (week),null,null);
                            else if(c.get(Calendar.MONTH) == 0 && week >= 52)
                                wexpLast = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)-1)) + "-W" + (week),null,null);
                            else
                                wexpLast = getTimeExpressions("DURATION-" + String.format("%04d",c.get(Calendar.YEAR)) + "-W" + (week),null,null);

                            for(TimeExpression t: wexpLast){t.setAnchor(anchorExprs.get(0));}
                            for(int i = 0; i < x;i++)
                            {

                                c.add(Calendar.WEEK_OF_YEAR,-1);
                                week = (c.get(Calendar.WEEK_OF_YEAR)+1);
                                List<TimeExpression> wexp;
                                if(c.get(Calendar.MONTH) == 11 && week <= 2)
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)+1)) + "-W" + (week),null,null);
                                else if(c.get(Calendar.MONTH) == 0 && week >= 52)
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",(c.get(Calendar.YEAR)-1)) + "-W" + (week),null,null);
                                else
                                    wexp = getTimeExpressions("DURATION-" + String.format("%04d",c.get(Calendar.YEAR)) + "-W" + week,null,null);
                                for(TimeExpression t: wexp){t.setAnchor(anchorExprs.get(0));}
                                timeExpressions.addAll(0,wexp);
                            }
                            timeExpressions.addAll(wexpLast);
                            setIntrevalLimits(timeExpressions);
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

    public int getStartOffset() {
        return timex2.getRstart();
    }

    public int getEndOffset() {
        return timex2.getRend();
    }

    public static void main(String[] args) throws TimeExpression.BadTimeExpression {

        GregorianCalendar g = new GregorianCalendar(2001,11,24);
        g.add(Calendar.WEEK_OF_YEAR,1);

        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));

        g.set(Calendar.WEEK_OF_YEAR,54);

        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));

        g.set(Calendar.WEEK_OF_YEAR,55);

        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));

        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));

        g.add(Calendar.WEEK_OF_YEAR,1);

        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));

        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
        g.add(Calendar.DAY_OF_YEAR,1);
        System.out.println(g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/" + g.get(Calendar.YEAR));
    }
}
