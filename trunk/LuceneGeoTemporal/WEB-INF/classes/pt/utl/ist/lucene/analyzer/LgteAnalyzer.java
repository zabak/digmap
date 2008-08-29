package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.List;

import pt.utl.ist.lucene.analyzer.LgteDiacriticFilter;
import pt.utl.ist.lucene.treceval.IndexCollections;

/**
 * LgteAnalyzer for brazilian language. Supports an external list of stopwords (words that
 * will not be indexed at indexText) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 *
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteAnalyzer extends org.apache.lucene.analysis.Analyzer
{

    private static final Logger logger = Logger.getLogger(LgteAnalyzer.class);


    public static Analyzer defaultAnalyzer;

    static
    {
        try
        {
            defaultAnalyzer = new LgteAnalyzer();
        }
        catch (IOException e)
        {
            logger.fatal("Cant start default Analyzer");
            System.exit(-1);
        }
    }

    /**
     * Contains the stopwords used with the StopFilter.
     */
    protected Set<String> stoptable = new HashSet<String>();
    protected Set<String> notTokenizedFields = null;
   
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
        stoptable = new HashSet<String>(stopwords.keySet());
    }

    /**
     * Builds an analyzer with the given stop words.
     * @param stopwords z
     * @throws java.io.IOException on error
     */
    public LgteAnalyzer( File stopwords ) throws IOException
    {
        stoptable = WordlistLoader.getWordSet( stopwords );
    }

    /**
     * Builds an analyzer.
     *
     * @throws java.io.IOException on error
     */
    public LgteAnalyzer( ) throws IOException
    {
        stoptable = null;
    }

    public LgteAnalyzer(Set<String> notTokenizedFields) throws IOException
    {
        this.notTokenizedFields = notTokenizedFields;
    }


    public final TokenStream tokenStream(String fieldName, Reader reader)
    {

        if(notTokenizedFields != null && notTokenizedFields.contains(fieldName))
            return new LgteNothingTokenizer( reader );
        
        TokenStream result = new StandardTokenizer( reader );
        result = new StandardFilter( result );
        result = new LowerCaseFilter( result );
        result = new LgteDiacriticFilter( result );
        if(stoptable != null)
            result = new StopFilter( result, stoptable );
        return result;
    }


}
