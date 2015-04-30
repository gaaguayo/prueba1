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
public class Metric_AUC<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;
    protected int nonRelevantsRecommended;
    protected int numRelevantsAbove;
    protected int numCorrectPairs;
        
    public Metric_AUC() {
        super(null);
        init();
    }
    
    @Override
    protected synchronized final void init(){
        relevantsRecommended=0;
        nonRelevantsRecommended=0;
        recommendationPosition=0;
        numRelevantsAbove=0;
        numCorrectPairs=0;
        
        super.init();
    }

    @Override
    public String shortName() {
        return "AUC";
    }
    
    @Override
    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
        nonRelevantsRecommended=0;
        numRelevantsAbove=0;
        numCorrectPairs=0;
    }

    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        
        // Computing pairs correctly ranked
        if (isRelevant(recommendation,userRelevantSet)){ // item in relevant items
            relevantsRecommended++;
            numRelevantsAbove++;
        }
        else {
            nonRelevantsRecommended++;
            numCorrectPairs+=numRelevantsAbove;
        }
        
        // computing number of pairs evaluated
        int pairsEvaluated= relevantsRecommended*nonRelevantsRecommended;

        // Computing ranking ordering at actual level
        double rankingStrength=0.0;
        if (pairsEvaluated>0){
            rankingStrength=(double)numCorrectPairs/(double)pairsEvaluated;
        }
        
        // Computing AUC at actual level
        double AUC = rankingStrength;
        return AUC;
    }
    
    public void reset() {
        init();
    }

}
