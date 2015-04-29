package es.uam.eps.ir.core.model;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.Collection;

/**
 *
 * @author Alejandro Bellogin
 */
public interface ModelIF<U, I, C extends ContextIF> {

    public Collection<U> getUsers();

    public Collection<I> getItems();

    public void addPreference(U user, I item, Float pref, C context);

    public Float getPreferenceValue(U user, I item, C context);

    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromUser(U user);
    
    public Collection<? extends PreferenceIF<U, I, C>> getPreferencesFromItem(I item);

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f);

    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromItem(I item, PreferenceAggregationFunction<U, I, C> f);

    public Collection<? extends PreferenceIF<U, I, C>> getPreferences(U user,I item);

}
