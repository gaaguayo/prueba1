package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.cars.bias.TimeBiasRecommender;
import es.uam.eps.ir.cars.factorization.FactorizationRecommender;
import es.uam.eps.ir.cars.contextualfiltering.ContextFilteringBasedRecommenderBuilder;
import es.uam.eps.ir.cars.contextualfiltering.ContextFilteringModelBasedRecommenderBuilder;
import es.uam.eps.ir.cars.contextualfiltering.ContextualSlicer_CategoricalContext;
import es.uam.eps.ir.cars.contextualfiltering.ContextualSlicer_ContinuousTimeContext;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.model.ContinuousTimeUtils;
import es.uam.eps.ir.cars.recommender.NeighborBasedRecommenderBuilder;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.cars.timeDecay.ContinuousTimeRecommenderBuilder;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.mahout.MahoutRecommenderFactory;
import es.uam.eps.ir.optimization.ModelBasedRecommenderOptimizer;
import es.uam.eps.ir.optimization.RecommenderOptimizerFactory;
import es.uam.eps.ir.rec.impl.ItemAvgRecommender;
import es.uam.eps.ir.rec.impl.RandomRecommender;
import es.uam.eps.ir.recommender.baseline.ItemPopularityBasedRecommender;
import es.uam.eps.ir.recommender.offline.HybridOfflineRecommender;
import es.uam.eps.ir.split.DatasetSplitterIF;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class CommonRecommenders<U,I,C extends ContextIF> {
    public enum METHOD{
        kNN_PearsonWeightedSimilarity_UserBased,
        kNN_PearsonWeightedSimilarity_ItemBased,
        kNN_CosineSimilarity_UserBased,
        kNN_CosineSimilarity_ItemBased,
        Raw_CosineSimilarity_UserBased,
        Raw_CosineSimilarity_ItemBased,
        kNN_PRF_UserBased,
        kNN_POF_UserBased,
        kNN_FPOF_UserBased,
        kNN_WPOF_UserBased,
        kNN_PRF_UserBased_Categorical,
        kNN_POF_UserBased_Categorical,
        kNN_PRF_MahoutUserBased,
        kNN_POF_MahoutUserBased,
        kNN_FPOF_MahoutUserBased,
        kNN_WPOF_MahoutUserBased,
        kNN_PRF_MahoutUserBased_Categorical,
        kNN_POF_MahoutUserBased_Categorical,
        kNN_CM_MahoutUserBased,
        kNN_CM_MahoutUserBased_Categorical,
        kNN_CM2_MahoutUserBased_Categorical,
        MF_PRF_Mahout,
        MF_PRF_Mahout_Categorical,
        MF_POF_Mahout,
        MF_FPOF_Mahout,
        MF_WPOF_Mahout,
        MF_POF_Mahout_Categorical,
        MF_FPOF_Mahout_Categorical,
        MF_WPOF_Mahout_Categorical,
        MF_CM_Mahout,
        MF_CM_Mahout_Categorical,
        Bias,
        TimeBias,
        MF_Default,
        MF_Optimized,
        MFBiasTime,
        MFTimeFull,
        ParallelMF,
        ItemPopularity,
        ItemAvg,
        Random,
        TimeDecay_UserBased,
        TimeDecay_UserBased_TrProp,
        TimeDecay_ItemBased,
        MF_Mahout,
        kNN_UB_Mahout,
        kNN_IB_Mahout,
        Hybrid
    }
    
    // Neighborhood-based recommenders
    private int neighbors = 200;
    private int minCommonRatingsForPrediction = 3;
    // Time decay recommenders
    private int halfLifeDays = 90;
    private double halfLifeProp = 0.5;
    private long trTimespan;
    // Pre/Post-filtering recommenders
    List<ContextDefinition> ctxDefs;
    List<TimeContext> timeContexts;
    
    // Factorization-based recommenders
    private int iterations = 60;
    private int factors = 30;
    private double learnRate = 0.02; //0.005
    private double lambda = 0.2; //0.02
    private double learnRateStaticBias = 0.0;
    private double lambdaStaticBias = 0.0;
    private double learnRateBiasBins = 0.005497800639184945;
    private double lambdaBiasBins = 2.759865117127897;
    private double learnRateAlpha = 0.007054647367802872;
    private double lambdaAlpha = 239.21662481276354 ;
    private double beta = 0.0994940769756727;
    private double learnRateDayBias = 0.005792325328112727;
    private double lambdaDayBias = 0.025324542461239156;
    private double learnRateUserAlpha = 0.00631385047326437;
    private double lambdaUserAlpha = 1.6616848104418143;
    private double learnRateDayFactor = 0.037632353045647354;
    private double lambdaDayFactor = 1.274366946311926;
    private int bins = 4;
    private boolean WITH_BIAS = true;
    
    // Parallelization parameters
    private int maxThreads = 8;
    
    // Optimization parameters
    private SplitIF<U,I,C> optSplit;
    private DatasetSplitterIF<U, I, C> optSplitter;
    private int maxOptimizationIterations = 10000;
    private int maxTrainingIterations = 100;
    

    public CommonRecommenders() {
    }
    
    public CommonRecommenders<U,I,C> neighbors(int neighbors){
        this.neighbors = neighbors;
        return this;
    }
    
    public CommonRecommenders<U,I,C> factors(int factors){
        this.factors = factors;
        return this;
    }
    
    public CommonRecommenders<U,I,C> iterations(int iterations){
        this.iterations = iterations;
        return this;
    }
    
    public CommonRecommenders<U,I,C> learnRate(double learnRate){
        this.learnRate = learnRate;
        return this;
    }
    
    public CommonRecommenders<U,I,C> lambda(double lambda){
        this.lambda = lambda;
        return this;
    }
    
    public CommonRecommenders<U,I,C> minCommonRatingsForPrediction(int minCommonRatings){
        this.minCommonRatingsForPrediction = minCommonRatings;
        return this;
    }
    
    public CommonRecommenders<U,I,C> halfLife(int halfLife){
        this.halfLifeDays = halfLife;
        return this;
    }
    
    public CommonRecommenders<U,I,C> halfLifeProp(double halfLifeProp){
        this.halfLifeProp = halfLifeProp;
        return this;
    }
    
    public CommonRecommenders<U,I,C> trTimespan(SplitIF split){
        long timespan = ContinuousTimeUtils.trainingTimespan(split);
        this.trTimespan = timespan;
        return this;
    }
    
    public CommonRecommenders<U,I,C> categoricalContextFilter(List<ContextDefinition> ctxDefs){
        this.ctxDefs = ctxDefs;
        return this;
    }

    public CommonRecommenders<U,I,C> continuousTimeContextFilter(List<TimeContext> timeContexts){
        this.timeContexts = timeContexts;
        return this;
    }

    public CommonRecommenders<U,I,C> optimizationData(DatasetSplitterIF<U, I, C> splitter, SplitIF split){
        optSplit = split;
        optSplitter = splitter;
        return this;
    }
    
    public RecommenderIF<U,I,C> getRecommender(METHOD method, ModelIF<U,I,C> model){
        RecommenderIF<U,I,C> recommender = null;
        MahoutRecommenderFactory<U,I,C> mrf;                
        ContextualSlicer_CategoricalContext categoricalSlicer;
        ContextualSlicer_ContinuousTimeContext continuousTimeSlicer;
        switch (method){
            case kNN_PearsonWeightedSimilarity_UserBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.kNN_PearsonWeightSimilarity_UserBased);
                break;
            case kNN_PearsonWeightedSimilarity_ItemBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.kNN_PearsonWeightSimilarity_ItemBased);
                break;
            case kNN_CosineSimilarity_UserBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.kNN_CosineSimilarity_UserBased);
                break;
            case kNN_CosineSimilarity_ItemBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.kNN_CosineSimilarity_ItemBased);
                break;
            case Raw_CosineSimilarity_UserBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.Raw_CosineSimilarity_UserBased);
                break;
            case Raw_CosineSimilarity_ItemBased:
                recommender = new NeighborBasedRecommenderBuilder<U,I,C>(model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.Raw_CosineSimilarity_ItemBased);
                break;
            case kNN_PRF_UserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_UserBased);
                break;
            case kNN_PRF_UserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_UserBased);
                break;
            case kNN_PRF_MahoutUserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_MahoutUserBased);
                break;
            case kNN_PRF_MahoutUserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_MahoutUserBased);
                break;
            case kNN_POF_UserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_UserBased);
                break;
            case kNN_FPOF_UserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualFilterPostFiltering_UserBased);
                break;
            case kNN_WPOF_UserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualWeightPostFiltering_UserBased);
                break;
            case kNN_POF_UserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_UserBased);
                break;
            case kNN_POF_MahoutUserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_MahoutUserBased);
                break;
            case kNN_FPOF_MahoutUserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualFilterPostFiltering_MahoutUserBased);
                break;
            case kNN_WPOF_MahoutUserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualWeightPostFiltering_MahoutUserBased);
                break;
            case kNN_POF_MahoutUserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_MahoutUserBased);
                break;
            case kNN_CM_MahoutUserBased:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualModeling_MahoutUserBased);
                break;
            case kNN_CM_MahoutUserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualModeling_MahoutUserBased);
                break;
            case kNN_CM2_MahoutUserBased_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringBasedRecommenderBuilder<U,I,C>(model)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualModeling2_MahoutUserBased);
                break;
            case MF_PRF_Mahout:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(continuousTimeSlicer)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_MahoutSVD);
                break;
            case MF_PRF_Mahout_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(categoricalSlicer)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_MahoutSVD);
                break;
            case MF_POF_Mahout:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_MahoutSVD);
                break;
            case MF_FPOF_Mahout:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualFilterPostFiltering_MahoutSVD);
                break;
            case MF_WPOF_Mahout:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(continuousTimeSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualWeightPostFiltering_MahoutSVD);
                break;
            case MF_POF_Mahout_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_MahoutSVD);
                break;
            case MF_FPOF_Mahout_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualFilterPostFiltering_MahoutSVD);
                break;
            case MF_WPOF_Mahout_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(categoricalSlicer)
                    .neighbors(neighbors)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualWeightPostFiltering_MahoutSVD);
                break;
            case MF_CM_Mahout:
                continuousTimeSlicer = new ContextualSlicer_ContinuousTimeContext(timeContexts);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(continuousTimeSlicer)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualModeling_MahoutSVD);
                break;
            case MF_CM_Mahout_Categorical:
                categoricalSlicer = new ContextualSlicer_CategoricalContext(ctxDefs);
                recommender = new ContextFilteringModelBasedRecommenderBuilder<U,I,C>(model)
                    .numFeatures(factors)
                    .numIterations(iterations)
                    .slicer(categoricalSlicer)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualModeling_MahoutSVD);
                break;
            case Bias:
                recommender = new ModelBasedRecommender<U,I,C>(model, new ContextualModelUtils(model), 0, iterations, learnRate, lambda);
                break;
            case TimeBias:
                recommender = new TimeBiasRecommender(model, new ContextualModelUtils(model), 0, iterations, learnRate, lambda, learnRateBiasBins, lambdaBiasBins, learnRateAlpha, lambdaAlpha, beta, learnRateDayBias, lambdaDayBias, bins, bins);
                break;
            case MF_Default:
                recommender = new FactorizationRecommender(model, new ContextualModelUtils(model), factors, 0, iterations, learnRate, lambda, learnRateStaticBias, lambdaStaticBias, false, "EM");
                break;
            case ParallelMF:
                recommender = new FactorizationRecommender(model, new ContextualModelUtils(model), factors, 0, iterations, learnRate, lambda, learnRateStaticBias, lambdaStaticBias, false, maxThreads);
                break;
            case MFBiasTime:    
                recommender = new FactorizationRecommender( model, new ContextualModelUtils(model),  50, 0, 71, 0.0050,0.02,0.0050,0.02,5.0E-5,0.02,5.0E-4,0.02,0.08,5.0E-4,0.3, true, bins, bins, true);
                //recommender = new FactorizationRecommender( model, new ExtendedModel(model),  50, 0, 8, 0.5049999999999999, 0.52, 0.5049999999999999, 0.52, 5.0E-5 ,0.02 , 5.0E-4, 0.02, 0.08, 5.0E-4, 0.3, true, bins, bins, true);
                break;
            case MFTimeFull:
//                recommender = new FactorizationRecommender(model, new ExtendedModel(model), features, 0, iterations, 0.007753004973743459, 0.02489067140028797, 0.001970619750361941, 0.022013935352256923 , 1.0843649355866893E-4, 0.013173932717992325, 1.3512440469673524E-5 , 0.020498566546703345, 0.09431253705289161, 0.010598468129197909, 0.3033035514116042 , 8.869200125510603E-10, 0.0057122194145052966, 1.6373472347643278E-4 , 0.018361427373369897, WITH_BIAS, bins, bins, true);
                recommender = new FactorizationRecommender(model, new ContextualModelUtils(model), factors, 0, iterations, learnRate, lambda, learnRateStaticBias, lambdaStaticBias, learnRateBiasBins, lambdaBiasBins, learnRateAlpha, lambdaAlpha, beta, learnRateDayBias, lambdaDayBias, learnRateUserAlpha, lambdaUserAlpha, learnRateDayFactor, lambdaDayFactor, WITH_BIAS, bins, bins, true);
                break;
            case ItemPopularity:
                recommender = new ItemPopularityBasedRecommender<U,I,C>(model);
                break;                
            case ItemAvg:
                recommender = new ItemAvgRecommender<U,I,C>(model);
                break;
            case Random:
                recommender = new RandomRecommender<U,I,C>(model);
                break;
                
            case TimeDecay_UserBased:
                recommender = (RecommenderIF<U,I,C>) new ContinuousTimeRecommenderBuilder<U,I,ContinuousTimeContextIF>((ModelIF<U,I,ContinuousTimeContextIF>)model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .halfLifeDays(halfLifeDays)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.TimeDecay_UserBased);
                break;
            case TimeDecay_UserBased_TrProp:
                recommender = (RecommenderIF<U,I,C>) new ContinuousTimeRecommenderBuilder<U,I,ContinuousTimeContextIF>((ModelIF<U,I,ContinuousTimeContextIF>)model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .halfLifeProp(halfLifeProp)
                    .trTimespan(trTimespan)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.TimeDecay_UserBased_TrProp);
                break;
            case TimeDecay_ItemBased:
                recommender = (RecommenderIF<U,I,C>) new ContinuousTimeRecommenderBuilder<U,I,ContinuousTimeContextIF>((ModelIF<U,I,ContinuousTimeContextIF>)model)
                    .neighbors(neighbors)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .halfLifeDays(halfLifeDays)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.TimeDecay_ItemBased);
                break;
                
                // Optimization
            case MF_Optimized:
                final Logger logger = Logger.getLogger("ExperimentLog");
                Level level = logger.getLevel();
                logger.setLevel(Level.OFF);
                ModelBasedRecommenderOptimizer.RECOMMENDER_METHOD recommender_method  = ModelBasedRecommenderOptimizer.RECOMMENDER_METHOD.MF;
                RecommenderOptimizerFactory.OPTIMIZATION_METHOD optimization_method  = RecommenderOptimizerFactory.OPTIMIZATION_METHOD.VALIDATION;
                ModelBasedRecommenderOptimizer optimizer = new RecommenderOptimizerFactory<U, I, C>()
                    .getOptimizer(optimization_method, optSplitter, maxOptimizationIterations, maxTrainingIterations, maxThreads);            
                recommender = optimizer
                    .getRecommender(recommender_method, optSplit, factors, bins);
                System.out.println(recommender);
                logger.setLevel(level);
                break;
                
                
            case Hybrid:
                List<String> recommenders = new ArrayList<String>();
//                String path = "/datos/experiments/recommenders/MovieLens100k/";
                String path = "/collections/ivan/temp/pedro/results/MovieLens100k/";
                // Hybrid 1
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid 2
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid 3
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid 4
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid AllInfGain
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfWeek]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[InformationGainImpurity(0.9)_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid AllMean
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfWeek]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[MeanImpurity(4.03)_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
                // Hybrid Proportion
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfDay_Meridian_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfDay_Meridian]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfDay_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfDay]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfWeek]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");
//                recommenders.add(path + "ItemSplit[ProportionImpurity(1.65)_PeriodOfYear]__MF_Default_NonPers=None__Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_USER_MEAN_0.0__recommendations.txt");

                recommender = new HybridOfflineRecommender(optSplit.getTestingSet(), recommenders, "4");                
                break;
            case MF_Mahout:
                mrf = new MahoutRecommenderFactory<U,I,C>()
                        .factors(factors)
                        .iterations(iterations);
                recommender = mrf.getRecommender(MahoutRecommenderFactory.Type.SVDRecommender, model, new ContextualModelUtils<U,I,C>(model));
                break;
            case kNN_UB_Mahout:
                mrf = new MahoutRecommenderFactory<U,I,C>()
                        .neighbors(neighbors);
                recommender = mrf.getRecommender(MahoutRecommenderFactory.Type.kNN_UB, model, new ContextualModelUtils<U,I,C>(model));
                break;
            case kNN_IB_Mahout:
                mrf = new MahoutRecommenderFactory<U,I,C>()
                        .neighbors(neighbors);
                recommender = mrf.getRecommender(MahoutRecommenderFactory.Type.kNN_IB, model, new ContextualModelUtils<U,I,C>(model));
                break;
        }
        return recommender;
    }
}
