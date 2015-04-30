package es.uam.eps.ir.core.similarity;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.ModelIF;

/**
 *
 * @author pedro
 */
public interface SimilarityComputerIF<U,I,C extends ContextIF> {
    public double getUsersSimilarity(ModelIF<U,I,C> model, U user1ID, U user2ID);
    public double getItemsSimilarity(ModelIF<U,I,C> model, I item1ID, I item2ID);
}
