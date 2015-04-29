package es.uam.eps.ir.dataset;

import es.uam.eps.ir.core.context.ContextDefinition;
import es.uam.eps.ir.core.context.ContextIF;
import java.util.List;

/**
 *
 * @author pedro
 */
public interface ContextualDatasetIF<U,I,C extends ContextIF> extends DatasetIF<U,I,C> {
    public List<ContextDefinition> getContextDefinitions();    
}
