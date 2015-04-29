package es.uam.eps.ir.metrics;

import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.split.SplitIF;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pedro
 */
public class RankingMetricBuilder<U,I,C extends ContextIF> {
    
    public enum METRIC {
        PRECISION,
        RECALL,
        F1,
        NDCG,
        MAP,
        AUC,
        ILS_CF,
        SELF_INFORMATION,
        UNSERENDIPITY,
        CANDIDATE_ITEMS_PERSONALIZED,
        CANDIDATE_ITEMS_COVERAGE,
        CANDIDATE_ITEMS,
        RELEVANT_ITEMS;
    }
    
    private List<Integer> levels = Arrays.asList(5,10,20,50);
    private SimilarityComputerIF<U,I,C> simComputer = new PearsonWeightedSimilarity<U,I,C>();
    private SplitIF<U,I,C> split;
    
    public RankingMetricBuilder<U,I,C> levels(final List<Integer> levels){
        this.levels = levels;
        return this;
    }
    
    public RankingMetricBuilder<U,I,C> similarityComputer(SimilarityComputerIF<U,I,C> simComputer){
        this.simComputer = simComputer;
        return this;
    }
    
    public RankingMetricBuilder<U,I,C> split(SplitIF<U,I,C> split){
        this.split = split;
        return this;
    }
        
    
    public MetricIF<U,I,C> buildMetric(METRIC metric){
        MetricIF<U,I,C> _metric = null;
        switch (metric){
            case AUC:
                _metric = getAUCMetric();
                break;
            case F1:
                _metric = getF1Metric();
                break;
            case ILS_CF:
                _metric = getILS_CFMetric();
                break;
            case CANDIDATE_ITEMS:
                _metric = getCandidateItemsMetric();
                break;
            case CANDIDATE_ITEMS_PERSONALIZED:
                _metric = getCandidateItemsPersonalizedMetric();
                break;
            case CANDIDATE_ITEMS_COVERAGE:
                _metric = getCandidateItemsCoverageMetric();
                break;
            case PRECISION:
                _metric = getPrecisionMetric();
                break;
            case MAP:
                _metric = getMAPMetric();
                break;
            case RECALL:
                _metric = getRecallMetric();
                break;
            case RELEVANT_ITEMS:
                _metric = getRelevantItemsMetric();
                break;
            case SELF_INFORMATION:
                _metric = getSelfInformationMetric();
                break;
            case UNSERENDIPITY:
                _metric = getUnserendipityMetric();
                break;
            case NDCG:
                _metric = getnDCGMetric();
                break;
        }
        return _metric;
    }
    
    public MetricIF<U,I,C> getAUCMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_AUC<U,I,C>();
        return _metric;
    }    
    
    public MetricIF<U,I,C> getF1Metric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_F1<U,I,C>(levels);
        return _metric;
    }
    
    public MetricIF<U,I,C> getILS_CFMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_ILS_CF<U,I,C>(levels, simComputer);
        ((Metric_ILS_CF<U,I,C>) _metric).init(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getPrecisionMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_Precision<U,I,C>(levels);
        return _metric;
    }
    
    public MetricIF<U,I,C> getMAPMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_MAP<U,I,C>(levels);
        return _metric;
    }
    
    public MetricIF<U,I,C> getRecallMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_Recall<U,I,C>(levels);
        return _metric;
    }

    public MetricIF<U,I,C> getCandidateItemsPersonalizedMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_CandidateItemsPersonalized<U,I,C>();
        return _metric;
    }

    public MetricIF<U,I,C> getCandidateItemsCoverageMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_CandidateItemsCoverage<U,I,C>();
        return _metric;
    }

    public MetricIF<U,I,C> getRelevantItemsMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_RelevantItems<U,I,C>(levels);
        return _metric;
    }
    
    public MetricIF<U,I,C> getSelfInformationMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_SelfInformation<U,I,C>(levels);
        ((Metric_SelfInformation<U,I,C>) _metric).init(split);
        return _metric;
    }
    
    public MetricIF<U,I,C> getCandidateItemsMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_CandidateItems<U,I,C>(levels);
        return _metric;
    }

    public MetricIF<U,I,C> getUnserendipityMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_Unserendipity<U,I,C>(levels, simComputer);
        return _metric;
    }
    
    public MetricIF<U,I,C> getnDCGMetric(){
        MetricIF<U,I,C> _metric;
        _metric = new Metric_nDCG<U,I,C>(levels);
        return _metric;
    }
}
