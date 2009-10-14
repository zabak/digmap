package digmapFrbr;

import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.config.LocalProperties;

import java.io.*;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;
import org.apache.commons.io.output.FileWriterWithEncoding;


public class Preprocessor {


    static Properties props;

    static {
        try {
            props = new LocalProperties("digmapFrbr/conf.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String originalsDir = props.getProperty("originals.dir");
    static String dataDir = props.getProperty("data.dir");
    static String xslDir = props.getProperty("xsl.dir");

    public static void main(String [] args) throws IOException {


        String xslFullText = xslDir + "/recordFullText.xsl";

        preprocessNobel(dataDir + "/kbr/nobel/dc",originalsDir + "/kbr/nobel",xslDir + "/unimarc2oai_dc.xsl",xslFullText,"kbr");
        preprocessNobel(dataDir + "/bnp/nobel/dc",originalsDir + "/bnp/nobel",xslDir + "/unimarc2oai_dc.xsl",xslFullText,"bnp");
        preprocessNobel(dataDir + "/lit/nobel/dc",originalsDir + "/lit/nobel",xslDir + "/unimarc2oai_dc.xsl",xslFullText,"lit");
        preprocessNobel(dataDir + "/fra/nobel/dc",originalsDir + "/fra/nobel",xslDir + "/unimarc2oai_dc.xsl",xslFullText,"fra");

        preprocessNobel(dataDir + "/bl/nobel/dc",originalsDir + "/bl/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"bl");
        preprocessNobel(dataDir + "/cze/nobel/dc",originalsDir + "/cze/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"cze");
        preprocessNobel(dataDir + "/ger/nobel/dc",originalsDir + "/ger/nobel", xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"ger");
        preprocessNobel(dataDir + "/lat/nobel/dc",originalsDir + "/lat/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"lat");
        preprocessNobel(dataDir + "/rus/nobel/dc",originalsDir + "/rus/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"rus");
        preprocessNobel(dataDir + "/ser/nobel/dc",originalsDir + "/ser/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"ser");
        preprocessNobel(dataDir + "/spa/nobel/dc",originalsDir + "/spa/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"spa");
        preprocessNobel(dataDir + "/nsz/nobel/dc",originalsDir + "/nsz/nobel",xslDir + "/marcXchange2oai_dc.xsl",xslFullText,"nsz");

    }
    public static void preprocessNobel(String toDir,String originals, String xsl, String xslFullText, String collection) throws IOException {
        int counter = 0;

        File[] files = new File(originals).listFiles();
        File dcDir =  new File(toDir);
        dcDir.mkdirs();
        Writer fw = new FileWriterWithEncoding(dcDir.getAbsolutePath()+ "/processed_" + counter + ".xml","UTF-8");
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<recordSet collection=\""+collection+"\">");
        for(File f: files)
        {
            if(f.isFile())
            {
                counter++;
                if(counter % 100 == 0)
                {
                    fw.write("</recordSet>");
                    fw.close();
                    fw = new FileWriterWithEncoding(dcDir.getAbsolutePath()+ "/processed_" + counter + ".xml","UTF-8");
                    fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    fw.write("<recordSet collection=\""+collection+"\">");
                }
                try
                {
                    fw.write("<processedRecord relativeFilePath=\"" + f.getAbsolutePath().substring(originalsDir.length()).replace("\\","/") + "\" filepath=\"" + f.getAbsolutePath() + "\" filename=\"" + f.getName() + "\" possibleId=\"" + Files.getFileWithoutExtension(f.getName()) + "\">");
                    Document d = nmaf.util.Dom4jUtil.parseDomFromFile(f);
                    Document d2 = nmaf.util.Dom4jUtil.transformDomIntoDom(d,xsl);
                    Dom4jUtil.writeDontCloseStream(d2.getRootElement(),fw);
                    Document d3 = nmaf.util.Dom4jUtil.transformDomIntoDom(d,xslFullText);
                    Dom4jUtil.writeDontCloseStream(d3.getRootElement(),fw);
                    fw.write("</processedRecord>");
                    System.out.println("Processed: " + originals + "/" + f.getName());
                }
                catch (SAXException e)
                {
                    e.printStackTrace();
                }
                catch (DocumentException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }
        fw.write("</recordSet>");
        fw.close();
    }
}
