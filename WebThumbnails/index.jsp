<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="eu.digmap.thumbnails.*"%>
<%@ page import="javazoom.upload.*"%>
<%@ page import="java.awt.*"%>
<%@ page import="org.jCharts.chartData.*"%>
<%@ page import="org.jCharts.properties.*"%>
<%@ page import="org.jCharts.types.ChartType"%>
<%@ page import="org.jCharts.axisChart.*"%>
<%@ page import="org.jCharts.nonAxisChart.*"%>
<%@ page import="org.jCharts.properties.util.ChartFont"%>
<%@ page import="org.jCharts.encoders.ServletEncoderHelper"%>
<%
	String url = request.getParameter("url");
	if (url != null && url.equals("chart")) {
		try {
			int width = request.getParameter("width")!=null ? (new Integer(request.getParameter("width"))).intValue() : 600;
			int height = request.getParameter("height")!=null ? (new Integer(request.getParameter("height"))).intValue() : 400;
			int series = request.getParameter("series")!=null ? (new Integer(request.getParameter("series"))).intValue() : 1;
			int classes = request.getParameter("classes")!=null ? (new Integer(request.getParameter("classes"))).intValue() : 1;
			String colorscheme = request.getParameter("colorscheme")!=null ? request.getParameter("colorscheme") : "default";
			String type = request.getParameter("type")!=null ? request.getParameter("type") : "line";
			String[] xAxisLabels= request.getParameter("classlabel")!=null ? request.getParameter("classlabel").split(";") : new String[classes]; 
			String xAxisTitle= request.getParameter("xtitle")!=null ? request.getParameter("xtitle") : "";
			String yAxisTitle= request.getParameter("ytitle")!=null ? request.getParameter("ytitle") : "";
			String title=request.getParameter("title")!=null ? request.getParameter("title") : "";
			String[] legendLabels = request.getParameter("legendlabel")!=null ? request.getParameter("legendlabel").split(";") : new String[series]; 
			String[] dataAux = request.getParameter("data").split(";");
			double[][] data = new double[series][classes];
			Stroke[] strokes = new Stroke[series];
			Shape[] shapes = new Shape[series];
			boolean[] fillPointFlags = new boolean[series];
			for(int i=0, k=0; i<series; i++) for(int j=0; j<classes; j++) data[i][j] = (new Double(dataAux[k++])).doubleValue();
			for (int i=0; i<legendLabels.length; i++) legendLabels[i] = legendLabels[i] == null ? "" : legendLabels[i].trim();
			for (int i=0; i<xAxisLabels.length; i++) xAxisLabels[i] = xAxisLabels[i] == null ? "" : xAxisLabels[i].trim();
			for (int i=0; i<strokes.length; i++) strokes[i] = LineChartProperties.DEFAULT_LINE_STROKE;
			for (int i=0; i<fillPointFlags.length; i++) fillPointFlags[i] = true; 
			for (int i=0; i<shapes.length; i++) {
				if((i % 4) == 0) shapes[i] = PointChartProperties.SHAPE_CIRCLE;
				if((i % 4) == 1) shapes[i] = PointChartProperties.SHAPE_DIAMOND;
				if((i % 4) == 2) shapes[i] = PointChartProperties.SHAPE_SQUARE;
				else shapes[i] = PointChartProperties.SHAPE_TRIANGLE;
			}
			Paint[] paints = new Paint[series];
			if (colorscheme.equals("default")) paints = HeatMapPanel.createGradient(Color.YELLOW,Color.BLUE,series);
			else if (colorscheme.equals("reds")) paints = HeatMapPanel.createGradient(Color.RED,Color.GRAY,series);
			else paints = HeatMapPanel.createGradient(Color.YELLOW,Color.BLUE,series);	
			ChartTypeProperties lineChartProperties;
			ChartType ctype;
			if(type.equals("line")) {
				lineChartProperties = new LineChartProperties(strokes,shapes);
				ctype = ChartType.LINE;
			} else if(type.equals("point")) {
				lineChartProperties = new PointChartProperties(shapes,fillPointFlags,paints);
				ctype = ChartType.POINT;
			} else if(type.equals("stackedarea")) {
				lineChartProperties = new StackedAreaChartProperties();
				ctype = ChartType.AREA_STACKED;
			} else if(type.equals("scatter")) {
				lineChartProperties = new ScatterPlotProperties(strokes,shapes);
				ctype = ChartType.SCATTER_PLOT;
			} else if(type.equals("area")) {
				lineChartProperties = new AreaChartProperties();
				ctype = ChartType.AREA;
			} else if(type.equals("clusteredbar")) {
				lineChartProperties = new ClusteredBarChartProperties();
				ctype = ChartType.BAR_CLUSTERED;
			} else if(type.equals("stackedbar")) {
				lineChartProperties = new StackedBarChartProperties();
				ctype = ChartType.BAR_STACKED;
			} else {
				lineChartProperties = new LineChartProperties(strokes,shapes);
				ctype = ChartType.LINE;
			}
			LegendProperties legendProperties = new LegendProperties();
			ChartProperties chartProperties = new ChartProperties();
			AxisProperties axisProperties = new AxisProperties( false );
			ChartFont axisScaleFont = new ChartFont( new Font( "Georgia Negreta cursiva", Font.PLAIN, 13 ), Color.black );
			axisProperties.getXAxisProperties().setScaleChartFont( axisScaleFont );
			axisProperties.getYAxisProperties().setScaleChartFont( axisScaleFont );
			ChartFont axisTitleFont = new ChartFont( new Font( "Arial Narrow", Font.PLAIN, 14 ), Color.black );
			axisProperties.getXAxisProperties().setTitleChartFont( axisTitleFont );
			axisProperties.getYAxisProperties().setTitleChartFont( axisTitleFont );		
			DataSeries dataSeries = new DataSeries( xAxisLabels, xAxisTitle, yAxisTitle, title );
			response.setContentType("image/png");
			if(type.equals("pie")) {
				if(classes>1) throw new Exception("Pie charts do not support multiple data classes.");
				double data2[] = new double[series];
				for (int i=0; i<series; i++) data2[i] = data[i][0]; 
				PieChart2DProperties pieChart2DProperties= new PieChart2DProperties();
				PieChartDataSet pieChartDataSet= new PieChartDataSet( title, data2, legendLabels, paints, pieChart2DProperties );
				PieChart2D chart = new PieChart2D( pieChartDataSet, new LegendProperties(), new ChartProperties(), width, height );
				ServletEncoderHelper.encodePNG(chart, response);
			} else {
				AxisChartDataSet acds = new AxisChartDataSet(data, legendLabels, paints, ctype, lineChartProperties );
				dataSeries.addIAxisPlotDataSet(acds);
				AxisChart chart = new AxisChart(dataSeries, chartProperties, axisProperties,legendProperties, width, height);	
				ServletEncoderHelper.encodePNG(chart, response);
			}
		} catch(Exception e) {
			response.sendError(500,"Service error: "+e.toString());
		}
	} if (url != null && url.equals("heatmap")) {		
		int width = request.getParameter("width")==null ?  0 : (new Integer(request.getParameter("width"))).intValue();
		int height = request.getParameter("width")==null ? 0 : (new Integer(request.getParameter("height"))).intValue();
		String data = request.getParameter("data");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		new HeatMapThumbnailMaker(url, width, height, (byte) 125, data).make(false, os);
		os.close();
	} else if (url != null && (url.equals("simplemap") || url.equals("reliefmap") || url.equals("oldmap") || url.equals("satellitemap") || url.equals("lightmap"))) {
		int width = request.getParameter("width")==null ?  0 : (new Integer(request.getParameter("width"))).intValue();
		int height = request.getParameter("height")==null ?  0 : (new Integer(request.getParameter("height"))).intValue();
		byte transparency = (byte)0;
		if(request.getParameter("transparency")!=null) {
			if (request.getParameter("transparency").equals("true")) transparency = 125;
			else if (request.getParameter("transparency").equals("false")) transparency = 0;
			else transparency = (new Byte(request.getParameter("transparency"))).byteValue();
		}
		int radius = request.getParameter("radius") ==null ? 30 : (new Integer(request.getParameter("radius"))).intValue() * 2;
		float cradius = request.getParameter("cradius")==null ? Float.MAX_VALUE : (new Float(request.getParameter("cradius"))).floatValue();
		float lat  = request.getParameter("lat")==null  ? Float.MAX_VALUE : (new Float(request.getParameter("lat"))).floatValue();
		float lon  = request.getParameter("lon")==null  ? Float.MAX_VALUE : (new Float(request.getParameter("lon"))).floatValue();
		float lat1 = request.getParameter("lat1")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat1"))).floatValue();
		float lon1 = request.getParameter("lon1")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon1"))).floatValue();
		float lat2 = request.getParameter("lat2")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat2"))).floatValue();
		float lon2 = request.getParameter("lon2")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon2"))).floatValue();
		float lat3 = request.getParameter("lat3")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat3"))).floatValue();
		float lon3 = request.getParameter("lon3")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon3"))).floatValue();
		float lat4 = request.getParameter("lat4")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat4"))).floatValue();
		float lon4 = request.getParameter("lon4")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon4"))).floatValue();
		float lat5 = request.getParameter("lat5")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat5"))).floatValue();
		float lon5 = request.getParameter("lon5")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon5"))).floatValue();
		float lat6 = request.getParameter("lat6")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat6"))).floatValue();
		float lon6 = request.getParameter("lon6")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon6"))).floatValue();
		float lat7 = request.getParameter("lat7")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lat7"))).floatValue();
		float lon7 = request.getParameter("lon7")==null ? Float.MAX_VALUE : (new Float(request.getParameter("lon7"))).floatValue();
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		new MapThumbnailMaker(url, width, height, transparency, lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, lat4, lon4, lat5, lon5, lat6, lon6, lat7, lon7, radius).make(false, os);
		os.close();
	} else if (url != null && url.length() != 0) {
		int width = request.getParameter("width")==null ?  0 : (new Integer(request.getParameter("width"))).intValue();
		int height = request.getParameter("height")==null ?  0 : (new Integer(request.getParameter("height"))).intValue();
		float rotation = request.getParameter("rotation")==null ?  0 : (new Float(request.getParameter("rotation"))).floatValue();
		byte transparency = (byte)0;
		if(request.getParameter("transparency")!=null) {
			if (request.getParameter("transparency").equals("true")) transparency = 125;
			else if (request.getParameter("transparency").equals("false")) transparency = 0;
			else transparency = (new Byte(request.getParameter("transparency"))).byteValue();
		}
		float transparencyWidth1 = request.getParameter("twidth1")==null ?  101 : (new Float(request.getParameter("twidth1"))).floatValue();
		float transparencyWidth2 = request.getParameter("twidth2")==null ?  101 : (new Float(request.getParameter("twidth2"))).floatValue();
		float transparencyHeight1 = request.getParameter("theight1")==null ?  101 : (new Float(request.getParameter("theight1"))).floatValue();
		float transparencyHeight2 = request.getParameter("theight2")==null ?  101 : (new Float(request.getParameter("theight2"))).floatValue();
		if (url.indexOf("://") == -1) url = "http://" + url;
		String replaceurl = request.getParameter("replaceurl");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		if (replaceurl == null || replaceurl.length() == 0) {
			ThumbnailMakerFactory.getThumbnailMaker(url, width, height, transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).make(true, os);
		} else {
			if (replaceurl.indexOf("://") == -1) replaceurl = "http://" + replaceurl;
			ThumbnailMakerFactory.getThumbnailMaker(url, width, height, transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).makeAndUpdate(os, replaceurl);
		}
		os.close();
	} else if (MultipartFormDataRequest.isMultipartFormData(request)) {
		MultipartFormDataRequest mrequest = new MultipartFormDataRequest(request);
		int width = mrequest.getParameter("width")==null ?  0 : (new Integer(mrequest.getParameter("width"))).intValue();
		int height = mrequest.getParameter("height")==null ?  0 : (new Integer(mrequest.getParameter("height"))).intValue();
		float rotation = mrequest.getParameter("rotation")==null ?  0 : (new Float(mrequest.getParameter("rotation"))).floatValue();
		byte transparency = (byte)0;
		if(mrequest.getParameter("transparency")!=null) {
			if (mrequest.getParameter("transparency").equals("true")) transparency = 125;
			else if (mrequest.getParameter("transparency").equals("false")) transparency = 0;
			else transparency = (new Byte(mrequest.getParameter("transparency"))).byteValue();
		}
		float transparencyWidth1 = mrequest.getParameter("twidth1")==null ?  101 : (new Float(mrequest.getParameter("twidth1"))).floatValue();
		float transparencyWidth2 = mrequest.getParameter("twidth2")==null ?  101 : (new Float(mrequest.getParameter("twidth2"))).floatValue();
		float transparencyHeight1 = mrequest.getParameter("theight1")==null ?  101 : (new Float(mrequest.getParameter("theight1"))).floatValue();
		float transparencyHeight2 = mrequest.getParameter("theight2")==null ?  101 : (new Float(mrequest.getParameter("theight2"))).floatValue();
		Hashtable files = mrequest.getFiles();
		if ((files != null) && (!files.isEmpty())) {
			UploadFile file = (UploadFile) files.get("file");
			if (file != null) {
				String replaceurl = mrequest.getParameter("replaceurl");
				response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				if (replaceurl == null || replaceurl.length() == 0) {
					new ImageThumbnailMaker(file.getFileName(),file.getInpuStream(), width, height, transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).make(true, os);
				} else {
					if (replaceurl.indexOf("://") == -1) replaceurl = "http://" + replaceurl;
					new ImageThumbnailMaker(file.getFileName(),file.getInpuStream(), width, height, transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).makeAndUpdate(os, replaceurl);
				}
				os.close();
			}
		}
	} else {
%>
<head>
<title>DIGMAP Thumbnail Services</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<link href="style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<body class="bodymain">
<center><img src="DIGMAP_logo.png" /></center>
<table border="0">
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600" border="0">
			<tr>
				<td>

				<table class="tablemain">
					<tr>
						<td class="tableheader">DIGMAP Thumbnail Service</td>
					</tr>
					<tr>
						<td class="infobody">This service can generate thumbnails for
						HTML pages, PDF documents or raster images. These thumbnails can
						either be PNG files, for usage in the DIGMAP user interface, or
						GeoTIFF files, for incorporation into a Web mapping interface.</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="8"></td>
	</tr>
</table>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail</td>
					</tr>
					<tr>
						<td class="tablekey">URL</td>
						<td class="infobody"><input type="text" size="51" name="url"
							value="http://digmap1.ist.utl.pt:8080/nail.map/DIGMAP_logo.png" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Rotation</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="rotation" value="0" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail with a transparency window</td>
					</tr>
					<tr>
						<td class="tablekey">URL</td>
						<td class="infobody"><input type="text" size="51" name="url"
							value="http://digmap1.ist.utl.pt:8080/nail.map/DIGMAP_logo.png" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency Window</td>
						<td class="infobody">
						  H1<input type="text" size="3" maxlength="3" name="theight1" value="30" />%&nbsp;
						  W1<input type="text" size="3" maxlength="3" name="twidth1" value="30" />%&nbsp;
						  H2<input type="text" size="3" maxlength="3" name="theight2" value="70" />%&nbsp;
						  W2<input type="text" size="3" maxlength="3" name="twidth2" value="70" />%&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="50" /></td>
					</tr>
					<tr>
						<td class="tablekey">Rotation</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="rotation" value="0" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="post" enctype="multipart/form-data" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						from image</td>
					</tr>
					<tr>
						<td class="tablekey">Image</td>
						<td class="infobody"><input type="file" size="51" name="file" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Rotation</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="rotation" value="0" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						with a map and a marker</td>
					</tr>
					<tr>
						<td class="tablekey">Map Type</td>
						<td class="infobody"><select name="url">
							<option value="simplemap">Simple Map</option>
							<option value="reliefmap">Relief Map</option>
							<option value="satellitemap">Satellite Map</option>
							<option value="oldmap">Old Map</option>
							<option value="lightmap">Light Map</option>
						</select></td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker Latitude</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lat" value="39" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker Longitude</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lon" value="-9" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker radius</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="radius" value="15" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						with a map and a rectangular marker</td>
					</tr>
					<tr>
						<td class="tablekey">Map Type</td>
						<td class="infobody"><select name="url">
							<option value="simplemap">Simple Map</option>
							<option value="reliefmap">Relief Map</option>
							<option value="satellitemap">Satellite Map</option>
							<option value="oldmap">Old Map</option>
							<option value="lightmap">Light Map</option>
						</select></td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Lower Lat/Lon</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lat1" value="39" /><input type="text"
							size="10" maxlength="10" name="lon1" value="-9" /></td>
					</tr>
					<tr>
						<td class="tablekey">Upper Lat/Lon</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lat2" value="70" /><input type="text"
							size="10" maxlength="10" name="lon2" value="50" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker radius</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="radius" value="15" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						with a map and a circular marker</td>
					</tr>
					<tr>
						<td class="tablekey">Map Type</td>
						<td class="infobody"><select name="url">
							<option value="simplemap">Simple Map</option>
							<option value="reliefmap">Relief Map</option>
							<option value="satellitemap">Satellite Map</option>
							<option value="oldmap">Old Map</option>
							<option value="lightmap">Light Map</option>
						</select></td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker Latitude</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lat3" value="39" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker Longitude</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="lon3" value="-9" /></td>
					</tr>
					<tr>
						<td class="tablekey">Marker radius</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="radius" value="15" /></td>
					</tr>
					<tr>
						<td class="tablekey">Circle Radius in Kms</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="cradius" value="1000" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Replace image in the
						cache</td>
					</tr>
					<tr>
						<td class="tablekey">Image URL</td>
						<td class="infobody"><input type="text" size="51" name="url"
							value="http://digmap1.ist.utl.pt:8080/nail.map/DIGMAP_logo.png" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">URL to replace</td>
						<td class="infobody"><input type="text" size="51"
							name="replaceurl"
							value="http://digmap1.ist.utl.pt:8080/nail.map/DIGMAP_logo.png" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Rotation</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="rotation" value="0" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="post" enctype="multipart/form-data" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Replace image in the
						cache</td>
					</tr>
					<tr>
						<td class="tablekey">Image</td>
						<td class="infobody"><input type="file" size="51" name="file" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">URL to replace</td>
						<td class="infobody"><input type="text" size="51"
							name="replaceurl"
							value="http://digmap1.ist.utl.pt:8080/nail.map/DIGMAP_logo.png" />
						</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="160" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="100" /></td>
					</tr>
					<tr>
						<td class="tablekey">Transparency</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="transparency" value="false" /></td>
					</tr>
					<tr>
						<td class="tablekey">Rotation</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="rotation" value="0" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="submit" name="button"
							value="Generate" /> <input type="reset" name="reset"
							value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						with a heat map</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="360" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="180" /></td>
					</tr>
					<tr>
						<td class="tablekey">Data points</td>
						<td class="infobody"><input type="text" size="51" name="data"
							value="0,0,1 ; -20,20,1" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="hidden" name="url" value="heatmap" />
						<input type="submit" name="button" value="Generate" /> <input
							type="reset" name="reset" value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<form method="get" action="index.jsp">
<table>
	<tr>
		<td width="33%">&nbsp;</td>
		<td width="33%">
		<table width="600">
			<tr>
				<td>
				<table class="tablemain">
					<tr>
						<td class="tableheader" colspan="2">Generate PNG thumbnail
						with a chart image</td>
					</tr>
					<tr>
						<td class="tablekey">Width</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="width" value="600" /></td>
					</tr>
					<tr>
						<td class="tablekey">Height</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="height" value="400" /></td>
					</tr>
					<tr>
						<td class="tablekey">Chart Type</td>
						<td class="infobody"><select name="type">
							<option value="line">Line Chart</option>
							<option value="point">Point Chart</option>
							<option value="clusteredbar">Bar Chart</option>
							<option value="stackedbar">Stacked Bar Chart</option>
							<option value="area">Area Chart</option>
							<option value="stackedarea">Stacked Area Chart</option>
							<option value="pie">Pie Chart</option>
							<option value="scatter">Scatter Plot</option>
						</select></td>
					</tr>
					<tr>
						<td class="tablekey">Color Scheme</td>
						<td class="infobody"><select name="colorscheme">
							<option value="default">Default</option>
							<option value="reds">Red Tones</option>
						</select></td>
					</tr>
					<tr>
						<td class="tablekey">Number of Classes</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="classes" value="4" /></td>
					</tr>
					<tr>
						<td class="tablekey">Number of Series</td>
						<td class="infobody"><input type="text" size="10"
							maxlength="10" name="series" value="3" /></td>
					</tr>
					<tr>
						<td class="tablekey">Title</td>
						<td class="infobody"><input type="text" size="51"
							name="title" value="Test chart" /></td>
					</tr>
					<tr>
						<td class="tablekey">X Title</td>
						<td class="infobody"><input type="text" size="51"
							name="xtitle" value="X Values" /></td>
					</tr>
					<tr>
						<td class="tablekey">Y Title</td>
						<td class="infobody"><input type="text" size="51"
							name="ytitle" value="Y Values" /></td>
					</tr>
					<tr>
						<td class="tablekey">Class Labels</td>
						<td class="infobody"><input type="text" size="51"
							name="classlabel" value="Year 1 ; Year 2 ; Year 3 ; Year 4" /></td>
					</tr>
					<tr>
						<td class="tablekey">Legend Labels</td>
						<td class="infobody"><input type="text" size="51"
							name="legendlabel" value="Series 1 ; Series 2 ; Series 3" /></td>
					</tr>
					<tr>
						<td class="tablekey">Data points</td>
						<td class="infobody"><input type="text" size="51" name="data"
							value="100 ; 200 ; 300 ; 250 ; 150 ; 100 ; 250 ; 280; 50 ; 150 ; 300; 50" /></td>
					</tr>
				</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="8"></td>
					</tr>
				</table>
				<table class="tablemain">
					<tr>
						<td class="tablebody">
						<center><input type="hidden" name="url" value="chart" />
						<input type="submit" name="button" value="Generate" /> <input
							type="reset" name="reset" value="Reset" /></center>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td width="33%">&nbsp;</td>
	</tr>
</table>
</form>

<!-- <form name="geotiffForm" method="get" action="geotiff.jsp">
    <table>
        <tr>
            <td width="33%">&nbsp;</td>
            <td width="33%">
                <table width="600">
                    <tr>
                        <td>
                            <table class="tablemain">
                                <tr>
                                    <td class="tableheader" colspan="2">
                                        Generate GeoTIFF
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tablekey">
                                        URL</td><td class="infobody"><input type="text" size="51" name="url" value="http://localhost:8080/nail.map/DIGMAP_logo.png" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tablekey">
                                        Width</td><td class="infobody"><input type="text" size="10" maxlength="10" name="width" value="160" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tablekey">
                                        Height</td><td class="infobody"><input type="text" size="10" maxlength="10" name="height" value="100" />
                                    </td>
                                </tr>

                                <tr>
                                    <td class="tablekey">
                                        Lat/Long Left-Bottom</td><td class="infobody"><input type="text" size="10" maxlength="10" name="lat1" value="0.0" />&nbsp;<input type="text" size="10" maxlength="10" name="lon1" value="0.0" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tablekey">
                                        Lat/Long Right-Top</td><td class="infobody"><input type="text" size="10" maxlength="10" name="lat2" value="0.0" />&nbsp;<input type="text" size="10" maxlength="10" name="lon2" value="0.0" />
                                    </td>
                                </tr>
                            </table>
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td height="8"></td>
                                </tr>
                            </table>
                            
                            <table class="tablemain">
                                <tr>
                                    <td class="tablebody">
                                        <center>
  <div style="width:100%; height:300" id="map"></div>
  <script defer="defer" type="text/javascript">
    var map = new OpenLayers.Map('map');
    var bounds = new OpenLayers.Bounds(-198.00019226074218,-98.68115158081055,198.00000915527343,92.30418319702149);
    var wcs = new OpenLayers.Layer.WMS( "DIGMAP NATURAL WORLD", "http://digmap1.ist.utl.pt:8080/geoserver/wms", { layers: 'digmap:WorldNatural', tiled: 'true' } );
    map.addLayer(wcs);
    var wms = new OpenLayers.Layer.WMS( "DIGMAP WMS", "http://digmap1.ist.utl.pt:8080/geoserver/wms", { layers: 'digmap:world', format: 'image/png', transparent: 'true', tiled: 'true' } );
    map.addLayer(wms);
    map.zoomToExtent(bounds);
    map.addControl(new OpenLayers.Control.MouseDefaults());
    map.addControl(new OpenLayers.Control.Scale($('scale')));
    map.addControl(new OpenLayers.Control.MousePosition({element: $('position')}));
    map.events.register("click", map, function(e) { 
                var lonlat = map.getLonLatFromViewPortPx(e.xy);
                document.geotiffForm.lat1.value = lonlat.lat;
                document.geotiffForm.lat2.value = lonlat.lat;
                document.geotiffForm.lon1.value = lonlat.lon;
                document.geotiffForm.lon2.value = lonlat.lon;
                // This gets the map extent instead of the click-point
                var bnds = map.getExtent().toString();
                document.geotiffForm.lon1.value = bnds.substring(13,bnds.indexOf(","));
                document.geotiffForm.lat1.value = bnds.substring(bnds.indexOf(",")+1,bnds.indexOf(")"));
                document.geotiffForm.lon2.value = bnds.substring(bnds.indexOf("top=(")+5,bnds.lastIndexOf(","));
                document.geotiffForm.lat2.value = bnds.substring(bnds.lastIndexOf(",")+1,bnds.lastIndexOf(")"));
    });
  </script>
                                        </center>
                                    </td>
                                </tr>
                            </table>
                            
                            <table class="tablemain">
                                <tr>
                                    <td class="tablebody">
                                        <center>
                                            <input type="submit" name="button" value="Generate"/>
                                            <input type="reset" name="reset" value="Reset"/>
                                        </center>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
            <td width="33%">&nbsp;</td>
        </tr>
    </table>
</form> -->
</body>
</html>
<% } %>