package pt.utl.ist.lucene.treceval.geotime.index;

import pt.utl.ist.lucene.treceval.geotime.DocumentIterator;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.IndexCollections;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteBrokerStemAnalyzer;
import pt.utl.ist.lucene.analyzer.LgteWhiteSpacesAnalyzer;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.utils.nlp.Sentence;
import pt.utl.ist.lucene.utils.nlp.SentenceSpliter;
import pt.utl.ist.lucene.utils.LgteAnalyzerManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexSentences {

    public static String indexPath =  Config.indexBase + File.separator + "sentences";
    public static String documentPath = Config.documentPath;

    public static void main(String[] args) throws IOException {

        new File(indexPath).mkdir();
        DocumentIterator iterator = new DocumentIterator(documentPath);
        NyTimesDocument d;

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
                System.out.println("docs:" + i + " sentences: " + p + " :" + d.getDId());
            }

        }
        writer.close();
        System.out.println("docs:" + i + " sentences: " + p);
    }
    static int i = 0;
    static int p = 0;

    private static void indexDocument(LgteIndexWriter writer,  NyTimesDocument d) throws IOException
    {
        Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();


        List<Sentence> sentences = SentenceSpliter.split(d.getSgmlWithoutTags(), Sentence.class);


        try{
//            String title = null;
//            StringBuilder textIndexBuilder = new StringBuilder();

            //Stemming sentences
            for(Sentence s: sentences)
            {
                s.setPhrase(LgteAnalyzerManager.stemm(null,s.getPhrase(),analyzer));
            }

//            if(d.getDHeadline() != null)
//            {
//                //stemming title and adding to text field
//                title = LgteAnalyzerManager.stemm("",d.getDHeadline(),analyzer);
//                textIndexBuilder.append(title).append(" ");
//            }
//            else
//                System.out.println( d.getDId() + " don't have HeadLine ");

//            if(sentences.size() > 0)
//                textIndexBuilder.append(sentences.get(0).getPhrase()).append(" ");
//            else
//                System.out.println( d.getDId() + " don't have sentences ");
//
//            if(sentences.size() > 1)
//                textIndexBuilder.append(sentences.get(1).getPhrase()).append(" ");
////            else
////                System.out.println( d.getDId() + " only have one sentence ");
//
//            for(Sentence s: sentences)
//            {
//                textIndexBuilder.append(s.getPhrase()).append(" ");
//            }



            for(Sentence s: sentences)
            {
                LgteDocumentWrapper doc = new LgteDocumentWrapper();
//                System.out.println(i++);
//                System.out.println(d.getDId() + "$" + s.getIndex());
                doc.indexString(Config.ID,d.getDId() + "$$" + s.getIndex());
                doc.indexString(Config.DOC_ID,d.getDId());
//                if(title!=null)
//                    doc.indexText(Config.TITLE,title);
//                doc.addField(Config.CONTENTS,textIndexBuilder.toString(),false,true,true,true);
                doc.addField(Config.SENTENCES,s.getPhrase(),false,true,true,true);
                p++;
                writer.addDocument(doc);
            }
        }catch(Throwable e){
            System.out.println("Error:" + d.getDId() + " : " + e.toString());
            e.printStackTrace();
        }


    }
}
