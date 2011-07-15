package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.queries.Query;
import pt.utl.ist.lucene.utils.queries.StrategyQueryBuilder;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.metrics.TemporalMetrics;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 12/Jul/2011
 * Time: 17:47:05
 * To change this template use File | Settings | File Templates.
 */
public class GeoTemporalTopicsGenerator {
    static String topicsFile =    Config.ntcirBase +  File.separator + "topics" + File.separator + "XSL OutputWikipedia.xml";

    public static void main(String[] args) throws Exception, IOException, ParseException
    {
//        StrategyQueryBuilder strategyQueryBuilderTimeKeys = new StrategyQueryBuilder(topicsFile,true);
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);
        int step = 0;
        if(args != null && args.length > 0)
        {
            step = Integer.parseInt(args[0]);
        }

        StrategyQueryBuilder.Iterator  iter = strategyQueryBuilderTimeAll.originalTermsIterator();


        StrategyQueryBuilder.Iterator.QueryPackage qp;
        while((qp = iter.next())!= null)
        {
            System.out.println("<geo_time_metric_topic id=\"" + qp.getTopicId() + "\">");

            System.out.println("<!--" + qp.query + "-->");
            System.out.println("<!--");
            TimexesDocument td = TimexesDocument.loadTimexes(qp.queryProcessor.getQ().getOriginalDesc() + " " + qp.queryProcessor.getQ().getOriginalNarr(),qp.queryProcessor.getQ().getOriginalDesc() ,qp.getTopicId(),"http://192.168.1.253:8080/jmachado/TIMEXTAG3/index.php");
            PlaceMakerDocument pd = PlaceMakerDocument.loadDocument(qp.queryProcessor.getQ().getOriginalDesc() + " " + qp.queryProcessor.getQ().getOriginalNarr(),qp.queryProcessor.getQ().getOriginalDesc() ,qp.getTopicId());
//            PlaceMakerDocument pd = PlaceMakerDocument.loadDocument("Portugal","Portugal" ,qp.getTopicId());

            System.out.println("-->");
            if(td != null && td.getAllIndexableTimeExpressions() != null && td.getAllIndexableTimeExpressions().size() > 0)
            {
                List<TimeExpression> timeExpressions = td.getAllIndexableTimeExpressions();
                
                TemporalMetrics tm = new TemporalMetrics(timeExpressions);
                String[] period = tm.getNormalizedDesvioPadraoStartEndDates();
                if(period[0].equals(period[1]))
                {
                    System.out.println("<time_metric_query>" +
                            " time:" + period[0].substring(0,4) + "-" + period[0].substring(4,6) + "-" + period[0].substring(6,8) +
                            " radiumYears:1</time_metric_query>");
                }
                else
                {
                    System.out.println("<time_metric_query>" +
                            " starttime:" + period[0].substring(0,4) + "-" + period[0].substring(4,6) + "-" + period[0].substring(6,8) +
                            " endtime:"+ period[1].substring(0,4) + "-" + period[1].substring(4,6) + "-" + period[1].substring(6,8) + "</time_metric_query>");
                }
            }
            else if(td == null || td.getAllIndexableTimeExpressions() == null || td.getAllIndexableTimeExpressions().size() == 0)
            {
                List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();
                for(Query.WikipediaTerms.TimeTerm t: qp.queryProcessor.getQ().getWikipediaTerms().getTimeTerms())
                {
                    if(Float.parseFloat(t.getBoost()) > 0.2f)
                    {
                        timeExpressions.add(new TimeExpression(t.getTime()));
                    }
                }

                TemporalMetrics tm = new TemporalMetrics(timeExpressions);
                String[] period = tm.getNormalizedDesvioPadraoStartEndDates();

                if(period != null && period[0].equals(period[1]))
                {
                    System.out.println("<time_metric_query wikipedia=\"true\">" +
                            " time:" + period[0].substring(0,4) + "-" + period[0].substring(4,6) + "-" + period[0].substring(6,8) +
                            " radiumYears:1</time_metric_query>");
                }
                else if(period != null)
                {
                    System.out.println("<time_metric_query wikipedia=\"true\">" +
                            " starttime:" + period[0].substring(0,4) + "-" + period[0].substring(4,6) + "-" + period[0].substring(6,8) +
                            " endtime:"+ period[1].substring(0,4) + "-" + period[1].substring(4,6) + "-" + period[1].substring(6,8) + "</time_metric_query>");
                }
            }

            if(pd != null && pd.getBoundingBoxPoint1() != null)
            {

                System.out.println("<geo_metric_query>" +
                        " west:" + pd.getWestLimit() +
                        " south:" + pd.getSouthLimit() +
                        " east:" + pd.getEastLimit() +
                        " north:" + pd.getNorthLimit() + "</geo_metric_query>");

            }
            else if(qp.queryProcessor.getQ().getWikipediaTerms() != null && qp.queryProcessor.getQ().getWikipediaTerms().getPlaceTerms() != null)
            {
                StringBuilder sb = new StringBuilder();
                for(Query.WikipediaTerms.PlaceTerm p: qp.queryProcessor.getQ().getWikipediaTerms().getPlaceTerms())
                {
                    if(Float.parseFloat(p.getBoost()) > 0.2f)
                    {
                        sb.append(p.getTerm()).append(", ");
                    }
                }
                if(sb.toString().length() > 0)
                {   System.out.println("<!--");
                    System.out.println("Using wikipediaTerms in narrative");
                    PlaceMakerDocument pd2 = PlaceMakerDocument.loadDocument(qp.queryProcessor.getQ().getOriginalDesc() + " " + sb.toString() + qp.queryProcessor.getQ().getOriginalNarr(),qp.queryProcessor.getQ().getOriginalDesc() ,qp.getTopicId());
                    System.out.println("-->");
                    if(pd2 != null && pd2.getBoundingBoxPoint1() != null)
                    {

                        System.out.println("<geo_metric_query wikipedia=\"true\">" +
                                " west:" + pd2.getWestLimit() +
                                " south:" + pd2.getSouthLimit() +
                                " east:" + pd2.getEastLimit() +
                                " north:" + pd2.getNorthLimit() + "</geo_metric_query>");

                    }
                }
            }

//            System.out.println(td.toString());
//            System.out.println(pd.toString());

            System.out.println("</geo_time_metric_topic>");
        }






    }
}
