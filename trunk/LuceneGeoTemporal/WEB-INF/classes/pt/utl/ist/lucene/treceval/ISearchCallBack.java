package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @time 4:31:34
 * @see pt.utl.ist.lucene.treceval
 */
public interface ISearchCallBack
{
    public void searchCallback(Topic t);
}
