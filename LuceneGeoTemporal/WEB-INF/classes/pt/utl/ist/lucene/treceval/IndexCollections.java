package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
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
    static int maxDocsLocalBuffer = 25000;
    IndexWriter writer;
    String idField;

    public static LgteAnalyzerManager.LanguagePackage pt;
    public static LgteAnalyzerManager.LanguagePackage en;
    public static LgteAnalyzerManager.LanguagePackage fr;
    public static LgteAnalyzerManager.LanguagePackage de;
    public static LgteAnalyzerManager.LanguagePackage du;

    public static LgteAnalyzerManager.LanguagePackage ptStop4gramsStem;
    public static LgteAnalyzerManager.LanguagePackage enStop4gramsStem;
    public static LgteAnalyzerManager.LanguagePackage frStop4gramsStem;
    public static LgteAnalyzerManager.LanguagePackage deStop4gramsStem;
    public static LgteAnalyzerManager.LanguagePackage duStop4gramsStem;

    public static LgteAnalyzerManager.LanguagePackage ptStop5gramsStem;
    public static LgteAnalyzerManager.LanguagePackage enStop5gramsStem;
    public static LgteAnalyzerManager.LanguagePackage frStop5gramsStem;
    public static LgteAnalyzerManager.LanguagePackage deStop5gramsStem;
    public static LgteAnalyzerManager.LanguagePackage duStop5gramsStem;

    public static LgteAnalyzerManager.LanguagePackage n4gramsStem;
    public static LgteAnalyzerManager.LanguagePackage n5gramsStem;

    public static LgteAnalyzerManager.LanguagePackage n2_6gramsStem;
    public static LgteAnalyzerManager.LanguagePackage n3_5gramsStem;
    public static LgteAnalyzerManager.LanguagePackage n3_6gramsStem;
    public static LgteAnalyzerManager.LanguagePackage n3_6gramsFrontEdjeStem;


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
            de = LgteAnalyzerManager.getInstance().getLanguagePackage("German2", "snowball-german.list");
            du = LgteAnalyzerManager.getInstance().getLanguagePackage("Dutch", "snowball-dutch.list");

            ptStop4gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(4, "stopwords_por.txt");
            enStop4gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(4, "snowball-english.list");
            frStop4gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(4, "snowball-french.list");
            deStop4gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(4, "snowball-german.list");
            duStop4gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(4, "snowball-dutch.list");

            ptStop5gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(5, "stopwords_por.txt");
            enStop5gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(5, "snowball-english.list");
            frStop5gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(5, "snowball-french.list");
            deStop5gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(5, "snowball-german.list");
            duStop5gramsStem = LgteAnalyzerManager.getInstance().getLanguagePackage(5, "snowball-dutch.list");

            n4gramsStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(4);
            n5gramsStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(5);

            n3_6gramsStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(3,6);
            n3_6gramsFrontEdjeStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(3,6, EdgeNGramTokenFilter.Side.FRONT);

            n3_5gramsStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(3,5);
            n2_6gramsStem =  LgteAnalyzerManager.getInstance().getLanguagePackage(2,6);
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
        writer = new LgteIndexWriter(c.getIndexPath(), c.getAnalyzer(), c.isCreateIndex(), c.getModel());
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
    public void indexDoc(String id, Map<String,String> indexFields, Map<String,String> storedFields, Collection<Field> uniqueFields) throws IOException
    {
        LgteDocumentWrapper d = new LgteDocumentWrapper();
        d.addField(idField, id, true, true, false);
        for (Map.Entry<String,String> entry : indexFields.entrySet())
        {
            d.addField(entry.getKey(), entry.getValue(), true, true, true, true);
        }
        for (Map.Entry<String,String> entry : storedFields.entrySet())
        {
            d.addField(entry.getKey(), entry.getValue(), true, false, false);
        }
        if(uniqueFields!=null)
            d.addFields(uniqueFields);
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
