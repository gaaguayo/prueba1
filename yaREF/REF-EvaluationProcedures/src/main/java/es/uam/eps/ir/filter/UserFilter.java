package es.uam.eps.ir.filter;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author
 */
public class UserFilter<U, I, C extends ContextIF> implements DatasetFilterIF<U, I, C> {

    private int minRatingPerUser;

    public UserFilter(int minRatingPerUser) {
        this.minRatingPerUser = minRatingPerUser;
    }

    public ModelIF<U, I, C> filter(ModelIF<U, I, C> model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
