package es.uam.eps.ir.metrics;

import java.util.List;

/**
 *
 * @author pedro
 */
public interface RecommendationListMetricIF<U,I,C> extends MetricIF<U,I,C>{

    /*
     * Set the levels at which the metric must be computed
     * @param levels
     *      levels to compute the metric
     * @return the metric value at level
     */
    public void setLevels(List levels);
}
