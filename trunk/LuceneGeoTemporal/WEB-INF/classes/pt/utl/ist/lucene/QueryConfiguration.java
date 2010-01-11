package pt.utl.ist.lucene;

import pt.utl.ist.lucene.config.ConfigProperties;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.ilps.DataCacher;

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

    static int cacheSize = 32;
    private Object[] cache = new Object[32];


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
        String property = (String) DataCacher.Instance().get(key);
        if(property != null)
            return property;
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
        String property = (String) DataCacher.Instance().get(key);
        if(property != null)
            return Integer.parseInt(property);
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
        String property = (String) DataCacher.Instance().get(key);
        if(property != null)
            return Float.parseFloat(property);
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
        String property = (String) DataCacher.Instance().get(key);
        if(property != null)
            return Double.parseDouble(property);
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
        String property = (String) DataCacher.Instance().get(key);
        if(property != null)
            return Boolean.parseBoolean(property);    
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


    public void addCacheObject(Object o, int index)
    {
        if(cache == null)
            cache = new Object[cacheSize];
        cache[index] = o;
    }

    public Object getCacheObject(int index)
    {
        if(cache == null)
            return null;
       return cache[index];
    }

    public int getCacheSize()
    {
        return cacheSize;
    }


}
