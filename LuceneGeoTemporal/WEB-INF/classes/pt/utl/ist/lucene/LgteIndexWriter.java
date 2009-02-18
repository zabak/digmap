package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcherLanguageModel;
import org.apache.lucene.search.LangModelSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.versioning.LuceneVersion;
import pt.utl.ist.lucene.context.Context;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteIndexWriter extends IndexWriter
{


    private static String docId_txt = "/docid.txt";
    List<Integer> deleteted = new ArrayList<Integer>();
    List<Term> deleteteTermDocs = new ArrayList<Term>();

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


        if (model == Model.VectorSpaceModel)
        {
            System.setProperty("RetrievalModel", "VectorSpace");
            setSimilarity(new DefaultSimilarity());
        }
        else if (model.isProbabilistcModel())
        {
            System.setProperty("RetrievalModel", model.getName());
            storeTermVectors = true;
            setSimilarity(new LangModelSimilarity());
        }
        else
        {
            System.err.println("Unknown retrieval model: " + model);
            throw new IllegalArgumentException();
        }
    }

    private void deleteWaiting() throws IOException
    {
        IndexReader reader;
        if (directory != null)
            reader = IndexReader.open(directory);
        else if (file != null)
            reader = IndexReader.open(file);
        else
            reader = IndexReader.open(indexPath);
        for (Integer deletedDoc : deleteted)
            reader.delete(deletedDoc);
        deleteted.clear();
        TermDocs termDocs = reader.termDocs();
        for (Term t : deleteteTermDocs)
        {
            termDocs.seek(t);
            if (termDocs.next())
            {
                int previous = termDocs.doc();
                while (termDocs.next())
                {
                    reader.delete(previous);
                    previous = termDocs.doc();
                }
            }
        }
        termDocs.close();
        deleteteTermDocs.clear();
        reader.close();
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
        super.close();
        deleteWaiting();
        optimizeOpeningNewIndex();

        if (storeTermVectors)
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

    public void addDocument(LgteDocumentWrapper documentWrapper) throws IOException
    {
        super.addDocument(documentWrapper.getDocument());
    }

    public void addDocument(LgteDocumentWrapper documentWrapper, Analyzer analyzer) throws IOException
    {
        super.addDocument(documentWrapper.getDocument(), analyzer);
    }

    public void deleteDocument(int docId)
    {
        deleteted.add(docId);
    }

    public void deleteDocument(Term term)
    {
        logger.warn("This method is not safe: Will put deleted document in memory to delete at the end, will delete only first n-1 ocurrences to dont delete the added document");
        deleteteTermDocs.add(term);
    }

    public void deleteDocument(Term[] terms)
    {
        logger.warn("This method is not safe: Will put deleted document in memory to delete at the end, will delete only first n-1 ocurrences to dont delete the added document");
        if (terms != null)
            for (Term t : terms)
                deleteteTermDocs.add(t);
    }

    public void updateDocument(int docId, Document doc) throws IOException
    {
        deleteDocument(docId);
        addDocument(doc);
    }

    public void updateDocument(Term term, Document doc) throws IOException
    {
        logger.warn("This method is not safe: Will put deleted document in memory to delete at the end, will delete only first n-1 ocurrences to dont delete the added document");
        deleteteTermDocs.add(term);
        addDocument(doc);
    }

    public void updateDocument(Term term, Document doc, Analyzer analyzer) throws IOException
    {
        logger.warn("This method is not safe: Will put deleted document in memory to delete at the end, will delete only first n-1 ocurrences to dont delete the added document");
        deleteteTermDocs.add(term);
        addDocument(doc, analyzer);
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


}
