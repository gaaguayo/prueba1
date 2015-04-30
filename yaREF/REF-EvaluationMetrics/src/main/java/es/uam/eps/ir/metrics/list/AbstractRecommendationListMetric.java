package es.uam.eps.ir.metrics.list;

import es.uam.eps.ir.metrics.AbstractMetric;
import es.uam.eps.ir.metrics.BasicMetricResults;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.metrics.RecommendationListMetricIF;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author pedro
 * Creation date: 11-ene-2011
 */
public abstract class AbstractRecommendationListMetric<U,I,C> extends AbstractMetric<U,I,C> implements RecommendationListMetricIF<U,I,C>{
    protected List<Integer> levels;
    protected Map<Integer,Double> metricAtLevel;
    protected Map<Object,Double[]> userMetricAtLevel;
    protected Map<Object,Integer> userListMap;
    protected int overallList;
    protected Integer userList;
    protected double[] MetricAtLevel__;
    
    
    protected boolean computedAtLevels=true;
    protected final int maxLevel;
    protected int recommendationPosition;
        
    AbstractRecommendationListMetric(List<Integer> levels){
        super();
        this.levels=levels;
        if (levels != null){
            this.maxLevel=levels.get(levels.size()-1);
        }
        else{
            this.maxLevel=0;
        }
    }
    
    public void setLevels(List levels){
        this.levels=levels;
    }
        
    public MetricResultsIF<U> computeMetric(Map<U, List<RecommendationIF<I>>> userRecommendationsMap, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
//    public MetricResults computeMetric(Map<Object, List<Recommendation>> userRecommendationsMap, TrainingTestSplit sample, Map<Object, Set<Object>> userRelevantsMap){
        this.reset();
        
        // Perform measurements
        for (U user:userRecommendationsMap.keySet()){
            // If there are not relevant items for this user, nothing is done
            if (userRelevantSet.isEmpty() && this instanceof RecommendationListMetricIF){
                continue;
            }
            List<RecommendationIF<I>> userRecommendations=userRecommendationsMap.get(user);
            processUserList(user, userRecommendations, userRelevantSet, userNotRelevantSet);
        }
        
        MetricResultsIF<U> results=new BasicMetricResults(levels, metric, metricAtLevel, userMetric, userMetricAtLevel, shortName());
        
        return results;
    }
    
    
    public abstract String shortName(); 
    
    public MetricResultsIF<U> getResults(){
        if (!this.computationFinished){
            this.finishComputation();
        }
        MetricResultsIF<U> results=new BasicMetricResults(levels, metric, metricAtLevel, userMetric, userMetricAtLevel, shortName());
        return results;
    }
    
    
    public void processUserList(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
//        // Perform measurements
//        for (RecommendationIF<I> recommendation:userRecommendations){
//            this.processNextRecommendation(user, recommendation, userRelevantSet, userNotRelevantSet);
//        }
        if (userRelevantSet.isEmpty()) { return; } // Relevance-based metrics work this way (see trec_eval computations).. For consistency, all list-based metrics are forced to work this way
        initMaps(user);
        try{
            initValues(user, userRecommendations, userRelevantSet, userNotRelevantSet);
        }
        catch(InvalidParameterException e){
            return;
        }
        
        double metricValue = 0.0;
        
        try{
            for (RecommendationIF<I> recommendation : userRecommendations){
                // Processing a new recommendation
                recommendationPosition++;
                
                metricValue = this.processNextRecommendation(recommendation, userRelevantSet, userNotRelevantSet);
                if (levels != null){
                    saveMetricAtLevel(user, metricValue, recommendationPosition);
                }
            }
            while (recommendationPosition <= maxLevel){
                // Processing a new recommendation
                recommendationPosition++;
                
                metricValue = this.processNextRecommendation(null, userRelevantSet, userNotRelevantSet);
                if (levels != null){
                    saveMetricAtLevel(user, metricValue, recommendationPosition);
                }                
            }
        } catch (RuntimeException e){
        }
        
        saveMetricValue(user, metricValue);
    }
    
    protected double processNextRecommendation(RecommendationIF<I> recommendation, Set<I> userRelevantSet, Set<I> userNotRelevantSet){
        return 0.0;
    }
    
    protected boolean isRelevant(RecommendationIF<I> recomm, Set<I> relevantItems){
        if (recomm != null) {
            I itemID=recomm.getItemID();
            if (relevantItems.contains(itemID)){
                return true;
            }
        }
        return false;
    }
    
    protected void initMaps(U user){
        userList = userListMap.get(user);
        if (userList == null){
            userList = new Integer(0);
        }
        userList++;
        overallList++;
        if (levels != null){
            userMetricAtLevel.put(user, new Double[levels.size()]);
            for (int i = 0; i < levels.size(); i++){
                userMetricAtLevel.get(user)[i] = 0.0;
            }
        }
    }
    
    protected abstract void initValues(U user, List<RecommendationIF<I>> userRecommendations, Set<I> userRelevantSet, Set<I> userNotRelevantSet);
    
    protected void saveMetricValue(U user, double metricValue){
        // Saving full list's metric
        Double avgUserMetric = userMetric.get(user);
        if (avgUserMetric == null){
            avgUserMetric = 0.0;
        }
        avgUserMetric += (metricValue - avgUserMetric) / (userList);
        userMetric.put(user, avgUserMetric);
        userListMap.put(user, userList);
        
        metric += (metricValue - metric) / overallList;
    }
    
    protected void saveMetricAtLevel(U user, double metricValue, int position){
        // saving metric at this level, if required
        int i=0;
        for (int level:levels){
            if (level == position){
                userMetricAtLevel.get(user)[i] += ( metricValue - userMetricAtLevel.get(user)[i]) / userList;
                metricAtLevel.put(level, metricAtLevel.get(level) + (metricValue - metricAtLevel.get(level)) / (overallList) );
            }
            i++;
        }        
    }

    @Override
    protected synchronized void init(){
        if (levels != null){
            metricAtLevel=new HashMap();
            for (int level : levels){
                metricAtLevel.put(level, 0.0);
            }
        }
        
        userMetricAtLevel=new HashMap();
        userMetric=new HashMap();
        
        userListMap = new HashMap<Object,Integer>();
        overallList = 0;
        
        super.init();
    }

}
