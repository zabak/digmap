package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;
import pt.utl.ist.lucene.utils.DocumentPlaceMakerAndTemporalSentences;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import jomm.utils.StreamsUtils;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 16:05:13
 * @email machadofisher@gmail.com
 */
public class SentenceTagger
{
    public static void main(String [] args) throws IOException {


        String documentPath = "D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste\\docs";
        String timexesPath = "D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste\\timexes";
        String placemakerPath = "D:\\Servidores\\DATA\\ntcir\\PlaceMaker";
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(documentPath,timexesPath,placemakerPath);


        String output = "D:\\Servidores\\DATA\\ntcir\\sentencesGeoTemporais";
        String fileId = "nyt_eng_200201";
        String filePath = output + "\\" + fileId + ".sentences.xml";
        FileWriter nowWriter = new FileWriter(filePath,false);

        int count = 0;

        while(iterator.hasNext())
        {
            count++;
            IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes document = iterator.next();
            if(!document.getD().getDId().substring(0,14).toLowerCase().equals(fileId))
            {
                nowWriter.flush();
                nowWriter.close();
                FileInputStream fileInputStream = new FileInputStream(filePath);
                File toDelete = new File(filePath);
                fileId = document.getD().getDId().substring(0,14).toLowerCase();
                filePath = output + "\\" + fileId + ".sentences.xml";
                String fileZipPath = output + "\\" + fileId + ".sentences.xml";
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(fileZipPath)));
                zipOutputStream.putNextEntry(new ZipEntry(fileId + ".sentences.zip"));
                StreamsUtils.inputStream2OutputStream(fileInputStream,zipOutputStream);
                nowWriter = new FileWriter(output + "\\" + fileId + ".sentences.xml",false);
                toDelete.delete();
                toDelete.deleteOnExit();
            }
            if(count > 100)
            {
                count = 0;
                nowWriter.flush();
            }

            writeSentences(nowWriter,document);

        }
        nowWriter.flush();
        nowWriter.close();
        FileInputStream fileInputStream = new FileInputStream(filePath);
        File toDelete = new File(filePath);
        String fileZipPath = output + "\\" + fileId + ".sentences.zip";
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(fileZipPath)));
        zipOutputStream.putNextEntry(new ZipEntry(fileId + ".sentences.xml"));
        StreamsUtils.inputStream2OutputStream(fileInputStream,zipOutputStream);
        toDelete.delete();
        toDelete.deleteOnExit();

    }

    static String last = "";
    public static void writeSentences(FileWriter fw, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes document) throws IOException {

        if(!document.getD().getDId().substring(0,16).equals(last))
            System.out.println("Doc:" + document.getD().getDId());
        last = document.getD().getDId().substring(0,16);
        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(document.getD(),document.getTd(),document.getPm());
        fw.write("<DOC id=\"" + document.getD().getDId() + "\">\n");
        String date = String.format("%04d%02d%02d",document.getD().getArticleYear(),document.getD().getArticleMonth(), document.getD().getArticleDay());
        fw.write("<DATE_TIME>" + date + "</DATE_TIME>\n");
        if(document.getPm()!=null && document.getPm().getGeographicWoeid() != null || document.getPm().getAdministrativeWoeid() != null)
        {
            fw.write("<DOC_GEO_SIGNATURE>\n");
            if(document.getPm().getAdministrativeWoeid() != null)
            {
                fw.write("<ADMINISTRATIVE_SCOPE woeid=\"" + document.getPm().getAdministrativeWoeid() + "\">\n");
                fw.write("<TYPE>" + document.getPm().getAdministrativeType() + "</TYPE>\n");
                fw.write("<SHORT>" + document.getPm().getAdministrativeShortName() + "</SHORT>\n");
                fw.write("<NAME>" + document.getPm().getAdministrativeName() + "</NAME>\n");
                fw.write("<CENTROIDE LAT=\"" + document.getPm().getAdministrativeCentroide().getLat() + "\" LNG=\"" + document.getPm().getAdministrativeCentroide().getLng() + "\"/>\n");
                fw.write("</ADMINISTRATIVE_SCOPE>\n");
            }
            if(document.getPm().getGeographicWoeid() != null)
            {
                fw.write("<GEOGRAPHIC_SCOPE woeid=\"" + document.getPm().getGeographicWoeid() + "\">\n");
                fw.write("<TYPE>" + document.getPm().getGeographicType() + "</TYPE>\n");
                fw.write("<SHORT>" + document.getPm().getGeographicShortName() + "</SHORT>\n");
                fw.write("<NAME>" + document.getPm().getGeographicName() + "</NAME>\n");
                fw.write("<CENTROIDE LAT=\"" + document.getPm().getGeographicCentroide().getLat() + "\" LNG=\"" + document.getPm().getGeographicCentroide().getLng() + "\"/>\n");
                fw.write("<BOX LAT1=\"" + document.getPm().getBoundingBoxPoint1().getLat() + "\" LNG1=\""+ document.getPm().getBoundingBoxPoint1().getLng() +"\" LAT2=\"" + + document.getPm().getBoundingBoxPoint2().getLat() + "\" LNG2=\"" + + document.getPm().getBoundingBoxPoint2().getLng() + "\"/>\n");
                fw.write("</GEOGRAPHIC_SCOPE>\n");
            }
            fw.write("</DOC_GEO_SIGNATURE>\n");
        }


        for(PlaceMakerAndTemporalSentence sentence: documentPlaceMakerAndTemporalSentences.getSentences())
        {
            fw.write("<SENTENCE id=\"" + document.getD().getDId() + "." + sentence.getIndex() + "\">\n");
            fw.write("<TEXT start=\"" +  sentence.getStartOffset() + "\" end=\"" + sentence.getEndOffset() + "\" >\n");
            fw.write(sentence.getCleanedPhrase() + "\n");
            fw.write("</TEXT>\n");
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
                    fw.write("<GEO_SIGNATURE>\n");
                    for(Map.Entry<PlaceMakerDocument.PlaceDetails,Integer> placeDetailsEntry : mapDetails.entrySet())
                    {
                        PlaceMakerDocument.PlaceDetails placeDetails = placeDetailsEntry.getKey();
                        fw.write("<PLACE count=\"" + placeDetailsEntry.getValue() + "\" woeid=\"" + placeDetails.getWoeId() + "\" yahooConfidence=\"" + placeDetails.getConfidence() + "\">\n");
                        fw.write("<TYPE>" + placeDetails.getType() + "</TYPE>\n");
                        fw.write("<SHORT>" + placeDetails.getShortName() + "</SHORT>\n");
                        fw.write("<NAME>" + placeDetails.getName() + "</NAME>\n");
                        fw.write("<CENTROIDE LAT=\"" + placeDetails.getCentroide().getLat() + "\" LNG=\"" + placeDetails.getCentroide().getLng() + "\"/>\n");
                        fw.write("</PLACE>\n");
                    }
                    fw.write("</GEO_SIGNATURE>\n");
                }
            }
            if(timeExpressions != null)
            {
                fw.write("<TIME_SIGNATURE>\n");

//                Map<String,TimeExpression> map = new HashMap<String,TimeExpression>();
//                for(TimeExpression timeExpression: timeExpressions)
//                {
//                    TimeExpression te = map.get(timeExpression.getNormalizedExpression());
//                    if(te == null)
//                        map.put(timeExpression.getNormalizedExpression(), timeExpression);
//                    else
//                        te.incCount();
//                }
                for(TimeExpression timeExpression: timeExpressions)
                {
                    fw.write("<TIME_EXPRESSION teClass=\"" + timeExpression.getTeClass().toString() + "\" type=\"" + timeExpression.getType().toString() + "\" index=\""+timeExpression.getNormalizedExpression()+"\">" + timeExpression.getRefNLTxt() + "</TIME_EXPRESSION>\n");
                }
                fw.write("</TIME_SIGNATURE>\n");
            }
            fw.write("</SENTENCE>\n");
        }

        fw.write("</DOC>\n");
    }
}
