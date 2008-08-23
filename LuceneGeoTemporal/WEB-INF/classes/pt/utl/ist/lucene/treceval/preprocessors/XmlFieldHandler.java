package pt.utl.ist.lucene.treceval.preprocessors;

import org.dom4j.Element;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public interface XmlFieldHandler
{
    public Map<String,String> getFields(Element element);
    public String getFieldXpath();
}
