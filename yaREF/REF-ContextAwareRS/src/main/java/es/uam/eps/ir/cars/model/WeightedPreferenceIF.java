package es.uam.eps.ir.cars.model;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author pedro
 */
public interface WeightedPreferenceIF<U,I,C extends ContextIF> extends PreferenceIF<U,I,C> {
    public double getWeight();
}
