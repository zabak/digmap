package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.treceval.output.Topic;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:31:34
 * @see pt.utl.ist.lucene.treceval
 */
public interface ISearchCallBack
{
    public void searchCallback(Topic t, OutputStream stream, int maxDocsFlush, int maxResultsForOutput);
}
