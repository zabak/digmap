package pt.utl.ist.lucene.treceval.preprocessors;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import org.dom4j.Element;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public interface ResourceHandler
{
    public String getResourceXpath();
    public void handle(Element element, IndexFilesCallBack callBack) throws IOException;
}
