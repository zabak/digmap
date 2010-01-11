package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoTime
{
    public static String indexPath =  Config.indexBase + "\\GEO_TEMP_INDEXED";

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
        if(d.hasTimexes() && d.hasPlaces())
            doc.indexString(Config.GEO_AND_TEMPORAL_INDEXED,"true");
        else
            doc.indexString(Config.GEO_AND_TEMPORAL_INDEXED,"false");

        if(d.hasTimexes() || d.hasPlaces())
            doc.indexString(Config.GEO_OR_TEMPORAL_INDEXED,"true");
        else
            doc.indexString(Config.GEO_OR_TEMPORAL_INDEXED,"false");

        if(d.hasTimexes())
            doc.indexString(Config.TEMPORAL_INDEXED,"true");
        else
            doc.indexString(Config.TEMPORAL_INDEXED,"false");

        if(d.hasPlaces())
            doc.indexString(Config.GEO_INDEXED, "true");
        else
            doc.indexString(Config.GEO_INDEXED,"false");
        writer.addDocument(doc);
    }
}
