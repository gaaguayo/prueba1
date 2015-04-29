package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.context.RatingPreferenceComparator;
import es.uam.eps.ir.core.model.PreferenceAggregationFunction;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.model.impl.ExplicitPreference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 */
public class AdhocAggregationFunction<U, I, C extends ContinuousTimeContextIF> implements PreferenceAggregationFunction<U,I,C>{

    public PreferenceIF<U, I, C> getAggregatedValue(Collection<? extends PreferenceIF<U, I, C>> preferences) {

        final List<PreferenceIF<U, I, C>> sortedPrefs = new ArrayList<PreferenceIF<U, I, C>>(preferences);
        Collections.sort(sortedPrefs, new RatingPreferenceComparator<U, I, C>() );

        Float aggregatedValue = new Float(0);
        for (PreferenceIF<U,I,C> pref: sortedPrefs){
            aggregatedValue += pref.getValue();
        }
        PreferenceIF<U,I,C> pref = (PreferenceIF<U,I,C>)sortedPrefs.toArray()[sortedPrefs.size()-1];
        return new ExplicitPreference(pref.getUser(), pref.getItem(), pref.getContext(), aggregatedValue);
    }
}