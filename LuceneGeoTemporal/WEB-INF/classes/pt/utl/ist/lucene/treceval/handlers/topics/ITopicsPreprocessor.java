package pt.utl.ist.lucene.treceval.handlers.topics;

import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.SearchConfiguration;

import java.net.MalformedURLException;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 2:26:07
 * @see pt.utl.ist.lucene.treceval
 */
public interface ITopicsPreprocessor
{
    public void handle(String collectionPath, ISearchCallBack callBack, String confId, String run,String collection,String outputDir, SearchConfiguration.TopicsConfiguration topicsConfiguration) throws MalformedURLException, DocumentException;
}
