package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.FilterEnum;
import pt.utl.ist.lucene.OrderEnum;

import java.util.Map;
import java.io.File;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class SearchConfiguration
{
    int id;
    QueryConfiguration queryConfiguration;
    Configuration configuration;
    

    static int count = 0;

    public SearchConfiguration(QueryConfiguration queryConfiguration, Configuration configuration)
    {
        this(queryConfiguration,configuration,++count);
    }
    
    public SearchConfiguration(QueryConfiguration queryConfiguration, Configuration configuration,int id)
    {
        this.queryConfiguration = queryConfiguration;
        this.configuration = configuration;
        if(queryConfiguration.getAnalyzer() == null)
            queryConfiguration.setAnalyzer(configuration.getAnalyzer());
        this.id = id;
    }

    public QueryConfiguration getQueryConfiguration()
    {
        return queryConfiguration;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }


    public String getFileId()
    {
        String filter = "-filter_" + FilterEnum.defaultFilter.toString();
        String order = "-order_" + OrderEnum.defaultOrder.toString();
        if(getQueryConfiguration() != null)
        {
            String filter2 = getQueryConfiguration().getProperty("lgte.default.filter");
            if(filter2 != null)
                filter = "-filter_" + filter2;
            String order2 = getQueryConfiguration().getProperty("lgte.default.order");
            if(order2 != null)
                order = "-order_" + order2;
        }

            String stem = "no";
            if(configuration.getDir().indexOf("stem")>=0)
                stem=configuration.getDir();
         return getRun() + "-" +  configuration.getModel().getShortName() + "-Stemming_" + stem + "-qe_" + getQueryConfiguration().getForceQE() +filter+order + ".txt";
    }

    public File getOutputFile()
    {
        return new File(configuration.getOutputDir() + File.separator + getFileId());
    }

    public File getOutputReportFile(String reportSuffix)
    {
        return new File(configuration.getOutputDir() + File.separator + getFileId() + reportSuffix);
    }


    public String getRun()
    {
        return "run-" + id;
    }
}
