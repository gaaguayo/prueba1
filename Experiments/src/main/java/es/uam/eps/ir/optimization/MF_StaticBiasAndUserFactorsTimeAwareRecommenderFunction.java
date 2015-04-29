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
public class MF_StaticBiasAndUserFactorsTimeAwareRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    public MF_StaticBiasAndUserFactorsTimeAwareRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int features, int bins) {
        super(split, eModel, iterations, features, bins);
    }

    @Override
    protected ModelBasedRecommender<U, I, C> getRecommender(SplitIF<U,I,C> theSplit, ContextualModelUtils<U,I,C> theEModel, int iterations, double[] p, int features, int bins) {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        // p[4] learnRateUserAlpha
        // p[5] lambdaUserAlpha
        // p[6] learnRateUserDayFactor
        // p[7] lambdaUserDayFactor
        // p[8] beta
        return new FactorizationRecommender(theSplit.getTrainingSet(), theEModel, features, 0, iterations, p[0], p[1], p[2], p[3], p[8], p[4], p[5], p[6], p[7], true, true);
    }

    @Override
    protected double[] getStartPoint() {
        // p[0] learnRate
        // p[1] lambda
        // p[2] learnRateStaticBias
        // p[3] lambdaStaticBias
        // p[4] learnRateUserAlpha
        // p[5] lambdaUserAlpha
        // p[6] learnRateUserDayFactor
        // p[7] lambdaUserDayFactor
        // p[8] beta
        double[] startPoint={0.005, 0.02, 0.005, 0.02, 0.00002, 0.008, 0.00005, 0.02, 0.08};
        return startPoint;
    }    
}
