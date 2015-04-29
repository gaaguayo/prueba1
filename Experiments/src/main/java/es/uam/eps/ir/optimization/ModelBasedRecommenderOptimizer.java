package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public abstract class ModelBasedRecommenderOptimizer<U,I,C extends ContextIF> {
    public enum RECOMMENDER_METHOD{
        BiasStatic,
        BiasTime,
        MF,
        MFBiasStatic,
        MFUserFactorsTimeAware,
        MFBiasStaticAndUserFactorsTimeAware,
        MFBiasTime,
        MFTimeFull,
        ParallelMF,
        ParallelMFBiasStatic,
        ParallelMFUserFactorsTimeAware,
        ParallelMFBiasStaticAndUserFactorsTimeAware,
        ParallelMFBiasTime,
        ParallelMFTimeFull
    }
    
    public enum OPTIMIZATION_METHOD{
        VALIDATION,
        TEST
    }
    
    protected DatasetSplitterIF<U,I,C> splitter;
    protected int maxOptimizationIterations = 1000;
    protected int trainingIterations = 50;
    private int maxThreads = 4;
    
    private RecommenderParameterOptimizer optimizer;
    private RECOMMENDER_METHOD rm;

    protected ModelBasedRecommenderOptimizer(DatasetSplitterIF<U, I, C> splitter) {
        this.splitter = splitter;
    }
    
    protected ModelBasedRecommenderOptimizer(DatasetSplitterIF<U, I, C> splitter, int maxOptimizationIterations, int trainingIterations, int maxThreads) {
        this.splitter = splitter;
        this.maxOptimizationIterations = maxOptimizationIterations;
        this.trainingIterations = trainingIterations;
        this.maxThreads = maxThreads;
    }
    
    public abstract SplitIF<U,I,C> getOptimizationData(SplitIF<U,I,C> split);
    
    public RecommenderIF<U,I,C> getRecommender(RECOMMENDER_METHOD recommenderMethod, SplitIF<U,I,C> originalSplit, int features, int bins){
        this.rm = recommenderMethod;
        AbstractObjectiveFunction function = null;
        
        SplitIF<U,I,C> split = getOptimizationData(originalSplit);
        
        switch (recommenderMethod){
            case BiasStatic:
                function = new BiasRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations);
                break;
            case BiasTime:
                function = new TimeBiasRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, bins);
                break;
            case MF:
                function = new MFRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features);
                break;
            case MFBiasStatic:
                function = new MF_StaticBiasRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features);
                break;
            case MFBiasTime:
                function = new MF_BiasTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins);
                break;
            case MFTimeFull:
                function = new MF_FullTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins);
                break;
            case MFUserFactorsTimeAware:
                function = new MF_UserFactorsTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins);
                break;
            case MFBiasStaticAndUserFactorsTimeAware:
                function = new MF_StaticBiasAndUserFactorsTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins);
                break;
            case ParallelMF:
                function = new ParallelMFRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, maxThreads);
                break;
            case ParallelMFBiasStatic:
                function = new ParallelMF_StaticBiasRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, maxThreads);
                break;
            case ParallelMFUserFactorsTimeAware:
                function = new ParallelMF_UserFactorsTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins, maxThreads);
                break;
            case ParallelMFBiasStaticAndUserFactorsTimeAware:
                function = new ParallelMF_StaticBiasAndUserFactorsTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins, maxThreads);
                break;
            case ParallelMFBiasTime:
                function = new ParallelMF_BiasTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins, maxThreads);
                break;
            case ParallelMFTimeFull:
                function = new ParallelMF_FullTimeAwareRecommenderFunction(split, new ContextualModelUtils(split.getTrainingSet()), trainingIterations, features, bins, maxThreads);
                break;
        }
//        optimizer = new RecommenderParameterOptimizer(function, split, features, bins, maxOptimizationIterations);
        optimizer = new JtemRecommenderParameterOptimizer(function, originalSplit, features, bins, maxOptimizationIterations);
        return optimizer.getOptimizedRecommender();
    }
    
    @Override
    public String toString(){
        return rm +" using "+optimizer.toString();
    }
}
