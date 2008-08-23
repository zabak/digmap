package pt.utl.ist.lucene.utils;

import org.apache.solr.util.NumberUtils;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class QueryUtils
{
    /**
     * Build a range level1query given a date and a Radium in years
     * @param query to range
     * @param field to range
     * @param time to build interval
     * @param radium to build interval
     * @return ranged level1query like e.g. time:[1223213123123 TO 1231231231231]
     */
    public static String buildFieldRangeQueryRadium(String query, String field, String time, int radium)
    {
        GregorianCalendar c = Dates.getGregorianCalendar(time);

        int topYear = c.get(Calendar.YEAR) + radium;
        int lowYear = c.get(Calendar.YEAR) - radium;

        MyCalendar cmtop = new MyCalendar(topYear,12,31);
        MyCalendar cmlow = new MyCalendar(lowYear,1,1);

        String endDate = NumberUtils.long2sortableStr(cmtop.getTime().getTime());
        String startDate = NumberUtils.long2sortableStr(cmlow.getTime().getTime());

        return buildFieldRangeQuery(query,field,startDate,endDate);
    }

    public static String buildFieldRangeQuery(String query, String field, String startDate, String endDate)
    {
        if(query != null && query.trim().length() > 0)
            return buildFieldRangeQuery(field, startDate, endDate) + " AND (" + query + ")";
        else
            return buildFieldRangeQuery(field, startDate, endDate);
    }

    /**
     * build a generic range date level1query
     *
     * @param field     to searchCallback
     * @param startDate to interval
     * @param endDate   to interval
     * @return a level1query ranged e.g. time:[1223213123123 TO 1231231231231]
     */
    public static String buildFieldRangeQuery(String field, String startDate, String endDate)
    {
        if ((startDate != null && startDate.length() > 0) || (endDate != null && endDate.length() > 0))
        {
            StringBuilder fieldRangeQuery = new StringBuilder();
            String rangeQuery = buildRange(startDate, endDate);
            fieldRangeQuery.append(field).append(":").append(rangeQuery);
            return fieldRangeQuery.toString();
        }
        return null;
    }

    /**
     * return an interval of two longs corresponding to miliseconds
     * example [1231233 TO 12342344]
     *
     * @param start date
     * @param end   date
     * @return String
     */
    private static String buildRange(String start, String end)
    {
        return "[" + start + " TO " + end + "]";
    }
}
