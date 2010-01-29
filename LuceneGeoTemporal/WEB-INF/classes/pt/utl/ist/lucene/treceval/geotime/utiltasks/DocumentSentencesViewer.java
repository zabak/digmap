package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;
import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.utils.DocumentPlaceMakerAndTemporalSentences;
import pt.utl.ist.lucene.utils.XmlUtils;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;
import pt.utl.ist.lucene.utils.temporal.metrics.TemporalMetrics;

import java.io.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jomm.utils.StreamsUtils;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 16:05:13
 * @email machadofisher@gmail.com
 */
public class DocumentSentencesViewer {

    static String documentPath = Config.documentPath;
    static String timexesPath = Config.timexesPath;
    static String placemakerPath = Config.placemakerPath;

    /*
   * <DOCUMENT RANK="1" DOCID="NYT_ENG_20050328.0114" SCORE="88.28996"/>
         <DOCUMENT RANK="2" DOCID="NYT_ENG_20050328.0123" SCORE="86.2685"/>
         <DOCUMENT RANK="3" DOCID="NYT_ENG_20050111.0375" SCORE="84.196686"/>
         <DOCUMENT RANK="4" DOCID="NYT_ENG_20050328.0061" SCORE="80.77064"/>
         <DOCUMENT RANK="5" DOCID="NYT_ENG_20050328.0085" SCORE="77.514305"/>
         <DOCUMENT RANK="6" DOCID="NYT_ENG_20041230.0186" SCORE="65.598305"/>
         <DOCUMENT RANK="7" DOCID="NYT_ENG_20041230.0245" SCORE="65.598305"/>
         <DOCUMENT RANK="8" DOCID="NYT_ENG_20041231.0009" SCORE="65.598305"/>
         <DOCUMENT RANK="9" DOCID="NYT_ENG_20050328.0205" SCORE="22.542877"/>
         <DOCUMENT RANK="10" DOCID="NYT_ENG_20050219.0155" SCORE="16.078392"/>
         <DOCUMENT RANK="11" DOCID="NYT_ENG_20050101.0094" SCORE="15.299353"/>
         <DOCUMENT RANK="12" DOCID="NYT_ENG_20050101.0070" SCORE="6.9142475"/>
         <DOCUMENT RANK="13" DOCID="NYT_ENG_20050218.0090" SCORE="6.081147"/>
         <DOCUMENT RANK="14" DOCID="NYT_ENG_20050218.0173" SCORE="6.081147"/>
         <DOCUMENT RANK="15" DOCID="NYT_ENG_20050218.0212" SCORE="6.081147"/>
         <DOCUMENT RANK="16" DOCID="NYT_ENG_20050219.0032" SCORE="6.081147"/>
         <DOCUMENT RANK="17" DOCID="NYT_ENG_20041231.0111" SCORE="3.9242918"/>

   * */
    public static void main(String[] args) throws IOException {
        show("NYT_ENG_20050118.0251");
        show("NYT_ENG_20050101.0070");
        show("NYT_ENG_20050218.0090");
        show("NYT_ENG_20050218.0173");
        show("NYT_ENG_20050218.0212");
        show("NYT_ENG_20050219.0032");
        show("NYT_ENG_20041231.0111");


    }
    public static void show(String docId) throws IOException {

        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(documentPath  + File.separator + "nyt_eng_" + docId.substring(8,14) + ".gz" , timexesPath  + File.separator + "nyt_eng_" + docId.substring(8,14) + ".gz_notes01.zip", placemakerPath  + File.separator + "nyt_eng_" + docId.substring(8,14) + ".gz_notes01.zip");

        while(iterator.hasNext())
        {

            IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes document = iterator.next();
      
            if(document.getD().getDId().equals(docId))
            {
                writeSentences(new OutputStreamWriter(System.out), document);
                break;
            }
        }
    }


    static String last = "";
    public static void writeSentences(Writer fw, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes document) throws IOException {

      

        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(document.getD(),document.getTd(),document.getPm());
        fw.write("<DOC id=\"" + document.getD().getDId() + "\">\n");
        fw.write("\t<DATE_TIME>" + documentPlaceMakerAndTemporalSentences.getDocumentDate().getNormalizedExpression() + "</DATE_TIME>\n");
        if(document.getPm()!=null && document.getPm().getGeographicWoeid() != null || document.getPm().getAdministrativeWoeid() != null)
        {
            fw.write("\t<DOC_GEO_SCOPE>\n");
            if(document.getPm().getAdministrativeWoeid() != null)
            {
                fw.write("\t\t<ADMINISTRATIVE_SCOPE woeid=\"" + document.getPm().getAdministrativeWoeid() + "\">\n");
                fw.write("\t\t\t<TYPE>" + document.getPm().getAdministrativeType() + "</TYPE>\n");
                fw.write("\t\t\t<SHORT>" + document.getPm().getAdministrativeShortName() + "</SHORT>\n");
                fw.write("\t\t\t<NAME>" + document.getPm().getAdministrativeName() + "</NAME>\n");
                fw.write("\t\t\t<CENTROIDE LAT=\"" + document.getPm().getAdministrativeCentroide().getLat() + "\" LNG=\"" + document.getPm().getAdministrativeCentroide().getLng() + "\"/>\n");
                fw.write("\t\t</ADMINISTRATIVE_SCOPE>\n");
            }
            if(document.getPm().getGeographicWoeid() != null)
            {
                fw.write("\t\t<GEOGRAPHIC_SCOPE woeid=\"" + document.getPm().getGeographicWoeid() + "\">\n");
                fw.write("\t\t\t<TYPE>" + document.getPm().getGeographicType() + "</TYPE>\n");
                fw.write("\t\t\t<SHORT>" + document.getPm().getGeographicShortName() + "</SHORT>\n");
                fw.write("\t\t\t<NAME>" + document.getPm().getGeographicName() + "</NAME>\n");
                fw.write("\t\t\t<CENTROIDE LAT=\"" + document.getPm().getGeographicCentroide().getLat() + "\" LNG=\"" + document.getPm().getGeographicCentroide().getLng() + "\"/>\n");
                fw.write("\t\t\t<BOX LAT1=\"" + document.getPm().getBoundingBoxPoint1().getLat() + "\" LNG1=\""+ document.getPm().getBoundingBoxPoint1().getLng() +"\" LAT2=\"" + + document.getPm().getBoundingBoxPoint2().getLat() + "\" LNG2=\"" + + document.getPm().getBoundingBoxPoint2().getLng() + "\"/>\n");
                fw.write("\t\t</GEOGRAPHIC_SCOPE>\n");
            }
            fw.write("\t</DOC_GEO_SCOPE>\n");
        }

        if(document.getTd() != null)
        {
            List<TimeExpression> timeExpressions = document.getTd().getAllTimeExpressions();
            if(timeExpressions != null && timeExpressions.size() > 0)
            {
                fw.write("\t<DOC_TEMPORAL_SCOPE timexes=\"" + document.getTd().getTimex2TimeExpressions().size() + "\" expressions=\"" + timeExpressions.size() + "\">\n");
                for(Map.Entry<TimeExpression.TEClass,Integer> entry: document.getTd().getStats().entrySet())
                {
                    fw.write("\t\t<TYPE name=\"" + entry.getKey().toString() + "\">" + entry.getValue() + "</TYPE>\n");
                }
                List<TimeExpression> metricTimeExpressions = document.getTd().getAllIndexableTimeExpressions();
                if(metricTimeExpressions.size() > 0)
                {
                    try {
                        TemporalMetrics temporalMetrics = new TemporalMetrics(metricTimeExpressions);
                        fw.write("\t\t<CENTROIDE type=\"timeAbsolute\">" + temporalMetrics.getTemporalCentroideTimeExpression().getNormalizedExpression() + "</CENTROIDE>\n");
                        fw.write("\t\t<CENTROIDE type=\"timeLimits\">" + temporalMetrics.getIntervalCentroideTimeExpression().getNormalizedExpression() + "</CENTROIDE>\n");
                        fw.write("\t\t<CENTROIDE type=\"timeLeftLimit\">" + temporalMetrics.getLeftLimitsCentroideTimeExpression().getNormalizedExpression() + "</CENTROIDE>\n");
                        fw.write("\t\t<CENTROIDE type=\"nRefs\">" + temporalMetrics.getNumberRefsCentroide() + "</CENTROIDE>\n");
                        fw.write("\t\t<LEFT>" + document.getTd().getMin().getNormalizedExpression() + "</LEFT>\n");
                        fw.write("\t\t<RIGHT>" + document.getTd().getMax().getNormalizedExpression() + "</RIGHT>\n");
                    } catch (TimeExpression.BadTimeExpression badTimeExpression) {
                        fw.write("\t\t<CENTROIDE>ERROR " + (XmlUtils.escape(badTimeExpression.toString())) + "</CENTROIDE>\n");
                    }
                }
                fw.write("\t</DOC_TEMPORAL_SCOPE>\n");
            }
        }




        for(PlaceMakerAndTemporalSentence sentence: documentPlaceMakerAndTemporalSentences.getSentences())
        {
            fw.write("\t<SENTENCE id=\"" + document.getD().getDId() + "." + sentence.getIndex() + "\">\n");
            fw.write("\t\t<TEXT start=\"" +  sentence.getStartOffset() + "\" end=\"" + sentence.getEndOffset() + "\" >\n");
            fw.write("\t\t"+sentence.getCleanedPhrase() + "\n");
            fw.write("\t\t</TEXT>\n");
            List<TimeExpression> timeExpressions = sentence.getAllTimeExpressions();


            if(sentence.getPlaceRefs() != null)
            {
                List<PlaceMakerDocument.PlaceRef> placeRefs = sentence.getPlaceRefs();
                Map<PlaceMakerDocument.PlaceDetails, Integer> mapDetails = new HashMap<PlaceMakerDocument.PlaceDetails, Integer>();
                for(PlaceMakerDocument.PlaceRef ref : placeRefs)
                {
                    Integer count = mapDetails.get(ref.getPlaceDetails());
                    if(count == null)
                        mapDetails.put(ref.getPlaceDetails(),1);
                    else
                        mapDetails.put(ref.getPlaceDetails(),count+1);
                }
                if(mapDetails.size() > 0)
                {
                    fw.write("\t\t<GEO_SIGNATURE>\n");
                    for(Map.Entry<PlaceMakerDocument.PlaceDetails,Integer> placeDetailsEntry : mapDetails.entrySet())
                    {
                        PlaceMakerDocument.PlaceDetails placeDetails = placeDetailsEntry.getKey();
                        fw.write("\t\t\t<PLACE count=\"" + placeDetailsEntry.getValue() + "\" woeid=\"" + placeDetails.getWoeId() + "\" yahooConfidence=\"" + placeDetails.getConfidence() + "\">\n");
                        fw.write("\t\t\t\t<TYPE>" + placeDetails.getType() + "</TYPE>\n");
                        fw.write("\t\t\t\t<SHORT>" + placeDetails.getShortName() + "</SHORT>\n");
                        fw.write("\t\t\t\t<NAME>" + placeDetails.getName() + "</NAME>\n");
                        fw.write("\t\t\t\t<CENTROIDE LAT=\"" + placeDetails.getCentroide().getLat() + "\" LNG=\"" + placeDetails.getCentroide().getLng() + "\"/>\n");
                        fw.write("\t\t\t</PLACE>\n");
                    }
                    fw.write("\t\t</GEO_SIGNATURE>\n");
                }
            }
            if(timeExpressions != null && timeExpressions.size() > 0)
            {

                fw.write("\t\t<TIME_SIGNATURE>\n");
                for(TimeExpression timeExpression: timeExpressions)
                {
                    String more = "";
                    if(timeExpression.isWeekDuration())
                        more += " week=\"true\"";
                    if(timeExpression.getTimex2LimitType() != TimeExpression.Timex2LimitType.NONE)
                    {
                        more += " durationPos=\"" + timeExpression.getTimex2LimitType().name() + "\"";
                    }
                    if(timeExpression.getAnchor() != null)
                    {
                        more += " anchor=\"" + timeExpression.getAnchor().getNormalizedExpression() + "\"";
                    }
                    if (timeExpression.getTeClass() == TimeExpression.TEClass.Point && timeExpression.getTimex2().getPrenorm() != null && timeExpression.getTimex2().getPrenorm().startsWith("|fq|"))
                        more += " pointSubType=\"date\"";
                    else
                        more += " pointSubType=\"relative\"";
                    fw.write("\t\t\t<TIME_EXPRESSION valid=\"" + timeExpression.isValid() + "\" teClass=\"" + timeExpression.getTeClass().toString() + "\" type=\"" + timeExpression.getType().toString() + "\" index=\""+timeExpression.getNormalizedExpression()+"\" timex2id=\"" + timeExpression.getTimex2id() + "\"" + more + ">" + timeExpression.getRefNLTxt() + "</TIME_EXPRESSION>\n");
                }
                fw.write("\t\t</TIME_SIGNATURE>\n");
            }
            fw.write("\t</SENTENCE>\n");
        }

        fw.write("</DOC>\n");
    }
}
