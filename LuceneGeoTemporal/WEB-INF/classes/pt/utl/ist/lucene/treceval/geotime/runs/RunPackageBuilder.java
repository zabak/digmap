package pt.utl.ist.lucene.treceval.geotime.runs;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.geoclef2.index.Config;
import pt.utl.ist.lucene.treceval.SearchTopics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 26/Jan/2010
 * @time 20:15:29
 * @email machadofisher@gmail.com
 */
public class RunPackageBuilder {

    private static final Logger logger = Logger.getLogger(RunPackageBuilder.class);

    static String outputFile =    pt.utl.ist.lucene.treceval.geotime.index.Config.ntcirBase +  File.separator + "runs2" + File.separator;
    static String topicsFile =    pt.utl.ist.lucene.treceval.geotime.index.Config.ntcirBase +  File.separator + "topics" + File.separator + "topics.xml";

//    static String outputFile06 = Config.geoclefBase +  File.separator + "runs06" + File.separator;
//    static String outputFile08 = Config.geoclefBase +  File.separator + "runs08" + File.separator;

   

//    static String topicsFile06 = Config.geoclefBase +  File.separator + "topics" + File.separator + "topics06Formatted.xml";
//    static String topicsFile08 = Config.geoclefBase +  File.separator + "topics" + File.separator + "topics08Formatted.xml";

    public static void main(String[] args) throws DocumentException, IOException, ParseException {
        List<String> outputs = new ArrayList<String>();
        for(File f: new File(outputFile).listFiles())
        {
            if(f.getName().endsWith(SearchTopics.outputReportSuffix))
                outputs.add(f.getAbsolutePath().substring(0,f.getAbsolutePath().length() - SearchTopics.outputReportSuffix.length()));
        }
        SearchTopics.createRunPackage(outputFile,outputs,new Properties());

//        List<String> outputs = new ArrayList<String>();
//        for(File f: new File(outputFile06).listFiles())
//        {
//            if(f.getName().endsWith(SearchTopics.outputReportSuffix))
//                outputs.add(f.getAbsolutePath().substring(0,f.getAbsolutePath().length() - SearchTopics.outputReportSuffix.length()));
//        }
//        SearchTopics.createRunPackage(outputFile06,outputs,new Properties());
//        outputs = new ArrayList<String>();
//        for(File f: new File(outputFile08).listFiles())
//        {
//            if(f.getName().endsWith(SearchTopics.outputReportSuffix))
//                outputs.add(f.getAbsolutePath().substring(0,f.getAbsolutePath().length() - SearchTopics.outputReportSuffix.length()));
//        }
//        SearchTopics.createRunPackage(outputFile08,outputs,new Properties());
    }
}
