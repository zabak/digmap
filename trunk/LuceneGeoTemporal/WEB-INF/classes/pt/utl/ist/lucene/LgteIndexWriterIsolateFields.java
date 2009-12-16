package pt.utl.ist.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.versioning.LuceneVersion;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;

import java.io.*;
import java.util.*;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexWriterIsolateFields {

    Collection<String> targetFields = null;

    Map<String,LgteIndexWriter> writers = new HashMap<String,LgteIndexWriter>();

    String path;
    Analyzer analyzer = new LgteAnalyzer();
    boolean writeNew = true;
    Model model = Model.defaultModel;

    public LgteIndexWriterIsolateFields(String path, Analyzer analyzer, boolean writeNew)
            throws IOException {
        this.path = path;
        this.analyzer = analyzer;
        this.writeNew = writeNew;
    }

    public LgteIndexWriterIsolateFields(String path, boolean writeNew)
            throws IOException
    {
        this.path = path;
        this.writeNew = writeNew;
    }

    public LgteIndexWriterIsolateFields(String path, boolean writeNew, Model model)
            throws IOException
    {
        this.path = path;
        this.writeNew = writeNew;
        this.model = model;
    }

    public LgteIndexWriterIsolateFields(String path, Analyzer analyzer, boolean writeNew, Model model)
            throws IOException
    {
        this.path = path;
        this.analyzer = analyzer;
        this.writeNew = writeNew;
        this.model = model;
    }

    public LgteIndexWriterIsolateFields(File path, Analyzer analyzer, boolean writeNew)
            throws IOException
    {
        this(path.getAbsolutePath(),analyzer,writeNew);
    }

    public LgteIndexWriterIsolateFields(File path, Analyzer analyzer, boolean writeNew, Model model) throws IOException
    {
        this(path.getAbsolutePath(),analyzer,writeNew,model);
    }

    /**
     * will index the document in all the available indexes
     * buiding a sub-Document for each one of them
     * The subDocument which goes to each writer
     * will include the ID field and the target field of the writer
     *
     * @param writers Mapping the name of the field with the Writer
     */
    public LgteIndexWriterIsolateFields(Map<String,LgteIndexWriter> writers)
            throws IOException {
        this.writers = writers;
        this.targetFields = writers.keySet();
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields, String path, Analyzer analyzer, boolean writeNew)
            throws IOException {
        this.path = path;
        this.analyzer = analyzer;
        this.writeNew = writeNew;
        this.targetFields = targetFields;
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields,String path, boolean writeNew)
            throws IOException
    {
        this.path = path;
        this.writeNew = writeNew;
        this.targetFields = targetFields;
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields,String path, boolean writeNew, Model model)
            throws IOException
    {
        this.path = path;
        this.writeNew = writeNew;
        this.model = model;
        this.targetFields = targetFields;
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields,String path, Analyzer analyzer, boolean writeNew, Model model)
            throws IOException
    {
        this.path = path;
        this.analyzer = analyzer;
        this.writeNew = writeNew;
        this.model = model;
        this.targetFields = targetFields;
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields,File path, Analyzer analyzer, boolean writeNew)
            throws IOException
    {
        this(path.getAbsolutePath(),analyzer,writeNew);
        this.targetFields = targetFields;
    }

    /**
     * will index the document in one index for each target field
     * using the ID field and the target field
     * even if the document doesn't have that field
     * In those cases the document will be indexed only with ID field
     */
    public LgteIndexWriterIsolateFields(List<String> targetFields,File path, Analyzer analyzer, boolean writeNew, Model model) throws IOException
    {
        this(path.getAbsolutePath(),analyzer,writeNew,model);
        this.targetFields = targetFields;
    }



    public void close() throws IOException
    {
        for(Map.Entry<String,LgteIndexWriter> entry: writers.entrySet())
        {
            writers.get(entry.getKey()).close();
        }
    }

    private LgteIndexWriter getWriter(String field) throws IOException
    {
        LgteIndexWriter writer = writers.get(field);
        if(writer == null)
        {
            writer = new LgteIndexWriter(path + File.separator + field + "_field",writeNew,model);
            writers.put(field,writer);
        }
        return writer;
    }



    public void addDocument(LgteDocumentWrapper documentWrapper) throws IOException
    {
        if(targetFields == null)
        {
            Enumeration e = documentWrapper.getDocument().fields();
            while(e.hasMoreElements())
            {
                Field field = (Field) e.nextElement();
                if(!field.name().equals(Globals.DOCUMENT_ID_FIELD))
                {
                    Document d = new Document();
                    d.add(field);
                    d.add(documentWrapper.getId());
                    getWriter(field.name()).addDocument(d);
                }
            }
        }
        else
        {
            for(String targetField: targetFields)
            {
                Field field = documentWrapper.getDocument().getField(targetField);
                Document d = new Document();
                if(field!=null)
                    d.add(field);
                d.add(documentWrapper.getId());
                getWriter(targetField).addDocument(d);
            }
        }
    }

    public void addDocument(LgteDocumentWrapper documentWrapper, Analyzer analyzer) throws IOException
    {
        if(targetFields == null)
        {
            Enumeration e = documentWrapper.getDocument().fields();
            while(e.hasMoreElements())
            {
                Field field = (Field) e.nextElement();
                if(!field.name().equals(Globals.DOCUMENT_ID_FIELD))
                {
                    Document d = new Document();
                    d.add(field);
                    d.add(documentWrapper.getId());
                    getWriter(field.name()).addDocument(d,analyzer);
                }
            }
        }
        else
        {
            for(String targetField: targetFields)
            {
                Field field = documentWrapper.getDocument().getField(targetField);
                Document d = new Document();
                if(field!=null)
                    d.add(field);
                d.add(documentWrapper.getId());
                getWriter(targetField).addDocument(d,analyzer);
            }
        }
    }



}
