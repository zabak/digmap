package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.store.Directory;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.LuceneVersion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexWriter extends IndexWriter
{


    private static LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();
    private static final Logger logger = Logger.getLogger(LgteIndexWriter.class);
    private String indexPath = null;
    private Directory directory = null;
    private File file = null;
    private Model model = Model.defaultModel;
    private boolean storeTermVectors = false;

    public LgteIndexWriter(String s, Analyzer analyzer, boolean b)
            throws IOException
    {
        super(s, analyzer, b);
        indexPath = s;
        init(model);
    }
    public LgteIndexWriter(String s, boolean b)
            throws IOException
    {
        super(s, new LgteAnalyzer(), b);
        indexPath = s;
        init(model);
    }

    public LgteIndexWriter(String s, boolean b, Model model)
            throws IOException
    {
        super(s, new LgteAnalyzer(), b);
        indexPath = s;
        init(model);
    }

    public LgteIndexWriter(String s, Analyzer analyzer, boolean b, Model model)
            throws IOException
    {
        super(s, analyzer, b);
        indexPath = s;
        init(model);
    }

    public LgteIndexWriter(File file, Analyzer analyzer, boolean b)
            throws IOException
    {
        super(file, analyzer, b);
        indexPath = file.getAbsolutePath();
        this.file = file;
        init(model);
    }

    public LgteIndexWriter(File file, Analyzer analyzer, boolean b, Model model) throws IOException
    {
        super(file, analyzer, b);
        indexPath = file.getAbsolutePath();
        this.file = file;
        init(model);
    }

    public LgteIndexWriter(Directory directory, Analyzer analyzer, boolean b) throws IOException
    {
        super(directory, analyzer, b);
        this.directory = directory;
        init(model);
    }


    public LgteIndexWriter(Directory directory, Analyzer analyzer, boolean b, Model model) throws IOException
    {
        super(directory, analyzer, b);
        this.directory = directory;
        init(model);
    }

    public void init(Model model)
            throws IOException
    {

        this.model = model;
        luceneVersion.setWriterBuffer(this);

        if (model == Model.LanguageModel)
        {
            System.setProperty("RetrievalModel", "LanguageModel");
            storeTermVectors = true;
            setSimilarity(new LangModelSimilarity());
        }
        else if (model == Model.VectorSpaceModel)
        {
            System.setProperty("RetrievalModel", "VectorSpace");
            setSimilarity(new DefaultSimilarity());
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    public void close() throws IOException
    {
        optimize();
        super.close();
        if (storeTermVectors)
        {
            // store some cached data
            if(indexPath == null)
            {
                indexPath = Globals.TMP_DIR;
                new File(indexPath).mkdirs();
            }
            IndexSearcherLanguageModel searcher = new IndexSearcherLanguageModel(indexPath);
            searcher.storeExtendedData(indexPath);
        }

        // write docid to internal id mapping
        String docidFile = indexPath + "/docid.txt";
        logger.info("Writing docid file to " + docidFile);
        FileWriter outFile = new FileWriter(docidFile);
        PrintWriter fileOutput = new PrintWriter(outFile);

        IndexReader reader;

        if(directory != null)
            reader = IndexReader.open(directory);
        else if(file != null)
            reader = IndexReader.open(file);
        else
            reader = IndexReader.open(indexPath);

        String docid;
        org.apache.lucene.document.Document doc;
        for (int i = 0; i < reader.maxDoc(); ++i)
        {
            doc = reader.document(i);
            docid = doc.get("id");
            fileOutput.println(i + " " + docid);
        }
        fileOutput.close();
        outFile.close();
        reader.close();
    }

    public void addDocument(LgteDocumentWrapper documentWrapper) throws IOException
    {
        super.addDocument(documentWrapper.getDocument());
    }

    public void addDocument(LgteDocumentWrapper documentWrapper, Analyzer analyzer) throws IOException
    {
        super.addDocument(documentWrapper.getDocument(),analyzer);
    }
}
