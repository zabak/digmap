package pt.utl.ist.lucene.treceval.geoclef2.index;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LgteIsolatedIndexReader;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexManager;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.config.LocalProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jorge Machado
 * @date 10/Jan/2010
 * @time 17:54:44
 * @email machadofisher@gmail.com
 */
public class Config {

    static Properties props;
    static
    {
        try
        {
            props = new LocalProperties("pt/utl/ist/lucene/treceval/geoclef2/index/config.properties");
            indexBase = props.getProperty("indexBase");
            geoclefBase = props.getProperty("geoclefBase");
            documentPath = props.getProperty("documentPath");
            placemakerPath = props.getProperty("placemakerPath");
            outputDocs = Integer.parseInt(props.getProperty("outputDocs"));
            combSentencesFactor = props.getProperty("comb.sentences.factor");
            combContentsFactor = props.getProperty("comb.contents.factor");

            belongTosFactor = props.getProperty("belongTosFactor");
            placeRefFactor = props.getProperty("placeRefFactor");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String geoclefBase;
    public static String indexBase;
    public static String documentPath;
    public static String placemakerPath;
    public static int outputDocs;
    public static String combSentencesFactor;
    public static String combContentsFactor;

    public static String belongTosFactor;
    public static String placeRefFactor;


    static void init()
    {

    }
    public static final String TITLE = pt.utl.ist.lucene.treceval.Globals.DOCUMENT_TITLE;

    public static final String TEXT_DB = "TEXT_DB";
    public static final String GEO_DB = "GEO_DB";

    public static final String ID = Globals.DOCUMENT_ID_FIELD;
    public static final String CONTENTS = Globals.LUCENE_DEFAULT_FIELD;
    public static final String DOC_ID = "doc_id";
    public static final String SENTENCES = "sentences";
    public static final String SEP = "_";

    //FOUND Expressions
    public static final String S_GEO_INDEXED = "S_GEO_INDEXED";

    //Geo Indexes


    public static String G_PLACE_REF_WOEID = "g_placeRefWoeid";
    public static String G_PLACE_BELONG_TOS_WOEID = "g_placeBelongTosWoeid";
    public static String G_GEO_ALL_WOEID = "g_allWoeid";

    public static String G_GEO_PLACE_TYPE = "g_place_type";





    public static LgteIndexSearcherWrapper openMultiSearcher() throws IOException
    {
        IndexReader readerContents = LgteIndexManager.openReader(pt.utl.ist.lucene.treceval.geoclef2.index.IndexContents.indexPath, Model.LanguageModel);
        IndexReader readerGeoScope = LgteIndexManager.openReader(IndexGeoScope.indexPath, Model.LanguageModel);
        IndexReader readerWoeid = LgteIndexManager.openReader(IndexWoeid.indexPath, Model.LanguageModel);

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();
        readers.put(CONTENTS,readerContents);
        readers.put(ID,readerContents);
        readers.put("regexpr(^S_.*)",readerGeoScope);
        readers.put("regexpr(^g_.*)",readerWoeid);
        return new LgteIndexSearcherWrapper(Model.LanguageModel,new LgteIsolatedIndexReader(readers));
    }


    public static LgteIndexSearcherWrapper openMultiSearcherForContentsAndParagraphs() throws IOException
    {
        IndexReader readerContents = LgteIndexManager.openReader(IndexContents.indexPath, Model.LanguageModel);
        IndexReader readerGeoScope = LgteIndexManager.openReader(IndexGeoScope.indexPath, Model.LanguageModel);
        IndexReader readerWoeid = LgteIndexManager.openReader(IndexWoeid.indexPath, Model.LanguageModel);
        IndexReader readerParagraphs = LgteIndexManager.openReader(IndexParagraphs.indexPath, Model.LanguageModel);
        IndexReader readerGeoScopeParagraphs = LgteIndexManager.openReader(IndexGeoScopeParagraphs.indexPath, Model.LanguageModel);
        IndexReader readerWoeidParagraphs = LgteIndexManager.openReader(IndexWoeidParagraphs.indexPath, Model.LanguageModel);

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();


        readers.put(ID,readerParagraphs);
        readers.put(DOC_ID,readerParagraphs);
        readers.put(CONTENTS,readerContents);
        readers.put(SENTENCES,readerParagraphs);

        readers.put("regexpr(^S_.*)",readerGeoScope);
        readers.put("regexpr(^g_.*)",readerWoeid);
        readers.put("regexpr(^S_.*_sentences)",readerGeoScopeParagraphs);
        readers.put("regexpr(^g_.*_sentences)",readerWoeidParagraphs);
        LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
        lgteIsolatedIndexReader.addTreeMapping(readerContents,readerParagraphs, DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerWoeid,readerWoeidParagraphs,readerContents, DOC_ID);
        lgteIsolatedIndexReader.addTreeMapping(readerGeoScope,readerGeoScopeParagraphs,readerContents, DOC_ID);

        return new LgteIndexSearcherWrapper(Model.LanguageModel,lgteIsolatedIndexReader);
    }

     public static LgteIndexSearcherWrapper openMultiSearcherParagraphs() throws IOException
    {
        IndexReader readerParagraphs = LgteIndexManager.openReader(IndexParagraphs.indexPath, Model.LanguageModel);
        IndexReader readerGeoScopeParagraphs = LgteIndexManager.openReader(IndexGeoScopeParagraphs.indexPath, Model.LanguageModel);
        IndexReader readerWoeidParagraphs = LgteIndexManager.openReader(IndexWoeidParagraphs.indexPath, Model.LanguageModel);

        Map<String,IndexReader> readers = new HashMap<String,IndexReader>();

        readers.put(ID,readerParagraphs);
        readers.put(DOC_ID,readerParagraphs);
        readers.put(SENTENCES,readerParagraphs);

        readers.put("regexpr(^S_.*_sentences)",readerGeoScopeParagraphs);
        readers.put("regexpr(^g_.*_sentences)",readerWoeidParagraphs);
        LgteIsolatedIndexReader lgteIsolatedIndexReader = new LgteIsolatedIndexReader(readers);
        return new LgteIndexSearcherWrapper(Model.LanguageModel,lgteIsolatedIndexReader);
    }

    public static void main(String[] args)
    {

    }
}
