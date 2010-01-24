package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;
import pt.utl.ist.lucene.utils.DocumentPlaceMakerAndTemporalSentences;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoTimeSentences
{
    public static String indexPath =  IndexGeoTime.indexPath + "_sentences";

    public static void main(String[] args) throws IOException {
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(Config.documentPath,Config.timexesPath,Config.placemakerPath);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(),true, Model.BM25b);
        IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d;
        while((d = iterator.next())!=null)
        {
            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {
                System.out.println("docs:" + i + " sentence: " + p + " :" + d.getD().getDId());
            }
        }
        writer.close();
        System.out.println("docs:" + i + " stmts: " + p);
    }

    static int i = 0;
    static int p = 0;

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes document) throws IOException
    {
        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(document.getD(),document.getTd(),document.getPm());
        for(PlaceMakerAndTemporalSentence sentence: documentPlaceMakerAndTemporalSentences.getSentences())
        {
            LgteDocumentWrapper doc = new LgteDocumentWrapper();
            doc.indexString(Config.ID,document.getD().getDId() + "$$" + sentence.getIndex());
            doc.indexString(Config.DOC_ID,document.getD().getDId());
            p++;
            if(sentence.hasTimexes())
                doc.indexStringNoStore(Config.S_HAS_TIMEXES + "_" + Config.SENTENCES,"true");

            if(sentence.hasIndexableTimeExpressions() && sentence.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_AND_TEMPORAL_INDEXED + "_" + Config.SENTENCES,"true");

            if(sentence.hasIndexableTimeExpressions() || sentence.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_OR_TEMPORAL_INDEXED + "_" + Config.SENTENCES,"true");

            if(sentence.hasIndexableTimeExpressions())
                doc.indexStringNoStore(Config.S_TEMPORAL_INDEXED +  "_" + Config.SENTENCES,"true");

            if(sentence.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_INDEXED +  "_" + Config.SENTENCES, "true");

            if(sentence.hasPointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_ANY_TIME_POINT +  "_" + Config.SENTENCES, "true");

            if(sentence.hasKeyPointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_TIME_POINTS_KEY +  "_" + Config.SENTENCES, "true");

            if(sentence.hasRelativePointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_TIME_POINTS_RELATIVE +  "_" + Config.SENTENCES, "true");

            if(sentence.hasYTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_Y +  "_" + Config.SENTENCES,"true");
            else if(sentence.hasYYTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_YY +  "_" + Config.SENTENCES,"true");
            else if(sentence.hasYYYTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_YYY +  "_" + Config.SENTENCES,"true");
            else if(sentence.hasYYYYTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_YYYY +  "_" + Config.SENTENCES,"true");
            else if(sentence.hasYYYYMMTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_YYYYMM +  "_" + Config.SENTENCES,"true");
            else if(sentence.hasYYYYMMDDTimeExpressions()) doc.indexStringNoStore(Config.S_HAS_YYYYMMDD +  "_" + Config.SENTENCES,"true");

            writer.addDocument(doc);
        }
    }
}
