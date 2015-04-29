package es.uam.eps.ir.core.model.impl;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class DefaultAggregationFunction<U, I, C extends ContextIF> implements PreferenceAggregationFunction<U,I,C>{

    public PreferenceIF<U, I, C> getAggregatedValue(Collection<? extends PreferenceIF<U, I, C>> preferences) {
        Float aggregatedValue = new Float(0);
        for (PreferenceIF<U,I,C> pref: preferences){
            aggregatedValue += pref.getValue();
        }
        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)preferences.toArray()[0];
        return new ExplicitPreference(pref.getUser(), pref.getItem(), pref.getContext(), aggregatedValue);
    }
}
