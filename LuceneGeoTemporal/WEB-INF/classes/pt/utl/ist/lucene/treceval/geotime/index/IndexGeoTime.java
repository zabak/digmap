package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.utils.nlp.SentenceSpliter;
import pt.utl.ist.lucene.utils.nlp.Sentence;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.geotime.IntegratedDocPlaceMakerAndTimexIterator;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoTime
{



    static final String TEMPORAL_INDEXED = "TEMPORAL_INDEXED";
    static final String GEO_INDEXED = "GEO_INDEXED";

    public static void main(String[] args) throws IOException
    {
//        String path = "F:\\coleccoesIR\\ntcir\\data";
        String documentPath = "D:\\Servidores\\DATA\\ntcir\\data";
        String timexesPath = "D:\\Servidores\\DATA\\ntcir\\TIMEXTAG";
        String placemakerPath = "D:\\Servidores\\DATA\\ntcir\\PlaceMaker";
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator(documentPath,timexesPath,placemakerPath);


        IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d;

        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Globals.DOCUMENT_ID_FIELD, new LgteNothingAnalyzer());
        anaMap.put(Globals.LUCENE_DEFAULT_FIELD,IndexCollections.en.getAnalyzerWithStemming());
        anaMap.put(TEMPORAL_INDEXED, new LgteNothingAnalyzer());
        anaMap.put(GEO_INDEXED, new LgteNothingAnalyzer());

        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);

//        LgteIndexWriter writer = new LgteIndexWriter("F:\\INDEXES\\lmstem\\version1\\NTCIR",analyzer, true, Model.BM25b);
        LgteIndexWriter writer = new LgteIndexWriter("D:\\Servidores\\DATA\\INDEXES\\TEXT_TEMP_GEO_INDEXED",analyzer, true, Model.BM25b);
        int i = 1;
        while((d = iterator.next())!=null)
        {
            
            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {
                
                System.out.println(i + ":" + d.getD().getDId());
            }

        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes d) throws IOException
    {

        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,d.getD().getDId());

        String text =  d.toString();
        List<Sentence> sentences = SentenceSpliter.split(d.getD().getSgmlWithoutTags(), Sentence.class);

        try{
            String textIndex = d.getD().getDHeadline() + " " + sentences.get(0).getPhrase() + " " + sentences.get(1).getPhrase() + " " + text;
            doc.indexText(Globals.LUCENE_DEFAULT_FIELD,textIndex);
        }catch(Throwable e){
            System.out.println("Error:" + d.getD().getDId());
        }

        if(d.hasTimexes())
            doc.indexString(TEMPORAL_INDEXED,"true");
        else
            doc.indexString(TEMPORAL_INDEXED,"false");
        if(d.hasPlaces())
            doc.indexString(GEO_INDEXED, "true");
        else
            doc.indexString(GEO_INDEXED,"false");
        writer.addDocument(doc);
    }
}
