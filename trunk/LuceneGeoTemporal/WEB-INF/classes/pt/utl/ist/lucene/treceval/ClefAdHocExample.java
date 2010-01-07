package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.dom4j.DocumentException;

import java.io.*;
import java.util.*;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class ClefAdHocExample
{

    /*
     * Basic Configuration
     */
    
    static boolean buildIndexes = false;
    static boolean buildRuns = true;
    static boolean evaluate = true;

//    static String collectionYear = "2008";
//    static String assessmentsBl =  "10.2454-AH-TEL-ENGLISH-CLEF2008.txt";
//    static String assessmentsBnf =  "10.2454-AH-TEL-FRENCH-CLEF2008.txt";
//    static String assessmentsOnb =  "10.2454-AH-TEL-GERMAN-CLEF2008.txt";

    static String collectionYear = "2009";
    static String assessmentsBl =  "10.2454_AH-TEL-ENGLISH-CLEF2009.txt";
    static String assessmentsBnf =  "10.2454_AH-TEL-FRENCH-CLEF2009.txt";
    static String assessmentsOnb =  "10.2454_AH-TEL-GERMAN-CLEF2009.txt";

    /**
     * End of Basic configuration
     */


    private static final Logger logger = Logger.getLogger(ClefAdHocExample.class);

    public static Map<String,String> parameterTunning = null;
    public static String assessmentsFile;
    public static float map;

    public static String country = "bl";
    public static LgteAnalyzerManager.LanguagePackage lang;
    static String qeDocNum;
    static String qeTermNum;
    static String nGramsBoostContents;
    static String nGramsBoostN5;
    static String nGramsBoostN4;
    static String nGramsBoostN3;
    static String nGramsBoostN2;


    public static void main(String [] args) throws DocumentException, IOException
    {
        /**Collection Configuration*/
        country = "bl";
        assessmentsFile = assessmentsBl;
        lang  = IndexCollections.en;
        qeDocNum = "7";
        qeTermNum = "64";
        nGramsBoostContents = "0.45f";
        nGramsBoostN5 = "0.24f";
        nGramsBoostN4 = "0.22f";
        nGramsBoostN3 = "0.01f";
        nGramsBoostN2 = "0.00f";
        compute(args);

//        country = "bnf";
//        assessmentsFile = assessmentsBnf;
//        lang  = IndexCollections.fr;
//        qeDocNum = "8";
//        qeTermNum = "40";
//        nGramsBoostContents = "0.55f";
//        nGramsBoostN5 = "0.24f";
//        nGramsBoostN4 = "0.22f";
//        nGramsBoostN3 = "0.01f";
//        nGramsBoostN2 = "0.00f";
//        compute(args);
//
//        country = "onb";
//        assessmentsFile = assessmentsOnb;
//        lang  = IndexCollections.de;
//        qeDocNum = "8";
//        qeTermNum = "40";
//        nGramsBoostContents = "0.55f";
//        nGramsBoostN5 = "0.24f";
//        nGramsBoostN4 = "0.22f";
//        nGramsBoostN3 = "0.01f";
//        nGramsBoostN2 = "0.00f";
//        compute(args);
    }
    public static void compute(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "F:\\INDEXES";
        args[1] = "D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        String collectionPath = "F:\\coleccoesIR\\CLEFAdHoc\\telCollection\\" + country;
//        String collectionPath = "C:\\WORKSPACE_JM\\DATA\\COLLECTIONS\\telCollection\\" + country;
        String topicsPath = Globals.DATA_DIR + "\\clef" + collectionYear + "AdHoc\\topics\\" + country;
        String outputDir = Globals.DATA_DIR + "\\clef" + collectionYear + "AdHoc\\output\\" + country;
        String assessementsFile = Globals.DATA_DIR + "\\clef" + collectionYear + "AdHoc\\assessements\\" + country + "\\" + assessmentsFile;

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         *
         *  Collections Configuration
         */

        Map<String,String> namespaces = new HashMap<String,String>();
        namespaces.put("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaces.put("mods","http://www.loc.gov/mods");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("dcterms", "http://purl.org/dc/terms/");

        /*Regular Indexes*/
        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlers,"contents");
        ResourceHandler resourceHandler = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlers,namespaces);
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /*N-Grams*/
        List<XmlFieldHandler> xmlFieldHandlersNG = new ArrayList<XmlFieldHandler>();
        addFields(xmlFieldHandlersNG,"contents");
        addFields(xmlFieldHandlersNG,"contentsN2");
        addFields(xmlFieldHandlersNG,"contentsN3");
        addFields(xmlFieldHandlersNG,"contentsN4");
        addFields(xmlFieldHandlersNG,"contentsN5");
        addFields(xmlFieldHandlersNG,"contentsN6");
        ResourceHandler resourceHandlerNG = new XmlResourceHandler("//record","./header/id","./id",xmlFieldHandlersNG,namespaces);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryNG = new CDirectory(resourceHandlerNG,null);

        /*****************************************************
         *
         *  Topics Configuration
         *
         *
         * <topic lang="fr">
         *       <identifier>10.2452/451-AH</identifier>
         *       <title>L'arme romaine en Grande-Bretagne</title>
         *       <description>Trouver des livres ou des publications sur l'invasion et l'occupation de la Grande-Bretagne par les Romains.</description>
         *   </topic>
         ******************************************************/

        //Regular Indexes
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        addTopics(xmlTopicFieldHandlers,"contents");
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //N-Grams
        //The handlers will be the same in 6 different fields, each one associated with different analyzers
        //The fields suffixed with NX will have XGrams Tokenizers        
        List<XmlFieldHandler> xmlTopicFieldHandlersNG = new ArrayList<XmlFieldHandler>();
        addTopics(xmlTopicFieldHandlersNG,"contents");
        addTopics(xmlTopicFieldHandlersNG,"contentsN2");
        addTopics(xmlTopicFieldHandlersNG,"contentsN3");
        addTopics(xmlTopicFieldHandlersNG,"contentsN4");
        addTopics(xmlTopicFieldHandlersNG,"contentsN5");
        ResourceHandler topicResourceHandlerNG = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlersNG);
        TrecEvalOutputFormatFactory factoryNG =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectoryNG = new TDirectory(topicResourceHandlerNG,factoryNG);



        /*****************
         *
         * Configurations
         *
         */
        //maxResults in output File per topic;
        int maxResults = 1000;

        //Regular Indexes
        Configuration VS_ADHOC = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lm", Model.VectorSpaceModel, lang.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration LM_ADHOC = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lm",Model.LanguageModel , lang.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER_ADHOC = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lmstem", Model.VectorSpaceModel, lang.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER_ADHOC = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lmstem", Model.LanguageModel, lang.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", lang.getWordList(),outputDir,maxResults);

        //N-Grams will use one broker analyzer where each index will be created using different grams
        Map<String, Analyzer> ngramsAnalizers = new HashMap<String,Analyzer>();
        ngramsAnalizers.put("contents",lang.getAnalyzerNoStemming());
        ngramsAnalizers.put("contentsN2", LgteAnalyzerManager.getInstance().getLanguagePackage(2,2,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN3", LgteAnalyzerManager.getInstance().getLanguagePackage(3,3,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN4", LgteAnalyzerManager.getInstance().getLanguagePackage(4,4,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN5", LgteAnalyzerManager.getInstance().getLanguagePackage(5,5,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        ngramsAnalizers.put("contentsN6", LgteAnalyzerManager.getInstance().getLanguagePackage(6,6,EdgeNGramTokenFilter.Side.FRONT).getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer lgteBrokerStemAnalyzer = new LgteBrokerStemAnalyzer(ngramsAnalizers);
        Configuration VS_2_6GRAMS = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lmstem2_6grams", Model.VectorSpaceModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectoryNG,topicsPath, topicsDirectoryNG,"contents", null,outputDir,maxResults);
        Configuration LM_2_6GRAMS = new Configuration("version1", "clef" + collectionYear + "AdHoc" + country,"lmstem2_6grams", Model.LanguageModel, lgteBrokerStemAnalyzer,collectionPath,collectionsDirectoryNG,topicsPath, topicsDirectoryNG,"contents", null,outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(LM_ADHOC);
        configurations.add(LM_STEMMER_ADHOC);
        configurations.add(LM_2_6GRAMS);


        if(buildIndexes)
            IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);


//        /***
//         * Search Configurations
//         */



        QueryConfiguration queryConfigurationNoQE = new QueryConfiguration();
        queryConfigurationNoQE.setForceQE(QEEnum.no);
        queryConfigurationNoQE.getQueryProperties().setProperty("lgte.default.order", "sc");

        QueryConfiguration queryConfigurationNoQE_ngrams = new QueryConfiguration();
        queryConfigurationNoQE_ngrams.setForceQE(QEEnum.no);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contents",nGramsBoostContents);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN5",nGramsBoostN5);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN4",nGramsBoostN4);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN3",nGramsBoostN3);
        queryConfigurationNoQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN2",nGramsBoostN2);


        //With Query Expansion
        QueryConfiguration queryConfigurationQE = new QueryConfiguration();
        queryConfigurationQE.setForceQE(QEEnum.text);
        queryConfigurationQE.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfigurationQE.getQueryProperties().setProperty("QE.doc.num",qeDocNum);
        queryConfigurationQE.getQueryProperties().setProperty("QE.term.num",qeTermNum);

        QueryConfiguration queryConfigurationQE_ngrams = new QueryConfiguration();
        queryConfigurationQE_ngrams.setForceQE(QEEnum.text);

        queryConfigurationQE_ngrams.getQueryProperties().setProperty("lgte.default.order", "sc");
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("QE.doc.num",qeDocNum);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("QE.term.num",qeTermNum);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contents",nGramsBoostContents);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN5",nGramsBoostN5);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN4",nGramsBoostN4);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN3",nGramsBoostN3);
        queryConfigurationQE_ngrams.getQueryProperties().setProperty("field.boost.contentsN2",nGramsBoostN2);



        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, VS_ADHOC, 1));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, LM_ADHOC, 2));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, VS_STEMMER_ADHOC, 3));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE, LM_STEMMER_ADHOC, 4));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE_ngrams,VS_2_6GRAMS, 5));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationNoQE_ngrams,LM_2_6GRAMS, 6));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, VS_ADHOC, 7));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, LM_ADHOC, 8));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, VS_STEMMER_ADHOC, 9));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE, LM_STEMMER_ADHOC, 10));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE_ngrams,VS_2_6GRAMS, 11));
//        searchConfigurations.add(new SearchConfiguration(queryConfigurationQE_ngrams,LM_2_6GRAMS, 12 ));


        if(parameterTunning != null)
            tunning(queryConfigurationQE_ngrams);


//        //Search Topics Runs to submission
        if(buildRuns)
            SearchTopics.search(searchConfigurations);

        if(evaluate)
        {
            SearchTopics.evaluateMetrics(searchConfigurations,assessementsFile);
            SearchTopics.createRunPackage(searchConfigurations.get(0).getConfiguration().getOutputDir(),searchConfigurations);
        }

        if(parameterTunning != null)
            reportResults(searchConfigurations.get(0));


    }

    static FileWriter parameterTunningOutput;

    private static void tunning(QueryConfiguration queryConfiguration) throws IOException
    {

        if(parameterTunning != null)
        {
            for(Map.Entry<String,String> entry: parameterTunning.entrySet())
            {
                queryConfiguration.getQueryProperties().setProperty(entry.getKey(),entry.getValue());
            }
        }

    }

    private static void reportResults(SearchConfiguration searchConfiguration) throws IOException
    {
        ReportFile reportFile = new ReportFile(searchConfiguration.getOutputReportFile(SearchTopics.outputReportSuffix));
        ReportResult result = reportFile.getResult("all");
        for(int i = 0;i < result.getValues().length;i++)
        {

            parameterTunningOutput.write(result.getValues()[i]+";");
            if(ReportResult.names[i].equals("map"))
            {
                map = Float.parseFloat(result.getValues()[i]);
            }
        }
        parameterTunningOutput.write("\n");
    }


    private static void addTopics(List<XmlFieldHandler> xmlTopicFieldHandlers, String field)
    {
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(3),field);
        XmlFieldHandler xmlDescTopicFieldHandler = new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),field);
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlDescTopicFieldHandler);
    }

    private static void addFields(List<XmlFieldHandler> xmlFieldHandlers, String field)
    {
        XmlFieldHandler xmlTitle = new SimpleXmlFieldHandler(".//dc:title",new MultipleFieldFilter(4),field);
        XmlFieldHandler xmlAlternative = new SimpleXmlFieldHandler(".//dcterms:alternative",new MultipleFieldFilter(3),field);


        XmlFieldHandler xmlSubject = new SimpleXmlFieldHandler(".//dc:subject",new MultipleFieldFilter(3),field);

        XmlFieldHandler xmlCreator = new SimpleXmlFieldHandler(".//dc:creator",new MultipleFieldFilter(3),field);
        XmlFieldHandler xmlContributor = new SimpleXmlFieldHandler(".//dc:contributor",new MultipleFieldFilter(2),field);

        XmlFieldHandler xmlDescription = new SimpleXmlFieldHandler(".//dc:description",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlTableOfContents = new SimpleXmlFieldHandler(".//dc:tableOfContents",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlDctermsAbstract = new SimpleXmlFieldHandler(".//dcterms:abstract",new MultipleFieldFilter(2),field);
        XmlFieldHandler xmlDcAbstract = new SimpleXmlFieldHandler(".//dc:abstract",new MultipleFieldFilter(2),field);



        XmlFieldHandler xmlRights = new SimpleXmlFieldHandler(".//dc:rights",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlFormat = new SimpleXmlFieldHandler(".//dc:format",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlRelation = new SimpleXmlFieldHandler(".//dc:relation",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlIsPartOf = new SimpleXmlFieldHandler(".//dcterms:isPartOf",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlBCitation = new SimpleXmlFieldHandler(".//dcterms:bibliographicCitation",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlDate = new SimpleXmlFieldHandler(".//dc:date",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlCoverage = new SimpleXmlFieldHandler(".//dc:coverage",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlPublisher = new SimpleXmlFieldHandler(".//dc:publisher",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlType = new SimpleXmlFieldHandler(".//dc:type",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlLocation = new SimpleXmlFieldHandler(".//dc:location",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlModsLocation = new SimpleXmlFieldHandler(".//mods:location",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlSource = new SimpleXmlFieldHandler(".//dc:source",new MultipleFieldFilter(1),field);
        XmlFieldHandler xmlIssued = new SimpleXmlFieldHandler(".//dcterms:issued",new MultipleFieldFilter(1),field);

        //Global Search Index



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
    }


    public static class Tunning
    {

        public static void main(String[] args) throws DocumentException, IOException
        {

            SearchTopics.useAllaysTheSameSearcher = true;

            parameterTunningOutput = new FileWriter("D:/optimizeGE.txt");
//

            TunningParameter t1 = new TunningParameter("QE.doc.num", 6, 10, 2,true,5);
            TunningParameter t2 = new TunningParameter("QE.term.num", 30, 70, 10,true,30);
            TunningParameter t3 = new TunningParameter("field.boost.contents", 0.45f, 0.6f, 0.05f,0.53f);
            TunningParameter t4 = new TunningParameter("field.boost.contentsN5", 0.05f, 0.35f, 0.1f,0.14f);
            TunningParameter t5 = new TunningParameter("field.boost.contentsN4", 0.05f, 0.35f, 0.1f,0.11f);
            TunningParameter t6 = new TunningParameter("field.boost.contentsN3", 0.0f, 0.3f, 0.1f,0.11f);
            TunningParameter t7 = new TunningParameter("field.boost.contentsN2", 0.0f, 0.3f, 0.1f,0.11f);

//            TunningParameter t1 = new TunningParameter("QE.doc.num", 4, 15, 2,true,5);
//            TunningParameter t2 = new TunningParameter("QE.term.num", 15, 60, 5,true,30);
//            TunningParameter t3 = new TunningParameter("field.boost.contents", 0.45f, 0.8f, 0.05f,0.53f);
//            TunningParameter t4 = new TunningParameter("field.boost.contentsN5", 0.05f, 0.3f, 0.05f,0.14f);
//            TunningParameter t5 = new TunningParameter("field.boost.contentsN4", 0.05f, 0.3f, 0.05f,0.11f);
//            TunningParameter t6 = new TunningParameter("field.boost.contentsN3", 0.05f, 0.3f, 0.05f,0.11f);
//            TunningParameter t7 = new TunningParameter("field.boost.contentsN2", 0.0f, 0.3f, 0.05f,0.11f);
//            8	55	0,45	0,25	0,2	0,1	0


//            TunningParameter t1 = new TunningParameter("QE.doc.num", 6, 12, 1,true,8);
//            TunningParameter t2 = new TunningParameter("QE.term.num", 40, 70, 3,true,55);
//            TunningParameter t3 = new TunningParameter("field.boost.contents", 0.35f, 0.6f, 0.02f,0.45f);
//            TunningParameter t4 = new TunningParameter("field.boost.contentsN5", 0.20f, 0.4f, 0.02f,0.25f);
//            TunningParameter t5 = new TunningParameter("field.boost.contentsN4", 0.1f, 0.3f, 0.01f,0.2f);
//            TunningParameter t6 = new TunningParameter("field.boost.contentsN3", 0.0f, 0.2f, 0.01f,0.1f);
//            TunningParameter t7 = new TunningParameter("field.boost.contentsN2", 0.0f, 0.1f, 0.01f,0.0f);


            List<TunningParameter> tunningParameters = new ArrayList<TunningParameter>();
            tunningParameters.add(t1);
            tunningParameters.add(t2);
            tunningParameters.add(t3);
            tunningParameters.add(t4);
            tunningParameters.add(t5);
            tunningParameters.add(t6);
            tunningParameters.add(t7);
            tune(tunningParameters);
            parameterTunningOutput.flush();
            parameterTunningOutput.close();
            SearchTopics.indexSearcher.close();
        }

        public static void tune(List<TunningParameter> parameters) throws DocumentException, IOException
        {

            HashMap<String,String> fixed = new HashMap<String,String>();
            for(TunningParameter t: parameters)
            {
                parameterTunningOutput.write(t.key + ";");
                fixed.put(t.key,"" + t.getStartValue());
            }
            for(String name : ReportResult.names)
                parameterTunningOutput.write(name + ";");
            parameterTunningOutput.write("\n");

            for(TunningParameter t: parameters)
            {

                ClefAdHocExample.parameterTunning = new HashMap<String,String>();
                ClefAdHocExample.parameterTunning.putAll(fixed);
                float maxMAPObtained = 0.0f;
                String parameter = "";
                for(t.nowParameter = t.minValue; t.nowParameter < t.maxValue; t.nowParameter += t.increment)
                {
                    System.out.println("Testing Parameter: " + t.key + "=" + t.nowParameter);
                    ClefAdHocExample.parameterTunning.put(t.key,t.getValue());
                    for(TunningParameter tAux: parameters)
                    {
                        parameterTunningOutput.write(ClefAdHocExample.parameterTunning.get(tAux.key) + ";");
                    }
                    ClefAdHocExample.main(null);
                    System.out.println("MAP:"+map );
                    if(ClefAdHocExample.map > maxMAPObtained)
                    {
                        maxMAPObtained = ClefAdHocExample.map;
                        parameter = t.getValue();
                        parameterTunningOutput.flush();
                    }
                }
                fixed.put(t.key,""+parameter);
                parameterTunningOutput.flush();
            }
        }

        public static class TunningParameter
        {
            public float nowParameter;
            public String key;
            public float maxValue;
            public float minValue;
            public float increment;
            public boolean typeInt = false;
            public float startValue;

            public TunningParameter(String key, float minValue, float maxValue, float increment, float startValue)
            {
                this(key,minValue,maxValue,increment,false,startValue);

            }
            public TunningParameter(String key, float minValue, float maxValue, float increment,boolean typeInt,float startValue)
            {
                this.key = key;
                this.maxValue = maxValue;
                this.minValue = minValue;
                this.increment = increment;
                this.typeInt = typeInt;
                this.startValue = startValue;
            }

            public String getValue()
            {
                if(typeInt)
                    return "" + ((int) nowParameter);
                else
                    return "" + nowParameter;
            }
            public String getStartValue()
            {
                if(typeInt)
                    return "" + ((int) startValue);
                else
                    return "" + startValue;
            }
        }
    }





}
