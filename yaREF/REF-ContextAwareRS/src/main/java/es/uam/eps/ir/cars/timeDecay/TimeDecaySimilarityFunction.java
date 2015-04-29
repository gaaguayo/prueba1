package es.uam.eps.ir.cars.timeDecay;

import es.uam.eps.ir.cars.neighborhood.SimilarityDatumIF;
import es.uam.eps.ir.cars.recommender.PreferenceSimilarityFunction;
import es.uam.eps.ir.core.context.ContinuousTimeContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;

/**
 *
 * @author Pedro G. Campos
 */
public class TimeDecaySimilarityFunction<U,I,C extends ContinuousTimeContextIF> implements PreferenceSimilarityFunction<U,I,C> {
    private final int T0;

    public TimeDecaySimilarityFunction(int T0) {
        this.T0 = T0;
    }

    public float getSimilarity(C context, SimilarityDatumIF similarityDatum, PreferenceIF<U,I,C> preference) {
        float similarity=(float)( similarityDatum.getSimilarity() * Math.exp(-1.0 / T0*(double)this.getDaysEllapsedFrom ( preference.getContext().getTimestamp(), context.getTimestamp() ) ) ); // assuming date (of recommendation) after date of rating
        return similarity;
    }
    
    private int getDaysEllapsedFrom(Long firstDate, Long lastDate){
        return ((int)((lastDate-firstDate)/((long)3600*24*1000)));
    }
    
}