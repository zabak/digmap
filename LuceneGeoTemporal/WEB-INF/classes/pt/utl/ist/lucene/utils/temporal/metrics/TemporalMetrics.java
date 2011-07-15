package pt.utl.ist.lucene.utils.temporal.metrics;

import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 18:12:24
 * @email machadofisher@gmail.com
 */
public class TemporalMetrics
{

    List<TimeExpression> expressions;
    TimeExpressionsTree tree;

    /**
     * @param expressions temporal expressions must be of the type YYYYMMDD or YYYYMM or YYYY
     * @throws TimeExpression.BadTimeExpression on bad expression format
     */
    public TemporalMetrics(String[] expressions) throws TimeExpression.BadTimeExpression
    {
        init(expressions);
    }

    /**
     * @param expressions temporal expressions must be of the type YYYYMMDD or YYYYMM or YYYY
     * @throws TimeExpression.BadTimeExpression on bad expression format
     */
    public TemporalMetrics(List<TimeExpression> expressions) throws TimeExpression.BadTimeExpression
    {
        String[] expressionsStr = new String[expressions.size()];
        for(int i = 0; i < expressions.size();i++)
        {
            expressionsStr[i] = expressions.get(i).getNormalizedExpression();
        }
        init(expressionsStr);
    }

    public TemporalMetrics(String expressions) throws TimeExpression.BadTimeExpression
    {
        String[] exprs = expressions.split(" ");
        init(exprs);
    }

    private void init(String[] expressions) throws TimeExpression.BadTimeExpression {
        tree = new TimeExpressionsTree();
        this.expressions = new ArrayList<TimeExpression>();
        for(String expr: expressions)
        {
            TimeExpression timexpr = new TimeExpression(expr);
            this.expressions.add(timexpr);
            tree.addExpression(timexpr);
        }
    }

    /**
     * Example using the terms 1990  199001 199001 199002  19900101
     *
     *
     *      |
     *      |*| <-- 4 refs (19900101)
     * Refs |************| <-- 3 refs (199001 30 - 1 days)
     *      |************|************|  <-- 2 refs (199002) (28 days)
     *      |********************************************************| <-- 1 ref (1990)  (365days - 28 - 31)
     *      |-| <-19900101
     *      |---199001---|---199002---|
     *      |-----------------------1990-----------------------------|
     *
     *      Centroide in Refs Count in each day of year is given by
     *
     *      (4*1 + 3*30 + 2*28 + 1*(365-30-28)) / 365 =~ 1.249315
     *
     *
     * @return the vertical centroide
     */
    double centroideRefs = -1;
    public double getNumberRefsCentroide()
    {
        if(centroideRefs >= 0)
            return centroideRefs;
        double total = 0;
        double points = 0;
        for(TimeExpressionsTree.TimeExpressionNode year: tree.getRootNodes())
        {
            boolean yearRef = false;
            if(year.getRefs() > 0)
            {
                yearRef = true;
                total += (year.getTimeExpression().getNumberOfDays()*year.getRefs());
                points += year.getTimeExpression().getNumberOfDays();
            }
            for(TimeExpressionsTree.TimeExpressionNode month: year.getChilds())
            {
                boolean monthRef = false;
                if(month.getRefs() > 0)
                {
                    monthRef = true;
                    total += (month.getTimeExpression().getNumberOfDays()*(year.getRefs() + month.getRefs()));
                    if(!yearRef && monthRef)
                        points += month.getTimeExpression().getNumberOfDays();
                    if(yearRef)
                        total -= (month.getTimeExpression().getNumberOfDays()*year.getRefs());
                }
                for(TimeExpressionsTree.TimeExpressionNode day: month.getChilds())
                {
                    if(day.getRefs() > 0)
                    {
                        total += (year.getRefs() + month.getRefs() + day.getRefs());
                        if(!yearRef && !monthRef)
                            points += 1;
                        if(monthRef)
                            total -= (year.getRefs() + month.getRefs());
                    }
                }
            }
        }
        centroideRefs = total / points;
        return centroideRefs;
    }

    Date temporalCentroide = null;
    /**
     *
     */
    static final long halfYearMiliseconds = (long) ((1000d*60d*60d*24d*365d)/2d);
    static final long halfMonthMiliseconds = (long) (1000d*60d*60d*24d*15d);

    /**
     * This formula uses granularity of days to calculate the centroide
     * For example for a year all the 365 days will be used to calculate the avarege point
     * In the set of expressions 1990 1990-1-1  the last one will contribute to the final average
     * with 1/366 factor
     *
     * centroide = (2*1990-1-1 + 1990-1-2 + 1990-1-3 .... + 1990-12-31) / 366
     *
     * @return the centroide
     */
    public Date getTemporalCentroide()
    {
        if(temporalCentroide != null)
            return temporalCentroide;

        double total = 0;
        double points = 0;
        for(TimeExpression expression : expressions)
        {
            if(expression.getType() == TimeExpression.Type.YYYY)
            {
                double localCentroide = expression.getC().getTimeInMillis() + halfYearMiliseconds;
                total += localCentroide * expression.getNumberOfDays();
                points += expression.getNumberOfDays();
            }
            else if(expression.getType() == TimeExpression.Type.YYYYMM)
            {
                double localCentroide = expression.getC().getTimeInMillis() + halfMonthMiliseconds;
                total += localCentroide * expression.getNumberOfDays();
                points += expression.getNumberOfDays();
            }
            else if(expression.getType() == TimeExpression.Type.YYYYMMDD)
            {
                double localCentroide = expression.getC().getTimeInMillis();
                total += localCentroide;
                points += 1;
            }
        }
        temporalCentroide = new Date((long)(total / points));
        return temporalCentroide;
    }


    public TimeExpression getTemporalCentroideTimeExpression() throws TimeExpression.BadTimeExpression
    {
        Date d = getTemporalCentroide();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return new TimeExpression(df.format(d));
    }



    Date temporalIntervalPointsCentroide = null;

    /**
     * With this formula the centroide will e calculated as the average of the limits of all timeexpressions
     * For example in the set 1990-1-1 and 1990 1990-1-1 will contribute with 2/4 parts and the same is true for 1990
     *
     *  (1990-1-1 + 1990-1-1   +   1990-1-1 + 1990-12-31) / 4
     *
     *  Example using the terms 1990  199001 199001 199002  19900101
     *
     * The formula Centroide = [For each Expression e in exprs  Sum ( e.leftLimit + e.rightLimit )] / 2*exprs.size 
     * @return
     */
    public Date getTemporalIntervalPointsCentroide()
    {
        if(temporalIntervalPointsCentroide != null)
            return temporalIntervalPointsCentroide;

        double total = 0;
        double points = 0;
        for(TimeExpression expression : expressions)
        {
            total += expression.getLeftLimit().getTimeInMillis() + expression.getRightLimit().getTimeInMillis();
            points += 2;
        }
        temporalIntervalPointsCentroide = new Date((long)(total / points));
        return temporalIntervalPointsCentroide;
    }

    /**
     * The same as the getTemporalIntervalPointsCentroide but days only contribute with 1 part and not with 2 like the intervals
     * Example 1990-1-1 and 1990 will be (1990-1-1 + 1990-1-1 + 1990-12-31) / 3
     * @return
     */
    public Date getTemporalIntervalPointsCentroide2()
    {
        if(temporalIntervalPointsCentroide != null)
            return temporalIntervalPointsCentroide;

        double total = 0;
        double points = 0;
        for(TimeExpression expression : expressions)
        {
            if(expression.getType() == TimeExpression.Type.YYYYMMDD)
            {
                total += expression.getLeftLimit().getTimeInMillis();
                points += 1;
            }
            else
            {
                total += expression.getLeftLimit().getTimeInMillis() + expression.getRightLimit().getTimeInMillis();
                points += 2;
            }
        }
        temporalIntervalPointsCentroide = new Date((long)(total / points));
        return temporalIntervalPointsCentroide;
    }

    public TimeExpression getIntervalCentroideTimeExpression() throws TimeExpression.BadTimeExpression
    {
        Date d = getTemporalIntervalPointsCentroide();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return new TimeExpression(df.format(d));
    }


    /**
     * With this formula the centroide will e calculated as the average of the left points off all timeexpressions
     * For example in the set 1990-1-1 and 1990 1990-1-1 will contribute with 1/2 parts and the same is true for 1990
     *
     *  left limit for 1990 = 1990-1-1
     *  left limit for 1990-1-1 = 1990-1-1
     *  (1990-1-1 + 1990-1-1) / 2
     *
     * @return a temporal centroide
     */
    public Date getTemporalPointsCentroide()
    {
        if(temporalIntervalPointsCentroide != null)
            return temporalIntervalPointsCentroide;

        double total = 0;
        double points = 0;
        for(TimeExpression expression : expressions)
        {
            total += expression.getLeftLimit().getTimeInMillis();
            points ++;
        }
        temporalIntervalPointsCentroide = new Date((long)(total / points));
        return temporalIntervalPointsCentroide;
    }

    public TimeExpression getLeftLimitsCentroideTimeExpression() throws TimeExpression.BadTimeExpression
    {
        Date d = getTemporalPointsCentroide();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return new TimeExpression(df.format(d));
    }


    /**
     *
     * @return A date in miliseconds
     */
    public long getMedia()
    {
        long sum = 0;
        for(TimeExpression te : expressions)
        {
            sum += te.getC().getTimeInMillis();
        }
        return sum / expressions.size();
    }

    public long getDesvioPadrao()
    {
        if(expressions.size() < 2)
            return 0;
        long media = getMedia();
        double sum = 0;
        for(TimeExpression te : expressions)
        {

            sum += ((double)((double)media - (double)te.getC().getTimeInMillis())*((double)media - (double)te.getC().getTimeInMillis())/(double)(expressions.size()-1));
        }

        return (long) Math.sqrt(sum);
    }

    public GregorianCalendar[] getNormalizedDesvioPadraoStartEndDatesCalendar()
    {
        Long[] startEnd = getNormalizedDesvioPadraoStartEndDatesLongs();
        if(startEnd ==null)
            return null;
        long start = startEnd[0];
        long end = startEnd[1];
        GregorianCalendar gcStart = new GregorianCalendar();
        gcStart.setTimeInMillis(start);

        GregorianCalendar gcEnd = new GregorianCalendar();
        gcEnd.setTimeInMillis(end);

        return new GregorianCalendar[]{gcStart,gcEnd};
    }
    public String[] getNormalizedDesvioPadraoStartEndDates()
    {

        GregorianCalendar[] startEnd = getNormalizedDesvioPadraoStartEndDatesCalendar();
        if(startEnd ==null)
            return null;

        GregorianCalendar gcStart = startEnd[0];
        GregorianCalendar gcEnd = startEnd[1];

        try {

            TimeExpression teStart = new TimeExpression(gcStart.get(GregorianCalendar.YEAR),1+gcStart.get(GregorianCalendar.MONTH),gcStart.get(GregorianCalendar.DAY_OF_MONTH));
            TimeExpression teEnd = new TimeExpression(gcEnd.get(GregorianCalendar.YEAR),1+gcEnd.get(GregorianCalendar.MONTH),gcEnd.get(GregorianCalendar.DAY_OF_MONTH));

            String[] interval = new String[]{teStart.getNormalizedExpression(),teEnd.getNormalizedExpression()};
            return interval;
        } catch (TimeExpression.BadTimeExpression badTimeExpression) {
            badTimeExpression.printStackTrace();
        }
        return null;
    }

    public Long[] getNormalizedDesvioPadraoStartEndDatesLongs()
    {
        if(expressions.size() < 1)
            return null;
        double months6 = 60.0 * 1000.0 * 60.0 * 24.0 * 30.0 * 6.0;
        if(expressions.size() < 1)
            return null;
        long start;
        long end;
        if(expressions.size() < 2)
        {
            start  = expressions.get(0).getC().getTimeInMillis();
            end  = expressions.get(0).getC().getTimeInMillis();
        }
        else
        {
            long media = getMedia();
            long desvio = getDesvioPadrao();
            start = media - desvio;
            end = media + desvio;
        }

        if(start == end)
        {

            start -= months6;//6 months = 1minute*60*24*30*6
            end += months6;//6 months = 1minute*60*24*30*6
        }

        return new Long[]{start,end};
    }


    public static void main(String[] args) throws TimeExpression.BadTimeExpression {
//        TemporalMetrics te = new TemporalMetrics(new String[]{"19960101","20020101","20040101","20050101","19980102","20050103","20100101","20400105","21000606"});
        TemporalMetrics te = new TemporalMetrics(new String[]{"19960101"});
        System.out.println("[" + te.getNormalizedDesvioPadraoStartEndDates()[0] + ";" + te.getNormalizedDesvioPadraoStartEndDates()[1] + "]");
    }
}
