package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.context.ContextIF;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_Recall<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    
    public Metric_Recall(List<Integer> levels) {
        super(levels);
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
        return "R";
    }

    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
        if (userRelevantSet.isEmpty()){
            throw new InvalidParameterException("There are no relevant items for user " + user + "!");
        }
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        if (isRelevant(recommendation,userRelevantSet)){ // item in relevant items
            relevantsRecommended++;
        }

        // Computing Recall at actual level
        double recall = (double)relevantsRecommended/(double)userRelevantSet.size();        
        return recall;
    }
    
    public void reset() {
        init();
    }

}
