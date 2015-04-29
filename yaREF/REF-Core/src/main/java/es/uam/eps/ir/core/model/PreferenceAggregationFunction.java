package es.uam.eps.ir.core.model;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.Collection;

/**
 *
 * @author Alejandro Bellogin
 */
public interface PreferenceAggregationFunction<U, I, C extends ContextIF> {

    public PreferenceIF<U, I, C> getAggregatedValue(Collection<? extends PreferenceIF<U, I, C>> preferences);
}