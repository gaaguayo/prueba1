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
public class ParallelMF_BiasTimeAwareRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    private int maxThreads;
    
    public ParallelMF_BiasTimeAwareRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int features, int bins, int maxThreads) {
        super(split, eModel, iterations, features, bins);
        this.maxThreads = maxThreads;
    }

    @Override
    protected ModelBasedRecommender<U, I, C> getRecommender(SplitIF<U,I,C> theSplit, ContextualModelUtils<U,I,C> theEModel, int iterations, double[] p, int features, int bins) {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        // p[4] learnRateBinBias
        // p[5] lambdaBinBias
        // p[6] learnRateAlpha
        // p[7] lambdaAlpha
        // p[8] learnRateDayBias
        // p[9] lambdaDayBias
        // p[10] beta
        return new FactorizationRecommender(theSplit.getTrainingSet(), theEModel, features, 0, iterations, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[10], p[8], p[9], true, bins, bins, true, maxThreads);
    }

    @Override
    protected double[] getStartPoint() {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        // p[4] learnRateBinBias
        // p[5] lambdaBinBias
        // p[6] learnRateAlpha
        // p[7] lambdaAlpha
        // p[8] learnRateDayBias
        // p[9] lambdaDayBias
        // p[10] beta
        double[] startPoint={0.005, 0.02, 0.005, 0.02, 0.00005, 0.02, 0.0005, 0.02, 0.0005, 0.3, 0.08};
        return startPoint;
    }    
}
