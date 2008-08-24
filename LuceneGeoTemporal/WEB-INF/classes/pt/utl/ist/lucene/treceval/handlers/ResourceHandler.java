package pt.utl.ist.lucene.treceval.handlers;

import org.dom4j.Element;
import pt.utl.ist.lucene.treceval.handlers.IdMap;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public interface ResourceHandler
{
    public String getResourceXpath();
    public IdMap handle(Element element) throws IOException;
}
