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
public class Metric_nDCG<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    protected double userDCG;
    protected double user_iDCG;
    
    
    public Metric_nDCG(List<Integer> levels) {
        super(levels);
        init();
    }
    
    @Override
    protected final void init(){
        relevantsRecommended=0;
        recommendationPosition=0;
        userDCG=0.0;
        user_iDCG=0.0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "nDCG";
    }
    
   protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
        userDCG=0.0;
        user_iDCG=0.0;
        if (userRelevantSet.isEmpty()){
            throw new InvalidParameterException("There are no relevant items for user " + user + "!");
        }
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        if (isRelevant(recommendation,userRelevantSet)){ // item in relevant items
            relevantsRecommended++;
            
            // Computing DCG at this position
            double denominator=1;
            if (recommendationPosition > 1){
                denominator=Math.log(recommendationPosition+1)/Math.log(2);
            }
            double DCG_position=1.0/denominator;
            
            
            // Accumulating values
            userDCG+=DCG_position;
            
        }
        
        double nDCG=nDCG(relevantsRecommended, userRelevantSet.size(), recommendationPosition);
        return nDCG;
    }
    
    private double nDCG(int relevantsRecommendedSoFar, int relevantItemsSize, int position){
        // Computing iDCG at this position
        double ideal_numerator=1;
        double ideal_denominator=1;
        if (position > 1){
            if (position <=  relevantItemsSize){
                ideal_denominator=Math.log(position+1)/Math.log(2);
            }
            else{
                ideal_denominator=Math.log(relevantItemsSize+1)/Math.log(2);
                ideal_numerator=0.0;
            }
        }
        double iDCG_position=ideal_numerator/ideal_denominator;
        user_iDCG+=iDCG_position;


        
        // Computing nDCG at actual level
        double nDCG=0.0;
        if (user_iDCG>0.0){
            nDCG=userDCG/user_iDCG;
        }

        return nDCG;
    }


    public void reset() {
        init();
    }

}
