package pt.utl.ist.lucene.treceval.handlers.collections;

import org.apache.lucene.index.IndexWriter;
import org.dom4j.DocumentException;

import java.net.MalformedURLException;
import java.util.Properties;

import pt.utl.ist.lucene.treceval.IndexFilesCallBack;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:26:07
 * @see pt.utl.ist.lucene.treceval
 */
public interface ICollectionPreprocessor
{
    public void handle(String collectionPath, IndexFilesCallBack callBack) throws MalformedURLException, DocumentException;
}
