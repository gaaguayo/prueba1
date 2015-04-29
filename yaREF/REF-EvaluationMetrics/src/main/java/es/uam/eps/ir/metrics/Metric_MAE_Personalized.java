package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_MAE_Personalized<U,I,C extends ContextIF> extends AbstractPredictionMetric<U,I,C> implements MetricIF<U,I,C>{
    protected SplitIF<U,I,C> split;
    protected Map<U,Integer> userPredsMap;
    protected U lastUserID;
    protected int usersCount;
    protected int predictionsCount;
    protected double acummError;
    
    public Metric_MAE_Personalized(SplitIF<U,I,C> split) {
        super();
        this.split = split;
        init();
    }
    
    @Override
    protected final void init(){
        userMetric=new HashMap();
        userPredsMap=new HashMap();
        lastUserID=null;
        usersCount=0;
        predictionsCount=0;
        acummError=0.0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "MAE_Pers";
    }
    

    @Override
    protected void finishComputation() {
        //Computing per-user MAE
        for (U userID:userMetric.keySet()){
            double userMAE=userMetric.get(userID)/(double)userPredsMap.get(userID);
            userMetric.put(userID, userMAE);
        }
        
        // Computing overall MAE
        metric=acummError/(double)predictionsCount;
        
        super.finishComputation();
    }


    protected void processNextRecommendation(U userID, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet) {
//    protected void processNextRecommendation(Object userID, Recommendation recommendation, TrainingTestSplit sample) {
        if (lastUserID != userID){ // Processing a new recommendation list
            usersCount++;
            lastUserID=userID;
            userMetric.put(userID, new Double(0.0));
            userPredsMap.put(userID, new Integer(0));
        }
        
        // If recommended item is not part of test set, then no further processing is needed
        I itemID=recommendation.getItemID();
        if (split.getTestingSet().getPreferences(userID, itemID) == null)
            return;
        
        if (!recommendation.isPersonalized())
            return;
        
        // Process Recommendation
        userPredsMap.put(userID, userPredsMap.get(userID) + 1);
        predictionsCount++;
        
        // Computing error
        Float rating=split.getTestingSet().getPreferenceValue(userID, itemID, null);
        double error=Math.abs(recommendation.getValue() - rating);
        
        //Acummulating error
        acummError+=error;
        userMetric.put(userID, userMetric.get(userID) + error);
    }
    

    public void reset() {
        init();
    }

    
}
