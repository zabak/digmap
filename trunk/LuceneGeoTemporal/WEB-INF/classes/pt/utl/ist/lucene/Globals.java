package pt.utl.ist.lucene;

import java.io.File;
import pt.utl.ist.lucene.config.ConfigProperties;
import pt.utl.ist.lucene.versioning.VersionEncoder;
import org.apache.lucene.search.highlight.Formatter;

/**
 * @author Jorge Machado
 * @date 14/Ago/2008
 * @see pt.utl.ist.lucene
 */
public class Globals {
	
	public static ConfigProperties ConfigProperties;
	
	private static Globals instance = new Globals();
	
	private Globals() {
  		    String aux = Globals.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("%20"," ");
  		    if(aux.endsWith("Globals.class")) aux = aux.substring(0,aux.lastIndexOf("/")) + "/../../../../../";
		    ConfigProperties = new ConfigProperties();
		    TMP_DIR = ConfigProperties.getProperty("tmp.dir");
		    DATA_DIR = ConfigProperties.getProperty("data.dir");
		    if(DATA_DIR == null || (!new File(DATA_DIR).exists() && !DATA_DIR.startsWith(File.separator))) {
		    	DATA_DIR = aux.substring(aux.indexOf("/"))+"test-index";
		    }
		    if(TMP_DIR == null || (!new File(TMP_DIR).exists() && !DATA_DIR.startsWith(File.separator))) {
		    	TMP_DIR = aux.substring(aux.indexOf("/"))+"test-index";
		    }
	}

    //This ID must be respected for use of LuceneLanguageModel
    public static final String DOCUMENT_ID_FIELD = "id";

    public Globals getInstance() { return instance; }
	
    public static String TMP_DIR;
    public static String DATA_DIR;
    
    public static final String LUCENE_DEFAULT_FIELD = ConfigProperties.getProperty("lucene.default.field");
    public static final String LGTE_USE_QE_BY_DEFAULT = ConfigProperties.getProperty("lgte.use.qe.by.default");

    public static final String LUCENE_ORDER_FIELD_QUERY = ConfigProperties.getProperty("lucene.order.field.query.language");
    public static final String LUCENE_FILTER_FIELD_QUERY = ConfigProperties.getProperty("lucene.filter.field.query.language");
    public static final String LUCENE_QE_FIELD_QUERY = ConfigProperties.getProperty("lucene.qe.field.query.language");
    public static final String LUCENE_MODEL_FIELD_QUERY = ConfigProperties.getProperty("lucene.model.field.query.language");

    public static final String LUCENE_LATITUDE_FIELD_QUERY = ConfigProperties.getProperty("lucene.latitude.field.query.language");
    public static final String LUCENE_LONGITUDE_FIELD_QUERY = ConfigProperties.getProperty("lucene.longitude.field.query.language");
    public static final String LUCENE_NORTHLIMIT_FIELD_QUERY = ConfigProperties.getProperty("lucene.northlimit.field.query.language");
    public static final String LUCENE_SOUTHLIMIT_FIELD_QUERY = ConfigProperties.getProperty("lucene.southlimit.field.query.language");
    public static final String LUCENE_EASTLIMIT_FIELD_QUERY = ConfigProperties.getProperty("lucene.eastlimit.field.query.language");
    public static final String LUCENE_WESTLIMIT_FIELD_QUERY = ConfigProperties.getProperty("lucene.westlimit.field.query.language");
    public static final String LUCENE_RADIUM_FIELD_QUERY = ConfigProperties.getProperty("lucene.radium.field.query.language");
    public static final String LUCENE_RADIUM_MILES_FIELD_QUERY = ConfigProperties.getProperty("lucene.radium.miles.field.query.language");
    public static final String LUCENE_RADIUM_KM_FIELD_QUERY = ConfigProperties.getProperty("lucene.radium.km.field.query.language");
    public static final String LUCENE_STARTTIME_FIELD_QUERY = ConfigProperties.getProperty("lucene.starttime.field.query.language");
    public static final String LUCENE_ENDTIME_FIELD_QUERY = ConfigProperties.getProperty("lucene.endtime.field.query.language");
    public static final String LUCENE_STARTTIME_MILISECONDS_FIELD_QUERY = ConfigProperties.getProperty("lucene.starttime.mili.field.query.language");
    public static final String LUCENE_ENDTIME_MILISECONDS_FIELD_QUERY = ConfigProperties.getProperty("lucene.endtime.mili.field.query.language");
    public static final String LUCENE_TIME_FIELD_QUERY = ConfigProperties.getProperty("lucene.time.field.query.language");
    public static final String LUCENE_TIME_MILISECONDS_FIELD_QUERY = ConfigProperties.getProperty("lucene.time.mili.field.query.language");

    public static final String LUCENE_RADIUMTIME_YEARS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.y.field.query.language");
    public static final String LUCENE_RADIUMTIME_MONTHS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.m.field.query.language");
    public static final String LUCENE_RADIUMTIME_DAYS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.d.field.query.language");
    public static final String LUCENE_RADIUMTIME_HOURS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.h.field.query.language");
    public static final String LUCENE_RADIUMTIME_MINUTES_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.min.field.query.language");
    public static final String LUCENE_RADIUMTIME_SECONDS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.s.field.query.language");
    public static final String LUCENE_RADIUMTIME_MILISECONDS_FIELD_QUERY = ConfigProperties.getProperty("lucene.radiumtime.ms.field.query.language");

    public static final String LUCENE_CENTROIDE_LATITUDE_INDEX = ConfigProperties.getProperty("lucene.centroide.latitude.index");
    public static final String LUCENE_CENTROIDE_LONGITUDE_INDEX = ConfigProperties.getProperty("lucene.centroide.longitude.index");
    public static final String LUCENE_NORTHLIMIT_INDEX = ConfigProperties.getProperty("lucene.north.limit.index");
    public static final String LUCENE_SOUTHLIMIT_INDEX = ConfigProperties.getProperty("lucene.south.limit.index");
    public static final String LUCENE_EASTLIMIT_INDEX = ConfigProperties.getProperty("lucene.east.limit.index");
    public static final String LUCENE_WESTLIMIT_INDEX = ConfigProperties.getProperty("lucene.west.limit.index");
    public static final String LUCENE_DIAGONAL_INDEX = ConfigProperties.getProperty("lucene.geo.diagonal.index");
    public static final String LUCENE_RADIUM_INDEX = ConfigProperties.getProperty("lucene.geo.radium.index");

    public static final String LUCENE_RADIUM_ORIGINAL_INDEX = LUCENE_RADIUM_INDEX + "_ORIGINAL";

    //When document has geo fields
    public static final String LUCENE_GEO_DOC_INDEX = "GEO_DOC_LE";
    public static final String LUCENE_GEO_DOC_YES = "YES";
    //When document has time fields
    public static final String LUCENE_TIME_DOC_INDEX = "TIME_DOC_LE";
    public static final String LUCENE_TIME_DOC_YES = "YES";

    public static final String LUCENE_GEO_DOC_QUERY = LUCENE_GEO_DOC_INDEX + ":" + LUCENE_GEO_DOC_YES;
    public static final String LUCENE_TIME_DOC_QUERY = LUCENE_TIME_DOC_INDEX + ":" + LUCENE_TIME_DOC_YES;

    public static final String LUCENE_CENTROIDE_LATITUDE_ORIGINAL_INDEX = LUCENE_CENTROIDE_LATITUDE_INDEX + "_ORIGINAL";
    public static final String LUCENE_CENTROIDE_LONGITUDE_ORIGINAL_INDEX = LUCENE_CENTROIDE_LONGITUDE_INDEX + "_ORIGINAL";
    public static final String LUCENE_NORTHLIMIT_ORGINAL_INDEX = LUCENE_NORTHLIMIT_INDEX + "_ORIGINAL";
    public static final String LUCENE_SOUTHLIMIT_ORGINAL_INDEX = LUCENE_SOUTHLIMIT_INDEX + "_ORIGINAL";
    public static final String LUCENE_EASTLIMIT_ORGINAL_INDEX = LUCENE_EASTLIMIT_INDEX + "_ORIGINAL";
    public static final String LUCENE_WESTLIMIT_ORGINAL_INDEX = LUCENE_WESTLIMIT_INDEX + "_ORIGINAL";
    public static final String LUCENE_DIAGONAL_ORIGINAL_INDEX = LUCENE_DIAGONAL_INDEX + "_ORIGINAL";

    public static final String LUCENE_TIMEWIDTH_INDEX = ConfigProperties.getProperty("lucene.time.width.index");
    public static final String LUCENE_TIME_INDEX = ConfigProperties.getProperty("lucene.time.index");
    public static final String LUCENE_START_TIME_LIMIT_INDEX = ConfigProperties.getProperty("lucene.starttime.limit.index");
    public static final String LUCENE_END_TIME_LIMIT_INDEX = ConfigProperties.getProperty("lucene.endtime.limit.index");
    
    public static final String LUCENE_TIME_ORIGINAL_INDEX = ConfigProperties.getProperty("lucene.time.index") + "_ORIGINAL";
    public static final String LUCENE_TIMEWIDTH_ORIGINAL_INDEX = LUCENE_TIMEWIDTH_INDEX + "_ORIGINAL";
    public static final String LUCENE_START_TIME_LIMIT_INDEX_ORIGINAL = LUCENE_START_TIME_LIMIT_INDEX + "_ORIGINAL";
    public static final String LUCENE_END_TIME_LIMIT_INDEX_ORIGINAL = LUCENE_END_TIME_LIMIT_INDEX + "_ORIGINAL";

    public static final long ONE_DAY_IN_MILISECONDS = 1000*60*60*24;
    public static final long HALF_DAY_IN_MILISECONDS = 1000*60*60*12;

    public static final int HIGHLIGHT_FRAGMENT_SIZE = ConfigProperties.getIntProperty("highlight.fragment.size");
    public static final int HIGHLIGHT_FRAGMENTS = ConfigProperties.getIntProperty("highlight.fragments");
    public static final String HIGHLIGHT_FRAGMENT_SEPARATOR = ConfigProperties.getProperty("highlight.fragment.separator");
    public static final Formatter HIGHLIGHT_FORMATTER = (Formatter) ConfigProperties.getPlugin("highlight.formatter");
    public static final VersionEncoder HIGHLIGHT_ENCODER = (VersionEncoder) ConfigProperties.getPlugin("highlight.encoder");
}
