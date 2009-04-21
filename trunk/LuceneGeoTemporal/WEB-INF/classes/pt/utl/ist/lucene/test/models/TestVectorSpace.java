package pt.utl.ist.lucene.test.models;

import junit.framework.TestCase;
import pt.utl.ist.lucene.*;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.analyzer.LgteStemAnalyzer;

import java.io.IOException;

import com.pjaol.search.geo.utils.InvalidGeoException;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * The objective of this class s help you to use Lgte in a very quick example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestVectorSpace extends TestCase
{

    /**
     * You can use the diferent Probabilistic Models creating the index just once with any one of the probabilist models
     *
     */
    private String path = Globals.INDEX_DIR + "/" + getClass().getName();


    protected void setUp() throws IOException
    {
        LgteIndexWriter writer = new LgteIndexWriter(path,true);

        LgteDocumentWrapper doc1 = new LgteDocumentWrapper();
        doc1.indexText(Globals.DOCUMENT_ID_FIELD, "1");
        doc1.indexText("contents","word1 word2 word3 word32 word45 word56 word67 word67 word67 word88 word99 word99 word33");
        doc1.indexText("contents2","word1 word2 word3 word34");


        LgteDocumentWrapper doc2 = new LgteDocumentWrapper();
        doc2.indexText(Globals.DOCUMENT_ID_FIELD, "2");
        doc2.indexText("contents","word2 word3 word4 word55 word96 word54 word33 wordss");
        doc2.indexText("contents2","word2 word3 word4 word35 word45");

        writer.addDocument(doc1);
        writer.addDocument(doc2);

        writer.close();
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        LgteIndexSearcherWrapper searcher = new LgteIndexSearcherWrapper(Model.VectorSpaceModel, path);

        try
        {
            System.setProperty("lgte.score.fields.query.independent","true");

            LgteHits lgteHits = searcher.search("contents:word1^0.3 contents:word2^0.4 contents2:word3^0.7");
            System.out.println("doc:" + lgteHits.id(0) + ":"  + lgteHits.score(0));
            System.out.println("doc:" + lgteHits.id(1) + ":" + lgteHits.score(1));
        }
        catch (ParseException e)
        {
            fail(e.toString());
        }
        searcher.close();
    }



}
