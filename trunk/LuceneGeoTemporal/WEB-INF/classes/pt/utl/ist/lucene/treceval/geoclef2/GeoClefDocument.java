package pt.utl.ist.lucene.treceval.geoclef2;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;

import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;


/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:37:34
 * @email machadofisher@gmail.com
 */
public abstract class GeoClefDocument {


    private static final Logger logger = Logger.getLogger(GaDocument.class);

    StringBuilder sgml = new StringBuilder();
    StringBuilder sgmlClean = new StringBuilder();
    Document dom;
    String headline;
    String text = "";
    protected String year;
    protected String month;
    protected String day;
    String fileName;
    String docNO;
    
    public GeoClefDocument() 
    {
    }

    protected abstract void setDate(String filename);
    

    public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getDocNO() {
		return docNO;
	}

	public void setDocNO(String docNO) {
		this.docNO = docNO;
	}

	public GeoClefDocument(BufferedReader reader, String fileName) throws IOException, EOFException
    {	
		this.fileName = fileName;
    	String line;
    	while((line = reader.readLine()) != null && !line.toUpperCase().equals("<DOC>"));
    	if(line == null)
    		throw new EOFException();
    	
    	appendSgmlLine("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
    	appendSgmlLine(line.replace("&", "&amp;"));
        while((line = reader.readLine()) != null && !line.toUpperCase().equals("</DOC>"))
        {
            appendSgmlLine(line.replace("&", "&amp;"));            
        }
        appendSgmlLine(line.replace("&", "&amp;"));
        try 
        {
			dom = Dom4jUtil.parse(sgml.toString());
			XPath xPathDocNo = dom.createXPath("//DOCNO");
			
			XPath xPathHeadLine;
			if(fileName.endsWith(".gz"))
				xPathHeadLine = dom.createXPath("//HEADLINE/*/text()");
			else
				xPathHeadLine = dom.createXPath("//HEADLINE/text()");
			
			XPath xPathText;
			if(fileName.endsWith(".gz"))
				xPathText = dom.createXPath("//TEXT/*/text()");
			else
				xPathText = dom.createXPath("//TEXT/text()");
			
			
			docNO = ((Element)xPathDocNo.selectSingleNode(dom)).getTextTrim();
			Node headLineElem = xPathHeadLine.selectSingleNode(dom);
			List<Node> textElems= (List<Node>) xPathText.selectNodes(dom);
			if(headLineElem != null && headLineElem.getText() != null && headLineElem.getText().trim().length() > 0)
				headline = headLineElem.getText().trim();
			for(Node textElem: textElems)
			{
				if(textElem.getText() != null && textElem.getText().trim().length() > 0)
					text += " " + textElem.getText();
			}
			
			setDate(fileName);
			
			sgmlClean
			.append("<DOC docno=\"" + docNO + "\">\n")
			.append("<DATA_TIME>" + year + "-" + month + "-" + day + "</DATE_TIME>\n")
			.append("<HEADLINE>\n")
			.append(headline)
			.append("\n</HEADLINE>\n")
			.append("<TEXT>\n")
			.append(text)
			.append("\n</TEXT>\n</DOC>\n");
		} 
        catch (DocumentException e) 
		{ 
			logger.error(e,e);
		}
    }

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSgmlWithoutTags()
    {
        return sgmlClean.toString().replaceAll("<[^>]+>","");
    }

    public String getSgml()
    {
        return sgmlClean.toString();
    }

    public void appendSgmlLine(String line)
    {
        sgml.append(line).append("\n");
    } 
    
    public String toString()
    {
    	return "####################\n" + 
    	"DOC:" + docNO + "\n"+
    	"HEADLINE: " + headline + "\n" +
    	"TEXT: " + text + "\n";
    }
}
