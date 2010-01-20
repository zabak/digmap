package pt.utl.ist.lucene.treceval.geotime.index;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocTimexIterator;
import pt.utl.ist.lucene.utils.temporal.DocumentTemporalSentences;
import pt.utl.ist.lucene.utils.temporal.TemporalSentence;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.Timex2TimeExpression;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class IndexTimexesSentences {



    public static String indexPath = Config.indexBase + File.separator + "timexes_sentences";

    public static void main(String[] args) throws IOException, DocumentException
    {
        new File(indexPath).mkdir();
        IntegratedDocTimexIterator integratedDocTimexIterator = new IntegratedDocTimexIterator(Config.documentPath,Config.timexesPath);
        IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument;

        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.T_TIME_EXPRESSION_TEXT + Config.SEP + Config.SENTENCES, IndexCollections.en.getAnalyzerWithStemming());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap,new LgteWhiteSpacesAnalyzer());

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.OkapiBM25Model);


        String previousID = "";
        while((timexesDocument = integratedDocTimexIterator.next())!=null)
        {
            if(previousID.length() > 0 && !previousID.substring(0,14).equals(timexesDocument.getD().getDId().substring(0,14)))
            {
                System.out.println(i + ":" + timexesDocument.getD().getDId());
            }
            indexDocument(writer,timexesDocument);
            previousID = timexesDocument.getD().getDId();
            i++;
            if(i % 1000 == 0)
            {
                System.out.println("docs:" + i + " sentences: " + p + " :" + timexesDocument.getD().getDId());
            }
        }
        writer.close();
        System.out.println("docs:" + i + " sentences: " + p);
    }


    static int i = 0;
    static int p = 0;

    static boolean left = false;
    static TimeExpression leftTimeExpression;
    static String leftId;

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument) throws IOException
    {


        DocumentTemporalSentences documentTemporalSentences = new DocumentTemporalSentences(timexesDocument.getD().getSgml(),timexesDocument.getTd());
        for(TemporalSentence sentence: documentTemporalSentences.getSentences())
        {
            LgteDocumentWrapper doc = new LgteDocumentWrapper();
            doc.indexString(Config.ID,timexesDocument.getD().getDId() + "$$" + sentence.getIndex());
            doc.indexString(Config.DOC_ID,timexesDocument.getD().getDId());

            if(timexesDocument.getTd() != null)
            {
                StringBuilder T_TIME_EXPRESSION_TEXT_SENTENCES = new StringBuilder();
                StringBuilder T_YYYYMMDD_SENTENCES = new StringBuilder();
                StringBuilder T_YYYYMM_SENTENCES = new StringBuilder();
                StringBuilder T_YYYY_SENTENCES = new StringBuilder();
                StringBuilder T_YYY_SENTENCES = new StringBuilder();
                StringBuilder T_YY_SENTENCES = new StringBuilder();
                StringBuilder T_Y_SENTENCES = new StringBuilder();
                StringBuilder T_POINT_KEY_SENTENCES = new StringBuilder();
                StringBuilder T_POINT_RELATIVE_SENTENCES = new StringBuilder();
                StringBuilder T_GENPOINT_SENTENCES = new StringBuilder();
                StringBuilder T_DURATION_SENTENCES = new StringBuilder();
                StringBuilder T_TIME_EXPRESSIONS_SENTENCES = new StringBuilder();
                for(Timex2TimeExpression timex2: sentence.getTimexes())
                {
                    T_TIME_EXPRESSION_TEXT_SENTENCES.append(timex2.getTimex2().getText()).append(" ");
                }

                for(TimeExpression timeExpression : sentence.getAllTimeExpressions())
                {
                    if(timeExpression.getType() == TimeExpression.Type.Y)               T_Y_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    else if(timeExpression.getType() == TimeExpression.Type.YY)         T_YY_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    else if(timeExpression.getType() == TimeExpression.Type.YYY)        T_YYY_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    else if(timeExpression.getType() == TimeExpression.Type.YYYY)       T_YYYY_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    else if(timeExpression.getType() == TimeExpression.Type.YYYYMM)     T_YYYYMM_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    else if(timeExpression.getType() == TimeExpression.Type.YYYYMMDD)   T_YYYYMMDD_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");

                    if(timeExpression.getTeClass() == TimeExpression.TEClass.Point)
                    {
                        if(timeExpression.getTimex2().getPrenorm() != null && timeExpression.getTimex2().getPrenorm().startsWith("|fq|"))
                            T_POINT_KEY_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                        else
                            T_POINT_RELATIVE_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                        T_TIME_EXPRESSIONS_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    }
                    else if(timeExpression.getTeClass() == TimeExpression.TEClass.GenPoint)
                    {
                        T_GENPOINT_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                        T_TIME_EXPRESSIONS_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    }
                    else if(timeExpression.getTeClass() == TimeExpression.TEClass.Duration)
                    {
                        T_DURATION_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                        T_TIME_EXPRESSIONS_SENTENCES.append(timeExpression.getNormalizedExpression()).append(" ");
                    }
                }
                doc.indexTextNoStore(Config.T_TIME_EXPRESSION_TEXT + Config.SEP + Config.SENTENCES, T_TIME_EXPRESSION_TEXT_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_Y +                    Config.SEP + Config.SENTENCES, T_Y_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_YY +                   Config.SEP + Config.SENTENCES, T_YY_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_YYY +                  Config.SEP + Config.SENTENCES, T_YYY_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_YYYY +                 Config.SEP + Config.SENTENCES, T_YYYY_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_YYYYMM +               Config.SEP + Config.SENTENCES, T_YYYYMM_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_YYYYMMDD +             Config.SEP + Config.SENTENCES, T_YYYYMMDD_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_TIME_EXPRESSIONS +     Config.SEP + Config.SENTENCES, T_TIME_EXPRESSIONS_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_POINT_KEY +            Config.SEP + Config.SENTENCES, T_POINT_KEY_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_POINT_RELATIVE +       Config.SEP + Config.SENTENCES, T_POINT_RELATIVE_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_GENPOINT +             Config.SEP + Config.SENTENCES, T_GENPOINT_SENTENCES.toString());
                doc.indexTextNoStore(Config.T_DURATION +             Config.SEP + Config.SENTENCES, T_DURATION_SENTENCES.toString());
            }
            writer.addDocument(doc);
        }

    }
}
