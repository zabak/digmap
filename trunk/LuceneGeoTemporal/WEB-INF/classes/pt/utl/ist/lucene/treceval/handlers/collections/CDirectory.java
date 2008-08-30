package pt.utl.ist.lucene.treceval.handlers.collections;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class CDirectory implements ICollectionPreprocessor
{

    private static Logger logger =Logger.getLogger(CDirectory.class);

    ResourceHandler resourceHandler;
    Properties fileTypesPlugins;

    public CDirectory(ResourceHandler resourceHandler,Properties fileTypesPlugins)
    {
        this.resourceHandler = resourceHandler;
        this.fileTypesPlugins = fileTypesPlugins;
    }

    public CDirectory(ResourceHandler resourceHandler)
    {
        this.resourceHandler = resourceHandler;
    }


    public void handle(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException
    {

        File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files)
        {
            logger.info("directory> handling (" + i + "): " + f.getName());
            CDocumentHandler documentHandler = CFileTypeFactory.createHandler(f.getName(),fileTypesPlugins);
            try
            {
                InputStream fileInputStream = new FileInputStream(f);
                documentHandler.handle(fileInputStream, collectionPath.substring(Globals.DATA_DIR.length()) + File.separator + f.getName(), resourceHandler,callBack, fileTypesPlugins);
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
