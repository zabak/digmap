package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocTimexIterator;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 20:06:34
 * @email machadofisher@gmail.com
 */
public class TimexesStatistics {

    private static final Logger logger = Logger.getLogger(TimexesStatistics.class);



    public static void main(String[]args) throws IOException
    {
        String destFile = "statsTimexesTotals.xml";
        if(args != null && args.length > 0)
             destFile = args[0];
        FileWriter fw = new FileWriter(destFile,false);
        fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
        fw.write("<statsNtcirTemporal>\n");


        Map<String,Integer> expressionsTypeMonth = new HashMap<String,Integer>();
        Map<String,Integer> expressionsTypeTotals = new HashMap<String,Integer>();

        int docs = 0;
        int docsWithTimexes = 0;
        int numberTimexes = 0;
        int numberTemporalExprs = 0;
        int invalidExpressions = 0;



        IntegratedDocTimexIterator documentTimexesIterator = new IntegratedDocTimexIterator(Config.documentPath,Config.timexesPath);

        String previousID = "";
        IntegratedDocTimexIterator.DocumentWithTimexes d;

        int docsMonth = 0;
        int docsWithTimexesMonth = 0;
        int numberTimexesMonth = 0;
        int numberTemporalExprsMonth = 0;
        int invalidExpressionsMonth = 0;

        String previousFile = "";
        while((d = documentTimexesIterator.next())!=null)
        {
            String nowfile = d.getD().getFSourceFile();//getD().getDId().substring(0,d.getD().getDId().lastIndexOf(".") - 2);

            if(previousID.length() > 0 && !nowfile.equals(previousFile))
                        
//            logger.fatal(d.getD().getDId());
           // if(previousID.length() > 0 && !previousID.substring(0,14).equals(d.getD().getDId().substring(0,14)))
            {

                printStats(previousFile, fw, expressionsTypeMonth, previousID, docsMonth, docsWithTimexesMonth, numberTimexesMonth, numberTemporalExprsMonth, invalidExpressionsMonth);
                docsMonth = 0;
                docsWithTimexesMonth = 0;
                numberTimexesMonth = 0;
                numberTemporalExprsMonth = 0;
                invalidExpressionsMonth = 0;
                expressionsTypeMonth.clear();
            }
            previousID = d.getD().getDId();
            previousFile = nowfile;
            docs++;
            docsMonth++;
            if(d.hasTimexes())
            {
                docsWithTimexes++;
                docsWithTimexesMonth++;
            }
            else if(d.getTd() == null || d.getTd().getTimex2TimeExpressions() == null || d.getTd().getTimex2TimeExpressions().size() == 0)
            {
                logger.error(d.getD().getDId() + ": FAILED Timexes anotation for Document with 0 timexes");
            }
            else
                logger.error(d.getD().getDId() + ": " + d.countTimexes() + " Timexes " );
            numberTimexes+=d.countTimexes();
            numberTimexesMonth+=d.countTimexes();

            numberTemporalExprs+=d.countTimeExpressions();
            numberTemporalExprsMonth+=d.countTimeExpressions();

            int invalid = d.countInvalidTimeExpressions();
            invalidExpressions += invalid;
            invalidExpressionsMonth += invalid;
            if(invalid > 0)
            {
                logger.error("Document: " + d.getD().getDId() + " has invalid TimeExpressions");
                for(TimeExpression invalidTimeExpression : d.getTd().getAllInvalidTimeExpressions())
                {
                    logger.error(invalidTimeExpression.getNormalizedExpression() + ": " + invalidTimeExpression.getValidationError() + " context: " + invalidTimeExpression.getRefNLTxt());

                }
            }

            /**
             * Stats Expressions
             */
            if(d.hasTimexes() && d.getTd().getAllTimeExpressions() != null)
            {
                for(Timex2TimeExpression timex2TimeExpression: d.getTd().getTimex2TimeExpressions())
                {
                    if(timex2TimeExpression.getTimex2().getType().isP())
                    {
                        String val = timex2TimeExpression.getTimex2().getType().name();
                        String anchorVal = timex2TimeExpression.getTimex2().getTypeAnchorVal().name();
                        if(anchorVal == null)
                            anchorVal = "NULL";
                        String anchorDir = timex2TimeExpression.getTimex2().getAnchorDir();
                        if(anchorDir == null)
                            anchorDir = "NULL";
                        String key = val + "$$" + anchorVal + "$$" + anchorDir;
                        Integer count = expressionsTypeMonth.get(key);
                        if(count == null)
                            count = 0;
                        expressionsTypeMonth.put(key,count+1);

                        Integer countTotal = expressionsTypeTotals.get(key);
                        if(countTotal == null)
                            countTotal = 0;
                        expressionsTypeTotals.put(key,countTotal+1);
                    }
                    else
                    {
                        //KEY: ExpressionType-Month VAL: Count
                        String key = timex2TimeExpression.getTimex2().getType().name();
                        Integer count = expressionsTypeMonth.get(key);
                        if(count == null)
                            count = 0;
                        expressionsTypeMonth.put(key,count+1);


                        Integer countTotal = expressionsTypeTotals.get(key);
                        if(countTotal == null)
                            countTotal = 0;
                        expressionsTypeTotals.put(key,countTotal+1);
                    }
                }
            }

        }
        printStats(previousFile,fw, expressionsTypeMonth, previousID, docsMonth, docsWithTimexesMonth, numberTimexesMonth, numberTemporalExprsMonth, invalidExpressionsMonth);
        
        logger.fatal("###############################################");
        logger.fatal("# stat: docs:" + docs);
        logger.fatal("# stat: docsWithTimexes:" + docsWithTimexes);
        logger.fatal("# stat: numberTimexes:" + numberTimexes);
        logger.fatal("# stat: numberTemporalExprs:" + numberTemporalExprs);
        logger.fatal("# stat: invalidExpressions:" + invalidExpressions);
        logger.fatal("###############################################");
        fw.write("<totals>\n");
        fw.write("<stat name=\"docs\">" + docs + "</stat>\n");
        fw.write("<stat name=\"docsWithTimexes\">" + docsWithTimexes + "</stat>\n");
        fw.write("<stat name=\"numberTimexes\">" + numberTimexes + "</stat>\n");
        fw.write("<stat name=\"numberTemporalExprs\">" + numberTemporalExprs + "</stat>\n");
        fw.write("<stat name=\"invalidExpressions\">" + invalidExpressions + "</stat>\n");

        printTimexes(expressionsTypeTotals,fw);
        fw.write("</totals>");
        fw.write("</statsNtcirTemporal>\n");
        fw.flush();
        fw.close();

    }

    private static void printStats(String file, FileWriter fw, Map<String, Integer> expressionsTypeMonth, String previousID, int docsMonth, int docsWithTimexesMonth, int numberTimexesMonth, int numberTemporalExprsMonth, int invalidExpressionsMonth) throws IOException {
        logger.fatal("###############################################");
        logger.fatal("# stat: File: " + file) ;
        logger.fatal("# stat: docsMonth:" + docsMonth);
        logger.fatal("# stat: docsWithTimexes:" + docsWithTimexesMonth);
        logger.fatal("# stat: numberTimexes:" + numberTimexesMonth);
        logger.fatal("# stat: numberTemporalExprs:" + numberTemporalExprsMonth);
        logger.fatal("# stat: invalidExpressions:" + invalidExpressionsMonth);
        logger.fatal("###############################################");
        fw.write("<file name=\"" + file + "\">\n");
        fw.write("<stat name=\"docs\">" + docsMonth + "</stat>\n");
        fw.write("<stat name=\"docsWithTimexesMonth\">" + docsWithTimexesMonth + "</stat>\n");
        fw.write("<stat name=\"numberTimexesMonth\">" + numberTimexesMonth + "</stat>\n");
        fw.write("<stat name=\"numberTemporalExprsMonth\">" + numberTemporalExprsMonth + "</stat>\n");
        fw.write("<stat name=\"invalidExpressionsMonth\">" + invalidExpressionsMonth + "</stat>\n");
        printTimexes(expressionsTypeMonth,fw);
        fw.write("</file>");
        fw.flush();
    }

    private static void printTimexes(Map<String,Integer> expressionsMap, FileWriter fw) throws IOException {
        fw.write("<temporal_expressions>\n");
        for(Map.Entry<String,Integer> entry: expressionsMap.entrySet())
        {
            String val = entry.getKey();

            Integer count = entry.getValue();
            if(val.indexOf("$$")>0)
            {
                String[] vals = val.split("\\$\\$");
                val = vals[0];
                String anchor_val= vals[1];
                String anchor_dir= vals[2];

                fw.write("<timex2 type=\"p\" val=\"" + val + "\" anchor_val=\"" + anchor_val + "\" anchor_dir=\"" + anchor_dir + "\" count=\"" + count + "\"/>");
            }
            else
            {
                fw.write("<timex2 val=\"" + val + "\" count=\"" + count + "\"/>");
            }
        }
        fw.write("</temporal_expressions>\n");
    }
}
