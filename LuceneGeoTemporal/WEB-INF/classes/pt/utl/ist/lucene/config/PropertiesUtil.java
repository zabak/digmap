package pt.utl.ist.lucene.config;

import org.apache.log4j.Logger;

import pt.utl.ist.lucene.utils.StringComparator;

import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Jorge
 * @date 17/Mar/2009
 * @time 16:36:07
 */
public class PropertiesUtil
{
    private static final Logger logger = Logger.getLogger(PropertiesUtil.class);

    public static String getProperty(Properties properties,String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return null;
        }
        String return_property = properties.getProperty(property);
        if (return_property == null)
        {
            logger.warn("Warning: Null property: " + property);
            return property;
        }

        return return_property;
    }

    public static int getIntProperty(Properties properties,String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return -1;
        }

        String stringValue = properties.getProperty(property);
        int intValue = 0;

        if (stringValue != null)
        {
            try
            {
                intValue = new Integer(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return intValue;
    }

    public static long getLongProperty(Properties properties,String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return -1;
        }

        String stringValue = properties.getProperty(property);
        long longValue = 0;

        if (stringValue != null)
        {
            try
            {
                longValue = new Long(stringValue).intValue();
            }
            catch (NumberFormatException e)
            {
                logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return longValue;
    }

    public static float getFloatProperty(Properties properties,String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return -1;
        }

        String stringValue = properties.getProperty(property);
        float floatValue = 0;

        if (stringValue != null)
        {
            try
            {
                floatValue = new Float(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return floatValue;
    }

    public static double getDoubleProperty(Properties properties,String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return -1;
        }

        String stringValue = properties.getProperty(property);
        double doubleValue = 0;

        if (stringValue != null)
        {
            try
            {
                doubleValue = new Float(stringValue).doubleValue();
            }
            catch (NumberFormatException e)
            {
                logger.warn("Warning: Number format exception in property: " + property);
            }
        }

        return doubleValue;
    }
     public static boolean getBooleanProperty(Properties properties, String property)
    {
        if (properties == null)
        {
            logger.fatal("Warning: Null property: " + property);
            return false;
        }

        String stringValue = properties.getProperty(property);
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
     
     /**
     *
     * @param p properties in use
     * @param propertyPrefix to find
     * @return a list of all property names hat starts with the given prefix
     *
     */
    public static List<String> getListPropertiesSuffix(Properties p, String propertyPrefix)
    {

        List<String> props = new ArrayList<String>();
        Enumeration<?> enumE;
       
        enumE = p.propertyNames();
        while (enumE.hasMoreElements())
        {
            String propertyName = (String)enumE.nextElement();
            if (propertyName.startsWith(propertyPrefix))
            {
                props.add(propertyName.substring(propertyPrefix.length()));
            }
        }
        Collections.sort(props, StringComparator.getInstance());
        return props;
    }
     
     /**
     *
     * @param p properties in use
     * @param propertyPrefix to find
     * @return a list of all property names hat starts with the given prefix
     *
     */
    public static List<String> getListProperties(Properties p, String propertyPrefix)
    {

        List<String> props = new ArrayList<String>();
        Enumeration<?> enumE;
       
        enumE = p.propertyNames();
        while (enumE.hasMoreElements())
        {
            String propertyName = (String)enumE.nextElement();
            if (propertyName.startsWith(propertyPrefix))
            {
                props.add(propertyName);
            }
        }
        Collections.sort(props, StringComparator.getInstance());
        return props;
    }

    /**
     * @param p to use
     * @param propertyPrefix to find
     * @return a list of values
     */
    public static List<String> getListValuesOrderedByPropertyName(Properties p, String propertyPrefix)
    {
        List<String> props = new ArrayList<String>();
        List<String> properties = getListProperties(p,propertyPrefix);
        for (Object property1 : properties)
        {
            String property = (String) property1;
            props.add(p.getProperty(property));
        }
        return props;
    }

    /**
     * @param p to use
     * @param propertyPrefix to find
     * @return a list of values
     */
    public static List<String> getListValuesOrderedByValue(Properties p, String propertyPrefix)
    {
        List<String> props = new ArrayList<String>();
        List<String> properties = getListProperties(p,propertyPrefix);
        for (Object property1 : properties)
        {
            String property = (String) property1;
            props.add(p.getProperty(property));
        }
        Collections.sort(props,StringComparator.getInstance());
        return props;
    }



}
