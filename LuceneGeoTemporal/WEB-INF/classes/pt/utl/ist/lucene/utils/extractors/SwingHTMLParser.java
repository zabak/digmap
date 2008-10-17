package pt.utl.ist.lucene.utils.extractors;


import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.ChangedCharSetException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.utils.Files;

/**
 * Created by IntelliJ IDEA.
 * Creator: Jorge Machado
 * Email: jmachado@ext.bn.pt
 * User: Utilizador
 * DateMonthYearArticles: 19/Set/2005
 * Time: 13:51:53
 * To change this template use File | Settings | File Templates.
 */
public class SwingHTMLParser extends HTMLEditorKit implements Extractor
{

    private static final Logger logger = Logger.getLogger(SwingHTMLParser.class);

    private ParserCallBack parserCallback = null;
    HashSet metaTags = new HashSet();
    File file;

    public SwingHTMLParser(File file) throws IOException
    {
        this.file = file;
        parseFile();
    }

    public SwingHTMLParser()
    {
    }

    public void init(File file) throws IOException
    {
        this.file = file;
        parseFile();
    }

    public SwingHTMLParser(String html) throws IOException
    {
        parseHtml(html);
    }

    private void parseFile() throws IOException
    {
        String html = null;
        try
        {
            html = Files.getText(file, "iso-8859-1");
        }
        catch (IOException e)
        {
            logger.error(e);
            throw e;
        }
        HTMLEditorKit.Parser parse = getParser();
        parserCallback = new ParserCallBack(null);
        StringReader r = new StringReader(html);
        try
        {
            parse.parse(r, parserCallback, false);
            r.close();
        }
        catch (ChangedCharSetException e)
        {
            r.close();
            String charset = e.getCharSetSpec();
            String enc = getEncodingFromContentType(charset);
            if (enc.toLowerCase().compareTo("iso-8859-1") != 0)
            {
                try
                {
                    html = Files.getMaxText(file, Integer.MAX_VALUE, enc);
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                parserCallback.clear();
            }
            r = new StringReader(html);

            parse.parse(r, parserCallback, true);
            r.close();
        }
        r = null;
    }

    private String getEncodingFromContentType(String contentType)
    {
        String enc = "iso-8859-1";
        int indexOfCharSet = contentType.indexOf("charset=");
        if (indexOfCharSet > 0)
        {
            enc = contentType.substring(indexOfCharSet + "charset=".length());
            try
            {
                new String("".getBytes(), enc);
                System.out.println("Using: " + enc);
            }
            catch (UnsupportedEncodingException ue)
            {
                System.out.println("ERROR: parsing html in SwingHTMLEditor:" + ue);
                enc = "iso-8859-1";
            }
        }
        return enc;
    }

    private void parseHtml(String html) throws IOException
    {
        HTMLEditorKit.Parser parse = getParser();
        parserCallback = new ParserCallBack(null);
        StringReader r = new StringReader(html);
        parse.parse(r, parserCallback, true);
        r.close();
    }


    public HTMLEditorKit.Parser getParser()
    {
        return super.getParser();
    }

    public ParserCallback getCallBack()
    {
        return new ParserCallback();
    }


    public String getComments()
    {
        return parserCallback.getComments();
    }

    public String getText()
    {
        return parserCallback.getText();
    }

    public String getTitle()
    {
        return parserCallback.getTitle();
    }

    public String getAnchor()
    {
        return parserCallback.getAnchor();
    }

    public String getH()
    {
        return parserCallback.getH();
    }

    public String getH1()
    {
        return parserCallback.getH1();
    }

    public String getH2()
    {
        return parserCallback.getH2();
    }

    public String getH3()
    {
        return parserCallback.getH3();
    }

    public String getH4()
    {
        return parserCallback.getH4();
    }

    public String getH5()
    {
        return parserCallback.getH5();
    }

    public String getH6()
    {
        return parserCallback.getH6();
    }

    public String getH7()
    {
        return parserCallback.getH7();
    }

    public String getAuthor()
    {
        return parserCallback.getAuthor();
    }

    public String getAbstract()
    {
        return parserCallback.getAbstract();
    }

    public String getKeywords()
    {
        return parserCallback.getKeywords();
    }

    public String getDescription()
    {
        return parserCallback.getDescription();
    }

    public String getScript()
    {
        return parserCallback.getScript();
    }

    public Set getOutLinks(String urlStr)
    {
        return parserCallback.getOutLinks(urlStr);
    }

    public List getImages(String urlStr)
    {
        return parserCallback.getImages(urlStr);
    }

    public String getBannedTextEnd()
    {
        return parserCallback.getBannedTextEnd();
    }

    public String getBannedTextStart()
    {
        return parserCallback.getBannedTextStart();
    }


    public static void main(String[] args)
    {
        String html = "<html><HEAD>\n" +
                "<meta http-equiv=\"Title\" content=\"title2\">" +
                "<TITLE>Stamp Collecting World</TITLE>\n" +
                "<meta name=\"Description\" Content=\"Hello World Desc\">\n" +
                "<meta name=\"mitra-index\" Content=\"false\">\n" +
                "<META name=\"Keywords\" content=\"hello, world\">\n" +
                "<META name=\"author\" content=\"JORGE MACHADO\">\n" +
                "<Script>java</script>" +
                "<META name=\"abstract\" content=\"abstract\">\n" +
                "" +
                "</HEAD>\n" +
                "<body bgcolor=\"#FFFFFF\"><script>A imagem Link é um horror<a href='http://deptal.bn.pt:80/122343/index.html'><img src=\"imgs/imgLink.gif\"></a> <a href='http://deptal.bn.pt:80/122343/index.html'> a </a><!--document.write('JAVASCRIPT');--></script>Please watch <!--This is a comment-->this:\nHello é World</p><a href='/122343/index.html?sdfsdf'>ola</a><a href='122343/index.html?jorge=ola'>ola</a>Este é o texto perto da imagem número 2<img src=\"noticias_files/2003/jorgemachado.jpg\"/>texto depois da dois é este texto aqui</body></html>";


        html = "<html>\n" +
                "<body>pesca desportiva de competição <img src=\"noticias_files/2003/jorgemachado.jpg\"/> para todos os gostos e feitios</body></html>";

        html = "<html><head><title>teste XHTML 1</title></head>\n" +
                "<body>pesca desportiva de competição <img src=\"noticias_files/2003/jorgemachado.jpg\" / >  para todos os gostos e feitios</body></html>";
        //File f = new File("c:\\htmlEditorTeste.html");
        try
        {
            SwingHTMLParser parser = new SwingHTMLParser(html);
            System.out.println(parser.getText());
//           System.out.println("Author:" + parser.getAuthor());
//           System.out.println("Comments:" + parser.getComments());
//           System.out.println("Desc:" + parser.getDescription());
//           System.out.println("Abstract:" + parser.getAbstract());
//           System.out.println("Keywords:" + parser.getKeywords());
//           System.out.println("Title:" + parser.getTitle());
//           System.out.println("Script:" + parser.getScript());
//           System.out.println("text:" + parser.getText());
//           System.out.print("url:" + parser.getOutLinks("dited.bn.pt/deptal/233/"));
//           System.out.println(parser.getImages("http://jorgemachado.com.sapo.pt/teste/index.html"));

//           testeTextImage1();
//           testeTextImage2();
//           testeTextImage3();
//           testeTextImage4();
            testeTextStartEndArticle();

        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void testeTextStartEndArticle() throws IOException
    {
        List l = new ArrayList();
        String html = "<html><body>This text should not appear <!-- Begin Article Header --> This should Appear <!-- Begin the bottom sections -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <!-- Begin the bottom sections -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <!-- Standard Copyright line here -->  This text should not appear <!-- Begin the bottom sections -->  This text should not appear  </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <h3>References</h3> This text should not appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This should Appear <h3>References</h3> This text should not appear <!-- Begin the bottom sections --> This text should not appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body><h2>References</h2> This should Appear <h3>References</h3> This text should not appear <!-- Begin the bottom sections --> This text should not appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        html = "<html><body>This text should not appear <!-- Begin Article Header --> This should Appear <h3>References</h3> This text should not appear <!-- Begin the bottom sections --> This text should not appear <!-- Standard Copyright line here -->  This text should not appear </body></html>";
        l.add(html);
        Iterator iter = l.iterator();
        while (iter.hasNext())
        {
            String s = (String) iter.next();
            SwingHTMLParser parser = new SwingHTMLParser(s);
//            System.out.println(s);
//            System.out.println(parser.getText());
            if (parser.getText().contains("This text should not appear") || !parser.getText().contains("This should Appear"))
            {
                System.out.println("Error in HTML:" + s);
            }
//            System.out.println("-----------------------------------------------");
        }
    }


    public static void testeTextImage1()
    {
        try
        {

            System.out.println("TESTING testeTextImage1...........");

            String html1 = "<html><body><img src='1.jpg'/>p1 p2 p3 p4 p5 p6 p7 p8 p9 p10</body></html>";
            String html2 = "<html><body>p1<img src='1.jpg'/> p2 p3 p4 p5 p6 p7 p8 p9 p10</body></html>";
            String html3 = "<html><body>p1 p2<img src='1.jpg'/> p3 p4 p5 p6 p7 p8 p9 p10</body></html>";
            String html4 = "<html><body>p1 p2 p3<img src='1.jpg'/> p4 p5 p6 p7 p8 p9 p10</body></html>";
            String html5 = "<html><body>p1 p2 p3 p4<img src='1.jpg'/> p5 p6 p7 p8 p9 p10</body></html>";
            String html6 = "<html><body>p1 p2 p3 p4 p5<img src='1.jpg'/> p6 p7 p8 p9 p10</body></html>";
            String html7 = "<html><body>p1 p2 p3 p4 p5 p6<img src='1.jpg'/> p7 p8 p9 p10</body></html>";
            String html8 = "<html><body>p1 p2 p3 p4 p5 p6 p7<img src='1.jpg'/> p8 p9 p10</body></html>";
            String html9 = "<html><body>p1 p2 p3 p4 p5 p6 p7 p8<img src='1.jpg'/> p9 p10</body></html>";
            String html10 = "<html><body>p1 p2 p3 p4 p5 p6 p7 p8 p9<img src='1.jpg'/> p10</body></html>";
            String html11 = "<html><body>p1 p2 p3 p4 p5 p6 p7 p8 p9 p10<img src='1.jpg'/></body></html>";

            SwingHTMLParser parser1 = new SwingHTMLParser(html1);
            SwingHTMLParser parser2 = new SwingHTMLParser(html2);
            SwingHTMLParser parser3 = new SwingHTMLParser(html3);
            SwingHTMLParser parser4 = new SwingHTMLParser(html4);
            SwingHTMLParser parser5 = new SwingHTMLParser(html5);
            SwingHTMLParser parser6 = new SwingHTMLParser(html6);
            SwingHTMLParser parser7 = new SwingHTMLParser(html7);
            SwingHTMLParser parser8 = new SwingHTMLParser(html8);
            SwingHTMLParser parser9 = new SwingHTMLParser(html9);
            SwingHTMLParser parser10 = new SwingHTMLParser(html10);
            SwingHTMLParser parser11 = new SwingHTMLParser(html11);


            if (((WebImage) parser1.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser2.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4 p5"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html2);
            if (((WebImage) parser3.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4 p5 p6"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html3);
            if (((WebImage) parser4.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4 p5 p6 p7"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html4);
            if (((WebImage) parser5.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4 p5 p6 p7 p8"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html5);
            if (((WebImage) parser6.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p2 p3 p4 p5 p6 p7 p8 p9"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html6);
            if (((WebImage) parser7.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p3 p4 p5 p6 p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html7);
            if (((WebImage) parser8.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p4 p5 p6 p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html8);
            if (((WebImage) parser9.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p5 p6 p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html9);
            if (((WebImage) parser10.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p6 p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html10);
            if (((WebImage) parser11.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html11);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public static void testeTextImage2()
    {
        try
        {

            System.out.println("TESTING testeTextImage2...........");

            String html1 = "<html><body><img src='1.jpg'/>p1</body></html>";
            String html2 = "<html><body>p1<img src='1.jpg'/></body></html>";
            String html3 = "<html><body>p1 p2<img src='1.jpg'/></body></html>";
            String html4 = "<html><body>p1 p2 p3<img src='1.jpg'/></body></html>";
            String html5 = "<html><body>p1 p2 p3 p4<img src='1.jpg'/></body></html>";
            String html6 = "<html><body>p1 p2 p3 p4 p5<img src='1.jpg'/></body></html>";
            String html7 = "<html><body><img src='1.jpg'/> p6 p7 p8 p9 p10</body></html>";
            String html8 = "<html><body><img src='1.jpg'/> p7 p8 p9 p10</body></html>";
            String html9 = "<html><body><img src='1.jpg'/> p8 p9 p10</body></html>";
            String html10 = "<html><body><img src='1.jpg'/> p9 p10</body></html>";
            String html11 = "<html><body><img src='1.jpg'/> p10</body></html>";
            String html12 = "<html><body><img src='1.jpg'/></body></html>";

            SwingHTMLParser parser1 = new SwingHTMLParser(html1);
            SwingHTMLParser parser2 = new SwingHTMLParser(html2);
            SwingHTMLParser parser3 = new SwingHTMLParser(html3);
            SwingHTMLParser parser4 = new SwingHTMLParser(html4);
            SwingHTMLParser parser5 = new SwingHTMLParser(html5);
            SwingHTMLParser parser6 = new SwingHTMLParser(html6);
            SwingHTMLParser parser7 = new SwingHTMLParser(html7);
            SwingHTMLParser parser8 = new SwingHTMLParser(html8);
            SwingHTMLParser parser9 = new SwingHTMLParser(html9);
            SwingHTMLParser parser10 = new SwingHTMLParser(html10);
            SwingHTMLParser parser11 = new SwingHTMLParser(html11);
            SwingHTMLParser parser12 = new SwingHTMLParser(html12);


            if (((WebImage) parser1.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser2.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html2);
            if (((WebImage) parser3.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html3);
            if (((WebImage) parser4.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html4);
            if (((WebImage) parser5.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html5);
            if (((WebImage) parser6.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p2 p3 p4 p5"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html6);
            if (((WebImage) parser7.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p6 p7 p8 p9"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html7);
            if (((WebImage) parser8.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html8);
            if (((WebImage) parser9.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html9);
            if (((WebImage) parser10.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html10);
            if (((WebImage) parser11.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser12.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(""))
                System.out.println("OK");
            else System.out.println("FAIL:" + html12);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void testeTextImage3()
    {
        try
        {

            System.out.println("TESTING testeTextImage2...........");

            String html1 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/>p1</body></html>";
            String html2 = "<html><body><img src='1.jpg'/><img src='1.jpg'/>p1<img src='1.jpg'/></body></html>";
            String html3 = "<html><body>p1 p2<img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";
            String html4 = "<html><body>p1 p2 p3<img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";
            String html5 = "<html><body>p1 p2 p3 p4<img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";
            String html6 = "<html><body>p1 p2 p3 p4 p5<img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";
            String html7 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p6 p7 p8 p9 p10</body></html>";
            String html8 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p7 p8 p9 p10</body></html>";
            String html9 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p8 p9 p10</body></html>";
            String html10 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p9 p10</body></html>";
            String html11 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p10</body></html>";
            String html12 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";

            SwingHTMLParser parser1 = new SwingHTMLParser(html1);
            SwingHTMLParser parser2 = new SwingHTMLParser(html2);
            SwingHTMLParser parser3 = new SwingHTMLParser(html3);
            SwingHTMLParser parser4 = new SwingHTMLParser(html4);
            SwingHTMLParser parser5 = new SwingHTMLParser(html5);
            SwingHTMLParser parser6 = new SwingHTMLParser(html6);
            SwingHTMLParser parser7 = new SwingHTMLParser(html7);
            SwingHTMLParser parser8 = new SwingHTMLParser(html8);
            SwingHTMLParser parser9 = new SwingHTMLParser(html9);
            SwingHTMLParser parser10 = new SwingHTMLParser(html10);
            SwingHTMLParser parser11 = new SwingHTMLParser(html11);
            SwingHTMLParser parser12 = new SwingHTMLParser(html12);


            if (((WebImage) parser1.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser2.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html2);
            if (((WebImage) parser3.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html3);
            if (((WebImage) parser4.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html4);
            if (((WebImage) parser5.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html5);
            if (((WebImage) parser6.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p2 p3 p4 p5"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html6);
            if (((WebImage) parser7.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p6 p7 p8 p9"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html7);
            if (((WebImage) parser8.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p7 p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html8);
            if (((WebImage) parser9.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p8 p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html9);
            if (((WebImage) parser10.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p9 p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html10);
            if (((WebImage) parser11.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p10"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser12.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(""))
                System.out.println("OK");
            else System.out.println("FAIL:" + html12);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void testeTextImage4()
    {
        try
        {

            System.out.println("TESTING testeTextImage2...........");

            String html1 = "<html><body><img src='1.jpg'/>p1<img src='1.jpg'/><img src='1.jpg'/>p2</body></html>";
            String html2 = "<html><body>p1 p2<img src='1.jpg'/>p3<img src='1.jpg'/>p4<img src='1.jpg'/></body></html>";
            String html3 = "<html><body>p1 p2 <img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/>p3 p4</body></html>";
            String html4 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/>p1 p2 p3 p4</body></html>";
            String html5 = "<html><body>p1 p2<img src='1.jpg'/>p3 p4<img src='1.jpg'/><img src='1.jpg'/></body></html>";
            String html6 = "<html><body>p1 p2 <img src='1.jpg'/><img src='1.jpg'/>p3<img src='1.jpg'/></body></html>";
            String html7 = "<html><body><a href='link'>p1 p2</a><img src='1.jpg'/>p3<img src='2.jpg'/><img src='3.jpg'/> p4 p5 p6 p7 p8</body></html>";
            String html8 = "<html><body><h1>p1 p2 p3 p4</h1><a href='link'>p5<img src='1.jpg'/> p6</a><img src='2.jpg'/><img src='3.jpg'/><img src='4.jpg'/> p7 p8 p9 p10</body></html>";
//            String html9 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p8 p9 p10</body></html>";
//            String html10 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p9 p10</body></html>";
//            String html11 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/> p10</body></html>";
//            String html12 = "<html><body><img src='1.jpg'/><img src='1.jpg'/><img src='1.jpg'/></body></html>";

            SwingHTMLParser parser1 = new SwingHTMLParser(html1);
            SwingHTMLParser parser2 = new SwingHTMLParser(html2);
            SwingHTMLParser parser3 = new SwingHTMLParser(html3);
            SwingHTMLParser parser4 = new SwingHTMLParser(html4);
            SwingHTMLParser parser5 = new SwingHTMLParser(html5);
            SwingHTMLParser parser6 = new SwingHTMLParser(html6);
            SwingHTMLParser parser7 = new SwingHTMLParser(html7);
            SwingHTMLParser parser8 = new SwingHTMLParser(html8);
//            SwingHTMLParser parser9 = new SwingHTMLParser(html9);
//            SwingHTMLParser parser10 = new SwingHTMLParser(html10);
//            SwingHTMLParser parser11 = new SwingHTMLParser(html11);
//            SwingHTMLParser parser12 = new SwingHTMLParser(html12);


            if (((WebImage) parser1.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html1);
            if (((WebImage) parser2.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html2);
            if (((WebImage) parser3.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html3);
            if (((WebImage) parser4.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html4);
            if (((WebImage) parser5.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3 p4"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html5);
            if (((WebImage) parser6.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p1 p2 p3"))
                System.out.println("OK");
            else System.out.println("FAIL:" + html6);
            WebImage image = ((WebImage) parser7.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next());
            String text = image.getSrc() + ":" + image.getSurroundText();
            if (image.getSurroundText().equals(" p1 p2 p3 p4 p5 p6")) System.out.println("OK");
            else System.out.println("FAIL:" + html7 + " - \n TEXT RETURNED:" + text);
            image = ((WebImage) parser8.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next());
            text = image.getSrc() + ":" + image.getSurroundText();
            if (image.getSurroundText().equals(" p2 p3 p4 p5 p6 p7 p8 p9")) System.out.println("OK");
            else System.out.println("FAIL:" + html8 + " - \n TEXT RETURNED:" + text);
//            if(((WebImage)parser9.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p8 p9 p10")) System.out.println("OK");
//            else System.out.println("FAIL:" + html9);
//            if(((WebImage)parser10.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p9 p10")) System.out.println("OK");
//            else System.out.println("FAIL:" + html10);
//            if(((WebImage)parser11.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals(" p10")) System.out.println("OK");
//            else System.out.println("FAIL:" + html1);
//            if(((WebImage)parser12.getImages("http://jorgemachado.com.sapo.pt/teste/index.html").iterator().next()).getSurroundText().equals("")) System.out.println("OK");
//            else System.out.println("FAIL:" + html12);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}

