package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.treceval.Globals;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:29:25
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class TrecEvalOutputFormat implements OutputFormat
{

    private String idField1;
    private String idField2;

    private static int rank = 0;
    private int maxFlush = 100;
    private int done = 0;
    private static final Logger logger = Logger.getLogger(TrecEvalOutputFormat.class);

    private OutputStreamWriter writer;
    Topic topic;


    public TrecEvalOutputFormat()
    {

    }

    public void init(OutputStream outputStream,String idField1, String idField2)
    {
        this.idField1 = idField1;
        this.idField2 = idField2;
        setOutputStream(outputStream);
    }

    public void init(String idField1, String idField2)
    {
        this.idField1 = idField1;
        this.idField2 = idField2;
        if(idField1 == null && idField2 == null)
        {
            logger.warn("ID Field not Provided to Output Format, using default from treceval.Globals:" + Globals.DOCUMENT_ID_FIELD);
            this.idField1 = Globals.DOCUMENT_ID_FIELD;
        }
    }

    public void setOutputStream(OutputStream outputStream)
    {
        try
        {
            writer = new OutputStreamWriter(outputStream, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error(e, e);
        }
    }


    public void setTopic(DefaultTopic topic)
    {
        this.topic = topic;
    }

    /**
     * If you want to write some header like in an output XML file
     * @param totalResults to write
     */
    public void writeHeader(int totalResults)
    {
        rank = 0;
//        try
//        {
//            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
//            writer.write("<?xml-stylesheet href=\"response.xsl\" type=\"text/xsl\"?>\n");
//            writer.write("<output xmlns:mods=\"http://www.loc.gov/mods\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns=\"http://output.clef.2008/jorge\" type=\"clef2008\" totalResults=\"" + totalResults + "\">");
//            writer.flush();
//            topic.writeTopic(writer);
//        }
//        catch (IOException e)
//        {
//            logger.error(e,e);
//        }
    }

    public void writeRecord(int docId , Document d, float score, String run)
    {
        //351   0  FR940104-0-00001  1   42.38   handle-name
        try
        {

            String id = d.get(idField1);
            if (id == null)
                id = d.get(idField2);
            if (id == null)
                logger.error("Record " + d.get("id") + " come with null id");
            else
            {
                writer.write(topic.getIdentifier().trim());
                writer.write(" ");
                writer.write("Q0");
                writer.write(" ");
//                writer.write(id.trim() + "-docID-" + docId);
                writer.write(id.trim());
                writer.write(" ");
                writer.write("" + rank);
                rank++;
                writer.write(" ");
                writer.write("" + score);
                writer.write(" ");
                writer.write(run);
                writer.write('\n');
            }
            done++;
            if (done > maxFlush)
            {
                done = 0;
                writer.flush();
            }
        }
        catch (IOException e)
        {
            logger.error(e, e);
        }
    }

    /**
     * If you want to write some footer like in an output XML file
     */
    public void writeFooter()
    {

    }

    public void setMaxDocsToFlush(int max)
    {
        this.maxFlush = max;
    }

    public void setTopic(Topic topic)
    {
        this.topic = topic;
    }

    public void close()
    {
        try
        {
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            logger.error(e, e);
        }
    }
}
