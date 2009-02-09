package pt.utl.ist.lucene.treceval.geoclef.parametermax;

import java.io.IOException;
import java.io.FileWriter;

/**
 * @author Jorge
 * @date 8/Fev/2009
 * @time 14:12:30
 */
public class OptimizerRunnerLMLambda
{

    public static void main(String[] args) throws IOException
    {
        OptimizerLMLambda optimizable = new OptimizerLMLambda(0);

//                Optimizer optimizer = new  AGIS(optimizable,1);
//
//                boolean converged = false;

                double maxValue = -1;
                double maxParameter = 0;

                double inc = 0.05;
                double actualParameter = 0;
                try {
//                    converged = optimizer.optimize();


                    FileWriter fw = new FileWriter("d:/optimize.out");
                    fw.write("Parameter (retrieval report)\n");
                    while(actualParameter < 1)
                    {

                        optimizable.setParameter(0,actualParameter);
                        OptimizerLMLambda.Result r = optimizable.getValue();
                        double value = Double.parseDouble(r.map);
                        fw.write(actualParameter + " " + r.toString() + "\n");
                        fw.flush();
                        System.out.println(actualParameter + "\t" + value);
                        if(value > maxValue)
                        {
                            maxValue = value;
                            maxParameter = actualParameter;
                        }
                        actualParameter +=inc;
                    }
                    fw.flush();
                    fw.close();



                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                System.out.println(maxParameter + ", maxValue:" + maxValue);

    }
}
