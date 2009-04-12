package nmaf.digmap.gis;

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

public class GazeteerPlace {
	String name;
	String namePrefered;
	ArrayList<GazeteerFeature> features;
	GazeteerFeature mainFeature;
	
	
	public GazeteerPlace(String entryID, String placeName, Document d, int topFeaturesToGet) {
		this.name=placeName;
		features=new ArrayList<GazeteerFeature>();
        for(Iterator<Element> it2=d.getRootElement().element("EntryCollection").elementIterator("GazetteerEntry"); it2.hasNext(); ) {
        	Element idEl=it2.next();
        	if(idEl.attributeValue("entryID").equals(entryID)) {
        		GazeteerFeature f=new GazeteerFeature(idEl.attributeValue("id"));
        		if(mainFeature==null) {
        			namePrefered=idEl.attributeValue("name");
        			mainFeature=f;
        		}
        		String coordinates=getCoordinates(idEl);
        		f.setCoordinates(coordinates);
        		features.add(f);
        		if (features.size() >= topFeaturesToGet)
        			break;
        	}
        }
	}

	
	public GazeteerPlace(String name, String namePrefered, GazeteerFeature feature) {
		super();
		this.name = name;
		this.namePrefered = namePrefered;
		this.features = new ArrayList<GazeteerFeature>();
		features.add(feature);
	}

	
	
	public GazeteerPlace(String name, String namePrefered,
			ArrayList<GazeteerFeature> features) {
		super();
		this.name = name;
		this.namePrefered = namePrefered;
		this.features = features;
	}


	public String getDcCoordinates() {
		return mainFeature==null ? null : mainFeature.getDcCoordinates();
	}
	
	public String getGazeteerId() {
		return mainFeature==null ? null : mainFeature.getId();
	}
	
	public boolean isPointCoordinates() {
		return mainFeature==null ? null : mainFeature.isPointCoordinates();
	}
	
	
	public boolean equals(Object arg0) {
    	return name.equals(arg0);
    }
	
	
	
    
    @Override
    public int hashCode() {
    	return name.hashCode();
    }
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamePrefered() {
		return namePrefered;
	}
	public void setNamePrefered(String namePrefered) {
		this.namePrefered = namePrefered;
	}
	public ArrayList<GazeteerFeature> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<GazeteerFeature> features) {
		this.features = features;
	}
	
	
	public static String getCoordinates(Element el) {
		if(el.getName().equals("coordinates")) {
			return el.getTextTrim();
		}
        for(Iterator<Element> it2=el.elementIterator(); it2.hasNext(); ) {
        	Element subEl=it2.next();
        	String c=getCoordinates(subEl);
        	if (c!=null) 
        		return c;
        }
        return null;
	}


	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "GazeteerPlace ( "
	        + "name = " + this.name + TAB
	        + "namePrefered = " + this.namePrefered + TAB
	        + "features = " + this.features + TAB
	        + "mainFeature = " + this.mainFeature + TAB
	        + " )";
	
	    return retValue;
	}
	
	
}
