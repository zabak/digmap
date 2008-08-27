package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Element;
import org.dom4j.Node;

import java.util.Map;

import pt.utl.ist.lucene.treceval.handlers.XmlFieldHandler;
import pt.utl.ist.lucene.treceval.handlers.FieldFilter;

/**
 * Just return the value of the filtered field 
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class SimpleXmlFieldHandler implements XmlFieldHandler
{
    String fieldXpath;
    FieldFilter fieldFilter;
    String fieldName;


    public SimpleXmlFieldHandler(String fieldXpath, FieldFilter fieldFilter, String fieldName)
    {
        this.fieldXpath = fieldXpath;
        this.fieldFilter = fieldFilter;
        this.fieldName = fieldName;
    }

    public Map<String, String> getFields(Node element)
    {
        return fieldFilter.filter(element,fieldName);
    }

    public String getFieldXpath()
    {
        return fieldXpath;
    }

    public FieldFilter getFieldFilter()
    {
        return fieldFilter;
    }
}
