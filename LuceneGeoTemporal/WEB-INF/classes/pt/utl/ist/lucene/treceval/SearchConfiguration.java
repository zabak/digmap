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
    TopicsConfiguration topicsConfiguration = null;


    public SearchConfiguration(QueryConfiguration queryConfiguration, Configuration configuration)
    {
        this.queryConfiguration = queryConfiguration;
        this.configuration = configuration;
    }

    public SearchConfiguration(QueryConfiguration queryConfiguration, Configuration configuration, TopicsConfiguration topicsConfiguration)
    {
        this(queryConfiguration,configuration);
        this.topicsConfiguration = topicsConfiguration;
    }


    public QueryConfiguration getQueryConfiguration()
    {
        return queryConfiguration;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }


    public TopicsConfiguration getTopicsConfiguration()
    {
        return topicsConfiguration;
    }

    public void setTopicsConfiguration(TopicsConfiguration topicsConfiguration)
    {
        this.topicsConfiguration = topicsConfiguration;
    }

    public static class TopicsConfiguration
    {
        Map<String,Float> fieldBoost;

        public Map<String, Float> getFieldBoost()
        {
            return fieldBoost;
        }

        public void setFieldBoost(Map<String, Float> fieldBoost)
        {
            this.fieldBoost = fieldBoost;
        }
    }
}
