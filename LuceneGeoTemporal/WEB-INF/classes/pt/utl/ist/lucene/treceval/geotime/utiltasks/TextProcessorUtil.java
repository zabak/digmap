package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 12/Jul/2011
 * Time: 14:05:20
 * To change this template use File | Settings | File Templates.
 */
public class TextProcessorUtil {

    public static void main(String [] args) throws IOException {
//        String terms = "administrative areas\n" +
//                "cadastral areas \n" +
//                "military areas \n" +
//                "political areas \n" +
//                "countries \n" +
//                "countries, 1st order divisions \n" +
//                "countries, 2nd order divisions \n" +
//                "countries, 3rd order divisions \n" +
//                "countries, 4th order divisions \n" +
//                "multinational entities \n" +
//                "populated places \n" +
//                "cities \n" +
//                "capitals \n" +
//                "postal areas \n" +
//                "school districts \n" +
//                "statistical areas \n" +
//                "census areas \n" +
//                "Metropolitan Statistical Areas \n" +
//                "territorial waters \n" +
//                "tribal areas \n" +
//                "hydrographic features\n" +
//                "aquifers \n" +
//                "bays \n" +
//                "fjords \n" +
//                "channels \n" +
//                "drainage basins \n" +
//                "estuaries \n" +
//                "floodplains \n" +
//                "gulfs \n" +
//                "guts \n" +
//                "ice masses \n" +
//                "glacier features \n" +
//                "lakes \n" +
//                "seas \n" +
//                "oceans \n" +
//                "ocean currents \n" +
//                "ocean regions \n" +
//                "streams \n" +
//                "rivers \n" +
//                "bends (river) \n" +
//                "rapids \n" +
//                "waterfalls \n" +
//                "springs (hydrographic) \n" +
//                "thermal features \n" +
//                "land parcels\n" +
//                "manmade features\n" +
//                "agricultural sites \n" +
//                "buildings \n" +
//                "capitol buildings \n" +
//                "commercial sites \n" +
//                "industrial sites \n" +
//                "power generation sites \n" +
//                "court houses \n" +
//                "institutional sites \n" +
//                "correctional facilities \n" +
//                "educational facilities \n" +
//                "medical facilities \n" +
//                "religious facilities \n" +
//                "library buildings \n" +
//                "museum buildings \n" +
//                "post office buildings \n" +
//                "research facilities \n" +
//                "data collection facilities \n" +
//                "residential sites \n" +
//                "housing areas \n" +
//                "mobile home parks \n" +
//                "cemeteries \n" +
//                "disposal sites \n" +
//                "firebreaks \n" +
//                "fisheries \n" +
//                "fortifications \n" +
//                "historical sites \n" +
//                "archaeological sites \n" +
//                "hydrographic structures \n" +
//                "breakwaters \n" +
//                "canals \n" +
//                "dam sites \n" +
//                "gaging stations \n" +
//                "harbors \n" +
//                "marinas \n" +
//                "levees \n" +
//                "offshore platforms \n" +
//                "piers \n" +
//                "reservoirs \n" +
//                "waterworks \n" +
//                "launch facilities \n" +
//                "mine sites \n" +
//                "monuments \n" +
//                "oil fields \n" +
//                "parks \n" +
//                "viewing locations \n" +
//                "recreational facilities \n" +
//                "amusement parks \n" +
//                "camps \n" +
//                "performance sites \n" +
//                "sports facilities \n" +
//                "reference locations \n" +
//                "research areas \n" +
//                "ecological research sites \n" +
//                "paleontological sites \n" +
//                "reserves \n" +
//                "storage structures \n" +
//                "telecommunication features \n" +
//                "towers \n" +
//                "transportation features \n" +
//                "airport features \n" +
//                "heliports \n" +
//                "seaplane bases \n" +
//                "aqueducts \n" +
//                "bridges \n" +
//                "cableways \n" +
//                "locks \n" +
//                "parking sites \n" +
//                "pipelines \n" +
//                "railroad features \n" +
//                "roadways \n" +
//                "trails \n" +
//                "tunnels \n" +
//                "wells \n" +
//                "windmills \n" +
//                "physiographic features\n" +
//                "alluvial fans \n" +
//                "arroyos \n" +
//                "badlands \n" +
//                "banks (hydrographic) \n" +
//                "bars (physiographic) \n" +
//                "basins \n" +
//                "storage basins \n" +
//                "beaches \n" +
//                "bights \n" +
//                "capes \n" +
//                "caves \n" +
//                "cirques \n" +
//                "cliffs \n" +
//                "craters \n" +
//                "deltas \n" +
//                "dunes \n" +
//                "flats \n" +
//                "gaps \n" +
//                "isthmuses \n" +
//                "karst areas \n" +
//                "ledges \n" +
//                "massifs \n" +
//                "mesas \n" +
//                "mineral deposit areas \n" +
//                "moraines \n" +
//                "mountains \n" +
//                "continental divides \n" +
//                "mountain ranges \n" +
//                "mountain summits \n" +
//                "ridges \n" +
//                "drumlins \n" +
//                "natural rock formations \n" +
//                "arches (natural formation) \n" +
//                "plains \n" +
//                "plateaus \n" +
//                "playas \n" +
//                "reefs \n" +
//                "coral reefs \n" +
//                "seafloor features \n" +
//                "abyssal features \n" +
//                "continental margins \n" +
//                "fracture zones \n" +
//                "hydrothermal vents \n" +
//                "ocean trenches \n" +
//                "seamounts \n" +
//                "submarine canyons \n" +
//                "tectonic features \n" +
//                "earthquake features \n" +
//                "faults \n" +
//                "fault zones \n" +
//                "rift zones \n" +
//                "folds (geologic) \n" +
//                "anticlines \n" +
//                "synclines \n" +
//                "valleys \n" +
//                "canyons \n" +
//                "volcanic features \n" +
//                "lava fields \n" +
//                "volcanoes \n" +
//                "regions\n" +
//                "agricultural regions \n" +
//                "biogeographic regions \n" +
//                "barren lands \n" +
//                "deserts \n" +
//                "forests \n" +
//                "petrified forests \n" +
//                "rain forests \n" +
//                "woods \n" +
//                "grasslands \n" +
//                "habitats \n" +
//                "jungles \n" +
//                "oases \n" +
//                "shrublands \n" +
//                "snow regions \n" +
//                "tundras \n" +
//                "wetlands \n" +
//                "climatic regions \n" +
//                "coastal zones \n" +
//                "economic regions \n" +
//                "land regions \n" +
//                "continents \n" +
//                "islands \n" +
//                "archipelagos \n" +
//                "subcontinents \n" +
//                "linguistic regions \n" +
//                "map regions \n" +
//                "chart regions \n" +
//                "map quadrangle regions \n" +
//                "UTM zones ";
//
//        BufferedReader br = new BufferedReader(new StringReader(terms));
//
//        String line;
//        while((line = br.readLine())!= null)
//        {
//            Analyzer analyzer = IndexCollections.en.getAnalyzerWithStemming();
//            TokenStream ts = analyzer.tokenStream(new StringReader(line));
//            String finalStr = "";
//            Token t =null;
//            while((t = ts.next())!=null)
//            {
//                finalStr += " " + t.termText();
//            }
//            System.out.println("| " + "\"" + finalStr.trim() + "\"");
//        }
//        br.close();

        String terms = "Beijing\n" +
                "\n" +
                "NONE\n" +
                "\n" +
                "in Beijing\n" +
                "\n" +
                "IN\n" +
                "\n" +
                "on the Long Island\n" +
                "\n" +
                "ON\n" +
                "\n" +
                "of Beijing\n" +
                "\n" +
                "OF\n" +
                "\n" +
                "near Beijing\n" +
                "\n" +
                "next to Beijing\n" +
                "\n" +
                "NEAR\n" +
                "\n" +
                "in or around Beijing\n" +
                "\n" +
                "in and around Beijing\n" +
                "\n" +
                "IN_NEAR\n" +
                "\n" +
                "along the Rhine\n" +
                "\n" +
                "ALONG\n" +
                "\n" +
                "at Beijing University\n" +
                "\n" +
                "AT\n" +
                "\n" +
                "from Beijing\n" +
                "\n" +
                "FROM\n" +
                "\n" +
                "to Beijing\n" +
                "\n" +
                "TO\n" +
                "\n" +
                "within d miles of Beijing\n" +
                "\n" +
                "DISTANCE\n" +
                "\n" +
                "north of Beijing\n" +
                "\n" +
                "in the north of Beijing\n" +
                "\n" +
                "NORTH_OF\n" +
                "\n" +
                "south of Beijing\n" +
                "\n" +
                "in the south of Beijing\n" +
                "\n" +
                "SOUTH_OF\n" +
                "\n" +
                "east of Beijing\n" +
                "\n" +
                "in the east of Beijing\n" +
                "\n" +
                "EAST_OF\n" +
                "\n" +
                "west of Beijing\n" +
                "\n" +
                "in the west of Beijing\n" +
                "\n" +
                "WEST_OF\n" +
                "\n" +
                "northeast of Beijing\n" +
                "\n" +
                "in the northeast of Beijing\n" +
                "\n" +
                "NORTH_EAST_OF\n" +
                "\n" +
                "northwest of Beijing\n" +
                "\n" +
                "in the northwest of Beijing\n" +
                "\n" +
                "NORTH_WEST_OF\n" +
                "\n" +
                "southeast of Beijing\n" +
                "\n" +
                "in the southeast of Beijing\n" +
                "\n" +
                "SOUTH_EAST_OF\n" +
                "\n" +
                "southwest of Beijing\n" +
                "\n" +
                "in the southwest of Beijing\n" +
                "\n" +
                "SOUTH_WEST_OF\n" +
                "\n" +
                "north to Beijing\n" +
                "\n" +
                "NORTH_TO\n" +
                "\n" +
                "south to Beijing\n" +
                "\n" +
                "SOUTH_TO\n" +
                "\n" +
                "east to Beijing\n" +
                "\n" +
                "EAST_TO\n" +
                "\n" +
                "west to Beijing\n" +
                "\n" +
                "WEST_TO\n" +
                "\n" +
                "northeast to Beijing\n" +
                "\n" +
                "NORTH_EAST_TO\n" +
                "\n" +
                "northwest to Beijing\n" +
                "\n" +
                "NORTH_WEST_TO\n" +
                "\n" +
                "southeast to Beijing\n" +
                "\n" +
                "SOUTH_EAST_TO\n" +
                "\n" +
                "southwest to Beijing\n" +
                "\n" +
                "SOUTH_WEST_TO";

        BufferedReader br = new BufferedReader(new StringReader(terms));
        String line;
//        while((line = br.readLine())!= null)
//        {
//            if(line.trim().length() > 0 && line.charAt(0)>='A' && line.charAt(0)<='Z')
//            System.out.println("| " + "\""+ line.toLowerCase().replace("_"," ") + "\"");
//        }
//        br.close();


        terms = "above\n" +
                "across\n" +
                "after\n" +
                "against\n" +
                "along\n" +
                "among\n" +
                "around\n" +
                "behind\n" +
                "below\n" +
                "beside\n" +
                "between\n" +
                "by\n" +
                "close to\n" +
                "down\n" +
                "from\n" +
                "in front of\n" +
                "inside\n" +
                "into\n" +
                "near\n" +
                "next to\n" +
                "off\n" +
                "onto\n" +
                "opposite\n" +
                "out of\n" +
                "outside\n" +
                "over\n" +
                "past\n" +
                "round\n" +
                "through\n" +
                "to\n" +
                "towards\n" +
                "under\n" +
                "up";

//        br = new BufferedReader(new StringReader(terms));
//
//        while((line = br.readLine())!= null)
//        {
//
//                System.out.println("| " + "\""+ line.toLowerCase() + "\"");
//        }
//        br.close();


        /**
         * TIME
         */
        terms = "in\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "at\n" +
                "\n" +
                "\n" +
                "\n" +
                "on\n" +
                "\n" +
                "\n" +
                "\n" +
                "after\n" +
                "ago\n" +
                "before\n" +
                "between\n" +
                "by\n" +
                "during\n" +
                "for\n" +
                "from ... to\n" +
                "from... till/until\n" +
                "\n" +
                "past\n" +
                "since\n" +
                "till/until\n" +
                "\n" +
                "to\n" +
                "up to\n" +
                "within"    ;
           br = new BufferedReader(new StringReader(terms));

        while((line = br.readLine())!= null)
        {
            if(line.trim().length()>0)
                System.out.println("| " + "\""+ line.toLowerCase() + "\"");
        }
        br.close();

    }
}
