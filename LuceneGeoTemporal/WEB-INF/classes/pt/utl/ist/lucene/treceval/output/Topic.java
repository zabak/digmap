package pt.utl.ist.lucene.treceval.output;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:34:53
 * @see pt.utl.ist.lucene.treceval.output
 */
public interface Topic
{
    public String getQuery();
    public OutputFormat getOutputFormat(OutputStream outputStream);
}
