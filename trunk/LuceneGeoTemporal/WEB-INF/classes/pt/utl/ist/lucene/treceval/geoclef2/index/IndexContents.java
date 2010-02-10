package pt.utl.ist.lucene.treceval.geoclef2.index;

import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geoclef2.DocumentIterator;
import pt.utl.ist.lucene.treceval.geoclef2.GeoClefDocument;
import pt.utl.ist.lucene.treceval.geoclef2.ParagraphSpliter;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.utils.nlp.Paragraph;

import java.io.File;
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
public class IndexContents {

    public static String indexPath =  Config.indexBase + File.separator + "contents";
    public static String documentPath = Config.documentPath;

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {

        new File(indexPath).mkdir();
        DocumentIterator iterator = new DocumentIterator(documentPath,"");
        GeoClefDocument d;

        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.ID, new LgteNothingAnalyzer());
        anaMap.put(Config.CONTENTS, IndexCollections.en.getAnalyzerWithStemming());
        anaMap.put(Config.TITLE, IndexCollections.en.getAnalyzerWithStemming());

        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.BM25b);


        int i = 1;
        while((d = iterator.next())!=null)
        {

            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {
                System.out.println(i + ":" + d.getDocNO());
            }

        }
        writer.close();
    }

    private static void indexDocument(LgteIndexWriter writer, GeoClefDocument d) throws IOException, IllegalAccessException, InstantiationException {

        LgteDocumentWrapper doc = new LgteDocumentWrapper();
        doc.indexString(Globals.DOCUMENT_ID_FIELD,d.getDocNO());


        String text =  d.getSgmlWithoutTags();
        List<Paragraph> paragraphs = ParagraphSpliter.split(d, Paragraph.class);
        try{
            StringBuilder textIndexBuilder = new StringBuilder();
            if(d.getHeadline() != null)
            {
                doc.indexText(Config.TITLE,d.getHeadline());
                textIndexBuilder.append(d.getHeadline()).append(" ");
            }
            else
                System.out.println( d.getDocNO() + " don't have HeadLine ");

            if(paragraphs.size() > 0)
                textIndexBuilder.append(paragraphs.get(0).getPhrase()).append(" ");
            else
                System.out.println( d.getDocNO() + " don't have paragraphs ");

            if(text != null)
                textIndexBuilder.append(text);


            doc.indexText(Globals.LUCENE_DEFAULT_FIELD,textIndexBuilder.toString());
        }catch(Throwable e){
            System.out.println("Error:" + d.getDocNO() + " : " + e.toString());
            e.printStackTrace();
        }

        writer.addDocument(doc);
    }
}
