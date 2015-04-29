package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.cars.itemsplitting.ContextBasedItemSplitterIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author pedro
 */
public class ItemSplittingExplicitModel<U, I, C extends ContextIF> implements ModelIF<U, I, C> {
    private ContextBasedItemSplitterIF<U,I,C> itemSplitter;
    private ModelIF<Object,Object,C> splitModel;

    public ItemSplittingExplicitModel(ContextBasedItemSplitterIF<U,I,C> itemSplitter, ModelIF<U,I,C> originalModel){
        this.itemSplitter = itemSplitter;
        this.splitModel = itemSplitter.splitModel(originalModel);
    }
    
    public Object getSplitItemID(U user, I item, C context){
        return itemSplitter.getSplitItemID(user, item, context);
    }

    public Collection<U> getUsers() {
        return (Collection<U>)splitModel.getUsers();
    }

    public Collection<I> getItems() {
        return (Collection<I>)splitModel.getItems();
    }

    public void addPreference(U user, I item, Float pref, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Float getPreferenceValue(U user, I item, C context) {
        return splitModel.getPreferenceValue(user, item, context);
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromUser(U user) {
        return (Collection<? extends PreferenceIF<U, I, C>>)splitModel.getPreferencesFromUser(user);
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromItem(I item) {
        return (Collection<? extends PreferenceIF<U, I, C>>)splitModel.getPreferencesFromItem(item);
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        return (Collection<? extends PreferenceIF<U, I, C>>)splitModel.getUniquePreferencesFromUser(user, (PreferenceAggregationFunction<Object, Object, C>)f);
    }

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f) {
        return (Collection<? extends PreferenceIF<U, I, C>>)splitModel.getUniquePreferencesFromItem(item, (PreferenceAggregationFunction<Object, Object, C>)f);
    }

    public Collection<? extends PreferenceIF<U, I, C>> getPreferences(U user, I item) {
        return (Collection<? extends PreferenceIF<U, I, C>>)splitModel.getPreferences(user, item);
    }
    
    public String getDetails(){
        return "IS["  + itemSplitter + "]";        
    }
}