package es.uam.eps.ir.split.ratingorder;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import java.util.List;

/**
 *
 * @author Pedro G. Campos
 * @param <U> User ID
 * @param <I> Item ID
 * @param <C> Context information
 */
public interface RatingOrderIF<U,I,C extends ContextIF> {
    List<PreferenceIF<U,I,C>> getOrderedRatings(ModelIF<U,I,C> model);
}
