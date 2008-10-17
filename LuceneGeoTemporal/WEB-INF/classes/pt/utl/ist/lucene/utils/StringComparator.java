package pt.utl.ist.lucene.utils;

/**
 * @author Jorge Machado
 * @date 17/Out/2008
 * @see pt.utl.ist.lucene.utils
 */
public class StringComparator implements java.util.Comparator<String>
{
     private static StringComparator instance = null;

    public static StringComparator getInstance()
    {
        if(StringComparator.instance == null)
        {
            StringComparator.instance = new StringComparator();
        }
        return StringComparator.instance;
    }

    /** Creates a new instance of LongComparator */
    private StringComparator() {
    }

    /** Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     *
     * It is generally the case, but <i>not</i> strictly required that
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     *
     * @param s1 the first object to be compared.
     * @param s2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     * 	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     *
     */
    public int compare(String s1, String s2)
    {
        int compareResult = s2.compareTo(s1);
        if(compareResult > 0)
            return -1;
        else if(compareResult < 0)
            return 1;
        return 0;
    }
}
