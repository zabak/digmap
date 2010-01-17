package pt.utl.ist.lucene.analyzer;

import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.io.IOException;
import java.io.Reader;

/**
 * LgteAnalyzer for brazilian language. Supports an external list of stopwords (words that
 * will not be indexed at indexText) and an external list of exclusions (word that will
 * not be stemmed, but indexed).
 *
 * @author     Kramer
 */
public class LgteBrokerStemAnalyzer extends org.apache.lucene.analysis.Analyzer
{



    Map<String,Analyzer> analizers;
    Analyzer defaultAnalyzer = LgteAnalyzer.defaultAnalyzer;


    public LgteBrokerStemAnalyzer(Map<String, Analyzer> analizers, Analyzer defaultAnalyzer)
    {
        this.analizers = analizers;
        this.defaultAnalyzer = defaultAnalyzer;
    }
    public LgteBrokerStemAnalyzer(Map<String, Analyzer> analizers)
    {
        this.analizers = analizers;
    }

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
//    	try{
        if(analizers.get(fieldName) == null)
            return defaultAnalyzer.tokenStream(fieldName,reader);
        return analizers.get(fieldName).tokenStream(fieldName,reader);
//    	}catch(Exception e)
//    	{
//    		System.out.println(fieldName);
//    		System.out.println(e.toString());
//    		return null;
//    	}
    }
}
