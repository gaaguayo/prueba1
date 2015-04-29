package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.recommender.AbstractRecommenderModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro G. Campos
 */
public abstract class AbstractTrainingAlgorithm<U,I,C extends ContextIF> {
    protected AbstractRecommenderModel recommenderModel;
    protected ContextualModelUtils<U,I,C> eModel;
    protected double ratingMean;
    protected static final Logger logger = Logger.getLogger("ExperimentLog");
    protected List<PreferenceIF<U,I,C>> prefs;
    protected Random random = new Random(0);
    protected double LR_updateFactor = 0.9;
    protected final boolean updateLearningRates = false;

    // Training error
    protected double cummError;
    protected final boolean computeTrainingError = false;

    public abstract void trainStep(int dim1Index, int dim2Index, PreferenceIF<U,I,C> pref, double error);
    
    public boolean train(){
        logger.info("Starting training");
        
        int n = 0;
        double previousRMSE = Double.MAX_VALUE;
        double RMSE = 0.0;
        while (true){
            logger.log(Level.INFO, "Train Iteration{0}", (n+1));
            if (computeTrainingError){
                cummError = 0.0;
            }
            
            // A training iteration, with data randomly sorted
            Collections.shuffle(prefs, random);
            for (PreferenceIF<U,I,C> pref: prefs){
                int userIndex=recommenderModel.getKeysAndIndexList().getUserIndex(pref.getUser());
                int itemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(pref.getItem());
                double rating=pref.getValue();
                double prediction=this.getEstimationByIndex(userIndex, itemIndex, pref.getContext());
                double error=(rating-prediction);
                
                trainStep(userIndex, itemIndex, pref, error);

                // Training error
                if (computeTrainingError){
                    cummError += error * error;
                }
            }
            
            if (computeTrainingError){
                RMSE = Math.sqrt(cummError / (double)prefs.size());
                System.out.println("Training RMSE ("+ (n+1) +")\t" + RMSE );
            }
            
            // Learning rate update
            if (updateLearningRates){
                updateLearningRates();
            }
            
            n++;
            if (n >= recommenderModel.getIterationSteps()){ break; }
            else { if (computeTrainingError && n > 20 && RMSE > previousRMSE - recommenderModel.getMinImprovement()) { break; }}
            previousRMSE = RMSE;
        }
        logger.info("Training finished");
        return true;
    }

    public Float getEstimation(U user, I item, C context){
        int userIndex=recommenderModel.getKeysAndIndexList().getUserIndex(user);
        int itemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(item);
        if (userIndex >= 0 && itemIndex >= 0){
            return getEstimationByIndex(userIndex, itemIndex, context);
        }
        else{
            return Float.NaN;
        }
    }

    public abstract Float getEstimationByIndex(int userIndex, int itemIndex, C context);

    @Override
    public String toString(){
        return this.algorithmDescription()+"[updateLR="+updateLearningRates+"("+LR_updateFactor+"), "+ this.recommenderModel.toString()+"]";
    }

    public abstract String algorithmDescription();
    public abstract void updateLearningRates();    
}
