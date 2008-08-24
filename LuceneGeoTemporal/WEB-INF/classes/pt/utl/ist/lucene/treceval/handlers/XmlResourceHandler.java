package pt.utl.ist.lucene.treceval.handlers;

import pt.utl.ist.lucene.treceval.handlers.XmlFieldHandler;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.handlers.IdMap;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.Node;

/**
 * XML Resource Handler will Call Field Handler for each given field
 * Call indexer in a callback with fields and ResourceId
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class XmlResourceHandler implements ResourceHandler
{
    private String resourceXpath;
    private String idXpath;
    private List<XmlFieldHandler> fieldHandlers;


    public XmlResourceHandler(String resourceXpath, String idXpath, List<XmlFieldHandler> fieldHandlers)
    {
        this.resourceXpath = resourceXpath;
        this.idXpath = idXpath;
        this.fieldHandlers = fieldHandlers;
    }


    public String getResourceXpath()
    {
        return resourceXpath;
    }

    public IdMap handle(Element element) throws IOException
    {
        XPath idXPath = element.createXPath(idXpath);
        Element id = (Element) idXPath.selectSingleNode(element);

        Map<String,String> map = new HashMap<String,String>();
        for(XmlFieldHandler handler: fieldHandlers)
        {
            XPath xPath = element.createXPath(handler.getFieldXpath());
            List<Node> nodes = xPath.selectNodes(element);
            for(Node node: nodes)
            {
                Map<String,String> nodeMap = handler.getFields((Element) node);
                for(Map.Entry<String,String> entry:nodeMap.entrySet())
                {
                    String old = map.get(entry.getKey());
                    if(old == null)
                        map.put(entry.getKey(),entry.getValue());
                    else
                        map.put(entry.getKey(),old + ' ' + entry.getValue());
                }
            }
        }
        return new IdMap(id.getTextTrim(),map);
    }

    public String getIdXpath()
    {
        return idXpath;
    }

    public List<XmlFieldHandler> getFieldHandlers()
    {
        return fieldHandlers;
    }
}
