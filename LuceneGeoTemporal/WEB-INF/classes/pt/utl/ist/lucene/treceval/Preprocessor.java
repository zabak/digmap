package pt.utl.ist.lucene.treceval;

import org.apache.lucene.index.IndexWriter;
import org.dom4j.DocumentException;

import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:26:07
 * @see pt.utl.ist.lucene.treceval
 */
public interface Preprocessor
{
    public void run(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException;
}
