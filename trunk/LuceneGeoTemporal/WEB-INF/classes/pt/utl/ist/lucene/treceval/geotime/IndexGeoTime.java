package pt.utl.ist.lucene.treceval.geotime;

import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoTime
{

    static final String TEMP_EXPRESSIONS_INDEX = "TEMP_EXPRESSIONS_INDEX";

    public static void main(String[] args) throws IOException
    {
        String path = "F:\\coleccoesIR\\ntcir\\data";
        DocumentIterator di = new DocumentIterator(path);
        Document d;
        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(Globals.LUCENE_DEFAULT_FIELD,IndexCollections.en.getAnalyzerWithStemming());
        anaMap.put(TEMP_EXPRESSIONS_INDEX,new LgteNothingAnalyzer());
        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);
        LgteIndexWriter writer = new LgteIndexWriter("F:\\INDEXES\\lmstem\\version1\\NTCIR",analyzer, true, Model.BM25b);
        int i = 1;
        while((d = di.next())!=null)
        {
            System.out.println(i + ":" + d.getDId());
            indexDocument(writer,d);
            i++ ;
        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, Document d) throws IOException
    {
        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,d.getDId());
        doc.indexText(Globals.LUCENE_DEFAULT_FIELD,d.toString());
        writer.addDocument(doc);
    }
}
