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

    public static final String TEMPORAL_INDEXED = "TEMPORAL_INDEXED";
    public static final String GEO_INDEXED = "GEO_INDEXED";
    public static final String GEO_AND_TEMPORAL_INDEXED = "GEO_AND_TEMPORAL_INDEXED";
    public static final String GEO_OR_TEMPORAL_INDEXED = "GEO_OR_TEMPORAL_INDEXED";


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

        readers.put(Config.GEO_AND_TEMPORAL_INDEXED,readerGeoTime);
        readers.put(Config.GEO_INDEXED,readerGeoTime);
        readers.put(Config.TEMPORAL_INDEXED,readerGeoTime);
        readers.put(Config.GEO_OR_TEMPORAL_INDEXED,readerGeoTime);

        return new LgteIndexSearcherWrapper(Model.OkapiBM25Model,new LgteIsolatedIndexReader(readers));
    }
}
