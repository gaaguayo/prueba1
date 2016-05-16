package es.uam.eps.ir.experiments;

import ImplicitToExplicitTransformation.TransformationManager;
import es.uam.eps.ir.cars.itemsplitting.FisherExactImpurity;
import es.uam.eps.ir.cars.itemsplitting.InformationGainImpurity;
import es.uam.eps.ir.cars.itemsplitting.MeanImpurity;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
/**
 *
 */
public class Experiment extends Experiment_Main
{
    public static void main( String[] args )
    {
    
        //////////////////////
        // PARAMETER VALUES //
        //////////////////////
        CONFIG_FILE = "F:/experiments/global.config";
//        CONFIG_FILE = "/home/pedro/experiments/global.config";
        // Dataset
        dataset_name = CommonDatasets.DATASET.Context_LDOS_CoMoDa;
        // Dataset_ItemSplit
        impurity = CommonImpurityComputers.IMPURITY.NoSplitting;
        is_impurityThreshold = 1.9673; //Proportion/Mean: 1.9673; Chi: 3.84
        is_valueThreshold=8;
        minContextSizeForSplitting = 5;
//        is_contexts = Arrays.asList(TimeContextItemSplitter.TimeContext.PeriodOfDay1.name());
//        is_contexts = Arrays.asList(TimeContext.PeriodOfDay1.name());
//        is_contexts = Arrays.asList("PeriodOfWeek");
//        is_contexts = Arrays.asList("PeriodOfDay");
//        is_contexts = Arrays.asList("day","week","company");
        is_contexts = Arrays.asList("time","daytype","season","location","weather","social","endEmo","dominantEmo","mood","physical","decision","");
//        is_contexts = Arrays.asList("quadrant");
//        is_contexts = Arrays.asList("year","company","daytype","release_weekend","will_recommend");
//        is_contexts = Arrays.asList("year","company","daytype","release_weekend");
//        is_contexts = Arrays.asList("will_recommend");
//        filtering_contexts = Arrays.asList("year","company","daytype","release_weekend","will_recommend");
        is_contexts = Arrays.asList("daytype");
//        filtering_contexts = Arrays.asList("Company","Week","Day");
//        filtering_contexts = Arrays.asList("daytype","conpany");
//        filtering_contexts = Arrays.asList("quadrant");
//        filtering_contexts = Arrays.asList("PeriodOfWeek");
    
        // Dataset_ImplicitToExplicitFeedback
        applyImplicitToExplicitFeedbackTransformation = false; // Perform context-aware implicit to explicit feeback transformation?
        contextsToAnalyze = null;
        transformationMethod = TransformationManager.TransformationMethod.Basic;
        tranformationLevel = TransformationManager.TransformationLevel.IndividualContext;
        
        
        // Evaluation Methodology
        split_method        = CommonDatasetSplitters.METHOD.CommunityXFold_CV;
        testProportion = 0.1;
        testSize = 5;
        candidates    = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST;
        relevance_threshold=(float) 1.0;
        nForOnePlusRandom = 10;
        
        non_personalized = NonPersonalizedPrediction.Type.None;
        trimPredictionValue = true;
        genTrainingAndTestFiles = false;
    
        // Evaluation Metrics
        rankingMetrics = CommonRankingMetrics.METRICS.NONE;
        errorMetrics = CommonErrorMetrics.METRICS.ALL;
        levels = Arrays.asList(5,10,20,50);
    
//      // Recommendation method
//        recommender_method  = CommonRecommenders.METHOD.kNN_PRF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_POF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_FPOF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_PRF_UserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.kNN_POF_UserBased_Categorical;
//        //recommender_method  = CommonRecommenders.METHOD.kNN_PRF_MahoutUserBased;
//        recommender_method  = CommonRecommenders.METHOD.ContextualPOF_MahoutUserBased;
//        recommender_method  = CommonRecommenders.METHOD.ContextualFPOF_MahoutUserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_PRF_MahoutUserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.kNN_POF_MahoutUserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.kNN_CM_MahoutUserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_CM_MahoutUserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.kNN_CM2_MahoutUserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_ItemBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.MF_PRF_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.MF_PRF_Mahout_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.MF_POF_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.MF_POF_Mahout_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.MF_FPOF_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.MF_FPOF_Mahout_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.MF_WPOF_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.MF_WPOF_Mahout_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.MF_CM_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.MF_CM_Mahout_Categorical;
        
//        recommender_method  = CommonRecommenders.METHOD.kNN_CosineSimilarity_ItemBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_CosineSimilarity_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.Raw_CosineSimilarity_ItemBased;
//        recommender_method  = CommonRecommenders.METHOD.Raw_CosineSimilarity_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp;
//        recommender_method  = CommonRecommenders.METHOD.TimeDecay_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.ItemPopularity;
//        recommender_method  = CommonRecommenders.METHOD.ItemAvg;
//        recommender_method  = CommonRecommenders.METHOD.Random;
//        recommender_method  = CommonRecommenders.METHOD.TimeContextItemSplitting_KNN;
//        recommender_method  = CommonRecommenders.METHOD.MF_Default;
//        recommender_method  = CommonRecommenders.METHOD.MF_Optimized;
//        recommender_method  = CommonRecommenders.METHOD.Hybrid;
//        recommender_method  = CommonRecommenders.METHOD.MF_Mahout;
        recommender_method  = CommonRecommenders.METHOD.kNN_UB_Mahout;
//        recommender_method  = CommonRecommenders.METHOD.kNN_IB_Mahout;
    
    
        neighbors = 30;
        minCommonRatingsForPrediction = 3;
        halfLifeProportion = 0.0;
        halfLifeDays = 200;
    
        // Only for optimization of Model-based recommenders
        factors = 60;    
        iterations = 60;
        learnRate = 0.005;
        lambda = 0.02;


        // Parallel processor?
        useParallelProcessor = false;
        maxThreads = 1;
        // Use validation data
        useValidationData = false;
        // Training Data Filtering
        train_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
        // Test Data Filtering
        test_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
    
        // Results Saving
        skipIfResultsExist = false;
        SAVE_RESULTS = true;                 //TRUE!!!!!!!!!!!!!
        SAVE_CLASSIFICATION_RESULTS = false;  //TRUE!!!!!!!!!!!!!
        SAVE_DETAILED_RESULTS = false;
        SEND_EMAIL_REPORT=false;
        SEND_RESULT_FILES=false;
        EXPERIMENT_PREFIX = "";
        EXPERIMENT_DESCRIPTION = "";
        RESULTS_PATH = "F:/experiments/"; //"/datos/experiments/";
        level = Level.INFO;
    
        // execution statistics
        maxMem=0;
        exec_experiment(args);
    }    
}
