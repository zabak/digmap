package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.exceptions.NotImplementedException;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesDocument;
import pt.utl.ist.lucene.utils.temporal.tides.TimexesIterator;

import java.io.IOException;

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
                logger.fatal("Timex id is smaller than the now document id, probably timexes are not sorted as documents: timexes:"  + nowTimexesDocument.getId() + " Document:" + nowNyTimesDocument.getDId());
                return null;
            }

            if(nowPlaceMakerDocument.getDocId().compareTo(nowNyTimesDocument.getDId()) < 0)
            {
                logger.fatal("PlaceMaker id is smaller than the now document id, probably PlaceMaker are not sorted as documents : PlaceMaker:" + nowPlaceMakerDocument.getDocId() + " Document:" + nowNyTimesDocument.getDId());
                return null;
            }

            System.out.println("DOC:" + nowNyTimesDocument.getDId() + ", G:" + nowPlaceMakerDocument.getDocId() + ", T:" + nowTimexesDocument.getId());

            NyTimesDocument dToAdd = nowNyTimesDocument;
            PlaceMakerDocument pmToAdd = null;
            TimexesDocument tmToAdd = null;

            //Exists allays is the reference collection


            if(nowTimexesDocument.getId().equals(nowNyTimesDocument.getDId()))
            {
                try
                {
                    tmToAdd = nowTimexesDocument;
                    nowTimexesDocument = timexesIterator.next();
                }
                catch (IOException e) {
                    throw e;
                }
            }
            else{
                System.out.println("Document" + dToAdd.getDId() + " ID NOT SINCRONIZED has no timexesDocument: " + nowTimexesDocument.getId());}

            if(nowPlaceMakerDocument.getDocId().equals(nowNyTimesDocument.getDId()))
            {
                try {
                    pmToAdd = nowPlaceMakerDocument;
                    nowPlaceMakerDocument = placeMakerIterator.next();
                } catch (DocumentException e)
                {
                    logger.error(e,e);
                }
            }
            else{
                System.out.println("Document" + dToAdd.getDId() + " has o placesDocument");}

            nowNyTimesDocument = documentIterator.next();
            documentWithTimexes = new IntegratedDocPlaceMakerAndTimexIterator.DocumentWithPlacesAndTimexes(dToAdd,tmToAdd,pmToAdd);

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
            return td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0;
        }

        public boolean hasIndexableTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllIndexableTimeExpressions().size() > 0;
        }

        public boolean hasPointTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllPointsTimeExpressions().size() > 0;
        }

        public boolean hasKeyPointTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllKeyPointsTimeExpressions().size() > 0;
        }

        public boolean hasRelativePointTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllRelativePointsTimeExpressions().size() > 0;
        }

        public boolean hasKeyTimeExpressions(TimeExpression.Type type)
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllKeyTimeExpressions(type).size() > 0;
        }

        public boolean hasYTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.Y).size() > 0;
        }
        public boolean hasYYTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.YY).size() > 0;
        }
        public boolean hasYYYTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.YYY).size() > 0;
        }
        public boolean hasYYYYTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.YYYY).size() > 0;
        }
        public boolean hasYYYYMMTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.YYYYMM).size() > 0;
        }
        public boolean hasYYYYMMDDTimeExpressions()
        {
            //we are ignoring the article publication date
            return td != null && td.getTimex2TimeExpressions() != null && td.getAllTimeExpressions(TimeExpression.Type.YYYYMMDD).size() > 0;
        }



        public boolean hasPlaces()
        {
            //we are ignoring the article publication date
            return pm != null && pm.getPlaceDetails() != null && pm.getPlaceDetails().size() > 0;
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

        public int countIndexableTimeExpressions()
        {
            //we are ignoring the article publication date
            if(td != null && td.getTimex2TimeExpressions() != null && td.getTimex2TimeExpressions().size() > 0)
            {
                return td.getAllIndexableTimeExpressions().size();
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


    public static void main(String[]args) throws IOException
    {
        IntegratedDocPlaceMakerAndTimexIterator iterator = new IntegratedDocPlaceMakerAndTimexIterator("D:\\Servidores\\DATA\\ntcir\\data","D:\\Servidores\\DATA\\ntcir\\TIMEXTAG","D:\\Servidores\\DATA\\ntcir\\PlaceMaker");
        DocumentWithPlacesAndTimexes documentWithPlacesAndTimexes;
        String last = "";
        while((documentWithPlacesAndTimexes = iterator.next())!=null)
        {
                        System.out.println("DOC:" + documentWithPlacesAndTimexes.getD().getDId() + ", G:" + documentWithPlacesAndTimexes.getPm().getDocId() + ", T:" + documentWithPlacesAndTimexes.getTd().getId());
              if(documentWithPlacesAndTimexes.getPm() == null)
                  System.out.println("PM = null " + documentWithPlacesAndTimexes.getD().getDId());
            if(documentWithPlacesAndTimexes.getTd() == null)
                  System.out.println("TD = null " + documentWithPlacesAndTimexes.getD().getDId());
        }

    }
}
