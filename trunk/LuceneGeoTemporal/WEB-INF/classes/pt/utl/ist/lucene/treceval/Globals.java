package pt.utl.ist.lucene.treceval;

/**
 * @author Jorge Machado
 * @date 20/Ago/2008
 * @see pt.utl.ist.lucene.treceval
 */
public class Globals
{
    public static final String DOCUMENT_ID_FIELD = pt.utl.ist.lucene.Globals.DOCUMENT_ID_FIELD;
    public static final String DOCUMENT_FILE_PATH = "filepath";
    public static final String DOCUMENT_TITLE = "title";
    public static String DATA_DIR = pt.utl.ist.lucene.Globals.DATA_DIR;
    public static String INDEX_DIR = pt.utl.ist.lucene.Globals.INDEX_DIR;
    public static String GZipDefaultContentType = "sgml";
    public static String COLLECTION_FILES_DEFAULT_ENCODING = "ISO-8859-1";

    /*Parameter to be used by external LGTE classes that need the run output*/
    public static String RUN_OUTPUT_FILE = null;
}
