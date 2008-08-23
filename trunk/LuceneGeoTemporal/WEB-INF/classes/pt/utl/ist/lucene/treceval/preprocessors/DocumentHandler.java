package pt.utl.ist.lucene.treceval.preprocessors;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.preprocessors
 */
public interface DocumentHandler
{
    public void handle(InputStream stream, ResourceHandler handler, IndexFilesCallBack callBack, Properties filehandlers) throws IOException;
}
