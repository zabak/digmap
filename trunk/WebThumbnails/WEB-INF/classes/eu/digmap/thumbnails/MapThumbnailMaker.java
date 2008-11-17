	// TODO: FIX

package eu.digmap.thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapThumbnailMaker extends AbstractThumbnailMaker {
	private MapThumbnailParams params;
	
	public MapThumbnailParams getParams() {
		return params;
	}

	public void setParams(MapThumbnailParams params) {
		super.setParams(params);
		this.params = params;
	}

	@Override
	protected BufferedImage getImage() throws IOException {
		Map<String, String> props = new HashMap<String, String>();
		props.put("mapname", params.uri);
		props.put("bgcolor", params.background);
		props.put("fgcolor", "black");
		props.put("title", "");
		props.put("date", "");
		props.put("width", "1024");
		props.put("height", "768");
		props.put("default_node_size", "6");
		props.put("default_line_size", "1");
		props.put("default_node_color", "red");
		props.put("default_line_color", "red");
		props.put("default_font_size value", "");
		props.put("refresh_interval", "");
		props.put("hide_key", "false");
		props.put("top_lat", "" + params.top_lat);
		props.put("top_lon", "" + params.top_lon);
		props.put("bot_lat", "" + params.bot_lat);
		props.put("bot_lon", "" + params.bot_lon);
		String auxs = "";
		int sz = Math.max(1, params.radius / 2);
		if (params.lat1 != Float.MAX_VALUE && params.lon1 != Float.MAX_VALUE && params.lat2 != Float.MAX_VALUE && params.lon2 != Float.MAX_VALUE) {
			auxs += "\nn	1	" + params.lat1 + "	" + params.lon1 + "	sz:" + params.radius + "	cl:red;\n" + "n	2	" + params.lat2 + "	" + params.lon1 + "	sz:"
					+ params.radius + "	cl:red;\n" + "n	3	" + params.lat2 + "	" + params.lon2 + "	sz:" + params.radius + "	cl:red;\n" + "n	4	"
					+ params.lat1 + "	" + params.lon2 + "	sz:" + params.radius + "	cl:red;\n" + "l	1	2	sz:" + sz + "	cl:red;\n"
					+ "l	2	3	sz:" + sz + "	cl:red;\n" + "l	3	4	sz:" + sz + "	cl:red;\n" + "l	4	1	sz:" + sz + "	cl:red;";

		}
		if (params.lat != Float.MAX_VALUE && params.lon != Float.MAX_VALUE) {
			auxs += "\nn	5	" + params.lat + "	" + params.lon + "	sz:" + params.radius + "	cl:red;";
		}
		if (params.lat3 != Float.MAX_VALUE && params.lon3 != Float.MAX_VALUE) {
			auxs += "\nn	6	" + params.lat3 + "	" + params.lon3 + "	sz:" + params.radius + "	cl:green;\n" + "l	6	6	sz:" + (sz / 2)
					+ "	cl:green	rd:" + params.cradius + ";";
		}
		if (params.lat4 != Float.MAX_VALUE && params.lon4 != Float.MAX_VALUE && params.lat5 != Float.MAX_VALUE && params.lon5 != Float.MAX_VALUE) {
			auxs += "\nn	7	" + params.lat4 + "	" + params.lon4 + "	sz:" + params.radius + "	cl:red;\n" + "n	8	" + params.lat5 + "	" + params.lon4 + "	sz:"
					+ params.radius + "	cl:red;\n" + "n	9	" + params.lat5 + "	" + params.lon5 + "	sz:" + params.radius + "	cl:red;\n" + "n	10	"
					+ params.lat4 + "	" + params.lon5 + "	sz:" + params.radius + "	cl:red;\n" + "l	7	8	sz:" + sz + "	cl:red;\n"
					+ "l	8	9	sz:" + sz + "	cl:red;\n" + "l	9	10	sz:" + sz + "	cl:red;\n" + "l	10	7	sz:" + sz
					+ "	cl:red;";
		}
		props.put("data", auxs);
		BufferedImage bimage;
		if (params.lat6 != Float.MAX_VALUE && params.lon6 != Float.MAX_VALUE && params.lat7 != Float.MAX_VALUE && params.lon7 != Float.MAX_VALUE) {
			bimage = MapGenerator.getImage(props, params.lat6, params.lon6, params.lat7, params.lon7);
		} else {
			bimage = MapGenerator.getImage(props);
		}
		return bimage;
	}

}