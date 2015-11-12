package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.itemsplitting.FisherExactImpurity;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import java.util.Arrays;
import java.util.Random;
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
        CONFIG_FILE = "F:\\experiments\\global.config";
//        CONFIG_FILE = "/home/pedro/experiments/global.config";
        // Dataset
        dataset_name = CommonDatasets.DATASET.MovieLens100k;
        // Dataset_ItemSplit
        item_split = false;
        minContextSizeForSplitting = 5;
//        is_impurity = new MeanImpurity(1.7); // kNN min3, Mean1.7, IG0.6 Popr1.0
        is_impurity = new FisherExactImpurity(0.4); // MF-30-10 min3, Mean1.7
//        is_contexts = Arrays.asList(TimeContextItemSplitter.TimeContext.PeriodOfDay1.name());
        is_contexts = Arrays.asList("cuadrante");
//        filtering_contexts = Arrays.asList("Company","Week","Day");
//        filtering_contexts = Arrays.asList("daytype");
//        filtering_contexts = Arrays.asList("cuadrante");
//        filtering_contexts = Arrays.asList("PeriodOfDay");
    
        // Evaluation Methodology
        split_method        = CommonDatasetSplitters.METHOD.CommunityRandomOrderProportionHoldout;
        testProportion = 0.1;  // 0.085 for Ml100k // 0.054 for Ml1m
        testSize = 10;
        candidates    = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST;
        relevance_threshold=(float) 4.0;
        nForOnePlusRandom = 10;
        
        non_personalized = NonPersonalizedPrediction.Type.OverallMean;
        controlPredictionValue = true;
        genTrainingAndTestFiles = false;
    
        // Evaluation Metrics
        rankingMetrics = CommonRankingMetrics.METRICS.BASIC;
        errorMetrics = CommonErrorMetrics.METRICS.ALL;
        levels = Arrays.asList(5,10,20,50);
    
//      // Recommendation method
//        recommender_method  = CommonRecommenders.METHOD.ContextualPRF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_POF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.kNN_FPOF_UserBased;
//        recommender_method  = CommonRecommenders.METHOD.ContextualPRF_UserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.ContextualPOF_UserBased_Categorical;
//        recommender_method  = CommonRecommenders.METHOD.ContextualPRF_MahoutUserBased;
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
        iterations = 30;
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
        level = Level.SEVERE;
    
        // execution statistics
        maxMem=0;
        exec_experiment(args);
    }    
}
