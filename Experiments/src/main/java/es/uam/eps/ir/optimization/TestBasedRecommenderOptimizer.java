package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public class TestBasedRecommenderOptimizer<U,I,C extends ContextIF> extends ModelBasedRecommenderOptimizer<U,I,C> {
    public enum METHOD{
        Bias,
        TimeBias,
        MF,
        MFBias,
        TimeMF
    }

    public TestBasedRecommenderOptimizer(DatasetSplitterIF<U, I, C> splitter, int maxOptimizationIterations, int trainingIterations, int maxThreads) {
        super(splitter, maxOptimizationIterations, trainingIterations, maxThreads);
    }
    
    public SplitIF<U,I,C> getOptimizationData(SplitIF<U,I,C> split){
        return split;
    }
}
