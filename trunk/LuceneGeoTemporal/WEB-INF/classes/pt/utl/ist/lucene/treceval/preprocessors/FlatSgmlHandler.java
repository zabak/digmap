package pt.utl.ist.lucene.treceval.preprocessors;

import pt.utl.ist.lucene.treceval.util.EscapeChars;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class FlatSgmlHandler implements DocumentHandler
{
    private static final Logger logger = Logger.getLogger(XmlHandler.class);


    public void handle(InputStream stream, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers) throws IOException
    {
        String xml = getXmlRootedDocument(stream);
        try
        {
            Document dom = Dom4jUtil.parse(xml);
            new XmlHandler().run(dom,handler,callBack);
        }
        catch (DocumentException e)
        {
            logger.error(e,e);
        }
    }

    private String getXmlRootedDocument(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[1024];
        // decompress the file
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\"?><docs>");
        int length;
        while ((length = stream.read(buffer, 0, 1024)) != -1)
        {
            String newStr = EscapeChars.forXMLOnlySpecialInternal(new String(buffer, 0, length));
            builder.append(newStr);
        }
        builder.append("</docs>");
        return builder.toString();
    }
}
