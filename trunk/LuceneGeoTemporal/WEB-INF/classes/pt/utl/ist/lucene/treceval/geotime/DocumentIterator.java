package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:36:56
 * @email machadofisher@gmail.com
 */
public class DocumentIterator
{
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
                if(f.isFile() && f.getName().endsWith("gz"))
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
        inputStream = new GZIPInputStream(new FileInputStream(files.get(index)));
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public NyTimesDocument next() throws IOException
    {
        String fileName = files.get(index).getName();
        try
        {
            return new NyTimesDocument(reader,fileName);
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
}
