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
    Map<String,String> storedTextFields;
    Collection<Field> preparedFields;
    Collection<TextField> isolatedTextFields;

    public static class TextField
    {
        public String name;
        public String value;


        public TextField(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }



    public FilteredFields()
    {
    }

    public FilteredFields(Collection<Field> fields)
    {
        this.preparedFields = fields;
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


    public FilteredFields(Map<String, String> textFields, Collection<Field> preparedFields, Map<String, String> storedTextFields)
    {
        this.textFields = textFields;
        this.preparedFields = preparedFields;
        this.storedTextFields = storedTextFields;
    }
    public FilteredFields(Map<String, String> textFields, Collection<Field> preparedFields, Map<String, String> storedTextFields, Collection<TextField> isolatedTextFields)
    {
        this.textFields = textFields;
        this.preparedFields = preparedFields;
        this.storedTextFields = storedTextFields;
        this.isolatedTextFields = isolatedTextFields;
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


    public Map<String, String> getStoredTextFields()
    {
        return storedTextFields;
    }

    public void setStoredTextFields(Map<String, String> storedTextFields)
    {
        this.storedTextFields = storedTextFields;
    }


    public Collection<TextField> getIsolatedTextFields() {
        return isolatedTextFields;
    }

    public void setIsolatedTextFields(Collection<TextField> isolatedTextFields) {
        this.isolatedTextFields = isolatedTextFields;
    }
}
