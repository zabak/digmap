package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;

public class ThumbResult {
	private BufferedImage image;
	private File imageFile;

	public ThumbResult() {
		super();
	}

	public ThumbResult(BufferedImage image, File imageFile) {
		super();
		this.image = image;
		this.imageFile = imageFile;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	
	public boolean isEmpty() {
		return image == null && imageFile == null;
	}
}
