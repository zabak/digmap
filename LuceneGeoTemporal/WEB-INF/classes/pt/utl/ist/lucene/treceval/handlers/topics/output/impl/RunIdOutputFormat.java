package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import pt.utl.ist.lucene.treceval.Globals;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:29:25
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class RunIdOutputFormat implements OutputFormat {

    private String idField1;
    private String idField2;

    private static int rank = 0;
    private int maxFlush = 100;
    private int done = 0;
    private static final Logger logger = Logger.getLogger(RunIdOutputFormat.class);

    private OutputStreamWriter writer;
    Topic topic;

    String runId;
    String desc;

    public RunIdOutputFormat(String runId,String desc)
    {
        this.runId = runId;
        this.desc = desc; 
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
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<TOPIC_SET>\n");
            writer.write("  <METADATA>\n" +
                         "    <RUNID>" + runId  + "</RUNID>\n" +
                         "    <DESCRIPTION>" + desc + "</DESCRIPTION>\n" +
                         "    <RUN_TYPE>Automatic</RUN_TYPE>\n" + 
                         "  </METADATA>\n");
        }
        catch (IOException e)
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
        rank = 1;
        try{
            writer.write(
                "  <TOPIC ID=\"GEOTIME-EN-T" + topic.getIdentifier().trim().substring("GeoTime-".length()) + "\">\n" +
                "    <GEOTIME_RESULT>\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void writeRecord(String docId, int hit, Hits hits, float score, String run)
    {
        //351   0  FR940104-0-00001  1   42.38   handle-name
        try
        {
            String id = docId;
            if(docId == null)
            {
                Document d = hits.doc(hit);
                id = d.get(Globals.DOCUMENT_ID_FIELD);

                if (id == null)
                    id = d.get(idField1);
                if (id == null)
                    id = d.get(idField2);
                if (id == null)
                    logger.error("Record " + d.get("id") + " come with null id");
            }

            if(id != null)
            {
                logger.debug("TRECEVAL OUTPUT: " + topic.getIdentifier().trim() + " Q0 " + id + " " + rank + " " + score + " " + run);
                writer.write("      <DOCUMENT RANK=\"" + rank + "\" DOCID=\"" + id.trim() + "\" SCORE=\"" + score + "\"/>\n");
            }
            rank++;
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
           try{
            writer.write(
                "    </GEOTIME_RESULT>\n" +
                "  </TOPIC>\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
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
            writer.write("</TOPIC_SET>\n");
        } catch (IOException e)
        {
            logger.error(e,e);
        }
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
