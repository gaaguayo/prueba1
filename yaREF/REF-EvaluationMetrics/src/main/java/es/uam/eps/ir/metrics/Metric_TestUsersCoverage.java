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
public class Metric_TestUsersCoverage<U,I,C extends ContextIF> extends AbstractPredictionMetric<U,I,C> implements MetricIF<U,I,C>{
    SplitIF<U,I,C> split;
    ContextualModelUtils<U,I,C> eModelTest;
    protected Map<U,Integer> userPredsMap;
    protected Map<U,Integer> userTotalMap;
    protected U lastUserID;
    protected int usersCount;
    protected int predictionsCount;
    protected int totalCount;
    protected double acummError;
    
    public Metric_TestUsersCoverage(SplitIF<U,I,C> split) {
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
        return "Cov_TestUsers";
    }
    

    @Override
    protected void finishComputation() {
        // Computing overall Test Coverage
        metric=(double)usersCount/(double)split.getTestingSet().getUsers().size();
        
        super.finishComputation();
    }


    protected void processNextRecommendation(U userID, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet) {
        if (lastUserID != userID){ // Processing a new recommendation list
            usersCount++;
            lastUserID=userID;
        }        
    }
    

    public void reset() {
        init();
    }
    
}
