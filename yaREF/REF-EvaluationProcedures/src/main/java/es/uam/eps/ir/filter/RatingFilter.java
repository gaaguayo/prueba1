package es.uam.eps.ir.filter;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author
 */
public class RatingFilter<U, I, C extends ContextIF> implements DatasetFilterIF<U, I, C> {

    private float minRatingValue;

    public RatingFilter(float minRatingValue) {
        this.minRatingValue = minRatingValue;
    }

    public ModelIF<U, I, C> filter(ModelIF<U, I, C> model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
