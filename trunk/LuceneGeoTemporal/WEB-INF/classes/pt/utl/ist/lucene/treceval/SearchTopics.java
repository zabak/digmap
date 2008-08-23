package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.LgteHits;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.LgteQuery;
import pt.utl.ist.lucene.LgteQueryParser;
import pt.utl.ist.lucene.treceval.output.OutputFormat;
import pt.utl.ist.lucene.treceval.output.Topic;

import java.io.IOException;
import java.io.OutputStream;
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


    private Configuration configuration;
    private LgteIndexSearcherWrapper indexSearcher;
    private String run;


    /**
     * constructor
     * @param configuration to search topics in index
     * @param run name of the handle
     * @throws IOException opening indexes
     */
    public SearchTopics(Configuration configuration, String run) throws IOException
    {
        this.run = run;
        this.configuration = configuration;
        indexSearcher = new LgteIndexSearcherWrapper(configuration.getModel(),configuration.getIndexPath());
    }


    /**
     * Static service to search in a list of Configurations
     * @param configurations to search
     * @throws IOException opening indexes
     * @throws DocumentException opening topics xml
     */
    public static void search(List<Configuration> configurations) throws IOException, DocumentException
    {
        int runId = 1;
        for(Configuration c: configurations)
        {
            SearchTopics searchTopics = new SearchTopics(c,"handle-" + runId);
            searchTopics.searchTopics();
            searchTopics.close();
            runId++;
        }
    }

    /**
     * In the same fashion used in IndexConfiguration we will set a TopicsPreprocessor
     * witch will process each topic and send it back with callback method to be writen in output
     */
    public void searchTopics()
    {
        configuration.getITopicsProcessor().processPath(configuration.getTopicsPath(), configuration.getDir(), this,run, configuration.getCollectionId(), configuration.getOutputDir());
    }

    /**
     * This callback will be invoked during topics cicle in topics processor
     * @param t topic to search
     * @param stream ro write in
     * @param maxDocsFlush configuration from processor may be diferent from processor to processor
     * @param maxResultsForOutput configuration from processor may be diferent from processor to processor
     */
    public void searchCallback(Topic t, OutputStream stream, int maxDocsFlush, int maxResultsForOutput)
    {
        String query = t.getQuery();
        logger.info("Topic Query:" + query.trim().replace('\n',' ').replace('\r',' '));

        try
        {
            //lets pass query configuration analizer and very important, the indexsearcher
            //for QueryExpansion in case of been setted
            LgteQuery lgteQuery = LgteQueryParser.parseQuery(
                    query,
                    configuration.getSearchIndex(),
                    configuration.getAnalyzer(),
                    indexSearcher,
                    configuration.getQueryConfiguration());

            LgteHits hits = indexSearcher.search(lgteQuery);

            OutputFormat outputFormat = t.getOutputFormat(stream);
            outputFormat.setMaxDocsToFlush(maxDocsFlush);
            writeSearch(outputFormat,hits,maxResultsForOutput);
        }
        catch (ParseException e)
        {
            logger.error("cant searchCallback",e);
        }
        catch (IOException e)
        {
            logger.error("cant searchCallback",e);
        }
    }

    /**
     * This method will call write for each hit in hits
     * @param format output format class
     * @param hits to write
     * @param maxResultsForOutput to stop
     * @throws IOException on file write
     */
    private void writeSearch(OutputFormat format, LgteHits hits, int maxResultsForOutput) throws IOException
    {
        long time = System.currentTimeMillis();
        format.writeHeader(hits.length());

        for(int i = 0; i < hits.length() && i < maxResultsForOutput; i++)
        {
            format.writeRecord(hits.doc(i).getDocument(),hits.score(i),run);
        }
        format.writeFooter();
        logger.info("writeTime:" + (System.currentTimeMillis() - time) + " ms");
    }

    public void close() throws IOException
    {
        indexSearcher.close();
    }
}
