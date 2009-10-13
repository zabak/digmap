package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * For Fields with multiplicity 1
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class SimpleIsolatedFieldFilter implements FieldFilter
{

    public FilteredFields filter(Node element, String fieldName)
    {


        Set<FilteredFields.TextField> fields = new HashSet<FilteredFields.TextField>();
        fields.add(new FilteredFields.TextField(fieldName,element.getText()));
        return new FilteredFields(null,null,null,fields);
    }
}
