package es.uam.eps.ir.metrics;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public abstract class AbstractPredictionMetric<U,I,C> extends AbstractMetric<U,I,C> implements MetricIF<U,I,C> {
    AbstractPredictionMetric(){
        super();
    }

    public MetricResultsIF<U> computeMetric(Map<U, List<RecommendationIF<I>>> userRecommendationsMap, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        this.reset();
        
        // Perform measurements
        for (U user:userRecommendationsMap.keySet()){
            List<RecommendationIF<I>> userRecommendations=userRecommendationsMap.get(user);
            for (RecommendationIF<I> recommendation:userRecommendations){
                this.processNextRecommendation(user, recommendation, userRelevantSet, userNotRelevantSet);
            }
        }
        
        this.finishComputation();
        
        MetricResultsIF<U> results=new BasicMetricResults(null, metric, null, userMetric, null, shortName());
        
        return results;
    }

    public void processUserList(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        // Perform measurements
        for (RecommendationIF<I> recommendation:userRecommendations){
            this.processNextRecommendation(user, recommendation, userRelevantSet, userNotRelevantSet);
        }
        
    }
    
    public MetricResultsIF<U> getResults(){
        if (!this.computationFinished){
            this.finishComputation();
        }
        MetricResultsIF<U> results=new BasicMetricResults(null, metric, null, userMetric, null, shortName());
        return results;
    }
    
    protected abstract void processNextRecommendation(U user, RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet);
    
}
