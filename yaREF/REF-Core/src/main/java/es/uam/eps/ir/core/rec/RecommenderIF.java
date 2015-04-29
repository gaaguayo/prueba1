package es.uam.eps.ir.core.rec;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import java.util.List;

/**
 *
 * @author Alejandro Bellogin
 */
public interface RecommenderIF<U, I, C extends ContextIF> {

    public Float predict(U user, I item, C context);

    public List<I> recommend(U user, C context);

    public I getMostRelevant(U user, I item1, I item2, C context);

    public ModelIF<U, I, C> getModel();
}
