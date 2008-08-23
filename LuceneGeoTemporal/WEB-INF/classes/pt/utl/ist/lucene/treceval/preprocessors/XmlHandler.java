package pt.utl.ist.lucene.treceval.preprocessors;

import org.dom4j.*;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.List;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;

/**
 * Handles a XML Document and select all resources from it invoking the handler for each one
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors.adhoc
 */
public class XmlHandler implements DocumentHandler
{
    private static final Logger logger = Logger.getLogger(XmlHandler.class);

    public void handle(InputStream stream, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers)
    {
        InputSource source = new InputSource(stream);
        try
        {
            Document dom = Dom4jUtil.parse(source);
            run(dom,handler,callBack);
        }
        catch (DocumentException e)
        {
            logger.error(e,e);
        }
        catch (MalformedURLException e)
        {
            logger.error(e,e);
        }
        catch (IOException e)
        {
            logger.error(e,e);
        }
    }

    public void run(Document dom, ResourceHandler handler, IndexFilesCallBack callBack) throws IOException
    {
        XPath resourceXpath = dom.createXPath(handler.getResourceXpath());
        List<Element> resources = resourceXpath.selectNodes(dom.getRootElement());
        for(Element element: resources)
        {
            try
            {
                handler.handle(element,callBack);
            }
            catch (IOException e)
            {
                throw e;
            }
        }
    }
}
