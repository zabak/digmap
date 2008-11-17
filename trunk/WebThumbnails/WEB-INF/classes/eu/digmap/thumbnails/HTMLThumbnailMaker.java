package eu.digmap.thumbnails;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.*;
import org.w3c.dom.Document;

public class HTMLThumbnailMaker extends AbstractThumbnailMaker {

	protected static final float HTML_HEIGHT_RATIO = 0.75f;
	protected HTMLSpecialCasesThumbnailMaker specialCasesThumbnailMaker;

	/*
	 * public HTMLThumbnailMaker(String uri, InputStream connection, int w, int
	 * h, byte t) { this(uri,connection,w,h,t,Float.MAX_VALUE); }
	 * 
	 * public HTMLThumbnailMaker(String uri, InputStream connection, int w, int
	 * h, byte t, float rotation) { this(uri, connection, w, h, t,
	 * 101,101,101,101, rotation); }
	 * 
	 * public HTMLThumbnailMaker(String uri, InputStream connection, int width,
	 * int height, byte t, float transparencyWidth1, float transparencyWidth2,
	 * float transparencyHeight1, float transparencyHeight2, float rotation) {
	 * super(uri, connection, width, height, t, transparencyWidth1,
	 * transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation);
	 * exceptions = new
	 * ExceptionsThumbnailMaker(uri,connection,width,height,t,rotation);
	 * Logger.getLogger("org.lobobrowser").setLevel(Level.OFF); }
	 */
	public HTMLThumbnailMaker() {
		specialCasesThumbnailMaker = new HTMLSpecialCasesThumbnailMaker();
		Logger.getLogger("org.lobobrowser").setLevel(Level.OFF);
	}

	protected int getAutoHeight(BufferedImage bimage) {
		return (int) Math.round(params.width * HTML_HEIGHT_RATIO);
	}

	protected BufferedImage getImage() throws Exception {
		BufferedImage img = specialCasesThumbnailMaker.getImage();
		if (img != null)
			return img;
		return getImage_lobo();
	}

	protected BufferedImage getImage_lobo() throws Exception {
		if (params.height == 0)
			params.height = getAutoHeight(null);
		
		final float ratio = (float) params.width / (float) params.height;
		final int tempWidth = 1024;
		final int tempHeight = (int) (tempWidth / ratio);
		
		InputStream in = params.connection;
		Reader reader = new InputStreamReader(in);
		
		final HtmlPanel panel = new HtmlPanel();
		UserAgentContext ucontext = new SimpleUserAgentContext();
		SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(panel, ucontext);
		DocumentBuilderImpl dbi = new DocumentBuilderImpl(ucontext, rcontext);
		Document document = dbi.parse(new InputSourceImpl(reader, params.uri));
		
		panel.setDocument(document, rcontext);
		panel.setVisible(true);
		panel.setOpaque(false);
		panel.setBounds(0, 0, tempWidth, tempHeight);
		panel.setPreferredSize(new Dimension(tempWidth, tempHeight));
		
		BufferedImage bimage = new BufferedImage(tempWidth - 20, tempHeight - 20, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = bimage.createGraphics();
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				Frame f = new Frame();
				f.add(panel);
				f.setSize(tempWidth, tempHeight);
				f.pack();
				panel.doLayout();
				try {
					Thread.sleep(config.waitTimeAfterRender);
				} catch (InterruptedException e) {
				}
				panel.update(g2d);
			}
		});
		
		bimage = scaleImage(bimage);
		g2d.dispose();
		
		return bimage;
	}

}