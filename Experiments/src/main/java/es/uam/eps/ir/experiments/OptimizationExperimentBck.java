package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.metrics.Recommendation;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.optimization.ModelBasedRecommenderOptimizer;
import es.uam.eps.ir.optimization.RecommenderOptimizerFactory;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.rank.CandidateItemsIF;
import es.uam.eps.ir.split.DatasetSplitterIF;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;
import utils.MailSender;
import utils.PrintUtils;
import utils.SummaryPrinter;
/**
 *
 */
public class OptimizationExperimentBck 
{
    // Dataset
    final static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.MovieLens1m;
    // Evaluation Methodology
    final static CommonDatasetSplitters.METHOD   split_method        = CommonDatasetSplitters.METHOD.CommunityTimeOrderProportionHoldout;
    final static double testProportion = 0.054;  // 0.085 for Ml100k // 0.054 for Ml1m
    final static CandidateItemsBuilder.CANDIDATE_ITEMS candidates    = CandidateItemsBuilder.CANDIDATE_ITEMS.COMMUNITY_TEST;
    final static float relevance_threshold=(float)0.0;
    
    // Evaluation Metrics
    final static CommonRankingMetrics.METRICS rankingMetrics = CommonRankingMetrics.METRICS.COMMON;
    final static List<Integer> levels = Arrays.asList(5,10,20,50);
    
    // Recommendation method
    final static ModelBasedRecommenderOptimizer.RECOMMENDER_METHOD recommender_method  = ModelBasedRecommenderOptimizer.RECOMMENDER_METHOD.BiasTime;
    final static RecommenderOptimizerFactory.OPTIMIZATION_METHOD optimization_method  = RecommenderOptimizerFactory.OPTIMIZATION_METHOD.TEST;
    final static int maxOptimizationIterations = 10000;
    final static int maxTrainingIterations = 100;
    final static int maxThreads = 4;
    final static int features = 50;
    final static int bins = 4;
        
    // Results Saving
    final static boolean SAVE_RESULTS = true;
    final static boolean SEND_EMAIL_REPORT=true;
    final static boolean SEND_RESULT_FILES=true;
    static String EXPERIMENT_PREFIX = "";
    final static String METRICS_FILE = "metrics.txt";
    final static String RELEVANTS_FILE = "relevants.txt";
    final static String RECOMMENDATIONS_FILE = "recommendations.txt";
    final static String RESULTS_SERVER_PATH = "/media/sdb6/results/optimization/";
    final static Level level = Level.WARNING;
    static ContinuousTimeContextIF defaultContext;
    
    // execution statistics
    static long maxMem=0;
    static long iniTime;
    static long endTime;
    
    
    public static void main( String[] args )
    {
        // Logger init
        //////////////
        Logger logger = Logger.getLogger("ExperimentLog");
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
                
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        experimentInfo.append("Optimization experiment (").append(optimization_method).append(" maxIters:").append(maxTrainingIterations).append("): ").append(recommender_method).append(newline);
        System.out.print(experimentInfo);

        logger.info("Starting experiment");
        iniTime = new java.util.Date().getTime();
        checkMem();
        
        // Data load
        logger.info("Loading data");
        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        ModelIF<Object, Object, ContinuousTimeContextIF> model = dataset.getModel();
        logger.info("Data loaded");
        final StringBuilder datasetInfo = new StringBuilder();
        datasetInfo.append(newline).append("DatasetSummary").append(newline);
        datasetInfo.append("Name\t").append(dataset).append(newline);
        datasetInfo.append(new SummaryPrinter().summary(model, null));
        experimentInfo.append(datasetInfo);
        System.out.print(datasetInfo);
        checkMem();
        
        // Training-test splitter
        logger.info("Splitting data");        
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter;
        splitter = new CommonDatasetSplitters<Object, Object, ContinuousTimeContextIF>().proportion(testProportion).getDatasetSplitter(split_method);
        final StringBuilder splitterInfo = new StringBuilder();
        splitterInfo.append("Splitter\t").append(splitter).append(newline);
        experimentInfo.append(splitterInfo);
        System.out.print(splitterInfo);
        checkMem();
        
        // Candidate Items  
        CandidateItemsBuilder<Object, Object, ContinuousTimeContextIF> candidateItemsBuilder;
        candidateItemsBuilder = new CandidateItemsBuilder<Object, Object, ContinuousTimeContextIF>()
                .threshold(relevance_threshold)
                .set(candidates);
        
        SplitIF<Object, Object, ContinuousTimeContextIF> splits[] = splitter.split(model);
        logger.info("Data splitted");
        checkMem();
        
        
        // Process each split
        for (SplitIF<Object, Object, ContinuousTimeContextIF> split:splits){
            logger.info("Processing a new split");
            
            // Recommender initialization
            /////////////////////////////
            logger.info("Initializing recommender");
            
            RecommenderIF<Object, Object, ContinuousTimeContextIF> recommender;
            ModelBasedRecommenderOptimizer optimizer = new RecommenderOptimizerFactory<Object, Object, ContinuousTimeContextIF>()
                    .getOptimizer(optimization_method, splitter, maxOptimizationIterations, maxTrainingIterations, maxThreads);            
            recommender = optimizer
                    .getRecommender(recommender_method, split, features, bins);
            final StringBuilder optimizerInfo = new StringBuilder();
            optimizerInfo.append("Optimization\t").append(optimizer).append(newline);
            experimentInfo.append(optimizerInfo);
            System.out.print(optimizerInfo);
            
            final StringBuilder recommenderInfo = new StringBuilder();
            recommenderInfo.append("Recommender\t").append(recommender).append(newline);
            experimentInfo.append(recommenderInfo);
            System.out.print(recommenderInfo);
            checkMem();
            
            
            
            ModelIF<Object, Object, ContinuousTimeContextIF> trainSet = split.getTrainingSet();
            ModelIF<Object, Object, ContinuousTimeContextIF> testSet  = split.getTestingSet();
            ContextualModelUtils<Object, Object, ContinuousTimeContextIF> eTrain = new ContextualModelUtils<Object, Object, ContinuousTimeContextIF>(trainSet);
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
            defaultContext = new ContinuousTimeContext(eTrain.getMaxDate().getTime());
            checkMem();

            // Relevants & Non-Relevants
            ////////////////////////////
            logger.info("Computing candidate items");        
            CandidateItemsIF<Object, Object, ContinuousTimeContextIF> candidateItems;
            candidateItems = candidateItemsBuilder.buildCandidateItems(split);
            final StringBuilder candidateItemsInfo = new StringBuilder();
            candidateItemsInfo.append("CandidateItems\t").append(candidateItems).append(newline);
            experimentInfo.append(candidateItemsInfo);
            System.out.print(candidateItemsInfo);
            checkMem();

            EXPERIMENT_PREFIX = dataset.toString() + "__"  + recommender_method + "__" + splitter + candidateItems + "__";
            
            // Files init
            if (SAVE_RESULTS){
                logger.info("Deleting previous results files");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+METRICS_FILE);
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+RELEVANTS_FILE+".gz");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE+".gz");
            }
            
            
            // Metrics initialization
            /////////////////////////
            MetricIF<Object, Object, ContinuousTimeContextIF> metricsComputer[] = new CommonRankingMetrics().setLevels(levels).setSplit(split).getMetrics(rankingMetrics);

            // Metrics computation
            //////////////////////
            logger.info("Computing recommendation and metrics");
            int nUser=0;
            int tUsers=testSet.getUsers().size();
            for (Object user:testSet.getUsers()){
                nUser++;
                logger.log(Level.INFO, "Processing user {0} out of {1}", new Object[]{String.format(Locale.US,"%,10d",nUser), String.format(Locale.US,"%,d",tUsers)});
                int j = 0;
                // Determines items to evaluate
                Set<Object> userRelevantSet = candidateItems.getRelevantSet(user, null);
                Set<Object> userNotRelevantSet = candidateItems.getNonRelevantSet(user, null);

                Set<Object> itemsToEvaluate = new TreeSet<Object>();
                itemsToEvaluate.addAll(userRelevantSet);
                itemsToEvaluate.addAll(userNotRelevantSet);

                // Relevant set info
                if (SAVE_RESULTS){
                    final StringBuilder relevantsInfo = new StringBuilder();
                    for (Object item:userRelevantSet){
                        relevantsInfo.append(user).append("\t0\t").append(item).append("\t1").append(newline);
                    }
                    PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+RELEVANTS_FILE+".gz", relevantsInfo.toString());
                }

                // Computes predictions for each item
                List<RecommendationIF<Object>> userRecommendations = new ArrayList<RecommendationIF<Object>>();
                for (Object item:itemsToEvaluate){
                    ContinuousTimeContextIF context = getDefaultContext();
                    Collection<PreferenceIF<Object, Object, ContinuousTimeContextIF>> prefs = (Collection<PreferenceIF<Object, Object, ContinuousTimeContextIF>>)testSet.getPreferences(user, item);
                    if (prefs != null){
                        PreferenceIF<Object, Object, ContinuousTimeContextIF> pref = (PreferenceIF<Object, Object, ContinuousTimeContextIF>)prefs.toArray()[0];
                        context = pref.getContext();
                    }
                    Float prediction = recommender.predict(user, item, context);
                    RecommendationIF<Object> recom;
                    if (!prediction.isNaN()){
                        recom = new Recommendation(item, prediction, true);
                        userRecommendations.add(recom);
                    }
                }
                Collections.sort(userRecommendations);

                // Recommendations info
                if (SAVE_RESULTS){
                    final StringBuilder recommendationsInfo = new StringBuilder();
                    for (RecommendationIF<Object> r:userRecommendations){
                        recommendationsInfo.append(user).append("\t1\t").append(r.getItemID()).append("\t").append(++j).append("\t").append(r.getValue()).append("\t0").append(newline);
                    }
                    PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE+".gz", recommendationsInfo.toString());
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
                MetricResultsIF<Object> results = metricsComputer[i].getResults();
                metricsInfo.append(results.columnFormat());
            }
            experimentInfo.append(metricsInfo);
            System.out.print(metricsInfo);
            checkMem();
        }
        endTime = new java.util.Date().getTime();
        
        logger.info("Finishing");
        long totalTimeMillis=endTime-iniTime;
        long totalTimeSeconds=totalTimeMillis/1000;
        final StringBuilder execInfo = new StringBuilder();
        String maxMemGB = String.format(Locale.US,"%,.1f",maxMem/(double)(1024*1024*1024));
        execInfo.append("time\t").append(String.format(Locale.US,"%,d",totalTimeSeconds)).append("[s]").append(newline);
        execInfo.append("maxMem\t").append(maxMemGB).append("[GB]").append(newline);
        experimentInfo.append(execInfo);
        System.out.print(execInfo);
        
        if (SAVE_RESULTS){
            PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+METRICS_FILE, experimentInfo.toString());
        }
        if (SEND_EMAIL_REPORT){
            String computerName=null;
            try{
                computerName = InetAddress.getLocalHost().getHostName();
            } catch (java.net.UnknownHostException e){
                computerName="unknown";
            }
            String[] mail=new String[]{computerName+" finished job!", experimentInfo.toString()};
            MailSender.main(mail);
        }
        if (SEND_RESULT_FILES){
            String origin=dataset.getResultsPath()+EXPERIMENT_PREFIX+METRICS_FILE;
            String dest=RESULTS_SERVER_PATH+EXPERIMENT_PREFIX+METRICS_FILE;
//            String origin=dataset.getPath()+"results/"+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE+".gz";
//            String dest=RESULTS_SERVER_PATH+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE+".gz";
            System.out.println("orig:"+origin);
            System.out.println("dest:"+dest);
            PrintUtils.ftpTransfer(origin, dest);
            
//            String[] data={origin, dest};
//            SecureCopier.ScpTo(data);
        }
    }
    
    private static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    private static ContinuousTimeContextIF getDefaultContext(){
        ContinuousTimeContextIF theContext = defaultContext;
        return theContext;
    }
}
