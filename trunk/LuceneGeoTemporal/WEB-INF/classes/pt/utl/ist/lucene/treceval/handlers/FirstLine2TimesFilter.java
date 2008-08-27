package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Element;
import org.dom4j.Node;

import java.util.Map;
import java.util.HashMap;

import pt.utl.ist.lucene.treceval.handlers.FieldFilter;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.collections
 */
public class FirstLine2TimesFilter implements FieldFilter
{
    public Map<String, String> filter(Node element, String fieldName)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        String text = element.getText();
        int endOfLine = text.indexOf('\n');
        if(endOfLine > 0)
        {
            String title = text.substring(0,endOfLine);
            text = title + ' ' + text;
        }
        else
        {
            text += ' ' + text;
        }
        map.put(fieldName,text);
        return map;
    }
}