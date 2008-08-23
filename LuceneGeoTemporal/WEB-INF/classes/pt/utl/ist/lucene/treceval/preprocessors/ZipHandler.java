package pt.utl.ist.lucene.treceval.preprocessors;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;

/**
 * Open a Zip file and create handler for internal files
 * 
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public class ZipHandler implements DocumentHandler
{
    private static Logger logger = Logger.getLogger(DirectoryPreprocessor.class);

    public void handle(InputStream stream, ResourceHandler handler,IndexFilesCallBack callBack, Properties filehandlers) throws IOException
    {
        ZipInputStream zipInputStream = new ZipInputStream(stream);
        ZipEntry ze;
        while((ze = zipInputStream.getNextEntry()) !=  null)
        {
            logger.info("zip handling:" + ze.getName());
            DocumentHandler documentHandler = FileTypeFactory.createHandler(ze.getName(),null);
            if(documentHandler==null)
            {
                logger.error("Cant handle document:" +ze.getName());
            }
            else
            {
                documentHandler.handle(zipInputStream,handler,callBack, filehandlers);
            }
        }
    }

}