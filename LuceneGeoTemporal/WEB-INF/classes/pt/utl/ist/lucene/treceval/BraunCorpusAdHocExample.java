package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.treceval.handlers.adhoc.BraunCorpusPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.adhoc.BraunCorpusTopicsProcessor;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.dom4j.DocumentException;

/**
 * @author Jorge Machado
 * @date 20/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class BraunCorpusAdHocExample
{
    //THE DIFERENCE IS THE PREPROCESSOR
    
    public static void main(String [] args) throws DocumentException, IOException
    {

//        String collectionPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\documents";
//        String topicsPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\topics";
//        String outputDir = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\output";

        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

        int maxResults = 1000;


        Configuration LM_STEMMER_BC = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexCollections.du.getAnalyzerWithStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration LM_BC = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexCollections.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration VS_BC = new Configuration("version1", "bc","vs",Model.VectorSpaceModel, IndexCollections.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexCollections.du.getWordList(),outputDir,maxResults);

        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(LM_STEMMER_BC);
        configurations.add(LM_BC);
        configurations.add(VS_BC);
        IndexCollections.indexConfiguration(configurations, Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.yes);
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_BC));
        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);
    }
}
