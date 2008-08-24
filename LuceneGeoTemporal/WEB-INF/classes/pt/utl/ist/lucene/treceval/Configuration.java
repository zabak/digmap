package pt.utl.ist.lucene.treceval;

import org.apache.lucene.analysis.Analyzer;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.treceval.handlers.topics.ITopicsPreprocessor;
import pt.utl.ist.lucene.treceval.handlers.collections.ICollectionPreprocessor;

import java.util.HashSet;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class Configuration
{

//    VS_LAT_EN_GH95("lat-en_gh-95","vs","vs",IndexCollections.analyzer_en,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexCollections.stopEn),
//    LM_LAT_EN_GH95("lat-en_gh-95","lm","lm",IndexCollections.analyzer_en,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexCollections.stopEn),
//    LM_STEM_LAT_EN_GH95("lat-en_gh-95","lmstem","lm",IndexCollections.analyzer_en_stemming,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexCollections.stopEn),
//
//    VS_FOLHA_PUBLICO("folha_publico","vs","vs",IndexCollections.analyzer_pt,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexCollections.stopPt),
//    LM_FOLHA_PUBLICO("folha_publico","lm","lm",IndexCollections.analyzer_pt,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexCollections.stopPt),
//    LM_STEM_FOLHA_PUBLICO("folha_publico","lmstem","lm",IndexCollections.analyzer_pt_stemming,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexCollections.stopPt);

//    VS_STEMMER_BL("bl","vsstem","vs",IndexCollections.analyzer_en_stemming,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopEn),
//    VS_STEMMER_BNF("bnf","vsstem","vs",IndexCollections.analyzer_fr_stemming,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopFr),
//    VS_STEMMER_ONB("onb","vsstem","vs",IndexCollections.analyzer_de_stemming,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopDe),

//    VS_BL("bl","vs","vs",IndexCollections.analyzer_en,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopEn),
//    VS_BNF("bnf","vs","vs",IndexCollections.analyzer_fr,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopFr),
//    VS_ONB("onb","vs","vs",IndexCollections.analyzer_de,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopDe),
//
//    LM_STEMMER_BL("bl","lmstem","lm",IndexCollections.analyzer_en_stemming,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopEn),
//    LM_STEMMER_BNF("bnf","lmstem","lm",IndexCollections.analyzer_fr_stemming,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopFr),
//    LM_STEMMER_ONB("onb","lmstem","lm",IndexCollections.analyzer_de_stemming,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopDe),
//
//    LM_BL("bl","lm","lm",IndexCollections.analyzer_en,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopEn),
//    LM_BNF("bnf","lm","lm",IndexCollections.analyzer_fr,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopFr),
//    LM_ONB("onb","lm","lm",IndexCollections.analyzer_de,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexCollections.stopDe);

   

    public String version;
    private String dir;
    private String collectionId;
    private String collectionPath;
    private org.apache.lucene.analysis.Analyzer analyzer;
    private Model model;
    private ICollectionPreprocessor preprocessor;
    private String topicsPath;
    private ITopicsPreprocessor iTopicsProcessor;
    private String searchIndex;
    private HashSet stop;
    private String outputDir;
    private int maxResultsPerTopic;



    Configuration(String version, String collectionId,String dir, Model model, Analyzer analyzer, String collectionPath, ICollectionPreprocessor preprocessor, String topicsPath, ITopicsPreprocessor iTopicsProcessor,String defaultSearchField, HashSet stop, String outputDir, int maxResultsPerTopic)
    {
        this.version = version;
        this.collectionId = collectionId;
        this.dir = dir;
        this.model = model;
        this.analyzer = analyzer;
        this.collectionPath = collectionPath;
        this.preprocessor = preprocessor;
        this.topicsPath = topicsPath;
        this.iTopicsProcessor = iTopicsProcessor;
        this.searchIndex = defaultSearchField;
        this.stop = stop;
        this.outputDir = outputDir;
        this.maxResultsPerTopic = maxResultsPerTopic;
    }

    public String getIndexPath()
    {
        return  Globals.DATA_DIR + "/" + dir + "/" + version + "/" + collectionId;
    }


    public String getDir()
    {
        return dir;
    }

    public String getCollectionPath()
    {
        return collectionPath;
    }

    public Analyzer getAnalyzer()
    {
        return analyzer;
    }

    public Model getModel()
    {
        return model;
    }


    public String getCollectionId()
    {
        return collectionId;
    }

    public ICollectionPreprocessor getPreprocessor()
    {
        return preprocessor;
    }


    public ITopicsPreprocessor getITopicsProcessor()
    {
        return iTopicsProcessor;
    }

    public String getTopicsPath()
    {
        return topicsPath;
    }


    public String getSearchIndex()
    {
        return searchIndex;
    }


    public HashSet getStop()
    {
        return stop;
    }



    public String getOutputDir()
    {
        return outputDir;
    }


    public int getMaxResultsPerTopic()
    {
        return maxResultsPerTopic;
    }
}
