package pt.utl.ist.lucene.treceval.handlers.topics.output;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output
 */
public interface OutputFormatFactory
{
    public OutputFormat createNew(OutputStream outputStream);
}
