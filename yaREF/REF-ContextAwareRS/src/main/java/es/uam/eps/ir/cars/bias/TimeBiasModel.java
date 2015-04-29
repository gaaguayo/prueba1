package es.uam.eps.ir.cars.bias;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;
/**
 * Time-Aware Baseline Model for Recommendation
 * Based on descriptions in:
 *  Koren, Y., Collaborative Filtering with Temporal Dynamics, Proc. of KDD'09, 2009
 *  Koren, Y., The BellKor Solution to the Netflix Grand Prize, Report from the Netflix Prize Winners, 2009
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 *
 */
public class TimeBiasModel<U,I,C extends ContinuousTimeContextIF> extends BiasModel<U,I,C> implements Serializable{
    private int userBiasBins;
    private int itemBiasBins;

    private double[][] userTimeBias;
    private double[][] itemTimeBias;
    private Date[] userBinDates;
    private Date[] itemBinDates;
    private double lambda_bin_bias;
    private double lr_bin_bias;

    //private double[][] userDayBias;
    //private double lambda_user_day_bias;

    private double[] alpha;
    private double lambda_alpha;
    private double lr_alpha;
    private double beta;

    private HashMap<Integer,Double>[] userDayBias;
    private double lambda_dayBias;
    private double lr_dayBias;

    public TimeBiasModel(ModelIF<U,I,C> dataset, ContextualModelUtils<U,I,C> statistics, int iterationSteps, double initValue, double learningRate, double lambda, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double initNoise, int userBiasBins, int itemBiasBins){
        super(dataset, iterationSteps, initValue, learningRate, lambda, initNoise);
        this.userBiasBins=userBiasBins;
        this.itemBiasBins=itemBiasBins;
        this.lr_bin_bias=lrBinBias;
        this.lambda_bin_bias=lambdaBinBias;
        this.lr_alpha=lrAlpha;
        this.lambda_alpha=lambdaAlpha;
        this.beta=beta;
        this.lr_dayBias=lrDayBias;
        this.lambda_dayBias=lambdaDayBias;
        userTimeBias=new double[userMaxIndex][userBiasBins];
        itemTimeBias=new double[itemMaxIndex][itemBiasBins];
        alpha=new double[userMaxIndex];
        for (int i=0; i<userMaxIndex; i++)
            for (int j=0; j<userBiasBins; j++)
                //userTimeBias[i][j]=0.001+random.nextDouble()*0.01;
                userTimeBias[i][j]=(random.nextDouble()-0.5)*0.005;
        for (int i=0; i<itemMaxIndex; i++)
            for (int j=0; j<itemBiasBins; j++)
                //itemTimeBias[i][j]=0.001+random.nextDouble()*0.01;
                itemTimeBias[i][j]=(random.nextDouble()-0.5)*0.005;

        if (lambda_alpha!=0.0)
            for (int j=0; j<userMaxIndex; j++)
                alpha[j]=(random.nextDouble()-0.5)*0.005;

        long minDate=statistics.getMinDate().getTime();
        long maxDate=statistics.getMaxDate().getTime();
        long days=(maxDate-minDate)/(3600*24*1000); // total number of days
        int userDaysInterval=(int)days/userBiasBins;
        int itemDaysInterval=(int)days/itemBiasBins;


        userBinDates=new Date[userBiasBins];
        itemBinDates=new Date[itemBiasBins];
        Calendar initIntervalCal,endIntervalCal;
        initIntervalCal=Calendar.getInstance();
        initIntervalCal.setTime(statistics.getMinDate());
        initIntervalCal.set(Calendar.HOUR, 0);
        initIntervalCal.set(Calendar.MINUTE, 0);
        initIntervalCal.set(Calendar.SECOND, 0);
        endIntervalCal=(Calendar)initIntervalCal.clone();

        //Dates for user bins
        for (int i=0;i<userBiasBins;i++){
            endIntervalCal.add(Calendar.DATE, userDaysInterval);
            userBinDates[i]=endIntervalCal.getTime();
        }
        if (userBinDates[userBiasBins-1].before(statistics.getMaxDate()))
            userBinDates[userBiasBins-1]=statistics.getMaxDate();

        //Dates for item bins
        endIntervalCal=(Calendar)initIntervalCal.clone();
        for (int i=0;i<itemBiasBins;i++){
            endIntervalCal.add(Calendar.DATE, itemDaysInterval);
            itemBinDates[i]=endIntervalCal.getTime();
        }
        if (itemBinDates[itemBiasBins-1].before(statistics.getMaxDate()))
            itemBinDates[itemBiasBins-1]=statistics.getMaxDate();

        //Initialization of userDayBias (only on days needed)
        this.userDayBias=new HashMap[userMaxIndex];
        
        for (U user:dataset.getUsers()){
            int userIndex=this.getKeysAndIndexList().getUserIndex(user);
            this.userDayBias[userIndex]=new HashMap<Integer, Double>();
            for (PreferenceIF<U,I,C> pref : dataset.getPreferencesFromUser(user)){
                Long prefTime = pref.getContext().getTimestamp();
                Long initTime = statistics.getMinDate().getTime();
                Long timeDiff = prefTime - initTime;
                Long _dayIndex = timeDiff / ((long)3600*24*1000);
                int dayIndex = _dayIndex.intValue();
                if (!this.userDayBias[userIndex].containsKey(dayIndex)){
                    this.userDayBias[userIndex].put(dayIndex, (random.nextDouble()-0.5)*0.005);
                }
            }
        }        
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

    public int getUserTimeBin(Date date){
        for (int i=0;i<userBiasBins;i++){
            if (date.before(userBinDates[i]))
                return i;
        }
        return userBiasBins-1;
    }

    public int getItemTimeBin(Date date){
        //System.out.println("date:"+date);
        for (int i=0;i<itemBiasBins;i++){
            if (date.before(itemBinDates[i])){
                //System.out.println("bin:"+i+"(endDate:"+itemDates[i]+")");
                return i;
            }
        }
        return itemBiasBins-1;
    }

    public double getLRBinBias() {
        return lr_bin_bias;
    }

    public double getLambdaBinBias() {
        return lambda_bin_bias;
    }

    public double getLRAlpha() {
        return lr_alpha;
    }

    public double getLambdaAlpha() {
        return lambda_alpha;
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

//    public void setBeta(double beta) {
//        this.beta = beta;
//    }

    public double getLRDayBias() {
        return lr_dayBias;
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

    public void setLr_bin_bias(double lr_bin_bias) {
        this.lr_bin_bias = lr_bin_bias;
    }

    public void setLr_dayBias(double lr_dayBias) {
        this.lr_dayBias = lr_dayBias;
    }

    public void setLr_alpha(double lr_alpha) {
        this.lr_alpha = lr_alpha;
    }
    
    

    @Override
    public String toString(){
        String desc=super.toString();
        desc+= " lrBinBias:"+ this.lr_bin_bias +
                " lambdaBinBias:"+ this.lambda_bin_bias +
                " lrAlpha:" + this.lr_alpha+
                " lambdaAlpha:" + this.lambda_alpha+
                " beta:"+this.beta+
                " lrDayBias"+this.lr_dayBias +
                " lambdaDayBias"+this.lambda_dayBias +
                " userBiasBins:"+this.userBiasBins+
                " itemBiasBins:"+this.itemBiasBins;
        return desc;
    }
}
