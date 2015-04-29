package es.uam.eps.ir.dataset.MovieLens1m;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.split.ratingorder.ROTime;
import es.uam.eps.ir.split.ratingorder.RatingOrderIF;
import es.uam.eps.ir.cars.inferred.ContextualAttributeComputerIF;
import es.uam.eps.ir.cars.inferred.PeriodOfWeekAttributeComputer;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelUtils.ModelPrintUtils;



/**
 *
 * @author pedro
 */
public class ContextDatasetGenerator<U,I,C extends ContextIF> {
    private ContextualAttributeComputerIF contextComputer;
    private float maxRatingValue = (float)5.0;
    private float minRatingValue = (float)1.0;
    private final static Logger logger = Logger.getLogger("ExperimentLog");
    
    private final static String path = "/datos/datasets/MovieLens/million/contextAware/";

    public enum CONTEXT_MODIFICATION{
        PeriodOfTheWeek_Rating,
        PeriodOfTheWeek_RatingMixed,
        PeriodOfTheWeek_Frequency,
        PeriodOfTheWeek_FrequencyMixed,
        Timestamp_Rating
    }
    
    public static void main(String args[]){
        ContextDatasetGenerator cdg = new ContextDatasetGenerator();
        cdg.getContextDataset(CONTEXT_MODIFICATION.Timestamp_Rating, new CommonDatasets(args).getDataset(CommonDatasets.DATASET.MovieLens1m));
    }
    
    
    
    public void saveDataset(ModelIF<U,I,C> model, String file){
        ModelPrintUtils.toGz(model, file);
    }
    
    public void getContextDataset(CONTEXT_MODIFICATION modification, DatasetIF<U,I,C> dataset){
        ModelIF<U,I,C> originalModel = dataset.getModel();
        
        for (int percentage = 10; percentage <= 100; percentage += 10){
            ModelIF<U,I,C> modifiedModel = modifyRatingByPeriodOfTheWeek(originalModel, percentage/100.0);
            saveDataset(modifiedModel, path + "periodOfTheWeek_modified_" + percentage + "percent.dat.gz");
        }
        
        for (int percentage = 10; percentage <= 100; percentage += 10){
            ModelIF<U,I,C> modifiedModel = modifyRatingByPeriodOfTheWeek_MixedValues(originalModel, percentage/100.0);
            saveDataset(modifiedModel, path + "periodOfTheWeekMixed_modified_" + percentage + "percent.dat.gz");
        }
        
        for (int percentage = 10; percentage <= 100; percentage += 10){
            ModelIF<U,I,C> modifiedModel = modifyRatingByTimestamp(originalModel, percentage/100.0);
            saveDataset(modifiedModel, path + "timestamp_modified_" + percentage + "percent.dat.gz");
        }
    };
    
    public ModelIF<U,I,C> modifyRatingByPeriodOfTheWeek(ModelIF<U,I,C> model, double percentageToModify){
        
        ModelIF<U,I,C> contextAwareModel = null;
        try{
            contextAwareModel = model.getClass().newInstance();
        } catch (Exception e){
        System.err.println("Problem instatiating copy of model " + model.toString() + ": " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        Random random = new Random(0);
        int totalPreferences = 0;
        int modifiedPreferences = 0;
        int context1Modifs = 0;
        int context2Modifs = 0;
        for (U user : model.getUsers()){
            for (PreferenceIF<U,I,C> preference : model.getPreferencesFromUser(user)){
                totalPreferences++;
                float contextAwareValue = preference.getValue();
                double prob = random.nextDouble();
                
                if ( prob < percentageToModify){
                    modifiedPreferences++;
                    contextComputer = new PeriodOfWeekAttributeComputer();
                    double contextValue = contextComputer.getAttributeValue(preference.getContext());
                    if (contextValue > 0.0){
                        if (contextAwareValue < maxRatingValue){
                            contextAwareValue += 1;
                            context1Modifs++;
                        }
                    }
                    else {
                        if (contextAwareValue > minRatingValue){
                            contextAwareValue -= 1;
                            context2Modifs++;
                        }
                    }
                }                
                contextAwareModel.addPreference(preference.getUser(), preference.getItem(), contextAwareValue, preference.getContext());
            }
        }
        logger.log(Level.INFO, "Modified {0} out of {1}preferences, ({2} + and {3} -)", new Object[]{modifiedPreferences, totalPreferences, context1Modifs, context2Modifs});
        return contextAwareModel;
    }
    
    public ModelIF<U,I,C> modifyRatingByPeriodOfTheWeek_MixedValues(ModelIF<U,I,C> model, double percentageToModify){
        
        ModelIF<U,I,C> contextAwareModel = null;
        try{
            contextAwareModel = model.getClass().newInstance();
        } catch (Exception e){
        System.err.println("Problem instatiating copy of model " + model.toString() + ": " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        Random random = new Random(0);
        int totalPreferences = 0;
        int modifiedPreferences = 0;
        float context1AddValue = 1;
        float context2AddValue = -1;
        int context1Modifs = 0;
        int context2Modifs = 0;
        for (U user : model.getUsers()){
            for (PreferenceIF<U,I,C> preference : model.getPreferencesFromUser(user)){
                totalPreferences++;
                float contextAwareValue = preference.getValue();
                double prob = random.nextDouble();
                
                if ( prob < percentageToModify){
                    modifiedPreferences++;
                    
                    contextComputer = new PeriodOfWeekAttributeComputer();
                    double contextValue = contextComputer.getAttributeValue(preference.getContext());
                    if (contextValue > 0.0){
                        if (contextAwareValue < maxRatingValue){
                            contextAwareValue += context1AddValue;
                            context1AddValue *= -1;
                            context1Modifs++;
                        }
                    }
                    else {
                        if (contextAwareValue < minRatingValue){
                            contextAwareValue += context2AddValue;
                            context2AddValue *= -1;
                            context2Modifs++;
                        }
                    }
                }                
                contextAwareModel.addPreference(preference.getUser(), preference.getItem(), contextAwareValue, preference.getContext());                
            }
        }
        logger.log(Level.INFO, "Modified {0} out of {1}preferences, ({2} + and {3} -)", new Object[]{modifiedPreferences, totalPreferences, context1Modifs, context2Modifs});
        return contextAwareModel;
    }
    
    public ModelIF<U,I,C> modifyRatingByTimestamp(ModelIF<U,I,C> model, double percentageToModify){
        
        ModelIF<U,I,C> contextAwareModel = null;
        try{
            contextAwareModel = model.getClass().newInstance();
        } catch (Exception e){
        System.err.println("Problem instatiating copy of model " + model.toString() + ": " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        Random random = new Random(0);

        int modifiedPreferences = 0;
        
        RatingOrderIF<U,I,C> order = new ROTime();
        List<PreferenceIF<U, I, C>> orderedPreferences = order.getOrderedRatings(model);
        
        int totalPreferences = orderedPreferences.size();
        int context1Modifs = 0;
        int context2Modifs = 0;
        
        int currentPreference = 0;
        for (PreferenceIF<U,I,C> preference : orderedPreferences){
            currentPreference++;
            float contextAwareValue = preference.getValue();
            double prob = random.nextDouble();

            if ( prob < percentageToModify){
                modifiedPreferences++;
                contextComputer = new PeriodOfWeekAttributeComputer();

                if (currentPreference > (totalPreferences / 2) ){
                    if (contextAwareValue < maxRatingValue){
                        contextAwareValue += 1;
                        context1Modifs++;
                    }
                }
                else {
                    if (contextAwareValue < minRatingValue){
                        contextAwareValue -= 1;
                        context2Modifs++;
                    }
                }
            }                
            contextAwareModel.addPreference(preference.getUser(), preference.getItem(), contextAwareValue, preference.getContext());                
        }
        logger.log(Level.INFO, "Modified {0} out of {1}preferences, ({2} + and {3} -)", new Object[]{modifiedPreferences, totalPreferences, context1Modifs, context2Modifs});
        return contextAwareModel;
    }
}
