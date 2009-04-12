package nmaf.digmap.ingest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nmaf.digmap.gis.Coordinates;
import nmaf.digmap.gis.GeographicPoint;
import nmaf.marc.Field;
import nmaf.marc.Record;
import nmaf.marc.util.MarcUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class CustomDigmapTransformationBnpFixUrls extends CustomDigmapTransformation{
	Pattern recIdPattern=Pattern.compile("@!(\\d+)@!");
	
	public Document costumizeTransformation(Record rec, Document digmapDom) {
		
		Element recEl=digmapDom.getRootElement();
		for(Element el: (List<Element>) recEl.elements("recordId")) {
			Matcher m=recIdPattern.matcher(el.getTextTrim());
			if(m.find()) {
				el.setText("http://catalogo.bn.pt/ipac20/ipac.jsp?profile=bn&source=~!bnp&view=subscriptionsummary&uri=full=3100024~!" + m.group(1) + "~!0&ri=1&aspect=subtab13&menu=search&ipp=20&spp=20&staffonly=&term=teste&index=.GW&uindex=&aspect=subtab13&menu=search&ri=1");
				break;
			}
		}
		return digmapDom;
	}
}
