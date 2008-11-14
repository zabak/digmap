package pt.utl.ist.lucene.treceval.handlers.collections;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.GZIPInputStream;

import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.Globals;

/**
 * Open a Zip file and create handler for internal files
 *
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public class CGZipHandler implements CDocumentHandler
{
    private static Logger logger = Logger.getLogger(CDirectory.class);

    public void handle(InputStream stream,String filePath, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers) throws IOException
    {
        GZIPInputStream zipInputStream = new GZIPInputStream(stream);

        logger.info("GZip handling");
        CDocumentHandler documentHandler = CFileTypeFactory.createHandler(new File(filePath).getName() + "." + Globals.GZipDefaultContentType,null);
        if(documentHandler==null)
        {
            logger.error("Cant handle document:" +new File(filePath).getName());
        }
        else
        {
            documentHandler.handle(zipInputStream, filePath + "." + Globals.GZipDefaultContentType , handler,callBack, filehandlers);
        }

    }

}
