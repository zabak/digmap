package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;

import java.util.Properties;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class QueryConfiguration
{

    private static final Logger logger = Logger.getLogger(QueryConfiguration.class);

    private Properties queryProperties = null;
    private QEEnum forceQE = QEEnum.defaultQE;
    private Analyzer analyzer;


    public QueryConfiguration()
    {
    }


    public Analyzer getAnalyzer()
    {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer)
    {
        this.analyzer = analyzer;
    }

    public Properties getQueryProperties()
    {
        if(queryProperties == null)
            queryProperties = new Properties();
        return queryProperties;
    }

    /**
     * Search for a property in QueryProperties if don't find use Config Properties
     *
     * @param key to get
     * @return property
     */
    public String getProperty(String key)
    {
        if(queryProperties != null)
        {
            String property = queryProperties.getProperty(key);
            if (property != null)
                return property;
        }
        return ConfigProperties.getProperty(key);
    }

    /**
     * Search for a property in QueryProperties if don't find use Config Properties
     *
     * @param key to get
     * @return property
     */
    public int getIntProperty(String key)
    {
        if(queryProperties != null)
        {
            String property = queryProperties.getProperty(key);
            if (property != null)
            {
                try
                {
                    return Integer.parseInt(property);
                }
                catch (NumberFormatException e)
                {
                    logger.error(e);
                }
            }
        }
        return ConfigProperties.getIntProperty(key);
    }

    /**
     * Search for a property in QueryProperties if don't find use Config Properties
     *
     * @param key to get
     * @return property
     */
    public float getFloatProperty(String key)
    {
        if(queryProperties != null)
        {
            String property = queryProperties.getProperty(key);
            if (property != null)
            {
                try
                {
                    return Float.parseFloat(property);
                }
                catch (NumberFormatException e)
                {
                    logger.error(e);
                }
            }
        }
        return ConfigProperties.getFloatProperty(key);
    }

    /**
     * Search for a property in QueryProperties if don't find use Config Properties
     *
     * @param key to get
     * @return property
     */
    public double getDoubleProperty(String key)
    {
        if(queryProperties != null)
        {
            String property = queryProperties.getProperty(key);
            if (property != null)
            {
                try
                {
                    return Double.parseDouble(property);
                }
                catch (NumberFormatException e)
                {
                    logger.error(e);
                }
            }
        }
        return ConfigProperties.getDoubleProperty(key);
    }

    /**
     * Search for a property in QueryProperties if don't find use Config Properties
     *
     * @param key to get
     * @return property
     */
    public boolean getBooleanProperty(String key)
    {
        if(queryProperties != null)
        {
            String property = queryProperties.getProperty(key);
            if (property != null)
            {
                try
                {
                    return Boolean.parseBoolean(property);
                }
                catch (NumberFormatException e)
                {
                    logger.error(e);
                }
            }
        }
        return ConfigProperties.getBooleanProperty(key);
    }

    public void setProperty(String key,String value)
    {
        getQueryProperties().put(key,value);
    }

    public void setQueryProperties(Properties queryProperties)
    {
        this.queryProperties = queryProperties;
    }


    public QEEnum getForceQE()
    {
        return forceQE;
    }

    public void setForceQE(QEEnum forceQE)
    {
        this.forceQE = forceQE;
    }

    public Object getPlugin(String pluginStr)
    {
        if(queryProperties != null)
        {
            String implName = getProperty(pluginStr);
            if (implName == null || implName.length() == 0)
            {
                throw new RuntimeException("implementation not specified for " + pluginStr + " in PluginFactory propeties.");
            }
            try
            {
                return Class.forName(implName).newInstance();
            }
            catch (Exception ex)
            {
                throw new RuntimeException("factory unable to construct instance of " + implName);

            }
        }
        return ConfigProperties.getPlugin(pluginStr);
    }
}
