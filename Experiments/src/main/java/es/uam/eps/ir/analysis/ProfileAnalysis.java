package es.uam.eps.ir.analysis;

import es.uam.eps.ir.cars.contextualfiltering.ContextFilteringBasedRecommenderBuilder;
import es.uam.eps.ir.core.model.impl.DefaultAggregationFunction;
import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.model.WeightedExplicitPreference;
import es.uam.eps.ir.cars.model.WeightedPreferenceIF;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.cars.neighborhood.PearsonWeightedSimilarity;
import es.uam.eps.ir.cars.neighborhood.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.cars.recommender.RecommenderBuilderIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.ItemDataIF;
import es.uam.eps.ir.dataset.MovieLens1m.Movielens1mMovieData;
import es.uam.eps.ir.experiments.CommonDatasetSplitters;
import es.uam.eps.ir.experiments.CommonRecommenders;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.timeanalysis.ComparablePair;
import es.uam.eps.ir.timeanalysis.TimeUtils;
import es.uam.eps.ir.timesequence.FeaturedTimeSequence;
import es.uam.eps.ir.timesequence.PredictionRatingsTimeSequence;
import es.uam.eps.ir.timesequence.TimeSequence;
import es.uam.eps.ir.timesequence.TimeSequenceIF;
import es.uam.eps.ir.timesequence.feature.AverageFeatureVector;
import es.uam.eps.ir.timesequence.feature.FeatureVector;
import es.uam.eps.ir.timesequence.feature.ItemPopularity;
import es.uam.eps.ir.timesequence.feature.ItemRating;
import es.uam.eps.ir.timesequence.feature.MeanPopularity;
import es.uam.eps.ir.timesequence.feature.MeanSimilarity;
import es.uam.eps.ir.timesequence.feature.MeanSimilarityTimesTestDistance;
import es.uam.eps.ir.timesequence.feature.MeanSimilarity_Quartile;
import es.uam.eps.ir.timesequence.feature.RatingMean;
import es.uam.eps.ir.timesequence.feature.RatingNumber;
import es.uam.eps.ir.timesequence.feature.RatingSD;
import es.uam.eps.ir.timesequence.feature.SimilarityTimesTestDistance_Quartile;
import es.uam.eps.ir.timesequence.feature.TestDistanceToFirst;
import es.uam.eps.ir.timesequence.feature.TestDistanceToLast;
import es.uam.eps.ir.timesequence.feature.TestDistance_Mean;
import es.uam.eps.ir.timesequence.feature.TestDistance_Quartile;
import es.uam.eps.ir.timesequence.feature.TimeSequenceFeature;
import es.uam.eps.ir.timesequence.feature.Timespan;
import es.uam.eps.ir.timesequence.feature.TimestampKurtosis;
import es.uam.eps.ir.timesequence.feature.TimestampMean;
import es.uam.eps.ir.timesequence.feature.TimestampSD;
import es.uam.eps.ir.timesequence.feature.TimestampSkewness;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;
import utils.SummaryPrinter;

/**
 *
 * @author pedro
 */
public class ProfileAnalysis {

    public enum TimestampType{
        DAY,
        SECOND
    };
    
    public enum MovieAge{
        AFTER_2000,
        DECADE_1990,
        DECADE_1980,
        DECADE_1960_1970,
        BEFORE_1960,
        ALL
    };
    
    static double moviePopularity = 1.0;
    static boolean mostPopulars = true;
    
    static MovieAge movieAge = MovieAge.ALL;
    
    final static Map<String, Double> protocolProportionMap = new HashMap<String, Double>();
    final static Map<String, String> protocolNameMap = new HashMap<String, String>();
    static Map<String, RecommenderIF> algorithmNameMap = new HashMap<String, RecommenderIF>();

    // Dataset
    final static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.MovieLens1m;
    static ItemDataIF movieData;
    
    // Evaluation Methodology
    static CommonDatasetSplitters.METHOD   split_method        = CommonDatasetSplitters.METHOD.CommunityTimeOrderTimeProportionHoldout;
    final static double testProportion = 0.1;
    final static int testSize = 9;
    
    final static CommonRecommenders.METHOD base_method  = CommonRecommenders.METHOD.kNN_PearsonWeightedSimilarity_UserBased;
    final static int neighbors = 200;        
    final static int minNeighbors = 3;
    
    final static CommonRecommenders.METHOD comp_method  = CommonRecommenders.METHOD.kNN_PRF_UserBased;
    // Time Decay parameters
    final static double halfLifeProportion = 0.25;
    static int halfLifeDays = 0;
    

    final static int nUsers = 20;

    static String basePath = "/datos/experiments/ProfileAnalysis/";
    
    // Logger
    final static Logger logger = Logger.getLogger("ExperimentLog");
    final static Level logLevel = Level.INFO;

    final static String newline = System.getProperty("line.separator");
    
    // default values
    static List<String> protocols = Arrays.asList("protocol1");
    static List<String> algorithms = Arrays.asList("algorithm4");
    static String resultsFileString = "results3";
    static String protocol;
    
    static String path = basePath;

    @SuppressWarnings("CallToThreadDumpStack")
    public static void main( String[] args )
    {
        protocol = protocols.get(0);
        processArgs(args);
        // Logger init
        //////////////
        logger.setUseParentHandlers(false);
        logger.setLevel(logLevel);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.info("Starting profile analysis");
        // Protocol init
        protocolProportionMap.put("protocol1", 0.4);
        protocolProportionMap.put("protocol2", 0.3);
        protocolProportionMap.put("protocol3", 0.2);
        protocolProportionMap.put("protocol4", 0.1);
        protocolProportionMap.put("protocol5", 0.08);
        protocolProportionMap.put("protocol6", 0.06);
        protocolProportionMap.put("protocol7", 0.04);
        protocolProportionMap.put("protocol8", 0.02);
        protocolProportionMap.put("protocol10", 0.2);

        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        movieData = new Movielens1mMovieData(dataset);
        TimeUtils.STATS[] stats = TimeUtils.STATS.values();

        protocolNameMap.put("protocol1", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.4)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol2", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.3)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol3", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.2)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol4", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.1)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol5", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.08)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol6", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.06)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol7", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.04)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol8", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.02)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolNameMap.put("protocol10", "Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        path += dataset + "/" + protocol + "/";

        
        // split
        SplitIF split = null;
        ModelIF trainSet = null;
        ContextualModelUtils eTrain = null;
        ModelIF testSet = null;
        ContextualModelUtils eTest = null;
        RecommenderIF<Object, Object, ContinuousTimeContextIF> baseRecommender = null;
        RecommenderIF<Object, Object, ContinuousTimeContextIF> compRecommender = null;        
        List selectedUsers;
        List selectedItems;
        
        TimestampType type = TimestampType.DAY;
        int step = 3;
        

        PrintStream ps = null;
        switch (step){

            case 1: // user ratings sequences and features
                // protocol
                split = getSplit(dataset, protocol);
                algorithmNameMap = initAlgorithms(split, neighbors);
                trainSet = split.getTrainingSet();
                eTrain = new ContextualModelUtils(trainSet);
                testSet = split.getTestingSet();
                eTest = new ContextualModelUtils(testSet);        
                // recommenders
                baseRecommender = new CommonRecommenders().neighbors(neighbors).getRecommender(base_method, split.getTrainingSet());
                compRecommender = algorithmNameMap.get(algorithms.get(0));
                // users & items
                selectedUsers = selectUsers(testSet, eTrain);
                selectedItems = selectItems(testSet, eTrain, moviePopularity, mostPopulars, movieAge, movieData);
        
                try{
                    String dataFile = "userRatings";
                    Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = genUserRatingSequenceMap(selectedUsers, trainSet, eTrain, testSet, eTest);
//                    timeSequenceMapToFile(ratingsSequenceMap, dataFile + "Sequence_all", type);
                    
                    List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                            new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
                    features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new MeanPopularity(TimeSequenceFeature.TIME.TRAINING, eTrain));
                    features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));
                    
                    Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                            = getFeaturedTimeSequences(ratingsSequenceMap, features);
//                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);

//                    ps = new PrintStream(new File(path + "stats/results.tsv"));

                    
                    // Select users according to relative performance
                    Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
                    Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
                    List<Integer> levels = Arrays.asList(10,20,50);
                    for (int level: levels){
                        List<Object> topUsersPos = getTopUsersInPerformance(userPerformanceMap, level, true);
                        FeatureVector<Double> avgPos = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + level + "UsersPos", avgPos);

                        List<Object> topUsersNeg = getTopUsersInPerformance(userPerformanceMap, level, false);
                        FeatureVector<Double> avgNeg = getAverageFeatures(topUsersNeg, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + level + "UsersNeg", avgNeg);
                    }

                    List<String> categories = new ArrayList(avgs.keySet());
                    Collections.sort(categories);

                    String popCat;
                    if (mostPopulars){
                        popCat="mostPops";
                    }
                    else{
                        popCat="leastPops";                            
                    }
                    String postFix = "_" + algorithms.get(0) + "_" + moviePopularity + popCat + "_age" + movieAge;
                    saveSelectedSequencesFeaturesAndPerformance(new ArrayList(featuredTimeSequenceMap.keySet()), featuredTimeSequenceMap, userPerformanceMap, dataFile + postFix, type);
 
                    for (String category: categories){
                        for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                            String result = compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\t" + value;
                            System.out.println(result);
//                            ps.println(result);
                        }
                    }
                        

                    
//                    saveAverageFeaturesIn(topUsers, featuredTimeSequenceMap, userPerformanceMap, dataFile + "Averages_top2", type);
                    
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
                
            case 2: // all neighbors' ratings sequence
                // protocol
                split = getSplit(dataset, protocol);
                algorithmNameMap = initAlgorithms(split, neighbors);
                trainSet = split.getTrainingSet();
                eTrain = new ContextualModelUtils(trainSet);
                testSet = split.getTestingSet();
                eTest = new ContextualModelUtils(testSet);        
                // recommenders
                baseRecommender = new CommonRecommenders().neighbors(neighbors).getRecommender(base_method, split.getTrainingSet());
                compRecommender = algorithmNameMap.get(algorithms.get(0));
                // users & items
                selectedUsers = selectUsers(testSet, eTrain);
                selectedItems = selectItems(testSet, eTrain, moviePopularity, mostPopulars, movieAge, movieData);
                try{
                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);

                    String dataFile = "neighborsRatingsSequence";
                    Map<Object, TimeSequenceIF<Object, PreferenceIF<Object, Object, ContinuousTimeContext>>> ratingsSequenceMap = genNeighborsRatingsSequences(userNeighborsMap, trainSet, eTrain, testSet, eTest);
//                    timeSequenceMapToFile(ratingsSequenceMap,  dataFile, type);
                    
                    List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                            new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
                    features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new MeanPopularity(TimeSequenceFeature.TIME.TRAINING, eTrain));
                    features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new MeanSimilarity(TimeSequenceFeature.TIME.TRAINING));
                    for (int i = 1; i <= 4; i++){
                        features.add(new MeanSimilarity_Quartile(i));
                    }
                    
                    Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                            = getFeaturedTimeSequences(ratingsSequenceMap, features);
//                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);

                    // Select users according to relative performance
                    Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
                    Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
                    List<Double> levels = Arrays.asList(0.1, 0.2, 0.5, 1.0);
                    for (double level: levels){
                        List<Object> topUsersPos = getTopUsersInPerformance(userPerformanceMap, level, true);
                        FeatureVector<Double> avgPos = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + (level*100) + "NeighborsPos", avgPos);
                        
                        List<Object> topUsersNeg = getTopUsersInPerformance(userPerformanceMap, level, false);
                        FeatureVector<Double> avgNeg = getAverageFeatures(topUsersNeg, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + (level*100) + "NeighborsNeg", avgNeg);
                    }
                    
                    List<String> categories = new ArrayList(avgs.keySet());
                    Collections.sort(categories);
                    
                    String popCat;
                    if (mostPopulars){
                        popCat="mostPops";
                    }
                    else{
                        popCat="leastPops";                            
                    }
                    String postFix = "_" + algorithms.get(0) + "_" + moviePopularity + popCat + "_age" + movieAge;
                    saveSelectedSequencesFeaturesAndPerformance(new ArrayList(featuredTimeSequenceMap.keySet()), featuredTimeSequenceMap, userPerformanceMap, dataFile + postFix, type);
                    
                    for (String category: categories){
                        for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                            System.out.println(compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\t" + value);
                        }
                    }
                    
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 3: // neighbors' used ratings sequence
                // protocol
                split = getSplit(dataset, protocol);
                algorithmNameMap = initAlgorithms(split, neighbors);
                trainSet = split.getTrainingSet();
                eTrain = new ContextualModelUtils(trainSet);
                testSet = split.getTestingSet();
                eTest = new ContextualModelUtils(testSet);        
                // recommenders
                baseRecommender = new CommonRecommenders().neighbors(neighbors).getRecommender(base_method, split.getTrainingSet());
                compRecommender = algorithmNameMap.get(algorithms.get(0));
                // users & items
                selectedUsers = selectUsers(testSet, eTrain);
                selectedItems = selectItems(testSet, eTrain, moviePopularity, mostPopulars, movieAge, movieData);
                try{
                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);
                    Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = genUsedRatingsInPrediction(userNeighborsMap, selectedUsers, selectedItems, minNeighbors, trainSet, eTrain, testSet);

                    String dataFile = "testRatings";
                    Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = genNeighborsUsedRatingsSequences(useritemNeighborsusedMap, trainSet, eTrain, testSet, eTest);
//                    timeSequenceMapToFile(ratingsSequenceMap, dataFile + "Sequence_all", type);

                    List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                            new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
                    features.add(new ItemRating(TimeSequenceFeature.TIME.TRAINING, testSet));
                    features.add(new ItemPopularity(TimeSequenceFeature.TIME.TRAINING, eTrain));
                    features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));
                    features.add(new TestDistanceToFirst());
                    features.add(new TestDistanceToLast());
                    features.add(new TestDistance_Mean());
                    for (int i = 1; i <= 4; i++){
                        features.add(new TestDistance_Quartile(i));
                    }
                    features.add(new MeanSimilarity(TimeSequenceFeature.TIME.TRAINING));
                    for (int i = 1; i <= 4; i++){
                        features.add(new MeanSimilarity_Quartile(i));
                    }
                    
                    Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                            = getFeaturedTimeSequences(ratingsSequenceMap, features);
                    
//                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);
                    
                    // Select users according to relative performance
                    Map<ComparablePair<Object,Object>,Double> ratingPerformanceMap = getRatingPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
                    Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
                    List<Integer> levels = Arrays.asList(10, 20, 50);
                    System.out.println("levels");
                    for (double level: levels){
                        level /= 100.0;
                        List<? extends Object> topRatingsPos = getTopRatingsInPerformance(ratingPerformanceMap, level, true);
                        FeatureVector<Double> avgPos = getAverageFeatures(topRatingsPos, featuredTimeSequenceMap, ratingPerformanceMap);
                        avgs.put("top" + level + "RatingsPos", avgPos);
                        
                        List<? extends Object> topRatingsNeg = getTopRatingsInPerformance(ratingPerformanceMap, level, false);
                        FeatureVector<Double> avgNeg = getAverageFeatures(topRatingsNeg, featuredTimeSequenceMap, ratingPerformanceMap);
                        avgs.put("top" + level + "RatingsNeg", avgNeg);
                    }
                    
                    List<String> categories = new ArrayList(avgs.keySet());
                    Collections.sort(categories);
                    
                    String popCat;
                    if (mostPopulars){
                        popCat="mostPops";
                    }
                    else{
                        popCat="leastPops";                            
                    }
                    String postFix = "_" + algorithms.get(0) + "_" + moviePopularity + popCat + "_age" + movieAge;
                    saveSelectedSequencesFeaturesAndPerformance(new ArrayList(featuredTimeSequenceMap.keySet()), featuredTimeSequenceMap, ratingPerformanceMap, dataFile + postFix, type);

                    for (String category: categories){
                        for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                            System.out.println(compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\t" + value);
                        }
                    }
                    
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;                
                
            case 10: // obtain metrics for all combinations. Allows command-line parameters input
                
                for (String protocol:protocols){
                    path += basePath + "/" + dataset + "/" + protocol + "/";
                    logger.log(Level.INFO, "Starting analysis with {0}", protocol);
                    split = getSplit(dataset, protocol);
                    Map<String, RecommenderIF> algorithmsMap = initAlgorithms(split, neighbors);
                    // recommenders
                    baseRecommender = new CommonRecommenders().neighbors(neighbors).getRecommender(base_method, split.getTrainingSet());

                    for (String algorithm: algorithms){
                        compRecommender = algorithmsMap.get(algorithm);
                        try{
                            ps = new PrintStream(new File( basePath + "/" + dataset + "/" + protocol + "/" + resultsFileString + "_" + algorithm + ".tsv"));
                        }
                        catch (Exception e){
                            System.err.println(e);
                            e.printStackTrace();
                            System.exit(1);
                        }

                        List<Double> levels = Arrays.asList(0.1,0.2,0.5,1.0); // top x% users or ratings in metric (RMSE)
                        List<Double> popularities = Arrays.asList(0.1,0.2,0.5);

                        RecommenderIF compRecomm = algorithmsMap.get(algorithm);
                        for (MovieAge age: MovieAge.values()){
                            // each popularity
                            for (double popularity: popularities){
                                computeUserStats(ps, protocol, split, baseRecommender, compRecomm, popularity, true, age, movieData, levels, type);
                                computeUserStats(ps, protocol, split, baseRecommender, compRecomm, popularity, false, age, movieData, levels, type);
                                computeNeighborsStats(ps, protocol, split, baseRecommender, compRecomm, popularity, true, age, movieData, levels, type);
                                computeNeighborsStats(ps, protocol, split, baseRecommender, compRecomm, popularity, false, age, movieData, levels, type);
                                computeUsedRatingsStats(ps, protocol, split, baseRecommender, compRecomm, popularity, true, age, movieData, levels, type);
                                computeUsedRatingsStats(ps, protocol, split, baseRecommender, compRecomm, popularity, false, age, movieData, levels, type);
                            }
                            // full data (regardless of item popularity)
                            computeUserStats(ps, protocol, split, baseRecommender, compRecommender, 1.0, true, age, movieData, levels, type);
                            computeNeighborsStats(ps, protocol, split, baseRecommender, compRecommender, 1.0, true, age, movieData, levels, type);
                            computeUsedRatingsStats(ps, protocol, split, baseRecommender, compRecommender, 1.0, true, age, movieData, levels, type);
                        }
                    }
                }
                break;
        }
        

        logger.info("done");
    }

    public static void computeUsedRatingsStats(PrintStream ps, String protocol, SplitIF split, RecommenderIF baseRecommender, RecommenderIF compRecommender, double popularity, boolean mostPopulars, MovieAge movieAge, ItemDataIF movieData, List<Double> levels, TimestampType type){
        // Initializations
        ModelIF trainSet = split.getTrainingSet();
        ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
        ModelIF testSet = split.getTestingSet();
        ContextualModelUtils eTest = new ContextualModelUtils(testSet);

        List selectedUsers = selectUsers(testSet, eTrain);
        List selectedItems = selectItems(testSet, eTrain, popularity, mostPopulars, movieAge, movieData);
        
        try{
            Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);
            Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = genUsedRatingsInPrediction(userNeighborsMap, selectedUsers, selectedItems, minNeighbors, trainSet, eTrain, testSet);

            String dataFile = "testRatings";
            Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = genNeighborsUsedRatingsSequences(useritemNeighborsusedMap, trainSet, eTrain, testSet, eTest);
//                    timeSequenceMapToFile(ratingsSequenceMap, dataFile + "Sequence_all", type);

            List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                    new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
            features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TestDistanceToFirst());
            features.add(new TestDistanceToLast());
            features.add(new TestDistance_Mean());
            for (int i = 1; i <= 4; i++){
                features.add(new TestDistance_Quartile(i));
            }
            features.add(new MeanSimilarity(TimeSequenceFeature.TIME.TRAINING));
            for (int i = 1; i <= 4; i++){
                features.add(new MeanSimilarity_Quartile(i));
            }

            features.add(new MeanSimilarityTimesTestDistance(TimeSequenceFeature.TIME.TRAINING));
            for (int i = 1; i <= 4; i++){
                features.add(new SimilarityTimesTestDistance_Quartile(i));
            }

            Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                    = getFeaturedTimeSequences(ratingsSequenceMap, features);


            // Select users according to relative performance
            Map<ComparablePair<Object,Object>,Double> ratingPerformanceMap = getRatingPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
            Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
            for (double level: levels){
                List<? extends Object> topRatingsPos = getTopRatingsInPerformance(ratingPerformanceMap, level, true);
                FeatureVector<Double> avgPos = getAverageFeatures(topRatingsPos, featuredTimeSequenceMap, ratingPerformanceMap);
                avgs.put("top" + (level*100) + "%RatingsPos", avgPos);

                List<? extends Object> topRatingsNeg = getTopRatingsInPerformance(ratingPerformanceMap, level, false);
                FeatureVector<Double> avgNeg = getAverageFeatures(topRatingsNeg, featuredTimeSequenceMap, ratingPerformanceMap);
                avgs.put("top" + (level*100) + "%RatingsNeg", avgNeg);
            }

            List<String> categories = new ArrayList(avgs.keySet());
            Collections.sort(categories);

            String popCat;
            if (mostPopulars){
                popCat="mostPops";
            }
            else{
                popCat="leastPops";                            
            }

            for (String category: categories){
                for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                    String result =  (popularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tratings " + value;
//                    System.out.println(compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tratings " + value);
                    ps.println(result);
                }
            }

        }
        catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void computeNeighborsStats(PrintStream ps, String protocol, SplitIF split, RecommenderIF baseRecommender, RecommenderIF compRecommender, double popularity, boolean mostPopulars, MovieAge movieAge, ItemDataIF movieData, List<Double> levels, TimestampType type){
        // Initializations
        ModelIF trainSet = split.getTrainingSet();
        ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
        ModelIF testSet = split.getTestingSet();
        ContextualModelUtils eTest = new ContextualModelUtils(testSet);

        List selectedUsers = selectUsers(testSet, eTrain);
        List selectedItems = selectItems(testSet, eTrain, popularity, mostPopulars, movieAge, movieData);
        
        try{
            Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);

            String dataFile = "neighborsRatingsSequence";
            Map<Object, TimeSequenceIF<Object, PreferenceIF<Object, Object, ContinuousTimeContext>>> ratingsSequenceMap = genNeighborsRatingsSequences(userNeighborsMap, trainSet, eTrain, testSet, eTest);
//                    timeSequenceMapToFile(ratingsSequenceMap,  dataFile, type);

            List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                    new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
            features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));
            features.add(new MeanSimilarity(TimeSequenceFeature.TIME.TRAINING));
            for (int i = 1; i <= 4; i++){
                features.add(new MeanSimilarity_Quartile(i));
            }

            Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                    = getFeaturedTimeSequences(ratingsSequenceMap, features);
//            featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);

            // Select users according to relative performance
            Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
            Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
            for (double level: levels){
                List<Object> topUsersPos = getTopUsersInPerformance(userPerformanceMap, level, true);
                FeatureVector<Double> avgPos = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                avgs.put("top" + (level*100) + "%NeighborsPos", avgPos);

                List<Object> topUsersNeg = getTopUsersInPerformance(userPerformanceMap, level, false);
                FeatureVector<Double> avgNeg = getAverageFeatures(topUsersNeg, featuredTimeSequenceMap, userPerformanceMap);
                avgs.put("top" + (level*100) + "%NeighborsNeg", avgNeg);
            }

            List<String> categories = new ArrayList(avgs.keySet());
            Collections.sort(categories);

            String popCat;
            if (mostPopulars){
                popCat="mostPops";
            }
            else{
                popCat="leastPops";                            
            }
            for (String category: categories){
                for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                    String result =  (popularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tneighbors " + value;
//                    String result =  protocolNameMap.get(protocol) + "\t" + compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tneighbors " + value;
//                    System.out.println(result)1;
                    ps.println(result);
                }
            }

        }
        catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
    public static void computeUserStats(PrintStream ps, String protocol, SplitIF split, RecommenderIF baseRecommender, RecommenderIF compRecommender, double popularity, boolean mostPopulars, MovieAge movieAge, ItemDataIF movieData, List<Double> levels, TimestampType type){
        // Initializations
        ModelIF trainSet = split.getTrainingSet();
        ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
        ModelIF testSet = split.getTestingSet();
        ContextualModelUtils eTest = new ContextualModelUtils(testSet);

        List selectedUsers = selectUsers(testSet, eTrain);
        List selectedItems = selectItems(testSet, eTrain, popularity, mostPopulars, movieAge, movieData);
        
        try{
            String dataFile = "userRatings";
            Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = genUserRatingSequenceMap(selectedUsers, trainSet, eTrain, testSet, eTest);
//            timeSequenceMapToFile(ratingsSequenceMap, dataFile + "Sequence_all", type);

            List<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features =
                    new ArrayList<TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double>>();
            features.add(new RatingNumber(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new RatingSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new Timespan(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampMean(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSD(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampSkewness(TimeSequenceFeature.TIME.TRAINING));
            features.add(new TimestampKurtosis(TimeSequenceFeature.TIME.TRAINING));

            Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                    = getFeaturedTimeSequences(ratingsSequenceMap, features);
//            featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);

            // Select users according to relative performance
            Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, selectedItems, baseRecommender, compRecommender, testSet);
            Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
            for (double level: levels){
                List<Object> topUsersPos = getTopUsersInPerformance(userPerformanceMap, level, true);
                FeatureVector<Double> avgPos = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                avgs.put("top" + (level*100) + "%UsersPos", avgPos);

                List<Object> topUsersNeg = getTopUsersInPerformance(userPerformanceMap, level, false);
                FeatureVector<Double> avgNeg = getAverageFeatures(topUsersNeg, featuredTimeSequenceMap, userPerformanceMap);
                avgs.put("top" + (level*100) + "%UsersNeg", avgNeg);
            }

            List<String> categories = new ArrayList(avgs.keySet());
            Collections.sort(categories);

            String popCat;
            if (mostPopulars){
                popCat="mostPops";
            }
            else{
                popCat="leastPops";                            
            }
            for (String category: categories){
                for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                    String result =  (popularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tusers " + value;
//                    String result =  protocolNameMap.get(protocol) + "\t" + compRecommender + "\t" + (moviePopularity*100) + "% " + popCat + "\t" + movieAge + "\t" + category + "\tusers " + value;
//                    System.out.println(result)1;
                    ps.println(result);
                }
            }

        }
        catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
    public static List<ComparablePair<Object,Object>> getTopRatingsInPerformance(Map<ComparablePair<Object,Object>,Double> ratingPerformanceMap, int n, boolean reverse) throws IOException{

        List topRatings = new ArrayList();
        List<ComparablePair<ComparablePair<Object,Object>,Double>> list = new ArrayList<ComparablePair<ComparablePair<Object,Object>,Double>>();
        List<ComparablePair<Object,Object>> ratings = new ArrayList(ratingPerformanceMap.keySet());
        for (ComparablePair<Object,Object> rating: ratings){
            Double performance = ratingPerformanceMap.get(rating);
            if (Double.isNaN(performance)) { continue; }
            ComparablePair<ComparablePair<Object,Object>,Double> pair = new ComparablePair<ComparablePair<Object,Object>,Double>(rating, performance);
            list.add(pair);
        }

        Collections.sort(list);

        if (reverse){
            Collections.reverse(list);
        }

        for (int i = 0; i < n; i++){
            topRatings.add(list.get(i).getKey());
        }

        return topRatings;
    }

    @SuppressWarnings("empty-statement")
    public static List<ComparablePair<Object,Object>> getTopRatingsInPerformance(Map<ComparablePair<Object,Object>,Double> ratingPerformanceMap, double proportion, boolean reverse) throws IOException{

        List topRatings = new ArrayList();
        List<ComparablePair<ComparablePair<Object,Object>,Double>> list = new ArrayList<ComparablePair<ComparablePair<Object,Object>,Double>>();
        List<ComparablePair<Object,Object>> ratings = new ArrayList(ratingPerformanceMap.keySet());
        for (ComparablePair<Object,Object> rating: ratings){
            Double performance = ratingPerformanceMap.get(rating);
            if (Double.isNaN(performance)) { continue; }
            ComparablePair<ComparablePair<Object,Object>,Double> pair = new ComparablePair<ComparablePair<Object,Object>,Double>(rating, performance);
            list.add(pair);
        }

        if (list.isEmpty()) { return topRatings; }

        Collections.sort(list);

        if (reverse){
            Collections.reverse(list);
        }
        
        int nSign = 0;
        double sign = reverse ? Math.signum(1.0) : Math.signum(-1.0);
        for (nSign = 1; nSign < list.size() && Math.signum(list.get(nSign).getValue()) == sign;nSign++);
        
        int n = (int)(nSign * proportion);

        for (int i = 0; i < n; i++){
            topRatings.add(list.get(i).getKey());
        }

        return topRatings;
    }

    private static Map<ComparablePair<Object,Object>, Double> getRatingPerformanceMap(List users, List items, RecommenderIF baseRecommender, RecommenderIF compRecommender, ModelIF testSet) throws IOException{
        logger.info("Computing rating recommendation performance data");
        Map<ComparablePair<Object,Object>, Double> ratingPerformanceMap = new HashMap<ComparablePair<Object,Object>, Double>();
        
        for (Object user: users){
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getUniquePreferencesFromUser(user, new DefaultAggregationFunction());
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                int pos = Collections.binarySearch(items, pref.getItem());
                if (pos < 0) { continue; }
                Object item = pref.getItem();
                float realValue = pref.getValue();
                float basePrediction = baseRecommender.predict(user, item, pref.getContext());
                float compPrediction = compRecommender.predict(user, item, pref.getContext());
                if (Float.isNaN(basePrediction) || Float.isNaN(compPrediction)){ continue; }
                double baseRMSE = Math.sqrt(Math.pow(basePrediction - realValue, 2.0));
                double compRMSE = Math.sqrt(Math.pow(compPrediction - realValue, 2.0));
                ratingPerformanceMap.put(new ComparablePair(user,item), (double)(baseRMSE - compRMSE));
            }
        }

        return ratingPerformanceMap;
    }    

    public static List<Object> getTopUsersInPerformance(Map<Object,Double> userPerformanceMap, double proportion, boolean reverse) throws IOException{
        List topUsers = new ArrayList();
        List<ComparablePair<Object,Double>> list = new ArrayList<ComparablePair<Object,Double>>();
        List users = new ArrayList(userPerformanceMap.keySet());
        Collections.sort(users);
        for (Object user: users){
            Double performance = userPerformanceMap.get(user);
            if (Double.isNaN(performance)) { continue; }
            ComparablePair<Object,Double> pair = new ComparablePair<Object,Double>(user, performance);
            list.add(pair);
        }

        if (list.isEmpty()) { return topUsers; }
        
        Collections.sort(list);

        if (reverse){
            Collections.reverse(list);
        }

        int nSign = 0;
        double sign = reverse ? Math.signum(1.0) : Math.signum(-1.0);
        for (nSign = 1; nSign < list.size() && Math.signum(list.get(nSign).getValue()) == sign;nSign++);
        
        int n = (int)(nSign * proportion);

        for (int i = 0; i < n; i++){
            topUsers.add(list.get(i).getKey());
        }

        return topUsers;
    }
    
    public static List<Object> getTopUsersInPerformance(Map<Object,Double> userPerformanceMap, int n, boolean reverse) throws IOException{

        List topUsers = new ArrayList();
        List<ComparablePair<Object,Double>> list = new ArrayList<ComparablePair<Object,Double>>();
        List users = new ArrayList(userPerformanceMap.keySet());
        Collections.sort(users);
        for (Object user: users){
            Double performance = userPerformanceMap.get(user);
            if (Double.isNaN(performance)) { continue; }
            ComparablePair<Object,Double> pair = new ComparablePair<Object,Double>(user, performance);
            list.add(pair);
        }

        Collections.sort(list);

        if (reverse){
            Collections.reverse(list);
        }

        for (int i = 0; i < n; i++){
            topUsers.add(list.get(i).getKey());
        }

        return topUsers;
    }
    
    private static Map<Object, Double> getUserPerformanceMap(List users, List items, RecommenderIF baseRecommender, RecommenderIF compRecommender, ModelIF testSet) throws IOException{
        logger.info("Computing user recommendation performance data");
        Map<Object, Double> userPerformanceMap = new HashMap<Object, Double>();
       
        for (Object user: users){
            double baseSqError = 0.0;
            double compSqError = 0.0;
            int n = 0;
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getUniquePreferencesFromUser(user, new DefaultAggregationFunction());
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                int pos = Collections.binarySearch(items, pref.getItem());
                if (pos < 0) { continue; }
                float realValue = pref.getValue();
                float basePrediction = baseRecommender.predict(user, pref.getItem(), pref.getContext());
                float compPrediction = compRecommender.predict(user, pref.getItem(), pref.getContext());
                if (Float.isNaN(basePrediction) || Float.isNaN(compPrediction)){ continue; }
                baseSqError += Math.pow(basePrediction - realValue, 2.0);
                compSqError += Math.pow(compPrediction - realValue, 2.0);
                n++;
            }
            float baseRMSE,compRMSE;
            baseRMSE = (n > 0 ? (float)(Math.sqrt(baseSqError / (double)n)) : 0);
            compRMSE = (n > 0 ? (float)(Math.sqrt(compSqError / (double)n)) : 0);
            if (n > 0){
                userPerformanceMap.put(user, (double)(baseRMSE - compRMSE));
            }
        }

        return userPerformanceMap;
    }
    
    private static Map<Object, Double> getUserPerformanceMap(List users, RecommenderIF baseRecommender, RecommenderIF compRecommender, ModelIF testSet) throws IOException{
        logger.info("Computing user recommendation performance data");
        Map<Object, Double> userPerformanceMap = new HashMap<Object, Double>();
        
        for (Object user: users){
            double baseSqError = 0.0;
            double compSqError = 0.0;
            int n = 0;
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getUniquePreferencesFromUser(user, new DefaultAggregationFunction());
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                float realValue = pref.getValue();
                float basePrediction = baseRecommender.predict(user, pref.getItem(), pref.getContext());
                float compPrediction = compRecommender.predict(user, pref.getItem(), pref.getContext());
                if (Float.isNaN(basePrediction) || Float.isNaN(compPrediction)){ continue; }
                baseSqError += Math.pow(basePrediction - realValue, 2.0);
                compSqError += Math.pow(compPrediction - realValue, 2.0);
                n++;
            }
            float baseRMSE,compRMSE;
            baseRMSE = (n > 0 ? (float)(Math.sqrt(baseSqError / (double)n)) : 0);
            compRMSE = (n > 0 ? (float)(Math.sqrt(compSqError / (double)n)) : 0);
            if (n > 0){
                userPerformanceMap.put(user, (double)(baseRMSE - compRMSE));
            }
        }

        return userPerformanceMap;
    }
    
    private static Map<Object, Double> getUserPerformanceMap(List users, DatasetIF dataset, String protocol, String alg1, String alg2, String metric) throws IOException{
        logger.info("Loading recommendation performance data");
        
        Map<Object, Double> userPerformanceAlg1Map = new HashMap<Object, Double>();
        Map<Object, Double> userPerformanceAlg2Map = new HashMap<Object, Double>();
        Map<Object, Double> userPerformanceMap = new HashMap<Object, Double>();

        logger.log(Level.INFO, "Loading recommendation performance data of algorithm{0}", alg1);
        String longProtocol = protocolNameMap.get(protocol);
        BufferedReader in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "-defaultContextWeekend__" + alg1 + "__"  + longProtocol + "__per_user_metrics.txt.tsv"));
        String metrics_title = in.readLine();
        String[] metrics = metrics_title.split("\t");
        int metricIndex=0;
        for (String m: metrics){
            if (m.equalsIgnoreCase(metric)){
                break;
            }
            metricIndex++;
        }
        String line;
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
            Integer user = Integer.parseInt(toks[3]);
            double performanceMetric = Double.parseDouble(toks[metricIndex]);
            userPerformanceAlg1Map.put(user, performanceMetric);
        }
        
        logger.log(Level.INFO, "Loading recommendation performance data of algorithm{0}", alg2);
        in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "-defaultContextWeekend__" + alg2 + "__"  + longProtocol + "__per_user_metrics.txt.tsv"));
        metrics_title = in.readLine();
        metrics = metrics_title.split("\t");
        metricIndex=0;
        for (String m: metrics){
            if (m.equalsIgnoreCase(metric)){
                break;
            }
            metricIndex++;
        }
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
            Integer user = Integer.parseInt(toks[3]);
            double performanceMetric = Double.parseDouble(toks[metricIndex]);
            userPerformanceAlg2Map.put(user, performanceMetric);
        }
        
        logger.info("Computing recommendation performance difference between algorithms");
        for (Object user:users){
            double userPerformance1 = userPerformanceAlg1Map.get(user);
            double userPerformance2 = userPerformanceAlg2Map.get(user);
            userPerformanceMap.put(user, userPerformance1 - userPerformance2);
        }
        
        return userPerformanceMap;
    }
    
    private static void timeSequenceMapToFile(Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> map, String file, TimestampType type){
        List<Integer> seqIDs = new ArrayList(map.keySet());
        Collections.sort(seqIDs);
        
        logger.log(Level.INFO, "Saving time sequences (training data) to file: {0}analysis/{1}_Tr_{2}.dat", new Object[]{path, file, type});
        
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" + file + "_Tr_" + type + ".dat"));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        

        boolean first = true;
        for (Object seqID: seqIDs){
            TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>> ts = map.get(seqID);
            for (PreferenceIF<Object,Object,ContinuousTimeContext> pref: ts.getTrainingSequence().getDataPoints()){
                if (first){ // if this is the first sequence processed, print the title line
                    ps.println("seqID\t" + ((DescriptionIF)pref).description());
                    first = false;
                }
                ps.println(seqID + "\t" +  pref);
            }
        }
        ps.flush();
        ps.close();
        
        try {
            ps = new PrintStream(new File(path + "analysis/" + file + "_Te_" + type + ".dat"));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        

        first = true;
        for (Object seqID: seqIDs){
            TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>> ts = map.get(seqID);
            for (PreferenceIF<Object,Object,ContinuousTimeContext> pref: ts.getTestSequence().getDataPoints()){
                if (first){ // if this is the first sequence processed, print the title line
                    ps.println("seqID\t" + ((DescriptionIF)pref).description());
                    first = false;
                }
                ps.println(seqID + "\t" +  pref);
            }
        }
        ps.flush();
        ps.close();
        
    }
    
    private static void readSequenceFeaturesMap(List sequences, String file, TimestampType type){
//        Map<Object, List<Double>> 
    }
    
    public static FeatureVector getAverageFeatures(List sequences, Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, Map<? extends Object,Double> sequencePerformanceMap){
        FeatureVector<Double> avg = new AverageFeatureVector<Double>();
        int i = 0;
        for (Object seqID: sequences){
            FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double> fts = map.get(seqID);
            for (String feature:fts.getFeatureNames()){
                avg.addFeature(feature, fts.getNamedFeature(feature));
            }
            avg.addFeature("performance", sequencePerformanceMap.get(seqID));
        }        
        return avg;
    }
    
    public static void saveAverageFeaturesIn(List sequences, Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, Map<Object,Double> sequencePerformanceMap, String file, TimestampType type){
        String theFile = path + "analysis/" + file + "_" + type + ".dat";
        logger.log(Level.INFO, "Computing and saving features and performance into file: {0}", theFile);
        Collections.sort(sequences);
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(theFile));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        
        Map<String,Double> averages = new HashMap<String,Double>();
        
        int i = 0;
        for (Object seqID: sequences){
            i++;
            FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double> fts = map.get(seqID);
            if (i==1){ // if this is the first sequence processed, print the title line
                for (String feature: fts.getFeatureNames()){
                    ps.print("\t" +  feature);
                    averages.put(feature, fts.getNamedFeature(feature));
                }
                ps.println("\tperformance");
                averages.put("performance", sequencePerformanceMap.get(seqID));
                continue;
            }
            for (String feature:fts.getFeatureNames()){
                Double value = averages.get(feature);
                value = (value*(double)(i-1) + fts.getNamedFeature(feature))/(double)i;
                averages.put(feature, value);
            }
            Double value = averages.get("performance");
            value = (value*(double)(i-1) + sequencePerformanceMap.get(seqID))/(double)i;
            averages.put("performance", value);
            
            if (i==sequences.size()){
                for (String feature: fts.getFeatureNames()){
                    ps.print("\t" +  averages.get(feature));
                }
                ps.println("\t"+averages.get("performance"));                
            }
        }
        ps.flush();
        ps.close();
        
    }

    public static void saveSelectedSequencesFeaturesAndPerformance(List sequences, Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, Map<? extends Object,Double> sequencePerformanceMap, String file, TimestampType type){
        String theFile = path + "analysis/" + file + "_" + type + ".dat";
        logger.log(Level.INFO, "Computing and saving features and performance into file: {0}", theFile);
        Collections.sort(sequences);
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(theFile));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        
        boolean first = true;
        for (Object seqID: sequences){
            FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double> fts = map.get(seqID);
            if (first){ // if this is the first sequence processed, print the title line
                ps.print("seqID");
                for (String feature: fts.getFeatureNames()){
                    ps.print("\t" +  feature);
                }
                ps.println("\tperformance");
                first = false;
            }
            ps.print(fts.getID());
            for (Double feature: fts.getFeatures()){
                ps.print("\t" +  feature);
            }
            ps.println("\t" +  sequencePerformanceMap.get(seqID));
        }
        ps.flush();
        ps.close();
        
    }
        
    private static void featuredTimeSequencesMapToFile(Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, String file, TimestampType type){
        String theFile = path + "analysis/" + file + "_" + type + ".dat";
        logger.log(Level.INFO, "Computing and saving time sequence features into file: {0}", theFile);
        List<Integer> seqIDs = new ArrayList(map.keySet());
        Collections.sort(seqIDs);
        
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(theFile));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        
        boolean first = true;
        for (Object seqID: seqIDs){
            FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double> fts = map.get(seqID);
            if (first){ // if this is the first sequence processed, print the title line
                ps.print("seqID");
                for (String feature: fts.getFeatureNames()){
                    ps.print("\t" +  feature);
                }
                ps.println();
                first = false;
            }
            ps.print(fts.getID());
            for (Double feature: fts.getFeatures()){
                ps.print("\t" +  feature);
            }
            ps.println();
        }
        ps.flush();
        ps.close();
    }
    
    private static Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> getFeaturedTimeSequences(Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> seqs, List<TimeSequenceFeature<Object, PreferenceIF<Object,Object,ContinuousTimeContext>, Double>> features){
        logger.log(Level.INFO, "Adding features to time sequence");
        Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map = new HashMap<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>>();
        for (Object seqID: seqs.keySet()){
            FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double> fts =
                    new FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>( seqs.get(seqID) );
            for (TimeSequenceFeature<Object,PreferenceIF<Object,Object,ContinuousTimeContext>, Double> feature: features){
                fts.addFeature(feature);
            }
            map.put(seqID, fts);
        }
        return map;
    }
    
    private static Map<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object,ContinuousTimeContext>>> readPredictionRatingsSequences(String file, TimestampType type) throws IOException{
        Map<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object,ContinuousTimeContext>>> seqs = new HashMap<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object,ContinuousTimeContext>>>();
        // Data load
        logger.info("Loading prediction ratings sequences");
        BufferedReader in = new BufferedReader(new FileReader(path + "analysis/" +  file + "_Tr_" + type + ".dat"));
        String line;
        in.readLine(); // title line
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[1]);
            int item = Integer.parseInt(toks[2]);
            ComparablePair<Object,Object> seqID = new ComparablePair(user,item);
            float rating = Float.parseFloat(toks[3]);
            long timestamp = Long.parseLong(toks[4]);
            double similarity = Double.parseDouble(toks[5]);
            ContinuousTimeContextIF ctx = new ContinuousTimeContext(timestamp);
            WeightedPreferenceIF<Integer, Integer, ContinuousTimeContextIF> pref = new WeightedExplicitPreference<Integer, Integer, ContinuousTimeContextIF>(similarity, user, item, ctx, rating);
            PredictionRatingsTimeSequence seq = (PredictionRatingsTimeSequence)seqs.get(seqID);
            if (seq == null){
                seq = new PredictionRatingsTimeSequence(user, item, 0);
            }
            seq.add(pref, timestamp);
            seqs.put(seqID, seq);
        }
        in.close();
        
        in = new BufferedReader(new FileReader(path + "analysis/" +  file + "_Te_" + type + ".dat"));
        in.readLine(); // title line
        long minTestTimestamp = Long.MAX_VALUE;
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
//            int seqID = Integer.parseInt(toks[0]);
            int user = Integer.parseInt(toks[1]);
            int item = Integer.parseInt(toks[2]);
            ComparablePair<Object,Object> seqID = new ComparablePair(user,item);            
            float rating = Float.parseFloat(toks[3]);
            long timestamp = Long.parseLong(toks[4]);
            ContinuousTimeContextIF ctx = new ContinuousTimeContext(timestamp);
            PreferenceIF<Integer, Integer, ContinuousTimeContextIF> pref = new ExplicitPreference<Integer, Integer, ContinuousTimeContextIF>(user, item, ctx, rating);
            
            PredictionRatingsTimeSequence seq = (PredictionRatingsTimeSequence)seqs.get(seqID);
            if (seq == null){
                seq = new PredictionRatingsTimeSequence(user, item, 0);
            }
            seq.add(pref, timestamp);
            seqs.put(seqID, seq);
            if (timestamp < minTestTimestamp){
                minTestTimestamp = timestamp;
            }
        }
        
        for (Object seqID: seqs.keySet()){
            PredictionRatingsTimeSequence seq = (PredictionRatingsTimeSequence)seqs.get(seqID);
            seq.updateSplitTime(minTestTimestamp);
            seqs.put(seqID, seq);
        }
        
        return seqs;
    }
    
    private static Map<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>>> genUserRatingSequenceMap(List users, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet, ContextualModelUtils eTest){
        Map<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>>> ratingsSequenceMap = new HashMap<Object, TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>>>();
        Collections.sort(users);
        int totalUsers = users.size();
        int i = 0;
        for (Object user : users){
            i++;
            TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>> ts =
                    new TimeSequence<Object,PreferenceIF<Object, Object, ContinuousTimeContext>>(user, eTest.getMinDate().getTime());
            logger.log(Level.INFO, "Computing ratings sequence of user {0} out of {1} ({2})", new Object[]{i, totalUsers, user});

            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> trainingPrefs = trainSet.getPreferencesFromUser(user);
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: trainingPrefs){
                ts.add(pref, pref.getContext().getTimestamp());
            }
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getPreferencesFromUser(user);
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                ts.add(pref, pref.getContext().getTimestamp());
            }
            ratingsSequenceMap.put(user, ts);
        }
        return ratingsSequenceMap;
    }
    
    private static Map<Object, TimeSequenceIF<Object, PreferenceIF<Object,Object,ContinuousTimeContext>>> genNeighborsRatingsSequences(Map<Object,List<SimilarityDatumIF>> userNeighborsMap, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet, ContextualModelUtils eTest){
        Map<Object, TimeSequenceIF<Object, PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = new HashMap<Object, TimeSequenceIF<Object, PreferenceIF<Object,Object,ContinuousTimeContext>>>();
        List users = new ArrayList(userNeighborsMap.keySet());
        int totalUsers = users.size();
        int i = 0;
        for (Object user : users){
            i++;
            TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>> ts =
                    new TimeSequence<Object,PreferenceIF<Object, Object, ContinuousTimeContext>>(user, eTest.getMinDate().getTime(),neighbors*100);
            logger.log(Level.INFO, "Computing ratings sequence of user {0} out of {1} ({2})", new Object[]{i, totalUsers, user});
            for (SimilarityDatumIF datum: userNeighborsMap.get(user)){
                Object neighbor = datum.getKey();
                Collection<PreferenceIF<Object,Object,ContinuousTimeContext>> trainingPrefs = trainSet.getPreferencesFromUser(neighbor);
                for (PreferenceIF<Object,Object,ContinuousTimeContext> pref: trainingPrefs){
                    WeightedPreferenceIF wPref = new WeightedExplicitPreference(datum.getSimilarity(), pref.getUser(), pref.getItem(), pref.getContext(), pref.getValue());
                    ts.add(wPref, pref.getContext().getTimestamp());                
                }
            }
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getPreferencesFromUser(user);
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                ts.add(pref, pref.getContext().getTimestamp());
            }
            ratingsSequenceMap.put(user, ts);
        }
        return ratingsSequenceMap;
    }
    
    private static Map<Object, TimeSequenceIF<Object, PreferenceIF<Object, Object, ContinuousTimeContext>>> genNeighborsUsedRatingsSequences(Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet, ContextualModelUtils eTest){
        Map<Object, TimeSequenceIF<Object, PreferenceIF<Object, Object, ContinuousTimeContext>>> ratingsSequenceMap = new HashMap<Object, TimeSequenceIF<Object, PreferenceIF<Object, Object, ContinuousTimeContext>>>();
        int totalPairs = useritemNeighborsusedMap.size();
        int pair = 0;
        for (ComparablePair<Object,Object> testPair : useritemNeighborsusedMap.keySet()){
            Object user = testPair.getKey();
            Object item = testPair.getValue();
            pair++;
            TimeSequenceIF<Object,PreferenceIF<Object, Object, ContinuousTimeContext>> ts =
                    new PredictionRatingsTimeSequence(user, item, eTest.getMinDate().getTime(),neighbors*100);
            Map<Object,Float> neighborsUsed = useritemNeighborsusedMap.get(testPair);
            logger.log(Level.INFO, "Computing ratings sequence of test pair {0} out of {1} {2}", new Object[]{pair, totalPairs, testPair});

            List neighbors = new ArrayList(neighborsUsed.keySet());
            Collections.sort(neighbors);
            
            for (Object neighbor: neighbors){
                Collection<PreferenceIF<Object,Object,ContinuousTimeContext>> trainingPrefs = trainSet.getPreferences(neighbor,item);
                if (trainingPrefs.size() > 1)  {
                    logger.log(Level.SEVERE, "Duplicated rating for test pair ({0},{1}), neighbor {2}", new Object[]{user,item,neighbor});
                    System.exit(1);
                }
                for (PreferenceIF<Object,Object,ContinuousTimeContext> pref: trainingPrefs){
                    Float similarity = neighborsUsed.get(neighbor);
                    WeightedPreferenceIF wPref = new WeightedExplicitPreference(similarity, pref.getUser(), pref.getItem(), pref.getContext(), pref.getValue());
                    ts.add(wPref, pref.getContext().getTimestamp());                
                }
            }
            Collection<PreferenceIF<Object,Object, ContinuousTimeContext>> testingPrefs = testSet.getPreferences(user,item);
                if (testingPrefs.size() > 1)  {
                    logger.log(Level.SEVERE, "Duplicated rating for test pair ({0},{1})", new Object[]{user,item});
                    System.exit(1);
                }
            for (PreferenceIF<Object,Object, ContinuousTimeContext> pref: testingPrefs){
                ts.add(pref, pref.getContext().getTimestamp());
            }
            ratingsSequenceMap.put(testPair, ts);
        }
        return ratingsSequenceMap;
    }

    /*
     * Generates a mapping from test ratings (pair user, item) to a map of ratings used to predict the rating
     * The keys of the map of ratings used for prediction correspond to the neighbors' identifier, 
     * and the values correspond to the similarity of that neighbor to the target user.
     */
    private static Map<ComparablePair<Object,Object>, Map<Object,Float>> genUsedRatingsInPrediction(Map<Object, List<SimilarityDatumIF>> userNeighborsMap, List selectedUsers, List selectedItems, int minNeighbors, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet){
        
        // StringBuilders init
        logger.log(Level.INFO, "Identifying used ratings in predictions");

        if (selectedUsers == null){
            selectedUsers = new ArrayList<Integer>(testSet.getUsers());
        }
        Collections.sort(selectedUsers);
        
        int totalUsers = selectedUsers.size();

        int i = 0;
        Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = new HashMap<ComparablePair<Object,Object>, Map<Object,Float>>();
        for (Object user: selectedUsers){
            i++;
            logger.log(Level.INFO, "identifying used ratings for user{0}({1} out of {2})", new Object[]{user, i, totalUsers});
            if (eTrain.getUserRatingCount(user) == 0) { continue; } // no training data for user
            List<PreferenceIF> testPrefs = new ArrayList(testSet.getPreferencesFromUser(user));
            List<SimilarityDatumIF> neighbors = userNeighborsMap.get(user);
            if (testPrefs == null || neighbors == null) { continue; } // no test ratings or neighbors data
            for (PreferenceIF pref: testPrefs){
                Object item = pref.getItem();
                if (Collections.binarySearch(selectedItems, item) < 0) { continue; }
                if (eTrain.getItemRatingCount(item) == 0) { continue; } // no training data for item
                ComparablePair<Object,Object> testPair = new ComparablePair<Object,Object>(user, item);
                int count = 0;
                Map<Object,Float> neighborsUsed = useritemNeighborsusedMap.get(testPair);
                if (neighborsUsed == null){
                    neighborsUsed = new HashMap<Object,Float>();
                }
                for (SimilarityDatumIF sd: neighbors){
                    Integer neighbor = (Integer)sd.getKey();
                    Collection<PreferenceIF> prefs = trainSet.getPreferences(neighbor, item);
                    if (prefs == null) { continue; }
                    count++;
                    neighborsUsed.put(neighbor,sd.getSimilarity());
                }
                if (count > minNeighbors) {
                    useritemNeighborsusedMap.put(testPair, neighborsUsed);
                }
            }
        }

        return useritemNeighborsusedMap;
    }

    private static Map<Object, List<SimilarityDatumIF>> getUserNeighborsMap(List users, ModelIF trainSet, int neighbors){
        // StringBuilders init
        logger.log(Level.INFO, "Generating user -> neighbors map");
        SimilarityComputerIF simComputer;
        NeighborhoodIF neighborhoodComputer;

        simComputer = new PearsonWeightedSimilarity();
        neighborhoodComputer = new UserNeighborhoodComputer(trainSet, neighbors, simComputer);

        HashMap<Object, List<SimilarityDatumIF>> userNeighborsMap = new HashMap<Object, List<SimilarityDatumIF>>();

        if (users == null){
            users = new ArrayList<Integer>(trainSet.getUsers());
        }
        Collections.sort(users);

        int totalUsers = users.size();
        int i = 0;
        for (Object user: users){
            i++;
            logger.log(Level.INFO, "Identifying neighbors of user: {0}({1} out of {2})", new Object[]{user, i, totalUsers});
            userNeighborsMap.put(user, neighborhoodComputer.getNeighbors(user));
        }

        return userNeighborsMap;
    }
    
    @SuppressWarnings("CallToThreadDumpStack")
    public static void UserFeaturesMapToFile(Map<String,String> userFeaturesMap, DatasetIF dataset, String protocol, String file, TimestampType type)  throws IOException{
        logger.info("Saving computed feature vectors");
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" +  file +"_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        ps.println(userFeaturesMap.get("title"));
        List<String> users = new ArrayList(userFeaturesMap.keySet());
        Collections.sort(users);

        for (String user: users){
            if (user.equalsIgnoreCase("title")) { continue; }
            ps.println(userFeaturesMap.get(user));
        }
        ps.flush();
        ps.close();

    }

    private static SplitIF getSplit(DatasetIF dataset, String protocol){
        final StringBuilder experimentInfo = new StringBuilder();

        // Data load
        logger.info("Loading data");
        ModelIF<Object, Object, ContinuousTimeContextIF> model = dataset.getModel();
        ContextualModelUtils<Object, Object, ContinuousTimeContextIF> eModel = new ContextualModelUtils(model);

        logger.info("Data loaded");
        final StringBuilder datasetInfo = new StringBuilder();
        datasetInfo.append(newline).append("DatasetSummary").append(newline);
        datasetInfo.append("Name\t").append(dataset).append(newline);
        datasetInfo.append(new SummaryPrinter().summary(model, null));
        experimentInfo.append(datasetInfo);
        System.out.print(datasetInfo);

        // Training-test splitter
        logger.info("Splitting data");
        DatasetSplitterIF<Object, Object, ContinuousTimeContextIF> splitter;
        splitter = new CommonDatasetSplitters<Object, Object, ContinuousTimeContextIF>().proportion(protocolProportionMap.get(protocol)).dataset(dataset).getDatasetSplitter(split_method);
        final StringBuilder splitterInfo = new StringBuilder();
        splitterInfo.append("Splitter\t").append(splitter).append(newline);
        experimentInfo.append(splitterInfo);
        System.out.print(splitterInfo);

        SplitIF<Object, Object, ContinuousTimeContextIF> splits[] = splitter.split(model);
        return splits[0];
    }
    
    static int substringPos(String line, String substring, String separator){
        int pos;
        String[] toks = line.split(separator);
        for (pos = 0; pos < toks.length; pos++){
            String tok = toks[pos];
            if (tok.equalsIgnoreCase(substring)){
                break;
            }
        }
        return pos;
    }
    
    public static List<Object> selectUsers(ModelIF testSet, ContextualModelUtils eTrain){
        logger.info("Selecting users");
        List tentativeUsers = new ArrayList(testSet.getUsers());
        List selectedUsers = new ArrayList();
        
        for (Object user: tentativeUsers){
            if (eTrain.getUserRatingCount(user) == 0) { continue; }
            selectedUsers.add(user);
        }
        Collections.sort(selectedUsers);
        return selectedUsers;
    }
    
    public static List<Object> selectItems(ModelIF testSet, ContextualModelUtils eTrain, double itemPopularity, boolean mostPopulars, MovieAge itemAge, ItemDataIF itemData){
        logger.info("Selecting items");
        List<Object> allItems = new ArrayList(testSet.getItems());
        
        // Popularity
        List<ComparablePair<Object, Integer>> movieRatingsList = new ArrayList<ComparablePair<Object,Integer>>();
        for (Object item: allItems){
            movieRatingsList.add(new ComparablePair<Object, Integer>(item, eTrain.getItemRatingCount(item)));
        }
        Collections.sort(movieRatingsList);
        
        List<Object> popularItems = new ArrayList<Object>();
        int nItems = allItems.size();
        int nPopularItems = (int)(nItems * itemPopularity);
        int begin, end;
        if (mostPopulars){
            begin = nItems - nPopularItems; 
            end = nItems - 1;
        }
        else{
            begin = 0;
            end = nPopularItems - 1;
        }
        for(int i = begin; i <= end; i++){
            popularItems.add(movieRatingsList.get(i).getKey());
        }
        
        // Age
        List finalItems = new ArrayList();
        for (Object item: popularItems){
            int year = itemData.year(item);
            switch(itemAge){
                case AFTER_2000:
                    if (year >= 2000){
                        finalItems.add(item);
                    }
                    break;
                case DECADE_1990:
                    if (year >= 1990 && year < 2000){
                        finalItems.add(item);
                    }
                    break;
                case DECADE_1980:
                    if (year >= 1980 && year < 1990){
                        finalItems.add(item);
                    }
                    break;
                case DECADE_1960_1970:
                    if (year >= 1960 && year < 1980){
                        finalItems.add(item);
                    }
                    break;
                case BEFORE_1960:
                    if (year < 1960){
                        finalItems.add(item);
                    }
                    break;
                case ALL:
                default:
                        finalItems.add(item);
            }
        }
        Collections.sort(finalItems);
        return finalItems;
    }
    
    
    
    public static enum FEATURE{
        timeDistance;
    }
    
    static String getFeatureValue(FEATURE feature, String originalFeatures, String originalFeaturesTitles, String testFeatures, String testFeaturesTitles){
        String featureValue = "";
        switch(feature){
            case timeDistance:
                int pos = substringPos(originalFeaturesTitles, "timestamp", "\t");
                Long timestamp = Long.parseLong(originalFeatures.split("\t")[pos]);
                int testPos = substringPos(testFeaturesTitles, "timestamp", "\t");
                Long testTimestamp = Long.parseLong(testFeatures.split("\t")[testPos]);
                Long distance = testTimestamp - timestamp;
                featureValue = distance.toString();
                break;
        }
        return featureValue;
    }

    static Map<String, RecommenderIF> initAlgorithms(SplitIF split, int neighbors){
        Map<String, RecommenderIF> algorithmsMap = new HashMap<String, RecommenderIF>();
        algorithmsMap.put("algorithm1", new CommonRecommenders().neighbors(neighbors).halfLifeProp(0.25).trTimespan(split).getRecommender(CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp, split.getTrainingSet()));
        algorithmsMap.put("algorithm2", new CommonRecommenders().neighbors(neighbors).halfLifeProp(0.50).trTimespan(split).getRecommender(CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp, split.getTrainingSet()));
        algorithmsMap.put("algorithm3", new CommonRecommenders().neighbors(neighbors).halfLifeProp(0.75).trTimespan(split).getRecommender(CommonRecommenders.METHOD.TimeDecay_UserBased_TrProp, split.getTrainingSet()));
        algorithmsMap.put("algorithm4", new ContextFilteringBasedRecommenderBuilder(split.getTrainingSet()).WeekPeriod().neighbors(neighbors).buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPreFiltering_UserBased));
        algorithmsMap.put("algorithm5", new ContextFilteringBasedRecommenderBuilder(split.getTrainingSet()).WeekPeriod().neighbors(neighbors).buildRecommender(RecommenderBuilderIF.RECOMMENDER_METHOD.ContextualPostFiltering_UserBased));
        
        return algorithmsMap;
    }
    
    static void processArgs(String[] args){
        if (args.length == 0) { return; }
        List<String> theProts = new ArrayList<String>();
        List<String> theAlgos = new ArrayList<String>();
        for (String arg : args){
            String[] param = arg.split("=");

            if (param[0].startsWith("/") || param[0].startsWith(".")){
                basePath = param[0];
            }
            
            if (param[0].equalsIgnoreCase("protocol")){
                String[] prots = param[1].split(",");
                for (String prot:prots){
                    theProts.add("protocol"+prot);
                }
                protocols = theProts;
            }
            
            if (param[0].equalsIgnoreCase("algorithm")){
                String[] algs = param[1].split(",");
                for (String alg:algs){
                    theAlgos.add("algorithm"+alg);
                }
                algorithms = theAlgos;
            }
            
            if (param[0].equalsIgnoreCase("file")){
                resultsFileString = param[1];
            }
            
        }
    }
}
