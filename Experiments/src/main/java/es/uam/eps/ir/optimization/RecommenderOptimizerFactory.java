package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public class RecommenderOptimizerFactory<U,I,C extends ContextIF> {
    public enum OPTIMIZATION_METHOD{
        VALIDATION,
        TEST
    }

    public ModelBasedRecommenderOptimizer getOptimizer(OPTIMIZATION_METHOD optimizationMethod, DatasetSplitterIF<U, I, C> splitter, int maxOptimizationIterations, int trainingIterations, int maxThreads) {
        ModelBasedRecommenderOptimizer optimizer = null;
        switch(optimizationMethod){
            case VALIDATION:
                optimizer = new ValidationBasedRecommenderOptimizer(splitter, maxOptimizationIterations, trainingIterations, maxThreads);
                break;
            case TEST:
                optimizer = new TestBasedRecommenderOptimizer(splitter, maxOptimizationIterations, trainingIterations, maxThreads);
                break;
        }
        return optimizer;
    }   
}
