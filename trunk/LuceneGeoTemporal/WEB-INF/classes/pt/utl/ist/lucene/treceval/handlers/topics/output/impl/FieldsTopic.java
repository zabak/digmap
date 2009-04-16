package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.treceval.SearchConfiguration;
import pt.utl.ist.lucene.utils.QueryUtils;

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


    public FieldsTopic(String identifier, Map<String,String> map, OutputFormat outputFormat, SearchConfiguration.TopicsConfiguration topicsConfiguration)
    {
        this.identifier = identifier;
        this.outputFormat = outputFormat;
        this.outputFormat.setTopic(this);
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String,String> entry: map.entrySet())
        {
            String escapedQuery = QueryUtils.escapeSpecialChars(entry.getValue());

            if(topicsConfiguration != null && topicsConfiguration.getFieldBoost() != null)
            {
                addField(builder,entry.getKey(),escapedQuery);
                Float boost = topicsConfiguration.getFieldBoost().get(entry.getKey());
                if(boost != null)
                {
                    builder.append("^").append(boost);
                }
            }
            else if(topicsConfiguration == null)
            {
                addField(builder,entry.getKey(),escapedQuery);
            }
            builder.append(" ");
        }
        query = builder.toString();
    }

    private void addField(StringBuilder builder, String field, String query)
    {
        builder.append(field)
                .append(":(")
                .append(query)
                .append(")");
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
