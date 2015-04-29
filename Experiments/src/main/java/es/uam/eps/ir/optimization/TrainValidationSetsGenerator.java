package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;

/**
 *
 * @author pedro
 */
public class TrainValidationSetsGenerator<U,I,C extends ContextIF> {
    private ModelIF<U,I,C> trainSet;
    private ModelIF<U,I,C> validationSet;

    public TrainValidationSetsGenerator(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, DatasetSplitterIF<U,I,C> splitter){
        SplitIF<U,I,C> split[] = splitter.split(model);
        
        trainSet = split[0].getTrainingSet();
        validationSet  = split[0].getTestingSet();               
    }

    public ModelIF<U, I, C> getTrainSet() {
        return trainSet;
    }

    public ModelIF<U, I, C> getValidationSet() {
        return validationSet;
    }
}
