package es.uam.eps.ir.metrics;

import es.uam.eps.ir.cars.neighborhood.RatingItemSimilarityMatrix;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author pedro
 */
public class Metric_ILS_CF<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected int relevantsRecommended;    
    protected boolean averageValue;
    
    protected double userSimAccum;
    protected Set<I> userRecommendedItems;
    protected RatingItemSimilarityMatrix<U,I,C> simMatrix;
    protected SimilarityComputerIF<U,I,C> simComputer;
    
    public Metric_ILS_CF(List<Integer> levels, SimilarityComputerIF<U,I,C> simComputer) {
        super(levels);
        averageValue=true;
        this.simComputer=simComputer;
    }
    
    protected final void init(SplitIF<U,I,C> sample){
        recommendationPosition=0;
        simMatrix=new RatingItemSimilarityMatrix(sample.getTrainingSet(), simComputer);
        
        super.init();
    }

    @Override
    public String shortName() {
        return "ILS_CF";
    }

    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        relevantsRecommended=0;
        userRecommendedItems=new HashSet();
        userSimAccum=0.0;
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        double denominator=1.0;

        // Compute only untill reaching the last level required (performance requirement)
        if (recommendationPosition > maxLevel){
            throw new RuntimeException("maxLevel reached");
        }

        // Computing similarity of the actual item with previous items
        double simAccum=0.0;
        if (recommendation != null){ // If recommendation list length is less than maxLevel
            for (I otherItemID:userRecommendedItems){
                double similarity=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
                if (!Double.isInfinite(similarity)){
                    simAccum+=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
                }
            }
        }
        userSimAccum+=simAccum;

        // Computing average similarity
        if (averageValue && recommendationPosition>1){
            denominator=(recommendationPosition*(recommendationPosition-1))/2;
        }

        // Adding the new recommendation to recommended items
        if (recommendation != null){
            userRecommendedItems.add(recommendation.getItemID());
        }
            
        double ILS = userSimAccum / denominator;
        return ILS;
    }

    public void reset() {
//        init();
    }

    public void reset(SplitIF<U,I,C> sample) {
        init(sample);
    }
    
}
