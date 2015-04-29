package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.cars.bias.TimeBiasRecommender;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class TimeBiasRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    public TimeBiasRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int bins) {
        super(split, eModel, iterations, 0, bins);
    }

    @Override
    protected ModelBasedRecommender<U, I, C> getRecommender(SplitIF<U,I,C> theSplit, ContextualModelUtils<U,I,C> theEModel, int iterations, double[] p, int features, int bins) {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateBinBias
        // p[3] lambdaBinBias
        // p[4] learnRateAlpha
        // p[5] lambdaAlpha
        // p[6] learnRateDayBias
        // p[7] lambdaDayBias
        // p[8] beta
        return new TimeBiasRecommender(theSplit.getTrainingSet(), theEModel, 0, iterations, p[0], p[1], p[2], p[3], p[4], p[5], p[8], p[6], p[7],bins, bins) ;
    }

    @Override
    protected double[] getStartPoint() {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateBinBias
        // p[3] lambdaBinBias
        // p[4] learnRateAlpha
        // p[5] lambdaAlpha
        // p[6] learnRateDayBias
        // p[7] lambdaDayBias
        // p[8] beta
        double[] startPoint={0.005, 0.005, 0.005, 0.9, 0.005, 0.9, 0.005, 0.9, 0.1};
        return startPoint;
    }    
}
