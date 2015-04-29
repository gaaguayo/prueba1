package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.RankingMetricBuilder;
import es.uam.eps.ir.split.SplitIF;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class CommonRankingMetrics<U,I,C extends ContextIF> {
    public enum METRICS{
        ALL,
        COMMON,
        BASIC,
        P,
        NONE
    }
    
    private final RankingMetricBuilder.METRIC[] allRankingButUnserendipityMetrics = new RankingMetricBuilder.METRIC[]{
            RankingMetricBuilder.METRIC.PRECISION,
            RankingMetricBuilder.METRIC.RECALL,
            RankingMetricBuilder.METRIC.F1,
            RankingMetricBuilder.METRIC.NDCG,
            RankingMetricBuilder.METRIC.MAP,
            RankingMetricBuilder.METRIC.AUC,
            RankingMetricBuilder.METRIC.ILS_CF,
            RankingMetricBuilder.METRIC.SELF_INFORMATION,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS_PERSONALIZED,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS_COVERAGE,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS,
            RankingMetricBuilder.METRIC.RELEVANT_ITEMS
    };

    private final RankingMetricBuilder.METRIC[] basicRankingMetrics = new RankingMetricBuilder.METRIC[]{
            RankingMetricBuilder.METRIC.PRECISION,
            RankingMetricBuilder.METRIC.RECALL,
            RankingMetricBuilder.METRIC.F1,
            RankingMetricBuilder.METRIC.NDCG,
            RankingMetricBuilder.METRIC.MAP,
            RankingMetricBuilder.METRIC.AUC,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS_PERSONALIZED,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS_COVERAGE,
            RankingMetricBuilder.METRIC.CANDIDATE_ITEMS,
            RankingMetricBuilder.METRIC.RELEVANT_ITEMS
    };
        
    private final RankingMetricBuilder.METRIC[] pRankingMetrics = new RankingMetricBuilder.METRIC[]{
            RankingMetricBuilder.METRIC.PRECISION,
    };
            
    private List<Integer> levels = Arrays.asList(5,10,20,50);
    private SimilarityComputerIF<U,I,C> simComputer = new PearsonWeightedSimilarity<U,I,C>();
    private SplitIF<U,I,C> split;
    
    public CommonRankingMetrics<U,I,C> setLevels(final List<Integer> levels){
        this.levels = levels;
        return this;
    }
    
    public CommonRankingMetrics<U,I,C> setSimilarityComputer(SimilarityComputerIF<U,I,C> simComputer){
        this.simComputer = simComputer;
        return this;
    }
    
    public CommonRankingMetrics<U,I,C> setSplit(SplitIF<U,I,C> split){
        this.split = split;
        return this;
    }
        
    public MetricIF<U,I,C>[] getMetrics(METRICS metrics){
        MetricIF<U,I,C> _metrics[] = null;
        RankingMetricBuilder.METRIC[] individualMetrics;
        switch (metrics){
            case ALL:
                individualMetrics = RankingMetricBuilder.METRIC.values();
                break;
            case COMMON:
                individualMetrics = allRankingButUnserendipityMetrics;
                break;
            case P:
                individualMetrics = pRankingMetrics;
                break;
            case NONE:
                return null;
            case BASIC:
            default:
                individualMetrics = basicRankingMetrics;
                break;
        }
        int i = 0;
        _metrics = new MetricIF[individualMetrics.length];
        for (RankingMetricBuilder.METRIC _metric:individualMetrics){
            _metrics[i++] = getIndividualMetric(_metric);
        }
        
        return _metrics;
    }
    
    private MetricIF<U,I,C> getIndividualMetric(RankingMetricBuilder.METRIC theMetric){
        MetricIF<U,I,C> _metric = new RankingMetricBuilder<U,I,C>()
                .levels(levels)
                .similarityComputer(simComputer)
                .split(split)
                .buildMetric(theMetric);
        return _metric;
    }
}