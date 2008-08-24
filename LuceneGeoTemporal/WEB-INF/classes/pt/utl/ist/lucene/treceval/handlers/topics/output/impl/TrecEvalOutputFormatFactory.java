package pt.utl.ist.lucene.treceval.handlers.topics.output.impl;

import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.output.OutputFormat;

import java.io.OutputStream;

/**
 * @author Jorge Machado
 * @date 22/Ago/2008
 * @see pt.utl.ist.lucene.treceval.handlers.topics.output.impl
 */
public class TrecEvalOutputFormatFactory implements OutputFormatFactory
{

    String luceneDocumentIdField1;
    String luceneDocumentIdField2;

    public TrecEvalOutputFormatFactory(String luceneDocumentIdField)
    {
        this.luceneDocumentIdField1 = luceneDocumentIdField;
    }

    public TrecEvalOutputFormatFactory(String possibleLuceneDocumentIdField1, String possibleLuceneDocumentIdField2)
    {
        this.luceneDocumentIdField1 = possibleLuceneDocumentIdField1;
        this.luceneDocumentIdField2 = possibleLuceneDocumentIdField2;
    }

    public OutputFormat createNew(OutputStream outputStream)
    {
        TrecEvalOutputFormat trecEvalOutputFormat = new TrecEvalOutputFormat();
        trecEvalOutputFormat.init(luceneDocumentIdField1,luceneDocumentIdField2);
        trecEvalOutputFormat.setOutputStream(outputStream);
        trecEvalOutputFormat.setMaxDocsToFlush(1000);
        return trecEvalOutputFormat;
    }
}
