package pt.utl.ist.lucene.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.io.*;

import pt.utl.ist.lucene.analyzer.LgteStemAnalyzer;

/**
 * @author Jorge Machado
 * @date 20/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class LgteAnalyzerManager
{

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

    public LanguagePackage getLanguagePackage(String snowballCountry, String stopwordsFile) throws IOException
    {
        String key = snowballCountry + "-" + stopwordsFile;
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;
        InputStream file = LgteAnalyzerManager.class.getResourceAsStream(stopwordsFile);
        HashSet<String> stop = new HashSet<String>();
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

    public LanguagePackage getLanguagePackage(int grams, String stopwordsFile) throws IOException
    {
        String key = grams + "-" + stopwordsFile;
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;
        InputStream file = LgteAnalyzerManager.class.getResourceAsStream(stopwordsFile);
        HashSet<String> stop = new HashSet<String>();
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
        Analyzer analyzerStemming = new LgteStemAnalyzer(grams, strs);
        languagePackage = new LanguagePackage(stop, analyzer, analyzerStemming);
        languageCache.put(key,languagePackage);
        return languagePackage;
    }

    public LanguagePackage getLanguagePackage(int grams) throws IOException
    {
        String key = grams + "";
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;

        Analyzer analyzerStemming = new LgteStemAnalyzer(grams);
        languagePackage = new LanguagePackage(null, null, analyzerStemming);
        languageCache.put(key,languagePackage);
        return languagePackage;
    }

    public LanguagePackage getLanguagePackage(int m, int n) throws IOException
    {
        String key = m + "-" + n;
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;

        Analyzer analyzerStemming = new LgteStemAnalyzer(m,n);
        languagePackage = new LanguagePackage(null, null, analyzerStemming);
        languageCache.put(key,languagePackage);
        return languagePackage;
    }

    public LanguagePackage getLanguagePackage(int m, int n, EdgeNGramTokenFilter.Side side) throws IOException
    {
        String key = m + "-" + n;
        LanguagePackage languagePackage = languageCache.get(key);
        if(languagePackage != null) return languagePackage;

        Analyzer analyzerStemming = new LgteStemAnalyzer(m,n,side);
        languagePackage = new LanguagePackage(null, null, analyzerStemming);
        languageCache.put(key,languagePackage);
        return languagePackage;
    }


    public static String stemm(String field, String text, Analyzer analyzer) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        TokenStream tokenStream = analyzer.tokenStream(field,new StringReader(text));
        Token token;
        while((token = tokenStream.next())!=null)
        {
            builder.append(" ").append(token.termText());
        }
        return builder.toString();
    }


    public static String highlight(String qq, String annotatedText,String openTag, String closeTag) throws IOException
    {
        org.apache.lucene.analysis.TokenStream stream = pt.utl.ist.lucene.treceval.IndexCollections.en.getAnalyzerWithStemming().tokenStream("",new java.io.StringReader(qq));
        org.apache.lucene.analysis.Token t;
        while((t=stream.next())!=null)
        {
            String str = t.termText();
            String Str = str.substring(0,1).toUpperCase()+str.substring(1);
            String STR = str.toUpperCase();

            if(str.trim().length()>1)
            {
                annotatedText = annotatedText.replaceAll(str,openTag + str + closeTag);
                annotatedText = annotatedText.replaceAll(Str,openTag + Str + closeTag);
                annotatedText = annotatedText.replaceAll(STR,openTag + STR + closeTag);
            }
        }
        return annotatedText;
    }

    public static void main(String [] args) throws IOException {
        System.out.println(highlight("Some test text highlight words","This is a Test textual to highlight Some words","<label class=\"words\">","</label>"));
        System.out.println(highlight("Where and when ocurred races won by Michael Schumacher","2002-5-17\n" +
                "\n" +
                "FERRARI FIASCO HAS FAR-REACHING EFFECTS\n" +
                "\n" +
                "\n" +
                "(BC-CAR-AUTOCOL-COLUMN-LADN)\n" +
                "\n" +
                "\n" +
                "\n" +
                "Doesn't Ferrari know May is a sacred month for auto racing?\n" +
                "\n" +
                "\n" +
                "It must not because it just spit in the face of every motor\n" +
                "sports\n" +
                "fan in the world.\n" +
                "\n" +
                "\n" +
                "Ferrari's decision to have driver Rubens Barrichello yield his\n" +
                "lead to teammate Michael Schumacher in last weekend's Austrian\n" +
                "Grand\n" +
                "Prix has cast a dark shadow over auto racing, making it comparable\n" +
                "to\n" +
                "the scripted World Wrestling Federation.\n" +
                "\n" +
                "\n" +
                "Never has a fix been so blatant since the 1919 Black Sox took\n" +
                "the\n" +
                "field in the World Series.\n" +
                "\n" +
                "\n" +
                "This is hallowed ground for auto racing fans. It is a time when\n" +
                "burned rubber and boiling oil carry the same meaning as pine tar\n" +
                "and\n" +
                "grass stains in October, squeaky sneakers and buzzer beaters in\n" +
                "March.\n" +
                "\n" +
                "\n" +
                "Well, maybe not that sacred.\n" +
                "\n" +
                "\n" +
                "This is a time of year dedicated to The Winston, the all-star\n" +
                "event for NASCAR fans. It also is the time of year for the\n" +
                "Indianapolis 500, the premier open-wheel racing event in the\n" +
                "country,\n" +
                "if not the world.\n" +
                "\n" +
                "\n" +
                "Instead, there is more being written about how the best team in\n" +
                "Formula One is going out of its way to favor one of its drivers\n" +
                "over\n" +
                "the other.\n" +
                "\n" +
                "\n" +
                "At a time when mainstream sports fans cast a curious eye to auto\n" +
                "racing, Ferrari did the equivalent of jabbing that eye with a hot\n" +
                "poker.\n" +
                "\n" +
                "\n" +
                "Ferrari has made a mockery of auto racing. Its defense is that\n" +
                "it\n" +
                "was a decision made for the good of the team, that by having\n" +
                "Schumacher win, it padded his already insurmountable points lead.\n" +
                "Schumacher gained an extra four points toward the championship by\n" +
                "winning and now holds a 27-point lead over his nearest competitor,\n" +
                "Juan Montoya.\n" +
                "\n" +
                "\n" +
                "That lead is the equivalent of three races. In other words,\n" +
                "Montoya would have to win three races and Schumacher would have to\n" +
                "score zero points in those three races for Montoya to take a\n" +
                "three-point lead over Schumacher, who has won five of the six races\n" +
                "in Formula One this year.\n" +
                "\n" +
                "\n" +
                "Schumacher losing his lead in the championship is unlikely. But\n" +
                "a\n" +
                "week ago, it was just as unlikely to see a race car all but park\n" +
                "100\n" +
                "yards from the finish line to let another car pass and win a race.\n" +
                "\n" +
                "\n" +
                "This truly is a shame, not just for Ferrari and its drivers but\n" +
                "for all of auto racing.\n" +
                "\n" +
                "\n" +
                "--Hearn qualifies for Indy 500: Pasadena High graduate and\n" +
                "one-time Glendale resident Richie Hearn qualified for his third\n" +
                "Indianapolis 500.\n" +
                "\n" +
                "\n" +
                "He turned a four-lap average speed of 227.233 mph at\n" +
                "Indianapolis\n" +
                "Motor Speedway on Saturday. His average speed was the 22nd-fastest\n" +
                "of\n" +
                "the day.\n" +
                "\n" +
                "\n" +
                "``I probably could have gone a little faster and moved up higher\n" +
                "on the grid,'' said Hearn, who is driving the Grill 2 Go\n" +
                "Dallara/Chevrolet for Sam Schmidt Motorsports. ``It was really\n" +
                "sketchy going into turn 1, and it was hard to balance out the push\n" +
                "everywhere else and trying to crank more front wing. I got a little\n" +
                "loose the first lap but was OK after that.''\n" +
                "\n" +
                "\n" +
                "Hearn, a veteran of the Indy Racing League and CART, is a\n" +
                "substitute driver for Sam Schmidt Motorsports. Mark Dismore, the\n" +
                "regular driver for Sam Schmidt, was in a one-car crash last week in\n" +
                "practice at Indianapolis.\n" +
                "\n" +
                "\n" +
                "Brazil's Bruno Junqueira, driving for Chip Ganassi Racing, won\n" +
                "the\n" +
                "pole with a four-lap average speed of 231.342 mph.\n" +
                "\n" +
                "\n" +
                "--Winston West Series addition: The NASCAR Winston West Series\n" +
                "has\n" +
                "added a 10th race to its schedule.\n" +
                "\n" +
                "\n" +
                "The NAPA Auto Parts 200 will be run Sept. 21 at Douglas County\n" +
                "Speedway in Roseburg, Ore.\n" +
                "\n" +
                "\n" +
                "Greg Pursley of Canyon Country has emerged as the top rookie on\n" +
                "the Winston West Series and is in contention for the championship.\n" +
                "\n" +
                "\n" +
                "The return to Douglas County Speedway, a .333-mile paved oval,\n" +
                "will mark the first visit by the Winston West Series to the track\n" +
                "in\n" +
                "three decades.\n" +
                "\n" +
                "\n" +
                "``The NASCAR Winston West Series has a very strong following in\n" +
                "Oregon and throughout the Northwest, and we're very excited to add\n" +
                "this event to the schedule,'' said Chris Boals, director of NASCAR\n" +
                "Touring and the NASCAR Weekly Racing Series. ``It's great to see\n" +
                "the\n" +
                "series return to Oregon.''\n" +
                "\n" +
                "\n" +
                "The Winston West Series added a race at Stockton 99 Speedway on\n" +
                "Aug. 17. The Douglas County peedway race is the second addition to\n" +
                "the schedule.","<label class=\"words\">","</label>"));
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
