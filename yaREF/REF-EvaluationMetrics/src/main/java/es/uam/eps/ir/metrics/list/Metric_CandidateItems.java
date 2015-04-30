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
public class Metric_CandidateItems<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    
    public Metric_CandidateItems(List<Integer> levels) {
        super(null);
        init();
    }
    
    @Override
    protected final void init(){
        relevantsRecommended=0;
        recommendationPosition=0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "Targets";
    }

    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
    }
    

    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        if (isRelevant(recommendation,userRelevantSet)){ // item in relevant items
            relevantsRecommended++;
        }

        // Computing Recall at actual level
        double items = recommendationPosition;
        return items;
    }
    
    public void reset() {
        init();
    }
}
