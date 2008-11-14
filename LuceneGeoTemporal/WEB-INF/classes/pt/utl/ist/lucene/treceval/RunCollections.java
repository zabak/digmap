package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.dom4j.DocumentException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.io.IOException;
import java.io.File;

import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.LgteDocumentWrapper;

/**
 * @author Jorge Machado
 * @date 21/Abr/2008
 * @time 11:00:47
 * @see pt.utl.ist.lucene.treceval
 */
public class RunCollections implements IndexFilesCallBack
{

    private static final Logger logger = Logger.getLogger(RunCollections.class);

    String idField;

    public RunCollections(String idField)
    {
        this.idField = idField;
    }

    /**
     * Index all Configuration Indexes
     * @throws java.io.IOException writing files
     * @throws org.dom4j.DocumentException opening XML documents
     * @param configurations to index
     * @param idField to indentify document
     */

    public static void runConfiguration(List<Configuration> configurations, String idField) throws IOException, DocumentException
    {
        for (Configuration c : configurations)
        {
            new RunCollections(idField).run(c);
        }
    }

    /**
     * This is the main method
     * Will call index to each Configuration and pass callback handler so Processors can pass us the preprocessed document to index
     * @param c configuration
     * @throws java.io.IOException writing indexes
     * @throws org.dom4j.DocumentException opening XML
     */
    public void run(Configuration c) throws IOException, DocumentException
    {
        c.getPreprocessor().handle(c.getCollectionPath(), this);
    }


    public void indexDoc(String id, Map<String, String> textFields, Map<String, String> storedFields, Collection<Field> uniqueFields) throws IOException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
