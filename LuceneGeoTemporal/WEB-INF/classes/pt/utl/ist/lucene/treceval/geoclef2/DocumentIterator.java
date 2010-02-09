package pt.utl.ist.lucene.treceval.geoclef2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.apache.solr.handler.XmlUpdateRequestHandler;
import org.python.core.parser;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.XmlUtils;

public class DocumentIterator {
    private static final Logger logger = Logger.getLogger(DocumentIterator.class);
    String dataPath;

    InputStream inputStream;
    BufferedReader reader;
    List<File> files;
    int index = 0;

    public DocumentIterator(String dataPath) throws IOException
    {
        this.dataPath = dataPath;
        init();
    }

    public DocumentIterator(InputStream inputStream) throws IOException
    {
        this.dataPath = null;
        index = 0;
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        files = new ArrayList<File>();//Zero size prepareRead will not try to open a new one
    }


    public void close() throws IOException {
        if(inputStream != null)
            inputStream.close();
    }

    private void init() throws IOException
    {
        files = new ArrayList<File>();
        File d = new File(dataPath);
        if(d.isFile())
            files.add(d);
        else
        {
            File[] filesArray = d.listFiles();
            Arrays.sort(filesArray,new Comparator<File>()
            {
                public int compare(File o1, File o2) {
                    int compare = o1.getName().compareTo(o2.getName());
                    if(compare > 0)
                        return 1;
                    else if(compare < 0)
                        return -1;
                    else
                        return 0;
                }
            });
            for(File f: filesArray)
            {
                if(f.isFile() && (f.getName().endsWith("gz") || f.getName().endsWith(".sgml")))
                    files.add(f);
            }
        }
        prepareRead();
    }

    private void prepareRead() throws IOException
    {
        logger.info("Preparing: " + files.get(index));
        if(inputStream != null)
        {
            inputStream.close();
            reader.close();
            reader = null;
            inputStream = null;
            System.gc();
        }
        if(files.get(index).getName().endsWith(".gz"))
        	inputStream = new GZIPInputStream(new FileInputStream(files.get(index)));
        else
        	inputStream = new FileInputStream(files.get(index));
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public GeoClefDocument next() throws IOException
    {
        String fileName = files.get(index).getName();
        try
        {
        	if(fileName.endsWith(".gz"))
        		return new LaDocument(reader,fileName);
        	else
        		return new GaDocument(reader,fileName);
        }
        catch (EOFException e)
        {
            if((index+1) < files.size())
            {
                index++;
                prepareRead();
                return next();
            }
            else
                return null;
        }
    }
    
    public static void main(String[] args) throws IOException
    {
    	String out = args[0];
    	String data = args[1];    	
    	
    	//String out = "C:\\WORKSPACE_JM\\DATA\\PARSE";
    	//String data = "C:\\WORKSPACE_JM\\DATA\\COLLECTIONS\\GeoCLEF\\en\\";
    	
    	parse(out, data + File.separator + "gh-95");
    	parse(out, data + File.separator + "lat-en");
    }
    
    private static void parse(String outputPath, String dataPath) throws IOException
    {
    	DocumentIterator iter = new DocumentIterator(dataPath);
    	FileOutputStream fos = null;
    	GeoClefDocument doc;
    	String oldFilename = "";
    	while ((doc = iter.next()) != null)
    	{
    		try 
    		{
	            
	    		if(!doc.getFileName().equals(oldFilename))
	    		{
	    			if(fos!= null)
	    			{	
	    				fos.write("\n</docs>\n".getBytes());
	    				fos.close();
	    			}
	    			oldFilename = doc.getFileName();
	    			fos = new FileOutputStream(outputPath + File.separator + doc.getFileName() + ".xml");
	    			fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes());
	    			fos.write("<docs>\n".getBytes());
	    		}
    		
				Document dom = CallWebServices.callServices(doc.getSgmlWithoutTags(),doc.headline,0,0,0,null,doc.getDocNO());
				fos.write(("<doc docno=\"" + doc.getDocNO() + "\">\n").getBytes());
				OutputFormat of = new OutputFormat("XML","UTF-8",true);
                of.setIndent(1);
                of.setOmitXMLDeclaration(true);
                of.setIndenting(true);
                XMLSerializer serializer = new XMLSerializer(fos,of);
                serializer.asDOMSerializer();
                serializer.serialize( dom.getDocumentElement() );
                fos.write("</doc>\n".getBytes());
                
                
                fos.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		//System.out.println(doc.getSgml());
			//System.out.println(doc.getSgmlWithoutTags());
		}
    	fos.write("\n</docs>\n".getBytes());
		fos.close();
    }
}
