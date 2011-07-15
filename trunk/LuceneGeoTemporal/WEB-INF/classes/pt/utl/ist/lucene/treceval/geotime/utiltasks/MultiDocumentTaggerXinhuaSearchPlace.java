package pt.utl.ist.lucene.treceval.geotime.utiltasks;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jorge Machado
 * @date 31/Dez/2009
 * @time 13:25:50
 * @email machadofisher@gmail.com
 */
public class MultiDocumentTaggerXinhuaSearchPlace implements Runnable{
    private static final Logger logger = Logger.getLogger(MultiDocumentTaggerXinhuaSearchPlace.class);

    String path;
    String mode;
    String url = null;
    String startDocument = null;


    public MultiDocumentTaggerXinhuaSearchPlace(String path,String mode, String url, String startDocument) {
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

            DocumentTagger.main(args);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public static void main(String [] args) throws InterruptedException
    {

        String path = "D:\\Servidores\\DATA\\ntcir\\NOVAS\\koreatimes\\M2";
//        String path = "F:\\coleccoesIR\\ntcir\\data";
        
        String url = "http://192.168.1.253:8080/jmachado/TIMEXTAG1/index.php";
//        String url = "http://192.168.1.66/jmachado/TIMEXTAG/index.php";
//        args = new String[] {path,url};
        args = new String[] {path,url};
//        String path = null;
        String startFile = null;
//        String path;
//        String url;
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
                    MultiDocumentTaggerXinhuaSearchPlace multiDocumentIterator = new MultiDocumentTaggerXinhuaSearchPlace(f.getAbsolutePath(),"timextag",url,startDocument);
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

        public static List<String>  failIds = null;
//    public static List<String> failIds =
//                    Arrays.asList(("NYT_ENG_20020203.0189\n" +
//                            "NYT_ENG_20020204.0010\n" +
//                            "NYT_ENG_20020213.0427\n" +
//                            "NYT_ENG_20020216.0225\n" +
//                            "NYT_ENG_20020221.0438\n" +
//                            "NYT_ENG_20020222.0427\n" +
//                            "NYT_ENG_20020226.0251\n" +
//                            "NYT_ENG_20020228.0388\n" +
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
//                            "NYT_ENG_20020613.0100\n" +
//                            "NYT_ENG_20020621.0092\n" +
//                            "NYT_ENG_20020630.0036\n" +
//                            "NYT_ENG_20020724.0020\n" +
//                            "NYT_ENG_20020802.0315\n" +
//                            "NYT_ENG_20020802.0316\n" +
//                            "NYT_ENG_20020802.0317\n" +
//                            "NYT_ENG_20020802.0318\n" +
//                            "NYT_ENG_20020802.0319\n" +
//                            "NYT_ENG_20020802.0320\n" +
//                            "NYT_ENG_20020802.0321\n" +
//                            "NYT_ENG_20020802.0322\n" +
//                            "NYT_ENG_20020802.0323\n" +
//                            "NYT_ENG_20020802.0324\n" +
//                            "NYT_ENG_20020802.0325\n" +
//                            "NYT_ENG_20020802.0326\n" +
//                            "NYT_ENG_20020802.0327\n" +
//                            "NYT_ENG_20020802.0328\n" +
//                            "NYT_ENG_20020802.0329\n" +
//                            "NYT_ENG_20020802.0330\n" +
//                            "NYT_ENG_20020802.0331\n" +
//                            "NYT_ENG_20020802.0332\n" +
//                            "NYT_ENG_20020802.0333\n" +
//                            "NYT_ENG_20020803.0029\n" +
//                            "NYT_ENG_20020803.0030\n" +
//                            "NYT_ENG_20020803.0031\n" +
//                            "NYT_ENG_20020803.0032\n" +
//                            "NYT_ENG_20020803.0033\n" +
//                            "NYT_ENG_20020803.0034\n" +
//                            "NYT_ENG_20020803.0035\n" +
//                            "NYT_ENG_20020803.0036\n" +
//                            "NYT_ENG_20020803.0037\n" +
//                            "NYT_ENG_20020803.0038\n" +
//                            "NYT_ENG_20020803.0039\n" +
//                            "NYT_ENG_20020803.0040\n" +
//                            "NYT_ENG_20020803.0041\n" +
//                            "NYT_ENG_20020803.0042\n" +
//                            "NYT_ENG_20020803.0043\n" +
//                            "NYT_ENG_20020803.0044\n" +
//                            "NYT_ENG_20020803.0045\n" +
//                            "NYT_ENG_20020803.0046\n" +
//                            "NYT_ENG_20020803.0047\n" +
//                            "NYT_ENG_20020803.0048\n" +
//                            "NYT_ENG_20020803.0049\n" +
//                            "NYT_ENG_20020803.0050\n" +
//                            "NYT_ENG_20020803.0051\n" +
//                            "NYT_ENG_20020803.0052\n" +
//                            "NYT_ENG_20020803.0053\n" +
//                            "NYT_ENG_20020803.0054\n" +
//                            "NYT_ENG_20020803.0055\n" +
//                            "NYT_ENG_20020803.0056\n" +
//                            "NYT_ENG_20020803.0057\n" +
//                            "NYT_ENG_20020803.0058\n" +
//                            "NYT_ENG_20020803.0059\n" +
//                            "NYT_ENG_20020803.0060\n" +
//                            "NYT_ENG_20020803.0061\n" +
//                            "NYT_ENG_20020803.0062\n" +
//                            "NYT_ENG_20020803.0063\n" +
//                            "NYT_ENG_20020803.0064\n" +
//                            "NYT_ENG_20020803.0065\n" +
//                            "NYT_ENG_20020803.0066\n" +
//                            "NYT_ENG_20020803.0067\n" +
//                            "NYT_ENG_20020803.0068\n" +
//                            "NYT_ENG_20020803.0069\n" +
//                            "NYT_ENG_20020803.0070\n" +
//                            "NYT_ENG_20020804.0066\n" +
//                            "NYT_ENG_20020805.0325\n" +
//                            "NYT_ENG_20020805.0326\n" +
//                            "NYT_ENG_20020805.0327\n" +
//                            "NYT_ENG_20020805.0328\n" +
//                            "NYT_ENG_20020805.0329\n" +
//                            "NYT_ENG_20020805.0330\n" +
//                            "NYT_ENG_20020805.0331\n" +
//                            "NYT_ENG_20020806.0001\n" +
//                            "NYT_ENG_20020806.0002\n" +
//                            "NYT_ENG_20020806.0003\n" +
//                            "NYT_ENG_20020806.0004\n" +
//                            "NYT_ENG_20020806.0005\n" +
//                            "NYT_ENG_20020806.0006\n" +
//                            "NYT_ENG_20020806.0007\n" +
//                            "NYT_ENG_20020806.0008\n" +
//                            "NYT_ENG_20020806.0009\n" +
//                            "NYT_ENG_20020806.0010\n" +
//                            "NYT_ENG_20020806.0011\n" +
//                            "NYT_ENG_20020806.0012\n" +
//                            "NYT_ENG_20020806.0013\n" +
//                            "NYT_ENG_20020806.0014\n" +
//                            "NYT_ENG_20020806.0015\n" +
//                            "NYT_ENG_20020806.0016\n" +
//                            "NYT_ENG_20020806.0017\n" +
//                            "NYT_ENG_20020806.0018\n" +
//                            "NYT_ENG_20020806.0019\n" +
//                            "NYT_ENG_20020806.0020\n" +
//                            "NYT_ENG_20020806.0021\n" +
//                            "NYT_ENG_20020806.0022\n" +
//                            "NYT_ENG_20020806.0023\n" +
//                            "NYT_ENG_20020806.0024\n" +
//                            "NYT_ENG_20020806.0025\n" +
//                            "NYT_ENG_20020806.0026\n" +
//                            "NYT_ENG_20020806.0027\n" +
//                            "NYT_ENG_20020806.0028\n" +
//                            "NYT_ENG_20020806.0029\n" +
//                            "NYT_ENG_20020806.0030\n" +
//                            "NYT_ENG_20020806.0031\n" +
//                            "NYT_ENG_20020806.0032\n" +
//                            "NYT_ENG_20020806.0033\n" +
//                            "NYT_ENG_20020806.0034\n" +
//                            "NYT_ENG_20020806.0035\n" +
//                            "NYT_ENG_20020806.0036\n" +
//                            "NYT_ENG_20020806.0037\n" +
//                            "NYT_ENG_20020806.0038\n" +
//                            "NYT_ENG_20020806.0039\n" +
//                            "NYT_ENG_20020806.0040\n" +
//                            "NYT_ENG_20020806.0041\n" +
//                            "NYT_ENG_20020806.0042\n" +
//                            "NYT_ENG_20020806.0043\n" +
//                            "NYT_ENG_20020806.0044\n" +
//                            "NYT_ENG_20020806.0045\n" +
//                            "NYT_ENG_20020806.0046\n" +
//                            "NYT_ENG_20020806.0047\n" +
//                            "NYT_ENG_20020806.0048\n" +
//                            "NYT_ENG_20020806.0049\n" +
//                            "NYT_ENG_20020806.0050\n" +
//                            "NYT_ENG_20020806.0051\n" +
//                            "NYT_ENG_20020806.0052\n" +
//                            "NYT_ENG_20020806.0053\n" +
//                            "NYT_ENG_20020806.0054\n" +
//                            "NYT_ENG_20020806.0055\n" +
//                            "NYT_ENG_20020806.0056\n" +
//                            "NYT_ENG_20020806.0057\n" +
//                            "NYT_ENG_20020806.0058\n" +
//                            "NYT_ENG_20020806.0059\n" +
//                            "NYT_ENG_20020806.0060\n" +
//                            "NYT_ENG_20020806.0061\n" +
//                            "NYT_ENG_20020806.0062\n" +
//                            "NYT_ENG_20020806.0063\n" +
//                            "NYT_ENG_20020806.0064\n" +
//                            "NYT_ENG_20020806.0065\n" +
//                            "NYT_ENG_20020806.0066\n" +
//                            "NYT_ENG_20020806.0067\n" +
//                            "NYT_ENG_20020806.0068\n" +
//                            "NYT_ENG_20020806.0069\n" +
//                            "NYT_ENG_20020806.0070\n" +
//                            "NYT_ENG_20020806.0071\n" +
//                            "NYT_ENG_20020806.0072\n" +
//                            "NYT_ENG_20020806.0073\n" +
//                            "NYT_ENG_20020806.0074\n" +
//                            "NYT_ENG_20020806.0075\n" +
//                            "NYT_ENG_20020806.0076\n" +
//                            "NYT_ENG_20020806.0077\n" +
//                            "NYT_ENG_20020806.0078\n" +
//                            "NYT_ENG_20020806.0079\n" +
//                            "NYT_ENG_20020806.0080\n" +
//                            "NYT_ENG_20020806.0081\n" +
//                            "NYT_ENG_20020806.0082\n" +
//                            "NYT_ENG_20020806.0083\n" +
//                            "NYT_ENG_20020806.0084\n" +
//                            "NYT_ENG_20020806.0085\n" +
//                            "NYT_ENG_20020806.0086\n" +
//                            "NYT_ENG_20020806.0087\n" +
//                            "NYT_ENG_20020806.0088\n" +
//                            "NYT_ENG_20020806.0089\n" +
//                            "NYT_ENG_20020806.0090\n" +
//                            "NYT_ENG_20020806.0091\n" +
//                            "NYT_ENG_20020806.0092\n" +
//                            "NYT_ENG_20020817.0022\n" +
//                            "NYT_ENG_20020912.0093\n" +
//                            "NYT_ENG_20020913.0285\n" +
//                            "NYT_ENG_20021009.0170\n" +
//                            "NYT_ENG_20021011.0014\n" +
//                            "NYT_ENG_20021021.0195\n" +
//                            "NYT_ENG_20021025.0370\n" +
//                            "NYT_ENG_20021104.0286\n" +
//                            "NYT_ENG_20021125.0006\n" +
//                            "NYT_ENG_20021129.0105\n" +
//                            "NYT_ENG_20021202.0008\n" +
//                            "NYT_ENG_20021213.0239\n" +
//                            "NYT_ENG_20021231.0005\n" +
//                            "NYT_ENG_20030104.0170\n" +
//                            "NYT_ENG_20030128.0025\n" +
//                            "NYT_ENG_20030129.0057\n" +
//                            "NYT_ENG_20030203.0029\n" +
//                            "NYT_ENG_20030204.0050\n" +
//                            "NYT_ENG_20030211.0010\n" +
//                            "NYT_ENG_20030211.0033\n" +
//                            "NYT_ENG_20030211.0069\n" +
//                            "NYT_ENG_20030212.0035\n" +
//                            "NYT_ENG_20030216.0027\n" +
//                            "NYT_ENG_20030219.0068\n" +
//                            "NYT_ENG_20030221.0028\n" +
//                            "NYT_ENG_20030221.0068\n" +
//                            "NYT_ENG_20030222.0012\n" +
//                            "NYT_ENG_20030222.0040\n" +
//                            "NYT_ENG_20030223.0022\n" +
//                            "NYT_ENG_20030224.0077\n" +
//                            "NYT_ENG_20030226.0025\n" +
//                            "NYT_ENG_20030226.0057\n" +
//                            "NYT_ENG_20030227.0080\n" +
//                            "NYT_ENG_20030228.0001\n" +
//                            "NYT_ENG_20030306.0065\n" +
//                            "NYT_ENG_20030306.0071\n" +
//                            "NYT_ENG_20030309.0035\n" +
//                            "NYT_ENG_20030312.0058\n" +
//                            "NYT_ENG_20030314.0018\n" +
//                            "NYT_ENG_20030315.0020\n" +
//                            "NYT_ENG_20030316.0016\n" +
//                            "NYT_ENG_20030318.0071\n" +
//                            "NYT_ENG_20030319.0002\n" +
//                            "NYT_ENG_20030319.0007\n" +
//                            "NYT_ENG_20030320.0047\n" +
//                            "NYT_ENG_20030322.0025\n" +
//                            "NYT_ENG_20030327.0058\n" +
//                            "NYT_ENG_20030331.0041\n" +
//                            "NYT_ENG_20030331.0045\n" +
//                            "NYT_ENG_20030331.0051\n" +
//                            "NYT_ENG_20030401.0023\n" +
//                            "NYT_ENG_20030401.0087\n" +
//                            "NYT_ENG_20030407.0045\n" +
//                            "NYT_ENG_20030407.0075\n" +
//                            "NYT_ENG_20030410.0017\n" +
//                            "NYT_ENG_20030414.0037\n" +
//                            "NYT_ENG_20030415.0050\n" +
//                            "NYT_ENG_20030418.0041\n" +
//                            "NYT_ENG_20030421.0060\n" +
//                            "NYT_ENG_20030424.0014\n" +
//                            "NYT_ENG_20030424.0082\n" +
//                            "NYT_ENG_20030426.0002\n" +
//                            "NYT_ENG_20030428.0036\n" +
//                            "NYT_ENG_20030429.0053\n" +
//                            "NYT_ENG_20030430.0048\n" +
////                            "NYT_ENG_20030515.0027\n" +
//                            "NYT_ENG_20030606.0052\n" +
//                            "NYT_ENG_20030716.0021\n" +
//                            "NYT_ENG_20030724.0029\n" +
//                            "NYT_ENG_20030807.0011\n" +
////                            "NYT_ENG_20030819.0042\n" +
//                            "NYT_ENG_20030924.0044\n" +
//                            "NYT_ENG_20031001.0040\n" +
//                            "NYT_ENG_20031003.0028\n" +
//                            "NYT_ENG_20031003.0086\n" +
//                            "NYT_ENG_20031006.0031\n" +
//                            "NYT_ENG_20031013.0023\n" +
//                            "NYT_ENG_20031016.0081\n" +
//                            "NYT_ENG_20031018.0018\n" +
//                            "NYT_ENG_20031020.0027\n" +
//                            "NYT_ENG_20031023.0020\n" +
//                            "NYT_ENG_20031023.0051\n" +
//                            "NYT_ENG_20031024.0068\n" +
//                            "NYT_ENG_20031029.0021\n" +
//                            "NYT_ENG_20031029.0060\n" +
//                            "NYT_ENG_20031030.0062\n" +
////                            "NYT_ENG_20031121.0059\n" +
////                            "NYT_ENG_20031217.0030\n" +
////                            "NYT_ENG_20031217.0031\n" +
//                            "NYT_ENG_20040101.0018\n" +
//                            "NYT_ENG_20040102.0040\n" +
//                            "NYT_ENG_20040103.0009\n" +
//                            "NYT_ENG_20040105.0077\n" +
//                            "NYT_ENG_20040105.0080\n" +
//                            "NYT_ENG_20040107.0043\n" +
//                            "NYT_ENG_20040107.0080\n" +
//                            "NYT_ENG_20040108.0010\n" +
//                            "NYT_ENG_20040108.0075\n" +
//                            "NYT_ENG_20040109.0016\n" +
//                            "NYT_ENG_20040109.0073\n" +
//                            "NYT_ENG_20040113.0025\n" +
//                            "NYT_ENG_20040114.0047\n" +
//                            "NYT_ENG_20040116.0014\n" +
//                            "NYT_ENG_20040116.0050\n" +
//                            "NYT_ENG_20040118.0005\n" +
//                            "NYT_ENG_20040118.0015\n" +
//                            "NYT_ENG_20040119.0031\n" +
//                            "NYT_ENG_20040121.0020\n" +
//                            "NYT_ENG_20040121.0026\n" +
//                            "NYT_ENG_20040125.0022\n" +
//                            "NYT_ENG_20040126.0089\n" +
//                            "NYT_ENG_20040128.0017\n" +
//                            "NYT_ENG_20040201.0035\n" +
//                            "NYT_ENG_20040202.0017\n" +
//                            "NYT_ENG_20040203.0015\n" +
//                            "NYT_ENG_20040204.0053\n" +
//                            "NYT_ENG_20040211.0058\n" +
//                            "NYT_ENG_20040212.0008\n" +
//                            "NYT_ENG_20040212.0045\n" +
//                            "NYT_ENG_20040213.0032\n" +
//                            "NYT_ENG_20040214.0005\n" +
//                            "NYT_ENG_20040214.0007\n" +
//                            "NYT_ENG_20040219.0033\n" +
//                            "NYT_ENG_20040219.0034\n" +
//                            "NYT_ENG_20040220.0043\n" +
//                            "NYT_ENG_20040225.0011\n" +
//                            "NYT_ENG_20040226.0017\n" +
//                            "NYT_ENG_20040227.0038\n" +
//                            "NYT_ENG_20040228.0011\n" +
//                            "NYT_ENG_20040228.0026\n" +
//                            "NYT_ENG_20040229.0012\n" +
//                            "NYT_ENG_20040229.0043\n" +
//                            "NYT_ENG_20040301.0080\n" +
//                            "NYT_ENG_20040304.0058\n" +
//                            "NYT_ENG_20040305.0012\n" +
//                            "NYT_ENG_20040305.0050\n" +
//                            "NYT_ENG_20040308.0021\n" +
//                            "NYT_ENG_20040310.0076\n" +
//                            "NYT_ENG_20040310.0083\n" +
//                            "NYT_ENG_20040312.0010\n" +
//                            "NYT_ENG_20040317.0027\n" +
//                            "NYT_ENG_20040317.0065\n" +
//                            "NYT_ENG_20040319.0016\n" +
//                            "NYT_ENG_20040321.0020\n" +
//                            "NYT_ENG_20040323.0036\n" +
//                            "NYT_ENG_20040324.0024\n" +
//                            "NYT_ENG_20040324.0044\n" +
//                            "NYT_ENG_20040326.0051\n" +
//                            "NYT_ENG_20040329.0053\n" +
//                            "NYT_ENG_20040401.0032\n" +
//                            "NYT_ENG_20040401.0041\n" +
//                            "NYT_ENG_20040402.0039\n" +
//                            "NYT_ENG_20040403.0008\n" +
//                            "NYT_ENG_20040404.0044\n" +
//                            "NYT_ENG_20040405.0024\n" +
//                            "NYT_ENG_20040407.0033\n" +
//                            "NYT_ENG_20040413.0027\n" +
//                            "NYT_ENG_20040415.0057\n" +
//                            "NYT_ENG_20040415.0073\n" +
//                            "NYT_ENG_20040416.0042\n" +
//                            "NYT_ENG_20040421.0060\n" +
//                            "NYT_ENG_20040502.0033\n" +
//                            "NYT_ENG_20040505.0016\n" +
//                            "NYT_ENG_20040715.0021\n" +
//                            "NYT_ENG_20040720.0256\n" +
//                            "NYT_ENG_20040725.0190\n" +
//                            "NYT_ENG_20040728.0326\n" +
//                            "NYT_ENG_20040729.0006\n" +
//                            "NYT_ENG_20040801.0105\n" +
//                            "NYT_ENG_20040805.0046\n" +
//                            "NYT_ENG_20040806.0095\n" +
//                            "NYT_ENG_20040813.0142\n" +
//                            "NYT_ENG_20040813.0284\n" +
//                            "NYT_ENG_20040820.0095\n" +
//                            "NYT_ENG_20040901.0058\n" +
//                            "NYT_ENG_20040909.0383\n" +
//                            "NYT_ENG_20040917.0244\n" +
//                            "NYT_ENG_20041004.0425\n" +
//                            "NYT_ENG_20041007.0223\n" +
//                            "NYT_ENG_20041022.0273\n" +
//                            "NYT_ENG_20041101.0241\n" +
//                            "NYT_ENG_20041103.0217\n" +
//                            "NYT_ENG_20041103.0218\n" +
//                            "NYT_ENG_20041103.0219\n" +
//                            "NYT_ENG_20041103.0220\n" +
//                            "NYT_ENG_20041103.0221\n" +
//                            "NYT_ENG_20041103.0222\n" +
//                            "NYT_ENG_20041103.0223\n" +
//                            "NYT_ENG_20041103.0224\n" +
//                            "NYT_ENG_20041103.0225\n" +
//                            "NYT_ENG_20041103.0226\n" +
//                            "NYT_ENG_20041103.0227\n" +
//                            "NYT_ENG_20041103.0228\n" +
//                            "NYT_ENG_20041103.0229\n" +
//                            "NYT_ENG_20041103.0230\n" +
//                            "NYT_ENG_20041103.0231\n" +
//                            "NYT_ENG_20041103.0232\n" +
//                            "NYT_ENG_20041103.0233\n" +
//                            "NYT_ENG_20041103.0234\n" +
//                            "NYT_ENG_20041103.0235\n" +
//                            "NYT_ENG_20041103.0236\n" +
//                            "NYT_ENG_20041103.0237\n" +
//                            "NYT_ENG_20041103.0238\n" +
//                            "NYT_ENG_20041103.0239\n" +
//                            "NYT_ENG_20041103.0240\n" +
//                            "NYT_ENG_20041103.0241\n" +
//                            "NYT_ENG_20041103.0242\n" +
//                            "NYT_ENG_20041103.0243\n" +
//                            "NYT_ENG_20041103.0244\n" +
//                            "NYT_ENG_20041103.0245\n" +
//                            "NYT_ENG_20041103.0246\n" +
//                            "NYT_ENG_20041103.0247\n" +
//                            "NYT_ENG_20041103.0248\n" +
//                            "NYT_ENG_20041103.0249\n" +
//                            "NYT_ENG_20041103.0250\n" +
//                            "NYT_ENG_20041103.0251\n" +
//                            "NYT_ENG_20041103.0252\n" +
//                            "NYT_ENG_20041103.0253\n" +
//                            "NYT_ENG_20041103.0254\n" +
//                            "NYT_ENG_20041112.0231\n" +
//                            "NYT_ENG_20041115.0100\n" +
//                            "NYT_ENG_20041115.0254\n" +
//                            "NYT_ENG_20041120.0124\n" +
//                            "NYT_ENG_20041121.0003\n" +
//                            "NYT_ENG_20041129.0062\n" +
//                            "NYT_ENG_20041201.0145\n" +
//                            "NYT_ENG_20041218.0109\n" +
//                            "NYT_ENG_20041223.0265\n" +
//                            "NYT_ENG_20050106.0227\n" +
//                            "NYT_ENG_20050111.0153\n" +
//                            "NYT_ENG_20050111.0294\n" +
//                            "NYT_ENG_20050112.0338\n" +
//                            "NYT_ENG_20050112.0339\n" +
//                            "NYT_ENG_20050112.0340\n" +
//                            "NYT_ENG_20050112.0341\n" +
//                            "NYT_ENG_20050112.0342\n" +
//                            "NYT_ENG_20050112.0343\n" +
//                            "NYT_ENG_20050112.0344\n" +
//                            "NYT_ENG_20050112.0345\n" +
//                            "NYT_ENG_20050112.0346\n" +
//                            "NYT_ENG_20050112.0347\n" +
//                            "NYT_ENG_20050112.0348\n" +
//                            "NYT_ENG_20050112.0349\n" +
//                            "NYT_ENG_20050112.0350\n" +
//                            "NYT_ENG_20050112.0351\n" +
//                            "NYT_ENG_20050112.0352\n" +
//                            "NYT_ENG_20050112.0353\n" +
//                            "NYT_ENG_20050112.0354\n" +
//                            "NYT_ENG_20050112.0355\n" +
//                            "NYT_ENG_20050112.0356\n" +
//                            "NYT_ENG_20050112.0357\n" +
//                            "NYT_ENG_20050112.0358\n" +
//                            "NYT_ENG_20050112.0359\n" +
//                            "NYT_ENG_20050112.0360\n" +
//                            "NYT_ENG_20050112.0361\n" +
//                            "NYT_ENG_20050112.0362\n" +
//                            "NYT_ENG_20050112.0363\n" +
//                            "NYT_ENG_20050112.0364\n" +
//                            "NYT_ENG_20050112.0365\n" +
//                            "NYT_ENG_20050112.0366\n" +
//                            "NYT_ENG_20050112.0367\n" +
//                            "NYT_ENG_20050112.0368\n" +
//                            "NYT_ENG_20050112.0369\n" +
//                            "NYT_ENG_20050113.0001\n" +
//                            "NYT_ENG_20050113.0002\n" +
//                            "NYT_ENG_20050113.0003\n" +
//                            "NYT_ENG_20050113.0004\n" +
//                            "NYT_ENG_20050113.0005\n" +
//                            "NYT_ENG_20050113.0006\n" +
//                            "NYT_ENG_20050113.0007\n" +
//                            "NYT_ENG_20050113.0008\n" +
//                            "NYT_ENG_20050113.0009\n" +
//                            "NYT_ENG_20050113.0010\n" +
//                            "NYT_ENG_20050113.0011\n" +
//                            "NYT_ENG_20050113.0012\n" +
//                            "NYT_ENG_20050113.0013\n" +
//                            "NYT_ENG_20050113.0014\n" +
//                            "NYT_ENG_20050113.0015\n" +
//                            "NYT_ENG_20050113.0016\n" +
//                            "NYT_ENG_20050113.0017\n" +
//                            "NYT_ENG_20050113.0018\n" +
//                            "NYT_ENG_20050113.0019\n" +
//                            "NYT_ENG_20050113.0020\n" +
//                            "NYT_ENG_20050113.0021\n" +
//                            "NYT_ENG_20050113.0022\n" +
//                            "NYT_ENG_20050113.0023\n" +
//                            "NYT_ENG_20050113.0024\n" +
//                            "NYT_ENG_20050113.0025\n" +
//                            "NYT_ENG_20050113.0026\n" +
//                            "NYT_ENG_20050113.0027\n" +
//                            "NYT_ENG_20050113.0028\n" +
//                            "NYT_ENG_20050113.0029\n" +
//                            "NYT_ENG_20050113.0030\n" +
//                            "NYT_ENG_20050113.0031\n" +
//                            "NYT_ENG_20050113.0032\n" +
//                            "NYT_ENG_20050113.0033\n" +
//                            "NYT_ENG_20050113.0034\n" +
//                            "NYT_ENG_20050113.0035\n" +
//                            "NYT_ENG_20050113.0036\n" +
//                            "NYT_ENG_20050113.0037\n" +
//                            "NYT_ENG_20050113.0038\n" +
//                            "NYT_ENG_20050113.0039\n" +
//                            "NYT_ENG_20050113.0040\n" +
//                            "NYT_ENG_20050113.0041\n" +
//                            "NYT_ENG_20050113.0042\n" +
//                            "NYT_ENG_20050113.0043\n" +
//                            "NYT_ENG_20050113.0044\n" +
//                            "NYT_ENG_20050113.0045\n" +
//                            "NYT_ENG_20050113.0046\n" +
//                            "NYT_ENG_20050113.0047\n" +
//                            "NYT_ENG_20050113.0048\n" +
//                            "NYT_ENG_20050113.0049\n" +
//                            "NYT_ENG_20050113.0050\n" +
//                            "NYT_ENG_20050113.0051\n" +
//                            "NYT_ENG_20050113.0052\n" +
//                            "NYT_ENG_20050113.0053\n" +
//                            "NYT_ENG_20050113.0054\n" +
//                            "NYT_ENG_20050113.0055\n" +
//                            "NYT_ENG_20050113.0056\n" +
//                            "NYT_ENG_20050113.0057\n" +
//                            "NYT_ENG_20050113.0058\n" +
//                            "NYT_ENG_20050113.0059\n" +
//                            "NYT_ENG_20050113.0060\n" +
//                            "NYT_ENG_20050113.0061\n" +
//                            "NYT_ENG_20050113.0062\n" +
//                            "NYT_ENG_20050113.0063\n" +
//                            "NYT_ENG_20050113.0064\n" +
//                            "NYT_ENG_20050113.0065\n" +
//                            "NYT_ENG_20050113.0066\n" +
//                            "NYT_ENG_20050113.0067\n" +
//                            "NYT_ENG_20050113.0068\n" +
//                            "NYT_ENG_20050113.0069\n" +
//                            "NYT_ENG_20050113.0070\n" +
//                            "NYT_ENG_20050113.0071\n" +
//                            "NYT_ENG_20050113.0072\n" +
//                            "NYT_ENG_20050113.0073\n" +
//                            "NYT_ENG_20050113.0074\n" +
//                            "NYT_ENG_20050113.0075\n" +
//                            "NYT_ENG_20050113.0076\n" +
//                            "NYT_ENG_20050113.0077\n" +
//                            "NYT_ENG_20050113.0078\n" +
//                            "NYT_ENG_20050113.0079\n" +
//                            "NYT_ENG_20050113.0080\n" +
//                            "NYT_ENG_20050113.0081\n" +
//                            "NYT_ENG_20050113.0082\n" +
//                            "NYT_ENG_20050113.0083\n" +
//                            "NYT_ENG_20050113.0084\n" +
//                            "NYT_ENG_20050113.0085\n" +
//                            "NYT_ENG_20050113.0086\n" +
//                            "NYT_ENG_20050113.0087\n" +
//                            "NYT_ENG_20050113.0088\n" +
//                            "NYT_ENG_20050113.0089\n" +
//                            "NYT_ENG_20050113.0090\n" +
//                            "NYT_ENG_20050113.0091\n" +
//                            "NYT_ENG_20050113.0092\n" +
//                            "NYT_ENG_20050113.0093\n" +
//                            "NYT_ENG_20050113.0094\n" +
//                            "NYT_ENG_20050113.0095\n" +
//                            "NYT_ENG_20050113.0096\n" +
//                            "NYT_ENG_20050113.0097\n" +
//                            "NYT_ENG_20050113.0098\n" +
//                            "NYT_ENG_20050113.0099\n" +
//                            "NYT_ENG_20050113.0100\n" +
//                            "NYT_ENG_20050113.0101\n" +
//                            "NYT_ENG_20050113.0102\n" +
//                            "NYT_ENG_20050113.0103\n" +
//                            "NYT_ENG_20050113.0104\n" +
//                            "NYT_ENG_20050113.0105\n" +
//                            "NYT_ENG_20050113.0106\n" +
//                            "NYT_ENG_20050113.0107\n" +
//                            "NYT_ENG_20050113.0108\n" +
//                            "NYT_ENG_20050113.0109\n" +
//                            "NYT_ENG_20050113.0110\n" +
//                            "NYT_ENG_20050113.0111\n" +
//                            "NYT_ENG_20050113.0112\n" +
//                            "NYT_ENG_20050113.0113\n" +
//                            "NYT_ENG_20050113.0114\n" +
//                            "NYT_ENG_20050113.0115\n" +
//                            "NYT_ENG_20050113.0116\n" +
//                            "NYT_ENG_20050113.0117\n" +
//                            "NYT_ENG_20050113.0118\n" +
//                            "NYT_ENG_20050113.0119\n" +
//                            "NYT_ENG_20050113.0120\n" +
//                            "NYT_ENG_20050113.0121\n" +
//                            "NYT_ENG_20050113.0122\n" +
//                            "NYT_ENG_20050113.0123\n" +
//                            "NYT_ENG_20050113.0124\n" +
//                            "NYT_ENG_20050113.0125\n" +
//                            "NYT_ENG_20050113.0126\n" +
//                            "NYT_ENG_20050113.0127\n" +
//                            "NYT_ENG_20050113.0128\n" +
//                            "NYT_ENG_20050113.0129\n" +
//                            "NYT_ENG_20050113.0130\n" +
//                            "NYT_ENG_20050113.0131\n" +
//                            "NYT_ENG_20050113.0132\n" +
//                            "NYT_ENG_20050113.0133\n" +
//                            "NYT_ENG_20050113.0134\n" +
//                            "NYT_ENG_20050113.0135\n" +
//                            "NYT_ENG_20050113.0136\n" +
//                            "NYT_ENG_20050113.0137\n" +
//                            "NYT_ENG_20050113.0138\n" +
//                            "NYT_ENG_20050113.0139\n" +
//                            "NYT_ENG_20050113.0140\n" +
//                            "NYT_ENG_20050113.0141\n" +
//                            "NYT_ENG_20050113.0142\n" +
//                            "NYT_ENG_20050113.0143\n" +
//                            "NYT_ENG_20050113.0144\n" +
//                            "NYT_ENG_20050113.0145\n" +
//                            "NYT_ENG_20050113.0146\n" +
//                            "NYT_ENG_20050113.0147\n" +
//                            "NYT_ENG_20050113.0148\n" +
//                            "NYT_ENG_20050113.0149\n" +
//                            "NYT_ENG_20050113.0150\n" +
//                            "NYT_ENG_20050113.0151\n" +
//                            "NYT_ENG_20050113.0152\n" +
//                            "NYT_ENG_20050113.0153\n" +
//                            "NYT_ENG_20050113.0154\n" +
//                            "NYT_ENG_20050113.0155\n" +
//                            "NYT_ENG_20050113.0156\n" +
//                            "NYT_ENG_20050113.0157\n" +
//                            "NYT_ENG_20050113.0158\n" +
//                            "NYT_ENG_20050113.0159\n" +
//                            "NYT_ENG_20050113.0160\n" +
//                            "NYT_ENG_20050113.0161\n" +
//                            "NYT_ENG_20050113.0162\n" +
//                            "NYT_ENG_20050113.0163\n" +
//                            "NYT_ENG_20050113.0164\n" +
//                            "NYT_ENG_20050113.0165\n" +
//                            "NYT_ENG_20050113.0166\n" +
//                            "NYT_ENG_20050113.0167\n" +
//                            "NYT_ENG_20050113.0168\n" +
//                            "NYT_ENG_20050113.0169\n" +
//                            "NYT_ENG_20050113.0170\n" +
//                            "NYT_ENG_20050113.0171\n" +
//                            "NYT_ENG_20050113.0172\n" +
//                            "NYT_ENG_20050113.0173\n" +
//                            "NYT_ENG_20050113.0174\n" +
//                            "NYT_ENG_20050113.0175\n" +
//                            "NYT_ENG_20050113.0176\n" +
//                            "NYT_ENG_20050113.0177\n" +
//                            "NYT_ENG_20050113.0178\n" +
//                            "NYT_ENG_20050113.0179\n" +
//                            "NYT_ENG_20050113.0180\n" +
//                            "NYT_ENG_20050113.0181\n" +
//                            "NYT_ENG_20050113.0182\n" +
//                            "NYT_ENG_20050113.0183\n" +
//                            "NYT_ENG_20050113.0184\n" +
//                            "NYT_ENG_20050113.0185\n" +
//                            "NYT_ENG_20050113.0186\n" +
//                            "NYT_ENG_20050113.0187\n" +
//                            "NYT_ENG_20050113.0188\n" +
//                            "NYT_ENG_20050113.0189\n" +
//                            "NYT_ENG_20050113.0190\n" +
//                            "NYT_ENG_20050113.0191\n" +
//                            "NYT_ENG_20050113.0192\n" +
//                            "NYT_ENG_20050113.0193\n" +
//                            "NYT_ENG_20050113.0194\n" +
//                            "NYT_ENG_20050113.0195\n" +
//                            "NYT_ENG_20050113.0196\n" +
//                            "NYT_ENG_20050113.0197\n" +
//                            "NYT_ENG_20050113.0198\n" +
//                            "NYT_ENG_20050113.0199\n" +
//                            "NYT_ENG_20050113.0200\n" +
//                            "NYT_ENG_20050113.0201\n" +
//                            "NYT_ENG_20050113.0202\n" +
//                            "NYT_ENG_20050113.0203\n" +
//                            "NYT_ENG_20050113.0204\n" +
//                            "NYT_ENG_20050113.0205\n" +
//                            "NYT_ENG_20050113.0206\n" +
//                            "NYT_ENG_20050113.0207\n" +
//                            "NYT_ENG_20050113.0208\n" +
//                            "NYT_ENG_20050113.0209\n" +
//                            "NYT_ENG_20050113.0210\n" +
//                            "NYT_ENG_20050113.0211\n" +
//                            "NYT_ENG_20050113.0212\n" +
//                            "NYT_ENG_20050113.0213\n" +
//                            "NYT_ENG_20050113.0214\n" +
//                            "NYT_ENG_20050113.0215\n" +
//                            "NYT_ENG_20050113.0216\n" +
//                            "NYT_ENG_20050113.0217\n" +
//                            "NYT_ENG_20050113.0218\n" +
//                            "NYT_ENG_20050113.0219\n" +
//                            "NYT_ENG_20050113.0220\n" +
//                            "NYT_ENG_20050113.0221\n" +
//                            "NYT_ENG_20050113.0222\n" +
//                            "NYT_ENG_20050113.0223\n" +
//                            "NYT_ENG_20050113.0224\n" +
//                            "NYT_ENG_20050113.0225\n" +
//                            "NYT_ENG_20050113.0226\n" +
//                            "NYT_ENG_20050113.0227\n" +
//                            "NYT_ENG_20050113.0228\n" +
//                            "NYT_ENG_20050113.0229\n" +
//                            "NYT_ENG_20050113.0230\n" +
//                            "NYT_ENG_20050113.0231\n" +
//                            "NYT_ENG_20050113.0232\n" +
//                            "NYT_ENG_20050113.0233\n" +
//                            "NYT_ENG_20050113.0234\n" +
//                            "NYT_ENG_20050113.0235\n" +
//                            "NYT_ENG_20050113.0236\n" +
//                            "NYT_ENG_20050113.0237\n" +
//                            "NYT_ENG_20050113.0238\n" +
//                            "NYT_ENG_20050113.0239\n" +
//                            "NYT_ENG_20050113.0240\n" +
//                            "NYT_ENG_20050113.0241\n" +
//                            "NYT_ENG_20050113.0242\n" +
//                            "NYT_ENG_20050113.0243\n" +
//                            "NYT_ENG_20050113.0244\n" +
//                            "NYT_ENG_20050113.0245\n" +
//                            "NYT_ENG_20050113.0246\n" +
//                            "NYT_ENG_20050113.0247\n" +
//                            "NYT_ENG_20050113.0248\n" +
//                            "NYT_ENG_20050113.0249\n" +
//                            "NYT_ENG_20050113.0250\n" +
//                            "NYT_ENG_20050113.0251\n" +
//                            "NYT_ENG_20050113.0252\n" +
//                            "NYT_ENG_20050113.0253\n" +
//                            "NYT_ENG_20050113.0254\n" +
//                            "NYT_ENG_20050113.0255\n" +
//                            "NYT_ENG_20050113.0256\n" +
//                            "NYT_ENG_20050113.0257\n" +
//                            "NYT_ENG_20050113.0258\n" +
//                            "NYT_ENG_20050113.0259\n" +
//                            "NYT_ENG_20050113.0260\n" +
//                            "NYT_ENG_20050113.0261\n" +
//                            "NYT_ENG_20050113.0262\n" +
//                            "NYT_ENG_20050113.0263\n" +
//                            "NYT_ENG_20050113.0264\n" +
//                            "NYT_ENG_20050113.0265\n" +
//                            "NYT_ENG_20050113.0266\n" +
//                            "NYT_ENG_20050113.0267\n" +
//                            "NYT_ENG_20050113.0268\n" +
//                            "NYT_ENG_20050113.0269\n" +
//                            "NYT_ENG_20050113.0270\n" +
//                            "NYT_ENG_20050113.0271\n" +
//                            "NYT_ENG_20050113.0272\n" +
//                            "NYT_ENG_20050113.0273\n" +
//                            "NYT_ENG_20050113.0274\n" +
//                            "NYT_ENG_20050113.0275\n" +
//                            "NYT_ENG_20050113.0276\n" +
//                            "NYT_ENG_20050113.0277\n" +
//                            "NYT_ENG_20050113.0280\n" +
//                            "NYT_ENG_20050113.0281\n" +
//                            "NYT_ENG_20050113.0283\n" +
//                            "NYT_ENG_20050113.0285\n" +
//                            "NYT_ENG_20050113.0286\n" +
//                            "NYT_ENG_20050113.0287\n" +
//                            "NYT_ENG_20050113.0288\n" +
//                            "NYT_ENG_20050113.0289\n" +
//                            "NYT_ENG_20050113.0290\n" +
//                            "NYT_ENG_20050113.0291\n" +
//                            "NYT_ENG_20050113.0292\n" +
//                            "NYT_ENG_20050113.0293\n" +
//                            "NYT_ENG_20050113.0294\n" +
//                            "NYT_ENG_20050113.0295\n" +
//                            "NYT_ENG_20050113.0296\n" +
//                            "NYT_ENG_20050113.0297\n" +
//                            "NYT_ENG_20050113.0298\n" +
//                            "NYT_ENG_20050113.0299\n" +
//                            "NYT_ENG_20050113.0300\n" +
//                            "NYT_ENG_20050113.0301\n" +
//                            "NYT_ENG_20050113.0302\n" +
//                            "NYT_ENG_20050113.0303\n" +
//                            "NYT_ENG_20050113.0304\n" +
//                            "NYT_ENG_20050113.0305\n" +
//                            "NYT_ENG_20050113.0306\n" +
//                            "NYT_ENG_20050113.0307\n" +
//                            "NYT_ENG_20050113.0308\n" +
//                            "NYT_ENG_20050113.0309\n" +
//                            "NYT_ENG_20050113.0310\n" +
//                            "NYT_ENG_20050113.0311\n" +
//                            "NYT_ENG_20050113.0312\n" +
//                            "NYT_ENG_20050113.0313\n" +
//                            "NYT_ENG_20050113.0314\n" +
//                            "NYT_ENG_20050113.0315\n" +
//                            "NYT_ENG_20050113.0316\n" +
//                            "NYT_ENG_20050113.0317\n" +
//                            "NYT_ENG_20050113.0318\n" +
//                            "NYT_ENG_20050113.0319\n" +
//                            "NYT_ENG_20050113.0320\n" +
//                            "NYT_ENG_20050113.0321\n" +
//                            "NYT_ENG_20050113.0322\n" +
//                            "NYT_ENG_20050113.0323\n" +
//                            "NYT_ENG_20050113.0325\n" +
//                            "NYT_ENG_20050113.0326\n" +
//                            "NYT_ENG_20050113.0327\n" +
//                            "NYT_ENG_20050113.0328\n" +
//                            "NYT_ENG_20050113.0329\n" +
//                            "NYT_ENG_20050113.0330\n" +
//                            "NYT_ENG_20050113.0331\n" +
//                            "NYT_ENG_20050113.0332\n" +
//                            "NYT_ENG_20050113.0333\n" +
//                            "NYT_ENG_20050123.0127\n" +
//                            "NYT_ENG_20050212.0103\n" +
//                            "NYT_ENG_20050223.0231\n" +
//                            "NYT_ENG_20050224.0074\n" +
//                            "NYT_ENG_20050225.0039\n" +
//                            "NYT_ENG_20050314.0111\n" +
//                            "NYT_ENG_20050319.0144\n" +
//                            "NYT_ENG_20050401.0018\n" +
//                            "NYT_ENG_20050402.0107\n" +
//                            "NYT_ENG_20050402.0110\n" +
//                            "NYT_ENG_20050408.0002\n" +
//                            "NYT_ENG_20050408.0044\n" +
//                            "NYT_ENG_20050420.0047\n" +
//                            "NYT_ENG_20050420.0081\n" +
//                            "NYT_ENG_20050421.0035\n" +
//                            "NYT_ENG_20050422.0145\n" +
//                            "NYT_ENG_20050504.0123\n" +
//                            "NYT_ENG_20050505.0154\n" +
//                            "NYT_ENG_20050508.0160\n" +
//                            "NYT_ENG_20050511.0307\n" +
//                            "NYT_ENG_20050523.0074\n" +
//                            "NYT_ENG_20050601.0033\n" +
//                            "NYT_ENG_20050612.0010\n" +
//                            "NYT_ENG_20050703.0168\n" +
//                            "NYT_ENG_20050708.0233\n" +
//                            "NYT_ENG_20050710.0002\n" +
//                            "NYT_ENG_20050714.0197\n" +
//                            "NYT_ENG_20050715.0074\n" +
//                            "NYT_ENG_20050815.0198\n" +
//                            "NYT_ENG_20050819.0035\n" +
//                            "NYT_ENG_20050826.0027\n" +
//                            "NYT_ENG_20050830.0251\n" +
//                            "NYT_ENG_20050831.0145\n" +
//                            "NYT_ENG_20050902.0330\n" +
//                            "NYT_ENG_20050909.0326\n" +
//                            "NYT_ENG_20050917.0041\n" +
//                            "NYT_ENG_20050917.0205\n" +
//                            "NYT_ENG_20050917.0206\n" +
//                            "NYT_ENG_20051011.0219\n" +
//                            "NYT_ENG_20051013.0399\n" +
//                            "NYT_ENG_20051025.0268\n" +
//                            "NYT_ENG_20051027.0039\n" +
//                            "NYT_ENG_20051028.0301\n" +
//                            "NYT_ENG_20051030.0004\n" +
//                            "NYT_ENG_20051030.0123\n" +
//                            "NYT_ENG_20051108.0137\n" +
//                            "NYT_ENG_20051108.0151\n" +
//                            "NYT_ENG_20051109.0267\n" +
//                            "NYT_ENG_20051109.0338\n" +
//                            "NYT_ENG_20051110.0166\n" +
//                            "NYT_ENG_20051111.0116\n" +
//                            "NYT_ENG_20051112.0062\n" +
//                            "NYT_ENG_20051113.0147\n" +
//                            "NYT_ENG_20051114.0078\n" +
//                            "NYT_ENG_20051114.0210\n" +
//                            "NYT_ENG_20051116.0096\n" +
//                            "NYT_ENG_20051116.0101\n" +
//                            "NYT_ENG_20051116.0283\n" +
//                            "NYT_ENG_20051116.0305\n" +
//                            "NYT_ENG_20051117.0128\n" +
//                            "NYT_ENG_20051118.0084\n" +
//                            "NYT_ENG_20051119.0129\n" +
//                            "NYT_ENG_20051119.0137\n" +
//                            "NYT_ENG_20051120.0143\n" +
//                            "NYT_ENG_20051120.0169\n" +
//                            "NYT_ENG_20051122.0127\n" +
//                            "NYT_ENG_20051122.0131\n" +
//                            "NYT_ENG_20051123.0052\n" +
//                            "NYT_ENG_20051123.0056\n" +
//                            "NYT_ENG_20051123.0075\n" +
//                            "NYT_ENG_20051123.0089\n" +
//                            "NYT_ENG_20051123.0090\n" +
//                            "NYT_ENG_20051123.0114\n" +
//                            "NYT_ENG_20051123.0126\n" +
//                            "NYT_ENG_20051123.0129\n" +
//                            "NYT_ENG_20051123.0318\n" +
//                            "NYT_ENG_20051129.0258\n" +
//                            "NYT_ENG_20051130.0282\n" +
//                            "NYT_ENG_20051205.0209\n" +
//                            "NYT_ENG_20051209.0415").split("\n"));
}

