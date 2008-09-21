package org.apache.lucene.search;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;


/**
 * Constructs a filter for docs matching any of the terms added to this class. 
 * Unlike a RangeFilter this can be used for filtering on multiple terms that are not necessarily in 
 * a sequence. An example might be a collection of primary keys from a database query result or perhaps 
 * a choice of "category" labels picked by the end user. As a filter, this is much faster than the 
 * equivalent query (a BooleanQuery with many "should" TermQueries)
 * 
 * @author maharwood
 */
public class TermsFilter extends Filter
{
	Set terms=new TreeSet();
	
	/**
	 * Adds a term to the list of acceptable terms   
	 * @param term
	 */
	public void addTerm(Term term)
	{
		terms.add(term);
	}
	
  public BitSet bits(IndexReader reader) throws IOException {
	  return getDocIdSet(reader);
  }

	/* (non-Javadoc)
   * @see org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.IndexReader)
	 */
  public BitSet getDocIdSet(IndexReader reader) throws IOException {
        BitSet result=new BitSet(reader.maxDoc());
        TermDocs td = reader.termDocs();
        try
        {
            for (Iterator iter = terms.iterator(); iter.hasNext();)
            {
                Term term = (Term) iter.next();
                td.seek(term);
                while (td.next())
                {
                    result.set(td.doc());
                }
            }
        }
        finally
        {
            td.close();
        }
        return result;
	}
	
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
				return false;
		TermsFilter test = (TermsFilter)obj;
		return (terms == test.terms ||
					 (terms != null && terms.equals(test.terms)));
	}

	public int hashCode()
	{
		int hash=9;
		for (Iterator iter = terms.iterator(); iter.hasNext();)
		{
			Term term = (Term) iter.next();
			hash = 31 * hash + term.hashCode();			
		}
		return hash;
	}
	
}