package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.core.util.ContextualModelUtils;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.rec.RecommenderIF;

/**
 *
 * @author pedro
 */
public abstract class AbstractRecommender<U,I,C extends ContextIF> implements RecommenderIF<U,I,C> {
    protected ModelIF<U,I,C> model;
    protected ContextualModelUtils<U,I,C> eModel;

    public ModelIF<U, I, C> getModel() {
        return model;
    }
}
