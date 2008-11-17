package eu.digmap.thumbnails;

import java.io.File;
import java.net.MalformedURLException;

public class MapThumbnailParams extends ThumbnailParams {
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
	public int radius = 30;
	public String background = "lightGray";

	public MapThumbnailParams(String url, int width, int height, byte t) {
		super(null, null, width, height, t);
		
		ThumbnailConfig cfg = ThumbnailConfig.INSTANCE;
		
		try {
			if (url != null && url.equals("reliefmap")) {
				uri = new File(cfg.webappRoot, "ReliefWorldMap.gif").toURL().toString();
			}
			else if (url != null && url.equals("oldmap")) {
				uri = new File(cfg.webappRoot, "OldWorldMap.gif").toURL().toString();
			}
			else if (url != null && url.equals("satellitemap")) {
				uri = new File(cfg.webappRoot, "SatelliteWorldMap.gif").toURL().toString();
			}
			else if (url != null && url.equals("lightmap")) {
				uri = new File(cfg.webappRoot, "LightWorldMap.gif").toURL().toString();
				background = "white";
			} else {
				uri = new File(cfg.webappRoot, "SimpleWorldMap.gif").toURL().toString();
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon) {
		this(url, width, height, t, lat, lon, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, int radius) {
		this(url, width, height, t, lat, lon, Float.MAX_VALUE, Float.MAX_VALUE, radius);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat1, float lon1, float lat2, float lon2) {
		this(url, width, height, t, Float.MAX_VALUE, Float.MAX_VALUE, lat1, lon1, lat2, lon2, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat1, float lon1, float lat2, float lon2,
			int radius) {
		this(url, width, height, t, Float.MAX_VALUE, Float.MAX_VALUE, lat1, lon1, lat2, lon2, radius);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, int radius) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, Float.MAX_VALUE, Float.MAX_VALUE, -1, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius, int radius) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, Float.MAX_VALUE,
				Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, radius);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5,
			float lon5) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, lat4, lon4, lat5, lon5, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5,
			float lon5, int radius) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, lat4, lon4, lat5, lon5,
				Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, radius);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5,
			float lon5, float lat6, float lon6, float lat7, float lon7) {
		this(url, width, height, t, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, lat4, lon4, lat5, lon5,
				lat6, lon6, lat7, lon7, 15);
	}

	public MapThumbnailParams(String url, int width, int height, byte t, float lat, float lon, float lat1, float lon1,
			float lat2, float lon2, float lat3, float lon3, float cradius, float lat4, float lon4, float lat5,
			float lon5, float lat6, float lon6, float lat7, float lon7, int radius) {
		this(url, width, height, t);
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
}
