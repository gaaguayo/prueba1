package es.uam.eps.ir.split;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class SimpleModel<U,I,C extends ContextIF> implements ModelIF<U,I,C>{
    Set<I> items;
    
    private Map<U,Map<I,Collection<PreferenceIF<U,I,C>>>> theModel;

    public SimpleModel() {
        theModel = new HashMap<U,Map<I,Collection<PreferenceIF<U,I,C>>>>();
        items = new HashSet<I>();
    }

    public Collection<U> getUsers() {
        return theModel.keySet();
    }

    public Collection<I> getItems() {
        return items;
    }

    public void addPreference(U user, I item, Float pref, C context) {
        Map<I,Collection<PreferenceIF<U,I,C>>> itemPrefsMap = theModel.get(user);
        if (itemPrefsMap == null){
            itemPrefsMap = new HashMap<I,Collection<PreferenceIF<U,I,C>>>();
        }
        Collection<PreferenceIF<U,I,C>> prefs = itemPrefsMap.get(item);
        if (prefs == null){
            prefs = new ArrayList<PreferenceIF<U,I,C>>();
        }
        prefs.add(new SimplePreference(user, item, pref, context));
        itemPrefsMap.put(item, prefs);
        theModel.put(user, itemPrefsMap);
        items.add(item);
    }

    public Float getPreferenceValue(U user, I item, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromUser(U user) {
        Collection<PreferenceIF<U,I,C>> prefs = new ArrayList<PreferenceIF<U,I,C>>();
        Map<I,Collection<PreferenceIF<U,I,C>>> itemPrefMap = theModel.get(user);
        if (itemPrefMap == null){ return prefs; }
        for (I item : itemPrefMap.keySet()){
            prefs.addAll(itemPrefMap.get(item));
        }
        return prefs;
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromItem(I item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferences(U user, I item) {
        Map<I,Collection<PreferenceIF<U,I,C>>> itemPrefsMap = theModel.get(user);
        return itemPrefsMap.get(item);
    }
    
}
