package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.exceptions.NotImplementedException;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 20:06:34
 * @email machadofisher@gmail.com
 */
public class IntegratedDocPlaceMakerAndTimexIterator {

    private static final Logger logger = Logger.getLogger(IntegratedDocPlaceMakerAndTimexIterator.class);

    String documentsPath;
    String timexesPath;
    String placeMakerPath;

   

    DocumentIterator documentIterator;
    TimexesIterator timexesIterator;
    PlaceMakerIterator placeMakerIterator;

    TimexesDocument nowTimexesDocument;
    NyTimesDocument nowNyTimesDocument;
    PlaceMakerDocument nowPlaceMakerDocument;

    public IntegratedDocPlaceMakerAndTimexIterator(String documentsPath, String timexesPath, String placeMakerPath) throws IOException {
        this.documentsPath = documentsPath;
        this.timexesPath = timexesPath;
        this.placeMakerPath = placeMakerPath;
        try {
            documentIterator = new DocumentIterator(documentsPath);
            nowNyTimesDocument = documentIterator.next();
            placeMakerIterator = new PlaceMakerIterator(placeMakerPath);
            try {
                nowPlaceMakerDocument = placeMakerIterator.next();
            } catch (DocumentException e) {
                logger.error(e,e);
            }
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

    public IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes next() throws IOException {
        if(hasNext())
        {
            IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes documentWithTimexes;
            if(nowTimexesDocument.getId().compareTo(nowNyTimesDocument.getDId()) < 0)
            {
                logger.fatal("Timex id is smaller than the now document id, probably timexes are not sorted as documents");
                return null;
            }
            if(nowTimexesDocument.getId().equals(nowNyTimesDocument.getDId()))
            {
                documentWithTimexes = new IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes(nowNyTimesDocument,nowTimexesDocument,nowPlaceMakerDocument);
                try {
                    nowNyTimesDocument = documentIterator.next();
                    nowTimexesDocument = timexesIterator.next();
                    try {
                        nowPlaceMakerDocument = placeMakerIterator.next();
                    } catch (DocumentException e)
                    {
                        logger.error(e,e);
                    }
                } catch (IOException e) {
                    throw e;
                }
            }
            else
            {
                documentWithTimexes = new IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes(nowNyTimesDocument,nowPlaceMakerDocument);
                try {
                    nowNyTimesDocument = documentIterator.next();
                    try {
                        nowPlaceMakerDocument = placeMakerIterator.next();
                    } catch (DocumentException e)
                    {
                        logger.error(e,e);
                    }
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

    public class DocumentWithPlacesAndTimexes
    {
        NyTimesDocument d;
        TimexesDocument td = null;
        PlaceMakerDocument pm = null;

        public DocumentWithPlacesAndTimexes(NyTimesDocument d, TimexesDocument td, PlaceMakerDocument pm)
        {
            this.d = d;
            this.td = td;
            this.pm = pm;
        }

        public DocumentWithPlacesAndTimexes(NyTimesDocument d, PlaceMakerDocument pm)
        {
            this.d = d;
            this.pm = pm;
        }

        public NyTimesDocument getD() {
            return d;
        }

        public TimexesDocument getTd() {
            return td;
        }

        public PlaceMakerDocument getPm() {
            return pm;
        }

        public boolean hasTimexes()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1;
        }

        public boolean hasPlaces()
        {
            //we are ignoring the article publication date
            return pm != null && pm.getPlaceDetails() != null && pm.getPlaceDetails().size() > 0;
        }

        public int countTimexes()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1)
            {
                return td.getTimex2TimeExpressions().size() - 1;
            }
            return 0;
        }

        public int countTimeExpressions()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 1)
            {
                return td.getAllTimeExpressions().size() - 1;
            }
            return 0;
        }

        public int countPlaces()
        {
            //we are ignoring the article publication date
            if(hasPlaces())
            {
                pm.getPlaceDetails().size();
            }
            return 0;
        }

        public int countPlaceRefs()
        {
            //we are ignoring the article publication date
            if(hasPlaces())
            {
                return pm.getAllRefs().size();
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
