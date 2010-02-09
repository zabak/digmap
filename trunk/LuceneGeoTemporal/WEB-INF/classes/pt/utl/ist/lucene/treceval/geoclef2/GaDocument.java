package pt.utl.ist.lucene.treceval.geoclef2;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
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
public class GaDocument extends GeoClefDocument{


    private static final Logger logger = Logger.getLogger(GaDocument.class);

    public GaDocument(BufferedReader reader, String fileName) throws IOException, EOFException
    {
    	super(reader,fileName); 
    }

	@Override
	protected void setDate(String filename) {
		year = "1995";
		month = filename.substring(2 , 4);
		day = filename.substring(4 , 6);
	}


			
			
		
}
