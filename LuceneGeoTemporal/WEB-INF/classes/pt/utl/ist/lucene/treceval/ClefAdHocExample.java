package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.treceval.dublincore.DcFieldEnum;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class ClefAdHocExample
{

    private static final Logger logger = Logger.getLogger(ClefAdHocExample.class);

    public static void main(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


//        String collectionPath = Globals.DATA_DIR + "\\clef2008AdHoc\\documents";
        String collectionPath = "D:\\Projectos\\coleccoesIR\\CLEFAdHoc\\telCollection\\bl";
        String topicsPath = Globals.DATA_DIR + "\\clef2008AdHoc\\topics\\bl";
        String outputDir = Globals.DATA_DIR + "\\clef2008AdHoc\\output\\bl";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         *

         */

//    ldEnum.creator ||
//                    dcFieldEnum == DcFieldEnum.contributor ||
//                    dcFieldEnum == DcFieldEnum.description ||
//                    dcFieldEnum == DcFieldEnum.location ||
//                    dcFieldEnum == DcFieldEnum.issued ||
//                    dcFieldEnum == DcFieldEnum.relation)


        XmlFieldHandler xmlTitle = new SimpleXmlFieldHandler(".//dc:title",new MultipleFieldFilter(4),"contents");
        XmlFieldHandler xmlAlternative = new SimpleXmlFieldHandler(".//dcterms:alternative",new MultipleFieldFilter(3),"contents");


        XmlFieldHandler xmlSubject = new SimpleXmlFieldHandler(".//dc:subject",new MultipleFieldFilter(3),"contents");

        XmlFieldHandler xmlCreator = new SimpleXmlFieldHandler(".//dc:creator",new MultipleFieldFilter(3),"contents");
        XmlFieldHandler xmlContributor = new SimpleXmlFieldHandler(".//dc:contributor",new MultipleFieldFilter(2),"contents");

        XmlFieldHandler xmlDescription = new SimpleXmlFieldHandler(".//dc:description",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlTableOfContents = new SimpleXmlFieldHandler(".//dc:tableOfContents",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlDctermsAbstract = new SimpleXmlFieldHandler(".//dcterms:abstract",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlDcAbstract = new SimpleXmlFieldHandler(".//dc:abstract",new MultipleFieldFilter(2),"contents");



        XmlFieldHandler xmlRights = new SimpleXmlFieldHandler(".//dc:rights",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlFormat = new SimpleXmlFieldHandler(".//dc:format",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlRelation = new SimpleXmlFieldHandler(".//dc:relation",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlIsPartOf = new SimpleXmlFieldHandler(".//dcterms:isPartOf",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlBCitation = new SimpleXmlFieldHandler(".//dcterms:bibliographicCitation",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlDate = new SimpleXmlFieldHandler(".//dc:date",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlCoverage = new SimpleXmlFieldHandler(".//dc:coverage",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlPublisher = new SimpleXmlFieldHandler(".//dc:publisher",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlType = new SimpleXmlFieldHandler(".//dc:type",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlLocation = new SimpleXmlFieldHandler(".//dc:location",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlModsLocation = new SimpleXmlFieldHandler(".//mods:location",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlSource = new SimpleXmlFieldHandler(".//dc:source",new MultipleFieldFilter(1),"contents");
        XmlFieldHandler xmlIssued = new SimpleXmlFieldHandler(".//dcterms:issued",new MultipleFieldFilter(1),"contents");
        
        //Global Search Index


        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlTitle);
        xmlFieldHandlers.add(xmlAlternative);
        xmlFieldHandlers.add(xmlSubject);
        xmlFieldHandlers.add(xmlCreator);
        xmlFieldHandlers.add(xmlContributor);
        xmlFieldHandlers.add(xmlDescription);
        xmlFieldHandlers.add(xmlTableOfContents);
        xmlFieldHandlers.add(xmlDctermsAbstract);
        xmlFieldHandlers.add(xmlDcAbstract);
        xmlFieldHandlers.add(xmlRights);
        xmlFieldHandlers.add(xmlFormat);
        xmlFieldHandlers.add(xmlRelation);
        xmlFieldHandlers.add(xmlIsPartOf);
        xmlFieldHandlers.add(xmlBCitation);
        xmlFieldHandlers.add(xmlDate);
        xmlFieldHandlers.add(xmlCoverage);
        xmlFieldHandlers.add(xmlPublisher);
        xmlFieldHandlers.add(xmlType);
        xmlFieldHandlers.add(xmlLocation);
        xmlFieldHandlers.add(xmlModsLocation);
        xmlFieldHandlers.add(xmlSource);
        xmlFieldHandlers.add(xmlIssued);


        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaces.put("mods","http://www.loc.gov/mods");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");
        ResourceHandler resourceHandler = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlers,namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /**
         *
           <topic lang="fr">
                <identifier>10.2452/451-AH</identifier>
                <title>L'armée romaine en Grande-Bretagne</title>
                <description>Trouver des livres ou des publications sur l'invasion et l'occupation de la Grande-Bretagne par les Romains.</description>
            </topic>
         */
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(3),"contents");
        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 1000;

        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser

        Configuration VS_ADHOC = new Configuration("version1", "clef2008AdHoc","lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_ADHOC = new Configuration("version1", "clef2008AdHoc","lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_ADHOC = new Configuration("version1", "clef2008AdHoc","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);

        Configuration VS_3_5GRAMS_CRAN = new Configuration("version1", "clef2008AdHoc","lmstem3_5grams", Model.VectorSpaceModel, IndexCollections.n2_6gramsStem.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", null,outputDir,maxResults);
        List<Configuration> configurations = new ArrayList<Configuration>();
        //we just need these two configurations because the lm and lmstem indexes are the same for all probabilistic models and can be used in vector space because the diference is just an extra index with documents lenght

        configurations.add(LM_ADHOC);
        configurations.add(LM_STEMMER_ADHOC);
//        configurations.add(VS_3_6GRAMS_CRAN);
//        configurations.add(LM_3_6GRAMS_FRONT_CRAN);

//        configurations.add(LM_CRAN);
        //      configurations.add(LM_STEMMER_CRAN);
//        configurations.add(VS_3_5GRAMS_CRAN);

        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);


//        /***
//         * Search Configurations
//         */
//        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
//        queryConfiguration1.setForceQE(QEEnum.no);
//        queryConfiguration1.getQueryProperties().setProperty("lgte.default.order", "sc");
//
//        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_ADHOC));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_ADHOC));
//
//        //Search Topics Runs to submission
//        SearchTopics.search(searchConfigurations);
    }

}
