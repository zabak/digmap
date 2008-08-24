package pt.utl.ist.lucene.level1query.parser;

import org.apache.log4j.Logger;

import java.util.Iterator;

import pt.utl.ist.lucene.level1query.Level1Query;

/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class TermIterator implements Iterator
{

    private static final Logger logger = Logger.getLogger(TermIterator.class);

    String[] notAdmitedIndexes = null;
    Level1Query query = null;
    Iterator iter = null;
    Term next = null;
    int i = 0;

    private boolean isAdmitedTerm(Term t)
    {
        if (notAdmitedIndexes == null || t.getIndex() == null)
            return true;
        for (String notAdmitedIndexe : notAdmitedIndexes)
        {
            if (notAdmitedIndexe.equals(next.getIndex()))
                return false;
        }
        return true;
    }

    private void calcNext()
    {
        next = null;
        while (iter.hasNext())
        {
            next = (Term) iter.next();
            if (isAdmitedTerm(next))
            {
                i++;
                break;
            }
            else next = null;
        }
    }

    public TermIterator(Level1Query query, String[] notAdmitedIndexes)
    {
        this.notAdmitedIndexes = notAdmitedIndexes;
        this.query = query;
        this.iter = query.getAllObjects().iterator();
        calcNext();
    }

    public boolean hasNext()
    {
        return next != null;
    }

    public Object next()
    {
        if (next != null)
        {
            Term toReturn = next;
            calcNext();
            return toReturn;
        }
        else
            throw new ArrayIndexOutOfBoundsException("TermIterator trying access " + i + 1);
    }

    public void remove()
    {
        logger.error("method not implemented");
    }
}
