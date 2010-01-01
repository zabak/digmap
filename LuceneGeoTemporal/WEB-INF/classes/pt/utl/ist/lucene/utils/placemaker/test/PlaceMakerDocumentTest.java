package pt.utl.ist.lucene.utils.placemaker.test;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.placemaker.PlaceMakerDocument;
import org.dom4j.DocumentException;

/**
 * @author Jorge Machado
 * @date 1/Jan/2010
 * @time 20:25:59
 * @email machadofisher@gmail.com
 */
public class PlaceMakerDocumentTest extends TestCase {

    public void testReader()
    {
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
                "            <woeId>12587689</woeId>\n" +
                "            <type>County2Teste</type>\n" +
                "            <name><![CDATA[Los Angeles, CA, US]]></name>\n" +
                "            <centroid>\n" +
                "                <latitude>35.2635</latitude>\n" +
                "                <longitude>-119.296</longitude>\n" +
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
                "                <type>Town2</type>\n" +
                "                <name><![CDATA[Marina del Rey, CA, US]]></name>\n" +
                "                <centroid>\n" +
                "                    <latitude>33.9852</latitude>\n" +
                "                    <longitude>-118.452</longitude>\n" +
                "                </centroid>\n" +
                "            </place>\n" +
                "            <matchType>1</matchType>\n" +
                "            <weight>2</weight>\n" +
                "            <confidence>8</confidence>\n" +
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

        try {

            /**
             *
             */
            PlaceMakerDocument placeMakerDocument = new PlaceMakerDocument(xml);
            assertEquals(placeMakerDocument.getAdministrativeWoeid(),"12587688");
            assertEquals(placeMakerDocument.getGeographicWoeid(),"12587689");
            assertEquals(placeMakerDocument.getAdministrativeType(),"County");
            assertEquals(placeMakerDocument.getGeographicType(),"County2Teste");
            assertEquals(placeMakerDocument.getAdministrativeCentroide().getLat(),34.2635d);
            assertEquals(placeMakerDocument.getAdministrativeCentroide().getLng(),-118.296d);
            assertEquals(placeMakerDocument.getGeographicCentroide().getLat(),35.2635d);
            assertEquals(placeMakerDocument.getGeographicCentroide().getLng(),-119.296d);

            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getWoeId(),"2441472");
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getType(),"Town");
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getName(),"Long Beach, CA, US");
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getMatchType(),0);
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getWeight(),1);
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getConfidence(),7);
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getCentroide().getLat(),33.7667d);
            assertEquals(placeMakerDocument.getPlaceDetails().get(0).getCentroide().getLng(),-118.192d);


            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getWoeId(),"2445713");
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getType(),"Town2");
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getName(),"Marina del Rey, CA, US");
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getMatchType(),1);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getWeight(),2);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getConfidence(),8);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getCentroide().getLat(),33.9852d);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getCentroide().getLng(),-118.452d);

            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(0).getStartOffset(),156);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(0).getEndOffset(),170);

            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(1).getStartOffset(),346);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(1).getEndOffset(),360);

            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(2).getStartOffset(),35);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(2).getEndOffset(),49);

            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(3).getStartOffset(),693);
            assertEquals(placeMakerDocument.getPlaceDetails().get(1).getRefs().get(3).getEndOffset(),707);




            assertEquals(placeMakerDocument.getPlaceDetails().get(3).getRefs().get(0).getStartOffset(),1212);
            assertEquals(placeMakerDocument.getPlaceDetails().get(3).getRefs().get(0).getEndOffset(),1231);

            assertEquals(placeMakerDocument.getPlaceDetails().get(3).getRefs().get(1).getStartOffset(),133);
            assertEquals(placeMakerDocument.getPlaceDetails().get(3).getRefs().get(1).getEndOffset(),152);

            assertTrue(placeMakerDocument.getPlaceDetails().get(0).getRefs().size() > 0);
            assertTrue(placeMakerDocument.getPlaceDetails().get(1).getRefs().size() > 0);
            assertTrue(placeMakerDocument.getPlaceDetails().get(2).getRefs().size() > 0);
            assertTrue(placeMakerDocument.getPlaceDetails().get(3).getRefs().size() > 0);




        } catch (DocumentException e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }
}
