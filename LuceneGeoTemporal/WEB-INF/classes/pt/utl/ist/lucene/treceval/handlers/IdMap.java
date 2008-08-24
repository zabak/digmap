package pt.utl.ist.lucene.treceval.handlers;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.topics
 */
public class IdMap
{
    String id;
    Map<String,String> fields;


    public IdMap(String id, Map<String, String> fields)
    {
        this.id = id;
        this.fields = fields;
    }

    public String getId()
    {
        return id;
    }

    public Map<String, String> getFields()
    {
        return fields;
    }
}
