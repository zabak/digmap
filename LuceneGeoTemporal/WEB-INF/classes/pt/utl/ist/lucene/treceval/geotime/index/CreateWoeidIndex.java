package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceNameNormalizer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class CreateWoeidIndex {


    public static String indexPath = Config.indexBase + "\\woeid";

    public static void main(String[] args) throws IOException, DocumentException
    {

        new File(indexPath).mkdir();
        
        PlaceMakerIterator placeMakerIterator = new PlaceMakerIterator(Config.placemakerPath);

        PlaceMakerDocument placeMakerDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(Config.G_ADMIN_SCOPE_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_GEO_SCOPE_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_PLACE_REF_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_GEO_ALL_WOEID, new LgteNothingAnalyzer());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.OkapiBM25Model);
        int i = 1;
        String previousID = "";
        while((placeMakerDocument = placeMakerIterator.next())!=null)
        {

            if(previousID.length() > 0 && !previousID.substring(0,14).equals(placeMakerDocument.getDocId().substring(0,14)))
                System.out.println(i + ":" + placeMakerDocument.getDocId());
            previousID = placeMakerDocument.getDocId();
            indexDocument(writer,placeMakerDocument);
            i++ ;
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, PlaceMakerDocument placeMakerDocument) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,placeMakerDocument.getDocId());

        if(placeMakerDocument.getAdministrativeWoeid() != null)
        {
            doc.indexString(Config.G_ADMIN_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getAdministrativeWoeid()));
            doc.indexString(Config.G_GEO_ALL_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getAdministrativeWoeid()));
        }
        if(placeMakerDocument.getGeographicWoeid() != null)
        {
            doc.indexString(Config.G_GEO_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
            doc.indexString(Config.G_GEO_ALL_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
        }
        if(placeMakerDocument.getPlaceDetails() != null && placeMakerDocument.getPlaceDetails().size()>0)
        {
            for(PlaceMakerDocument.PlaceDetails placeDetails: placeMakerDocument.getPlaceDetails())
            {
                for(int i =0; i <  placeDetails.getRefs().size();i++)
                {
                    doc.indexString(Config.G_PLACE_REF_WOEID, PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId()));
                    doc.indexString(Config.G_GEO_ALL_WOEID, PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId()));
                }
            }
        }
        writer.addDocument(doc);
    }
}
