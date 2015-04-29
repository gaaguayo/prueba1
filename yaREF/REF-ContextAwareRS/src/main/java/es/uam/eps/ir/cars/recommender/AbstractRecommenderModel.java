package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.model.KeysAndIndexList;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.io.Serializable;
import java.util.Random;
/**
 *
 * @author Pedro G. Campos, pedro.campos@estudiante.uam.
 */
public abstract class AbstractRecommenderModel<U,I,C extends ContextIF> implements Serializable{
    protected static final long serialVersionUID = 1001L;
    protected ModelIF<U,I,C> dataModel;
    protected int userMaxIndex;
    protected int itemMaxIndex;
    protected int iterationSteps;
    protected double minImprovement = 0.0001;
    protected double initValue;
    protected double initNoise;
    protected double learningRate;
    protected double lambda;

    protected KeysAndIndexList keys;
    protected Random random;

    public AbstractRecommenderModel(ModelIF<U,I,C> model, int iterationSteps, double initValue, double learningRate, double lambda, double initNoise) {
        this.dataModel = model;
        this.iterationSteps = iterationSteps;
        this.initValue = initValue;
        this.initNoise = initNoise;
        this.learningRate = learningRate;
        this.lambda = lambda;

        this.random=new java.util.Random(0);

        keys=new KeysAndIndexList();
        keys.generateKeys(model);
        this.userMaxIndex=keys.getUserMaxIndex();
        this.itemMaxIndex=keys.getItemMaxIndex();
    }

    public KeysAndIndexList getKeysAndIndexList(){
        return keys;
    }
    
    public int getIterationSteps() {
        return iterationSteps;
    }

    public double getMinImprovement() {
        return minImprovement;
    }

    public double getLambda() {
        return lambda;
    }

    public double getInitValue() {
        return initValue;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public Random getRandom() {
        return random;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
    
    public int getDim1MaxIndex() {
        return userMaxIndex;
    }

    public int getDim2MaxIndex() {
        return itemMaxIndex;
    }

    public boolean existKeys(U user, I item){
        return keys.exists(user, item);
    }

    public boolean existsUser(U user){
        return keys.existsUser(user);
    }

    public boolean existsItem(I item){
        return keys.existsItem(item);
    }

    public ModelIF<U, I, C> getDataModel() {
        return dataModel;
    }    
}
