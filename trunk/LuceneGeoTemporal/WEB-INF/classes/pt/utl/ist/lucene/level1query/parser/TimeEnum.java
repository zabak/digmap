package pt.utl.ist.lucene.level1query.parser;

import pt.utl.ist.lucene.Globals;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 3:04:45
 * @see 
 */
public enum TimeEnum
{
    TIME(Globals.LUCENE_TIME_FIELD_QUERY),
    START_TIME(Globals.LUCENE_STARTTIME_FIELD_QUERY),
    END_TIME(Globals.LUCENE_ENDTIME_FIELD_QUERY),
    TIME_MILISECONDS(Globals.LUCENE_TIME_MILISECONDS_FIELD_QUERY),
    START_TIME_MILISECONDS(Globals.LUCENE_STARTTIME_MILISECONDS_FIELD_QUERY),
    END_TIME_MILISECONDS(Globals.LUCENE_ENDTIME_MILISECONDS_FIELD_QUERY),
    RADIUM_YEARS(Globals.LUCENE_RADIUMTIME_YEARS_FIELD_QUERY),
    RADIUM_MONTHS(Globals.LUCENE_RADIUMTIME_MONTHS_FIELD_QUERY),
    RADIUM_DAYS(Globals.LUCENE_RADIUMTIME_DAYS_FIELD_QUERY),
    RADIUM_HOURS(Globals.LUCENE_RADIUMTIME_HOURS_FIELD_QUERY),
    RADIUM_MINUTES(Globals.LUCENE_RADIUMTIME_MINUTES_FIELD_QUERY),
    RADIUM_SECONDS(Globals.LUCENE_RADIUMTIME_SECONDS_FIELD_QUERY),
    RADIUM_MILISECONDS(Globals.LUCENE_RADIUMTIME_MILISECONDS_FIELD_QUERY),

    EXTRA_TIME("***");


    private String name;

    private TimeEnum(String name)
    {
        this.name = name;
    }

    public static TimeEnum parse(String name)
    {
        if(name == null)
            return null;
        for(TimeEnum orderEnum: TimeEnum.values())
        {
            if(orderEnum.name.equals(name))
                return orderEnum;
        }
        if(name.startsWith(TIME.getName()))
            return EXTRA_TIME;
        return null;
    }


    public String getName()
    {
        return name;
    }
}
