package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Calendar;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Factorization + Time-Aware Baseline Model for Recommendation
 * Based on descriptions in:
 *  Koren, Y., Collaborative Filtering with Temporal Dynamics, Proc. of KDD'09, 2009
 *  Koren, Y., The BellKor Solution to the Netflix Grand Prize, Report from the Netflix Prize Winners, 2009
 *
 * @author Pedro G. Campos, pedro.campos@uam.es
 *
 */
public class FactorModelTimeBaseline<U,I,C extends ContinuousTimeContextIF> extends FactorModel<U,I,C> implements Serializable{
    private double lambda_bin_bias;
    private double lr_bin_bias;
    private int userBiasBins;
    private int itemBiasBins;
    private double[][] userTimeBias;
    private double[][] itemTimeBias;
    private long[] userTimes;
    private long[] itemTimes;

    private double[] alpha;
    private double lambda_alpha;
    private double lr_alpha;
    private double beta=0.25;

    private HashMap<Integer,Double>[] userDayBias;
    private double lambda_dayBias;
    private double lr_dayBias;

    public FactorModelTimeBaseline(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, int nFeatures, int iterationSteps, double initValue, double learningRate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, boolean withBias, double initNoise, int userBiasBins, int itemBiasBins){
        super(model, nFeatures, iterationSteps, initValue, learningRate, lambda, lrStaticBias, lambdaStaticBias, withBias, initNoise);
        this.lr_bin_bias = lrBinBias;
        this.lambda_bin_bias = lambdaBinBias;
        this.lr_alpha = lrAlpha;
        this.lambda_alpha = lambdaAlpha;
        this.beta = beta;
        this.lr_dayBias = lrDayBias;
        this.lambda_dayBias = lambdaDayBias;
        this.userBiasBins = userBiasBins;
        this.itemBiasBins = itemBiasBins;
        
        long minTime = eModel.getMinDate().getTime();
        long maxTime = eModel.getMaxDate().getTime();
        
        userTimeBias = new double[userMaxIndex][userBiasBins];
        itemTimeBias = new double[itemMaxIndex][itemBiasBins];
        alpha = new double[userMaxIndex];
        if (this.lambda_bin_bias!=0.0){
            for (int i=0; i<userMaxIndex; i++)
                for (int j=0; j<userBiasBins; j++)
                    //userTimeBias[i][j]=0.001+random.nextDouble()*0.01;
                    userTimeBias[i][j]=(random.nextDouble()-0.5)*0.005;
            for (int i=0; i<itemMaxIndex; i++)
                for (int j=0; j<itemBiasBins; j++)
                    //itemTimeBias[i][j]=0.001+random.nextDouble()*0.01;
                    itemTimeBias[i][j]=(random.nextDouble()-0.5)*0.005;
        }
        if (lambda_alpha!=0.0)
            for (int j=0; j<userMaxIndex; j++)
                alpha[j]=(random.nextDouble()-0.5)*0.005;

        long minDate=eModel.getMinDate().getTime();
        long maxDate=eModel.getMaxDate().getTime();
        long days=(maxDate-minDate)/(3600*24*1000); // total number of days
        int userDaysInterval=(int)days/userBiasBins;
        int itemDaysInterval=(int)days/itemBiasBins;


        userTimes=new long[userBiasBins];
        itemTimes=new long[itemBiasBins];
        Calendar initIntervalCal,endIntervalCal;
        initIntervalCal=Calendar.getInstance();
        initIntervalCal.setTime(eModel.getMinDate());
        initIntervalCal.set(Calendar.HOUR, 0);
        initIntervalCal.set(Calendar.MINUTE, 0);
        initIntervalCal.set(Calendar.SECOND, 0);
        endIntervalCal=(Calendar)initIntervalCal.clone();
        //userDates[0]=initIntervalCal.getTime();
        for (int i=0;i<userBiasBins;i++){
            endIntervalCal.add(Calendar.DATE, userDaysInterval);
            userTimes[i]=endIntervalCal.getTimeInMillis();
        }
        if (userTimes[userBiasBins-1] < maxTime)
            userTimes[userBiasBins-1] = maxTime;

        endIntervalCal=(Calendar)initIntervalCal.clone();
        //itemDates[0]=initIntervalCal.getTime();
        for (int i=0; i < itemBiasBins; i++){
            endIntervalCal.add(Calendar.DATE, itemDaysInterval);
            itemTimes[i] = endIntervalCal.getTimeInMillis();
        }
        if (itemTimes[itemBiasBins-1] < maxDate){
            itemTimes[itemBiasBins-1] = maxDate;
        }

        //Initialization of userDayBias (only on days needed)
        this.userDayBias=new HashMap[userMaxIndex];
        
        for (U user: model.getUsers()){
            int userIndex=this.getKeysAndIndexList().getUserIndex(user);
            this.userDayBias[userIndex]=new HashMap<Integer, Double>();
            for (PreferenceIF<U,I,C> pref: model.getPreferencesFromUser(user)){
                Long prefTime = pref.getContext().getTimestamp();
                Long initTime = eModel.getMinDate().getTime();
                Long timeDiff = prefTime - initTime;
                Long _dayIndex = timeDiff / ((long)3600*24*1000);
                int dayIndex = _dayIndex.intValue();
                if (!this.userDayBias[userIndex].containsKey(dayIndex)){
                    this.userDayBias[userIndex].put(dayIndex, (random.nextDouble()-0.5)*0.005);
                }
            }
        }        
    }

    public double getLambdaBinBias() {
        return lambda_bin_bias;
    }

    public double getLRBinBias() {
        return lr_bin_bias;
    }

    public void setLrBinBias(double lr_bin_bias) {
        this.lr_bin_bias = lr_bin_bias;
    }

    public double getLRAlpha() {
        return lr_alpha;
    }

    public void setLrAlpha(double lr_alpha) {
        this.lr_alpha = lr_alpha;
    }
    
    public double getLRDayBias() {
        return lr_dayBias;
    }
    
    public void setLrDayBias(double lr_day_bias) {
        this.lr_dayBias = lr_day_bias;
    }    

    public double getAlpha(int i) {
        return alpha[i];
    }

    public void setAlpha(int i, double value) {
        this.alpha[i] = value;
    }

    public double getBeta() {
        return beta;
    }

    public double getLambdaAlpha() {
        return lambda_alpha;
    }

    public double getLambdaDayBias() {
        return lambda_dayBias;
    }

    public double getUserDayBias(int userIndex, int dayIndex){
        if (!this.userDayBias[userIndex].containsKey(dayIndex))
            return 0.0;
        return this.userDayBias[userIndex].get(dayIndex);
    }

    public void setUserDayBias(int userIndex, int dayIndex, double value){
        this.userDayBias[userIndex].put(dayIndex, value);
    }

    public double getUserTimeBias(int i, int j){
        return this.userTimeBias[i][j];
    }

    public void setUserTimeBias(int i, int j, double value) {
        this.userTimeBias[i][j] = value;
    }

    public double getItemTimeBias(int i, int j){
        return this.itemTimeBias[i][j];
    }

    public void setItemTimeBias(int i, int j, double value) {
        this.itemTimeBias[i][j] = value;
    }

    public int getUserTimeBin(long time){
        for (int i=0;i<userBiasBins;i++){
            if (time < userTimes[i])
                return i;
        }
        return userBiasBins-1;
    }

    public int getItemTimeBin(long time){
        for (int i=0;i<itemBiasBins;i++){
            if (time < itemTimes[i]){
                return i;
            }
        }
        return itemBiasBins-1;
    }

    @Override
    public String toString(){
        String desc=super.toString();
        desc+=  " LRbb:"+this.lr_bin_bias +
                " Lbb:"+this.lambda_bin_bias +
                " LRa:"+this.lr_alpha+
                " La:"+this.lambda_alpha+
                " beta:"+this.beta+
                " LRdb:"+this.lr_dayBias+
                " Ldb:"+this.lambda_dayBias+
                " itemBiasBins:"+this.itemBiasBins;
        return desc;
    }
}
