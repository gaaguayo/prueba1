package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.HashMap;

/**
 * Full Time-Aware Factorization + Baseline Model for Recommendation
 * Based on descriptions in:
 *  Koren, Y., Collaborative Filtering with Temporal Dynamics, Proc. of KDD'09, 2009
 *  Koren, Y., The BellKor Solution to the Netflix Grand Prize, Report from the Netflix Prize Winners, 2009
 *
 * @author Pedro G. Campos, pedro.campos@uam.es
 * @date 27-mar-2011
 */
public class FactorModelTimeFull<U,I,C extends ContinuousTimeContextIF> extends FactorModelTimeBaseline<U,I,C>{
    private double[][] userAlphaFactor;
    private double lambda_userAlphaFactor;
    private double lr_userAlphaFactor;

    private HashMap<Integer, Double>[][] userDayFactor;

    private double lambda_userDayFactor;
    private double lr_userDayFactor;


    public FactorModelTimeFull(ModelIF<U,I,C> dataset, ContextualModelUtils<U,I,C> statistics, int nFeatures, int iterationSteps, double initValue, double learningRate, double lambda, double lrStaticBias, double lambdaStaticBias, double lrBinBias, double lambdaBinBias, double lrAlpha, double lambdaAlpha, double beta, double lrDayBias, double lambdaDayBias, double lrUserAlphaFactor, double lambdaUserAlphaFactor, double lrUserDayFactor, double lambdaUserDayFactor, boolean withBias, double initNoise, int userBiasBins, int itemBiasBins) {
        super(dataset, statistics, nFeatures, iterationSteps, initValue, learningRate, lambda, lrStaticBias, lambdaStaticBias, lrBinBias, lambdaBinBias, lrAlpha, lambdaAlpha, beta, lrDayBias, lambdaDayBias, withBias, initNoise, userBiasBins, itemBiasBins);
        this.lambda_userAlphaFactor = lambdaUserAlphaFactor;
        this.lr_userAlphaFactor = lrUserAlphaFactor;
        this.lambda_userDayFactor = lambdaUserDayFactor;
        this.lr_userDayFactor = lrUserDayFactor;

        this.userAlphaFactor=new double[this.userMaxIndex][this.nFeatures];
        if (this.lr_userAlphaFactor!=0.0)
            for (int i=0; i<this.userMaxIndex; i++)
                for (int j=0; j<this.nFeatures; j++)
                    this.userAlphaFactor[i][j]=(random.nextDouble()-0.5)*0.005;

        //Initialization of userDayFactor (only on days needed)
        this.userDayFactor=new HashMap[this.userMaxIndex][this.nFeatures];
        for (U user: dataset.getUsers()){
            int userIndex=this.getKeysAndIndexList().getUserIndex(user);
            for (int feature=0; feature<nFeatures;feature++){
                this.userDayFactor[userIndex][feature]=new HashMap<Integer, Double>();            
                for (PreferenceIF<U,I,C> pref: dataset.getPreferencesFromUser(user)){
                    Long prefTime = pref.getContext().getTimestamp();
                    Long initTime = statistics.getMinDate().getTime();
                    Long timeDiff = prefTime - initTime;
                    Long _dayIndex = timeDiff / ((long)3600*24*1000);
                    int dayIndex = _dayIndex.intValue();
                    if (!this.userDayFactor[userIndex][feature].containsKey(dayIndex)){
                        this.userDayFactor[userIndex][feature].put(dayIndex, (random.nextDouble()-0.5)*0.005);
                    }
                }
            }
        }
    }

    public double getLambdaUserAlphaFactor(){
        return this.lambda_userAlphaFactor;
    }

    public double getLRUserAlphaFactor(){
        return this.lr_userAlphaFactor;
    }

    public void setLRUserAlphaFactor(double lr_userAlphaFactor) {
        this.lr_userAlphaFactor = lr_userAlphaFactor;
    }    

    public double getLambdaUserDayFactor(){
        return this.lambda_userDayFactor;
    }

    public double getLRUserDayFactor(){
        return this.lr_userDayFactor;
    }

    public void setLRUserDayFactor(double lr_userDayFactor) {
        this.lr_userDayFactor = lr_userDayFactor;
    }

    public double getUserAlphaFactor(int userIndex, int feature){
        return this.userAlphaFactor[userIndex][feature];
    }

    public void setUserAlphaFactor(int userIndex, int feature, double value){
        this.userAlphaFactor[userIndex][feature]=value;
    }

    public double getUserDayFactor(int userIndex, int feature, int dayIndex){
        if (!this.userDayFactor[userIndex][feature].containsKey(dayIndex))
            return 0.0;
        return this.userDayFactor[userIndex][feature].get(dayIndex);
    }

    public void setUserDayFactor(int userIndex, int feature, int dayIndex, double value){
        this.userDayFactor[userIndex][feature].put(dayIndex,value);
    }

    @Override
    public String toString(){
        String desc=super.toString();
        desc+=  " LRaf:"+this.lr_userAlphaFactor +
                " Laf:"+this.lambda_userAlphaFactor +
                " LRdf:"+this.lr_userDayFactor+
                " Ldf:"+this.lambda_userDayFactor;
        return desc;
    }
}
