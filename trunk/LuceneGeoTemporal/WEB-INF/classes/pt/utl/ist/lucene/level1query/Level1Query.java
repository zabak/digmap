package pt.utl.ist.lucene.level1query;

import pt.utl.ist.lucene.level1query.IQuery;
import pt.utl.ist.lucene.level1query.QueryParams;
import pt.utl.ist.lucene.level1query.parser.*;

import java.util.Iterator;
import java.util.List;


/**
 * @author Jorge Machado jmachado@estgp.pt
 * @date 5/Jan/2007
 */
public class Level1Query implements IQuery
{

    //Can be Terms, Subqueries, Logical, IndexFields
    List objects;
    List allObjects;

    QueryParams queryParams = new QueryParams();


    public List getObjects()
    {
        return objects;
    }

    public void setObjects(List objects)
    {
        this.objects = objects;
    }

    public List getAllObjects()
    {
        return allObjects;
    }

    public void setAllObjects(List allObjects)
    {
        this.allObjects = allObjects;
    }

    public Iterator termIterator()
    {
        return new TermIterator(this, null);
    }

    public Iterator termIterator(String[] notAdmitedIndexes)
    {
        return new TermIterator(this, notAdmitedIndexes);

    }

    public String toStringToPresent()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toStringToPresent();
        }
        return toReturn.trim();
    }

    public String toString()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toString();
        }
        return toReturn.trim();
    }

    public String toString(String[] notAdmitedIndexes)
    {
        return toString(notAdmitedIndexes, objects);
    }

    public static String toString(String[] notAdmitedIndexes, List objects)
    {
        String toReturn = "";
        Logical waitLogical = null;
        for (Object object : objects)
        {
            if (object instanceof RectParentisis)
            {
                String inside = toString(notAdmitedIndexes, ((RectParentisis) object).getObjects());
                if (inside.trim().length() > 0)
                    toReturn += " [" + inside + "] ";
            }
            if (object instanceof Parentisis)
            {
                String inside = toString(notAdmitedIndexes, ((Parentisis) object).getObjects());
                if (inside.trim().length() > 0)
                    toReturn += " (" + inside + ") ";
            }
            if (object instanceof RectParentisis)
            {
                String inside = toString(notAdmitedIndexes, ((RectParentisis) object).getObjects());
                if (inside.trim().length() > 0)
                    toReturn += " [" + inside + "] ";
            }
            else if (object instanceof Logical)
            {
                waitLogical = (Logical) object;
            }
            else if (object instanceof IndexField)
            {
                boolean admited = true;
                for (String notAdmitedIndex : notAdmitedIndexes)
                {
                    if (((IndexField) object).getIndexName().equals(notAdmitedIndex))
                    {
                        admited = false;
                        break;
                    }
                }
                if (admited)
                {
                    if (waitLogical != null)
                    {
                        toReturn += " " + waitLogical.toString();
                    }
                    toReturn += " " + object.toString();
                }
                waitLogical = null;
            }
            else
            {
                if (waitLogical != null)
                {
                    toReturn += " " + waitLogical.toString();
                }
                toReturn += " " + object.toString();
                waitLogical = null;
            }

        }
        return toReturn.trim();
    }

    public String toStringHighLight()
    {
        String toReturn = "";
        for (Object object : objects)
        {
            IQuery o = (IQuery) object;
            toReturn += " " + o.toStringHighLight();
        }
        return toReturn.trim();
    }

    public String getTermsString(String[] notAdmitedIndexes)
    {
        String toReturn = "";
        Iterator iter = termIterator(notAdmitedIndexes);
        while (iter.hasNext())
        {
            Term term = (Term) iter.next();
            term.setGetTermsStringOffSet(toReturn.length());
            toReturn += " " + term.toString();
        }
        return toReturn.trim();
    }

    public Term getTerm(int i)
    {
        for (Object allObject : allObjects)
        {
            Term term = (Term) allObject;
            if (term.getId() == i)
                return term;
        }
        return null;
    }

    public Term getField(String field)
    {
        for (Object allObject : allObjects)
        {
            Term term = (Term) allObject;
            if (term.getIndex().equals(field))
                return term;
        }
        return null;
    }

    public QueryParams getQueryParams()
    {
        return queryParams;
    }

    public void setQueryParams(QueryParams queryParams)
    {
        this.queryParams = queryParams;
    }
}
