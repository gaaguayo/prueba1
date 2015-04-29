package es.uam.eps.ir.cars.dynamicSelection;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import java.util.Map;

/**
 *
 * @author pedro
 */
public abstract class DynamicSelector<U,I,C extends ContextIF> implements RecommenderIF<U,I,C>{
    protected Map<String, RecommenderIF<U,I,C>> recommenders;
    
    protected abstract String select(U user, I item, C context);
}
