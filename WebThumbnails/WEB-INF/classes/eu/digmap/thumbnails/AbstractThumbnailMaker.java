package eu.digmap.thumbnails;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;

public abstract class AbstractThumbnailMaker {
	protected ThumbnailManager thumbnailManager = ThumbnailManager.INSTANCE;
	protected ThumbnailConfig config = ThumbnailConfig.INSTANCE;
	protected Cache cache = Cache.INSTANCE;
	protected ThumbnailParams params = null;

	public ThumbnailParams getParams() {
		return params;
	}

	public void setParams(ThumbnailParams params) {
		this.params = params;
	}

	public void makeAndUpdate(OutputStream out) throws Exception {
		makeAndUpdate(out, params.uri);
	}
	
	public void makeAndUpdate(OutputStream out, String url) throws Exception {
		ThumbResult thumbResult = generateImage(false, config.nowait);

		if (thumbResult == null || thumbResult.isEmpty()) {
			return;
		}
		try {
			if (thumbResult.getImage() != null) {
				cache.putInCache(url, params.width, params.height, thumbResult.getImage(), true);
			}
		} catch (Exception e) {
			// ignore
		}
		writeImageToOutput(out, thumbResult);
	}

	private void writeImageToOutput(OutputStream out, ThumbResult result) throws IOException {
		OutputStream bufferedOut = (out instanceof BufferedOutputStream ? out : new BufferedOutputStream(out));
		
		if (result.getImage() != null) {
			ImageIO.write(result.getImage(), "png", bufferedOut);
		} else {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(result.getImageFile()));
			int c;
			while ((c = inputStream.read()) != -1) {
				bufferedOut.write(c);
			}
		}
		bufferedOut.flush();
	}

	public void make(boolean useCache, OutputStream out) throws Exception {
		make(useCache, config.nowait, out);
	}

	public void make(boolean useCache, File file) throws Exception {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		make(useCache, config.nowait, out);
		out.flush();
		out.close();
	}

	public void make(boolean useCache, boolean nowait, OutputStream out) throws Exception {
		ThumbResult thumbResult = generateImage(useCache, nowait);
		if (thumbResult == null || thumbResult.isEmpty()) {
			return;
		}
		writeImageToOutput(out, thumbResult);
	}

	public void make(boolean useCache, boolean nowait, File file) throws Exception {
		OutputStream out = new FileOutputStream(file);
		make(useCache, nowait, out);
		out.close();
	}

	protected abstract BufferedImage getImage() throws Exception;

	protected ThumbResult generateImage(boolean useCache, int tempWidth, int tempHeight, byte tempTransparency)
			throws Exception {
		return generateImage(useCache, config.nowait, tempWidth, tempHeight, tempTransparency);
	}

	protected ThumbResult generateImage(boolean useCache, boolean nowait, int tempWidth, int tempHeight,
			byte tempTransparency) throws Exception {
		int realWidth = params.width;
		int realHeight = params.height;
		byte realTransparency = params.transparency;
		params.width = tempWidth;
		params.height = tempHeight;
		params.transparency = tempTransparency;
		ThumbResult result = generateImage(useCache, nowait);
		params.width = realWidth;
		params.height = realHeight;
		params.transparency = realTransparency;
		return result;
	}

	protected ThumbResult generateImage(boolean useCache) throws Exception {
		return generateImage(useCache, config.nowait);
	}

	private ThumbResult generateImage(boolean useCache, boolean nowait) throws Exception {
		BufferedImage image = null;
		File imageFile = null;
		
		boolean useRotation = (params.rotation != 0 || params.rotation != Float.MAX_VALUE);
		boolean useTransparency = (params.transparency > 0 && params.transparency < 255);
		boolean postProcessImage = useRotation || useTransparency;
		
		try {
			if (useCache) {
				imageFile = cache.getImageFile(params.uri, params.width, params.height);
				if (!imageFile.exists()) {
					imageFile = null;
				}
			}
		} catch (Exception e) {
			// ignore
		}

		try {
			if (image == null && imageFile == null) {
				boolean sync = !nowait;
				Future<ThumbResult> executionResult = thumbnailManager.execute(this, sync, useCache);
				
				if (!nowait) {
					ThumbResult thumbResult = executionResult.get();
					image = thumbResult.getImage();
					imageFile = thumbResult.getImageFile();
				}
				else {
					image = scaleImage(config.working);
					postProcessImage = false;
				}
			}
		} catch (Exception e) {
			// ignore
		}
		
		if (postProcessImage) {
			if (image == null && imageFile != null) {
				image = ImageUtils.load(imageFile).getAsBufferedImage();
			}
			if (image != null) {
				image = transparencyAndRotateImage(image);
			}
		}
		return new ThumbResult(image, imageFile);
	}

	protected int getAutoHeight(BufferedImage bimage) {
		return (int) (params.width * ((float) bimage.getHeight() / (float) bimage.getWidth()));
	}

	protected int getAutoWidth(BufferedImage bimage) {
		return (int) (params.height * ((float) bimage.getWidth() / (float) bimage.getHeight()));
	}

	protected BufferedImage scaleImage(BufferedImage bimage) {
		int width = 0;
		int height = 0;
		if (params.width == 0 && params.height == 0) {
			width = bimage.getWidth();
			height = bimage.getHeight();
		} else {
			height = (params.height == 0) ? getAutoHeight(bimage) : params.height;
			width = (params.width == 0) ? getAutoWidth(bimage) : params.width;
		}
		if ((width != bimage.getWidth()) || (height != bimage.getHeight())) {
			GraphicsConfiguration gc = bimage.createGraphics().getDeviceConfiguration();
			BufferedImage out = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			Graphics2D g2d = out.createGraphics();
			g2d.setComposite(AlphaComposite.Src);
			g2d.drawImage(bimage, 0, 0, width, height, null);
			g2d.dispose();
			bimage = out;
		}
		return bimage;
	}

	protected BufferedImage rotateImage(BufferedImage img) {
		if (params.rotation == Float.MAX_VALUE || params.rotation == 0)
			return img;
		float angle = (float) Math.toRadians(params.rotation);
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

	protected BufferedImage transparencyAndRotateImage(BufferedImage img) {
		return rotateImage(addTransparency(img));
	}

	protected BufferedImage addTransparency(BufferedImage img) {
		if (params.transparency == 255) {
			return img;
		}

		if (params.transparencyWidth1 <= params.transparencyWidth2
				&& params.transparencyHeight1 <= params.transparencyHeight2 && params.transparencyWidth1 <= 100
				&& params.transparencyWidth2 <= 100 && params.transparencyHeight1 <= 100
				&& params.transparencyHeight2 <= 100) {
			int x1 = (int) (img.getWidth() * params.transparencyWidth1 / 100.0);
			int x2 = (int) (img.getWidth() * params.transparencyWidth2 / 100.0);
			int y1 = (int) (img.getHeight() * params.transparencyHeight1 / 100.0);
			int y2 = (int) (img.getHeight() * params.transparencyHeight2 / 100.0);
			int rgb[] = new int[img.getWidth() * img.getHeight()];

			img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
			int _alpha = (params.transparency & 0xFF) << 24;
			for (int i = 0; i < rgb.length; i++) {
				rgb[i] = (rgb[i] & 0xFFFFFF) | _alpha;
			}

			img.setRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
			for (int i = x1; i < x2; i++) {
				for (int j = y1; j < y2; j++) {
					img.setRGB(i, j, (img.getRGB(i, j) & 0xFFFFFF) | (0xFF << 24));
				}
			}
		} else {
			int rgb[] = new int[img.getWidth() * img.getHeight()];
			img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
			int _alpha = (params.transparency & 0xFF) << 24;
			for (int i = 0; i < rgb.length; i++)
				rgb[i] = (rgb[i] & 0xFFFFFF) | _alpha;
			img.setRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
		}
		return img;
	}
}