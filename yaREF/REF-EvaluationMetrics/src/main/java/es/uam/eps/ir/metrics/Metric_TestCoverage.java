package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.split.SplitIF;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_TestCoverage<U,I,C extends ContextIF> extends AbstractPredictionMetric<U,I,C> implements MetricIF<U,I,C>{
    SplitIF<U,I,C> split;
    ContextualModelUtils<U,I,C> eModelTest;
    protected Map<U,Integer> userPredsMap;
    protected Map<U,Integer> userTotalMap;
    protected U lastUserID;
    protected int usersCount;
    protected int predictionsCount;
    protected int totalCount;
    protected double acummError;
    
    public Metric_TestCoverage(SplitIF<U,I,C> split) {
        super();
        this.split = split;
        init();
    }
    
    @Override
    protected final void init(){
        userMetric=new HashMap();
        userPredsMap=new HashMap();
        userTotalMap=new HashMap();
        lastUserID=null;
        usersCount=0;
        predictionsCount=0;
        totalCount=0;
        acummError=0.0;
        this.eModelTest = new ContextualModelUtils<U,I,C>(split.getTestingSet());
        
        super.init();
    }

    @Override
    public String shortName() {
        return "Cov_Test";
    }
    

    @Override
    protected void finishComputation() {
        // Computing overall Test Coverage
        metric=(double)predictionsCount/(double)totalCount;
        
        super.finishComputation();
    }


    protected void processNextRecommendation(U userID, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet) {
//    protected void processNextRecommendation(Object userID, Recommendation recommendation, TrainingTestSplit sample) {
        if (lastUserID != userID){ // Processing a new recommendation list
            usersCount++;
            lastUserID=userID;
            userMetric.put(userID, new Double(0.0));
            userPredsMap.put(userID, new Integer(0));
            userTotalMap.put(userID, new Integer(0));
        }
        
        // If recommended item is not part of test set, then no further processing is needed
        I itemID=recommendation.getItemID();
        if (split.getTestingSet().getPreferences(userID, itemID) == null)
            return;
        
        
        // count as covered
        userPredsMap.put(userID, userPredsMap.get(userID) + 1);
        predictionsCount++;
        
        // Total count
        userTotalMap.put(userID, eModelTest.getUserAggregatedRatingCount(userID));
        totalCount=eModelTest.getAggregatedRatingCount();

        
        double user_cov=(double)userPredsMap.get(userID) / (double)userTotalMap.get(userID);
        userMetric.put(userID, user_cov);
    }
    

    public void reset() {
        init();
    }

    
}
