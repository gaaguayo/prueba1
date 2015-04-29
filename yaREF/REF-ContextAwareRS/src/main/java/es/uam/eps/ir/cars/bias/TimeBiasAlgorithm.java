package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 */
public class TimeBiasAlgorithm<U,I,C extends ContinuousTimeContextIF> extends AbstractTrainingAlgorithm<U,I,C>{
    protected Long initialTime;
    protected double initNoise = 0.005;
    
    public TimeBiasAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> statistics, double defaultValue, int iterationSteps, double lrate, double lambda, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, int dim1BiasBins, int dim2BiasBins){
        double initValue=Math.sqrt(defaultValue-1.0);
        this.recommenderModel=new TimeBiasModel(model, statistics, iterationSteps, initValue, lrate, lambda, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, initNoise, dim1BiasBins, dim2BiasBins);
        this.eModel=statistics;
        this.ratingMean=statistics.getMeanRating();
        this.initialTime = statistics.getMinDate().getTime();
        
        // List of preferences
        prefs=new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:model.getUsers()){
            prefs.addAll(model.getPreferencesFromUser(user));
        }
    }



    public void trainStep(int userIndex, int itemIndex, PreferenceIF<U,I,C> data, double err){
        Long prefTime = data.getContext().getTimestamp();
        Date date = new Date(prefTime);
        
        TimeBiasModel theModel = (TimeBiasModel)recommenderModel;
        theModel.setUserBias(userIndex, theModel.getUserBias(userIndex)+theModel.getLearningRate()*(err-theModel.getLambda()*theModel.getUserBias(userIndex)));  //train1
        theModel.setItemBias(itemIndex, theModel.getItemBias(itemIndex)+theModel.getLearningRate()*(err-theModel.getLambda()*theModel.getItemBias(itemIndex)));  //train1

        //Item temporal bias
        if (theModel.getLRBinBias()!=0.0){
            int itemTimeBin=theModel.getItemTimeBin(date);
            theModel.setItemTimeBias(itemIndex, itemTimeBin, theModel.getItemTimeBias(itemIndex, itemTimeBin)+theModel.getLRBinBias()*(err-theModel.getLambdaBinBias()*theModel.getItemTimeBias(itemIndex, itemTimeBin)));
        }

        // User temporal bias
        if (theModel.getLRAlpha()!=0.0){
            theModel.setAlpha(userIndex, theModel.getAlpha(userIndex)+theModel.getLRAlpha()*(err*dev(userIndex, prefTime)-theModel.getLambdaAlpha()*theModel.getAlpha(userIndex)));
        }

        if (theModel.getLambdaDayBias()!=0.0){
            Long timeDiff = prefTime - initialTime;
            Long _dayIndex = timeDiff / ((long)3600*24*1000);
            int dayIndex = _dayIndex.intValue();
            theModel.setUserDayBias(userIndex, dayIndex, theModel.getUserDayBias(userIndex, dayIndex)+theModel.getLRDayBias()*(err-theModel.getLambdaDayBias()*theModel.getUserDayBias(userIndex, dayIndex)));
        }
    }

    public Float getEstimationByIndex(int userIndex, int itemIndex, C context){
        double sum = Double.NaN;
        long prefTime = context.getTimestamp();
        Date date = new Date(prefTime);
        if (userIndex>=0 && itemIndex>=0){
            TimeBiasModel theModel = (TimeBiasModel)recommenderModel;
            sum=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
            
            //checks if temporal trends can be applied (prediction date's trends available)
            if (theModel.getLRBinBias()!=0.0){
                int itemTimeBin=theModel.getItemTimeBin(date);
                sum+=theModel.getItemTimeBias(itemIndex, itemTimeBin);
            }
            // User temporal bias
            if (theModel.getLRAlpha()!=0.0){
                sum+=theModel.getAlpha(userIndex)*dev(userIndex,prefTime);
            }
            if (theModel.getLRDayBias()!=0.0){
                Long timeDiff = prefTime - initialTime;
                Long _dayIndex = timeDiff / ((long)3600*24*1000);
                int dayIndex = _dayIndex.intValue();

                sum+=theModel.getUserDayBias(userIndex, dayIndex);
            }
        }
        return (float)sum;
    }
    
    @Override
    public void updateLearningRates() {
            this.recommenderModel.setLearningRate(this.recommenderModel.getLearningRate()*0.9);
            ((TimeBiasModel)this.recommenderModel).setLr_bin_bias(((TimeBiasModel)this.recommenderModel).getLRBinBias()*0.9);
            ((TimeBiasModel)this.recommenderModel).setLr_alpha(((TimeBiasModel)this.recommenderModel).getLRAlpha()*0.9);
            ((TimeBiasModel)this.recommenderModel).setLr_dayBias(((TimeBiasModel)this.recommenderModel).getLRDayBias()*0.9);
    }
    
    
    private double dev(int userIndex, Long prefTime){
        U user = (U)recommenderModel.getKeysAndIndexList().getUserKey(userIndex);
        Date meanDate=eModel.getMeanUserDate(user);
        long milisMean=meanDate.getTime();
        long milisDif=prefTime-milisMean;
        Long daysDif=milisDif/((long)24*3600*1000);
        long magnitude=Math.abs(daysDif);
        double sign = Math.signum(daysDif.doubleValue());
        double value=sign*Math.pow(magnitude, ((TimeBiasModel)this.recommenderModel).getBeta());
        return value;
    }
    
    @Override
    public String algorithmDescription() {
        return "TimeBias Estimation";
    }
}
