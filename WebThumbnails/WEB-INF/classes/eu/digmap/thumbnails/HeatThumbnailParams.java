package eu.digmap.thumbnails;

public class HeatThumbnailParams extends ThumbnailParams {
	public double[][] data = HeatMapPanel.generateRandomData();

	public HeatThumbnailParams(String url, int width, int height, byte t) {
		super(null,null,width,height,t);
	}
	
	public HeatThumbnailParams(String url, int width, int height, byte t, String data) {
		this(url,width,height,t);
		this.data=HeatMapPanel.generateData(data);
	}
}
