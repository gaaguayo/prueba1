package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.cars.recommender.AbstractRecommenderModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.Serializable;
/**
 *
 * @author Pedro G. Campos, pcampossoto@gmail.com
 */
public class FactorModel<U,I,C extends ContextIF> extends AbstractRecommenderModel<U,I,C> implements Serializable{
    protected double[][] userFactors;
    protected double[][] itemFactors;
    protected int nFeatures;
    protected int lastFactorTrained;
    protected double lambda_static_bias;
    protected double lr_static_bias;
    protected boolean withBias;

    protected double userBias[];
    protected double itemBias[];

    protected double minRating;
    protected double maxRating;

    public FactorModel(ModelIF<U,I,C> model, int nFeatures, int iterationSteps, double initValue, double learningRate, double lambda, double lrStaticBias, double lambdaStaticBias, boolean withBias, double initNoise) {
        super(model, iterationSteps, Math.sqrt((initValue-1.0)/(double)nFeatures), learningRate, lambda, initNoise);
        this.nFeatures = nFeatures;
        this.lr_static_bias = lrStaticBias;
        this.lambda_static_bias = lambdaStaticBias;
        this.withBias = withBias;
        
        this.userFactors=new double[nFeatures][userMaxIndex];
        this.itemFactors=new double[nFeatures][itemMaxIndex];
        this.lastFactorTrained=0;
        this.userBias=new double[userMaxIndex];
        this.itemBias=new double[itemMaxIndex];
        
        for (int i=0;i<nFeatures;i++){
            for (int j=0; j<userMaxIndex; j++) this.userFactors[i][j]=(random.nextDouble()-0.5)*0.005;
            for (int j=0; j<itemMaxIndex; j++) this.itemFactors[i][j]=(random.nextDouble()-0.5)*0.005;
        }
        for (int j=0; j<userMaxIndex; j++) this.userBias[j]=(random.nextDouble()-0.5)*0.005;
        for (int j=0; j<itemMaxIndex; j++) this.itemBias[j]=(random.nextDouble()-0.5)*0.005;
    }

    public double[] getUserBias() {
        return userBias;
    }

    public double getUserBias(int i) {
        return userBias[i];
    }

    public void setUserBias(double[] userBias) {
        this.userBias = userBias;
    }

    public void setUserBias(int i, double value) {
        this.userBias[i] = value;
    }

    public double[][] getUserFactors() {
        return userFactors;
    }

    public double getUserFactors(int i, int j){
        return this.userFactors[i][j];
    }
    
    public void setUserFactors(double[][] userFactors) {
        this.userFactors = userFactors;
    }

    public void setUserFactors(int i, int j, double value) {
        this.userFactors[i][j] = value;
    }

    public double[] getItemBias() {
        return itemBias;
    }

    public double getItemBias(int i) {
        return itemBias[i];
    }

    public void setItemBias(double[] itemBias) {
        this.itemBias = itemBias;
    }

    public void setItemBias(int i, double value) {
        this.itemBias[i] = value;
    }

    public double[][] getItemFactors() {
        return itemFactors;
    }

    public double getItemFactors(int i, int j){
        return this.itemFactors[i][j];
    }

    public void setItemFactors(double[][] itemFactors) {
        this.itemFactors = itemFactors;
    }

    public void setItemFactors(int i, int j, double value) {
        this.itemFactors[i][j] = value;
    }

    public double getLambdaStaticBias() {
        return lambda_static_bias;
    }

    public double getLRStaticBias() {
        return lr_static_bias;
    }

    public void setLr_static_bias(double lr_static_bias) {
        this.lr_static_bias = lr_static_bias;
    }

    public int getLastFactorTrained() {
        return lastFactorTrained;
    }

    public void setLastFactorTrained(int lastFactorTrained) {
        this.lastFactorTrained = lastFactorTrained;
    }

    public int getnFeatures() {
        return nFeatures;
    }

    public boolean isWithBias() {
        return withBias;
    }

    public int getUserMaxIndex() {
        return userMaxIndex;
    }

    public int getItemMaxIndex() {
        return itemMaxIndex;
    }

    public boolean existsUserKey(U user){
        return keys.existsUser(user);
    }

    public boolean existsItemKey(I item){
        return keys.existsItem(item);
    }

    @Override
    public String toString(){
        return " Factors:"+this.nFeatures+
                " Iters:"+this.iterationSteps+
                " LRate:"+this.learningRate+
                " L:"+this.lambda+
                " LRsb:"+this.lr_static_bias+
                " Lsb:"+this.lambda_static_bias+
                " WithBias:"+this.withBias;
    }

}
