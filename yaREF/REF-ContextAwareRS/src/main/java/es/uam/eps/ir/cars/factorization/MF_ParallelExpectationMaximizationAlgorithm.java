package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public class MF_ParallelExpectationMaximizationAlgorithm<U,I,C extends ContextIF> extends AbstractParallelFactorizationAlgorithm<U,I,C>{
    private double initNoise = 0.005;

    public MF_ParallelExpectationMaximizationAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS, int maxThreads){
        double initValue = defaultValue;//Math.sqrt((defaultValue-1.0)/(double)nFeatures);
        this.recommenderModel = new FactorModel(model, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS, initNoise);
        this.eModel = eModel;
        this.maxThreads = maxThreads;        
        if (WITH_BIAS){
            this.ratingMean = eModel.getMeanRating();
        }
    }
        
    public Float getEstimationByIndex(int userIndex, int itemIndex, C context){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        double sum = Double.NaN;
        if (userIndex >= 0 && itemIndex >= 0){
            sum = 0.0;//this.ratingMean;
            for (int i = 0; i<theModel.getnFeatures(); i++)
                sum += theModel.getUserFactors(i,userIndex) * theModel.getItemFactors(i,itemIndex);
            if (theModel.isWithBias()){
                sum += ratingMean+theModel.getUserBias(userIndex) + theModel.getItemBias(itemIndex);
            }
        }
        return (float)sum;
    }
            
    public void trainUserFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        double userValue = theModel.getUserFactors(factor, userIndex);
        double itemValue = theModel.getItemFactors(factor, itemIndex);
        theModel.setUserFactors(factor,userIndex, userValue+theModel.getLearningRate()*(error*itemValue-theModel.getLambda()*userValue));
    }
    
    public void trainItemFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        double userValue = theModel.getUserFactors(factor, userIndex);
        double itemValue = theModel.getItemFactors(factor, itemIndex);
        theModel.setItemFactors(factor,itemIndex, itemValue+theModel.getLearningRate()*(error*userValue-theModel.getLambda()*itemValue));
    }
    
    
    public void trainUserBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        if (theModel.isWithBias()){
            theModel.setUserBias(userIndex, theModel.getUserBias(userIndex)+theModel.getLRStaticBias()*(error-theModel.getLambdaStaticBias()*theModel.getUserBias(userIndex)));
        }
    }
    
    public void trainItemBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        if (theModel.isWithBias()){
            theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex)+theModel.getLRStaticBias()*(error-theModel.getLambdaStaticBias()*theModel.getItemBias(itemIndex)));
        }
    }
    

    @Override
    public void updateLearningRates() {
            FactorModel theModel = (FactorModel)this.recommenderModel;    
            theModel.setLearningRate(theModel.getLearningRate() * LR_updateFactor);
            theModel.setLr_static_bias(theModel.getLRStaticBias() * LR_updateFactor);
    }
    

    @Override
    public String algorithmDescription() {
        return "ExpectationMaximization Factorization";
    }
}
