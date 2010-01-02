package pt.utl.ist.lucene.utils.placemaker;

import java.io.*;

/**
 * @author Jorge Machado
 * @date 2/Dez/2009
 * @time 12:36:56
 * @email machadofisher@gmail.com
 */
public class PlaceMakerDocumentTagNormalizer {


    public static void main(String[] args) throws IOException {
        File d = new File("D:\\Servidores\\DATA\\ntcir\\PlaceMaker");
        int bytes = 0;
        for(File f: d.listFiles())
        {
            if(f.isFile() && f.getName().endsWith(".xml"))
            {
                FileInputStream inputStream = new FileInputStream(f);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                FileWriter fw = new FileWriter(f.getParentFile().getAbsolutePath() + File.separator + "output" +  File.separator + f.getName());
                String line;
                int cont = 0;
                line = reader.readLine();
                int endProcessingInst = line.indexOf("?>");
                if(endProcessingInst < line.length())
                {
                    fw.write(line.substring(0,endProcessingInst+2));
                    writeLine(fw,line.substring(endProcessingInst+2));
                }
                while((line = reader.readLine())!=null)
                {
                    writeLine(fw,line);
                    cont++;
                    if(cont > 10000)
                    {
                        cont =0;
                        fw.flush();
                        System.out.println("flushing");
                    }
                }
                fw.flush();
                fw.close();
                inputStream.close();
            }
        }

    }

    private static void writeLine(FileWriter fw, String line) throws IOException
    {
        int startOfTag;
        if(line.length()>0)
        {
            while((startOfTag = line.indexOf("<doc ",1))>0)
            {
                String firstPart = line.substring(0,startOfTag);
                fw.write(firstPart + "\n");
                line = line.substring(startOfTag);
            }
        }
        if(line.length()>0)
            fw.write(line + "\n");
    }




}
