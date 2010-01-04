package pt.utl.ist.lucene.utils.temporal;

import org.apache.log4j.Logger;

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

    private static final Logger logger = Logger.getLogger(TimeExpression.class);
    protected String refNLTxt;
    protected String normalizedExpression;
    int year = -1;
    int month = -1;
    int day = -1;
    Type type = Type.UNKNOWN;
    GregorianCalendar c;

    int auxCount = 1;

    public void incCount()
    {
        auxCount++;
    }

    /**
     * Not filled by default
     * @return
     */
    public int getCount() {
        return auxCount;
    }

    GregorianCalendar leftLimit;
    GregorianCalendar rightLimit;


    boolean valid = true;
    String validationError = null;

    TEClass teClass = TEClass.Point; //by default

    protected TimeExpression()
    {
        normalizedExpression = "UNKNOWN";
        teClass = TEClass.UNKNOWN;
        type = Type.UNKNOWN;
    }

    protected void setRefNLTxt(String refNLTxt)
    {
        this.refNLTxt = refNLTxt;
    }

    public TimeExpression(int year) throws BadTimeExpression
    {
        this(year,true);
    }
    public TimeExpression(int year, boolean exceptionOnValidate) throws BadTimeExpression
    {
        init(year,-1,-1,exceptionOnValidate);
        if(year < 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }

    public TimeExpression(int year, int month) throws BadTimeExpression
    {
        this(year,month,true);
    }
    public TimeExpression(int year, int month, boolean exceptionOnValidate) throws BadTimeExpression
    {
        init(year,month,-1,exceptionOnValidate);
        if(year < 0 || month <= 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }

    public TimeExpression(int year, int month, int day) throws BadTimeExpression
    {
        this(year,month,day,true);
    }
    public TimeExpression(int year, int month, int day, boolean exceptionOnValidate) throws BadTimeExpression {
        init(year,month,day,exceptionOnValidate);
        if(year < 0 || month <= 0 || day <= 0)
            throw new BadTimeExpression("Bad date, args must be positive");
    }


    public TimeExpression(int year, int month, int day, String refTxt, boolean exceptionOnValidate) throws BadTimeExpression {
        this(year,month,day,exceptionOnValidate);
        this.refNLTxt = refTxt;
    }
    public TimeExpression(int year, int month, String refTxt, boolean exceptionOnValidate) throws BadTimeExpression {
        this(year,month,exceptionOnValidate);
        this.refNLTxt = refTxt;
    }
    public TimeExpression(int year, String refTxt, boolean exceptionOnValidate) throws BadTimeExpression {
        this(year,exceptionOnValidate);
        this.refNLTxt = refTxt;
    }

    public TimeExpression(int year, int month, int day, String refTxt, boolean exceptionOnValidate, TEClass teClass) throws BadTimeExpression {
        this(year,month,day,exceptionOnValidate);
        this.refNLTxt = refTxt;
        this.teClass = teClass;
    }
    public TimeExpression(int year, int month, String refTxt, boolean exceptionOnValidate, TEClass teClass) throws BadTimeExpression {
        this(year,month,exceptionOnValidate);
        this.refNLTxt = refTxt;
        this.teClass = teClass;
    }
    public TimeExpression(int year, String refTxt, boolean exceptionOnValidate, TEClass teClass) throws BadTimeExpression {
        this(year,exceptionOnValidate);
        this.refNLTxt = refTxt;
        this.teClass = teClass;
    }


    private void init(int year, int month, int day, boolean validate) throws BadTimeExpression
    {
        try
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

            normalizedExpression = String.format("%04d",year);
            normalizedExpression += month > 0 ? String.format("%02d",month):"";
            normalizedExpression += day > 0 ? String.format("%02d",day):"";

            validate();

        }catch(BadTimeExpression e)
        {
            if(validate)
                throw e;
            valid = false;
            validationError = e.getMessage();
            logger.error(validationError);
        }
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
     * @throws BadTimeExpression on validation error
     */
    public TimeExpression(String normalizedExpression) throws BadTimeExpression
    {
        this(normalizedExpression,true);
    }
    private TimeExpression(String normalizedExpression,boolean validate) throws BadTimeExpression
    {
        this.normalizedExpression = normalizedExpression;
        try
        {
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
                    default: throw new BadTimeExpression("wrong number of chars: " + normalizedExpression.length() + " could be only 4(YYYY), 6(YYYYMM) or 8(YYYYMMDD)",this);
                }
            }
            catch (NumberFormatException e)
            {
                throw new BadTimeExpression("TimeExpression must be only numbers, came:" + normalizedExpression + " could be only 4(YYYY), 6(YYYYMM) or 8(YYYYMMDD) where Y, M and D must be integers between 0 and 9",this);
            }
            validate();
        }catch(BadTimeExpression e)
        {
            if(validate)
                throw e;
            valid = false;
            validationError = e.getMessage();
            logger.error(validationError);
        }
    }

    /**
     *
     * @param normalizedExpression YYYYMMDD
     * @param refTxt natural language text from the timex2
     * @throws BadTimeExpression when text is not valid in grammar YYYYMMDD
     */
    public TimeExpression(String normalizedExpression, String refTxt) throws BadTimeExpression
    {
        this(normalizedExpression,false);
        this.refNLTxt = refTxt;
    }

    public TimeExpression(String normalizedExpression, String refTxt, TEClass teClass) throws BadTimeExpression
    {
        this(normalizedExpression,false);
        this.refNLTxt = refTxt;
        this.teClass = teClass;
    }


    public TEClass getTeClass() {
        return teClass;
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
            throw new BadTimeExpression("Bad Year could be only 4(YYYY)",this);
        if(type != Type.YYYY && type != Type.YYY && type != Type.YY && type != Type.Y)
        {
            if(month < 1 || month > 12)
                throw new BadTimeExpression("Bad Month must be between 1 and 12",this);
            if(type == Type.YYYYMMDD)
            {
                if((month == 6 || month == 11 || month == 4 || month == 9)  && (day < 1 || day > 30))
                    throw new BadTimeExpression("Bad Day. For month " + month + ", day must be between 1 and 30",this);
                else if(month == 2 && (day < 1 || day > 29))
                    throw new BadTimeExpression("Bad Day. For month 2, day must be between 1 and 29",this);
                else if(day < 1 || day > 31)
                    throw new BadTimeExpression("Bad Day. For month " + month + ", day must be between 1 and 31",this);
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
        private TimeExpression timeExpression;

        public BadTimeExpression() {
        }

        public BadTimeExpression(String message, TimeExpression timeExpression)
        {
            super(message);
            this.timeExpression = timeExpression;
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

        public TimeExpression getTimeExpression() {
            return timeExpression;
        }
    }

    public boolean isValid()
    {
        return valid;
    }

    public boolean isMetric()
    {
        return valid && type != Type.UNKNOWN;
    }

    public String getValidationError()
    {
        return validationError;
    }

    public static enum Type
    {
        Y("Y"),
        YY("YY"),
        YYY("YYY"),
        YYYY("YYYY"),
        YYYYMM("YYYYMM"),
        YYYYMMDD("YYYYMMDD"),

        UNKNOWN("UNKNOWN");

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

    public static enum TEClass
    {
        Point("Point"),
        GenPoint("GenPoint"),
        Duration("Duration"),
        UNKNOWN("UNKNOWN"),
        ;

        String teclass;

        private TEClass(String teclass)
        {
            this.teclass = teclass;
        }

        public String toString()
        {
            return teclass;            
        }
    }


}
