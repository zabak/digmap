package pt.utl.ist.lucene.utils;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * @author Jorge Machado
 * @date 2005
 */
public class MyCalendar extends java.util.GregorianCalendar implements java.io.Serializable
{
    SimpleDateFormat webTextDate = new SimpleDateFormat("yyyy/MM/dd");
    public static final String simpleDateTimeFormat = "dd-MM-yyyy HH-mm-ss";
    public static final String TimestampFormat = "yyyyMMddHHmmss";
    public static final String GmtTimezone = "GMT";

    public MyCalendar()
    {
        super();
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the default locale.
     * @param zone the given time zone.
     */
    public MyCalendar(TimeZone zone)
    {
        super(zone);
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the default time zone with the given locale.
     * @param aLocale the given locale.
     */
    public MyCalendar(Locale aLocale)
    {
        super(aLocale);
    }

    /**
     * Constructs a GregorianCalendar based on the current time
     * in the given time zone with the given locale.
     * @param zone the given time zone.
     * @param aLocale the given locale.
     */
    public MyCalendar(TimeZone zone, Locale aLocale)
    {
        super(zone, aLocale);
    }

    /**
     * Constructs a GregorianCalendar with the given date set
     * in the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the DATE time field in the calendar.
     */
    public MyCalendar(int year, int month, int day)
    {
        super(year, month - 1, day);
    }

    /**
     * Constructs a GregorianCalendar with the given date
     * and time set for the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the DATE time field in the calendar.
     * @param hour the value used to set the HOUR_OF_DAY time field
     * in the calendar.
     * @param minute the value used to set the MINUTE time field
     * in the calendar.
     */
    public MyCalendar(int year, int month, int day, int hour,
                      int minute)
    {
        super(year, month - 1, day, hour, minute);
    }

    /**
     * Constructs a GregorianCalendar with the given date
     * and time set for the default time zone with the default locale.
     * @param year the value used to set the YEAR time field in the calendar.
     * @param month the value used to set the MONTH time field in the calendar.
     * Month value is 0-based. e.g., 0 for January.
     * @param day the value used to set the DATE time field in the calendar.
     * @param hour the value used to set the HOUR_OF_DAY time field
     * in the calendar.
     * @param minute the value used to set the MINUTE time field
     * in the calendar.
     * @param second the value used to set the SECOND time field
     * in the calendar.
     */
    public MyCalendar(int year, int month, int day, int hour,
                      int minute, int second)
    {
        super(year, month - 1, day, hour, minute, second);
    }

    public int getDay()
    {
        return get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth()
    {
        return get(Calendar.MONTH) + 1;
    }

    public int getYear()
    {
        return get(Calendar.YEAR);
    }

    public int getHour()
    {
        return get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute()
    {
        return get(Calendar.MINUTE);
    }

    public int getSecond()
    {
        return get(Calendar.SECOND);
    }

    public void setDay(int value)
    {
        set(Calendar.DAY_OF_MONTH, value);
    }

    public void setMonth(int value)
    {
        set(Calendar.MONTH, value - 1);
    }

    public void setYear(int value)
    {
        set(Calendar.YEAR, value);
    }

    public void setHour(int value)
    {
        set(Calendar.HOUR_OF_DAY, value);
    }

    public void setMinute(int value)
    {
        set(Calendar.MINUTE, value);
    }

    public void setSecond(int value)
    {
        set(Calendar.SECOND, value);
    }

	public String getDateString(Locale locale)
	{
		/*
		 int[] formats =
			{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
		*/
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
//		DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy", locale); Can't be this way because of i18n
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

    public String getFullDateString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
    }

	public String getDateTimeString(Locale locale)
	{
		/*
		 int[] formats =
			{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
		*/
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

    public String getTimestamp()
    {
        return MyCalendar.getTimestamp(getTime());
    }

    public static String getTimestamp(Date date)
	{
		DateFormat formatter = new SimpleDateFormat(MyCalendar.TimestampFormat);
        /**
         */
//        formatter.setTimeZone(TimeZone.getTimeZone(GmtTimezone));
        String ts = formatter.format(date);
        return ts;
	}

	public void setTimestamp(String dateString) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(MyCalendar.TimestampFormat);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        Date _date = formatter.parse(dateString);
        setTime(_date);
	}

	public String getSimpleDateTimeString(Locale locale)
	{
		/*
		 int[] formats =
			{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
		*/
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

	public String getSimpleDateTimeString2()
	{
		/*
		 int[] formats =
			{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
		*/
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, new Locale("asdasdasd"));
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

	public String getSimpleDateTime()
	{
		DateFormat formatter = new SimpleDateFormat(MyCalendar.simpleDateTimeFormat);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

	public void setSimpleDateTime(String date) throws ParseException
    {
		DateFormat formatter = new SimpleDateFormat(MyCalendar.simpleDateTimeFormat);
        formatter.setTimeZone(TimeZone.getDefault());
        Date _date = formatter.parse(date);
        setTime(_date);
	}

	public String getXMLDateTimeString()
	{
		/*
		 int[] formats =
			{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
		*/
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
	}

    public String getFullDateTimeString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
    }

    public String getTimeString(Locale locale)
    {
        /* int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
         */

        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(getTime());
    }

    public String getGMTDateString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        return formatter.format(getTime());
    }

    public String getGMTFullDateString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        return formatter.format(getTime());
    }
    public String getWebDateString()
    {
        return webTextDate.format(getTime());
    }

    public String getGMTDateTimeString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        return formatter.format(getTime());
    }

    public String getGMTFullDateTimeString(Locale locale)
    {
        /*
         int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        */
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        return formatter.format(getTime());
    }

    public String getGMTTimeString(Locale locale)
    {
/*        int[] formats =
            {DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
  */

        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        formatter.setTimeZone(TimeZone.getTimeZone(MyCalendar.GmtTimezone));
        return formatter.format(getTime());
    }


//    public int compareTo(Calendar obj)
//    {
//        if (obj instanceof MyCalendar || obj instanceof GregorianCalendar)
//        {
//            if (this.after(obj))
//            {
//                return 1;
//            }
//            else if(this.before(obj))
//            {
//                return -1;
//            }
//
//            return 0;
//        }
//
//        throw new ClassCastException("MyCalendar.compareTo: object is not of type GregorianCalendar: " + obj);
//    }

    public List getWeeks()
    {
        List weeks = new ArrayList();
        Object week[] = new Object[7];
        MyCalendar calendar = new MyCalendar();
        calendar.setTime(getTime());

        int firstDayOfWeek = MyCalendar.MONDAY;
        calendar.setFirstDayOfWeek(firstDayOfWeek);

        calendar.set(MyCalendar.DAY_OF_MONTH, 1);
        int month = calendar.getMonth();
        int currentWeek = calendar.get(MyCalendar.WEEK_OF_MONTH);
        do
        {
            if (calendar.getMonth() != month)
            {
                weeks.add(week);
                break;
            }

            if (calendar.get(MyCalendar.WEEK_OF_MONTH) != currentWeek)
            {
                weeks.add(week);
                currentWeek++;
                week = new Object[7];
            }
            int weekDay = calendar.get(MyCalendar.DAY_OF_WEEK);
            int pos = (weekDay - firstDayOfWeek) % (7);
            if (pos < 0) pos += 7;
            week[pos] = ("" + calendar.getDay());
            calendar.add(MyCalendar.DAY_OF_YEAR, 1);
        }
        while (true);

        return weeks;
    }

    public boolean isSameDay(MyCalendar calendar)
    {
        return (getDay() == calendar.getDay() &&
                getMonth() == calendar.getMonth() &&
                getYear() == calendar.getYear());
    }

    public boolean isWeekday()
    {
        return (!isWeekday());
    }

    public boolean isWeekendDay()
    {
        int weekday = get(MyCalendar.DAY_OF_WEEK);
        return (weekday == MyCalendar.SATURDAY || weekday == MyCalendar.SUNDAY);
    }

    /**
     * getMinuteDifference
     *
     * Returns the minute difference between this calendar and time2: hours(calendar - time2)
     */
    public int getMinuteDifference(Calendar time2)
    {
        long millisDifference = getTimeInMillis() - time2.getTimeInMillis();
        long millisInOneHour = 60 * 1000;

        return (int)Math.round(millisDifference / millisInOneHour);
    }



}
