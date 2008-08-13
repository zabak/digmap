<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="eu.digmap.thumbnails.*"%>
<%@ page import="javazoom.upload.*"%>
<%
	String url = request.getParameter("url");
	int width  = 0, height = 0;
	float lat  = Float.MAX_VALUE;
	float lon  = Float.MAX_VALUE;
	float lat1 = Float.MAX_VALUE;
	float lon1 = Float.MAX_VALUE;
	float lat2 = Float.MAX_VALUE;
	float lon2 = Float.MAX_VALUE;
	float lat3 = Float.MAX_VALUE;
	float lon3 = Float.MAX_VALUE;
	float lat4 = Float.MAX_VALUE;
	float lon4 = Float.MAX_VALUE;
	float lat5 = Float.MAX_VALUE;
	float lon5 = Float.MAX_VALUE;
	float lat6 = Float.MAX_VALUE;
	float lon6 = Float.MAX_VALUE;
	float lat7 = Float.MAX_VALUE;
	float lon7 = Float.MAX_VALUE;
	float cradius = Float.MIN_VALUE;
	float rotation = Float.MAX_VALUE;
	float transparencyWidth1 = 101;
	float transparencyWidth2 = 101;
	float transparencyHeight1 = 101;
	float transparencyHeight2 = 101;
	byte transparency = (byte) 255;
	int radius = 30;
	if (url != null && url.equals("heatmap")) {
		try {
			width = (new Integer(request.getParameter("width")))
					.intValue();
		} catch (Exception e) {
		}
		try {
			height = (new Integer(request.getParameter("height")))
					.intValue();
		} catch (Exception e) {
		}
		String data = request.getParameter("data");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		new HeatMapThumbnailMaker(url, width, height, (byte) 125, data)
				.make(false, os);
		os.close();
	} else if (url != null
			&& (url.equals("simplemap") || url.equals("reliefmap")
					|| url.equals("oldmap")
					|| url.equals("satellitemap") || url
					.equals("lightmap"))) {
		try {
			radius = (new Integer(request.getParameter("radius")))
					.intValue() * 2;
		} catch (Exception e) {
		}
		try {
			cradius = (new Float(request.getParameter("cradius")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat = (new Float(request.getParameter("lat"))).floatValue();
		} catch (Exception e) {
		}
		try {
			lon = (new Float(request.getParameter("lon"))).floatValue();
		} catch (Exception e) {
		}
		try {
			lat1 = (new Float(request.getParameter("lat1")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon1 = (new Float(request.getParameter("lon1")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat2 = (new Float(request.getParameter("lat2")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon2 = (new Float(request.getParameter("lon2")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat3 = (new Float(request.getParameter("lat3")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon3 = (new Float(request.getParameter("lon3")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat4 = (new Float(request.getParameter("lat4")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon4 = (new Float(request.getParameter("lon4")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat5 = (new Float(request.getParameter("lat5")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon5 = (new Float(request.getParameter("lon5")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat6 = (new Float(request.getParameter("lat6")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon6 = (new Float(request.getParameter("lon6")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lat7 = (new Float(request.getParameter("lat7")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			lon7 = (new Float(request.getParameter("lon7")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			width = (new Integer(request.getParameter("width")))
					.intValue();
		} catch (Exception e) {
		}
		try {
			height = (new Integer(request.getParameter("height")))
					.intValue();
		} catch (Exception e) {
		}
		try {
			if (request.getParameter("transparency").equals("true"))
				transparency = 125;
			else
				transparency = (new Byte(request
						.getParameter("transparency"))).byteValue();
		} catch (Exception e) {
		}
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		new MapThumbnailMaker(url, width, height, transparency, lat,
				lon, lat1, lon1, lat2, lon2, lat3, lon3, cradius, lat4,
				lon4, lat5, lon5, lat6, lon6, lat7, lon7, radius).make(
				false, os);
		os.close();
	} else if (url != null && url.length() != 0) {
		try {
			width = (new Integer(request.getParameter("width")))
					.intValue();
		} catch (Exception e) {
		}
		try {
			height = (new Integer(request.getParameter("height")))
					.intValue();
		} catch (Exception e) {
		}
		try {
			rotation = (new Float(request.getParameter("rotation")))
					.floatValue();
		} catch (Exception e) {
		}
		try {
			if (request.getParameter("transparency").equals("true"))
				transparency = 125;
			else
				transparency = (new Byte(request
						.getParameter("transparency"))).byteValue();
		} catch (Exception e) {
		}
		if (url.indexOf("://") == -1)
			url = "http://" + url;
		String replaceurl = request.getParameter("replaceurl");
		if (replaceurl == null || replaceurl.length() == 0) {
			response.setContentType("image/png");
			OutputStream os = response.getOutputStream();
			ThumbnailMakerFactory.getThumbnailMaker(url, width, height,
					transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).make(true, os);
			os.close();
		} else {
			if (replaceurl.indexOf("://") == -1)
				replaceurl = "http://" + replaceurl;
			response.setContentType("image/png");
			OutputStream os = response.getOutputStream();
			ThumbnailMakerFactory.getThumbnailMaker(url, width, height,
					transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).makeAndUpdate(os,
					replaceurl);
			os.close();
		}
	} else if (MultipartFormDataRequest.isMultipartFormData(request)) {
			MultipartFormDataRequest mrequest = new MultipartFormDataRequest(
					request);
			try {
				width = (new Integer(mrequest.getParameter("width")))
						.intValue();
			} catch (Exception e) {
			}
			try {
				height = (new Integer(mrequest.getParameter("height")))
						.intValue();
			} catch (Exception e) {
			}
			try {
				rotation = (new Float(request.getParameter("rotation")))
						.floatValue();
			} catch (Exception e) {
			}
			try {
				if (mrequest.getParameter("transparency")
						.equals("true"))
					transparency = 125;
				else
					transparency = (new Byte(mrequest
							.getParameter("transparency"))).byteValue();
			} catch (Exception e) {
			}
			Hashtable files = mrequest.getFiles();
			if ((files != null) && (!files.isEmpty())) {
				UploadFile file = (UploadFile) files.get("file");
				if (file != null) {
					String replaceurl = mrequest
							.getParameter("replaceurl");
					if (replaceurl == null || replaceurl.length() == 0) {
						response.setContentType("image/png");
						OutputStream os = response.getOutputStream();
						new ImageThumbnailMaker(file.getFileName(),
								file.getInpuStream(), width, height,
								transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).make(true, os);
						os.close();
					} else {
						if (replaceurl.indexOf("://") == -1)
							replaceurl = "http://" + replaceurl;
						response.setContentType("image/png");
						OutputStream os = response.getOutputStream();
						new ImageThumbnailMaker(file.getFileName(),
								file.getInpuStream(), width, height,
								transparency, transparencyWidth1, transparencyWidth2, transparencyHeight1, transparencyHeight2, rotation).makeAndUpdate(
								os, replaceurl);
						os.close();
					}
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

<form name="thumbForm" method="get" action="index.jsp">
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

<form name="thumbImgForm" method="post" enctype="multipart/form-data" action="index.jsp">
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

<form name="thumbMapForm" method="get" action="index.jsp">
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

<form name="thumbMapForm2" method="get" action="index.jsp">
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

<form name="thumbMapForm3" method="get" action="index.jsp">
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

<form name="thumbHeatMapForm" method="get" action="index.jsp">
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

<form name="thumbUpdateForm" method="get" action="index.jsp">
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

<form name="thumbUpdateImgForm" method="post" enctype="multipart/form-data" action="index.jsp">
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