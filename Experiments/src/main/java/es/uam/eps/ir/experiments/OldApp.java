package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.cars.recommender.NeighborBasedRecommenderBuilder;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.Movielens100kDataset;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.list.Metric_Precision;
import es.uam.eps.ir.metrics.error.Metric_RMSE;
import es.uam.eps.ir.metrics.list.Metric_Recall;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.rank.CandidateItemsIF;
import es.uam.eps.ir.rank.CandidateItems_CommunityTest;
import es.uam.eps.ir.split.baseset.BaseSetGeneratorIF;
import es.uam.eps.ir.split.baseset.BSCommunity;
import es.uam.eps.ir.split.impl.DatasetSplitterBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.sizecondition.SCProportion;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.split.sizecondition.SizeConditionIF;
import es.uam.eps.ir.split.impl.DatasetSplitter_Holdout;
import es.uam.eps.ir.split.ratingorder.ROTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;
/**
 *
 */
public class OldApp 
{
    public static void main( String[] args )
    {
        // Logger init
        //////////////
        Logger logger = Logger.getLogger("MyLog");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.info("Starting experiment");
        
        // Data load
        ////////////
        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset;
        dataset = new Movielens100kDataset();
        ModelIF<Object, Object, ContinuousTimeContextIF> model = dataset.getModel();
        
        // Training-test spliting
        /////////////////////////////////
        // BaseSet
        BaseSetGeneratorIF<Object, Object, ContinuousTimeContextIF> baseSet;
        baseSet = new BSCommunity<Object, Object, ContinuousTimeContextIF>();
        
        // RatingOrder
        RatingOrderIF<Object, Object, ContinuousTimeContextIF> ratingOrder;
        ratingOrder = new ROTime<Object, Object, ContinuousTimeContextIF>();
        
        // Size
        SizeConditionIF<Object, Object, ContinuousTimeContextIF> sizeCondition;
        sizeCondition = new SCProportion<Object, Object, ContinuousTimeContextIF>((float)0.2);
        
        // Splitting
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter = new DatasetSplitter_Holdout(baseSet, ratingOrder, sizeCondition);
        SplitIF<Object, Object, ContinuousTimeContextIF> splits[] = splitter.split(model);
        
        // Process each split
        for (SplitIF<Object, Object, ContinuousTimeContextIF> split:splits){
            ModelIF<Object, Object, ContinuousTimeContextIF> trainSet = split.getTrainingSet();
            ModelIF<Object, Object, ContinuousTimeContextIF> testSet  = split.getTestingSet();
            ContextualModelUtils<Object, Object, ContinuousTimeContextIF> eTrain = new ContextualModelUtils<Object, Object, ContinuousTimeContextIF>(trainSet);

            System.out.println("tr: " + trainSet.getUsers().size() + " users, "+ trainSet.getItems().size() + " items, " + eTrain.getRatingCount() + " ratings");
            System.out.println("te: " + testSet.getUsers().size() + " users, "+ testSet.getItems().size() + " items");

            // Relevants & Non-Relevants
            ////////////////////////////
            CandidateItemsIF<Object, Object, ContinuousTimeContextIF> candidateItems;
            candidateItems = new CandidateItems_CommunityTest<Object, Object, ContinuousTimeContextIF>(split, (float)0.0);


            // Recommender initialization
            /////////////////////////////
            RecommenderIF<Object, Object, ContinuousTimeContextIF> recomm;
            recomm = new NeighborBasedRecommenderBuilder<Object, Object, ContinuousTimeContextIF>(trainSet)
                    .neighbors(100)
                    .buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.kNN_PearsonWeightSimilarity_UserBased);

            // Metrics initialization
            /////////////////////////
            List<Integer> levels = Arrays.asList(5,10,20,50);
            MetricIF<Object, Object, ContinuousTimeContextIF> metrics[];
            metrics = new MetricIF[3];
            metrics[0] = new Metric_RMSE<Object, Object, ContinuousTimeContextIF>(splits[0]);
            metrics[1] = new Metric_Precision<Object, Object, ContinuousTimeContextIF>(levels);
            metrics[2] = new Metric_Recall<Object, Object, ContinuousTimeContextIF>(levels);

            // Metrics computation
            //////////////////////
            for (Object user:testSet.getUsers()){
                int j = 0;
                // Determines items to evaluate
                Set<Object> userRelevantSet = candidateItems.getRelevantSet(user, null);
                Set<Object> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

                Set<Object> itemsToEvaluate = new TreeSet<Object>();
                itemsToEvaluate.addAll(userRelevantSet);
                itemsToEvaluate.addAll(userNotRelevantSet);

                // Computes predictions for each item
                List<RecommendationIF<Object>> userRecommendations = new ArrayList<RecommendationIF<Object>>();
                for (Object item:itemsToEvaluate){
                    Float prediction = recomm.predict(user, item, null);
                    RecommendationIF<Object> recom;
                    if (!prediction.isNaN()){
                        recom = new Recommendation(item, prediction, true);
                        userRecommendations.add(recom);
                    }
                }
                Collections.sort(userRecommendations);

                // Print relevant set
    //            for (Object item:userRelevantSet){
    //                System.out.println(user + "\t0\t" + item + "\t1");
    //            }
                // Print recommendations
    //            for (RecommendationIF<Object> r:userRecommendations){
    //                System.out.println(user + "\t1\t" + r.getItemID() + "\t" + ++j + "\t" + r.getValue() + "\t0");
    //            }

                // Compute metrics
                for (int i = 0; i < metrics.length; i++){
                    metrics[i].processUserList(user, userRecommendations, userRelevantSet, userNotRelevantSet);
                }
            }

            // Metrics printing
            ///////////////////
            System.out.println("Results");
            for (int i = 0; i < metrics.length; i++){
                MetricResultsIF<Object> results = metrics[i].getResults();
                System.out.println(results.columnFormat());
            }
            
        }
    }
}
