package es.uam.eps.ir.cars.contextualfiltering;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public interface ContextDependentIF<U,I,C extends ContextIF> {
    public ContextualSlicerIF getSlicer();
    public ModelIF<U,I,C> getContextualModel(ContextualSegmentIF context);
}
