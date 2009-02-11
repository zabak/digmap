package pt.utl.ist.lucene.treceval.geoclef.parametermax;

import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.Optimizer;
import cc.mallet.optimize.LimitedMemoryBFGS;

/**
 * @author Jorge
 * @date 10/Fev/2009
 * @time 17:38:09
 */
public class OptimizerGradient implements Optimizable.ByGradientValue
{
// Optimizables encapsulate all state variables,
    //  so a single Optimizer object can be used to optimize
    //  several functions.

    double[] parameters;

    public OptimizerGradient(double x, double y) {
        parameters = new double[2];
        parameters[0] = x;
        parameters[1] = y;
    }

    public double getValue() {

        double x = parameters[0];
        double y = parameters[1];

        return -Math.pow(x+2,2) + y;

    }

    public void getValueGradient(double[] gradient) {

        double x = parameters[0];
        double y = parameters[1];
        gradient[0] = -2 * x + 4;
        gradient[1] = 0;

    }

    // The following get/set methods satisfy the Optimizable interface

    public int getNumParameters() { return 2; }
    public double getParameter(int i) { return parameters[i]; }
    public void getParameters(double[] buffer) {
        buffer[0] = parameters[0];
        buffer[1] = parameters[1];
    }

    public void setParameter(int i, double r) {
        parameters[i] = r;
    }
    public void setParameters(double[] newParameters) {
        parameters[0] = newParameters[0];
        parameters[1] = newParameters[1];
    }

    public static void main(String[] args)
    {
        OptimizerGradient optimizable = new OptimizerGradient(0, 0);
                Optimizer optimizer = new LimitedMemoryBFGS(optimizable);

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
}
