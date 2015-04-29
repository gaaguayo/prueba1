package es.uam.eps.ir.cars.factorization;

//import recommender.common.Data_Date;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.es
 */
public class TimeBiasFactorAlgorithm<U,I,C extends ContinuousTimeContextIF> extends AbstractFactorizationAlgorithm<U,I,C>{
    protected boolean useFutureDataInEstimation=false;
    private double initNoise = 0.005;
    private long iniTime;
    private long endTime;
    
    public TimeBiasFactorAlgorithm(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, double defaultValue, int iterationSteps, double lrate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean WITH_BIAS, int userBiasBins, int itemBiasBins, boolean useFutureData){
        //super(dataModel, statistics, nFeatures, defaultValue, iterationSteps, lrate, lambda, lambda2, WITH_BIAS);
        useFutureDataInEstimation=useFutureData;
        double initValue=Math.sqrt((defaultValue-1.0)/(double)nFeatures);
        
        if (lrUserAlphaFactor!=0.0)
            this.recommenderModel=new FactorModelTimeFull(model, eModel, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, lrUserAlphaFactor, lambdaUserAlphaFactor, lrUserDayFactor, lambdaUserDayFactor, WITH_BIAS, initNoise, userBiasBins, itemBiasBins);
        else
            this.recommenderModel=new FactorModelTimeBaseline(model, eModel, nFeatures, iterationSteps, initValue, lrate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, WITH_BIAS, initNoise, userBiasBins, itemBiasBins);
        this.eModel=eModel;
        prefs=new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:model.getUsers()){
            prefs.addAll(model.getPreferencesFromUser(user));
        }
        if (WITH_BIAS){
            this.ratingMean=eModel.getMeanRating();
        }
        this.iniTime = eModel.getMinDate().getTime();
        this.endTime = eModel.getMaxDate().getTime();
    }


    public void trainFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        double dev = dev(userIndex, prefTime);
        Long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / (long)(3600*24*1000);
        int dayIndex = _dayIndex.intValue();

        
        FactorModelTimeBaseline theModel = (FactorModelTimeBaseline)this.recommenderModel;
        double UserValue=theModel.getUserFactors(factor, userIndex);
        double ItemValue=theModel.getItemFactors(factor, itemIndex);
        double learningRate = theModel.getLearningRate();
        double lambda = theModel.getLambda();
        theModel.setUserFactors(factor,userIndex, UserValue + learningRate * (err * ItemValue - lambda * UserValue) );
        theModel.setItemFactors(factor,itemIndex, ItemValue + learningRate * (err * UserValue - lambda * ItemValue) );

        if (this.recommenderModel instanceof FactorModelTimeFull){
            FactorModelTimeFull fmtfModel=(FactorModelTimeFull)this.recommenderModel;
        
            if (fmtfModel.getLRUserAlphaFactor() != 0.0){
                double userAlphaFactor = fmtfModel.getUserAlphaFactor(userIndex, factor);
                fmtfModel.setUserAlphaFactor( userIndex, factor, userAlphaFactor + fmtfModel.getLRUserAlphaFactor() * (err * dev - fmtfModel.getLambdaUserAlphaFactor() * userAlphaFactor) );
            }
            
            if (fmtfModel.getLRUserDayFactor() != 0.0){
                double userDayFactor = fmtfModel.getUserDayFactor(userIndex, factor, dayIndex);
                fmtfModel.setUserDayFactor( userIndex, factor, dayIndex, userDayFactor + fmtfModel.getLRUserDayFactor() * (err - fmtfModel.getLambdaUserDayFactor() * userDayFactor) );
            }
        }
    }

    public void trainBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err){
        long prefTime = pref.getContext().getTimestamp();
        double dev = dev(userIndex, prefTime);
        Long timeDiff = prefTime - iniTime;
        Long _dayIndex = timeDiff / ((long)3600*24*1000);
        int dayIndex = _dayIndex.intValue();

        
        FactorModelTimeBaseline theModel = (FactorModelTimeBaseline)this.recommenderModel;

        if (theModel.getLRStaticBias() != 0.0){
            double userBias = theModel.getUserBias(userIndex);
            double itemBias = theModel.getItemBias(itemIndex);
            theModel.setUserBias( userIndex, userBias + theModel.getLRStaticBias() * (err - theModel.getLambdaStaticBias() * userBias) );  //train1
            theModel.setItemBias( itemIndex, itemBias + theModel.getLRStaticBias() * (err - theModel.getLambdaStaticBias() * itemBias) );  //train1
        }
        
        if (theModel.getLRBinBias() != 0.0){
            int ItemTimeBin=theModel.getItemTimeBin(prefTime);
            double itemTimeBias = theModel.getItemTimeBias(itemIndex, ItemTimeBin);
            theModel.setItemTimeBias( itemIndex, ItemTimeBin, itemTimeBias + theModel.getLRBinBias() * (err - theModel.getLambdaBinBias() * itemTimeBias) );
        }

        if (theModel.getLRAlpha() != 0.0){
            double alpha = theModel.getAlpha(userIndex);
            theModel.setAlpha( userIndex, alpha + theModel.getLRAlpha() * (err * dev - theModel.getLambdaAlpha() * alpha) );
        }

        if (theModel.getLRDayBias() != 0.0){
            double userDayBias = theModel.getUserDayBias(userIndex, dayIndex);
            theModel.setUserDayBias( userIndex, dayIndex, userDayBias + theModel.getLRDayBias() * (err - theModel.getLambdaDayBias() * userDayBias) );
        }        
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
            FactorModelTimeBaseline theModel = (FactorModelTimeBaseline)this.recommenderModel;
            
            for (int i=0;i<theModel.getnFeatures();i++){
                double sumUserFactor=theModel.getUserFactors(i,userIndex);
                if (this.recommenderModel instanceof FactorModelTimeFull){
                    FactorModelTimeFull fmtfModel=(FactorModelTimeFull)this.recommenderModel;
//                    if ( useFutureDataInEstimation && prefTime < endTime ){
                        if ( fmtfModel.getLRUserAlphaFactor()!=0.0 )
                            sumUserFactor+=fmtfModel.getUserAlphaFactor(userIndex, i)*dev;
                        if (fmtfModel.getLRUserDayFactor()!=0.0)
                            sumUserFactor+=fmtfModel.getUserDayFactor(userIndex, i, dayIndex);
//                    }
                }
                sum+=sumUserFactor*theModel.getItemFactors(i,itemIndex);
            }
            
            if (theModel.getLRStaticBias()!=0.0){
                sum+=ratingMean+theModel.getUserBias(userIndex)+theModel.getItemBias(itemIndex);
            }
            
            //checks if temporal trends can be applied (prediction date's trends available)
//            if ( useFutureDataInEstimation && prefTime < endTime ){
                if (theModel.getLRBinBias()!=0.0){
                    // Item temporal bias
                    int timeBin=theModel.getItemTimeBin(prefTime);
                    sum+=theModel.getItemTimeBias(itemIndex, timeBin);
                }
                
                // User temporal bias
                if (theModel.getLRAlpha()!=0.0){
                    sum+=theModel.getAlpha(userIndex)*dev;
                }
                
                if (theModel.getLRDayBias()!=0.0){
                    sum+=theModel.getUserDayBias(userIndex, dayIndex);
                }
            }
//        }
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
            FactorModelTimeBaseline theModel = (FactorModelTimeBaseline)this.recommenderModel;
            theModel.setLearningRate(theModel.getLearningRate() * 0.9);
            theModel.setLr_static_bias(theModel.getLRStaticBias() * 0.9);
            theModel.setLrBinBias(theModel.getLRBinBias() * 0.9);
            theModel.setLrAlpha(theModel.getLRAlpha() * 0.9);
            theModel.setLrDayBias(theModel.getLRDayBias() * 0.9);
            if (this.recommenderModel instanceof FactorModelTimeFull){
                ((FactorModelTimeFull)theModel).setLRUserAlphaFactor(((FactorModelTimeFull)theModel).getLRUserAlphaFactor() * 0.9);
                ((FactorModelTimeFull)theModel).setLRUserDayFactor(((FactorModelTimeFull)theModel).getLRUserDayFactor() * 0.9);
            }
    }
    
    

    @Override
    public String algorithmDescription() {
        return "TimeBaseline Factorization, usingFutureDataInEstimation:"+this.useFutureDataInEstimation;
    }
}
