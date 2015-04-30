package es.uam.eps.ir.analysis;

import es.uam.eps.ir.core.context.ContinuousTimeContext;
import es.uam.eps.ir.core.model.DescriptionIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.model.WeightedExplicitPreference;
import es.uam.eps.ir.cars.model.WeightedPreferenceIF;
import es.uam.eps.ir.cars.neighborhood.NeighborhoodIF;
import es.uam.eps.ir.core.similarity.PearsonWeightedSimilarity;
import es.uam.eps.ir.core.similarity.SimilarityComputerIF;
import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.cars.neighborhood.UserNeighborhoodComputer;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.experiments.CommonDatasetSplitters;
import es.uam.eps.ir.split.DatasetSplitterIF;
import es.uam.eps.ir.timeanalysis.ComparablePair;
import es.uam.eps.ir.timeanalysis.BasicTimeSequence;
import es.uam.eps.ir.timeanalysis.InformativeContext;
import es.uam.eps.ir.timeanalysis.TimeUtils;
import es.uam.eps.ir.timesequence.FeaturedTimeSequence;
import es.uam.eps.ir.timesequence.PredictionRatingsTimeSequence;
import es.uam.eps.ir.timesequence.TimeSequence;
import es.uam.eps.ir.timesequence.TimeSequenceIF;
import es.uam.eps.ir.timesequence.feature.AverageFeatureVector;
import es.uam.eps.ir.timesequence.feature.FeatureVector;
import es.uam.eps.ir.timesequence.feature.MeanSimilarity;
import es.uam.eps.ir.timesequence.feature.RatingMean;
import es.uam.eps.ir.timesequence.feature.RatingNumber;
import es.uam.eps.ir.timesequence.feature.TestDistanceToFirst;
import es.uam.eps.ir.timesequence.feature.TestDistanceToLast;
import es.uam.eps.ir.timesequence.feature.MeanSimilarity_Quartile;
import es.uam.eps.ir.timesequence.feature.RatingSD;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;
import utils.SummaryPrinter;

/**
 *
 * @author pedro
 */
public class UserProfileAnalysis {

    public enum TimestampType{
        DAY,
        SECOND
    };

    final static Map<String, String> protocolsMap = new HashMap<String, String>();

    // Dataset
    final static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.MovieLens1m;
    // Evaluation Methodology
    static CommonDatasetSplitters.METHOD   split_method        = CommonDatasetSplitters.METHOD.CommunityTimeOrderTimeProportionHoldout;
    final static double testProportion = 0.1;
    final static int testSize = 9;

    final static int nUsers = 20;
    final static int neighbors = 200;        
    final static int minNeighbors = 3;

    static String path = "/datos/experiments/ProfileAnalysis/";
    // Logger
    final static Logger logger = Logger.getLogger("ExperimentLog");
    final static Level level = Level.INFO;

    final static String newline = System.getProperty("line.separator");

    @SuppressWarnings("CallToThreadDumpStack")
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

        DatasetIF<Object, Object, ContinuousTimeContextIF> dataset = new CommonDatasets(args).getDataset(dataset_name);
        TimeUtils.STATS[] stats = TimeUtils.STATS.values();

        String protocol = "protocol4";
        protocolsMap.put("protocol1", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.4)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol2", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.3)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol3", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.2)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol4", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.1)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol5", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.08)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol6", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.06)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol7", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.04)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol8", "Holdout[communityCentric_timeOrder_TimeProportionSize(0.02)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
        protocolsMap.put("protocol10", "Holdout[communityCentric_timeOrder_ProportionSize(0.2)]USER_TEST_threshold=0.0__TrF_None_TeF_None");
//        String protocol = "R_No_TU_FS(9)_Te_Th(3.0)";
        
        String alg1 = "PearsonWeightedSimilarity_UserBased";
        String alg2 = "TimeDecay_UserBased_T0Prop=0.25_T0=233";
        String metric = "RMSE";
        

        path += dataset + "/" + protocol + "/";
        int step = 700;
        TimestampType type = TimestampType.SECOND;

        FEATURE[] basicFeatures = {
            FEATURE.timeDistance
        };

        switch (step){

            // User features
            case 100: // computes per user feature vector of profile statistics (training data profile)
                computePerUserProfileBasicFeatureVector(dataset, protocol, stats, null, type);
                break;

            case 110: // add RS metrics to each user's profile feature vector
                try{
                    Map<String, String> userFeaturesMap = readUserFeatureVectorMap(dataset, protocol, "basic_userFeatures", type);
                    userFeaturesMap = addMetric(userFeaturesMap, dataset, alg1, protocol, metric);
                    userFeaturesMap = addMetric(userFeaturesMap, dataset, alg2, protocol, metric);
                    UserFeaturesMapToFile(userFeaturesMap, dataset, protocol, "userFeaturesNoClass", type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            // User class and ratings sequence
            case 200: // User class and ratings sequence of selected users.
                    // Class is assigned according to the difference of the values on the last two columns of the feaure vector
                    //   (assumed as the RS metric values from two different RS)
                try{
                    Map<String, String> userFeaturesMap = readUserFeatureVectorMap(dataset, protocol, "basic_userFeatures", type);
                    userFeaturesMap = addMetric(userFeaturesMap, dataset, alg1, protocol, metric);
                    userFeaturesMap = addMetric(userFeaturesMap, dataset, alg2, protocol, metric);
                    userFeaturesMap = addMetricDifference(userFeaturesMap, metric);
                    UserFeaturesWithClassMapToFile(userFeaturesMap, dataset, protocol, null, "userFeatures_all" , type);

                    List<Integer> selectedUsers = getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, false);
                    selectedUsers.addAll(getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, true)); //the "inverse" top users
                    UserFeaturesWithClassMapToFile(userFeaturesMap, dataset, protocol, selectedUsers, "userFeatures_top" + nUsers, type);
                    
                    String file = "userRatingsSequence_top" + nUsers;
                    PerUserBasicRatingsSequenceToFile(dataset, protocol, selectedUsers, "basic_" + file , type);
                    addMetricsDiffToSequence(dataset, protocol, "basic_" + file, file, alg1, alg2, type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 201: // generate ratings sequences files from given users
//                int[] users ={427,255,807,768,121, 557,395,368,444,676};
//                int[] users ={2753, 5795, 4523, 1213, 5202, 4471, 5281, 5279, 3317, 2565,   2665, 1922, 1109, 5433, 4674, 4937, 5265, 3726, 657, 1873};
                List<Integer> users = Arrays.asList(2753, 5795, 4523, 1213, 5202, 4471, 5281, 5279, 3317, 2565,   2665, 1922, 1109, 5433, 4674, 4937, 5265, 3726, 657, 1873);
//                int[] users ={};
                PerUserBasicRatingsSequenceToFile(dataset, protocol, users, "basic_userRatingsSequence_selectedUsers", type);
                break;

            case 210: // add RS metric to users' ratings sequence file
                try{
                    String file = "userRatingsSequence_selectedUsers";
                    addMetricsDiffToSequence(dataset, protocol, "basic_" + file, file, alg1, alg2, type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 211: // add items' genres to a ratings sequence file (asumes the output of step 6 as input file)
                try{
                    String itemsDescriptionFile = "movies.dat";
                    Map<Integer,Set<String>> itemGenresMap = computeItemGenres(itemsDescriptionFile);
                    addGenresToSequence(dataset, protocol, "userRatingsSequence_top" + nUsers  , itemGenresMap, type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 300: // users' neighbors features
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();

                    Map<String, String> userFeaturesMap = readUserFeatureVectorMap(dataset, protocol, "userFeatures_top" + nUsers, type);
                    List<Integer> selectedUsers = getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, false);
                    selectedUsers.addAll(getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, true));

                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);
                    Map<String, String> neighborsDataMap = genNeighborsMeanFeaturesMap(userFeaturesMap, userNeighborsMap, dataset, protocol);
                    UserFeaturesMapToFile(neighborsDataMap, dataset, protocol, "neighborsFeatures_top" + nUsers, type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
            case 310: // neighbors' ratings sequence
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    Map<String, String> userFeaturesMap = readUserFeatureVectorMap(dataset, protocol, "userFeatures_top" + nUsers, type);
                    List<Integer> selectedUsers = getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, false);
                    selectedUsers.addAll(getTopUserInColumn(userFeaturesMap, metric + "Diff", nUsers, true));

                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);
                    Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = genUsedRatingsInPrediction(userNeighborsMap, selectedUsers, minNeighbors, trainSet, eTrain, testSet);

                    String fileSuffix = "testRatings";
                    Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> ratingsSequenceMap = genNeighborsUsedRatingsSequences(useritemNeighborsusedMap, trainSet, eTrain, testSet, eTest);
                    pairSequenceMapToFile(ratingsSequenceMap, dataset, protocol, "basic_"  + fileSuffix + "Sequence_top" + nUsers, type);
                    addMetricsDiffToSequence(dataset, protocol, "basic_" + fileSuffix + "Sequence_top" + nUsers + "_Te", fileSuffix + "Sequence_top" + nUsers + "_Te", alg1, alg2, type);

                    Map<String, String> ratingsProfileMap = genNeighborsUsedRatingsProfiles(stats, ratingsSequenceMap, type);
                    ratingsProfileMap = addPredictionErrorToPair(ratingsProfileMap, dataset, protocol, alg1, alg2, metric, testSet);
                    UserFeaturesWithClassMapToFile(ratingsProfileMap, dataset, protocol, null, fileSuffix + "Features_top" + nUsers, type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 320: // computes additional features of neighbors' used ratings sequence
                try{
                    String file = "testRatingsSequence_top" + nUsers;
                    for (FEATURE feature: basicFeatures){
                        addFeatureToNeighborsRatingSequence("basic_" + file, file , feature, type);
                    }
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
                
            case 400: // all neighbors' used ratings' profile analysis
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(null, trainSet, neighbors);

                    Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = genUsedRatingsInPrediction(userNeighborsMap, null, minNeighbors, trainSet, eTrain, testSet);


                    String fileSuffix = "usedRatings";
                    Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> ratingsSequenceMap = genNeighborsUsedRatingsSequences(useritemNeighborsusedMap, trainSet, eTrain, testSet, eTest);

                    Map<String, String> ratingsProfileMap = genNeighborsUsedRatingsProfiles(stats, ratingsSequenceMap, type);
                    ratingsProfileMap = addPredictionErrorToPair(ratingsProfileMap, dataset, protocol, alg1, alg2, metric, testSet);
                    UserFeaturesWithClassMapToFile(ratingsProfileMap, dataset, protocol, null, fileSuffix+"Features_all", type);
                }
                catch (IOException e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 500: // user ratings sequences and features
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    List tentativeUsers = new ArrayList(testSet.getUsers());
                    List selectedUsers = new ArrayList();
                    for (Object user: tentativeUsers){
                        if (eTrain.getUserRatingCount(user) == 0) { continue; }
                        selectedUsers.add(user);
                    }
                    Collections.sort(selectedUsers);
                    String dataFile = "userRatings";
                    Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> ratingsSequenceMap = genUserRatingSequenceMap(selectedUsers, trainSet, eTrain, testSet, eTest);
                    timeSequenceMapToFile(ratingsSequenceMap, dataFile + "Sequence_all", type);
                    
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
                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);
                    
                    Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, dataset, protocol, alg1, alg2, metric);
                    
                    
                    Map<String, List<Object>> categoryAvgsMap = new HashMap<String, List<Object>>();
                    Map<String, FeatureVector> avgs = new HashMap<String, FeatureVector>();
                    List<Integer> levels = Arrays.asList(10,20,50);
                    for (int level: levels){
                        categoryAvgsMap.put("top" + level + "UsersPos", getTopUsersInPerformance(userPerformanceMap, level, true) );
                        categoryAvgsMap.put("top" + level + "UsersNeg", getTopUsersInPerformance(userPerformanceMap, level, false) );
                        
                        List<Object> topUsersPos = getTopUsersInPerformance(userPerformanceMap, level, true);
                        FeatureVector<Double> avgPos = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + level + "UsersPos", avgPos);
                        
                        List<Object> topUsersNeg = getTopUsersInPerformance(userPerformanceMap, level, false);
                        FeatureVector<Double> avgNeg = getAverageFeatures(topUsersPos, featuredTimeSequenceMap, userPerformanceMap);
                        avgs.put("top" + level + "UsersNeg", avgNeg);
                    }
                    
                    List<String> categories = new ArrayList(avgs.keySet());
                    Collections.sort(categories);
                    
                    for (String category: categories){
                        for (Object value: avgs.get(category).getFeatureNamesAndValues()){
                            System.out.println(category + "\t" + value);
                        }
                    }

                    
//                    saveSelectedSequencesFeaturesAndPerformance(topUsers, featuredTimeSequenceMap, userPerformanceMap, dataFile + "Features_top2", type);
//                    saveAverageFeaturesIn(topUsers, featuredTimeSequenceMap, userPerformanceMap, dataFile + "Averages_top2", type);
                    
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
                
            case 600: // all neighbors' ratings sequence
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    List tentativeUsers = new ArrayList(testSet.getUsers());
                    List selectedUsers = new ArrayList();
                    for (Object user: tentativeUsers){
                        if (eTrain.getUserRatingCount(user) == 0) { continue; }
                        selectedUsers.add(user);
                    }
                    Collections.sort(selectedUsers);
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
                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;

            case 700: // neighbors' used ratings sequence
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    List tentativeUsers = new ArrayList(testSet.getUsers());
                    List selectedUsers = new ArrayList();
                    for (Object user: tentativeUsers){
                        if (eTrain.getUserRatingCount(user) == 0) { continue; }
                        selectedUsers.add(user);
                    }
                    Collections.sort(selectedUsers);
                    Map<Object, List<SimilarityDatumIF>> userNeighborsMap = getUserNeighborsMap(selectedUsers, trainSet, neighbors);
                    Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = genUsedRatingsInPrediction(userNeighborsMap, selectedUsers, minNeighbors, trainSet, eTrain, testSet);

                    String dataFile = "testRatings";
                    Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> ratingsSequenceMap = genNeighborsUsedRatingsSequences(useritemNeighborsusedMap, trainSet, eTrain, testSet, eTest);
                    pairSequenceMapToFile(ratingsSequenceMap, dataset , protocol, dataFile + "Sequence_all", type);

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
                    
                    Map<Object, TimeSequenceIF<Object,PreferenceIF<Object,Object,ContinuousTimeContext>>> predictionRatingsSequenceMap = readPredictionRatingsSequences("testRatingsSequence_all", type);
                    Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> featuredTimeSequenceMap
                            = getFeaturedTimeSequences(predictionRatingsSequenceMap, features);
                    
                    featuredTimeSequencesMapToFile(featuredTimeSequenceMap, dataFile + "Features_all", type);
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;                
                
            case 800: // obtain metrics
                try{
                    SplitIF split = getSplit(dataset);
                    ModelIF trainSet = split.getTrainingSet();
                    ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
                    ModelIF testSet = split.getTestingSet();
                    ContextualModelUtils eTest = new ContextualModelUtils(testSet);

                    List tentativeUsers = new ArrayList(testSet.getUsers());
                    List selectedUsers = new ArrayList();
                    for (Object user: tentativeUsers){
                        if (eTrain.getUserRatingCount(user) == 0) { continue; }
                        selectedUsers.add(user);
                    }
                    Collections.sort(selectedUsers);

                    Map<Object,Double> userPerformanceMap = getUserPerformanceMap(selectedUsers, dataset, protocol, alg1, alg2, metric);
                    
                    List<Object> topUsers = getTopUsersInPerformance(userPerformanceMap, nUsers, false);
                    
                }
                catch (Exception e){
                    System.err.println(e);
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
        }
        

        logger.info("done");
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
    
    private static Map<Object, Double> getUserPerformanceMap(List users, DatasetIF dataset, String protocol, String alg1, String alg2, String metric) throws IOException{
        logger.info("Loading recommendation performance data");
        
        Map<Object, Double> userPerformanceAlg1Map = new HashMap<Object, Double>();
        Map<Object, Double> userPerformanceAlg2Map = new HashMap<Object, Double>();
        Map<Object, Double> userPerformanceMap = new HashMap<Object, Double>();

        logger.log(Level.INFO, "Loading recommendation performance data of algorithm{0}", alg1);
        String longProtocol = protocolsMap.get(protocol);
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
        for (int seqID: seqIDs){
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
        for (int seqID: seqIDs){
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
    
    public static FeatureVector getAverageFeatures(List sequences, Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, Map<Object,Double> sequencePerformanceMap){
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

    public static void saveSelectedSequencesFeaturesAndPerformance(List sequences, Map<Object, FeaturedTimeSequence<Object,PreferenceIF<Object,Object,ContinuousTimeContext>,Double>> map, Map<Object,Double> sequencePerformanceMap, String file, TimestampType type){
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
        for (int seqID: seqIDs){
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
            int seqID = Integer.parseInt(toks[0]);
            int user = Integer.parseInt(toks[1]);
            int item = Integer.parseInt(toks[2]);
            float rating = Float.parseFloat(toks[3]);
            long timestamp = Long.parseLong(toks[4]);
            double similarity = Double.parseDouble(toks[5]);
            ContinuousTimeContextIF ctx = new ContinuousTimeContext(timestamp);
            WeightedPreferenceIF<Integer, Integer, ContinuousTimeContextIF> pref = new WeightedExplicitPreference<Integer, Integer, ContinuousTimeContextIF>(similarity, user, item, ctx, rating);
            PredictionRatingsTimeSequence seq = (PredictionRatingsTimeSequence)seqs.get(seqID);
            if (seq == null){
//                seq = new PredictionRatingsTimeSequence(user, item, seqID, 0);
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
            int seqID = Integer.parseInt(toks[0]);
            int user = Integer.parseInt(toks[1]);
            int item = Integer.parseInt(toks[2]);
            float rating = Float.parseFloat(toks[3]);
            long timestamp = Long.parseLong(toks[4]);
            ContinuousTimeContextIF ctx = new ContinuousTimeContext(timestamp);
            PreferenceIF<Integer, Integer, ContinuousTimeContextIF> pref = new ExplicitPreference<Integer, Integer, ContinuousTimeContextIF>(user, item, ctx, rating);
            
            PredictionRatingsTimeSequence seq = (PredictionRatingsTimeSequence)seqs.get(seqID);
            if (seq == null){
//                seq = new PredictionRatingsTimeSequence(user, item, seqID, 0);
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
    
    private static Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> genNeighborsUsedRatingsSequences(Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet, ContextualModelUtils eTest){
        Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> ratingsSequenceMap = new HashMap<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>>();
        int totalPairs = useritemNeighborsusedMap.size();
        int pair = 0;
        for (ComparablePair<Object,Object> testPair : useritemNeighborsusedMap.keySet()){
            pair++;
            Object user = testPair.getKey();
            Object item = testPair.getValue();
            BasicTimeSequence tsTrain = new BasicTimeSequence(user.toString()+","+item.toString(), eTrain);
            BasicTimeSequence tsTest = new BasicTimeSequence(user, eTest);
            Map<Object,Float> neighborsUsed = useritemNeighborsusedMap.get(testPair);
            logger.log(Level.INFO, "Computing neighbors for test pair {0} out of {1} ({2},{3})", new Object[]{pair, totalPairs, user,item});

            List neighbors = new ArrayList(neighborsUsed.keySet());
            Collections.sort(neighbors);
            for (Object neighbor: neighbors){
                List<PreferenceIF> prefs = (List)trainSet.getPreferences(neighbor, item);
                if (prefs.size() > 1)  {
                    logger.log(Level.SEVERE, "Duplicated rating for test pair ({0},{1})", new Object[]{neighbor,item});
                    System.exit(1);
                }
                PreferenceIF pref = prefs.get(0);
                Map<String,String> neighborInfo = new HashMap<String,String>();
                neighborInfo.put("similarity", neighborsUsed.get(neighbor).toString());
                InformativeContext iCtx = new InformativeContext(neighborInfo, ((ContinuousTimeContext)pref.getContext()).getTimestamp() );
                tsTrain.AddElement(neighbor, item, pref.getValue(), iCtx);
            }
            tsTest.addElement(user, item, testSet);

            ratingsSequenceMap.put(user.toString()+","+item.toString(), new ComparablePair(tsTest, tsTrain));
        }
        return ratingsSequenceMap;
    }

    private static Map<String, String> genNeighborsUsedRatingsProfiles(TimeUtils.STATS[] stats, Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> pairRatingsMap, TimestampType type){
        Map<String, String> pairDataMap = new HashMap<String, String>();

        StringBuilder title = new StringBuilder();
        title.append("user,item");

        for (TimeUtils.STATS stat: stats) { title.append("\tratings").append(stat); }
        for (TimeUtils.STATS stat: stats) { title.append("\ttimestamps").append(stat); }
        pairDataMap.put("title", title.toString());

        TimeUtils utils = new TimeUtils();

        int totalPairs = pairRatingsMap.size();
        int pair = 0;
        for (String testPair : pairRatingsMap.keySet()){
            pair++;
            StringBuilder pairData = new StringBuilder();
            pairData.append(testPair);
            logger.log(Level.INFO, "Computing profile statistics for pair {0} out of {1} ({0})", new Object[] {pair, totalPairs, testPair});
            BasicTimeSequence ts = pairRatingsMap.get(testPair).getValue();
            // Ratings statistics
            double[] ratingValues = utils.getRatingsStatistics(stats, ts);
            int i = 0;
            for (TimeUtils.STATS stat: stats){
                pairData.append("\t").append(ratingValues[i++]);
            }

            // Timestamp statistics
            double[] timestampsValues = null;
            switch(type){
                case DAY:
                    timestampsValues = utils.getTimestampsStatistics(stats, ts);
                    break;
                case SECOND:
                    timestampsValues = utils.getDayTimestampsStatistics(stats, ts);
                    break;
            }
            i = 0;
            for (TimeUtils.STATS stat: stats){
                pairData.append("\t").append(timestampsValues[i++]);
            }
            pairDataMap.put(testPair, pairData.toString());
        }

        return pairDataMap;
    }

    /*
     * Generates a mapping from test ratings (pair user, item) to a map of ratings used to predict the rating
     * The keys of the map of ratings used for prediction correspond to the neighbors' identifier, 
     * and the values correspond to the similarity of that neighbor to the target user.
     */
    private static Map<ComparablePair<Object,Object>, Map<Object,Float>> genUsedRatingsInPrediction(Map<Object, List<SimilarityDatumIF>> userNeighborsMap, List<Integer> selectedUsers, int minNeighbors, ModelIF trainSet, ContextualModelUtils eTrain, ModelIF testSet){
        
        // StringBuilders init
        logger.log(Level.INFO, "Identifying used ratings in predictions");

        if (selectedUsers == null){
            selectedUsers = new ArrayList<Integer>(testSet.getUsers());
        }
        Collections.sort(selectedUsers);
        
        int totalUsers = selectedUsers.size();

        int i = 0;
        Map<ComparablePair<Object,Object>, Map<Object,Float>> useritemNeighborsusedMap = new HashMap<ComparablePair<Object,Object>, Map<Object,Float>>();
        for (int user: selectedUsers){
            i++;
            logger.log(Level.INFO, "identifying used ratings for user{0}({1} out of {2})", new Object[]{user, i, totalUsers});
            if (eTrain.getUserRatingCount(user) == 0) { continue; } // no training data for user
            List<PreferenceIF> testPrefs = new ArrayList(testSet.getPreferencesFromUser(user));
            List<SimilarityDatumIF> neighbors = userNeighborsMap.get(user);
            if (testPrefs == null || neighbors == null) { continue; } // no test ratings or neighbors data
            for (PreferenceIF pref: testPrefs){
                Integer item = (Integer)pref.getItem();
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


    private static Map<String, String> genNeighborsMeanFeaturesMap(Map<String, String> userFeaturesMap, Map<Object, List<SimilarityDatumIF>> userNeighborsMap, DatasetIF dataset, String protocol){
        Map<String,String> userNeighborsdataMap = new HashMap<String,String>();

        String title = userFeaturesMap.get("title");
        userNeighborsdataMap.put("title", title);
        String[] titleToks = title.split("\t");
        int length = titleToks.length;

        for (Object user: userNeighborsMap.keySet()){
            int n = 0;
            double[] values = new double[length];
            String[] userValues = userFeaturesMap.get(user.toString()).split("\t");
            for (SimilarityDatumIF sd: userNeighborsMap.get(user)){
                Integer neighbor = (Integer)sd.getKey();
                String neighborData = userFeaturesMap.get(neighbor.toString());
                if (neighborData == null) { continue; }
                String[] toks = neighborData.split("\t");
                for (int i = 1; i < length; i++){
                    try{
                        values[i] += Double.parseDouble(toks[i]);
                    } catch (NumberFormatException e){
                    }
                }
                n++;
            }

            StringBuilder meansData = new StringBuilder();

            // userID in position 0
            meansData.append(user);

            for (int i = 1; i < length; i++){
                if (titleToks[i].equalsIgnoreCase("RMSEDiff") || titleToks[i].equalsIgnoreCase("sign")){
                    meansData.append("\t").append(userValues[i]);
                }
                else{
                    values[i] /= (double)n;
                    meansData.append("\t").append(values[i]);
                }
            }
            userNeighborsdataMap.put(user.toString(), meansData.toString());
        }

        return userNeighborsdataMap;
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
    private static void addGenresToSequence(DatasetIF dataset, String protocol, String file, Map<Integer, Set<String>> itemGenresMap, TimestampType type) throws IOException{
        BufferedReader in;

        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" + file + "_Genres_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        // Sequence
        in = new BufferedReader(new FileReader(path + "analysis/" +  file + type + ".dat"));

        String title_line = in.readLine(); // Title line
        title_line += "\t" + "genre";
        ps.println(title_line);
        String line;
        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int item = Integer.parseInt(toks[1]);

            Set<String> itemGenres = itemGenresMap.get(item);
            if (itemGenres != null){
                for (String genre: itemGenres){
                    String outLine = line + "\t" + genre;
                    ps.println(outLine);
                }
            }
            else{
                String outLine = line + "\t" + "noGenre";
                ps.println(outLine);
            }
        }
        ps.flush();
        ps.close();
    }

    private static Map<Integer, Set<String>> computeItemGenres(String itemFile) throws IOException{
        Map<Integer,Set<String>> itemGenresMap = new HashMap<Integer,Set<String>>();

        BufferedReader in;
        in = new BufferedReader(new FileReader(path + "recommendations/" + itemFile));
        String line;
        while ( (line = in.readLine()) != null){
            String[] toks = line.split("::");
            int item = Integer.parseInt(toks[0]);

            Set itemGenres = itemGenresMap.get(item);
            if (itemGenres == null){
                itemGenres = new HashSet<String>();
            }
            String[] genres = toks[2].split("\\|");
            itemGenres.addAll(Arrays.asList(genres));
            itemGenresMap.put(item, itemGenres);
        }
        return itemGenresMap;
    }

    private static Map<String, String> addPredictionErrorToPair(Map<String, String> ratingsProfileMap, DatasetIF dataset, String protocol, String algorithm1, String algorithm2, String metric, ModelIF testSet) throws IOException{
        BufferedReader in;

        Map<Integer,Map<Integer,Float>> userItemPred1Map = new HashMap<Integer,Map<Integer,Float>>();
        Map<Integer,Map<Integer,Float>> userItemPred2Map = new HashMap<Integer,Map<Integer,Float>>();

        String longProtocol = protocolsMap.get(protocol);

        // Algorithm 1
        in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "__" + algorithm1 + "__" + longProtocol + "__recommendations.txt"));
        String line;
        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[0]);
            int item = Integer.parseInt(toks[2]);
            float pred = Float.parseFloat(toks[4]);

            // store test data
            Map<Integer, Float> itemPredMap = userItemPred1Map.get(user);
            if (itemPredMap == null){
                itemPredMap = new HashMap<Integer,Float>();
            }
            itemPredMap.put(item, pred);
            userItemPred1Map.put(user, itemPredMap);
        }
        in.close();

        // Algorithm 2
        in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "__" + algorithm2 + "__" + longProtocol + "__recommendations.txt"));
        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[0]);
            int item = Integer.parseInt(toks[2]);
            float pred = Float.parseFloat(toks[4]);

            // store test data
            Map<Integer, Float> itemPredMap = userItemPred2Map.get(user);
            if (itemPredMap == null){
                itemPredMap = new HashMap<Integer,Float>();
            }
            itemPredMap.put(item, pred);
            userItemPred2Map.put(user, itemPredMap);
        }
        in.close();


        String title_line = ratingsProfileMap.get("title"); // Title line
        title_line += "\t" + algorithm1 + metric + "\t" + algorithm2 + metric + "\t" + metric + "Diff";
        ratingsProfileMap.put("title",title_line);

        int userPos=0;
        int itemPos=1;
//        int i = 0;
//        for (String title_item:title_line.split("\t")){
//            if (title_item.equalsIgnoreCase("user")) userPos = i;
//            else if (title_item.equalsIgnoreCase("item")) itemPos = i;
//            i++;
//        }

        for (String key: ratingsProfileMap.keySet()){
            if (key.equalsIgnoreCase("title")) { continue; }
            // read data
            String data = ratingsProfileMap.get(key);
            String[] toks = data.split("\t");
            String[] toks0 = toks[0].split(",");
            int user = Integer.parseInt(toks0[userPos]);
            int item = Integer.parseInt(toks0[itemPos]);
            StringBuilder outLine = new StringBuilder();
            outLine.append(data);
            //Computes difference
            float rating = testSet.getPreferenceValue(user, item, null);
            float pred1 = userItemPred1Map.get(user).get(item);
            float pred2 = userItemPred2Map.get(user).get(item);
            double RMSE1 = Math.sqrt(Math.pow(rating - pred1, 2));
            double RMSE2 = Math.sqrt(Math.pow(rating - pred2, 2));
            double diff = RMSE1 - RMSE2;
            outLine.append("\t").append(RMSE1).append("\t").append(RMSE2).append("\t").append(diff);
            ratingsProfileMap.put(key,outLine.toString());
        }

        return ratingsProfileMap;
    }
    
    @SuppressWarnings("CallToThreadDumpStack")
    private static void addMetricsDiffToSequence(DatasetIF dataset, String protocol, String file, String destFile, String algorithm1, String algorithm2, TimestampType type) throws IOException{
        BufferedReader in;

        Map<Integer,Map<Integer,Float>> userItemRatingMap = new HashMap<Integer,Map<Integer,Float>>();
        Map<Integer,Map<Integer,Float>> userItemPred1Map = new HashMap<Integer,Map<Integer,Float>>();
        Map<Integer,Map<Integer,Float>> userItemPred2Map = new HashMap<Integer,Map<Integer,Float>>();

        // Algorithm 1
        String longProtocol = protocolsMap.get(protocol);

        in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "__" + algorithm1 + "__" +longProtocol + "__recommendations.txt"));
        String line;
        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[0]);
            int item = Integer.parseInt(toks[2]);
            float pred = Float.parseFloat(toks[4]);

            // store test data
            Map<Integer, Float> itemPredMap = userItemPred1Map.get(user);
            if (itemPredMap == null){
                itemPredMap = new HashMap<Integer, Float>();
            }
            itemPredMap.put(item, pred);
            userItemPred1Map.put(user, itemPredMap);
        }
        in.close();

        // Algorithm 2
        in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "__" + algorithm2 + "__" +longProtocol + "__recommendations.txt"));
        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[0]);
            int item = Integer.parseInt(toks[2]);
            float pred = Float.parseFloat(toks[4]);

            // store test data
            Map<Integer, Float> itemPredMap = userItemPred2Map.get(user);
            if (itemPredMap == null){
                itemPredMap = new HashMap<Integer, Float>();
            }
            itemPredMap.put(item, pred);
            userItemPred2Map.put(user, itemPredMap);
        }
        in.close();

        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" +  destFile + "_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        // Sequence
        in = new BufferedReader(new FileReader(path + "analysis/" + file + "_" + type + ".dat"));

        String title_line = in.readLine(); // Title line
        title_line += "\t" + algorithm1 + "\t" + algorithm2 + "\tdiff";
        ps.println(title_line);

        int userPos=0;
        int itemPos=1;
        int ratingPos=2;
        int typePos=4;
        int i = 0;
        for (String title_item:title_line.split("\t")){
            if (title_item.equalsIgnoreCase("user")) { userPos = i; }
            else if (title_item.equalsIgnoreCase("item")) { itemPos = i; }
            else if (title_item.equalsIgnoreCase("rating")) { ratingPos = i; }
            else if (title_item.equalsIgnoreCase("type")) { typePos = i; }
            i++;
        }

        while ( (line = in.readLine()) != null){
            // read data
            String[] toks = line.split("\t");
            int user = Integer.parseInt(toks[userPos]);
            int item = Integer.parseInt(toks[itemPos]);
            float rating = Float.parseFloat(toks[ratingPos]);
            String ratingType = toks[typePos];
            StringBuilder outLine = new StringBuilder();
            outLine.append(line);
            //Computes difference
            if (ratingType.equalsIgnoreCase("Tr")){
                outLine.append("\t").append(0).append("\t").append(0).append("\t").append(0);
            }
            else{
                float pred1 = userItemPred1Map.get(user).get(item);
                float pred2 = userItemPred2Map.get(user).get(item);
                double RMSE1 = Math.sqrt(Math.pow(rating - pred1, 2));
                double RMSE2 = Math.sqrt(Math.pow(rating - pred2, 2));
                double diff = RMSE1 - RMSE2;
                outLine.append("\t").append(RMSE1).append("\t").append(RMSE2).append("\t").append(diff);
            }
            ps.println(outLine);
        }
        ps.flush();
        ps.close();
    }
    

    @SuppressWarnings("CallToThreadDumpStack")
    private static void addFeatureToNeighborsRatingSequence(String file, String destFile, FEATURE feature, TimestampType type) throws IOException{
        BufferedReader in;


        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" +  destFile + "_Tr_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        String line;
        // Test data
        in = new BufferedReader(new FileReader(path + "analysis/" + file + "_Te_" + type + ".dat"));
        Map<Integer,String> testFeaturesMap = new HashMap<Integer,String>();
        String title_test = in.readLine(); // title line
        int seqIdx = substringPos(title_test, "seq", "\t");
        
        while ( (line = in.readLine()) != null){
            String[] toks = line.split("\t");
            Integer seq = Integer.parseInt(toks[seqIdx]);
            testFeaturesMap.put(seq, line);
        }

        // Sequence of training data
        in = new BufferedReader(new FileReader(path + "analysis/" + file + "_Tr_" + type + ".dat"));
        String title_line = in.readLine(); // title line
        title_line += "\t"+feature;
        ps.println(title_line);
        
        seqIdx = substringPos(title_line, "seq", "\t");
        while ( (line = in.readLine()) != null){
            String[] toks = line.split("\t");
            Integer seq = Integer.parseInt(toks[seqIdx]);
            String testFeatures = testFeaturesMap.get(seq);
            String featureValue = getFeatureValue(feature, line, title_line, testFeatures, title_test);
            line += "\t" + featureValue;
            ps.println(line);
        }
        ps.flush();
        ps.close();
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private static void pairSequenceMapToFile(Map<String, ComparablePair<BasicTimeSequence,BasicTimeSequence>> sequencesMap, DatasetIF dataset, String protocol, String file, TimestampType type){
        // StringBuilders init

        String titleTest = "seq\t" + BasicTimeSequence.getTitleStringWithUser();
        String titleTrain = "seq\t" + BasicTimeSequence.getExtendedTitleStringWithUser();
        PrintStream psTest = null;
        PrintStream psTrain = null;
        try {
            psTest = new PrintStream(new File(path + "analysis/" + file + "_Te_" + type + ".dat"));
            psTrain = new PrintStream(new File(path + "analysis/" + file + "_Tr_" + type + ".dat"));
        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        psTest.print(titleTest);
        psTrain.print(titleTrain);

        List<String> pairs = new ArrayList<String>(sequencesMap.keySet());
        Collections.sort(pairs);

        int i = 1;
        for (String pair:pairs){
            ComparablePair<BasicTimeSequence, BasicTimeSequence> tsPair = sequencesMap.get(pair);
            BasicTimeSequence tsTest = tsPair.getKey();
            BasicTimeSequence tsTrain = tsPair.getValue();
            String[] toks = pair.split(",");
            Integer user = Integer.parseInt(toks[0]);
            Integer item = Integer.parseInt(toks[1]);
            String testSequence = tsTest.getSequenceStringWithUser(i+"\t","");
            String trainSequence = tsTrain.getExtendedSequenceStringWithUser(i+"\t","");
            psTest.print(testSequence);
            psTrain.print(trainSequence);
            i++;
        }
        psTest.flush();
        psTrain.flush();
        psTest.close();
        psTrain.close();
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private static void PerUserBasicRatingsSequenceToFile(DatasetIF dataset, String protocol, List<Integer> users, String file, TimestampType type){
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        experimentInfo.append("Experiment:").append(dataset_name).append(", ").append("Ratings sequence").append(newline);

        SplitIF split = getSplit(dataset);
        ModelIF trainSet = split.getTrainingSet();
        ModelIF testSet  = split.getTestingSet();
        ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);
        ContextualModelUtils eTest = new ContextualModelUtils(testSet);

        String title = BasicTimeSequence.getTitleStringWithUser() + "\ttype";

        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" + file + "_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        ps.print(title);

        if (users != null && users.size() > 0){
            for (int user:users){
                BasicTimeSequence tsTrain = new BasicTimeSequence(user, trainSet, eTrain);
                BasicTimeSequence tsTest = new BasicTimeSequence(user, testSet, eTest);
//                String sequence = tsTrain.getTitleString() + tsTrain.getSequenceString("Tr") + tsTest.getSequenceString("Te");
                String sequence = tsTrain.getSequenceStringWithUser("","\tTr") + tsTest.getSequenceStringWithUser("","\tTe");
                ps.print(sequence);
            }
        }
        ps.flush();
        ps.close();

        System.out.println("Min Date:" + eTrain.getMinDate() + "Min TS:" + eTrain.getMinDate().getTime());
        System.out.println("Max Date:" + eTest.getMaxDate() + "Max TS:" + eTest.getMaxDate().getTime());
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

    @SuppressWarnings("CallToThreadDumpStack")
    private static void UserFeaturesWithClassMapToFile(Map<String,String> userFeaturesMap, DatasetIF dataset, String protocol, List<Integer> users, String file, TimestampType type){
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" + file+ "_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        String title_line = userFeaturesMap.get("title")+"\tsign";
        ps.println(title_line);

        List<String> keys = new ArrayList<String>();
        if (users != null){
            for (Integer user: users){
                keys.add(Integer.toString(user));
            }
        }
        else{
            for (String key: userFeaturesMap.keySet()){
                if (key.equalsIgnoreCase("title")) { continue; }
                keys.add(key);
            }
        }
        Collections.sort(keys);

        for (String key: keys){
            String data = userFeaturesMap.get(key);
            if (data == null){
                System.out.println("User " + key + " not found!");
                continue;
            }
            String[] toks = data.split("\t");
            Double diff = Double.parseDouble(toks[toks.length-1]);
            if (diff == 0.0 || Double.isNaN(diff)) { continue; }
            if (diff < 0){
                data += "\t-";
            }
            else{
                data += "\t+";
            }
            ps.println(data);
        }
        ps.flush();
        ps.close();
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private static void computePerUserProfileBasicFeatureVector(DatasetIF dataset, String protocol, TimeUtils.STATS[] stats, List<Integer> users, TimestampType type){
        // StringBuilders init
        final StringBuilder experimentInfo = new StringBuilder();
        experimentInfo.append("Experiment:").append(dataset_name).append(", ").append("Profile Statistics (").append(type).append(")").append(newline);

        SplitIF split = getSplit(dataset);
        ModelIF trainSet = split.getTrainingSet();
        ContextualModelUtils eTrain = new ContextualModelUtils(trainSet);


        TimeUtils utils = new TimeUtils();

        if (users == null){
            users = new ArrayList(trainSet.getUsers());
        }
        Collections.sort(users);

        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(path + "analysis/" + "basic_UserFeatures_" + type + ".dat"));

        } catch (FileNotFoundException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        ps.print("user");
        for (TimeUtils.STATS stat: stats) { ps.print("\tratings"+stat); }
        for (TimeUtils.STATS stat: stats) { ps.print("\ttimestamps"+stat); }

        for (Object user: users){
            // new line
            logger.log(Level.INFO, "Computing profile statistics for user {0}", user);
            ps.println();
            ps.print(user);

            // Ratings statistics
            double[] ratingValues = utils.getRatingsStatistics(stats, user, trainSet, eTrain);
            int i = 0;
            for (TimeUtils.STATS stat: stats){
                ps.print("\t" + ratingValues[i++]);
            }

            // Timestamps statistics
            double[] timestampsValues = null;
            switch(type){
                case DAY:
                    timestampsValues = utils.getDayTimestampsStatistics(stats, user, trainSet, eTrain);
                    break;
                case SECOND:
                    timestampsValues = utils.getTimestampsStatistics(stats, user, trainSet, eTrain);
                    break;
            }
            i = 0;
            for (TimeUtils.STATS stat: stats){
                ps.print("\t" + timestampsValues[i++]);
            }
        }
        ps.flush();
        ps.close();
    }

    public static List<Integer> getTopUserInColumn(Map<String,String> userFeaturesMap, String column, int n, boolean reverse) throws IOException{

        String title_line = userFeaturesMap.get("title");
        String[] title_toks = title_line.split("\t");
        int columnIdx = 0;
        for (int i = 0; i < title_toks.length; i++){
            if (title_toks[i].equalsIgnoreCase(column)){
                columnIdx = i;
            }
        }

        List<Integer> topUsers = new ArrayList<Integer>();
        List<ComparablePair<String,Double>> list = new ArrayList<ComparablePair<String,Double>>();
        List<String> users = new ArrayList(userFeaturesMap.keySet());
        Collections.sort(users);
        for (String user: users){
            if (user.equalsIgnoreCase("title")) { continue; }
            String data = userFeaturesMap.get(user);
            String[] toks = data.split("\t");
            Double diff = Double.parseDouble(toks[columnIdx]);
            if (Double.isNaN(diff)) { continue; }
            ComparablePair<String,Double> pair = new ComparablePair<String,Double>(user, diff);
            list.add(pair);
        }

        Collections.sort(list);

        if (reverse){
            Collections.reverse(list);
        }

        for (int i = 0; i < n; i++){
            topUsers.add(Integer.parseInt(list.get(i).getKey()));
        }

        return topUsers;
    }

    public static Map<String,String> addMetricDifference(Map<String,String> userFeaturesMap, String metric) throws IOException{

        Map<String, String> userFeaturesMapWithMetricDiff = new HashMap<String, String>();
        userFeaturesMapWithMetricDiff.put("title", userFeaturesMap.get("title") + "\t" + metric + "Diff");


        List<String> users = new ArrayList(userFeaturesMap.keySet());
        Collections.sort(users);
        for (String user: users){
            if (user.equalsIgnoreCase("title")) { continue; }
            String data = userFeaturesMap.get(user);
            String[] toks = data.split("\t");
            double m1 = Double.parseDouble(toks[toks.length-2]);
            double m2 = Double.parseDouble(toks[toks.length-1]);
            double diff = m1-m2;
            if (Double.isNaN(diff)) { continue; }
            data +=  "\t" + diff;
            userFeaturesMapWithMetricDiff.put(user, data);
        }

        return userFeaturesMapWithMetricDiff;
    }

    public static Map<String,String> addMetric(Map<String,String> userFeaturesMap, DatasetIF dataset, String algorithm, String protocol, String metric) throws IOException{
        logger.info("Loading recommendation metrics data");
        String longProtocol = protocolsMap.get(protocol);
        Map<String, String> userFeaturesMapWithMetricDiff;
        BufferedReader in = new BufferedReader(new FileReader(path + "recommendations/" + dataset + "__" + algorithm + "__"  + longProtocol + "__per_user_metrics.txt.tsv"));
        String metrics_title = in.readLine();
        String[] metrics = metrics_title.split("\t");
        int metricIndex=0;
        for (String m: metrics){
            if (m.equalsIgnoreCase(metric)){
                break;
            }
            metricIndex++;
        }
        userFeaturesMapWithMetricDiff = new HashMap<String, String>();
        userFeaturesMapWithMetricDiff.put("title", userFeaturesMap.get("title") + "\t" + algorithm + "_" + protocol + "_" + metrics[metricIndex]);
        String line;
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
            String user = toks[3];
            String data = userFeaturesMap.get(user);
            if (data == null){
                continue;
            }
            data = data + "\t" + toks[metricIndex];
            userFeaturesMapWithMetricDiff.put(user, data);
        }

//        logger.info("Filling missing data");
//        // Fill missing data
//        List<String> users = new ArrayList(userFeaturesMap.keySet());
//        Collections.sort(users);
//
//        String[] fields = userFeaturesMap.get("title").split("\t");
//        String null_fields = "";
//        null_fields = null_fields + "\tNaN";
//        for (String user: users){
//            if (user.equalsIgnoreCase("title")) continue;
//            String data = userFeaturesMap.get(user);
//            String[] toks = data.split("\t");
//            if (toks.length < fields.length){
//                data = data + null_fields;
//                userFeaturesMap.put(user, data);
//            }
//        }
        return userFeaturesMapWithMetricDiff;
    }

    public static Map<String,String> readUserFeatureVectorMap(DatasetIF dataset, String protocol, String file, TimestampType type) throws IOException{
        // Data structure to save data
        Map<String, String> userDataMap = new HashMap<String, String>();

        // Data load
        logger.info("Loading profile statistics data");
        BufferedReader in = new BufferedReader(new FileReader(path + "analysis/" +  file + "_" + type + ".dat"));
        String line;
        userDataMap.put("title", in.readLine());
        while ((line = in.readLine()) != null) {
            String[] toks = line.split("\t");
            String user = toks[0];
            String data = line;
            userDataMap.put(user, data);
        }

        return userDataMap;
    }

    private static SplitIF getSplit(DatasetIF dataset){
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
        splitter = new CommonDatasetSplitters<Object, Object, ContinuousTimeContextIF>().proportion(testProportion).size(testSize).dataset(dataset).getDatasetSplitter(split_method);
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
}
