package pt.utl.ist.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LuceneVersion {
	
    public void deleteDocument(int i, IndexReader r) throws IOException
    {
        r.delete(i);
    }

    public void addField(Document doc, String field, String value,  boolean store, boolean index, boolean tokenized)
    {
        addField(doc, field, value, store, index, tokenized, false);
    }

    public void addField(Document doc, String field, String value, boolean store, boolean index, boolean tokenized, boolean termVector)
    {
        doc.add(new Field(field,value,store,index,tokenized,termVector));
    }

    public FSDirectory getDirectory(String dir) throws IOException
    {
        return FSDirectory.getDirectory(dir,false);
    }

    public void deleteDocuments(String field, String value, IndexWriter writer)
    {
        throw new NotImplementedException();
    }

    public void printMemorySatus(Logger logger, IndexWriter writer)
    {
        throw new NotImplementedException();
    }


//    private static final int MAX_DOCS_MEMORY = 10000;

    public void checkFlush(IndexWriter writer) throws IOException
    {

    }

    static int maxDocs = new Integer(Globals.ConfigProperties.getProperty("lucene.maxdocuments")).intValue();

    public void setWriterBuffer(IndexWriter writer)
    {
        writer.maxMergeDocs = maxDocs;
        writer.mergeFactor = 20;
        writer.minMergeDocs= maxDocs;
    }

// TODO: Mudar o import disto no BrazilianExtendedAnalyser
//   public BrazilianExtendedAnalyzer( File stopwords ) throws IOException {
//        stoptable = WordlistLoader.getWordSet( stopwords );
//    }

    public Token setTermText(Token t, String text)
    {
        return new Token(text, t.startOffset(),t.endOffset(),t.type());
    }


    public String doStandardHighlights(String text, String field, String queryStr, int fragmentSize, int maxfragments) throws Exception
    {
        return text;
    }

    public IndexReader getReader(IndexSearcher searcher)
    {
        return searcher.getReader();
    }

    public Query parseQuery(String query, String field, Analyzer analyzer) throws ParseException
    {
        return QueryParser.parse(query,field,analyzer);
    }

}
