package es.uam.eps.ir.filter;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author
 */
public interface DatasetFilterIF<U, I, C extends ContextIF> {

    public ModelIF<U, I, C> filter(ModelIF<U, I, C> model);
}
