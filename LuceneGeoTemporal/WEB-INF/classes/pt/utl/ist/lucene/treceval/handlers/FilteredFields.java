package pt.utl.ist.lucene.treceval.handlers;

import org.apache.lucene.document.Field;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 27/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class FilteredFields
{
    Map<String,String> textFields;
    Collection<Field> preparedFields;


    public FilteredFields(Collection<Field> uniqueFields)
    {
        this.preparedFields = uniqueFields;
    }

    public FilteredFields(Map<String, String> textFields)
    {
        this.textFields = textFields;
    }

    public FilteredFields(Map<String, String> textFields,  Collection<Field> uniqueFields)
    {
        this.textFields = textFields;
        this.preparedFields = uniqueFields;
    }

    public Map<String, String> getTextFields()
    {
        return textFields;
    }

    public void setTextFields(Map<String, String> textFields)
    {
        this.textFields = textFields;
    }

    public Collection<Field> getPreparedFields()
    {
        return preparedFields;
    }

    public void setPreparedFields(Collection<Field> preparedFields)
    {
        this.preparedFields = preparedFields;
    }


}
