package pt.utl.ist.lucene.versioning.tests;

import com.pjaol.search.geo.utils.InvalidGeoException;
import junit.framework.TestCase;
import org.apache.lucene.queryParser.ParseException;
import pt.utl.ist.lucene.Globals;
import pt.utl.ist.lucene.analyzer.LgteAnalyzer;
import pt.utl.ist.lucene.utils.Files;
import pt.utl.ist.lucene.utils.HighlightFormatter;
import pt.utl.ist.lucene.utils.TextToHTMLEnconder;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;

import java.io.IOException;

/**
 *
 * The objective of this class s help you to use Lgte Time Queries in a very quick example in a very simple example
 *
 * @author Jorge Machado
 * @date 2008
 *
 *
 */
public class TestHighlightVersion extends TestCase
{

    private String path = Globals.DATA_DIR + "/" + getClass().getName();



    protected void setUp() throws IOException
    {
    }


    protected void tearDown() throws Exception
    {
        Files.delDirsE(path);
    }

    public void testRange() throws IOException, InvalidGeoException
    {
        String text = "Jorge Machado and Bruno Martins several years ago at the high school and very far away in time from lucene";
        try
        {
           assertTrue(LuceneVersionFactory.getLuceneVersion()
                .highlight(new HighlightFormatter(),
                    new TextToHTMLEnconder(),
                    text,
                    "contents",
                    new LgteAnalyzer(),
                    "jorge martins",
                    50,
                    3,
                    " ... ").indexOf("<b><i>Jorge</i></b>")>=0);
        }
        catch (ParseException e)
        {
            fail(e.toString());
            e.printStackTrace();
        }
    }
}
