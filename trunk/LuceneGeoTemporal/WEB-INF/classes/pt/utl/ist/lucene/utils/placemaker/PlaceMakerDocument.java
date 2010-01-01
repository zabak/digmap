package pt.utl.ist.lucene.utils.placemaker;

import org.dom4j.*;
import org.apache.log4j.Logger;
import pt.utl.ist.lucene.utils.Dom4jUtil;
import pt.utl.ist.lucene.forms.GeoPoint;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 1/Jan/2010
 * @time 19:25:39
 * @email machadofisher@gmail.com
 */
public class PlaceMakerDocument
{

    private static final Logger logger = Logger.getLogger(PlaceMakerDocument.class);

    static Map<String,String> namespaces = new HashMap<String,String>();
    static
    {
        namespaces.put("d","http://wherein.yahooapis.com/v1/schema");
        namespaces.put("gml","http://www.opengis.net/gml");

    }
    Document dom;

    String docId;

    String administrativeWoeid;
    String administrativeType;
    GeoPoint administrativeCentroide;

    String geographicWoeid;
    String geographicType;
    GeoPoint geographicCentroide;


    GeoPoint boundingBoxPoint1;
    GeoPoint boundingBoxPoint2;

    List<PlaceDetails> placeDetails = new ArrayList<PlaceDetails>();

    public PlaceMakerDocument(String xml) throws DocumentException
    {
        xml.replace("<doc ","<doc xmlns:gml=\"gml\" ");
        dom = Dom4jUtil.parse(xml);

        XPath administrativeWoeidXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:administrativeScope/d:woeId");
        administrativeWoeidXPath.setNamespaceURIs(namespaces);
        XPath administrativeTypeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:administrativeScope/d:type");
        administrativeTypeXPath.setNamespaceURIs(namespaces);
        XPath administrativeNameXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:administrativeScope/d:name");
        administrativeNameXPath.setNamespaceURIs(namespaces);
        XPath administrativeCentroideLatitudeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:administrativeScope/d:centroid/d:latitude");
        administrativeCentroideLatitudeXPath.setNamespaceURIs(namespaces);
        XPath administrativeCentroideLongitudeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:administrativeScope/d:centroid/d:longitude");
        administrativeCentroideLongitudeXPath.setNamespaceURIs(namespaces);


        XPath geographicWoeidXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:geographicScope/d:woeId");
        geographicWoeidXPath.setNamespaceURIs(namespaces);
        XPath geographicTypeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:geographicScope/d:type");
        geographicTypeXPath.setNamespaceURIs(namespaces);
        XPath geographicNameXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:geographicScope/d:name");
        geographicNameXPath.setNamespaceURIs(namespaces);
        XPath geographicCentroideLongitudeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:geographicScope/d:centroid/d:longitude");
        geographicCentroideLongitudeXPath.setNamespaceURIs(namespaces);
        XPath geographicCentroideLatitudeXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:geographicScope/d:centroid/d:latitude");
        geographicCentroideLatitudeXPath.setNamespaceURIs(namespaces);


        XPath boundingBoxPointX0XPath = dom.createXPath("/doc/d:contentlocation/d:document/gml:Box/gml:coord[1]/gml:X");
        boundingBoxPointX0XPath.setNamespaceURIs(namespaces);
        XPath boundingBoxPointY0XPath = dom.createXPath("/doc/d:contentlocation/d:document/gml:Box/gml:coord[1]/gml:Y");
        boundingBoxPointY0XPath.setNamespaceURIs(namespaces);
        XPath boundingBoxPointX1XPath = dom.createXPath("/doc/d:contentlocation/d:document/gml:Box/gml:coord[2]/gml:X");
        boundingBoxPointX1XPath.setNamespaceURIs(namespaces);
        XPath boundingBoxPointY1XPath = dom.createXPath("/doc/d:contentlocation/d:document/gml:Box/gml:coord[2]/gml:Y");
        boundingBoxPointY1XPath.setNamespaceURIs(namespaces);

        XPath docIdXPath = dom.createXPath("//doc/@id");
        docIdXPath.setNamespaceURIs(namespaces);
        docId = ((Attribute)docIdXPath.selectSingleNode(dom)).getValue().trim();

        administrativeWoeid = administrativeWoeidXPath.selectSingleNode(dom).getText().trim();
        administrativeType = administrativeTypeXPath.selectSingleNode(dom).getText().trim();
        administrativeCentroide = new GeoPoint(Double.parseDouble(administrativeCentroideLatitudeXPath.selectSingleNode(dom).getText()),Double.parseDouble(administrativeCentroideLongitudeXPath.selectSingleNode(dom).getText()));

        geographicWoeid = geographicWoeidXPath.selectSingleNode(dom).getText().trim();
        geographicType = geographicTypeXPath.selectSingleNode(dom).getText().trim();
        geographicCentroide = new GeoPoint(Double.parseDouble(geographicCentroideLatitudeXPath.selectSingleNode(dom).getText()),Double.parseDouble(geographicCentroideLongitudeXPath.selectSingleNode(dom).getText()));

        boundingBoxPoint1 = new GeoPoint(Double.parseDouble(boundingBoxPointX0XPath.selectSingleNode(dom).getText()),Double.parseDouble(boundingBoxPointY0XPath.selectSingleNode(dom).getText()));
        boundingBoxPoint2 = new GeoPoint(Double.parseDouble(boundingBoxPointX1XPath.selectSingleNode(dom).getText()),Double.parseDouble(boundingBoxPointY1XPath.selectSingleNode(dom).getText()));


        XPath placeDetailsXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:placeDetails");
        placeDetailsXPath.setNamespaceURIs(namespaces);
        List<Element> placeDetailsElems = placeDetailsXPath.selectNodes(dom);
        if(placeDetailsElems != null)
        {
            for(Element placeDetailsElem: placeDetailsElems)
            {
                XPath placeDetailsWoeidXPath = dom.createXPath("./d:place/d:woeId");
                placeDetailsWoeidXPath.setNamespaceURIs(namespaces);
                XPath placeDetailsTypeXPath = dom.createXPath("./d:place/d:type");
                placeDetailsTypeXPath.setNamespaceURIs(namespaces);
                XPath placeDetailsNameXPath = dom.createXPath("./d:place/d:name");
                placeDetailsNameXPath.setNamespaceURIs(namespaces);

                XPath placeDetailsCentroideLatitudeXPath = dom.createXPath("./d:place/d:centroid/d:latitude");
                placeDetailsCentroideLatitudeXPath.setNamespaceURIs(namespaces);
                XPath placeDetailsCentroideLongitudeXPath = dom.createXPath("./d:place/d:centroid/d:longitude");
                placeDetailsCentroideLongitudeXPath.setNamespaceURIs(namespaces);

                XPath placeDetailsMatchTypeXPath = dom.createXPath("./d:matchType");
                placeDetailsMatchTypeXPath.setNamespaceURIs(namespaces);
                XPath placeDetailsWeightXPath = dom.createXPath("./d:weight");
                placeDetailsWeightXPath.setNamespaceURIs(namespaces);
                XPath placeDetailsConfidenceXPath = dom.createXPath("./d:confidence");
                placeDetailsConfidenceXPath.setNamespaceURIs(namespaces);


                PlaceDetails placeDetails = new PlaceDetails();
                placeDetails.setWoeId(placeDetailsWoeidXPath.selectSingleNode(placeDetailsElem).getText().trim());
                placeDetails.setType(placeDetailsTypeXPath.selectSingleNode(placeDetailsElem).getText().trim());
                placeDetails.setName(placeDetailsNameXPath.selectSingleNode(placeDetailsElem).getText().trim());

                placeDetails.setCentroide(new GeoPoint(Double.parseDouble(placeDetailsCentroideLatitudeXPath.selectSingleNode(placeDetailsElem).getText()),Double.parseDouble(placeDetailsCentroideLongitudeXPath.selectSingleNode(placeDetailsElem).getText())));

                placeDetails.setMatchType(Integer.parseInt(placeDetailsMatchTypeXPath.selectSingleNode(placeDetailsElem).getText().trim()));
                placeDetails.setWeight(Integer.parseInt(placeDetailsWeightXPath.selectSingleNode(placeDetailsElem).getText().trim()));
                placeDetails.setConfidence(Integer.parseInt(placeDetailsConfidenceXPath.selectSingleNode(placeDetailsElem).getText().trim()));

                XPath placeRefsXPath = dom.createXPath("/doc/d:contentlocation/d:document/d:referenceList/d:reference[contains(d:woeIds,'" + placeDetails.getWoeId() + "')]");
                placeRefsXPath.setNamespaceURIs(namespaces);
                List<Element> placeRefsElems = placeRefsXPath.selectNodes(dom);
                if(placeRefsElems != null)
                {
                    this.placeDetails.add(placeDetails);

                    for(Element placeRefElem: placeRefsElems)
                    {
                        XPath placeRefsStartXPath = dom.createXPath("./d:start");
                        placeRefsStartXPath.setNamespaceURIs(namespaces);
                        XPath placeRefsEndXPath = dom.createXPath("./d:end");
                        placeRefsEndXPath.setNamespaceURIs(namespaces);

                        PlaceRef placeRef = new PlaceRef();
                        placeRef.setStartOffset(Integer.parseInt(placeRefsStartXPath.selectSingleNode(placeRefElem).getText().trim()));
                        placeRef.setEndOffset(Integer.parseInt(placeRefsEndXPath.selectSingleNode(placeRefElem).getText().trim()));

                        placeDetails.getRefs().add(placeRef);
                    }
                }
                else
                    logger.error("PlaceDetails without refs at doc " + docId + " for reference: " + placeDetails.getWoeId() + ":" + placeDetails.getName());
            }
        }



    }


    public String getDocId() {
        return docId;
    }

    public String getAdministrativeWoeid() {
        return administrativeWoeid;
    }

    public String getAdministrativeType() {
        return administrativeType;
    }

    public GeoPoint getAdministrativeCentroide() {
        return administrativeCentroide;
    }

    public String getGeographicWoeid() {
        return geographicWoeid;
    }

    public String getGeographicType() {
        return geographicType;
    }

    public GeoPoint getGeographicCentroide() {
        return geographicCentroide;
    }

    public GeoPoint getBoundingBoxPoint1() {
        return boundingBoxPoint1;
    }

    public GeoPoint getBoundingBoxPoint2() {
        return boundingBoxPoint2;
    }

    public List<PlaceDetails> getPlaceDetails() {
        return placeDetails;
    }

    public Document getDom() {
        return dom;
    }

    public static class PlaceDetails
    {
        String type;
        String woeId;
        String name;
        int matchType;
        int weight;
        int confidence;
        GeoPoint centroide;
        List<PlaceRef> refs = new ArrayList<PlaceRef>();


        public PlaceDetails() {
        }


        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWoeId() {
            return woeId;
        }

        public void setWoeId(String woeId) {
            this.woeId = woeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMatchType() {
            return matchType;
        }

        public void setMatchType(int matchType) {
            this.matchType = matchType;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public GeoPoint getCentroide() {
            return centroide;
        }

        public void setCentroide(GeoPoint centroide) {
            this.centroide = centroide;
        }

        public List<PlaceRef> getRefs() {
            return refs;
        }

        public void setRefs(List<PlaceRef> refs) {
            this.refs = refs;
        }
    }
    public static class PlaceRef
    {
        int startOffset;
        int endOffset;


        public PlaceRef() {
        }


        public int getStartOffset() {
            return startOffset;
        }

        public void setStartOffset(int startOffset) {
            this.startOffset = startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public void setEndOffset(int endOffset) {
            this.endOffset = endOffset;
        }
    }



    public static void main(String[] args) throws DocumentException {
        String xml = "<doc id=\"NYT_ENG_20040507.0027\"><contentlocation xmlns:yahoo=\"http://www.yahooapis.com/v1/base.rng\"\n" +
                "    xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
                "    xmlns=\"http://wherein.yahooapis.com/v1/schema\" xml:lang=\"en-EN\">\n" +
                "    <processingTime>0.007809</processingTime>\n" +
                "    <version> build 091119</version>\n" +
                "    <documentLength>1363</documentLength>\n" +
                "    <document>\n" +
                "        <administrativeScope>\n" +
                "            <woeId>12587688</woeId>\n" +
                "            <type>County</type>\n" +
                "            <name><![CDATA[Los Angeles, CA, US]]></name>\n" +
                "            <centroid>\n" +
                "                <latitude>34.2635</latitude>\n" +
                "                <longitude>-118.296</longitude>\n" +
                "            </centroid>\n" +
                "        </administrativeScope>\n" +
                "        <geographicScope>\n" +
                "            <woeId>12587688</woeId>\n" +
                "            <type>County</type>\n" +
                "            <name><![CDATA[Los Angeles, CA, US]]></name>\n" +
                "            <centroid>\n" +
                "                <latitude>34.2635</latitude>\n" +
                "                <longitude>-118.296</longitude>\n" +
                "            </centroid>\n" +
                "        </geographicScope>\n" +
                "        <extents>\n" +
                "            <center>\n" +
                "                <latitude>33.7667</latitude>\n" +
                "                <longitude>-118.192</longitude>\n" +
                "            </center>\n" +
                "            <southWest>\n" +
                "                <latitude>22.3526</latitude>\n" +
                "                <longitude>-118.476</longitude>\n" +
                "            </southWest>\n" +
                "            <northEast>\n" +
                "                <latitude>33.994</latitude>\n" +
                "                <longitude>114.267</longitude>\n" +
                "            </northEast>\n" +
                "        </extents>\n" +
                "        <placeDetails>\n" +
                "            <place>\n" +
                "                <woeId>2441472</woeId>\n" +
                "                <type>Town</type>\n" +
                "                <name><![CDATA[Long Beach, CA, US]]></name>\n" +
                "                <centroid>\n" +
                "                    <latitude>33.7667</latitude>\n" +
                "                    <longitude>-118.192</longitude>\n" +
                "                </centroid>\n" +
                "            </place>\n" +
                "            <matchType>0</matchType>\n" +
                "            <weight>1</weight>\n" +
                "            <confidence>7</confidence>\n" +
                "        </placeDetails>\n" +
                "        <placeDetails>\n" +
                "            <place>\n" +
                "                <woeId>2445713</woeId>\n" +
                "                <type>Town</type>\n" +
                "                <name><![CDATA[Marina del Rey, CA, US]]></name>\n" +
                "                <centroid>\n" +
                "                    <latitude>33.9852</latitude>\n" +
                "                    <longitude>-118.452</longitude>\n" +
                "                </centroid>\n" +
                "            </place>\n" +
                "            <matchType>0</matchType>\n" +
                "            <weight>1</weight>\n" +
                "            <confidence>7</confidence>\n" +
                "        </placeDetails>\n" +
                "        <placeDetails>\n" +
                "            <place>\n" +
                "                <woeId>2488185</woeId>\n" +
                "                <type>Suburb</type>\n" +
                "                <name><![CDATA[San Pedro, Los Angeles, CA, US]]></name>\n" +
                "                <centroid>\n" +
                "                    <latitude>33.7409</latitude>\n" +
                "                    <longitude>-118.298</longitude>\n" +
                "                </centroid>\n" +
                "            </place>\n" +
                "            <matchType>0</matchType>\n" +
                "            <weight>1</weight>\n" +
                "            <confidence>7</confidence>\n" +
                "        </placeDetails>\n" +
                "        <placeDetails>\n" +
                "            <place>\n" +
                "                <woeId>56042320</woeId>\n" +
                "                <type>Town</type>\n" +
                "                <name><![CDATA[Fisherman Village, New Territories, HK]]></name>\n" +
                "                <centroid>\n" +
                "                    <latitude>22.3617</latitude>\n" +
                "                    <longitude>114.257</longitude>\n" +
                "                </centroid>\n" +
                "            </place>\n" +
                "            <matchType>0</matchType>\n" +
                "            <weight>1</weight>\n" +
                "            <confidence>7</confidence>\n" +
                "        </placeDetails>\n" +
                "        <referenceList>\n" +
                "            <reference>\n" +
                "                <woeIds>56042320</woeIds>\n" +
                "                <start>1212</start>\n" +
                "                <end>1231</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Fisherman's Village]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>56042320</woeIds>\n" +
                "                <start>133</start>\n" +
                "                <end>152</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Fisherman's Village]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2445713</woeIds>\n" +
                "                <start>156</start>\n" +
                "                <end>170</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Marina del Rey]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2488185</woeIds>\n" +
                "                <start>243</start>\n" +
                "                <end>252</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[San Pedro]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2441472</woeIds>\n" +
                "                <start>257</start>\n" +
                "                <end>267</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Long Beach]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2445713</woeIds>\n" +
                "                <start>346</start>\n" +
                "                <end>360</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Marina del Rey]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2445713</woeIds>\n" +
                "                <start>35</start>\n" +
                "                <end>49</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Marina del Rey]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2441472</woeIds>\n" +
                "                <start>494</start>\n" +
                "                <end>504</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Long Beach]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "            <reference>\n" +
                "                <woeIds>2445713</woeIds>\n" +
                "                <start>693</start>\n" +
                "                <end>707</end>\n" +
                "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                "                <text><![CDATA[Marina del Rey]]></text>\n" +
                "                <type>plaintext</type>\n" +
                "                <xpath><![CDATA[]]></xpath>\n" +
                "            </reference>\n" +
                "        </referenceList>\n" +
                "        <gml:Box xmlns:gml=\"http://www.opengis.net/gml\">\n" +
                "            <gml:coord>\n" +
                "                <gml:X>-118.944817</gml:X>\n" +
                "                <gml:Y>32.800701</gml:Y>\n" +
                "            </gml:coord>\n" +
                "            <gml:coord>\n" +
                "                <gml:X>-117.646187</gml:X>\n" +
                "                <gml:Y>34.823299</gml:Y>\n" +
                "            </gml:coord>\n" +
                "        </gml:Box>\n" +
                "    </document>\n" +
                "</contentlocation>\n" +
                "</doc>";

        PlaceMakerDocument placeMakerDocument = new PlaceMakerDocument(xml);
    }



}
