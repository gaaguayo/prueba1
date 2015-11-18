package es.uam.eps.ir.core.model.impl;

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
public class GenericExplicitModel<U, I, C extends ContextIF> implements ModelIF<U, I, C> {
    private static final long serialVersionUID = 1001L;
    protected Map<U,Map<I,Collection<InternalPreference<C,Float>>>> theModel;
    private List<I> items;
    private Map<I, List<U>> itemUsersMap;
    boolean dirty = false;

    protected void checkDirty(){
        if (dirty) {
            dirty = false;
            sort();
        }
    }
    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        return getPreferencesFromUser(user);
//        checkDirty();
//        Collection<PreferenceIF<U,I,C>> preferences = new ArrayList<PreferenceIF<U,I,C>>();
//        Set<I> userItems = theModel.get(user).keySet();
//        for (I item : userItems){
//            preferences.add(f.getAggregatedValue(this.getPreferences(user, item)));
//        }
//        return preferences;
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f) {
        checkDirty();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericExplicitModel(){
        theModel = new HashMap();
        items = new ArrayList<I>();
        itemUsersMap = new HashMap<I, List<U>>();
    }    
    
    public Collection<U> getUsers(){
        checkDirty();
        return theModel.keySet();        
    }

    public Collection<I> getItems(){
        checkDirty();
        return items;
    }
    
    protected PreferenceIF<U, I, C> getPreferenceInstance(U user, I item, C context, Float pref){
        return new ExplicitPreference<U,I,C>(user, item, context, pref);
    }
    
    private void sort(){
        GenericExplicitModel<U,I,C> gem = new GenericExplicitModel<U,I,C>();
        List<U> users = new ArrayList(getUsers());
        Collections.sort((List)users);
        for (U user : users){
            List<PreferenceIF<U,I,C>> userPrefs = (List)getPreferencesFromUser(user);
            Collections.sort((List)userPrefs);
            for (PreferenceIF<U,I,C> pref : userPrefs){
                gem.addPreference(pref.getUser(), pref.getItem(), pref.getValue(), pref.getContext());
            }
        }
        
        this.theModel = gem.theModel;
        this.items = gem.items;
        this.itemUsersMap = gem.itemUsersMap;
    }
    
    public void addPreference(U user, I item, Float pref, C context){
        // Registers item key
        int posItem=Collections.binarySearch((List)items, item);
        if (posItem<0){
            // keys list
            items.add(-posItem-1,item);
        }
        List<U> itemUsers = itemUsersMap.get(item);
        if (itemUsers == null){
            itemUsers = new ArrayList<U>();
        }
        int posUser=Collections.binarySearch((List)itemUsers, user);
        if (posUser<0){
            // keys list
            itemUsers.add(-posUser-1,user);
        }
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
        dirty = true;
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
        checkDirty();
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
        checkDirty();
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
        checkDirty();
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
