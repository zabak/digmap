/*
 * DomUtil
 *
 * Copyright 2007 Innovkey
 */
package pt.utl.ist.lucene.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Class Description
 *
 * @author Jorge Machado <machadofisher@gmail.com>
 * @see ;
 */

public class Dom4jUtil
{
    public static Document parse(URL url) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        createIgnoreErrorHandler(reader);
        return reader.read(url);
    }

    public static Document parse(String xml) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        createIgnoreErrorHandler(reader);
        return reader.read(new StringReader(xml));
    }
    public static void createIgnoreErrorHandler(SAXReader reader)
    {
        reader.setValidation(false);
        reader.setErrorHandler(new ErrorHandler()
        {

            public void warning(SAXParseException exception) throws SAXException
            {
                //System.out.println(exception);
            }

            public void error(SAXParseException exception) throws SAXException
            {
                //System.out.println(exception);
            }

            public void fatalError(SAXParseException exception) throws SAXException
            {
                //System.out.println(exception);
            }
        });
    }
    public static Document parse(File file) throws DocumentException, MalformedURLException
    {
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        createIgnoreErrorHandler(reader);
        return reader.read(file);
    }

    public static Document parse(InputSource source) throws DocumentException, MalformedURLException
    {
        SAXReader reader = new SAXReader();
        createIgnoreErrorHandler(reader);
        return reader.read(source);
    }




    public static void write(Document document,String fileDest) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(
                new FileWriter( fileDest )
        );
        writer.write( document );
        writer.close();
    }
    public static void write(Document document,File fileDest) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(
                new FileWriter( fileDest )
        );
        writer.write( document );
        writer.close();
    }

    public static void writeSout(Document document) throws IOException
    {
        // Compact format to System.out
        OutputFormat format = OutputFormat.createCompactFormat();
        XMLWriter writer = new XMLWriter( System.out, format );
        writer.write( document );
    }

    public static void write(Document document,OutputStream stream) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(stream);
        writer.write( document );
        writer.close();
    }

    public static void write(Element document,OutputStream stream) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(stream);
        writer.write( document );
        writer.close();
    }
    public static void write(Element document,Writer writerW) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(writerW);
        writer.write( document );
        writer.close();
    }

    public static void writeDontCloseStream(Element document,Writer writerW) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(writerW);
        writer.write( document );
    }

    public static void writeDontCloseStream(Element document,OutputStream stream) throws IOException
    {
        // lets write to a file
        XMLWriter writer = new XMLWriter(stream);
        writer.write( document );
    }

    public static org.dom4j.Document convert( org.w3c.dom.Document dom) 
    {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        org.w3c.dom.Document doc1 = builder.newDocument();
//        doc1.appendChild(dom.getFirstChild());
//        // Convert w3c document to dom4j document
        org.dom4j.io.DOMReader reader = new org.dom4j.io.DOMReader();
        return reader.read(dom);
    }


}
