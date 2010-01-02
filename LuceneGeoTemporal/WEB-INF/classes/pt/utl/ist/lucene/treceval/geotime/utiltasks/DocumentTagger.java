package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;

import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;
import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Jorge Machado
 * @date 31/Dez/2009
 * @time 13:27:29
 * @email machadofisher@gmail.com
 */
public class DocumentTagger
{
    private static final Logger logger = Logger.getLogger(DocumentTagger.class);
    
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
        NyTimesDocument d;
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

            if(MultiDocumentTagger.failIds != null)
            {
                if(MultiDocumentTagger.failIds.contains(d.getDId()))
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
                if((d.getDParagraphs() == null || d.getDParagraphs().size() == 0) && (d.getDText() == null || d.getDText().getP().trim().length() == 0))
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
