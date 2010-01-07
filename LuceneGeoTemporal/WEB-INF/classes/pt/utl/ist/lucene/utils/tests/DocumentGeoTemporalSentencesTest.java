package pt.utl.ist.lucene.utils.tests;

import junit.framework.TestCase;
import pt.utl.ist.lucene.utils.temporal.DocumentTemporalSentences;
import pt.utl.ist.lucene.utils.DocumentPlaceMakerAndTemporalSentences;
import pt.utl.ist.lucene.treceval.geotime.NyTimesDocument;
import pt.utl.ist.lucene.treceval.geotime.EOFException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

import org.dom4j.DocumentException;

/**
 * @author Jorge Machado
 * @date 29/Dez/2009
 * @time 18:12:17
 * @email machadofisher@gmail.com
 */
public class DocumentGeoTemporalSentencesTest extends TestCase
{

    public void testExample1() throws EOFException, IOException, DocumentException {
         String doc = "<DOC id=\"NYT_ENG_20020101.0001\" type=\"story\" >\n" +
                 "<HEADLINE>\n" +
                 "JANICE FARRAR THADDEUS, 68, LITERARY SCHOLAR\n" +
                 "</HEADLINE>\n" +
                 "<DATELINE>\n" +
                 " (BC-OBIT-THADDEUS-NYT)\n" +
                 "</DATELINE>\n" +
                 "<TEXT>\n" +
                 "<P>\n" +
                 "Janice Farrar Thaddeus, a scholar, poet, editor and former\n" +
                 "Harvard lecturer in English, died Dec. 23 in Cambridge, Mass. She\n" +
                 "was 68.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "The cause was a stroke, said her husband, Patrick Thaddeus.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "In ``When Women Look at Men'' (Harper &AMP; Row), the feminist\n" +
                 "anthology Dr. Thaddeus edited in 1963 with John A. Kouwenhoven, she\n" +
                 "and her co-editor provided an answer to the question why women do\n" +
                 "not achieve as much as men in the professions: ``They have no\n" +
                 "wives.''\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "Thaddeus also wrote scholarly articles on Jonathan Swift,\n" +
                 "Richard Wright and Anthony Burgess.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "She wrote a biography, ``Frances Burney: A Literary Life'' (St.\n" +
                 "Martin's, 2000), about a writer who had been considered a minor\n" +
                 "novelist of manners even though she was admired by Samuel Johnson,\n" +
                 "Edmund Burke, Jane Austen and Edward Gibbon.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "Janice Farrar Thaddeus was born in New York City on July 20,\n" +
                 "1933, into a family immersed in the literary world. Her father,\n" +
                 "John Chipman Farrar, was editor of The Bookman in the 1920s and a\n" +
                 "founder in 1946 with Roger Straus Jr. of the publishing house\n" +
                 "Farrar, Straus &AMP; Giroux. Her mother, Margaret Petherbridge Farrar,\n" +
                 "was the first crossword puzzle editor of The New York Times.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "Thaddeus received a bachelor's degree from Barnard College in\n" +
                 "1955 and a doctorate from Columbia in 1965. In 1986 she became a\n" +
                 "lecturer in history and literature at Harvard. She retired in 1999.\n" +
                 "</P>\n" +
                 "<P>\n" +
                 "In addition to her husband, an astrophysicist at Harvard and at\n" +
                 "the Smithsonian Astrophysical Observatory, Thaddeus is survived by\n" +
                 "a daughter, Eva Thaddeus of Albuquerque; a son, Michael Thaddeus of\n" +
                 "Manhattan; a sister, Alison Wilson of Palo Alto, Calif.; and a\n" +
                 "brother, Curtis Farrar of Washington.\n" +
                 "</P>\n" +
                 "</TEXT>\n" +
                 "</DOC>";

        String timex2Doc = "<doc id=\"NYT_ENG_20020101.0001\">\n" +
                "<DOC generator=\"timexdoc.py\">\n" +
                "    <reftime rstart=\"1\" rend=\"8\" val=\"2002-01-01\">\n" +
                "        <TIMEX2 rstart=\"1\" rend=\"8\" val=\"2002-01-01\">2002-1-1</TIMEX2>\n" +
                "    </reftime>\n" +
                "    <TEXT rstart=\"83\" rend=\"1766\">\n" +
                "        <TIMEX2 set=\"\" rend=\"142\" val=\"PAST_REF\" anchor_dir=\"BEFORE\"\n" +
                "            tmxclass=\"genpoint\" rstart=\"137\" anchor_val=\"2002-01-01\"\n" +
                "            dirclass=\"before\" parsenode=\".1 w11\" prenorm=\"PAST_REF\">former</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"184\" val=\"2001-12-23\" tmxclass=\"point\"\n" +
                "            rstart=\"178\" dirclass=\"before\" parsenode=\".1 p29\" prenorm=\"|dex|Y|XXXX-12-23\">Dec. 23</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"377\" val=\"1963\" tmxclass=\"point\"\n" +
                "            rstart=\"374\" dirclass=\"before\" parsenode=\".3 p40\" prenorm=\"|fq|_1963\">1963</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"950\" val=\"1933-07-20\" tmxclass=\"point\"\n" +
                "            rstart=\"938\" dirclass=\"before\" parsenode=\".6 p18\" prenorm=\"|fq|_1933-07-20\">July 20,\n" +
                "1933</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1069\" val=\"192\" tmxclass=\"point\"\n" +
                "            rstart=\"1061\" dirclass=\"before\" parsenode=\".7 p25\" prenorm=\"|fq|_192\">the 1920s</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1091\" val=\"1946\" tmxclass=\"point\"\n" +
                "            rstart=\"1088\" dirclass=\"before\" parsenode=\".7 p36\" prenorm=\"|fq|_1946\">1946</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1334\" val=\"1955\" tmxclass=\"point\"\n" +
                "            rstart=\"1331\" dirclass=\"before\" parsenode=\".9 p20\" prenorm=\"|fq|_1955\">1955</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1372\" val=\"1965\" tmxclass=\"point\"\n" +
                "            rstart=\"1369\" dirclass=\"before\" parsenode=\".9 p33\" prenorm=\"|fq|_1965\">1965</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1381\" val=\"1986\" tmxclass=\"point\"\n" +
                "            rstart=\"1378\" dirclass=\"before\" parsenode=\".10 p3\" prenorm=\"|fq|_1986\">1986</TIMEX2>\n" +
                "        <TIMEX2 set=\"\" rend=\"1461\" val=\"1999\" tmxclass=\"point\"\n" +
                "            rstart=\"1458\" dirclass=\"before\" parsenode=\".11 p7\" prenorm=\"|fq|_1999\">1999</TIMEX2>\n" +
                "    </TEXT>\n" +
                "</DOC>\n" +
                "</doc>";
                String placeMakerDoc =
                "<doc id=\"NYT_ENG_20020101.0001\"><contentlocation xmlns:yahoo=\"http://www.yahooapis.com/v1/base.rng\"\n" +
                        "    xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
                        "    xmlns=\"http://wherein.yahooapis.com/v1/schema\" xml:lang=\"en-EN\">\n" +
                        "    <processingTime>0.011991</processingTime>\n" +
                        "    <version> build 091119</version>\n" +
                        "    <documentLength>1730</documentLength>\n" +
                        "    <document>\n" +
                        "        <administrativeScope>\n" +
                        "            <woeId>23424977</woeId>\n" +
                        "            <type>Country</type>\n" +
                        "            <name><![CDATA[United States]]></name>\n" +
                        "            <centroid>\n" +
                        "                <latitude>37.1679</latitude>\n" +
                        "                <longitude>-95.845</longitude>\n" +
                        "            </centroid>\n" +
                        "        </administrativeScope>\n" +
                        "        <geographicScope>\n" +
                        "            <woeId>23689955</woeId>\n" +
                        "            <type>Colloquial</type>\n" +
                        "            <name><![CDATA[East Coast, US]]></name>\n" +
                        "            <centroid>\n" +
                        "                <latitude>35.8953</latitude>\n" +
                        "                <longitude>-77.2697</longitude>\n" +
                        "            </centroid>\n" +
                        "        </geographicScope>\n" +
                        "        <extents>\n" +
                        "            <center>\n" +
                        "                <latitude>38.8991</latitude>\n" +
                        "                <longitude>-77.029</longitude>\n" +
                        "            </center>\n" +
                        "            <southWest>\n" +
                        "                <latitude>18.0522</latitude>\n" +
                        "                <longitude>-122.206</longitude>\n" +
                        "            </southWest>\n" +
                        "            <northEast>\n" +
                        "                <latitude>42.4098</latitude>\n" +
                        "                <longitude>-63.013</longitude>\n" +
                        "            </northEast>\n" +
                        "        </extents>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>2352824</woeId>\n" +
                        "                <type>Town</type>\n" +
                        "                <name><![CDATA[Albuquerque, NM, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>35.0842</latitude>\n" +
                        "                    <longitude>-106.649</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>2373572</woeId>\n" +
                        "                <type>Town</type>\n" +
                        "                <name><![CDATA[Cambridge, MA, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>42.3668</latitude>\n" +
                        "                    <longitude>-71.106</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>2459115</woeId>\n" +
                        "                <type>Town</type>\n" +
                        "                <name><![CDATA[New York, NY, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>40.7146</latitude>\n" +
                        "                    <longitude>-74.0071</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>2467861</woeId>\n" +
                        "                <type>Town</type>\n" +
                        "                <name><![CDATA[Palo Alto, CA, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>37.4447</latitude>\n" +
                        "                    <longitude>-122.161</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>2514815</woeId>\n" +
                        "                <type>Town</type>\n" +
                        "                <name><![CDATA[Washington, DC, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>38.8991</latitude>\n" +
                        "                    <longitude>-77.029</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>7</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>12484137</woeId>\n" +
                        "                <type>Island</type>\n" +
                        "                <name><![CDATA[Saint-Martin Ile, MF]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>18.0913</latitude>\n" +
                        "                    <longitude>-63.0829</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>1</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>12589342</woeId>\n" +
                        "                <type>County</type>\n" +
                        "                <name><![CDATA[Manhattan, New York, NY, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>40.7909</latitude>\n" +
                        "                    <longitude>-73.9664</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>23511607</woeId>\n" +
                        "                <type>POI</type>\n" +
                        "                <name><![CDATA[Harvard University, Cambridge, MA, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>42.3742</latitude>\n" +
                        "                    <longitude>-71.1169</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>23511616</woeId>\n" +
                        "                <type>POI</type>\n" +
                        "                <name><![CDATA[Columbia University, New York, NY, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>40.8089</latitude>\n" +
                        "                    <longitude>-73.9616</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <placeDetails>\n" +
                        "            <place>\n" +
                        "                <woeId>24866150</woeId>\n" +
                        "                <type>POI</type>\n" +
                        "                <name><![CDATA[Barnard College, New York, NY, US]]></name>\n" +
                        "                <centroid>\n" +
                        "                    <latitude>40.8101</latitude>\n" +
                        "                    <longitude>-73.9624</longitude>\n" +
                        "                </centroid>\n" +
                        "            </place>\n" +
                        "            <matchType>0</matchType>\n" +
                        "            <weight>1</weight>\n" +
                        "            <confidence>8</confidence>\n" +
                        "        </placeDetails>\n" +
                        "        <referenceList>\n" +
                        "            <reference>\n" +
                        "                <woeIds>23511607</woeIds>\n" +
                        "                <start>106</start>\n" +
                        "                <end>113</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Harvard]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>24866150</woeIds>\n" +
                        "                <start>1276</start>\n" +
                        "                <end>1291</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Barnard College]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>23511616</woeIds>\n" +
                        "                <start>1321</start>\n" +
                        "                <end>1329</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Columbia]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>23511607</woeIds>\n" +
                        "                <start>1398</start>\n" +
                        "                <end>1405</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Harvard]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>23511607</woeIds>\n" +
                        "                <start>1478</start>\n" +
                        "                <end>1485</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Harvard]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>2373572</woeIds>\n" +
                        "                <start>151</start>\n" +
                        "                <end>160</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Cambridge]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>2352824</woeIds>\n" +
                        "                <start>1588</start>\n" +
                        "                <end>1599</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Albuquerque]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>12589342</woeIds>\n" +
                        "                <start>1628</start>\n" +
                        "                <end>1637</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Manhattan]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>2467861</woeIds>\n" +
                        "                <start>1666</start>\n" +
                        "                <end>1682</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Palo Alto, Calif]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>2514815</woeIds>\n" +
                        "                <start>1717</start>\n" +
                        "                <end>1727</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[Washington]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>12484137</woeIds>\n" +
                        "                <start>666</start>\n" +
                        "                <end>678</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[St. Martin's]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "            <reference>\n" +
                        "                <woeIds>2459115</woeIds>\n" +
                        "                <start>882</start>\n" +
                        "                <end>895</end>\n" +
                        "                <isPlaintextMarker>1</isPlaintextMarker>\n" +
                        "                <text><![CDATA[New York City]]></text>\n" +
                        "                <type>plaintext</type>\n" +
                        "                <xpath><![CDATA[]]></xpath>\n" +
                        "            </reference>\n" +
                        "        </referenceList>\n" +
                        "        <gml:Box xmlns:gml=\"http://www.opengis.net/gml\">\n" +
                        "            <gml:coord>\n" +
                        "                <gml:X>-87.634880</gml:X>\n" +
                        "                <gml:Y>24.521000</gml:Y>\n" +
                        "            </gml:coord>\n" +
                        "            <gml:coord>\n" +
                        "                <gml:X>-66.932640</gml:X>\n" +
                        "                <gml:Y>47.459831</gml:Y>\n" +
                        "            </gml:coord>\n" +
                        "        </gml:Box>\n" +
                        "    </document>\n" +
                        "</contentlocation>\n" +
                        "</doc>";

        NyTimesDocument document = new NyTimesDocument(new BufferedReader(new StringReader(doc)),"dummy.txt");
        DocumentPlaceMakerAndTemporalSentences documentPlaceMakerAndTemporalSentences = new DocumentPlaceMakerAndTemporalSentences(document,timex2Doc,placeMakerDoc);
        System.out.println(documentPlaceMakerAndTemporalSentences);


    }
}
