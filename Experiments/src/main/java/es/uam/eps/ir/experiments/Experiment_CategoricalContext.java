package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.cars.model.ContinuousTimeUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.ItemSplitDataset;
import es.uam.eps.ir.metrics.AveragedMetricResults;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.optimization.TrainValidationSetsGenerator;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.impl.Split;
import es.uam.eps.ir.cars.itemsplitting.ImpurityComputerIF;
import es.uam.eps.ir.cars.itemsplitting.InformationGainImpurity;
import es.uam.eps.ir.cars.itemsplitting.MeanImpurity;
import es.uam.eps.ir.cars.itemsplitting.ProportionImpurity;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelUtils.ModelPrintUtils;
import utils.LogFormatter;
import utils.MailSender;
import utils.PrintUtils;
import utils.SummaryPrinter;
/**
 *
 */
public class Experiment_CategoricalContext 
{
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.Context_Movies_IRG;
    // Dataset_ItemSplit
    static boolean item_split = false;
    static ImpurityComputerIF is_impurity = new MeanImpurity();
    static List<String> is_contexts = Arrays.asList("Day");
    
    // Evaluation Methodology
    static CommonDatasetSplitters.METHOD   split_method        = CommonDatasetSplitters.METHOD.CommunityRandomOrderProportionHoldout;
    static double testProportion = 0.1;  // 0.085 for Ml100k // 0.054 for Ml1m
    static int testSize = 10;
    static CandidateItemsBuilder.CANDIDATE_ITEMS candidates    = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST_USER_MEAN;
    static float relevance_threshold=(float) 0.0;
    static NonPersonalizedPrediction.Type non_personalized = NonPersonalizedPrediction.Type.None;
    static boolean controlPredictionValue = true;
    static boolean genTrainingAndTestFiles = false;
    
    // Evaluation Metrics
    static CommonRankingMetrics.METRICS rankingMetrics = CommonRankingMetrics.METRICS.NONE;
    static CommonErrorMetrics.METRICS errorMetrics = CommonErrorMetrics.METRICS.ALL;
    final static List<Integer> levels = Arrays.asList(5,10,20,50);
    
//    // Recommendation method
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.ContextualPreFiltering_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.ContextualPostFiltering_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_ItemBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.kNN_CosineSimilarity_ItemBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.kNN_CosineSimilarity_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.Raw_CosineSimilarity_ItemBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.Raw_CosineSimilarity_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.TimeDecay_UserBased;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.ItemPopularity;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.ItemAvg;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.TimeContextItemSplitting_KNN;
    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.MF_Default;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.MF_Optimized;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.Hybrid;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.Mahout_SVDRecommender;
//    static CommonRecommenders.METHOD       recommender_method  = CommonRecommenders.METHOD.Mahout_kNN_IBRecommender;
    
    
    static int neighbors = 20;    
    static int minCommonRatingsForPrediction = 3;
    final static double halfLifeProportion = 0.75;
    static int halfLifeDays = 0;
    
    // Only for optimization of Model-based recommenders
    static int factors = 60;    
    static int iterations =100;    
    


    // Parallel processor?
    static boolean useParallelProcessor = false;
    static int maxThreads = 1;
    // Use validation data
    final static boolean useValidationData = false;
    // Training Data Filtering
    final static FilterUtil.FILTER[] train_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
    // Test Data Filtering
    final static FilterUtil.FILTER[] test_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
    
    // Results Saving
    static boolean SAVE_RESULTS = true;
    static boolean SAVE_DETAILED_RESULTS = false;
    static boolean SEND_EMAIL_REPORT=false;
    static boolean SEND_RESULT_FILES=false;
    static String EXPERIMENT_PREFIX = "";
    static String EXPERIMENT_DESCRIPTION = "";
    final static String DESCRIPTION_FILE = "description.txt";
    final static String METRICS_FILE = "metrics.txt";
    final static String PERUSERMETRICS_FILE = "user_metrics.txt";
    final static String RELEVANTS_FILE = "relevants.txt.gz";
    final static String RECOMMENDATIONS_FILE = "recommendations.txt.gz";
    final static String RESULTS_SERVER_PATH = "/results/experiments/";
    static Level level = Level.INFO;
    
    // execution statistics
    static long maxMem=0;
    static long iniTime;
    static long endTime;
    
    final static Logger logger = Logger.getLogger("ExperimentLog");
    
    
    public static void main( String[] args )
    {
        // Logger init
        //////////////
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        
        // process parameters
        processArgs(args);
        
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        experimentInfo.append("Experiment:").append(dataset_name).append(", ").append(recommender_method).append(", ").append(split_method).append(", ").append(candidates).append(newline);

        logger.info("Starting experiment");
        iniTime = new java.util.Date().getTime();
        checkMem();
        
        // Data load
        logger.info("Loading data");
        DatasetIF<Object, Object, ContextIF> originalDataset = new CommonDatasets(args).getDataset(dataset_name);
        
        DatasetIF<Object, Object, ContextIF> dataset;
        if (item_split){
            dataset = new ItemSplitDataset(originalDataset, is_impurity, is_contexts);
        } else {
            dataset = originalDataset;
        }
        
        ModelIF<Object, Object, ContextIF> model = dataset.getModel();
        logger.info("Data loaded");
        final StringBuilder datasetInfo = new StringBuilder();
        datasetInfo.append(newline).append("DatasetSummary").append(newline);
        datasetInfo.append("Name\t").append(dataset).append(newline);
        datasetInfo.append(new SummaryPrinter().summary(model, null));
        experimentInfo.append(datasetInfo);
        System.out.print(datasetInfo);
        checkMem();
//        System.exit(0);
        
        // Training-test splitter
        logger.info("Splitting data");        
        DatasetSplitterIF<Object, Object, ContextIF> splitter;
        splitter = new CommonDatasetSplitters<Object, Object, ContextIF>().proportion(testProportion).size(testSize).dataset(dataset).getDatasetSplitter(split_method);
        final StringBuilder splitterInfo = new StringBuilder();
        splitterInfo.append("Splitter\t").append(splitter).append(newline);
        experimentInfo.append(splitterInfo);
        System.out.print(splitterInfo);
        checkMem();
        
        // Candidate Items  
        CandidateItemsBuilder<Object, Object, ContextIF> candidateItemsBuilder;
        candidateItemsBuilder = new CandidateItemsBuilder<Object, Object, ContextIF>()
                .threshold(relevance_threshold)
                .set(candidates);
        
        SplitIF<Object, Object, ContextIF> splits[] = splitter.split(model);
        logger.info("Data splitted");
        checkMem();
        
        
        MetricResultsIF[] averagedRankingMetricsResults = null;
        MetricResultsIF[] averagedErrorMetricsResults = null;

        int nSplits = 0;
        // Process each split
        for (SplitIF<Object, Object, ContextIF> split:splits){
            nSplits++;
            EXPERIMENT_DESCRIPTION = "";            
            // Only for validation tests
            if (useValidationData){
                TrainValidationSetsGenerator generator = new TrainValidationSetsGenerator(split.getTrainingSet(), new ContextualModelUtils(split.getTrainingSet()), splitter);
                SplitIF<Object, Object, ContextIF> trainValidationSplit = new Split(generator.getTrainSet(), generator.getValidationSet());
                split = trainValidationSplit;
            }
            
            // Data filtering
            /////////////////
            SplitFilterer<Object, Object, ContextIF> filterer;
            filterer = new SplitFilterer<Object, Object, ContextIF>();
            split = filterer.processSplit(split, train_filters, test_filters);
            
            // Generation of Training and Test files (if required)
            String datasetString  = dataset.toString();
            String datasetDetailsString = dataset.getDetails();
            if (useValidationData){
                datasetDetailsString = datasetDetailsString + "_validation";
            }
            if (genTrainingAndTestFiles){
                String filePrefix = dataset.getResultsPath() + datasetString  + "/" + datasetDetailsString + EXPERIMENT_PREFIX + "__" + splitter + "__";
                PrintUtils.toGz(filePrefix + "full.txt.gz", ModelPrintUtils.printModel2(model));
                PrintUtils.toGz(filePrefix + "Tr.txt.gz", ModelPrintUtils.printModel2(split.getTrainingSet()));
                PrintUtils.toGz(filePrefix + "Te.txt.gz", ModelPrintUtils.printModel2(split.getTestingSet()));
                StringBuilder builder = new StringBuilder();
                for (Object user : split.getTestingSet().getUsers()){
                    builder.append(user).append(newline);
                }
                PrintUtils.toFile(filePrefix + "TeUsers.txt", builder.toString());
            }
            
            // Recommender initialization
            /////////////////////////////
            logger.info("Initializing recommender");
            RecommenderIF<Object, Object, ContextIF> recommender;
            String recommenderString = recommender_method.toString();
            if (recommender_method.equals(CommonRecommenders.METHOD.TimeDecay_UserBased)){
                if (halfLifeProportion > 0.0){
                    long diff = ContinuousTimeUtils.trainingTimespan(split);
                    Long longDays = (long) (diff * halfLifeProportion) / (long) (1000*60*60*24);
                    halfLifeDays = longDays.intValue();
                    recommenderString += "_T0Prop=" + halfLifeProportion;
                }
                recommenderString += "_T0=" + halfLifeDays;
            }
            if (recommender_method.equals(CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp)){
                    recommenderString += "_Prop=" + halfLifeProportion;
            }
            recommenderString += "_NonPers=" + non_personalized;
            recommender = new CommonRecommenders()
                    .neighbors(neighbors)
                    .factors(factors)
                    .iterations(iterations)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .halfLife(halfLifeDays)
                    .halfLifeProp(halfLifeProportion)
                    .trTimespan(split)
                    .optimizationData(splitter, split)
                    .getRecommender(recommender_method, split.getTrainingSet());
            final StringBuilder recommenderInfo = new StringBuilder();
            recommenderInfo.append("Recommender\t").append(recommender).append(newline);
            experimentInfo.append(recommenderInfo);
            System.out.print(recommenderInfo);
            checkMem();

            if (recommender instanceof ModelBasedRecommender){
                ((ModelBasedRecommender)recommender).trainModel();
            }
            checkMem();
            
            // Processing split
            ///////////////////
            SplitRecommendationsProcessor<Object, Object, ContextIF> processer;
            if (useParallelProcessor){
                processer = new SplitRecommendationsProcessor_Parallel<Object, Object, ContextIF>(maxThreads);
            }
            else {
                processer = new SplitRecommendationsProcessor<Object, Object, ContextIF>();
            }
            
            processer.processSplit(recommender, split, candidateItemsBuilder, non_personalized, levels, rankingMetrics, errorMetrics, SAVE_DETAILED_RESULTS, SEND_EMAIL_REPORT, controlPredictionValue);
            

            // Post processing
            //////////////////
            
            // Computing metrics' average
            if (errorMetrics != CommonErrorMetrics.METRICS.NONE){
                MetricResultsIF[] errorMetricsResults = processer.getErrorMetricsResults();

                
                if (averagedErrorMetricsResults == null){
                    averagedErrorMetricsResults = new MetricResultsIF[errorMetricsResults.length];
                    for (int i = 0; i < errorMetricsResults.length; i++){
                        averagedErrorMetricsResults[i] = new AveragedMetricResults(errorMetricsResults[i]);
                    }                
                } else{
                    for (int i = 0; i < errorMetricsResults.length; i++){
                        ((AveragedMetricResults)averagedErrorMetricsResults[i]).add(errorMetricsResults[i]);
                    }
                }
            }
            
            if (rankingMetrics != CommonRankingMetrics.METRICS.NONE){
                MetricResultsIF[] rankingMetricsResults = processer.getRankingMetricsResults();

                
                if (averagedRankingMetricsResults == null){
                    averagedRankingMetricsResults = new MetricResultsIF[rankingMetricsResults.length];
                    for (int i = 0; i < rankingMetricsResults.length; i++){
                        averagedRankingMetricsResults[i] = new AveragedMetricResults(rankingMetricsResults[i]);
                    }
                } else {
                    for (int i = 0; i < rankingMetricsResults.length; i++){
                        ((AveragedMetricResults)averagedRankingMetricsResults[i]).add(rankingMetricsResults[i]);
                    }
                }
            }
            
            if (maxMem < processer.getMaxMemUsage()){
                maxMem = processer.getMaxMemUsage();
            }
            
            experimentInfo.append(filterer.getFiltersDetails());
            experimentInfo.append(processer.getExperimentDetails());
            EXPERIMENT_DESCRIPTION = datasetString  + "/" + datasetDetailsString + "__"  + recommenderString + "__" + splitter + candidates + "_" + relevance_threshold + "__";
            
            // Files init
            if (SAVE_RESULTS){
                logger.info("Deleting previous results files");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+METRICS_FILE);
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+PERUSERMETRICS_FILE);
                logger.info("generating new results files");
                PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+PERUSERMETRICS_FILE, processer.getPerUserMetrics());
                if (SEND_RESULT_FILES){
                    sendFile(dataset, PERUSERMETRICS_FILE);
                }
            }
            if (SAVE_DETAILED_RESULTS){
                logger.info("Deleting previous detailed results files");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+RELEVANTS_FILE);
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+RECOMMENDATIONS_FILE);
                logger.info("generating new detailed results files");
                PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+RELEVANTS_FILE, processer.getRelevantsDetails());
//                PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE, processer.getRecommendationsDetails());
                List<String> recommendationLists = processer.getRecommendationsDetails();
                int list = 0;
                for (String recommendationList : recommendationLists){
                    PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "list" + list + RECOMMENDATIONS_FILE, recommendationList);
                    list++;
                }
                if (SEND_RESULT_FILES){
                    sendFile(dataset, RELEVANTS_FILE);
                    sendFile(dataset, RECOMMENDATIONS_FILE);
                }
            }
        }
        endTime = new java.util.Date().getTime();
        
        logger.info("Finishing");
        long totalTimeMillis=endTime-iniTime;
        long totalTimeSeconds=totalTimeMillis/1000;
        final StringBuilder execInfo = new StringBuilder();
        String maxMemGB = String.format(Locale.US,"%,.1f",maxMem/(double)(1024*1024*1024));
        
        // Averaged results
        final StringBuilder averagedMetricsInfo = new StringBuilder();
        if (nSplits > 1) {
            averagedMetricsInfo.append(datasetInfo);
            averagedMetricsInfo.append("===============================================================").append(newline);
            averagedMetricsInfo.append("===============================================================").append(newline);
            averagedMetricsInfo.append("Averaged Results (over ").append(nSplits).append("  splits)").append(newline);
            if (averagedRankingMetricsResults != null){
                for (int i = 0; i < averagedRankingMetricsResults.length; i++){
                    averagedMetricsInfo.append(averagedRankingMetricsResults[i].columnFormat());
                }
            }
            if (averagedErrorMetricsResults != null){
                for (int i = 0; i < averagedErrorMetricsResults.length; i++){
                    averagedMetricsInfo.append(averagedErrorMetricsResults[i].columnFormat());
                }
            }
            experimentInfo.append(averagedMetricsInfo);
            System.out.print(averagedMetricsInfo);
        }
        
        execInfo.append("time\t").append(String.format(Locale.US,"%,d",totalTimeSeconds)).append("[s]").append(newline);
        execInfo.append("maxMem\t").append(maxMemGB).append("[GB]").append(newline);
        experimentInfo.append(execInfo);
        System.out.print(execInfo);
        
        if (SAVE_RESULTS){
            PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+METRICS_FILE, experimentInfo.toString());
            if (nSplits > 1){
                PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+DESCRIPTION_FILE, experimentInfo.toString());
                PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+METRICS_FILE, averagedMetricsInfo.toString());
            }
            if (SEND_RESULT_FILES){
                sendFile(dataset, METRICS_FILE);
            }
        }
        if (SEND_EMAIL_REPORT){
            String computerName;
            try{
                computerName = InetAddress.getLocalHost().getHostName();
            } catch (java.net.UnknownHostException e){
                computerName="unknown";
            }
            String[] mail=new String[]{computerName+" finished job!", experimentInfo.toString()};
            MailSender.main(mail);
        }
    }
    
    private static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    private static void sendFile(DatasetIF dataset, String file){
        String origin=dataset.getResultsPath()+EXPERIMENT_PREFIX+"_"+EXPERIMENT_DESCRIPTION+file;
        String dest=RESULTS_SERVER_PATH+EXPERIMENT_PREFIX+"_"+EXPERIMENT_DESCRIPTION+file;
        logger.log(Level.INFO,"Storing {0}" + " in " + RESULTS_SERVER_PATH, origin);

        PrintUtils.ftpTransfer(origin, dest);        
    }
    
    private static void processArgs(String[] args){
        // process parameters
        for (String arg:args){
            if (arg.startsWith("dataset")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("Movies_IRG")){
                    dataset_name = CommonDatasets.DATASET.Context_Movies_IRG;
                }
                else if (sm[1].equalsIgnoreCase("Musicians_IRG")){
                    dataset_name = CommonDatasets.DATASET.Context_Musicians_IRG;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("is_impurity")){
                item_split = true;
                String[] sm = arg.split("=");
                String[] smi = sm[1].split(",");
                if (smi.length > 2) {
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
                if (smi[0].equalsIgnoreCase("mean")){
                    if (smi.length==2){
                        double threshold = Double.parseDouble(smi[1]);
                        is_impurity = new MeanImpurity(threshold, 4);
                    }
                    else{
                        is_impurity = new MeanImpurity();
                    }
                }
                else if (smi[0].equalsIgnoreCase("proportion")){
                    if (smi.length==2){
                        double threshold = Double.parseDouble(smi[1]);
                        is_impurity = new ProportionImpurity(threshold, 4);
                    }
                    else{
                        is_impurity = new ProportionImpurity();                        
                    }
                }
                else if (smi[0].equalsIgnoreCase("IG")){
                    if (smi.length==2){
                        double threshold = Double.parseDouble(smi[1]);                    
                        is_impurity = new InformationGainImpurity(threshold, 4);
                    }
                    else{
                        is_impurity = new InformationGainImpurity();
                    }
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("is_context")){
                item_split = true;
                is_contexts = new ArrayList<String>();
                String[] sm = arg.split("=");
                is_contexts.addAll(Arrays.asList(sm[1].split(",")));
            }
            else if (arg.startsWith("recommender")){
                String[] sm = arg.split("=");
                String[] smi = sm[1].split(",");
                if (smi[0].equalsIgnoreCase("kNN_UB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_UserBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("kNN_IB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_ItemBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("kNN_Cos_UB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_CosineSimilarity_UserBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("kNN_Cos_IB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_CosineSimilarity_ItemBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("Raw_Cos_UB")){
                    recommender_method = CommonRecommenders.METHOD.Raw_CosineSimilarity_UserBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("Raw_Cos_IB")){
                    recommender_method = CommonRecommenders.METHOD.Raw_CosineSimilarity_ItemBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("PRF_UB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_PRF_UserBased;
                }
                else if (smi[0].equalsIgnoreCase("POF_UB")){
                    recommender_method = CommonRecommenders.METHOD.kNN_POF_UserBased;
                    if (smi.length == 2){
                        neighbors = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("IAvg")){
                    recommender_method = CommonRecommenders.METHOD.ItemAvg;
                }
                else if (smi[0].equalsIgnoreCase("TimeDecay_UB")){
                    recommender_method = CommonRecommenders.METHOD.TimeDecay_UserBased;
                    if (smi.length >= 2){
                        halfLifeDays = Integer.parseInt(smi[1]);
                    }
                    if (smi.length == 3){
                        neighbors = Integer.parseInt(smi[2]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("MF")){
                    recommender_method = CommonRecommenders.METHOD.MF_Default;
                    if (smi.length >= 2){
                        factors = Integer.parseInt(smi[1]);
                    }
                    if (smi.length == 3){
                        iterations = Integer.parseInt(smi[2]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("MFOpt")){
                    recommender_method = CommonRecommenders.METHOD.MF_Optimized;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("split_method")){
                String[] sm = arg.split("=");
                String[] smi = sm[1].split(",");
                if (smi.length > 2) {
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }                
                if (smi[0].equalsIgnoreCase("UC_TI_PR")){
                    split_method = CommonDatasetSplitters.METHOD.UserRandomOrderProportionHoldout;
                    if (smi.length == 2){
                        testProportion = Double.parseDouble(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("UC_TD_PR")){
                    split_method = CommonDatasetSplitters.METHOD.UserTimeOrderProportionHoldout;
                    if (smi.length == 2){
                        testProportion = Double.parseDouble(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("CC_TI_PR")){
                    split_method = CommonDatasetSplitters.METHOD.CommunityRandomOrderProportionHoldout;
                    if (smi.length == 2){
                        testProportion = Double.parseDouble(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("CC_TD_PR")){
                    split_method = CommonDatasetSplitters.METHOD.CommunityTimeOrderProportionHoldout;
                    if (smi.length == 2){
                        testProportion = Double.parseDouble(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("CC_TD_TP")){
                    split_method = CommonDatasetSplitters.METHOD.CommunityTimeOrderTimeProportionHoldout;
                    if (smi.length == 2){
                        testProportion = Double.parseDouble(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("UC_TD_FS")){
                    split_method = CommonDatasetSplitters.METHOD.UserTimeOrderFixedHoldout;
                    if (smi.length == 2){
                        testSize = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("CC_XFold")){
                    split_method = CommonDatasetSplitters.METHOD.CommunityXFold_CV;
                    if (smi.length == 2){
                        testSize = Integer.parseInt(smi[1]);
                    }
                }
                else if (smi[0].equalsIgnoreCase("UC_XFold")){
                    split_method = CommonDatasetSplitters.METHOD.UserXFold_CV;
                    if (smi.length == 2){
                        testSize = Integer.parseInt(smi[1]);
                    }
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }                
            }
            else if (arg.startsWith("candidate_items")){
                String[] sm = arg.split("=");
                String[] smi = sm[1].split(",");                
                if (smi[0].equalsIgnoreCase("UTe")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST;
                }
                else if (smi[0].equalsIgnoreCase("UTeUMean")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST_USER_MEAN;
                }
                else if (smi[0].equalsIgnoreCase("CTe")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.COMMUNITY_TEST;
                }
                else if (smi[0].equalsIgnoreCase("CTr")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.COMMUNITY_TRAINING;
                }
                else if (smi[0].equalsIgnoreCase("OPR")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.ONE_PLUS_RANDOM;
                }
                else if (smi[0].equalsIgnoreCase("OPRC")){
                    candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.ONE_PLUS_RANDOM_CONTEXT;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
                if (smi.length == 2){
                    relevance_threshold = Float.parseFloat(smi[1]);
                }
                if (smi.length > 2) {
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
                
            }            
            else if (arg.startsWith("ranking_metrics")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("all")){
                    rankingMetrics = CommonRankingMetrics.METRICS.ALL;
                }
                else if (sm[1].equalsIgnoreCase("common")){
                    rankingMetrics = CommonRankingMetrics.METRICS.COMMON;
                }
                else if (sm[1].equalsIgnoreCase("basic")){
                    rankingMetrics = CommonRankingMetrics.METRICS.BASIC;
                }
                else if (sm[1].equalsIgnoreCase("none")){
                    rankingMetrics = CommonRankingMetrics.METRICS.NONE;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("error_metrics")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("all")){
                    errorMetrics = CommonErrorMetrics.METRICS.ALL;
                }
                else if (sm[1].equalsIgnoreCase("basic")){
                    errorMetrics = CommonErrorMetrics.METRICS.COMMON;
                }
                else if (sm[1].equalsIgnoreCase("rmse")){
                    errorMetrics = CommonErrorMetrics.METRICS.RMSE;
                }
                else if (sm[1].equalsIgnoreCase("none")){
                    errorMetrics = CommonErrorMetrics.METRICS.NONE;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("non_pers")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("IMean")){
                    non_personalized = NonPersonalizedPrediction.Type.ItemMean;
                }
                else if (sm[1].equalsIgnoreCase("IUMean")){
                    non_personalized = NonPersonalizedPrediction.Type.ItemUserMean;
                }
                else if (sm[1].equalsIgnoreCase("IUOMean")){
                    non_personalized = NonPersonalizedPrediction.Type.ItemUserOverallMean;
                }
                else if (sm[1].equalsIgnoreCase("UMean")){
                    non_personalized = NonPersonalizedPrediction.Type.UserMean;
                }
                else if (sm[1].equalsIgnoreCase("OMean")){
                    non_personalized = NonPersonalizedPrediction.Type.OverallMean;
                }
                else if (sm[1].equalsIgnoreCase("None")){
                    non_personalized = NonPersonalizedPrediction.Type.None;
                }
                else{
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }                
            }            
            else if (arg.startsWith("max_threads")){
                String[] sm = arg.split("=");
                maxThreads = Integer.parseInt(sm[1]);
                if (maxThreads > 1){
                    useParallelProcessor = true;
                }
                else{
                    useParallelProcessor = false;                    
                }
            }
            else if (arg.startsWith("details")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    SAVE_DETAILED_RESULTS = true;
                }
                else{
                    SAVE_DETAILED_RESULTS = false;
                }
            }
            else if (arg.startsWith("genDataFiles")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    genTrainingAndTestFiles = true;
                }
                else{
                    genTrainingAndTestFiles = false;
                }
            }
            else if (arg.startsWith("control_predictions")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    controlPredictionValue = true;
                }
                else{
                    controlPredictionValue = false;
                }
            }
            else if (arg.startsWith("knn_min_common_ratings")){
                String[] sm = arg.split("=");
                    minCommonRatingsForPrediction = Integer.parseInt(sm[1]);
                
            }
            else if (arg.startsWith("prefix")){
                String[] sm = arg.split("=");
                EXPERIMENT_PREFIX = sm[1];
            }
            else{
                System.err.println("unrecognized option: " + arg);
                System.exit(1);
            }
        }        
    }
}
