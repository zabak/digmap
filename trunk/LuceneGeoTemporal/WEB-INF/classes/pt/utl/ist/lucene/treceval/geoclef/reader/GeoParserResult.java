package pt.utl.ist.lucene.treceval.geoclef.reader;

import pt.utl.ist.lucene.forms.UnknownForm;

import java.util.List;

/**
 * @author Jorge Machado
 * @date 10/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef.reader
 */
public class GeoParserResult
{
    private String docNo;

    public UnknownForm genericUnknownForm = null;
    
    public List<String> places;

    public String getDocNo()
    {
        return docNo;
    }

    public void setDocNo(String docNo)
    {
        this.docNo = docNo;
    }

    public UnknownForm getGenericUnknownForm()
    {
        return genericUnknownForm;
    }

    public void setGenericUnknownForm(UnknownForm genericUnknownForm)
    {
        this.genericUnknownForm = genericUnknownForm;
    }

    public List<String> getPlaces()
    {
        return places;
    }

    public void setPlaces(List<String> places)
    {
        this.places = places;
    }
}
