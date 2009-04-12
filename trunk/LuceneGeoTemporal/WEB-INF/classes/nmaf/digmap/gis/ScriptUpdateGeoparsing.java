package nmaf.digmap.gis;

import nmaf.digmap.DigmapServices;
import org.dom4j.Document;
import org.dom4j.Element;
import pt.utl.ist.lucene.config.LocalProperties;
import pt.utl.ist.lucene.config.PropertiesUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ScriptUpdateGeoparsing {

    public static void main(String[] args) throws Exception{



        LocalProperties conf  = new LocalProperties("digmap-ingest.properties");
        boolean testing= PropertiesUtil.getBooleanProperty(conf,"testing");
        boolean useGeoParserGazeteerIds=PropertiesUtil.getBooleanProperty(conf,"useGeoParserGazeteerIds");
        String outDir=conf.getProperty("outDir");
        boolean generateAll=PropertiesUtil.getBooleanProperty(conf,"generateAll");

        String[] imports=conf.getProperty("import").trim().split(" ");
        HashSet<String> importsToExecute=new HashSet<String>();
        for(String i:imports) {
            importsToExecute.add(i);
        }

        GeoParser.geoParserBaseUrl=conf.getProperty("geoParserBaseUrl");


        DigmapServices servs = new DigmapServices(new File(outDir, "out"), new File(outDir, "outAut"));
        servs.setTesting(testing);
        DigmapServices.setInstance(servs);

        if(generateAll) {
            for(String lib: importsToExecute) {
                int cont = 0;
                int total = new File(outDir, "out/" + lib + "/digmap").listFiles().length;
                 for ( Document d: DigmapServices.getBibliographicFile().allRecords(lib, "Digmap") ) {
                    System.out.println(++cont + " of " + total+ " found " + GeneralEntryCounter + " general entries:" + lib);
                    Element recEl=d.getRootElement();
                    String libFormat=lib+"Digmap";
                    String urn=recEl.attributeValue("urn");
                    String recId=urn.substring(urn.lastIndexOf(libFormat)+libFormat.length()+1);

                    for(Iterator<Element> it=recEl.elements().iterator(); it.hasNext(); ) {
                        Element el=it.next();
                        if(el.getName() != null && (el.getName().equals("place") || (el.getName().equals("spatial") && el.attributeValue("type") != null && el.attributeValue("type").contains("Automatic"))))
                            it.remove();
                    }

                    GeoParser.addPlaceNameTerms(d, useGeoParserGazeteerIds);

                    DigmapServices.getBibliographicFile().createRecord(lib, d, recId, "Digmap");
                }
            }
        }
    }


    public static int GeneralEntryCounter = 0; 





}
