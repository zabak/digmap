package pt.utl.ist.lucene.treceval.geotime;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Arrays;

import org.apache.log4j.Logger;

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

    public Document next() throws IOException
    {
        Document d = new Document();
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

    public static void main(String[]args) throws IOException
    {

        String path = args[0];
        String mode = args[1];

//          String path = "D:\\Servidores\\DATA\\ntcir\\nyt_eng_200509.gz";
        /**
         *
         * advis
              nyt_eng   512   81988  142193
            multi
              nyt_eng   124   20435   33183
            other
              nyt_eng   109   16664   25597
            story
              nyt_eng  6304 1069406 1454306
         */
        int count = 0;
        int advis = 0;
        int multi = 0;
        int other = 0;
        int story = 0;
        int unknown = 0;
        int EasternCount = 0;
        int CenturiesAgo = 0;
        int MilleniaAgo = 0;
        int ok = 0;
        int fail = 0;
        String startId = null;
        if(mode.equals("PlaceMaker") && args.length > 2)
            startId = args[2];
        else if(mode.equals("timextag") && args.length > 3)
            startId = args[3];

        DocumentIterator di = new DocumentIterator(path);
        Document d;
        int notesCounter = 1;
        FileOutputStream out;
        String file;
        if(new File(path).isFile())
            file = path + "_notes01.xml";
        else
            file = path + "/notes01.xml";
        out = new FileOutputStream(file,false);

        System.out.println("output file: " + file);
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        out.write("<docs>\n".getBytes());
        
        while((d = di.next()) != null)
        {
            System.out.println(count + ":" + d.getDId());
            logger.warn(count + ":" + d.getDId());
            count++;

            boolean go = false;

            if(MultiDocumentIterator.failIds != null)
            {
                if(MultiDocumentIterator.failIds.contains(d.getDId()))
                    go = true;
            }
            else if( startId==null || d.getDId().compareTo(startId) >= 0)
                go = true;

            if( go )
            {
                if(d.getSgml().toLowerCase().indexOf("Eastern")>0)
                    EasternCount++;
                if(d.getSgml().toLowerCase().indexOf("millennia ago")>0)
                    MilleniaAgo++;
                if(d.getSgml().toLowerCase().indexOf("centuries ago")>0)
                    CenturiesAgo++;

                startId = null;
                if((d.getDParagraphs() == null || d.getDParagraphs().size() == 0) && (d.getDText() == null || d.getDText().trim().length() == 0))
                    d.printOut();
                if(d.getDType().equals("story"))
                    story++;
                else if(d.getDType().equals("advis"))
                    advis++;
                else if(d.getDType().equals("multi"))
                    multi++;
                else if(d.getDType().equals("other"))
                    other++;
                else
                    unknown++;
                logger.debug("Calling with:" + d.toString());
                out.write(("<doc id=\"" + d.getDId() + "\">\n").getBytes());
                try {

                    org.w3c.dom.Document dxml = null;
                    if(mode.equals("PlaceMaker"))
                        dxml = CallWebServices.callServices(d.toString().replace("&AMP;","&amp;"),d.getDHeadline(),d.getArticleYear(),d.getArticleMonth(), d.getArticleDay(),d.getFSourceFile(),d.getDId());
                    else if(mode.equals("timextag"))
                    {
                        String url = args[2];
                        dxml = CallWebServices.callTimextag(url,d.getSgml(),d.getDHeadline(),d.getArticleYear(),d.getArticleMonth(),d.getArticleDay(),d.getFSourceFile(),d.getDId());
                    }
                    ok++;
                    OutputFormat of = new OutputFormat("XML","UTF-8",true);
                    of.setIndent(1);
                    of.setOmitXMLDeclaration(true);
                    of.setIndenting(true);
                    XMLSerializer serializer = new XMLSerializer(out,of);
                    serializer.asDOMSerializer();
                    serializer.serialize( dxml.getDocumentElement() );
                } catch (Throwable e) {
                    logger.error(d.getDId() +  "@" + path +": " + e.toString(),e);
                    fail++;
                }
                out.write(("</doc>\n").getBytes());
            }
            else
            {
                System.out.println("Skiping: " + d.getDId());
            }
        }
        out.write("</docs>\n".getBytes());
        out.flush();
        out.close();

        logger.fatal("******************************************************************************");
        logger.fatal("STATUS FOR FILE: " + file);
        logger.fatal("OK: " + ok);
        logger.fatal("FAIL: " + fail);
        logger.fatal("count: " + count);
        logger.fatal("story: " + story);
        logger.fatal("advis: " + advis);
        logger.fatal("multi: " + multi);
        logger.fatal("other: " + other);
        logger.fatal("unknown: " + unknown);
        logger.fatal("Eastern word: " + EasternCount);
        logger.fatal("millennia ago: " + MilleniaAgo);
        logger.fatal("centuries ago: " + CenturiesAgo);
        logger.fatal("******************************************************************************");
    }
}
