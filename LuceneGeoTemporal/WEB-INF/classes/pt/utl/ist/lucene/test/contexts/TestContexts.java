package pt.utl.ist.lucene.test.contexts;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.context.Context;
import pt.utl.ist.lucene.context.ContextNode;
import pt.utl.ist.lucene.utils.Files;

import java.io.IOException;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestContexts extends TestCase
{

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException, ParseException
    {
        LgteContextIndexWriter writer = new LgteContextIndexWriter(path,true, Model.LanguageModel);
        Context context = new Context(Globals.DOCUMENT_ID_FIELD);


        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents","Jorge Machado personal page");
        ContextNode topNode = context.createTopNode("1");

        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
        ContextNode node2 = context.createInternalNode("2");
        context.addBiDirectionalLink(topNode,node2);

        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
        doc3.indexText("contents","Experimental Results For Language Model LGTE");
        ContextNode node3 = context.createInternalNode("3");
        context.addBiDirectionalLink(node2,node3);

        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
        doc4.indexText("contents","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
        ContextNode node4 = context.createInternalNode("4");
        context.addBiDirectionalLink(topNode,node4);





//        doc1.indexText("contents","Jorge Machado personal page LGTE: Lucene Geo-Temporal Extensions Framework Experimental Results For Language Model Curriculum Master Thesis Universidade Tecnica de Lisboa");
//
//        doc1.indexText("contents1","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
//        doc1.indexText("contents2","Experimental Results For Language Model LGTE");
//        doc1.indexText("contents1","Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc1.indexText("contents$","1");
//        doc1.indexText("contents$","2");
//        doc1.indexText("contents$","3");
//        doc1.indexText("contents$","4");
//        doc1.indexText("contents$out","2");
//        doc1.indexText("contents$out","4");
//        doc1.storeUtokenized("contents$top0","true");
        //doc1.addGeoPointField(38.788440, -9.171290);
//
//        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
//        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
//        doc2.indexText("contents","Jorge Machado personal page LGTE: Lucene Geo-Temporal Extensions Framework Experimental Results For Language Model Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc2.indexText("contents1","Jorge Machado personal page");
//        doc2.indexText("contents0","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
//        doc2.indexText("contents1","Experimental Results For Language Model LGTE");
//        doc2.indexText("contents2","Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc2.indexText("contents$","1");
//        doc2.indexText("contents$","2");
//        doc2.indexText("contents$","3");
//        doc2.indexText("contents$","4");
//        doc2.indexText("contents$in","1");
//        doc2.indexText("contents$out","3");
//        doc2.storeUtokenized("contents$top1","true");
//
//        LgteDocumentWrapper doc3 = new LgteDocumentWrapper();
//        doc3.indexText(Globals.DOCUMENT_ID_FIELD, "3");
//        doc3.indexText("contents","Jorge Machado personal page LGTE LGTE: Lucene Geo-Temporal Extensions Framework Experimental Results For Language Model Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc3.indexText("contents2","Jorge Machado personal page");
//        doc3.indexText("contents1","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
//        doc3.indexText("contents0","Experimental Results For Language Model LGTE");
//        doc3.indexText("contents3","Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc3.indexText("contents$","1");
//        doc3.indexText("contents$","2");
//        doc3.indexText("contents$","3");
//        doc3.indexText("contents$","4");
//        doc3.indexText("contents$in","2");
//        doc3.storeUtokenized("contents$top2","true");
//
//
//        LgteDocumentWrapper doc4 = new LgteDocumentWrapper();
//        doc4.indexText(Globals.DOCUMENT_ID_FIELD, "4");
//        doc4.indexText("contents","Jorge Machado personal page LGTE: Lucene Geo-Temporal Extensions Framework Experimental Results For Language Model Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc4.indexText("contents1","Jorge Machado personal page");
//        doc4.indexText("contents2","LGTE LGTE: Lucene Geo-Temporal Extensions Framework");
//        doc4.indexText("contents3","Experimental Results For Language Model LGTE");
//        doc4.indexText("contents0","Curriculum Master Thesis Universidade Tecnica de Lisboa");
//        doc4.indexText("contents$","1");
//        doc4.indexText("contents$","2");
//        doc4.indexText("contents$","3");
//        doc4.indexText("contents$","4");
//        doc4.indexText("contents$in","1");
//        doc4.storeUtokenized("contents$top1","true");


        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.addDocument(doc3);
        writer.addDocument(doc4);

        writer.addContext(context);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
//        Files.delDirsE(path);
//        Files.delDirsE(path + LgteContextIndexWriter.CONTEXT_RELATIVE_PATH);
//        Files.delDirsE(path + LgteContextIndexWriter.DOCUMENTS_CONTEXT_RELATIVE_PATH);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.LanguageModel, path);
        
        try
        {
            LgteHits hits = searcher.search("jorge lgte");
            for(int i = 0; i < hits.length();i++)
                System.out.println(hits.doc(i).get("id"));
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }

    private void printQuery(String query,LgteIndexSearcherWrapper searcherWrapper) throws IOException, ParseException

    {
        printQuery(query,searcherWrapper,false);
    }

    private void printQuery(String query,LgteIndexSearcherWrapper searcherWrapper,boolean ignore3) throws IOException, ParseException
    {
//        LgteHits lgteHits = searcherWrapper.search(query);
//        System.out.println(query);
//        System.out.println(lgteHits.doc(0).get("id") + " - " + lgteHits.score(0));
//        System.out.println(lgteHits.doc(1).get("id") + " - " + lgteHits.score(1));
//        if(!ignore3)
//            System.out.println(lgteHits.doc(2).get("id") + " - " + lgteHits.score(2));
    }
}
