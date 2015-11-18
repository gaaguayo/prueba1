package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.cars.inferred.TimeContextDefinition;
import es.uam.eps.ir.core.model.ImplicitFeedbackModelIF;
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
public class CategoricalContextItemSplitter<U,I,C extends ContextIF> implements ContextBasedItemSplitterIF<U,I,C>{
    private ImpurityComputerIF<U,I,C> impurityComputer;
    private List<ContextDefinition> contextsForSplitting;
    private int minContextSize = 5;
    private final static Logger logger = Logger.getLogger("ExperimentLog");
//    private Map<U,Map<I,String>> useritemSplititemMap;
    private Map<I,Pair<ContextDefinition,String>> item_SplitContextMap;
    
    public CategoricalContextItemSplitter(ImpurityComputerIF<U, I, C> impurityComputer, List<ContextDefinition> contextsForSplitting) {
        this.impurityComputer = impurityComputer;
        this.contextsForSplitting = contextsForSplitting;
//        this.useritemSplititemMap = new HashMap<U,Map<I,String>>();
        this.item_SplitContextMap = new HashMap<I,Pair<ContextDefinition,String>>();
    }
    
    public CategoricalContextItemSplitter() {
        this.impurityComputer = new MeanImpurity();
        this.contextsForSplitting = new ArrayList<ContextDefinition>();
        this.item_SplitContextMap = new HashMap<I,Pair<ContextDefinition,String>>();
    }
    
    public void addContextDefinition(ContextDefinition ctxDef){
        contextsForSplitting.add(ctxDef);
    }

    public void setMinContextSize(int minContextSize) {
        this.minContextSize = minContextSize;
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
    
    private String getContextNominalValue(C context, ContextDefinition ctxDef){
        String contextNominalValue = null;

        if (ctxDef instanceof TimeContextDefinition){
            contextNominalValue = ((TimeContextDefinition)ctxDef).getNominalValue((ContinuousTimeContextIF)context);                
        }
        else { //assuming categorical context, which must be in a ContextContainer
            ContextContainer container = (ContextContainer)context;
            CategoricalContext ctxVar = (CategoricalContext)container.getCategoricalContext(ctxDef);
            contextNominalValue = ctxDef.getNominalValue(ctxVar.getValue());
        }
        return contextNominalValue;
    }
    
    public Object getSplitItemID(U user, I item, C context) {
        try{
            Pair<ContextDefinition,String> context_valuePair = item_SplitContextMap.get(item);
            if (context_valuePair == null){
                return item;
            }
            
            ContextDefinition ctxDef = context_valuePair.getValue1();
            String contextNominalValue = getContextNominalValue(context, ctxDef);
            
            String splitItemID;
            if (contextNominalValue.equalsIgnoreCase(context_valuePair.getValue2())){
                splitItemID = "" + item + "_" + ctxDef.getName()  + "_" + contextNominalValue;
            }
            else{
                splitItemID = "" + item + "_" + ctxDef.getName()  + "_Other";                
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
        StringBuilder sbCtx = new StringBuilder();
        for (ContextDefinition ctxDef : contextsForSplitting){
            sbCtx.append(" ").append(ctxDef.getName());
        }        
        logger.log(Level.INFO, "Contexts:{0}", sbCtx);
        boolean implicitData = model instanceof ImplicitFeedbackModelIF;
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
            ContextDefinition ctxDef_A = contextsForSplitting.get(0);
            Collection<PreferenceIF<U,I,C>> maxPreferencesA = null;
            Collection<PreferenceIF<U,I,C>> maxPreferencesB = null;
            
            logger.log(Level.CONFIG, "requesting item {0} ({1} of {2})", new Object[]{item, currentItem, items.size()});
            Collection<? extends PreferenceIF<U,I,C>> itemPreferences = model.getPreferencesFromItem(item);
            logger.log(Level.CONFIG, "splitting item {0} ({1} of {2})", new Object[]{item, currentItem, items.size()});
            for (ContextDefinition ctxDef : contextsForSplitting){
                Map<String, Collection<PreferenceIF<U,I,C>>> contextSplits = getContextSplits(itemPreferences, ctxDef);
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
                        ctxDef_A = ctxDef;
                    }
                }
            }
            if (maxImpurity > impurityComputer.impurityThreshold()){
                thresholdAccomplishments++;
//                for (PreferenceIF<U,I,C> preference : itemPreferences){
//                    if (maxPreferencesA.contains(preference)){ // is in maxPreferencesA
//                        String newItemA = "" + preference.getItem() + "_" + ctxDef_A.getName()  + "_" + context_A;
//                        splitModel.addPreference(preference.getUser(), newItemA, preference.getValue(), preference.getContext());                        
//                    }
//                    else {
//                        String newItemB = "" + preference.getItem() + "_" + ctxDef_A.getName() + "_Other";
//                        splitModel.addPreference(preference.getUser(), newItemB, preference.getValue(), preference.getContext());                        
//                    }
//                }
                for (PreferenceIF<U,I,C> preference : maxPreferencesA){
                    U user = preference.getUser();
                    String newItemA = "" + preference.getItem() + "_" + ctxDef_A.getName()  + "_" + context_A;
                    splitModel.addPreference(user, newItemA, preference.getValue(), preference.getContext());
                    
//                    Map<I,String> itemSplititemMap = useritemSplititemMap.get(user);
//                    if (itemSplititemMap == null){
//                        itemSplititemMap = new HashMap<I,String>();
//                    }
//                    itemSplititemMap.put(item, newItemA);
//                    useritemSplititemMap.put(user, itemSplititemMap);
                    
                    Pair<ContextDefinition,String> context_valuePair = item_SplitContextMap.get(item);
                    if (context_valuePair == null){
                        context_valuePair = new Pair<ContextDefinition,String>(ctxDef_A, context_A);
                    }
                    item_SplitContextMap.put(item, context_valuePair);
                }
                for (PreferenceIF<U,I,C> preference : maxPreferencesB){
                    U user = preference.getUser();
                    String newItemB = "" + preference.getItem() + "_" + ctxDef_A.getName() + "_Other";
                    splitModel.addPreference(preference.getUser(), newItemB, preference.getValue(), preference.getContext());
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
        return finalModel;
    }    
    
    
    public Map<String, Collection<PreferenceIF<U, I, C>>> getContextSplits(Collection<? extends PreferenceIF<U, I, C>> preferences, ContextDefinition ctxDef) {
        Map<String, Collection<PreferenceIF<U,I,C>>> contextSplits = new HashMap<String, Collection<PreferenceIF<U,I,C>>>();
        
        for (PreferenceIF<U,I,C> pref: preferences){
            C context = pref.getContext();
            String contextNominalValue = getContextNominalValue(context, ctxDef);
                        
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
        name.append(impurityComputer.getClass().getSimpleName()).append("(").append(impurityComputer.impurityThreshold()).append(",").append(minContextSize).append(name).append(")");
        name.append("C=");
        for (ContextDefinition context : contextsForSplitting){
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
                uniqueRating = getNormalizedRating(pair.getValue1(), uniqueRating, util);
            }
            PreferenceIF<U,I,C> pref = new ExplicitPreference<U,I,C>(pair.getValue1(), pair.getValue2(), uniqueContexts.get(pair), uniqueRating);
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
