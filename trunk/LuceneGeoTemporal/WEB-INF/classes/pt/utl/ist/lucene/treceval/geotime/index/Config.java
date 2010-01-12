package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.Model;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LanguageModelIndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Jan/2010
 * @time 17:54:44
 * @email machadofisher@gmail.com
 */
public class Config
{
    public static final String indexBase =  "D:\\Servidores\\DATA\\ntcir\\INDEXES";

    public static final String ID = Globals.DOCUMENT_ID_FIELD;
    public static final String CONTENTS = Globals.LUCENE_DEFAULT_FIELD;

    //FOUND Expressions
    public static final String S_TEMPORAL_INDEXED = "TEMPORAL_INDEXED";
    public static final String S_GEO_INDEXED = "GEO_INDEXED";
    public static final String S_GEO_AND_TEMPORAL_INDEXED = "GEO_AND_TEMPORAL_INDEXED";
    public static final String S_GEO_OR_TEMPORAL_INDEXED = "GEO_OR_TEMPORAL_INDEXED";

    //Geo Indexes
    public static String G_PLACE_REF_WOEID = "g_placeRefWoeid";
    public static String G_ADMIN_SCOPE_WOEID = "g_administrativeScopeWoeid";
    public static String G_GEO_SCOPE_WOEID = "g_geographicScopeWoeid";
    public static String G_GEO_ALL_WOEID = "g_allWoeid";

    //Time Indexes
    public static String T_ALL_EXPRESSIONS_AND_TIME_DOC = "t_all"; //point genpoint duration timeDocument
    public static String T_ALL_NOT_DURATION = "t_all_except_duration";  //all except duration point genpoint timeDocument
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


    public static String documentPath = "D:\\Servidores\\DATA\\ntcir\\data";
    public static String timexesPath = "D:\\Servidores\\DATA\\ntcir\\TIMEXTAG";
    public static String placemakerPath = "D:\\Servidores\\DATA\\ntcir\\PlaceMaker";

    public static LgteIndexSearcherWrapper openMultiSearcher() throws IOException
    {

        IndexReader readerGeoTime = new LanguageModelIndexReader(IndexReader.open(IndexGeoTime.indexPath));
        IndexReader readerContents = new LanguageModelIndexReader(IndexReader.open(IndexContents.indexPath));

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();

        readers.put(Config.CONTENTS,readerContents);
        readers.put(Config.ID,readerContents);

        readers.put(Config.S_GEO_AND_TEMPORAL_INDEXED,readerGeoTime);
        readers.put(Config.S_GEO_INDEXED,readerGeoTime);
        readers.put(Config.S_TEMPORAL_INDEXED,readerGeoTime);
        readers.put(Config.S_GEO_OR_TEMPORAL_INDEXED,readerGeoTime);

        return new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));
    }
}
