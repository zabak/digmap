package pt.utl.ist.lucene.treceval.geotime;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 6/Jul/2011
 * Time: 10:59:54
 * To change this template use File | Settings | File Templates.
 */
public class KoreaTimesDocument extends  NyTimesDocument
{

    String dateFinal = "";

    public KoreaTimesDocument(BufferedReader reader, String fileName) throws IOException, EOFException
    {

        String line;

        setFSourceFile(fileName);

        boolean first = true;

        String auxText = "";
        boolean inText = false;

        while((line = reader.readLine()) != null && !line.toUpperCase().equals("</DOC>"))
        {
            boolean appendLine = true;

            if(line.startsWith("<!-"))
            {
                String line2 = "";
                for(int i = 0; i < line.length();i++)
                    line2+=" ";
                line = line2;
            }
            else if(line.startsWith(("<DOCNO>")))
            {
                String docno = line.substring("<DOCNO>".length(),line.lastIndexOf("</DOCNO>"));
                setDId(docno);
                setDType("other");
            }
            else if(line.startsWith("<HEADLINE>"))
            {
                setDHeadline(line.substring("<HEADLINE>".length(),line.lastIndexOf("</HEADLINE>")));
            }
            else if(line.startsWith("<DATE>"))
            {
                String date = line.substring("<DATE>".length(),line.lastIndexOf("</DATE>"));

                for(int i = 0; i < date.length();i++)
                {
                    char c  =date.charAt(i);
                    if(c >= '0' && c <= '9')
                        dateFinal += "" + c;
                }
                if(dateFinal.length()==6)
                {
                    dateFinal += "01";
                    appendLine = false;
                    appendSgmlLine("<DATE>"+dateFinal+"</DATE>");
                }
                setPDateYearMonthDaySort(dateFinal);
                setArticleYear(Integer.parseInt(getPDateYearMonthDaySort().substring(0,4)));
                setArticleMonth(Integer.parseInt(getPDateYearMonthDaySort().substring(4,6)));
                setArticleDay(Integer.parseInt(getPDateYearMonthDaySort().substring(6)));
                GregorianCalendar c = new GregorianCalendar(getArticleYear(),getArticleMonth()-1,getArticleDay());
                setPDate(c.getTime());
            }
            if(appendLine)
                appendSgmlLine(line);


            if(line.startsWith("<TEXT>"))
            {
                inText = true;
                auxText = "";
            }
            else if(line.startsWith(("</TEXT>")))
            {
                inText = false;
                setDText(new TextFragment(auxText,0,0));
            }
            else if(inText)
            {
                auxText += " " + line;
            }
        }
        if(line == null)
        {
            throw new EOFException();
        }
        else if(line == null)
            sgmlValid = false;
        else
            appendSgmlLine(line);


//
//        toString(); //to fill offsets
    }

    public int toStringOffset2txtwithoutTagsOffset(int offset)
    {
        return offset;
    }


    public int compare(String id)
    {
        
        if(getDId().indexOf("-")>=0)
        {
        return Integer.parseInt(getDId().substring(getDId().indexOf("-")+1)) -
                Integer.parseInt(id.substring(id.indexOf("-")+1));
        }
        else
            return Integer.parseInt(getDId().substring(getDId().indexOf("_")+1)) -
                Integer.parseInt(id.substring(id.indexOf("_")+1));
    }

    public boolean isBiggerThan(NyTimesDocument anotherDoc)
    {
        return getFSourceFile().compareTo(anotherDoc.getFSourceFile()) > 0
                ||
                compare(anotherDoc.getDId())>0;
    }


}
