package es.uam.eps.ir.metrics.list;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.metrics.RecommendationListMetricIF;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_CandidateItemsPersonalized<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int personalizedPredictionsCount;
    
    public Metric_CandidateItemsPersonalized() {
        super(null);
        init();
    }
    
    @Override
    protected final void init(){
        personalizedPredictionsCount=0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "Pers_List";
    }
    
    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        personalizedPredictionsCount = 0;
    }

    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        // If the engine was able to compute a personalized recommendation
        if (recommendation.isPersonalized()){
            // count as covered
            personalizedPredictionsCount++;
        }
        
        double user_cov = (double) personalizedPredictionsCount / (userRelevantSet.size() + userNotRelevantSet.size());
        return user_cov;
    }

    public void reset() {
        init();
    }
    
}
