package eu.digmap.thumbnails;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * MapGenerator is a light-weight java applet which allows users to create a
 * geographical image of a data set overlayed over a map. The applet provides
 * the user with many options to represent the data set. Basically, MapGenerator
 * plots a set of nodes and a set of lines that connect these nodes on an image
 * specified by the user.
 * 
 * The MapGenerator code is loosely based on the GeoPlot tool developed by Ram
 * Periakaruppan at the University of California, San Diego
 */
public class MapGenerator extends Applet implements MouseMotionListener,
		MouseListener, Runnable {

	BufferedImage map = null;

	BufferedImage mapbuffer = null;

	boolean error = false;

	boolean colorkey = false;

	boolean sizekey = false;

	boolean mapLoaded = false;

	String title;

	String date;

	static Color fgcolor;

	int default_node_size;

	int default_line_size;

	String default_node_color;

	String default_line_color;

	int fontSize;

	int refresh_interval;

	boolean hide_key;

	Thread t;
	float aLat;

	float aLon;

	float bLat;

	float bLon;

	float top_lat = 0;

	float top_lon = 0;

	float bot_lat = 0;

	float bot_lon = 0;

	int Ckey_x = 0;

	int Ckey_y = 0;

	int Ckey_width = 100;

	int Ckey_height = 100;
	int Ckey_fontSize = 13;

	String Ckey_title = "";

	int Skey_x = 0;

	int Skey_y = 0;

	int Skey_width = 100;
	int Skey_height = 100;
	int Skey_fontSize = 13;

	String Skey_title = "";

	String data;

	Vector lines = new Vector();

	Vector color_key = new Vector();

	Vector size_key = new Vector();

	Vector matched_regions = new Vector();

	Hashtable nodes = new Hashtable();

	Hashtable lineMatrix = new Hashtable();

	Node node;
	Line line;
	Key key;

	static Rectangle r;

	Graphics gc;

	FontMetrics fm;

	StatusBar sb;
	boolean mouseOver = false;

	boolean bufferChanged = false;

	NodLin nodLin = null;
	Point mouseP = new Point();

	MediaTracker tracker;

	boolean paintDataCalled = false;

	final static int quadone = 0;
	final static int quadtwo = 1;
	final static int quadthree = 2;
	final static int quadfour = 3;
	final static int quadfive = 4;
	final static int quadsix = 5;
	final static int quadseven = 6;
	final static int quadeight = 7;
	final static int quadfivesix = 56;
	final static int quadtwoone = 21;
	final static int quadsevenzero = 70;
	final static int quadfourthree = 43;

	final static int KEY_DATA_SIZE = 3;

	static BufferedImage mapcacheOld = readImage("http://127.0.0.1:8080/nail.map/OldWorldMap.gif");
	static BufferedImage mapcacheRelief = readImage("http://127.0.0.1:8080/nail.map/ReliefWorldMap.gif");
	static BufferedImage mapcacheSimple = readImage("http://127.0.0.1:8080/nail.map/SimpleWorldMap.gif");

	public static BufferedImage readImage(String aux) {
		try {
			return ImageIO.read(new URL(aux));
		} catch (Exception e) {
			return null;
		}
	}

	public String getAppletInfo() {
		return "A general purpose Geographical Visualization Tool";
	}

	public void init() {
		String param;

		String str;

		Color bgcolor;

		StringTokenizer tokenizer;

		StringTokenizer fields;

		sb = new StatusBar(this);

		r = getBounds();
		// TODO: check if applet works without mapbuffer = createImage(r.width,
		// r.height);
		mapbuffer = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);

		gc = mapbuffer.getGraphics();
		tracker = new MediaTracker(this);

		addMouseMotionListener(this);
		addMouseListener(this);

		param = getParameter("mapname");
		if (param != null) {

			if (param.toLowerCase().startsWith("http://")) {
				try {
					if (param
							.equals("http://127.0.0.1:8080/nail.map/OldWorldMap.gif")
							&& mapcacheOld != null)
						map = mapcacheOld;
					else if (param
							.equals("http://127.0.0.1:8080/nail.map/ReliefWorldMap.gif")
							&& mapcacheRelief != null)
						map = mapcacheRelief;
					else if (param
							.equals("http://127.0.0.1:8080/nail.map/SimpleWorldMap.gif")
							&& mapcacheSimple != null)
						map = mapcacheSimple;
					else
						map = readImage(param);
				} catch (Exception e) {
					error = true;
					repaint();
				}
			} else {
				map = toBufferedImage(getImage(getDocumentBase(), param));
			}

			tracker.addImage(map, 1);

			param = getParameter("bgcolor");
			if (param != null) {
				bgcolor = NodLin.getColor(param);
				if (bgcolor == null) {
					bgcolor = Color.white;
				}
			} else {
				bgcolor = Color.white;
			}

			setBackground(bgcolor);

			param = getParameter("fgcolor");
			if (param != null) {
				fgcolor = NodLin.getColor(param);
				if (bgcolor == null) {
					fgcolor = Color.black;
				}
			} else {
				fgcolor = Color.black;
			}

			param = getParameter("title");
			if (param != null) {
				title = param;
			} else {
				title = "";
			}

			param = getParameter("date");
			if (param != null) {
				date = param;
			} else {
				date = "";
			}

			param = getParameter("refresh_interval");
			try {
				if (param != null) {
					refresh_interval = Integer.valueOf(param).intValue();
				} else {
					refresh_interval = 0;
				}
			} catch (NumberFormatException e) {
				refresh_interval = 0;
			}

			param = getParameter("default_node_size");
			try {
				if (param != null) {
					default_node_size = Integer.valueOf(param).intValue();
				} else {
					default_node_size = 6;
				}
			} catch (NumberFormatException e) {
				System.err
						.println("Handle numberformatexception default_node_size");
			}

			param = getParameter("default_line_size");
			try {
				if (param != null) {
					default_line_size = Integer.valueOf(param).intValue();
				} else {
					default_line_size = 1;
				}
			} catch (NumberFormatException e) {
				System.err
						.println("Handle numberformatexception default_line_size");
			}

			param = getParameter("default_node_color");
			if (param != null) {
				default_node_color = param;
			} else {
				default_node_color = "black";
			}

			param = getParameter("default_line_color");
			if (param != null) {
				default_line_color = param;
			} else {
				default_line_color = "red";
			}

			param = getParameter("default_font_size");
			try {
				if (param != null) {
					fontSize = Integer.valueOf(param).intValue();
				} else {
					fontSize = 10;
				}
			} catch (NumberFormatException e) {
				fontSize = 10;
			}

			param = getParameter("hide_key");
			hide_key = new Boolean(param).booleanValue();

			param = getParameter("top_lat");
			try {
				if (param != null) {
					top_lat = Float.valueOf(param).floatValue();
				} else {
					error = true;
					repaint();
				}
			} catch (NumberFormatException e) {
				System.err.println("Handle numberformatexception top_lat");
			}

			param = getParameter("top_lon");
			try {
				if (param != null) {
					top_lon = Float.valueOf(param).floatValue();
				} else {
					error = true;
					repaint();
				}
			} catch (NumberFormatException e) {
				System.err.println("Handle numberformatexception top_lon");
			}

			param = getParameter("bot_lat");
			try {
				if (param != null) {
					bot_lat = Float.valueOf(param).floatValue();
				} else {
					error = true;
					repaint();
				}
			} catch (NumberFormatException e) {
				System.err.println("Handle numberformatexception bot_lat");
			}

			param = getParameter("bot_lon");
			try {
				if (param != null) {
					bot_lon = Float.valueOf(param).floatValue();
				} else {
					error = true;
					repaint();
				}
			} catch (NumberFormatException e) {
				System.err.println("Handle numberformatexception bot_lon");
			}

			param = getParameter("color_key");
			if (param != null) {
				colorkey = true;
				tokenizer = new StringTokenizer(param, ";");
				while (tokenizer.hasMoreTokens()) {
					str = tokenizer.nextToken();

					if (str.startsWith("key")) {
						char ch;
						StringTokenizer key;
						fields = new StringTokenizer(str, ",");
						fields.nextToken();

						while (fields.hasMoreTokens()) {
							str = fields.nextToken();
							str = str.trim();
							ch = Character.toLowerCase(str.charAt(0));

							try {
								key = new StringTokenizer(str, ":");
								key.nextToken();

								switch (ch) {
								case 'x':
									try {
										Ckey_x = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'y':
									try {
										Ckey_y = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'w':
									try {
										Ckey_width = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'h':
									try {
										Ckey_height = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 't':
									try {
										Ckey_title = key.nextToken();
									} catch (NoSuchElementException nsee) {
									}
									break;

								case 's':
									try {
										Ckey_fontSize = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;
								}
							} catch (NoSuchElementException nsee) {
							}
						}
					} else {
						color_key.addElement(new Key(str.trim()));
					}
				}
			}

			param = getParameter("size_key");
			if (param != null) {
				sizekey = true;
				tokenizer = new StringTokenizer(param, ";");
				while (tokenizer.hasMoreTokens()) {
					str = tokenizer.nextToken();

					if (str.startsWith("key")) {
						char ch;
						StringTokenizer key;
						fields = new StringTokenizer(str, ",");
						fields.nextToken();

						while (fields.hasMoreTokens()) {
							str = fields.nextToken();
							str = str.trim();
							ch = Character.toLowerCase(str.charAt(0));

							try {
								key = new StringTokenizer(str, ":");
								key.nextToken();

								switch (ch) {
								case 'x':
									try {
										Skey_x = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'y':
									try {
										Skey_y = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'w':
									try {
										Skey_width = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 'h':
									try {
										Skey_height = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;

								case 't':
									try {
										Skey_title = key.nextToken();
									} catch (NoSuchElementException nsee) {
									}
									break;

								case 's':
									try {
										Skey_fontSize = Integer.valueOf(
												key.nextToken()).intValue();
									} catch (NumberFormatException nfe) {
									}
									break;
								}
							} catch (NoSuchElementException nsee) {
							}
						}
					} else {
						size_key.addElement(new Key(str.trim()));
					}
				}
			}

			param = getParameter("data");
			if (param != null) {
				data = param;
				parseData(data);
			} else {

			}

			buildLineMatrix();

			bLon = top_lon;
			bLat = top_lat;
			aLon = (bot_lon - top_lon) / r.width;
			aLat = (bot_lat - top_lat) / r.height;
		} else {

			error = true;
			repaint();
		}
	}

	private void parseData(String locData) {
		String str;

		String field[] = new String[4];

		StringTokenizer tokenizer;

		StringTokenizer fields;

		if (locData.toLowerCase().startsWith("http://")) {
			int c;

			URL data;
			URLConnection dataCon;
			StringBuffer sb;

			try {
				data = new URL(locData);
				dataCon = data.openConnection();

				int len = dataCon.getContentLength();

				if (len > 0) {
					sb = new StringBuffer(len);
					InputStream input = dataCon.getInputStream();
					int i = len;
					while (((c = input.read()) != -1) && (--i > 0)) {
						sb.append((char) c);
					}
					locData = sb.toString();
					input.close();
				}
			} catch (Exception e) {
				locData = null;
			}
		}

		if (locData != null) {
			int numofTokens = 0;

			int numofNodes = 0;
			char ch;

			tokenizer = new StringTokenizer(locData, ";");
			while (tokenizer.hasMoreTokens()) {
				str = tokenizer.nextToken();
				str = str.trim();
				ch = Character.toLowerCase(str.charAt(0));
				if (ch == 'n') {
					fields = new StringTokenizer(str);
					numofTokens = fields.countTokens();
					fields.nextToken();
					if (numofTokens < 4) {
						// Bad data handle case
					} else {
						for (int i = 0; i < 3; i++) {
							field[i] = fields.nextToken();
						}

						field[3] = "";

						while (fields.hasMoreTokens()) {
							field[3] = field[3] + " " + fields.nextToken();
						}

						if (field[3].length() > 0) {
							node = new Node(numofNodes++, field[1], field[2],
									default_node_size, default_node_color,
									field[3]);
						} else {
							node = new Node(numofNodes++, field[1], field[2],
									default_node_size, default_node_color);
						}

						nodes.put(field[0], node);
					}
				} else if (ch == 'l') {
					fields = new StringTokenizer(str);
					numofTokens = fields.countTokens();
					fields.nextToken();
					if (numofTokens < 3) {
						// Bad data handle case
					} else {
						for (int i = 0; i < 2; i++) {
							field[i] = fields.nextToken();
						}
						field[2] = "";
						while (fields.hasMoreTokens()) {
							field[2] = field[2] + " " + fields.nextToken();
						}

						if (field[2].length() > 0) {
							line = new Line(field[0], field[1],
									default_line_size, default_line_color,
									field[2]);
						} else {
							line = new Line(field[0], field[1],
									default_line_size, default_line_color);
						}

						lines.addElement(line);
					}
				} else {

				}
			}
		}
	}

	public void run() {
		lines.removeAllElements();
		nodes.clear();
		lineMatrix.clear();
		mapbuffer = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
		gc = mapbuffer.getGraphics();
		parseData(data);
		buildLineMatrix();
		paintData(gc);
		t = new Thread(this);
		t.start();
	}

	public void start() {
		if (refresh_interval > 0) {
			t = new Thread(this);
			t.start();
		}
	}

	public void stop() {
		if (refresh_interval > 0) {
			t.stop();
			t = null;
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		// xxx - pending error flag is also set for bad lat/lons.

		if (error) {
			String errorMsg = "Image Could Not Be Loaded";
			g.setColor(Color.red);
			g.fillRect(0, 0, r.width, r.height);
			g.setColor(Color.black);
			Font font = new Font("Dialog", Font.BOLD, 20);
			g.setFont(font);
			fm = g.getFontMetrics();
			g.drawString(errorMsg, r.width / 2 - fm.stringWidth(errorMsg) / 2,
					r.height / 2);
		} else {

			if (!mapLoaded) {
				g.drawImage(map, 0, 0, r.width, r.height, this);

				if (tracker.checkID(1, true)) {
					if (!paintDataCalled) {
						paintData(gc);
					}
					mapLoaded = true;
				}
			} else if (mouseOver)

			{
				drawNodLin(g);
			} else

			{
				g.drawImage(mapbuffer, 0, 0, r.width, r.height, this);
			}
		}
	}

	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if ((flags & ALLBITS) != 0) // entire image loaded
		{
			repaint();
			if (!paintDataCalled) {
				paintData(gc);
			}
			mapLoaded = true;
		} else if ((flags & SOMEBITS) != 0) // new partial data
		{
			repaint(x, y, w, h); // paint new pixels
		} else if ((flags & (ABORT | ERROR)) != 0) {
			error = true; // file not found
			repaint();
		}

		return (flags & (ALLBITS | ABORT | ERROR)) == 0;
	}

	private int getNodeLineSize(NodLin ln, int numofSkeys) {
		int nodlinSize = ln.getSize();
		if (sizekey && ln.getSValue() != Float.POSITIVE_INFINITY) {
			for (int sk = 0; sk < numofSkeys; sk++) {
				key = (Key) size_key.elementAt(sk);
				if (key.withinbounds(ln.getSValue())) {
					try {
						nodlinSize = Integer.valueOf(key.getValue()).intValue();
					} catch (NumberFormatException nfe) {
					}
					break;
				}
			}
		}

		return nodlinSize;
	}

	private void setColor(Graphics g, NodLin ln, int numofCkeys) {
		g.setColor(ln.getColor());

		if (colorkey && ln.getCValue() != Float.POSITIVE_INFINITY) {
			for (int ck = 0; ck < numofCkeys; ck++) {
				key = (Key) color_key.elementAt(ck);
				if (key.withinbounds(ln.getCValue())) {
					setColor(g, key.getValue());
					break;
				}
			}
		}
	}

	private void setColor(Graphics g, String color) {
		StringTokenizer tokenizer;

		if (!color.substring(0, 3).toLowerCase().startsWith("rgb")) {
			g.setColor((Color) NodLin.colormap.get(color));
		} else {
			tokenizer = new StringTokenizer(color, "-");
			if (tokenizer.countTokens() < 4) {
				g.setColor(fgcolor);
				return;
			}

			tokenizer.nextToken();
			try {
				g.setColor(new Color(Integer.valueOf(tokenizer.nextToken())
						.intValue(), Integer.valueOf(tokenizer.nextToken())
						.intValue(), Integer.valueOf(tokenizer.nextToken())
						.intValue()));
			} catch (NumberFormatException e) {
				System.err
						.println("Handle numberformatexception color_key setting color");
				g.setColor(fgcolor);
				return;
			}
		}
	}

	private static Point[] getRectPoints(Point p1, Point p2, float minusH,
			float plusH) {
		Point p[] = new Point[2];
		p[0] = new Point(0, 0);
		p[1] = new Point(0, 0);

		float angle = new Double(Math.PI / 2.0).floatValue();

		double theta = Math.atan(new Float(Math.abs(p1.y - p2.y)).floatValue()
				/ new Float(Math.abs(p1.x - p2.x)).floatValue());

		int quad = getQuadP1(p1, p2);

		if (quad == quadtwo || quad == quadfour) {
			theta = Math.PI / 2 - theta;
		}

		int x1 = new Double(Math.round(minusH * Math.cos(theta + angle)))
				.intValue();
		int y1 = new Double(Math.round(minusH * Math.sin(theta + angle)))
				.intValue();
		int x2 = new Double(Math.round(plusH * Math.cos(angle - theta)))
				.intValue();
		int y2 = new Double(Math.round(plusH * Math.sin(angle - theta)))
				.intValue();

		switch (quad) {
		case quadone:
			p[0].x = p1.x + x1;
			p[0].y = p1.y - y1;
			p[1].x = p1.x + x2;
			p[1].y = p1.y + y2;
			break;
		case quadtwo:
			p[0].x = p1.x - y1;
			p[0].y = p1.y - x1;
			p[1].x = p1.x + y2;
			p[1].y = p1.y - x2;
			break;
		case quadthree:
			p[1].x = p1.x - x1;
			p[1].y = p1.y + y1;
			p[0].x = p1.x - x2;
			p[0].y = p1.y - y2;
			break;
		case quadfour:
			p[1].x = p1.x + y1;
			p[1].y = p1.y + x1;
			p[0].x = p1.x - y2;
			p[0].y = p1.y + x2;
		}

		return p;
	}

	public static Point[] getArrowPoints(Point p1, Point p2, float length,
			float angle) {
		Point p[] = new Point[2];
		p[0] = new Point(0, 0);
		p[1] = new Point(0, 0);

		angle = new Double(angle * Math.PI / 180.0).floatValue();

		double h = length / Math.sin(angle);
		double theta = Math.atan(new Float(Math.abs(p1.y - p2.y)).floatValue()
				/ new Float(Math.abs(p1.x - p2.x)).floatValue());

		int quad = getQuadP1(p1, p2);

		if (quad == quadtwo || quad == quadfour) {
			theta = Math.PI / 2 - theta;
		}

		int x1 = new Double(Math.round(h * Math.cos(theta + angle))).intValue();
		int y1 = new Double(Math.round(h * Math.sin(theta + angle))).intValue();
		int x2 = new Double(Math.round(h * Math.cos(angle - theta))).intValue();
		int y2 = new Double(Math.round(h * Math.sin(angle - theta))).intValue();

		switch (quad) {
		case quadone:
			p[0].x = p1.x + x1;
			p[0].y = p1.y - y1;
			p[1].x = p1.x + x2;
			p[1].y = p1.y + y2;
			break;
		case quadtwo:
			p[0].x = p1.x - y1;
			p[0].y = p1.y - x1;
			p[1].x = p1.x + y2;
			p[1].y = p1.y - x2;
			break;
		case quadthree:
			p[1].x = p1.x - x1;
			p[1].y = p1.y + y1;
			p[0].x = p1.x - x2;
			p[0].y = p1.y - y2;
			break;
		case quadfour:
			p[1].x = p1.x + y1;
			p[1].y = p1.y + x1;
			p[0].x = p1.x - y2;
			p[0].y = p1.y + x2;
		}

		return p;
	}

	public static void drawArrow(Graphics g, int lineSize, Point p1, Point p2,
			int topBtm) {
		int wd;
		Point P1[] = new Point[2];

		if (lineSize == 1) {
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			return;
		} else if (lineSize <= 6) {
			wd = lineSize * 2;
		} else {
			wd = lineSize;
		}

		int xPoints[] = new int[3];
		int yPoints[] = new int[3];

		// P1 = getArrowPoints(p1, p2, lineSize, 45);
		P1 = getArrowPoints(p1, p2, wd, 45);
		xPoints[0] = p1.x;
		xPoints[1] = P1[topBtm].x;
		yPoints[0] = p1.y;
		yPoints[1] = P1[topBtm].y;

		// P1 = getArrowPoints(p1, p2, lineSize, 0);
		P1 = getArrowPoints(p1, p2, wd, 0);
		xPoints[2] = P1[topBtm].x;
		;
		yPoints[2] = P1[topBtm].y;
		;

		g.fillPolygon(xPoints, yPoints, 3);
	}

	public static void drawThickLine(Graphics g, int lineSize, Point p1,
			Point p2) {
		Point P1[] = new Point[2];
		Point P2[] = new Point[2];

		int wd = lineSize / 2;

		if ((lineSize % 2) == 0) {
			P1 = getRectPoints(p1, p2, wd, wd);
			P2 = getRectPoints(p2, p1, wd, wd);
		} else {
			P1 = getRectPoints(p1, p2, wd, wd + 1);
			P2 = getRectPoints(p2, p1, wd, wd + 1);
		}

		int xPoints[] = { P1[0].x, P2[0].x, P2[1].x, P1[1].x };
		int yPoints[] = { P1[0].y, P2[0].y, P2[1].y, P1[1].y };
		g.fillPolygon(xPoints, yPoints, 4);
	}

	public static void drawSLine(Graphics g, int lineSize, Point p[], int direc) {
		Point P1[] = new Point[2];
		Point P2[] = new Point[2];
		Point P3[] = new Point[2];

		if (lineSize == 1) {
			switch (p.length) {
			case 2:
				g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
				break;
			case 4:
			case 6:
				for (int i = 0; i < p.length - 1; i++) {
					if (!(p[i].x == r.width && p[i + 1].x == 0)) {
						g.drawLine(p[i].x, p[i].y, p[i + 1].x, p[i + 1].y);
					}
				}
				break;
			}
		} else {
			switch (p.length) {
			case 2:
				drawThickLine(g, lineSize, p[0], p[1]);
				break;
			case 4:
				if ((p[1].x == r.width && p[2].x == 0)) {
					drawThickLine(g, lineSize, p[0], p[1]);
					drawThickLine(g, lineSize, p[2], p[3]);
				} else {
					g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
					drawThickLine(g, lineSize, p[1], p[2]);
					g.drawLine(p[2].x, p[2].y, p[3].x, p[3].y);
				}
				break;
			case 6:
				g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
				drawThickLine(g, lineSize, p[1], p[2]);
				drawThickLine(g, lineSize, p[3], p[4]);
				g.drawLine(p[4].x, p[4].y, p[5].x, p[5].y);
				break;
			}
		}

		int wd = lineSize / 2;

		if (lineSize > 1) {
			if (lineSize < 4) {
				lineSize = 4;
			}

			lineSize /= 2;

			switch (p.length) {
			case 4:
				if ((lineSize % 2) == 0) {
					P1 = getRectPoints(p[0], p[1], wd, wd);
					P2 = getRectPoints(p[1], p[0], wd, wd);
				} else {
					P1 = getRectPoints(p[0], p[1], wd, wd + 1);
					P2 = getRectPoints(p[1], p[0], wd, wd + 1);
				}

				if (direc == 5 || direc == 6) {
					drawArrow(g, lineSize, P1[0], P2[0], 0);
					drawArrow(g, lineSize, P1[1], P2[1], 1);
				}
			case 2:
				if ((lineSize % 2) == 0) {
					P1 = getRectPoints(p[p.length - 1], p[p.length - 2], wd, wd);
					P2 = getRectPoints(p[p.length - 2], p[p.length - 1], wd, wd);
				} else {
					P1 = getRectPoints(p[p.length - 1], p[p.length - 2], wd,
							wd + 1);
					P2 = getRectPoints(p[p.length - 2], p[p.length - 1], wd,
							wd + 1);
				}

				if (direc == 4 || direc == 6) {
					drawArrow(g, lineSize, P1[0], P2[0], 0);
					drawArrow(g, lineSize, P1[1], P2[1], 1);
				}

				if ((direc == 5 || direc == 6) && p.length == 2) {
					drawArrow(g, lineSize, P2[0], P1[0], 0);
					drawArrow(g, lineSize, P2[1], P1[1], 1);
				}
			}
		}

		if (direc == 1 || direc == 3) {
			P1 = getArrowPoints(p[p.length - 1], p[p.length - 2], 7, 45);
			drawArrow(g, 1, p[p.length - 1], P1[0], 0);
			drawArrow(g, 1, p[p.length - 1], P1[1], 0);
		}

		if (direc == 2 || direc == 3) {
			P1 = getArrowPoints(p[0], p[1], 7, 45);
			drawArrow(g, 1, p[0], P1[0], 0);
			drawArrow(g, 1, p[0], P1[1], 0);
		}
	}

	public static void drawSCircle(Graphics g, int lineSize, Point p[]) {
		if (lineSize == 1) {
			g
					.drawOval(p[0].x - p[1].x, p[0].y - p[1].y, 2 * p[1].x,
							2 * p[1].y);
		} else {
			int rx = p[1].x;
			int ry = p[1].y;
			for (int i = 0; i < lineSize; i++) {
				g.drawOval(p[0].x - rx, p[0].y - ry, 2 * rx--, 2 * ry--);
				if (rx == 4 || ry == 4) {
					break;
				}
			}
		}
	}

	private void drawSelfLine(Graphics g, Line line[], float latp1,
			float lonp1, Node nodeP1) {
		int lineSize;
		int direc;
		int numofCkeys = color_key.capacity();
		int numofSkeys = size_key.capacity();
		int angleFac = 0;
		int lineNum = 0;
		int quad = 1;
		int radius;
		int X;
		int Y;
		String lineTitle;

		Point p1 = latLonToPoint(latp1, lonp1);

		int max_radius = Math.min(r.width, r.height) / 20;
		if (max_radius > 25) {
			max_radius = 25;
		}

		int xtmp = Math.min(Math.abs(p1.x - r.width), p1.x);
		int ytmp = Math.min(Math.abs(p1.y - r.height), p1.y);

		if (xtmp > max_radius && ytmp > max_radius) {
			radius = max_radius;
		} else {
			radius = Math.min(xtmp, ytmp);
		}

		for (int i = 1; i < 25; i++) {
			Point ptmp[] = new Point[2];
			ptmp[1] = new Point(radius, radius);
			if (i > 12) {
				ptmp[0] = getArcCenter(p1, angleFac, ptmp[1].x, 1 - quad);
			} else {
				ptmp[0] = getArcCenter(p1, angleFac, ptmp[1].x, 2 - quad);
			}

			if ((i % 20) == 0) {
				angleFac = 0;
			}

			if (!(r.contains(new Point(ptmp[0].x - ptmp[1].x, ptmp[0].y
					- ptmp[1].x)) && r.contains(new Point(
					ptmp[0].x + ptmp[1].x, ptmp[0].y + ptmp[1].x)))) {
				quad *= -1;

				if ((i % 2) == 0) {
					angleFac += 15;
				}
				continue;
			}

			setColor(g, line[lineNum], numofCkeys);
			lineSize = getNodeLineSize(line[lineNum], numofSkeys);
			line[lineNum].setSize(lineSize);
			if (line[lineNum].getRadius() > 0) {
				ptmp[0] = latLonToPoint(latp1, lonp1);
				ptmp[1] = radiusToPixels(line[lineNum].getRadius());
				drawSCircle(g, lineSize, ptmp);
			} else
				drawSCircle(g, lineSize, ptmp);
			if ((lineTitle = line[lineNum].getTitle()) != "") {
				int position = 2;
				if (lineTitle.indexOf(',') > 0) {
					StringTokenizer tokenizer = new StringTokenizer(lineTitle,
							",");
					while (tokenizer.countTokens() > 1) {
						tokenizer.nextToken();
					}

					try {
						position = Integer.parseInt(tokenizer.nextToken());
					} catch (NumberFormatException nfe) {
						lineTitle = lineTitle.concat(",2");
					}
				} else {
					lineTitle = lineTitle.concat(",2");
				}

				X = ptmp[0].x - ptmp[1].x;
				Y = ptmp[0].y - ptmp[1].x;

				g.setFont(new Font("Dialog", Font.BOLD, fontSize));
				fm = g.getFontMetrics();

				switch (position) {
				case 1:
					drawLineTitle(g, 1, new Point(X, Y), new Point(X, Y
							+ (2 * ptmp[1].x)), lineTitle);
					break;
				case 2:
					Y -= fm.getMaxAdvance();
					drawLineTitle(g, 1, new Point(X, Y), new Point(X
							+ (2 * ptmp[1].x), Y), lineTitle);
					break;
				case 3:
					drawLineTitle(
							g,
							1,
							new Point(X + (2 * ptmp[1].x), Y),
							new Point(X + (2 * ptmp[1].x), Y + (2 * ptmp[1].x)),
							lineTitle);
					break;
				case 4:
					Y = ptmp[0].y + ptmp[1].x + (2 * fm.getMaxAdvance());
					drawLineTitle(g, 1, new Point(X, Y), new Point(X
							+ (2 * ptmp[1].x), Y), lineTitle);
				}
			}

			line[lineNum].setType(8);
			line[lineNum].setPoint(ptmp, 8);

			nodeP1.quad_matrix[getQuadrant(p1, ptmp[0])]++;

			if (++lineNum == line.length) {
				break;
			}

			quad *= -1;
			if ((i % 2) == 0) {
				angleFac += 15;
			}
		}
	}

	private Point getArcCenter(Point p, int angle, int hyp, int quad) {
		Point pt = new Point();

		float anglerad = new Double(angle * Math.PI / 180.0).floatValue();

		int x = new Double(Math.cos(anglerad) * hyp).intValue();
		int y = new Double(Math.sin(anglerad) * hyp).intValue();

		switch (quad) {
		case quadone:
			pt.x = p.x + x;
			pt.y = p.y - y;
			break;
		case quadtwo:
			pt.x = p.x - y;
			pt.y = p.y - x;
			break;
		case quadthree:
			pt.x = p.x - x;
			pt.y = p.y + y;
			break;
		case quadfour:
			pt.x = p.x + y;
			pt.y = p.y + x;
		}

		return pt;
	}

	private void drawLineTitle(Graphics g, int lineSize, Point p1, Point p2,
			String lineTitle) {
		String tmp = new String();

		g.setFont(new Font("Dialog", Font.BOLD, fontSize));
		fm = g.getFontMetrics();
		int position = 3;
		if (lineTitle.indexOf(',') > 0) {
			StringTokenizer tokenizer = new StringTokenizer(lineTitle, ",");
			String temp = new String();

			while (tokenizer.countTokens() > 2) {
				temp = temp.concat(tokenizer.nextToken());
				temp = temp.concat(",");
			}
			temp = temp.concat(tokenizer.nextToken());

			try {
				tmp = tokenizer.nextToken();
				position = Integer.parseInt(tmp);
			} catch (NumberFormatException nfe) {
				temp = temp.concat(",");
				temp = temp.concat(tmp);
			}
			lineTitle = temp;
		}

		Point p = new Point();

		p.x = Math.min(p1.x, p2.x) + Math.abs(p1.x - p2.x) / 2;
		p.y = Math.min(p1.y, p2.y) + Math.abs(p1.y - p2.y) / 2;

		int strlen = fm.stringWidth(lineTitle);

		if (!(p1.x == p2.x || p1.y == p2.y)) {
			switch (getQuadP1(p1, p2)) {
			case quadone:
			case quadthree:
				switch (position) {
				case 1:
					p.x = p.x - (lineSize + 1) / 2 - strlen
							- fm.getMaxAdvance();
					p.y = p.y - fm.getLeading() - fm.getDescent();
					break;
				case 2:
					p.x = p.x - (lineSize + 1) / 2 - strlen / 2;
					break;
				case 3:
					p.x = p.x + (lineSize + 1) / 2 + fm.getMaxAdvance();
					p.y = p.y + fm.getLeading() + fm.getAscent();
				}
				break;
			case quadtwo:
			case quadfour:
				switch (position) {
				case 3:
					p.x = p.x - (lineSize + 1) / 2 - strlen
							- fm.getMaxAdvance();
					p.y = p.y + fm.getLeading() + fm.getAscent();
					break;
				case 2:
					p.x = p.x - (lineSize + 1) / 2 - strlen / 2;
					p.y += fm.getAscent();
					break;
				case 1:
					p.x = p.x + (lineSize + 1) / 2 + fm.getMaxAdvance();
					p.y = p.y - fm.getLeading() - fm.getDescent();
				}
			}
		} else {
			if (p1.x == p2.x) {
				switch (position) {
				case 1:
					p.x = p.x - (lineSize + 1) / 2 - strlen
							- fm.getMaxAdvance();
					break;
				case 2:
					p.x = p.x - (lineSize + 1) / 2 - strlen / 2;
					break;
				case 3:
					p.x = p.x + (lineSize + 1) / 2 + fm.getMaxAdvance();
				}
			} else {
				p.x = p.x - strlen / 2;
				switch (position) {
				case 1:
					p.y = p.y - fm.getLeading() - (lineSize + 1) / 2;
					break;
				case 3:
					p.y += fm.getAscent() + (lineSize + 1) / 2;
					break;
				case 2:
					p.y = p.y + fm.getLeading() + fm.getDescent();
				}
			}
		}

		// Handle case when p.x and p.y become invalid values
		g.drawString(lineTitle, p.x, p.y);
	}

	private void drawLine(Graphics g, Line line[], float latp1, float lonp1,
			float latp2, float lonp2, Node nodeP1, Node nodeP2) {
		int lineSize;
		int prevMaxLineSize = 1;
		int direc;
		int numofCkeys = color_key.capacity();
		int numofSkeys = size_key.capacity();
		String lineTitle = new String();

		Point p1 = latLonToPoint(latp1, lonp1);
		Point p2 = latLonToPoint(latp2, lonp2);
		Point P1[] = new Point[2];
		Point P2[] = new Point[2];

		if (comparelon(lonp1, lonp2) < Math.abs(bot_lon - top_lon) / 2) {
			int lineSize1;
			int lineSize2;
			int start = 0;
			boolean extra = false;
			int w = 0;

			if ((line.length % 2) != 0) {
				setColor(g, line[0], numofCkeys);
				lineSize = getNodeLineSize(line[0], numofSkeys);
				line[0].setSize(lineSize);

				if (lineSize > 1) {
					w = lineSize / 2;
					direc = line[0].setDirec(1);
				} else {
					direc = line[0].setDirec(0);
				}

				P1[0] = p1;
				P1[1] = p2;

				drawSLine(g, lineSize, P1, direc);

				if ((lineTitle = line[0].getTitle()) != "") {
					drawLineTitle(g, lineSize, p1, p2, lineTitle);
				}

				line[0].setType(1);
				line[0].setPoint(p1, p2);

				nodeP1.quad_matrix[getQuadrant(p1, p2)]++;
				nodeP2.quad_matrix[getQuadrant(p2, p1)]++;

				start = 1;
			}

			float angle = 45;

			for (int i = start; i < line.length; i += 2) {
				Point ptmp1[] = new Point[4];
				Point ptmp2[] = new Point[4];

				lineSize1 = getNodeLineSize(line[i], numofSkeys);

				lineSize2 = getNodeLineSize(line[i + 1], numofSkeys);
				line[i].setSize(lineSize1);
				line[i + 1].setSize(lineSize2);

				if (lineSize1 == 1 && lineSize2 == 1) {
					w += 5;
					if (extra) {
						w += prevMaxLineSize / 2;
						extra = false;
					}
				} else if (lineSize1 >= lineSize2) {
					w += lineSize1;
					prevMaxLineSize = lineSize1;
					extra = true;
				} else {
					w += lineSize2;
					prevMaxLineSize = lineSize2;
					extra = true;
				}

				if (angle > 165) // xxx - check 165
				{
					continue;
				}

				P1 = getArrowPoints(p1, p2, w, new Double(angle).floatValue());
				P2 = getArrowPoints(p2, p1, w, new Double(angle).floatValue());

				setColor(g, line[i], numofCkeys);

				direc = line[i].setDirec(0);

				if (((Node) nodes.get(line[i].getSrc())).getNodNum() == nodeP1
						.getNodNum()) {
					ptmp1[0] = p1;
					ptmp1[1] = P1[0];
					ptmp1[2] = P2[0];
					ptmp1[3] = p2;
				} else if (((Node) nodes.get(line[i].getSrc())).getNodNum() == nodeP2
						.getNodNum()) {
					ptmp1[0] = p2;
					ptmp1[1] = P2[0];
					ptmp1[2] = P1[0];
					ptmp1[3] = p1;
				}

				drawSLine(g, lineSize1, ptmp1, direc);

				if ((lineTitle = line[i].getTitle()) != "") {
					drawLineTitle(g, lineSize1, P1[0], P2[0], lineTitle);
				}

				line[i].setType(3);
				line[i].setPoint(ptmp1, 0);

				nodeP1.quad_matrix[getQuadrant(p1, P1[0])]++;
				nodeP2.quad_matrix[getQuadrant(p2, P2[0])]++;

				setColor(g, line[i + 1], numofCkeys);

				direc = line[i + 1].setDirec(0);

				if (((Node) nodes.get(line[i + 1].getSrc())).getNodNum() == nodeP1
						.getNodNum()) {
					ptmp2[0] = p1;
					ptmp2[1] = P1[1];
					ptmp2[2] = P2[1];
					ptmp2[3] = p2;
				} else if (((Node) nodes.get(line[i + 1].getSrc())).getNodNum() == nodeP2
						.getNodNum()) {
					ptmp2[0] = p2;
					ptmp2[1] = P2[1];
					ptmp2[2] = P1[1];
					ptmp2[3] = p1;
				}

				drawSLine(g, lineSize2, ptmp2, direc);

				if ((lineTitle = line[i + 1].getTitle()) != "") {
					drawLineTitle(g, lineSize2, P1[1], P2[1], lineTitle);
				}

				line[i + 1].setType(3);
				line[i + 1].setPoint(ptmp2, 0);

				nodeP1.quad_matrix[getQuadrant(p1, P1[1])]++;
				nodeP2.quad_matrix[getQuadrant(p2, P2[1])]++;

				angle += 15;
			}
		} else {
			if (pointWithinComponent(p1) && pointWithinComponent(p2)) {
				Point P[] = new Point[2];
				P[0] = new Point(0, 0);
				boolean flipped = false;

				if (p1.x < p2.x) {
					Point pTemp = p2;
					p2 = p1;
					p1 = pTemp;

					Node nTemp = nodeP2;
					nodeP2 = nodeP1;
					nodeP1 = nTemp;

					flipped = true;
				}

				P[0] = new Point(p1.x - r.width, p1.y);
				P[1] = p2;

				float m = new Float(P[0].y - P[1].y).floatValue()
						/ new Float(P[0].x - P[1].x).floatValue();
				float c = new Integer(P[0].y).floatValue()
						- (new Float(m).floatValue() * new Integer(P[0].x)
								.floatValue());

				int y = Math.round(c);

				int lineSize1;
				int lineSize2;
				int start = 0;
				boolean extra = false;
				int w = 0;
				int h = 0;

				if ((line.length % 2) != 0) {
					setColor(g, line[0], numofCkeys);
					lineSize = getNodeLineSize(line[0], numofSkeys);
					line[0].setSize(lineSize);

					Point ptmp[] = new Point[4];
					ptmp[0] = p1;
					ptmp[1] = new Point(r.width, y);
					ptmp[2] = new Point(0, y);
					ptmp[3] = p2;

					if (flipped) {
						switch (line[0].getDirec()) {
						case 1:
							line[0].chngDirec(2);
							break;
						case 2:
							line[0].chngDirec(1);
						}
					}

					if (lineSize > 1) {
						w = lineSize / 2;
						direc = line[0].setDirec(1);
					} else {
						direc = line[0].setDirec(0);
					}

					drawSLine(g, lineSize, ptmp, direc);

					if ((lineTitle = line[0].getTitle()) != "") {
						if ((r.width - p1.x) > p2.x) {
							drawLineTitle(g, lineSize, ptmp[0], ptmp[1],
									lineTitle);
						} else {
							drawLineTitle(g, lineSize, ptmp[2], ptmp[3],
									lineTitle);
						}
					}

					line[0].setType(3);
					line[0].setPoint(ptmp, 1);

					nodeP1.quad_matrix[getQuadrant(p1, new Point(r.width, y))]++;
					nodeP2.quad_matrix[getQuadrant(new Point(0, y), p2)]++;

					start = 1;
				}

				float angle = 45;

				for (int i = start; i < line.length; i += 2) {
					Point ptmp1[] = new Point[6];
					Point ptmp2[] = new Point[6];

					lineSize1 = getNodeLineSize(line[i], numofSkeys);
					lineSize2 = getNodeLineSize(line[i + 1], numofSkeys);
					line[i].setSize(lineSize1);
					line[i + 1].setSize(lineSize2);

					if (lineSize1 == 1 && lineSize2 == 1) {
						w += 5;
						if (extra) {
							w += prevMaxLineSize / 2;
							;
							extra = false;
						}
					} else if (lineSize1 >= lineSize2) {
						w += lineSize1;
						prevMaxLineSize = lineSize1;
						extra = true;
					} else {
						w += lineSize2;
						prevMaxLineSize = lineSize2;
						extra = true;
					}

					if (angle > 165) // xxx - check 165
					{
						continue;
					}

					// Code to draw Stuff for the LHS

					P1 = getArrowPoints(p1, new Point(r.width, y), w, angle);
					h = w;

					if (flipped) {
						switch (line[i].getDirec()) {
						case 1:
							line[i].chngDirec(2);
							break;
						case 2:
							line[i].chngDirec(1);
						}
					}
					line[i].setDirec(0);

					if (p1.y < y) {
						h = -w;
					}

					ptmp1[0] = p1;
					ptmp1[1] = P1[0];
					ptmp1[2] = new Point(r.width, y - h);

					if (flipped) {
						switch (line[i + 1].getDirec()) {
						case 1:
							line[i + 1].chngDirec(2);
							break;
						case 2:
							line[i + 1].chngDirec(1);
						}
					}
					line[i + 1].setDirec(0);

					ptmp2[0] = p1;
					ptmp2[1] = P1[1];
					ptmp2[2] = new Point(r.width, y + h);

					nodeP1.quad_matrix[getQuadrant(p1, P1[0])]++;
					nodeP1.quad_matrix[getQuadrant(p1, P1[1])]++;

					// Code to draw stuff on the RHS

					P1 = getArrowPoints(p2, new Point(0, y), w, angle);

					if (p2.y > y) {
						h = -w;
					}

					ptmp1[3] = new Point(0, y - h);
					ptmp1[4] = P1[0];
					ptmp1[5] = p2;

					ptmp2[3] = new Point(0, y + h);
					ptmp2[4] = P1[1];
					ptmp2[5] = p2;

					line[i].setType(5);
					line[i].setPoint(ptmp1, 1);
					line[i + 1].setType(5);
					line[i + 1].setPoint(ptmp2, 1);

					setColor(g, line[i], numofCkeys);
					direc = line[i].getDirec();
					drawSLine(g, lineSize1, ptmp1, direc);

					if ((lineTitle = line[i].getTitle()) != "") {
						if ((r.width - ptmp1[1].x) > ptmp1[4].x) {
							drawLineTitle(g, lineSize1, ptmp1[1], ptmp1[2],
									lineTitle);
						} else {
							drawLineTitle(g, lineSize1, ptmp1[3], ptmp1[4],
									lineTitle);
						}
					}

					setColor(g, line[i + 1], numofCkeys);
					direc = line[i + 1].getDirec();
					drawSLine(g, lineSize2, ptmp2, direc);

					if ((lineTitle = line[i + 1].getTitle()) != "") {
						if ((r.width - ptmp2[1].x) > ptmp2[4].x) {
							drawLineTitle(g, lineSize2, ptmp2[1], ptmp2[2],
									lineTitle);
						} else {
							drawLineTitle(g, lineSize2, ptmp2[3], ptmp2[4],
									lineTitle);
						}
					}

					nodeP2.quad_matrix[getQuadrant(p2, P1[0])]++;
					nodeP2.quad_matrix[getQuadrant(p2, P1[1])]++;

					// xxx - check angle increment
					angle += 15;
				}
			}

			else if (!pointWithinComponent(p1)) {
				// From P2's perspective of things
				Point orgp2 = latLonToPoint(latp2, lonp2);
				float x1 = new Float(r.width - orgp2.x).floatValue();
				float y1;
				float laty;
				if (latp2 < latp1) {
					y1 = new Float(orgp2.y).floatValue();
					laty = top_lat - latp2;
				} else {
					y1 = new Float(r.height - orgp2.y).floatValue();
					laty = Math.abs(bot_lat - latp2);
				}

				float latx = bot_lon - lonp2;

				float realx = difflon(lonp1, lonp2) * x1 / latx;
				float realy = difflat(latp1, latp2) * y1 / laty;

				float slope = realx / realy;
				int y1dash = new Float((r.width - p2.x) / slope).intValue();

				if (latp2 < latp1) {
					g.drawLine(r.width, p2.y - y1dash, p2.x, p2.y);

					nodeP2.quad_matrix[getQuadrant(p2, new Point(r.width, p2.y
							- y1dash))]++;
				} else {
					g.drawLine(r.width, p2.y + y1dash, p2.x, p2.y);

					nodeP2.quad_matrix[getQuadrant(p2, new Point(r.width, p2.y
							+ y1dash))]++;
				}
			}

			else {
				// From P1's perspective of things
				Point orgp1 = latLonToPoint(latp1, lonp1);
				float x1 = new Float(orgp1.x).floatValue();
				float y1;
				float laty;
				if (latp1 > latp2) {
					y1 = new Float(r.height - orgp1.y).floatValue();
					laty = Math.abs(bot_lat - latp1);
				} else {
					y1 = new Float(orgp1.y).floatValue();
					laty = latp1 - top_lat;
				}

				float latx = lonp1 - top_lon;

				float realx = difflon(lonp1, lonp2) * x1 / latx;
				float realy = difflat(latp1, latp2) * y1 / laty;

				float slope = realx / realy;
				int y1dash = new Float(p1.x / slope).intValue();

				if (latp1 > latp2) {
					g.drawLine(p1.x, p1.y, 1, p1.y + y1dash);

					nodeP1.quad_matrix[getQuadrant(p1, new Point(1, p1.y
							+ y1dash))]++;
				} else {
					g.drawLine(p1.x, p1.y, 1, p1.y - y1dash);

					nodeP1.quad_matrix[getQuadrant(p1, new Point(1, p1.y
							- y1dash))]++;
				}
			}
		}
	}

	public void paintData(Graphics g) {
		String token = new String();
		String nodTitle;
		String temp;
		StringTokenizer tokenizer;
		int numofLines;
		int numofNodes;
		int numofCkeys;
		int numofSkeys;
		int lineSize;
		LineMatrix lmn1;
		LineMatrix lmn2;

		Line line;
		Node nodeP1;
		Node nodeP2;

		float latp1 = 0;
		float lonp1 = 0;
		float latp2 = 0;
		float lonp2 = 0;
		Point p1 = new Point();
		Point p2 = new Point();

		paintDataCalled = true;
		g.drawImage(map, 0, 0, r.width, r.height, null);
		g.setFont(new Font("Dialog", Font.BOLD, fontSize));
		fm = g.getFontMetrics();
		g.setColor(fgcolor);
		g.drawString(title, (r.width - fm.stringWidth(title)) / 2, fontSize);
		g.drawString(date, r.width - fm.stringWidth(date) - 10, fontSize);

		color_key.trimToSize();
		size_key.trimToSize();
		lines.trimToSize();
		numofLines = lines.capacity();
		numofCkeys = color_key.capacity();
		numofSkeys = size_key.capacity();

		for (int i = 0; i < numofLines; i++) {
			line = (Line) lines.elementAt(i);
			nodeP1 = (Node) nodes.get(line.getSrc());
			if (nodeP1 == null) {
				continue;
			} else {
				latp1 = nodeP1.getLat();
				lonp1 = nodeP1.getLon();
				lmn1 = (LineMatrix) lineMatrix.get(line.getSrc()
						+ line.getDest());
				p1 = latLonToPoint(latp1, lonp1);
			}

			nodeP2 = (Node) nodes.get(line.getDest());

			if (nodeP2 == null) {
				continue;
			} else {
				latp2 = nodeP2.getLat();
				lonp2 = nodeP2.getLon();
				lmn2 = (LineMatrix) lineMatrix.get(line.getDest()
						+ line.getSrc());
				p2 = latLonToPoint(latp2, lonp2);
			}

			if (lmn1 == lmn2 && lmn1.getValue() > 0) {
				// case where node has a line going to itself
				int nlin = lmn1.getValue();
				int nlincount = 0;
				Line mline[] = new Line[nlin];

				String src = line.getSrc();

				if (lmn1.getValue() == 1) {
					mline[0] = line;
					lmn1.decrValue();
				} else {
					for (int k = 0; k < numofLines; k++) {
						line = (Line) lines.elementAt(k);
						if (line.getSrc().equals(src)
								&& line.getDest().equals(src)) {
							mline[nlincount++] = line;
							lmn1.decrValue();
						}
					}
				}
				drawSelfLine(g, mline, latp1, lonp1, nodeP1);
			} else if (lmn1.getValue() == 1 && lmn2 == null) {
				// case where there is only one line between nodes i and j
				Line mline[] = new Line[1];
				mline[0] = line;
				lmn1.decrValue();
				drawLine(g, mline, latp1, lonp1, latp2, lonp2, nodeP1, nodeP2);
			} else {

				int nlin;

				if (lmn1 != null && lmn2 != null) {
					nlin = lmn1.getValue() + lmn2.getValue();
				} else if (lmn2 == null) {
					nlin = lmn1.getValue();
				} else {
					nlin = lmn2.getValue();
				}

				if (nlin <= 0) {
					continue;
				}

				int nlincount = 0;
				Line mline[] = new Line[nlin];

				String src = line.getSrc();
				String dest = line.getDest();

				for (int k = 0; k < numofLines; k++) {
					line = (Line) lines.elementAt(k);

					if (line.getSrc().equals(src)
							&& line.getDest().equals(dest)) {
						mline[nlincount++] = line;
						lmn1.decrValue();
					} else if (line.getSrc().equals(dest)
							&& line.getDest().equals(src)) {
						mline[nlincount++] = line;
						lmn2.decrValue();
					}
				}

				drawLine(g, mline, latp1, lonp1, latp2, lonp2, nodeP1, nodeP2);
			}
		}

		Enumeration enode = nodes.elements();
		while (enode.hasMoreElements()) {
			nodTitle = "";

			nodeP1 = (Node) enode.nextElement();

			latp1 = nodeP1.getLat();
			lonp1 = nodeP1.getLon();
			p1 = latLonToPoint(latp1, lonp1);

			int width = getNodeLineSize(nodeP1, numofSkeys);
			nodeP1.setSize(width);
			setColor(g, nodeP1, numofCkeys);

			nodeP1.setPoint(p1);

			g.fillOval(p1.x - (width / 2), p1.y - (width / 2), width, width);

			if ((nodTitle = nodeP1.getTitle()) != "") {
				g.setColor(fgcolor);
				int quad = 56;
				if (nodTitle.indexOf(',') > 0) {
					tokenizer = new StringTokenizer(nodTitle, ",");
					temp = new String();

					while (tokenizer.countTokens() > 2) {
						temp = temp.concat(tokenizer.nextToken());
						temp = temp.concat(",");
					}
					temp = temp.concat(tokenizer.nextToken());

					try {
						token = tokenizer.nextToken();
						quad = Integer.parseInt(token);
					} catch (NumberFormatException nfe) {
						temp = temp.concat(",");
						temp = temp.concat(token);
					}
					nodTitle = temp;
				} else {
					quad = nodeP1.getQuadrant();
				}
				Point titlepoint = getTitleXY(nodTitle, fm, quad, p1, width);
				g.drawString(nodTitle, titlepoint.x, titlepoint.y);
			}
		}

		// Display the color key

		if (colorkey && !hide_key) {
			int curX = Ckey_x;
			int curY;

			g.setColor(Color.white);
			g.fillRect(Ckey_x, Ckey_y, Ckey_width, Ckey_height);

			g.setColor(Color.black);
			g.setFont(new Font("monospaced", Font.PLAIN, Ckey_fontSize));
			fm = g.getFontMetrics();
			curY = Ckey_y + fm.getLeading() + fm.getAscent();
			g.drawString(Ckey_title, curX
					+ (Ckey_width - fm.stringWidth(Ckey_title)) / 2, curY);
			curY = curY + fm.getAscent();

			g.setFont(new Font("monospaced", Font.PLAIN, Ckey_fontSize
					- KEY_DATA_SIZE));
			fm = g.getFontMetrics();

			for (int ck = 0; ck < numofCkeys; ck++, curY += fm.getHeight()) {
				key = (Key) color_key.elementAt(ck);
				setColor(g, key.getValue());

				g.fillRect(curX + 2, curY - fm.getAscent(), 10, fm.getAscent());
				g.setColor(Color.black);
				g.drawString(key.toString(), curX + 15, curY);
			}
		}

		// Display the size key

		if (sizekey && !hide_key) {
			Font font;
			String label;
			int labelWidth;
			int height;
			int prevHeight;
			int curX = Skey_x;
			int curY;
			int curH = 0;

			g.setColor(Color.white);
			g.fillRect(Skey_x, Skey_y, Skey_width, Skey_height);

			g.setColor(Color.black);
			g.setFont(new Font("monospaced", Font.PLAIN, Skey_fontSize));
			fm = g.getFontMetrics();
			curY = Skey_y + fm.getLeading() + fm.getAscent();
			g.drawString(Skey_title, curX
					+ (Skey_width - fm.stringWidth(Skey_title)) / 2, curY);
			curY = curY + 2 * fm.getAscent();
			curX += 5;

			g.setFont(new Font("monospaced", Font.PLAIN, Skey_fontSize
					- KEY_DATA_SIZE));
			g.setColor(Color.black);
			fm = g.getFontMetrics();
			height = prevHeight = 0;

			for (int sk = 0; sk < numofSkeys; sk++) {
				key = (Key) size_key.elementAt(sk);
				label = key.toString();
				labelWidth = fm.stringWidth(label);
				g.drawString(label, curX + labelWidth / 4, curY);

				try {
					prevHeight = height;
					height = Integer.parseInt(key.getValue());
					if (sk == 0) {
						prevHeight = height;
					}

				} catch (NumberFormatException nfe) {
					height = 0;
				}

				curH += (prevHeight - height) / 2;
				g.fillRect(curX, curY + fm.getHeight() + curH,
						3 * labelWidth / 2, height);
				curX += 3 * labelWidth / 2;
			}

		}

		repaint();
	}

	private Point getTitleXY(String title, FontMetrics fm, int quad, Point p,
			int nodeWidth) {
		Point tp = new Point();

		switch (quad) {
		case quadfivesix:
			tp.y = p.y + nodeWidth / 2 + fm.getHeight();
			tp.x = p.x - (fm.stringWidth(title) / 2);
			break;

		case quadtwoone:
			tp.y = p.y - nodeWidth / 2 - fm.getDescent();
			tp.x = p.x - (fm.stringWidth(title) / 2);
			break;

		case quadsevenzero:
			tp.y = p.y + nodeWidth / 2;
			tp.x = p.x + nodeWidth;
			break;

		case quadfourthree:
			tp.y = p.y + nodeWidth / 2;
			tp.x = p.x - (fm.stringWidth(title) + nodeWidth);
			break;

		case quadsix:
			tp.y = p.y + nodeWidth / 2 + fm.getHeight();
			tp.x = p.x - fm.stringWidth(title);
			break;
		case quadseven:
			tp.y = p.y + nodeWidth / 2 + fm.getHeight();
			tp.x = p.x;
			break;

		case quadthree:
			tp.y = p.y - nodeWidth / 2 - fm.getDescent();
			tp.x = p.x - fm.stringWidth(title);
			break;

		case quadtwo:
			tp.y = p.y - nodeWidth / 2 - fm.getDescent();
			tp.x = p.x;
			break;

		case quadeight:
			tp.y = p.y + fm.getAscent();
			tp.x = p.x + nodeWidth;
			break;
		case quadone:
			tp.y = p.y - nodeWidth / 2;
			tp.x = p.x + nodeWidth;
			break;
		case quadfive:
			tp.y = p.y + fm.getAscent();
			tp.x = p.x - (fm.stringWidth(title) + nodeWidth);
			break;
		case quadfour:
			tp.y = p.y - fm.getDescent();
			tp.x = p.x - (fm.stringWidth(title) + nodeWidth);
			break;
		}

		// Handle cases when tp.x or tp.y become an invalid value;
		return tp;
	}

	public static int getQuadP1(Point p1, Point p2) {
		if (p1.x <= p2.x) {
			if (p1.y >= p2.y) {
				return quadone;
			} else {
				return quadfour;
			}
		} else {
			if (p1.y > p2.y) {
				return quadtwo;
			} else {
				return quadthree;
			}
		}
	}

	private void buildLineMatrix() {
		LineMatrix lm;
		String src;
		String dest;
		int numofLines = lines.size();
		for (int i = 0; i < numofLines; i++) {
			line = (Line) lines.elementAt(i);
			src = line.getSrc();
			dest = line.getDest();
			lm = (LineMatrix) lineMatrix.get(src + dest);
			if (lm == null) {
				lineMatrix.put(src + dest, new LineMatrix(src, dest));
			} else {
				lm.incrValue();
			}
		}
	}

	private int getQuadrant(Point p1, Point p2) {
		int quad = getQuadP1(p1, p2);
		float height = new Float(Math.abs(p1.y - p2.y)).floatValue();
		float width = new Float(Math.abs(p1.x - p2.x)).floatValue();

		if (width == 0) {
			if (p1.y < p2.y) {
				return quadsix;
			} else {
				return quadthree;
			}
		} else if (height == 0) {
			if (p1.x < p2.x) {
				return quadone;
			} else {
				return quadfive;
			}
		} else {
			switch (quad) {
			case quadone:
			case quadthree:
				if (height > width) {
					return 2 * quad + 1;
				} else {
					return 2 * quad;
				}
			case quadtwo:
			case quadfour:
				if (width > height) {
					return 2 * quad + 1;
				} else {
					return 2 * quad;
				}
			}
		}

		return quadone;
	}

	private float difflon(float lon1, float lon2) {
		if ((lon1 > 0 && lon2 > 0) || (lon1 < 0 && lon2 < 0)) {
			return Math.abs(lon1) - Math.abs(lon2);
		} else {
			return (360 - (Math.abs(lon1) + Math.abs(lon2)));
		}
	}

	private float difflat(float lat1, float lat2) {
		if ((lat1 > 0 && lat2 > 0) || (lat1 < 0 && lat2 < 0)) {
			return Math.abs(Math.abs(lat1) - Math.abs(lat2));
		} else {
			return Math.abs(lat1) + Math.abs(lat2);
		}
	}

	private boolean pointWithinComponent(Point p) {
		if (p.x < 0 || p.x > r.width) {
			return false;
		} else if (p.y < 0 || p.y > r.height) {
			return false;
		} else {
			return true;
		}
	}

	private int comparelon(float lon1, float lon2) {
		if ((lon1 < 0 && lon2 > 0) || (lon1 > 0 && lon2 < 0)) {
			return new Float(Math.abs(lon1) + Math.abs(lon2)).intValue();
		}

		return 0;
	}

	public Double stringToLat(String lat) {
		Double latd = new Double(new Double(lat).doubleValue());
		return latd;
	}

	public Double stringToLon(String lon) {
		Double lond = new Double(new Double(lon).doubleValue());
		return lond;
	}

	public String roundNum(double d) {
		int i;
		d = Math.abs(d);
		i = (int) (d * 100.00) % 100;
		return (int) d + "." + i;
	}

	public String pointToLatLon(Point p) {
		double lat = aLat * (p.y) + bLat;
		double lon = aLon * (p.x) + bLon;

		if (lon > 180) {
			lon = lon - 360.0;
		}

		return roundNum(lat) + ((lat < 0.0) ? "S" : "N") + ", " + roundNum(lon)
				+ ((lon < 0.0) ? "W" : "E");
	}

	public Point latLonToPoint(float lat, float lon) {
		Point p = new Point();
		p.y = new Float((lat - bLat) / aLat + 0.5).intValue();
		p.x = new Float((lon - bLon) / aLon + 0.5).intValue();
		return p;
	}

	// TODO: fix
	public Point radiusToPixels(float radius) {
		double earths_radius = 6378.135;
		float longitude = 0;
		float latitude = 0;
		float r_latitude = (float) ((180.0 / Math.PI) * (radius / earths_radius));
		float r_longitude = (float) (r_latitude / Math.cos(latitude
				* (Math.PI / 180.0)));
		int x = (int) ((r_longitude * getWidth()) / 360.0);
		int y = (int) ((r_latitude * getHeight()) / 180.0);
		return new Point(x, y);
	}

	public static int getAppletWidth() {
		return r.width;
	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		mouseP = e.getPoint();

		matched_regions.removeAllElements();

		Enumeration enode = nodes.elements();
		while (enode.hasMoreElements()) {
			node = (Node) enode.nextElement();

			if (node.withinBoundary(mouseP)) {
				matched_regions.addElement(node);
			}
		}

		int numofLines = lines.size();
		for (int i = 0; i < numofLines; i++) {
			line = (Line) lines.elementAt(i);

			if (line.withinBoundary(mouseP)) {
				matched_regions.addElement(line);
			}
		}

		if (matched_regions.size() > 0) {
			mouseOver = true;
			repaint();
		} else if (bufferChanged) {
			mouseOver = false;
			bufferChanged = false;
			repaint();
		}

		showStatus("");
	}

	public void drawNodLin(Graphics g) {
		Object msObj;
		int dist;
		NodLin tmpnodLin;
		URL url;

		matched_regions.trimToSize();
		if (matched_regions.size() == 1) {
			dist = ((NodLin) matched_regions.elementAt(0)).getDistance(mouseP);

			if (dist >= 0 && dist < 3) {
				tmpnodLin = ((NodLin) matched_regions.elementAt(0));

				if (bufferChanged && tmpnodLin != nodLin) {
					g.drawImage(mapbuffer, 0, 0, r.width, r.height, this);
					tmpnodLin.drawSelf(g);
					nodLin = tmpnodLin;
				} else {
					tmpnodLin.drawSelf(g);
					nodLin = tmpnodLin;
				}

				url = tmpnodLin.getURL();

				bufferChanged = true;

				String sbval;

				sb.setNumber(0);

				if ((sbval = tmpnodLin.toString()) != "") {
					sb.setString(sbval, 0);
					if (url != null) {
						showStatus(url.toString());
						sb.setString(url.toString(), 1);
						sb.setString(tmpnodLin.getDesc(), 2);
					} else {
						sb.setString(tmpnodLin.getDesc(), 1);
					}

				} else {
					sb.setString(tmpnodLin.getDesc(), 0);
				}
				sb.draw(g);
			} else if (bufferChanged) {
				g.drawImage(mapbuffer, 0, 0, r.width, r.height, this);
				bufferChanged = false;
			}
		} else if (matched_regions.size() > 1) {
			int minI = 0;
			int tmpdist;

			int numofRegions = matched_regions.size();
			dist = ((NodLin) matched_regions.elementAt(0)).getDistance(mouseP);

			for (int i = 1; i < numofRegions; i++) {
				tmpdist = ((NodLin) matched_regions.elementAt(i))
						.getDistance(mouseP);

				if (tmpdist < dist) {
					minI = i;
					dist = tmpdist;
				}
			}

			if (dist >= 0 && dist < 3) {
				tmpnodLin = ((NodLin) matched_regions.elementAt(minI));

				if (bufferChanged && tmpnodLin != nodLin) {
					g.drawImage(mapbuffer, 0, 0, r.width, r.height, this);
					tmpnodLin.drawSelf(g);
					nodLin = tmpnodLin;
				} else {
					tmpnodLin.drawSelf(g);
					nodLin = tmpnodLin;
				}

				url = tmpnodLin.getURL();

				bufferChanged = true;

				String sbval;
				sb.setNumber(0);
				if ((sbval = tmpnodLin.toString()) != "") {
					sb.setString(sbval, 0);
					if (url != null) {
						showStatus(url.toString());
						sb.setString(url.toString(), 1);
						sb.setString(tmpnodLin.getDesc(), 2);
					} else {
						sb.setString(tmpnodLin.getDesc(), 1);
					}

				} else {
					sb.setString(tmpnodLin.getDesc(), 0);
				}
				sb.draw(g);
			} else if (bufferChanged) {
				g.drawImage(mapbuffer, 0, 0, r.width, r.height, this);
				bufferChanged = false;
			}
		}

		mouseOver = false;
	}

	public void mouseClicked(MouseEvent e) {
		NodLin tmpnodLin;
		int dist;
		mouseP = e.getPoint();

		matched_regions.removeAllElements();

		Enumeration enode = nodes.elements();
		while (enode.hasMoreElements()) {
			node = (Node) enode.nextElement();

			if (node.withinBoundary(mouseP)) {
				matched_regions.addElement(node);
			}
		}

		int numofLines = lines.size();
		for (int i = 0; i < numofLines; i++) {
			line = (Line) lines.elementAt(i);

			if (line.withinBoundary(mouseP)) {
				matched_regions.addElement(line);
			}
		}

		if (matched_regions.size() == 1) {
			dist = ((NodLin) matched_regions.elementAt(0)).getDistance(mouseP);

			if (dist >= 0 && dist < 3) {
				tmpnodLin = ((NodLin) matched_regions.elementAt(0));
				URL url = tmpnodLin.getURL();

				if (url != null) {
					AppletContext ac = getAppletContext();
					ac.showDocument(url, "_blank");
				}
			}
		} else if (matched_regions.size() > 1) {
			int minI = 0;
			int tmpdist;

			int numofRegions = matched_regions.size();
			dist = ((NodLin) matched_regions.elementAt(0)).getDistance(mouseP);

			for (int i = 1; i < numofRegions; i++) {
				tmpdist = ((NodLin) matched_regions.elementAt(i))
						.getDistance(mouseP);

				if (tmpdist < dist) {
					minI = i;
					dist = tmpdist;
				}
			}

			if (dist >= 0 && dist < 3) {
				tmpnodLin = ((NodLin) matched_regions.elementAt(minI));
				URL url = tmpnodLin.getURL();

				if (url != null) {
					AppletContext ac = getAppletContext();
					ac.showDocument(url, "_blank");
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	class LineMatrix {
		int numofLines;

		public LineMatrix(String src, String dest) {
			numofLines = 1;
		}

		public void incrValue() {
			numofLines++;
		}

		public void decrValue() {
			numofLines--;
		}

		public int getValue() {
			return numofLines;
		}
	}

	public static BufferedImage getImage(Map props) {
		return getImage(props, Float.MAX_VALUE, Float.MAX_VALUE,
				Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public static BufferedImage getImage(Map props, float lat1, float lon1,
			float lat2, float lon2) {
		class MyAppletStub implements AppletStub {
			private Map _properties;
			private Applet _applet;

			public MyAppletStub(Map<String, String> props, Applet a) {
				_applet = a;
				_properties = props;
			}

			public void appletResize(int width, int height) {
				_applet.resize(width, height);
			}

			public AppletContext getAppletContext() {
				return null;
			}

			public java.net.URL getCodeBase() {
				try {
					return new java.net.URL("http://127.0.0.1:8080/nail.map/");
				} catch (Exception e) {
					return null;
				}
			}

			public java.net.URL getDocumentBase() {
				return getCodeBase();
			}

			public String getParameter(String p) {
				return (String) _properties.get(p);
			}

			public boolean isActive() {
				return true;
			}
		}
		MapGenerator myApplet = new MapGenerator();
		myApplet.setFont(new Font("Aerial", 10, 10));
		myApplet.setSize(1024, 768);
		int seconds = 0;
		myApplet.setStub(new MyAppletStub(props, myApplet));
		myApplet.init();
		myApplet.lines.removeAllElements();
		myApplet.nodes.clear();
		myApplet.lineMatrix.clear();
		myApplet.mapbuffer = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);
		myApplet.gc = myApplet.mapbuffer.getGraphics();
		Color bgcolor;
		String param = myApplet.getParameter("bgcolor");
		if (param != null) {
			bgcolor = NodLin.getColor(param);
			if (bgcolor == null)
				bgcolor = Color.white;
		} else
			bgcolor = Color.white;
		myApplet.gc.setColor(bgcolor);
		myApplet.gc.fillRect(0, 0, myApplet.mapbuffer.getWidth(),
				myApplet.mapbuffer.getHeight());
		myApplet.parseData(myApplet.data);
		myApplet.buildLineMatrix();
		myApplet.paintData(myApplet.gc);
		while (!myApplet.mapLoaded && (seconds++) < 5)
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		BufferedImage aux = toBufferedImage(myApplet.mapbuffer);
		if (lat1 != Float.MAX_VALUE && lat2 != Float.MAX_VALUE
				&& lon1 != Float.MAX_VALUE && lon2 != Float.MAX_VALUE) {
			Point p1 = myApplet.latLonToPoint(lat1, lon1);
			Point p2 = myApplet.latLonToPoint(lat2, lon2); // TODO: check this
			aux = aux.getSubimage(p1.x, p2.y, p2.x - p1.x, p2.y - p1.y);
		}
		myApplet.stop();
		return aux;
	}

	public static BufferedImage toBufferedImage(java.awt.Image image) {
		if (image instanceof BufferedImage)
			return (BufferedImage) image;
		image = new ImageIcon(image).getImage();
		boolean hasAlpha = hasAlpha(image);
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			if (hasAlpha)
				transparency = Transparency.BITMASK;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image
					.getHeight(null), transparency);
		} catch (HeadlessException e) {
		}
		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha)
				type = BufferedImage.TYPE_INT_ARGB;
			bimage = new BufferedImage(image.getWidth(null), image
					.getHeight(null), type);
		}
		Graphics g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	public static boolean hasAlpha(Image image) {
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	public static void main(String args[]) {
		MapGenerator aux = new MapGenerator();
		System.out.println(" --- " + aux.radiusToPixels(100));
	}

}

class Key {
	float lowerbound;
	float upperbound;
	String value;
	String label;

	public Key(String data) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(data, ":");

			int numofTokens = tokenizer.countTokens();

			String token = tokenizer.nextToken();
			if (token.indexOf("&&") > 0) {

				StringTokenizer bounds = new StringTokenizer(token, "&&");

				try {
					String temp = bounds.nextToken();
					lowerbound = Float.valueOf(temp.trim()).floatValue();
				} catch (NumberFormatException nfe) {
					System.err.println("NumberFormatException LB");
					lowerbound = Float.POSITIVE_INFINITY;
				}

				try {
					String temp = bounds.nextToken();
					upperbound = Float.valueOf(temp.trim()).floatValue();
				} catch (NumberFormatException nfe) {
					System.err.println("NumberFormatException UB");
					upperbound = Float.POSITIVE_INFINITY;
				}
			} else {

				try {
					lowerbound = Float.valueOf(token).floatValue();
				} catch (NumberFormatException nfe) {
					lowerbound = Float.POSITIVE_INFINITY;
				}

				upperbound = lowerbound;
			}

			value = tokenizer.nextToken().trim();

			if (numofTokens > 2) {
				label = tokenizer.nextToken().trim();
			} else {
				label = "";
			}
		} catch (NoSuchElementException nsee) {
			lowerbound = upperbound = Float.POSITIVE_INFINITY;
			value = null;
			label = "";
		}
	}

	public boolean withinbounds(float val) {
		if (val >= lowerbound && val <= upperbound) {
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		if (label == "") {
			return (lowerbound + " -- " + upperbound);
		} else {
			return label;
		}
	}

	public String getValue() {
		return value;
	}
}

class NodLin {
	String title;
	int size;
	float Cvalue;
	float Svalue;
	float cradius;
	Color color;
	URL url; // change to type URL
	String descp;

	static Hashtable colormap = new Hashtable();

	static {
		colormap.put("black", Color.black);
		colormap.put("blue", Color.blue);
		colormap.put("cyan", Color.cyan);
		colormap.put("darkGray", Color.darkGray);
		colormap.put("gray", Color.gray);
		colormap.put("green", Color.green);
		colormap.put("lightGray", Color.lightGray);
		colormap.put("magenta", Color.magenta);
		colormap.put("orange", Color.orange);
		colormap.put("pink", Color.pink);
		colormap.put("red", Color.red);
		colormap.put("white", Color.white);
		colormap.put("yellow", Color.yellow);
	}

	public NodLin(int size, String color) {
		title = "";
		this.size = size;
		Cvalue = Float.POSITIVE_INFINITY;
		Svalue = Float.POSITIVE_INFINITY;
		this.color = getColor(color);
		this.cradius = -1;
		url = null;
		descp = "";
	}

	public NodLin(int size, String color, String data) {
		int length = data.length();
		int start;
		String str;

		if ((start = data.indexOf("T:")) > 0 || data.indexOf("t:") > 0) {
			if (start <= 0) {
				start = data.indexOf("t:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				this.title = str;
			} else {
				this.title = "";
			}
		} else {
			this.title = "";
		}

		if ((start = data.indexOf("RD:")) > 0 || data.indexOf("rd:") > 0) {
			if (start <= 0) {
				start = data.indexOf("rd:");
			}
			try {
				String aux = data.substring(start + 3);
				int length2;
				for (length2 = 0; length2 < aux.length(); length2++)
					if (aux.charAt(length2) != '.'
							&& !Character.isDigit(aux.charAt(length2)))
						break;
				this.cradius = new Float(aux.substring(0, length2))
						.floatValue();
			} catch (NumberFormatException e) {
				this.cradius = -1;
			}
		} else {
			this.cradius = -1;
		}

		if ((start = data.indexOf("SZ:")) > 0 || data.indexOf("sz:") > 0) {
			if (start <= 0) {
				start = data.indexOf("sz:");
			}

			try {
				this.size = new Integer(parseData(start, length, data))
						.intValue();
			} catch (NumberFormatException e) {
				this.size = size;
			}
		} else {
			this.size = size;
		}

		if ((start = data.indexOf("VC:")) > 0 || data.indexOf("vc:") > 0) {
			if (start <= 0) {
				start = data.indexOf("vc:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				try {
					this.Cvalue = Float.valueOf(str).floatValue();
				} catch (NumberFormatException nfe) {
					this.Cvalue = Float.POSITIVE_INFINITY;
				}
			} else {
				this.Cvalue = Float.POSITIVE_INFINITY;
			}
		} else {
			this.Cvalue = Float.POSITIVE_INFINITY;
		}

		if ((start = data.indexOf("VS:")) > 0 || data.indexOf("vs:") > 0) {
			if (start <= 0) {
				start = data.indexOf("vs:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				try {
					this.Svalue = Float.valueOf(str).floatValue();
				} catch (NumberFormatException nfe) {
					this.Svalue = Float.POSITIVE_INFINITY;
				}
			} else {
				this.Svalue = Float.POSITIVE_INFINITY;
			}
		} else {
			this.Svalue = Float.POSITIVE_INFINITY;
		}

		if ((start = data.indexOf("CL:")) > 0 || data.indexOf("cl:") > 0) {
			if (start <= 0) {
				start = data.indexOf("cl:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				this.color = getColor(str);
			} else {
				this.color = getColor(color);
			}
		} else {
			this.color = getColor(color);
		}

		if ((start = data.indexOf("U:")) > 0 || data.indexOf("u:") > 0) {
			if (start <= 0) {
				start = data.indexOf("u:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				try {
					this.url = new URL(str);
				} catch (MalformedURLException murle) {
					this.url = null;
				}
			} else {
				this.url = null;
			}
		} else {
			this.url = null;
		}

		if ((start = data.indexOf("D:")) > 0 || data.indexOf("d:") > 0) {
			if (start <= 0) {
				start = data.indexOf("d:");
			}

			str = parseData(start, length, data);
			if (str != null) {
				this.descp = str;
			} else {
				this.descp = "";
			}
		} else {
			this.descp = "";
		}
	}

	public String parseData(int start, int length, String data) {
		char ch;
		StringBuffer value = new StringBuffer();

		if (data.charAt(start + 1) == ':') {
			start += 2;
		} else if (data.charAt(start + 2) == ':') {
			start += 3;
		} else {
			// handle case here for bad input data
		}

		for (int i = start; i < length; i++) {
			ch = data.charAt(i);
			if (ch == ':' && !value.toString().toLowerCase().equals("http")) {
				value.setCharAt(value.length() - 1, ' ');
				char tmp = value.charAt(value.length() - 2);
				if (tmp == 'v' || tmp == 'V' || tmp == 's' || tmp == 'S'
						|| tmp == 'c' || tmp == 'C' || tmp == 'r' || tmp == 'R'
						|| tmp == 'd' || tmp == 'D') {
					value.setCharAt(value.length() - 2, ' ');
				}
				break;
			}
			value.append(data.charAt(i));
		}
		return (value.toString().trim());
	}

	public static Color getColor(String color) {
		StringTokenizer tokenizer;

		// check to see if the color is represented as rgb values
		if (!color.toLowerCase().startsWith("rgb")) {
			return (Color) colormap.get(color);
		} else {
			tokenizer = new StringTokenizer(color, "-");
			if (tokenizer.countTokens() < 4) {
				return null;
			}

			tokenizer.nextToken();
			try {
				return (new Color(Integer.valueOf(tokenizer.nextToken())
						.intValue(), Integer.valueOf(tokenizer.nextToken())
						.intValue(), Integer.valueOf(tokenizer.nextToken())
						.intValue()));
			} catch (NumberFormatException e) {
				System.err
						.println("Handle numberformatexception nodlin getColor");
				return null;
			}
		}
	}

	public boolean isTitle() {
		if (title != "") {
			return true;
		}

		return false;
	}

	public void drawSelf(Graphics g) {

	}

	public int getDistance(Point pt) {
		return 0;
	}

	public String getTitle() {
		return title;
	}

	public int getSize() {
		return size;
	}

	public float getCValue() {
		return Cvalue;
	}

	public float getSValue() {
		return Svalue;
	}

	public Color getColor() {
		return color;
	}

	public float getRadius() {
		return cradius;
	}

	public URL getURL() {
		return url;
	}

	public String getDesc() {
		return descp;
	}

	public String toString() {
		return "";
	}

	public void setSize(int size) {
		this.size = size;
	}
}

class Line extends NodLin {
	String src;
	String dest;
	int direc;
	int type = 0;

	Rectangle r[];
	Point p[];
	float m[];
	float c[];

	public Line(String src, String dest, int size, String color) {
		super(size, color);
		this.src = src;
		this.dest = dest;
	}

	public Line(String src, String dest, int size, String color, String data) {
		super(size, color, data);

		this.src = src;
		this.dest = dest;

		int start;
		if ((start = data.indexOf("R:")) > 0 || data.indexOf("r:") > 0) {
			if (start <= 0) {
				start = data.indexOf("r:");
			}

			try {
				this.direc = new Integer(parseData(start, data.length(), data))
						.intValue();
			} catch (NumberFormatException e) {
				this.direc = 0;
			}
		} else {
			this.direc = 0;
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setPoint(Point p1, Point p2) {
		this.p = new Point[2];
		this.p[0] = p1;
		this.p[1] = p2;

		r = new Rectangle[1];
		m = new float[1];
		c = new float[1];

		if ((p[0].x == p[1].x) || (p[0].y == p[1].y)) {
			r[0] = splCaseHorVerLines(p[0], p[1]);
			m[0] = 0;
			c[0] = 0;
		} else {
			m[0] = new Float(p1.y - p2.y).floatValue()
					/ new Float(p1.x - p2.x).floatValue();
			c[0] = new Integer(p1.y).floatValue()
					- (new Float(m[0]).floatValue() * new Integer(p1.x)
							.floatValue());

			int X = getMin(p, 0);
			int Y = getMin(p, 1);

			r[0] = new Rectangle(X, Y, getMax(p, 0) - X, getMax(p, 1) - Y);
		}
	}

	public void setPoint(Point p[], int typ) {
		this.p = p;

		if (typ == 0) {
			r = new Rectangle[1];
			Rectangle rtmp = new Rectangle();

			int X = getMin(p, 0);
			int Y = getMin(p, 1);
			r[0] = new Rectangle(X, Y, getMax(p, 0) - X, getMax(p, 1) - Y);

			m = new float[p.length - 1];
			c = new float[p.length - 1];

			for (int i = 0; i < p.length - 1; i++) {
				if ((p[i].y == p[i + 1].y) || (p[i].x == p[i + 1].x)) {
					rtmp = splCaseHorVerLines(p[i], p[i + 1]);
					r[0] = r[0].union(rtmp);
					m[i] = 0;
					c[i] = 0;
				} else {
					m[i] = new Float(p[i].y - p[i + 1].y).floatValue()
							/ new Float(p[i].x - p[i + 1].x).floatValue();
					c[i] = new Integer(p[i].y).floatValue()
							- (new Float(m[i]).floatValue() * new Integer(
									p[i].x).floatValue());
				}
			}
		} else if (typ == 8) {
			r = new Rectangle[1];
			r[0] = new Rectangle(p[0].x - p[1].x, p[0].y - p[1].x, 2 * p[1].x,
					2 * p[1].x);
		} else {
			r = new Rectangle[2];
			Rectangle rtmp = new Rectangle();
			int X;
			int Y;

			if (p.length == 4) {
				Point ptemp[] = new Point[2];
				ptemp[0] = p[0];
				ptemp[1] = p[1];

				m = new float[2];
				c = new float[2];

				X = getMin(ptemp, 0);
				Y = getMin(ptemp, 1);
				r[0] = new Rectangle(X, Y, getMax(ptemp, 0) - X, getMax(ptemp,
						1)
						- Y);

				if ((p[0].y == p[1].y) || (p[0].x == p[1].x)) {
					rtmp = splCaseHorVerLines(p[0], p[1]);
					r[0] = r[0].union(rtmp);
					m[0] = 0;
					c[0] = 0;
				} else {
					m[0] = new Float(p[0].y - p[1].y).floatValue()
							/ new Float(p[0].x - p[1].x).floatValue();
					c[0] = new Integer(p[0].y).floatValue()
							- (new Float(m[0]).floatValue() * new Integer(
									p[0].x).floatValue());
				}

				ptemp[0] = p[2];
				ptemp[1] = p[3];

				X = getMin(ptemp, 0);
				Y = getMin(ptemp, 1);
				r[1] = new Rectangle(X, Y, getMax(ptemp, 0) - X, getMax(ptemp,
						1)
						- Y);

				if ((p[2].x == p[3].x) || (p[2].y == p[3].y)) {
					rtmp = splCaseHorVerLines(p[2], p[3]);
					r[1] = r[1].union(rtmp);
					m[1] = 0;
					c[1] = 0;
				} else {
					m[1] = new Float(p[2].y - p[3].y).floatValue()
							/ new Float(p[2].x - p[3].x).floatValue();
					c[1] = new Integer(p[2].y).floatValue()
							- (new Float(m[1]).floatValue() * new Integer(
									p[2].x).floatValue());
				}
			} else if (p.length == 6) {
				// xxx --- investigate pure horizontal || vertical

				Point ptemp[] = new Point[3];
				m = new float[p.length - 1];
				c = new float[p.length - 1];

				ptemp[0] = p[0];
				ptemp[1] = p[1];
				ptemp[2] = p[2];

				X = getMin(ptemp, 0);
				Y = getMin(ptemp, 1);
				r[0] = new Rectangle(X, Y, getMax(ptemp, 0) - X, getMax(ptemp,
						1)
						- Y);

				ptemp[0] = p[3];
				ptemp[1] = p[4];
				ptemp[2] = p[5];

				X = getMin(ptemp, 0);
				Y = getMin(ptemp, 1);
				r[1] = new Rectangle(X, Y, getMax(ptemp, 0) - X, getMax(ptemp,
						1)
						- Y);

				for (int i = 0; i < p.length - 1; i++) {
					if (i != 2 && (p[i].y == p[i + 1].y)
							|| (p[i].x == p[i + 1].x)) {
						rtmp = splCaseHorVerLines(p[i], p[i + 1]);
						if (i > 2) {
							r[1] = r[1].union(rtmp);
						} else {
							r[0] = r[0].union(rtmp);
						}
						m[i] = 0;
						c[i] = 0;
					} else {
						m[i] = new Float(p[i].y - p[i + 1].y).floatValue()
								/ new Float(p[i].x - p[i + 1].x).floatValue();
						c[i] = new Integer(p[i].y).floatValue()
								- (new Float(m[i]).floatValue() * new Integer(
										p[i].x).floatValue());
					}
				}
			}
		}
	}

	private Rectangle splCaseHorVerLines(Point p1, Point p2) {
		int X;
		int Y;

		if (p1.x == p2.x) {
			Y = Math.min(p1.y, p2.y);

			X = p1.x - 3 - size / 2;

			if (X < 0) {
				X = 0;
			}

			int Xbot;
			if ((p1.x + 3 + size / 2) > MapGenerator.r.width) {
				Xbot = MapGenerator.r.width - p1.x;
			} else {
				Xbot = 3 + size / 2;
			}

			return new Rectangle(X, Y, (p1.x - X) + Xbot, Math.abs(p1.y - p2.y));
		} else if (p1.y == p2.y) {
			X = Math.min(p1.x, p2.x);

			Y = p1.y - 3 - size / 2;

			if (Y < 0) {
				Y = 0;
			}

			int Ybot;
			if ((p1.y + 3 + size / 2) > MapGenerator.r.height) {
				Ybot = MapGenerator.r.height - p1.y;
			} else {
				Ybot = 3 + size / 2;
			}

			return new Rectangle(X, Y, Math.abs(p1.x - p2.x), (p1.y - Y) + Ybot);
		}

		return new Rectangle();
	}

	public void drawSelf(Graphics g) {
		g.setColor(MapGenerator.fgcolor);

		if (type == 8) {
			MapGenerator.drawSCircle(g, size, p);
		} else {
			MapGenerator.drawSLine(g, size, p, direc);
		}
	}

	public int getDistance(Point pt) {
		int x2;
		int y2;
		int X;
		int Y;
		int minDist = MapGenerator.r.height;
		int tmpDist = MapGenerator.r.height;
		Point ptmp[] = new Point[2];
		boolean first = true;

		if (type == 8) {
			if (r[0].contains(pt)) {
				int a = Math.abs(pt.x - (r[0].x + p[1].x));
				int b = Math.abs(pt.y - (r[0].y + p[1].x));

				int c = new Long(Math.round(Math.sqrt(new Double(a * a + b * b)
						.doubleValue()))).intValue();

				if (size == 1) {
					return Math.abs(p[1].x - c);
				} else {
					if (c < p[1].x && c > p[1].x - size) {
						return 0;
					}

					return Math.min(Math.abs(c - p[1].x), Math.abs(c - p[1].x)
							- size);
				}
			}

			return 3;
		}

		for (int i = 0; i < m.length; i++) {
			if (p[i].x == MapGenerator.r.width && p[i + 1].x == 0
					&& m.length > 2) {
				continue;
			} else if (m.length == 2 && i == 1) {
				ptmp[0] = p[i + 1];
				ptmp[1] = p[i + 2];
			} else {
				ptmp[0] = p[i];
				ptmp[1] = p[i + 1];
			}

			if (m[i] == 0 && c[i] == 0) {
				if (p[i].x == p[i + 1].x) {
					tmpDist = Math.abs(pt.x - p[i].x);
				} else if (p[i].y == p[i + 1].y) {
					tmpDist = Math.abs(pt.y - p[i].y);
				}
			} else if (pt.x < getMin(ptmp, 0) || pt.x > getMax(ptmp, 0)) {
				continue;
			} else if (pt.y < getMin(ptmp, 1) || pt.y > getMax(ptmp, 1)) {
				continue;
			}

			if (!(m[i] == 0 && c[i] == 0)) {
				x2 = Math.round((pt.y - c[i]) / m[i]);
				y2 = Math.round(pt.x * m[i] + c[i]);
				X = Math.abs(pt.x - x2);
				Y = Math.abs(pt.y - y2);
				tmpDist = new Double(X * Y
						/ Math.sqrt(new Double(X * X + Y * Y).doubleValue()))
						.intValue();
			}

			// System.err.println("tmpDist = " +tmpDist + " i = " + i);
			// System.err.println("----");

			if (first) {
				if (tmpDist >= 0) {
					minDist = tmpDist;
				}
				first = false;
			} else if (tmpDist >= 0 && tmpDist < minDist) {
				minDist = tmpDist;
			}
		}

		// xxx - minDist calculation for the ends of lines type 4 and 6

		if (size > 1 && minDist > 2) {
			if (minDist > size / 2) {
				// System.err.println("***minDist = " + (minDist - size/2));
				return (minDist - size / 2);
			}
			// System.err.println("size = " + size);
			// System.err.println("---minDist = " + minDist);
			// System.err.println("minDist 1 returned");
			return 1;
		}

		// System.err.println("minDist = " + minDist);
		// System.err.println("*******************************************");

		return minDist;
	}

	public boolean withinBoundary(Point pt) {
		if (r != null) {
			for (int i = 0; i < r.length; i++) {
				if (r[i] != null && r[i].contains(pt)) {
					return true;
				}
			}
		}

		return false;
	}

	private int getMin(Point pt[], int XY) {
		int min;

		if (XY == 0) {
			min = pt[0].x;

			for (int i = 1; i < pt.length; i++) {
				min = Math.min(min, pt[i].x);
			}
		} else {
			min = pt[0].y;

			for (int i = 1; i < pt.length; i++) {
				min = Math.min(min, pt[i].y);
			}
		}

		return min;
	}

	private int getMax(Point pt[], int XY) {
		int max;

		if (XY == 0) {
			max = pt[0].x;

			for (int i = 1; i < pt.length; i++) {
				max = Math.max(max, pt[i].x);
			}
		} else {
			max = pt[0].y;

			for (int i = 1; i < pt.length; i++) {
				max = Math.max(max, pt[i].y);
			}
		}

		return max;
	}

	public int getType() {
		return type;
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public int getDirec() {
		return direc;
	}

	public String toStringPoint() {
		String temp = "";

		for (int i = 0; i < p.length; i++) {
			temp += "p[" + i + "] = " + p[i].toString();
		}

		return temp;
	}

	public String toString() {
		String tmp = "";

		if (Cvalue != Float.POSITIVE_INFINITY) {
			tmp = "Color = " + Cvalue + " ";
		}

		if (Svalue != Float.POSITIVE_INFINITY) {
			tmp += "Size = " + Svalue;
		}

		return tmp;
	}

	public int setDirec(int mult) {
		if (direc > 0) {
			direc = direc + (3 * mult);
		}
		return direc;
	}

	public void chngDirec(int direc) {
		this.direc = direc;
	}
}

class Node extends NodLin {
	int nodNum;
	float lat;
	float lon;
	int[] quad_matrix = new int[8];

	Rectangle r = null;

	static int labelorder[] = { 5, 6, 2, 1, 7, 0, 4, 3 };

	public Node(int nodNum, String lat, String lon, int size, String color) {
		super(size, color);

		this.nodNum = nodNum;

		try {
			this.lat = Float.valueOf(lat).floatValue();
		} catch (NumberFormatException e) {
			System.err.println("Handle numberformatexception node_lat");
		}

		try {
			this.lon = Float.valueOf(lon).floatValue();
		} catch (NumberFormatException e) {
			System.err.println("Handle numberformatexception node_lon");
		}

		for (int i = 0; i < 8; i++) {
			quad_matrix[i] = 0;
		}
	}

	public Node(int nodNum, String lat, String lon, int size, String color,
			String data) {
		super(size, color, data);

		this.nodNum = nodNum;

		try {
			this.lat = Float.valueOf(lat).floatValue();
		} catch (NumberFormatException e) {
			System.err.println("Handle numberformatexception node_lat");
		}

		try {
			this.lon = Float.valueOf(lon).floatValue();
		} catch (NumberFormatException e) {
			System.err.println("Handle numberformatexception node_lon");
		}

		for (int i = 0; i < 8; i++) {
			quad_matrix[i] = 0;
		}
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}

	public String toString() {
		return new String(Math.abs(lat) + ((lat < 0.0) ? " S" : " N") + ", "
				+ Math.abs(lon) + ((lon < 0.0) ? " W" : " E"));
	}

	public void setPoint(Point p) {
		int width = size / 2;
		r = new Rectangle(p.x - width, p.y - width, size, size);
	}

	public int[] getQuadMatrix() {
		return quad_matrix;
	}

	public String quadMatrixToString() {
		String temp = toString();
		for (int i = 0; i < 8; i++) {
			temp = temp + " " + i + " = " + quad_matrix[i];
		}
		return temp;
	}

	public int getQuadrant() {
		for (int i = 0; i < 8; i += 2) {
			if (quad_matrix[labelorder[i]] == 0
					&& quad_matrix[labelorder[i + 1]] == 0) {
				return (labelorder[i] * 10) + labelorder[i + 1];
			}
		}

		for (int i = 0; i < 8; i++) {
			if (quad_matrix[labelorder[i]] == 0) {
				return labelorder[i];
			}
		}

		int[] temp = new int[8];
		int tmp;

		for (int i = 0; i < 8; i++) {
			temp[i] = quad_matrix[labelorder[i]];
		}

		for (int i = 0; i < 8; i++) {
			for (int j = i + 1; j < 8; j++) {
				if (temp[i] > temp[j]) {
					tmp = temp[j];
					temp[j] = temp[i];
					temp[i] = tmp;
				}
			}
		}

		return temp[0];
	}

	public boolean isEmpty() {
		for (int i = 0; i < 8; i++) {
			if (quad_matrix[i] > 0) {
				return false;
			}
		}

		return true;
	}

	public boolean withinBoundary(Point pt) {
		if (r != null && r.contains(pt)) {
			return true;
		}

		return false;
	}

	public int getDistance(Point pt) {
		int a = Math.abs(pt.x - (r.x + size / 2));
		int b = Math.abs(pt.y - (r.y + size / 2));

		int c = new Long(Math.round(Math.sqrt(new Double(a * a + b * b)
				.doubleValue()))).intValue();

		if (c < size / 2) {
			return 0;
		}

		return Math.abs(c - size / 2);
	}

	public void drawSelf(Graphics g) {
		g.setColor(MapGenerator.fgcolor);
		g.fillOval(r.x, r.y, size, size);
	}

	public int getNodNum() {
		return nodNum;
	}

}

class StatusBar extends Panel {

	// The current string to be displayed in each slot
	protected String[] strings;

	// For now this will only work for the last stdin
	// This will allow the box to grow so that by wrapping the last string
	// around we can display the whole thing. Hope it doesn't look too horrible
	// 
	// wrap_strings will be set to null every time a value in strings is changed
	// That way they will be recalculated when before they are draw. I have to
	// do it in the draw. Because that is the only part where I am certain
	// to have the canvas set already, allowing me to get its dimensions and
	// fonts.
	protected String[] wrap_strings;

	// The length of the longest string so far displayed in each slot
	protected int[] lengths;
	protected int last_width = 0;

	// This are the colors which are used for the box effect.
	static final protected Color LIGHT = new Color(240, 240, 240);
	static final protected Color TOP = Color.lightGray;
	static final protected Color BOTTOM = Color.white;
	static final protected Color DARK = Color.darkGray;

	// This is the color of the text
	protected Color string_color = Color.black;

	// The size of the slop on the Box
	static final protected int SLOP_X = 2;
	static final protected int FLAT_X = 2;

	static final protected int SLOP_Y = 2;
	static final protected int FLAT_Y = 2;

	// This is the total height that the scroll bar will be if know
	protected int current_height = 0;

	// This the the parent_component on which the status bar will be drawn
	protected Component parent_component;

	// parents
	protected FontMetrics fm;

	public StatusBar(Component parent) {
		current_height = 0;
		parent_component = parent;
		strings = new String[1];
		lengths = new int[1];

		fm = parent_component.getFontMetrics(parent_component.getFont());
	}

	public StatusBar(int[] input_lengths) {
		strings = new String[input_lengths.length];
		lengths = new int[input_lengths.length];
		for (int index = 0; index < input_lengths.length; index++)
			lengths[index] = input_lengths[index];
		wrap_strings = null;
	}

	public void setString(String string, int index) {

		checkSize(index);
		strings[index] = string;
		// if (lengths[index] < fm.stringWidth(string) + fm.charWidth(' '))
		lengths[index] = fm.stringWidth(string) + fm.charWidth(' ');

		wrap_strings = null;
	}

	public String getString(int index) {
		if (index >= strings.length)
			return null;
		return strings[index];
	}

	public void setLength(int length, int index) {
		checkSize(index);
		lengths[index] = length;
		wrap_strings = null;
	}

	public int getLength(int index) {
		if (index >= lengths.length)
			return 0;
		return lengths[index];
	}

	public void setNumber(int number) {
		if (number < lengths.length) {
			String[] temp_s = new String[number];
			int[] temp_l = new int[number];
			for (int index = 0; index < number; index++) {
				temp_s[index] = strings[index];
				temp_l[index] = lengths[index];
			}
			strings = temp_s;
			lengths = temp_l;
		} else
			checkSize(number - 1);
	}

	public int getNumber() {
		return strings.length;
	}

	public int getHeight() {
		return current_height;
	}

	public void checkSize(int location) {
		if (location >= lengths.length) {
			String[] temp_s = new String[location + 1];
			int[] temp_l = new int[location + 1];
			for (int index = 0; index < lengths.length; index++) {
				temp_s[index] = strings[index];
				temp_l[index] = lengths[index];
			}
			strings = temp_s;
			lengths = temp_l;
		}
	}

	synchronized public void draw(Graphics g) {
		Dimension dim = parent_component.getSize();

		int padX = fm.charWidth(' ');
		int padY = fm.getHeight();

		int y_shift = FLAT_Y + SLOP_Y;
		int x_shift = FLAT_X + SLOP_Y;
		int box_width = dim.width;
		int box_x = 0;

		// This will check to see if the strings have been changed
		// If they have then they will be reset.
		// null means that the wrap strings need to be reset.
		if (wrap_strings == null || dim.width != last_width) {
			wrap_strings = CreateWrapStrings(dim, fm);
			if (wrap_strings == null)
				System.err.println("StatusBar:wrap_strings is null");
			last_width = dim.width;
		}

		int box_height = (5 * padY * wrap_strings.length) / 4 + 2 * y_shift
				+ padY / 4;
		int box_hole_height = box_height - 2 * y_shift;
		int box_y = dim.height - box_height;

		// First draw the raised level
		drawHill(g, box_x, box_y, box_width, box_height);
		box_y += y_shift;
		for (int index = 0; index < strings.length; index++) {
			String string = strings[index];
			if (string != null
					&& lengths[index] < fm.stringWidth(string) + padX)
				lengths[index] = fm.stringWidth(string) + padX;
			box_x += x_shift;
			box_width = lengths[index];

			// Makes the last one fill all the way across
			if (index == strings.length - 1)// && dim.width > box_x + box_width)
				box_width = dim.width - x_shift - box_x;
			// Draw the hole for the string to side in
			drawHole(g, box_x, box_y, box_width, box_hole_height);

			// Draw the string inside of each hole
			if (string != null && index < strings.length - 1) {
				g.setColor(string_color);
				g.drawString(string, box_x + padX / 2, box_y + padY);
			}
			// Draw the last string in stages
			else {
				g.setColor(string_color);
				int string_x = box_x + padX / 2;
				int string_y = box_y + padY;
				if (wrap_strings != null) {
					for (int i = 0; i < wrap_strings.length; i++) {
						String str = wrap_strings[i];
						if (str != null)
							g.drawString(str, string_x, string_y);
						string_y += (5 * padY) / 4;
					}
				}
			}

			box_x += box_width;
		}

		if (box_height < current_height && current_height > 0) {
			parent_component.repaint(0, dim.height - current_height, dim.width,
					current_height - box_height);
		}

		// Sets the total height
		current_height = box_height;
	}

	public void drawHill(Graphics g, int x, int y, int width, int height) {
		drawBox(g, LIGHT, TOP, DARK, x, y, width, height);
	}

	public void drawHole(Graphics g, int x, int y, int width, int height) {
		drawBox(g, DARK, BOTTOM, LIGHT, x, y, width, height);
	}

	protected void drawBox(Graphics g, Color left, Color middle, Color right,
			int x, int y, int width, int height) {
		int[] x_top = { x, x + width, x + width - SLOP_X, x + SLOP_X,
				x + SLOP_X, x };
		int[] y_top = { y, y, y + SLOP_Y, y + SLOP_Y, y + height - SLOP_Y,
				y + height };
		int[] x_bot = { x, x + SLOP_X, x + width - SLOP_X, x + width - SLOP_X,
				x + width, x + width };
		int[] y_bot = { y + height, y + height - SLOP_Y, y + height - SLOP_Y,
				y + SLOP_Y, y, y + height };
		g.setColor(left);
		g.fillPolygon(x_top, y_top, 6);
		g.setColor(right);
		g.fillPolygon(x_bot, y_bot, 6);
		x += SLOP_X;
		width -= 2 * SLOP_X;
		y += SLOP_Y;
		height -= 2 * SLOP_Y;
		g.setColor(middle);
		g.fillRect(x, y, width, height);
	}

	protected String[] CreateWrapStrings(Dimension dim, FontMetrics fm) {
		final int DIVIDER_SIZE = (2 * SLOP_X + FLAT_X);
		int box_width = dim.width - 2 * DIVIDER_SIZE;
		String[] answer;
		final int FINAL_STRING = strings.length - 1;
		for (int index = 0; index < FINAL_STRING; index++) {
			box_width -= lengths[index] + DIVIDER_SIZE;
		}
		int length = lengths[FINAL_STRING];
		String string = strings[FINAL_STRING];
		if (string == null)
			string = "";

		final char[] chars = string.toCharArray();

		if (box_width <= 0 || ((float) length) / box_width <= 1
				|| chars.length < 1) {
			answer = new String[1];
			if (string != null)
				answer[0] = string;
			else
				answer[0] = "";
		} else {
			answer = new String[(int) (length / box_width)];
			int char_start = 0;
			int char_end = 1;
			int current_string = 0;
			int current_length = 0;
			while (char_end < chars.length) {
				int char_length = fm.charWidth(chars[char_end]);
				if (current_length + char_length >= box_width) {
					if (current_string == answer.length) {
						String[] temp = answer;
						answer = new String[temp.length * 2];
						for (int i = 0; i < temp.length; i++)
							answer[i] = temp[i];
					}
					answer[current_string] = new String(chars, char_start,
							char_end - char_start);
					char_start = char_end;
					current_string++;
					current_length = 0;
				}
				current_length += char_length;
				char_end++;
			}
			if (current_string == answer.length) {
				String[] temp = answer;
				answer = new String[temp.length * 2];
				for (int i = 0; i < temp.length; i++)
					answer[i] = temp[i];
			}
			answer[current_string] = new String(chars, char_start, char_end
					- char_start);
			current_string++;
			if (answer.length != current_string) {
				String[] temp = answer;
				answer = new String[current_string];
				for (int i = 0; i < current_string; i++)
					answer[i] = temp[i];
			}
		}
		return answer;
	}
}
