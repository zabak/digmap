package pt.utl.ist.lucene.utils.temporal.tides;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

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

    private TimexesIterator secondaryIterator = null;

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
        if(secondaryIterator != null)
            secondaryIterator.close();
    }

    private void init() throws IOException
    {
        files = new ArrayList<File>();
        File d = new File(dataPath);


        if(d.isFile())
        {
            files.add(d);
            File secondary = new File(d.getParent() + File.separator + "more" + File.separator + d.getName());
            if(secondary.exists())
            {
                secondaryIterator = new TimexesIterator(secondary.getAbsolutePath());
                secondaryDocument = secondaryIterator.next();
            }
        }
        else
        {
            File secondary = new File(dataPath + File.separator + "more");
            if(secondary.exists())
            {
                secondaryIterator = new TimexesIterator(secondary.getAbsolutePath());
                secondaryDocument = secondaryIterator.next();
            }
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
                if(f.isFile() && f.getName().endsWith("notes01.zip"))
                {
                    files.add(f);
                }
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
        inputStream = new ZipInputStream(new FileInputStream(files.get(index)));
        inputStream.getNextEntry();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        readOne();
    }


    TimexesDocument nowDocument = null;
    TimexesDocument secondaryDocument = null;
    String lastDoc = null;
    public TimexesDocument next() throws IOException
    {
        TimexesDocument goingDocument;
        if(lastDoc == null)
        {
            if(secondaryDocument == null || (nowDocument != null && nowDocument.getId().compareTo(secondaryDocument.getId()) < 0))
            {
                goingDocument = nowDocument;
                readOne();
            }
            else
            {
                goingDocument = secondaryDocument;
                secondaryDocument = secondaryIterator.next();
            }
        }
        else
        {
            if(nowDocument != null)
                while(nowDocument != null && nowDocument.getId().compareTo(lastDoc) <= 0
                        /*Nova clausula para coleccoes com IDs nao formatados*/
                        && nowDocument.getId().length() == lastDoc.length())//While is before lets go to get a future document
                    readOne();
            if(secondaryDocument != null)
                while(secondaryDocument != null && secondaryDocument.getId().compareTo(lastDoc) <= 0
                        /*Nova clausula para coleccoes com IDs nao formatados*/
                         && nowDocument.getId().length() == lastDoc.length())//While is before lets go to get a future document
                    secondaryDocument = secondaryIterator.next();
            //Now that both iterators bypass the last document lets keep going
            if(secondaryDocument == null && nowDocument == null)
                return null;
            if(secondaryDocument != null && nowDocument != null && secondaryDocument.getId().equals(nowDocument.getId()))
            {
                if(secondaryDocument.getTimex2TimeExpressions() != null && nowDocument.getTimex2TimeExpressions() == null)
                {
                    goingDocument = secondaryDocument;
                    secondaryDocument = secondaryIterator.next();
                }
                else if(secondaryDocument.getTimex2TimeExpressions() == null && nowDocument.getTimex2TimeExpressions() != null)
                {
                    goingDocument = nowDocument;
                    readOne();
                }
                else if(secondaryDocument.getTimex2TimeExpressions().size() > nowDocument.getTimex2TimeExpressions().size())
                {
                    goingDocument = secondaryDocument;
                    secondaryDocument = secondaryIterator.next();
                }
                else
                {
                    goingDocument = nowDocument;
                    readOne();
                }
            }
            else if(secondaryDocument == null || (nowDocument != null && nowDocument.getId().compareTo(secondaryDocument.getId()) < 0))
            {
                goingDocument = nowDocument;
                readOne();
            }
            else
            {
                goingDocument = secondaryDocument;
                secondaryDocument = secondaryIterator.next();
            }
        }
        lastDoc = goingDocument.getId();
        return goingDocument;
    }

    public void readOne() throws IOException
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
                nowDocument = new TimexesDocument(timexBuilder.toString());
            }
            else
                logger.error("Timex not have end tag: " + timexBuilder.toString());
        }
        else
            nowDocument = null;

//        Line NULL
        if(nowDocument == null  && (index+1) < files.size())
        {
            index++;
            prepareRead();
       //     readOne();
        }
    }

    public static void main(String[]args) throws IOException
    {
        TimexesIterator timexesIterator = new TimexesIterator("D:\\Servidores\\DATA\\ntcir\\TIMEXTAG");
        TimexesDocument timexes;
        String last = "";
        while((timexes = timexesIterator.next())!=null)
        {
            if(timexes.getId().length() == last.length() && timexes.getId().compareTo(last) <= 0 )
                System.out.println("last:" + last + " now: " + timexes.getId());
            last = timexes.getId();
        }

    }
}
