package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.QueryConfiguration;

import java.util.Map;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class SearchConfiguration
{
    QueryConfiguration queryConfiguration;
    Configuration configuration;
    

    public SearchConfiguration(QueryConfiguration queryConfiguration, Configuration configuration)
    {
        this.queryConfiguration = queryConfiguration;
        this.configuration = configuration;
        if(queryConfiguration.getAnalyzer() == null)
            queryConfiguration.setAnalyzer(configuration.getAnalyzer());
    }

    public QueryConfiguration getQueryConfiguration()
    {
        return queryConfiguration;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }
}
