package eu.digmap.thumbnails;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import multivalent.Behavior;
import multivalent.Context;
import multivalent.Node;
import multivalent.std.adaptor.pdf.PDF;

import com.pt.io.InputUniByteArray;

public class PDFThumbnailMaker extends AbstractThumbnailMaker {
	
	protected static final int PDF_RENDER_PAGE = 1;
	protected static final int TEMP_COPY_BUFFER_SIZE = 8192;
		
	public PDFThumbnailMaker(String uri, InputStream connection, int width, int height, byte t) {
		super(uri, connection, width, height, t);
	}
	
	public PDFThumbnailMaker(String uri, InputStream connection, int width, int height, byte t, float rotation) {
		super(uri, connection, width, height, t, rotation);
	}
	
	public PDFThumbnailMaker(String uri, InputStream connection, int width, int height, byte t,  float transparencyWidth1, float transparencyWidth2, float transparencyHeight1, float transparencyHeight2, float rotation) {
		super(uri, connection, width, height, t, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation);
	}
		
	protected BufferedImage getImage() throws Exception {
		InputStream in = connection;
		PDF pdf = (PDF) Behavior.getInstance("AdobePDF", "AdobePDF", null, null, null);	
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[TEMP_COPY_BUFFER_SIZE];
		int len = 0;
		while ((len = in.read(buffer)) > 0) outputStream.write(buffer, 0, len);
		in.close();
		outputStream.close();
		pdf.setInput(new InputUniByteArray(outputStream.toByteArray()));	
		multivalent.Document doc = new multivalent.Document("doc", null, null);
		doc.clear();
		doc.putAttr(multivalent.Document.ATTR_PAGE, Integer.toString(PDF_RENDER_PAGE));
		pdf.parse(doc);
		Node top = doc.childAt(0);
		doc.formatBeforeAfter(200, 200, null);
		int pdfWidth = top.bbox.width;
		int pdfHeight = top.bbox.height;
		BufferedImage bimage = new BufferedImage(pdfWidth, pdfHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bimage.createGraphics();
		g.setClip(0, 0, pdfWidth, pdfHeight);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		Context cx = doc.getStyleSheet().getContext(g, null);
		top.paintBeforeAfter(g.getClipBounds(), cx);
		bimage = scaleAndRotateImage(bimage);
		doc.removeAllChildren();
		cx.reset();
		g.dispose();
		pdf.getReader().close();
		return bimage;
	}

}



