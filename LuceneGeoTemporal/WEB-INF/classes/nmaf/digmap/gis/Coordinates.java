package nmaf.digmap.gis;

import java.io.Serializable;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//@Embeddable
/**
 * @see CoordinatesType
 */
public class Coordinates implements Serializable  {
	private static final long serialVersionUID = 1;
	private GeographicPoint topRight;
	private GeographicPoint bottomLeft;
	private String units;
	private String projection;
	private String type;
	
	public Coordinates() {
		super();
	}
	
	public Coordinates(GeographicPoint topRight, GeographicPoint bottomLeft) {
		super();
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
	}

	public Coordinates(GeographicPoint topRight, GeographicPoint bottomLeft, String units, String projection) {
		super();
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.units = units;
		this.projection = projection;
	}

	
	public String toDcmiBox() {
		String stringValue = null;

		StringBuilder buffer = new StringBuilder();
		buffer.append("northlimit=").append(getTopRight().getLatitude()).append("; ");
		buffer.append("eastlimit=").append(getTopRight().getLongitude()).append("; ");
		buffer.append("southlimit=").append(getBottomLeft().getLatitude()).append("; ");
		buffer.append("westlimit=").append(getBottomLeft().getLongitude()).append(";");
		if (!StringUtils.isEmpty( getUnits() )) {
			buffer.append(" units=").append(getUnits()).append(";");
		}
		if (!StringUtils.isEmpty( getProjection() )) {
			buffer.append(" projection=").append(getProjection()).append(";");
		}
		stringValue = buffer.toString();
		
		return stringValue;
	}
	
	
	
	
	
	public GeographicPoint getTopRight() {
		return topRight;
	}

	public void setTopRight(GeographicPoint topRight) {
		this.topRight = topRight;
	}

	public GeographicPoint getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(GeographicPoint bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
//	 public GeographicPoint getCenter() {
//		 return CoordinateUtils.getCenter(bottomLeft, topRight);
//	 }

	public String toString() {
		return new ToStringBuilder(this)
			.append("topRight", this.topRight)
			.append("bottomLeft", this.bottomLeft)
			.append("units", this.units)
			.append("projection", this.projection)
			.toString();
	}

//	public boolean equals(Object object) {
//		if (!(object instanceof Coordinates)) {
//			return false;
//		}
//		Coordinates rhs = (Coordinates) object;
//		return new EqualsBuilder()
//			.append(this.topRight, rhs.topRight)
//			.append(this.bottomLeft, rhs.bottomLeft)
//			.append(this.units, rhs.units)
//			.append(this.projection, rhs.projection)
//			.isEquals();
//	}
//
//	public int hashCode() {
//		return new HashCodeBuilder(1878024601, -1290453355)
//			.append(this.bottomLeft)
//			.append(this.topRight)
//			.append(this.projection)
//			.append(this.units)
//			.toHashCode();
//	}
}
