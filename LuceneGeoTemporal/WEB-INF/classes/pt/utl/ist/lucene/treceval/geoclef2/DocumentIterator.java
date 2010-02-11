package pt.utl.ist.lucene.treceval.geoclef2;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class DocumentIterator {
    private static final Logger logger = Logger.getLogger(DocumentIterator.class);
    String dataPath;

    InputStream inputStream;
    BufferedReader reader;
    List<File> files;
    int index = 0;
    String startFile;
    public DocumentIterator(String dataPath, String startFile) throws IOException
    {
        this.startFile = startFile;
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
                if(f.getName().compareTo(startFile) >= 0)
                {
                    if(f.isFile() && (f.getName().endsWith("gz") || f.getName().endsWith(".sgml")))
                        files.add(f);
                }
            }
        }
        if(files.size() > 0)
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
        if(files.size() == 0)
            return null;
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
//    	String out = args[0];
//    	String data = args[1];
        String out = "C:\\Servidores\\DATA\\COLLECTIONS\\geoclef\\PlaceMaker";
        String data = "C:\\Servidores\\DATA\\COLLECTIONS\\geoclef\\en";

        String startFileGh = "";
        String startFileLat = "";
        if(args.length >= 3)
            startFileGh = args[2];
        if(args.length >= 4)
            startFileLat = args[3];
        new File(out).mkdir();

        parse(out, data, startFileGh);
//        parse(out, data + File.separator + "lat-en", startFileLat);
    }

    private static void parse(String outputPath, String dataPath, String startFile) throws IOException
    {
        DocumentIterator iter = new DocumentIterator(dataPath,startFile);
        FileOutputStream fos = null;
        GeoClefDocument doc;
        String oldFilename = "";
        while ((doc = iter.next()) != null)
        {
//            try {
//                Thread.sleep(Math.round(2000 * Math.random()));
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
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
        if(fos != null)
        {
            fos.write("\n</docs>\n".getBytes());
            fos.close();
        }
    }
}
