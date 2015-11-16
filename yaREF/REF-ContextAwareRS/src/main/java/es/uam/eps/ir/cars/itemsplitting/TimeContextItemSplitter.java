 package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.cars.inferred.CategoricalContextComputerIF;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder;
import es.uam.eps.ir.cars.inferred.ContinuousTimeContextComputerBuilder.TimeContext;
import es.uam.eps.ir.cars.model.ImplicitDataIF;
import es.uam.eps.ir.core.context.CategoricalContext;
import es.uam.eps.ir.core.context.ContextContainer;
import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro G. Campos
 */
public class TimeContextItemSplitter<U,I,C extends ContinuousTimeContextIF> implements ContextBasedItemSplitterIF<U,I,C>{
    private ImpurityComputerIF<U,I,C> impurityComputer;
    private List<TimeContext> contextsForSplitting;
    private int minContextSize = 5;
    private final static Logger logger = Logger.getLogger("ExperimentLog");
//    private Map<U,Map<I,String>> useritemSplititemMap;
    private Map<I,Pair<TimeContext,String>> item_SplitContextMap;
    
    public TimeContextItemSplitter(ImpurityComputerIF<U, I, C> impurityComputer, List<TimeContext> contextsForSplitting) {
        this.impurityComputer = impurityComputer;
        this.contextsForSplitting = contextsForSplitting;
//        this.useritemSplititemMap = new HashMap<U,Map<I,String>>();
        this.item_SplitContextMap = new HashMap<I,Pair<TimeContext,String>>();
    }
    
    public TimeContextItemSplitter() {
        this.impurityComputer = new MeanImpurity();
        this.contextsForSplitting = Arrays.asList(TimeContext.values());
        this.item_SplitContextMap = new HashMap<I,Pair<TimeContext,String>>();
    }

    public void setMinContextSize(int minContextSize) {
        this.minContextSize = minContextSize;
    }
    
    public static CategoricalContextComputerIF getContextComputer(TimeContext timeContext) {
        return ContinuousTimeContextComputerBuilder.getContextComputer(timeContext);
    }

//    public Object getSplitItemID_old(U user, I item, C context) {
//        try{
//            Map<I,String> itemSplititemMap = useritemSplititemMap.get(user);
//            Object splitItemID = itemSplititemMap.get(item);
//            if (splitItemID == null){
//                return item;
//            }
//            return splitItemID;
//        }
//        catch (NullPointerException e){
//            return item;
//        }
//    }
    
    public Object getSplitItemID(U user, I item, C context) {
        try{
            Pair<TimeContext,String> context_valuePair = item_SplitContextMap.get(item);
            if (context_valuePair == null){
                return item;
            }
            
            TimeContext ctxDef = context_valuePair.getUser();
            
            TimeContext timeContext = null;
            for (TimeContext _timeContext : contextsForSplitting){
                if (_timeContext.compareTo(ctxDef) == 0){
                    timeContext = _timeContext;
                    break;                    
                }
            }
            CategoricalContextComputerIF contextComputer = getContextComputer(timeContext);
            String contextNominalValue = contextComputer.getAttributeNominalValue(context);

            String splitItemID;
            if (contextNominalValue.equalsIgnoreCase(context_valuePair.getItem())){
                splitItemID = "" + item + "_" + ctxDef.name()  + "_" + contextNominalValue;
            }
            else{
                splitItemID = "" + item + "_" + ctxDef.name()  + "_Other";                
            }            
            return splitItemID;
            
        }  
        catch (NullPointerException e){
            return item;
        }
    }
    
    
    @SuppressWarnings("CallToThreadDumpStack")
    public ModelIF<Object,Object,C> splitModel(ModelIF model) {
        logger.log(Level.INFO,"ItemSplitting started");
        boolean implicitData = model instanceof ImplicitDataIF;
        ModelIF<Object,Object,C> splitModel = null;
        ModelIF<Object,Object,C> finalModel = null;
        try{
            splitModel = model.getClass().newInstance();
            finalModel = model.getClass().newInstance();
        } catch (Exception e){
        System.err.println("Problem instatiating copy of model " + model.toString() + ": " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        int totalCombinations = 0;
        int sizeAccomplishments = 0;
        int thresholdAccomplishments = 0;
        
        ContextualModelUtils<U,I,C> util = new ContextualModelUtils<U,I,C>(model);
        
        Collection<I> items = model.getItems();
        int currentItem = 0;
        for (I item: items){
            logger.log(Level.CONFIG, "processing item {0} ({1} of {2})", new Object[]{item, ++currentItem, items.size()});
            double maxImpurity = Double.NEGATIVE_INFINITY;
            String context_A = "";
            TimeContext timeContext_A = TimeContext.Meridian;
            Collection<PreferenceIF<U,I,C>> maxPreferencesA = null;
            Collection<PreferenceIF<U,I,C>> maxPreferencesB = null;
            
            logger.log(Level.CONFIG, "requesting item {0} ({1} of {2})", new Object[]{item, currentItem, items.size()});
            Collection<? extends PreferenceIF<U,I,C>> itemPreferences = model.getPreferencesFromItem(item);
            logger.log(Level.CONFIG, "splitting item {0} ({1} of {2})", new Object[]{item, currentItem, items.size()});
            for (TimeContext timeContext : contextsForSplitting){
                Map<String, Collection<PreferenceIF<U,I,C>>> contextSplits = getContextSplits(itemPreferences, getContextComputer(timeContext));
                for (String contextSplitA : contextSplits.keySet()){
                    totalCombinations++;
                    Collection<PreferenceIF<U,I,C>> preferencesA = contextSplits.get(contextSplitA);
                    Collection<PreferenceIF<U,I,C>> uniquePreferencesA = getUniquePreferences(preferencesA, implicitData, util);
                    Collection<PreferenceIF<U,I,C>> preferencesB = new ArrayList<PreferenceIF<U,I,C>>();
                    for (String contextSplitB : contextSplits.keySet()){
                        if (contextSplitA.equalsIgnoreCase(contextSplitB)) { continue; }
                        preferencesB.addAll(contextSplits.get(contextSplitB));
                    }
                    Collection<PreferenceIF<U,I,C>> uniquePreferencesB = getUniquePreferences(preferencesB, implicitData, util);
                    if (uniquePreferencesA.size() < minContextSize || uniquePreferencesB.size() < minContextSize) { continue; }
                    sizeAccomplishments++;
                    double impurity = impurityComputer.getImpurity(uniquePreferencesA, uniquePreferencesB);
                    if (impurity > maxImpurity){
                        maxImpurity = impurity;
                        maxPreferencesA = new ArrayList<PreferenceIF<U,I,C>>(preferencesA);
                        maxPreferencesB = new ArrayList<PreferenceIF<U,I,C>>(preferencesB);
                        context_A = contextSplitA;
                        timeContext_A = timeContext;
                    }
                }
            }
            if (maxImpurity > impurityComputer.impurityThreshold()){
                thresholdAccomplishments++;
                for (PreferenceIF<U,I,C> preference : maxPreferencesA){
                    U user = preference.getUser();
                    String newItemA = "" + preference.getItem() + "_" + timeContext_A.name()  + "_" + context_A;
                    splitModel.addPreference(user, newItemA, preference.getValue(), preference.getContext());

//                    Map<I,String> itemSplititemMap = useritemSplititemMap.get(user);
//                    if (itemSplititemMap == null){
//                        itemSplititemMap = new HashMap<I,String>();
//                    }
//                    itemSplititemMap.put(item, newItemA);
//                    useritemSplititemMap.put(user, itemSplititemMap);

                    Pair<TimeContext,String> context_valuePair = item_SplitContextMap.get(item);
                    if (context_valuePair == null){
                        context_valuePair = new Pair<TimeContext,String>(timeContext_A, context_A);
                    }
                    item_SplitContextMap.put(item, context_valuePair);                    
                }
                for (PreferenceIF<U,I,C> preference : maxPreferencesB){
                    U user = preference.getUser();
                    String newItemB = "" + preference.getItem() + "_" + timeContext_A.name() + "_Other";
                    splitModel.addPreference(user, newItemB, preference.getValue(), preference.getContext());
//                    Map<I,String> itemSplititemMap = useritemSplititemMap.get(user);
//                    if (itemSplititemMap == null){
//                        itemSplititemMap = new HashMap<I,String>();
//                    }
//                    itemSplititemMap.put(item, newItemB);
//                    useritemSplititemMap.put(user, itemSplititemMap);
                }
            }
            else{
                for (PreferenceIF<U,I,C> preference : itemPreferences){
                    splitModel.addPreference(preference.getUser(), "" + preference.getItem(), preference.getValue(), preference.getContext());
                }                
            } 
        }

        List users = new ArrayList(splitModel.getUsers());
        Collections.sort(users);
        for (Object user : users){
            List<PreferenceIF<Object,Object,C>> userPrefs = (List)splitModel.getPreferencesFromUser(user);
            Collections.sort((List)userPrefs);
            for (PreferenceIF<Object,Object,C> pref : userPrefs){
                finalModel.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
            }
        }
        
        
        logger.log(Level.INFO, "totalCombinations       ={0}", totalCombinations);
        logger.log(Level.INFO, "sizeAccomplishments     ={0}", sizeAccomplishments);
        logger.log(Level.INFO, "items                   ={0}", model.getItems().size());
        logger.log(Level.INFO, "thresholdAccomplishments={0}", thresholdAccomplishments);
//        System.exit(0);
        
        logger.log(Level.INFO, "ItemSplitting finished");
        return splitModel;
    }    
    
    
    public Map<String, Collection<PreferenceIF<U, I, C>>> getContextSplits(Collection<? extends PreferenceIF<U, I, C>> preferences, CategoricalContextComputerIF contextComputer) {
        Map<String, Collection<PreferenceIF<U,I,C>>> contextSplits = new HashMap<String, Collection<PreferenceIF<U,I,C>>>();
        
        for (PreferenceIF<U,I,C> pref: preferences){
            String contextNominalValue = contextComputer.getAttributeNominalValue(pref.getContext());
            
            Collection<PreferenceIF<U,I,C>> splitPreferences = contextSplits.get(contextNominalValue);
            if (splitPreferences == null){
                splitPreferences = new ArrayList<PreferenceIF<U,I,C>>();
            }
            splitPreferences.add(pref);
            contextSplits.put(contextNominalValue, splitPreferences);
        }
        
        return contextSplits;
    }
    
    @Override
    public String toString(){
        StringBuilder name = new StringBuilder();
        name.append(impurityComputer.getClass().getSimpleName()).append("(").append(impurityComputer.impurityThreshold()).append(",").append(minContextSize).append(")");
        name.append("C=");
        for (TimeContext context : contextsForSplitting){
            name.append(context.toString());
        }
        return name.toString();
    }
    
    private Collection<PreferenceIF<U,I,C>> getUniquePreferences(Collection<PreferenceIF<U,I,C>> preferences, boolean implicitData, ContextualModelUtils<U,I,C> util){
        Collection uniquePreferences = new ArrayList<PreferenceIF<U,I,C>>();
        Map<Pair<U,I>,Float> uniqueRatings = new HashMap<Pair<U,I>,Float>();
        Map<Pair<U,I>,C> uniqueContexts = new HashMap<Pair<U,I>,C>();
        for (PreferenceIF<U,I,C> pref: preferences){
            Pair pair = new Pair(pref.getUser(), pref.getItem());
            
            Float uniqueRating = uniqueRatings.get(pair);
            if (uniqueRating == null){
                uniqueRating = new Float(0);
                uniqueContexts.put(pair, pref.getContext()); // just 1 context is needed
            }
            uniqueRating += pref.getValue();
            uniqueRatings.put(pair, uniqueRating);           
        }
        
        for (Pair<U,I> pair : uniqueRatings.keySet()){
            Float uniqueRating = uniqueRatings.get(pair);
            if (implicitData){
                uniqueRating = getNormalizedRating(pair.getUser(), uniqueRating, util);
            }
            PreferenceIF<U,I,C> pref = new ExplicitPreference<U,I,C>(pair.getUser(), pair.getItem(), uniqueContexts.get(pair), uniqueRating);
            uniquePreferences.add(pref);
        }
        return uniquePreferences;
    }
    
    private Float getNormalizedRating(U user, Float itemFrequency, ContextualModelUtils<U,I,C> util){
        // normalization of values
        float minRating = new Float(1);
        float maxRating = new Float(5);
        Float minUserRating = util.getMinRating(user);
        Float maxUserRating = util.getMaxRating(user);
        Float userStep = (maxUserRating - minUserRating) / (maxRating - minRating);
        int steps = (int)maxRating - (int)minRating;
        float normalizedRating = minRating;
        for (int i = 0; i < maxRating && itemFrequency >= minUserRating + i*userStep; i++){
            normalizedRating = i + 1;
        }
        return normalizedRating;
    }
    
}
