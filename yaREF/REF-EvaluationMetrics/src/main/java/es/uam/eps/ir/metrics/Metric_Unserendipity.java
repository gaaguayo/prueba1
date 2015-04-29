package es.uam.eps.ir.metrics;

import es.uam.eps.ir.cars.neighborhood.RatingItemSimilarityMatrix;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
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
public class Metric_Unserendipity<U,I,C extends ContextIF> extends AbstractRecommendationListMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected SplitIF<U,I,C> split;
    protected ContextualModelUtils<U,I,C> eModelTraining;
    Collection<I> userHistory;
    protected int relevantsRecommended;
    
    protected int lastLevel;
    protected boolean averageValue;
    
    protected double userUnserendipityAccum;
    protected RatingItemSimilarityMatrix simMatrix;
    protected SimilarityComputerIF<U,I,C> simComputer;
    
    public Metric_Unserendipity(List<Integer> levels, SimilarityComputerIF<U,I,C> simComputer) {
        super(levels);
        lastLevel=levels.get(levels.size()-1);
        averageValue=true;
        this.simComputer=simComputer;
}

    protected final void init(SplitIF<U,I,C> split){
        recommendationPosition=0;
        simMatrix=new RatingItemSimilarityMatrix(split.getTrainingSet(), simComputer);
        
        super.init();
    }

    @Override
    public String shortName() {
        return "Unserendipity";
    }
    
    protected void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        recommendationPosition=0;
        userUnserendipityAccum=0.0;
        userHistory = eModelTraining.getItemsRatedBy(user);
    }
    
    @Override
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        
        // Computing similarity of the actual item with items in user's history       
        double simAccum=0.0;
        int userHistorySize=0;
        if (userHistory !=  null && recommendation != null){
            for (Object otherItemID:userHistory){
                double similarity=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
                if (!Double.isInfinite(similarity)){                
                    simAccum+=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
                }
            }
            userHistorySize=userHistory.size();
        }
        userUnserendipityAccum+=simAccum;
        
        // Computing average similarity
        double denominator=1;
        if (averageValue && userHistorySize>0){
            denominator=recommendationPosition*userHistorySize;
        }

        // Computing Unserendipity at actual level
        double unserendipity = userUnserendipityAccum/denominator;
        return unserendipity;
    }
    
    

    protected void processNextRecommendation(U userID, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet) {
////    protected void processNextRecommendation(Object userID, Recommendation recommendation, TrainingTestSplit sample, Set relevantItems) {
//        if (lastUserID != userID){ // Processing a new recommendation list
//            recommendationPosition=0;
//            
//            userUnserendipityAccum=0.0;
//            lastUserID=userID;
//        }
//        
//        // Processing a new recommendation
//        recommendationPosition++;
//        
////        // Compute only untill reaching the last level required (performance requirement)
////        if (recommendationPosition>lastLevel) return;
//
//        // Computing similarity of the actual item with items in user's history       
//        Collection<I> userHistory=eModelTraining.getItemsRatedBy(userID);
//        double simAccum=0.0;
//        int userHistorySize=0;
//        if (userHistory !=  null && recommendation != null){
//            for (Object otherItemID:userHistory){
//                double similarity=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
//                if (!Double.isInfinite(similarity)){                
//                    simAccum+=simMatrix.getSimilarity(recommendation.getItemID(), otherItemID);
//                }
//            }
//            userHistorySize=userHistory.size();
//        }
//        userUnserendipityAccum+=simAccum;
//        
//        // Computing average similarity
//        double denominator=1;
//        if (averageValue && userHistorySize>0){
//            denominator=recommendationPosition*userHistorySize;
//        }
//
//        
//        // saving Unserendipity at this level, if required
//        int i=0;
//        for (int level:levels){
//            if (level==recommendationPosition){
//                if (i == 0){
//                    userMetricAtLevel.put(userID, new Double[levels.size()]);
//                }
//                userMetricAtLevel.get(userID)[i]=userUnserendipityAccum/denominator;
//                MetricAtLevel__[i]+=userUnserendipityAccum/denominator;
//            }
//            i++;
//        }
//        
//        // Saving overall accumValue per user
//        userMetric.put(userID, userUnserendipityAccum/denominator);
//        
    }


    public void reset() {
//        init();
    }

    public void reset(SplitIF<U,I,C> split) {
        init(split);
    }

    @Override
    protected void finishComputation(){
//        // Computes overall accumValue
//        int i=0;
//        for (int level:levels){
//            MetricAtLevel__[i]/=(double)userMetric.size();
//            metricAtLevel.put(level,MetricAtLevel__[i]);
//            i++;
//        }
//        
//        Double accumValue=0.0;
//        for (Double userValue:userMetric.values()){
//            accumValue+=userValue;
//        }
//        metric=accumValue/(double)userMetric.size();
        
        super.finishComputation();
    }
    
}
