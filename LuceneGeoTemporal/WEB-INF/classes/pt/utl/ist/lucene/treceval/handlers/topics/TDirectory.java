package pt.utl.ist.lucene.treceval.handlers.topics;

import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import java.util.Properties;
import java.net.MalformedURLException;
import java.io.*;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class TDirectory implements ITopicsPreprocessor
{

    private static Logger logger = Logger.getLogger(TDirectory.class);

    ResourceHandler resourceHandler;
    Properties fileTypesPlugins;
    OutputFormatFactory outputFormatFactory;


    public TDirectory(ResourceHandler resourceHandler, OutputFormatFactory outputFormatFactory)
    {
        this.resourceHandler = resourceHandler;
        this.outputFormatFactory = outputFormatFactory;
    }


    public void handle(String collectionPath, ISearchCallBack callBack, String confId, String run,String collection,String outputDir, QueryConfiguration topicsConfiguration) throws MalformedURLException, DocumentException
    {

        File dir = new File(collectionPath);
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files)
        {
            logger.info("topics directory> handling (" + i + "): " + f.getName());
            TDocumentHandler documentHandler = TFileTypeFactory.createHandler(f.getName(),fileTypesPlugins);
            try
            {
                InputStream fileInputStream = new FileInputStream(f);
                documentHandler.handle(outputFormatFactory,fileInputStream,f.getName(),resourceHandler,callBack, fileTypesPlugins,confId,run,collection,outputDir, topicsConfiguration);
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
