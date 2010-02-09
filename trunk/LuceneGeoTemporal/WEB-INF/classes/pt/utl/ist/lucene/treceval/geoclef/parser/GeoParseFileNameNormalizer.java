package pt.utl.ist.lucene.treceval.geoclef.parser;

import java.io.File;

/**
 * @author Jorge Machado
 * @date 13/Nov/2008
 * @see pt.utl.ist.lucene.treceval.geoclef.parser
 */
public class GeoParseFileNameNormalizer
{

    public void normalize(String path)
    {
        File f = new File(path);
        File[] files = f.listFiles();
        for(File inFile: files)
        {
            if(inFile.getName().startsWith("geoParse"))
            {
                System.out.println("normalizing: " + inFile.getName());
                char[] endName = new char[6];
                for(int i = 0; i < 6; i++)
                {
                    endName[i] = '0';
                }
                String number = inFile.getName().substring("geoParse".length(),inFile.getName().indexOf('.'));
                if(!number.startsWith("0") || number.length() == 1) //for geoParser0.xml
                {
                    for(int i = 5; i >= 0 && number.length() >0;i--)
                    {
                        endName[i] = number.charAt(number.length()-1);
                        number = number.substring(0,number.length()-1);
                    }
                    inFile.renameTo(new File(path + File.separator + new String(endName) + ".xml"));
                }
            }
        }
    }

    public static void main(String[] args)
    {   File f = new File("C:\\WORKSPACE_JM\\DATA\\COLLECTIONS\\GeoCLEF\\en\\gh-95");
        for(File f2: f.listFiles())
        {
            System.out.println(f2.getName());
        }
        new GeoParseFileNameNormalizer().normalize("C:\\WORKSPACE_JM\\DATA\\COLLECTIONS\\GeoCLEF\\en\\gh-95");

        for(File f2: f.listFiles())
        {
            System.out.println(f2.getName());
        }

    }
}
