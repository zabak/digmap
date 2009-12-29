package pt.utl.ist.lucene.utils.temporal;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 17:32:13
 * @email machadofisher@gmail.com
 */
public class TimeExpressionUnkown extends TimeExpression
{
    static TimeExpression unknownTimeExpression = new TimeExpression();

    public static TimeExpression getInstance()
    {
        return unknownTimeExpression;
    }
}
