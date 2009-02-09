package pt.utl.ist.lucene.treceval.geoclef;

import java.util.HashMap;

/**
 * @author Jorge Machado
 * @date 10/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef
 */
public class Globals
{
    public static String collectionPathPt = "D:\\Projectos\\coleccoesIR\\geoclef\\pt";
    public static String collectionPathEn = "D:\\Projectos\\coleccoesIR\\geoclef\\en";

    public static String outputGeoParseDir = "D:\\Projectos\\coleccoesIR\\geoparse";

    public static HashMap<String,String> namespacesOutput = new HashMap();
    public static String localNamespace = "http://local";
    static
    {
        namespacesOutput.put("gp", "http://www.opengis.net/gp");
        namespacesOutput.put("local",localNamespace);
        namespacesOutput.put("gml","http://www.opengis.net/gml");
    }
}
