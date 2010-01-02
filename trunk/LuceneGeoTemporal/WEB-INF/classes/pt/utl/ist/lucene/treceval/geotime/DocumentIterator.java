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
        int headLineStartOffset = 0;
        int headLineEndOffset = 0;
        int dateLineStartOffSet = 0;
        int dateLineEndOffSet = 0;


        int pos = 0;

        NyTimesDocument d = new NyTimesDocument();
        d.setDParagraphs(new ArrayList<NyTimesDocument.TextFragment>());
        String line;
        boolean inHeadLine = false;
        boolean inDateLine = false;
        boolean inP = false;
        boolean inText = false;
        String auxP = "";
        String auxText = "";
        NyTimesDocument.TextFragment inParagrahp = null;
        NyTimesDocument.TextFragment inTextFrag = null;

        String fileName = files.get(index).getName();
        d.setFSourceFile(fileName);
        int i_ = fileName.lastIndexOf("_")+1;
        d.setFDateYearMonthSort(fileName.substring(i_,fileName.lastIndexOf(".")));
        boolean first = true;
        while((line = reader.readLine())!=null && !line.toUpperCase().equals("</DOC>"))
        {
            if(first)
            {
                first = false;
            }
            else
            {
                pos++;
            }
            d.appendSgmlLine(line);
            if(line.startsWith(("<DOC")))
            {
                pos += line.substring(line.indexOf(">") + 1).length();
                int iid  = line.indexOf("id=\"") + 4;
                d.setDId(line.substring(iid,line.indexOf("\"",iid)));

                d.setPArticleNumber(d.getDId().substring(d.getDId().lastIndexOf(".")+1));

                int liid_ = d.getDId().lastIndexOf("_")+1;
                d.setPDateYearMonthDaySort(d.getDId().substring(liid_,d.getDId().lastIndexOf(".")));

                d.setArticleYear(Integer.parseInt(d.getPDateYearMonthDaySort().substring(0,4)));
                d.setArticleMonth(Integer.parseInt(d.getPDateYearMonthDaySort().substring(4,6)));
                d.setArticleDay(Integer.parseInt(d.getPDateYearMonthDaySort().substring(6)));
                pos++;
                String datetime = d.getArticleYear() + "-" +d.getArticleMonth() + "-" + d.getArticleDay();
                d.appendSgmlLine("<DATE_TIME>" + datetime + "</DATE_TIME>");
                pos+=datetime.length();

                GregorianCalendar c = new GregorianCalendar(d.getArticleYear(),d.getArticleMonth()-1,d.getArticleDay());
                d.setPDate(c.getTime());

                int itype  = line.indexOf("type=\"") + 6;
                d.setDType(line.substring(itype,line.indexOf("\"",itype)));
            }
            else if(line.startsWith(("<HEADLINE>")))
            {
                pos += line.substring("<HEADLINE>".length()).length();
                headLineStartOffset = pos;
                headLineEndOffset = pos;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
                inHeadLine = true;
            }
            else if(line.startsWith(("</HEADLINE>")))
            {
                pos += line.substring("</HEADLINE>".length()).length();
                inHeadLine = false;
                d.setDHeadline(auxText);
                headLineEndOffset = pos;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
                auxText = "";
            }
            else if(inHeadLine)
            {
                auxText += " " + line;
                pos += line.length();
            }
            else if(line.startsWith(("<DATELINE>")))
            {
                pos += line.substring("<DATELINE>".length()).length();
                inDateLine = true;
                dateLineStartOffSet = pos;
                dateLineEndOffSet = pos;
            }
            else if(line.startsWith(("</DATELINE>")))
            {
                dateLineEndOffSet = pos;
                pos += line.substring("</DATELINE>".length()).length();
                inDateLine = false;
                d.setDDateline(auxText);
                auxText = "";
            }
            else if(inDateLine)
            {
                pos += line.length(); //dont use the " " because was introduced by this methos is not original
                auxText += " " + line;
            }
            else if(line.startsWith(("<P>")))
            {
                pos += line.substring("<P>".length()).length();
                inParagrahp = new NyTimesDocument.TextFragment(pos);
                inP = true;
            }
            else if(line.startsWith(("</P>")))
            {
                pos += line.substring("</P>".length()).length();
                inParagrahp.setEndOffset(pos);
                inParagrahp.setP(auxP);

                inP = false;
                d.getParagraphs().add(inParagrahp);
                auxP = "";
                inParagrahp = null;
            }
            else if(inP)
            {
                pos += line.length();
                auxP += " " + line;
            }
            else if(line.startsWith(("<TEXT>")))
            {
                pos += line.substring("<TEXT>".length()).length();
                inTextFrag = new NyTimesDocument.TextFragment(pos);
                inText = true;
            }
            else if(line.startsWith(("</TEXT>")))
            {
                inTextFrag.setEndOffset(pos);
                inTextFrag.setP(auxText);

                inText = false;
                d.setDText(inTextFrag);
                auxText = "";
                inTextFrag = null;
            }
            else if(inText)
            {
                pos += line.length();
                auxText += " " + line;
            }
            else
            {
                pos += line.length();
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


        d.setHeadLineStartOffset(headLineStartOffset);
        d.setHeadLineEndOffset(headLineEndOffset);
        d.setDateLineStartOffSet(dateLineStartOffSet);
        d.setDateLineEndOffSet(dateLineEndOffSet);

        return d;
    }


}
