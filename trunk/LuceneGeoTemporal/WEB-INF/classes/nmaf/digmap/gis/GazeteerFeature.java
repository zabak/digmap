package nmaf.digmap.gis;

public class GazeteerFeature {
	String id;
//	String name;
	String coordinates;

	public GazeteerFeature(String id) {
		super();
		this.id = id;
	}	

	public boolean isPointCoordinates() {
		return !coordinates.contains(" ");
	}

	public String getDcCoordinates() {
		if (isPointCoordinates()) {
			String[] points=coordinates.split(",");
			return new GeographicPoint(Double.parseDouble(points[0]), Double.parseDouble(points[1])).toDcmiPoint();
		}
		String[] points=coordinates.split(" ");
		
		double maxLong=Long.MIN_VALUE;;
		double minLong=Long.MAX_VALUE;
		double maxLat=Long.MIN_VALUE;;
		double minLat=Long.MAX_VALUE;
		
		for(String p: points) {
			String[] coords=p.split(",");
			double lng=Double.parseDouble(coords[1]);
			double lat=Double.parseDouble(coords[0]);
			
			maxLong=Math.max(maxLong, lng);
			minLong=Math.min(minLong, lng);
			maxLat=Math.max(maxLat, lat);
			minLat=Math.min(minLat, lat);
		}

		GeographicPoint topRight=new GeographicPoint(maxLat, maxLong);
		GeographicPoint bottomLeft=new GeographicPoint(minLat, minLong);

		return new Coordinates(topRight, bottomLeft).toDcmiBox();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getCoordinates() {
//		return coordinates;
//	}
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
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
	    
	    retValue = "GazeteerFeature ( "
	        + "id = " + this.id + TAB
	        + "coordinates = " + this.coordinates + TAB
	        + " )";
	
	    return retValue;
	}
	
	
}
