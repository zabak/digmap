package pt.utl.ist.lucene.treceval.handlers.topics;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.SearchConfiguration;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Open a Zip file and create handler for internal files
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class TZipHandler implements TDocumentHandler
{
    private static Logger logger = Logger.getLogger(CDirectory.class);

    public void handle(OutputFormatFactory factory, InputStream stream, String fromFile, ResourceHandler handler, ISearchCallBack callBack, Properties filehandlers, String confId, String run,String collection, String outputDir, SearchConfiguration.TopicsConfiguration topicsConfiguration) throws IOException
    {
        ZipInputStream zipInputStream = new ZipInputStream(stream);
        ZipEntry ze;
        while((ze = zipInputStream.getNextEntry()) !=  null)
        {
            logger.info("zip handling:" + ze.getName());
            TDocumentHandler documentHandler = TFileTypeFactory.createHandler(ze.getName(),null);
            if(documentHandler==null)
            {
                logger.error("Cant handle document:" +ze.getName());
            }
            else
            {
                documentHandler.handle(factory, zipInputStream,fromFile + "/" + ze.getName(), handler,callBack, filehandlers,confId,run,collection,outputDir, topicsConfiguration);
            }
        }
    }

}
