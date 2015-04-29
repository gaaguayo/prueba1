package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public interface PreferenceSimilarityFunction<U,I,C extends ContextIF> {
    public float getSimilarity(C context, SimilarityDatumIF similarityDatum, PreferenceIF<U,I,C> preference);
}
