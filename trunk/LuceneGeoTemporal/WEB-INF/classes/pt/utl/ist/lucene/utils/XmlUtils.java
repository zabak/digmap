package pt.utl.ist.lucene.utils;

import org.dom4j.DocumentException;
import org.dom4j.Document;
import org.dom4j.XPath;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;


/**
 * @author Jorge Machado
 * @date 23/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class XmlUtils
{

    public static Node getFragment(String filePath, String xpathString, Map<String,String> namespaces, String encoding) throws MalformedURLException, DocumentException, UnsupportedEncodingException, FileNotFoundException {
           try
           {
               Document dom = Dom4jUtil.parse(new InputSource(new InputStreamReader(new FileInputStream(filePath),encoding)));
               XPath xPath = dom.createXPath(xpathString);
               if(namespaces != null)
                   xPath.setNamespaceURIs(namespaces);
               return xPath.selectSingleNode(dom.getRootElement());
           }
           catch (DocumentException e)
           {
               throw e;
           }
           catch (MalformedURLException e)
           {
               throw e;
           } catch (FileNotFoundException e) {
               throw e;
           } catch (UnsupportedEncodingException e) {
               throw e;
           }
       }


     public static void writeSout(Document document) throws IOException
    {
        // Compact format to System.out
        OutputFormat format = OutputFormat.createCompactFormat();
        XMLWriter writer = new XMLWriter( System.out, format );
        writer.write( document );
    }
    public static Node getFragment(String filePath, String xpathString, Map<String,String> namespaces) throws MalformedURLException, DocumentException
    {
        try
        {
            Document dom = Dom4jUtil.parse(new File(filePath));
            XPath xPath = dom.createXPath(xpathString);
            if(namespaces != null)
                xPath.setNamespaceURIs(namespaces);
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

    public static String escape(String text)
    {
        if(text == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<text.length();i++)
        {
            if (text.charAt(i) == '<')
            {
                stringBuilder.append("&lt;");
            }
            else if (text.charAt(i) == '>')
            {
                stringBuilder.append("&gt;");
            }
            else
            {
                stringBuilder.append(text.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String [] args) throws MalformedURLException, DocumentException
    {
        System.out.println(getFragment("c:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\classes\\test-data\\documents\\GLS-1620.xml","//DOC[contains(DOCNO,'GLS-01-01-04']')]",null));
    }
}
