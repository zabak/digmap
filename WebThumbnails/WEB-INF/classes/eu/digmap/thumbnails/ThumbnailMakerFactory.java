package eu.digmap.thumbnails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ThumbnailMakerFactory {

	public static AbstractThumbnailMaker getThumbnailMaker(String uri, int width, int height) throws IOException {
		return getThumbnailMaker(uri,width,height,Float.MAX_VALUE);
	}
	
	public static AbstractThumbnailMaker getThumbnailMaker(String uri, int width, int height, byte transparency) throws IOException {
		return getThumbnailMaker(uri,width,height,transparency,Float.MAX_VALUE);
	}
	
	public static AbstractThumbnailMaker getThumbnailMaker(String uri, int width, int height, float rotation) throws IOException {
		return getThumbnailMaker(uri, width, height, (byte)255, rotation);
	}
	
	public static AbstractThumbnailMaker getThumbnailMaker(String uri, int width, int height, byte transparency, float rotation) throws IOException {		
		return getThumbnailMaker(uri, width, height, transparency, 101, 101, 101, 101, rotation);
	}
	
	public static AbstractThumbnailMaker getThumbnailMaker(String uri, int width, int height, byte t,  float transparencyWidth1, float transparencyWidth2, float transparencyHeight1, float transparencyHeight2, float rotation) throws IOException {
		URL url = new URL(uri);
		URLConnection connection = url.openConnection();
		String type = connection.getContentType();
		if (type == null) type="text/plain"; 
		if (type.contains("image/") || URLImageType(uri)) return new ImageThumbnailMaker(uri, connection.getInputStream(), width, height, t, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation);
		else if (type.contains("/pdf") || uri.toLowerCase().trim().endsWith(".pdf")) return new PDFThumbnailMaker(uri, connection.getInputStream(), width, height, t, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation );
		else return new HTMLThumbnailMaker(uri, connection.getInputStream(), width, height, t, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation);
	}
	
	private static boolean URLImageType(String uri) {
		uri = uri.toLowerCase().trim();
		if(uri.endsWith(".jpeg")) return true;
		if(uri.endsWith(".jpg")) return true;
		if(uri.endsWith(".gif")) return true;
		if(uri.endsWith(".png")) return true;
		return false;
	}
	
}


