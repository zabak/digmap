package eu.digmap.thumbnails;

import java.io.InputStream;

public class ThumbnailParams {
	public String uri = null;
	public int width = 0;
	public int height = 0;
	public float rotation = 0;
	public byte transparency = 0;
	public float transparencyWidth1 = 101;
	public float transparencyWidth2 = 101;
	public float transparencyHeight1 = 101;
	public float transparencyHeight2 = 101;
	public InputStream connection = null;
	
	public ThumbnailParams() {
		super();
	}
	
	public ThumbnailParams(String uri, InputStream connection, int w, int h, byte t) {
		this(uri, connection, w, h, t, 0);
	}

	public ThumbnailParams(String uri, InputStream connection, int w, int h, float rotation) {
		this(uri, connection, w, h, (byte) 0, rotation);
	}

	public ThumbnailParams(String uri, InputStream connection, int w, int h, byte t, float rotation) {
		this(uri, connection, w, h, t, 101, 101, 101, 101, rotation);
	}

	public ThumbnailParams(String uri, InputStream connection, int w, int h, byte t, float transparencyWidth1,
			float transparencyWidth2, float transparencyHeight1, float transparencyHeight2, float rotation) {
		this.uri = uri;
		this.connection = connection;
		this.width = w;
		this.height = h;
		this.transparency = t;
		this.rotation = rotation;
		this.transparencyWidth1 = transparencyWidth1;
		this.transparencyWidth2 = transparencyWidth2;
		this.transparencyHeight1 = transparencyHeight1;
		this.transparencyHeight2 = transparencyHeight2;
		if (transparency == 0) {
			transparency = (byte) 255;
		}
		if (width == 0 && height == 0) {
			width = 255;
		}
	}

	public boolean equalsThumbnail(Object obj) {
		if (!(obj instanceof ThumbnailParams)) {
			return false;
		}
		ThumbnailParams other = (ThumbnailParams) obj;
		return uri.equals(other.uri) && width == other.width && height == other.height;
	}
}
