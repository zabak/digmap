package pt.utl.ist.lucene.utils.temporal.tides;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 18:54:42
 * @email machadofisher@gmail.com
 */
public enum Timex2ValRegExprs {

//    YYYY_MM_DD("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}"),

    /**
     * Used
     */

    YYYY_MM_DD_ANY("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}.*"),
    YYYY_MM("[0-9]{4,4}\\-[0-9]{2,2}"),
    YYYY("[0-9]{4,4}"),
    YYY("[0-9]{3,3}"),
    YY("[0-9]{2,2}"),
    Y("[0-9]{1,1}"),
    YYYY_or_YYY_or_YY_or_Y("[0-9]{1,4}"),
    /* /Used */
//     YYYY_MM_DD_hh_mm_ss("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}T[0-2]{0,1}[0-9]:[0-9]{2,2}:[0-9]{2,2}"),
//     YYYY_MM_DD_hh_mm_ss_Z("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}T[0-2]{0,1}[0-9]:[0-9]{2,2}:[0-9]{2,2}Z"),

    /**
     * Used
     */
    //Week of the Year
    YYYY_Wweek("[0-9]{4,4}\\-W[0-9]{1,2}"),


    /*
    *   PXY   Pass X years from anchor val
    *   PXM   Pass X months from anchor val
    *   PXD  Pass X days from anchor val
    *   PXW  Pass X weeks from anchor val
    *
    * Used when Anchor Val is of the form of the used RegExprs
    * ad when Anchor_Dir == Starting or Ending
    * Otherwise is not used
    *
    * */

    /* /Used */
    PyearsY("P[0-9]{1,10}Y"),
    PmonthsM("P[0-9]{1,10}M"),
    PdaysD("P[0-9]{1,10}D"),
    PweeksW("P[0-9]{1,10}W"),

    UNKNOWN("");


    String regExpr;
    String desc;
//    Pattern pattern;


    public boolean isP()
    {
        return this == PyearsY || this == PmonthsM || this == PdaysD || this == PweeksW;
    }

    public boolean match(String str)
    {
        return str.matches(regExpr);
    }

    private Timex2ValRegExprs(String regExpr)
    {
        this.regExpr = regExpr;
        this.desc = this.name();
    }

    public static Timex2ValRegExprs getType(String expr)
    {
        if(expr == null)
            return UNKNOWN;
        for(Timex2ValRegExprs timex2ValRegExprs: values())
        {
            if(timex2ValRegExprs.match(expr))
                return timex2ValRegExprs;
        }
        return UNKNOWN;
    }

    public static void main(String[]args)
    {
        System.out.println(getType("19").name());
    }

}
