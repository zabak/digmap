package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.InputStream;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.SeekableStream;

public class ImageThumbnailMaker extends AbstractThumbnailMaker {
	
	public ImageThumbnailMaker(String uri, InputStream connection, int width, int height, byte t) {
		super(uri, connection, width, height, t);
	}
	
	public ImageThumbnailMaker(String uri, InputStream connection, int width, int height, byte t, float rotation) {
		super(uri, connection, width, height, t, rotation);
	}

	protected BufferedImage getImage() throws IOException {
		InputStream in = SeekableStream.wrapInputStream(connection, true);
		RenderedOp reader = JAI.create("stream", in);
		((OpImage)reader.getRendering()).setTileCache(null);
		ParameterBlock pb = new ParameterBlock();
		if(width==-1) width = reader.getWidth();
		if(height==-1) height = reader.getHeight();
		pb.addSource(reader);
		pb.add((float)1.0); 
		pb.add((float)1.0); 
		pb.add(0.0F);
		pb.add(0.0F);
		pb.add(new InterpolationNearest());
		reader = JAI.create("scale", pb, null);
		BufferedImage img = reader.getAsBufferedImage();
		return scaleAndRotateImage(img);
	}

}