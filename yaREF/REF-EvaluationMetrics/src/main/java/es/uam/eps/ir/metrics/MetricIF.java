package es.uam.eps.ir.metrics;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public interface MetricIF<U,I,C> {
    /*
     * Reset the metric evaluator internal state (useful for recomputing metric)
     */
    public void reset();
    
    /*
     * Computes metric value from a map containing all user,recommendations pairs
     */
    public MetricResultsIF<U> computeMetric(Map<U, List<RecommendationIF<I>>> userRecommendationsMap, Set<I> userRelevantSet, Set<I> userNotRelevantSet);
    
    /* Methods below are intended for processing one user a time. 
     * ATTENTION!
     * The proccesing must be performed carefully. Requires
     * 1) Invoque the reset() method prior to computations
     * 2) Invoque processUser(...) for each user
     * 3) Obtain final results by invoquingngetResults(...)
     * 
     */
    
    /*
     * Process recommendation list of a user
     */
    public void processUserList(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet);
    
    /*
     * Returns a {@link MetricResultsIF} object qith the results from this metric
     * Requires previous invocations of either computeMetric(...) or processUser(...) methods
     */
    public MetricResultsIF getResults();

}
