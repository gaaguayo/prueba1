package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.rank.CandidateItemsIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.SummaryPrinter;

/**
 *
 * @author pedro
 */
public class SplitRecommendationsProcesserBck<U,I,C extends ContextIF> {
    private static final Logger logger = Logger.getLogger("ExperimentLog");
    private static long maxMem=0;
    private C defaultContext;

    private String experimentInfoString;
    private String perUserMetricsString;
    private String relevantsInfoString;
    private String recommendationsInfoString;

    public void processSplit(final RecommenderIF<U,I,C> recommender, final SplitIF<U,I,C> split, final CandidateItemsBuilder<U,I,C> candidateItemsBuilder, final List<Integer> levels, CommonRankingMetrics.METRICS metrics, boolean SAVE_DETAILED_RESULTS){
            final StringBuilder experimentInfo = new StringBuilder();
            final StringBuilder recommendationsInfo = new StringBuilder();
            final StringBuilder relevantsInfo = new StringBuilder();
            
            final String newline = System.getProperty("line.separator");
            
            logger.info("Processing a new split");
            ModelIF<U,I,C> trainSet = split.getTrainingSet();
            ModelIF<U,I,C> testSet  = split.getTestingSet();
            ContextualModelUtils<U,I,C> eTrain = new ContextualModelUtils<U,I,C>(trainSet);
            final StringBuilder splitInfo = new StringBuilder();
            splitInfo.append("------------------------------------------------------------------------").append(newline);
            splitInfo.append("PROCESSING NEW SPLIT").append(newline);
            splitInfo.append("------------------------------------------------------------------------").append(newline);
                        
            splitInfo.append("Training summary").append(newline);
            splitInfo.append(new SummaryPrinter().summary(trainSet, eTrain));
            splitInfo.append("Test summary").append(newline);
            splitInfo.append(new SummaryPrinter().summary(testSet, null));
            experimentInfo.append(splitInfo);
            System.out.print(splitInfo);
            checkMem();
            defaultContext = (C)new ContinuousTimeContext(eTrain.getMaxDate().getTime());
                    
            // Relevants & Non-Relevants
            ////////////////////////////
            logger.info("Computing candidate items");        
            CandidateItemsIF<U,I,C> candidateItems;
            candidateItems = candidateItemsBuilder.buildCandidateItems(split);
            final StringBuilder candidateItemsInfo = new StringBuilder();
            candidateItemsInfo.append("CandidateItems\t").append(candidateItems).append(newline);
            experimentInfo.append(candidateItemsInfo);
            System.out.print(candidateItemsInfo);
            checkMem();
            
            // Metrics initialization
            /////////////////////////
            MetricIF<U,I,C> metricsComputer[] = new CommonRankingMetrics().setLevels(levels).setSplit(split).getMetrics(metrics);

            // Metrics computation
            //////////////////////
            logger.info("Computing recommendation and metrics");
            int nUser=0;
            int tUsers=testSet.getUsers().size();
            for (U user:testSet.getUsers()){
                nUser++;
                logger.log(Level.INFO, "Processing user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});
                int j = 0;
                // Determines items to evaluate
                Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
                Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

                Set<I> itemsToEvaluate = new TreeSet<I>();
                itemsToEvaluate.addAll(userRelevantSet);
                itemsToEvaluate.addAll(userNotRelevantSet);

                // Relevant set info
                if (SAVE_DETAILED_RESULTS){
                    for (I item:userRelevantSet){
                        relevantsInfo.append(user).append("\t0\t").append(item).append("\t1").append(newline);
                    }
                }

                // Computes predictions for each item
                List<RecommendationIF<I>> userRecommendations = new ArrayList<RecommendationIF<I>>();
                for (I item:itemsToEvaluate){
                    C context = getDefaultContext();
                    Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0];
                        context = pref.getContext();
                    }
                    Float prediction = recommender.predict(user, item, context);
                    RecommendationIF<I> recom;
                    if (!prediction.isNaN()){
                        recom = new Recommendation(item, prediction, true);
                        userRecommendations.add(recom);
                    }
                }
                Collections.sort(userRecommendations);

                // Recommendations info
                if (SAVE_DETAILED_RESULTS){
                    for (RecommendationIF<I> r:userRecommendations){
                        recommendationsInfo.append(user).append("\t1\t").append(r.getItemID()).append("\t").append(++j).append("\t").append(r.getValue()).append("\t0").append(newline);
                    }
                }

                // Compute metrics
                for (int i = 0; i < metricsComputer.length; i++){
                    metricsComputer[i].processUserList(user, userRecommendations, userRelevantSet, userNotRelevantSet);
                }
                checkMem();                
            }
            
            // Metrics printing
            ///////////////////
            final StringBuilder metricsInfo = new StringBuilder();
            metricsInfo.append("Results").append(newline);
            for (int i = 0; i < metricsComputer.length; i++){
                MetricResultsIF<U> results = metricsComputer[i].getResults();
                metricsInfo.append(results.columnFormat());
            }
            experimentInfo.append(metricsInfo);
            System.out.print(metricsInfo);
            checkMem();
            
            final StringBuilder perUserMetricsInfo = new StringBuilder();
            for (U user: testSet.getUsers()){
                perUserMetricsInfo.append("user\t").append(user).append(newline);
                for (int i = 0; i < metricsComputer.length; i++){
                    MetricResultsIF<U> results = metricsComputer[i].getResults();
                    perUserMetricsInfo.append(results.shortName()).append("\t").append(String.format("%.4f",results.getResult(user))).append(newline);
                    if (results.getLevels()!=null){
                        for (int level:results.getLevels()){
                            perUserMetricsInfo.append(results.shortName()).append("@").append(level).append("\t").append(String.format("%.4f",results.getResult(user,level))).append("\n");
                        }
                    }
                }
            }
            checkMem();
            
            
            // Generating Strings with results
            experimentInfoString = experimentInfo.toString();
            perUserMetricsString = perUserMetricsInfo.toString();
            if (SAVE_DETAILED_RESULTS){
                relevantsInfoString = relevantsInfo.toString();
                recommendationsInfoString = recommendationsInfo.toString();
            }
    }
    
    public String getExperimentDetails(){
        return experimentInfoString;
    }

    public String getPerUserMetrics(){
        return perUserMetricsString;
    }

    public String getRecommendationsDetails() {
        return recommendationsInfoString;
    }

    public String getRelevantsDetails() {
        return relevantsInfoString;
    }
    
    public long getMaxMemUsage(){
        return maxMem;
    }
    
    private static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    private C getDefaultContext(){
        C theContext = defaultContext;
        return theContext;
    }
}
