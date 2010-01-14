package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;

import java.io.IOException;
import java.io.File;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoTime
{
    public static String indexPath =  Config.indexBase + File.separator + "GEO_TEMP_INDEXED";

    public static void main(String[] args) throws IOException
    {
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(Config.documentPath,Config.timexesPath,Config.placemakerPath);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(),true,Model.BM25b);

        int count = 1;
        IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d;
        while((d = iterator.next())!=null)
        {
            indexDocument(writer,d);
            count++;
            if(count % 1000 == 0)
            {
                System.out.println(count + ":" + d.getD().getDId());
            }
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Config.ID,d.getD().getDId());

        if(d.hasTimexes())
            doc.indexString(Config.S_HAS_TIMEXES,"true");

        if(d.hasIndexableTimeExpressions() && d.hasPlaces())
            doc.indexString(Config.S_GEO_AND_TEMPORAL_INDEXED,"true");

        if(d.hasIndexableTimeExpressions() || d.hasPlaces())
            doc.indexString(Config.S_GEO_OR_TEMPORAL_INDEXED,"true");

        if(d.hasIndexableTimeExpressions())
            doc.indexString(Config.S_TEMPORAL_INDEXED,"true");

        if(d.hasPlaces())
            doc.indexString(Config.S_GEO_INDEXED, "true");

        writer.addDocument(doc);
    }
}
