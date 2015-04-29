package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Date;

/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.es
 */
public class MF_ParallelFullTimeAwareAlgorithm<U,I,C extends ContinuousTimeContextIF> extends AbstractParallelFactorizationAlgorithm<U,I,C>{
    protected boolean useFutureDataInEstimation=true;
    private double initNoise = 0.005;
    private long iniTime;
    
    public MF_ParallelFullTimeAwareAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData, int maxThreads){
        useFutureDataInEstimation=useFutureData;
        double initValue=Math.sqrt((defaultValue-1.0)/(double)nFeatures);
        
        this.recommenderModel=new FactorModelTimeFull(model, eModel, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, initNoise, userBiasBins, itemBiasBins);
        
        this.eModel=eModel;
        this.ratingMean=eModel.getMeanRating();
        this.iniTime = eModel.getMinDate().getTime();
        this.maxThreads = maxThreads;
    }

    public void trainUserFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        double dev = dev(userIndex, prefTime);
        Long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / (long)(3600*24*1000);
        int dayIndex = _dayIndex.intValue();

        
        FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;
        double UserValue=theModel.getUserFactors(factor, userIndex);
        double ItemValue=theModel.getItemFactors(factor, itemIndex);
        double learningRate = theModel.getLearningRate();
        double lambda = theModel.getLambda();
        double userAlphaFactor = theModel.getUserAlphaFactor(userIndex, factor);
        double userDayFactor = theModel.getUserDayFactor(userIndex, factor, dayIndex);
        
        theModel.setUserFactors(factor,userIndex, UserValue + learningRate * (err * ItemValue - lambda * UserValue) );
        
        theModel.setUserAlphaFactor( userIndex, factor, userAlphaFactor + theModel.getLRUserAlphaFactor() * (err * (ItemValue*dev) - theModel.getLambdaUserAlphaFactor() * userAlphaFactor) );

        theModel.setUserDayFactor( userIndex, factor, dayIndex, userDayFactor + theModel.getLRUserDayFactor() * (err * ItemValue - theModel.getLambdaUserDayFactor() * userDayFactor) );
    }

    public void trainUserBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        double dev = dev(userIndex, prefTime);
        Long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / ((long)3600*24*1000);
        int dayIndex = _dayIndex.intValue();

        
        FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;

        double userBias = theModel.getUserBias(userIndex);
        theModel.setUserBias( userIndex, userBias + theModel.getLRStaticBias() * (err - theModel.getLambdaStaticBias() * userBias) );  //train1
        
        double alpha = theModel.getAlpha(userIndex);
        theModel.setAlpha( userIndex, alpha + theModel.getLRAlpha() * (err * dev - theModel.getLambdaAlpha() * alpha) );

        double userDayBias = theModel.getUserDayBias(userIndex, dayIndex);
        theModel.setUserDayBias( userIndex, dayIndex, userDayBias + theModel.getLRDayBias() * (err - theModel.getLambdaDayBias() * userDayBias) );
    }
    
    public void trainItemFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        double dev = dev(userIndex, prefTime);
        Long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / (long)(3600*24*1000);
        int dayIndex = _dayIndex.intValue();
        
        FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;
        
        double UserValue=theModel.getUserFactors(factor, userIndex);
        double ItemValue=theModel.getItemFactors(factor, itemIndex);
        double learningRate = theModel.getLearningRate();
        double lambda = theModel.getLambda();
        double userAlphaFactor = theModel.getUserAlphaFactor(userIndex, factor);
        double userDayFactor = theModel.getUserDayFactor(userIndex, factor, dayIndex);
        
        theModel.setItemFactors(factor,itemIndex, ItemValue + learningRate * (err * (UserValue + userAlphaFactor * dev + userDayFactor) - lambda * ItemValue) );
        
    }

    public void trainItemBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;

        double itemBias = theModel.getItemBias(itemIndex);
        theModel.setItemBias( itemIndex, itemBias + theModel.getLRStaticBias() * (err - theModel.getLambdaStaticBias() * itemBias) );  //train1
        
        int ItemTimeBin=theModel.getItemTimeBin(prefTime);
        double itemTimeBias = theModel.getItemTimeBias(itemIndex, ItemTimeBin);
        theModel.setItemTimeBias( itemIndex, ItemTimeBin, itemTimeBias + theModel.getLRBinBias() * (err - theModel.getLambdaBinBias() * itemTimeBias) );
    }
        

    public Float getEstimation(int user, int item, C context){
        int dim1Index=recommenderModel.getKeysAndIndexList().getUserIndex(user);
        int dim2Index=recommenderModel.getKeysAndIndexList().getItemIndex(item);
        return getEstimationByIndex(dim1Index, dim2Index, context);
    }
    
    public Float getEstimationByIndex(int userIndex, int itemIndex, C context, double dev){
        double sum=Double.NaN;
        long prefTime = context.getTimestamp();
        long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / ((long)3600*24*1000);
        int dayIndex = _dayIndex.intValue();
        if (userIndex>=0 && itemIndex>=0){
            sum=0.0;
            FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;
            
            for (int i=0;i<theModel.getnFeatures();i++){
                double sumUserFactor=theModel.getUserFactors(i,userIndex);
                sumUserFactor+=theModel.getUserAlphaFactor(userIndex, i)*dev;
                sumUserFactor+=theModel.getUserDayFactor(userIndex, i, dayIndex);
                sum+=sumUserFactor*theModel.getItemFactors(i,itemIndex);
            }
            
            sum+=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
            
            // Item temporal bias
            int timeBin=theModel.getItemTimeBin(prefTime);
            sum+=theModel.getItemTimeBias(itemIndex, timeBin);
                
            // User temporal bias
            sum+=theModel.getAlpha(userIndex)*dev;
                
            sum+=theModel.getUserDayBias(userIndex, dayIndex);
        }
        return (float)sum;        
    }

    public Float getEstimationByIndex(int userIndex, int itemIndex, C context){
        double dev = dev(userIndex, context.getTimestamp());
        return getEstimationByIndex(userIndex, itemIndex, context, dev);
    }

    private double dev(int userIndex, Long prefTime){
        U user = (U)recommenderModel.getKeysAndIndexList().getUserKey(userIndex);
        Date meanDate=eModel.getMeanUserDate(user);
        long milisMean=meanDate.getTime();
        long milisDif=prefTime-milisMean;
        Long daysDif=milisDif/((long)24*3600*1000);
        long magnitude=Math.abs(daysDif);
        double sign = Math.signum(daysDif.doubleValue());
        double value=sign*Math.pow(magnitude, ((FactorModelTimeBaseline)this.recommenderModel).getBeta());
        return value;
    }

    @Override
    public void updateLearningRates() {
        FactorModelTimeFull theModel = (FactorModelTimeFull)this.recommenderModel;
        theModel.setLearningRate(theModel.getLearningRate() * LR_updateFactor);
        theModel.setLr_static_bias(theModel.getLRStaticBias() * LR_updateFactor);
        theModel.setLrBinBias(theModel.getLRBinBias() * LR_updateFactor);
        theModel.setLrAlpha(theModel.getLRAlpha() * LR_updateFactor);
        theModel.setLrDayBias(theModel.getLRDayBias() * LR_updateFactor);
        theModel.setLRUserAlphaFactor(theModel.getLRUserAlphaFactor() * LR_updateFactor);
        theModel.setLRUserDayFactor(theModel.getLRUserDayFactor() * LR_updateFactor);
    }

    @Override
    public String algorithmDescription() {
        return "FullTimeAware Factorization, usingFutureDataInEstimation:"+this.useFutureDataInEstimation;
    }
}
