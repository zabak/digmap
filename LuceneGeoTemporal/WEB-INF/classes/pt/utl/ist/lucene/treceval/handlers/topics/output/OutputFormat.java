package pt.utl.ist.lucene.treceval.handlers.topics.output;




import org.apache.lucene.document.Document;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:28:17
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output
 */
public interface OutputFormat
{
    public void init(String idField1, String idField2);
    public void setOutputStream(OutputStream outputStream);
    public void writeHeader(int totalResults);
    public void writeRecord(int docId, Document d, float score, String run);
    public void writeFooter();
    public void setMaxDocsToFlush(int max);
    public void setTopic(Topic topic);
    public void close();
}
