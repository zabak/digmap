package pt.utl.ist.lucene.treceval.handlers.collections;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.config.LocalProperties;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class CFileTypeFactory
{
    private static Logger logger = Logger.getLogger(CDirectory.class);

    static Properties fileTypes;

    static
    {
        try
        {
            fileTypes = new LocalProperties("pt/utl/ist/lucene/treceval/handlers/collections/cfiletype.properties");
        }
        catch (IOException e)
        {
            logger.fatal(e,e);
            System.exit(-1);
        }
    }

    public static CDocumentHandler createHandler(String fileName, Properties override)
    {
        String extension = Files.getExtension(fileName);
        String className = null;
        if(override != null)
        {
            className = override.getProperty(extension);
        }
        if(className == null)
        {

            className = fileTypes.getProperty(extension);
            if(className == null)
            {
                logger.fatal("File Type not Supported");
                return null;
            }
            try
            {
                return (CDocumentHandler) Class.forName(className).newInstance();
            }
            catch (InstantiationException e)
            {
                logger.error(e,e);
            }
            catch (IllegalAccessException e)
            {
                logger.error(e,e);
            }
            catch (ClassNotFoundException e)
            {
                logger.error(e,e);
            }
        }
        return null;
    }
}
