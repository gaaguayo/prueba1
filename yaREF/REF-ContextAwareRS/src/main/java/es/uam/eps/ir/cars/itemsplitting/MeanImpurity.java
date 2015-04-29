package es.uam.eps.ir.cars.itemsplitting;

import es.uam.eps.ir.core.context.ContextIF;
import es.uam.eps.ir.core.model.PreferenceIF;
import es.uam.eps.ir.core.util.Average;
import java.util.Collection;

/**
 *
 * @author Pedro G. Campos
 */
public class MeanImpurity<U,I,C extends ContextIF> implements ImpurityComputerIF<U, I, C> {
    private final double impurityThreshold;

    public MeanImpurity() {
        impurityThreshold = 4.03;
    }
    
    public MeanImpurity(double threshold) {
        impurityThreshold = threshold;
    }
    
    public double getImpurity(Collection<PreferenceIF<U,I,C>> preferencesA, Collection<PreferenceIF<U,I,C>> preferencesB){

        Average avgA = new Average();
        for (PreferenceIF<U,I,C> preferenceA : preferencesA){
            avgA.add(preferenceA.getValue());
        }
        
        Average avgB = new Average();
        for (PreferenceIF<U,I,C> preferenceB : preferencesB){
            avgB.add(preferenceB.getValue());
        }

        double numerator = avgA.average() - avgB.average();
        double denominator = Math.sqrt((avgA.variance()/(double)avgA.N()) + avgB.variance()/(double)avgB.N());
        
        double t_mean = Math.abs(numerator / denominator);
        
        return t_mean;
    }

    public double impurityThreshold() {
        return impurityThreshold;
    }
    
    
}
