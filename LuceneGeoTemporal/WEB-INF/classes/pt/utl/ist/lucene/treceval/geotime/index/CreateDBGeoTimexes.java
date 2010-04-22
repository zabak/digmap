package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.nlp.Sentence;
import pt.utl.ist.lucene.utils.nlp.SentenceSpliter;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author Jorge Machado
 * @date 10/Jan/2010
 * @time 10:24:32
 * @email machadofisher@gmail.com
 */
public class CreateDBGeoTimexes {

    public static String indexPath = Config.indexBase + File.separator + "TEXT_TEMP_GEO_DB";
//    public static String indexPath = "F:\\TEXT_TEMP_GEO_DB";
    


    public static void main(String[] args) throws IOException
    {

        new File(indexPath).mkdirs();
        String documentPath = Config.documentPath;
        String timexesPath = Config.timexesPath;
        String placemakerPath = Config.placemakerPath;

        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(documentPath,timexesPath,placemakerPath);


        IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d;


        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(), true);


        int i = 1;
        while((d = iterator.next())!=null)
        {

            storeDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {

                System.out.println(i + ":" + d.getD().getDId());
            }

        }
        writer.close();
    }

    private static void storeDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d) throws IOException
    {

        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,d.getD().getDId());

        doc.storeUtokenized(Config.TEXT_DB,d.getD().getSgml());

        if(d.hasPlaces())
            doc.storeUtokenized(Config.GEO_DB,d.getPm().getXml());
        if(d.hasTimexes())
            doc.storeUtokenized(Config.TEMPORAL_DB,d.getTd().getXml());


        writer.addDocument(doc);
    }
}