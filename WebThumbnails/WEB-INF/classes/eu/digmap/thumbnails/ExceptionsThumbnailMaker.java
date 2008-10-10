package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * This class provides methods for the handling of exceptional cases in the
 * construction of image thumbnails from web sites related to historical cartography.
 * For now, the provided methods are based on hand-coded rules, although future versions
 * of the software will look at automated methods for doing this.
 * 
 * The idea is to generate thumbnails with basis on the map image that in displayed on
 * an HTML page, instead of doing a thumbnail as a web browser would render the page.
 * This way, we can better deal with the case of several web known map libraries.
 * 
 * @author Bruno Martins (bgmartins@gmail.com)
 * @version 0.1
 */
public class ExceptionsThumbnailMaker extends AbstractThumbnailMaker {
	
	public ExceptionsThumbnailMaker(String uri, InputStream connection, int w, int h, byte t, float rotation) {
		super(uri, connection, w, h, t, rotation);
	}
	
	public ExceptionsThumbnailMaker(String uri, InputStream connection, int width, int height, byte t,  float transparencyWidth1, float transparencyWidth2, float transparencyHeight1, float transparencyHeight2, float rotation) {
		super(uri, connection, width, height, t, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation);
	}
	
	protected BufferedImage getImage() throws Exception {
		BufferedImage img = null;
		if ((img = handleNLAException()) != null) return img;
		if ((img = handleRaremapsException()) != null) return img;
		if ((img = handleICCException()) != null) return img;
		if ((img = handleFirenzeException()) != null) return img;
		if ((img = handleGMUException()) != null) return img;
		if ((img = handleFCLAException()) != null) return img;
		if ((img = handleBNException()) != null) return img;
		if ((img = handleKBRException()) != null) return img;
		if ((img = handleGOVException()) != null) return img;
		if ((img = handleBibDigHispanica()) != null) return img;
		return img;
	}
	
	/** Handle the exception case of the National Library of Australia */
	protected BufferedImage handleNLAException () {
		if(uri.startsWith("http://nla.gov.au/nla.map")) try {
			uri = uri + "-v.jpg";
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e ) { }
		else if(uri.startsWith("http://www.nla.gov.au/apps/cdview?pi=nla.map")) try {
			uri = "http://nla.gov.au/nla.map" + uri.substring(uri.indexOf('-'),uri.lastIndexOf('-')) + "-v.jpg";
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e ) { }	
		return null;
	}
	
	/** Handle the exception case of the Raremaps website */
	protected BufferedImage handleRaremapsException () {
		if(uri.startsWith("http://www.raremaps.com/cgi-bin/gallery.pl/detail/")) try {
			uri = uri.replace("cgi-bin/gallery.pl/detail","maps/medium");
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e ) { }
		return null;
	}
	
	/** Handle the exception case of the Instituto Cartografico Catalunha */
	protected BufferedImage handleICCException () {
		if(uri.startsWith("http://vacani.icc.cat") || uri.startsWith("http://louisdl.louislibraries.org")) try {
			uri = uri.replace("cdm4/item_viewer.php", "cgi-bin/getimage.exe") + "&DMSCALE=3";
			uri = uri.replace("/u?", "cgi-bin/getimage.exe?CISOROOT=").replace(",","&CISOPTR=") + "&DMSCALE=3";
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e ) { }
		return null;
	}
	
	/** Handle the exception case of the Biblioteca Nazionale Centrale di Firenze */
	protected BufferedImage handleFirenzeException () {
		if(uri.indexOf("bncf.firenze.sbn.it")!=-1) try {
			uri = uri.replace("http://opac.bncf.firenze.sbn.it/mdigit/jsp/mdigit.jsp?idr", "http://teca.bncf.firenze.sbn.it/TecaViewer/index.jsp?RisIdr");
			URLConnection connection = new URL(uri).openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String url = null;
			uri = "addPage('http://opac.bncf.firenze.sbn.it/php/xlimage/XLImageRV.php";
			while((url=reader.readLine())!=null) {
				int index = url.indexOf(uri);
				if(index!=-1) {
					url = url.substring(url.indexOf("'")+1,url.lastIndexOf("'"));
					break;
				}
			}
			connection = new URL(url).openConnection();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			uri = "<input type=\"image\" border=\"0\" name=\"tpos\" width=\"";
			while((url=reader.readLine())!=null) {
				int index = url.indexOf(uri);
				if(index!=-1) {
					url = url.substring(url.indexOf(" src=\"")+6,url.lastIndexOf("\" alt=\"")).replace("&z=2","&z=32").replace("&z=4","&z=64").replace("&z=8","&z=128");
					break;
				}
			}
			if(url!=null) {
				connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			}
		} catch ( Exception e ) { }	
		return null;
	}
	
	/** Handle the exception case of the George Mason University */
	protected BufferedImage handleGMUException () {
		if(uri.startsWith("http://mars.gmu.edu:8080")) try {
			URLConnection connection = new URL(uri).openConnection();
			int index = uri.lastIndexOf("?");
			uri = "<img class=\"itemthumb\" src=\""; 
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String url = null;
			while((url=reader.readLine())!=null) {
				index = url.indexOf(uri);
				if(index!=-1) {
					url = "http://mars.gmu.edu:8080" + url.substring(index+28);
					url = url.substring(0,url.indexOf("\" alt=\""));
					break;
				}
			}
			if(url!=null) {
				connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			}
		} catch ( Exception e ) { }
		return null;
	}
	
	/** Handle the exception case of the Florida Center for Library Automation */
	protected BufferedImage handleFCLAException () {
		if(uri.startsWith("http://image11.fcla.edu/cgi")) try {
			uri = uri.substring(uri.indexOf("q1=")+3);
			uri = uri.substring(0,uri.indexOf("&"));
			uri = "http://image11.fcla.edu/m/map/thumb/" + uri.substring(uri.length()-3,uri.length()-2) + "/" + uri.substring(uri.length()-2,uri.length()-1) + "/" + uri.substring(uri.length()-1,uri.length()) + "/" + uri + ".jpg"; 
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e ) { }
		return null;
	}
	
	/** Handle the exception case of the Portuguese National Library */
	protected BufferedImage handleBNException () {
		if(uri.startsWith("http://purl.pt/")) try {
			URLConnection connection = new URL(uri).openConnection();
			if(uri.endsWith("/")) uri = uri.substring(0,uri.length()-1);
			int index = uri.lastIndexOf("/");
			uri = "http://purl.pt/homepage/" + uri.substring(index+1) + "/" + uri.substring(index+1); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String url = null;
			while((url=reader.readLine())!=null) {
				index = url.indexOf(uri);
				if(index!=-1) {
					url = url.substring(index);
					url = url.substring(0,url.indexOf("\""));
					break;
				}
			}
			if(url!=null) {
				connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			}
		} catch ( Exception e) { }
		return null;
	}
	
	/** Handle the exception case of the National Library of Estonia */
	protected BufferedImage handleNLIBException () {
		if(uri.startsWith("http://digar.nlib.ee/otsing/") || uri.startsWith("http://digar.nlib.ee/show")) try {
			String url = "http://digar.nlib.ee/gmap/nd" + uri.substring(uri.indexOf(":")+1,uri.lastIndexOf("&")) + "-tiles/z0x0y0.jpeg";
			URLConnection connection = new URL(url).openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());
		} catch ( Exception e) { 
			try {
				if(uri.startsWith("http://digar.nlib.ee/show")) uri = "http://digar.nlib.ee/otsing/?pid=" +  uri.substring(uri.lastIndexOf("/")+1) + "&show";
	 			URLConnection connection = new URL(uri).openConnection();
				String url = uri;
	 			if(url.endsWith("&show")) url = url.substring(0,url.length()-5);
				int index = url.lastIndexOf("?");
				url = "stream" + url.substring(index); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String aux = null;
				while((aux=reader.readLine())!=null) {
					index = aux.indexOf(url);
					if(index!=-1) {
						url = "http://digar.nlib.ee/otsing/" + aux.substring(index);
						index = url.indexOf('>');
						if(index==-1) index = url.indexOf("\"");
						url = url.substring(0,index);
						break;
					}
				}
				connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			} catch ( Exception e2 ) {
			}			
		}
		return null;
	}
	
	/** Handle the exception case of the Royal Library of Belgium */
	protected BufferedImage handleKBRException () {		
		if(uri.startsWith("http://mara.kbr.be/kbrImage/CM/") || uri.startsWith("http://mara.kbr.be/kbrImage/maps/") || uri.startsWith("http://opteron2.kbr.be/kp/viewer/")) try {
			URLConnection connection = new URL(uri).openConnection();
			String url = "get_image.php?intId="; 
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String aux = null;
			while((aux=reader.readLine())!=null) {
				if(aux.indexOf(url)!=-1) {
					aux = aux.substring(aux.indexOf(url));					
					url = "http://mara.kbr.be/kbrImage/" + aux.substring(0,aux.indexOf("\""));
					break;
				}
			}
			connection = new URL(url).openConnection();
			return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
		} catch ( Exception e) { 
			try {
			  String url = "http://mara.kbr.be/xlimages/maps/thumbnails" + uri.substring(uri.lastIndexOf("/")).replace(".imgf",".jpg");
			  if(url!=null) {
				URLConnection connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			  }
			} catch ( Exception e2 ) { }
		}
		return null;
	}
	
	/** TODO: Handle the exception case of the Biblioteca Digital Hispanica */
	protected BufferedImage handleBibDigHispanica () {
		if(uri.startsWith("http://bibliotecadigitalhispanica.bne.es:80/view/action/singleViewer.do")) try {
		} catch ( Exception e) { }	
		return null;
	}
		
	/** Handle the exception case of the US Library of Congress */
	protected BufferedImage handleGOVException () {
		if(uri.startsWith("http://memory.loc.gov/cgi-bin/ampage")) try {
			URLConnection connection = new URL(uri).openConnection();
			uri = ".*/cgi-bin/map_item.pl.*<img src=.*"; 
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String url = null;
			while((url=reader.readLine())!=null) {
				if(url.matches(uri)) {
					url = "http://memory.loc.gov" + url.substring(url.indexOf("<img src=\"")+10);
					url = url.substring(0,url.indexOf("\""));
					break;
				}
			}
			if(url!=null) {
				connection = new URL(url).openConnection();
				return scaleImage((new ImageThumbnailMaker(uri,connection.getInputStream(),-1,-1,(byte)transparency)).getImage());				
			}
		} catch ( Exception e) { }	
		return null;
	}

}