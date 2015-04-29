package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class IndexedImplicitModel<U, I, C extends ContextIF> implements ModelIF<U, I, C> {
    private static final long serialVersionUID = 1001L;
    private final Map<U, Integer> userIndex;
    private final Map<I, Integer> itemIndex;
    private final Map<U,Map<I,Collection<InternalPreference<C,Float>>>> theModel;
    private final List<I> items;
    private final Map<I, List<U>> itemUsersMap;

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        Collection<PreferenceIF<U,I,C>> preferences = new ArrayList<PreferenceIF<U,I,C>>();
        Set<I> userItems = theModel.get(user).keySet();
        for (I item : userItems){
            preferences.add(f.getAggregatedValue(this.getPreferences(user, item)));
        }
        return preferences;
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IndexedImplicitModel(){
        theModel = new HashMap();
        items = new ArrayList<I>();
        itemUsersMap = new HashMap<I, List<U>>();
        userIndex = new HashMap();
        itemIndex = new HashMap();
    }    
    
    public Collection<U> getUsers(){
        return theModel.keySet();        
    }

    public Collection<I> getItems(){
        return items;
    }
    
    protected PreferenceIF<U, I, C> getPreferenceInstance(U user, I item, C context, Float pref){
        return new ExplicitPreference<U,I,C>(user, item, context, pref);
    }
    
    public void addPreference(U user, I item, Float pref, C context){
        // Looks for --create if needed-- user and item index
        Integer userIdx = userIndex.get(user);
        if (userIdx == null){
            userIdx = userIndex.size() + 1;
            userIndex.put(user, userIdx);
        }
        
        Integer itemIdx = itemIndex.get(item);
        if (itemIdx == null){
            itemIdx = itemIndex.size() + 1;
            itemIndex.put(item, itemIdx);
        }
        
        // Registers item key
        int pos=Collections.binarySearch((List)items, item);
        if (pos<0){
            // keys list
            items.add(-pos-1,item);
        }
        List<U> itemUsers = itemUsersMap.get(item);
        if (itemUsers == null){
            itemUsers = new ArrayList<U>();
        }
        itemUsers.add(user);
        itemUsersMap.put(item, itemUsers);
        
        
        // Checks map of items prefered by user
        Map<I,Collection<InternalPreference<C,Float>>> userItemsMap=theModel.get(user);
        if (userItemsMap == null){
            userItemsMap = new HashMap<I,Collection<InternalPreference<C,Float>>>();
        }
        
        // Checks collection(list) of preferences for the item (in different contexts)
        Collection<InternalPreference<C,Float>> itemPreferences=userItemsMap.get(item);
        if (itemPreferences == null){
            itemPreferences = new ArrayList<InternalPreference<C,Float>>();
        }
        
        // Adds new preference
        InternalPreference<C,Float> thePair= new InternalPreference<C, Float>(context, pref);
        itemPreferences.add(thePair);
        userItemsMap.put(item, itemPreferences);
        theModel.put(user, userItemsMap);
    }

    public Float getPreferenceValue(U user, I item, C context){
        Map<I,Collection<InternalPreference<C,Float>>> userItemsMap=theModel.get(user);
        if (userItemsMap == null){
            return null;
        }
        
        Collection<InternalPreference<C,Float>> itemPreferences=userItemsMap.get(item);
        if (itemPreferences == null){
            return null;
        }
        
        return getPreferenceValue(itemPreferences, context);
    }
    
    protected Float getPreferenceValue(Collection<InternalPreference<C,Float>> itemPreferences, C context){
        if (context == null){
            return ((InternalPreference<C, Float>)itemPreferences.toArray()[0]).pref;         
        }
        
        for (InternalPreference<C, Float> itemPref : itemPreferences){
            C valueContext = itemPref.getContext();
            if (valueContext == null) {
                return null;
            }
            else if (itemPref.getContext().compareTo(context) == 0){
                return itemPref.pref;
            }
        }
        
        return null;
    }
    
    
    public Collection<? extends PreferenceIF<U, I, C>> getPreferences(U user,I item){
        Map<I,Collection<InternalPreference<C,Float>>> itemPreferencesMap=theModel.get(user);
        if (itemPreferencesMap == null){
            return null;
        }
        
        try{
            Collection<PreferenceIF<U,I,C>> preferences = new ArrayList<PreferenceIF<U,I,C>>();
            for (InternalPreference<C, Float> ip : itemPreferencesMap.get(item)){
                C context = ip.getContext();
                Float pref = ip.getPref();
                PreferenceIF<U,I,C> preference = getPreferenceInstance(user, item, context, pref);
                preferences.add(preference);
            }

            return preferences;
        } catch (NullPointerException e){
            return null;
        }
        
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromUser(U user){
        Map<I,Collection<InternalPreference<C,Float>>> userItemsMap=theModel.get(user);
        if (userItemsMap == null){
            return null;
        }
        
        try {
            Collection<PreferenceIF<U,I,C>> userPreferences = new ArrayList<PreferenceIF<U,I,C>>();
            for (I item:userItemsMap.keySet()){
                for (InternalPreference<C, Float> ip : userItemsMap.get(item)){
                    C context = ip.getContext();
                    Float pref = ip.getPref();
                    PreferenceIF<U,I,C> preference = getPreferenceInstance(user, item, context, pref);
                    userPreferences.add(preference);
                }
            }

            return userPreferences;
        } catch (NullPointerException e){
            return null;
        }
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromItem(I item){
        Collection<U> itemUsers = itemUsersMap.get(item);
        if (itemUsers == null){
            return null;
        }
        
        Collection<PreferenceIF<U,I,C>> itemPreferences = new ArrayList<PreferenceIF<U,I,C>>();
        for (U user:itemUsers){
            Map<I,Collection<InternalPreference<C,Float>>> userItemsMap=theModel.get(user);
            for (InternalPreference<C, Float> ip : userItemsMap.get(item)){
                C context = ip.getContext();
                Float pref = ip.getPref();
                PreferenceIF<U,I,C> preference = getPreferenceInstance(user, item, context, pref);
                itemPreferences.add(preference);
            }            
        }        
        
        return itemPreferences;
    }
    
    protected class InternalPreference<C, Float>{
        C context;
        Float pref;

        public InternalPreference(C context, Float pref) {
            this.context = context;
            this.pref = pref;
        }

        public C getContext() {
            return context;
        }

        public Float getPref() {
            return pref;
        }
    }
}
