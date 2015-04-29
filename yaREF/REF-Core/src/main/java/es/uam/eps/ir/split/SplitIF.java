package es.uam.eps.ir.split;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author Alejandro Bellogín
 * @author Pedro G. Campos
 */
public interface SplitIF<U, I, C extends ContextIF> {

    public ModelIF<U, I, C> getTrainingSet();

    public ModelIF<U, I, C> getTestingSet();
}
