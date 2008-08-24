package pt.utl.ist.lucene.treceval.handlers.collections;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public interface CDocumentHandler
{
    public void handle(InputStream stream,String filePath, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers) throws IOException;
}
