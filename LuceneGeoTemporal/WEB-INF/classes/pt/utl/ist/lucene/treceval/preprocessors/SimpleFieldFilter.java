package pt.utl.ist.lucene.treceval.preprocessors;

import org.dom4j.Element;

import java.util.HashMap;

/**
 *
 * For Fields with multiplicity 1
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class SimpleFieldFilter extends MultipleFieldFilter
{
    public SimpleFieldFilter()
    {
        super(1);
    }
}
