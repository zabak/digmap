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

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d) throws IOException
    {
        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(d.getD(),d.getTd(),d.getPm());
        for(PlaceMakerAndTemporalSentence sentence: documentPlaceMakerAndTemporalSentences.getSentences())
        {
            LgteDocumentWrapper doc = new LgteDocumentWrapper();
            doc.indexString(Config.ID,d.getD().getDId() + "$$" + sentence.getIndex());
            doc.indexString(Config.DOC_ID,d.getD().getDId());
            p++;
            if(sentence.hasTimexes())
                doc.indexStringNoStore(Config.S_HAS_TIMEXES + "_sentences","true");

            if(sentence.hasIndexableTimeExpressions() && d.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_AND_TEMPORAL_INDEXED + "_sentences","true");

            if(sentence.hasIndexableTimeExpressions() || d.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_OR_TEMPORAL_INDEXED + "_sentences","true");

            if(sentence.hasIndexableTimeExpressions())
                doc.indexStringNoStore(Config.S_TEMPORAL_INDEXED + "_sentences","true");

            if(sentence.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_INDEXED + "_sentences", "true");

            if(sentence.hasPointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_ANY_TIME_POINT + "_sentences", "true");

            if(sentence.hasKeyPointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_TIME_POINTS_KEY + "_sentences", "true");

            if(sentence.hasRelativePointTimeExpressions())
                doc.indexStringNoStore(Config.S_HAS_TIME_POINTS_RELATIVE + "_sentences", "true");

            writer.addDocument(doc);
        }
    }
}
