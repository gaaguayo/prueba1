package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.ErrorMetricBuilder;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author pedro
 */
public class CommonErrorMetrics<U,I,C extends ContextIF> {
    public enum METRICS{
        ALL,
        COMMON,
        RMSE,
        NONE
    }
    
    private final ErrorMetricBuilder.METRIC[] commonError = new ErrorMetricBuilder.METRIC[]{
            ErrorMetricBuilder.METRIC.MAE,
            ErrorMetricBuilder.METRIC.RMSE,
    };
    
    private final ErrorMetricBuilder.METRIC[] RMSE= new ErrorMetricBuilder.METRIC[]{
            ErrorMetricBuilder.METRIC.RMSE,
    };
    
    
    private SimilarityComputerIF<U,I,C> simComputer = new PearsonWeightedSimilarity<U,I,C>();
    private SplitIF<U,I,C> split;
    
    public CommonErrorMetrics<U,I,C> setSimilarityComputer(SimilarityComputerIF<U,I,C> simComputer){
        this.simComputer = simComputer;
        return this;
    }
    
    public CommonErrorMetrics<U,I,C> setSplit(SplitIF<U,I,C> split){
        this.split = split;
        return this;
    }
        
    public MetricIF<U,I,C>[] getMetrics(METRICS metrics){
        MetricIF<U,I,C> _metrics[] = null;
        ErrorMetricBuilder.METRIC[] individualMetrics;
        switch (metrics){
            case ALL:
                individualMetrics = ErrorMetricBuilder.METRIC.values();
                break;
            case RMSE:
                individualMetrics = RMSE;
                break;
            case NONE:
                return null;
            case COMMON:
            default:
                individualMetrics = commonError;
                break;
        }
        int i = 0;
        _metrics = new MetricIF[individualMetrics.length];
        for (ErrorMetricBuilder.METRIC _metric:individualMetrics){
            _metrics[i++] = getIndividualMetric(_metric);
        }
        
        return _metrics;
    }
    
    private MetricIF<U,I,C> getIndividualMetric(ErrorMetricBuilder.METRIC theMetric){
        MetricIF<U,I,C> _metric = new ErrorMetricBuilder<U,I,C>()
                .similarityComputer(simComputer)
                .split(split)
                .buildMetric(theMetric);
        return _metric;
    }
}