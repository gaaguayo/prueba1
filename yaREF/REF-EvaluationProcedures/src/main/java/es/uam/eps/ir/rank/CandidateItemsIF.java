package es.uam.eps.ir.rank;

import es.uam.eps.ir.core.context.ContextIF;
import java.util.Set;

/**
 *
 * @author pedro
 */
public interface CandidateItemsIF<U,I,C extends ContextIF> {
    public Set<I> getRelevantSet(U user, C context);
    public Set<I> getNonRelevantSet(U user, C context);
    public Set<I> getNonRelevantSet(U user, I item, C context);
}
