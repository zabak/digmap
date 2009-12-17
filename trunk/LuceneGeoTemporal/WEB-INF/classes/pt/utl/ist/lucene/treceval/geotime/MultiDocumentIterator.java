package pt.utl.ist.lucene.treceval.geotime;

import org.apache.log4j.Logger;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 16:14:28
 * @email machadofisher@gmail.com
 */
public class MultiDocumentIterator implements Runnable{

    private static final Logger logger = Logger.getLogger(MultiDocumentIterator.class);
    String path;


    public MultiDocumentIterator(String path) {
        this.path = path;
    }

    public void run()
    {
        try {
            String [] args = new String[]{path};
            DocumentIterator.main(args);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public static void main(String [] args) throws InterruptedException {

//        String path = "D:\\Servidores\\DATA\\ntcir";
//        String path = "F:\\coleccoesIR\\ntcir\\data";
//        args = new String[] {path, "nyt_eng_200501.gz"};
        String path = null;
        String startFile = null;
        if(args!=null)
            path = args[0];
        if(args.length > 1)
            startFile = args[1];

        File d = new File(path);
        File[] files = d.listFiles();
        Arrays.sort( files, new Comparator()
        {
            public int compare(final Object o1, final Object o2) {
                return ((File)o1).getName().compareTo(((File) o2).getName());
            }
        });

        for(File f:files)
        {
            if(f.isFile() && f.getName().endsWith(".gz"))
            {
                if(startFile == null || f.getName().compareTo(startFile) >= 0)
                {
                    System.out.println("STARTING WITH FILE: " + f.getName());
                    startFile = null;
                    MultiDocumentIterator multiDocumentIterator = new MultiDocumentIterator(f.getAbsolutePath());
                    multiDocumentIterator.run();
//                    Thread t = new Thread(multiDocumentIterator);
//                    t.join();
//                    logger.warn("Starting thread with: " + f.getAbsolutePath());
//                    t.start();
                }
                else
                    System.out.println("SKIPING FILE: " + f.getName());

            }
        }
    }
}
