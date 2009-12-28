package pt.utl.ist.lucene.utils.temporal;

import org.apache.commons.digester.RegexMatcher;

import java.util.regex.Pattern;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 18:54:42
 * @email machadofisher@gmail.com
 */
public enum Timex2val
{

//    YYYY_MM_DD("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}"),

    /**
     * Used
     */

    REGEXPR_YYYY_MM_DD_ANY("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}.*"),
    REGEXPR_YYYY_MM("[0-9]{4,4}\\-[0-9]{2,2}"),
    REGEXPR_YYYY("[0-9]{4,4}"),
    REGEXPR_YYY("[0-9]{3,3}"),
    REGEXPR_YY("[0-9]{2,2}"),
    REGEXPR_Y("[0-9]{1,1}"),
    REGEXPR_YYYY_YYY_YY_Y("[0-9]{1,4}"),
    /* /Used */
//     YYYY_MM_DD_hh_mm_ss("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}T[0-2]{0,1}[0-9]:[0-9]{2,2}:[0-9]{2,2}"),
//     YYYY_MM_DD_hh_mm_ss_Z("[0-9]{4,4}\\-[0-9]{2,2}\\-[0-9]{2,2}T[0-2]{0,1}[0-9]:[0-9]{2,2}:[0-9]{2,2}Z"),

    /**
     * Used
     */
    //Week of the Year
    REGEXPR_YYYY_WXX("[0-9]{4,4}\\-W[0-9]{1,2}"),


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
    REGEXPR_PXY("P[0-9]{1,10}Y"),
    REGEXPR_PXM("P[0-9]{1,10}M"),
    REGEXPR_PXD("P[0-9]{1,10}D"),
    REGEXPR_PXW("P[0-9]{1,10}W");

    String regExpr;
//    Pattern pattern;

    public boolean match(String str)
    {
        return str.matches(regExpr);
    }

    private Timex2val(String regExpr)
    {
        this.regExpr = regExpr;
    }

}
