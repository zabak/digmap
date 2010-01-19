package pt.utl.ist.lucene.treceval.geotime.index;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.utiltasks.BelongTosTagger;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.placemaker.PlaceNameNormalizer;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class CreateWoeidIndex {


    public static String indexPath = Config.indexBase + File.separator + "woeid";

    private static Map<String,Long[]> openBelongTosWoeid() throws IOException
    {
        Config.init();
        Map<String,Long[]> belongTosMap = new HashMap<String,Long[]>();
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(BelongTosTagger.woeidBelongTos + ".zip")));
        zipInputStream.getNextEntry();
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        String line;
        while((line = reader.readLine())!=null)
        {
            String[] lineFlds = line.split(":");
//            long woeid = Long.parseLong(lineFlds[0]);
            String[] belongTosStr = lineFlds[1].split(";");

            List<Long> aux = new ArrayList<Long>();
            for(int i = 0; i < belongTosStr.length; i++)
            {
                if(belongTosStr[i].trim().length() > 0)
                    aux.add(Long.parseLong(belongTosStr[i]));
            }
            Long[] belongTos = new Long[aux.size()];
            for(int i = 0; i < belongTos.length; i++)
            {
                belongTos[i] = aux.get(i);
            }
            belongTosMap.put(lineFlds[0],belongTos);
        }
        return belongTosMap;
    }

    public static void main(String[] args) throws IOException, DocumentException
    {

        Map<String,Long[]> belongTosMap = openBelongTosWoeid();

        new File(indexPath).mkdir();
        
        PlaceMakerIterator placeMakerIterator = new PlaceMakerIterator(Config.placemakerPath);

        PlaceMakerDocument placeMakerDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(Config.G_ADMIN_SCOPE_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_GEO_SCOPE_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_PLACE_REF_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_GEO_ALL_WOEID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_PLACE_NAME_TEXT, IndexCollections.en.getAnalyzerWithStemming());
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
            doc.indexText(Config.G_PLACE_NAME_TEXT,placeMakerDocument.getAdministrativeName());
        }
        if(placeMakerDocument.getGeographicWoeid() != null)
        {
            doc.indexString(Config.G_GEO_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
            doc.indexString(Config.G_GEO_ALL_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
            doc.indexText(Config.G_PLACE_NAME_TEXT,placeMakerDocument.getGeographicName());
        }
        if(placeMakerDocument.getPlaceDetails() != null && placeMakerDocument.getPlaceDetails().size()>0)
        {
            for(PlaceMakerDocument.PlaceDetails placeDetails: placeMakerDocument.getPlaceDetails())
            {
                for(int i =0; i <  placeDetails.getRefs().size();i++)
                {
                    doc.indexString(Config.G_PLACE_REF_WOEID, PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId()));
                    doc.indexString(Config.G_GEO_ALL_WOEID, PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId()));
                    doc.indexText(Config.G_PLACE_NAME_TEXT,placeDetails.getName());
                }
            }
        }
        writer.addDocument(doc);
    }
}
