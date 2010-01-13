package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;

import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

/**
 * @author Jorge Machado
 * @date 11/Jan/2010
 * @time 17:24:30
 * @email machadofisher@gmail.com
 */
public class PlaceMakerStatistics
{
    private static final Logger logger = Logger.getLogger(PlaceMakerStatistics.class);



    public static void main(String[]args) throws IOException, DocumentException {
        FileWriter fw = new FileWriter("d:\\tmp\\geoStatsTotals.xml",false);
        fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
        fw.write("<statsNtcirGeographic>\n");


        Map<String,Integer> expressionsTypeMonth = new HashMap<String,Integer>();
        Map<String,Integer> expressionsTypeTotals = new HashMap<String,Integer>();


        int docs = 0;
        int docsWithPlaces = 0;
        int numberReferences = 0;



        PlaceMakerIterator placemakerIterator = new PlaceMakerIterator(Config.placemakerPath);

        String previousID = "";
        PlaceMakerDocument d;


        int docsMonth = 0;
        int docsWithPlacesMonth = 0;
        int numberRefsMonth = 0;

        while((d = placemakerIterator.next())!=null)
        {
//            logger.fatal(d.getD().getDId());
            if(previousID.length() > 0 && !previousID.substring(0,14).equals(d.getDocId().substring(0,14)))
            {

                logger.fatal("###############################################");
                logger.fatal("# stat: File: " + previousID.substring(0,14));
                logger.fatal("# stat: docsMonth:" + docsMonth);
                logger.fatal("# stat: docsWithPlaces:" + docsWithPlacesMonth);
                logger.fatal("# stat: numberReferences:" + numberRefsMonth);
                logger.fatal("###############################################");
                fw.write("<file name=\"" + previousID.substring(0,14) + "\">\n");
                fw.write("<stat name=\"docs\">" + docsMonth + "</stat>\n");
                fw.write("<stat name=\"docsWithTimexesMonth\">" + docsWithPlacesMonth + "</stat>\n");
                fw.write("<stat name=\"numberRefsMonth\">" + numberRefsMonth + "</stat>\n");
                printTypes(expressionsTypeMonth,fw);
                fw.write("</file>");
                fw.flush();
                docsMonth = 0;
                docsWithPlacesMonth = 0;
                numberRefsMonth = 0;
                expressionsTypeMonth.clear();
            }
            previousID = d.getDocId();
            docs++;
            docsMonth++;
            if(d.hasPlaces())
            {
                docsWithPlaces++;
                docsWithPlacesMonth++;
            }
            numberReferences += d.getAllRefs().size();
            numberRefsMonth +=d.getAllRefs().size();


            /**
             * Stats Expressions
             */
            if(d.hasPlaces())
            {
                for(PlaceMakerDocument.PlaceDetails placeDetails: d.getPlaceDetails() )
                {
                    //KEY: ExpressionType-Month VAL: Count
                    String key = placeDetails.getType();


                    Integer count = expressionsTypeMonth.get(key);
                    if(count == null)
                        count = 0;
                    expressionsTypeMonth.put(key,count+placeDetails.getRefs().size());

                    Integer countTotal = expressionsTypeTotals.get(key);
                    if(countTotal == null)
                        countTotal = 0;
                    expressionsTypeTotals.put(key,countTotal+placeDetails.getRefs().size());

                    Integer countCofidence = expressionsTypeMonth.get("confidence-" + placeDetails.getConfidence());
                    if(countCofidence == null)
                    countCofidence = 0;
                    expressionsTypeMonth.put("confidence-" + placeDetails.getConfidence(),countCofidence + placeDetails.getRefs().size());

                    Integer countCofidenceTotals = expressionsTypeTotals.get("confidence-" + placeDetails.getConfidence());
                    if(countCofidenceTotals == null)
                        countCofidenceTotals = 0;
                    expressionsTypeTotals.put("confidence-" + placeDetails.getConfidence(),countCofidenceTotals + placeDetails.getRefs().size());



//                    Integer countDocDf = expressionsTypeMonth.get(key + "-df");
//                    if(countDocDf == null)
//                        countDocDf = 0;
//                    expressionsTypeMonth.put(key + "-df",countDocDf+1);
//
//                    Integer countTotalDf = expressionsTypeTotals.get(key + "-df");
//                    if(countTotalDf == null)
//                        countTotalDf = 0;
//                    expressionsTypeTotals.put(key + "-df",countTotalDf+1);
//
//                    Integer countCofidenceDf = expressionsTypeMonth.get("confidence-" + placeDetails.getConfidence() + "-df");
//                    if(countCofidenceDf == null)
//                    countCofidenceDf = 0;
//                    expressionsTypeMonth.put("confidence-" + placeDetails.getConfidence() + "-df",countCofidenceDf + 1);
//
//                    Integer countCofidenceTotalsDf = expressionsTypeTotals.get("confidence-" + placeDetails.getConfidence() + "-df");
//                    if(countCofidenceTotalsDf == null)
//                        countCofidenceTotalsDf = 0;
//                    expressionsTypeTotals.put("confidence-" + placeDetails.getConfidence() + "-df",countCofidenceTotalsDf + 1);

                }
            }

        }

        logger.fatal("###############################################");
        logger.fatal("# stat: docs:" + docs);
        logger.fatal("# stat: docsWithPlaces:" + docsWithPlaces);
        logger.fatal("# stat: numberReferences:" + numberReferences);
        logger.fatal("###############################################");
        fw.write("<totals>\n");
        fw.write("<stat name=\"docs\">" + docs + "</stat>\n");
        fw.write("<stat name=\"docsWithPlaces\">" + docsWithPlaces + "</stat>\n");
        fw.write("<stat name=\"numberReferences\">" + numberReferences + "</stat>\n");

        printTypes(expressionsTypeTotals,fw);
        fw.write("</totals>");
        fw.write("</statsNtcirGeographic>\n");
        fw.flush();
        fw.close();

    }

    private static void printTypes(Map<String,Integer> expressionsMap, FileWriter fw) throws IOException {
        fw.write("<types>\n");
        for(Map.Entry<String,Integer> entry: expressionsMap.entrySet())
        {
            String val = entry.getKey();
            Integer count = entry.getValue();
            fw.write("<placeDetail stat=\"" + val + "\" count=\"" + count + "\"/>");
        }
        fw.write("</types>\n");
    }

}
