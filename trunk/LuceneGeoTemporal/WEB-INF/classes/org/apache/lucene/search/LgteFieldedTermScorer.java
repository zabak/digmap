package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;

import java.io.Reader;

/**
 * @author Jorge Machado
 * @date 16/Jan/2010
 * @time 12:44:33
 * @email machadofisher@gmail.com
 *
 * This class provide an interface to get the field of the term being scored
 *
 */
public abstract class LgteFieldedTermScorer extends Scorer
{
    protected Term term = null;
    protected int doc;
    protected IndexReader reader;

    /**
     * Constructs a Scorer.
     * @param similarity similarity
     */
    protected LgteFieldedTermScorer(IndexReader reader, Similarity similarity)
    {
        super(similarity);
        this.reader = reader;
    }

    public String getField()
    {
        if(term == null && this instanceof PhraseScorer)
        {
            if(((PhraseScorer)this).getWeight().getQuery() instanceof PhraseQuery)
            {
                return ((PhraseQuery)((PhraseScorer)this).getWeight().getQuery()).getTerms()[0].field();
            }
            else
            {
                throw new RuntimeException("Method getField not implemented for this type of Scorer: " + this.getClass().getName());
            }
        }
        else if(term != null)
            return term.field();
        else
            throw new RuntimeException("Method getField not implemented for this type of Scorer: " + this.getClass().getName());
    }

    public int doc()
    {
        return doc;
    }
    

    public IndexReader getIndexReader()
    {
        return reader;
    }
}
