package es.uam.eps.ir.cars.factorization;

import es.uam.eps.ir.cars.bias.AbstractParallelTrainingAlgorithm;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public abstract class AbstractParallelFactorizationAlgorithm<U,I,C extends ContextIF> extends AbstractParallelTrainingAlgorithm<U,I,C>{
    protected ModelIF<U,I,C> dataModel;

    protected void userTrainStep(int userIndex, int itemIndex,  PreferenceIF<U, I, C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        for (int factor=0;factor<theModel.getnFeatures();factor++){
            trainUserFactor(factor, userIndex, itemIndex, pref, error);
        }
        trainUserBias(userIndex, itemIndex, pref, error);        
    }
    
    protected abstract void trainUserFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err);
    protected abstract void trainUserBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err);
    
    protected void itemTrainStep(int userIndex, int itemIndex,  PreferenceIF<U,I,C> pref, double error){
        FactorModel theModel = (FactorModel)this.recommenderModel;
        for (int factor=0;factor<theModel.getnFeatures();factor++){
            trainItemFactor(factor, userIndex, itemIndex, pref, error);
        }
        trainItemBias(userIndex, itemIndex, pref, error);        
    }
    
    public abstract void trainItemFactor(int factor, int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err);
    public abstract void trainItemBias(int userIndex, int itemIndex, PreferenceIF<U,I,C> pref, double err);

}
