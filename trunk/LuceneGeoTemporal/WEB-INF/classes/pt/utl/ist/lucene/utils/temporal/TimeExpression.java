package pt.utl.ist.lucene.utils.temporal;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * @author Jorge Machado
 * @date 11/Dez/2009
 * @time 11:02:56
 * @email machadofisher@gmail.com
 */
public class TimeExpression
{

    protected String refNLTxt;
    protected String normalizedExpression;
    int year = -1;
    int month = -1;
    int day = -1;
    Type type;
    GregorianCalendar c;

    GregorianCalendar leftLimit;
    GregorianCalendar rightLimit;



    protected TimeExpression()
    {
        normalizedExpression = "UNKNOWN";
    }

    protected void setRefNLTxt(String refNLTxt)
    {
        this.refNLTxt = refNLTxt;
    }

    public TimeExpression(int year) throws BadTimeExpression
    {
        this(year,-1,-1,true);
        if(year < 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }

    public TimeExpression(int year, int month) throws BadTimeExpression
    {
        this(year,month,-1,true);
        if(year < 0 || month <= 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }

    public TimeExpression(int year, int month, int day) throws BadTimeExpression {
        this(year,month,day,true);
        if(year < 0 || month <= 0 || day <= 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }

    public TimeExpression(int year, int month, int day, String refTxt) throws BadTimeExpression {
        this(year,month,day);
        this.refNLTxt = refTxt;
    }
    public TimeExpression(int year, int month, String refTxt) throws BadTimeExpression {
        this(year,month);
        this.refNLTxt = refTxt;
    }
    public TimeExpression(int year, String refTxt) throws BadTimeExpression {
        this(year);
        this.refNLTxt = refTxt;
    }


    private TimeExpression(int year, int month, int day, boolean internal) throws BadTimeExpression
    {
        this.year = year;
        this.month = month;
        this.day = day;


        if(day > 0)
            type = Type.YYYYMMDD;
        else if(month > 0)
            type = Type.YYYYMM;
        else
            type = Type.YYYY;

        validate();

        normalizedExpression = String.format("%04d",year);
        normalizedExpression += month > 0 ? String.format("%02d",month):"";
        normalizedExpression += day > 0 ? String.format("%02d",day):"";
    }

    public int getNumberOfDays()
    {
        if(type == Type.YYYYMMDD)
            return 1;
        else if(type == Type.YYYY)
            return c.getActualMaximum(Calendar.DAY_OF_YEAR);
        else
            return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    public String getRefNLTxt() {
        return refNLTxt;
    }

    /**
     * Expression of Type "YYYYMMDD"
     *
     * @param normalizedExpression YYYYMMDD
     * @throws BadTimeExpression
     */
    public TimeExpression(String normalizedExpression) throws BadTimeExpression
    {
        this.normalizedExpression = normalizedExpression;
        try{
            switch(normalizedExpression.length()){
                case 1:
                    type = Type.Y;
                    year = Integer.parseInt(normalizedExpression)*1000;
                    break;
                case 2:
                    type = Type.YY;
                    year = Integer.parseInt(normalizedExpression)*100;
                    break;
                case 3:
                    type = Type.YYY;
                    year = Integer.parseInt(normalizedExpression)*10;
                    break;
                case 4:
                    type = Type.YYYY;
                    year = Integer.parseInt(normalizedExpression);
                    break;
                case 6:
                    type = Type.YYYYMM;
                    year = Integer.parseInt(normalizedExpression.substring(0,4));
                    month = Integer.parseInt(normalizedExpression.substring(4));
                    break;
                case 8:
                    type = Type.YYYYMMDD;
                    year = Integer.parseInt(normalizedExpression.substring(0,4));
                    month = Integer.parseInt(normalizedExpression.substring(4,6));
                    day = Integer.parseInt(normalizedExpression.substring(6));
                    break;
                default: throw new BadTimeExpression("wrong number of chars: " + normalizedExpression.length() + " could be only 4(YYYY), 6(YYYYMM) or 8(YYYYMMDD)");
            }
        }
        catch (NumberFormatException e)
        {
            throw new BadTimeExpression("TimeExpression must be only numbers, came:" + normalizedExpression + " could be only 4(YYYY), 6(YYYYMM) or 8(YYYYMMDD) where Y, M and D must be integers between 0 and 9");
        }
        validate();
    }

    /**
     *
     * @param normalizedExpression YYYYMMDD
     * @param refTxt natural language text from the timex2
     * @throws BadTimeExpression when text is not valid in grammar YYYYMMDD
     */
    public TimeExpression(String normalizedExpression, String refTxt) throws BadTimeExpression
    {
        this(normalizedExpression);
        this.refNLTxt = refTxt;
    }




    public String getNormalizedExpression() {
        return normalizedExpression;
    }

    public String getSubExpressionYYYY()
    {
        if(Type.Y == type || Type.YY == type || Type.YYY == type )
            return normalizedExpression;
        return normalizedExpression.substring(0,4);
    }

    public String getSubExpressionYYYYMM()
    {
        if(Type.Y == type || Type.YY == type || Type.YYY == type )
            return normalizedExpression;
        return normalizedExpression.substring(0,6);
    }

    public String getSubExpressionYYYYMMDD()
    {
        if(Type.Y == type || Type.YY == type || Type.YYY == type )
            return normalizedExpression;
        return normalizedExpression.substring(0,8);
    }

    public Type getType() {
        return type;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getYearStr() {
        return  String.format("%04d",year);
    }

    public String getMonthStr() {
        return  String.format("%02d",month);
    }

    public String getDayStr() {
        return  String.format("%02d",day);
    }

    public GregorianCalendar getC() {
        return c;
    }


    public GregorianCalendar getLeftLimit() {
        return leftLimit;
    }

    public GregorianCalendar getRightLimit() {
        return rightLimit;
    }

    private void validate() throws BadTimeExpression {
        if(year < 0)
            throw new BadTimeExpression("Bad Year could be only 4(YYYY)");
        if(type != Type.YYYY && type != Type.YYY && type != Type.YY && type != Type.Y)
        {
            if(month < 1 || month > 12)
                throw new BadTimeExpression("Bad Month must be between 1 and 12");
            if(type == Type.YYYYMMDD)
            {
                if((month == 6 || month == 11 || month == 4 || month == 9)  && (day < 1 || day > 30))
                    throw new BadTimeExpression("Bad Day. For month " + month + ", day must be between 1 and 30");
                else if(month == 2 && (day < 1 || day > 29))
                    throw new BadTimeExpression("Bad Day. For month 2, day must be between 1 and 29");
                else if(day < 1 || day > 31)
                    throw new BadTimeExpression("Bad Day. For month " + month + ", day must be between 1 and 31");
            }
        }
        int month = this.month <= 0 ? 1: this.month;
        int day = this.day <= 0 ? 1: this.day;
        c = new GregorianCalendar(year,month-1,day);
        leftLimit = c;


        if(type == Type.YYYY)
            rightLimit = new GregorianCalendar(year+1,0,1);
        else if(type == Type.YYYYMM)
        {
            if(this.month == 12)
                rightLimit = new GregorianCalendar(year+1,0,1);
            else
                rightLimit = new GregorianCalendar(year,month,1);
        }
        else
        {
            if(this.day == c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH))
            {
                if(this.month == 12)
                    rightLimit = new GregorianCalendar(year+1,0,1);
                else
                    rightLimit = new GregorianCalendar(year,month,1);
            }
            else
            {
                rightLimit = new GregorianCalendar(year,month-1,day+1);
            }
        }

    }

    public class BadTimeExpression extends Exception
    {
        public BadTimeExpression() {
        }

        public BadTimeExpression(String message) {
            super(message);
        }

        public BadTimeExpression(String message, Throwable cause) {
            super(message, cause);
        }

        public BadTimeExpression(Throwable cause) {
            super(cause);
        }
    }

    public static enum Type
    {
        Y("Y"),
        YY("YY"),
        YYY("YYY"),
        YYYY("YYYY"),
        YYYYMM("YYYYMM"),
        YYYYMMDD("YYYYMMDD");

        String type;


        Type(String type) {
            this.type = type;
        }

        public String toString(){
            return type;
        }
    }

    public String toString()
    {
        return toString(true);
    }

    public String toString(boolean withRefNLtxt)
    {
        String refTxt;
        if(withRefNLtxt)
            refTxt = " : (" + this.refNLTxt + ")";
        else
            refTxt = "";
        return type.toString() + ":" + normalizedExpression + refTxt;
    }
}
