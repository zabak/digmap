package pt.utl.ist.lucene.treceval.geotime.index;

import org.dom4j.DocumentException;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocTimexIterator;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.io.File;
import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class CreateTimeExprIndex {



    public static String indexPath = Config.indexBase + "\\timexes";

    public static void main(String[] args) throws IOException, DocumentException
    {
        new File(indexPath).mkdir();
        IntegratedDocTimexIterator integratedDocTimexIterator = new IntegratedDocTimexIterator(Config.documentPath,Config.timexesPath);
        IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument;

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(), true, Model.OkapiBM25Model);
        int i = 1;
        String previousID = "";
        while((timexesDocument = integratedDocTimexIterator.next())!=null)
        {
            if(previousID.length() > 0 && !previousID.substring(0,14).equals(timexesDocument.getD().getDId().substring(0,14)))
            {
                System.out.println(i + ":" + timexesDocument.getD().getDId());
            }
            indexDocument(writer,timexesDocument);
            previousID = timexesDocument.getD().getDId();
            i++ ;
        }
        writer.close();
    }

    static boolean left = false;
    static TimeExpression leftTimeExpression;
    static String leftId;

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument) throws IOException
    {


        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,timexesDocument.getD().getDId());

        doc.indexString(Config.T_TIME_DOCUMENT,timexesDocument.getD().getTimeExpressionDocumentNormalized());
        doc.indexString(Config.T_ALL_EXPRESSIONS_AND_TIME_DOC,timexesDocument.getD().getTimeExpressionDocumentNormalized());
        doc.indexString(Config.T_ALL_NOT_DURATION,timexesDocument.getD().getTimeExpressionDocumentNormalized());

//        if(timexesDocument.getD().getDId().equals("NYT_ENG_20020107.0019"))
//            System.out.println("");
        if(timexesDocument.getTd() != null)
            for(TimeExpression timeExpression : timexesDocument.getTd().getAllTimeExpressions())
            {
                if(timeExpression.isWeekDuration())
                    doc.indexString(Config.T_IS_WEEK,"true");

                if(timeExpression.getTimex2LimitType() == TimeExpression.Timex2LimitType.LEFT)
                {
                    if(left)
                    {
                        System.out.println("Error: LEFT DURATION not closed: DOC (" + leftId + ") expr: [" + leftTimeExpression + "] \n - found new LEFT: DOC (" + timexesDocument.getD().getDId() + ") expr: [" + timeExpression + "]");
                    }
                    left = true;
                    leftTimeExpression = timeExpression;
                    leftId = timexesDocument.getD().getDId();
                    doc.indexString(Config.T_LEFT_LIMIT,"true");
                    doc.indexString(Config.T_DURATION_LEFT,timeExpression.getNormalizedExpression());
                    
                    if(timeExpression.getTimex2().getType().isP())
                        doc.indexString(Config.T_DURATION_NORM,timeExpression.getTimex2().getVal() + "-" + timeExpression.getTimex2().getAnchorVal() + "-" + timeExpression.getTimex2().getAnchorDir());
                    if(timeExpression.isWeekDuration())
                        doc.indexString(Config.T_WEEK_NORM,timeExpression.getTimex2().getVal());

                }

                if(timeExpression.getTimex2LimitType() == TimeExpression.Timex2LimitType.RIGHT)
                {
                    if(!left)
                    {
                        System.out.println("Error: RIGHT DURATION not opened: DOC (" + timexesDocument.getD().getDId() + ") expr: [" + timeExpression + "]");
                    }
                    else
                        left = false;
                    doc.indexString(Config.T_RIGHT_LIMIT,"true");
                    doc.indexString(Config.T_DURATION_RIGHT,timeExpression.getNormalizedExpression());
                }

                if(timeExpression.getTimex2LimitType() == TimeExpression.Timex2LimitType.INSIDE)
                    doc.indexString(Config.T_INSIDE_LIMIT,"true");


                if(timeExpression.getType() == TimeExpression.Type.Y) doc.indexString(Config.T_Y,timeExpression.getNormalizedExpression());
                else if(timeExpression.getType() == TimeExpression.Type.YY) doc.indexString(Config.T_YY,timeExpression.getNormalizedExpression());
                else if(timeExpression.getType() == TimeExpression.Type.YYY) doc.indexString(Config.T_YYY,timeExpression.getNormalizedExpression());
                else if(timeExpression.getType() == TimeExpression.Type.YYYY) doc.indexString(Config.T_YYYY,timeExpression.getNormalizedExpression());
                else if(timeExpression.getType() == TimeExpression.Type.YYYYMM) doc.indexString(Config.T_YYYYMM,timeExpression.getNormalizedExpression());
                else if(timeExpression.getType() == TimeExpression.Type.YYYYMMDD) doc.indexString(Config.T_YYYYMMDD,timeExpression.getNormalizedExpression());

                if(timeExpression.getTeClass() == TimeExpression.TEClass.Point)
                {
                    doc.indexString(Config.T_ALL_EXPRESSIONS_AND_TIME_DOC,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_ALL_NOT_DURATION,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_NOT_DURATION_EXPRESSIONS,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_TIME_EXPRESSIONS,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_POINT,timeExpression.getNormalizedExpression());
                }
                else if(timeExpression.getTeClass() == TimeExpression.TEClass.GenPoint)
                {
                    doc.indexString(Config.T_ALL_EXPRESSIONS_AND_TIME_DOC,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_ALL_NOT_DURATION,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_NOT_DURATION_EXPRESSIONS,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_TIME_EXPRESSIONS,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_GENPOINT,timeExpression.getNormalizedExpression());
                }
                else if(timeExpression.getTeClass() == TimeExpression.TEClass.Duration)
                {
                    doc.indexString(Config.T_ALL_EXPRESSIONS_AND_TIME_DOC,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_TIME_EXPRESSIONS,timeExpression.getNormalizedExpression());
                    doc.indexString(Config.T_DURATION,timeExpression.getNormalizedExpression());
                }
                else if(timeExpression.getTeClass() == TimeExpression.TEClass.UNKNOWN)
                {
                    doc.indexString(Config.T_UNKNOWN,timeExpression.getNormalizedExpression());
                }
            }
        writer.addDocument(doc);
    }
}
