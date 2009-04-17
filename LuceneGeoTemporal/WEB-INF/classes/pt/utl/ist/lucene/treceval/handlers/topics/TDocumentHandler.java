package pt.utl.ist.lucene.treceval.handlers.topics;

import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.treceval.handlers.ResourceHandler;
import pt.utl.ist.lucene.treceval.ISearchCallBack;
import pt.utl.ist.lucene.treceval.SearchConfiguration;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormatFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers
 */
public interface TDocumentHandler
{
    public void handle(OutputFormatFactory factory, InputStream stream, String fromFile, ResourceHandler handler, ISearchCallBack callBack, Properties filehandlers, String confId, String run,String collection, String outputDir, QueryConfiguration topicsConfiguration) throws IOException;
}
