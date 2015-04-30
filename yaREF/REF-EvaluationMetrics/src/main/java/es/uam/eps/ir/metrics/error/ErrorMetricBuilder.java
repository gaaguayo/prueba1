package es.uam.eps.ir.metrics.error;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class ErrorMetricBuilder<U,I,C extends ContextIF> {
    
    public enum METRIC {
        MAE,
        MAE_PERSONALIZED,
        RMSE,
        RMSE_PERSONALIZED,
        TEST_PERSONALIZED,
        TEST_COVERAGE,
        TEST_USERS_COVERAGE,
    }
    
    private SplitIF<U,I,C> split;
    
    
    public ErrorMetricBuilder<U,I,C> split(SplitIF<U,I,C> split){
        this.split = split;
        return this;
    }
        
    
    public MetricIF<U,I,C> buildMetric(METRIC metric){
        MetricIF<U,I,C> _metric = null;
        switch (metric){
            case MAE:
                _metric = getMAEMetric();
                break;
            case MAE_PERSONALIZED:
                _metric = getMAEPersonalizedMetric();
                break;
            case RMSE:
                _metric = getRMSEMetric();
                break;
            case RMSE_PERSONALIZED:
                _metric = getRMSEPersonalizedMetric();
                break;
            case TEST_PERSONALIZED:
                _metric = getTestPersonalizedMetric();
                break;
            case TEST_COVERAGE:
                _metric = getTestCoverageMetric();
                break;
            case TEST_USERS_COVERAGE:
                _metric = getTestUsersCoverageMetric();
                break;
        }
        return _metric;
    }
    
    public MetricIF<U,I,C> getMAEMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_MAE<U,I,C>(split);
        return _metric;
    }

    public MetricIF<U,I,C> getMAEPersonalizedMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_MAE_Personalized<U,I,C>(split);
        return _metric;
    }

    public MetricIF<U,I,C> getRMSEMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_RMSE<U,I,C>(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getRMSEPersonalizedMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_RMSE_Personalized<U,I,C>(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getTestPersonalizedMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_TestPersonalized<U,I,C>(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getTestCoverageMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_TestCoverage<U,I,C>(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getTestUsersCoverageMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_TestUsersCoverage<U,I,C>(split);
        return _metric;
    }
    
}
