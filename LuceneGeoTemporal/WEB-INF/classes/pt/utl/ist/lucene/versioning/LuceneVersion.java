package pt.utl.ist.lucene.versioning;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public interface LuceneVersion
{
    public void deleteDocument(int i, IndexReader r) throws IOException;
    public void addField(Document doc, String field, String value, boolean store, boolean index, boolean tokenized);
    public Field getField(String field, String value, boolean store, boolean index, boolean tokenized);
    public Field getField( String field, String value,  boolean store, boolean index, boolean tokenized, boolean termVector);
    public void addFields(Document doc, Collection<Field> fields);
    public void addFieldUnIndexed(Document doc, String field, String value);
    public void addFieldUnStored(Document doc, String field, String value);
    public void addFieldUnStored(Document doc, String field, String value, boolean termVector);
    public void addFieldText(Document doc, String field, String value);
    public void addFieldText(Document doc, String field, String value, boolean termVector);
    public void addField(Document doc, String field, String value, boolean index, boolean store, boolean tokenized, boolean termVector);
    public FSDirectory getDirectory(String dir) throws IOException;
    public void deleteDocuments(String field, String value, IndexWriter writer);
    public void printMemorySatus(Logger logger, IndexWriter writer);
    public void checkFlush(IndexWriter writer) throws IOException;
    public void setWriterBuffer(IndexWriter writer);
    public Token setTermText(Token t, String text);
    public String highlight(Formatter formatter, VersionEncoder encoder, String text,String field,Analyzer analyzer,String queryStr,int fragmentSize,int maxfragments,String fragmentSeparator) throws ParseException, IOException;
    public String highlight(Formatter formatter, VersionEncoder encoder, String text,String field,Analyzer analyzer,Query query,int fragmentSize,int maxfragments,String fragmentSeparator) throws IOException;
    public IndexReader getReader(IndexSearcher searcher);
    public Query parseQuery(String query, String field, Analyzer analyzer) throws ParseException;
    public VersionEnum getVersion();
    public Collection getIndexedFieldsNames(IndexReader reader, boolean termvector);
    public Collection<String> getFieldsNames(IndexSearcher searcher) throws IOException;
    public Collection<String> getFieldsNames(IndexReader reader) throws IOException;
    public Collection<String> getFieldsNames(IndexReader reader, boolean indexed) throws IOException;
    public Collection<String> getFieldsNames(IndexSearcher searcher, boolean indexed) throws IOException;
}
