package digmapFrbr.webLgteFrbr;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.lucene.search.Hits;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.IOException;

import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteHits;
import pt.utl.ist.lucene.utils.XmlUtils;

/**
 * @author Jorge Machado
 * @date 21/Nov/2009
 * @time 20:15:10
 * @email machadofisher@gmail.com
 */
public class SearchRequestHandler implements RequestEntity
{
    private static final Logger logger = Logger.getLogger(SearchRequestHandler.class);
    int show;
    LgteHits hits;
    int startResult;
    int page;
    String query;

    public SearchRequestHandler(LgteHits hits, int startResult, int show, int page, String query)
    {
        this.startResult = startResult;
        this.hits = hits;
        this.show = show;
        this.page = page;
        this.query = query;
    }


    public boolean isRepeatable() {
        return false;
    }

    public void writeRequest(OutputStream outputStream) throws IOException
    {
        outputStream.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").getBytes());
        outputStream.write(("<lgte>").getBytes());
        outputStream.write(("<request query=\"" + XmlUtils.escape(query) + "\"/>").getBytes());
        outputStream.write(("<response>").getBytes());

        outputStream.write(("<results totalResults=\"" + hits.length() + "\" showing=\""+show+"\" page=\"" + page + "\">").getBytes());
        for (int i = startResult; i < hits.length(); i++)
        {
            LgteDocumentWrapper doc = hits.doc(i);
            String docno = doc.get(pt.utl.ist.lucene.treceval.Globals.DOCUMENT_ID_FIELD);
            try{
                outputStream.write(("<record catalog=\"" + doc.get("collection") + "\" score=\"" + hits.score(i) + "\" id=\"" + docno + "\"/>").getBytes());
                outputStream.flush();
            }catch(IOException e)
            {
                logger.info(e,e);
                logger.info("Possible connection closed by peer at result " + i);
                break;
            }
        }
        outputStream.write(("</results>").getBytes());
        outputStream.write(("</response>").getBytes());
        outputStream.write(("</lgte>").getBytes());
        outputStream.flush();

    }

    public long getContentLength()
    {
        return -1;
    }

    public String getContentType() {
        return "text/xml";
    }
}
