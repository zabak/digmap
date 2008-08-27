package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * @author Jorge Machado
 * @date 21/Abr/2008
 * @time 11:00:47
 * @see pt.utl.ist.lucene.treceval
 */
public class IndexCollections implements IndexFilesCallBack
{

    private static final Logger logger = Logger.getLogger(IndexCollections.class);

    private static List<Document> docs = new ArrayList<Document>();

    static boolean storeTermVectors = false;
    static int count = 0;
    static int maxDocsLocalBuffer = 50000;
    IndexWriter writer;
    String idField;

    public static LgteAnalyzerManager.LanguagePackage pt;
    public static LgteAnalyzerManager.LanguagePackage en;
    public static LgteAnalyzerManager.LanguagePackage fr;
    public static LgteAnalyzerManager.LanguagePackage de;
    public static LgteAnalyzerManager.LanguagePackage du;

    /**
     * Language Packages provide Analyser with stemming and with out stemming with a given list of stopwords
     * See etc package. All stopwords will be copied to build dir in order to be independent of the platform
     */
    static
    {
        try
        {
            pt = LgteAnalyzerManager.getInstance().getLanguagePackage("Portuguese", "stopwords_por.txt");
            en = LgteAnalyzerManager.getInstance().getLanguagePackage("English", "snowball-english.list");
            fr = LgteAnalyzerManager.getInstance().getLanguagePackage("French", "snowball-french.list");
            de = LgteAnalyzerManager.getInstance().getLanguagePackage("German", "snowball-german.list");
            du = LgteAnalyzerManager.getInstance().getLanguagePackage("Dutch", "snowball-dutch.list");
        }
        catch (IOException e)
        {
            logger.error(e,e);
        }
    }

    public IndexCollections(String idField)
    {
        this.idField = idField;
    }

    /**
     * Index all Configuration Indexes
     * @throws IOException writing files
     * @throws DocumentException opening XML documents
     * @param configurations to index
     * @param idField to indentify document
     */

    public static void indexConfiguration(List<Configuration> configurations, String idField) throws IOException, DocumentException
    {
        for (Configuration c : configurations)
        {
            new IndexCollections(idField).index(c);
        }
    }

    /**
     * This is the main method
     * Will call index to each Configuration and pass callback handler so Processors can pass us the preprocessed document to index
     * @param c configuration
     * @throws IOException writing indexes
     * @throws DocumentException opening XML
     */
    public void index(Configuration c) throws IOException, DocumentException
    {
        new File(c.getIndexPath()).mkdirs();
        //using LGTE writer to abstract from index Model, Temporal and Spatial details.
        writer = new LgteIndexWriter(c.getIndexPath(), c.getAnalyzer(), true, c.getModel());
        c.getPreprocessor().handle(c.getCollectionPath(), this);
        indexDocs(writer);
        docs.clear();
        writer.close();
    }


    /**
     * index a document in index writer
     * @param id identifier field in use
     * @param indexFields to add in document
     * @param uniqueFields to add to index
     * @throws IOException writing index
     */
    public void indexDoc(String id, Map<String,String> indexFields, Collection<Field> uniqueFields) throws IOException
    {
        LgteDocumentWrapper d = new LgteDocumentWrapper();
        d.addField(idField, id, true, false, false);
        for (Map.Entry<String,String> entry : indexFields.entrySet())
        {
            d.addField(entry.getKey(), entry.getValue(), true, true, true, true);
            if(uniqueFields!=null)
                d.addFields(uniqueFields);
        }
        try
        {
            docs.add(d.getDocument());
            tryWriteDocs(writer);
        }
        catch (Throwable e)
        {
            logger.error(e, e);
        }
        logger.info("indexed:" + count++);
    }

    /**
     * check if local buffer is full
     * @param writer lucene indexWriter
     */
    public void tryWriteDocs(IndexWriter writer)
    {
        if (docs.size() > maxDocsLocalBuffer)
        {
            indexDocs(writer);
        }
    }

    /**
     * write documents to indexWriter
     * @param writer lucene indexWriter
     */
    public void indexDocs(IndexWriter writer)
    {
        for (Document d : docs)
        {
            try
            {
                writer.addDocument(d);
            }
            catch (IOException e)
            {
                logger.error(e, e);
            }
        }
        docs.clear();
    }
}
