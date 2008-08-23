package pt.utl.ist.lucene.treceval.preprocessors;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.Preprocessor;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class DirectoryPreprocessor implements Preprocessor
{

    private static Logger logger =Logger.getLogger(DirectoryPreprocessor.class);

    ResourceHandler resourceHandler;
    Properties fileTypesPlugins;

    public DirectoryPreprocessor(ResourceHandler resourceHandler,Properties fileTypesPlugins)
    {
        this.resourceHandler = resourceHandler;
        this.fileTypesPlugins = fileTypesPlugins;
    }

    public DirectoryPreprocessor(ResourceHandler resourceHandler)
    {
        this.resourceHandler = resourceHandler;
    }


    public void run(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {

        File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files)
        {
            logger.info("directory> handling (" + i + "): " + f.getName());
            DocumentHandler documentHandler = FileTypeFactory.createHandler(f.getName(),fileTypesPlugins);
            try
            {
                InputStream fileInputStream = new FileInputStream(f);
                documentHandler.handle(fileInputStream,resourceHandler,callBack, fileTypesPlugins);
                fileInputStream.close();
            }
            catch (FileNotFoundException e)
            {
                logger.error(e,e);
            }
            catch (IOException e)
            {
                logger.error(e,e);
            }
            i++;
        }
    }
}
