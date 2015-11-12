package es.uam.eps.ir.analysis2;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.dataset.DatasetAttributesIF;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.MultiValuedAttribute;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.Context_IRG.Context_Movies_IRG_Attributes;
import es.uam.eps.ir.cars.inferred.CategoricalContextComputerIF;
import es.uam.eps.ir.cars.inferred.PeriodOfDayAttributeComputer4;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author pedro
 */
public class GenreContextAnalysis_Continuous {
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.Context_Movies_IRG;
    static DatasetIF<Object, Object, ContinuousTimeContextIF> dataset;
    static DatasetAttributesIF atts;
    static ModelIF<Object, Object, ContinuousTimeContextIF> model;
    
    public static void main (String args[]){
        dataset = new CommonDatasets(args).getDataset(dataset_name);
        atts = new Context_Movies_IRG_Attributes(dataset);
        model = dataset.getModel();
        final String file = "/datos/experiments/ItemAnalysis/" + dataset + "/ObservationsPerUserItemDistribution.txt";
        atts.readAttributes();
        for (String genre : Context_Movies_IRG_Attributes.GENRES){

            System.out.print(genre + "\t");
            CategoricalContextComputerIF computer;
            computer = new PeriodOfDayAttributeComputer4();
            for (String contextValue : Arrays.asList("Morning","Afternoon","Night")){
//            for (String contextValue : Arrays.asList("Morning","Noon","Evening","Night")){
                System.out.print(getItemGenreAvgRating(genre, computer, contextValue) + "\t");
            }
            System.out.print(getItemGenreAvgRating(genre) + "\t");
            for (String contextValue : Arrays.asList("Morning","Afternoon","Night")){
                System.out.print(getItemAvgRating(computer, contextValue) + "\t");
            }
            System.out.println(getAvgRating());
            
//            System.out.println("Freq of " + genre + ": " + getItemGenreCount(genre));
//            System.out.println("Avg of " + genre + ": " + getItemGenreAvgRating(genre));
//            ContextualAttributeComputerIF computer;
//            
//            computer = new PeriodOfWeekAttributeComputer();
//            for (String contextValue : Arrays.asList("Workday","Weekend")){
//                System.out.println("Avg of " + contextValue + ": " + getItemAvgRating(computer, contextValue));
//                System.out.println("Avg of " + genre + "," + contextValue + ": " + getItemGenreAvgRating(genre, computer, contextValue));
//                
//            }
//            computer = new PeriodOfDayAttributeComputer();
//            for (String contextValue : Arrays.asList("Night","Morning","Noon","Evening")){
//                System.out.println("Avg of " + contextValue + ": " + getItemAvgRating(computer, contextValue));
//                System.out.println("Avg of " + genre + "," + contextValue + ": " + getItemGenreAvgRating(genre, computer, contextValue));
//                
//            }
        }
    }

    public static float getItemGenreAvgRating(String genre, CategoricalContextComputerIF computer, String contextValue){
        float avg = 0;
        int count = 0;

        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            MultiValuedAttribute att = atts.getItemAttribute(item, "Genre");
            if (att.containsNominalValue(genre)){
                Collection<? extends PreferenceIF<Object, Object, ContinuousTimeContextIF>> preferences = model.getPreferencesFromItem(item);
                for (PreferenceIF pref : preferences){
                    String contextNominalValue = computer.getAttributeNominalValue(pref.getContext());
                    if (contextNominalValue.equalsIgnoreCase(contextValue)){
                        itemAvg += pref.getValue();
                        itemCount++;

                        avg +=  pref.getValue();
                        count++;
                    }
                }
            }
        }
        avg /= count;
        
        return avg;
    }
    
    public static float getItemAvgRating(CategoricalContextComputerIF computer, String contextValue){
        float avg = 0;
        int count = 0;

        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            Collection<? extends PreferenceIF<Object, Object, ContinuousTimeContextIF>> preferences = model.getPreferencesFromItem(item);
            for (PreferenceIF pref : preferences){
                String contextNominalValue = computer.getAttributeNominalValue(pref.getContext());
                if (contextNominalValue.equalsIgnoreCase(contextValue)){
                    itemAvg += pref.getValue();
                    itemCount++;

                    avg +=  pref.getValue();
                    count++;
                }
            }
        }
        avg /= count;
        
        return avg;
    }
    
    public static float getAvgRating(){
        float avg = 0;
        int count = 0;

        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            Collection<? extends PreferenceIF<Object, Object, ContinuousTimeContextIF>> preferences = model.getPreferencesFromItem(item);
            for (PreferenceIF pref : preferences){
                itemAvg += pref.getValue();
                itemCount++;

                avg +=  pref.getValue();
                count++;
            }
        }
        avg /= count;
        
        return avg;
    }
    
    public static float getItemGenreAvgRating(String genre, String contextName, String contextValue){
        float avg = 0;
        int count = 0;
        
        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            MultiValuedAttribute att = atts.getItemAttribute(item, "Genre");
            if (att.containsNominalValue(genre)){
                Collection<? extends PreferenceIF<Object, Object, ContinuousTimeContextIF>> preferences = model.getPreferencesFromItem(item);
                for (PreferenceIF pref : preferences){
                    itemAvg += pref.getValue();
                    itemCount++;
                    
                    avg +=  pref.getValue();
                    count++;
                }
            }
        }
        avg /= count;
        
        return avg;
    }
    
    
    public static float getItemGenreAvgRating(String genre){
        float avg = 0;
        int count = 0;
        
        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            MultiValuedAttribute att = atts.getItemAttribute(item, "Genre");
            if (att.containsNominalValue(genre)){
                Collection<? extends PreferenceIF<Object, Object, ContinuousTimeContextIF>> preferences = model.getPreferencesFromItem(item);
                for (PreferenceIF pref : preferences){
                    itemAvg += pref.getValue();
                    itemCount++;
                    
                    avg +=  pref.getValue();
                    count++;
                }
            }
        }
        avg /= count;
        
        return avg;
    }
    
    public static int getItemGenreCount(String genre){
        int result = 0;
        
        for (Object item : model.getItems()){
            MultiValuedAttribute att = atts.getItemAttribute(item, "Genre");
            if (att.containsNominalValue(genre)){
                result++;
            }
        }
        
        return result;
    }
}
