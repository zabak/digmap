package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
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

    GZIPInputStream inputStream;
    BufferedReader reader;
    List<File> files;
    int index = 0;

    public DocumentIterator(String dataPath) throws IOException
    {
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
        NyTimesDocument d = new NyTimesDocument();
        d.setDParagraphs(new ArrayList<String>());
        String line;
        boolean inHeadLine = false;
        boolean inDateLine = false;
        boolean inP = false;
        boolean inText = false;
        String auxP = "";
        String auxText = "";
        String fileName = files.get(index).getName();
        d.setFSourceFile(fileName);
        int i_ = fileName.lastIndexOf("_")+1;
        d.setFDateYearMonthSort(fileName.substring(i_,fileName.lastIndexOf(".")));
        while((line = reader.readLine())!=null && !line.toUpperCase().equals("</DOC>"))
        {
            d.appendSgmlLine(line);
            if(line.startsWith(("<DOC")))
            {

                int iid  = line.indexOf("id=\"") + 4;
                d.setDId(line.substring(iid,line.indexOf("\"",iid)));

                d.setPArticleNumber(d.getDId().substring(d.getDId().lastIndexOf(".")+1));

                int liid_ = d.getDId().lastIndexOf("_")+1;
                d.setPDateYearMonthDaySort(d.getDId().substring(liid_,d.getDId().lastIndexOf(".")));

                d.setArticleYear(Integer.parseInt(d.getPDateYearMonthDaySort().substring(0,4)));
                d.setArticleMonth(Integer.parseInt(d.getPDateYearMonthDaySort().substring(4,6)));
                d.setArticleDay(Integer.parseInt(d.getPDateYearMonthDaySort().substring(6)));
                d.appendSgmlLine("<DATE_TIME>" + d.getArticleYear() + "-" +d.getArticleMonth() + "-" + d.getArticleDay() + "</DATE_TIME>");
                GregorianCalendar c = new GregorianCalendar(d.getArticleYear(),d.getArticleMonth()-1,d.getArticleDay());
                d.setPDate(c.getTime());

                int itype  = line.indexOf("type=\"") + 6;
                d.setDType(line.substring(itype,line.indexOf("\"",itype)));
            }
            else if(line.startsWith(("<HEADLINE")))
            {
                inHeadLine = true;
            }
            else if(line.startsWith(("</HEADLINE")))
            {
                inHeadLine = false;
                d.setDHeadline(auxText);
                auxText = "";
            }
            else if(inHeadLine)
            {
                auxText += " " + line;
            }
            else if(line.startsWith(("<DATELINE")))
            {
                inDateLine = true;
            }
            else if(line.startsWith(("</DATELINE")))
            {
                inDateLine = false;
                d.setDDateline(auxText);
                auxText = "";
            }
            else if(inDateLine)
            {
                auxText += " " + line;
            }
            else if(line.startsWith(("<P>")))
            {
                inP = true;
            }
            else if(line.startsWith(("</P>")))
            {
                inP = false;
                d.getDParagraphs().add(auxP);
                auxP = "";
            }
            else if(inP)
            {
                auxP += " " + line;
            }
            else if(line.startsWith(("<TEXT>")))
            {
                inText = true;
            }
            else if(line.startsWith(("</TEXT>")))
            {
                inText = false;
                d.setDText(auxText);
                auxText = "";
            }
            else if(inText)
            {
                auxText += " " + line;
            }
        }
        d.appendSgmlLine(line);
//        System.out.println(d.getSgml());
        if(line == null && (index+1) < files.size())
        {
            index++;
            prepareRead();
            return next();
        }
        else if(line == null)
            return null;


        return d;
    }

   
}
