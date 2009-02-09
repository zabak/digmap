package pt.utl.ist.lucene.treceval.geoclef.parametermax;

import java.io.*;

/**
 * @author Jorge
 * @date 8/Fev/2009
 * @time 14:00:32
 */
public class OptimizerGeoModel
{

    // Optimizables encapsulate all state variables,
    //  so a single Optimizer object can be used to optimize
    //  several functions.

    double[] parameters;

    public OptimizerGeoModel(double x)
    {

        parameters = new double[1];
        parameters[0] = x;
    }


    public void getValueGradient(double[] buffer)
    {
//        buffer[0] =getValue();
    }

    static class Result
    {


        
        public String num_q;
        public String num_ret;
        public String num_rel;
        public String num_rel_ret;
        public String map = null;
        public String gm_ap;
        public String ndcg;
        public String R_prec;
        public String bpref;
        public String recip_rank;
        public String P5;
        public String P10;
        public String P15;
        public String P20;
        public String P30;
        public String P100;
        public String P200;
        public String P500;
        public String P1000;

        public String toString()
        {
            return num_q + " "
                    + num_ret + " "
                    + num_rel + " "
                    + num_rel_ret + " "
                    + map + " "
                    + gm_ap + " "
                    + ndcg + " "
                    + R_prec + " "
                    + bpref + " "
                    + recip_rank + " "
                    + P5 + " "
                    + P10 + " "
                    + P15 + " "
                    + P20 + " "
                    + P30 + " "
                    + P100 + " "
                    + P200 + " "
                    + P500 + " "
                    + P1000 ;
        }
    }
    /**
     * Função que retorna o MAP
     * @return
     */
    public Result getValue() {

//        double x = parameters[0];
//        double y = parameters[1];
//
//        return -3*x*x - 4*y*y + 2*x - 4*y + 18;

//        return 0.6 - Math.abs(0.6-parameters[0]);


        try
        {
            FileWriter fw = new FileWriter("d:\\geoClefParameters.properties");
            fw.write("LM_LAMBDA=0.15\n");
            fw.write("TEXT_FACTOR=" +  "" + (1 - parameters[0]) + "\n");
            fw.write("SPATIAL_FACTOR="+ parameters[0] +"\n");
            fw.close();

            executeCommand("D:\\Servidores\\workspace\\lgte\\WEB-INF\\geoclef.bat");

            for(File f : new File("D:\\Servidores\\workspace\\lgte\\WEB-INF\\build\\webapp\\lgte\\WEB-INF\\test-data\\geoclef08en\\output").listFiles())
            {
                if(f.getName().endsWith("-treceval-report.txt"))
                {
                    System.out.println("found output file: " + f.getName());
                    BufferedReader reader = new BufferedReader(new FileReader(f));

                    Result r = new Result();
                    String line;

                    while((line = reader.readLine())!=null)
                    {
                        String[] lineElements = line.split("[ \\n\\t]+");
                        if(lineElements.length == 3 && lineElements[1].equals("all"))
                        {
                            if(lineElements[0].equals("num_q"))
                                r.num_q = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("num_ret"))
                                r.num_ret = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("num_rel"))
                                r.num_rel = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("num_rel_ret"))
                                r.num_rel_ret = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("map"))
                                r.map = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("gm_ap"))
                                r.gm_ap = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("ndcg"))
                                r.ndcg = lineElements[2].replace(",",".");

                            else if(lineElements[0].equals("R-prec"))
                                r.R_prec = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("bpref"))
                                r.bpref = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("recip_rank"))
                                r.recip_rank = lineElements[2].replace(",",".");

                            else if(lineElements[0].equals("P5"))
                                r.P5 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P10"))
                                r.P10 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P15"))
                                r.P15 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P20"))
                                r.P20 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P30"))
                                r.P30 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P100"))
                                r.P100 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P200"))
                                r.P200 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P500"))
                                r.P500 = lineElements[2].replace(",",".");
                            else if(lineElements[0].equals("P1000"))
                                r.P1000 = lineElements[2].replace(",",".");
                        }
                    }
                    reader.close();
                    return r;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.exit(-1);
        return null;

    }


    public void getGISUpdate(double[] buffer)
    {
        buffer[0]= parameters[0] + 0.01f;
    }

    // The following get/set methods satisfy the Optimizable interface

    public int getNumParameters() {
        return 1; }
    public double getParameter(int i) {
        return parameters[i]; }
    public void getParameters(double[] buffer) {
        buffer[0] = parameters[0];
//        buffer[1] = parameters[1];
    }

    public void setParameter(int i, double r) {
        parameters[i] = r;
    }
    public void setParameters(double[] newParameters) {
        parameters[0] = newParameters[0];
//        parameters[1] = newParameters[1];
    }




    /*
     * @param timeOut to wait
     */
    private static void executeCommand(String executeString)
    {
        try
        {
            Process proc = Runtime.getRuntime().exec(executeString);
            InputStream reader = proc.getInputStream();
            BufferedReader readerB = new BufferedReader(new InputStreamReader(reader));
            String line;
            while((line = readerB.readLine())!=null)
            {
                System.out.println(line);
            }
            proc.waitFor();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
