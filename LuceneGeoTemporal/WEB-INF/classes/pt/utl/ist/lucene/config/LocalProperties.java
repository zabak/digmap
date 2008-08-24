package pt.utl.ist.lucene.config;

import java.util.Properties;
import java.net.URL;
import java.io.*;

/**
 * @author Jorge Machado
 * @date 15/Ago/2008
 * @see pt.utl.ist.lucene
 */

public class LocalProperties extends Properties
{


   public static String getPathOfResource(String resourceName)
   {
       URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url==null)
            return null;
        String ret;
        try{
            ret=java.net.URLDecoder.decode(url.getFile(),"iso-8859-1");
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
        if (ret==null)
            return null;
        if (ret.startsWith("/"))
            return ret.substring(1);
        return ret;
   }

   public LocalProperties(String filename) throws IOException
   {

        InputStream file = getClass().getClassLoader().getResourceAsStream(filename);
        int dot = filename.indexOf(".");
        String startFilename = filename.substring(0,filename.indexOf("."));
        InputStream fileEdited = getClass().getClassLoader().getResourceAsStream(startFilename + "Edited" + filename.substring(dot));
        load(file);
        if(fileEdited!=null){
            System.out.println("overriding " + filename + " >> " + startFilename + "Edited" + filename.substring(dot));
            load(fileEdited);
        }

  }

  public LocalProperties() {

  }

}
