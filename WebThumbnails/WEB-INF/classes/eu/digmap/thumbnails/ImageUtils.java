package eu.digmap.thumbnails;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.File;
import java.net.URL;

import javax.media.jai.BorderExtender;
import javax.media.jai.Histogram;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RIFRegistry;

public abstract class ImageUtils {

	public static PlanarImage load(File file) {
		return JAI.create("fileload", file.getAbsolutePath());
	}

	public static PlanarImage load(URL url) {
		return JAI.create("url", url);
	}

	public static void save(PlanarImage image, File outputFile, String format) {
		JAI.create("filestore", image, outputFile.getAbsolutePath(), format);
	}

	public static ParameterBlock getParameterBlockForImage(PlanarImage image) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		return pb;
	}

	public static ParameterBlockJAI getParameterBlockJAIForImage(PlanarImage image, String op) {
		ParameterBlockJAI pb = new ParameterBlockJAI(op);
		pb.addSource(image);
		return pb;
	}

	/**
	 * Register JAI operators
	 */
	public static void registerOp(String productName, String operationName, RegistryElementDescriptor descriptor,
			RenderedImageFactory rif) {
		try {
			OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();
			or.registerDescriptor(descriptor);
			RIFRegistry.register(or, operationName, productName, rif);
		} catch (IllegalArgumentException e) {
			// do nothing: operator already registered
		}
	}

	public static PlanarImage scale(PlanarImage image, float scale) {
		return scale(image, scale, InterpolationBicubic.INTERP_BICUBIC);
	}

	public static PlanarImage scale(PlanarImage image, float scale, int interpolation) {
		ParameterBlock pb = getParameterBlockForImage(image);
		pb.add(scale);
		pb.add(scale);
		pb.add(0.0F);
		pb.add(0.0F);
		pb.add(InterpolationBicubic.getInstance(interpolation));
		return JAI.create("scale", pb);
	}

	public static PlanarImage grayscale(PlanarImage image) {
		if (image.getNumBands() == 1)
			return image;
		final double[][] matrix1 = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
		ParameterBlock pb1 = getParameterBlockForImage(image);
		pb1.add(matrix1);
		return JAI.create("bandcombine", pb1, null);
	}

	public static PlanarImage invert(PlanarImage image) {
		return JAI.create("invert", getParameterBlockForImage(image), null);
	}

	public static PlanarImage binarize(PlanarImage image) {
		if (image.getNumBands() > 1)
			image = grayscale(image);
		return binarize(image, getBinarizationThreshold(image));
	}

	public static PlanarImage binarize(PlanarImage image, double threshold) {
		if (image.getNumBands() > 1)
			image = grayscale(image);
		ParameterBlock pb = getParameterBlockForImage(image);
		pb.add(threshold);
		return JAI.create("binarize", pb);
	}

	public static double getBinarizationThreshold(PlanarImage image) {
		Histogram histogram = (Histogram) JAI.create("histogram", image).getProperty("histogram");
		return histogram.getIterativeThreshold()[0];
	}

	public static PlanarImage crop(PlanarImage image, Rectangle2D rectangle) {
		return crop(image, (float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(),
				(float) rectangle.getHeight());
	}

	public static PlanarImage crop(PlanarImage image, float x, float y, float width, float height) {
		ParameterBlock pb = getParameterBlockForImage(image);
		pb.add(x);
		pb.add(y);
		pb.add(width);
		pb.add(height);
		return JAI.create("crop", pb);
	}

	public static PlanarImage blur(PlanarImage image, int radius) {
		int klen = Math.max(radius, 2);
		int ksize = klen * klen;
		float f = 1f / ksize;
		float[] kern = new float[ksize];
		for (int i = 0; i < ksize; i++)
			kern[i] = f;
		KernelJAI blur = new KernelJAI(klen, klen, kern);
		ParameterBlockJAI param = new ParameterBlockJAI("Convolve");
		param.addSource(image);
		param.setParameter("kernel", blur);
		// hint with border extender
		RenderingHints hint = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender
				.createInstance(BorderExtender.BORDER_COPY));
		return JAI.create("Convolve", param, hint);
	}
}
