package pt.utl.ist.lucene.treceval;

import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.treceval.preprocessors.adhoc.BraunCorpusPreprocessor;
import pt.utl.ist.lucene.treceval.preprocessors.adhoc.BraunCorpusTopicsProcessor;
import pt.utl.ist.lucene.treceval.preprocessors.DirectoryPreprocessor;
import pt.utl.ist.lucene.treceval.preprocessors.ResourceHandler;
import pt.utl.ist.lucene.treceval.preprocessors.XmlResourceHandler;
import pt.utl.ist.lucene.treceval.preprocessors.XmlFieldHandler;

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
    public static void main(String [] args) throws DocumentException, IOException
    {

//        String collectionPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\documents";
//        String topicsPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\topics";
//        String outputDir = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\output";

        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setForceQE(QEEnum.no);
        Configuration LM_STEMMER_BC = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexConfiguration.du.getAnalyzerWithStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);
        Configuration LM_BC = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);
        Configuration VS_BC = new Configuration("version1", "bc","vs",Model.VectorSpaceModel, IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.yes);
        Configuration LM_STEMMER_BC_QE = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexConfiguration.du.getAnalyzerWithStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);
        Configuration LM_BC_QE = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);
        Configuration VS_BC_QE = new Configuration("version1", "bc","vs",Model.VectorSpaceModel, IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,new BraunCorpusPreprocessor(),topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);

        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(LM_STEMMER_BC);
        configurations.add(LM_BC);
        configurations.add(VS_BC);

        IndexConfiguration.indexConfiguration(configurations);

        //We don't need to index these configurations because are the same, the only diference is the query Configuration
        //The index path is created from the version, collection and dir variables, so these thre new configurations
        //will use the same index  
        configurations.add(LM_STEMMER_BC_QE);
        configurations.add(LM_BC_QE);
        configurations.add(VS_BC_QE);

        //Search Topics Runs to submission
        SearchTopics.search(configurations);
    }
}
