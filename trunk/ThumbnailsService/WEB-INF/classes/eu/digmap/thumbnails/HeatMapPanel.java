package eu.digmap.thumbnails;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * 
 * HeatMapPanel is a JPanel that displays a 2-dimensional array of data using a
 * selected color gradient scheme. For specifying data, the first index into the
 * double[][] array is the x-coordinate, and the second index is the
 * y-coordinate.
 * 
 * Adapted from original HeatMap code by Matthew Beckler and Josh Hayes-Sheen
 * 
 * @author Bruno Martins (bgmartins@gmail.com)
 * @version 1.5
 */
public class HeatMapPanel extends JPanel {

	private double[][] data;
	private Color[][] dataColors;

	// these four variables are used to print the axis labels
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;

	private String title;
	private String xAxis;
	private String yAxis;

	private boolean drawTitle = false;
	private boolean drawXTitle = false;
	private boolean drawYTitle = false;
	private boolean drawLegend = false;
	private boolean drawXTicks = false;
	private boolean drawYTicks = false;

	private Color[] colors;
	private Color bg = Color.white;
	private Color fg = Color.black;

	public BufferedImage bufferedImage;
	private Graphics2D bufferedGraphics;

	/**
	 * Produces a gradient using the University of Minnesota's school colors,
	 * from maroon (low) to gold (high)
	 */
	public final static Color[] GRADIENT_MAROON_TO_GOLD = createGradient(
			new Color(0xA0, 0x00, 0x00), new Color(0xFF, 0xFF, 0x00), 500);

	/**
	 * Produces a gradient from blue (low) to red (high)
	 */
	public final static Color[] GRADIENT_BLUE_TO_RED = createGradient(
			Color.BLUE, Color.RED, 500);

	/**
	 * Produces a gradient from black (low) to white (high)
	 */
	public final static Color[] GRADIENT_BLACK_TO_WHITE = createGradient(
			Color.BLACK, Color.WHITE, 500);

	/**
	 * Produces a gradient from white (low) to red (high)
	 */
	public final static Color[] GRADIENT_WHITE_TO_BLACK = createGradient(
			Color.WHITE, Color.BLACK, 500);

	/**
	 * Produces a gradient from white (low) to red (high)
	 */
	public final static Color[] GRADIENT_WHITE_TO_RED = createGradient(
			Color.WHITE, Color.RED, 500);

	/**
	 * Produces a gradient from red (low) to green (high)
	 */
	public final static Color[] GRADIENT_RED_TO_GREEN = createGradient(
			Color.RED, Color.GREEN, 500);

	/**
	 * Produces a gradient through green, yellow, orange, red
	 */
	public final static Color[] GRADIENT_GREEN_YELLOW_ORANGE_RED = createMultiGradient(
			new Color[] { Color.green, Color.yellow, Color.orange, Color.red },
			500);

	/**
	 * Produces a gradient through the rainbow: violet, blue, green, yellow,
	 * orange, red
	 */
	public final static Color[] GRADIENT_RAINBOW = createMultiGradient(
			new Color[] { new Color(181, 32, 255), Color.blue, Color.green,
					Color.yellow, Color.orange, Color.red }, 500);

	/**
	 * Produces a gradient for hot things (black, red, orange, yellow, white)
	 */
	public final static Color[] GRADIENT_HOT = createMultiGradient(new Color[] {
			Color.black, new Color(87, 0, 0), Color.red, Color.orange,
			Color.yellow, Color.white }, 500);

	/**
	 * Produces a different gradient for hot things (black, brown, orange,
	 * white)
	 */
	public final static Color[] GRADIENT_HEAT = createMultiGradient(
			new Color[] { Color.black, new Color(105, 0, 0),
					new Color(192, 23, 0), new Color(255, 150, 38), Color.white },
			500);

	/**
	 * Produces a gradient through red, orange, yellow
	 */
	public final static Color[] GRADIENT_ROY = createMultiGradient(new Color[] {
			Color.red, Color.orange, Color.yellow }, 500);

	/**
	 * @param data
	 *            The data to display, must be a complete array (non-ragged)
	 * @param colors
	 *            A variable of the type Color[]. See also
	 *            {@link #createMultiGradient} and {@link #createGradient}.
	 */
	public HeatMapPanel(double[][] data, Color[] colors) {
		super();
		this.data = data;
		updateGradient(colors);
		updateData(data);
		this.setPreferredSize(new Dimension(60 + data.length,
				60 + data[0].length));
		this.setDoubleBuffered(true);
		this.bg = Color.white;
		this.fg = Color.black;
		drawData();
	}

	/**
	 * Specify the coordinate bounds for the map. Only used for the axis labels,
	 * which must be enabled seperately. Calls repaint() when finished.
	 * 
	 * @param xMin
	 *            The lower bound of x-values, used for axis labels
	 * @param xMax
	 *            The upper bound of x-values, used for axis labels
	 */
	public void setCoordinateBounds(double xMin, double xMax, double yMin,
			double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		repaint();
	}

	/**
	 * Specify the coordinate bounds for the X-range. Only used for the axis
	 * labels, which must be enabled seperately. Calls repaint() when finished.
	 * 
	 * @param xMin
	 *            The lower bound of x-values, used for axis labels
	 * @param xMax
	 *            The upper bound of x-values, used for axis labels
	 */
	public void setXCoordinateBounds(double xMin, double xMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		repaint();
	}

	/**
	 * Specify the coordinate bounds for the Y-range. Only used for the axis
	 * labels, which must be enabled seperately. Calls repaint() when finished.
	 * 
	 * @param yMin
	 *            The lower bound of y-values, used for axis labels
	 * @param yMax
	 *            The upper bound of y-values, used for axis labels
	 */
	public void setYCoordinateBounds(double yMin, double yMax) {
		this.yMin = yMin;
		this.yMax = yMax;
		repaint();
	}

	/**
	 * Updates the title. Calls repaint() when finished.
	 * 
	 * @param title
	 *            The new title
	 */
	public void setTitle(String title) {
		this.title = title;
		repaint();
	}

	/**
	 * Updates the state of the title. Calls repaint() when finished.
	 * 
	 * @param drawTitle
	 *            Specifies if the title should be drawn
	 */
	public void setDrawTitle(boolean drawTitle) {
		this.drawTitle = drawTitle;
		repaint();
	}

	/**
	 * Updates the X-Axis title. Calls repaint() when finished.
	 * 
	 * @param xAxisTitle
	 *            The new X-Axis title
	 */
	public void setXAxisTitle(String xAxisTitle) {
		this.xAxis = xAxisTitle;
		repaint();
	}

	/**
	 * Updates the state of the X-Axis Title. Calls repaint() when finished.
	 * 
	 * @param drawXAxisTitle
	 *            Specifies if the X-Axis title should be drawn
	 */
	public void setDrawXAxisTitle(boolean drawXAxisTitle) {
		this.drawXTitle = drawXAxisTitle;
		repaint();
	}

	/**
	 * Updates the Y-Axis title. Calls repaint() when finished.
	 * 
	 * @param yAxisTitle
	 *            The new Y-Axis title
	 */
	public void setYAxisTitle(String yAxisTitle) {
		this.yAxis = yAxisTitle;
		repaint();
	}

	/**
	 * Updates the state of the Y-Axis Title. Calls repaint() when finished.
	 * 
	 * @param drawYAxisTitle
	 *            Specifies if the Y-Axis title should be drawn
	 */
	public void setDrawYAxisTitle(boolean drawYAxisTitle) {
		this.drawYTitle = drawYAxisTitle;
		repaint();
	}

	/**
	 * Updates the state of the legend. Calls repaint() when finished.
	 * 
	 * @param drawLegend
	 *            Specifies if the legend should be drawn
	 */
	public void setDrawLegend(boolean drawLegend) {
		this.drawLegend = drawLegend;
		repaint();
	}

	/**
	 * Updates the state of the X-Axis ticks. Calls repaint() when finished.
	 * 
	 * @param drawXTicks
	 *            Specifies if the X-Axis ticks should be drawn
	 */
	public void setDrawXTicks(boolean drawXTicks) {
		this.drawXTicks = drawXTicks;

		repaint();
	}

	/**
	 * Updates the state of the Y-Axis ticks. Calls repaint() when finished.
	 * 
	 * @param drawYTicks
	 *            Specifies if the Y-Axis ticks should be drawn
	 */
	public void setDrawYTicks(boolean drawYTicks) {
		this.drawYTicks = drawYTicks;

		repaint();
	}

	/**
	 * Updates the foreground color. Calls repaint() when finished.
	 * 
	 * @param fg
	 *            Specifies the desired foreground color
	 */
	public void setColorForeground(Color fg) {
		this.fg = fg;

		repaint();
	}

	/**
	 * Updates the background color. Calls repaint() when finished.
	 * 
	 * @param bg
	 *            Specifies the desired background color
	 */
	public void setColorBackground(Color bg) {
		this.bg = bg;

		repaint();
	}

	/**
	 * Updates the gradient used to display the data. Calls drawData() and
	 * repaint() when finished.
	 * 
	 * @param colors
	 *            A variable of type Color[]
	 */
	public void updateGradient(Color[] colors) {
		this.colors = (Color[]) colors.clone();
		updateDataColors();
		drawData();
		repaint();
	}

	/**
	 * This uses the current array of colors that make up the gradient, and
	 * assigns a color to each data point, stored in the dataColors array, which
	 * is used by the drawData() method to plot the points.
	 */
	private void updateDataColors() {
		// We need to find the range of the data values,
		// in order to assign proper colors.
		double largest = Double.MIN_VALUE;
		double smallest = Double.MAX_VALUE;
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				largest = Math.max(data[x][y], largest);
				smallest = Math.min(data[x][y], smallest);
			}
		}
		double range = largest - smallest;

		// dataColors is the same size as the data array
		dataColors = new Color[data.length][data[0].length];

		// assign a Color to each data point
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[0].length; y++) {
				double norm = (data[x][y] - smallest) / range; // 0 < norm < 1
				int color = (int) Math.floor(norm * (colors.length - 1));
				dataColors[x][y] = colors[color];
			}
		}
	}
	
	/**
	 * Creates an array of Color objects for use as a gradient, using a linear
	 * interpolation between the two specified colors.
	 * 
	 * @param one
	 *            Color used for the bottom of the gradient
	 * @param two
	 *            Color used for the top of the gradient
	 * @param numSteps
	 *            The number of steps in the gradient. 250 is a good number.
	 */
	public static Color[] createGradient(Color one, Color two, int numSteps) {
		int r1 = one.getRed();
		int g1 = one.getGreen();
		int b1 = one.getBlue();

		int r2 = two.getRed();
		int g2 = two.getGreen();
		int b2 = two.getBlue();

		int newR = 0;
		int newG = 0;
		int newB = 0;

		Color[] gradient = new Color[numSteps];
		double iNorm;
		for (int i = 0; i < numSteps; i++) {
			iNorm = i / (double) numSteps; // a normalized [0:1] variable
			newR = (int) (r1 + iNorm * (r2 - r1));
			newG = (int) (g1 + iNorm * (g2 - g1));
			newB = (int) (b1 + iNorm * (b2 - b1));
			gradient[i] = new Color(newR, newG, newB);
		}

		return gradient;
	}

	/**
	 * Creates an array of Color objects for use as a gradient, using an array
	 * of Color objects. It uses a linear interpolation between each pair of
	 * points.
	 * 
	 * @param colors
	 *            An array of Color objects used for the gradient. The Color at
	 *            index 0 will be the lowest color.
	 * @param numSteps
	 *            The number of steps in the gradient. 250 is a good number.
	 */
	public static Color[] createMultiGradient(Color[] colors, int numSteps) {
		// we assume a linear gradient, with equal spacing between colors
		// The final gradient will be made up of n 'sections', where n =
		// colors.length - 1
		int numSections = colors.length - 1;
		int gradientIndex = 0; // points to the next open spot in the final
								// gradient
		Color[] gradient = new Color[numSteps];
		Color[] temp;

		if (numSections <= 0) {
			throw new IllegalArgumentException(
					"You must pass in at least 2 colors in the array!");
		}

		for (int section = 0; section < numSections; section++) {
			// we divide the gradient into (n - 1) sections, and do a regular
			// gradient for each
			temp = createGradient(colors[section], colors[section + 1],
					numSteps / numSections);
			for (int i = 0; i < temp.length; i++) {
				// copy the sub-gradient into the overall gradient
				gradient[gradientIndex++] = temp[i];
			}
		}

		if (gradientIndex < numSteps) {
			// The rounding didn't work out in our favor, and there is at least
			// one unfilled slot in the gradient[] array.
			// We can just copy the final color there
			for (/* nothing to initialize */; gradientIndex < numSteps; gradientIndex++) {
				gradient[gradientIndex] = colors[colors.length - 1];
			}
		}

		return gradient;
	}

	/**
	 * This function generates an appropriate data array for display. It uses
	 * the function: z = sin(x)*cos(y). The parameter specifies the number of
	 * data points in each direction, producing a square matrix.
	 * 
	 * @param dimension
	 *            Size of each side of the returned array
	 * @return double[][] calculated values of z = sin(x)*cos(y)
	 */
	public static double[][] generateSinCosData(int dimension) {
		if (dimension % 2 == 0) {
			dimension++; // make it better
		}

		double[][] data = new double[dimension][dimension];
		double sX, sY; // s for 'Scaled'

		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				sX = 2 * Math.PI * (x / (double) dimension); // 0 < sX < 2 *
																// Pi
				sY = 2 * Math.PI * (y / (double) dimension); // 0 < sY < 2 *
																// Pi
				data[x][y] = Math.sin(sX) * Math.cos(sY);
			}
		}

		return data;
	}

	/**
	 * This function generates an appropriate data array for display. It uses
	 * the function: z = Math.cos(Math.abs(sX) + Math.abs(sY)). The parameter
	 * specifies the number of data points in each direction, producing a square
	 * matrix.
	 * 
	 * @param dimension
	 *            Size of each side of the returned array
	 * @return double[][] calculated values of z = Math.cos(Math.abs(sX) +
	 *         Math.abs(sY));
	 */
	public static double[][] generatePyramidData(int dimension) {
		if (dimension % 2 == 0) {
			dimension++; // make it better
		}

		double[][] data = new double[dimension][dimension];
		double sX, sY; // s for 'Scaled'

		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				sX = 6 * (x / (double) dimension); // 0 < sX < 6
				sY = 6 * (y / (double) dimension); // 0 < sY < 6
				sX = sX - 3; // -3 < sX < 3
				sY = sY - 3; // -3 < sY < 3
				data[x][y] = Math.cos(Math.abs(sX) + Math.abs(sY));
			}
		}

		return data;
	}

	public static double[][] generateRandomData() {
		Double lon[] = new Double[10];
		Double lat[] = new Double[10];
		Double values[] = new Double[10];
		for (int i = 0; i < values.length; i++) {
			lat[i] = (Math.random() * 180) - 90;
			lon[i] = (Math.random() * 360) - 180;
			values[i] = Math.random();
		}
		return generateData(lat, lon, values);
	}

	public static double[][] generateData(String data) {
		java.util.List<Double> lat = new ArrayList<Double>();
		java.util.List<Double> lon = new ArrayList<Double>();
		java.util.List<Double> values = new ArrayList<Double>();
		String aux[] = data.split(";");
		for (int i = 0; i < aux.length; i++) {
			String aux2[] = aux[i].split(",");
			lat.add(new Double(aux2[0].trim()));
			lon.add(new Double(aux2[1].trim()));
			values.add(new Double(aux2[2].trim()));
		}
		return generateData(lat.toArray(new Double[0]), lon
				.toArray(new Double[0]), values.toArray(new Double[0]));
	}

	public static double[][] generateData(Double lat[], Double lon[],
			Double values[]) {
		Map<Point, Double> vals = new HashMap<Point, Double>();
		for (int i = 0; i < values.length; i++) {
			int x = (int) (lon[i] + 180);
			int y = (int) (lat[i] + 90);
			vals.put(new Point(x, y), values[i]);
		}
		double[][] data = new double[361][181];
		for (int x = 0; x < 361; x++) {
			for (int y = 0; y < 181; y++) {
				data[x][y] = vals.get(new Point(x, y)) == null ? 0 : vals
						.get(new Point(x, y));
			}
		}
		for (int k = 0; k < 150; k++)
			for (int x = 0; x < 361; x++) {
				for (int y = 1; y < 180; y++) {
					int xp1 = x + 1 <= 360 ? x + 1 : 0;
					int xm1 = x - 1 >= 0 ? x - 1 : 360;
					double avg = ((data[x][y] + data[xm1][y + 0]
							+ data[xp1][y + 0] + data[x][y - 1]
							+ data[x][y + 1] + data[xm1][y + 1]
							+ data[xp1][y + 1] + data[xp1][y - 1] + data[xm1][y - 1]) / 9.0);
					data[x][y] = vals.get(new Point(x, y)) == null ? avg : vals
							.get(new Point(x, y));
				}
			}
		return data;
	}

	/**
	 * Updates the data display, calls drawData() to do the expensive re-drawing
	 * of the data plot, and then calls repaint().
	 * 
	 * @param data
	 *            The data to display, must be a complete array (non-ragged)
	 */
	public void updateData(double[][] data) {
		this.data = data;
		updateDataColors();
		drawData();
		repaint();
	}

	/**
	 * Creates a BufferedImage of the actual data plot.
	 * 
	 * After doing some profiling, it was discovered that 90% of the drawing
	 * time was spend drawing the actual data (not on the axes or tick marks).
	 * Since the Graphics2D has a drawImage method that can do scaling, we are
	 * using that instead of scaling it ourselves. We only need to draw the data
	 * into the bufferedImage on startup, or if the data or gradient changes.
	 * This saves us an enormous amount of time. Thanks to Josh Hayes-Sheen
	 * (grey@grevian.org) for the suggestion and initial code to use the
	 * BufferedImage technique.
	 * 
	 * Since the scaling of the data plot will be handled by the drawImage in
	 * paintComponent, we take the easy way out and draw our bufferedImage with
	 * 1 pixel per data point. Too bad there isn't a setPixel method in the
	 * Graphics2D class, it seems a bit silly to fill a rectangle just to set a
	 * single pixel...
	 * 
	 * This function should be called whenever the data or the gradient changes.
	 */
	private void drawData() {
		bufferedImage = new BufferedImage(data.length, data[0].length,
				BufferedImage.TYPE_INT_RGB);
		bufferedGraphics = bufferedImage.createGraphics();
		for (int x = 0; x < data.length; x++) {
			for (int y = data[0].length - 1; y >= 0; y--) {
				bufferedGraphics.setColor(dataColors[x][y]);
				bufferedGraphics.fillRect((int) Math.ceil(x), (int) Math
						.ceil(data[0].length - y - 1), 1, 1);
			}
		}
	}

	/**
	 * The overridden painting method, now optimized to simply draw the data
	 * plot to the screen, letting the drawImage method do the resizing. This
	 * saves an extreme amount of time.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		int width = this.getWidth();
		int height = this.getHeight();
		this.setOpaque(true);
		// clear the panel
		g2d.setColor(bg);
		g2d.fillRect(0, 0, width, height);
		// draw the heat map
		if (bufferedImage == null) {
			// Ideally, we only to call drawData in the constructor, or if we
			// change the data or gradients. We include this just to be safe.
			drawData();
		}
		// The data plot itself is drawn with 1 pixel per data point, and the
		// drawImage method scales that up to fit our current window size. This
		// is very fast, and is much faster than the previous version, which
		// redrew the data plot each time we had to repaint the screen.
		g2d.drawImage(bufferedImage, 31, 31, width - 30, height - 30, 0, 0,
				bufferedImage.getWidth(), bufferedImage.getHeight(), null);

		// border
		g2d.setColor(fg);
		g2d.drawRect(30, 30, width - 60, height - 60);

		// title
		if (drawTitle && title != null) {
			g2d.drawString(title, (width / 2) - 4 * title.length(), 20);
		}

		// axis ticks - ticks start even with the bottom left coner, end very
		// close to end of line (might not be right on)
		int numXTicks = (width - 60) / 50;
		int numYTicks = (height - 60) / 50;

		String label = "";
		DecimalFormat df = new DecimalFormat("##.##");

		// Y-Axis ticks
		if (drawYTicks) {
			int yDist = (int) ((height - 60) / (double) numYTicks); // distance
																	// between
																	// ticks
			for (int y = 0; y <= numYTicks; y++) {
				g2d.drawLine(26, height - 30 - y * yDist, 30, height - 30 - y
						* yDist);
				label = df.format(((y / (double) numYTicks) * (yMax - yMin))
						+ yMin);
				int labelY = height - 30 - y * yDist - 4 * label.length();
				// to get the text to fit nicely, we need to rotate the graphics
				g2d.rotate(Math.PI / 2);
				g2d.drawString(label, labelY, -14);
				g2d.rotate(-Math.PI / 2);
			}
		}

		// Y-Axis title
		if (drawYTitle && yAxis != null) {
			// to get the text to fit nicely, we need to rotate the graphics
			g2d.rotate(Math.PI / 2);
			g2d.drawString(yAxis, (height / 2) - 4 * yAxis.length(), -3);
			g2d.rotate(-Math.PI / 2);
		}

		// X-Axis ticks
		if (drawXTicks) {
			int xDist = (int) ((width - 60) / (double) numXTicks); // distance
																	// between
																	// ticks
			for (int x = 0; x <= numXTicks; x++) {
				g2d.drawLine(30 + x * xDist, height - 30, 30 + x * xDist,
						height - 26);
				label = df.format(((x / (double) numXTicks) * (xMax - xMin))
						+ xMin);
				int labelX = (31 + x * xDist) - 4 * label.length();
				g2d.drawString(label, labelX, height - 14);
			}
		}

		// X-Axis title
		if (drawXTitle && xAxis != null) {
			g2d.drawString(xAxis, (width / 2) - 4 * xAxis.length(), height - 3);
		}

		// Legend
		if (drawLegend) {
			g2d.drawRect(width - 20, 30, 10, height - 60);
			for (int y = 0; y < height - 61; y++) {
				int yStart = height
						- 31
						- (int) Math.ceil(y
								* ((height - 60) / (colors.length * 1.0)));
				yStart = height - 31 - y;
				g2d
						.setColor(colors[(int) ((y / (double) (height - 60)) * (colors.length * 1.0))]);
				g2d.fillRect(width - 19, yStart, 9, 1);
			}
		}

	}
}
