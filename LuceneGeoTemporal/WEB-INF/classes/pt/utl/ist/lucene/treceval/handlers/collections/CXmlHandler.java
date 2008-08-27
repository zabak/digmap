package pt.utl.ist.lucene.treceval.handlers.collections;

import org.dom4j.*;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.List;
import java.util.Map;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.handlers.IdMap;

/**
 * Handles a XML Document and select all resources from it invoking the handler for each one
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.adhoc
 */
public class CXmlHandler implements CDocumentHandler
{
    private static final Logger logger = Logger.getLogger(CXmlHandler.class);

    public void handle(InputStream stream, String filePath, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers)
    {
        InputSource source = new InputSource(stream);
        try
        {
            Document dom = Dom4jUtil.parse(source);
            run(dom,filePath, handler,callBack);
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

    public void run(Document dom, String filepath, ResourceHandler handler, IndexFilesCallBack callBack) throws IOException
    {
        XPath resourceXpath = dom.createXPath(handler.getResourceXpath());
        Map<String,String> namespaces = handler.getNamespaces();
        if(namespaces != null)
            resourceXpath.setNamespaceURIs(namespaces);
        List<Element> resources = resourceXpath.selectNodes(dom.getRootElement());
        for(Element element: resources)
        {
            try
            {
                IdMap idMap = handler.handle(element);
                if(idMap != null)
                {
                    try
                    {
                        idMap.getTextFields().put(Globals.DOCUMENT_FILE_PATH,filepath);
                        callBack.indexDoc(idMap.getId(),idMap.getTextFields(),null);
                    }
                    catch (IOException e)
                    {
                        throw e;
                    }
                }
            }
            catch (IOException e)
            {
                throw e;
            }
        }
    }
}
