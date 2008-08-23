package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import pt.utl.ist.lucene.analyzer.LgteDiacriticFilter;

/**
 * LgteAnalyzer for brazilian language. Supports an external list of stopwords (words that
 * will not be indexed at indexText) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 *
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteAnalyzer extends org.apache.lucene.analysis.Analyzer {

    /**
     * Contains the stopwords used with the StopFilter.
     */
    protected Set stoptable = new HashSet();

   
    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords s
     */
    public LgteAnalyzer( String[] stopwords )
    {
        stoptable = StopFilter.makeStopSet( stopwords );
    }

    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords xx
     */
    public LgteAnalyzer( Hashtable stopwords ) {
        stoptable = new HashSet(stopwords.keySet());
    }

    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords z
     */
    public LgteAnalyzer( File stopwords ) throws IOException
    {
        stoptable = WordlistLoader.getWordSet( stopwords );
    }

    /**
     * Builds an analyzer with the given stop words.
     */
    public LgteAnalyzer( ) throws IOException
    {
        stoptable = null;
    }


    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new StandardTokenizer( reader );
        result = new StandardFilter( result );
        result = new LowerCaseFilter( result );
        result = new LgteDiacriticFilter( result );
        if(stoptable != null)
            result = new StopFilter( result, stoptable );
        return result;
    }


}
