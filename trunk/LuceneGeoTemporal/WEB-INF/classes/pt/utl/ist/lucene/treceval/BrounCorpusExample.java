package pt.utl.ist.lucene.treceval;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.treceval.preprocessors.*;
import pt.utl.ist.lucene.treceval.preprocessors.adhoc.BraunCorpusTopicsProcessor;

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
    public static void main(String [] args) throws DocumentException, IOException
    {

//        String collectionPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\documents";
//        String topicsPath = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\topics";
//        String outputDir = "C:\\Servidores\\workspace\\locallucene-r1.5\\collections\\brouncorpus\\output";


        String collectionPath = args[0];
        String topicsPath = args[1];
        String outputDir = args[2];

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
         * for details @see Preprocessor Architecture Diagrams @ LGTE website
         */

        XmlFieldHandler xmlFieldHandler = new SimpleXmlFieldHandler("./TEXT",new BrounCorpusTextFieldFilter(),"contents");
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlFieldHandler);
        ResourceHandler resourceHandler = new XmlResourceHandler("//DOC","DOCNO",xmlFieldHandlers);
        //we could set to directory preprocessor a Properties object with FileExtensions Implementations of DocumentHandler
        DirectoryPreprocessor directoryPreprocessor = new DirectoryPreprocessor(resourceHandler,null);


        //For Topics we didnot use a Topic Processor because topics change fron task to task so we build an AdHoc Class
        // @see preprocessors/addhoc/BraunCorpusTopicsProcessor 
        // it is very simple

        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser
        QueryConfiguration queryConfiguration = new QueryConfiguration();
        queryConfiguration.setForceQE(QEEnum.no);
        Configuration LM_STEMMER_BC = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexConfiguration.du.getAnalyzerWithStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);
        Configuration LM_BC = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);
        Configuration VS_BC = new Configuration("version1", "bc","vs", Model.VectorSpaceModel, IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration,outputDir);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.yes);
        Configuration LM_STEMMER_BC_QE = new Configuration("version1", "bc","lmstem", Model.LanguageModel, IndexConfiguration.du.getAnalyzerWithStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);
        Configuration LM_BC_QE = new Configuration("version1", "bc","lm",Model.LanguageModel , IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);
        Configuration VS_BC_QE = new Configuration("version1", "bc","vs",Model.VectorSpaceModel, IndexConfiguration.du.getAnalyzerNoStemming(),collectionPath,directoryPreprocessor,topicsPath, new BraunCorpusTopicsProcessor(),"contents", IndexConfiguration.du.getWordList(),queryConfiguration2,outputDir);

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
            map.put(fieldName,firstLines.toString() + " " + firstLines.toString() + " "  + text);
            return map;
        }
    }
}
