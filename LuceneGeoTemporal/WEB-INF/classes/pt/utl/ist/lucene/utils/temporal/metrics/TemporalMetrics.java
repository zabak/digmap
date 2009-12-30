package pt.utl.ist.lucene.utils.temporal.metrics;

import pt.utl.ist.lucene.utils.temporal.TimeExpression;

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

    public TimeExpression getTemporalIntervalPointsCentroideTimeExpression() throws TimeExpression.BadTimeExpression
    {
        Date d = getTemporalIntervalPointsCentroide();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return new TimeExpression(df.format(d));
    }

}
