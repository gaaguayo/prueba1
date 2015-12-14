package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.contextualfiltering.ContextDependentIF;
import es.uam.eps.ir.core.model.ImplicitFeedbackModelIF;
import es.uam.eps.ir.cars.model.ItemSplittingExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.metrics.BasicMetricResults;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.metrics.list.RecommendationList;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.rank.CandidateItemsIF;
import es.uam.eps.ir.rank.CandidateItems_OnePlusRandom;
import es.uam.eps.ir.rank.CandidateItems_OnePlusRandom_Context;
import es.uam.eps.ir.split.SplitIF;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.MailSender;
import utils.SummaryPrinter;

/**
 *
 * @author pedro
 */
public class SplitRecommendationsProcessor<U,I,C extends ContextIF> {
    protected static final Logger logger = Logger.getLogger("ExperimentLog");
    protected static long maxMem=0;
    protected C defaultContext;
    protected ContextualModelUtils<U,I,C> eTrain;
    protected ContextualModelUtils<U,I,C> eTest;
    protected ModelIF<U,I,C> trainingSet;
    protected ModelIF<U,I,C> testSet;
    
    MetricResultsIF<U>[] errorMetricsResults;
    MetricResultsIF<U>[] rankingMetricsResults;

    protected String experimentInfoString;
    protected String perUserMetricsString;
    protected String relevantsInfoString;
    protected List<String> recommendationsInfoString;
    
    private int cores = Runtime.getRuntime().availableProcessors();
    private boolean SEND_EMAIL;
    protected int nSplit = 0;

    public void processSplit(
            final RecommenderIF<U,I,C> recommender,
            final SplitIF<U,I,C> split,
            final CandidateItemsBuilder<U,I,C> candidateItemsBuilder,
            NonPersonalizedPrediction.Type non_personalized,
            final List<Integer> levels,
            CommonRankingMetrics.METRICS rankingMetrics,
            CommonErrorMetrics.METRICS errorMetrics,
            boolean SAVE_DETAILED_RESULTS,
            boolean SEND_EMAIL,
            boolean controlPredictionValue){
        this.nSplit++;
        this.SEND_EMAIL = SEND_EMAIL;
        
        final StringBuilder experimentInfo = new StringBuilder();
        final List<StringBuilder> recommendationsInfo = new ArrayList<StringBuilder>();
        final StringBuilder relevantsInfo = new StringBuilder();

        final String newline = System.getProperty("line.separator");

        logger.info("Processing a new split");
//        ModelIF<U,I,C> trainSet = split.getTrainingSet();
        trainingSet = split.getTrainingSet();
//        ModelIF<U,I,C> testSet  = split.getTestingSet();
        testSet  = split.getTestingSet();
//        ExtendedModel<U,I,C> eTrain = new ExtendedModel<U,I,C>(trainSet);
        eTrain = new ContextualModelUtils<U,I,C>(trainingSet);
//        ExtendedModel<U,I,C> eTest = new ExtendedModel<U,I,C>(testSet);
        eTest = new ContextualModelUtils<U,I,C>(testSet);
        
//        System.out.print(ModelPrintUtils.printModel(trainSet));
//        System.out.println("test");
//        System.out.print(ModelPrintUtils.printModel(testSet));
//        System.exit(0);
        
        final StringBuilder splitInfo = new StringBuilder();
        splitInfo.append("------------------------------------------------------------------------").append(newline);
        splitInfo.append("PROCESSING SPLIT ").append(nSplit).append(newline);
        splitInfo.append("------------------------------------------------------------------------").append(newline);

        splitInfo.append("Training summary").append(newline);
        splitInfo.append(new SummaryPrinter().summary(trainingSet, eTrain,"Tr"));
        splitInfo.append("Test summary").append(newline);
        splitInfo.append(new SummaryPrinter().summary(testSet, eTest, "Te"));
        experimentInfo.append(splitInfo);
        System.out.print(splitInfo);
        checkMem();

//        Commented 2015-11-12, due to introduction of TimestampedContextContainer
//        try {
//            defaultContext = (C)new ContinuousTimeContext(eTest.getMinDate().getTime());
////        defaultContext = (C)new ContinuousTimeContext(eTest.getMaxDate().getTime());
//        } catch (Exception e){
//            defaultContext = eTest.getExampleContext();
//        }
        defaultContext = eTest.getExampleContext();
            
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
        
        // Non personalized predictions (if recommender is unable to make a prediction)
        ////////////////////////////
        NonPersonalizedPrediction nonPersonalized = new NonPersonalizedPrediction(non_personalized, trainingSet);
        final StringBuilder nonPersonalizedInfo = new StringBuilder();
        nonPersonalizedInfo.append("NonPersonalizedPrediction\t").append(nonPersonalized).append(newline);
        experimentInfo.append(nonPersonalizedInfo);
        System.out.print(nonPersonalizedInfo);
        checkMem();        

        // Metrics initialization
        /////////////////////////
        MetricIF<U,I,C> rankingMetricsComputer[] = new CommonRankingMetrics().setLevels(levels).setSplit(split).getMetrics(rankingMetrics);
        MetricIF<U,I,C> errorMetricsComputer[] = new CommonErrorMetrics().setSplit(split).getMetrics(errorMetrics);

        // Recommendations generation and metrics computation
        logger.info("Computing recommendation and metrics");
        int tUsers=testSet.getUsers().size();
        processUsers(recommender, rankingMetricsComputer, errorMetricsComputer, tUsers, candidateItems, nonPersonalized, trainingSet, testSet,SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, controlPredictionValue);

        // Metrics printing
        ///////////////////
        final StringBuilder metricsInfo = new StringBuilder();
        metricsInfo.append("Results").append(newline);
        if (rankingMetricsComputer != null){
            rankingMetricsResults = new BasicMetricResults[rankingMetricsComputer.length];
            for (int i = 0; i < rankingMetricsComputer.length; i++){
                rankingMetricsResults[i] = rankingMetricsComputer[i].getResults();
                metricsInfo.append(rankingMetricsResults[i].columnFormat());
            }
        }
        if (errorMetricsComputer != null){
            errorMetricsResults = new BasicMetricResults[errorMetricsComputer.length];
            for (int i = 0; i < errorMetricsComputer.length; i++){
                errorMetricsResults[i] = errorMetricsComputer[i].getResults();
                metricsInfo.append(errorMetricsResults[i].columnFormat());
            }
        }
        experimentInfo.append(metricsInfo);
        System.out.print(metricsInfo);
        checkMem();

        final StringBuilder perUserMetricsInfo = new StringBuilder();
        Collection<U> users = new ArrayList(testSet.getUsers());
        Collections.sort((List)users);
//        Collection<U> users = (Collection<U>)Arrays.asList(33,42,87,192,195,368,462,495,546,578);
//        Collection<U> users = (Collection<U>)Arrays.asList(1,2);
        for (U user: users){
//            perUserMetricsInfo.append("user\t").append(user).append(newline);
            perUserMetricsInfo.append("split").append(nSplit).append("\t").append("user\t").append(user).append(newline);
            if (rankingMetricsComputer != null) {
                for (int i = 0; i < rankingMetricsComputer.length; i++){
                    MetricResultsIF<U> results = rankingMetricsComputer[i].getResults();
//                    perUserMetricsInfo.append(results.shortName()).append("\t").append(String.format("%.4f",results.getResult(user))).append(newline);
                    perUserMetricsInfo.append("split").append(nSplit).append("\t").append(results.shortName()).append("\t").append(String.format("%.4f",results.getResult(user))).append(newline);
                    if (results.getLevels()!=null){
                        for (int level:results.getLevels()){
//                            perUserMetricsInfo.append(results.shortName()).append("@").append(level).append("\t").append(String.format("%.4f",results.getResult(user,level))).append("\n");
                            perUserMetricsInfo.append("split").append(nSplit).append("\t").append(results.shortName()).append("@").append(level).append("\t").append(String.format("%.4f",results.getResult(user,level))).append("\n");
                        }
                    }
                }
            }
            if (errorMetricsComputer != null) {
                for (int i = 0; i < errorMetricsComputer.length; i++){
                    MetricResultsIF<U> results = errorMetricsComputer[i].getResults();
//                    perUserMetricsInfo.append(results.shortName()).append("\t").append(String.format("%.4f",results.getResult(user))).append(newline);
                    perUserMetricsInfo.append("split").append(nSplit).append("\t").append(results.shortName()).append("\t").append(String.format("%.4f",results.getResult(user))).append(newline);
                }
            }
            checkMem();
        }

        // Generating Strings with results
        experimentInfoString = experimentInfo.toString();
        perUserMetricsString = perUserMetricsInfo.toString();
//        if (SAVE_DETAILED_RESULTS){
            relevantsInfoString = relevantsInfo.toString();
            recommendationsInfoString = new ArrayList<String>();
            for (StringBuilder sb : recommendationsInfo){
                recommendationsInfoString.add(sb.toString());
            }
//        }
    }
    
    public MetricResultsIF<U>[] getErrorMetricsResults(){
        return errorMetricsResults;
    }
    
    public MetricResultsIF<U>[] getRankingMetricsResults(){
        return rankingMetricsResults;
    }
    
    public String getExperimentDetails(){
        return experimentInfoString;
    }

    public String getPerUserMetrics(){
        return perUserMetricsString;
    }

    public List<String> getRecommendationsDetails() {
        return recommendationsInfoString;
    }

    public String getRelevantsDetails() {
        return relevantsInfoString;
    }
    
    public long getMaxMemUsage(){
        return maxMem;
    }
    
    protected static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    protected C getDefaultContext(){
        C theContext = defaultContext;
        return theContext;
    }
    
    protected C getDefaultContext(Object recommender, final Object item){
        C theContext = defaultContext;
        if (recommender instanceof ContextDependentIF){
            Calendar cal=Calendar.getInstance();
            int pos=Collections.binarySearch((List)testSet.getItems(), (I)item);
            cal.set(Calendar.DAY_OF_WEEK, (pos%7)+1);
            theContext = (C)new ContinuousTimeContext(cal.getTimeInMillis());
        }
        return theContext;
    }
    
    
    protected void processUsers(
            final RecommenderIF<U,I,C> recommender, 
            final MetricIF<U,I,C> rankingMetricsComputer[], 
            final MetricIF<U,I,C> errorMetricsComputer[], 
            final int tUsers,
            final CandidateItemsIF<U,I,C> candidateItems,
            final NonPersonalizedPrediction nonPersonalized,
            final ModelIF<U,I,C> trainingSet,
            final ModelIF<U,I,C> testSet,
            final boolean SAVE_DETAILED_RESULTS,
            final List<StringBuilder> recommendationsInfo,
            final StringBuilder relevantsInfo,
            final boolean controlPredictionValue){
        UserProcessor userProcessor = new UserProcessor<U,I,C>();        
        MetricsProcessor metricsProcessor = new MetricsProcessor();
        
        int nUser=0;
        Collection<U> users = new ArrayList(testSet.getUsers());
        Collections.sort((List)users);
        for (U user:users){
            nUser++;
//            List<List<RecommendationIF<I>>> userRecommendationLists = userProcessor.processUser(recommender, user, nUser, tUsers, candidateItems, nonPersonalized, trainingSet, testSet, controlPredictionValue);
//            metricsProcessor.processMetrics(user, nUser, tUsers, rankingMetricsComputer, errorMetricsComputer, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, userRecommendationLists);

            List<RecommendationIF<I>> userPredictions = userProcessor.processUserPredictions(recommender, user, nonPersonalized, trainingSet, testSet, controlPredictionValue);
            List<RecommendationList<U,I>> userRecommendationLists = userProcessor.processUserRecommendations(recommender, user, candidateItems, nonPersonalized, trainingSet, testSet, controlPredictionValue);
            
            logger.log(Level.INFO, "Recommendations processed for user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});
            if (SEND_EMAIL && nUser % 1000 == 0){
                String computerName;
                try{
                    computerName = InetAddress.getLocalHost().getHostName();
                } catch (java.net.UnknownHostException e){
                    computerName="unknown";
                }
                StringBuilder header = new StringBuilder();
                StringBuilder body = new StringBuilder();
                header.append(computerName).append(" processed user ").append(nUser).append(" out of ").append(tUsers);
                body.append(header).append("\n\n");
                body.append("Recommender: ").append(recommender).append("\n");
                body.append("Available cores: ").append(cores).append("\n");
                body.append("Used memory: ").append(Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()).append("\n");
                String[] mail=new String[]{header.toString(), body.toString()};
                MailSender.main(mail);
                
            }

//            metricsProcessor.processMetrics(user, nUser, tUsers, rankingMetricsComputer, errorMetricsComputer, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, userRecommendationLists);
            metricsProcessor.processErrorMetrics(user, errorMetricsComputer, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, userPredictions);
            metricsProcessor.processRankingMetrics(user, rankingMetricsComputer, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, userRecommendationLists);
            
            // Recommendations info
            if (SAVE_DETAILED_RESULTS){
                final String newline = System.getProperty("line.separator");
                Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
                for (I item:userRelevantSet){
                    relevantsInfo.append(user).append("\t0\t").append(item).append("\t1").append(newline);
                }
                int j = 0;
                recommendationsInfo.add(new StringBuilder());
                for (RecommendationIF<I> r:userPredictions){
                    float trueRating = 0;
                    trueRating = ((ModelIF<U,I,C>)testSet).getPreferenceValue(user, r.getItemID(), null);
                    recommendationsInfo.get(0).append(user).append("\t").append(0).append("\t").append(r.getItemID()).append("\t").append(++j).append("\t").append(r.getValue()).append("\t").append(trueRating).append(newline);
                }
                                
                for (int list = 1; list < userRecommendationLists.size()+1; list++){
                    if (recommendationsInfo.size() < userRecommendationLists.size()+1){
                        recommendationsInfo.add(new StringBuilder());
                    }
                    j = 0;
                    for (RecommendationIF<I> r:userRecommendationLists.get(list).getRecommendations()){
                        recommendationsInfo.get(list).append(user).append("\t").append(list).append("\t").append(r.getItemID()).append("\t").append(++j).append("\t").append(r.getValue()).append("\t").append(0).append(newline);
                    }                    
                }
            }
            logger.log(Level.INFO, "Metrics processed for user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});            
            
            
        }        
    }
    
    public class UserProcessor<U,I,C extends ContextIF>{
        private ContextualModelUtils<U,I,C> eTest;  // Incluido por compatibilidad con codigo antiguo CodeTest (usado para UMUAI)
        
        protected List<RecommendationIF<I>> processUserPredictions(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final ModelIF<U,I,C> trainingSet,
                final ModelIF<U,I,C> testSet,
                final boolean controlPredictionValue){
            List<RecommendationIF<I>> predictions = new ArrayList();            
            if (! (testSet instanceof ImplicitFeedbackModelIF) ){
                predictions = this.getPredictions(recommender, user, nonPersonalized, trainingSet, testSet, controlPredictionValue);
            }
            return predictions;
        }
        
        protected List<RecommendationList<U,I>> processUserRecommendations(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final CandidateItemsIF<U,I,C> candidateItems,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final ModelIF<U,I,C> trainingSet,
                final ModelIF<U,I,C> testSet,
                final boolean controlPredictionValue){
            // Determines items to evaluate
            Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
            Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);
            
//            List<List<RecommendationIF<I>>> userRecommendationLists = new ArrayList<List<RecommendationIF<I>>>();
            List<RecommendationList<U,I>> lists = new ArrayList<RecommendationList<U,I>>();
            
            if (candidateItems instanceof CandidateItems_OnePlusRandom_Context){ // Context-aware Koren
                for (I item : userRelevantSet){
                    Set<I> relevantItem = new TreeSet<I>();
                    relevantItem.add(item);
                    Set<I> itemsToEvaluate = new TreeSet<I>();
                    itemsToEvaluate.addAll(userNotRelevantSet);
                    C context = (C) ((CandidateItems_OnePlusRandom_Context)candidateItems).getContext(item);
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    itemsToEvaluate.add(itemIDinTraining);
//                    List<RecommendationIF<I>> recommendations = this.getRecommendationsList(recommender, user, itemsToEvaluate, context, trainingSet, nonPersonalized, controlPredictionValue);
                    RecommendationList recommendations = this.getRecommendationsList(recommender, user, itemsToEvaluate, context, trainingSet, nonPersonalized, controlPredictionValue);
                    recommendations.setRelevantItems(relevantItem);
                    recommendations.setNotRelevantItems(userNotRelevantSet);
                    recommendations.sort();
                    lists.add(recommendations);
                }
            }
            else if (candidateItems instanceof CandidateItems_OnePlusRandom){ // common Koren
                C context = (C)getDefaultContext();                
//                List<RecommendationIF<I>> userNonRelevantRecommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                RecommendationList userNonRelevantRecommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                for (I item : userRelevantSet){
                    RecommendationList<U,I> recommendations = new RecommendationList<U,I>(userNonRelevantRecommendations);
//                    List<RecommendationIF<I>> recommendations = new ArrayList<RecommendationIF<I>>();
//                    recommendations.addAll(userNonRelevantRecommendations);
                    Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0]; // Every candidate item in a split is evaluated just once 
                        context = pref.getContext();
                    }
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    Float prediction = recommender.predict(user, itemIDinTraining, context);
                    RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                    if (recommendation != null){
//                        recommendations.add(recommendation);
                        recommendations.addRecommendation(recommendation);
                    }
//                    Collections.sort(recommendations);
//                    userRecommendationLists.add(recommendations);
                    recommendations.sort();
                    lists.add(recommendations);
                }                
            }
            else{ // common ranking evaluation
                C context = (C)getDefaultContext();                
//                List<RecommendationIF<I>> recommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                RecommendationList recommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                for (I item : userRelevantSet){
                    Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0]; // Every candidate item in a split is evaluated just once 
                        context = pref.getContext();
                    }
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    Float prediction = recommender.predict(user, itemIDinTraining, context);
                    RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                    if (recommendation != null){
//                        recommendations.add(recommendation);
                        recommendations.addRecommendation(recommendation);
                    }
                }
//                Collections.sort(recommendations);
//                userRecommendationLists.add(recommendations);
                    recommendations.sort();
                    lists.add(recommendations);                
            }
            

            
            checkMem();
            return lists;
        }
        
        
        protected List<List<RecommendationIF<I>>> processUser(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final int nUser,
                final int tUsers,
                final CandidateItemsIF<U,I,C> candidateItems,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final ModelIF<U,I,C> trainingSet,
                final ModelIF<U,I,C> testSet,
                final boolean controlPredictionValue){
            // Determines items to evaluate
            Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
            Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

//            Set<I> itemsToEvaluate = new TreeSet<I>();
//            itemsToEvaluate.addAll(userRelevantSet);
//            itemsToEvaluate.addAll(userNotRelevantSet);

            // Computes predictions for each item
//            List<RecommendationIF<I>> userRecommendations = new ArrayList<RecommendationIF<I>>();
            
            List<List<RecommendationIF<I>>> userRecommendationLists = new ArrayList<List<RecommendationIF<I>>>();
            List<RecommendationList<U,I>> lists = new ArrayList<RecommendationList<U,I>>();
            if (! (testSet instanceof ImplicitFeedbackModelIF) ){
                List<RecommendationIF<I>> userTestRecommendations = this.getPredictions(recommender, user, nonPersonalized, trainingSet, testSet, controlPredictionValue);
                userRecommendationLists.add(userTestRecommendations); // Always first
            }
            else{
                userRecommendationLists.add(new ArrayList()); // Predictons for error metrics. Always first. Empty in this case (implicit data)
            }
            
            if (candidateItems instanceof CandidateItems_OnePlusRandom_Context){ // Context-aware Koren
                for (I item : userRelevantSet){
                    Set<I> relevantItem = new TreeSet<I>();
                    relevantItem.add(item);
                    Set<I> itemsToEvaluate = new TreeSet<I>();
                    itemsToEvaluate.addAll(userNotRelevantSet);
                    C context = (C) ((CandidateItems_OnePlusRandom_Context)candidateItems).getContext(item);
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    itemsToEvaluate.add(itemIDinTraining);
//                    List<RecommendationIF<I>> recommendations = this.getRecommendationsList(recommender, user, itemsToEvaluate, context, trainingSet, nonPersonalized, controlPredictionValue);
                    RecommendationList recommendations = this.getRecommendationsList(recommender, user, itemsToEvaluate, context, trainingSet, nonPersonalized, controlPredictionValue);
                    recommendations.setRelevantItems(relevantItem);
                    recommendations.setNotRelevantItems(userNotRelevantSet);
                    recommendations.sort();
                    lists.add(recommendations);
                }
            }
            else if (candidateItems instanceof CandidateItems_OnePlusRandom){ // common Koren
                C context = (C)getDefaultContext();                
//                List<RecommendationIF<I>> userNonRelevantRecommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                RecommendationList userNonRelevantRecommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                for (I item : userRelevantSet){
                    RecommendationList recommendations = new RecommendationList(userNonRelevantRecommendations);
//                    List<RecommendationIF<I>> recommendations = new ArrayList<RecommendationIF<I>>();
//                    recommendations.addAll(userNonRelevantRecommendations);
                    Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0]; // Every candidate item in a split is evaluated just once 
                        context = pref.getContext();
                    }
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    Float prediction = recommender.predict(user, itemIDinTraining, context);
                    RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                    if (recommendation != null){
//                        recommendations.add(recommendation);
                        recommendations.addRecommendation(recommendation);
                    }
//                    Collections.sort(recommendations);
//                    userRecommendationLists.add(recommendations);
                    recommendations.sort();
                    lists.add(recommendations);
                }                
            }
            else{ // common ranking evaluation
                C context = (C)getDefaultContext();                
//                List<RecommendationIF<I>> recommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                RecommendationList recommendations = this.getRecommendationsList(recommender, user, userNotRelevantSet, context, trainingSet, nonPersonalized, controlPredictionValue);
                for (I item : userRelevantSet){
                    Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0]; // Every candidate item in a split is evaluated just once 
                        context = pref.getContext();
                    }
                    I itemIDinTraining = item;
                    if (trainingSet instanceof ItemSplittingExplicitModel){
                        itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                    }
                    Float prediction = recommender.predict(user, itemIDinTraining, context);
                    RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                    if (recommendation != null){
//                        recommendations.add(recommendation);
                        recommendations.addRecommendation(recommendation);
                    }
                }
//                Collections.sort(recommendations);
//                userRecommendationLists.add(recommendations);
                    recommendations.sort();
                    lists.add(recommendations);                
            }
            
//            for (I item:itemsToEvaluate){
//                C context = (C)getDefaultContext();
////                C context = (C)getDefaultContext(recommender, item);
//                Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getPreferences(user, item);
//                if (prefs != null){
//                    PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)prefs.toArray()[0]; // Every candidate item in a split is evaluated just once 
//                    context = pref.getContext();
//                }
//                Float prediction = recommender.predict(user, item, context);
//                RecommendationIF<I> recom;
//                if (!prediction.isNaN()){
//                    if (controlPredictionValue){
//                        if (prediction > eTrain.getMaxRating()){
//                            prediction = eTrain.getMaxRating();
//                        }
//                        else if (prediction < eTrain.getMinRating()){
//                            prediction = eTrain.getMinRating();
//                        }
//                    }
//                    recom = new Recommendation(item, prediction, true);
//                    userRecommendations.add(recom);
//                }
//                else { // unpersonalized recommendation
//                    prediction = nonPersonalized.getPrediction(user, item, context);
//                    if (!Float.isNaN(prediction)){
//                        recom = new Recommendation(item, prediction, false);
//                        userRecommendations.add(recom);
//                    }
//                }
//            }
//            Collections.sort(userRecommendations);

            logger.log(Level.INFO, "Recommendations processed for user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});
            if (SEND_EMAIL && nUser % 1000 == 0){
                String computerName;
                try{
                    computerName = InetAddress.getLocalHost().getHostName();
                } catch (java.net.UnknownHostException e){
                    computerName="unknown";
                }
                StringBuilder header = new StringBuilder();
                StringBuilder body = new StringBuilder();
                header.append(computerName).append(" processed user ").append(nUser).append(" out of ").append(tUsers);
                body.append(header).append("\n\n");
                body.append("Recommender: ").append(recommender).append("\n");
                body.append("Available cores: ").append(cores).append("\n");
                body.append("Used memory: ").append(Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()).append("\n");
                String[] mail=new String[]{header.toString(), body.toString()};
                MailSender.main(mail);
                
            }
            
            checkMem();
            return userRecommendationLists;
        }
        
        protected RecommendationIF<I> getRecommendation(
                final U user,
                final I item,
                final C context,
                final Float originalPrediction,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final ModelIF<U,I,C> trainingSet,
                final boolean controlPredictionValue){
            RecommendationIF<I> recommendation = null;
            Float prediction = originalPrediction;
            if (!prediction.isNaN()){
                if (controlPredictionValue){
                    if (prediction > eTrain.getMaxRating()){
                        prediction = eTrain.getMaxRating();
                    }
                    else if (prediction < eTrain.getMinRating()){
                        prediction = eTrain.getMinRating();
                    }
                }
                recommendation = new Recommendation(item, prediction, true);
            }
            else { // unpersonalized recommendation
                I itemIDinTraining = item;
                if (trainingSet instanceof ItemSplittingExplicitModel){
                    itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                }
                prediction = nonPersonalized.getPrediction(user, itemIDinTraining, context);                
                if (!Float.isNaN(prediction)){
                    recommendation = new Recommendation(item, prediction, false);
                }
            }
            return recommendation;
        }

        protected List<RecommendationIF<I>> getRecommendationsList2(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final Set<I> itemsToEvaluate,
                final C context,
                final ModelIF<U,I,C> trainingSet,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final boolean controlPredictionValue){
            
            List<RecommendationIF<I>> predictions = new ArrayList<RecommendationIF<I>>();
            for (I item:itemsToEvaluate){
                I itemIDinTraining = item;
                if (trainingSet instanceof ItemSplittingExplicitModel){
                    itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                }
                Float prediction = recommender.predict(user, itemIDinTraining, context);
                RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                if (recommendation != null){
                    predictions.add(recommendation);
                }
            }
            return predictions;

        }

        protected RecommendationList<U,I> getRecommendationsList(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final Set<I> itemsToEvaluate,
                final C context,
                final ModelIF<U,I,C> trainingSet,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final boolean controlPredictionValue){
            
            RecommendationList<U,I> list = new RecommendationList<U,I>(user, null, null);
//            List<RecommendationIF<I>> predictions = new ArrayList<RecommendationIF<I>>();
            for (I item:itemsToEvaluate){
                I itemIDinTraining = item;
                if (trainingSet instanceof ItemSplittingExplicitModel){
                    itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                }
                Float prediction = recommender.predict(user, itemIDinTraining, context);
                RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                if (recommendation != null){
//                    predictions.add(recommendation);
                    list.addRecommendation(recommendation);
                }
            }
//            return predictions;
            return list;
        }
        
        protected List<RecommendationIF<I>> getPredictions(
                final RecommenderIF<U,I,C> recommender,
                final U user,
                final NonPersonalizedPrediction<U,I,C> nonPersonalized,
                final ModelIF<U,I,C> trainingSet,
                final ModelIF<U,I,C> testSet,
                final boolean controlPredictionValue){
            
            if (testSet instanceof ImplicitFeedbackModelIF){
                throw new UnsupportedOperationException("Cannot compute predictions for implicit ratings");
            }
            
            List<RecommendationIF<I>> predictions = new ArrayList<RecommendationIF<I>>();
            Collection<PreferenceIF<U,I,C>> prefs = (Collection<PreferenceIF<U,I,C>>)testSet.getUniquePreferencesFromUser(user, new DefaultAggregationFunction());
            for (PreferenceIF<U,I,C> pref : prefs){
                I item = pref.getItem();
                C context = pref.getContext();
                I itemIDinTraining = item;
                if (trainingSet instanceof ItemSplittingExplicitModel){
                    itemIDinTraining = (I)((ItemSplittingExplicitModel)trainingSet).getSplitItemID(user, item, context);
                }
                Float prediction = recommender.predict(user, itemIDinTraining, context);
                RecommendationIF<I> recommendation = this.getRecommendation(user, item, context, prediction, nonPersonalized, trainingSet, controlPredictionValue);
                if (recommendation != null){
                    predictions.add(recommendation);
                }
            }
            return predictions;
        }        
    }
    
    public class MetricsProcessor<U,I,C extends ContextIF>{

        protected void processErrorMetrics(
                final U user,
                final MetricIF<U,I,C> errorMetricsComputer[],
                final CandidateItemsIF<U,I,C> candidateItems,
                final boolean SAVE_DETAILED_RESULTS,
                final List<StringBuilder> recommendationsInfo,
                final StringBuilder relevantsInfo,
                final List<RecommendationIF<I>> predictions){
            
            Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
            Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);
            // Compute metrics
            if (errorMetricsComputer != null){
                for (int i = 0; i < errorMetricsComputer.length; i++){
                    errorMetricsComputer[i].processUserList(user, predictions, userRelevantSet, userNotRelevantSet);
                }
            }
        }
        
        protected void processRankingMetrics(
                final U user,
                final MetricIF<U,I,C> rankingMetricsComputer[],
                final CandidateItemsIF<U,I,C> candidateItems,
                final boolean SAVE_DETAILED_RESULTS,
                final List<StringBuilder> recommendationsInfo,
                final StringBuilder relevantsInfo,
                final List<RecommendationList<U,I>> recommendations){
            
            Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
            Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

            // Compute metrics
            if (rankingMetricsComputer != null){
                for (int list = 0; list < recommendations.size(); list++){
                    for (int i = 0; i < rankingMetricsComputer.length; i++){
                        rankingMetricsComputer[i].processUserList(user, recommendations.get(list).getRecommendations(), userRelevantSet, userNotRelevantSet);
                    }
                }
            }
        }

        
        protected void processMetrics(
                final U user,
                final int nUser,
                final int tUsers,
                final MetricIF<U,I,C> rankingMetricsComputer[],
                final MetricIF<U,I,C> errorMetricsComputer[],
                final CandidateItemsIF<U,I,C> candidateItems,
                final boolean SAVE_DETAILED_RESULTS,
                final List<StringBuilder> recommendationsInfo,
                final StringBuilder relevantsInfo,
                final List<List<RecommendationIF<I>>> recommendations){
            final String newline = System.getProperty("line.separator");
            
            Set<I> userRelevantSet = candidateItems.getRelevantSet(user, null);
            Set<I> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

            // Compute metrics
            List<RecommendationIF<I>> predictions = recommendations.get(0);
            if (errorMetricsComputer != null){
                for (int i = 0; i < errorMetricsComputer.length; i++){
                    errorMetricsComputer[i].processUserList(user, predictions, userRelevantSet, userNotRelevantSet);
                }
            }
            if (rankingMetricsComputer != null){
                for (int list = 1; list < recommendations.size(); list++){
                    for (int i = 0; i < rankingMetricsComputer.length; i++){
                        rankingMetricsComputer[i].processUserList(user, recommendations.get(list), userRelevantSet, userNotRelevantSet);
                    }
                }
            }
            // Recommendations info
            if (SAVE_DETAILED_RESULTS){
                for (I item:userRelevantSet){
                    relevantsInfo.append(user).append("\t0\t").append(item).append("\t1").append(newline);
                }
                for (int list = 0; list < recommendations.size(); list++){
                    if (recommendationsInfo.size() < recommendations.size()){
                        recommendationsInfo.add(new StringBuilder());
                    }
                    int j = 0;
                    for (RecommendationIF<I> r:recommendations.get(list)){
                        float trueRating = 0;
                        if (list == 0){
                            trueRating = ((ModelIF<U,I,C>)testSet).getPreferenceValue(user, r.getItemID(), null);
                        }
                        recommendationsInfo.get(list).append(user).append("\t").append(list).append("\t").append(r.getItemID()).append("\t").append(++j).append("\t").append(r.getValue()).append("\t").append(trueRating).append(newline);
                    }                    
                }
            }
            logger.log(Level.INFO, "Metrics processed for user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});            
        }
    }
}
