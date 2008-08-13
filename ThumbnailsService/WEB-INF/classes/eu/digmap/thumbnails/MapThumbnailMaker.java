package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapThumbnailMaker extends ImageThumbnailMaker {
		
	public float lat = Float.MAX_VALUE;
	public float lon = Float.MAX_VALUE;
	public float lat1 = Float.MAX_VALUE;
	public float lon1 = Float.MAX_VALUE;
	public float lat2 = Float.MAX_VALUE;
	public float lon2 = Float.MAX_VALUE;
	public float lat3 = Float.MAX_VALUE;
	public float lon3 = Float.MAX_VALUE;
	public float lat4 = Float.MAX_VALUE;
	public float lon4 = Float.MAX_VALUE;
	public float lat5 = Float.MAX_VALUE;
	public float lon5 = Float.MAX_VALUE;
	public float lat6 = Float.MAX_VALUE;
	public float lon6 = Float.MAX_VALUE;
	public float lat7 = Float.MAX_VALUE;
	public float lon7 = Float.MAX_VALUE;
	public float cradius = -1;	
	public float top_lat = 90;
	public float top_lon = -180;
	public float bot_lat = -90;
	public float bot_lon = 180;
	public int   radius = 30;
	public String background = "lightGray";
	
	public MapThumbnailMaker(String url, int width, int height, byte t) {
		super(null,null,width,height,t);
		if(url!=null && url.equals("reliefmap")) uri = "http://127.0.0.1:8080/nail.map/ReliefWorldMap.gif";
		else if(url!=null && url.equals("oldmap")) uri = "http://127.0.0.1:8080/nail.map/OldWorldMap.gif";
		else if(url!=null && url.equals("satellitemap")) uri = "http://127.0.0.1:8080/nail.map/SatelliteWorldMap.gif";
		else if(url!=null && url.equals("lightmap")) { uri = "http://127.0.0.1:8080/nail.map/LightWorldMap.gif"; background = "white"; }
		else uri = "http://127.0.0.1:8080/nail.map/SimpleWorldMap.gif"; 
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon) {
		this(url,width,height,t,lat,lon,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, int radius) {
		this(url,width,height,t,lat,lon,Float.MAX_VALUE,Float.MAX_VALUE,radius);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat1, float lon1, float lat2, float lon2) {
		this(url,width,height,t,Float.MAX_VALUE,Float.MAX_VALUE,lat1,lon1,lat2,lon2,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat1, float lon1, float lat2, float lon2, int radius) {
		this(url,width,height,t,Float.MAX_VALUE,Float.MAX_VALUE,lat1,lon1,lat2,lon2,radius);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, int radius) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,Float.MAX_VALUE,Float.MAX_VALUE,-1,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,lat3,lon3,cradius,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius, int radius) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,lat3,lon3,cradius,Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,radius);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5, float lon5) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,lat3,lon3,cradius,lat4,lon4,lat5,lon5,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5, float lon5, int radius) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,lat3,lon3,cradius,lat4,lon4,lat5,lon5,Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,radius);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5, float lon5, float lat6, float lon6, float lat7, float lon7) {
		this(url,width,height,t,lat,lon,lat1,lon1,lat2,lon2,lat3,lon3,cradius,lat4,lon4,lat5,lon5,lat6,lon6,lat7,lon7,15);
	}
	
	public MapThumbnailMaker(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1, float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5, float lon5, float lat6, float lon6, float lat7, float lon7, int radius) {
		this(url,width,height,t);
		this.lat = lat;
		this.lon = lon;
		this.lat1 = lat1;
		this.lon1 = lon1;
		this.lat2 = lat2;
		this.lon2 = lon2;
		this.lat3 = lat3;
		this.lon3 = lon3;
		this.lat4 = lat4;
		this.lon4 = lon4;
		this.lat5 = lat5;
		this.lon5 = lon5;
		this.lat6 = lat4;
		this.lon6 = lon4;
		this.lat7 = lat5;
		this.lon7 = lon5;
		this.cradius = cradius;
		this.radius = radius;
	}

	@Override
	protected BufferedImage getImage() throws IOException {
        Map<String, String> props = new HashMap<String,String>();
        props.put("mapname", this.uri);
        props.put("bgcolor", this.background);
        props.put("fgcolor", "black");
        props.put("title", "");
        props.put("date", "");
        props.put("width", "1024");
        props.put("height", "768");
        props.put("default_node_size", "6");
        props.put("default_line_size", "1");
        props.put("default_node_color", "red");
        props.put("default_line_color", "red");
        props.put("default_font_size value", "");
        props.put("refresh_interval", "");
        props.put("hide_key", "false");
        props.put("top_lat", ""+top_lat);
        props.put("top_lon", ""+top_lon);
        props.put("bot_lat", ""+bot_lat);
        props.put("bot_lon", ""+bot_lon); 
        String auxs = "";
        int sz = Math.max(1, radius / 2);
        if(lat1!=Float.MAX_VALUE && lon1!=Float.MAX_VALUE && lat2!=Float.MAX_VALUE && lon2!=Float.MAX_VALUE) {
           auxs +=  "\nn	1	"+ lat1 + "	" + lon1 + "	sz:" + radius + "	cl:red;\n" +
                    "n	2	"+ lat2 + "	" + lon1 + "	sz:" + radius + "	cl:red;\n"+
                    "n	3	"+ lat2 + "	" + lon2 + "	sz:" + radius + "	cl:red;\n"+
                    "n	4	"+ lat1 + "	" + lon2 + "	sz:" + radius + "	cl:red;\n"+
                    "l	1	2	sz:"+sz+"	cl:red;\n"+
                    "l	2	3	sz:"+sz+"	cl:red;\n"+
                    "l	3	4	sz:"+sz+"	cl:red;\n"+
                    "l	4	1	sz:"+sz+"	cl:red;";

        }
        if(lat!=Float.MAX_VALUE && lon!=Float.MAX_VALUE)  { 
        	auxs +="\nn	5	"+ lat + "	" + lon + "	sz:" + radius + "	cl:red;";
        }
    	if(lat3!=Float.MAX_VALUE && lon3!=Float.MAX_VALUE) {
    		auxs+="\nn	6	"+ lat3 + "	" + lon3 + "	sz:" + radius + "	cl:green;\n"+
    		      "l	6	6	sz:"+(sz/2)+"	cl:green	rd:"+cradius+";";
    	}
        if(lat4!=Float.MAX_VALUE && lon4!=Float.MAX_VALUE && lat5!=Float.MAX_VALUE && lon5!=Float.MAX_VALUE) {
            auxs +=  "\nn	7	"+ lat4 + "	" + lon4 + "	sz:" + radius + "	cl:red;\n" +
                     "n	8	"+ lat5 + "	" + lon4 + "	sz:" + radius + "	cl:red;\n"+
                     "n	9	"+ lat5 + "	" + lon5 + "	sz:" + radius + "	cl:red;\n"+
                     "n	10	"+ lat4 + "	" + lon5 + "	sz:" + radius + "	cl:red;\n"+
                     "l	7	8	sz:"+sz+"	cl:red;\n"+
                     "l	8	9	sz:"+sz+"	cl:red;\n"+
                     "l	9	10	sz:"+sz+"	cl:red;\n"+
                     "l	10	7	sz:"+sz+"	cl:red;";
         }
         props.put("data",auxs);
         BufferedImage bimage;
         if(lat6!=Float.MAX_VALUE && lon6!=Float.MAX_VALUE && lat7!=Float.MAX_VALUE && lon7!=Float.MAX_VALUE) bimage = MapGenerator.getImage(props,lat6,lon6,lat7,lon7);
         else bimage = MapGenerator.getImage(props);
         bimage = scaleImage(bimage);
		 return bimage;
	}
 	
}