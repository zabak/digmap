package pt.utl.ist.lucene.utils;

import org.apache.lucene.analysis.Analyzer;
import java.util.HashMap;
import java.util.HashSet;
import java.io.*;

import pt.utl.ist.lucene.analyzer.LgteStemAnalyzer;

/**
 * @author Jorge Machado
 * @date 20/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class LgteAnalyzerManager {

    private HashMap<String,LanguagePackage> languageCache = new HashMap<String,LanguagePackage>();

    private static LgteAnalyzerManager instance = null;

    static
    {
        instance = new LgteAnalyzerManager();
    }

    private LgteAnalyzerManager()
    {}

    public static LgteAnalyzerManager getInstance()
    {
        return instance;
    }

    public LanguagePackage getLanguagePackage(String snowballCountry, String stopwordsFile) throws IOException {
        String key = snowballCountry + "-" + stopwordsFile;
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;
        InputStream file = LgteAnalyzerManager.class.getResourceAsStream(stopwordsFile);
        HashSet<String> stop = new HashSet();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
        String line;
        while((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if(line.length()>0) {
                stop.add(line);
            }
        }
        Object[] obj = stop.toArray();
        String[] strs = new String[obj.length];
        for (int i = 0; i < obj.length; i++) {
            strs[i] = obj[i].toString();
        }
        Analyzer analyzer = new pt.utl.ist.lucene.analyzer.LgteAnalyzer(strs);
        Analyzer analyzerStemming = new LgteStemAnalyzer(snowballCountry, strs);
        languagePackage = new LanguagePackage(stop, analyzer, analyzerStemming);
        languageCache.put(key,languagePackage);
        return languagePackage;
    }

    public void clear()
    {
        languageCache.clear();
    }

    public class LanguagePackage
    {
        Analyzer analyzerNoStemming;
        Analyzer analyzerWithStemming;
        HashSet wordList;


        public LanguagePackage(HashSet wordList, Analyzer analyzerNoStemming, Analyzer analyzerWithStemming)
        {
            this.analyzerNoStemming = analyzerNoStemming;
            this.wordList = wordList;
            this.analyzerWithStemming = analyzerWithStemming;
        }


        public Analyzer getAnalyzerNoStemming()
        {
            return analyzerNoStemming;
        }

        public Analyzer getAnalyzerWithStemming()
        {
            return analyzerWithStemming;
        }

        public HashSet getWordList()
        {
            return wordList;
        }
    }
}
