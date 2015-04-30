package es.uam.eps.ir.metrics.error;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author pedro
 */
public class Metric_RMSE_Personalized<U,I,C extends ContextIF> extends AbstractErrorMetric<U,I,C> implements MetricIF<U,I,C>{
    SplitIF<U,I,C> split;
    protected Map<Object,Integer> userPredsRmse;
    protected Object lastUserID;
    protected int usersCount;
    protected int predictionsCount;
    protected double acummSqError;
    
    public Metric_RMSE_Personalized(SplitIF<U,I,C> split) {
        super();
        this.split=split;
        init();
    }
    
    @Override
    protected final void init(){
        userMetric=new HashMap();
        userPredsRmse=new HashMap();
        lastUserID=null;
        usersCount=0;
        predictionsCount=0;
        acummSqError=0.0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "RMSE_Pers";
    }
    

    @Override
    protected void finishComputation() {
        //Computing per-user RMSE
        for (U userID:userMetric.keySet()){
            double userRMSE=Math.sqrt(userMetric.get(userID)/(double)userPredsRmse.get(userID));
            userMetric.put(userID, userRMSE);
        }
        
        // Computing overall MAE
        metric=Math.sqrt(acummSqError/(double)predictionsCount);
        
        super.finishComputation();
    }


    protected void processNextRecommendation(U userID, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet) {
        if (lastUserID != userID){ // Processing a new recommendation list
            usersCount++;
            lastUserID=userID;
            userMetric.put(userID, new Double(0.0));
            userPredsRmse.put(userID, new Integer(0));
        }
        
        
        // If recommended item is not part of test set, then no further processing is needed
        I itemID=recommendation.getItemID();
        if (split.getTestingSet().getPreferenceValue(userID, itemID, null) == null)
            return;
        
        if (!recommendation.isPersonalized())
            return;
        
        // Process Recommendation
        userPredsRmse.put(userID, userPredsRmse.get(userID) + 1);
        predictionsCount++;
        
        // Computing error
        float rating=split.getTestingSet().getPreferenceValue(userID, itemID, null);
        double error=Math.abs(recommendation.getValue() - rating);
        double sqError=error*error;
        
        //Acummulating error
        acummSqError+=sqError;
        userMetric.put(userID, userMetric.get(userID) + sqError);
    }
    

    public void reset() {
        init();
    }

    public int getNumberOfEvaluations(){
        return this.predictionsCount;
    }
}
