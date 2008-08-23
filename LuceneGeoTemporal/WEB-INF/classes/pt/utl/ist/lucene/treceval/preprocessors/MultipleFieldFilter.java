package pt.utl.ist.lucene.treceval.preprocessors;

import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * This filter return the value of the field multiplied by N
 * example:
 *      &lt;title&gt;
 *          LGTE: a GeoTemporal Extension for Lucene
 *      &lt;/title&gt;
 *  with multiplicity 3 will return a map with just one entry:
 *
 *  LGTE: a GeoTemporal Extension for Lucene LGTE: a GeoTemporal Extension for Lucene LGTE: a GeoTemporal Extension for Lucene
 *
 * 
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class MultipleFieldFilter implements FieldFilter
{
    int multiplicity;


    public MultipleFieldFilter(int multiplicity)
    {
        this.multiplicity = multiplicity;
    }

    public Map<String, String> filter(Element element, String fieldName)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        StringBuilder builder = new StringBuilder();
        String text = element.getTextTrim();
        for (int i = 0; i < multiplicity; i++)
        {
            builder.append(text).append(' ');
        }
        map.put(fieldName, builder.toString());
        return map;
    }
}
