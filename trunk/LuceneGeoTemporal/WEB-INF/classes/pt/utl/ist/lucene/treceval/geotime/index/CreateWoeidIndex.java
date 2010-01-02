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

    public static String INDEX_PLACE_REF_WOEID = "placeRefWoeid";
    public static String INDEX_ADMIN_SCOPE_WOEID = "administrativeWoeid";
    public static String INDEX_GEO_SCOPE_WOEID = "geographicWoeid";

    public static void main(String[] args) throws IOException, DocumentException {


        String indexPath = "D:\\Servidores\\DATA\\ntcir\\INDEXES\\woeid";
//        DocumentIterator di = new DocumentIterator("D:\\Servidores\\DATA\\ntcir\\data");
        PlaceMakerIterator placeMakerIterator = new PlaceMakerIterator("D:\\Servidores\\DATA\\ntcir\\PlaceMaker");

        NyTimesDocument d;
        PlaceMakerDocument placeMakerDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(INDEX_PLACE_REF_WOEID, new LgteNothingAnalyzer());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.BM25b);
        int i = 1;
        while((placeMakerDocument = placeMakerIterator.next())!=null)
        {
            System.out.println(i + ":" + placeMakerDocument.getDocId());
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
            doc.indexString(INDEX_ADMIN_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getAdministrativeWoeid()));
        if(placeMakerDocument.getGeographicWoeid() != null)
            doc.indexString(INDEX_GEO_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
        if(placeMakerDocument.getPlaceDetails() != null && placeMakerDocument.getPlaceDetails().size()>0)
        {
            for(PlaceMakerDocument.PlaceDetails placeDetails: placeMakerDocument.getPlaceDetails())
            {
                for(PlaceMakerDocument.PlaceRef ref : placeDetails.getRefs())
                {
                    doc.indexString(INDEX_PLACE_REF_WOEID, PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId()));

                }
            }
        }
        writer.addDocument(doc);
    }
}
