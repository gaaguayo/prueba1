package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public interface ImpurityComputerIF<U, I, C extends ContextIF> {

    double getImpurity(Collection<PreferenceIF<U, I, C>> preferencesA, Collection<PreferenceIF<U, I, C>> preferencesB);
    double impurityThreshold();
}
