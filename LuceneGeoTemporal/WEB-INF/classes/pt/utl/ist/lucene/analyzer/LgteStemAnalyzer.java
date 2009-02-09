package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.solr.analysis.NGramFilterFactory;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.io.Reader;

import pt.utl.ist.lucene.analyzer.LgteDiacriticFilter;

/**
 * LgteAnalyzer for brazilian language. Supports an external list of stopwords (words that
 * will not be indexed at indexText) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 *
 * @author     Kramer
 */
public class LgteStemAnalyzer extends org.apache.lucene.analysis.Analyzer
{



    /**
     * Contains the stopwords used with the StopFilter.
     */
    protected Set stoptable = new HashSet();



    protected String name;
    protected int mgrams;
    protected int ngrams;
    EdgeNGramTokenFilter.Side gramsSide = null;


    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords z
     */
    public LgteStemAnalyzer( String name, String[] stopwords ) throws IOException
    {
        this.name = name;
        stoptable = StopFilter.makeStopSet(stopwords);
    }

    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords z
     * @param grams .
     * @throws java.io.IOException .
     */
    public LgteStemAnalyzer( int grams, String[] stopwords ) throws IOException
    {
        this.name = null;
        this.mgrams = grams;
        this.ngrams = grams;
        stoptable = StopFilter.makeStopSet(stopwords);
    }

    /**
     *
     * @param grams .
     * @throws IOException .
     */
    public LgteStemAnalyzer( int grams ) throws IOException
    {
        this.name = null;
        this.mgrams = grams;
        this.ngrams = grams;
        stoptable = null;
    }


    /**
     *

     * @throws IOException .
     */
    public LgteStemAnalyzer( int mgrams, int ngrams ) throws IOException
    {
        this.name = null;
        this.mgrams = mgrams;
        this.ngrams = ngrams;
        stoptable = null;
    }

    /**
     *

     * @throws IOException .
     */
    public LgteStemAnalyzer( int mgrams, int ngrams, EdgeNGramTokenFilter.Side side) throws IOException
    {
        this.name = null;
        this.mgrams = mgrams;
        this.ngrams = ngrams;
        stoptable = null;
        this.gramsSide = side;
    }


    public final TokenStream tokenStream(String fieldName, Reader reader)
    {

        TokenStream result = new StandardTokenizer( reader );
//        result = new StandardFilter( result );
        result = new LowerCaseFilter( result );
        result = new LgteDiacriticFilter( result );
        if(stoptable != null)
            result = new StopFilter( result, stoptable );
        if(name != null)
            result = new SnowballFilter(result,name);
        else if(mgrams > 0)
            if(gramsSide == null)
                result = new NGramTokenFilter(result,mgrams,ngrams);
            else
                result = new EdgeNGramTokenFilter(result,gramsSide,mgrams,ngrams);
        return result;
    }
}
