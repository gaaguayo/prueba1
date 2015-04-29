package es.uam.eps.ir.metrics;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.split.SplitIF;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author pedro
 */
public class Metric_SelfInformation<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected SplitIF<U,I,C> split;
    ContextualModelUtils<U,I,C> eModelTraining;
    protected int relevantsRecommended;
        
    protected int lastLevel;
    protected boolean averageValue;
    
    protected double userSelfInformationAccum;
    
    public Metric_SelfInformation(List<Integer> levels) {
        super(levels);
        init();
        lastLevel=levels.get(levels.size()-1);
        averageValue=true;
}
    
    protected final void init(SplitIF<U,I,C> split){
        recommendationPosition=0;
        this.split = split;
        this.eModelTraining = new ContextualModelUtils<U,I,C>(split.getTrainingSet());
        
        super.init();
    }

    @Override
    public String shortName() {
        return "SelfInformation";
    }

    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        userSelfInformationAccum=0.0;
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        
        // Computing popularity of item
        I itemID=recommendation.getItemID();
        int itemRatings=0;
        Collection<U> usersList=eModelTraining.getUsersWhomRate(itemID);
        if (usersList != null){
            itemRatings=usersList.size();
        }
        int totalUsers=split.getTrainingSet().getUsers().size();
        double itemPopularity=itemRatings/(double)totalUsers;
        
        // Computing self information of item
        double itemSelfInformation=0.0;
        if (itemPopularity>0.0){
                itemSelfInformation=Math.log(1.0/itemPopularity)/Math.log(2);
        }
        
        // Accumulating for the user's list
        userSelfInformationAccum+=itemSelfInformation;

        double denominator=1;
        if (averageValue){
            denominator=recommendationPosition;
        }
        
        // Computing Recall at actual level
        double I = userSelfInformationAccum / denominator;
        return I;
    }
    
    public void reset() {
        init();
    }

}
