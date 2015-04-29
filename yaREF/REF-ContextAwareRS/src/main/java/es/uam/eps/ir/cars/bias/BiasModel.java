package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.cars.recommender.AbstractRecommenderModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.Serializable;
/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.
 */
public class BiasModel<U,I,C extends ContextIF> extends AbstractRecommenderModel<U,I,C> implements Serializable{
    protected double userBias[];
    protected double itemBias[];
    
    public BiasModel(ModelIF<U,I,C> model, int iterationSteps, double initValue, double learningRate, double lambda, double initNoise) {
        super(model, iterationSteps, initValue, learningRate, lambda, initNoise);
        
        this.userBias=new double[userMaxIndex];
        this.itemBias=new double[itemMaxIndex];

        for (int j=0; j<userMaxIndex; j++) this.userBias[j]=(random.nextDouble()-0.5)*0.005;
        for (int j=0; j<itemMaxIndex; j++) this.itemBias[j]=(random.nextDouble()-0.5)*0.005;
    }
    
    public double[] getUserBias() {
        return userBias;
    }

    public double getUserBias(int i) {
        return userBias[i];
    }

    public void setUserBias(double[] dim1Bias) {
        this.userBias = dim1Bias;
    }

    public void setUserBias(int i, double value) {
        this.userBias[i] = value;
    }

    public double[] getItemBias() {
        return itemBias;
    }

    public double getItemBias(int i) {
        return itemBias[i];
    }

    public void setItemBias(double[] dim2Bias) {
        this.itemBias = dim2Bias;
    }

    public void setItemBias(int i, double value) {
        this.itemBias[i] = value;
    }

    @Override
    public String toString(){
        return 
                " Iters:"+this.iterationSteps+
                " LRate:"+this.learningRate+
                " L:"+this.lambda;
    }

}
