package pt.utl.ist.lucene.treceval.geotime.index;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.utiltasks.BelongTosTagger;
import pt.utl.ist.lucene.treceval.geotime.utiltasks.BelongTosPlaceNamesTagger;
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
public class IndexWoeid {


    public static String indexPath = Config.indexBase + File.separator + "woeid";

    private static Map<String,Long[]> openBelongTosWoeid() throws IOException
    {
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
            for (String aBelongTosStr : belongTosStr) {
                if (aBelongTosStr.trim().length() > 0)
                    aux.add(Long.parseLong(aBelongTosStr));
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

    private static Map<String,String[]> openBelongTosPlaces() throws IOException
    {
        Map<String,String[]> belongTosMap = new HashMap<String,String[]>();
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(BelongTosPlaceNamesTagger.woeidBelongTosPlacesNames + ".zip")));
        zipInputStream.getNextEntry();
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        String line;
        while((line = reader.readLine())!=null)
        {
            String[] lineFlds = line.split(":");
//            long woeid = Long.parseLong(lineFlds[0]);
            String[] belongTosStr = lineFlds[1].split(";");

            List<String> aux = new ArrayList<String>();
            for (String aBelongTosStr : belongTosStr) {
                if (aBelongTosStr.trim().length() > 0)
                    aux.add(aBelongTosStr);
            }
            String[] belongTos = new String[aux.size()];
            for(int i = 0; i < belongTos.length; i++)
            {
                belongTos[i] = aux.get(i);
            }
            belongTosMap.put(lineFlds[0],belongTos);
        }
        return belongTosMap;
    }

    static Map<String, Long[]> belongTosMap;
    static Map<String, String[]> belongTosPlacesMap;
    static {
        try {
            belongTosMap = openBelongTosWoeid();
            belongTosPlacesMap = openBelongTosPlaces();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, DocumentException
    {



        new File(indexPath).mkdir();

        PlaceMakerIterator placeMakerIterator = new PlaceMakerIterator(Config.placemakerPath);

        PlaceMakerDocument placeMakerDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.ID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_ALL_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        anaMap.put(Config.G_PLACE_BELONG_TOS_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        anaMap.put(Config.G_PLACE_NAME_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap,new LgteWhiteSpacesAnalyzer());

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

        StringBuilder G_GEO_ALL_WOEID = new StringBuilder();
        StringBuilder G_PLACE_NAME_TEXT = new StringBuilder();
        StringBuilder G_ALL_TEXT = new StringBuilder();
        StringBuilder G_PLACE_REF_WOEID = new StringBuilder();
        StringBuilder G_PLACE_BELONG_TOS_TEXT = new StringBuilder();
        StringBuilder G_PLACE_BELONG_TOS_WOEID = new StringBuilder();
        StringBuilder G_GEO_PLACE_TYPE = new StringBuilder();
        if(placeMakerDocument.getAdministrativeWoeid() != null)
        {
            doc.indexString(Config.G_ADMIN_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getAdministrativeWoeid()));
            G_GEO_ALL_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getAdministrativeWoeid())).append(" ");
            G_PLACE_NAME_TEXT.append(placeMakerDocument.getAdministrativeName()).append(" ");
            G_ALL_TEXT.append(placeMakerDocument.getAdministrativeName()).append(" ");
            addBelongTos(placeMakerDocument.getAdministrativeWoeid(),G_PLACE_BELONG_TOS_TEXT,G_PLACE_BELONG_TOS_WOEID,G_ALL_TEXT,G_GEO_ALL_WOEID);
        }
        if(placeMakerDocument.getGeographicWoeid() != null)
        {
            doc.indexString(Config.G_GEO_SCOPE_WOEID, PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid()));
            G_GEO_ALL_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeMakerDocument.getGeographicWoeid())).append(" ");
            G_PLACE_NAME_TEXT.append(placeMakerDocument.getGeographicName()).append(" ");
            G_ALL_TEXT.append(placeMakerDocument.getGeographicName()).append(" ");
            addBelongTos(placeMakerDocument.getGeographicWoeid(),G_PLACE_BELONG_TOS_TEXT,G_PLACE_BELONG_TOS_WOEID,G_ALL_TEXT,G_GEO_ALL_WOEID);
        }
        if(placeMakerDocument.getPlaceDetails() != null && placeMakerDocument.getPlaceDetails().size()>0)
        {
            for(PlaceMakerDocument.PlaceDetails placeDetails: placeMakerDocument.getPlaceDetails())
            {
                for(int i =0; i <  placeDetails.getRefs().size();i++)
                {
                    G_PLACE_REF_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId())).append(" ");
                    G_GEO_ALL_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId())).append(" ");
                    G_PLACE_NAME_TEXT.append(placeDetails.getName()).append(" ");
                    G_ALL_TEXT.append(placeDetails.getName()).append(" ");
                    G_GEO_PLACE_TYPE.append(placeDetails.getType()).append(" ");
                    addBelongTos(placeDetails.getWoeId(),G_PLACE_BELONG_TOS_TEXT,G_PLACE_BELONG_TOS_WOEID,G_ALL_TEXT,G_GEO_ALL_WOEID);
                }
            }
        }
        doc.indexTextNoStore(Config.G_GEO_ALL_WOEID,G_GEO_ALL_WOEID.toString());       
        doc.indexTextNoStore(Config.G_PLACE_NAME_TEXT,G_PLACE_NAME_TEXT.toString());
        doc.indexTextNoStore(Config.G_ALL_TEXT,G_ALL_TEXT.toString());
        doc.indexText(Config.G_PLACE_REF_WOEID,G_PLACE_REF_WOEID.toString());
        doc.indexTextNoStore(Config.G_PLACE_BELONG_TOS_TEXT,G_PLACE_BELONG_TOS_TEXT.toString());
        doc.indexTextNoStore(Config.G_PLACE_BELONG_TOS_WOEID,G_PLACE_BELONG_TOS_WOEID.toString());
        doc.indexTextNoStore(Config.G_GEO_PLACE_TYPE,G_GEO_PLACE_TYPE.toString());
        writer.addDocument(doc);
    }

    private static void addBelongTos(String woeid,
                                     StringBuilder G_PLACE_BELONG_TOS_TEXT,
                                     StringBuilder G_PLACE_BELONG_TOS_WOEID,
                                     StringBuilder G_ALL_TEXT,
                                     StringBuilder G_GEO_ALL_WOEID)
    {
        if(woeid.equals("0") || woeid.equals("1"))
            return ;
        String[] places = belongTosPlacesMap.get(woeid);
        Long[] woeids = belongTosMap.get(woeid);
        if(places != null)
            for(String place: places)
            {
                G_ALL_TEXT.append(place).append(" ");
                G_PLACE_BELONG_TOS_TEXT.append(place).append(" ");
            }
        else
            System.out.println("ERROR: No belongtos WOEID for woeid: " + woeid);
        if(woeids != null)
            for(Long woeidB: woeids)
            {
                G_GEO_ALL_WOEID.append(PlaceNameNormalizer.normalizeWoeid(""+woeidB)).append(" ");
                G_PLACE_BELONG_TOS_WOEID.append(PlaceNameNormalizer.normalizeWoeid(""+woeidB)).append(" ");
            }
        else
            System.out.println("ERROR: No belongtos places for woeid: " + woeid);

    }
}
