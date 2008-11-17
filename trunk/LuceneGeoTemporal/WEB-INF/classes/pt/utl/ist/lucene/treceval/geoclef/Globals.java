package pt.utl.ist.lucene.treceval.geoclef;

import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 10/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef
 */
public class Globals
{
    public static String collectionPathPt = "C:\\Servidores\\DATA\\geo\\pt";
    public static String collectionPathEn = "C:\\Servidores\\DATA\\geo\\en";

    public static String outputGeoParseDir = "C:\\Servidores\\DATA\\geo\\geoparse";

    public static HashMap<String,String> namespacesOutput = new HashMap();
    public static String localNamespace = "http://local";
    static
    {
        namespacesOutput.put("gp", "http://www.opengis.net/gp");
        namespacesOutput.put("local",localNamespace);
        namespacesOutput.put("gml","http://www.opengis.net/gml");
    }
}
