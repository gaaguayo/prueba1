package es.uam.eps.ir.split.baseset;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 */
public interface BaseSetGeneratorIF<U,I,C  extends ContextIF> {
    List<ModelIF<U,I,C>> getBaseSets(ModelIF<U,I,C> model);
}
