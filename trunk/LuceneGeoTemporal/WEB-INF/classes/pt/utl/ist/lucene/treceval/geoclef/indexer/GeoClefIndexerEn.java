package pt.utl.ist.lucene.treceval.geoclef.indexer;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.dom4j.*;
import org.xml.sax.SAXException;
import pt.utl.ist.lucene.treceval.*;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResultsIterator;
import pt.utl.ist.lucene.treceval.geoclef.reader.GeoParserResult;
import pt.utl.ist.lucene.treceval.handlers.*;
import pt.utl.ist.lucene.treceval.handlers.topics.output.impl.TrecEvalOutputFormatFactory;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.topics.TDirectory;
import pt.utl.ist.lucene.treceval.handlers.collections.CDirectory;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.QEEnum;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.forms.RectangleForm;
import pt.utl.ist.lucene.forms.UnknownForm;
import pt.utl.ist.lucene.forms.GeoPoint;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.vividsolutions.jts.io.gml2.GMLReader;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Jorge Machado
 * @date 5/Nov/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class GeoClefIndexerEn
{

    private static final Logger logger = Logger.getLogger(GeoClefExample.class);

    /**
     * Number of Files to skip if already done
     */


    static int maxResults = 1000;
    static GeoParserResultsIterator geoParserResultsIterator = null;

    public static void main(String[] args) throws DocumentException, IOException
    {

        args = new String[2];
        args[0] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-index";
        args[1] = "C:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data";

        Globals.INDEX_DIR = args[0];
        Globals.DATA_DIR = args[1];


        //topics and output wil be the same for english collections
        String topics08PathEn = Globals.DATA_DIR + "\\geoclef08en\\topics";
        String output08DirEn = Globals.DATA_DIR + "\\geoclef08en\\output";


        //Collection CDirectory iterator will use DATA dir to substring files path
        //so in this special case we will set data dir with special collection path dir and
        //then we will put the old value again before call Topics Runners
        String dataDir = Globals.DATA_DIR;

        //we will use a specific location for collection dir, and not data dir like in other examples.
        String gh95 = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\gh-95";
        String latEn = pt.utl.ist.lucene.treceval.geoclef.Globals.collectionPathEn + "\\lat-en";


        logger.info("Writing indexes to:" + Globals.INDEX_DIR);
        logger.info("Reading data from:" + gh95);
        logger.info("and Reading data from:" + latEn);

        //Global Search Index

        /**
         * GH 95 Collection Doc FIELDS
         */
        List<XmlFieldHandler> xmlFieldHandlersGh95 = new ArrayList<XmlFieldHandler>();
        XmlFieldHandler gh95XmlHandler = new SimpleXmlFieldHandler(".", new GenericFieldFilter(new Gh95TextFieldsReader()),"contents");
        xmlFieldHandlersGh95.add(gh95XmlHandler);
        ResourceHandler resourceHandlerGh95 = new XmlResourceHandler("//DOC", "DOCNO", xmlFieldHandlersGh95);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryGh95 = new CDirectory(resourceHandlerGh95, null);


        /**
         * LAT EN Collection Doc FIELDS
         */
        List<XmlFieldHandler> xmlFieldHandlersLatEn = new ArrayList<XmlFieldHandler>();
        XmlFieldHandler latEnXmlHandler = new SimpleXmlFieldHandler(".", new GenericFieldFilter(new LatEnTextFieldsReader()),"contents");
        xmlFieldHandlersLatEn.add(latEnXmlHandler);
        ResourceHandler resourceHandlerLatEn = new XmlResourceHandler("//DOC", "DOCNO", xmlFieldHandlersLatEn);
        //we could set to topicsDirectory preprocessor a Properties object with FileExtensions Implementations of CDocumentHandler
        CDirectory collectionsDirectoryLatEn = new CDirectory(resourceHandlerLatEn, null);

        /**
         * EN TOPICS
         * <topic lang="en">
         <identifier>10.2452/100-GC</identifier>
         <title>Natural disasters in the Western USA</title>
         <description>Douments need to describe natural disasters in the Western USA</description>
         <narrative>Relevant documents report on natural disasters like earthquakes or
         flooding which took place in Western states of the United States. To the Western states belong California, Washington and Oregon.</narrative>
         </topic>
         ....
         ....
         ....
         <Folder>
         <name>GEO_CLEF2008_PT</name>
         <open>1</open>
         <Placemark>
         <name>10.2452/100-GC</name>
         <styleUrl>#msn_ylw-pushpin4</styleUrl>
         <Polygon>
         <tessellate>1</tessellate>
         <outerBoundaryIs>
         <LinearRing>
         <coordinates>
         -124.498358122888,48.49603245415199,0 -124.5200563442772,40.45084338706569,0 -123.7647141075587,38.74297829560835,0 -120.384613013749,34.43506099289216,0 -117.0812613361983,32.49464394321812,0 -114.5320789875417,32.78102951503264,0 -114.581922096389,35.00220307292381,0 -120.019926726697,38.97223517088855,0 -119.9531998861885,41.98891288245463,0 -116.8977822526316,41.90123522215136,0 -116.8927350005536,44.13990621280611,0 -117.1406641320271,44.28484372301948,0 -116.454233154681,45.61098788882967,0 -116.6873899985583,45.76042303867632,0 -116.8608635868606,45.81735407688642,0 -116.9524586951087,46.19855779194396,0 -116.9975325174537,48.99651222436788,0 -123.2253443267105,48.93184033829019,0 -123.155470485594,48.31707212522704,0 -124.498358122888,48.49603245415199,0 </coordinates>
         </LinearRing>
         </outerBoundaryIs>
         </Polygon>
         </Placemark>
         ....
         */
        XmlFieldHandler xmlTitleTopicFieldHandler = new SimpleXmlFieldHandler("./title",new MultipleFieldFilter(2),"contents");
        XmlFieldHandler xmlDescriptionTopicFieldHandler = new SimpleXmlFieldHandler("./description",new SimpleFieldFilter(),"contents");
        XmlFieldHandler xmlGeoTopicFieldHandler = new SimpleXmlFieldHandler(".",new GeoTopicFieldFilter(),"contents");
        List<XmlFieldHandler> xmlTopicFieldHandlers = new ArrayList<XmlFieldHandler>();
        xmlTopicFieldHandlers.add(xmlDescriptionTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlTitleTopicFieldHandler);
        xmlTopicFieldHandlers.add(xmlGeoTopicFieldHandler);
        ResourceHandler topicResourceHandler = new XmlResourceHandler("//topic","./identifier",xmlTopicFieldHandlers);
        TrecEvalOutputFormatFactory factory =  new TrecEvalOutputFormatFactory(Globals.DOCUMENT_ID_FIELD);
        ITopicsPreprocessor topicsDirectory = new TDirectory(topicResourceHandler,factory);


        //Create Index Configurations for LM ( and will be used both to LM and Vector Space Model)
        Configuration LM_GEO_GH_95 = new Configuration("version1", "geoclef_en","lm", Model.LanguageModel, IndexCollections.en.getAnalyzerNoStemming(),gh95,collectionsDirectoryGh95,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);
        Configuration LM_GEO_LAT_EN = new Configuration("version1", "geoclef_en","lm", Model.LanguageModel, IndexCollections.en.getAnalyzerNoStemming(),latEn,collectionsDirectoryLatEn,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);
        LM_GEO_LAT_EN.setCreateIndex(false);       //will use index of first configuration
        //Create Index Configurations LM with stemming (will work both to LM and Vector Space Model)
        Configuration LM_STEMMER_GEO_GH_95 = new Configuration("version1", "geoclef_en","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),gh95,collectionsDirectoryGh95,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);
        Configuration LM_STEMMER_GEO_LAT_EN = new Configuration("version1", "geoclef_en","lmstem", Model.LanguageModel, IndexCollections.en.getAnalyzerWithStemming(),latEn,collectionsDirectoryLatEn,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);
        LM_STEMMER_GEO_LAT_EN.setCreateIndex(false); //will use index of first configuration

        //Index GH95 without stemming
        List<Configuration> configurations = new ArrayList<Configuration>();
        configurations.add(LM_GEO_GH_95);
        geoParserResultsIterator = new GeoParserResultsIterator(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\gh95");
        Globals.COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";
        Globals.DATA_DIR = gh95;
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        //Index in same index folder LatEn without stemming
        configurations = new ArrayList<Configuration>();
        configurations.add(LM_GEO_LAT_EN);
        geoParserResultsIterator = new GeoParserResultsIterator(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\latEn");
        Globals.COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";
        Globals.GZipDefaultContentType = "sgml";
        Globals.DATA_DIR = latEn;
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        //Index GH95 with stemming
        configurations = new ArrayList<Configuration>();
        configurations.add(LM_STEMMER_GEO_GH_95);
        geoParserResultsIterator = new GeoParserResultsIterator(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\gh95");
        Globals.COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";
        Globals.DATA_DIR = gh95;
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);

        //Index in same index folder LatEn with stemming
        configurations = new ArrayList<Configuration>();
        configurations.add(LM_STEMMER_GEO_LAT_EN);
        geoParserResultsIterator = new GeoParserResultsIterator(pt.utl.ist.lucene.treceval.geoclef.Globals.outputGeoParseDir + "\\latEn");
        Globals.COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";
        Globals.GZipDefaultContentType = "sgml";
        Globals.DATA_DIR = latEn;
        IndexCollections.indexConfiguration(configurations,Globals.DOCUMENT_ID_FIELD);









        Globals.DATA_DIR = dataDir;
        //Configurations to Perform Topic Search
        Configuration LM_GEO_EN_UNION = LM_GEO_GH_95;
        Configuration LM_STEMMER_GEO_EN_UNION = LM_STEMMER_GEO_GH_95;
        Configuration VS_GEO_EN_UNION = new Configuration("version1", "geoclef_en","lm",Model.VectorSpaceModel , IndexCollections.en.getAnalyzerNoStemming(),null,null,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);
        Configuration VS_STEMMER_GEO_EN_UNION = new Configuration("version1", "geoclef_en","lmstem", Model.VectorSpaceModel, IndexCollections.en.getAnalyzerWithStemming(),null,null,topics08PathEn, topicsDirectory,"contents", IndexCollections.en.getWordList(),output08DirEn,maxResults);



        QueryConfiguration queryConfiguration1 = new QueryConfiguration();
        queryConfiguration1.setForceQE(QEEnum.no);
        queryConfiguration1.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration1.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration1.getQueryProperties().put("lgte.default.order","sc");

        QueryConfiguration queryConfiguration2 = new QueryConfiguration();
        queryConfiguration2.setForceQE(QEEnum.text);
        queryConfiguration2.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration2.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration2.getQueryProperties().put("lgte.default.order","sc");

        QueryConfiguration queryConfiguration3 = new QueryConfiguration();
        queryConfiguration3.setForceQE(QEEnum.lgte);
        queryConfiguration3.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration3.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration3.getQueryProperties().put("lgte.default.order","sc");




        QueryConfiguration queryConfiguration4 = new QueryConfiguration();
        queryConfiguration4.setForceQE(QEEnum.no);
        queryConfiguration4.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration4.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration4.getQueryProperties().put("lgte.default.order","sc_sp");


        QueryConfiguration queryConfiguration5 = new QueryConfiguration();
        queryConfiguration5.setForceQE(QEEnum.text);
        queryConfiguration5.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration5.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration5.getQueryProperties().put("lgte.default.order","sc_sp");

        QueryConfiguration queryConfiguration6 = new QueryConfiguration();
        queryConfiguration6.setForceQE(QEEnum.lgte);
        queryConfiguration6.getQueryProperties().put("lgte.default.filter","no");
        queryConfiguration6.getQueryProperties().put("spatial.score.strategy","pt.utl.ist.lucene.sort.sorters.priors.comparators.strategy.BoxQueryWithBoxDoc");
        queryConfiguration6.getQueryProperties().put("lgte.default.order","sc_sp");



        List<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();

        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration1, LM_STEMMER_GEO_EN_UNION));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration2, LM_STEMMER_GEO_EN_UNION));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration3, LM_STEMMER_GEO_EN_UNION));



        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration4, LM_STEMMER_GEO_EN_UNION));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration5, LM_STEMMER_GEO_EN_UNION));

        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, VS_STEMMER_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM_GEO_EN_UNION));
        searchConfigurations.add(new SearchConfiguration(queryConfiguration6, LM_STEMMER_GEO_EN_UNION));

//
        //Search Topics Runs to submission
        SearchTopics.search(searchConfigurations);

    }



    static GeometryFactory fact = new GeometryFactory();

    static class GeoTopicFieldFilter implements FieldFilter
    {
        public FilteredFields filter(Node element, String fieldName)
        {

            //todo testar isto ver se le mesmo do Ficheiro
            Element identifierElem = (Element) element.selectSingleNode("identifier");
            String identifier = identifierElem.getText();
            XPath xPath = element.getDocument().createXPath("//Placemark/[name='" + identifier.trim() + "']/Polygon");
            Element polygon = (Element) xPath.selectSingleNode(element.getDocument());
            StringWriter sw = new StringWriter();
            try
            {
                Dom4jUtil.write(polygon,sw);
                GMLReader reader = new GMLReader();
                Geometry geo = reader.read(sw.toString().replace("xmlns:gml=\"http://www.opengis.net/gml\"",""), fact);
                Envelope envelope = geo.getEnvelopeInternal();
                UnknownForm unknownForm = new RectangleForm(envelope.getMaxX(),envelope.getMinY(),envelope.getMinX(),envelope.getMaxY(),new GeoPoint(geo.getCentroid().getX(), geo.getCentroid().getY()));
                //radium will be passed through and LGTE will use it as Diagonal
                double radium = unknownForm.getWidth() / ((double)2);
                Map<String,String> fields = new HashMap<String,String>();
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_LATITUDE_FIELD_QUERY,""+geo.getCentroid().getX());
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_LONGITUDE_FIELD_QUERY,""+geo.getCentroid().getY());
                fields.put(pt.utl.ist.lucene.Globals.LUCENE_RADIUM_FIELD_QUERY,""+radium);

                return new FilteredFields(fields);
            }
            catch (IOException e)
            {
                logger.error(e,e);
            }
            catch (ParserConfigurationException e)
            {
                logger.error(e,e);
            }
            catch (SAXException e)
            {
                logger.error(e,e);
            }
            return null;
        }
    }



    static class GenericFieldFilter implements FieldFilter
    {


        TextFieldsReader textFieldsReader;

        public GenericFieldFilter(TextFieldsReader textFieldsReader)
        {
            this.textFieldsReader = textFieldsReader;
        }

        public FilteredFields filter(Node element, String fieldName)
        {

            Element docElem = (Element) element;
            List<Field> fieldsGeo = null;
            Map<String, String> fieldsText = new HashMap<String, String>();

            Element docnoElem = (Element) docElem.selectSingleNode("DOCNO");
            if (docnoElem == null)
            {
                logger.error("Record with no DOCNO");
                logger.warn("trying docid");
                docnoElem = (Element) docElem.selectSingleNode("DOCID");
            }
            if (docnoElem == null)
                logger.error("Record with no DOCID");
            else
            {
                String docno = docnoElem.getText();
                fieldsText = textFieldsReader.getText(docElem,docno,fieldName);
                //AppendingPlaces
                GeoParserResult geoParserResult = geoParserResultsIterator.next(docno);
                if(geoParserResult == null)
                    logger.error("No GEO PARSE RESULT to docno: " + docno);
                else
                {
                    //GeoParserIterator allays return a GeoResult in an UnkownForm if is a point diagonal comes zero
                    if(geoParserResult.getGenericUnknownForm() != null)
                    {
                        if(geoParserResult.getGenericUnknownForm() instanceof RectangleForm)
                        {
                            fieldsGeo = LgteDocumentWrapper.getGeoBoxFields((RectangleForm) geoParserResult.getGenericUnknownForm());
                        }
                        else
                        {
                            fieldsGeo = LgteDocumentWrapper.getGeoPointFields(geoParserResult.getGenericUnknownForm().getCentroide());
                        }
                    }
                }
                StringBuilder strBuilder = new StringBuilder(fieldsText.get(fieldName));
                if(geoParserResult != null)
                {
                    for(String place : geoParserResult.getPlaces())
                    {
                        strBuilder
                                .append(" ").append(place)
                                .append(" ").append(place);
                    }
                }
                fieldsText.put(fieldName,strBuilder.toString());
            }

            if(fieldsGeo == null)
                return new FilteredFields(fieldsText);
            else
                return new FilteredFields(fieldsText,fieldsGeo);
        }
    }

    public static interface TextFieldsReader
    {
        public Map<String,String> getText(Element docElem, String docno, String fieldName);
    }

    /**
     * gh-95 example
     * <DOC>
     * <DOCNO>GH950103-000000</DOCNO>
     * <DOCID>GH950103-000000</DOCID>
     * <DATE>950103</DATE>
     * <HEADLINE>Chance of being a victim of crime is less than you think</HEADLINE>
     * <EDITION>3</EDITION>
     * <PAGE>3</PAGE>
     * <RECORDNO>980549733</RECORDNO>
     * <TEXT>
     * PEOPLE greatl...
     * </TEXT>
     * </DOC>
     */
    public static class Gh95TextFieldsReader implements TextFieldsReader
    {
        public Map<String,String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
            Element headlineElem = (Element) docElem.selectSingleNode("HEADLINE");
            String headline = "";
            if (headlineElem != null)
                headline = headlineElem.getText();
            else
                logger.warn("DOC " + docno + " with no headline");
            Element textElem = (Element) docElem.selectSingleNode("TEXT");
            String text = "";
            if (textElem != null)
                text = textElem.getText();
            else
                logger.warn("DOC " + docno + " with no text");
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(headline).append(" ")
                    .append(headline).append(" ")
                    .append(text);
            fields.put(fieldName,strBuilder.toString());
            return fields;
        }
    }
    /**
     *  latin-en example
     <DOC>
     <DOCNO> LA012394-0086 </DOCNO>
     <DOCID> 006365 </DOCID>
     <SOURCE><P>Los Angeles Times</P></SOURCE>
     <DATE><P>January 23, 1994, Sunday, Ventura West Edition</P></DATE>
     <SECTION><P>Metro; Part B; Page 1; Column 2</P></SECTION>
     <LENGTH><P>4337 words</P></LENGTH>
     <HEADLINE><P>
     EARTHQUAKE: THE LONG ROAD BACK; DIARY OF A DISASTER; RESIDENTS STRUGGLING TO
     PUT LIVES BACK IN ORDER ONE DAY AT A TIME</P>
     </HEADLINE>
     <BYLINE><P>By STEPHANIE SIMON, TIMES STAFF WRITER</P></BYLINE>
     <TEXT>
     <P>DAY 1</P>
     <P>Darkness. Then, abruptly, a jolt. A crash. Rumbling, screaming, shattering,tumbling. Panic. And again, darkness.</P>
     ...
     </TEXT>
     </DOC>
     */

    public static class LatEnTextFieldsReader implements TextFieldsReader
    {
        public Map<String,String> getText(Element docElem, String docno, String fieldName)
        {
            Map<String, String> fields = new HashMap<String, String>();
            XPath xPathHeadline = docElem.createXPath("./HEADLINE//text()");
            XPath xPathText = docElem.createXPath("./TEXT//text()");
            List<Node> headlineElems = xPathHeadline.selectNodes(docElem);
            String headline = "";
            if (headlineElems != null)
            {
                StringBuilder headlinesBuilder = new StringBuilder();
                for(Node n: headlineElems)
                {
                    headlinesBuilder.append(" ").append(n.getText());
                }
                headline = headlinesBuilder.toString();
            }
            else
                logger.warn("DOC " + docno + " with no headline");
            List<Node> textElems = xPathText.selectNodes(docElem);
            String text = "";
            if (textElems != null)
            {
                StringBuilder textBuilder = new StringBuilder();
                for(Node n: textElems)
                {
                    textBuilder.append(" ").append(n.getText());
                }
                text = textBuilder.toString();
            }
            else
                logger.warn("DOC " + docno + " with no text");
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(headline).append(" ").append(headline).append(" ").append(text);
            return fields;
        }
    }
}