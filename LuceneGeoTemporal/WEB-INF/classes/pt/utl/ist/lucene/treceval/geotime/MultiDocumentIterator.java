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


    public static List<String>  failIds = null;
//    public static List<String> failIds =
//                    Arrays.asList(("NYT_ENG_20021129.0105\n" +
//                            "NYT_ENG_20021202.0008\n" +
//                            "NYT_ENG_20021213.0239\n" +
//                            "NYT_ENG_20021231.0005\n" +
//                            "NYT_ENG_20020601.0175\n" +
//                            "NYT_ENG_20020604.0027\n" +
//                            "NYT_ENG_20020613.0100\n" +
//                            "NYT_ENG_20020621.0092\n" +
//                            "NYT_ENG_20020630.0036\n" +
//                            "NYT_ENG_20020724.0020\n" +
//                            "NYT_ENG_20020804.0066\n" +
//                            "NYT_ENG_20020817.0022\n" +
//                            "NYT_ENG_20020912.0093\n" +
//                            "NYT_ENG_20020913.0285\n" +
//                            "NYT_ENG_20021009.0170\n" +
//                            "NYT_ENG_20021011.0014\n" +
//                            "NYT_ENG_20021021.0195\n" +
//                            "NYT_ENG_20021025.0370\n" +
//                            "NYT_ENG_20021104.0286\n" +
//                            "NYT_ENG_20021125.0006\n" +
//
//                            "NYT_ENG_20020203.0189\n" +
//                            "NYT_ENG_20020204.0010\n" +
//                            "NYT_ENG_20020213.0427\n" +
//                            "NYT_ENG_20020216.0225\n" +
//                            "NYT_ENG_20020221.0438\n" +
//                            "NYT_ENG_20020222.0427\n" +
//                            "NYT_ENG_20020226.0251\n" +
//                            "NYT_ENG_20020228.0388\n" +
//                            "NYT_ENG_20020305.0155\n" +
//                            "NYT_ENG_20020314.0459\n" +
//                            "NYT_ENG_20020318.0033\n" +
//                            "NYT_ENG_20020319.0036\n" +
//                            "NYT_ENG_20020319.0041\n" +
//                            "NYT_ENG_20020322.0048\n" +
//                            "NYT_ENG_20020402.0209\n" +
//                            "NYT_ENG_20020403.0115\n" +
//                            "NYT_ENG_20020411.0440\n" +
//                            "NYT_ENG_20020412.0028\n" +
//                            "NYT_ENG_20020413.0021\n" +
//                            "NYT_ENG_20020416.0143\n" +
//                            "NYT_ENG_20020418.0417\n" +
//                            "NYT_ENG_20020419.0047\n" +
//                            "NYT_ENG_20020429.0387\n" +
//                            "NYT_ENG_20020429.0391\n" +
//                            "NYT_ENG_20020501.0121\n" +
//                            "NYT_ENG_20020501.0404\n" +
//                            "NYT_ENG_20020502.0071\n" +
//                            "NYT_ENG_20020502.0449\n" +
//                            "NYT_ENG_20020502.0453\n" +
//                            "NYT_ENG_20020503.0460\n" +
//                            "NYT_ENG_20020505.0119\n" +
//                            "NYT_ENG_20020507.0146\n" +
//                            "NYT_ENG_20020508.0327\n" +
//                            "NYT_ENG_20020509.0039\n" +
//                            "NYT_ENG_20020509.0437\n" +
//                            "NYT_ENG_20020517.0465\n" +
//                            "NYT_ENG_20020520.0102\n" +
//                            "NYT_ENG_20020525.0066\n" +
//                            "NYT_ENG_20020525.0164\n" +
//                            "NYT_ENG_20020525.0178\n" +
//                            "NYT_ENG_20020601.0175\n" +
//                            "NYT_ENG_20020604.0027\n" +
//                            "NYT_ENG_20020613.0100").split("\n"));


    private static final Logger logger = Logger.getLogger(MultiDocumentIterator.class);
    String path;
    String mode;
    String url = null;
    String startDocument = null;


    public MultiDocumentIterator(String path,String mode, String url, String startDocument) {
        this.path = path;
        this.mode = mode;
        this.url = url;
        this.startDocument = startDocument;
    }

    public void run()
    {
        try {
            String [] args;
            if(url != null && startDocument == null)
                args = new String[]{path,mode,url};
            else if(url == null && startDocument == null)
                args = new String[]{path,mode};
            else if(url != null)
                args = new String[]{path,mode,url,startDocument};
            else
                args = new String[]{path,mode,startDocument};

            DocumentIterator.main(args);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public static void main(String [] args) throws InterruptedException {

//        String path = "D:\\Servidores\\DATA\\ntcir\\2002";
//        String path = "F:\\coleccoesIR\\ntcir\\data";
//        String url = "http://deptal.estgp.pt:9090/jmachado/TIMEXTAG/index.php";
//        String url = "http://192.168.1.66/jmachado/TIMEXTAG/index.php";
//        args = new String[] {path,url};
//        args = new String[] {path,url,"nyt_eng_200201.gz"};
//        String path = null;
        String startFile = null;
        String path;
        String url;
        String startDocument = null;
        if(args!=null)
        {
            path = args[0];
            url = args[1];
            if(path.startsWith("\""))
                path = path.substring(1,path.length()-1);
            if(url.startsWith("\""))
                url = url.substring(1,url.length()-1);
        }
        else
        {
            logger.fatal("No arguments, use: path url startfile");
            return;
        }

        if(args.length > 2)
        {
            startFile = args[2];
            if(startFile.startsWith("\""))
                startFile = startFile.substring(1,startFile.length()-1);
        }
        if(args.length > 3)
        {
            startDocument = args[3];
            if(startDocument.startsWith("\""))
                startDocument = startDocument.substring(1,startDocument.length()-1);
        }


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
                    MultiDocumentIterator multiDocumentIterator = new MultiDocumentIterator(f.getAbsolutePath(),"timextag",url,startDocument);
                    startDocument = null;//por a null depois da primeira chamada pois so conta no primeiro ficheiro
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
