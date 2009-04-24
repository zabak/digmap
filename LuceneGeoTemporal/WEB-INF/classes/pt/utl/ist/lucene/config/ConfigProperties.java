package pt.utl.ist.lucene.config;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */

public class ConfigProperties
{

    /**
     * Creates a new instance of ConfigProperties
     */

    private static Logger logger = Logger.getLogger(ConfigProperties.class);

    private static LocalProperties properties = null;

    public static Properties getProperties()
    {
        return properties;
    }
    
    public static void loadProperties()
    {
        try
        {
            ConfigProperties.properties = new LocalProperties("pt/utl/ist/lucene/app.properties");
        }
        catch (Exception e)
        {
            //Whith out properties this application cant procced
            ConfigProperties.logger.fatal("Properties cant be loaded", e);
            //System.exit(-1);
        }
    }

    public static String getProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }
        String return_property = ConfigProperties.properties.getProperty(property);

        if (return_property == null)
        {
            ConfigProperties.logger.warn("Warning: Null property: " + property);
            return property;
        }

        return return_property;
    }

    public static int getIntProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }

        String stringValue = ConfigProperties.properties.getProperty(property);
        int intValue = 0;

        if (stringValue != null)
        {
            try
            {
                intValue = new Integer(stringValue);
            }
            catch (NumberFormatException e)
            {
                ConfigProperties.logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return intValue;
    }

    public static long getLongProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }

        String stringValue = ConfigProperties.properties.getProperty(property);
        long longValue = 0;

        if (stringValue != null)
        {
            try
            {
                longValue = new Long(stringValue).intValue();
            }
            catch (NumberFormatException e)
            {
                ConfigProperties.logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return longValue;
    }

    public static float getFloatProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }

        String stringValue = ConfigProperties.properties.getProperty(property);
        float floatValue = 0;

        if (stringValue != null)
        {
            try
            {
                floatValue = new Float(stringValue);
            }
            catch (NumberFormatException e)
            {
                ConfigProperties.logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return floatValue;
    }

    public static double getDoubleProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }

        String stringValue = ConfigProperties.properties.getProperty(property);
        double doubleValue = 0;

        if (stringValue != null)
        {
            try
            {
                doubleValue = new Float(stringValue).doubleValue();
            }
            catch (NumberFormatException e)
            {
                ConfigProperties.logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return doubleValue;
    }

    public static boolean getBooleanProperty(String property)
    {
        if (ConfigProperties.properties == null)
        {
            ConfigProperties.loadProperties();
        }

        String stringValue = ConfigProperties.properties.getProperty(property);
        boolean boolValue = false;

        if (stringValue != null)
        {
            if (stringValue.equalsIgnoreCase("true"))
            {
                boolValue = true;
            }
            else if (stringValue.equalsIgnoreCase("yes"))
            {
                boolValue = true;
            }
        }

        return boolValue;
    }

//    private static HashMap<String, Object> plugins = new HashMap<String, Object>();

    public static Object getPlugin(Class iface)
    {

       Object plugin;// = plugins.get(iface.toString());
//        if (plugin != null)
//            return plugin;

        String implName = getProperty(iface.getName());
        if (implName == null)
        {
            throw new RuntimeException("implementation not specified for " + iface.getName() + " in PluginFactory propeties.");
        }
        try
        {
            plugin = Class.forName(implName).newInstance();
//            plugins.put(iface.toString(), plugin);
            return plugin;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("factory unable to construct instance of " + iface.getName());
        }
    }

    public static Object getPlugin(String pluginStr)
    {

        Object plugin = null;// = plugins.get(pluginStr);
//        if (plugin != null)
//            return plugin;
        String implName = getProperty(pluginStr);
        if (implName == null || implName.length() == 0)
        {
            throw new RuntimeException("implementation not specified for " + pluginStr + " in PluginFactory propeties.");
        }
        try
        {
            plugin = Class.forName(implName).newInstance();
//            plugins.put(pluginStr, plugin);
            return plugin;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("factory unable to construct instance of " + plugin);
        }
    }






    /**
     *
     * @param propertyPrefix to find
     * @return a list of indexText property names hat starts with the given prefix
     *
     */
    public static List<String> getListProperties(String propertyPrefix)
    {

        List<String> props = new ArrayList<String>();
        Enumeration<?> enumE;
        try
        {
            enumE = properties.propertyNames();
        }
        catch(Exception e)
        {
            logger.fatal("cant load properties");
            return null;
        }

        while (enumE.hasMoreElements())
        {
            String propertyName = (String)enumE.nextElement();
            if (propertyName.startsWith(propertyPrefix))
            {
                props.add(propertyName);
            }
        }
        return props;
    }

    /**
     *
     * @param propertyPrefix to find
     * @return a list of indexText property names hat starts with the given prefix
     *
     */
    public static List<String> getListValues(String propertyPrefix)
    {

        List<String> props = new ArrayList<String>();
        Enumeration<?> enumE;
        try
        {
            enumE = properties.propertyNames();
        }
        catch(Exception e)
        {
            logger.fatal("cant load properties");
            return null;
        }

        while (enumE.hasMoreElements())
        {
            String propertyName = (String)enumE.nextElement();
            if (propertyName.startsWith(propertyPrefix))
            {
                props.add(getProperty(propertyName));
            }
        }
        return props;
    }
}
