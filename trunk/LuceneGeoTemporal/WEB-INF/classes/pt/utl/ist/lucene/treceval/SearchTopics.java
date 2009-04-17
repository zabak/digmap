package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

import java.io.IOException;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 11:47:56
 * @see pt.utl.ist.lucene.treceval
 */
public class SearchTopics implements ISearchCallBack
{

    private static final Logger logger = Logger.getLogger(SearchTopics.class);

    private int maxResultsForOutput;
    private SearchConfiguration searchConfiguration;
    private Configuration configuration;
    private LgteIndexSearcherWrapper indexSearcher;
    private String run;



    /**
     * constructor
     *
     * @param searchConfiguration to search topics in index
     * @param run           name of the handle
     * @throws IOException opening indexes
     */

    public SearchTopics(SearchConfiguration searchConfiguration, String run) throws IOException
    {
        this.maxResultsForOutput = searchConfiguration.getConfiguration().getMaxResultsPerTopic();
        this.run = run;
        this.searchConfiguration = searchConfiguration;
        configuration = searchConfiguration.getConfiguration();
        indexSearcher = new LgteIndexSearcherWrapper(configuration.getModel(), configuration.getIndexPath(),searchConfiguration.getQueryConfiguration().getQueryProperties());
    }


    //static FileWriter topicTotalResults;


    /**
     * Static service to search in a list of Configurations
     *
     * @param searchConfigurations to search
     * @throws IOException       opening indexes
     * @throws DocumentException opening topics xml
     */
    public static void search(List<SearchConfiguration> searchConfigurations) throws IOException, DocumentException
    {

        //topicTotalResults = new FileWriter("D:/topicTotalResults.csv");
        //topicTotalResults.write("topic;total;s>=0.99;s>=0.98;s>=0.95;s>=0.9;s>=0.8;s>=0.5;\n");
        int runId = 1;
        for (SearchConfiguration c : searchConfigurations)
        {
            SearchTopics searchTopics = new SearchTopics(c, "run-" + runId);
            searchTopics.searchTopics();
            searchTopics.close();
            runId++;
        }
        //topicTotalResults.close();

    }



    /**
     * In the same fashion used in IndexCollections we will set a TopicsPreprocessor
     * witch will process each topic and send it back with callback method to be writen in output
     */
    public void searchTopics()
    {

        String filter = "-filter_" + FilterEnum.defaultFilter.toString();
        String order = "-order_" + OrderEnum.defaultOrder.toString();
        if(searchConfiguration.getQueryConfiguration() != null)
        {
            String filter2 = searchConfiguration.getQueryConfiguration().getProperty("lgte.default.filter");
            if(filter2 != null)
                filter = "-filter_" + filter2;
            String order2 = searchConfiguration.getQueryConfiguration().getProperty("lgte.default.order");
            if(order2 != null)
                order = "-order_" + order2;
        }
        try
        {
            String stem = "no";
            if(configuration.getDir().indexOf("stem")>=0)
                stem=configuration.getDir();
            configuration.getITopicsProcessor().handle(configuration.getTopicsPath(), this, configuration.getModel().getShortName() + "-Stemming_" + stem + "-qe_" + searchConfiguration.getQueryConfiguration().getForceQE() +filter+order, run, configuration.getCollectionId(), configuration.getOutputDir(), searchConfiguration.getTopicsConfiguration());
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
            format.writeRecord(hits.id(i), hits.doc(i).getDocument(), hits.score(i), run);
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
}
