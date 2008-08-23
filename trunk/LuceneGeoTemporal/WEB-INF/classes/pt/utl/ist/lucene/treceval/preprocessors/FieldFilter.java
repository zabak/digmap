package pt.utl.ist.lucene.treceval.preprocessors;

import org.dom4j.Element;

import java.util.Map;

/**
 * Can be used for example to get the first line and multiply by 3 and then the rest of the text
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public interface FieldFilter
{
    public Map<String,String> filter(Element element, String fieldName);
}
