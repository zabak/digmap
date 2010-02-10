package pt.utl.ist.lucene.treceval.geoclef2;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.utils.PlaceMakerAndTemporalSentence;
import pt.utl.ist.lucene.utils.PlaceMakerParagraph;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import pt.utl.ist.lucene.utils.temporal.TimeExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:54:03
 * @email machadofisher@gmail.com
 */
public class DocumentPlaceMakerParagraphs {

    private static Logger logger = Logger.getLogger(DocumentPlaceMakerParagraphs.class);

    GeoClefDocument document;
    String text;
    List<PlaceMakerParagraph> placemakerParagraphs;

    PlaceMakerDocument placeMakerDocument;


    /**
     * Creates a List of sentences with time expressions
     * @param document
     */
    public DocumentPlaceMakerParagraphs(GeoClefDocument document,  PlaceMakerDocument placeMakerDocument)
    {
        init(document,placeMakerDocument);
    }

    public DocumentPlaceMakerParagraphs(GeoClefDocument document,  String placeMakerDocumentXml) throws DocumentException {

        PlaceMakerDocument placeMakerDocument = new PlaceMakerDocument(placeMakerDocumentXml);
        init(document,placeMakerDocument);
    }



    public void init(GeoClefDocument document,   PlaceMakerDocument placeMakerDocument)
    {
        this.document = document;
        this.text = document.getSgmlWithoutTags();
        placemakerParagraphs = ParagraphSpliter.split(document, PlaceMakerParagraph.class);
        this.placeMakerDocument = placeMakerDocument;


        PlaceMakerParagraph sentence0 = placemakerParagraphs.get(0);
        int indexOfLineFeedFirstLine = sentence0.getPhrase().indexOf("\n");

        //Lets see if first line start with a DATE
        if(sentence0.getPhrase().substring(0,indexOfLineFeedFirstLine).matches("[0-9]{4,4}-[0-9]{1,2}-[0-9]{1,2}"))
        {
            sentence0.setPhrase(sentence0.getPhrase().substring(indexOfLineFeedFirstLine));
        }

        if(placeMakerDocument != null && placeMakerDocument.getPlaceDetails() != null && placeMakerDocument.getPlaceDetails().size() > 0)
        {
            for(PlaceMakerDocument.PlaceDetails placeDetails : placeMakerDocument.getPlaceDetails())
            {
                if(placeDetails.getRefs() != null)
                {
                    for(PlaceMakerDocument.PlaceRef placeRef : placeDetails.getRefs())
                    {
                        int startOffset = placeRef.getStartOffset();
                        //todo meter as place nas geotemporalsentences
                        for(PlaceMakerParagraph placeMakerParagraph : placemakerParagraphs)
                        {
                            if(startOffset >= placeMakerParagraph.getStartOffset() && startOffset < placeMakerParagraph.getEndOffset())
                            {
                                placeMakerParagraph.getPlaceRefs().add(placeRef);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public List<PlaceMakerParagraph> getParagraphs()
    {
        return placemakerParagraphs;
    }


    public GeoClefDocument getDocument() {
        return document;
    }

    public String getText() {
        return text;
    }

    public String toString()
    {
        String txt = "\n";
        for(PlaceMakerParagraph s: getParagraphs())
        {
            txt += s.getPhrase();
        }
        return txt;
    }
}
