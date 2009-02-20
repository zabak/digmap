package pt.utl.ist.lucene.analyzer;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.de.WordlistLoader;

import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * LgteAnalyzer for brazilian language. Supports an external list of stopwords (words that
 * will not be indexed at indexText) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 *
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class LgteWhiteSpacesAnalyzer extends org.apache.lucene.analysis.Analyzer
{

    private static final Logger logger = Logger.getLogger(LgteWhiteSpacesAnalyzer.class);



    public LgteWhiteSpacesAnalyzer( ) throws IOException
    {
        System.out.println("");
    }

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new WhitespaceTokenizer(reader);
    }


}