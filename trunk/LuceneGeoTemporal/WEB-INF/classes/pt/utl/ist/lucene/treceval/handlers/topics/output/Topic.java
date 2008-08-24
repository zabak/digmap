package pt.utl.ist.lucene.treceval.handlers.topics.output;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:34:53
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output
 */
public interface Topic
{
    public String getIdentifier();
    public String getQuery();
    public OutputFormat getOutputFormat();
}
