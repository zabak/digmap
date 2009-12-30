package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.exceptions.NotImplementedException;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 20:06:34
 * @email machadofisher@gmail.com
 */
public class IntegratedDocTimexIterator {

    private static final Logger logger = Logger.getLogger(IntegratedDocTimexIterator.class);

    String documentsPath;
    String timexesPath;

    DocumentIterator documentIterator;
    TimexesIterator timexesIterator;

    TimexesDocument nowTimexesDocument;
    Document nowDocument;

    public IntegratedDocTimexIterator(String documentsPath, String timexesPath) throws IOException {
        this.documentsPath = documentsPath;
        this.timexesPath = timexesPath;
        try {
            documentIterator = new DocumentIterator(documentsPath);
            nowDocument = documentIterator.next();
        } catch (IOException e) {
            throw e;
        }
        timexesIterator = new TimexesIterator(timexesPath);
        nowTimexesDocument = timexesIterator.next();
    }


    public boolean hasNext()
    {
        return nowDocument != null;
    }

    public DocumentWithTimexes next() throws IOException {
        if(hasNext())
        {
            DocumentWithTimexes documentWithTimexes;
            if(nowTimexesDocument.getId().compareTo(nowDocument.getDId()) < 0)
            {
                logger.fatal("Timex id is smaller than the now document id, probably timexes are not sorted as documents");
                return null;
            }
            if(nowTimexesDocument.getId().equals(nowDocument.getDId()))
            {
                documentWithTimexes = new DocumentWithTimexes(nowDocument,nowTimexesDocument);
                try {
                    nowDocument = documentIterator.next();
                    nowTimexesDocument = timexesIterator.next();
                } catch (IOException e) {
                    throw e;
                }
            }
            else
            {
                documentWithTimexes = new DocumentWithTimexes(nowDocument);
                try {
                    nowDocument = documentIterator.next();
                } catch (IOException e) {
                    throw e;
                }
            }
            return documentWithTimexes;
        }
        return null;
    }

    public void remove() {
        throw new NotImplementedException("This iterator just reads the collection documents and timexes anotations");
    }

    public class DocumentWithTimexes
    {
        Document d;
        TimexesDocument td = null;

        public DocumentWithTimexes(Document d, TimexesDocument td)
        {
            this.d = d;
            this.td = td;
        }

        public DocumentWithTimexes(Document d)
        {
            this.d = d;
        }

        public Document getD() {
            return d;
        }

        public TimexesDocument getTd() {
            return td;
        }

        public boolean hasTimexes()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1;
        }

        public int countTimexes()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1)
            {
                return td.getTimex2TimeExpressions().size() - 1;
            }
            return 0;
        }

        public int countTimeExpressions()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1)
            {
                return td.getAllTimeExpressions().size() - 1;
            }
            return 0;
        }

        public int countInvalidTimeExpressions()
        {
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0)
            {
                return td.getAllInvalidTimeExpressions().size();
            }
            return 0;
        }
    }


    public static void main(String[]args) throws IOException
    {
        FileWriter fw = new FileWriter("d:\\tmp\\statsTotals.xml",false);
        fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
        fw.write("<statsNtcirTemporal>\n");


        Map<String,Integer> expressionsTypeMonth = new HashMap<String,Integer>();
        Map<String,Integer> expressionsTypeTotals = new HashMap<String,Integer>();

        int docs = 0;
        int docsWithTimexes = 0;
        int numberTimexes = 0;
        int numberTemporalExprs = 0;
        int invalidExpressions = 0;



        IntegratedDocTimexIterator documentTimexesIterator = new IntegratedDocTimexIterator("D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste\\docs","D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste\\timexes");

        String previousID = "";
        DocumentWithTimexes d;

        int docsMonth = 0;
        int docsWithTimexesMonth = 0;
        int numberTimexesMonth = 0;
        int numberTemporalExprsMonth = 0;
        int invalidExpressionsMonth = 0;

        while((d = documentTimexesIterator.next())!=null)
        {
//            logger.fatal(d.getD().getDId());
            if(previousID.length() > 0 && !previousID.substring(0,14).equals(d.getD().getDId().substring(0,14)))
            {

                logger.fatal("###############################################");
                logger.fatal("# stat: File: " + previousID.substring(0,14));
                logger.fatal("# stat: docsMonth:" + docsMonth);
                logger.fatal("# stat: docsWithTimexes:" + docsWithTimexesMonth);
                logger.fatal("# stat: numberTimexes:" + numberTimexesMonth);
                logger.fatal("# stat: numberTemporalExprs:" + numberTemporalExprsMonth);
                logger.fatal("# stat: invalidExpressions:" + invalidExpressionsMonth);
                logger.fatal("###############################################");
                fw.write("<file name=\"" + previousID.substring(0,14) + "\">\n");
                fw.write("<stat name=\"docs\">" + docsMonth + "</stat>\n");
                fw.write("<stat name=\"docsWithTimexesMonth\">" + docsWithTimexesMonth + "</stat>\n");
                fw.write("<stat name=\"numberTimexesMonth\">" + numberTimexesMonth + "</stat>\n");
                fw.write("<stat name=\"numberTemporalExprsMonth\">" + numberTemporalExprsMonth + "</stat>\n");
                fw.write("<stat name=\"invalidExpressionsMonth\">" + invalidExpressionsMonth + "</stat>\n");
                printTimexes(expressionsTypeMonth,fw);
                fw.write("</file>");
                fw.flush();
                docsMonth = 0;
                docsWithTimexesMonth = 0;
                numberTimexesMonth = 0;
                numberTemporalExprsMonth = 0;
                invalidExpressionsMonth = 0;
                expressionsTypeMonth.clear();
            }
            previousID = d.getD().getDId();
            docs++;
            docsMonth++;
            if(d.hasTimexes())
            {
                docsWithTimexes++;
                docsWithTimexesMonth++;
            }
            else
            {
                logger.error(d.getD().getDId() + ": FAILED Timexes anotation for Document with 0 timexes");
            }
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
