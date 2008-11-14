package pt.utl.ist.lucene.treceval.handlers.collections;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.util.EscapeChars;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class CFlatSgmlHandler implements CDocumentHandler
{
    private static final Logger logger = Logger.getLogger(CXmlHandler.class);


    public void handle(InputStream stream,String filePath, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers) throws IOException
    {
        String xml = getXmlRootedDocument(stream);
        try
        {
            Document dom = Dom4jUtil.parse(xml);
            new CXmlHandler().run(dom,filePath, handler,callBack);
        }
        catch (DocumentException e)
        {
            logger.error(e,e);
        }
    }

    private String getXmlRootedDocument(InputStream stream) throws IOException
    {
        char[] buffer = new char[1024];
        // decompress the file
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\"?><docs>");
        int length;
        InputStreamReader r = new InputStreamReader(stream, Globals.COLLECTION_FILES_DEFAULT_ENCODING);
        while ((length = r.read(buffer, 0, 1024)) != -1)
        {
            String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length));
            builder.append(newStr);
        }
        builder.append("</docs>");
        return builder.toString();
    }
}
