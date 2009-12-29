package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;

import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.io.*;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import pt.utl.ist.lucene.utils.temporal.TimexesDocument;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:36:56
 * @email machadofisher@gmail.com
 */
public class TimexesIterator
{
    private static final Logger logger = Logger.getLogger(TimexesIterator.class);
    String dataPath;

    ZipInputStream inputStream;
    BufferedReader reader;
    List<File> files;
    int index = 0;

    public TimexesIterator(String dataPath) throws IOException {
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
            for(File f: d.listFiles())
            {
                if(f.isFile() && f.getName().endsWith("notes01.zip"))
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
        }
        inputStream = new ZipInputStream(new FileInputStream(files.get(index)));
        inputStream.getNextEntry();
        reader = new BufferedReader(new InputStreamReader(inputStream));

    }

    public TimexesDocument next() throws IOException
    {
        StringBuilder timexBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine())!=null && !line.startsWith("<doc id="));
        if(line != null)
        {
            timexBuilder.append(line).append("\n");
            while((line = reader.readLine())!=null && !line.equals("</doc>"))
            {
                timexBuilder.append(line).append("\n");
            }
            if(line != null)
            {
                timexBuilder.append(line).append("\n");
                return new TimexesDocument(timexBuilder.toString());
            }
            else
                logger.error("Timex not have end tag: " + timexBuilder.toString());
        }

//        Line NULL
        if((index+1) < files.size())
        {
            index++;
            prepareRead();
            return next();
        }
        else return null;
    }

    public static void main(String[]args) throws IOException
    {
        TimexesIterator timexesIterator = new TimexesIterator("D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste");
        TimexesDocument timexes;
        while((timexes = timexesIterator.next())!=null)
        {
            System.out.println(timexes);
        }

    }
}
