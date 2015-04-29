package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;

/**
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 */
public class BiasAlgorithm<U,I,C extends ContextIF> extends AbstractTrainingAlgorithm<U,I,C>{
    private double initNoise = 0.005;
    
    public BiasAlgorithm(ModelIF<U,I,C> dataModel, ContextualModelUtils<U,I,C> eModel, double defaultValue, int iterationSteps, double lrate, double lambda){
        double initValue=Math.sqrt(defaultValue-1.0);
        
        this.recommenderModel=new BiasModel(dataModel, iterationSteps, initValue, lrate, lambda, initNoise);
        this.eModel=eModel;
        prefs=new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:dataModel.getUsers()){
            prefs.addAll(dataModel.getPreferencesFromUser(user));
        }
        
        this.ratingMean=eModel.getMeanRating();
    }

    public void trainStep(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double error){
        BiasModel theModel = (BiasModel)recommenderModel;
        theModel.setUserBias(userIndex, theModel.getUserBias(userIndex)+theModel.getLearningRate()*(error-this.recommenderModel.getLambda()*theModel.getUserBias(userIndex)));
        theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex)+theModel.getLearningRate()*(error-this.recommenderModel.getLambda()*theModel.getItemBias(itemIndex)));
    }

    public Float getEstimationByIndex(int userIndex, int itemIndex, C context){
        BiasModel theModel = (BiasModel)recommenderModel;
        double sum=Double.NaN;
        if (userIndex>=0 && itemIndex>=0){
            sum=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
        }
        return (float)sum;
    }

    @Override
    public void updateLearningRates() {
        this.recommenderModel.setLearningRate(this.recommenderModel.getLearningRate()*0.9);
    }
    
    

    @Override
    public String algorithmDescription() {
        return "Bias_initNoise="+initNoise;
    }
}
