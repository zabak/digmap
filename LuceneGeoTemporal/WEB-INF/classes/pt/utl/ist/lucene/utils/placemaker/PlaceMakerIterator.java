package pt.utl.ist.lucene.utils.placemaker;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.io.*;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:36:56
 * @email machadofisher@gmail.com
 */
public class PlaceMakerIterator {
    private static final Logger logger = Logger.getLogger(PlaceMakerIterator.class);
    String dataPath;

    ZipInputStream inputStream;
    BufferedReader reader;
    List<File> files;
    int index = 0;
    String docIdXPath = null;

    public PlaceMakerIterator(String dataPath, String docIdXPath) throws IOException
    {
        this.docIdXPath = docIdXPath;
        this.dataPath = dataPath;
        init();
    }

    public PlaceMakerIterator(String dataPath) throws IOException {
        this.dataPath = dataPath;
        init();
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
                if(f.isFile() && f.getName().endsWith(".zip"))
                    files.add(f);
            }
        }
        System.out.println("\nFile: " + files.get(index).getName());
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
        inputStream = new ZipInputStream(new FileInputStream(files.get(index)));

        ZipEntry  z = inputStream.getNextEntry();
        System.out.println("\nZipEntry: " + z.getName());
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    String restLine = null;
    public PlaceMakerDocument next() throws IOException, DocumentException {
        String line;

        StringBuilder builder = new StringBuilder();

        while((line = reader.readLine())!=null && line.toLowerCase().indexOf("<doc ")<0);
        if(line != null)
        {
            builder.append(line);
            while((line = reader.readLine())!=null && line.toLowerCase().indexOf("</doc>")<0)
                builder.append(line);
            if(line == null)
                logger.error("Found null line before </doc>: " + builder.toString());
            else
            {
                builder.append(line.substring(0,line.indexOf("</doc>")+6));
                return new PlaceMakerDocument(builder.toString(),docIdXPath);
            }
        }


        ZipEntry  z = inputStream.getNextEntry();
        if(z != null)
        {
            System.out.println("\nZipEntry: " + z.getName());
            next();
        }
        else if((index+1) < files.size())
        {
            index++;
            System.out.println("\nFile: " + files.get(index).getName());
            prepareRead();
            return next();
        }


        return null;
    }



    



}
