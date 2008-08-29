package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Node;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * For Fields with multiplicity 1
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class SimpleStoreFieldFilter implements FieldFilter
{

    public FilteredFields filter(Node element, String fieldName)
    {
		Map<String,String> fields = new HashMap<String,String>();
        fields.put(fieldName,element.getText());
        return new FilteredFields(null,null,fields);
    }
}
