package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Cache {
	public static Cache INSTANCE = new Cache();
	private ThumbnailConfig config = ThumbnailConfig.INSTANCE;

	public BufferedImage getFromCache(String uri, int width, int height) throws Exception {
		File imageFileNoExt = getPathNoExt(uri, width, height);
		File imageFile = new File(imageFileNoExt.getAbsolutePath() + ".png");
		
		if (!imageFile.exists()) {
			return null;
		}
		
		imageFileNoExt.getParentFile().mkdirs();
		long duration = getDuration(imageFileNoExt);
		if (duration != -1 && duration < System.currentTimeMillis())
			return null;
		return ImageIO.read(new FileInputStream(imageFileNoExt.getAbsolutePath() + ".png"));
	}

	public synchronized void putInCache(String uri, int width, int height, BufferedImage img, boolean forever)
			throws Exception {
		File imageFileNoExt = getPathNoExt(uri, width, height);
		imageFileNoExt.getParentFile().mkdirs();
		setDuration(imageFileNoExt, forever);
		ImageIO.write(img, "png", new FileOutputStream(imageFileNoExt.getAbsolutePath() + ".png"));
	}

	public File getPathNoExt(String uri, int width, int height) {
		String filename = "0000" + (uri + width + height).hashCode();
		String dir1 = filename.substring(filename.length() - 2);
		String dir2 = filename.substring(filename.length() - 4, filename.length() - 2);
		filename = 
			config.cacheDirectory 
			+ File.separator + dir1 
			+ File.separator + dir2 
			+ File.separator + filename.substring(0, filename.length() - 4);
		return new File(filename);
	}
	
	public File getImageFile(String uri, int width, int height) {
		return new File(getPathNoExt(uri, width, height) + ".png");
	}

	private long getDuration(File imageFile) throws IOException, FileNotFoundException {
		DataInputStream dataInputStream = null;
		long duration = -1;
		try {
			File durationFile = new File(imageFile.getAbsolutePath() + ".duration");;
			if (durationFile.exists()) {
				dataInputStream = new DataInputStream(new FileInputStream(durationFile));
				duration = (dataInputStream).readLong();
			}
			return duration;
		} finally {
			if (dataInputStream != null) {
				dataInputStream.close();
			}
		}
	}

	private void setDuration(File imageFile, boolean forever) throws IOException, FileNotFoundException {
		DataOutputStream dataOutputStream = null;
		long duration = (forever || config.cacheDuration <= 0 ? -1 : System.currentTimeMillis() + config.cacheDuration);
		try {
			dataOutputStream = new DataOutputStream(new FileOutputStream(imageFile.getAbsolutePath() + ".duration"));
			dataOutputStream.writeLong(duration);
		} finally {
			if (dataOutputStream != null) {
				dataOutputStream.flush();
				dataOutputStream.close();
			}
		}
	}
}
