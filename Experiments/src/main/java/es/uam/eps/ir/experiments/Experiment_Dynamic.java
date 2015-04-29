package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.timeDecay.DynamicSelectorBuilder;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.optimization.TrainValidationSetsGenerator;
import es.uam.eps.ir.rank.CandidateItemsBuilder;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.split.impl.Split;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
public class Experiment_Dynamic 
{
    // Dataset
    final static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.MovieLens1m;
    // Evaluation Methodology
    static CommonDatasetSplitters.METHOD   split_method        = CommonDatasetSplitters.METHOD.CommunityTimeOrderProportionHoldout;
    final static double testProportion = 0.2;  // 0.085 for Ml100k // 0.054 for Ml1m
    final static int testSize = 9;
    final static CandidateItemsBuilder.CANDIDATE_ITEMS candidates    = CandidateItemsBuilder.CANDIDATE_ITEMS.USER_TEST;
    final static float relevance_threshold=(float)0.0;
    static NonPersonalizedPrediction.Type non_personalized = NonPersonalizedPrediction.Type.ItemUserOverallMean;
    static boolean controlPredictionValue = true;
    // Evaluation Metrics
    final static CommonRankingMetrics.METRICS rankingMetrics = CommonRankingMetrics.METRICS.COMMON;
    final static CommonErrorMetrics.METRICS errorMetrics = CommonErrorMetrics.METRICS.COMMON;
    final static List<Integer> levels = Arrays.asList(5,10,20,50);
    
    // Recommendation method
    final static int neighbors = 200;

    // Parallel processor?
    final static boolean useParallelProcessor = false;
    static int maxThreads = 4;
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
    final static boolean SAVE_RESULTS = true;
    final static boolean SAVE_DETAILED_RESULTS = true;
    final static boolean SEND_EMAIL_REPORT=false;
    final static boolean SEND_RESULT_FILES=false;
    static String EXPERIMENT_PREFIX = "-defaultContextWeekend";
    final static String METRICS_FILE = "metrics.txt";
    final static String PERUSERMETRICS_FILE = "per_user_metrics.txt";
    final static String RELEVANTS_FILE = "relevants.txt.gz";
    final static String RECOMMENDATIONS_FILE = "recommendations.txt.gz";
    final static String RESULTS_SERVER_PATH = "/media/sdb6/results/experiments/";
    final static Level level = Level.INFO;
    
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
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        
        // process parameters
        for (String arg:args){
            if (arg.startsWith("split_method")){
                String[] sm = arg.split("=");
                if (sm[1].equalsIgnoreCase("RU")){
                    split_method = CommonDatasetSplitters.METHOD.UserRandomOrderProportionHoldout;
                }
                else if (sm[1].equalsIgnoreCase("TU")){
                    split_method = CommonDatasetSplitters.METHOD.UserTimeOrderProportionHoldout;
                }
                else if (sm[1].equalsIgnoreCase("Ti")){
                    split_method = CommonDatasetSplitters.METHOD.CommunityTimeOrderProportionHoldout;
                }
                else if (sm[1].equalsIgnoreCase("TuLe")){
                    split_method = CommonDatasetSplitters.METHOD.UserTimeOrderFixedHoldout;
                }
            }
            if (arg.startsWith("max_threads")){
                String[] sm = arg.split("=");
                maxThreads = Integer.parseInt(sm[1]);
            }
        }
        
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        final String newline = System.getProperty("line.separator");
        experimentInfo.append("Experiment:").append(dataset_name).append(", ").append("DynamicSelector(KNN-TimeDecay)").append(", ").append(split_method).append(", ").append(candidates).append(newline);

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
//        System.exit(0);
        
        // Training-test splitter
        logger.info("Splitting data");        
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter;
        splitter = new CommonDatasetSplitters<Object, Object, ContinuousTimeContextIF>().proportion(testProportion).size(testSize).dataset(dataset).getDatasetSplitter(split_method);
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
            // Only for validation tests
            if (useValidationData){
                TrainValidationSetsGenerator generator = new TrainValidationSetsGenerator(split.getTrainingSet(), new ContextualModelUtils(split.getTrainingSet()), splitter);
                SplitIF<Object, Object, ContinuousTimeContextIF> trainValidationSplit = new Split(generator.getTrainSet(), generator.getValidationSet());
                split = trainValidationSplit;
            }
            
            // Data filtering
            /////////////////
            SplitFilterer<Object, Object, ContinuousTimeContextIF> filterer;
            filterer = new SplitFilterer<Object, Object, ContinuousTimeContextIF>();
            split = filterer.processSplit(split, train_filters, test_filters);
            
            // Recommender initialization
            /////////////////////////////
            logger.info("Initializing recommender");
            RecommenderIF<Object, Object, ContinuousTimeContextIF> recommender;
            recommender = new DynamicSelectorBuilder(split.getTrainingSet()).neighbors(neighbors).buildRecommender(null);
            final StringBuilder recommenderInfo = new StringBuilder();
            recommenderInfo.append("Recommender\t").append(recommender).append(newline);
            experimentInfo.append(recommenderInfo);
            System.out.print(recommenderInfo);
            checkMem();
            
            // Processing split
            ///////////////////
            SplitRecommendationsProcessor<Object, Object, ContinuousTimeContextIF> processer;
            if (useParallelProcessor){
                processer = new SplitRecommendationsProcessor_Parallel<Object, Object, ContinuousTimeContextIF>(maxThreads);
            }
            else {
                processer = new SplitRecommendationsProcessor<Object, Object, ContinuousTimeContextIF>();
            }
            
            processer.processSplit(recommender, split, candidateItemsBuilder, non_personalized, levels, rankingMetrics, errorMetrics, SAVE_DETAILED_RESULTS, SEND_EMAIL_REPORT, controlPredictionValue);
            

            // Post processing
            //////////////////
            if (maxMem < processer.getMaxMemUsage()){
                maxMem = processer.getMaxMemUsage();
            }
            
            experimentInfo.append(filterer.getFiltersDetails());
            experimentInfo.append(processer.getExperimentDetails());
            String datasetString  = dataset.toString();
            if (useValidationData){
                datasetString = datasetString + "_validation";
            }
            EXPERIMENT_PREFIX = datasetString + EXPERIMENT_PREFIX + "__"  + "DynamicSelector(KNN-TimeDecay)" + "__" + splitter + candidates + "_threshold=" + relevance_threshold + "__" + filterer.getFilters()  + "__";
            
            // Files init
            if (SAVE_RESULTS){
                logger.info("Deleting previous results files");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+METRICS_FILE);
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+PERUSERMETRICS_FILE);
                logger.info("generating new results files");
                PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+PERUSERMETRICS_FILE, processer.getPerUserMetrics());
                if (SEND_RESULT_FILES){
                    sendFile(dataset, PERUSERMETRICS_FILE);
                }
            }
            if (SAVE_DETAILED_RESULTS){
                logger.info("Deleting previous detailed results files");
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+RELEVANTS_FILE);
                PrintUtils.cleanFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE);
                logger.info("generating new detailed results files");
                PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+RELEVANTS_FILE, processer.getRelevantsDetails());
//                PrintUtils.toGz(dataset.getResultsPath()+EXPERIMENT_PREFIX+RECOMMENDATIONS_FILE, processer.getRecommendationsDetails());
                List<String> recommendationLists = processer.getRecommendationsDetails();
                int list = 0;
                for (String recommendationList : recommendationLists) {
                    PrintUtils.toGz(dataset.getResultsPath() + EXPERIMENT_PREFIX + "list" + list + RECOMMENDATIONS_FILE, recommendationList);
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
        execInfo.append("time\t").append(String.format(Locale.US,"%,d",totalTimeSeconds)).append("[s]").append(newline);
        execInfo.append("maxMem\t").append(maxMemGB).append("[GB]").append(newline);
        experimentInfo.append(execInfo);
        System.out.print(execInfo);
        
        if (SAVE_RESULTS){
            PrintUtils.toFile(dataset.getResultsPath()+EXPERIMENT_PREFIX+METRICS_FILE, experimentInfo.toString());
            if (SEND_RESULT_FILES){
                sendFile(dataset, METRICS_FILE);
            }
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
    }
    
    private static void checkMem(){
        long mem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        if (mem>maxMem){
            maxMem=mem;
        }        
    }
    
    private static void sendFile(DatasetIF dataset, String file){
        String origin=dataset.getResultsPath()+EXPERIMENT_PREFIX+file;
        String dest=RESULTS_SERVER_PATH+EXPERIMENT_PREFIX+file;
        logger.log(Level.INFO,"Storing {0}" + " in " + RESULTS_SERVER_PATH, origin);

        PrintUtils.ftpTransfer(origin, dest);        
    }
}
