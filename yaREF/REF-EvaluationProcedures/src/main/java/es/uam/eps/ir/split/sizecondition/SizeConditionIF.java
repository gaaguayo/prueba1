package es.uam.eps.ir.split.sizecondition;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author Pedro G. Campos
 */
public interface SizeConditionIF<U,I,C extends ContextIF> {
    public int getNumberOfRatingsForTraining(ModelIF<U,I,C> model);
}
