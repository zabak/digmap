<%@ page import="javax.imageio.stream.ImageOutputStream"%>
<%@ page import="org.deegree.model.csct.cs.CoordinateSystemFactory"%>
<%@ page import="eu.digmap.thumbnails.*" %>
<%@ page import="javax.imageio.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.awt.image.*"%>
<%@ page import="org.deegree.io.geotiff.*" %>
<%@ page import="org.deegree.model.crs.*" %>
<%@ page import="org.deegree.model.spatialschema.*" %>
<%@ page import="java.net.*" %>
<%
String url = request.getParameter("url");
if (url == null || url.length() == 0) throw new IllegalArgumentException("No URL was specified."); else {
	url = "thumbnail.jsp?width="+request.getParameter("width")+"&height="+request.getParameter("height")+"&url="+url;
	int width = 0, height = 0;
	double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
	try {
		width = (new Integer(request.getParameter("width"))).intValue();
		height = (new Integer(request.getParameter("height"))).intValue();
	    y1 = (new Double(request.getParameter("lat1"))).doubleValue();
	    x1 = (new Double(request.getParameter("lon1"))).doubleValue();
	    y2 = (new Double(request.getParameter("lat2"))).doubleValue();
	    x2 = (new Double(request.getParameter("lon2"))).doubleValue();
	} catch (Exception e) { 	
	}
	response.setContentType("image/tiff");
	OutputStream os = response.getOutputStream();
    CoordinateSystem system = new DeegreeCoordinateSystem("WGS84");
    Envelope enve = GeometryFactory.createEnvelope( x1, y1, x2, y2, system );
    URLConnection connection = (new URL(url)).openConnection();
	String type = connection.getContentType();
	if (type == null) throw new IllegalArgumentException("Content type could not be determined.");
	InputStream in = connection.getInputStream();
    BufferedImage img = ImageIO.read(in);
    double resx = img.getWidth();
    double resy = img.getHeight();
	GeoTiffWriter writer = new GeoTiffWriter( img, enve, ( x2 - x1 ) / resx, ( y2 - y1 ) / resy, system, 0, 1 );
	response.setContentType("image/tiff");
	writer.write(os);
	os.close();
}
%>