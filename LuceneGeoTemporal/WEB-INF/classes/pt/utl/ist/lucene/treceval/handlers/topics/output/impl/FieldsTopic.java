package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.config.PropertiesUtil;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;
import pt.utl.ist.lucene.utils.QueryUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.List;


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
            Properties p = null;
            Analyzer a = null;
            if(queryConfiguration != null)
            {
                p = queryConfiguration.getQueryProperties();
                a = queryConfiguration.getAnalyzer();
            }
            String subQuery = expandQueryInFields(p,escapedQuery,entry.getKey(),a);
            if(subQuery != null)
                builder.append(subQuery);
        }
        query = builder.toString();
    }

    public static String expandQueryInFields(Properties props, String query,  Analyzer analyzer)
    {
        List<String> fields = PropertiesUtil.getListPropertiesSuffix(props, "field.boost.");
        if(fields.size() > 0)
        {
            StringBuilder builder = new StringBuilder();
            String escapedQuery = QueryUtils.escapeSpecialChars(query);
            for(String field: fields)
            {
                String subQuery = expandQueryInFields(props,escapedQuery,field,analyzer);
                if(subQuery != null)
                    builder.append(subQuery);
            }
            return builder.toString();
        }
        return null;
    }
    public static String expandQueryInFields(Properties props, String query, String field, Analyzer analyzer)
    {
        String boostStr;
        if(props != null && (boostStr = props.getProperty("field.boost." + field)) != null)
        {
            Float boost =  Float.parseFloat(boostStr);
            TokenStream stream = analyzer.tokenStream(field,new StringReader(query));
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
                    return new StringBuilder()
                            .append(" ")
                            .append(field)
                            .append(":(")
                            .append(sub.toString())
                            .append(")^")
                            .append(boost)
                            .append(" ").toString();
                }
                else
                    return null;
            }
            catch (IOException e)
            {
                logger.error(e,e);
                return null;
            }
        }
        else
        {
            return
                    new StringBuilder()
                            .append(" ")
                            .append(field)
                            .append(":(")
                            .append(query)
                            .append(")")
                            .append(" ")
                            .toString();
        }

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
