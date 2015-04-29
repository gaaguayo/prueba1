package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.es
 */
public class TimeBiasRecommender<U,I,C extends ContinuousTimeContextIF> extends ModelBasedRecommender<U,I,C> implements RecommenderIF<U,I,C> {

    public TimeBiasRecommender(ModelIF<U,I,C> dataset, ContextualModelUtils<U,I,C> statistics, double defaultValue, int iterationSteps, double lrate, double lambda, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, int dim1BiasBins, int dim2BiasBins){
        super(dataset);
        eModel=statistics;
        algorithm=new TimeBiasAlgorithm(dataset, statistics, defaultValue, iterationSteps, lrate, lambda, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, dim1BiasBins, dim2BiasBins);
    }

}
