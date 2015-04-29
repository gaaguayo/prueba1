package es.uam.eps.ir.analysis2;

import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.dataset.DatasetAttributesIF;
import es.uam.eps.ir.dataset.DatasetIF;
import es.uam.eps.ir.dataset.MultiValuedAttribute;
import es.uam.eps.ir.dataset.CommonDatasets;
import es.uam.eps.ir.dataset.Context_IRG.Context_Movies_IRG_Attributes;
import es.uam.eps.ir.dataset.ContextualDatasetIF;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LogFormatter;

/**
 *
 * @author pedro
 */
public class GenreContextAnalysis_Categorical {
    // Dataset
    static CommonDatasets.DATASET dataset_name = CommonDatasets.DATASET.Context_LDOS_CoMoDa;
    static DatasetIF<Object, Object, ContextIF> dataset;
    static DatasetAttributesIF atts;
    static ModelIF<Object, Object, ContextIF> model;
    final static Logger logger = Logger.getLogger("ExperimentLog");
    
    public static void main (String args[]){
        // Logger init
        //////////////
        Level level = Level.SEVERE;
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        
        
        dataset = new CommonDatasets(args).getDataset(dataset_name);
        atts = new Context_Movies_IRG_Attributes(dataset);
//        String context = "Company";
//        List<String> contextValues = Arrays.asList("alone","couple","family","friends","indifferent");
        String context = "daytype";
        List<String> contextValues = Arrays.asList("weekday","weekend","indifferent");
        model = dataset.getModel();
        final String file = "/datos/experiments/ItemAnalysis/" + dataset + "/ObservationsPerUserItemDistribution.txt";
        atts.readAttributes();
        
        StringBuilder title = new StringBuilder();
        title.append("Genre\t");
        for (String contextValue : contextValues){
            title.append("Genre_").append(contextValue).append("_Avg\t");
        }
        title.append("Genre_Avg\t");
        for (String contextValue : contextValues){
            title.append(contextValue).append("_Avg\t");
        }
        title.append("Avg");
        
        System.out.println(title);
        for (String genre : Context_Movies_IRG_Attributes.GENRES){

            System.out.print(genre + "\t");
            
            ContextDefinition ctxDef = null;
            List<ContextDefinition> allContextDefinitions = ((ContextualDatasetIF)dataset).getContextDefinitions();
            for (ContextDefinition _ctxDef : allContextDefinitions){
                if (_ctxDef.getName().equalsIgnoreCase(context)){
                    ctxDef = _ctxDef;
                }
            }

            for (String contextValue : contextValues){
//            for (String contextValue : Arrays.asList("Morning","Noon","Evening","Night")){
                System.out.print(getItemGenreAvgRating(genre, ctxDef, contextValue) + "\t");
            }
            System.out.print(getItemGenreAvgRating(genre) + "\t");
            for (String contextValue : contextValues){
                System.out.print(getItemAvgRating(ctxDef, contextValue) + "\t");
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

    public static float getItemGenreAvgRating(String genre, ContextDefinition ctxDef, String contextValue){
        float avg = 0;
        int count = 0;

        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            MultiValuedAttribute att = atts.getItemAttribute(item, "Genre");
            if (att.containsNominalValue(genre)){
                Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromItem(item);
                for (PreferenceIF pref : preferences){
                    ContextContainer container = (ContextContainer)pref.getContext();
                    CategoricalContext ctx = container.getCategoricalContext(ctxDef);
                    String contextNominalValue = ctxDef.getNominalValue(  ctx.getValue() );
                                        
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
    
    public static float getItemAvgRating(ContextDefinition ctxDef, String contextValue){
        float avg = 0;
        int count = 0;

        for (Object item : model.getItems()){
            float itemAvg = 0;
            int itemCount = 0;
            Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromItem(item);
            for (PreferenceIF pref : preferences){
                ContextContainer container = (ContextContainer)pref.getContext();
                CategoricalContext ctx = container.getCategoricalContext(ctxDef);
                String contextNominalValue = ctxDef.getNominalValue(  ctx.getValue() );
                
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
            Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromItem(item);
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
                Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromItem(item);
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
                Collection<? extends PreferenceIF<Object, Object, ContextIF>> preferences = model.getPreferencesFromItem(item);
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
