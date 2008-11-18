package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;

public class ThumbnailConfig {
	public static final String PROPERTY_FILE = "htmlthumbnail.properties";
	public static ThumbnailConfig INSTANCE = getInstance();
	
	public boolean nowait = false;
	public int timeout = 10000;
	public long waitTimeAfterRender = 0;
	public float compressionQuality = 0.95f;
	public String cacheDirectory = "/tmp/cache-thumbnails";
	public long cacheDuration = -1;
	public int syncTaskThreads = 10;
	public int asyncTaskThreads = 10;
	public BufferedImage working;
	
	public File webappRoot = null;

	protected static ThumbnailConfig getInstance() {
		ThumbnailConfig config = new ThumbnailConfig();
		
		Properties properties = new Properties();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL webInfClassesPlaceholderUrl = classLoader.getResource(PROPERTY_FILE);
			File webInfClassesPlaceholderFile = new File(webInfClassesPlaceholderUrl.getPath()).getParentFile();
			config.webappRoot = new File(webInfClassesPlaceholderFile, "../..").getCanonicalFile();
			config.working = ImageIO.read(new File(config.webappRoot, "Working.png").toURL());

			InputStream in = classLoader.getResourceAsStream(PROPERTY_FILE);
			properties.load(in);
			in.close();

			config.nowait = Boolean.parseBoolean(properties.getProperty("nail.map.nowait"));
			config.timeout = Integer.parseInt(properties.getProperty("nail.map.timeout"));
			config.waitTimeAfterRender = Long.parseLong(properties.getProperty("nail.map.waitTimeAfterRender"));
			config.cacheDirectory = properties.getProperty("cache.path");
			String durationStr = properties.getProperty("cache.duration");
			String qualityStr = properties.getProperty("nail.map.compressionQuality");
			if (config.cacheDirectory.endsWith(File.separator)) {
				config.cacheDirectory = config.cacheDirectory.substring(0, config.cacheDirectory.length() - 1);
			}
			if (qualityStr != null) {
				config.compressionQuality = Float.parseFloat(qualityStr);
			}
			if (durationStr != null) {
				config.cacheDuration = Long.parseLong(durationStr);
			}

			String asyncTaskThreadsStr = properties.getProperty("nail.map.threads.async");
			if (asyncTaskThreadsStr != null) {
				config.asyncTaskThreads = Integer.parseInt(asyncTaskThreadsStr);
			}
			String syncTaskThreadsStr = properties.getProperty("nail.map.threads.async");
			if (syncTaskThreadsStr != null) {
				config.syncTaskThreads = Integer.parseInt(syncTaskThreadsStr);
			}

			(new File(config.cacheDirectory)).mkdir();
		} catch (Exception e) {
			System.err.println("Unable to load properties for the thumbnails service. Using default.");
		}
		return config;
	}
}
