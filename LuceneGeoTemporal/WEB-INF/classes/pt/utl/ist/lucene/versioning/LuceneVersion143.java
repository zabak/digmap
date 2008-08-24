package pt.utl.ist.lucene.versioning;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.config.ConfigProperties;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */

public class LuceneVersion143 implements LuceneVersion
{
    public void deleteDocument(int i, IndexReader r) throws IOException
    {
        r.delete(i);
    }

    public void addFieldUnIndexed(Document doc, String field, String value)
    {
        addField(doc, field, value, true, false, false, false);
    }

    public void addFieldUnStored(Document doc, String field, String value)
    {
        addField(doc, field, value, false, true, true, false);
    }

    public void addFieldUnStored(Document doc, String field, String value, boolean termVector)
    {
        addField(doc, field, value, false, true, true, termVector);
    }

    public void addFieldText(Document doc, String field, String value)
    {
        addField(doc, field, value, true, true, true, false);
    }

    public void addFieldText(Document doc, String field, String value, boolean termVector)
    {
        addField(doc, field, value, true, true, true, termVector);
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

    static int maxDocs = ConfigProperties.getIntProperty("lucene143.maxdocuments");

    public void setWriterBuffer(IndexWriter writer)
    {
        writer.maxMergeDocs = maxDocs;
        writer.mergeFactor = 20;
        writer.minMergeDocs= maxDocs;
    }

// Mudar o import disto no BrazilianExtendedAnalyser
//   public BrazilianExtendedAnalyzer( File stopwords ) throws IOException {
//        stoptable = WordlistLoader.getWordSet( stopwords );
//    }

    public Token setTermText(Token t, String text)
    {
        return new Token(text, t.startOffset(),t.endOffset(),t.type());
    }


    public String highlight(Formatter formatter,
                            VersionEncoder encoder,
                            String text,
                            String field,
                            Analyzer analyzer,
                            Query query,
                            int fragmentSize,
                            int maxfragments,
                            String fragmentSeparator) throws IOException
    {
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter,scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(fragmentSize));
        TokenStream tokenStream = analyzer.tokenStream(field,new StringReader(text));
        try
        {
            return highlighter.getBestFragments(tokenStream, text,maxfragments,fragmentSeparator);
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public String highlight(Formatter formatter, VersionEncoder encoder,
                            String text,
                            String field,
                            Analyzer analyzer,
                            String queryStr,
                            int fragmentSize,
                            int maxfragments,
                            String fragmentSeparator) throws ParseException, IOException
    {
        Query query;
        try
        {
            query = parseQuery(queryStr,field,analyzer);
        }
        catch (ParseException e)
        {
            throw e;
        }
        return highlight(formatter,
                encoder,
                text,
                field,
                analyzer,
                query,
                fragmentSize,
                maxfragments,
                fragmentSeparator);
    }

    public IndexReader getReader(IndexSearcher searcher)
    {
        return searcher.getReader();
    }

    public Query parseQuery(String query, String field, Analyzer analyzer) throws ParseException
    {
        return QueryParser.parse(query,field,analyzer);
    }


    public VersionEnum getVersion()
    {
        return VersionEnum.v143;
    }

    public Collection getIndexedFieldsNames(IndexReader reader, boolean termvector)
    {
        return reader.getIndexedFieldNames(termvector);
    }

    public Collection getFieldsNames(IndexSearcher searcher) throws IOException
    {
        return getFieldsNames(getReader(searcher));
    }

    public Collection getFieldsNames(IndexReader reader) throws IOException
    {
        return reader.getFieldNames();
    }

    public Collection getFieldsNames(IndexReader reader, boolean indexed) throws IOException
    {
        return reader.getFieldNames(indexed);
    }

    public Collection getFieldsNames(IndexSearcher searcher, boolean indexed) throws IOException
    {
        return getReader(searcher).getFieldNames(indexed);
    }
}
