package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.QueryConfiguration;

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
