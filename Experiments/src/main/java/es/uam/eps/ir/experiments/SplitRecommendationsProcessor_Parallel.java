package es.uam.eps.ir.experiments;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.metrics.MetricIF;
import es.uam.eps.ir.metrics.RecommendationIF;
import es.uam.eps.ir.nonpersonalized.NonPersonalizedPrediction;
import es.uam.eps.ir.rank.CandidateItemsIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author pedro
 */
public class SplitRecommendationsProcessor_Parallel<U,I,C extends ContextIF> extends SplitRecommendationsProcessor<U,I,C>{
    private int maxThreads = 6;

    public SplitRecommendationsProcessor_Parallel() {
    }
    
    public SplitRecommendationsProcessor_Parallel(int maxThreads) {
        this.maxThreads = maxThreads;
    }
    
    @Override
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
        
        // Queue for threads' communication
        final BlockingQueue<UserRecommendations<U,I>> queue = new ArrayBlockingQueue(1000, true);
        
        // Thread pool (at least 2 threads are required)
        ExecutorService threadPool = null;
        if (maxThreads > 1) {
            threadPool = Executors.newFixedThreadPool(maxThreads);
        }
        else{
            threadPool = Executors.newFixedThreadPool(2);            
        }
        logger.log(Level.INFO, "created thread pool of size {0}: {1}", new Object[]{maxThreads, threadPool.toString()});
        
        // One thread dedicated to metrics computing
        if (threadPool != null) {
            final MetricsThread mc= new MetricsThread(rankingMetricsComputer, errorMetricsComputer, queue, tUsers, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo);
            threadPool.execute(mc);
        }
        
        int nUser = 0;
        // And then process each user
        Collection<U> users = new ArrayList(testSet.getUsers());
        Collections.sort((List)users);
//        Collection<U> users = (Collection<U>)Arrays.asList(33,42,87,192,195,368,462,495,546,578);
//        Collection<U> users = (Collection<U>)Arrays.asList(192);
        for (U user:users){
            nUser++;
                        
            // Start thread for computing user's recommendations
            final UserRecommendationsThread uc= new UserRecommendationsThread(recommender, user, nUser, tUsers, candidateItems, nonPersonalized, defaultContext, trainingSet, testSet, queue, controlPredictionValue);
            if (threadPool == null) {
                uc.run();
            } else {
                threadPool.execute(uc);
            }
//            if (nUser >= 10) break;
        }
        
        // Wait for threads' termination
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                while (!threadPool.awaitTermination(10 * 24 * 60 * 60, TimeUnit.SECONDS)) {
                    System.err.println("Try again!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        logger.info("All recommendations computed");
    }
    
    private class UserRecommendationsThread<U,I,C extends ContextIF> extends TimerTask{
        
        private final RecommenderIF<U,I,C> recommender;
        private final U user;
        private final ModelIF<U,I,C> testSet;
        
        private final BlockingQueue<UserRecommendations<U,I>> queue;
        
        private final int nUser;
        private final int tUsers;
        private final CandidateItemsIF<U,I,C> candidateItems;
        private final NonPersonalizedPrediction<U,I,C> nonPersonalized;
        private final boolean controlPredictionValue;
        
        public UserRecommendationsThread(
                final RecommenderIF<U,I,C> recommender, 
                final U user,
                final int nUser,
                final int tUsers,
                final CandidateItemsIF<U,I,C> candidateItems,
                final NonPersonalizedPrediction nonPersonalized,
                final C defaultContext,
                final ModelIF<U,I,C> trainingSet,
                final ModelIF<U,I,C> testSet,
                final BlockingQueue<UserRecommendations<U,I>> queue,
                final boolean controlPredictionValue) {
            this.recommender = recommender;
            this.user = user;
            this.testSet=testSet;
            this.queue=queue;            
            this.nUser = nUser;
            this.tUsers = tUsers;
            this.candidateItems = candidateItems;
            this.nonPersonalized = nonPersonalized;
            this.controlPredictionValue = controlPredictionValue;
        }
        
        public void run() {
            UserProcessor processor = new UserProcessor();
//            List<RecommendationIF<I>> userRecommendations = processor.processUser(recommender, user, nUser, tUsers, candidateItems, nonPersonalized, testSet, controlPredictionValue);
            List<List<RecommendationIF<I>>> userRecommendations = processor.processUser(recommender, user, nUser, tUsers, candidateItems, nonPersonalized, trainingSet, testSet, controlPredictionValue);
            try {
                queue.put(new UserRecommendations(user, userRecommendations));
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Problem with queue! : {0}", ex);                
            }
        }
    }    

    /*
     * Class for temporal storing of user recommendations
     */
    private class UserRecommendations<U,I>{
        U userID;
//        List<RecommendationIF<I>> userRecommendations;
        List<List<RecommendationIF<I>>> userRecommendations;

//        public UserRecommendations(U userID, List<RecommendationIF<I>> userRecommendations) {
        public UserRecommendations(U userID, List<List<RecommendationIF<I>>> userRecommendations) {
            this.userID = userID;
            this.userRecommendations = userRecommendations;
        }

        public U getUserID() {
            return this.userID;
        }

//        public List<RecommendationIF<I>> getUserRecommendations() {
        public List<List<RecommendationIF<I>>> getUserRecommendations() {
            return this.userRecommendations;
        }
    }
    
    
    private class MetricsThread<U,I,C extends ContextIF> extends TimerTask{                
        private final MetricIF<U,I,C> rankingMetricsComputer[];
        private final MetricIF<U,I,C> errorMetricsComputer[];
        private final BlockingQueue<UserRecommendations<U,I>> queue;
        private final CandidateItemsIF<U,I,C> candidateItems;
        private final int tUsers;
        private final boolean SAVE_DETAILED_RESULTS;
        private final List<StringBuilder> recommendationsInfo;
        private final StringBuilder relevantsInfo;
        
        public MetricsThread(
                final MetricIF<U,I,C> rankingMetricsComputer[],
                final MetricIF<U,I,C> errorMetricsComputer[],
                final BlockingQueue<UserRecommendations<U,I>> queue,
                final int tUsers,
                final CandidateItemsIF<U,I,C> candidateItems,
                final boolean SAVE_DETAILED_RESULTS,
                final List<StringBuilder> recommendationsInfo,
                final StringBuilder relevantsInfo) {
            this.rankingMetricsComputer = rankingMetricsComputer;
            this.errorMetricsComputer = errorMetricsComputer;
            this.queue = queue;
            this.candidateItems = candidateItems;
            this.tUsers = tUsers;
            this.SAVE_DETAILED_RESULTS = SAVE_DETAILED_RESULTS;
            this.recommendationsInfo = recommendationsInfo;
            this.relevantsInfo = relevantsInfo;
        }

        @Override
        public void run() {
            int nUser=0;
            MetricsProcessor processor = new MetricsProcessor();
            try {
                while (nUser < tUsers){
                    nUser++;
                    UserRecommendations<U,I> userRecommendations=this.queue.take();
                    U user=userRecommendations.getUserID();
//                    List<RecommendationIF<I>> recommendations= userRecommendations.getUserRecommendations();
                    List<List<RecommendationIF<I>>> recommendations= userRecommendations.getUserRecommendations();
                    
                    processor.processMetrics(user, nUser, tUsers, rankingMetricsComputer, errorMetricsComputer, candidateItems, SAVE_DETAILED_RESULTS, recommendationsInfo, relevantsInfo, recommendations);
//                    if (nUser >= 10) break;
                }
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Problem with queue! : {0}", ex);
                ex.printStackTrace();
                System.exit(1);
            }
            catch (Exception e){
                logger.log(Level.SEVERE, "Other Problem with queue! : " + e);
                e.printStackTrace();
                System.exit(1);
            }
        }
    }    
}
