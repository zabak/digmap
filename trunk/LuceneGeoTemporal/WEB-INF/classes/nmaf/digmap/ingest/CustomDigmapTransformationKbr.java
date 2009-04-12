package nmaf.digmap.ingest;

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

public class CustomDigmapTransformationKbr extends CustomDigmapTransformation{

	private static String numberPattern = "\\s*\\d+\\.?\\d*\\s*"; 
	private static String coordPattern = numberPattern+"[°º]"+numberPattern+"'("+numberPattern+"\")?";
//	static Pattern coordinatesPattern=Pattern.compile(coordPattern);
//	static Pattern coordinatesPattern=Pattern.compile("([EW]"+coordPattern+")-([EW]"+coordPattern+")");
	static Pattern coordinatesPattern=Pattern.compile("\\(\\s*([EW]"+coordPattern+")-([EW]"+coordPattern+")/([NS]"+coordPattern+")-([NS]"+coordPattern+")\\)");
	static Pattern pointPattern=Pattern.compile("\\(\\s*([EW]"+coordPattern+")/([NS]"+coordPattern+")\\)");

	static Pattern urlImagePattern=Pattern.compile("\\s*(http://[^/]+/)[^/]+(/[^/]+/)([^\\.]+\\.)imgf\\s*");
	
	
	public Document costumizeTransformation(Record rec, Document digmapDom) {
		String f206a=rec.getSingleFieldValue(206, 'a');
		if(f206a!=null) {
			Matcher m=coordinatesPattern.matcher(f206a);
			if(m.find()) {
				GeographicPoint top=new GeographicPoint(m.group(5), m.group(3));
				GeographicPoint bottom=new GeographicPoint(m.group(7), m.group(1));
				Coordinates coord=new Coordinates(top, bottom);
				
				Element el=digmapDom.getRootElement().addElement(new QName("spatial",new Namespace("dcterms","http://purl.org/dc/terms/")));
				el.addAttribute(new QName("type",new Namespace("xsi","http://www.w3.org/2001/XMLSchema-instance")), "dcterms:Box");
				el.addText(coord.toDcmiBox());
			}else {
				m=pointPattern.matcher(f206a);
				if(m.find()) {
					GeographicPoint top=new GeographicPoint(m.group(1), m.group(3));
					Element el=digmapDom.getRootElement().addElement(new QName("spatial",new Namespace("dcterms","http://purl.org/dc/terms/")));
					el.addAttribute(new QName("type",new Namespace("xsi","http://www.w3.org/2001/XMLSchema-instance")), "dcterms:Point");
					el.addText(top.toDcmiPoint());
				}
//				else
//					System.out.println("NOT FOUND "+ f206a);
			}
		}

		Namespace telns=new Namespace("tel","http://krait.kb.nl/coop/tel/handbook/telterms.html");
		Namespace xsins=new Namespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
		
		String f856u=rec.getSingleFieldValue(856, 'u');
		if(f856u!=null) {
			Matcher m=urlImagePattern.matcher(f856u);
			if(m.matches()) {
				Element el=digmapDom.getRootElement().addElement(new QName("thumbnail", telns));
				el.addAttribute(new QName("type", xsins), "dcterms:URI");
				el.addText(m.group(1)+"xlimages"+m.group(2)+"thumbnails/"+m.group(3)+"jpg");
			}
		}
		
		return digmapDom;
	}
	
	
	
	public static void main(String[] args) {

		Record r=MarcUtil.createTestBibliographicRecord();
		Field f=r.addField(206);
//		f.addSubfield('a',"[1:115 000], lieues d'une heure de chemin, 2 = [11,3 cm], [et al.] (E 4°11-E 6°08'/N 51°48'-N 50°34')");
//		f.addSubfield('a',"[1:115 000], lieues d'une heure de chemin, 2 = [11,3 cm], [et al.] (E 3°04\'1\"-E 3°52\'1\"/N 51°28\'-N 51°10\'2\")");
		f.addSubfield('a',"[1:115 000], lieues d'une heure de chemin, 2 = [11,3 cm], [et al.] (E 5°06\'/N 50°50\')");

		f=r.addField(856);
		f.addSubfield('u',"http://mara.kbr.be/kbrImage/CM/1056315_4.imgf");
		System.out.println(
		new CustomDigmapTransformationKbr().costumizeTransformation(r, null).asXML()
		);
		
	}
}
