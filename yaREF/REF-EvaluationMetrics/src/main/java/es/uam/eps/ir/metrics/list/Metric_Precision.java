package es.uam.eps.ir.metrics.list;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.metrics.RecommendationListMetricIF;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class Metric_Precision<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;        
    
    public Metric_Precision(List<Integer> levels) {
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
        return "P";
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

        // Computing precision at actual level
        double precision=(double)relevantsRecommended/(double)recommendationPosition;
        return precision;
    }
    
    public void reset() {
        init();
    }

}
