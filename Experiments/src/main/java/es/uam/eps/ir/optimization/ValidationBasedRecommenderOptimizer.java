package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.impl.Split;

/**
 *
 * @author pedro
 */
public class ValidationBasedRecommenderOptimizer<U,I,C extends ContextIF> extends ModelBasedRecommenderOptimizer<U,I,C> {

    public ValidationBasedRecommenderOptimizer(DatasetSplitterIF<U, I, C> splitter, int maxOptimizationIterations, int trainingIterations, int maxThreads) {
        super(splitter, maxOptimizationIterations, trainingIterations, maxThreads);
    }
    
    public SplitIF<U,I,C> getOptimizationData(SplitIF<U,I,C> split){
        TrainValidationSetsGenerator generator = new TrainValidationSetsGenerator(split.getTrainingSet(), new ContextualModelUtils(split.getTrainingSet()), splitter);
        SplitIF<U,I,C> trainValidationSplit = new Split(generator.getTrainSet(), generator.getValidationSet());
        return trainValidationSplit;
    }
}
