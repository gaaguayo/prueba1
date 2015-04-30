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
public class Metric_F1<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    protected double userAverageP;
    
    
    public Metric_F1(List<Integer> levels) {
        super(levels);
        init();
    }
    
    @Override
    protected synchronized final void init(){
        relevantsRecommended=0;
        recommendationPosition=0;
        userAverageP=0.0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "F";
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
        }

        // Computing precision at actual level
        double precision = (double)relevantsRecommended/(double)recommendationPosition;
        // Computing Recall at actual level
        double recall = (double)relevantsRecommended/(double)userRelevantSet.size();
        
        // Computing F1 at actual level
        double F1=0.0;
        if (precision+recall > 0.0){
            F1=2*precision*recall/(precision+recall);
        }
        return F1;
    }
    

    public void reset() {
        init();
    }

}
