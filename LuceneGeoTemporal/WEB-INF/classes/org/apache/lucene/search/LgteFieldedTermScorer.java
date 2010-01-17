package org.apache.lucene.search;

import org.apache.lucene.index.Term;

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
    /**
     * Constructs a Scorer.
     * @param similarity similarity
     */
    protected LgteFieldedTermScorer(Similarity similarity) {
        super(similarity);
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
}
