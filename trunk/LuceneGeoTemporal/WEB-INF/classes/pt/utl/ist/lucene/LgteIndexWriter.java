package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.store.Directory;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.LuceneVersion;

import java.io.*;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexWriter extends IndexWriter
{


    private static String docId_txt = "/docid.cache";

    private static LuceneVersion luceneVersion = LuceneVersionFactory.getLuceneVersion();
    private static final Logger logger = Logger.getLogger(LgteIndexWriter.class);
    private String indexPath = null;
    private Directory directory = null;
    private File file = null;
    private Model model = Model.defaultModel;
    private boolean storeProbabilisticCaches = false;

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


        if (model == Model.VectorSpaceModel)
        {
            System.setProperty("RetrievalModel", "VectorSpace");
            setSimilarity(new DefaultSimilarity());
        }
        else if (model.isProbabilistcModel())
        {
            System.setProperty("RetrievalModel", model.getName());
            storeProbabilisticCaches = true;
            setSimilarity(new LangModelSimilarity());
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }



    private void optimizeOpeningNewIndex() throws IOException
    {
        IndexWriter writer;
        if (directory != null)
            writer = new IndexWriter(directory, super.getAnalyzer(), false);
        else if (file != null)
            writer = new IndexWriter(file, super.getAnalyzer(), false);
        else
            writer = new IndexWriter(indexPath, super.getAnalyzer(), false);
        writer.optimize();
        writer.close();
    }

    public void close() throws IOException
    {
        close(true);
    }
    public void close(boolean optimizeAndCreateGlobals) throws IOException
    {
        super.close();
        if(optimizeAndCreateGlobals)
        {
            optimizeOpeningNewIndex();

            if (storeProbabilisticCaches)
            {
                // store some cached data
                IndexSearcherLanguageModel searcher = new IndexSearcherLanguageModel(getIndexPath());
                searcher.storeExtendedData(indexPath);
            }

            // write docid to internal id mapping
            String docidFile = indexPath + docId_txt;
            logger.info("Writing docid file to " + docidFile);
            FileWriter outFile = new FileWriter(docidFile);
            PrintWriter fileOutput = new PrintWriter(outFile);


            IndexReader reader;
            if (directory != null)
                reader = IndexReader.open(directory);
            else if (file != null)
                reader = IndexReader.open(file);
            else
                reader = IndexReader.open(indexPath);

            String docid;
            org.apache.lucene.document.Document doc;
            for (int i = 0; i < reader.maxDoc(); ++i)
            {
                doc = reader.document(i);
                docid = doc.get(Globals.DOCUMENT_ID_FIELD);
                fileOutput.println(i + " " + docid);
            }
            fileOutput.close();
            outFile.close();
            reader.close();
        }

    }



    private String getIndexPath()
    {
        if (indexPath == null && file == null)
        {
            indexPath = Globals.TMP_DIR;
            new File(indexPath).mkdirs();
        }
        else if (indexPath == null)
        {
            indexPath = file.getAbsolutePath();
        }
        return indexPath;
    }
    public DocIdDocNoIterator getDocIdDocNoIterator() throws FileNotFoundException
    {
        return new DocIdDocNoIterator(getIndexPath());
    }

    public static class DocIdDocNoIterator
    {

        BufferedReader reader;

        public DocIdDocNoIterator(String indexPath) throws FileNotFoundException
        {
            reader = new BufferedReader(new FileReader(indexPath + docId_txt));
        }

        int docId;
        String docNo;

        public boolean next() throws IOException
        {
            String line;
            if ((line = reader.readLine()) != null && line.trim().length() > 0)
            {
                String[] docsIdDocNo = line.split(" ");
                docId = Integer.parseInt(docsIdDocNo[0]);
                docNo = docsIdDocNo[1];
                return true;
            }
            return false;
        }


        public int getDocId()
        {
            return docId;
        }

        public String getDocNo()
        {
            return docNo;
        }

        public void close() throws IOException
        {
            reader.close();
        }
    }

    public void addDocument(LgteDocumentWrapper documentWrapper) throws IOException
    {
        addDocument(documentWrapper.getDocument());
    }

    public void addDocument(LgteDocumentWrapper documentWrapper, Analyzer analyzer) throws IOException
    {
        addDocument(documentWrapper.getDocument(), analyzer);
    }



}
