package eu.digmap.thumbnails;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;

public abstract class AbstractThumbnailMaker {
	
	protected static BufferedImage working;
	
	protected static final String PROPERTY_FILE = "htmlthumbnail.properties";
	
	protected static boolean initialized = false;
	
	protected static boolean nowait = false;
	
	protected static int timeout = 10000;
	
	protected static long waitTimeAfterRender = 0;
	
	protected static float compressionQuality = 0.95f;
	
	protected static String cacheDirectory = "/tmp/cache-thumbnails";
	
	protected static long cacheDuration = -1;
	
	protected String uri = null;
	
	protected InputStream connection = null;
	
	protected int width = 0, height = 0;
	
	protected float rotation = 0;
	
	protected byte transparency = 0;
	
	protected float transparencyWidth1 = 101;
	
	protected float transparencyWidth2 = 101;
	
	protected float transparencyHeight1 = 101;
	
	protected float transparencyHeight2 = 101;
	
	public AbstractThumbnailMaker(String uri, InputStream connection, int w, int h, byte t) {
		this(uri,connection,w,h,t,0);
	}
	
	public AbstractThumbnailMaker(String uri, InputStream connection, int w, int h, byte t, float rotation) {
		this(uri,connection,w,h,t,101,101,101,101,0);
	}
	
	public AbstractThumbnailMaker(String uri, InputStream connection, int w, int h, byte t, float transparencyWidth1, float transparencyWidth2, float transparencyHeight1, float transparencyHeight2, float rotation) {
		init();
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
		if (transparency==0) transparency = (byte)255;
		if (width==0 && height==0) width = 255;
	}
	
	public void makeAndUpdate(OutputStream out, String url) throws Exception {
		BufferedImage image = generateImage(false);
		if (image == null) return;
		try { putInCache(url,width,height,rotation,image,true); } catch ( Exception e ) { }
		ImageIO.write(addTransparency(image), "png", out);
	}
	
	public void make(boolean useCache, OutputStream out) throws Exception {
		BufferedImage image = generateImage(useCache);
		if (image == null) return;
		ImageIO.write(addTransparency(image), "png", out);
	}

	public void make(boolean useCache, File file) throws Exception {
		OutputStream out = new FileOutputStream(file);
		make(useCache,out);
		out.close();
	}
	
	protected static synchronized void init() {
		if (initialized) return;
		Properties properties = new Properties();
		try {
			working = ImageIO.read(new URL("http://127.0.0.1:8080/nail.map/Working.png"));
			InputStream in = AbstractThumbnailMaker.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
			properties.load(in);
			in.close();
			nowait = Boolean.parseBoolean(properties.getProperty("nail.map.nowait"));
			timeout = Integer.parseInt(properties.getProperty("nail.map.timeout"));
			waitTimeAfterRender = Long.parseLong(properties.getProperty("nail.map.waitTimeAfterRender"));
			cacheDirectory = properties.getProperty("cache.path");
			String durationStr = properties.getProperty("cache.duration");
			String qualityStr = properties.getProperty("nail.map.compressionQuality");
			if(cacheDirectory.endsWith(File.separator)) cacheDirectory = cacheDirectory.substring(0,cacheDirectory.length()-1);
			if (qualityStr != null) compressionQuality = Float.parseFloat(qualityStr);
			if (durationStr != null) cacheDuration = Long.parseLong(durationStr);
			(new File(cacheDirectory)).mkdir();
		} catch (Exception e) {
			System.err.println("Unable to load properties for the thumbnails service. Using default.");
		}
		initialized = true;
	}
	
	protected abstract BufferedImage getImage() throws Exception;
	
	protected BufferedImage generateImage(boolean useCache, int tempWidth, int tempHeight, byte tempTransparency) throws Exception {
		int realWidth = width;
		int realHeight = height;
		byte realTransparency = transparency;
		width = tempWidth;
		height = tempHeight;
		transparency = tempTransparency;
		BufferedImage image = generateImage(useCache);
		width = realWidth;
		height = realHeight;
		transparency = realTransparency;
		return image;
	}

	private BufferedImage generateImage(boolean useCache) throws Exception {
		BufferedImage image = null;
		boolean done = false;
		boolean incache = false;
		boolean updated = false;
		try { if(useCache) { incache = ((image = getFromCache(uri,width,height,rotation))!=null); } } catch (Exception e) { }
		try { if(!incache || image==null) {
			if(!nowait || !useCache) image = getImage(); else {
				AsynchronosGenerator.getInstance().addRequest(this);
				image = scaleImage(working);
			}
		} } catch (Exception e) { }
		try { if(!nowait && !incache && image!=null) putInCache(uri,width,height,rotation,image,false); } catch (Exception ex) { }
		done = true;
		return image;
	}

	protected int getAutoHeight(BufferedImage bimage) {
		return (int) (width * ((float) bimage.getHeight() / (float) bimage.getWidth()));
	}
	
	protected int getAutoWidth(BufferedImage bimage) {
		return (int) (height * ((float) bimage.getWidth() / (float) bimage.getHeight()));
	}
	
	protected BufferedImage scaleImage(BufferedImage bimage) {
		int height = (this.height == 0) ? getAutoHeight(bimage) : this.height;
		int width = (this.width == 0) ? getAutoWidth(bimage) : this.width;
		if ((width != bimage.getWidth()) || (height != bimage.getHeight())) {
			Image b2 = bimage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bimage.getGraphics(); 
			g.drawImage(b2, 0, 0, null);
			g.dispose();
		}
		return bimage;
	}
	
	protected BufferedImage rotateImage (BufferedImage img ) {
		if (rotation==Float.MAX_VALUE || rotation == 0) return img;
		float angle = (float)Math.toRadians(rotation);
		float centerX = img.getWidth() / 2; 
		float centerY = img.getHeight() / 2;  
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(centerX);
		pb.add(centerY);
		pb.add(angle);
		pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		return JAI.create("rotate", pb).getAsBufferedImage();
	}
	
	protected BufferedImage scaleAndRotateImage ( BufferedImage img ) {
		return scaleImage(rotateImage(img));
	}
	
	protected BufferedImage addTransparency (BufferedImage img ) {
		if (transparency==255) return img;
		if(transparencyWidth1 <= transparencyWidth2 && 
		   transparencyHeight1 <= transparencyHeight2 &&
		   transparencyWidth1 <= 100 &&
		   transparencyWidth2 <= 100 &&
		   transparencyHeight1 <=100 &&
		   transparencyHeight2 <=100) {
			int x1 = (int)(img.getWidth() * transparencyWidth1 / 100.0);
			int x2 = (int)(img.getWidth() * transparencyWidth2 / 100.0);
			int y1 = (int)(img.getHeight() * transparencyHeight1 / 100.0);
			int y2 = (int)(img.getHeight() * transparencyHeight2 / 100.0);
			int rgb[] = new int[img.getWidth() * img.getHeight()];
			img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
			int _alpha = (transparency & 0xFF) << 24;
			for(int i = 0; i < rgb.length; i++) rgb[i] = (rgb[i] & 0xFFFFFF) | _alpha;
			img.setRGB(0,0,img.getWidth(),img.getHeight(), rgb, 0, img.getWidth());
			for (int i=x1; i<x2; i++) for (int j=y1; j<y2; j++) img.setRGB(i, j, (img.getRGB(i, j) & 0xFFFFFF) | (0xFF << 24));
		} else {
			int rgb[] = new int[img.getWidth() * img.getHeight()];
			img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
			int _alpha = (transparency & 0xFF) << 24;
			for(int i = 0; i < rgb.length; i++) rgb[i] = (rgb[i] & 0xFFFFFF) | _alpha;
			img.setRGB(0,0,img.getWidth(),img.getHeight(), rgb, 0, img.getWidth());
		}
		return img;
	}
	
	protected BufferedImage getFromCache ( String uri, int width, int height, float rotation ) throws Exception {
		if(rotation==Float.MAX_VALUE) rotation = 0;
		String file = "0000" + (uri + width + height + rotation).hashCode();
		String dir1 = file.substring(file.length()-2);
		String dir2 = file.substring(file.length()-4,file.length()-2);
		file = cacheDirectory+File.separator+dir1+File.separator+dir2+File.separator+file.substring(0,file.length()-4);
		(new File(cacheDirectory+File.separator+dir1)).mkdir();
		(new File(cacheDirectory+File.separator+dir1+File.separator+dir2)).mkdir();
		long duration = (new DataInputStream(new FileInputStream(file+".duration"))).readLong();
		if (duration!=-1 && duration<System.currentTimeMillis()) return null;
		return ImageIO.read(new FileInputStream(file+".png"));
	}
	
	protected synchronized void putInCache ( String uri, int width, int height, float rotation, BufferedImage img, boolean forever ) throws Exception {
		if(rotation==Float.MAX_VALUE) rotation = 0;
		String file = "0000" + (uri + width + height + rotation).hashCode();
		String dir1 = file.substring(file.length()-2);
		String dir2 = file.substring(file.length()-4,file.length()-2);
		file = cacheDirectory+File.separator+dir1+File.separator+dir2+File.separator+file.substring(0,file.length()-4);
		(new File(cacheDirectory+File.separator+dir1)).mkdir();
		(new File(cacheDirectory+File.separator+dir1+File.separator+dir2)).mkdir();
		if(forever || cacheDuration<=0) (new DataOutputStream(new FileOutputStream(file+".duration"))).writeLong(-1);
		else (new DataOutputStream(new FileOutputStream(file+".duration"))).writeLong(System.currentTimeMillis()+cacheDuration);
		ImageIO.write(img,"png",new FileOutputStream(file+".png"));
	}
	
}