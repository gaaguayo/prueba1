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
public class ParallelMF_FullTimeAwareRecommenderFunction<U,I,C extends ContextIF> extends AbstractObjectiveFunction<U,I,C> {
    private int maxThreads;
    
    public ParallelMF_FullTimeAwareRecommenderFunction(SplitIF<U, I, C> split, ContextualModelUtils<U, I, C> eModel, int iterations, int features, int bins, int maxThreads) {
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
        // p[7] lambdaalpha
        // p[8] learnRateDayBias
        // p[9] lambdaDayBias
        // p[10] learnRateUserAlpha
        // p[11] lambdaUserAlpha
        // p[12] learnRateUserDayFactor
        // p[13] lambdaUserDayFactor
        // p[14] beta
        return new FactorizationRecommender(theSplit.getTrainingSet(), theEModel, features, 0, iterations, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[14], p[8], p[9], p[10], p[11], p[12], p[13], true, bins, bins, true, maxThreads);
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
        // p[7] lambdaalpha
        // p[8] learnRateDayBias
        // p[9] lambdaDayBias
        // p[10] learnRateUserAlpha
        // p[11] lambdaUserAlpha
        // p[12] learnRateUserDayFactor
        // p[13] lambdaUserDayFactor
        // p[14] beta
        double[] startPoint={0.005, 0.02, 0.005, 0.02, 0.005, 0.02, 0.005, 0.02, 0.005, 0.02, 0.005, 0.02, 0.005, 0.02, 0.08}; //general
//        double[] startPoint={0.005, 0.02, 0.005, 0.02, 0.00005, 0.02, 0.0005, 0.02, 0.0005, 0.3, 0.00002, 0.008, 0.00005, 0.02, 0.08};       
//        double[] startPoint={0.007753004973743459, 0.02489067140028797, 0.001970619750361941, 0.022013935352256923, 1.0843649355866893E-4, 0.013173932717992325, 1.3512440469673524E-5, 0.020498566546703345, 0.010598468129197909, 0.3033035514116042, 8.869200125510603E-10, 0.0057122194145052966, 1.6373472347643278E-4, 0.018361427373369897, 0.09431253705289161};
//        double[] startPoint={0.026615211625514404, 0.022529453223593968, 0.012976537014403296, 0.022408959396433472, 0.005586420919067216, 0.024307231001371745, 0.006076807750342936, 1.2396687810699587, 0.006076807750342935, 0.024307231001371745, 0.006076807750342936, 0.26737954101508915, 0.006076807750342936, 0.02430723100137175, 0.097228924005487}; //Ml1m_TFMSplit5_validation
        return startPoint;
    }    
}
