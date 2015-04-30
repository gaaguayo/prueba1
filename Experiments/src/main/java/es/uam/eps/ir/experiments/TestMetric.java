/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.list.Metric_AUC;
import es.uam.eps.ir.metrics.list.Metric_Precision;
import es.uam.eps.ir.metrics.list.Metric_nDCG;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pedro
 */
public class TestMetric {
    public static void main( String[] args )
    {
        System.out.println("Hello");
        List<Integer> levels = Arrays.asList(5,10,20,50);
        
        MetricIF<Object,Object,CategoricalContext> nDCG;
        nDCG = new Metric_nDCG<Object,Object,CategoricalContext>(levels);
        MetricIF<Object,Object,CategoricalContext> P;
        P = new Metric_Precision<Object,Object,CategoricalContext>(levels);
        MetricIF<Object,Object,CategoricalContext> AUC;
        AUC = new Metric_AUC<Object,Object,CategoricalContext>();
        
        Set<Object> userRelevantSet = new HashSet();
        userRelevantSet.add(new Integer(2));
        Set<Object> userNotRelevantSet = new HashSet();
        userNotRelevantSet.add(new Integer(1));
//        userNotRelevantSet.add(new Integer(2));
        userNotRelevantSet.add(new Integer(3));
        userNotRelevantSet.add(new Integer(4));
        userNotRelevantSet.add(new Integer(5));
        userNotRelevantSet.add(new Integer(6));
        userNotRelevantSet.add(new Integer(7));
        userNotRelevantSet.add(new Integer(8));
        userNotRelevantSet.add(new Integer(9));
        userNotRelevantSet.add(new Integer(10));
        userNotRelevantSet.add(new Integer(11));
        
        List<RecommendationIF<Object>> predictions = new ArrayList<RecommendationIF<Object>>();
        predictions.add(new Recommendation(new Integer(1), (float)1, true));
        predictions.add(new Recommendation(new Integer(2), (float)1, true));
        predictions.add(new Recommendation(new Integer(3), (float)1, true));
        predictions.add(new Recommendation(new Integer(4), (float)1, true));
        predictions.add(new Recommendation(new Integer(5), (float)1, true));
        predictions.add(new Recommendation(new Integer(6), (float)1, true));
        predictions.add(new Recommendation(new Integer(7), (float)1, true));
        predictions.add(new Recommendation(new Integer(8), (float)1, true));
        predictions.add(new Recommendation(new Integer(9), (float)1, true));
        predictions.add(new Recommendation(new Integer(10), (float)1, true));
        predictions.add(new Recommendation(new Integer(11), (float)1, true));
        
        Map<Object,List<RecommendationIF<Object>>> userRecommendations = new HashMap<Object,List<RecommendationIF<Object>>>();
        userRecommendations.put(new Integer(100), predictions);
        
        nDCG.computeMetric(userRecommendations, userRelevantSet, userNotRelevantSet);
        AUC.computeMetric(userRecommendations, userRelevantSet, userNotRelevantSet);
        P.computeMetric(userRecommendations, userRelevantSet, userNotRelevantSet);
        MetricResultsIF results_nDCG=nDCG.getResults();
        MetricResultsIF results_AUC=AUC.getResults();
        MetricResultsIF results_P=P.getResults();
        
        System.out.println(results_nDCG.columnFormat());
        System.out.println(results_P.columnFormat());
        System.out.println(results_AUC.columnFormat());
    }    
}
