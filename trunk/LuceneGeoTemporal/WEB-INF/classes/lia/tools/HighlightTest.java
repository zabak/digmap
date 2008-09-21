package lia.tools;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class HighlightTest extends TestCase {
  public void testHighlighting() throws Exception {
    String text = "The quick brown fox jumps over the lazy dog";

    TermQuery query = new TermQuery(new Term("field", "fox"));
    Scorer scorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(scorer);

    TokenStream tokenStream =
        new SimpleAnalyzer().tokenStream("field",
            new StringReader(text));

    assertEquals("The quick brown <B>fox</B> jumps over the lazy dog",
        highlighter.getBestFragment(tokenStream, text));
  }

  public void testHits() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);

    TermQuery query = new TermQuery(new Term("title", "action"));
    Hits hits = searcher.search(query);

    QueryScorer scorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(scorer);

    for (int i = 0; i < hits.length(); i++) {
      String title = hits.doc(i).get("title");

      TokenStream stream =
          new SimpleAnalyzer().tokenStream("title",
              new StringReader(title));
      String fragment =
          highlighter.getBestFragment(stream, title);

      System.out.println(fragment);
    }
  }
  
  private String indexDir = System.getProperty("index.dir");
  protected Directory directory;

  protected void setUp() throws Exception {
    directory = FSDirectory.getDirectory(indexDir, false);
  }

  protected void tearDown() throws Exception {
    directory.close();
  }

  /**
   * For troubleshooting
   */
  protected final void dumpHits(Hits hits) throws IOException {
    if (hits.length() == 0) {
      System.out.println("No hits");
    }

    for (int i=0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      System.out.println(hits.score(i) + ":" + doc.get("title"));
    }
  }

  protected final void assertHitsIncludeTitle(
                                          Hits hits, String title)
    throws IOException {
    for (int i=0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      if (title.equals(doc.get("title"))) {
        assertTrue(true);
        return;
      }
    }

    fail("title '" + title + "' not found");
  }

  protected final Date parseDate(String s) throws ParseException {
      return new SimpleDateFormat("yyyy-MM-dd").parse(s);
  }


}
