package es.uam.eps.ir.nonpersonalized;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;
import es.uam.eps.ir.core.util.ContextualModelUtils;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 */
public class ItemMean<U,I,C extends ContextIF> implements RecommenderIF<U,I,C>{
    ContextualModelUtils<U,I,C> utils;

    public ItemMean(ModelIF<U,I,C> model) {
        utils = new ContextualModelUtils(model);
    }

    public Float predict(U user, I item, C context) {
        return utils.getItemMeanRating(item);
    }

    public List<I> recommend(U user, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public I getMostRelevant(U user, I item1, I item2, C context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ModelIF<U, I, C> getModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName();
    }
}
