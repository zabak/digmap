package pt.utl.ist.lucene.treceval;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.dom4j.*;

import java.io.*;
import java.util.*;
import java.net.MalformedURLException;

import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.forms.RectangleForm;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.utils.StringComparator;
import pt.utl.ist.lucene.utils.extractors.SwingHTMLParser;

/**
 * @author Jorge Machado
 * @date 21/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class DmozExample
{

    private static final String HTML_PATH = "C:\\Users\\Jorge\\Desktop\\AdvertisementCollection\\dmoz-ads-docs";

    private static final Logger logger = Logger.getLogger(DmozExample.class);

    private static final String INDEXES_PATH_NAME = "dmoz";

    public static void main(String [] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";


        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        String collectionPath = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator + "documents";
        String topicsPath = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator + "topics";
        String outputDir = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator +"output";

        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + Globals.DATA_DIR);


        /**
         * Lets create DMOZ Collection preprocessor
         * We just need one field handler for TEXT field @see example
         * example:
         *
         * In this collection we will just index the text in the HTML file corresponding to URL field in each document
         *
         *
         * for details @see ICollectionPreprocessor Architecture Diagrams @ LGTE website
         */

        //Global Search Index
        XmlFieldHandler xmlTextFieldHandler = new SimpleXmlFieldHandler("@id",new DmozTextFieldFilter(),"contents");
        XmlFieldHandler xmlGeoBoxFieldHandler = new SimpleXmlFieldHandler("./yPlace/boundingBox",new DmozGeoBoxFieldFilter(),"contents");
        XmlFieldHandler xmlGeoPointFieldHandler = new SimpleXmlFieldHandler("./yPlace/centroid",new DmozGeoCentroideFieldFilter(),"contents");


        List<XmlFieldHandler> xmlFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlFieldHandlers.add(xmlTextFieldHandler);
        xmlFieldHandlers.add(xmlGeoBoxFieldHandler);
        xmlFieldHandlers.add(xmlGeoPointFieldHandler);

        ResourceHandler resourceHandler = new XmlResourceHandler("//ad","@id",xmlFieldHandlers);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectory = new CDirectory(resourceHandler,null);

        /**
         * Now let's create a Topics processor
         * The principle is the same, we need a directoryHandler
         * and we need to give it a ResourceHandler
         *  Example
         *  <top>
         *     <num>1</num>
         *     <description>what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .</description>
         *  </top>
         */
        XmlFieldHandler xmlLocaleTopicFieldHandler = new SimpleXmlFieldHandler("./locale",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlCategoryTopicFieldHandler = new SimpleXmlFieldHandler("./category",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlGeoBoxTopicFieldHandler = new SimpleXmlFieldHandler("./boundingBox",new DmozGeoBoxTopicFieldFilter(),"contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlTopicFieldHandlers.add(xmlLocaleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlCategoryTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlGeoBoxTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./num",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);

        //maxResults in output File per topic;
        int maxResults = 500;

        //Lets create our configuration indexes
        //We gone put the diferences about model, output folder name, analyser

        Configuration VS = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM = new Configuration("version1", INDEXES_PATH_NAME,"lm",Model.LanguageModel , IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration VS_STEMMER = new Configuration("version1", INDEXES_PATH_NAME,"lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
        Configuration LM_STEMMER = new Configuration("version1", INDEXES_PATH_NAME,"lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M3 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.BB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M4 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.DLHHypergeometricDFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M5 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.IFB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M6 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.InExpB2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M7 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.InExpC2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M8 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.InL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M9 = new Configuration("version1", INDEXES_PATH_NAME"lm", Model.OkapiBM25Model, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);
//        Configuration M10 = new Configuration("version1", INDEXES_PATH_NAME,"lm", Model.PL2DFRModel, IndexCollections.en.getAnalyzerNoStemming(),collectionPath,collectionsDirectory,topicsPath, topicsDirectory,"contents", IndexCollections.en.getWordList(),outputDir,maxResults);


        List<Configuration> configurations = new ArrayList<Configuration>();
        //we just need these two configurations because the lm and lmstem indexes are the same for all probabilistic models and can be used in vector space because the diference is just an extra index with documents lenght

//        configurations.add(LM);
//        configurations.add(LM_STEMMER);
//        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        /***
         * Search Configurations
         */
        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.text);

        QueryConfiguration queryConfiguration3 = new QueryConfiguration();
        queryConfiguration3.setForceQE(QEEnum.lgte);



        QueryConfiguration queryConfiguration4 = new QueryConfiguration();
        queryConfiguration4.setForceQE(QEEnum.no);
        queryConfiguration4.getQueryProperties().put("lgte.default.filter","no");

        QueryConfiguration queryConfiguration5 = new QueryConfiguration();
        queryConfiguration5.setForceQE(QEEnum.text);
        queryConfiguration5.getQueryProperties().put("lgte.default.filter","no");

        QueryConfiguration queryConfiguration6 = new QueryConfiguration();
        queryConfiguration6.setForceQE(QEEnum.lgte);
        queryConfiguration6.getQueryProperties().put("lgte.default.filter","no");





        QueryConfiguration queryConfiguration7 = new QueryConfiguration();
        queryConfiguration7.setForceQE(QEEnum.no);
        queryConfiguration7.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration7.getQueryProperties().put("lgte.default.order","sc");

        QueryConfiguration queryConfiguration8 = new QueryConfiguration();
        queryConfiguration8.setForceQE(QEEnum.text);
        queryConfiguration8.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration8.getQueryProperties().put("lgte.default.order","sc");


        QueryConfiguration queryConfiguration9 = new QueryConfiguration();
        queryConfiguration9.setForceQE(QEEnum.no);
        queryConfiguration9.getQueryProperties().put("lgte.default.order","sc");

        QueryConfiguration queryConfiguration10 = new QueryConfiguration();
        queryConfiguration10.setForceQE(QEEnum.text);
        queryConfiguration10.getQueryProperties().put("lgte.default.order","sc");




        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM_STEMMER));
////
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM_STEMMER));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM_STEMMER));
//
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS_STEMMER));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM_STEMMER));
//
        searchConfigurations.add(new SearchConfiguration(queryConfiguration7, VS));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration7, LM));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration7, VS_STEMMER));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration7, LM_STEMMER));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration8, VS));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration8, LM));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration8, VS_STEMMER));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration8, LM_STEMMER));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration9, VS));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration9, LM));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration9, VS_STEMMER));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration9, LM_STEMMER));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration10, VS));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration10, LM));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration10, VS_STEMMER));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration10, LM_STEMMER));
//
//

//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M3));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M4));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M5));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M6));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M7));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M8));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M9));
//        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, M10));

        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);
    }

    static class DmozTextFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {
            FilteredFields fields = new FilteredFields();
            String id = element.getText();
            String filePathHtml = HTML_PATH + File.separator + id + ".html";
            SwingHTMLParser htmlParser = new SwingHTMLParser();
            try
            {
                htmlParser.init(new File(filePathHtml));
                
                String title = htmlParser.getTitle();
                String h1 = htmlParser.getH1();
                String h2 = htmlParser.getH2();
                String h3 = htmlParser.getH3();
                String h4 = htmlParser.getH4();
                String anchors = htmlParser.getAnchor();
                String text = htmlParser.getText();
                StringBuilder textToIndex = new StringBuilder();
                textToIndex.append(title).append(" ")
                        .append(title).append(" ")
                        .append(title).append(" ")
                        .append(h1).append(" ")
                        .append(h1).append(" ")
                        .append(h2).append(" ")
                        .append(h3).append(" ")
                        .append(h4).append(" ")
                        .append(anchors).append(" ")
                        .append(text);
                Map<String,String> indexedFields = new HashMap<String,String>();
                indexedFields.put(fieldName,textToIndex.toString());
                fields.setTextFields(indexedFields);

                Map<String,String> storedFields = new HashMap<String,String>();
                storedFields.put("contentStore",text);
                fields.setStoredTextFields(storedFields);

                return fields;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class DmozGeoBoxFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {
            RectangleForm rectangleForm = getRectangleFormFromBoundingBox((Element) element);
            List<Field> fields = LgteDocumentWrapper.getGeoBoxFields(rectangleForm);
            return new FilteredFields(fields);
        }
    }

    static class DmozGeoCentroideFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {
            Element centroide = (Element) element;
            double latitude = Double.parseDouble(centroide.selectSingleNode("latitude").getText());
            double longitude = Double.parseDouble(centroide.selectSingleNode("longitude").getText());
            List<Field> fields = LgteDocumentWrapper.getGeoPointFields(latitude,longitude);
            return new FilteredFields(fields);
        }
    }

    static class DmozGeoBoxTopicFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {
            Element boundingBox = (Element) element;
            double north = Double.parseDouble(boundingBox.selectSingleNode("north").getText());
            double south = Double.parseDouble(boundingBox.selectSingleNode("south").getText());
            double east = Double.parseDouble(boundingBox.selectSingleNode("east").getText());
            double west = Double.parseDouble(boundingBox.selectSingleNode("west").getText());
            Map<String,String> fields = new HashMap<String,String>();
            fields.put("north",""+north);
            fields.put("south",""+south);
            fields.put("east",""+east);
            fields.put("west",""+west);
            return new FilteredFields(fields);

        }
    }

    private static RectangleForm getRectangleFormFromBoundingBox(Element boundingBox)
    {
        Element southWest = (Element) ((Element)boundingBox).selectSingleNode("southWest");
        Element northEast = (Element) ((Element)boundingBox).selectSingleNode("northEast");
        double latitudeSW = Double.parseDouble(southWest.selectSingleNode("latitude").getText());
        double longitudeSW = Double.parseDouble(southWest.selectSingleNode("longitude").getText());
        double latitudeNE = Double.parseDouble(northEast.selectSingleNode("latitude").getText());
        double longitudeNE = Double.parseDouble(northEast.selectSingleNode("longitude").getText());
        return new RectangleForm(latitudeNE,longitudeSW,latitudeSW,longitudeNE);

    }

    public static class TopicsGenerator
    {
        public static void main(String[] args)
        {

            args = new String[2];
            args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
            args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";

            System.out.println("porra");
            Globals.INDEX_DIR = args[0];
            Globals.DATA_DIR = args[1];

            String collectionPath = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator + "documents";
            String topicsPath = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator + "topics";
            String assessementsPath = Globals.DATA_DIR + File.separator + INDEXES_PATH_NAME + File.separator + "assessements";

            try
            {
                Document dom = Dom4jUtil.parse(new File(collectionPath + File.separator + "dmoz-ads.xml"));
                //distinct-values nao funciona no Dom4j não implementa o XPATH 2.0 penso eu
                List<CategoryRecord> categories = getCategories(dom);



//                List<Object> selectedCategories = selectRandom(categories,25);
                List<CategoryRecord> selectedCategories = categories.subList(0,25);
                try
                {
                    FileOutputStream topicsFileOutputStream =  new FileOutputStream(new File(topicsPath + File.separator + "topics.xml"));
                    OutputStreamWriter topicsWriter = new OutputStreamWriter(topicsFileOutputStream,"UTF-8");
                    FileOutputStream qrelsOutputStream =  new FileOutputStream(new File(assessementsPath + File.separator + "qrels"));
                    OutputStreamWriter qrelsWriter = new OutputStreamWriter(qrelsOutputStream,"UTF-8");
                    topicsWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
                    topicsWriter.write("<topics>");
                    int i = 1;
                    for(CategoryRecord selectedCategory: selectedCategories)
                    {
                        List <RectangleFormLocal> rectangleForms = getRectanglesInCategory(dom, selectedCategory.getCategory());
                        RectangleFormLocal selectedRectangleForm = (RectangleFormLocal) selectRandom(rectangleForms,1).get(0);
                        System.out.println("[ " + selectedCategory.getCategory() + " : " + selectedRectangleForm + " ]");
                        topicsWriter.write("<topic>");
                        topicsWriter.write("<num>" + i + "</num>");
                        topicsWriter.write("<category>" + selectedCategory.getCategory() + "</category>");
                        topicsWriter.write("<locale>" + selectedRectangleForm.getLocale() + "</locale>");
                        topicsWriter.write("<boundingBox>");
                        topicsWriter.write("<north>" + selectedRectangleForm.getRectangleForm().getNorth() + "</north>");
                        topicsWriter.write("<west>" + selectedRectangleForm.getRectangleForm().getWest() + "</west>");
                        topicsWriter.write("<south>" + selectedRectangleForm.getRectangleForm().getSouth() + "</south>");
                        topicsWriter.write("<east>" + selectedRectangleForm.getRectangleForm().getEast() + "</east>");
                        topicsWriter.write("</boundingBox>");
                        topicsWriter.write("</topic>");

                        List<String> docnums = getContainedRecords(dom,selectedCategory.getCategory(),selectedRectangleForm.getRectangleForm());
                        for(String docnum:docnums)
                        {
                            qrelsWriter.write(i + " 0 " + docnum + " 1\n");
                        }
                        i++;
                    }
                    topicsWriter.write("</topics>");
                    topicsWriter.close();
                    topicsFileOutputStream.close();
                    qrelsWriter.close();
                    qrelsOutputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            catch (DocumentException e)
            {
                e.printStackTrace();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

        }

        public static List<String> getContainedRecords(Document dom, String category, RectangleForm rectangleForm)
        {
            return getRelevantRecords(dom,category,rectangleForm,true);
        }
        public static List<String> getOverlapedRecords(Document dom, String category, RectangleForm rectangleForm)
        {
            return getRelevantRecords(dom,category,rectangleForm,false);
        }
        public static List<String> getRelevantRecords(Document dom, String category, RectangleForm rectangleForm, boolean contained)
        {
            List<String> docnums = new ArrayList<String>();
            XPath xPathLocales = dom.createXPath("//ad[contains(dmozCategories,'" + category + "')]");
            List<Node> nodes = xPathLocales.selectNodes(dom.getRootElement());
            for(Node n: nodes)
            {
                Node id = ((Element)n).selectSingleNode("@id");
                Element yPlace = (Element) ((Element)n).selectSingleNode("yPlace");
                Element boundingBox = (Element) ((Element)yPlace).selectSingleNode("boundingBox");
                RectangleForm evaluatedRectangleForm = getRectangleFormFromBoundingBox(boundingBox);
                if(contained && evaluatedRectangleForm.isContained(rectangleForm))
                    docnums.add(id.getText());
                else if(!contained && evaluatedRectangleForm.isOverlap(rectangleForm))
                    docnums.add(id.getText());
            }
            System.out.println("=========================");
            System.out.println("Docs Found:");
            System.out.println("=========================");
            for(String docnum: docnums)
            {
                System.out.println(docnum);
            }
            return docnums;
        }



        private static List<String> getLocales(Document dom)
        {
            XPath xPathLocales = dom.createXPath("//yPlace/name");
            List<Node> nodes = xPathLocales.selectNodes(dom.getRootElement());
            List<String> allLocales = new ArrayList<String>();
            HashMap<String,Integer> localesMap = new HashMap<String,Integer>();
            for(Node n: nodes)
            {
                Integer localeCounter = localesMap.get(n.getText());
                if(localeCounter == null)
                    localesMap.put(n.getText(),1);
                else
                    localesMap.put(n.getText(), localeCounter + 1);
            }

            System.out.println("=========================");
            System.out.println("Locales Found:");
            System.out.println("=========================");
            for(Map.Entry entry: localesMap.entrySet())
            {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                allLocales.add(entry.getKey().toString());
            }
            Collections.sort(allLocales,StringComparator.getInstance());
            return allLocales;
        }

        private static List<CategoryRecord> getCategories(Document dom)
        {
            XPath xPathLocales = dom.createXPath("//dmozCategories");
            List<Node> nodes = xPathLocales.selectNodes(dom.getRootElement());
            List<CategoryRecord> allCategories = new ArrayList<CategoryRecord>();
            HashMap<String,Integer> localesMap = new HashMap<String,Integer>();
            for(Node n: nodes)
            {
                String[] categoriesArray = n.getText().split(",");
                for(String category: categoriesArray)
                {
                    Integer categoryCounter = localesMap.get(category);
                    if(categoryCounter == null)
                        localesMap.put(category,1);
                    else
                        localesMap.put(category, categoryCounter + 1);
                }
            }

            System.out.println("=========================");
            System.out.println("Categories Found:");
            System.out.println("=========================");


            for(Map.Entry<String,Integer> entry: localesMap.entrySet())
            {
                allCategories.add(new CategoryRecord(entry.getValue(),entry.getKey()));
            }
            Collections.sort(allCategories,CategoryRecordsNumberComparator.getInstance());
            for(CategoryRecord ceCategoryRecord: allCategories)
            {
                System.out.println(ceCategoryRecord.getCategory() + ":" + ceCategoryRecord.getRecords());
            }

            return allCategories;
        }

        private static List<RectangleFormLocal> getRectanglesInCategory(Document dom, String category)
        {
            List<RectangleFormLocal> rectangles = new ArrayList<RectangleFormLocal>();
            XPath xPathLocales = dom.createXPath("//ad[contains(dmozCategories,'" + category + "')]/yPlace");
            List<Node> nodes = xPathLocales.selectNodes(dom.getRootElement());
            for(Node place: nodes)
            {
                Element name = (Element) place.selectSingleNode("name");
                Element n = (Element) place.selectSingleNode("boundingBox");
                Element southWest = (Element) ((Element)n).selectSingleNode("southWest");
                Element northEast = (Element) ((Element)n).selectSingleNode("northEast");
                double latitudeSW = Double.parseDouble(southWest.selectSingleNode("latitude").getText());
                double longitudeSW = Double.parseDouble(southWest.selectSingleNode("longitude").getText());
                double latitudeNE = Double.parseDouble(northEast.selectSingleNode("latitude").getText());
                double longitudeNE = Double.parseDouble(northEast.selectSingleNode("longitude").getText());
                RectangleForm rectangleForm = new RectangleForm(latitudeNE,longitudeSW,latitudeSW,longitudeNE);
                RectangleFormLocal rectangleFormLocal = new RectangleFormLocal(rectangleForm,name.getText());
                rectangles.add(rectangleFormLocal);
            }
            System.out.println("=========================");
            System.out.println("Rectangles Found:");
            System.out.println("=========================");
            for(RectangleFormLocal rectangleFormLocal: rectangles)
            {
                System.out.println(rectangleFormLocal.getLocale() + ":" + rectangleFormLocal.getRectangleForm());
            }
            return rectangles;
        }

        private static List<Object> selectRandom(List objects, int n)
        {
            List selectedElements = new ArrayList();
            for(int i =0; i < n; i++)
            {
                Random random = new Random(System.currentTimeMillis());
                int randomInt = random.nextInt();
                while(randomInt < 0)
                {
//                        System.out.println("< 0");
                    randomInt = random.nextInt();
                }

                int selectedPos = randomInt % objects.size();

                selectedElements.add(objects.get(selectedPos));
                objects.remove(selectedPos);
            }
            System.out.println("=========================");
            System.out.println("Selected:");
            System.out.println("=========================");
            if(objects.size() > 0 && objects.get(0) instanceof String)
            {
                Collections.sort(selectedElements,StringComparator.getInstance());
            }
            for(Object local: selectedElements)
            {
                System.out.println(local);
            }
            return selectedElements;
        }

    }

    public static class CategoryRecord
    {
        private int records;
        private String category;


        public CategoryRecord(int records, String category)
        {
            this.records = records;
            this.category = category;
        }


        public int getRecords()
        {
            return records;
        }

        public void setRecords(int records)
        {
            this.records = records;
        }

        public String getCategory()
        {
            return category;
        }

        public void setCategory(String category)
        {
            this.category = category;
        }

        public String toString()
        {
            return category;
        }
    }

    public static class CategoryRecordsNumberComparator implements java.util.Comparator<CategoryRecord>
    {
        private static CategoryRecordsNumberComparator instance = null;

        public static CategoryRecordsNumberComparator getInstance()
        {
            if(CategoryRecordsNumberComparator.instance == null)
            {
                CategoryRecordsNumberComparator.instance = new CategoryRecordsNumberComparator();
            }
            return CategoryRecordsNumberComparator.instance;
        }

        /** Creates a new instance of LongComparator */
        private CategoryRecordsNumberComparator() {
        }

        public int compare(CategoryRecord s1, CategoryRecord s2)
        {
            if(s2.records > s1.records)
                return 1;
            else if(s2.records < s1.records)
                return -1;
            return 0;
        }
    }
    public static class CategoryRecordsNameComparator implements java.util.Comparator<CategoryRecord>
    {
        private static CategoryRecordsNameComparator instance = null;

        public static CategoryRecordsNameComparator getInstance()
        {
            if(CategoryRecordsNameComparator.instance == null)
            {
                CategoryRecordsNameComparator.instance = new CategoryRecordsNameComparator();
            }
            return CategoryRecordsNameComparator.instance;
        }

        /** Creates a new instance of LongComparator */
        private CategoryRecordsNameComparator() {
        }

        public int compare(CategoryRecord s1, CategoryRecord s2)
        {
            if(s2.records > s1.records)
                return -1;
            else if(s2.records < s1.records)
                return 1;
            return 0;
        }
    }

    public static class RectangleFormLocal
    {
        RectangleForm rectangleForm;
        String locale;


        public RectangleFormLocal(RectangleForm rectangleForm, String locale)
        {
            this.rectangleForm = rectangleForm;
            this.locale = locale;
        }

        public RectangleForm getRectangleForm()
        {
            return rectangleForm;
        }

        public void setRectangleForm(RectangleForm rectangleForm)
        {
            this.rectangleForm = rectangleForm;
        }

        public String getLocale()
        {
            return locale;
        }

        public void setLocale(String locale)
        {
            this.locale = locale;
        }
    }


}
