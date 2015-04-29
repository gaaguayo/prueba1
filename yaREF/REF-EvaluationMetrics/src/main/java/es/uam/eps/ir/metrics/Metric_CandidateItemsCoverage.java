package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_CandidateItemsCoverage<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int predictionsCount;
    
    public Metric_CandidateItemsCoverage() {
        super(null);
        init();
    }
    
    @Override
    protected final void init(){
        predictionsCount=0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "Cov_List";
    }
    
    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        predictionsCount = 0;
    }

    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        // count as covered
        predictionsCount++;
        
        double user_cov = (double) predictionsCount / (userRelevantSet.size() + userNotRelevantSet.size());
        return user_cov;
    }

    public void reset() {
        init();
    }
    
}
