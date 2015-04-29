package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.cars.factorization.FactorizationRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class MF_StaticBiasRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    public MF_StaticBiasRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int features) {
        super(split, eModel, iterations, features, 0);
    }

    @Override
    protected ModelBasedRecommender<U, I, C> getRecommender(SplitIF<U,I,C> theSplit, ContextualModelUtils<U,I,C> theEModel, int iterations, double[] p, int features, int bins) {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        return new FactorizationRecommender(theSplit.getTrainingSet(), theEModel, features, 0, iterations, p[0], p[1], p[2], p[3], true);
    }

    @Override
    protected double[] getStartPoint() {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        double[] startPoint={0.005, 0.02, 0.005, 0.02};
        return startPoint;
    }    
}
