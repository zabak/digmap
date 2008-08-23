package pt.utl.ist.lucene.treceval.output;




import org.apache.lucene.document.Document;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:28:17
 * @see pt.utl.ist.lucene.treceval.output
 */
public interface OutputFormat
{
    public void writeHeader(int totalResults);
    public void writeRecord(Document d, float score, String run);
    public void writeFooter();
    public void setMaxDocsToFlush(int max);
}
