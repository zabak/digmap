package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

import java.util.Map;

/**
 * Provides a simple Fields Topic
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:36:00
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class FieldsTopic implements Topic
{

    String identifier;
    String query;
    OutputFormat outputFormat;


    public FieldsTopic(String identifier, Map<String,String> map, OutputFormat outputFormat)
    {
        this.identifier = identifier;
        this.outputFormat = outputFormat;
        this.outputFormat.setTopic(this);
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String,String> entry: map.entrySet())
        {
            builder.append(entry.getKey())
                    .append(":(")
                    .append(entry.getValue()
                            .replace("(","\\(")
                            .replace(")","\\)")
                            .replace("[","\\[")
                            .replace("]","\\]"
                            .replace(":","\\:")))
                    .append(") ");
        }
        query = builder.toString();
    }


    public String getIdentifier() {
        return identifier;
    }



    public String getQuery()
    {
        return query;
    }

    public OutputFormat getOutputFormat()
    {
        return outputFormat;
    }
}
