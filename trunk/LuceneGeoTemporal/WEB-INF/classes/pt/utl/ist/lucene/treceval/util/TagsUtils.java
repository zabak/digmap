package pt.utl.ist.lucene.treceval.util;

import org.dom4j.Element;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 18/Jun/2008
 * @time 14:56:06
 * @see pt.utl.ist.lucene.treceval.util
 */
public class TagsUtils
{

    private static final Logger logger = Logger.getLogger(TagsUtils.class);

    private static final String EMPTY = "";

    public static String getMultipleElementsText(Element e, String tag)
    {
        StringBuilder result = new StringBuilder();
        Iterator iter = e.elementIterator(tag);
        while(iter.hasNext())
        {
            result
                    .append(' ')
                    .append(((Element)iter.next()).getStringValue());
        }
        return result.toString();
    }

    public static String parseDate(Element e, String tag)
    {
        if(e.element(tag) == null)
            return EMPTY;
        String date = e.element(tag).getStringValue().replace('\n',' ').replace('\r',' ').replace('\t',' ').trim();
        String year;
        if(date.length() == 6)
            year = date.substring(0,2);
        else
            year = date.substring(2,4);

        int yearInt = Integer.parseInt(year);
        String yearStr = "";
        if(yearInt < 2000)
            yearStr = "19" + year + " " + year;
        else
            yearStr = "20" + year + " " + year;

        return parseMonth(date) + " " + yearStr;

    }
    public static String parseDatePt(Element e, String tag)
    {
        if(e.element(tag) == null)
            return EMPTY;
        String date = e.element(tag).getStringValue().replace('\n',' ').replace('\r',' ').replace('\t',' ').trim();
        String year;
        if(date.length() == 6)
            year = date.substring(0,2);
        else
            year = date.substring(2,4);
        int yearInt = Integer.parseInt(year);
        String yearStr = "";
        if(yearInt < 2000)
            yearStr = "19" + year + " " + year;
        else
            yearStr = "20" + year + " " + year;

        return parseMonthPt(date) + " " + yearStr;

    }
    public static String parseMonth(String date)
    {
        String month;
        if(date.length() == 6)
            month = date.substring(2,4);
        else
            month = date.substring(4,6);
        if(month.equals("01"))
            return "January";
        else if(month.equals("02"))
            return "February";
        else if(month.equals("03"))
            return "March";
        else if(month.equals("04"))
            return "April";
        else if(month.equals("05"))
            return "May";
        else if(month.equals("06"))
            return "June";
        else if(month.equals("07"))
            return "July";
        else if(month.equals("08"))
            return "August";
        else if(month.equals("09"))
            return "September";
        else if(month.equals("10"))
            return "October";
        else if(month.equals("11"))
            return "November";
        else  if(month.equals("12"))
            return "December";
        else
            return "";


    }
    public static String parseMonthPt(String date)
    {
        String month;
        if(date.length() == 6)
            month = date.substring(2,4);
        else
            month = date.substring(4,6);
        if(month.equals("01"))
            return "Janeiro";
        else if(month.equals("02"))
            return "Fevereiro";
        else if(month.equals("03"))
            return "Marco";
        else if(month.equals("04"))
            return "Abril";
        else if(month.equals("05"))
            return "Maio";
        else if(month.equals("06"))
            return "Junho";
        else if(month.equals("07"))
            return "Julho";
        else if(month.equals("08"))
            return "Agosto";
        else if(month.equals("09"))
            return "Setembro";
        else if(month.equals("10"))
            return "Outubro";
        else if(month.equals("11"))
            return "Novembro";
        else  if(month.equals("12"))
            return "Dezembro";
        else
            return "";


    }


    public static FirstSecondLines readFirstSecondLinesSlashN(Element e, String tag)
    {
        if(e.element(tag) == null)
            return new FirstSecondLines();
        String text = e.element(tag).getStringValue();
        BufferedReader bf = new BufferedReader(new StringReader(text));
        String firstLine = null;
        String secondLine = null;
        String line;

        try
        {
            while((line = bf.readLine())!=null && line.trim().length() == 0);

            if(line !=null)
            {
                firstLine = line;

                while((line = bf.readLine())!=null && line.trim().length() == 0);

                if(line != null)
                {
                    secondLine = line;
                }
            }
        }
        catch (IOException e1)
        {
            logger.error(e1,e1);
        }
        return new FirstSecondLines(firstLine,secondLine,text);

    }

    public static FirstSecondLines readFirstSecondLinesFirstDot(Element e, String tag)
    {
        if(e.element(tag) == null)
            return new FirstSecondLines();
        String text = e.element(tag).getStringValue();

        String secondLine = null;
        String firstLine;
        firstLine = getFisrtLineOrSlashN(text);
        return new FirstSecondLines(firstLine,secondLine,text);
    }


    public static FirstSecondLines readFirstSecondLinesSubelements(Element e, String tag, String subTag)
    {
        if(e.element(tag) == null)
            return new FirstSecondLines();
        Iterator lines = e.element(tag).elementIterator(subTag);

        String firstLine = null;
        String secondLine = null;

        if(lines.hasNext())
        {
            firstLine = ((Element)lines.next()).getStringValue();
            if(lines.hasNext())
            {
                secondLine = ((Element)lines.next()).getStringValue();
            }
        }
        return new FirstSecondLines(firstLine,secondLine, e.element(tag).getStringValue());
    }
    public static FirstSecondLines readFirstSecondLinesSubelementsFirstDot(Element e, String tag, String subTag)
    {
        if(e.element(tag) == null)
            return new FirstSecondLines();
        Iterator lines = e.element(tag).elementIterator(subTag);

        String firstLine = null;
        String secondLine = null;

        if(lines.hasNext())
        {
            firstLine = ((Element)lines.next()).getStringValue();
            firstLine = getFisrtLineOrSlashN(firstLine);
        }
        return new FirstSecondLines(firstLine,secondLine, e.element(tag).getStringValue());
    }




    public static class FirstSecondLines
    {

        String firstLine;
        String secondLine;
        String allText;


        public FirstSecondLines()
        {

        }
        public FirstSecondLines(String firstLine, String secondLine, String allText)
        {
            this.firstLine = firstLine;
            this.secondLine = secondLine;
            this.allText = allText;
        }


        public String getFirstLine()
        {
            if(firstLine == null)
                return EMPTY;
            return firstLine;
        }

        public String getSecondLine()
        {
            if(secondLine == null)
                return EMPTY;
            return secondLine;
        }


        public String getAllText()
        {
            if(allText == null)
                return EMPTY;
            return allText;
        }
    }


    public static String getFisrtLineOrSlashN(String firstLine)
    {
        BufferedReader bf = new BufferedReader(new StringReader(firstLine));
        try
        {   String line;
            while((line = bf.readLine()) != null && line.trim().length() == 0)
            {
                System.out.println(line);
            }
            firstLine = line + "\n";
            while((line = bf.readLine())!=null)
            {
                firstLine += line + "\n";
            }
        }
        catch (IOException e1)
        {
            logger.error(e1,e1);
        }
        int dot = firstLine.indexOf('.');
        if(dot < 0)
            dot = 0;
        int n = firstLine.indexOf('\n');
        if(dot == 0 || (n < dot && n > 0))
            dot = n;
        if(dot > 0)
            firstLine = firstLine.substring(0,dot);
        return firstLine;
    }
    public static void main(String[] args)
    {
        String firstLine = "\n  \n \nola primeira linha wf werw er wer w er werwer\nsegunda olinha";

        System.out.println(getFisrtLineOrSlashN("\n \t  \n \nola primeira linha wf werw er wer w er werwer\nsegunda olinha"));
        System.out.println(getFisrtLineOrSlashN("\n \t \n \nola primeira. linha wf werw er wer w er werwer\nsegunda olinha"));
        System.out.println(getFisrtLineOrSlashN("\n \r teste, teste2. \n \nola primeira. linha wf werw er wer w er werwer\nsegunda olinha"));
        System.out.println(getFisrtLineOrSlashN("\n \t\r teste teste2  \n \nola primeira. linha wf werw er wer w er werwer\nsegunda olinha"));
        System.out.println(getFisrtLineOrSlashN("\n  teste. teste2\n \nola primeira. linha wf werw er wer w er werwer\nsegunda olinha"));
    }
}
