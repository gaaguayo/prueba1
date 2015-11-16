package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.model.ImplicitFeedbackModelIF;
import es.uam.eps.ir.core.model.impl.ImplicitPreference;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Pedro G. Campos
 */
public class GenericImplicitModel<U, I, C extends ContextIF> extends GenericExplicitModel<U,I,C> implements ModelIF<U, I, C>, ImplicitFeedbackModelIF  {

    public GenericImplicitModel() {
        super();
    }

    @Override
    public Collection<? extends PreferenceIF<U, I, C>> getUniquePreferencesFromUser(U user, PreferenceAggregationFunction<U, I, C> f) {
        checkDirty();
        Collection<PreferenceIF<U,I,C>> preferences = new ArrayList<PreferenceIF<U,I,C>>();
        Set<I> userItems = theModel.get(user).keySet();
        for (I item : userItems){
            preferences.add(f.getAggregatedValue(this.getPreferences(user, item)));
        }
        return preferences;
    }
    
    @Override
    protected PreferenceIF<U, I, C> getPreferenceInstance(U user, I item, C context, Float pref){
        return new ImplicitPreference<U,I,C>(user, item, context, pref);
    }
    
    @Override
    protected Float getPreferenceValue(Collection<InternalPreference<C,Float>> itemPreferences, C context){
        Float rating = new Float(0);
        if (context == null){
            for (InternalPreference<C, Float> itemPref : itemPreferences){
                rating += itemPref.getPref();
            }
        }
        else{
            for (InternalPreference<C, Float> itemPref : itemPreferences){
                C valueContext = itemPref.getContext();
                if (valueContext == null) {
                    continue;
                }
                else if (itemPref.getContext().compareTo(context) == 0){
                    rating += itemPref.getPref();
                }
            }
        }
        if (rating==0) return null;
        return rating;
    }
}
