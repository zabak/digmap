package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.config.LocalProperties;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Jan/2010
 * @time 17:54:44
 * @email machadofisher@gmail.com
 */
public class Config
{

    static Properties props;
    static
    {
        try
        {
            props = new LocalProperties("pt/utl/ist/lucene/treceval/geotime/index/config.properties");
            indexBase = props.getProperty("indexBase");
            ntcirBase = props.getProperty("ntcirBase");
            documentPath = props.getProperty("documentPath");
            timexesPath = props.getProperty("timexesPath");
            placemakerPath = props.getProperty("placemakerPath");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String ntcirBase;
    public static String indexBase;
    public static String documentPath;
    public static String timexesPath;
    public static String placemakerPath;

    public static final String TITLE = "title";

    public static final String TEXT_DB = "TEXT_DB";
    public static final String TEMPORAL_DB = "TEMPORAL_DB";
    public static final String GEO_DB = "GEO_DB";


    public static final String ID = Globals.DOCUMENT_ID_FIELD;
    public static final String CONTENTS = Globals.LUCENE_DEFAULT_FIELD;


    //FOUND Expressions
    public static final String S_TEMPORAL_INDEXED = "S_TEMPORAL_INDEXED";
    public static final String S_GEO_INDEXED = "S_GEO_INDEXED";
    public static final String S_GEO_AND_TEMPORAL_INDEXED = "S_GEO_AND_TEMPORAL_INDEXED";
    public static final String S_GEO_OR_TEMPORAL_INDEXED =  "S_GEO_OR_TEMPORAL_INDEXED";
    public static final String S_HAS_TIMEXES = "S_HAS_TIMEXES";

    //Geo Indexes
    public static String G_PLACE_REF_WOEID = "g_placeRefWoeid";
    public static String G_ADMIN_SCOPE_WOEID = "g_administrativeScopeWoeid";
    public static String G_GEO_SCOPE_WOEID = "g_geographicScopeWoeid";
    public static String G_GEO_ALL_WOEID = "g_allWoeid";

    //Time Indexes
    public static String T_ALL_EXPRESSIONS_AND_TIME_DOC = "t_all"; //point genpoint duration timeDocument
    public static String T_ALL_NOT_DURATION = "t_all_except_duration";  //all except duration ( point genpoint timeDocument)
    public static String T_TIME_EXPRESSIONS = "t_exprs";  //all except TimeDocument
    public static String T_NOT_DURATION_EXPRESSIONS = "t_not_duration_exprs";  //all except time document and duration


    public static String T_POINT    = "t_point";
    public static String T_GENPOINT = "t_genpoint";
    public static String T_DURATION = "t_duration";
    public static String T_UNKNOWN  = "t_unknown";
    public static String T_TIME_DOCUMENT = "t_time_doc";

    public static String T_WEEK_NORM  = "t_week_norm";
    public static String T_DURATION_NORM  = "t_dur_norm";
    public static String T_DURATION_LEFT  = "t_dur_left";
    public static String T_DURATION_RIGHT = "t_dur_right";

    public static String T_IS_WEEK  =      "t_is_week";
    public static String T_LEFT_LIMIT  =   "t_is_dur_left";
    public static String T_INSIDE_LIMIT  = "t_is_dur_inside";
    public static String T_RIGHT_LIMIT  =  "t_is_dur_right";

    public static String T_Y    = "t_y";
    public static String T_YY    = "t_yy";
    public static String T_YYY    = "t_yyy";
    public static String T_YYYY    = "t_yyyy";
    public static String T_YYYYMM = "t_yyyymm";
    public static String T_YYYYMMDD = "t_yyyymmdd";

    //Metrics
    //All the Lgte internal GeoTemp Indexes to calculate distances are considered metric indexes
    public static String METRIC_T_CENTROIDE_1 = "metric_t_centroide_1";
    public static String METRIC_T_CENTROIDE_2 = "metric_t_centroide_2";
    public static String METRIC_T_CENTROIDE_3 = "metric_t_centroide_3";
    public static String METRIC_T_CENTROIDE_REFS = "metric_t_centroide_refs";





    public static LgteIndexSearcherWrapper openMultiSearcher() throws IOException
    {




        IndexReader readerContents = new LanguageModelIndexReader(IndexReader.open(IndexContents.indexPath));
        IndexReader readerGeoTime = new LanguageModelIndexReader(IndexReader.open(IndexGeoTime.indexPath));
        IndexReader readerTimeRefs = new LanguageModelIndexReader(IndexReader.open(CreateTimeExprIndex.indexPath));
        IndexReader readerGeoRefs = new LanguageModelIndexReader(IndexReader.open(CreateWoeidIndex.indexPath));
        IndexReader readerMetrics = new LanguageModelIndexReader(IndexReader.open(IndexMetrics.indexPath));
        IndexReader readerDB = new LanguageModelIndexReader(IndexReader.open(CreateDBGeoTimexes.indexPath));

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();

        readers.put(Config.CONTENTS,readerContents);
        readers.put(Config.TITLE,readerContents);
        readers.put(Config.ID,readerContents);

        readers.put("regexpr(^S_.*)",readerGeoTime);
        readers.put("regexpr(^t_.*)",readerTimeRefs);
        readers.put("regexpr(^g_.*)",readerGeoRefs);
        readers.put("regexpr(.*_DB$)",readerDB);
        readers.put("*",readerMetrics);

        return new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));
    }

    public static void main(String[] args)
    {
        System.out.println(Config.T_YYYYMMDD.matches("^S_.*"));
        System.out.println(Config.T_YYYYMMDD.matches("^t_.*"));
        System.out.println(Config.S_GEO_AND_TEMPORAL_INDEXED.matches("^S_.*"));
        System.out.println(Config.S_GEO_INDEXED.matches("^S_.*"));
        System.out.println(Config.S_TEMPORAL_INDEXED.matches("^S_.*"));
        System.out.println(Config.S_GEO_OR_TEMPORAL_INDEXED.matches("^S_.*"));
        System.out.println(Config.S_TEMPORAL_INDEXED.matches("^S_.*"));
        System.out.println(Config.S_HAS_TIMEXES.matches("^S_.*"));
        System.out.println(TEXT_DB.matches(".*_DB$"));
        System.out.println(GEO_DB.matches(".*_DB$"));
        System.out.println(TEMPORAL_DB.matches(".*_DB$"));
        System.out.println("teste_db".matches(".*_DB$"));
    }
}
