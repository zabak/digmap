package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.utils.XmlUtils;
import pt.utl.ist.lucene.utils.StringComparator;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 11:47:56
 * @see pt.utl.ist.lucene.treceval
 */
public class SearchTopics implements ISearchCallBack
{

    public static final String outputReportSuffix = "-treceval-report.txt";
    private static final Logger logger = Logger.getLogger(SearchTopics.class);

    private int maxResultsForOutput;
    private SearchConfiguration searchConfiguration;
    private Configuration configuration;
    private LgteIndexSearcherWrapper indexSearcher;



    /**
     * constructor
     *
     * @param searchConfiguration to search topics in index
     * @throws IOException opening indexes
     */

    public SearchTopics(SearchConfiguration searchConfiguration) throws IOException
    {
        this.maxResultsForOutput = searchConfiguration.getConfiguration().getMaxResultsPerTopic();
        this.searchConfiguration = searchConfiguration;
        configuration = searchConfiguration.getConfiguration();
        indexSearcher = new LgteIndexSearcherWrapper(configuration.getModel(), configuration.getIndexPath(),searchConfiguration.getQueryConfiguration().getQueryProperties());
    }


    //static FileWriter topicTotalResults;


    public static void search(List<SearchConfiguration> searchConfigurations) throws IOException, DocumentException
    {
        search(searchConfigurations,searchConfigurations.get(0).getConfiguration().getOutputDir());
    }

    public static void evaluateMetrics(List<SearchConfiguration> searchConfigurations, String assessementsFile) throws IOException, DocumentException
    {
        for (SearchConfiguration c : searchConfigurations)
        {
            String[] args = new String[]{c.getOutputFile().getAbsolutePath(), assessementsFile};
            PrintStream out = System.out;
            System.setOut(new PrintStream(new FileOutputStream(c.getOutputReportFile(outputReportSuffix))));
            ireval.Main.main(args);
            System.out.close();
            System.setOut(out);
        }

    }

    /**
     * Static service to search in a list of Configurations
     *
     * @param searchConfigurations to search
     * @throws IOException       opening indexes
     * @throws DocumentException opening topics xml
     */
    public static void search(List<SearchConfiguration> searchConfigurations, String runPackageFileOutputDir) throws IOException, DocumentException
    {

        //topicTotalResults = new FileWriter("D:/topicTotalResults.csv");
        //topicTotalResults.write("topic;total;s>=0.99;s>=0.98;s>=0.95;s>=0.9;s>=0.8;s>=0.5;\n");

        for (SearchConfiguration c : searchConfigurations)
        {
            SearchTopics searchTopics = new SearchTopics(c);
            searchTopics.searchTopics();
            searchTopics.close();
        }



        createRunPackage(runPackageFileOutputDir,searchConfigurations);


        //topicTotalResults.close();

    }





    /**
     * In the same fashion used in IndexCollections we will set a TopicsPreprocessor
     * witch will process each topic and send it back with callback method to be writen in output
     */
    public void searchTopics()
    {
        try
        {
            configuration
                    .getITopicsProcessor()
                    .handle(
                            configuration.getTopicsPath(),
                            this,
                            searchConfiguration.getFileId(),
                            searchConfiguration.getRun(),
                            configuration.getCollectionId(),
                            configuration.getOutputDir(),
                            searchConfiguration.getQueryConfiguration());
        }
        catch (MalformedURLException e)
        {
            logger.error(e, e);
        }
        catch (DocumentException e)
        {
            logger.error(e,e);
        }
    }

    /**
     * This callback will be invoked during topics cicle in topics processor
     *
     * @param t topic to search
     */
    public void searchCallback(Topic t)
    {
        String query = t.getQuery();
        logger.info("Topic Query " + t.getIdentifier() + ":" + query.trim().replace('\n', ' ').replace('\r', ' '));

        try
        {
            //lets pass query configuration analizer and very important, the indexsearcher
            //for QueryExpansion in case of been setted
            LgteQuery lgteQuery = LgteQueryParser.parseQuery(
                    query,
                    configuration.getSearchIndex(),
                    configuration.getAnalyzer(),
                    indexSearcher,
                    searchConfiguration.getQueryConfiguration());

            LgteHits hits = indexSearcher.search(lgteQuery);

            int total0_99 = 0;
            int total0_98 = 0;
            int total0_95 = 0;
            int total0_9 = 0;
            int total0_8 = 0;
            int total0_5 = 0;
            for(int i = 0; i < hits.length();i++)
            {
                if(hits.spatialScore(i) >= 0.99f)
                    total0_99++;
                if(hits.spatialScore(i) >= 0.98f)
                    total0_98++;
                if(hits.spatialScore(i) > 0.95f)
                    total0_95++;
                if(hits.spatialScore(i) > 0.9f)
                    total0_9++;
                if(hits.spatialScore(i) > 0.8f)
                    total0_8++;
                if(hits.spatialScore(i) > 0.5f)
                    total0_5++;
            }

            //topicTotalResults.write(t.getIdentifier() + ";" + hits.length() + ";" + total0_99 + ";" + total0_98+ ";" + total0_95+ ";" + total0_9+ ";" + total0_8+ ";" + total0_5+ ";\n");
            //topicTotalResults.flush();


            OutputFormat outputFormat = t.getOutputFormat();

            writeSearch(outputFormat, hits);
        }
        catch (ParseException e)
        {
            logger.error("cant searchCallback", e);
        }
        catch (IOException e)
        {
            logger.error("cant searchCallback", e);
        }
    }

    /**
     * This method will call write for each hit in hits
     *
     * @param format output format class
     * @param hits   to write
     * @throws IOException on file write
     */
    private void writeSearch(OutputFormat format, LgteHits hits) throws IOException
    {
        long time = System.currentTimeMillis();
        format.writeHeader(hits.length());

        for (int i = 0; i < hits.length() && i < maxResultsForOutput; i++)
        {
            format.writeRecord(hits.id(i), hits.doc(i).getDocument(), hits.score(i), searchConfiguration.getRun());
        }
        format.writeFooter();
        logger.info("writeTime:" + (System.currentTimeMillis() - time) + " ms");
    }

    public void close() throws IOException
    {
        indexSearcher.close();
    }

    public void setMaxResultsForOutput(int maxResultsForOutput)
    {
        this.maxResultsForOutput = maxResultsForOutput;
    }

    public static void createRunPackage(String toDir, List<SearchConfiguration> searchConfigurations)
    {
        try
        {
            FileWriter fw = new FileWriter(toDir + "\\runs.xml");
            fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            fw.write("<?xml-stylesheet type=\"text/xsl\" href=\"runs.xsl\"?>\n");
            fw.write("<runs>");
            for(SearchConfiguration searchConfiguration: searchConfigurations)
            {
                createRunPackage(fw,searchConfiguration);
            }
            fw.write("</runs>");
            fw.flush();
            fw.close();
        }catch(Exception e)
        {
            logger.error(e,e);
        }
    }

    private static void writeProps(FileWriter fw, Properties props) throws IOException
    {
        List<String> propsList = new ArrayList<String>(props.stringPropertyNames());
        Collections.sort(propsList, StringComparator.getInstance());
        for(String key : propsList)
        {
            String value = (String) props.getProperty(key);
            fw.write("<key id=\"" + XmlUtils.escape(key) + "\">");
            fw.write(XmlUtils.escape(value));
            fw.write("</key>");
        }
    }
    public static void createRunPackage(FileWriter fw, SearchConfiguration searchConfiguration) throws IOException
    {

        fw.write("<run id=\"" + XmlUtils.escape(searchConfiguration.getFileId()) + "\">");

        fw.write("<configuration>");
        fw.write("<base>");
        writeProps(fw, ConfigProperties.getProperties());     
        fw.write("</base>");
        fw.write("<override>");
        writeProps(fw,searchConfiguration.getQueryConfiguration().getQueryProperties());
        fw.write("</override>");
        fw.write("</configuration>");
        fw.write("<results>");
        ReportFile rf = new ReportFile(searchConfiguration.getOutputReportFile(outputReportSuffix));
        ReportResult all = rf.getResult("all");
        for(int i = 0; i < all.getValues().length; i++)
        {
            fw.write("<result name=\"" + XmlUtils.escape(ReportResult.names[i]) + "\">");
            fw.write(XmlUtils.escape(all.getValues()[i]));
            fw.write("</result>");
        }
        fw.write("</results>");
        fw.write("</run>");
    }
}
