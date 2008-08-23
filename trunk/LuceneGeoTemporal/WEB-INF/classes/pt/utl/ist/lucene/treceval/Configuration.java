package pt.utl.ist.lucene.treceval;

import org.apache.lucene.analysis.Analyzer;
import pt.utl.ist.lucene.QueryConfiguration;
import pt.utl.ist.lucene.Model;

import java.util.HashSet;

/**
 * @author Jorge Machado
 * @date 8/Jun/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class Configuration
{

//    VS_LAT_EN_GH95("lat-en_gh-95","vs","vs",IndexConfiguration.analyzer_en,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexConfiguration.stopEn),
//    LM_LAT_EN_GH95("lat-en_gh-95","lm","lm",IndexConfiguration.analyzer_en,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexConfiguration.stopEn),
//    LM_STEM_LAT_EN_GH95("lat-en_gh-95","lmstem","lm",IndexConfiguration.analyzer_en_stemming,"c:/Servidores/DATA/geo/en",new LatEnGh95Preprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/en", new GeoClefTopicsProcessor("EN-","en"),"contents",IndexConfiguration.stopEn),
//
//    VS_FOLHA_PUBLICO("folha_publico","vs","vs",IndexConfiguration.analyzer_pt,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexConfiguration.stopPt),
//    LM_FOLHA_PUBLICO("folha_publico","lm","lm",IndexConfiguration.analyzer_pt,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexConfiguration.stopPt),
//    LM_STEM_FOLHA_PUBLICO("folha_publico","lmstem","lm",IndexConfiguration.analyzer_pt_stemming,"c:/Servidores/DATA/geo/pt",new FolhaPublicoPreprocessor(),"C:/Servidores/DATA/geo/topics_qrels_geo_2006/topics/pt", new GeoClefTopicsProcessor("PT-","pt"),"contents",IndexConfiguration.stopPt);

//    VS_STEMMER_BL("bl","vsstem","vs",IndexConfiguration.analyzer_en_stemming,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopEn),
//    VS_STEMMER_BNF("bnf","vsstem","vs",IndexConfiguration.analyzer_fr_stemming,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopFr),
//    VS_STEMMER_ONB("onb","vsstem","vs",IndexConfiguration.analyzer_de_stemming,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopDe),

//    VS_BL("bl","vs","vs",IndexConfiguration.analyzer_en,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopEn),
//    VS_BNF("bnf","vs","vs",IndexConfiguration.analyzer_fr,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopFr),
//    VS_ONB("onb","vs","vs",IndexConfiguration.analyzer_de,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopDe),
//
//    LM_STEMMER_BL("bl","lmstem","lm",IndexConfiguration.analyzer_en_stemming,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopEn),
//    LM_STEMMER_BNF("bnf","lmstem","lm",IndexConfiguration.analyzer_fr_stemming,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopFr),
//    LM_STEMMER_ONB("onb","lmstem","lm",IndexConfiguration.analyzer_de_stemming,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopDe),
//
//    LM_BL("bl","lm","lm",IndexConfiguration.analyzer_en,"c:/Servidores/DATA/tel/bl",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bl", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopEn),
//    LM_BNF("bnf","lm","lm",IndexConfiguration.analyzer_fr,"c:/Servidores/DATA/tel/bnf",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/bnf", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopFr),
//    LM_ONB("onb","lm","lm",IndexConfiguration.analyzer_de,"c:/Servidores/DATA/tel/onb",new ClefPreprocessor(),"c:/Servidores/DATA/tel/topics2/onb", new ClefTopicsProcessor(),"contentsFilteredTitle3Subject2",IndexConfiguration.stopDe);

   

    public String version;
    private String dir;
    private String collectionId;
    private String collectionPath;
    private org.apache.lucene.analysis.Analyzer analyzer;
    private Model model;
    private Preprocessor preprocessor;
    private String topicsPath;
    private ITopicsProcessor iTopicsProcessor;
    private String searchIndex;
    private HashSet stop;
    private QueryConfiguration queryConfiguration;
    private String outputDir;



    Configuration(String version, String collectionId,String dir, Model model, Analyzer analyzer, String collectionPath, Preprocessor preprocessor, String topicsPath, ITopicsProcessor iTopicsProcessor,String searchIndex, HashSet stop, QueryConfiguration queryConfiguration, String outputDir)
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
        this.searchIndex = searchIndex;
        this.stop = stop;
        this.queryConfiguration = queryConfiguration;
        this.outputDir = outputDir;
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

    public Preprocessor getPreprocessor()
    {
        return preprocessor;
    }


    public ITopicsProcessor getITopicsProcessor()
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

    public QueryConfiguration getQueryConfiguration()
    {
        return queryConfiguration;
    }


    public String getOutputDir()
    {
        return outputDir;
    }
}
