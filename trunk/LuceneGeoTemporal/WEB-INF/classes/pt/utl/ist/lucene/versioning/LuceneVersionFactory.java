package pt.utl.ist.lucene.versioning;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.config.LocalProperties;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LuceneVersionFactory {
	
    private static LuceneVersion luceneVersion = null;

    private static final Logger logger = Logger.getLogger(LuceneVersionFactory.class);

    public static LuceneVersion getLuceneVersion()
    {
        try
        {
            if (luceneVersion == null)
            {
                String className = new LocalProperties("pt/utl/ist/lucene/versioning/luceneversion.properties").getProperty("lucene.version");
                Class c = Class.forName(className);
                luceneVersion = (LuceneVersion) c.newInstance();
            }
        }
        catch (IOException e)
        {
            logger.fatal(e);
            System.exit(-1);

        }
        catch (IllegalAccessException e)
        {
            logger.fatal(e);
            System.exit(-1);
        }
        catch (InstantiationException e)
        {
            logger.fatal(e);
            System.exit(-1);
        }
        catch (ClassNotFoundException e)
        {
            logger.fatal(e);
            System.exit(-1);
        }
        return luceneVersion;
    }

}
