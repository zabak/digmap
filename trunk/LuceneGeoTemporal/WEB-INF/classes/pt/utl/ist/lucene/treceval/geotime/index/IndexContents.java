package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.nlp.Sentence;
import pt.utl.ist.lucene.utils.nlp.SentenceSpliter;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;

import java.io.IOException;
import java.io.File;
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
public class IndexContents {

    public static String indexPath =  Config.indexBase + "\\contents";
    public static String documentPath = Config.documentPath;

    public static void main(String[] args) throws IOException {

        new File(indexPath).mkdir();
        DocumentIterator iterator = new DocumentIterator(documentPath);
        NyTimesDocument d;

        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.ID, new LgteNothingAnalyzer());
        anaMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());

        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.BM25b);


        int i = 1;
        while((d = iterator.next())!=null)
        {

            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {

                System.out.println(i + ":" + d.getDId());
            }

        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, NyTimesDocument d) throws IOException
    {

        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,d.getDId());

        String text =  d.toString();
        List<Sentence> sentences = SentenceSpliter.split(d.getSgmlWithoutTags(), Sentence.class);

        try{
            StringBuilder textIndexBuilder = new StringBuilder();
            if(d.getDHeadline() != null)
                textIndexBuilder.append(d.getDHeadline()).append(" ");
            else
                System.out.println( d.getDId() + " don't have HeadLine ");
            if(sentences.size() > 0)
                textIndexBuilder.append(sentences.get(0).getPhrase()).append(" ");
            else
                System.out.println( d.getDId() + " don't have sentences ");
            if(sentences.size() > 1)
                textIndexBuilder.append(sentences.get(1).getPhrase()).append(" ");
            else
                System.out.println( d.getDId() + " only have one sentence ");
            if(text != null)
                textIndexBuilder.append(text);


            doc.indexText(Globals.LUCENE_DEFAULT_FIELD,textIndexBuilder.toString());
        }catch(Throwable e){
            System.out.println("Error:" + d.getDId() + " : " + e.toString());
            e.printStackTrace();
        }

        writer.addDocument(doc);
    }
}
