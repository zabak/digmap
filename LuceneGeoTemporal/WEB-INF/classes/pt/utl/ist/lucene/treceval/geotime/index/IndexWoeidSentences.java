package pt.utl.ist.lucene.treceval.geotime.index;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerIterator;
import pt.utl.ist.lucene.treceval.geotime.utiltasks.BelongTosPlaceNamesTagger;
import pt.utl.ist.lucene.treceval.geotime.utiltasks.BelongTosTagger;
import pt.utl.ist.lucene.utils.DocumentPlaceMakerAndTemporalSentences;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceNameNormalizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class IndexWoeidSentences {


    public static String indexPath = Config.indexBase + File.separator + "woeid_sentences";

    private static Map<String,Long[]> openBelongTosWoeid() throws IOException {
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

    public static void main(String[] args) throws IOException, DocumentException {



        new File(indexPath).mkdir();

        IntegratedDocPlaceMakerIterator placeMakerIterator = new IntegratedDocPlaceMakerIterator(Config.documentPath, Config.placemakerPath);

        IntegratedDocPlaceMakerIterator.DocumentWithPlaces placeMakerDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.ID, new LgteNothingAnalyzer());
        anaMap.put(Config.G_ALL_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        anaMap.put(Config.G_PLACE_BELONG_TOS_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        anaMap.put(Config.G_PLACE_NAME_TEXT, IndexCollections.en.getAnalyzerNoStemming());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap,new LgteWhiteSpacesAnalyzer());

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.OkapiBM25Model);

        String previousID = "";
        while((placeMakerDocument = placeMakerIterator.next())!=null)
        {

            if(previousID.length() > 0 && !previousID.substring(0,14).equals(placeMakerDocument.getD().getDId().substring(0,14)))
                System.out.println(i + ":" + placeMakerDocument.getD().getDId());
            previousID = placeMakerDocument.getD().getDId();
            indexDocument(writer,placeMakerDocument);
            i++;
            if(i % 1000 == 0)
            {
                System.out.println("docs:" + i + " sentences: " + p + " :" + placeMakerDocument.getD().getDId());
            }
        }
        writer.close();
        System.out.println("docs:" + i + " sentences: " + p);
    }

    static int i = 0;
    static int p = 0;


    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerIterator.DocumentWithPlaces placeMakerDocument) throws IOException
    {
        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(placeMakerDocument.getD(),null,placeMakerDocument.getPm());
        for(PlaceMakerAndTemporalSentence sentence: documentPlaceMakerAndTemporalSentences.getSentences())
        {

            LgteDocumentWrapper doc = new LgteDocumentWrapper();
            doc.indexString(Config.ID,placeMakerDocument.getD().getDId() + "$$" + sentence.getIndex());
            doc.indexString(Config.DOC_ID,placeMakerDocument.getD().getDId());

            StringBuilder G_GEO_ALL_WOEID = new StringBuilder();
            StringBuilder G_PLACE_NAME_TEXT = new StringBuilder();
            StringBuilder G_ALL_TEXT = new StringBuilder();
            StringBuilder G_PLACE_REF_WOEID = new StringBuilder();
            StringBuilder G_PLACE_BELONG_TOS_TEXT = new StringBuilder();
            StringBuilder G_PLACE_BELONG_TOS_WOEID = new StringBuilder();

            if(sentence.getPlaceRefs() != null && sentence.getPlaceRefs().size()>0)
            {

                for(PlaceMakerDocument.PlaceRef placeRef:  sentence.getPlaceRefs())
                {
                    PlaceMakerDocument.PlaceDetails placeDetails = placeRef.getPlaceDetails();
                    G_PLACE_REF_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId())).append(" ");
                    G_GEO_ALL_WOEID.append(PlaceNameNormalizer.normalizeWoeid(placeDetails.getWoeId())).append(" ");
                    G_PLACE_NAME_TEXT.append(placeDetails.getName()).append(" ");
                    G_ALL_TEXT.append(placeDetails.getName()).append(" ");
                    addBelongTos(placeDetails.getWoeId(),G_PLACE_BELONG_TOS_TEXT,G_PLACE_BELONG_TOS_WOEID,G_ALL_TEXT,G_GEO_ALL_WOEID);
                }
            }
            doc.indexTextNoStore(Config.G_GEO_ALL_WOEID + "_" + Config.SENTENCES,G_GEO_ALL_WOEID.toString());
            doc.indexTextNoStore(Config.G_PLACE_NAME_TEXT + "_" + Config.SENTENCES,G_PLACE_NAME_TEXT.toString());
            doc.indexTextNoStore(Config.G_ALL_TEXT + "_" + Config.SENTENCES,G_ALL_TEXT.toString());
            doc.indexText(Config.G_PLACE_REF_WOEID + "_" + Config.SENTENCES,G_PLACE_REF_WOEID.toString());
            doc.indexTextNoStore(Config.G_PLACE_BELONG_TOS_TEXT + "_" + Config.SENTENCES,G_PLACE_BELONG_TOS_TEXT.toString());
            doc.indexTextNoStore(Config.G_PLACE_BELONG_TOS_WOEID + "_" + Config.SENTENCES,G_PLACE_BELONG_TOS_WOEID.toString());
            writer.addDocument(doc);
        }
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
