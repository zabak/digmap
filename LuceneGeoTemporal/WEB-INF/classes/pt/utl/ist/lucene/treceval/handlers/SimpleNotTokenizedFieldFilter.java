package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Node;
import org.apache.lucene.document.Field;

import java.util.Collection;
import java.util.ArrayList;

import pt.utl.ist.lucene.LgteDocumentWrapper;

/**
 *
 * For Fields with multiplicity 1
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class SimpleNotTokenizedFieldFilter implements FieldFilter
{

    public FilteredFields filter(Node element, String fieldName)
    {
        Collection<Field> fields = new ArrayList<Field>();
        fields.add(LgteDocumentWrapper.getField(fieldName,element.getText().trim(),true,true,false));
        return new FilteredFields(fields);
    }
}
