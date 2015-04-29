package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author Pedro G. Campos
 */
public class EM_ImplicitAlgorithm<U,I,C extends ContextIF> extends AbstractFactorizationAlgorithm<U,I,C>{
    private double initNoise = 0.005;

    public EM_ImplicitAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean WITH_BIAS) {
        double initValue=Math.sqrt((defaultValue-1.0)/(double)nFeatures);
        this.recommenderModel=new FactorRatedModel(model, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, WITH_BIAS, initNoise);
        this.eModel=statistics;
        prefs=new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:model.getUsers()){
            prefs.addAll(model.getPreferencesFromUser(user));
        }
    }

    public Float getEstimationByIndex(int userIndex, int itemIndex, C context) {
        double sum=Double.NaN;
        if (userIndex>=0 && itemIndex>=0){
            FactorRatedModel theModel = (FactorRatedModel)this.recommenderModel;
            sum=0.0;//this.recommenderModel.getMinRating();
            Collection<I> ratedItems=eModel.getItemsRatedBy((U)theModel.getKeysAndIndexList().getUserKey(userIndex));
            double Ru=Math.sqrt(ratedItems.size());
            for (int i=0;i<theModel.getnFeatures();i++){
                double ratedFactorsSum=0.0;
                for (I ratedItem:ratedItems){
                    int ratedItemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(ratedItem);
                    ratedFactorsSum+=((FactorRatedModel)recommenderModel).getRatedFactors(i, ratedItemIndex);
                }
                sum+=theModel.getItemFactors(i,itemIndex)*(ratedFactorsSum/Ru);
            }
            if (theModel.isWithBias())
                sum+=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
        }
        return (float)sum;
    }

    public void trainFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> data, double error) {
        FactorRatedModel theModel = (FactorRatedModel)this.recommenderModel;
        
        //double userValue=theModel.getUserFactors(factor, userIndex);
        double itemValue=theModel.getItemFactors(factor, itemIndex);
        Collection<I> ratedItems=eModel.getItemsRatedBy((U)recommenderModel.getKeysAndIndexList().getUserKey(userIndex));
        double Ru=Math.sqrt(ratedItems.size());
        double ratedFactorsSum=0.0;
        double dPu=0.0;
        for (I ratedItem: ratedItems){
            int ratedItemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(ratedItem);
            ratedFactorsSum+=((FactorRatedModel)recommenderModel).getRatedFactors(factor, ratedItemIndex);
            dPu+=itemValue/Ru;
        }
        double pu=ratedFactorsSum/Ru;
        //theModel.setUserFactors(factor,userIndex, theModel.getUserFactors(factor, userIndex)+theModel.getLearningRate()*(error*itemValue-theModel.getLambda()*userValue));
        theModel.setItemFactors(factor,itemIndex, theModel.getItemFactors(factor, itemIndex)+theModel.getLearningRate()*(error*pu-theModel.getLambda()*itemValue));
        for (I ratedItem: ratedItems){
            int ratedItemIndex=recommenderModel.getKeysAndIndexList().getItemIndex(ratedItem);
            theModel.setRatedFactors(factor, ratedItemIndex, theModel.getRatedFactors(factor, ratedItemIndex)+theModel.getLearningRate()*(error*dPu-theModel.getLambda()*ratedFactorsSum));
        }
        if (theModel.isWithBias()){
            theModel.setUserBias(userIndex, theModel.getUserBias(userIndex) + theModel.getLRStaticBias() * ( error - theModel.getLambdaStaticBias() * theModel.getUserBias(userIndex)) );
            theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex) + theModel.getLRStaticBias() * ( error - theModel.getLambdaStaticBias() * theModel.getItemBias(itemIndex)) );
        }
    }
    
    public void trainBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> data, double error) {
        FactorRatedModel theModel = (FactorRatedModel)this.recommenderModel;
        if (theModel.isWithBias()){
            theModel.setUserBias(userIndex, theModel.getUserBias(userIndex) + theModel.getLRStaticBias() * ( error - theModel.getLambdaStaticBias() * theModel.getUserBias(userIndex)) );
            theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex) + theModel.getLRStaticBias() * ( error - theModel.getLambdaStaticBias() * theModel.getItemBias(itemIndex)) );
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
        return "Implicit ExpectationMaximization Factorization";
    }
}
