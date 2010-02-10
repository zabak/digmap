package pt.utl.ist.lucene.utils;

import pt.utl.ist.lucene.utils.nlp.Paragraph;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jorge Machado
 * @date 28/Dez/2009
 * @time 16:57:08
 * @email machadofisher@gmail.com
 */
public class PlaceMakerParagraph extends Paragraph {

    List<PlaceMakerDocument.PlaceRef> placeRefs;


    public PlaceMakerParagraph() {
        this.placeRefs = new ArrayList<PlaceMakerDocument.PlaceRef>();
    }

    public PlaceMakerParagraph(int index)
    {
        super(index);
    }

    public PlaceMakerParagraph(int index, List<PlaceMakerDocument.PlaceRef> placeRefs)
    {
        super(index);
        if(placeRefs == null)
            this.placeRefs = new ArrayList<PlaceMakerDocument.PlaceRef>();
        else
            this.placeRefs = placeRefs;
    }

    public List<PlaceMakerDocument.PlaceRef> getPlaceRefs() {
        return placeRefs;
    }

    public void setPlaceRefs(List<PlaceMakerDocument.PlaceRef> placeRefs) {
        this.placeRefs = placeRefs;
    }



    public String toString()
    {
        String txt = super.toString();
        txt+="\nPlaceExpressions [\n";
        if(placeRefs!=null)
        {
            String catToken = "";
            for(PlaceMakerDocument.PlaceRef placeRef: placeRefs)
            {
                txt+= catToken + placeRef.getPlaceDetails().getName() + ",";
            }
        }
        txt+="\n]";
        return txt;
    }

    public int countPlaces()
    {
        Map<PlaceMakerDocument.PlaceDetails,Boolean> places = new HashMap<PlaceMakerDocument.PlaceDetails,Boolean>();
        for(PlaceMakerDocument.PlaceRef placeRef: placeRefs)
        {
            places.put(placeRef.getPlaceDetails(),true);
        }
        return places.size();
    }

    public int countPlaceRefs()
    {
        return placeRefs.size();
    }
    
    public boolean hasPlaces()
    {
        return getPlaceRefs().size() > 0;
    }


}
