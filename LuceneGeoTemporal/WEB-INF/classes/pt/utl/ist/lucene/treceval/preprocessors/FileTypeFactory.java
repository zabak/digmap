package pt.utl.ist.lucene.treceval.preprocessors;

import pt.utl.ist.lucene.utils.Files;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class FileTypeFactory
{
    private static Logger logger = Logger.getLogger(DirectoryPreprocessor.class);

    static Properties fileTypes;  
    
    static
    {
        try
        {
        	fileTypes = new Properties();
            fileTypes.load(FileTypeFactory.class.getResourceAsStream("filetype.properties"));
        }
        catch (IOException e)
        {
            logger.fatal(e,e);
            System.exit(-1);
        }
    }

    public static DocumentHandler createHandler(String fileName, Properties override)
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
                return (DocumentHandler) Class.forName(className).newInstance();
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
