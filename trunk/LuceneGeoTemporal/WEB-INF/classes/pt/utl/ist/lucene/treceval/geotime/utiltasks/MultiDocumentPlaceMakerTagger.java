package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Jorge Machado
 * @date 31/Dez/2009
 * @time 13:25:50
 * @email machadofisher@gmail.com
 */
public class MultiDocumentPlaceMakerTagger implements Runnable{
    private static final Logger logger = Logger.getLogger(MultiDocumentPlaceMakerTagger.class);

    String path;
    String mode;
    String startDocument = null;


    public MultiDocumentPlaceMakerTagger(String path,String mode, String startDocument) {
        this.path = path;
        this.mode = mode;
        this.startDocument = startDocument;
    }

    public void run()
    {
        try {
            String [] args;
            if(startDocument == null)
                args = new String[]{path,mode};
            else
                args = new String[]{path,mode,startDocument};

            DocumentTagger.main(args);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public static void main(String [] args) throws InterruptedException
    {

//        String path = "D:\\Servidores\\DATA\\ntcir\\2002";
//        String path = "F:\\coleccoesIR\\ntcir\\data";
//        String url = "http://deptal.estgp.pt:9090/jmachado/TIMEXTAG/index.php";
//        String url = "http://192.168.1.66/jmachado/TIMEXTAG/index.php";
//        args = new String[] {path,url};
//        args = new String[] {path,url,"nyt_eng_200201.gz"};
//        String path = null;
//        args = new String[3];
//        args[0] = Config.documentPath;
//        args[1] = "nyt_eng_200501.gz";
//        args[2] = "NYT_ENG_20050105.0263";


//        String path = "F:\\coleccoesIR\\ntcir\\data";
        String url = "http://deptal.estgp.pt:9090/jmachado/TIMEXTAG/index.php";
//        String url = "http://192.168.1.66/jmachado/TIMEXTAG/index.php";



//
//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P0";
//        args = new String[] {path,"xie200105.gz","XIE20010514.0282"};

//
//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P1";
//        args = new String[] {path,"xie200107.gz","XIE20010729.0127"};


//        String path = "D:\\Servidores\\DATA\\ntcir\\mainichidaly";
//        args = new String[] {path};

           /****/


//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P4";
//        args = new String[] {path,"xie200109.gz","XIE20010927.0162"};

//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P5";
//        args = new String[] {path,"xie200102.gz","XIE20010222.0064"};

                 /**********/
//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P6";
//        args = new String[] {path,"xie199907.gz","XIE19990731.0041"};

//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P7";
//        args = new String[] {path,"xie199901.gz","XIE19990122.0269"};

//        String path = "D:\\Servidores\\DATA\\collections_ir\\placemaker\\P8";
//        args = new String[] {path,"xie200010.gz","XIE20001016.0397"};

//        args = new String[] {path,url};
//        String path = null;
        String startFile = null;
//        String path;
//        String url;
        String startDocument = null;


//        String startFile = null;
        String path;
//        String startDocument = null;
        if(args!=null)
        {
            path = args[0];
            if(path.startsWith("\""))
                path = path.substring(1,path.length()-1);

        }
        else
        {
            logger.fatal("No arguments, use: path url startfile");
            return;
        }



        if(args.length > 1)
        {
            startFile = args[1];
            if(startFile.startsWith("\""))
                startFile = startFile.substring(1,startFile.length()-1);
        }
//
        if(args.length > 2)
        {
            startDocument = args[2];
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
                    MultiDocumentPlaceMakerTagger multiDocumentIterator = new MultiDocumentPlaceMakerTagger(f.getAbsolutePath(),"placemaker",startDocument);
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
