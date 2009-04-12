package nmaf.digmap.gis;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class GeographicPoint implements Serializable {
	public static String SECOND_SYMBOL_PATTERN = "\"";
	private String numberPattern = "\\s*(\\d+\\.?\\d*)\\s*"; 
	private Pattern[] dmsPatterns = new Pattern[] {
			Pattern.compile("([NSEW])"+numberPattern + ":" + numberPattern + "(:" + numberPattern + ")?"),
			Pattern.compile("([NSEW])"+numberPattern + "[°º]" + numberPattern + "'(" + numberPattern + "\")?"),
			Pattern.compile("([NSEW])"+numberPattern + "d" + numberPattern + "('" + numberPattern + "\")?")
	};
	
	private static final long serialVersionUID = 1;
	private double latitude;
	private double longitude;

	public GeographicPoint() {
		super();
	}

	public GeographicPoint(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}


	public GeographicPoint(String latitudeInDMS, String longitudeInDMS) {
		super();
		this.latitude = convertToDD(latitudeInDMS);
		this.longitude = convertToDD(longitudeInDMS);
	}
	
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	

	private double convertToDD(String coordinate) {
		Double ddValue = 0d;

		for (Pattern dmsPattern : dmsPatterns) {
			Matcher m = dmsPattern.matcher(coordinate);
			if (m.matches()) {
				ddValue += Double.valueOf( m.group(2) );
				if (m.group(3)!=null) {
					ddValue += Double.valueOf( m.group(3) ) / 60;
				}
				if (m.group(5)!=null) {
					ddValue += Double.valueOf( m.group(5) ) / 3600;
				}
				if(m.group(1).equals("W") || m.group(1).equals("S"))
					ddValue= -ddValue;
				break;
			}
		}
		
		return ddValue;
	}
	
	public String toDcmiPoint() {
		String stringValue = null;
		StringBuilder buffer = new StringBuilder();

		buffer.append("east=").append(longitude).append("; ");
		buffer.append("north=").append(latitude).append(";");
//		if (!StringUtils.isEmpty( coordinates.getUnits() )) {
//			buffer.append(" units=").append(coordinates.getUnits()).append(";");
//		}
//		if (!StringUtils.isEmpty( coordinates.getProjection() )) {
//			buffer.append(" projection=").append(coordinates.getProjection()).append(";");
//		}
		stringValue = buffer.toString();
		
		return stringValue;
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
			.append("latitude", this.latitude)
			.append("longtitude", this.longitude)
			.toString();
	}

//	/**
//	 * @see java.lang.Object#equals(Object)
//	 */
//	public boolean equals(Object object) {
//		if (!(object instanceof GeographicPoint)) {
//			return false;
//		}
//		GeographicPoint rho = (GeographicPoint) object;
//		return new EqualsBuilder()
//			.append(this.longitude, rho.longitude)
//			.append(this.latitude, rho.latitude)
//			.isEquals();
//	}
//
//	/**
//	 * @see java.lang.Object#hashCode()
//	 */
//	public int hashCode() {
//		return new HashCodeBuilder(1618981363, -1709719843)
//			.append(this.longitude)
//			.append(this.latitude)
//			.toHashCode();
//	}
}
