package es.uam.eps.ir.split.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;

/**
 *
 * @author Pedro G. Campos
 */
public class Split<U,I,C extends ContextIF> implements SplitIF<U,I,C>{
    ModelIF<U, I, C> trainSet;
    ModelIF<U, I, C> testSet;

    public Split(ModelIF<U, I, C> trainSet, ModelIF<U, I, C> testSet) {
        this.trainSet = trainSet;
        this.testSet = testSet;
    }
    
    public ModelIF<U, I, C> getTestingSet() {
        return testSet;
    }

    public ModelIF<U, I, C> getTrainingSet() {
        return trainSet;
    }
    
}
