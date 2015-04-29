package es.uam.eps.ir.optimization;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.cars.model.GenericExplicitModel;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.split.SplitIF;
import es.uam.eps.ir.split.DatasetSplitterIF;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author pedro
 */
public class TrainValidationSetsGenerator_SizeLimit<U,I,C extends ContextIF> {
    private ModelIF<U,I,C> trainSet;
    private ModelIF<U,I,C> validationSet;

    public TrainValidationSetsGenerator_SizeLimit(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, DatasetSplitterIF<U,I,C> splitter){
        // Default values
        // int minRatingsAmount=10000;
        // double proportionFromOriginalSet = 0.2;
        this(model, eModel, splitter, 10000, 0.2);        
    }

    public TrainValidationSetsGenerator_SizeLimit(ModelIF<U,I,C> model, ContextualModelUtils<U,I,C> eModel, DatasetSplitterIF<U,I,C> splitter, int minRatingsAmount, double proportionFromOriginalSet){
        int ratingsToSelect = (int)(eModel.getRatingCount() * proportionFromOriginalSet);
        if (ratingsToSelect <= minRatingsAmount ){
            proportionFromOriginalSet = (double)minRatingsAmount / (double)eModel.getRatingCount();
        }
        SplitIF<U,I,C> split[] = splitter.split(model);
        
        ModelIF<U,I,C> theTrain = split[0].getTrainingSet();
        ModelIF<U,I,C> theValidation  = split[0].getTestingSet();       
        
        List<PreferenceIF<U,I,C>> trainPrefs = new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:theTrain.getUsers()){
            trainPrefs.addAll(theTrain.getPreferencesFromUser(user));
        }
        int trainSize = trainPrefs.size();

        List<PreferenceIF<U,I,C>> validationPrefs = new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:theValidation.getUsers()){
            validationPrefs.addAll(theValidation.getPreferencesFromUser(user));
        }
        int validationSize = validationPrefs.size();
        
        trainSize = (int)(trainPrefs.size() * proportionFromOriginalSet);
        validationSize = (int)(validationPrefs.size() * proportionFromOriginalSet);
        
        Random random = new Random(0);
        
        trainSet = new GenericExplicitModel<U,I,C>();
        for (int trainRatingsSelected = 0; trainRatingsSelected < trainSize; trainRatingsSelected++){
            int pos = random.nextInt(trainPrefs.size());
            PreferenceIF<U,I,C> pref = trainPrefs.remove(pos);
            trainSet.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
        }
        
        random = new Random(0);
        validationSet = new GenericExplicitModel<U,I,C>();
        for (int validationRatingsSelected = 0; validationRatingsSelected < validationSize; validationRatingsSelected++){
            int pos = random.nextInt(validationPrefs.size());
            PreferenceIF<U,I,C> pref = validationPrefs.remove(pos);
            validationSet.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
        }
    }

    public ModelIF<U, I, C> getTrainSet() {
        return trainSet;
    }

    public ModelIF<U, I, C> getValidationSet() {
        return validationSet;
    }
}
