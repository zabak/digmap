package pt.utl.ist.lucene.treceval.geoclef2.index;

import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.treceval.geoclef2.DocumentIterator;
import pt.utl.ist.lucene.treceval.geoclef2.GeoClefDocument;
import pt.utl.ist.lucene.treceval.geoclef2.ParagraphSpliter;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.utils.nlp.Paragraph;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;

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
public class IndexParagraphs {

    public static String indexPath = Config.indexBase + File.separator + "sentences";
    public static String documentPath = Config.documentPath;

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {

        new File(indexPath).mkdir();
        DocumentIterator iterator = new DocumentIterator(documentPath,"");
        GeoClefDocument d;

        Map<String, Analyzer> anaMap = new HashMap<String,Analyzer>();
        anaMap.put(Config.ID, new LgteNothingAnalyzer());
        anaMap.put(Config.DOC_ID, new LgteNothingAnalyzer());
//        anaMap.put(Config.CONTENTS, new LgteWhiteSpacesAnalyzer());  //because text will be stemmed below for performance purposes
        anaMap.put(Config.SENTENCES, new LgteWhiteSpacesAnalyzer());
//        anaMap.put(Config.TITLE, new LgteWhiteSpacesAnalyzer());

        LgteBrokerStemAnalyzer analyzer = new LgteBrokerStemAnalyzer(anaMap);

        LgteIndexWriter writer = new LgteIndexWriter(indexPath,analyzer, true, Model.BM25b);


        int i = 1;
        while((d = iterator.next())!=null)
        {

            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {
                System.out.println("docs:" + i + " paragraphs: " + p + " :" + d.getDocNO());
            }

        }
        writer.close();
        System.out.println("docs:" + i + " paragraphs: " + p);
    }
    static int i = 0;
    static int p = 0;

    private static void indexDocument(LgteIndexWriter writer,  GeoClefDocument d) throws IOException, IllegalAccessException, InstantiationException {
        Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();
        List<Paragraph> paragraphs = ParagraphSpliter.split(d, Paragraph.class);
        try
        {
            //Stemming sentences
            for(Paragraph s: paragraphs)
            {
                s.setPhrase(LgteAnalyzerManager.stemm(null,s.getPhrase(),analyzer));
            }

            for(Paragraph s: paragraphs)
            {
                LgteDocumentWrapper doc = new LgteDocumentWrapper();
                doc.indexString(Config.ID,d.getDocNO() + "$$" + s.getIndex());
                doc.indexString(Config.DOC_ID,d.getDocNO());
                doc.addField(Config.SENTENCES,s.getPhrase(),false,true,true,true);
                p++;
                writer.addDocument(doc);
            }
        }
        catch(Throwable e)
        {
            System.out.println("Error:" + d.getDocNO() + " : " + e.toString());
            e.printStackTrace();
        }


    }
}
