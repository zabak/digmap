package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.queries.StrategyQueryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 12/Jul/2011
 * Time: 17:47:05
 * To change this template use File | Settings | File Templates.
 */
public class TopicRunnerCheck {
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

        StrategyQueryBuilder.Iterator  iter = strategyQueryBuilderTimeAll.baseMetricsIterator();


        StrategyQueryBuilder.Iterator.QueryPackage qp;
        while((qp = iter.next())!= null)
        {
            System.out.println("<topic id=\"" + qp.getTopicId() + "\">");

            System.out.println("<!--" + qp.query + "-->");


            System.out.println("</topic>");
        }






    }
}
