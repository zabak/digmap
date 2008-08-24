package pt.utl.ist.lucene.utils;

import org.dom4j.DocumentException;
import org.dom4j.Document;
import org.dom4j.XPath;
import org.dom4j.Node;

import java.io.File;
import java.net.MalformedURLException;


/**
 * @author Jorge Machado
 * @date 23/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class XmlUtils
{

    public static Node getFragment(String filePath, String xpathString) throws MalformedURLException, DocumentException
    {
        try
        {
            Document dom = Dom4jUtil.parse(new File(filePath));
            XPath xPath = dom.createXPath(xpathString);
            return xPath.selectSingleNode(dom.getRootElement());
        }
        catch (DocumentException e)
        {
            throw e;
        }
        catch (MalformedURLException e)
        {
            throw e;
        }
    }

    public static void main(String [] args) throws MalformedURLException, DocumentException
    {
        System.out.println(getFragment("c:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\classes\\test-data\\documents\\GLS-1620.xml","//DOC[contains(DOCNO,'GLS-01-01-04']')]"));
    }
}
