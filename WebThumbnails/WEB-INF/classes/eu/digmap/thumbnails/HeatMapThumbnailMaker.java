package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class HeatMapThumbnailMaker extends ImageThumbnailMaker {
		
	public double[][] data = HeatMapPanel.generateRandomData();
	
	public HeatMapThumbnailMaker(String url, int width, int height, byte t) {
		super(null,null,width,height,t);
	}
	
	public HeatMapThumbnailMaker(String url, int width, int height, byte t, String data) {
		this(url,width,height,t);
		this.data=HeatMapPanel.generateData(data);
	}
	    
	protected BufferedImage getImage() throws IOException {
        HeatMapPanel panel = new HeatMapPanel(data, HeatMapPanel.GRADIENT_WHITE_TO_RED);
        panel.setCoordinateBounds(0, 360, 0, 180);
        panel.setSize(width,height);  
        panel.setVisible(true);
		panel.setOpaque(false);
		BufferedImage bimage = panel.bufferedImage;
		bimage = scaleImage(bimage);
		return bimage;
	}
 	
}