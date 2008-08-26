package pt.utl.ist.lucene.treceval;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class BrounCorpusExample
{

    private static final Logger logger = Logger.getLogger(BrounCorpusExample.class);
    public static void main(String [] args) throws DocumentException, IOException
    {

//        String collectionPath = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\documents";
//        String topicsPath = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\topics";
//        String outputDir = "/tmp/output";





        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

        String dataDir = Globals.DATA_DIR;
        if(args.length>3)
            dataDir = args[3];

        logger.info("Forcing data dir to: " + dataDir);

        Globals.DATA_DIR = dataDir;

        /**
         * Lets create Broun Corpus Collection preprocessor
         * We just need one field handler for TEXT field @see example
         * example:
         *
         * <DOC>
         *  <DOCNO> AC-01-02-01 </DOCNO>
         *  <TEXT>
         *      EERSTEN DEEL Antwerpse Compilatae
         *      VAN DER STADT, HAERE PAELEN, RECHTEN, OVERHEIJT, POORTERS ENDE INGESETENEN INT GEMEIJN.
         *      TITEL II.
         *      VAN DER STADTS OVERHEIJT, ENDE EERS .........
         *  </TEXT>
         * .......
         *
         * Our text field will have a filter defined in the <b>bottom of this class</b>,
         * what we gone do is to duplicate every text
         * until keyword TITEL and the rest of the text one time
         * we gone put every text in the sabe index field "contents"
         *
         * for details @see ICollectionPreprocessor Architecture Diagrams @ LGTE website
         */

        XmlFieldHandler xmlFieldHandler = new SimpleXmlFieldHandler("./TEXT",new BrounCorpusTextFieldFilter(),"contents");
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlFieldHandler);
        ResourceHandler resourceHandler = new XmlResourceHandler("//DOC","DOCNO",xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /**
         * Now let's create a Topics processor
         * The principle is the same, we need a directoryHandler
         * and we need to give it a ResourceHandler
         */
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./desc",new SimpleFieldFilter(),"contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//top","./num",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 500;
        
        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser

        Configuration VS_BC = new Configuration("version1", "bc","vs", Model.VectorSpaceModel, IndexCollections.du.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration LM_BC = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexCollections.du.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_BC = new Configuration("version1", "bc","vsstem", Model.VectorSpaceModel, IndexCollections.du.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_BC = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexCollections.du.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.du.getWordList(),outputDir,maxResults);
        Configuration BM25_STEMMER_BC = new Configuration("version1", "bc","bm25", Model.OkapiBM25Model, IndexCollections.du.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.du.getWordList(),outputDir,maxResults);

        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(VS_BC);
        configurations.add(LM_BC);
        configurations.add(VS_STEMMER_BC);
        configurations.add(LM_STEMMER_BC);
        configurations.add(BM25_STEMMER_BC);
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.yes);
        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, BM25_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_BC));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, BM25_STEMMER_BC));
        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);
    }

    /*Specific filter for BrounCorpus Resource Collection*/
    static class BrounCorpusTextFieldFilter implements FieldFilter
    {
        public Map<String, String> filter(Element element, String fieldName)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            String text = element.getText();
            BufferedReader reader = new BufferedReader(new StringReader(text));
            StringBuilder firstLines = new StringBuilder();
            try
            {
                String line = reader.readLine();
                for(int li = 0; li < 10 && line != null;li++)
                {
                    if(line.indexOf("TITEL") < 0)
                        firstLines.append(" ").append(line);
                    line = reader.readLine();
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            map.put(Globals.DOCUMENT_TITLE,firstLines.toString());
            map.put(fieldName,firstLines.toString() + " " + firstLines.toString() + " "  + text);
            return map;
        }
    }
}
