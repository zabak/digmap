package pt.utl.ist.lucene.treceval.geoclef.parametermax;

import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.Optimizer;
import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.AGIS;

/**
 * @author Jorge
 * @date 10/Fev/2009
 * @time 17:38:09
 */
public class OptimizerValue implements Optimizable.ByGISUpdate
{

    public static void main(String[] args)
    {
        OptimizerValue optimizable = new OptimizerValue();
        Optimizer optimizer = new AGIS(optimizable,0,true);

        boolean converged = false;

        try {
            converged = optimizer.optimize();
        } catch (IllegalArgumentException e) {
            // This exception may be thrown if L-BFGS
            //  cannot step in the current direction.
            // This condition does not necessarily mean that
            //  the optimizer has failed, but it doesn't want
            //  to claim to have succeeded...
        }

        System.out.println(optimizable.getParameter(0) + ", " +
                optimizable.getParameter(1));

    }

    double[] params = {-5.0};



    public double getValue()
    {
        return -1*((params[0]+1)*(params[0] + 1));
    }

    public void getGISUpdate(double[] buffer)
    {
        buffer[0] = params[0];
    }

    public int getNumParameters()
    {
        return 1;
    }

    public void getParameters(double[] buffer)
    {
        buffer[0] = params[0];
    }

    public double getParameter(int index)
    {
        return 0;
    }

    public void setParameters(double[] params)
    {
        this.params = params;
    }

    public void setParameter(int index, double value)
    {
        params[index] = value;
    }
}
