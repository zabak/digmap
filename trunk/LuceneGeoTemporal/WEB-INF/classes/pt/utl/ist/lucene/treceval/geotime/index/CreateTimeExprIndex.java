package pt.utl.ist.lucene.treceval.geotime.index;

import org.apache.lucene.analysis.Analyzer;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocTimexIterator;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 2/Jan/2010
 * @time 10:21:53
 * @email machadofisher@gmail.com
 */
public class CreateTimeExprIndex {

    public static String INDEX_TIME_EXPRESSIONS = "timeExpr";


    public static void main(String[] args) throws IOException, DocumentException {


        String indexPath = "D:\\Servidores\\DATA\\ntcir\\INDEXES\\timexes";
//        DocumentIterator di = new DocumentIterator("D:\\Servidores\\DATA\\ntcir\\data");

        IntegratedDocTimexIterator integratedDocTimexIterator = new IntegratedDocTimexIterator("D:\\Servidores\\DATA\\ntcir\\data","D:\\Servidores\\DATA\\ntcir\\TEMPORAL\\teste\\timexes");
        IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(INDEX_TIME_EXPRESSIONS, new LgteNothingAnalyzer());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.BM25b);
        int i = 1;
        while((timexesDocument = integratedDocTimexIterator.next())!=null)
        {
            System.out.println(i + ":" + timexesDocument.getD().getDId());
            indexDocument(writer,timexesDocument);
            i++ ;
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocTimexIterator.DocumentWithTimexes timexesDocument) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,timexesDocument.getD().getDId());

        if(timexesDocument.getTd() != null)
            for(TimeExpression timeExpression : timexesDocument.getTd().getAllTimeExpressions())
            {
                doc.indexString(INDEX_TIME_EXPRESSIONS,timeExpression.getNormalizedExpression());
            }
        writer.addDocument(doc);
    }
}
