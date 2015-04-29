package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author Pedro G. Campos
 */
public class ParallelRechtReExpectationMaximizationAlgorithm<U,I,C extends ContextIF> extends AbstractFactorizationAlgorithm<U,I,C>{
    private double initNoise=0.005;

    public ParallelRechtReExpectationMaximizationAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS){
        double initValue=defaultValue;//Math.sqrt((defaultValue-1.0)/(double)nFeatures);
        this.recommenderModel=new FactorModel(model, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS, initNoise);
        this.eModel=eModel;
        prefs=new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:model.getUsers()){
            prefs.addAll(model.getPreferencesFromUser(user));
        }
        
        if (WITH_BIAS){
            this.ratingMean=eModel.getMeanRating();
        }
    }
    
    // parallel training. Based on Recht & Ré, 2011, "Parallel Stochastic Gradient Algorithms for Large-Scale Matrix Completion"
    @Override
    public synchronized boolean train(){
        logger.info("Starting training");
        
        RandomGenerator rg = new JDKRandomGenerator();
        rg.setSeed(0);
        RandomData d = new RandomDataImpl(rg);
        eModel.getMeanRating();
        
        for (int n=0; n < recommenderModel.getIterationSteps();n++){//
            logger.log(Level.INFO, "Train Iteration{0}", (n+1));            
            int maxThreads = 4;
            int l = maxThreads;
            
            Map<Integer,Map<Integer,List<PreferenceIF<U,I,C>>>> chunks = new HashMap<Integer,Map<Integer,List<PreferenceIF<U,I,C>>>>();
            for (int i = 1; i <= l; i++){
                Map<Integer, List<PreferenceIF<U,I,C>>> chunk = new HashMap<Integer,List<PreferenceIF<U,I,C>>>();
                for (int j = 1; j<= l; j++){
                    chunk.put(j, new ArrayList<PreferenceIF<U,I,C>>());
                }
                chunks.put(i, chunk);
            }
            Map<Integer, List<List<PreferenceIF<U,I,C>>>> round = new HashMap<Integer,List<List<PreferenceIF<U,I,C>>>>();
            for (int i = 1; i <= l; i++){
                round.put(i, new ArrayList<List<PreferenceIF<U,I,C>>>());
            }
            
            
            int nUsers = recommenderModel.getDim1MaxIndex();
            int nItems = recommenderModel.getDim2MaxIndex();
            int[] piUsers = d.nextPermutation(nUsers, nUsers);
            int[] piItems = d.nextPermutation(nItems, nItems);
            
            // A training iteration, with data randomly sorted
//            Collections.shuffle(prefs, random);
            for (PreferenceIF<U,I,C> pref: prefs){
                int userIndex=recommenderModel.getKeysAndIndexList().getUserIndex(pref.getUser());
                int itemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(pref.getItem());
                int a = (int) ( ( l/(double)recommenderModel.getKeysAndIndexList().getUserMaxIndex() ) * (piUsers[userIndex] - 1)) + 1;
                int b = (int) ( ( l/(double)recommenderModel.getKeysAndIndexList().getItemMaxIndex() ) * (piItems[itemIndex] - 1)) + 1;
                
//                System.out.println(a+","+b);
                chunks.get(a).get(b).add(pref);
            }
            
            for (int u = 1; u <= l; u++){
                for (int a = 1; a <= l; a++){
                    int b = a + u % l;
                    List<List<PreferenceIF<U,I,C>>> lPrefs = round.get(u);
                    if (lPrefs == null){
                        lPrefs = new ArrayList<List<PreferenceIF<U,I,C>>>();
                    }
                    lPrefs.add(chunks.get(a).get(b));
                    round.put(u, lPrefs);
                }
            }
            
            for (int u = 1; u <= l; u++){
                ExecutorService threadPool = null;
                if (maxThreads > 1) {
                    threadPool = Executors.newFixedThreadPool(maxThreads);
                }
                
                for (List<PreferenceIF<U,I,C>> lPrefs:round.get(u)){
                    if (threadPool != null) {
                        final TrainThreadRechtRe tt= new TrainThreadRechtRe(lPrefs);
                        threadPool.execute(tt);
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
            }
            
            // Learning rate update
//            updateLearningRates();
        }
        logger.info("Training finished");
        return true;
    }
    

    public Float getEstimationByIndex(int userIndex, int itemIndex, C context){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        double sum=Double.NaN;
        if (userIndex>=0 && itemIndex>=0){
            sum=0.0;//this.ratingMean;
            for (int i=0;i<theModel.getnFeatures();i++)
                sum+=theModel.getUserFactors(i,userIndex)*theModel.getItemFactors(i,itemIndex);
            if (theModel.isWithBias()){
                sum+=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
            }
        }
        return (float)sum;
    }
    
    public void trainFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        double userValue=theModel.getUserFactors(factor, userIndex);
        double itemValue=theModel.getItemFactors(factor, itemIndex);
        
        int Omega_i = eModel.getItemsRatedBy(pref.getUser()).size();
        int Omega_j = eModel.getUsersWhomRate(pref.getItem()).size();
        
        double lr = theModel.getLearningRate();
        double lambda = theModel.getLambda();
        
        theModel.setUserFactors(factor,userIndex, (1.0-(lr*lambda/(double)Omega_i)) * userValue - lr*(error*itemValue - lambda*userValue)*itemValue);
        theModel.setItemFactors(factor,itemIndex, (1.0-(lr*lambda/(double)Omega_j)) * itemValue - lr*(error*userValue - lambda*itemValue)*userValue);
    }
    
    public void trainBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        if (theModel.isWithBias()){
            theModel.setUserBias(userIndex, theModel.getUserBias(userIndex)+theModel.getLRStaticBias()*(error-theModel.getLambdaStaticBias()*theModel.getUserBias(userIndex)));
            theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex)+theModel.getLRStaticBias()*(error-theModel.getLambdaStaticBias()*theModel.getItemBias(itemIndex)));
        }
    }
    

    @Override
    public void updateLearningRates() {
            FactorModel theModel = (FactorModel)this.recommenderModel;    
            theModel.setLearningRate(theModel.getLearningRate()*0.9);
            theModel.setLr_static_bias(theModel.getLRStaticBias()*0.9);
    }
    

    @Override
    public String algorithmDescription() {
        return "ExpectationMaximization Factorization";
    }

    private class TrainThreadRechtRe extends TimerTask{
        private List<PreferenceIF<U,I,C>> preferences;

        public TrainThreadRechtRe(final List<PreferenceIF<U, I, C>> preferences) {
            this.preferences = preferences;
        }
        
        public void run(){
            if (preferences == null) return;
            for (PreferenceIF<U,I,C> pref: preferences){
                int userIndex = recommenderModel.getKeysAndIndexList().getUserIndex(pref.getUser());
                int itemIndex = recommenderModel.getKeysAndIndexList().getItemIndex(pref.getItem());
                double rating = pref.getValue();
                double prediction = getEstimationByIndex(userIndex, itemIndex, pref.getContext());
                double error = (rating-prediction);
                
                trainStep(userIndex, itemIndex, pref, error);
            }
        }
    }
    
}
