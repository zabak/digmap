package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.Token;
import pt.utl.ist.lucene.treceval.SearchConfiguration;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.utils.QueryUtils;

import java.io.IOException;
import java.io.StringReader;
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

    private static final Logger logger = Logger.getLogger(FieldsTopic.class);

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



                Float boost = topicsConfiguration.getFieldBoost().get(entry.getKey());
                if(boost != null)
                {
                    StandardTokenizer tokenizer = new StandardTokenizer(new StringReader(escapedQuery));

                    Token t;
                    try
                    {
                        while((t = tokenizer.next()) != null)
                        {
                            builder.append(entry.getKey()).append(":").append(t.termText()).append("^").append(boost).append(" ");
                        }
                    }
                    catch (IOException e)
                    {
                        logger.error(e,e);
                    }
                }
                else
                {
                    addField(builder,entry.getKey(),escapedQuery);
                    builder.append(" ");
                }
            }
            else if(topicsConfiguration == null)
            {
                addField(builder,entry.getKey(),escapedQuery);
                builder.append(" ");
            }

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
