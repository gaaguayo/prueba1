package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public interface ContextBasedItemSplitterIF<U,I,C extends ContextIF> {
    public ModelIF<Object,Object,C> splitModel(ModelIF<U,I,C> model);
    public Object getSplitItemID(U user, I item, C context);
    public void setMinContextSize(int minContextSize);
}
