package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;
import pt.utl.ist.lucene.treceval.handlers.topics.output.Topic;

/**
 * @author Jorge Machado
 * @date 11/Mai/2008
 * @time 10:36:00
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class DefaultTopic implements Topic
{

//    private static final Logger logger = Logger.getLogger(DefaultTopic.class);

    private String identifier;
    private String title;
    private String description;
    private String lang;
    TrecEvalOutputFormat trecEvalOutputFormat;


    public DefaultTopic(String identifier, String title, String description, String lang)
    {
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.lang = lang;
    }

     public DefaultTopic(String identifier, String title, String description, String lang, TrecEvalOutputFormat trecEvalOutputFormat)
    {
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.lang = lang;
        this.trecEvalOutputFormat = trecEvalOutputFormat;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLang() {
        return lang;
    }

    public String getQuery()
    {
        return title + " " + title + " " + description;
//        return expansionQuery.expand();
    }

    public OutputFormat getOutputFormat()
    {
        trecEvalOutputFormat.setTopic(this);
        return trecEvalOutputFormat;
    }
}
