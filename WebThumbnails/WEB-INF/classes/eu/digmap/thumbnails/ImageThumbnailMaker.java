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

	protected BufferedImage getImage() throws IOException {
		InputStream in = SeekableStream.wrapInputStream(params.connection, true);
		RenderedOp reader = JAI.create("stream", in);
//		((OpImage) reader.getRendering()).setTileCache(null);
		if (params.width == -1)
			params.width = reader.getWidth();
		if (params.height == -1)
			params.height = reader.getHeight();

//		ParameterBlock pb = new ParameterBlock();
//		pb.addSource(reader);
//		pb.add((float) 1.0);
//		pb.add((float) 1.0);
//		pb.add(0.0F);
//		pb.add(0.0F);
//		pb.add(new InterpolationNearest());
//		reader = JAI.create("scale", pb, null);
		BufferedImage img = reader.getAsBufferedImage();
		return img;
	}

}