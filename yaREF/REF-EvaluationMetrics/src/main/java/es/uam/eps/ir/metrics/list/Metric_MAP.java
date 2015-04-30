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
public class Metric_MAP<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    protected double userAverageP;
    
    
    public Metric_MAP(List<Integer> levels) {
        super(levels);
        init();
    }
    
    @Override
    protected final void init(){
        relevantsRecommended=0;
        recommendationPosition=0;
        userAverageP=0.0;
                
        super.init();
    }

    @Override
    public String shortName() {
        return "MAP";
    }
    
    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
        userAverageP=0.0;
        if (userRelevantSet.isEmpty()){
            throw new InvalidParameterException("There are no relevant items for user " + user + "!");
        }
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        if (isRelevant(recommendation,userRelevantSet)){ // item in relevant items
            relevantsRecommended++;
            
            // Computing precision at actual level
            double precision=(double)relevantsRecommended/(double)recommendationPosition;
            // Accumulating values
            userAverageP+=precision;
        }

        
        // Computing MAP at actual level
        double MAP=0.0;
        MAP=userAverageP/(double)userRelevantSet.size();
        
        return MAP;
    }
    
    public void reset() {
        init();
    }

}
