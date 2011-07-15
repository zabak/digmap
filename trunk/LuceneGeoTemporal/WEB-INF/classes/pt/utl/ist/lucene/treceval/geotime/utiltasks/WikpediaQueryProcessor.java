package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.lucene.queryParser.ParseException;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.queries.StrategyQueryBuilder;
import pt.utl.ist.lucene.utils.wikipedia.qe.WikipediaQueryExpansion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 12/Jul/2011
 * Time: 18:04:48
 * To change this template use File | Settings | File Templates.
 */
public class WikpediaQueryProcessor {

    static String topicsFile =    Config.ntcirBase +  File.separator + "topics" + File.separator + "XSL OutputWikipedia.xml";

    public static void main(String[] args) throws DocumentException, IOException, ParseException
    {
        StrategyQueryBuilder strategyQueryBuilderTimeAll = new StrategyQueryBuilder(topicsFile,false);
        StrategyQueryBuilder.Iterator  iter = strategyQueryBuilderTimeAll.originalTermsIterator();
        StrategyQueryBuilder.Iterator.QueryPackage qp;
        FileWriter writer =  new FileWriter(Config.indexBase + File.separator + "wikipediaExpansion.xml");
        writer.write("<?xml version=\"1.0\">\n");
        writer.write("<topics desc=\"wikipedia expansion terms\">\n");
        System.out.println("<topics desc=\"wikipedia expansion terms\">\n");
        while((qp = iter.next())!= null)
        {
            writer.write("<topic id=\"" + qp.getTopicId() + "\">\n");
            System.out.println("<topic id=\"" + qp.getTopicId() + "\">");
            System.out.println("<!--");
            System.out.println(qp.query);
            GregorianCalendar gc = new GregorianCalendar(2005,12-1,31);
            boolean fresh = qp.query.indexOf("last") >= 0;
            System.out.println("USING FRESH=" + fresh);
            WikipediaQueryExpansion wikipediaQueryExpansion = new WikipediaQueryExpansion("",qp.query,qp.query,false,true,true,Config.wikipediaMaxDocs,Config.wikipediaMaxParagrahps,Config.wikipediaDocDecay,Config.wikipediaParagraphDecay,Config.wikipediaParagraphThreshold,50,fresh,Config.wikipediaNumberOfWordsThreshold,gc.getTimeInMillis(), WikipediaQueryExpansion.Similarity.BM25);
            wikipediaQueryExpansion.generate();
            List<WikipediaQueryExpansion.PlaceTerm> placeTerms = wikipediaQueryExpansion.getGterms();
            List<WikipediaQueryExpansion.TimeTerm> timeTerms = wikipediaQueryExpansion.getTterms();
            System.out.println("-->");
            for(WikipediaQueryExpansion.PlaceTerm p: placeTerms)
            {
                writer.write("<wikpedia type=\"place\" boost=\"" + p.getRankToQuery() + "\" woeid=\"" + p.getWoeid() + "\" term=\"" + p.getTerm() + "\"/>\n");
                System.out.println("<wikpedia type=\"place\" boost=\"" + p.getRankToQuery() + "\" woeid=\"" + p.getWoeid() + "\" term=\"" + p.getTerm() + "\"/>");
            //                System.out.println("PLACE::::::::::::::::::::::::::::::::::::::");
//                System.out.println(p);
            }


            for(WikipediaQueryExpansion.TimeTerm t: timeTerms)
            {
                writer.write("<wikpedia type=\"time\" boost=\"" + t.getRankToQuery() + "\" time=\"" + t.getTimexnormalized() + "\" term=\"" + t.getTerm() + "\"/>\n");
                System.out.println("<wikpedia type=\"time\" boost=\"" + t.getRankToQuery() + "\" time=\"" + t.getTimexnormalized() + "\" term=\"" + t.getTerm() + "\"/>");
//                System.out.println("TIME::::::::::::::::::::::::::::::::::::::");
//                System.out.println(t);

            }
            writer.write("</topic>\n");
            System.out.println("</topic>");
            writer.flush();
        }
        writer.write("</topics>\n");
        writer.flush();
        writer.close();
        System.out.println("</topics>");
    }
}
