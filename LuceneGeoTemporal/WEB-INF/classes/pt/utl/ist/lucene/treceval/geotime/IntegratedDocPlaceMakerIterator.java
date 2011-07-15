package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.exceptions.NotImplementedException;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerIterator;

import java.io.IOException;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 20:06:34
 * @email machadofisher@gmail.com
 */
public class IntegratedDocPlaceMakerIterator {

    private static final Logger logger = Logger.getLogger(IntegratedDocPlaceMakerIterator.class);

    String documentsPath;
    String placeMakerPath;



    DocumentIterator documentIterator;
    PlaceMakerIterator placeMakerIterator;

    NyTimesDocument nowNyTimesDocument;
    PlaceMakerDocument nowPlaceMakerDocument;

    public IntegratedDocPlaceMakerIterator(String documentsPath, String placeMakerPath) throws IOException {
        this.documentsPath = documentsPath;
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


    }


    public boolean hasNext()
    {
        return nowNyTimesDocument != null;

    }

    public static int i = 0;
    public static int j = 0;
    public static int w = 0;
    public static int z = 0;

    public IntegratedDocPlaceMakerIterator.DocumentWithPlaces next() throws IOException {
        IntegratedDocPlaceMakerIterator.DocumentWithPlaces documentWithPlaces = null;
        i++;
        if(hasNext())
        {
            j++;

            System.out.println("DOC:" + nowNyTimesDocument.getDId() + ", G:" + nowPlaceMakerDocument.getDocId());
            if(nowPlaceMakerDocument.getDocId().compareTo(nowNyTimesDocument.getDId()) < 0)
            {
                logger.fatal("PlaceMaker doc id is smaller than the now document id, probably places are not sorted as documents : PlaceMaker:" + nowPlaceMakerDocument.getDocId() + " Document:" + nowNyTimesDocument.getDId());
                return null;
            }
            if(nowPlaceMakerDocument.getDocId().equals(nowNyTimesDocument.getDId()))
            {
                w++;
                documentWithPlaces = new IntegratedDocPlaceMakerIterator.DocumentWithPlaces(nowNyTimesDocument,nowPlaceMakerDocument);
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
            else
            {
                z++;
                documentWithPlaces = new DocumentWithPlaces(nowNyTimesDocument);
                System.out.println("Document: " + nowNyTimesDocument.getDId() + " has no PlaceMakerDocument ");
                try
                {
                    nowNyTimesDocument = documentIterator.next();
                }
                catch (IOException e)
                {
                    throw e;
                }
            }
        }

        return documentWithPlaces;
    }

    public void remove() {
        throw new NotImplementedException("This iterator just reads the collection documents and timexes anotations");
    }

    public class DocumentWithPlaces
    {
        NyTimesDocument d;
        PlaceMakerDocument pm = null;

        public DocumentWithPlaces(NyTimesDocument d, PlaceMakerDocument pm)
        {
            this.d = d;
            this.pm = pm;
        }

        public DocumentWithPlaces(NyTimesDocument d)
        {
            this.d = d;
            this.pm = pm;
        }


        public NyTimesDocument getD() {
            return d;
        }


        public PlaceMakerDocument getPm() {
            return pm;
        }

        public boolean hasPlaces()
        {
            //we are ignoring the article publication date
            return pm != null && pm.getPlaceDetails() != null && pm.getPlaceDetails().size() > 0;
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

    }


    public static void main(String[] args) throws IOException
    {

        IntegratedDocPlaceMakerIterator iterator = new IntegratedDocPlaceMakerIterator("D:\\Servidores\\DATA\\ntcir\\TESTES\\docs","D:\\Servidores\\DATA\\ntcir\\TESTES\\placemaker");
        while(iterator.hasNext())
        {
            DocumentWithPlaces dc = iterator.next();
            System.out.println(dc.getD().getDId());
        }
    }

}
