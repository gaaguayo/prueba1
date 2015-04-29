package es.uam.eps.ir.cars.recommender;

import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public class DefaultSimilarityFunction<U,I,C extends ContextIF> implements PreferenceSimilarityFunction<U,I,C> {

    public float getSimilarity(C context, SimilarityDatumIF similarityDatum, PreferenceIF<U,I,C> preference) {
        return similarityDatum.getSimilarity();
    }
    
}
