package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import pt.utl.ist.lucene.QueryConfiguration;
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


    public FieldsTopic(String identifier, Map<String,String> map, OutputFormat outputFormat, QueryConfiguration queryConfiguration)
    {
        this.identifier = identifier;
        this.outputFormat = outputFormat;
        this.outputFormat.setTopic(this);
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String,String> entry: map.entrySet())
        {
            String escapedQuery = QueryUtils.escapeSpecialChars(entry.getValue());

            /*In cases that we have boosted fields we will split the query and build a term independent query with the boost fields*/
            if(queryConfiguration != null && queryConfiguration.getQueryProperties() != null)
            {
            	String boostStr = queryConfiguration.getQueryProperties().getProperty("field.boost." + entry.getKey());
                if(boostStr != null)
                {
                	Float boost =  Float.parseFloat(boostStr);
                    TokenStream stream = queryConfiguration.getAnalyzer().tokenStream(entry.getKey(),new StringReader(entry.getValue()));
                    Token t;
                    try
                    {
                        StringBuilder sub = new StringBuilder();
                        while((t = stream.next()) != null)
                        {
                            sub.append(t.termText()).append(" ");
                        }
                        if(sub.toString().trim().length() > 0)
                        {
                            builder.append(entry.getKey())
                                    .append(":(")
                                    .append(sub.toString())
                                    .append(")^")
                                    .append(boost)
                                    .append(" ");
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
            else if(queryConfiguration == null)
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
