package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Pedro G. Campos
 */
public abstract class AbstractParallelTrainingAlgorithm<U,I,C extends ContextIF> extends AbstractTrainingAlgorithm<U,I,C>{
    protected int maxThreads;

    // parallel training. Based on Luo et al., 2011, "A parallel matrix factorization based recommender by alternating stochastic gradient decent"
    @Override
    public synchronized boolean train(){
        logger.info("Starting training");
        List<U> users = new ArrayList<U>(recommenderModel.getDataModel().getUsers());
        List<I> items = new ArrayList<I>(recommenderModel.getDataModel().getItems());
        for (int n=0; n < recommenderModel.getIterationSteps();n++){//
            logger.log(Level.INFO, "Train Iteration{0}", (n+1));
            if (computeTrainingError){
                cummError = 0.0;
            }
            
            // Creates a thread pool
            ExecutorService threadPool = null;
            if (maxThreads > 1) {
                threadPool = Executors.newFixedThreadPool(maxThreads);
            }
            
            // first, process users
            Collections.shuffle(users, random);
            for (U u: users){
                if (threadPool != null) {
                    final UserTrainThread utt= new UserTrainThread(u, recommenderModel.getDataModel().getPreferencesFromUser(u));
                    threadPool.execute(utt);
                }                                    
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

            if (maxThreads > 1) {
                threadPool = Executors.newFixedThreadPool(maxThreads);
            }
            // then, process items
            Collections.shuffle(items, random);
            for (I i: items){
                if (threadPool != null) {
                    final ItemTrainThread itt= new ItemTrainThread(i, recommenderModel.getDataModel().getPreferencesFromItem(i));
                    threadPool.execute(itt);
                }                                    
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
            
            // Learning rate update
            if (updateLearningRates){
                updateLearningRates();
            }
        }
        logger.info("Training finished");
        return true;
    }    
    
    protected abstract void userTrainStep(int userIndex, int itemIndex,  PreferenceIF<U, I, C> pref, double error);
    
    protected abstract void itemTrainStep(int userIndex, int itemIndex,  PreferenceIF<U, I, C> pref, double error);

    public abstract Float getEstimationByIndex(int userIndex, int itemIndex, C context);
    
    @Override
    public String toString(){
        return "Parallel"+this.algorithmDescription()+"[updateLR="+updateLearningRates+"("+LR_updateFactor+"), "+ this.recommenderModel.toString()+"]";
    }    

    @Override
    public void trainStep(int dim1Index, int dim2Index, PreferenceIF<U, I, C> pref, double error) {
        throw new UnsupportedOperationException("Not supposed to be called in parallel implementation!.");
    }

    
    private class UserTrainThread extends TimerTask{
        private U user;
        private Collection<PreferenceIF<U,I,C>> preferences;

        public UserTrainThread(U user, final Collection<PreferenceIF<U, I, C>> preferences) {
            this.user = user;
            this.preferences = preferences;
        }
        
        public void run(){
            if (preferences == null) return;
            int userIndex = recommenderModel.getKeysAndIndexList().getUserIndex(user);
            for (PreferenceIF<U,I,C> pref: preferences){
                int itemIndex = recommenderModel.getKeysAndIndexList().getItemIndex(pref.getItem());
                double rating = pref.getValue();
                double prediction = getEstimationByIndex(userIndex, itemIndex, pref.getContext());
                double error = (rating-prediction);
                
                userTrainStep(userIndex, itemIndex, pref, error);
            }
        }
    }
    
    private class ItemTrainThread extends TimerTask{
        private I item;
        private Collection<PreferenceIF<U,I,C>> preferences;

        public ItemTrainThread(I item, final Collection<PreferenceIF<U, I, C>> preferences) {
            this.item = item;
            this.preferences = preferences;
        }
        
        public void run(){
            if (preferences == null) return;
            int itemIndex = recommenderModel.getKeysAndIndexList().getItemIndex(item);
            for (PreferenceIF<U,I,C> pref: preferences){
                int userIndex = recommenderModel.getKeysAndIndexList().getUserIndex(pref.getUser());
                double rating = pref.getValue();
                double prediction = getEstimationByIndex(userIndex, itemIndex, pref.getContext());
                double error = (rating-prediction);
                
                itemTrainStep(userIndex, itemIndex, pref, error);
            }
        }
    }
}
