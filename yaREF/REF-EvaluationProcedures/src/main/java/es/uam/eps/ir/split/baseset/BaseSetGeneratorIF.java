package es.uam.eps.ir.split.baseset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public interface BaseSetGeneratorIF<U,I,C  extends ContextIF> {
    Collection<ModelIF<U,I,C>> getBaseSets(ModelIF<U,I,C> model);
}
