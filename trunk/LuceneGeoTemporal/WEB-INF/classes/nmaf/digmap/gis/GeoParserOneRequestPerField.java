package nmaf.digmap.gis;

import nmaf.util.structure.Tuple;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class GeoParserOneRequestPerField {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(GeoParserOneRequestPerField.class);
	
	private static final boolean TESTING=false;
	
	public static String geoParserBaseUrl="http://digmap1.ist.utl.pt:8080/geoparser/geoparser-dispatch";

	public static void addPlaceNameTerms(Document dmDom, boolean getGazeteerIds) {
		ArrayList<Tuple<String, ArrayList<String>>> placeTerms=new ArrayList<Tuple<String,ArrayList<String>>>();
		HashSet<String> placeTermsSet=new HashSet<String>();
		for(Iterator<Element> it=dmDom.getRootElement().elementIterator(); it.hasNext(); ) {
			Element el=it.next();
			if(!el.getName().equals("identifier") && !el.getName().equals("recordId") && !el.getName().equals("creator") && !el.getName().equals("contributor") && !el.getName().equals("format") && !el.getName().equals("type") && !el.getName().equals("publisher")) {
				String fieldText = el.getTextTrim();
				Collection<Tuple<String, ArrayList<String>>> fieldPlaceTerms=readGeoParserResult(fieldText, getGazeteerIds);
				for(Tuple<String, ArrayList<String>> plc: fieldPlaceTerms) {
					if(!placeTermsSet.contains(plc.getV1())) {
						placeTermsSet.add(plc.getV1());
						placeTerms.add(plc);
					}
				}
			}
		}

		Namespace digmapns=new Namespace("","http://www.digmap.eu/schemas/resource/");
		for(Tuple<String, ArrayList<String>> plc:placeTerms) {
			Element plcEl=dmDom.getRootElement().addElement(new QName("place",digmapns));
			plcEl.addAttribute("name", plc.getV1());
			for(String id: plc.getV2()) {
				Element idEl=plcEl.addElement(new QName("gazeteer-id",digmapns));
				idEl.setText(id);
			}
		}
	}

    public static Collection<Tuple<String, ArrayList<String>>> readGeoParserResult(String recordContent,boolean getGazeteerIds){
    	if(TESTING) {
        	HashSet<Tuple<String, ArrayList<String>>> ret=new HashSet<Tuple<String, ArrayList<String>>>();
    		ret.add(new Tuple<String, ArrayList<String>>("teste", new ArrayList<String>()));
    		return ret;
    	}

    	int retries=0;
    	while(retries<3) {
	        try{
//	        	if (retries>0)
//	        		Thread.sleep(Math.min(retries * (60000), (10 * 60000)));
		    	
		    	ArrayList<Tuple<String, ArrayList<String>>> ret=new ArrayList<Tuple<String, ArrayList<String>>>();
		    	String reqPre=	"<?xml version=\"1.0\"?>\r\n" + 
		    	(getGazeteerIds ? "<GetFeature" : "<GetParsing") +
		    	" xmlns=\"http://www.opengis.net/gp\" xmlns:wfs=\"http://www.opengis.net/wfs\"" + 
		    	" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"" + 
		    	" xsi:schemaLocation=\"http://www.opengis.net/gp ../gp/GetFeatureRequest.xsd http://www.opengis.net/wfs ../wfs/GetFeatureRequest.xsd\"\r\n" + 
		    	" wfs:outputFormat=\"GML2\">" + 
		    	"<wfs:Query wfs:TypeName=\"PlaceName\" />" + 
		//    	"  <wfs:Query wfs:TypeName=\"DateTime\" />\r\n" + 
		//    	"  <wfs:Query wfs:TypeName=\"People\" />\r\n" + 
		//    	"  <wfs:Query wfs:TypeName=\"Organizations\" />\r\n" + 
		    	"<Resource mine=\"text/plain\">" + 
		    	"<Contents></Contents>" + 
		    	"</Resource>" + 
		    	(getGazeteerIds ? "</GetFeature>" : "</GetParsing>");
	        	Document doc=DocumentHelper.parseText(reqPre);
	        	doc.getRootElement().element("Resource").element("Contents").setText(recordContent);
//	        	doc.getRootElement().element("Resource").element("Contents").addCDATA(recordContent);
	//        	HttpClient cli=new HttpClient();
	////        	PostMethod post=new PostMethod("http://digmap2.ist.utl.pt:8080/geoparser/geoparser-dispatch");
	//        	GetMethod post=new GetMethod("http://digmap2.ist.utl.pt:8080/geoparser/geoparser-dispatch?request="+URLEncoder.encode(reqPre + recordContent + reqSuf));
	////        	post.addParameter("request", reqPre + recordContent + reqSuf);
	//        	cli.executeMethod(post);
	        	
	//        	if(log.isDebugEnabled())
	//        		log.debug(doc.asXML());
	        	
//	        	System.out.println(doc.asXML());
	        	
	        	URL url = new URL(geoParserBaseUrl+"?request="+URLEncoder.encode(doc.asXML(),"ISO8859-1"));
	//            URL url = new URL("http://digmap2.ist.utl.pt:8080/geoparser/geoparser-dispatch?request="+URLEncoder.encode(doc.asXML(),"UTF-8"));
	            InputStreamReader reader = new InputStreamReader(url.openStream(),"UTF-8");
	//            InputStreamReader reader = new InputStreamReader(post.getResponseBodyAsStream(),"UTF-8");
	            BufferedReader buffered = new BufferedReader(reader);
	            StringBuffer sb = new StringBuffer();
	            String line;
	            while ((line=buffered.readLine()) != null){
	                sb.append(line);
	            }
	            
	            Document d=DocumentHelper.parseText(sb.toString());
	            
	            HashSet<String> places=new HashSet<String>();
	            
	            for(Iterator<Element> it=d.getRootElement().element("EntryCollection").elementIterator("PlaceName"); it.hasNext(); ) {
	            	Element plcEl=it.next();
	            	String val=plcEl.elementTextTrim("TermName");
	            	if(!val.equals("") && !places.contains(val)) {
	            		places.add(val);
	            		String entryID=plcEl.attributeValue("entryID");
	            		Tuple<String, ArrayList<String>> plc=new Tuple<String, ArrayList<String>>(val, new ArrayList<String>());
	                    for(Iterator<Element> it2=d.getRootElement().element("EntryCollection").elementIterator("GazetteerEntry"); it2.hasNext(); ) {
	                    	Element idEl=it2.next();
	                    	if(idEl.attributeValue("entryID").equals(entryID))
	                    		plc.getV2().add(idEl.attributeValue("id"));
	                    }
	            		ret.add(plc);
	            	}
	            }
	            
	            return ret;
	        } catch(Exception e){
	//        	e.printStackTrace();
	            log.debug("Erro ao pesquisar a lista de termos para o registo " + recordContent + "! " + e.getMessage(), e);
	            System.out.println("Erro ao pesquisar a lista de termos para o registo " + recordContent + "! " + e.getMessage());
	            retries++;
	        }
	    }
        System.out.println("Too many retries. Giving up.");
        return new HashSet<Tuple<String, ArrayList<String>>>();
    }
    
    public static void main(String[] args) {
//    	System.out.println(URLDecoder.decode("http://digmap1.ist.utl.pt:8080/geoparser/geoparser-dispatch?request=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3CGetFeature+xmlns%3D%22http%3A%2F%2Fwww.opengis.net%2Fgp%22+xmlns%3Awfs%3D%22http%3A%2F%2Fwww.opengis.net%2Fwfs%22+xmlns%3Axsi%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2F10%2FXMLSchema-instance%22+xsi%3AschemaLocation%3D%22http%3A%2F%2Fwww.opengis.net%2Fgp+..%2Fgp%2FGetFeatureRequest.xsd+http%3A%2F%2Fwww.opengis.net%2Fwfs+..%2Fwfs%2FGetFeatureRequest.xsd%22+wfs%3AoutputFormat%3D%22GML2%22%3E%3Cwfs%3AQuery+wfs%3ATypeName%3D%22PlaceName%22%2F%3E%3CResource+mine%3D%22text%2Fplain%22%3E%3CContents%3E.+Plano+de+la+Bah%EDa+de+San+Juan+de+Luz.+San+Juan+de+Luz+%28Francia%29+%28Bah%EDa%29.+Cartas+n%E1uticas%2C+-Francia.+%5BMaterial+cartogr%E1fico%5D.+levantado+en+1876+por+los+ingenieros+hidr%F3graficos+de+la+Marina+francesa+%3B+J.+Galv%E1n+lo+grab%F3%2C+P.+Bacot+grab%F3+la+letra.+Escala+gr%E1fica+de+longitudes.+Constan+coordenadas+del+Faro+de+Socoa.+Longitud+del+meridiano+de+San+Fernando.+Se%F1alizaci%F3n+mar%EDtima+coloreada+a+mano.+Grabado+en+cobre.+Indicaci%F3n+gr%E1fica+del+Norte+Magn%E9tico.+Escala+1%3A5.000.+1880.+Edition%3A+%5B2%AA+ed.%5D%3C%2FContents%3E%3C%2FResource%3E%3C%2FGetFeature%3E"));
    	
    	
		System.out.println(readGeoParserResult("Bruno Martins works at IST and he lives in a country called Portugal, in Serra. ", true));
//		System.out.println(readGeoParserResult("Plano de la Bahía de San Juan de Luz. San Juan de Luz (Francia) . ", true));
//		System.out.println(readGeoParserResult("Bruno Martins works at IST and he lives in a country called Portugal, in Serra. ", false));
	}
    
//    class Place{
//    	String name;
//    	ArrayList<String> ids=new ArrayList<String>();
//    	
//    	public Place(String name) {
//    		this.name = name;
//		}
//    }
    
}
