package pt.utl.ist.lucene.treceval.handlers;

import org.apache.lucene.document.Field;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.topics
 */
public class IdMap
{
    String id;
    Map<String,String> textFields;
    Collection<Field> uniqueFields;


    public IdMap(String id, Map<String, String> textFields, Collection<Field> uniqueFields)
    {
        this.id = id;
        this.textFields = textFields;
        this.uniqueFields = uniqueFields;
    }

    public IdMap(String id, Map<String, String> textFields)
    {
        this.id = id;
        this.textFields = textFields;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setTextFields(Map<String, String> textFields)
    {
        this.textFields = textFields;
    }

    public Map<String, String> getTextFields()
    {
        return textFields;
    }


    public Collection<Field> getUniqueFields()
    {
        return uniqueFields;
    }

    public void setUniqueFields(Collection<Field> uniqueFields)
    {
        this.uniqueFields = uniqueFields;
    }
}
