package pt.utl.ist.lucene.treceval.geoclef2.index;

import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteIndexWriter;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.analyzer.LgteNothingAnalyzer;
import pt.utl.ist.lucene.treceval.geoclef2.DocumentPlaceMakerParagraphs;
import pt.utl.ist.lucene.treceval.geoclef2.IntegratedDocPlaceMakerIterator;
import pt.utl.ist.lucene.treceval.geotime.index.Config;
import pt.utl.ist.lucene.utils.PlaceMakerParagraph;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 10/Dez/2009
 * @time 17:29:40
 * @email machadofisher@gmail.com
 */
public class IndexGeoScopeParagraphs {
    public static String indexPath =  pt.utl.ist.lucene.treceval.geotime.index.IndexGeoTime.indexPath + "_sentences";

    public static void main(String[] args) throws IOException {
        IntegratedDocPlaceMakerIterator iterator = new IntegratedDocPlaceMakerIterator(Config.documentPath,Config.placemakerPath);
        LgteIndexWriter writer = new LgteIndexWriter(indexPath,new LgteNothingAnalyzer(),true, Model.BM25b);
        IntegratedDocPlaceMakerIterator.DocumentWithPlaces d;
        while((d = iterator.next())!=null)
        {
            indexDocument(writer,d);
            i++ ;

            if(i % 1000 == 0)
            {
                System.out.println("docs:" + i + " paragraph: " + p + " :" + d.getD().getDocNO());
            }
        }
        writer.close();
        System.out.println("docs:" + i + " paragraph: " + p);
    }

    static int i = 0;
    static int p = 0;

    private static void indexDocument(LgteIndexWriter writer, IntegratedDocPlaceMakerIterator.DocumentWithPlaces document) throws IOException
    {
        DocumentPlaceMakerParagraphs documentPlaceMakerParagraphs = new DocumentPlaceMakerParagraphs(document.getD(),document.getPm());
        for(PlaceMakerParagraph paragraph: documentPlaceMakerParagraphs.getParagraphs())
        {
            LgteDocumentWrapper doc = new LgteDocumentWrapper();
            doc.indexString(Config.ID,document.getD().getDocNO() + "$$" + paragraph.getIndex());
            doc.indexString(Config.DOC_ID,document.getD().getDocNO());
            p++;

            if(paragraph.hasPlaces())
                doc.indexStringNoStore(Config.S_GEO_INDEXED +  "_" + Config.SENTENCES, "true");

            writer.addDocument(doc);
        }
    }
}
