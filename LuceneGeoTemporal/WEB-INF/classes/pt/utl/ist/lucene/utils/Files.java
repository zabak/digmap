package pt.utl.ist.lucene.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashSet;

/**
 * @author Jorge Machado
 * @date 17/Ago/2008
 * @see pt.utl.ist.lucene.utils
 */
public class Files
{
    private static final Logger logger = Logger.getLogger(Files.class);


    private static Files instance;

    private Files(){}

    static
    {
        instance = new Files();
    }

    public static Files getInstance()
    {
        return instance;
    }
    /**
     * Delete a dir and all internal files and dir's
     *
     * @param startPath to delete
     * @return 0 if everything was deleted and -1 if something was not deleted
     */
    public static int delDirsE(String startPath)
    {
        int result = 0;

        File f = new File(startPath);

        if(!f.exists())
        {
            return -1;
        }
        else
        {

            File aux;
            File[] files = f.listFiles();
            for (File file : files) {

                aux = file;
                if (aux.list() == null) {
                    logger.info("Deleting file: " + startPath + "/" + aux.getName());
                    if (!aux.delete())
                    {
                        result = -1;
                        logger.info("Can't deleting file: " + startPath + "/" + aux.getName());
                        aux.deleteOnExit();
                    }
                }
                else {
                    if (result >= 0)
                        result = delDirsE(startPath + "/" + aux.getName());
                    else
                        delDirsE(startPath + "/" + aux.getName());
                }
            }
            logger.warn("Deleting Dir: " + startPath + "/" + f.getName());
            if(!f.delete())
            {
                logger.info("Can't deleting file: " + startPath);
                f.deleteOnExit();
                result = -1;
            }
        }
        return result;
    }

    public HashSet<String> readWords(String path) throws IOException
    {
        InputStream file = getClass().getClassLoader().getResourceAsStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
        String line;

        HashSet<String> set = new HashSet();
        while((line = bufferedReader.readLine()) != null)
        {
            line = line.trim();
            if(line.length()>0)
            {
                set.add(line);
            }
        }
        return set;
    }
    
    public static String getExtension(String fileName)
    {
        if(fileName == null)
            return "";
        int lastDot = fileName.lastIndexOf('.');
        if(lastDot < 0)
            return "";
        else if(fileName.length() - lastDot <= 1)
            return "";
        else
            return fileName.substring(lastDot + 1);
    }

}