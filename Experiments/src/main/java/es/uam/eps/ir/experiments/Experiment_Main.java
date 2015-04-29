package es.uam.eps.ir.experiments;

import es.uam.eps.ir.cars.bias.ModelBasedRecommender;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.itemsplitting.CategoricalContextItemSplitter;
import es.uam.eps.ir.cars.itemsplitting.ChiSquaredImpurity;
import es.uam.eps.ir.cars.itemsplitting.ContextBasedItemSplitterIF;
import es.uam.eps.ir.cars.itemsplitting.FisherExactImpurity;
import es.uam.eps.ir.cars.itemsplitting.ImpurityComputerIF;
import es.uam.eps.ir.cars.itemsplitting.InformationGainImpurity;
import es.uam.eps.ir.cars.itemsplitting.MeanImpurity;
import es.uam.eps.ir.cars.itemsplitting.ProportionImpurity;
import es.uam.eps.ir.cars.itemsplitting.TimeContextItemSplitter;
import es.uam.eps.ir.cars.model.ContinuousTimeUtils;
import es.uam.eps.ir.cars.model.ItemSplittingExplicitModel;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.metrics.AveragedMetricResults;
import es.uam.eps.ir.metrics.MetricResultsIF;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.optimization.TrainValidationSetsGenerator;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.impl.Split;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelUtils.ModelPrintUtils;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import utils.LogFormatter;
import utils.MailSender;
import utils.PrintUtils;
import utils.SummaryPrinter;
/**
 *
 */
public class Experiment_Main 
{
    // Dataset
    static CommonDatasets.DATASET dataset_name;
    // Dataset_ItemSplit
    static boolean item_split = false;  // Perform item splitting pre-filtering?
    static ImpurityComputerIF is_impurity;  // Impurity test used in item splitting pre-filtering
    static int minContextSizeForSplitting = 3; // Min. number of ratings in contexts to perform the splitting
    static List<String> is_contexts = null; // context variables used in item splitting pre-filtering
    static List<String> filtering_contexts = null; // context variables used in pre- and post-filtering (PRF and POF)
    
    // Evaluation Methodology
    static CommonDatasetSplitters.METHOD   split_method; // Training-test splitting
    static double testProportion = 0;  // 0.085 for Ml100k // 0.054 for Ml1m
    static int testSize = 0;
    static CandidateItemsBuilder.CANDIDATE_ITEMS candidates; // items to be ranked in top-N recommendation
    static float relevance_threshold=(float) 0.0; // rating threshold for relevance assessment
    static NonPersonalizedPrediction.Type non_personalized; // defaul rating value
    static boolean controlPredictionValue = false; // adjust prediction values? (shrink to min/max rating value)
    static boolean genTrainingAndTestFiles = false; // save training and test data in text files?
    
    // Evaluation Metrics
    static CommonRankingMetrics.METRICS rankingMetrics;
    static CommonErrorMetrics.METRICS errorMetrics;
    static List<Integer> levels = Arrays.asList(5,10,20,50);
    static int nForOnePlusRandom = 100;
    
    // Recommendation method
    static CommonRecommenders.METHOD recommender_method;
    
    static int neighbors = 200;
    static int minCommonRatingsForPrediction = 3;
    static double halfLifeProportion = 0.0;
    static int halfLifeDays = 0;
    
    // Only for optimization of Model-based recommenders
    static int factors = 60;    
    static int iterations = 30;
    static double learnRate = 0.005;
    static double lambda = 0.02;


    // Parallel processor?
    static boolean useParallelProcessor = false;
    static int maxThreads = 1;
    // Use validation data
    static boolean useValidationData = false;
    // Training Data Filtering
    static FilterUtil.FILTER[] train_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
    // Test Data Filtering
    static FilterUtil.FILTER[] test_filters  = 
            new FilterUtil.FILTER[] {
                FilterUtil.FILTER.None
            };
    
    // Results Saving
    static boolean skipIfResultsExist = false;
    static boolean SAVE_RESULTS = true;
    static boolean SAVE_CLASSIFICATION_RESULTS = true;
    static boolean SAVE_DETAILED_RESULTS = false;
    static boolean SEND_EMAIL_REPORT=false;
    static boolean SEND_RESULT_FILES=false;
    static String EXPERIMENT_PREFIX = "";
    static String EXPERIMENT_DESCRIPTION = "";
    static String RESULTS_PATH = null;
    static String CONFIG_FILE = null;
    static GlobalParams params;
    final static String AVERAGES_FILE = "averages.txt";
    final static String METRICS_FILE = "metrics.txt";
    final static String PERUSERMETRICS_FILE = "user_metrics.txt.gz";
    final static String RELEVANTS_FILE = "relevants.txt.gz";
    final static String RECOMMENDATIONS_FILE = "recommendations.txt.gz";
    final static String RESULTS_SERVER_PATH = "/results/experiments/";
    static Level level = Level.SEVERE;
    
    // execution statistics
    static long maxMem=0;
    static long iniTime;
    static long endTime;
    
    final static Logger logger = Logger.getLogger("ExperimentLog");
    
    
    public static void main( String[] args )
    {
        // process parameters
        processArgs(args);
        exec_experiment(args);
    }
    public static void exec_experiment( String[] args ){
        params = GlobalParams.getGlobalParams(CONFIG_FILE);
        // Logger init
        //////////////
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        handler.setFormatter(formatter);
        logger.addHandler(handler);
                
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        experimentInfo.append("Experiment:").append(dataset_name).append(", ").append(recommender_method).append(", ").append(split_method).append(", ").append(candidates).append(newline);

        logger.info("Starting experiment");
        iniTime = new java.util.Date().getTime();
        checkMem();
        
        // Data load
        logger.info("Loading data");
//        DatasetIF<Object, Object, ContextIF> originalDataset = new CommonDatasets(args).getDataset(dataset_name);
        DatasetIF<Object, Object, ContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        
//        DatasetIF<Object, Object, ContextIF> dataset;
//        if (item_split){
//            dataset = new ItemSplitDataset(originalDataset, is_impurity, is_contexts);
//        } else {
//            dataset = originalDataset;
//        }
        if (RESULTS_PATH == null){
            RESULTS_PATH = dataset.getResultsPath();
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
                .nForOnePlusRandom(nForOnePlusRandom)
                .threshold(relevance_threshold)
                .set(candidates);
        
        SplitIF<Object, Object, ContextIF> splits[] = splitter.split(model);
        logger.info("Data splitted");
        checkMem();
        
        
        MetricResultsIF[] averagedRankingMetricsResults = null;
        MetricResultsIF[] averagedErrorMetricsResults = null;

        SplitRecommendationsProcessor<Object, Object, ContextIF> processer;
        if (useParallelProcessor){
            processer = new SplitRecommendationsProcessor_Parallel<Object, Object, ContextIF>(maxThreads);
        }
        else {
            processer = new SplitRecommendationsProcessor<Object, Object, ContextIF>();
        }
        int nSplit = 0;
        // Process each split 
        for (SplitIF<Object, Object, ContextIF> split:splits){
            if (item_split){
                split = getItemSplittingSplit(split, dataset, is_impurity, minContextSizeForSplitting, is_contexts);
            }
            nSplit++;
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
//            String datasetDetailsString = dataset.getDetails();
            String datasetDetailsString = "o";
            if (useValidationData){
                datasetDetailsString = datasetDetailsString + "_validation";
            }
            if (genTrainingAndTestFiles){
                String filePrefix = RESULTS_PATH + datasetString  + "/" + datasetDetailsString + EXPERIMENT_PREFIX + "__" + splitter + "__";
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
            if (recommender_method.toString().startsWith("kNN")){
                    recommenderString += "_k=" + neighbors;                
            }
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
            if (recommender_method.toString().contains("PRF") ||
                recommender_method.toString().contains("POF") ||   
                recommender_method.toString().contains("CM")     ) {
                recommenderString += "[C=";
                for (String context : filtering_contexts){
                    recommenderString += context;
                }
                recommenderString += "]";
            }
            else if (!item_split){
                recommenderString += "[C=ALL]";                
            }
            
            // ItemSplitting details
            if (split.getTrainingSet() instanceof ItemSplittingExplicitModel){
                recommenderString += "_" + ((ItemSplittingExplicitModel)split.getTrainingSet()).getDetails();
            }
            
            recommenderString += "_NonPers=" + non_personalized;
            List<ContextDefinition> selectedCategoricalContextDefinitions = null;
            List<TimeContext> selectedContinuousTimeContextDefinitions = null;
            // Slicer (for pre/post-filtering)
            if (filtering_contexts != null){
                if (dataset instanceof ContextualDatasetIF){
                    List<ContextDefinition> allCategoricalContextDefinitions = ((ContextualDatasetIF)dataset).getContextDefinitions();
                    selectedCategoricalContextDefinitions = new ArrayList<ContextDefinition>();
                    for (String context : filtering_contexts){
                        for (ContextDefinition ctxDef : allCategoricalContextDefinitions){
                            if (ctxDef.getName().equalsIgnoreCase(context)){
                                selectedCategoricalContextDefinitions.add(ctxDef);
                            }
                        }
                    }
                }
                else{
                    selectedContinuousTimeContextDefinitions = new ArrayList<TimeContext>();
                    for (String context : filtering_contexts){
                        for (TimeContext tc : TimeContext.values()){
                            if (tc.name().equalsIgnoreCase(context)){
                                selectedContinuousTimeContextDefinitions.add(tc);
                            }
                        }
                    }
                }
            }                        
            EXPERIMENT_DESCRIPTION = datasetString  + "/" + datasetDetailsString + "__"  + recommenderString + "__" + splitter + candidates + "_" + relevance_threshold + "__";

            // Check if previous results file exist
            logger.log(Level.INFO, "cheking if results file already exists");
            if (skipIfResultsExist){
                String resultsFileName = RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + METRICS_FILE;
                File f = new File(resultsFileName);
                if (f.exists()){
                    logger.log(Level.SEVERE, "Results file {0} already exists!\nNo additional computations required", resultsFileName);
                    System.exit(0);
                }
            }
            
            recommender = new CommonRecommenders()
                    .neighbors(neighbors)
                    .factors(factors)
                    .iterations(iterations)
                    .learnRate(learnRate)
                    .lambda(lambda)
                    .minCommonRatingsForPrediction(minCommonRatingsForPrediction)
                    .halfLife(halfLifeDays)
                    .halfLifeProp(halfLifeProportion)
                    .trTimespan(split)
                    .categoricalContextFilter(selectedCategoricalContextDefinitions)
                    .continuousTimeContextFilter(selectedContinuousTimeContextDefinitions)
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
            processer.processSplit(recommender, split, candidateItemsBuilder, non_personalized, levels, rankingMetrics, errorMetrics, SAVE_CLASSIFICATION_RESULTS || SAVE_DETAILED_RESULTS, SEND_EMAIL_REPORT, controlPredictionValue);
            

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
            // Files init
            if (SAVE_RESULTS){
                logger.info("Deleting previous results files");
                PrintUtils.cleanFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "split" + nSplit + "__" + METRICS_FILE);
                if (nSplit == 1){
                    PrintUtils.cleanFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + PERUSERMETRICS_FILE);
                }
                logger.info("generating new results files");
                PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + PERUSERMETRICS_FILE, processer.getPerUserMetrics(), true);
                if (SEND_RESULT_FILES){
                    sendFile(dataset, PERUSERMETRICS_FILE);
                }
            }
            if (SAVE_CLASSIFICATION_RESULTS){
                if (nSplit == 1){
                    logger.info("Deleting previous classification results files");
                    PrintUtils.cleanFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "testset_" +  RECOMMENDATIONS_FILE);
                }
                List<String> recommendationLists = processer.getRecommendationsDetails();
                String recommendationList = recommendationLists.get(0);
                PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "testset_" +  RECOMMENDATIONS_FILE, recommendationList, true);
            }
                
            if (SAVE_DETAILED_RESULTS){
                logger.info("Deleting previous detailed results files");
                PrintUtils.cleanFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "split" + nSplit + "__" + RELEVANTS_FILE);
                PrintUtils.cleanFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "split" + nSplit + "__" + RECOMMENDATIONS_FILE);
                logger.info("generating new detailed results files");
                PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION + "split" + nSplit + "__" + RELEVANTS_FILE, processer.getRelevantsDetails());
//                PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE, processer.getRecommendationsDetails());
                List<String> recommendationLists = processer.getRecommendationsDetails();
                int list = 0;
                for (String recommendationList : recommendationLists){
                    if (list == 0){
                        PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+ "split" + nSplit + "_testset_" +  RECOMMENDATIONS_FILE, recommendationList);                        
                    }
                    else {
                        PrintUtils.toGz(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+ "split" + nSplit + "_list" + list   + "_" +  RECOMMENDATIONS_FILE, recommendationList);
                    }
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
        if (nSplit > 1) {
            averagedMetricsInfo.append(datasetInfo);
            averagedMetricsInfo.append("===============================================================").append(newline);
            averagedMetricsInfo.append("===============================================================").append(newline);
            averagedMetricsInfo.append("Averaged Results (over ").append(nSplit).append("  splits)").append(newline);
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
//            experimentInfo.append(averagedMetricsInfo);
            System.out.print(averagedMetricsInfo);
        }
        
        execInfo.append("time\t").append(String.format(Locale.US,"%,d",totalTimeSeconds)).append("[s]").append(newline);
        execInfo.append("maxMem\t").append(maxMemGB).append("[GB]").append(newline);
        experimentInfo.append(execInfo);
        System.out.print(execInfo);
        
        if (SAVE_RESULTS){
            PrintUtils.toFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+METRICS_FILE, experimentInfo.toString());
            if (nSplit > 1){
                PrintUtils.toFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+METRICS_FILE, experimentInfo.toString());
                PrintUtils.toFile(RESULTS_PATH+EXPERIMENT_PREFIX+EXPERIMENT_DESCRIPTION+AVERAGES_FILE, averagedMetricsInfo.toString());
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
    
    protected static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    protected static void sendFile(DatasetIF dataset, String file){
        String origin=RESULTS_PATH+EXPERIMENT_PREFIX+"_"+EXPERIMENT_DESCRIPTION+file;
        String dest=RESULTS_SERVER_PATH+EXPERIMENT_PREFIX+"_"+EXPERIMENT_DESCRIPTION+file;
        logger.log(Level.INFO,"Storing {0}" + " in " + RESULTS_SERVER_PATH, origin);

        PrintUtils.ftpTransfer(origin, dest);        
    }
    
    protected static SplitIF getItemSplittingSplit(SplitIF originalSplit, DatasetIF dataset,  ImpurityComputerIF impurityComputer, int minContextSize, List<String> contexts){
        ContextBasedItemSplitterIF itemSplitter;
        
        if (dataset instanceof ContextualDatasetIF){
            List<ContextDefinition> allContextDefinitions = ((ContextualDatasetIF)dataset).getContextDefinitions();
            List<ContextDefinition> selectedContextDefinitions = new ArrayList<ContextDefinition>();
            for (String context : contexts){
                for (ContextDefinition ctxDef : allContextDefinitions){
                    if (ctxDef.getName().equalsIgnoreCase(context)){
                        selectedContextDefinitions.add(ctxDef);
                    }
                }
                
            }
            itemSplitter = new CategoricalContextItemSplitter(impurityComputer, selectedContextDefinitions);
        }
        else{
            List<TimeContext> the_contexts = new ArrayList<TimeContext>();
                for (String context : contexts){
                    for (TimeContext tc : TimeContext.values()){
                        if (tc.name().equalsIgnoreCase(context)){
                            the_contexts.add(tc);
                        }
                    }
                }
            
            itemSplitter = new TimeContextItemSplitter(impurityComputer, the_contexts);            
        }
        itemSplitter.setMinContextSize(minContextSize);
        ItemSplittingExplicitModel trainingModel = new ItemSplittingExplicitModel(itemSplitter, originalSplit.getTrainingSet());
        SplitIF<Object, Object, ContextIF> newSplit = new Split(trainingModel, originalSplit.getTestingSet());
        
        return newSplit;
    }
    
    protected static void processArgs(String[] args){
        
        // Arguments' parse with argparse4j
        ArgumentParser parser = ArgumentParsers.newArgumentParser("experimenter")
                .description("Java library of recommendation algorithms & evaluation procedures and metrics, for recommender systems' experimentation");

        // dataset options
        ArgumentGroup datasetParser = parser.addArgumentGroup("dataset");
        datasetParser.addArgument("dataset")
                .required(true)
                .choices(Arrays.toString(CommonDatasets.DATASET.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .metavar("dataset")
                .help("the dataset to experiment with");
                
        datasetParser.addArgument("--datasets")
                .action(new PrintMessageAction("Available datasets :" + Arrays.toString(CommonDatasets.DATASET.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available datasets");
                
        // recommender options
        ArgumentGroup recommenderParser = parser.addArgumentGroup("recommender");
        recommenderParser.addArgument("recommender")
                .required(true)
                .choices(Arrays.toString(CommonRecommenders.METHOD.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .metavar("algorithm")
                .help("the recommendation algorithm");
        
        recommenderParser.addArgument("--algorithms")
                .action(new PrintMessageAction("Available algorithms :" + Arrays.toString(CommonRecommenders.METHOD.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available recommendation algorithms");
        
        recommenderParser.addArgument("-k")
                .type(Integer.class)
                .setDefault(neighbors)
                .help("number of neighbors in kNN algorithms");

        recommenderParser.addArgument("-c")
                .type(Integer.class)
                .setDefault(minCommonRatingsForPrediction)
                .help("minimum number of neighbors in kNN algorithms required to compute predictions");

        recommenderParser.addArgument("-d")
                .type(Integer.class)
                .setDefault(halfLifeDays)
                .help("number of days to decrease to half value, in TimeDecay algorithms");

        recommenderParser.addArgument("-f")
                .type(Integer.class)
                .setDefault(factors)
                .help("number of factors in Matrix Factorization algorithms");
        recommenderParser.addArgument("-i")
                .type(Integer.class)
                .setDefault(iterations)
                .help("number of iterations in Matrix Factorization algorithms");
        recommenderParser.addArgument("-l")
                .type(Double.class)
                .setDefault(learnRate)
                .help("learning rate in Matrix Factorization algorithms");
        recommenderParser.addArgument("-r")
                .type(Double.class)
                .setDefault(lambda)
                .help("regularization term in Matrix Factorization algorithms");

        
        // training-test data split options
        ArgumentGroup eval_splitParser = parser.addArgumentGroup("eval_split");
        eval_splitParser.addArgument("eval_split")
                .required(true)
                .choices(Arrays.toString(CommonDatasetSplitters.METHOD.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .metavar("method")
                .help("the training-test data split method for evaluation");
        
        eval_splitParser.addArgument("--methods")
                .action(new PrintMessageAction("Available methods for data split :" + Arrays.toString(CommonDatasetSplitters.METHOD.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available methods for data split");
        
        eval_splitParser.addArgument("-p")
                .type(Double.class)
                .setDefault(testProportion)
                .help("proportion of ratings to be asigned into test set");
        
        eval_splitParser.addArgument("-s")
                .type(Integer.class)
                .setDefault(testSize)
                .help("size of test set (in number of ratings)");


        // candidate items options
        ArgumentGroup candidateParser = parser.addArgumentGroup("candidate");
        candidateParser.addArgument("candidate_items")
                .required(true)
                .choices(Arrays.toString(CandidateItemsBuilder.CANDIDATE_ITEMS.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .metavar("candidate_items")
                .help("the candidate items to be ranked in ranking precision metrics computation");

        candidateParser.addArgument("--candidates")
                .action(new PrintMessageAction("Available candidate items strategies :" + Arrays.toString(CandidateItemsBuilder.CANDIDATE_ITEMS.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available candidate items strategies");
        
        candidateParser.addArgument("-t")
                .type(Float.class)
                .setDefault(relevance_threshold)
                .help("number of ratings to be asigned into test set");

        candidateParser.addArgument("-n")
                .type(Float.class)
                .setDefault(nForOnePlusRandom)
                .help("number of non-relevant items to include in the ranking (in OnePlusRandom candidate items strategy");
        
        
        // metrics options
        ArgumentGroup metricsParser = parser.addArgumentGroup("metrics");
        metricsParser.addArgument("error")
                .choices(Arrays.toString(CommonErrorMetrics.METRICS.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .setDefault(errorMetrics)
                .metavar("error_metrics")
                .help("the error metrics to be computed");

        metricsParser.addArgument("--error_metrics")
                .action(new PrintMessageAction("Available error metrics :" + Arrays.toString(CommonErrorMetrics.METRICS.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available error metrics");
        
        metricsParser.addArgument("ranking")
                .choices(Arrays.toString(CommonRankingMetrics.METRICS.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .setDefault(rankingMetrics)
                .metavar("ranking_metrics")
                .help("the ranking metrics to be computed");

        metricsParser.addArgument("--ranking_metrics")
                .action(new PrintMessageAction("Available ranking metrics :" + Arrays.toString(CommonRankingMetrics.METRICS.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available ranking metrics");

        metricsParser.addArgument("default_prediction")
                .choices(Arrays.toString(NonPersonalizedPrediction.Type.values()).replaceAll("\\[", "").replaceAll("\\]", "").split(", "))
                .setDefault(non_personalized)
                .metavar("default_prediction")
                .help("the default prediction strategy to compute metrics");

        metricsParser.addArgument("--default_predictions")
                .action(new PrintMessageAction("Available default prediction strategies:" + Arrays.toString(NonPersonalizedPrediction.Type.values()).replaceAll("\\[", "").replaceAll("\\]", "")))
                .help("diplay available default prediction strategies");

        metricsParser.addArgument("--check_limits")
                .action(Arguments.storeTrue())
                .setDefault(false)
                .help("turn on the cheking of prediction value limits (avoids values below or above the minimum and maximum rating value, respectively)");

        // other options
        ArgumentGroup othersParser = parser.addArgumentGroup("others");
        othersParser.addArgument("--max_threads")
                .type(Integer.class)
                .setDefault(maxThreads)
                .help("the maximum number of threads to be created");
        
        othersParser.addArgument("--config_file")
                .setDefault(CONFIG_FILE)
                .help("the file name of basic configurations");
        
        othersParser.addArgument("--results_path")
                .setDefault(RESULTS_PATH)
                .help("the location (path) where result files will be stored");

        othersParser.addArgument("--prefix")
                .setDefault(EXPERIMENT_DESCRIPTION)
                .help("sets a prefix string to identify the experiment");

        othersParser.addArgument("--save_details")
                .action(Arguments.storeTrue())
                .setDefault(false)
                .help("turn on generation of files with detailed results");

        othersParser.addArgument("--save_data_files")
                .action(Arguments.storeTrue())
                .setDefault(false)
                .help("turn on generation of files with training and test set data");

        othersParser.addArgument("--log_level")
                .type(String.class)
                .setDefault(Level.SEVERE.toString())
                .choices(new String[]{Level.ALL.toString(), Level.CONFIG.toString(), Level.FINE.toString(), Level.FINER.toString(), Level.FINEST.toString(), Level.INFO.toString(), Level.OFF.toString(), Level.SEVERE.toString(), Level.WARNING.toString()})
                .help("sets the logging level");
        try{
            Namespace mainNS = parser.parseArgs(args);
            // dataset
            
            dataset_name = CommonDatasets.DATASET.valueOf((String)mainNS.get("dataset"));
            
            //recommender
            recommender_method = CommonRecommenders.METHOD.valueOf((String)mainNS.get("recommender"));
            neighbors = mainNS.get("k");
            minCommonRatingsForPrediction = mainNS.get("c");
            
            halfLifeDays=mainNS.get("d");
            factors = mainNS.get("f");
            iterations = mainNS.get("i");
            learnRate = mainNS.get("l");
            lambda = mainNS.get("r");

            //split method
            split_method = CommonDatasetSplitters.METHOD.valueOf((String)mainNS.get("eval_split"));
            testProportion = mainNS.get("p");
            testSize = mainNS.get("s");
            
            // candidate items
            candidates = CandidateItemsBuilder.CANDIDATE_ITEMS.valueOf((String)mainNS.get("candidate_items"));
            relevance_threshold = mainNS.get("t");
            nForOnePlusRandom = mainNS.get("n");
            
            // metrics
            errorMetrics = CommonErrorMetrics.METRICS.valueOf((String)mainNS.get("error"));
            rankingMetrics = CommonRankingMetrics.METRICS.valueOf((String)mainNS.get("ranking"));
            non_personalized = NonPersonalizedPrediction.Type.valueOf((String)mainNS.get("default_prediction"));
            controlPredictionValue = mainNS.get("check_limits");
            
            //others
            maxThreads = mainNS.get("max_threads");
            if (maxThreads > 1) useParallelProcessor = true;
            else useParallelProcessor = false;
            CONFIG_FILE = mainNS.get("config_file");
            RESULTS_PATH = mainNS.get("results_path");
            EXPERIMENT_DESCRIPTION = mainNS.get("prefix");
            SAVE_DETAILED_RESULTS = mainNS.get("save_details");
            genTrainingAndTestFiles = mainNS.get("save_data_files");
            level = Level.parse((String)mainNS.get("log_level"));
            
            /*
            else if (arg.startsWith("log_level")){
                String[] sm = arg.split("=");
                level = Level.parse(sm[1]);
            }

            
            else if (arg.startsWith("classification")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    SAVE_CLASSIFICATION_RESULTS = true;
                }
                else{
                    SAVE_CLASSIFICATION_RESULTS = false;
                }
            }
            else if (arg.startsWith("skip")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    skipIfResultsExist = true;
                }
                else{
                    skipIfResultsExist = false;
                }
            }
            
            
            
            /***************************************/
            System.out.println("dataset: "+dataset_name);
            System.out.println("recommender: "+recommender_method+", factors"+factors);
            System.out.println("split method: "+split_method);
        }
        catch (ArgumentParserException e){
            parser.handleError(e);
        }
        System.exit(0);
        
        // process parameters
        for (String arg:args){
            if (arg.startsWith("dataset")){
                String[] sm = arg.split("=");
                dataset_name = null;
                for (CommonDatasets.DATASET _dataset : CommonDatasets.DATASET.values()){
                    if (_dataset.name().equalsIgnoreCase(sm[1])){
                        dataset_name = _dataset;
                    }
                }                   
//                if (sm[1].equalsIgnoreCase("MovieLens100k")){
//                    dataset_name = TimestampedDatasets.DATASET.MovieLens100k;
//                }
//                else if (sm[1].equalsIgnoreCase("MovieLens1m")){
//                    dataset_name = TimestampedDatasets.DATASET.MovieLens1m;
//                }
//                else if (sm[1].equalsIgnoreCase("CAMRa2011t1")){
//                    dataset_name = TimestampedDatasets.DATASET.CAMRa2011t1;
//                }
//                else if (sm[1].equalsIgnoreCase("CAMRa2011t2")){
//                    dataset_name = TimestampedDatasets.DATASET.CAMRa2011t2;
//                }
//                else if (sm[1].equalsIgnoreCase("LastFM_Time")){
//                    dataset_name = TimestampedDatasets.DATASET.LastFM_Time;
//                }
//                else if (sm[1].equalsIgnoreCase("Netflix1")){
//                    dataset_name = TimestampedDatasets.DATASET.NetflixSubsample1;
//                }
//                else if (sm[1].equalsIgnoreCase("Netflix2")){
//                    dataset_name = TimestampedDatasets.DATASET.NetflixSubsample2;
//                }
//                else if (sm[1].equalsIgnoreCase("Netflix3")){
//                    dataset_name = TimestampedDatasets.DATASET.NetflixSubsample3;
//                }
//                else if (sm[1].equalsIgnoreCase("Netflix4")){
//                    dataset_name = TimestampedDatasets.DATASET.NetflixSubsample4;
//                }
//                else if (sm[1].equalsIgnoreCase("Netflix5")){
//                    dataset_name = TimestampedDatasets.DATASET.NetflixSubsample5;
//                }
//                else if (sm[1].equalsIgnoreCase("TV1")){
//                    dataset_name = TimestampedDatasets.DATASET.TV1;
//                }
//                else if (sm[1].equalsIgnoreCase("TV2")){
//                    dataset_name = TimestampedDatasets.DATASET.TV2;
//                }
//                else{
                if (dataset_name==null){
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }
            }
            else if (arg.startsWith("item_split")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    item_split = true;    
                }
                else if (sm[1].equalsIgnoreCase("false")){
                    item_split = true;                        
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
                
                if (smi.length == 3) {
                    minContextSizeForSplitting = Integer.parseInt(smi[2]);
                }
                if (smi.length > 3) {
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }

                if (smi[0].equalsIgnoreCase("mean")){
                    if (smi.length>=2){
                        double threshold = Double.parseDouble(smi[1]);
                        is_impurity = new MeanImpurity(threshold);
                    }
                    else{
                        is_impurity = new MeanImpurity();
                    }
                }
                else if (smi[0].equalsIgnoreCase("proportion")){
                    if (smi.length>=2){
                        double threshold = Double.parseDouble(smi[1]);
                        is_impurity = new ProportionImpurity(threshold);
                    }
                    else{
                        is_impurity = new ProportionImpurity();                        
                    }
                }
                else if (smi[0].equalsIgnoreCase("IG")){
                    if (smi.length>=2){
                        double threshold = Double.parseDouble(smi[1]);                    
                        is_impurity = new InformationGainImpurity(threshold);
                    }
                    else{
                        is_impurity = new InformationGainImpurity();
                    }
                }
                else if (smi[0].equalsIgnoreCase("fisher")){
                    if (smi.length>=2){
                        double threshold = Double.parseDouble(smi[1]);                    
                        is_impurity = new FisherExactImpurity(threshold);
                    }
                    else{
                        is_impurity = new FisherExactImpurity();
                    }
                }
                else if (smi[0].equalsIgnoreCase("chiSquared")){
                    if (smi.length>=2){
                        double threshold = Double.parseDouble(smi[1]);                    
                        is_impurity = new ChiSquaredImpurity(threshold);
                    }
                    else{
                        is_impurity = new ChiSquaredImpurity();
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
                
//                is_contexts = new ArrayList<TimeContextItemSplitter.TimeContext>();
//                String[] sm = arg.split("=");
//                for (String smi : sm[1].split(",")){
//                    for (TimeContextItemSplitter.TimeContext tc : TimeContextItemSplitter.TimeContext.values()){
//                        if (tc.name().equalsIgnoreCase(smi)){
//                            is_contexts.add(tc);
//                        }
//                    }
//                }
            }
            else if (arg.startsWith("filtering_context")){
                filtering_contexts = new ArrayList<String>();
                String[] sm = arg.split("=");
                filtering_contexts.addAll(Arrays.asList(sm[1].split(",")));
            }
            else if (arg.startsWith("recommender")){
                String[] sm = arg.split("=");
                String[] smi = sm[1].split(",");
                
                recommender_method = null;
                for (CommonRecommenders.METHOD _method : CommonRecommenders.METHOD.values()){
                    if (_method.name().equalsIgnoreCase(smi[0])){
                        recommender_method = _method;
                        if (_method.name().startsWith("kNN")){
                            if (smi.length == 2){
                                neighbors = Integer.parseInt(smi[1]);
                            }                       
                        }
                        if (_method.name().startsWith("Raw")){
                            if (smi.length == 2){
                                neighbors = Integer.parseInt(smi[1]);
                            }                       
                        }
                        if (_method.name().startsWith("TimeDecay")){
                            if (smi.length >= 2){
                                halfLifeDays = Integer.parseInt(smi[1]);
                            }
                            if (smi.length == 3){
                                neighbors = Integer.parseInt(smi[2]);
                            }                    
                        }
                        else if(_method.name().startsWith("MF")){
                            if (smi.length >= 2){
                                factors = Integer.parseInt(smi[1]);
                            }
                            if (smi.length >= 3){
                                iterations = Integer.parseInt(smi[2]);
                            }
                            if (smi.length >= 4){
                                learnRate = Double.parseDouble(smi[3]);
                            }
                            if (smi.length >= 5){
                                lambda = Double.parseDouble(smi[4]);
                            }                            
                        }
                    }
                }                   
                if (recommender_method==null){
                    System.err.println("unrecognized option: " + arg);
                    System.exit(1);
                }                
//                if (smi[0].equalsIgnoreCase("kNN_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("kNN_IB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_ItemBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("kNN_Cos_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_CosineSimilarity_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("kNN_Cos_IB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_CosineSimilarity_ItemBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("kNN_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_UB_Mahout;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("Raw_Cos_UB")){
//                    recommender_method = CommonRecommenders.METHOD.Raw_CosineSimilarity_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("Raw_Cos_IB")){
//                    recommender_method = CommonRecommenders.METHOD.Raw_CosineSimilarity_ItemBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("PRF_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_PRF_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("POF_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_POF_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("FPOF_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_FPOF_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("WPOF_UB")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_WPOF_UserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("PRF_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_PRF_MahoutUserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("POF_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_POF_MahoutUserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("FPOF_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_FPOF_MahoutUserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("WPOF_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_WPOF_MahoutUserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("PRF_UB_Mahout_Categorical")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_PRF_MahoutUserBased_Categorical;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("POF_UB_Mahout_Categorical")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_POF_MahoutUserBased_Categorical;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("CM_UB_Mahout")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_CM_MahoutUserBased;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("CM_UB_Mahout_Categorical")){
//                    recommender_method = CommonRecommenders.METHOD.kNN_CM_MahoutUserBased_Categorical;
//                    if (smi.length == 2){
//                        neighbors = Integer.parseInt(smi[1]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("IAvg")){
//                    recommender_method = CommonRecommenders.METHOD.ItemAvg;
//                }
//                else if (smi[0].equalsIgnoreCase("TimeDecay_UB")){
//                    recommender_method = CommonRecommenders.METHOD.TimeDecay_UserBased;
//                    if (smi.length >= 2){
//                        halfLifeDays = Integer.parseInt(smi[1]);
//                    }
//                    if (smi.length == 3){
//                        neighbors = Integer.parseInt(smi[2]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("MF")){
//                    recommender_method = CommonRecommenders.METHOD.MF_Mahout;
//                    if (smi.length >= 2){
//                        factors = Integer.parseInt(smi[1]);
//                    }
//                    if (smi.length >= 3){
//                        iterations = Integer.parseInt(smi[2]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("MF_Default")){
//                    recommender_method = CommonRecommenders.METHOD.MF_Default;
//                    if (smi.length >= 2){
//                        factors = Integer.parseInt(smi[1]);
//                    }
//                    if (smi.length >= 3){
//                        iterations = Integer.parseInt(smi[2]);
//                    }
//                    if (smi.length >= 4){
//                        learnRate = Double.parseDouble(smi[3]);
//                    }
//                    if (smi.length >= 5){
//                        lambda = Double.parseDouble(smi[4]);
//                    }
//                }
//                else if (smi[0].equalsIgnoreCase("MFOpt")){
//                    recommender_method = CommonRecommenders.METHOD.MF_Optimized;
//                }
//                else{
//                    System.err.println("unrecognized option: " + arg);
//                    System.exit(1);
//                }
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
                else if (smi[0].equalsIgnoreCase("predefined")){
                    split_method = CommonDatasetSplitters.METHOD.PredefinedTest;
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
                if (smi.length > 1){
                    relevance_threshold = Float.parseFloat(smi[1]);
                }
                if (smi.length > 2){
                    nForOnePlusRandom = Integer.parseInt(smi[2]);
                }
                if (smi.length > 3) {
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
            else if (arg.startsWith("classification")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    SAVE_CLASSIFICATION_RESULTS = true;
                }
                else{
                    SAVE_CLASSIFICATION_RESULTS = false;
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
            else if (arg.startsWith("results_path")){
                String[] sm = arg.split("=");
                RESULTS_PATH = sm[1];
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
                EXPERIMENT_DESCRIPTION = sm[1];
            }
            else if (arg.startsWith("skip")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("true")){
                    skipIfResultsExist = true;
                }
                else{
                    skipIfResultsExist = false;
                }
            }
            else if (arg.startsWith("log_level")){
                String[] sm = arg.split("=");
                level = Level.parse(sm[1]);
            }
            else if (arg.startsWith("config_file")){
                String[] sm = arg.split("=");
                CONFIG_FILE = sm[1];
            }
            else{
                System.err.println("unrecognized option: " + arg);
                System.exit(1);
            }
        }        
    }
    private static class PrintMessageAction implements ArgumentAction {

        private String message;

        public PrintMessageAction(String message) {
            this.message = message;
        }
    
        @Override
        public void run(ArgumentParser arg0, Argument arg1, Map<String, Object> arg2, String arg3, Object arg4) throws ArgumentParserException {
            System.out.println(message);
            System.exit(0);
        }

        @Override
        public void onAttach(Argument arg) {
        }

        @Override
        public boolean consumeArgument() {
            return false;
        }

    }
}
