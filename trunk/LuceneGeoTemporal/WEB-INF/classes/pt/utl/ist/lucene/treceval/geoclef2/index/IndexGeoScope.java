package pt.utl.ist.lucene.treceval.geoclef2.index;

import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geoclef2.IntegratedDocPlaceMakerIterator;

import java.io.File;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoScope {
    public static String indexPath =  pt.utl.ist.lucene.treceval.geotime.index.Config.indexBase + File.separator + "GEO_TEMP_INDEXED";

    public static void main(String[] args) throws IOException {
        IntegratedDocPlaceMakerIterator iterator = new IntegratedDocPlaceMakerIterator(Config.documentPath,Config.placemakerPath);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(),true, Model.BM25b);

        int count = 1;
        IntegratedDocPlaceMakerIterator.DocumentWithPlaces d;
        while((d = iterator.next())!=null)
        {
            indexDocument(writer,d);
            count++;
            if(count % 1000 == 0)
            {
                System.out.println(count + ":" + d.getD().getDocNO());
            }
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerIterator.DocumentWithPlaces d) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Config.ID,d.getD().getDocNO());
        if(d.hasPlaces())
            doc.indexString(Config.S_GEO_INDEXED, "true");
        writer.addDocument(doc);
    }
}
