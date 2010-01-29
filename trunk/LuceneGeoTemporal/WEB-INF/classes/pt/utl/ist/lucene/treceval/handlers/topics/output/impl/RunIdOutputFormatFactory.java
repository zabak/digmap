package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class RunIdOutputFormatFactory implements OutputFormatFactory {

    String luceneDocumentIdField1;
    String luceneDocumentIdField2;
    String runId;
    String desc;

    public RunIdOutputFormatFactory(String luceneDocumentIdField,String runId,String desc)
    {
        this.luceneDocumentIdField1 = luceneDocumentIdField;
        this.runId = runId;
        this.desc = desc;
    }

    public RunIdOutputFormatFactory(String possibleLuceneDocumentIdField1, String possibleLuceneDocumentIdField2)
    {
        this.luceneDocumentIdField1 = possibleLuceneDocumentIdField1;
        this.luceneDocumentIdField2 = possibleLuceneDocumentIdField2;
    }

    public OutputFormat createNew(OutputStream outputStream)
    {
        RunIdOutputFormat runIdOutputFormat = new RunIdOutputFormat(runId,desc);
        runIdOutputFormat.init(luceneDocumentIdField1,luceneDocumentIdField2);
        runIdOutputFormat.setOutputStream(outputStream);
        runIdOutputFormat.setMaxDocsToFlush(1000);
        return runIdOutputFormat;
    }
}
