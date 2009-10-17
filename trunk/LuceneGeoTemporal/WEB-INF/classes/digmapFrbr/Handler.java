package digmapFrbr;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.io.StringReader;
import java.io.InputStreamReader;

import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import pt.utl.ist.lucene.utils.Dom4jUtil;


public class Handler extends HttpServlet
{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        response.getWriter().write("<frbrCluster>Does not support GET</frbrCluster>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");


        try
        {
//           //O XML vem BOM que eu já testei, para testares faz um pedido com o post que está no index.html e descomenta isto           
//            String XML = request.getParameter("request");
//            System.out.println(XML);
//            SAXReader reader = new SAXReader();
//            createIgnoreErrorHandler(reader);
//            Document dom = reader.read(new StringReader(XML));
//
//
//
//
//
//            XMLWriter writer = new XMLWriter(response.getOutputStream());
//            writer.write( dom );
//            writer.close();
//
//          e comenta o resto

            InputSource is = new InputSource(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("retrieval.xml"), "UTF-8"));
            SAXReader reader2 = new SAXReader();
            createIgnoreErrorHandler(reader2);
            Document dom2 = reader2.read(is);
            XMLWriter writer = new XMLWriter(response.getOutputStream());
            writer.write( dom2 );
            writer.close();
        }
        catch (DocumentException e)
        {
            response.getWriter().write("<frbrCluster><error>" + e.toString() + "</error></frbrCluster>");
            e.printStackTrace();
        }

    }

    public static void createIgnoreErrorHandler(SAXReader reader)
    {
        reader.setValidation(false);
//        reader.setErrorHandler(new ErrorHandler()
//        {
//
//            public void warning(SAXParseException exception) throws SAXException {
//                //System.out.println(exception);
//            }
//
//            public void error(SAXParseException exception) throws SAXException
//            {
//                //System.out.println(exception);
//            }
//
//            public void fatalError(SAXParseException exception) throws SAXException
//            {
//                //System.out.println(exception);
//            }
//        });
    }

}
