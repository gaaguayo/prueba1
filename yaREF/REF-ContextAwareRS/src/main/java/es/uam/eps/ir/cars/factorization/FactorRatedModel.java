package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.Serializable;
/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.es
 */
public class FactorRatedModel<U,I,C extends ContextIF> extends FactorModel<U,I,C> implements Serializable{
    protected double[][] ratedFactors;

    public FactorRatedModel(ModelIF<U,I,C> model, int nFeatures, int iterationSteps, double initValue, double learningRate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean withBias, double initNoise) {
        super(model, nFeatures, iterationSteps, initValue, learningRate, lambda, lrStaticBias, lambdaStaticBias, withBias, initNoise);
        ratedFactors=new double[nFeatures][itemMaxIndex];
        for (int i=0;i<nFeatures;i++){
            for (int j=0; j<itemMaxIndex; j++) this.ratedFactors[i][j]=0.001+random.nextDouble()*0.01;
        }
    }

    public double getRatedFactors(int i, int j){
        return this.ratedFactors[i][j];
    }

    public void setRatedFactors(int i, int j, double value) {
        this.ratedFactors[i][j] = value;
    }

}
