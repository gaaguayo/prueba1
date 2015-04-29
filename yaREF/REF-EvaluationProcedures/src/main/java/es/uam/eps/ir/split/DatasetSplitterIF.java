package es.uam.eps.ir.split;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author Alejandro Bellogín
 * @author Pedro G. Campos
 */
public interface DatasetSplitterIF<U, I, C extends ContextIF> {

    public SplitIF<U, I, C>[] split(ModelIF<U, I, C> model);
}
