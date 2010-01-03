package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import pt.utl.ist.lucene.exceptions.NotImplementedException;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 20:06:34
 * @email machadofisher@gmail.com
 */
public class IntegratedDocTimexIterator {

    private static final Logger logger = Logger.getLogger(IntegratedDocTimexIterator.class);

    String documentsPath;
    String timexesPath;

    DocumentIterator documentIterator;
    TimexesIterator timexesIterator;

    TimexesDocument nowTimexesDocument;
    NyTimesDocument nowNyTimesDocument;

    public IntegratedDocTimexIterator(String documentsPath, String timexesPath) throws IOException {
        this.documentsPath = documentsPath;
        this.timexesPath = timexesPath;
        try {
            documentIterator = new DocumentIterator(documentsPath);
            nowNyTimesDocument = documentIterator.next();
        } catch (IOException e) {
            throw e;
        }
        timexesIterator = new TimexesIterator(timexesPath);
        nowTimexesDocument = timexesIterator.next();
    }


    public boolean hasNext()
    {
        return nowNyTimesDocument != null;
    }

    public DocumentWithTimexes next() throws IOException {
        if(hasNext())
        {
            DocumentWithTimexes documentWithTimexes;
            if(nowTimexesDocument.getId().compareTo(nowNyTimesDocument.getDId()) < 0)
            {
                logger.fatal("Timex id is smaller than the now document id, probably timexes are not sorted as documents");
                return null;
            }
            if(nowTimexesDocument.getId().equals(nowNyTimesDocument.getDId()))
            {
                documentWithTimexes = new DocumentWithTimexes(nowNyTimesDocument,nowTimexesDocument);
                try {
                    nowNyTimesDocument = documentIterator.next();
                    nowTimexesDocument = timexesIterator.next();
                } catch (IOException e) {
                    throw e;
                }
            }
            else
            {
                documentWithTimexes = new DocumentWithTimexes(nowNyTimesDocument);
                try {
                    nowNyTimesDocument = documentIterator.next();
                } catch (IOException e) {
                    throw e;
                }
            }
            return documentWithTimexes;
        }
        return null;
    }

    public void remove() {
        throw new NotImplementedException("This iterator just reads the collection documents and timexes anotations");
    }

    public class DocumentWithTimexes
    {
        NyTimesDocument d;
        TimexesDocument td = null;

        public DocumentWithTimexes(NyTimesDocument d, TimexesDocument td)
        {
            this.d = d;
            this.td = td;
        }

        public DocumentWithTimexes(NyTimesDocument d)
        {
            this.d = d;
        }

        public NyTimesDocument getD() {
            return d;
        }

        public TimexesDocument getTd() {
            return td;
        }

        public boolean hasTimexes()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0;
        }

        public int countTimexes()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0)
            {
                return td.getTimex2TimeExpressions().size();
            }
            return 0;
        }

        public int countTimeExpressions()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0)
            {
                return td.getAllTimeExpressions().size();
            }
            return 0;
        }

        public int countInvalidTimeExpressions()
        {
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0)
            {
                return td.getAllInvalidTimeExpressions().size();
            }
            return 0;
        }
    }
}
