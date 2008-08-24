package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;

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
 * @author    Jo�o Kramer
 */
public class LgteStemAnalyzer extends org.apache.lucene.analysis.Analyzer
{



    /**
     * Contains the stopwords used with the StopFilter.
     */
    protected Set stoptable = new HashSet();


    protected String name;

    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords z
     */
    public LgteStemAnalyzer( String name, String[] stopwords ) throws IOException
    {
        this.name = name;
        stoptable = StopFilter.makeStopSet(stopwords);
    }


    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new StandardTokenizer( reader );
        result = new StandardFilter( result );
        result = new LowerCaseFilter( result );
        result = new LgteDiacriticFilter( result );
        result = new StopFilter( result, stoptable );
        result = new SnowballFilter(result,name);
        return result;
    }


}
