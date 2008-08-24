package pt.utl.ist.lucene.treceval.handlers;

import pt.utl.ist.lucene.treceval.handlers.MultipleFieldFilter;

/**
 *
 * For Fields with multiplicity 1
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class SimpleFieldFilter extends MultipleFieldFilter
{
    public SimpleFieldFilter()
    {
        super(1);
    }
}
